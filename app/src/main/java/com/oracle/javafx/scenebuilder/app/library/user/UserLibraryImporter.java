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
package com.oracle.javafx.scenebuilder.app.library.user;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.oracle.javafx.scenebuilder.app.AppPlatform;
import com.oracle.javafx.scenebuilder.app.AppPlatformDirectories;
import com.oracle.javafx.scenebuilder.app.preferences.AppVersion;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesImporter;
import com.oracle.javafx.scenebuilder.app.util.AppSettings;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

public final class UserLibraryImporter {

    protected static final String PREF_IMPORT_USER_LIBRARY = "IMPORT_USER_LIBRARY";
    
    private final Logger logger = Logger.getLogger(PreferencesImporter.class.getName());
    private final Optional<AppVersion> version;
    private final Preferences preferences;
    private final AppPlatformDirectories appDirectories;

    public UserLibraryImporter(Preferences applicationPreferences) {
        this(AppPlatform.getAppDirectories(), applicationPreferences);
    }

    public UserLibraryImporter(AppPlatformDirectories directories, Preferences applicationPreferences) {
        this(AppVersion.fromString(AppSettings.getSceneBuilderVersion()), directories, applicationPreferences);
    }

    public UserLibraryImporter(Optional<AppVersion> appVersion, AppPlatformDirectories appDirectories, Preferences applicationPreferences) {
        this.version = Objects.requireNonNull(appVersion);
        this.preferences = Objects.requireNonNull(applicationPreferences);
        this.appDirectories = Objects.requireNonNull(appDirectories);
    }
    
    AppPlatformDirectories getPlatformDirectories() {
        return this.appDirectories;
    }

    /**
     * The library import is only performed when the property key
     * {@code IMPORT_USER_LIBRARY} does not exist or is defined as "true".
     *  
     * @return {@link ImportResult} describing how the activity was completed.
     */
    ImportResult performImportWhenDesired() {
        boolean forcedImport = importForced();
        boolean requiresImport = preferences.getBoolean(PREF_IMPORT_USER_LIBRARY, true);
        if (requiresImport || forcedImport) {
            if (forcedImport) {
                logger.log(Level.FINE, "detected -DforceImport=true");
            }
            return importPreviousVersionUserLibrary();
        }
        logger.log(Level.INFO, "Previous version user library will not be imported.");
        return ImportResult.SKIPPED;
    }

    private boolean importForced() {
        String forceImport = System.getProperty("forceImport", "false");
        return "true".equalsIgnoreCase(forceImport);
    }
    
    void clearImportDecision() {
        preferences.remove(PREF_IMPORT_USER_LIBRARY);
    }

    private void documentThatImportIsDone() {
        preferences.putBoolean(PREF_IMPORT_USER_LIBRARY, false);
    }

    ImportResult importPreviousVersionUserLibrary() {
        Path appData = appDirectories.getApplicationDataRoot();
        List<Path> candidates = collectLibraryCandidates(appData);
        Optional<Path> oldSettingsDirectory = previousVersionUserLibraryPath(candidates);
        if (oldSettingsDirectory.isPresent()) {
            Path userLib = appDirectories.getUserLibraryFolder();
            return importUserLibraryContentsFrom(oldSettingsDirectory.get(), userLib);
        }
        return ImportResult.NOTHING_TO_IMPORT;
    }

    private ImportResult importUserLibraryContentsFrom(Path sourceSettings, Path actualLibraryDir) {
        Path oldLibrary = sourceSettings.resolve("Library");
        ImportResult status = ImportResult.COMPLETED;
        if (Files.exists(oldLibrary) && Files.isDirectory(oldLibrary)) {
            logger.log(Level.INFO, oldLibrary.toAbsolutePath().toString());
            try {
                copyDirectory(oldLibrary, actualLibraryDir);
            } catch (IOException e) {
                String template = "Failed to import user library from %s";
                String message = String.format(template, oldLibrary);
                logger.log(Level.SEVERE, message, e);
                status = ImportResult.COMPLETED_WITH_ERRORS;
            }
        }
        documentThatImportIsDone();
        return status;
    }

    void copyDirectory(Path sourceDir, Path destinationDir) throws IOException {
        Files.walk(sourceDir).forEach(item -> 
            adjustDestinationPathAndTryCopy(sourceDir, destinationDir, item));
    }

