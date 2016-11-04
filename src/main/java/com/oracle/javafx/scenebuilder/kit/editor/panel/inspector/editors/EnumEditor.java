package com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors;

import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.EnumerationPropertyMetadata;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;

import java.util.Set;

public abstract class EnumEditor extends PropertyEditor {
    private final Control choiceControl;

    public EnumEditor(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses, Control choiceControl) {
        super(propMeta, selectedClasses);
        this.choiceControl = choiceControl;
        choiceControl.disableProperty().bind(disableProperty());
        EditorUtils.makeWidthStretchable(choiceControl);
    }

    @Override
    public Node getValueEditor() {
        return super.handleGenericModes(choiceControl);
    }

    @Override
    protected void valueIsIndeterminate() {
        handleIndeterminate(choiceControl);
    }

    protected Control getChoiceControl() {
        return choiceControl;
    }

    protected void updateItems(ObservableList<String> itemsList) {
        assert getPropertyMeta() instanceof EnumerationPropertyMetadata;
        final EnumerationPropertyMetadata enumPropMeta
                = (EnumerationPropertyMetadata) getPropertyMeta();
        itemsList.clear();
        for (Object val : enumPropMeta.getValidValues()) {
            itemsList.add(val.toString());
        }
    }

    @Override
    public void requestFocus() {
        EditorUtils.doNextFrame(() -> choiceControl.requestFocus());
    }

}
