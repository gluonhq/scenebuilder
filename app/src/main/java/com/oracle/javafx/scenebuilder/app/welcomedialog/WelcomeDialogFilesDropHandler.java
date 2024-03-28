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
