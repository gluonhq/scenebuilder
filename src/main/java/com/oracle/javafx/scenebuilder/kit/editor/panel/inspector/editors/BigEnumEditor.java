package com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors;

import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import javafx.beans.InvalidationListener;
import javafx.scene.control.ComboBox;

import java.util.Set;

public class BigEnumEditor extends EnumEditor{
    private ComboBox<String> comboBox;

    public BigEnumEditor(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses) {
        super(propMeta, selectedClasses, new ComboBox<String>());
        comboBox = (ComboBox<String>) getChoiceControl();
        comboBox.getSelectionModel().selectedItemProperty().addListener((InvalidationListener) o -> {
            if (!isUpdateFromModel()) {
                userUpdateValueProperty(getValue());
            }
        });
        initialize();
    }

    private void initialize() {
        updateItems();
    }

    @Override
    public Object getValue() {
        return comboBox.getSelectionModel().getSelectedItem();
    }

    @Override
    public void setValue(Object value) {
        setValueGeneric(value);
        if (isSetValueDone()) {
            return;
        }

        if (value != null) {
            comboBox.getSelectionModel().select(value.toString());
        } else {
            comboBox.getSelectionModel().clearSelection();
        }
    }

    @Override
    public void reset(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses) {
        super.reset(propMeta, selectedClasses);
        // ComboBox items have to be updated, since this editor may have been used by a different Enum...
        updateItems();
    }

    protected ComboBox<String> getComboBox() {
        return comboBox;
    }

    protected void updateItems() {
        updateItems(comboBox.getItems());
    }
}
