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

import com.oracle.javafx.scenebuilder.app.i18n.I18N;
import com.oracle.javafx.scenebuilder.app.util.SBSettings;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

public class TemplatesWindowController extends TemplatesBaseWindowController {

    public TemplatesWindowController() {
        super(TemplatesWindowController.class.getResource("TemplatesWindow.fxml"), //NOI18N
                I18N.getBundle());
    }

    @Override
    protected void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();

        setupTemplateButtonHandlers();
    }


    @Override
    public void onCloseRequest(WindowEvent event) {
        getStage().hide();
    }

    @Override
    protected void controllerDidCreateStage() {
        super.controllerDidCreateStage();
        getStage().setTitle(I18N.getString("template.dialog.title"));
        getStage().initModality(Modality.APPLICATION_MODAL);
        SBSettings.setWindowIcon(getStage());
    }

    @Override
    protected void controllerDidCreateScene() {
        super.controllerDidCreateScene();
        getScene().getStylesheets().add(TemplatesWindowController.class.getResource("TemplatesWindow.css").toString());
    }

    @Override
    protected EventHandler getTemplateEventHandler(Button button) {
        return event -> sceneBuilderApp.performNewTemplateInNewWindow((Template)button.getUserData());
    }

}
