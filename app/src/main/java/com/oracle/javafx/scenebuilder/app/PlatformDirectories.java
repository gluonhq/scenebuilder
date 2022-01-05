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

public interface PlatformDirectories {
    
    /**
     * @return the root location where application data shall be stored on the
     *         corresponding platform.
     */
    String getApplicationDataRoot();
    
    /**
     * Scene Builder provides the option to hold custom JAR files in a user library.
     * This method provides the full path to the location of the user library
     * directory. Typically, the user library directory (Library) is placed inside
     * the application data folder.
     */
    String getUserLibraryFolder();
    
    /**
     * @return Provides the name of the by default version specific application data
     *         sub directory. This folder is will be located inside the application
     *         data root folder.
     */
    String getApplicationDataSubFolder();

    /**
     * In previous versions, Scene Builder stored its files in a directory without
     * version number. Hence, in some cases it might be helpful to control when the
     * version number is used or not.
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
    String getApplicationDataFolder();
    
    /**
     * 
     * @return
     */
    String getLogFolder();
    
    String getMessageBoxFolder();
}
