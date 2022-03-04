/*
 * Copyright (c) 2017, 2022, Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.kit.template;

import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class TemplatesWindowController extends TemplatesBaseWindowController {
    private final Window owner;

    public TemplatesWindowController(Stage owner) {
        super(TemplatesWindowController.class.getResource("TemplatesWindow.fxml"), //NOI18N
                I18N.getBundle(), owner);
        this.owner = owner;
    }

    @Override
    protected void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
    }

    @Override
    public void onCloseRequest(WindowEvent event) {
        getStage().hide();
    }

    @Override
    protected void controllerDidCreateStage() {
        super.controllerDidCreateStage();
        getStage().setTitle(I18N.getString("template.dialog.title"));
        getStage().setMinWidth(750);
        getStage().setMinHeight(650);

        if (this.owner == null) {
            // Window will be application modal
            getStage().initModality(Modality.APPLICATION_MODAL);
        } else {
            // Window will be window modal
            getStage().initOwner(this.owner);
            getStage().initModality(Modality.WINDOW_MODAL);
        }
    }
}

