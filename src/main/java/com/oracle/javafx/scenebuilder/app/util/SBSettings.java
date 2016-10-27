/*
 * Copyright (c) 2016, Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.app.util;

import com.oracle.javafx.scenebuilder.app.SceneBuilderApp;
import com.oracle.javafx.scenebuilder.app.about.AboutWindowController;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.function.Consumer;

public class SBSettings {
    public static final String APP_ICON_16 = SceneBuilderApp.class.getResource("SceneBuilderLogo_16.png").toString();
    public static final String APP_ICON_32 = SceneBuilderApp.class.getResource("SceneBuilderLogo_32.png").toString();

    private static String sceneBuilderVersion;
    private static String latestVersion;

    static {
        initSceneBuiderVersion();
    }

    private static void initSceneBuiderVersion() {
        try (InputStream in = AboutWindowController.class.getResourceAsStream("about.properties")) {
            if (in != null) {
                Properties sbProps = new Properties();
                sbProps.load(in);
                sceneBuilderVersion = sbProps.getProperty("build.version", "UNSET");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void setWindowIcon(Stage stage) {
        Image icon16 = new Image(SBSettings.APP_ICON_16);
        Image icon32 = new Image(SBSettings.APP_ICON_32);
        stage.getIcons().addAll(icon16, icon32);
    }

    public static String getSceneBuilderVersion() {
        return sceneBuilderVersion;
    }

    public static boolean isCurrentVersionLowerThan(String version) {
        String[] versionNumbers = version.split("\\.");
        String[] currentVersionNumbers = sceneBuilderVersion.split("\\.");
        for (int i = 0; i < versionNumbers.length; ++i) {
            int number = Integer.parseInt(versionNumbers[i]);
            int currentVersionNumber = Integer.parseInt(currentVersionNumbers[i]);
            if (number > currentVersionNumber) {
                return true;
            }
        }
        return false;
    }

    public static void getLatestVersion(Consumer<String> consumer) {

            if (latestVersion == null) {
                new Thread (() -> {
                    Properties prop = new Properties();
                    String onlineVersionNumber = null;

                    URL url = null;
                    try {
                        url = new URL("http://download.gluonhq.com/scenebuilder/settings.properties");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    try (InputStream inputStream = url.openStream()) {
                        prop.load(inputStream);
                        onlineVersionNumber = prop.getProperty("latestversion");

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    latestVersion = onlineVersionNumber;
                    consumer.accept(latestVersion);
                }, "GetLatestVersion").start();
            }
            consumer.accept(latestVersion);
    }

}
