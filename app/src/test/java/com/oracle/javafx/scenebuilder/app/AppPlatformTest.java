/*
 * Copyright (c) 2016, 2022 Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.oracle.javafx.scenebuilder.app.util.AppSettings;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform.OS;

class AppPlatformTest {

    private final Map<String, String> testEnvironment = new HashMap<>();

    private final Properties testProperties = new Properties();

    @BeforeEach
    void prepare() {
        AppPlatform.clear();

        testEnvironment.clear();
        testEnvironment.put("APPDATA", "C:\\Users\\UserName\\AppData\\Roaming");

        testProperties.clear();
        testProperties.put("user.home", "/home/user");
    }

    @ParameterizedTest
    @CsvSource({ "WINDOWS, 18.0.0,          C:\\Users\\UserName\\AppData\\Roaming\\Scene Builder\\18.0.0",
            "LINUX,   19.1.2-SNAPSHOT, /home/user/.scenebuilder/19.1.2-SNAPSHOT",
            "MAC,     17,              /home/user/Library/Application Support/Scene Builder/17" })
    void that_applications_data_folder_matches_OS_requirements(OS operatingSystem, String version,
            String expectedPath) {
        String appDir = AppPlatform.getApplicationDataFolder(testEnvironment, testProperties, operatingSystem, version);
        assertEquals(expectedPath, appDir);
    }

    @Test
    void that_library_path_is_subdir_of_appdata() {
        Path appDir = Path.of(AppPlatform.getApplicationDataFolder());
        Path expectedLibDir = appDir.resolve("Library");
        Path generatedLibDir = Path.of(AppPlatform.getUserLibraryFolder());

        assertEquals(expectedLibDir, generatedLibDir);
    }

    @Test
    @EnabledOnOs(value = org.junit.jupiter.api.condition.OS.WINDOWS)
    void that_application_settings_directory_is_created_properly_on_windows() {
        Path appDir = Path.of(AppPlatform.getApplicationDataFolder());
        Path expected = Path.of(System.getenv("APPDATA") + "\\Scene Builder\\" + AppSettings.getSceneBuilderVersion());
        assertEquals(expected, appDir);
    }

    @Test
    @EnabledOnOs(value = org.junit.jupiter.api.condition.OS.LINUX)
    void that_application_settings_directory_is_created_properly_on_linux() {
        Path appDir = Path.of(AppPlatform.getApplicationDataFolder());
        Path expected = Path
                .of(System.getProperty("user.home") + "/.scenebuilder/" + AppSettings.getSceneBuilderVersion());
        assertEquals(expected, appDir);
    }

    @Test
    @EnabledOnOs(value = org.junit.jupiter.api.condition.OS.MAC)
    void that_application_settings_directory_is_created_properly_on_mac() {
        Path appDir = Path.of(AppPlatform.getApplicationDataFolder());
        Path expected = Path.of(System.getProperty("user.home") + "/Library/Application Support/Scene Builder/"
                + AppSettings.getSceneBuilderVersion());
        assertEquals(expected, appDir);
    }

    @Test
    void that_user_library_folder_resides_in_applications_data_folder() {
        // init app data folder first as this is the basis
        AppPlatform.getApplicationDataFolder(testEnvironment, testProperties, OS.WINDOWS, "19.0.0-SNAPSHOT");
        String libraryDir = AppPlatform.getUserLibraryFolder(OS.WINDOWS);
        assertEquals("C:\\Users\\UserName\\AppData\\Roaming\\Scene Builder\\19.0.0-SNAPSHOT\\Library", libraryDir);
    }

    @Test
    void that_messagebox_folder_resides_in_applications_data_folder() {
        // init app data folder first as this is the basis
        AppPlatform.getApplicationDataFolder(testEnvironment, testProperties, OS.WINDOWS, "19.0.0-SNAPSHOT");
        Path messageBoxDir = Path.of(AppPlatform.getMessageBoxFolder());
        Path expectedDir = Path.of("C:\\Users\\UserName\\AppData\\Roaming\\Scene Builder\\19.0.0-SNAPSHOT\\MB");
        assertEquals(expectedDir, messageBoxDir);
    }

    @Test
    void that_logfiles_are_stored_in_userhome_scenebuilder_logs() {
        String expectedLogDir = Path.of(System.getProperty("user.home"), ".scenebuilder", "logs").toString();
        assertEquals(expectedLogDir, AppPlatform.getLogFolder());
    }
}
