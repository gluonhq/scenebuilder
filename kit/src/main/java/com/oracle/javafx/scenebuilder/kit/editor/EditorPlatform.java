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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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
        LOGGER.log(Level.FINE, "Detected Operating System: {0}", osName);
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
     * Default theme
     */
    public static final Theme DEFAULT_THEME = Theme.MODENA;

    /**
     * Themes supported by Scene Builder Kit.
     */
    public record Theme(String name, String value, String... stylesheetURLs) {

        private static final String MODENA_PATH = "com/sun/javafx/scene/control/skin/modena/";
        private static final String CASPIAN_PATH = "com/sun/javafx/scene/control/skin/caspian/";

        public static final Theme MODENA =
            new Theme("MODENA", I18N.getString("title.theme.modena"), MODENA_PATH + "modena.css");
        public static final Theme MODENA_TOUCH =
            new Theme("MODENA_TOUCH", I18N.getString("title.theme.modena_touch"), MODENA_PATH + "modena.css", MODENA_PATH + "touch.css");
        public static final Theme MODENA_HIGH_CONTRAST_BLACK_ON_WHITE =
            new Theme("MODENA_HIGH_CONTRAST_BLACK_ON_WHITE", I18N.getString("title.theme.modena_high_contrast_black_on_white"), MODENA_PATH + "modena.css", MODENA_PATH + "blackOnWhite.css");
        public static final Theme MODENA_HIGH_CONTRAST_WHITE_ON_BLACK =
            new Theme("MODENA_HIGH_CONTRAST_WHITE_ON_BLACK", I18N.getString("title.theme.modena_high_contrast_white_on_black"), MODENA_PATH + "modena.css", MODENA_PATH + "whiteOnBlack.css");
        public static final Theme MODENA_HIGH_CONTRAST_YELLOW_ON_BLACK =
            new Theme("MODENA_HIGH_CONTRAST_YELLOW_ON_BLACK", I18N.getString("title.theme.modena_high_contrast_yellow_on_black"), MODENA_PATH + "modena.css", MODENA_PATH + "yellowOnBlack.css");
        public static final Theme MODENA_TOUCH_HIGH_CONTRAST_BLACK_ON_WHITE =
            new Theme("MODENA_TOUCH_HIGH_CONTRAST_BLACK_ON_WHITE", I18N.getString("title.theme.modena_touch_high_contrast_black_on_white"), MODENA_PATH + "modena.css", MODENA_PATH + "touch.css", MODENA_PATH + "blackOnWhite.css");
        public static final Theme MODENA_TOUCH_HIGH_CONTRAST_WHITE_ON_BLACK =
            new Theme("MODENA_TOUCH_HIGH_CONTRAST_WHITE_ON_BLACK", I18N.getString("title.theme.modena_touch_high_contrast_white_on_black"), MODENA_PATH + "modena.css", MODENA_PATH + "touch.css", MODENA_PATH + "whiteOnBlack.css");
        public static final Theme MODENA_TOUCH_HIGH_CONTRAST_YELLOW_ON_BLACK =
            new Theme("MODENA_TOUCH_HIGH_CONTRAST_YELLOW_ON_BLACK", I18N.getString("title.theme.modena_touch_high_contrast_yellow_on_black"), MODENA_PATH + "modena.css", MODENA_PATH + "touch.css", MODENA_PATH + "yellowOnBlack.css");
        public static final Theme CASPIAN =
            new Theme("CASPIAN", I18N.getString("title.theme.caspian"), CASPIAN_PATH + "caspian.css");
        public static final Theme CASPIAN_HIGH_CONTRAST =
            new Theme("CASPIAN_HIGH_CONTRAST", I18N.getString("title.theme.caspian_high_contrast"), CASPIAN_PATH + "caspian.css", CASPIAN_PATH + "highcontrast.css");
        public static final Theme CASPIAN_EMBEDDED =
            new Theme("CASPIAN_EMBEDDED", I18N.getString("title.theme.caspian_embedded"), CASPIAN_PATH + "caspian.css", CASPIAN_PATH + "embedded.css");
        public static final Theme CASPIAN_EMBEDDED_HIGH_CONTRAST =
            new Theme("CASPIAN_EMBEDDED_HIGH_CONTRAST", I18N.getString("title.theme.caspian_embedded_high_contrast"), CASPIAN_PATH + "caspian.css", CASPIAN_PATH + "embedded.css", CASPIAN_PATH + "highcontrast.css");
        public static final Theme CASPIAN_EMBEDDED_QVGA =
            new Theme("CASPIAN_EMBEDDED_QVGA", I18N.getString("title.theme.caspian_embedded_qvga"), CASPIAN_PATH + "caspian.css", CASPIAN_PATH + "embedded.css", CASPIAN_PATH + "embedded-qvga.css");
        public static final Theme CASPIAN_EMBEDDED_QVGA_HIGH_CONTRAST =
            new Theme("CASPIAN_EMBEDDED_QVGA_HIGH_CONTRAST", I18N.getString("title.theme.caspian_embedded_qvga_high_contrast"), CASPIAN_PATH + "caspian.css", CASPIAN_PATH + "embedded.css", CASPIAN_PATH + "embedded-qvga.css", CASPIAN_PATH + "highcontrast.css");

        @Override
        public String toString() {
            return value;
        }

        public List<String> getStylesheetURLs() {
            return List.of(stylesheetURLs);
        }

        public static List<Theme> getThemeList() {
            List<Theme> themeList = new ArrayList<>(List.of(Theme.MODENA, Theme.MODENA_TOUCH, Theme.MODENA_HIGH_CONTRAST_BLACK_ON_WHITE,
                Theme.MODENA_HIGH_CONTRAST_WHITE_ON_BLACK, Theme.MODENA_HIGH_CONTRAST_YELLOW_ON_BLACK, Theme.MODENA_TOUCH_HIGH_CONTRAST_BLACK_ON_WHITE,
                Theme.MODENA_TOUCH_HIGH_CONTRAST_WHITE_ON_BLACK, Theme.MODENA_HIGH_CONTRAST_YELLOW_ON_BLACK,
                Theme.CASPIAN, Theme.CASPIAN_EMBEDDED, Theme.CASPIAN_EMBEDDED_HIGH_CONTRAST, Theme.CASPIAN_EMBEDDED_QVGA, Theme.CASPIAN_EMBEDDED_QVGA_HIGH_CONTRAST));
            themeList.addAll(0, getExternalThemes());
            return themeList;
        }

        public static Theme valueOf(String themeName) {
            return getThemeList().stream()
                .filter(t -> t.name().equals(themeName))
                .findFirst()
                .orElse(DEFAULT_THEME);
        }
    }

    EditorPlatform() {
        // no-op
    }

    public static boolean isPlatformThemeStylesheetURL(String stylesheetURL) {
        // Return USER_AGENT css, which is Modena for fx 8.0
        return stylesheetURL != null && stylesheetURL.equals(Theme.MODENA.getStylesheetURLs().getFirst());
    }

    public static String getPlatformThemeStylesheetURL() {
        // Return USER_AGENT css, which is Modena for fx 8.0
        return Theme.MODENA.getStylesheetURLs().getFirst();
    }

    public static boolean isModena(Theme theme) {
        return theme.toString().startsWith("MODENA");
    }
    
    public static boolean isModenaBlackOnWhite(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("BLACK_ON_WHITE");
    }
    
    public static boolean isModenaWhiteOnBlack(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("WHITE_ON_BLACK");
    }
    
    public static boolean isModenaYellowOnBlack(Theme theme) {
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

    /**
     * Requests the underlying platform to open a given file. On Linux, it runs
     * 'xdg-open'. On Mac, it runs 'open'. On Windows, it runs 'cmd /c start'.
     *
     * @param path path for the file to be opened
     * @throws IOException in case the application called failed to open due to an error.
     * @throws FileBrowserRevealException in case the application opened indicates an error (unexpected return code).
     */
    public static void open(String path) throws IOException, FileBrowserRevealException {
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
            executeDaemon(args, null, 0);
        }
    }
    
    /**
     * Requests the underlying platform to open the specified folder in its default file system viewer. This will reveal any file therein.
     * On Linux, it runs {@code 'xdg-open'}. On Mac, it runs {@code 'open'} and on Windows it runs {@code 'explorer /select'}.
     *
     * @param filePath path for the folder to be revealed
     * @throws FileBrowserRevealException This exception allows to catch exits codes != 0 from the called process.
     * @throws IOException General IOExceptions are thrown by Java System Call Processes in case of an error. 
     */
    public static void revealInFileBrowser(File filePath) throws IOException, FileBrowserRevealException {
        List<String> args = new ArrayList<>();
        String path = Paths.get(filePath.toURI()).normalize().toAbsolutePath().toString();
        int exitCodeOk = 0;
        if (EditorPlatform.IS_MAC) {
            args.add("open"); //NOI18N
            args.add("-R"); //NOI18N
            args.add(path);
        } else if (EditorPlatform.IS_WINDOWS) {
            args.add("explorer"); //NOI18N
            args.add("/select," + path); //NOI18N
            exitCodeOk = 1;
        } else if (EditorPlatform.IS_LINUX) {           
            args.add("xdg-open"); //NOI18N
            path = filePath.getAbsoluteFile().getParent();
            if (path == null) {
                path = "."; //NOI18N
            }
            args.add(path);
        } else {
            // Not Supported
            LOGGER.log(Level.SEVERE, "Unsupported operating system! Cannot reveal location {0} in file browser.", path);
        }

        if (!args.isEmpty()) {
            executeDaemon(args, null, exitCodeOk);
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
    
    /**
     * Executes a system process using the given cmd list as command line definition within the provided
     * working directory.
     * 
     * @param cmd        Command line definition as {@link List} of {@link String}
     * @param wDir       Working Directory as {@link File}
     * @param exitCodeOk Certain applications (e.g. Windows Explorer) do report exit code 1 in case
     *                   everything is okay. Hence one can configure the expected exit code here.
     * @throws IOException                Any given runtime exception is collected and re-thrown as
     *                                    IOException.
     * 
     * @throws FileBrowserRevealException This exception is only thrown if the exit code of the command
     *                                    line call is not 0. This allows to specifically react e.g. to
     *                                    invalid command line calls or to unsuccessful calls. Not every
     *                                    cmd call which ends with an error code != 0 is creating an
     *                                    exception.
     */
    private static void executeDaemon(List<String> cmd, File wDir, int exitCodeOk)
            throws IOException, FileBrowserRevealException {
        var cmdLine = String.join(" ", cmd);
        long timeoutSec = 5;
        try {
            int exitValue = new Cmd().exec(cmd, wDir, timeoutSec);
            if (exitCodeOk != exitValue) {
                LOGGER.log(Level.SEVERE, "Error during attempt to run: {0} in {1}", new Object[] { cmdLine, wDir });
                throw new FileBrowserRevealException(
                        "The command to reval the file exited with an error (exitValue=%s).\nCommand: %s\nWorking Dir: %s"
                                .formatted(Integer.toString(exitValue), cmdLine, wDir));
            } else {
                LOGGER.log(Level.FINE, "Successfully executed command: {0} in {1}", new Object[] { cmdLine, wDir });
            }
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Unknown error during attempt to run: {0} in {1}", new Object[] { cmdLine, wDir });
            throw new IOException(ex);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Process timeout after {0}s: {1} in {2}",
                    new Object[] { timeoutSec, cmdLine, wDir });
            Thread.currentThread().interrupt();
            String msg = "The command to reveal the file exited with an error after timeout.\nCommand: %s\nWorking Dir: %s\nTimeout (s):%s"
                    .formatted(cmdLine, wDir, timeoutSec);
            String detailMsg = msg + "\n" + e.getMessage();
            throw new IOException(detailMsg);
        }
    }

    //  External
    private static final Collection<ExternalThemeProvider> externalThemeProviders = getExternalThemeProviders();

    private static List<EditorPlatform.Theme> getExternalThemes() {
        List<EditorPlatform.Theme> result = new ArrayList<>();
        for (ExternalThemeProvider provider : externalThemeProviders) {
            result.addAll(provider.getExternalThemes());
        }
        return result;
    }

    public static List<String> getStylesheetsForTheme(Theme theme) {
        if (getExternalThemes().contains(theme)) {
            for (ExternalThemeProvider provider : externalThemeProviders) {
                return provider.getExternalStylesheets();
            }
        }
        return List.of();
    }

    public static void showThemeAlert(Stage owner, EditorPlatform.Theme currentTheme, Consumer<EditorPlatform.Theme> onSuccess) {
        for (ExternalThemeProvider provider : externalThemeProviders) {
            provider.showThemeAlert(owner, currentTheme, onSuccess);
        }
    }

    public static void showImportAlert(Stage owner) {
        for (ExternalThemeProvider provider : externalThemeProviders) {
            provider.showImportAlert(owner);
        }
    }

    public static boolean hasClassFromExternalPlugin(String text) {
        for (ExternalThemeProvider provider : externalThemeProviders) {
            if (provider.hasClassFromExternalPlugin(text)) {
                return true;
            }
        }
        return false;
    }

    public static Optional<String> getExternalJavadocURL(String classname) {
        for (ExternalThemeProvider provider : externalThemeProviders) {
            if (provider.hasClassFromExternalPlugin(classname)) {
                return Optional.of(provider.getExternalJavadocURL());
            }
        }
        return Optional.empty();
    }

    private static Collection<ExternalThemeProvider> getExternalThemeProviders() {
        ServiceLoader<ExternalThemeProvider> loader = ServiceLoader.load(ExternalThemeProvider.class);
        Collection<ExternalThemeProvider> providers = new ArrayList<>();
        loader.iterator().forEachRemaining(providers::add);
        return providers;
    }
}
