/*
 * Copyright (c) 2017 Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.kit;

import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourceUtils {

    public static final String BASE = ResourceUtils.class.getResource("css/Base.css").toExternalForm();
    public static final String THEME_DARK_STYLESHEET = ResourceUtils.class.getResource("css/ThemeDark.css").toExternalForm();
    public static final String THEME_DEFAULT_STYLESHEET = ResourceUtils.class.getResource("css/ThemeDefault.css").toExternalForm();
    private static List<String> imageExtensions;
    private static List<String> audioExtensions;
    private static List<String> videoExtensions;
    private static List<String> mediaExtensions;

    public static String getToolStylesheet(ToolTheme theme) {
        switch(theme) {
            case DARK:
                return THEME_DARK_STYLESHEET;
            case DEFAULT:
                return THEME_DEFAULT_STYLESHEET;
        }
        return null;
    }

    public static void setToolTheme(ToolTheme theme, Scene scene) {
        setToolTheme(theme, scene.getStylesheets());
    }

    public static void setToolTheme(ToolTheme theme, Parent parent) {
        setToolTheme(theme, parent.getStylesheets());
    }

    private static void setToolTheme(ToolTheme theme, ObservableList<String> stylesheets) {
        if (!stylesheets.contains(BASE)) {
            stylesheets.add(BASE);
        }
        String themeStylesheet = getToolStylesheet(theme);
        if (!stylesheets.contains(themeStylesheet)) {
            stylesheets.add(themeStylesheet);
        }
    }

    public static List<String> getSupportedImageExtensions() {
        if (imageExtensions == null) {
            imageExtensions = new ArrayList<>();
            imageExtensions.add("*.jpg"); //NOI18N
            imageExtensions.add("*.jpeg"); //NOI18N
            imageExtensions.add("*.png"); //NOI18N
            imageExtensions.add("*.gif"); //NOI18N
            imageExtensions = Collections.unmodifiableList(imageExtensions);
        }
        return imageExtensions;
    }

    public static List<String> getSupportedAudioExtensions() {
        if (audioExtensions == null) {
            audioExtensions = new ArrayList<>();
            audioExtensions.add("*.aif"); //NOI18N
            audioExtensions.add("*.aiff"); //NOI18N
            audioExtensions.add("*.mp3"); //NOI18N
            audioExtensions.add("*.m4a"); //NOI18N
            audioExtensions.add("*.wav"); //NOI18N
            audioExtensions.add("*.m3u"); //NOI18N
            audioExtensions.add("*.m3u8"); //NOI18N
            audioExtensions = Collections.unmodifiableList(audioExtensions);
        }
        return audioExtensions;
    }

    public static List<String> getSupportedVideoExtensions() {
        if (videoExtensions == null) {
            videoExtensions = new ArrayList<>();
            videoExtensions.add("*.flv"); //NOI18N
            videoExtensions.add("*.fxm"); //NOI18N
            videoExtensions.add("*.mp4"); //NOI18N
            videoExtensions.add("*.m4v"); //NOI18N
            videoExtensions = Collections.unmodifiableList(videoExtensions);
        }
        return videoExtensions;
    }

    public static List<String> getSupportedMediaExtensions() {
        if (mediaExtensions == null) {
            mediaExtensions = new ArrayList<>();
            mediaExtensions.addAll(getSupportedImageExtensions());
            mediaExtensions.addAll(getSupportedAudioExtensions());
            mediaExtensions.addAll(getSupportedVideoExtensions());
            mediaExtensions = Collections.unmodifiableList(mediaExtensions);
        }
        return mediaExtensions;
    }
}
