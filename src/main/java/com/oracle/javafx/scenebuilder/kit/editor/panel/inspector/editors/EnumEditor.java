/*
 * Copyright (c) 2016, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
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

import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.EnumerationPropertyMetadata;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import java.util.Set;

public class EnumEditor extends PropertyEditor {
    private ComboBox<String> comboBox;

    public EnumEditor(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses) {
        super(propMeta, selectedClasses);
        comboBox = new ComboBox<String>();
        comboBox.disableProperty().bind(disableProperty());
        EditorUtils.makeWidthStretchable(comboBox);
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

    @Override
    public Node getValueEditor() {
        return super.handleGenericModes(comboBox);
    }

    @Override
    protected void valueIsIndeterminate() {
        handleIndeterminate(comboBox);
    }

    protected ComboBox<String> getComboBox() {
        return comboBox;
    }

    protected void updateItems() {
        updateItems(comboBox.getItems());
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
        EditorUtils.doNextFrame(() -> comboBox.requestFocus());
    }

}
