/*
 * Copyright (c) 2020, codebb.gr and/or its affiliates.
 * Copyright (c) 2016-2017 Gluon and/or its affiliates.
 * Check license.txt for license
 */

package com.oracle.javafx.scenebuilder.app.util;

import com.oracle.javafx.scenebuilder.app.SceneBuilderApp;
import com.oracle.javafx.scenebuilder.app.about.AboutWindowController;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.function.Consumer;

public class AppSettings {
    public static final String APP_ICON_16 = SceneBuilderApp.class.getResource("SceneBuilderLogo_16.png").toString();
    public static final String APP_ICON_32 = SceneBuilderApp.class.getResource("SceneBuilderLogo_32.png").toString();

    public static final String LATEST_VERSION_CHECK_URL = "http://download.gluonhq.com/scenebuilder/settings.properties";
    public static final String LATEST_VERSION_NUMBER_PROPERTY = "latestversion";

    public static final String LATEST_VERSION_INFORMATION_URL = "http://download.gluonhq.com/scenebuilder/version-8.4.0.json";

    public static final String DOWNLOAD_URL = "http://gluonhq.com/labs/scene-builder";

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
        } catch (IOException ex) {
            ex.printStackTrace();
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
            new Thread (() -> {
                Properties prop = new Properties();
                String onlineVersionNumber = null;

                URL url = null;
                try {
                    url = new URL(LATEST_VERSION_CHECK_URL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try (InputStream inputStream = url.openStream()) {
                    prop.load(inputStream);
                    onlineVersionNumber = prop.getProperty(LATEST_VERSION_NUMBER_PROPERTY);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                latestVersion = onlineVersionNumber;
                consumer.accept(latestVersion);
            }, "GetLatestVersion").start();
        } else {
            consumer.accept(latestVersion);
        }
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
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
