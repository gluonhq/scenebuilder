/*
 * Copyright (c) 2024, Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.app.welcomedialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

final class WelcomeDialogFilesDropHandler {

    private static final Logger LOGGER = Logger.getLogger(WelcomeDialogFilesDropHandler.class.getName());

    private final List<File> droppedFiles;
    private final List<String> toOpen;
    private final List<String> unsupportedItems;
    private Consumer<List<String>> openFiles;
    private Consumer<List<String>> handleUnsupported;

    WelcomeDialogFilesDropHandler(List<File> droppedFiles) {
        this.droppedFiles = Objects.requireNonNull(droppedFiles);
        this.toOpen = new ArrayList<>(droppedFiles.size());
        this.unsupportedItems = new ArrayList<>(droppedFiles.size());
    }

    final WelcomeDialogFilesDropHandler withSupportedFiles(Consumer<List<String>> handleOpen) {
        this.openFiles = handleOpen;
        return this;
    }

    final WelcomeDialogFilesDropHandler withUnsupportedFiles(Consumer<List<String>> unsupportedHandler) {
        this.handleUnsupported = unsupportedHandler;
        return this;
    }

    final void run() {
        analyzeDroppedItems();
        handleDropResult();
    }

    /**
     *  
     * SceneBuilder will silently ignore unsupported files and empty directories as long
     * as there is at least one FXML file to be opened. For individual FXML files, the actual 
     * error handling happens later. Here the files to be opened are not tested or validated.
     * 
     * The idea is, that if a user picks one file intentionally, a concise feedback in case
     * of the wrong file format or an empty directory can be helpful and is desired.
     * 
     * But when selecting a whole directory or multiple files, it might be helpful to ignore
     * unsupported files as long actual candidates to be opened exist. The unsupported ones
     * can result from an imprecise selection. One example could be that the user knowingly 
     * picked a resources folder with FXMLs, which also includes other files such as properties
     * or icons. In that case the user would get annoyed about an message.
     * Another case is a quick selection e. g. in Gnome Nautilus or Windows Explorer where some
     * other files are unintentionally (by accident) selected. In such case, an error message 
     * could be annoying as well.
     * 
     * Supported files are passed into the openFiles handler.
     * The error message for unsupported files is only shown when the list of files toOpen is 
     * empty.
     * 
     * Nevertheless, the fact that unsupported files were dropped and the location of unsupported 
     * files are logged.
     *
     */
    final void handleDropResult() {
        if (this.openFiles == null) {
            throw new IllegalStateException("Please configure a dropped file handling action using the withSupportedFiles(...) method.");
        }
        if (this.handleUnsupported == null) {
            throw new IllegalStateException("Please configure an action for handling of unsupported files using withUnsupportedFiles(...) method.");
        }
        
        if (!toOpen.isEmpty()) {
            LOGGER.log(Level.INFO, "Received drop event to open files...");
            openFiles.accept(toOpen);
        } else {
            LOGGER.log(Level.INFO, "Dropped object does not contain any loadable FXML files.");
            handleUnsupported.accept(unsupportedItems);
        }
        
        if (!unsupportedItems.isEmpty()) {
            LOGGER.log(Level.WARNING, "{0} unsupported items dropped.", unsupportedItems.size());
            for (var unsupportedItem : unsupportedItems) {
                LOGGER.log(Level.INFO, "Unsupported file or empty directory: {0}", unsupportedItem);
            }
        }
    }

    final void analyzeDroppedItems() {
        if (droppedFiles.isEmpty()) {
            return;
        }

        for (var file : droppedFiles) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                List<String> inDir = new ArrayList<>(children.length);
                for (var child : children) {
                    if (isFxml(child)) {
                        inDir.add(child.getAbsolutePath());
                    }
                }
                if (inDir.isEmpty()) {
                    unsupportedItems.add(file.getAbsolutePath());
                } else {
                    toOpen.addAll(inDir);
                }
            } else {
                if (isFxml(file)) {
                    toOpen.add(file.getAbsolutePath());
                } else {
                    unsupportedItems.add(file.getAbsolutePath());
                }
            }
        }
    }

    final boolean isFxml(File file) {
        if (file.isDirectory()) {
            return false;
        }
        return file.toString()
                   .toLowerCase()
                   .endsWith(".fxml");
    }
}
