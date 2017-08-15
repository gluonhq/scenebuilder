/*
 * Copyright (c) 2017, Gluon and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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

import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PrefixedValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Editor for including FXML files into the main document (through fx:include).
 */
public class IncludeFxmlEditor extends InlineListEditor {

    private final StackPane root = new StackPane();
    private EditorController editorController;
    @FXML
    private Button includeFxmlButton;
    @FXML
    private TextField includeFxmlField;

    public IncludeFxmlEditor(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses, EditorController editorController) {
        super(propMeta, selectedClasses);
        this.editorController = editorController;
        initialize();
    }

    private void initialize() {
        Parent rootInitialBt = EditorUtils.loadFxml("IncludeFXMLButton.fxml", this);
        Tooltip tooltip = new Tooltip("Include FXML");
        includeFxmlButton.setTooltip(tooltip);
        root.getChildren().add(rootInitialBt);
        super.disableResetValueMenuItem();
    }

    @Override
    public Node getValueEditor() {
        return super.handleGenericModes(root);
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                includeFxmlField.setText(obj.toString());
            }
        }
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void requestFocus() {
        throw new UnsupportedOperationException();
    }

    @FXML
    public void addIncludeFile() {
        File fxmlFile = chooseFxml();
        if (fxmlFile != null) {
            EditorController.updateNextInitialDirectory(fxmlFile);
            editorController.performIncludeFxml(fxmlFile);
            includeFxmlField.setText(getRelativePath(fxmlFile));
        }
    }

    private File chooseFxml() {
        final FileChooser fileChooser = new FileChooser();
        final FileChooser.ExtensionFilter filter
                = new FileChooser.ExtensionFilter(I18N.getString("file.filter.label.fxml"), "*.fxml");
        fileChooser.getExtensionFilters().add(filter);
        setInitialDirectory(fileChooser);
        return fileChooser.showOpenDialog(root.getScene().getWindow());
    }

    private void setInitialDirectory(FileChooser fileChooser) {
        if (editorController.getIncludedFile() != null) {
            File file = editorController.getIncludedFile();
            final Path chosenFolder = file.toPath().getParent();
            fileChooser.setInitialDirectory(chosenFolder.toFile());
        } else {
            fileChooser.setInitialDirectory(EditorController.getNextInitialDirectory());
        }
    }

    private String getRelativePath(File includedFile) {
        URL url = null;
        try {
            url = includedFile.toURI().toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(IncludeFxmlEditor.class.getName()).log(Level.SEVERE, "Path could not be determined.", ex);
        }
        String prefixedValue = PrefixedValue.makePrefixedValue(url, editorController.getFxmlLocation()).toString();
        return removeAtSign(prefixedValue);
    }

    private static String removeAtSign(String prefixedValue) {
        String prefixedValueWithNoAt = "";
        if (prefixedValue.contains("@")) {
            prefixedValueWithNoAt = prefixedValue.replace("@", "");
        }
        return prefixedValueWithNoAt;
    }
}