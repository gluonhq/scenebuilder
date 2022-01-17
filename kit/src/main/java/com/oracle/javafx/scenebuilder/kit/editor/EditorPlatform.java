/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
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

import com.gluonhq.charm.glisten.visual.GlistenStyleClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    
    public enum OS {
        LINUX, MAC, WINDOWS;

        public static OS get() {
            return IS_LINUX ? LINUX : IS_MAC ? MAC : WINDOWS;
        }
    }

    private static final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT); //NOI18N

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
     * This URL is where you go when the user takes Scene Builder Help action (shortcut F1)
     */
    public static final String DOCUMENTATION_URL = "https://docs.oracle.com/javafx/index.html"; //NOI18N

    /**
     * Javadoc home (for Inspector and CSS Analyzer properties)
     */
    public final static String JAVADOC_HOME = "https://openjfx.io/javadoc/11/"; //NOI18N

    /**
     * Gluon javadoc home (for Inspector and CSS Analyzer properties)
     */
    public final static String GLUON_JAVADOC_HOME = "http://docs.gluonhq.com/charm/javadoc/" + "latest" +"/"; //NOI18N

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
        GLUON_MOBILE_LIGHT(GlistenStyleClasses.impl_loadResource("glisten.gls")),
        GLUON_MOBILE_DARK(GlistenStyleClasses.impl_loadResource("glisten.gls")),
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
            return GlistenStyleClasses.impl_loadResource("swatch_" + name().toLowerCase(Locale.ROOT) + ".gls");
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
                    e.printStackTrace();
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
            return GlistenStyleClasses.impl_loadResource("theme_" + name().toLowerCase(Locale.ROOT) + ".gls");
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
     * Linux, it runs 'nautilus'. On Mac, it runs 'open'. On Windows, it runs
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
            // nautilus does fine on Ubuntu, which is a Debian.
            // I've no idea how it does with other Linux flavors.
            args.add("nautilus"); //NOI18N
            // The nautilus that comes with Ubuntu up to 11.04 included doesn't
            // take a file path as parameter (you get an error popup), you must
            // provide a dir path.
            // Starting with Ubuntu 11.10 (the first based on kernel 3.x) a
            // file path is well managed.
            int osVersionNumerical = Integer.parseInt(System.getProperty("os.version").substring(0, 1)); //NOI18N
            if (osVersionNumerical < 3) {
                // Case Ubuntu 10.04 to 11.04: What you provide to nautilus is
                // the name of the directory containing the file you want to see
                // listed. See DTL-5384.
                path = filePath.getAbsoluteFile().getParent();
                if (path == null) {
                    path = "."; //NOI18N
                }
            }
            args.add(path);
        } else {
            // Not Supported
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
            builder.start();
        } catch (RuntimeException ex) {
            throw new IOException(ex);
        }
    }

}
