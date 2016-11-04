/*
 * Copyright (c) 2016, Gluon and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors;

import com.oracle.javafx.scenebuilder.kit.editor.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors.util.SBDuration;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Editor for Duration properties.
 */
public class DurationEditor extends AutoSuggestEditor{

    private static final Map<String, SBDuration> constants = new HashMap<>();

    private static final String MILLISECONDS = I18N.getString("inspector.duration.milliseconds");
    private static final String SECONDS = I18N.getString("inspector.duration.seconds");
    private static final String MINUTES = I18N.getString("inspector.duration.minutes");
    private static final String HOURS = I18N.getString("inspector.duration.hours");

    static {
        // Doesn't work because Duration.valueOf(String) is broken: doesn't accept "INDEFINITE"
//        constants.put("INDEFINITE", new SBDuration(Duration.INDEFINITE));
    }

    @FXML
    private StackPane textfieldStackPane;
    @FXML
    private ComboBox unitsComboBox;



    private Parent root;

    public DurationEditor(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses) {
        super(propMeta, selectedClasses, new ArrayList<>(constants.keySet()), AutoSuggestEditor.Type.DOUBLE);
        initialize();
    }

    private void initialize() {
        root = EditorUtils.loadFxml("DurationEditor.fxml", this);

        //
        // Text field
        //
        EventHandler<ActionEvent> onActionListener = event -> {
            if (isHandlingError()) {
                // Event received because of focus lost due to error dialog
                return;
            }
            if (isUpdateFromModel()) {
                // nothing to do
                return;
            }

            Object value = getValue();
            assert value instanceof SBDuration;
            SBDuration valDuration = (SBDuration) value;
            // Check if the entered value is a constant string
            boolean isConstant = constants.get(getTextField().getText().toUpperCase(Locale.ROOT)) != null;
            // Check if the entered value is a constant value
            for (Map.Entry<String, SBDuration> entry : constants.entrySet()) {
                if (value.equals(entry.getValue())) {
                    isConstant = true;
                    break;
                }
            }
            getTextField().selectAll();
            userUpdateValueProperty(valDuration);
        };
        setNumericEditorBehavior(this, getTextField(), onActionListener, false);

        //
        // ComboBox
        //
        localizeComboBox();
        unitsComboBox.getSelectionModel().select(MILLISECONDS);
        unitsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            SBDuration value = getValue(getTextField().getText(), (String)oldValue);
            getTextField().setText(getNumericValue(value, (String)newValue).toString());
        });

        // Add the AutoSuggest text field in the scene graph
        textfieldStackPane.getChildren().add(super.getRoot());
    }

    private void localizeComboBox() {
        ArrayList<String> localizedStrings = new ArrayList<>();
        for (Object item : unitsComboBox.getItems()) {
            String itemString = (String) item;
            if (itemString.charAt(0) == '%') {
                localizedStrings.add(I18N.getString(itemString.substring(1)));
            } else {
                localizedStrings.add(itemString);
            }
        }
        unitsComboBox.getItems().clear();
        unitsComboBox.getItems().addAll(localizedStrings);
    }

    @Override
    public Node getValueEditor() {
        return super.handleGenericModes(root);
    }

    @Override
    public Object getValue() {
        return getValue(getTextField().getText(), (String)unitsComboBox.getSelectionModel().getSelectedItem());
    }

    private SBDuration getValue(String valueString, String units) {
        if (valueString.isEmpty()) {
            valueString = "0"; //NOI18N
            getTextField().setText(valueString);
            return new SBDuration(Duration.ZERO);
        }
        SBDuration constantValue = constants.get(valueString.toUpperCase(Locale.ROOT));
        if (constantValue != null) {
            return constantValue;
        }
        try {
            if (units.equals(MILLISECONDS)) {
                valueString = valueString + "ms";
            } else if (units.equals(SECONDS)) {
                valueString = valueString + "s";
            } else if (units.equals(MINUTES)) {
                valueString = valueString + "m";
            } else if (units.equals(HOURS)) {
                valueString = valueString + "h";
            }
            return SBDuration.valueOf(valueString);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void setValue(Object value) {
        setValueGeneric(value);
        if (isSetValueDone()) {
            return;
        }
        if (value == null){
            getTextField().setText("");
            return;
        }

        assert (value instanceof SBDuration);
        SBDuration durationValue = (SBDuration) value;

        // Get the corresponding constant if any
        for (Map.Entry<String, SBDuration> entry : constants.entrySet()) {
            if (value.equals(entry.getValue())) {
                getTextField().setText(entry.getKey());
                return;
            }
        }
        String units = (String) unitsComboBox.getSelectionModel().getSelectedItem();
        getTextField().setText(getNumericValue(durationValue, units).toString());
    }

    private Double getNumericValue(SBDuration durationValue, String units) {
        Double convertedValue = null;
        if (units.equals(MILLISECONDS)) {
            convertedValue = durationValue.toMillis();
        } else if (units.equals(SECONDS)) {
            convertedValue = durationValue.toSeconds();
        } else if (units.equals(MINUTES)) {
            convertedValue = durationValue.toMinutes();
        } else if (units.equals(HOURS)) {
            convertedValue = durationValue.toHours();
        }
        return convertedValue;
    }

    public void reset(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses) {
        super.reset(propMeta, selectedClasses, new ArrayList<>(constants.keySet()));
    }

    @Override
    protected void valueIsIndeterminate() {
        handleIndeterminate(getTextField());
    }

    @Override
    public void requestFocus() {
        EditorUtils.doNextFrame(() -> getTextField().requestFocus());
    }

}
