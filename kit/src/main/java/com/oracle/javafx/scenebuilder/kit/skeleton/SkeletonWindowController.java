/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.skeleton;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SkeletonWindowController extends AbstractFxmlWindowController {

    @FXML
    ChoiceBox<SkeletonSettings.LANGUAGE> languageChoiceBox;
    @FXML
    CheckBox commentCheckBox;
    @FXML
    CheckBox formatCheckBox;
    @FXML
    TextArea textArea;

    @FXML
    private void onCopyAction(ActionEvent event) {
        final Map<DataFormat, Object> content = new HashMap<>();

        if (textArea.getSelection().getLength() == 0) {
            content.put(DataFormat.PLAIN_TEXT, textArea.getText());
        } else {
            content.put(DataFormat.PLAIN_TEXT, textArea.getSelectedText());
        }

        Clipboard.getSystemClipboard().setContent(content);
    }

    private final EditorController editorController;
    private boolean dirty = false;

    private final String documentName;

    public SkeletonWindowController(EditorController editorController, String documentName, Stage owner) {
        super(SkeletonWindowController.class.getResource("SkeletonWindow.fxml"), I18N.getBundle(), owner); //NOI18N
        this.editorController = editorController;
        this.documentName = documentName;

        this.editorController.fxomDocumentProperty().addListener(
            (ChangeListener<FXOMDocument>) (ov, od, nd) -> {
                assert editorController.getFxomDocument() == nd;
                if (od != null) {
                    od.sceneGraphRevisionProperty().removeListener(fxomDocumentRevisionListener);
                }
                if (nd != null) {
                    nd.sceneGraphRevisionProperty().addListener(fxomDocumentRevisionListener);
                    update();
                }
            });

        if (editorController.getFxomDocument() != null) {
            editorController.getFxomDocument().sceneGraphRevisionProperty().addListener(fxomDocumentRevisionListener);
        }
    }

    @Override
    public void onCloseRequest(WindowEvent event) {
        getStage().close();
    }

    @Override
    public void openWindow() {
        super.openWindow();

        if (dirty) {
            update();
        }
    }

    /*
     * AbstractFxmlWindowController
     */
    @Override
    protected void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert languageChoiceBox != null;
        assert commentCheckBox != null;
        assert formatCheckBox != null;
        assert textArea != null;

        languageChoiceBox.getItems().addAll(SkeletonSettings.LANGUAGE.values());
        languageChoiceBox.getSelectionModel().select(SkeletonSettings.LANGUAGE.JAVA);

        languageChoiceBox.getSelectionModel().selectedItemProperty().addListener(fxomDocumentRevisionListener);
        commentCheckBox.selectedProperty().addListener(fxomDocumentRevisionListener);
        formatCheckBox.selectedProperty().addListener(fxomDocumentRevisionListener);

        update();
    }

    /*
     * Private
     */
    private final InvalidationListener fxomDocumentRevisionListener = (observable) -> update();

    private void updateTitle() {
        final String title = I18N.getString("skeleton.window.title", documentName);
        getStage().setTitle(title);
    }

    private void update() {
        assert editorController.getFxomDocument() != null;

        // No need to eat CPU if the skeleton window isn't opened
        if (getStage().isShowing()) {
            updateTitle();
            final SkeletonBuffer buf = new SkeletonBuffer(editorController.getFxomDocument(), documentName);

            buf.setLanguage(languageChoiceBox.getSelectionModel().getSelectedItem());

            if (commentCheckBox.isSelected()) {
                buf.setTextType(SkeletonSettings.TEXT_TYPE.WITH_COMMENTS);
            } else {
                buf.setTextType(SkeletonSettings.TEXT_TYPE.WITHOUT_COMMENTS);
            }

            if (formatCheckBox.isSelected()) {
                buf.setFormat(SkeletonSettings.FORMAT_TYPE.FULL);
            } else {
                buf.setFormat(SkeletonSettings.FORMAT_TYPE.COMPACT);
            }

            textArea.setText(buf.toString());
            dirty = false;
        } else {
            dirty = true;
        }
    }
}
