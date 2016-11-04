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
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PrefixedValue;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StringListEditor extends InlineListEditor {


    public StringListEditor(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses) {
        super(propMeta, selectedClasses);
        initialize();
    }

    private void initialize() {
        setLayoutFormat(PropertyEditor.LayoutFormat.DOUBLE_LINE);
        addItem(getNewStringListItem());
    }

    // Creates an empty item
    private StringListItem getNewStringListItem() {
        return new StringListItem(this, "");
    }

    @Override
    public Object getValue() {
        List<String> value = FXCollections.observableArrayList();
        // Group all the item values in a list
        for (EditorItem styleItem : getEditorItems()) {
            String itemValue = EditorUtils.toString(styleItem.getValue());
            if (itemValue.isEmpty()) {
                continue;
            }
            value.add(itemValue);
        }
        if (value.isEmpty()) {
            // no style class
            return super.getPropertyMeta().getDefaultValueObject();
        } else {
            return value;
        }
    }

    @Override
    public void setValue(Object value) {
        setValueGeneric(value);
        if (value == null) {
            reset();
            return;
        }
        assert value instanceof List;
        // Warning : value is the editing list.
        // We do not want to set the valueProperty() to editing list
        setValueGeneric(value);
        if (isSetValueDone()) {
            return;
        }

        Iterator<EditorItem> itemsIter = new ArrayList<>(getEditorItems()).iterator();
        for (String item : (List<String>) value) {
            EditorItem editorItem;
            if (itemsIter.hasNext()) {
                // re-use the current items first
                editorItem = itemsIter.next();
            } else {
                // additional items required
                editorItem = addItem(new StringListItem(this, item));
            }
            editorItem.setValue(item);
        }
        // Empty the remaining items, if needed
        while (itemsIter.hasNext()) {
            EditorItem editorItem = itemsIter.next();
            removeItem(editorItem);
        }
    }

    public void reset(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses,
                      Set<FXOMInstance> selectedInstances) {
        super.reset(propMeta, selectedClasses);
        addItem(getNewStringListItem());
    }

    @Override
    public void requestFocus() {
        EditorItem firstItem = getEditorItems().get(0);
        assert firstItem instanceof StringListItem;
        ((StringListItem) firstItem).requestFocus();
    }

    /**
     * **************************************************************************
     * <p>
     * StringList item : text field, + and action button.
     * <p>
     * **************************************************************************
     */
    private class StringListItem implements EditorItem {

        @FXML
        private Button plusBt;
        @FXML
        private MenuItem removeMi;
        @FXML
        private MenuItem moveUpMi;
        @FXML
        private MenuItem moveDownMi;
        @FXML
        private Label prefixLb;
        @FXML
        private TextField textTextfield;

        private Pane root;
        private String currentValue;
        private EditorItemDelegate editor;
        private PrefixedValue.Type itemType = PrefixedValue.Type.PLAIN_STRING;

        public StringListItem(EditorItemDelegate editor, String text) {
            initialize(editor, text);
        }

        private void initialize(EditorItemDelegate editor, String text) {
            this.editor = editor;
            Parent parentRoot = EditorUtils.loadFxml("StringListEditorItem.fxml", this);
            assert parentRoot instanceof Pane;
            root = (Pane) parentRoot;

            setValue(text);
            EventHandler<ActionEvent> onActionListener = event -> {
                if (getValue().equals(currentValue)) {
                    // no change
                    return;
                }
                editor.commit(StringListItem.this);
                if (event != null && event.getSource() instanceof TextField) {
                    ((TextField) event.getSource()).selectAll();
                }
                updateButtons();
                currentValue = EditorUtils.toString(getValue());
            };

            ChangeListener<String> textPropertyChange = (ov, prevText, newText) -> {
                if (prevText.isEmpty() || newText.isEmpty()) {
                    // Text changed FROM empty value, or TO empty value: buttons status change
                    updateButtons();
                }
            };
            textTextfield.textProperty().addListener(textPropertyChange);
            updateButtons();

            setTextEditorBehavior(textTextfield, onActionListener);

            removeMi.setText(I18N.getString("inspector.list.remove"));
            moveUpMi.setText(I18N.getString("inspector.list.moveup"));
            moveDownMi.setText(I18N.getString("inspector.list.movedown"));
        }

        @Override
        public final Node getNode() {
            return root;
        }

        @Override
        public Object getValue() {
            String suffix;
            if (textTextfield.getText().isEmpty()) {
                return "";
            } else {
                suffix = textTextfield.getText().trim();
            }
            return (new PrefixedValue(itemType, suffix)).toString();
        }

        @Override
        public void setValue(Object text) {
            PrefixedValue prefixedValue = new PrefixedValue(EditorUtils.toString(text));
            itemType = prefixedValue.getType();
            handlePrefix(itemType);
            if (prefixedValue.getSuffix() != null) {
                textTextfield.setText(prefixedValue.getSuffix().trim());
            } else {
                textTextfield.setText("");
            }
            updateButtons();
            currentValue = EditorUtils.toString(getValue());
        }

        @Override
        public void reset() {
            textTextfield.setText("");
            textTextfield.setPromptText(null);
        }

        @Override
        public void setValueAsIndeterminate() {
            handleIndeterminate(textTextfield);
        }

        protected void requestFocus() {
            EditorUtils.doNextFrame(() -> textTextfield.requestFocus());
        }

        @Override
        public MenuItem getMoveUpMenuItem() {
            return moveUpMi;
        }

        @Override
        public MenuItem getMoveDownMenuItem() {
            return moveDownMi;
        }

        @Override
        public MenuItem getRemoveMenuItem() {
            return removeMi;
        }

        @Override
        public Button getPlusButton() {
            return plusBt;
        }

        @Override
        public Button getMinusButton() {
            // not used here
            return null;
        }

        @FXML
        void add(ActionEvent event) {
            StringListEditor.StringListItem styleClassItem = getNewStringListItem();
            editor.add(this, styleClassItem);
            styleClassItem.requestFocus();

        }

        @FXML
        void remove(ActionEvent event) {
            editor.remove(this);
        }

        @FXML
        void up(ActionEvent event) {
            editor.up(this);
        }

        @FXML
        void down(ActionEvent event) {
            editor.down(this);
        }


        @FXML
        void plusBtTyped(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                add(null);
            }
        }

        private void updateButtons() {
            if (textTextfield.getText().isEmpty()) {
                // if no content, disable plus
                plusBt.setDisable(true);
                removeMi.setDisable(false);
            } else {
                // enable plus and minus
                plusBt.setDisable(false);
                removeMi.setDisable(false);
            }
        }

        protected void disablePlusButton(boolean disable) {
            plusBt.setDisable(disable);
        }

        protected void disableRemove(boolean disable) {
            removeMi.setDisable(disable);
        }

        protected void handlePrefix(PrefixedValue.Type type) {
            this.itemType = type;
            if (type == PrefixedValue.Type.DOCUMENT_RELATIVE_PATH) {
                setPrefix(FXMLLoader.RELATIVE_PATH_PREFIX);
            } else if (type == PrefixedValue.Type.CLASSLOADER_RELATIVE_PATH) {
                setPrefix(FXMLLoader.RELATIVE_PATH_PREFIX + "/");//NOI18N
            } else {
                // absolute
                removeLabel();
            }
        }

        private void setPrefix(String str) {
            if (!prefixLb.isVisible()) {
                prefixLb.setVisible(true);
                prefixLb.setManaged(true);
            }
            prefixLb.setText(str);
        }

        private void removeLabel() {
            prefixLb.setVisible(false);
            prefixLb.setManaged(false);
        }
    }
}

