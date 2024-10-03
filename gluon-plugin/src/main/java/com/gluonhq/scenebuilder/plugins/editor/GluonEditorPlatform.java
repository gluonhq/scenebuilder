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

import com.gluonhq.charm.glisten.visual.GlistenStyleClasses;
import com.gluonhq.scenebuilder.plugins.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GluonEditorPlatform {

    private static final Logger LOGGER = Logger.getLogger(GluonEditorPlatform.class.getName());

    /**
     * Gluon Mobile
     */
    public static final EditorPlatform.Theme GLUON_MOBILE =
        new EditorPlatform.Theme("GLUON_MOBILE", "Gluon Mobile", GlistenStyleClasses.impl_loadResource("glisten.css"));

    /**
     * Gluon Theme
     */
    public static final EditorPlatform.Theme GLUON_MOBILE_LIGHT =
        new EditorPlatform.Theme("GLUON_MOBILE_LIGHT", I18N.getString("title.gluon.theme.light"), GlistenStyleClasses.impl_loadResource("theme_light.css"));
    public static final EditorPlatform.Theme GLUON_MOBILE_DARK =
        new EditorPlatform.Theme("GLUON_MOBILE_DARK", I18N.getString("title.gluon.theme.dark"), GlistenStyleClasses.impl_loadResource("theme_dark.css"));

    /**
     * Gluon Swatch
     */
    public static final EditorPlatform.Theme GLUON_SWATCH_AMBER =
        new EditorPlatform.Theme("AMBER", I18N.getString("title.gluon.swatch.amber"), GlistenStyleClasses.impl_loadResource("swatch_amber.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_BLUE =
        new EditorPlatform.Theme("BLUE", I18N.getString("title.gluon.swatch.blue"), GlistenStyleClasses.impl_loadResource("swatch_blue.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_BLUE_GREY =
        new EditorPlatform.Theme("BLUE_GREY", I18N.getString("title.gluon.swatch.blue_grey"), GlistenStyleClasses.impl_loadResource("swatch_blue_grey.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_BROWN =
        new EditorPlatform.Theme("BROWN", I18N.getString("title.gluon.swatch.brown"), GlistenStyleClasses.impl_loadResource("swatch_brown.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_CYAN =
        new EditorPlatform.Theme("CYAN", I18N.getString("title.gluon.swatch.cyan"), GlistenStyleClasses.impl_loadResource("swatch_cyan.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_DEEP_ORANGE =
        new EditorPlatform.Theme("DEEP_ORANGE", I18N.getString("title.gluon.swatch.deep_orange"), GlistenStyleClasses.impl_loadResource("swatch_deep_orange.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_DEEP_PURPLE =
        new EditorPlatform.Theme("DEEP_PURPLE", I18N.getString("title.gluon.swatch.deep_purple"), GlistenStyleClasses.impl_loadResource("swatch_deep_purple.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_GREEN =
        new EditorPlatform.Theme("GREEN", I18N.getString("title.gluon.swatch.green"), GlistenStyleClasses.impl_loadResource("swatch_green.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_GREY =
        new EditorPlatform.Theme("GREY", I18N.getString("title.gluon.swatch.grey"), GlistenStyleClasses.impl_loadResource("swatch_grey.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_INDIGO =
        new EditorPlatform.Theme("INDIGO", I18N.getString("title.gluon.swatch.indigo"), GlistenStyleClasses.impl_loadResource("swatch_indigo.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_LIGHT_BLUE =
        new EditorPlatform.Theme("LIGHT_BLUE", I18N.getString("title.gluon.swatch.light_blue"), GlistenStyleClasses.impl_loadResource("swatch_light_blue.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_LIGHT_GREEN =
        new EditorPlatform.Theme("LIGHT_GREEN", I18N.getString("title.gluon.swatch.light_green"), GlistenStyleClasses.impl_loadResource("swatch_light_green.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_LIME =
        new EditorPlatform.Theme("LIME", I18N.getString("title.gluon.swatch.lime"), GlistenStyleClasses.impl_loadResource("swatch_lime.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_ORANGE =
        new EditorPlatform.Theme("ORANGE", I18N.getString("title.gluon.swatch.orange"), GlistenStyleClasses.impl_loadResource("swatch_orange.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_PINK =
        new EditorPlatform.Theme("PINK", I18N.getString("title.gluon.swatch.pink"), GlistenStyleClasses.impl_loadResource("swatch_pink.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_PURPLE =
        new EditorPlatform.Theme("PURPLE", I18N.getString("title.gluon.swatch.purple"), GlistenStyleClasses.impl_loadResource("swatch_purple.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_RED =
        new EditorPlatform.Theme("RED", I18N.getString("title.gluon.swatch.red"), GlistenStyleClasses.impl_loadResource("swatch_red.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_TEAL =
        new EditorPlatform.Theme("TEAL", I18N.getString("title.gluon.swatch.teal"), GlistenStyleClasses.impl_loadResource("swatch_teal.css"));
    public static final EditorPlatform.Theme GLUON_SWATCH_YELLOW =
        new EditorPlatform.Theme("YELLOW", I18N.getString("title.gluon.swatch.yellow"), GlistenStyleClasses.impl_loadResource("swatch_yellow.css"));

    public static List<EditorPlatform.Theme> getGluonSwatchList() {
        return List.of(GLUON_SWATCH_AMBER, GLUON_SWATCH_BLUE, GLUON_SWATCH_BLUE_GREY, GLUON_SWATCH_BROWN, GLUON_SWATCH_CYAN,
            GLUON_SWATCH_DEEP_ORANGE, GLUON_SWATCH_DEEP_PURPLE, GLUON_SWATCH_GREEN, GLUON_SWATCH_GREY, GLUON_SWATCH_INDIGO,
            GLUON_SWATCH_LIGHT_BLUE, GLUON_SWATCH_LIGHT_GREEN, GLUON_SWATCH_LIME, GLUON_SWATCH_ORANGE, GLUON_SWATCH_PINK,
            GLUON_SWATCH_PURPLE, GLUON_SWATCH_RED, GLUON_SWATCH_TEAL, GLUON_SWATCH_YELLOW);
    }

    public static EditorPlatform.Theme swatchValueOf(String themeName) {
        return getGluonSwatchList().stream()
            .filter(t -> t.name().equals(themeName))
            .findFirst()
            .orElse(DEFAULT_GLUON_SWATCH);
    }
    /**
     * Default Gluon Swatch
     */
    public static final EditorPlatform.Theme DEFAULT_GLUON_SWATCH = GLUON_SWATCH_BLUE;

    /**
     * Default Gluon Theme
     */
    public static final EditorPlatform.Theme DEFAULT_GLUON_THEME = GLUON_MOBILE_LIGHT;

    /**
     * Gluon Glisten package
     */
    public static final String GLUON_PACKAGE = "com.gluonhq.charm.glisten";
    public static final String GLUON_IMPL_PACKAGE = "com.gluonhq.impl.charm.glisten";

    /**
     * scene builder specific tweaks to Gluon theme
     */
    public static final String GLUON_DOCUMENT_STYLESHEET = "com/gluonhq/scenebuilder/plugins/css/GluonDocument.css";


    private static final String PRIMARY_SWATCH_500_STR = "-primary-swatch-500:";


    public static boolean isGluonMobileLight(EditorPlatform.Theme theme) { return theme == GLUON_MOBILE_LIGHT; }

    public static boolean isGluonMobileDark(EditorPlatform.Theme theme) {
        return theme == GLUON_MOBILE_DARK;
    }

    private static Color color;

    private static Color getSwatchColor(EditorPlatform.Theme theme) {
        if (color == null) {
            try {
                URL url = new URL(theme.getStylesheetURLs().getFirst());
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String s = reader.readLine();
                    while (s != null) {
                        // Remove white spaces
                        String trimmedString = s.replaceAll("\\s+", "");
                        int indexOf = trimmedString.indexOf(PRIMARY_SWATCH_500_STR);
                        if (indexOf != -1) {
                            String colorString = trimmedString.substring(indexOf + PRIMARY_SWATCH_500_STR.length(), trimmedString.indexOf(";"));
                            color = Color.web(colorString);
                            break;
                        }
                        s = reader.readLine();
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Failed reading color from stylesheet: ", e);
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to get color from stylesheet: ", e);
            }
        }
        return color;
    }

    public static Node createGraphicForSwatch(EditorPlatform.Theme theme) {
        Rectangle rect = new Rectangle(8, 8);
        rect.setFill(getSwatchColor(theme));
        rect.setStroke(Color.BLACK);
        return rect;
    }

}
