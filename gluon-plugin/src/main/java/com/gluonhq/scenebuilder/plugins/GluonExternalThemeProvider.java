/*
 * Copyright (c) 2024, Gluon and/or its affiliates.
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
 *  - Neither the name of Gluon nor the names of its
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
package com.gluonhq.scenebuilder.plugins;

import com.gluonhq.scenebuilder.plugins.alert.ImportingGluonControlsAlert;
import com.gluonhq.scenebuilder.plugins.alert.WarnThemeAlert;
import com.gluonhq.scenebuilder.plugins.editor.GluonEditorController;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.editor.ExternalThemeProvider;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;

import static com.gluonhq.scenebuilder.plugins.editor.GluonEditorPlatform.GLUON_DOCUMENT_STYLESHEET;
import static com.gluonhq.scenebuilder.plugins.editor.GluonEditorPlatform.GLUON_IMPL_PACKAGE;
import static com.gluonhq.scenebuilder.plugins.editor.GluonEditorPlatform.GLUON_MOBILE;
import static com.gluonhq.scenebuilder.plugins.editor.GluonEditorPlatform.GLUON_MOBILE_DARK;
import static com.gluonhq.scenebuilder.plugins.editor.GluonEditorPlatform.GLUON_MOBILE_LIGHT;
import static com.gluonhq.scenebuilder.plugins.editor.GluonEditorPlatform.GLUON_PACKAGE;

public class GluonExternalThemeProvider implements ExternalThemeProvider {

    @Override
    public List<EditorPlatform.Theme> getExternalThemes() {
        return List.of(GLUON_MOBILE, GLUON_MOBILE_LIGHT, GLUON_MOBILE_DARK);
    }

    @Override
    public List<String> getExternalStylesheets() {
        // keep modena first, for user agent stylesheet
        String modenaStylesheet = EditorPlatform.Theme.MODENA.getStylesheetURLs().getFirst();
        String gluonStylesheet = GLUON_MOBILE.getStylesheetURLs().getFirst();
        String gluonThemeStylesheet = GluonEditorController.getInstance().getGluonTheme().getStylesheetURLs().getFirst();
        String gluonSwatchStylesheet = GluonEditorController.getInstance().getGluonSwatch().getStylesheetURLs().getFirst();
        String gluonDocumentStylesheet = getGluonDocumentStylesheetURL();
        return List.of(modenaStylesheet, gluonStylesheet, gluonThemeStylesheet, gluonSwatchStylesheet, gluonDocumentStylesheet);
    }

    @Override
    public boolean hasClassFromExternalPlugin(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        if (text.startsWith(GLUON_PACKAGE) || text.contains(GLUON_PACKAGE)) {
            return true;
        }
        return text.startsWith(GLUON_IMPL_PACKAGE) || text.contains(GLUON_IMPL_PACKAGE);
    }

    @Override
    public void showThemeAlert(Stage owner, EditorPlatform.Theme currentTheme, Consumer<EditorPlatform.Theme> onSuccess) {
        WarnThemeAlert.showAlertIfRequired(owner, currentTheme, onSuccess);
    }

    @Override
    public void showImportAlert(Stage owner) {
        new ImportingGluonControlsAlert(owner).showAndWait();
    }

    @Override
    public String getExternalJavadocURL() {
        return "https://docs.gluonhq.com/charm/javadoc/latest/";
    }

    public static String getGluonDocumentStylesheetURL() {
        return GLUON_DOCUMENT_STYLESHEET;
    }

}
