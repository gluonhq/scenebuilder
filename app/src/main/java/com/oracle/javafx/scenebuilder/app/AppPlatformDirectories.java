/*
 * Copyright (c) 2022, Gluon and/or its affiliates.
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface AppPlatformDirectories {

    /**
     * @return the root location where application data shall be stored on the
     *         corresponding platform.
     */
    Path getApplicationDataRoot();

    /**
     * Scene Builder provides the option to hold custom JAR files in a user library.
     * This method provides the full path to the location of the user library
     * directory. Typically, the user library directory (Library) is placed inside
     * the application data folder.
     */
    Path getUserLibraryFolder();

    /**
     * @return Provides the name of the by default version specific application data
     *         sub directory. This folder is will be located inside the application
     *         data root folder.
     */
    String getApplicationDataSubFolder();

    /**
     * In previous versions, Scene Builder stored its files in a directory without
     * version number. Hence, in some cases it might be helpful to control when the
     * version number is used or not. To obtain this folder without version number
     * can be beneficial in cases, where one wants to search the application data root
     * folder for other existing settings of Scene Builder.
     * 
     * @param includeVersion If true, the version number might be a part of the sub
     *                       folder name.
     * @return Provides the name of the application data sub directory with or
     *         without version information.
     */
    String getApplicationDataSubFolder(boolean includeVersion);

    /**
     * The application data folder is usually a child folder inside the application
     * data root.
     * 
     * @return the exact location, specifically for this version of Scene Builder,
     *         where settings and arbitrary files can be placed.
     */
    Path getApplicationDataFolder();

    /**
     * Logfile location
     * @return The directory containing the application log file.
     */
    Path getLogFolder();

    /**
     * Creates the user library folder when needed.
     * If the directory cannot be created, the error is logged.
     */
    public default void createUserLibraryFolder() {
        Path libDir = getUserLibraryFolder();
        if (Files.notExists(libDir)) {
            try {
                Files.createDirectories(libDir);
            } catch (IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to create user library directory!", e);
            }
        }
    }

    /* MessageBox folder is not provided by this as MessageBox folder previously
     * was package private, here all previously public directories are accessible.
     */
}
