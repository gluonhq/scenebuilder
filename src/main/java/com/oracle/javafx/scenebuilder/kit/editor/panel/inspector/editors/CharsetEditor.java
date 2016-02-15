package com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors;

import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.StringPropertyMetadata;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Editor for setting a charset property of an included element.
 */
public class CharsetEditor extends AutoSuggestEditor {

    private Map<String, Charset> availableCharsets;

    public CharsetEditor(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses, Map<String, Charset> availableCharsets) {
        super(propMeta, selectedClasses, new ArrayList<>(availableCharsets.keySet()), Type.ALPHA);
        initialize(availableCharsets);
    }

    private void initialize(Map<String, Charset> availableCharsets) {
        this.availableCharsets = availableCharsets;
        EventHandler<ActionEvent> onActionListener = event -> {
            if (isHandlingError()) {
                // Event received because of focus lost due to error dialog
                return;
            }
            Object value = getValue();
            if ((value != null) && isValidValue((String) value)) {
                userUpdateValueProperty(value);
                getTextField().selectAll();
            } else {
                handleInvalidValue(getTextField().getText());
            }
        };
        setTextEditorBehavior(this, textField, onActionListener);
    }

    @Override
    public Object getValue() {
        String val = getTextField().getText();
        if (val.isEmpty()) {
            val = "";
            getTextField().setText(val);
            return String.valueOf(val);
        }
        Object constantValue = availableCharsets.get(val.toUpperCase(Locale.ROOT));
        if (constantValue != null) {
            val = EditorUtils.valAsStr(constantValue);
        }
        return String.valueOf(val);
    }

    @Override
    public void setValue(Object value) {
        setValueGeneric(value);
        if (isSetValueDone()) {
            return;
        }
        assert (value instanceof String);
        // Get the corresponding constant if any
        for (Map.Entry<String, Charset> entry : availableCharsets.entrySet()) {
            if (value.equals(entry.getValue())) {
                value = entry.getKey();
            }
        }
        getTextField().setText(EditorUtils.valAsStr(value));
    }

    @Override
    public void requestFocus() {
        EditorUtils.doNextFrame(() -> getTextField().requestFocus());
    }

    public void reset(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses,
                      Map<String, Charset> constants) {
        super.reset(propMeta, selectedClasses, new ArrayList<>(constants.keySet()));
        this.availableCharsets = constants;
    }

    private boolean isValidValue(String value) {
        boolean valid = false;
        if (Charset.availableCharsets().containsKey(value)) {
            valid = true;
        }
        return valid;
    }

    /**
     * Gets the collection of charsets, which are available for the current JVM.
     *
     * @param propertyMetadata value property metadata
     * @return collection of charsets
     */
    public static Map<String, Charset> getAvailableCharsets(StringPropertyMetadata propertyMetadata) {
        return Charset.availableCharsets();
    }
}