    void adjustDestinationPathAndTryCopy(Path sourceDir, Path destinationDir, Path source) {
        Path relativeSrc = sourceDir.relativize(source);
        Path destination = destinationDir.resolve(relativeSrc).toAbsolutePath();
        tryFileCopy(source, destination);
    }

    void tryFileCopy(Path source, Path destination) {
        try {
            createDirectoryIfNeeded(destination);
            copyFile(source, destination);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Import failed", e);
        }
    }

    void copyFile(Path source, Path destination) throws IOException {
        if (!Files.isDirectory(source)) {
            logger.log(Level.INFO, "Importing {0} into {1}", new Object[] { source, destination });
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    void createDirectoryIfNeeded(Path destination) throws IOException {
        if (Files.isDirectory(destination) && Files.notExists(destination)) {
            Files.createDirectories(destination);
        }
    }

    private List<Path> collectLibraryCandidates(Path appData) {
        try (Stream<Path> files = Files.list(appData)) {
            return files.filter(Files::isDirectory).collect(Collectors.toList());
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    "Error while searching previous version user library locations.", e);
        }
        return Collections.emptyList();
    }

    Optional<Path> previousVersionUserLibraryPath(List<Path> candidates) {
        Objects.requireNonNull(candidates, "list of path candidates must not be null");
        Map<AppVersion, Path> previousVersionLibDirs = collectPreviousVersionLibraryDirs(candidates);
        return previousVersionLibDirs.entrySet().stream()
                                         .sorted((a,b) -> b.getKey().compareTo(a.getKey()))
                                         .map(e -> e.getValue())
                                         .findFirst();
    }

    Map<AppVersion, Path> collectPreviousVersionLibraryDirs(List<Path> candidates) {
        if (version.isEmpty()) {
            return Collections.emptyMap();
        }
        AppVersion currentVersion = version.get();
        String legacyDirName = appDirectories.getApplicationDataSubFolder(false);
        Map<AppVersion, Path> sceneBuilderDirs = new HashMap<>();
        for (Path candidate : candidates) {
            String name = candidate.getFileName().toString();
            if (name.startsWith(legacyDirName)) {
                if (name.contains("-")) {
                    String[] parts = name.split("[-]");
                    if (parts.length == 2) {
                        AppVersion.fromString(parts[1])
                                  .ifPresent(v -> {
                                      if (v.compareTo(currentVersion) < 0) {
                                          sceneBuilderDirs.put(v, candidate);
                                      }
                                  });
                    }
                } else {
                    sceneBuilderDirs.put(new AppVersion(2, 0), candidate);
                }
            }
        }
        
        return sceneBuilderDirs;
    }

    static class UserLibraryImportTask extends Task<ImportResult> {
        
        private final Logger logger = Logger.getLogger(UserLibraryImportTask.class.getName());
        private final UserLibraryImporter userLibImporter;

        UserLibraryImportTask(UserLibraryImporter userLibImporter) {
            this.userLibImporter = userLibImporter;
            setOnFailed(this::logException);
            setOnSucceeded(this::logSuccess);
        }

        @Override
        protected ImportResult call() throws Exception {
            return userLibImporter.performImportWhenDesired();
        }

        private void logException(WorkerStateEvent event) {
            if (getException() != null) {
                logger.log(Level.SEVERE,
                        "Import of User Library failed with error!", getException());
            } else {
                logger.log(Level.SEVERE,
                        "Import of User Library failed with error!");
            }
        }

        private void logSuccess(WorkerStateEvent event) {
            logger.log(Level.SEVERE, "User Library Import finished with: {0}", getValue());
        }

        UserLibraryImporter getUserLibImporter() {
            return userLibImporter;
        }
    }

    public static Task<ImportResult> createImportTask() {
        return new UserLibraryImportTask(PreferencesController.getSingleton()
                                                              .getUserLibraryImporter());
    }

    enum ImportResult {
        /**
         * User library was successfully imported. 
         */
        COMPLETED,

        
        /**
         * User library import failed with an error.
         */
        FAILED,

        /**
         * User library import was skipped as user opted out.
         */
        SKIPPED,

        /**
         * No older user library directory found so the import was skipped. 
         */
        NOTHING_TO_IMPORT, 

        /**
         * User library was imported but there were errors importing individual JARs.
         */
        COMPLETED_WITH_ERRORS;
    }
}
