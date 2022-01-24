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
package com.oracle.javafx.scenebuilder.app.preferences;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.oracle.javafx.scenebuilder.app.library.user.UserLibraryImporter;
import com.oracle.javafx.scenebuilder.app.util.AppSettings;
import com.oracle.javafx.scenebuilder.kit.alert.SBAlert;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Imports all keys and children (including keys) from an arbitrary
 * {@link Preferences} node into the predefined applicationPreferences node.
 */
public final class PreferencesImporter {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/

    public static final String PREF_PERFORM_IMPORT = "PERFORM_IMPORT";

    /***************************************************************************
     *                                                                         *
     * Instance fields                                                         *
     *                                                                         *
     **************************************************************************/

    private final Logger logger = Logger.getLogger(PreferencesImporter.class.getName());
    private final Preferences target;
    private final Optional<VersionedPreferences> optionalSourceNode;
    private Runnable actionAfterImport;

    /**
     * Creates a new Preferences importer.
     * 
     * @param applicationPreferences Scene Builder {@link Preferences} node,
     *                               determines where all settings shall be imported
     *                               into.
     * @param optionalSourceNode {@link VersionedPreferences} as a possible candidate to import settings from
     */
    public PreferencesImporter(Preferences applicationPreferences, Optional<VersionedPreferences> optionalSourceNode) {
        this.target = Objects.requireNonNull(applicationPreferences);
        this.optionalSourceNode = Objects.requireNonNull(optionalSourceNode);
        this.actionAfterImport = () -> logger.log(Level.INFO, "Importing settings completed. No post-import action defined.");
    }

    /**
     * Imports preferences from an existing node of an older Scene Builder version
     * to the corresponding node of the current Scene Builder version.
     * 
     * @param oldVersion       {@link AppVersion} Version number so that, if needed,
     *                         the import process can be customized depending on the
     *                         version number.
     * @param importSourceNode {@link Preferences} node of the previous version of
     *                         Scene Builder.
     * 
     * @throws BackingStoreException in case of problems accessing the Preferences
     *                               store.
     */
    void importFrom(Preferences importSourceNode) throws BackingStoreException {
        copyChildren(importSourceNode,target);
    }

    void importFrom(VersionedPreferences importSourceNode) throws BackingStoreException {
        importFrom(importSourceNode.node());
    }
    

    private void copyChildren(Preferences source, Preferences targetNode) throws BackingStoreException {
        logger.log(Level.INFO, "from node {0}", source.name());
        String[] keys = source.keys();
        for (String key : keys) {
            String value = source.get(key, null);
            if (value != null) {
                targetNode.put(key, value);
            }
        }
        
        String[] children = source.childrenNames();
        for (String child : children) {
           copyChildren(source.node(child), targetNode.node(child));
        }
    }

    /**
     * Decides if Preferences need to be imported. If the key
     * {@code PREF_PERFORM_IMPORT} is not defined, then true is returned, otherwise
     * the configured value.
     * 
     * @return true in case the key is not {@code PREF_PERFORM_IMPORT} set.
     */
    boolean askForImport() {
        boolean importRequired = target.getBoolean(PREF_PERFORM_IMPORT, true);
        logger.log(Level.FINE, "preferences import required; {0}", importRequired);
        return importRequired;
    }

    /**
     * By default, Scene Builder will attempt to import preferences from a previous
     * version. After an attempt to import, Scene Builder will store a preference to
     * document that another import attempt is not needed.
     */
    void documentThatNoImportIsDesired() {
        target.putBoolean(PREF_PERFORM_IMPORT, false);
    }

    void clearImportDecision() {
        target.remove(PREF_PERFORM_IMPORT);
    }

    /**
     * Attempts to import settings of a previous version if existing.
     * There is no user feedback in case of error. The ope
     */
    void tryImportingPreviousVersionSettings() {
        if (this.optionalSourceNode.isPresent()) {
            VersionedPreferences source = this.optionalSourceNode.get();
            try {
                importFrom(source);
            } catch (Exception importError) {
                logger.log(Level.SEVERE, String.format("Error during preferences import!", importError));
            }
            try {
                this.actionAfterImport.run();
            } catch (Exception postImportActionError) {
                logger.log(Level.SEVERE, String.format("Error while running post-import action!", postImportActionError));
            }
            documentThatNoImportIsDesired();
        }
    }

    /**
     * Decides if the user shall be asked to import settings of previous Scene
     * Builder version but ONLY when settings of a previous version have been found.
     * 
     * @return true when previous version settings have been found and user has not yet decided 
     */
    boolean askForImportIfOlderSettingsExist() {
        boolean previousVersionFound = this.optionalSourceNode.isPresent();
        if (!previousVersionFound) {
            return false;
        }

        logger.log(Level.FINE, "Importing previous version preferences");
        VersionedPreferences prefs = this.optionalSourceNode.get();
        logger.log(Level.FINE, "Version: {0}", prefs.version());
        logger.log(Level.FINE, "Node   : {0}", prefs.node());

        boolean forcedImport = isForcedImport();
        if (forcedImport) {
            logger.log(Level.FINE, "detected -DforceImport=true");
            return true;
        }

        boolean importRequired = askForImport();
        if (!importRequired) {
            logger.log(Level.FINE, "Import was already performed, disabling user library import.");
            UserLibraryImporter.disableImport(target);
        }
        return importRequired;
    }

    private boolean isForcedImport() {
        String forceImport = System.getProperty("forceImport", "false");
        return "true".equalsIgnoreCase(forceImport);
    }
    /**
     * Defines an activity which will be executed after import of settings.
     * 
     * @param action {@link Runnable}
     * @throws NullPointerException when action is null
     */
    void runAfterImport(Runnable action) {
        this.actionAfterImport = Objects.requireNonNull(action);
    }

    /**
     * Will raise a JavaFX {@link Alert} to ask the user whether to import previous
     * version settings or not. The question will only appear in cases where
     * previous version settings exist and the user decision has not been saved yet.
     * <p>
     * If the user declines to perform the import, it will also prevent the user
     * library import by setting the corresponding preference value.
     */
    public void askForActionAndRun() {
        Supplier<Optional<ButtonType>> alertInteraction = () -> {
            SBAlert customAlert = new SBAlert(AlertType.CONFIRMATION, ButtonType.YES, ButtonType.NO);
            
            customAlert.setTitle("Gluon Scene Builder");
            customAlert.setHeaderText("Import settings");
            customAlert.setHeight(350);
            customAlert.setIcons(AppSettings.APP_ICON_16, AppSettings.APP_ICON_32); 
            customAlert.setContentText(
                    "Previous version settings found. Do you want to import those?"
                  + "\nScene Builder will also import the JAR files from your library."
                  + "\n\nYour decision will be remembered so you won't be asked again.");
            return customAlert.showAndWait();
        };
        askForActionAndRun(alertInteraction);
    }

    void askForActionAndRun(Supplier<Optional<ButtonType>> alertInteraction) {
        if (askForImportIfOlderSettingsExist()) {
            executeInteractionAndImport(alertInteraction);
        }
    }

    boolean executeInteractionAndImport(Supplier<Optional<ButtonType>> alertInteraction) {
        Optional<ButtonType> response = alertInteraction.get();
        if (response.isPresent() && ButtonType.YES.equals(response.get())) {
            logger.log(Level.INFO, "User decided to import previous version settings.");
            tryImportingPreviousVersionSettings();
            return true;
        } else {
            documentThatNoImportIsDesired();
        }
        return false;
    }

}
