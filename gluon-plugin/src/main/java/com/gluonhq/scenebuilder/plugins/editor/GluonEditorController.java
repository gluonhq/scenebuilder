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
package com.gluonhq.scenebuilder.plugins.editor;

import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class GluonEditorController {

    private static GluonEditorController instance;

    private GluonEditorController() {}

    public static GluonEditorController getInstance() {
        if (instance == null) {
            instance = new GluonEditorController();
        }
        return instance;
    }

    private final ObjectProperty<EditorPlatform.Theme> gluonThemeProperty
        = new SimpleObjectProperty<>(GluonEditorPlatform.DEFAULT_GLUON_THEME);
    private final ObjectProperty<EditorPlatform.Theme> gluonSwatchProperty
        = new SimpleObjectProperty<>(GluonEditorPlatform.DEFAULT_GLUON_SWATCH);

    /**
     * Returns the gluon theme used by this editor
     *
     * @return the gluon theme used by this editor
     */
    public EditorPlatform.Theme getGluonTheme() {
        return gluonThemeProperty.get();
    }

    /**
     * Sets the gluon theme used by this editor.
     * Content and Preview panels sharing this editor will update
     * their content to use this new theme.
     *
     * @param theme the theme to be used in this editor
     */
    public void setGluonTheme(EditorPlatform.Theme theme) {
        gluonThemeProperty.set(theme);
    }

    /**
     * The property holding the gluon theme used by this editor
     *
     * @return the property holding the gluon theme used by this editor.
     */
    public ObjectProperty<EditorPlatform.Theme> gluonThemeProperty() {
        return gluonThemeProperty;
    }

    /**
     * Sets the gluon swatch used by this editor.
     * Content and Preview panels sharing this editor will update
     * their content to use this new swatch.
     *
     * @param swatch the swatch to be used in this editor
     */
    public void setGluonSwatch(EditorPlatform.Theme swatch) {
        gluonSwatchProperty.set(swatch);
    }

    /**
     * Returns the gluon swatch used by this editor
     *
     * @return the gluon swatch used by this editor
     */
    public EditorPlatform.Theme getGluonSwatch() {
        return gluonSwatchProperty.get();
    }

    /**
     * The property holding the gluon swatch used by this editor
     *
     * @return the property holding the gluon swatch used by this editor.
     */
    public ObjectProperty<EditorPlatform.Theme> gluonSwatchProperty() {
        return gluonSwatchProperty;
    }
}
