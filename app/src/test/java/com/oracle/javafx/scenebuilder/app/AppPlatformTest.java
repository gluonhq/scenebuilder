/*
 * Copyright (c) 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.Test;

import com.oracle.javafx.scenebuilder.app.util.AppSettings;

public class AppPlatformTest {
    
    private final String appVersion = AppSettings.getSceneBuilderVersion();
    
    @Test
    public void that_windows_application_data_directory_is_specific_to_version() {
        assumeTrue(getOsName().contains("windows"));
        Path appDataDir = Paths.get(AppPlatform.getAppDirectories().getApplicationDataFolder());
        Path expected   = Paths.get(System.getenv("APPDATA"))
                               .resolve("Scene Builder-"+appVersion);
        assertEquals(expected, appDataDir);
    }
    
    @Test
    public void that_mac_application_data_directory_is_specific_to_version() {
        assumeTrue(getOsName().contains("mac"));
        Path appDataDir = Paths.get(AppPlatform.getAppDirectories().getApplicationDataFolder());
        Path expected   = Paths.get(System.getProperty("user.home"))
                               .resolve("Library")
                               .resolve("Application Support")
                               .resolve("Scene Builder-"+appVersion);
        assertEquals(expected, appDataDir);
    }
    
    @Test
    public void that_linux_application_data_directory_is_specific_to_version() {
        assumeTrue(getOsName().contains("linux"));
        Path appDataDir = Paths.get(AppPlatform.getAppDirectories().getApplicationDataFolder());
        Path expected   = Paths.get(System.getProperty("user.home"))
                               .resolve(".scenebuilder-"+appVersion);
        assertEquals(expected, appDataDir);
    }
    
    @Test
    public void that_user_library_resides_in_application_settings_folder() {
        Path userLibraryFolder = Paths.get(AppPlatform.getUserLibraryFolder());
        Path expected          = Paths.get(AppPlatform.getAppDirectories().getApplicationDataFolder())
                                      .resolve("Library");
        assertEquals(expected, userLibraryFolder);
    }
    
    @Test
    public void that_logfile_stored_in_userhome_dot_scenebuilder_log_dir() {
        Path logDir   = Paths.get(AppPlatform.getLogFolder());
        Path expected = Paths.get(System.getProperty("user.home"))
                             .resolve(".scenebuilder")
                             .resolve("logs");
        assertEquals(expected, logDir);
    }
    
    @Test
    public void that_messagebox_is_placed_in_application_dir() {
        Path mboxDir   = Paths.get(AppPlatform.getMessageBoxFolder());
        Path expected  = Paths.get(AppPlatform.getAppDirectories().getApplicationDataFolder())
                              .resolve("MB");
        assertEquals(expected, mboxDir);
    }
    
    private String getOsName() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT);
    }
}
