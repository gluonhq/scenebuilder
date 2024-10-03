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
package com.oracle.javafx.scenebuilder.kit.preferences;

import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.prefs.Preferences;

public abstract class PreferencesRecordGlobalBase {

    /***************************************************************************
     *                                                                         *
     * Support Classes                                                         *
     *                                                                         *
     **************************************************************************/

    public enum BackgroundImage {

        BACKGROUND_01 {

            @Override
            public String toString() {
                return I18N.getString("prefs.background.value1");
            }
        },
        BACKGROUND_02 {

            @Override
            public String toString() {
                return I18N.getString("prefs.background.value2");
            }
        },
        BACKGROUND_03 {

            @Override
            public String toString() {
                return I18N.getString("prefs.background.value3");
            }
        }
    }

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/

    public static final BackgroundImage DEFAULT_BACKGROUND_IMAGE
            = BackgroundImage.BACKGROUND_03;
    public static final Color DEFAULT_ALIGNMENT_GUIDES_COLOR = Color.RED;
    public static final Color DEFAULT_PARENT_RING_COLOR = Color.rgb(238, 168, 47);
    public static final EditorPlatform.Theme DEFAULT_THEME = EditorPlatform.DEFAULT_THEME;

    /***************************************************************************
     *                                                                         *
     * Instance fields                                                         *
     *                                                                         *
     **************************************************************************/

    protected Preferences applicationRootPreferences;

    protected double rootContainerHeight;
    protected double rootContainerWidth;
    protected BackgroundImage backgroundImage = DEFAULT_BACKGROUND_IMAGE;
    protected Color alignmentGuidesColor = DEFAULT_ALIGNMENT_GUIDES_COLOR;
    protected Color parentRingColor = DEFAULT_PARENT_RING_COLOR;

    protected EditorPlatform.Theme theme = DEFAULT_THEME;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public PreferencesRecordGlobalBase() {
    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    public void setApplicationRootPreferences(Preferences applicationRootPreferences) {
        this.applicationRootPreferences = applicationRootPreferences;
    }

    public double getRootContainerHeight() {
        return rootContainerHeight;
    }

    public void setRootContainerHeight(double value) {
        rootContainerHeight = value;
    }

    public double getRootContainerWidth() {
        return rootContainerWidth;
    }

    public void setRootContainerWidth(double value) {
        rootContainerWidth = value;
    }

    public BackgroundImage getBackgroundImage() {
        return backgroundImage;
    }

    public Image getBackgroundImageImage() { return getImage(backgroundImage); }

    public void setBackgroundImage(BackgroundImage value) {
        backgroundImage = value;
    }

    public Color getAlignmentGuidesColor() {
        return alignmentGuidesColor;
    }

    public void setAlignmentGuidesColor(Color value) {
        alignmentGuidesColor = value;
    }

    public Color getParentRingColor() {
        return parentRingColor;
    }

    public void setParentRingColor(Color value) {
        parentRingColor = value;
    }

    public EditorPlatform.Theme getTheme() { return theme; }

    public void setTheme(EditorPlatform.Theme theme) { this.theme = theme; }

    /**
     * Read data from the java preferences DB and initialize properties.
     */
    public void readFromJavaPreferences() {

        assert applicationRootPreferences != null;

        // Document size
        final double height = applicationRootPreferences.getDouble(PreferencesControllerBase.ROOT_CONTAINER_HEIGHT,
                -1);
        setRootContainerHeight(height);
        final double width = applicationRootPreferences.getDouble(PreferencesControllerBase.ROOT_CONTAINER_WIDTH,
                -1);
        setRootContainerWidth(width);

        // Background image
        final String image = applicationRootPreferences.get(PreferencesControllerBase.BACKGROUND_IMAGE,
                DEFAULT_BACKGROUND_IMAGE.name());
        setBackgroundImage(BackgroundImage.valueOf(image));

        // Alignment guides color
        final String agColor = applicationRootPreferences.get(PreferencesControllerBase.ALIGNMENT_GUIDES_COLOR,
                DEFAULT_ALIGNMENT_GUIDES_COLOR.toString());
        setAlignmentGuidesColor(Color.valueOf(agColor));

        // Parent ring color
        final String prColor = applicationRootPreferences.get(PreferencesControllerBase.PARENT_RING_COLOR,
                DEFAULT_PARENT_RING_COLOR.toString());
        setParentRingColor(Color.valueOf(prColor));

        // Document theme
        String themeName = applicationRootPreferences.get(PreferencesControllerBase.THEME, DEFAULT_THEME.name());
        theme = EditorPlatform.Theme.valueOf(themeName);
    }

    public void writeToJavaPreferences(String key) {
        assert applicationRootPreferences != null;
        assert key != null;
        switch (key) {
            case PreferencesControllerBase.ROOT_CONTAINER_HEIGHT:
                applicationRootPreferences.putDouble(PreferencesControllerBase.ROOT_CONTAINER_HEIGHT, getRootContainerHeight());
                break;
            case PreferencesControllerBase.ROOT_CONTAINER_WIDTH:
                applicationRootPreferences.putDouble(PreferencesControllerBase.ROOT_CONTAINER_WIDTH, getRootContainerWidth());
                break;
            case PreferencesControllerBase.BACKGROUND_IMAGE:
                applicationRootPreferences.put(PreferencesControllerBase.BACKGROUND_IMAGE, backgroundImage.name());
                break;
            case PreferencesControllerBase.ALIGNMENT_GUIDES_COLOR:
                applicationRootPreferences.put(PreferencesControllerBase.ALIGNMENT_GUIDES_COLOR, getAlignmentGuidesColor().toString());
                break;
            case PreferencesControllerBase.PARENT_RING_COLOR:
                applicationRootPreferences.put(PreferencesControllerBase.PARENT_RING_COLOR, getParentRingColor().toString());
                break;
            case PreferencesControllerBase.THEME:
                applicationRootPreferences.put(PreferencesControllerBase.THEME, getTheme().name());
                break;
            default:
                assert false;
                break;
        }
    }

    private static Image getImage(BackgroundImage bgi) {
        final URL url;
        switch (bgi) {
            case BACKGROUND_01:
                url = PreferencesRecordGlobalBase.class.getResource("Background-Blue-Grid.png"); //NOI18N
                break;
            case BACKGROUND_02:
                url = PreferencesRecordGlobalBase.class.getResource("Background-Neutral-Grid.png"); //NOI18N
                break;
            case BACKGROUND_03:
                url = ContentPanelController.getDefaultWorkspaceBackgroundURL();
                break;
            default:
                url = null;
                assert false;
                break;
        }
        assert url != null;
        return new Image(url.toExternalForm());
    }

}
