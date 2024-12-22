/*
 * Copyright (c) 2017, 2024, Gluon and/or its affiliates.
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

package com.gluonhq.scenebuilder.plugins.alert;

import com.gluonhq.scenebuilder.plugins.editor.GluonEditorPlatform;
import com.oracle.javafx.scenebuilder.kit.alert.SBAlert;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.gluonhq.scenebuilder.plugins.i18n.I18N;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * Used when the user adds a Gluon control to the document or loads a document with a Gluon control and
 * Gluon Mobile theme is not set.
 * When a Gluon control is used, Gluon Mobile theme must be set in order for the control to work correctly.
 */
public class WarnThemeAlert extends SBAlert {

    private static boolean hasBeenShown = false;

    private WarnThemeAlert(Stage owner, Consumer<EditorPlatform.Theme> onSuccess) {
        super(AlertType.WARNING, owner);

        setTitle(I18N.getString("alert.theme.gluon.mobile.title"));
        setHeaderText(I18N.getString("alert.theme.gluon.mobile.headertext"));
        setContentText(I18N.getString("alert.theme.gluon.mobile.contenttext"));

        ButtonType setGluonTheme = new ButtonType(I18N.getString("alert.theme.gluon.mobile.setgluontheme"), ButtonBar.ButtonData.OK_DONE);
        ButtonType ignore = new ButtonType(I18N.getString("alert.theme.gluon.mobile.ignore"), ButtonBar.ButtonData.CANCEL_CLOSE);

        getButtonTypes().setAll(setGluonTheme, ignore);

        resultProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == setGluonTheme && onSuccess != null) {
                onSuccess.accept(GluonEditorPlatform.GLUON_MOBILE_LIGHT);
            }
        });

        setOnShown(event -> hasBeenShown = true);
    }

    public static void showAlertIfRequired(Stage owner, EditorPlatform.Theme currentTheme, Consumer<EditorPlatform.Theme> onSuccess) {
        if (!hasBeenShown &&
            (!GluonEditorPlatform.isGluonMobileLight(currentTheme) && !GluonEditorPlatform.isGluonMobileDark(currentTheme))) {
            new WarnThemeAlert(owner, onSuccess).showAndWait();
        }
    }

}
