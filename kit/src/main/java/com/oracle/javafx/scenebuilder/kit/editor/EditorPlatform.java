/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.gluonhq.charm.glisten.visual.GlistenStyleClasses;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This class contains static methods that depends on the platform.
 *
 * @treatAsPrivate
 */
public class EditorPlatform {
    
    private static final Logger LOGGER = Logger.getLogger(EditorPlatform.class.getName());
    
    public enum OS {
        LINUX, MAC, WINDOWS;

        public static OS get() {
            return IS_LINUX ? LINUX : IS_MAC ? MAC : WINDOWS;
        }
    }

    private static final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT); //NOI18N

    static {
        LOGGER.log(Level.INFO, "Detected Operating System: {0}", osName);
    }
    
    /**
     * True if current platform is running Linux.
     */
    public static final boolean IS_LINUX = osName.contains("linux"); //NOI18N

    /**
     * True if current platform is running Mac OS X.
     */
    public static final boolean IS_MAC = osName.contains("mac"); //NOI18N

    /**
     * True if current platform is running Windows.
     */
    public static final boolean IS_WINDOWS = osName.contains("windows"); //NOI18N

    /**
     * Gluon Glisten package
     */
    public static final String GLUON_PACKAGE = "com.gluonhq.charm.glisten";

    /**
     * scene builder specific tweaks to Gluon theme
     */
    public static final String GLUON_DOCUMENT_STYLESHEET = "com/oracle/javafx/scenebuilder/app/css/GluonDocument.css";

    /**
     * Default theme
     */
    public static final Theme DEFAULT_THEME = Theme.MODENA;

    /**
     * Default Gluon Swatch
     */
    public static final EditorPlatform.GluonSwatch DEFAULT_SWATCH = GluonSwatch.BLUE;

    /**
     * Default Gluon Theme
     */
    public static final EditorPlatform.GluonTheme DEFAULT_GLUON_THEME = GluonTheme.LIGHT;

    interface StylesheetProvider {
        String getStylesheetURL();
    }

    /**
     * Themes supported by Scene Builder Kit.
     */
    public enum Theme implements StylesheetProvider {
        GLUON_MOBILE_LIGHT(GlistenStyleClasses.impl_loadResource("glisten.css")),
        GLUON_MOBILE_DARK(GlistenStyleClasses.impl_loadResource("glisten.css")),
        MODENA("com/sun/javafx/scene/control/skin/modena/modena.bss"),
        MODENA_TOUCH("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-touch.css"),
        MODENA_HIGH_CONTRAST_BLACK_ON_WHITE("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-highContrast-blackOnWhite.css"),
        MODENA_HIGH_CONTRAST_WHITE_ON_BLACK("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-highContrast-whiteOnBlack.css"),
        MODENA_HIGH_CONTRAST_YELLOW_ON_BLACK("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-highContrast-yellowOnBlack.css"),
        MODENA_TOUCH_HIGH_CONTRAST_BLACK_ON_WHITE("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-touch-highContrast-blackOnWhite.css"),
        MODENA_TOUCH_HIGH_CONTRAST_WHITE_ON_BLACK("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-touch-highContrast-whiteOnBlack.css"),
        MODENA_TOUCH_HIGH_CONTRAST_YELLOW_ON_BLACK("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-touch-highContrast-yellowOnBlack.css"),
        CASPIAN("com/sun/javafx/scene/control/skin/caspian/caspian.bss"),
        CASPIAN_HIGH_CONTRAST("com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-highContrast.css"),
        CASPIAN_EMBEDDED("com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded.css"),
        CASPIAN_EMBEDDED_HIGH_CONTRAST("com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded-highContrast.css"),
        CASPIAN_EMBEDDED_QVGA("com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded-qvga.css"),
        CASPIAN_EMBEDDED_QVGA_HIGH_CONTRAST("com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded-qvga-highContrast.css");

        private String url;

        Theme(String url) {
            this.url = url;
        }

        @Override
        public String getStylesheetURL() {
            return url;
        }

        @Override
        public String toString() {
            String lowerCaseName = name().toLowerCase(Locale.ROOT);
            return I18N.getString("title.theme." + lowerCaseName);
        }
    }

    /**
     * Gluon Swatch
     */
    public enum GluonSwatch implements StylesheetProvider {
        BLUE,
        CYAN,
        DEEP_ORANGE,
        DEEP_PURPLE,
        GREEN,
        INDIGO,
        LIGHT_BLUE,
        PINK,
        PURPLE,
        RED,
        TEAL,
        LIGHT_GREEN,
        LIME,
        YELLOW,
        AMBER,
        ORANGE,
        BROWN,
        GREY,
        BLUE_GREY;

        private static final String PRIMARY_SWATCH_500_STR = "-primary-swatch-500:";

        Color color;

        @Override
        public String toString() {
            String lowerCaseSwatch = "title.gluon.swatch." + name().toLowerCase(Locale.ROOT);
            return I18N.getString(lowerCaseSwatch);
        }

        @Override
        public String getStylesheetURL() {
            return GlistenStyleClasses.impl_loadResource("swatch_" + name().toLowerCase(Locale.ROOT) + ".css");
        }

        public Color getColor() {
            if (color == null) {
                URL url = null;
                try {
                    url = new URL(getStylesheetURL());
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                        String s = reader.readLine();
                        while (s != null) {
                            // Remove white spaces
                            String trimmedString = s.replaceAll("\\s+", "");
                            int indexOf = trimmedString.indexOf(PRIMARY_SWATCH_500_STR);
                            if (indexOf != -1) {
                                int indexOfSemiColon = trimmedString.indexOf(";");
                                String colorString = trimmedString.substring(indexOf + PRIMARY_SWATCH_500_STR.length(), indexOfSemiColon);
                                color = Color.web(colorString);
                            }
                            s = reader.readLine();
                        }
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Failed to get color from stylesheet: ", e);
                }
            }
            return color;
        }

        public Node createGraphic() {
            Rectangle rect = new Rectangle(8, 8);
            rect.setFill(getColor());
            rect.setStroke(Color.BLACK);
            return rect;
        }
    }

    /**
     * Gluon Theme
     */
    public enum GluonTheme implements StylesheetProvider {
        LIGHT,
        DARK;

        @Override
        public String toString() {
            String lowerCaseName = "title.gluon.theme." + name().toLowerCase(Locale.ROOT);
            return I18N.getString(lowerCaseName);
        }

        @Override
        public String getStylesheetURL() {
            return GlistenStyleClasses.impl_loadResource("theme_" + name().toLowerCase(Locale.ROOT) + ".css");
        }
    }
    
    public static String getPlatformThemeStylesheetURL() {
        // Return USER_AGENT css, which is Modena for fx 8.0
        return Theme.MODENA.getStylesheetURL();
    }

    public static String getGluonDocumentStylesheetURL() {
        return GLUON_DOCUMENT_STYLESHEET;
    }

    public static boolean isModena(Theme theme) {
        return theme.toString().startsWith("MODENA");
    }
    
    public static boolean isModenaBlackonwhite(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("BLACK_ON_WHITE");
    }
    
    public static boolean isModenaWhiteonblack(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("WHITE_ON_BLACK");
    }
    
    public static boolean isModenaYellowonblack(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("YELLOW_ON_BLACK");
    }
    
    public static boolean isModenaHighContrast(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("HIGH_CONTRAST");
    }
    
    public static boolean isModenaTouch(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("TOUCH");
    }
    
    public static boolean isModenaTouchHighContrast(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("HIGH_CONTRAST")
                && theme.toString().contains("TOUCH");
    }
    
    public static boolean isCaspian(Theme theme) {
        return theme.toString().startsWith("CASPIAN");
    }

    public static boolean isGluonMobileLight(Theme theme) { return theme == Theme.GLUON_MOBILE_LIGHT; }

    public static boolean isGluonMobileDark(Theme theme) {
        return theme == Theme.GLUON_MOBILE_DARK;
    }

    /**
     * Requests the underlying platform to open a given file. On Linux, it runs
     * 'xdg-open'. On Mac, it runs 'open'. On Windows, it runs 'cmd /c start'.
     *
     * @param path path for the file to be opened
     * @throws IOException if an error occurs
     */
    public static void open(String path) throws IOException {
        List<String> args = new ArrayList<>();
        if (EditorPlatform.IS_MAC) {
            args.add("open"); //NOI18N
            args.add(path);
        } else if (EditorPlatform.IS_WINDOWS) {
            args.add("cmd"); //NOI18N
            args.add("/c"); //NOI18N
            args.add("start"); //NOI18N

            if (path.contains(" ")) { //NOI18N
                args.add("\"html\""); //NOI18N
            }

            args.add(path);
        } else if (EditorPlatform.IS_LINUX) {
            // xdg-open does fine on Ubuntu, which is a Debian.
            // I've no idea how it does with other Linux flavors.
            args.add("xdg-open"); //NOI18N
            args.add(path);
        }

        if (!args.isEmpty()) {
            executeDaemon(args, null);
        }
    }
    
    /**
     * Requests the underlying platform to "reveal" the specified folder. On
     * Linux, it runs 'xdg-open'. On Mac, it runs 'open'. On Windows, it runs
     * 'explorer /select'.
     *
     * @param filePath path for the folder to be revealed
     * @throws IOException if an error occurs
     */
    public static void revealInFileBrowser(File filePath) throws IOException {
        List<String> args = new ArrayList<>();
        String path = filePath.toURI().toURL().toExternalForm();
        if (EditorPlatform.IS_MAC) {
            args.add("open"); //NOI18N
            args.add("-R"); //NOI18N
            args.add(path);
        } else if (EditorPlatform.IS_WINDOWS) {
            args.add("explorer"); //NOI18N
            args.add("/select," + path); //NOI18N
        } else if (EditorPlatform.IS_LINUX) {
            
            args.add("xdg-open"); //NOI18N
            
            path = filePath.getAbsoluteFile().getParent();
            if (path == null) {
                path = "."; //NOI18N
            }
            args.add(path);
        } else {
            // Not Supported
            LOGGER.log(Level.SEVERE, "Unsupported operating system! Cannot reveal file in file browser.");
        }

        if (!args.isEmpty()) {
            executeDaemon(args, null);
        }
    }

    /**
     * Returns true if the modifier key for continuous selection is down.
     *
     * @param e mouse event to check (never null)
     * @return true if the modifier key for continuous selection is down.
     */
    public static boolean isContinuousSelectKeyDown(MouseEvent e) {
        return e.isShiftDown();
    }

    /**
     * Returns true if the modifier key for non-continuous selection is down.
     *
     * @param e mouse event to check (never null).
     * @return true if the modifier key for non-continuous selection is down.
     */
    public static boolean isNonContinousSelectKeyDown(MouseEvent e) {
        return IS_MAC ? e.isMetaDown(): e.isControlDown();
    }

    /**
     * Returns true if the jvm is running with assertions enabled.
     *
     * @return true if the jvm is running with assertions enabled.
     */
    public static boolean isAssertionEnabled() {
        return EditorPlatform.class.desiredAssertionStatus();
    }

    /*
     * Private
     */
    private static void executeDaemon(List<String> cmd, File wDir) throws IOException {
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder = builder.directory(wDir);
            Process proc = builder.start();
            int exitValue = proc.exitValue();
            if (0 != exitValue) {
                LOGGER.log(Level.SEVERE, "Error during attempt to run: {0} in {1}", new Object[] {cmd.stream().collect(Collectors.joining(" ")), wDir});
            }
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Exception during attempt to run: {0} in {1}", new Object[] {cmd.stream().collect(Collectors.joining(" ")), wDir});
            throw new IOException(ex);
        }
    }

}
