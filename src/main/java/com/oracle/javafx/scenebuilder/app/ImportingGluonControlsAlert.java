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

package com.oracle.javafx.scenebuilder.app;

import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal;
import com.oracle.javafx.scenebuilder.app.util.SBSettings;
import com.oracle.javafx.scenebuilder.kit.editor.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.library.util.JarReport;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ImportingGluonControlsAlert extends Alert {

    public ImportingGluonControlsAlert() {
        super(AlertType.WARNING);

        setHeaderText(I18N.getString("alert.importing.gluon.headertext"));
        setContentText(I18N.getString("alert.importing.gluon.contenttext"));

        ButtonType OKButton = new ButtonType(I18N.getString("alert.importing.gluon.ok.button"), ButtonBar.ButtonData.OK_DONE);

        getButtonTypes().setAll(OKButton);

        getDialogPane().getStyleClass().add("SB-alert");
        getDialogPane().getStylesheets().add(SceneBuilderApp.class.getResource("css/Alert.css").toString());

        SBSettings.setWindowIcon((Stage)getDialogPane().getScene().getWindow());
    }

    public static void updateImportedGluonJars(List<JarReport> jars) {
        PreferencesController pc = PreferencesController.getSingleton();
        PreferencesRecordGlobal recordGlobal = pc.getRecordGlobal();
        List<String> jarReportCollection = new ArrayList<>();
        for (JarReport jarReport : jars) {
            if (jarReport.hasGluonControls()) {
                jarReportCollection.add(jarReport.getJar().getFileName().toString());
            }
        }
        if (jarReportCollection.isEmpty()) {
            recordGlobal.setImportedGluonJars(new String[0]);
        } else {
            recordGlobal.setImportedGluonJars(jarReportCollection.toArray(new String[0]));
        }
    }

    public static boolean hasGluonJarBeenImported(String jar) {
        PreferencesController pc = PreferencesController.getSingleton();
        PreferencesRecordGlobal recordGlobal = pc.getRecordGlobal();
        String[] importedJars = recordGlobal.getImportedGluonJars();
        if (importedJars == null) {
            return false;
        }

        for (String importedJar : importedJars) {
            if (jar.equals(importedJar)) {
                return true;
            }
        }
        return false;
    }
}
