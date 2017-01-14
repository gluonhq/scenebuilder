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

package com.oracle.javafx.scenebuilder.app.template;

import com.oracle.javafx.scenebuilder.app.SceneBuilderApp;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class TemplatesBaseWindowController extends AbstractFxmlWindowController {

    @FXML
    protected Button basicDesktopApp;
    @FXML
    protected Button complexDesktopApp;
    @FXML
    protected Button emptyPhoneApp;
    @FXML
    protected Button basicPhoneApp;

    @FXML
    protected VBox templateContainer;

    protected SceneBuilderApp sceneBuilderApp;

    public TemplatesBaseWindowController(URL fxmlURL, ResourceBundle resources, Window owner) {
        super(fxmlURL, resources, owner);
        sceneBuilderApp = SceneBuilderApp.getSingleton();
    }


    @Override
    public void onCloseRequest(WindowEvent event) {
        getStage().hide();
    }

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
        assert getRoot().getScene().getWindow() != null;
    }

    @Override
    protected void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert templateContainer != null;

        basicDesktopApp.setUserData(Template.BASIC_DESKTOP_APP);
        complexDesktopApp.setUserData(Template.COMPLEX_DESKTOP_APP);
        emptyPhoneApp.setUserData(Template.EMPTY_PHONE_APP);
        basicPhoneApp.setUserData(Template.BASIC_PHONE_APP);
    }

    protected void setupTemplateButtonHandlers() {
        setupTemplateButtonHandlers(templateContainer);
    }

    private void setupTemplateButtonHandlers(Parent templateContainer) {
        for (Node child : templateContainer.getChildrenUnmodifiable()) {
            if (!(child instanceof Button) && child instanceof Parent){
                setupTemplateButtonHandlers((Parent)child);
            }
            if (child instanceof Button) {
                Button button = (Button) child;
                button.setOnAction(event -> {
                    getTemplateEventHandler(button).handle(event);
                    getStage().hide();
                });
            }
        }
    }

    protected abstract EventHandler getTemplateEventHandler(Button button);
}

