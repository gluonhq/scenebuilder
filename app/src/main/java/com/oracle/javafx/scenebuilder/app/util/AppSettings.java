/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
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

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppSettings {

    private static final Logger LOGGER = Logger.getLogger(AppSettings.class.getName());

    public static final String APP_ICON_16 = SceneBuilderApp.class.getResource("SceneBuilderLogo_16.png").toString();
    public static final String APP_ICON_32 = SceneBuilderApp.class.getResource("SceneBuilderLogo_32.png").toString();

    public static final String LATEST_VERSION_CHECK_URL = "http://download.gluonhq.com/scenebuilder/settings.properties";
    public static final String LATEST_VERSION_NUMBER_PROPERTY = "latestversion";

    public static final String LATEST_VERSION_INFORMATION_URL = "http://download.gluonhq.com/scenebuilder/version.json";

    public static final String DOWNLOAD_URL = "https://gluonhq.com/products/scene-builder/";

    private static String sceneBuilderVersion;
    private static String latestVersion;

    private static String latestVersionText;
    private static String latestVersionAnnouncementURL;

    private static final JsonReaderFactory readerFactory = Json.createReaderFactory(null);

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
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Cannot init SB version:", e);
        }
    }

    public static void setWindowIcon(Alert alert) {
        setWindowIcon((Stage)alert.getDialogPane().getScene().getWindow());
    }
    public static void setWindowIcon(Stage stage) {
        Image icon16 = new Image(AppSettings.APP_ICON_16);
        Image icon32 = new Image(AppSettings.APP_ICON_32);
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
            } else if (number < currentVersionNumber) {
                return false;
            }
        }
        return false;
    }

    public static void getLatestVersion(Consumer<String> consumer) {
        if (latestVersion == null) {
            var fetchTask = createFetchTask(consumer);
            new Thread(fetchTask, "GetLatestVersion").start();
        } else {
            consumer.accept(latestVersion);
        }
    }

    private static final Task<String> createFetchTask(Consumer<String> consumer) {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                LOGGER.log(Level.FINE, "Fetching latest Scenebuilder version from: {0}", LATEST_VERSION_CHECK_URL);
                Properties prop = new Properties();
                String onlineVersionNumber = null;

                URL url = null;
                try {
                    url = new URL(LATEST_VERSION_CHECK_URL);
                } catch (MalformedURLException e) {
                    LOGGER.log(Level.WARNING, "Failed to construct version check URL: ", e);
                }

                try (InputStream inputStream = url.openStream()) {
                    prop.load(inputStream);
                    onlineVersionNumber = prop.getProperty(LATEST_VERSION_NUMBER_PROPERTY);

                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Failed to load latest version number property: ", e);
                }
                return onlineVersionNumber;
            }

            protected void succeeded() { 
                String fetchedVersion = getValue();
                LOGGER.log(Level.INFO, "Latest online available version is: {0}", fetchedVersion);
                consumer.accept(fetchedVersion);
                latestVersion = fetchedVersion;
            }
        };
    }

    public static String getLatestVersionText() {
        if (latestVersionText == null) {
            updateLatestVersionInfo();
        }
        return latestVersionText;
    }

    private static void updateLatestVersionInfo() {
        try {
            URL url = new URL(LATEST_VERSION_INFORMATION_URL);

            try (JsonReader reader = readerFactory.createReader(new InputStreamReader(url.openStream()))) {
                JsonObject object = reader.readObject();
                JsonObject announcementObject = object.getJsonObject("announcement");
                latestVersionText = announcementObject.getString("text");
                latestVersionAnnouncementURL = announcementObject.getString("url");
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to read latest version json: ", e);
            }
        } catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, "Failed to construct latest version info URL: ", e);
        }
    }

    public static String getLatestVersionAnnouncementURL() {
        if (latestVersionAnnouncementURL == null) {
            updateLatestVersionInfo();
        }
        return latestVersionAnnouncementURL;
    }

    public static String getUserM2Repository() {
        String m2Path = System.getProperty("user.home") + File.separator +
                ".m2" + File.separator + "repository"; //NOI18N

        // TODO: Allow custom path for .m2

        assert m2Path != null;

        return m2Path;
    }

    public static String getTempM2Repository() {
        String m2Path = System.getProperty("java.io.tmpdir") + File.separator + "m2Tmp"; //NOI18N

        assert m2Path != null;

        return m2Path;
    }
}
