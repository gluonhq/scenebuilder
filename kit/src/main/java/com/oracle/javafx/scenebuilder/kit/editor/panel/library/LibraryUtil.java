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
package com.oracle.javafx.scenebuilder.kit.editor.panel.library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LibraryUtil {

    public static final String FOLDERS_LIBRARY_FILENAME = "library.folders"; //NOI18N
    private static Set<ResolvedModule> MODULES;

    LibraryUtil() {
        // no-op
    }

    public static Optional<ModuleReference> getModuleReference(Path path) {
        if (path == null) {
            return Optional.empty();
        }
        if (MODULES == null) {
            MODULES = ModuleLayer.boot().configuration().modules();
        }
        return MODULES.stream()
            .map(ResolvedModule::reference)
            .filter(r -> path.equals(r.location().map(Path::of).orElse(null)))
            .findFirst();
    }

    public static boolean isJarPath(Path path) {
        final String pathString = path.toString().toLowerCase(Locale.ROOT);
        return pathString.endsWith(".jar"); //NOI18N
    }

    public static boolean isFxmlPath(Path path) {
        final String pathString = path.toString().toLowerCase(Locale.ROOT);
        return pathString.endsWith(".fxml"); //NOI18N
    }

    public static boolean isFolderMarkerPath(Path path) {
        final String pathString = path.toString().toLowerCase(Locale.ROOT);
        return pathString.endsWith(".folders"); //NOI18N
    }

    public static List<Path> getFolderPaths(Path libraryFile) throws FileNotFoundException, IOException {
        return Files.readAllLines(libraryFile).stream()
                .map(line -> {
                    File f = new File(line);
                    if (f.exists() && f.isDirectory())
                        return f.toPath();
                    else
                        return null;
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }
}
