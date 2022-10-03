/*
 * Copyright (c) 2017, 2022, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.preferences;

import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.Repository;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public abstract class PreferencesControllerBase {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/

    // NODES
    private static final String DOCUMENTS = "DOCUMENTS"; //NOI18N
    private static final String ARTIFACTS = "ARTIFACTS"; //NOI18N
    private static final String REPOSITORIES = "REPOSITORIES"; //NOI18N

    // GLOBAL PREFERENCES
    public static final String ROOT_CONTAINER_HEIGHT = "ROOT_CONTAINER_HEIGHT"; //NOI18N
    public static final String ROOT_CONTAINER_WIDTH = "ROOT_CONTAINER_WIDTH"; //NOI18N
    public static final String BACKGROUND_IMAGE = "BACKGROUND_IMAGE"; //NOI18N
    public static final String ALIGNMENT_GUIDES_COLOR = "ALIGNMENT_GUIDES_COLOR"; //NOI18N
    public static final String PARENT_RING_COLOR = "PARENT_RING_COLOR"; //NOI18N
    public static final String LIBRARY_DISPLAY_OPTION = "LIBRARY_DISPLAY_OPTION"; //NOI18N
    public static final String HIERARCHY_DISPLAY_OPTION = "HIERARCHY_DISPLAY_OPTION"; //NOI18N
    public static final String ACCORDION_ANIMATION = "ACCORDION_ANIMATION"; //NOI18N
    public static final String WILDCARD_IMPORT = "WILDCARD_IMPORT"; //NOI18N
    public static final String ALTERNATE_TEXT_INPUT_PASTE = "ALTERNATE_TEXT_INPUT_PASTE"; //NOI18N

    // DOCUMENT SPECIFIC PREFERENCES
    public static final String PATH = "path"; //NOI18N
    public static final String X_POS = "X"; //NOI18N
    public static final String Y_POS = "Y"; //NOI18N
    public static final String STAGE_HEIGHT = "height"; //NOI18N
    public static final String STAGE_WIDTH = "width"; //NOI18N
    public static final String SCENE_STYLE_SHEETS = "sceneStyleSheets"; //NOI18N
    public static final String I18N_RESOURCE = "I18NResource"; //NOI18N
    public static final String THEME = "theme";
    public static final String GLUON_SWATCH = "gluonSwatch";
    public static final String GLUON_THEME = "gluonTheme";

    /***************************************************************************
     *                                                                         *
     * Instance fields                                                         *
     *                                                                         *
     **************************************************************************/

    protected final Preferences applicationRootPreferences;
    protected final PreferencesRecordGlobalBase recordGlobal;
    protected final Preferences documentsRootPreferences;
    protected  final Preferences artifactsRootPreferences;
    protected final Preferences repositoriesRootPreferences;
    protected final MavenPreferences mavenPreferences;
    protected final RepositoryPreferences repositoryPreferences;



    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public PreferencesControllerBase(String basePrefNodeName, PreferencesRecordGlobalBase recordGlobal) {
        applicationRootPreferences = Preferences.userNodeForPackage(getClass()).node(basePrefNodeName);

        // Preferences global to the SB application
        this.recordGlobal = recordGlobal;
        recordGlobal.setApplicationRootPreferences(applicationRootPreferences);

        // Preferences specific to the document
        // Create the root node for all documents preferences
        documentsRootPreferences = applicationRootPreferences.node(DOCUMENTS);

        // Preferences specific to the maven artifacts
        // Create the root node for all artifacts preferences
        artifactsRootPreferences = applicationRootPreferences.node(ARTIFACTS);

        // Preferences specific to the repositories
        // Create the root node for all repositories preferences
        repositoriesRootPreferences = applicationRootPreferences.node(REPOSITORIES);

        // maven artifacts
        mavenPreferences = new MavenPreferences();

        // create initial map of existing artifacts
        try {
            final String[] childrenNames = artifactsRootPreferences.childrenNames();
            for (String child : childrenNames) {
                Preferences artifactPreferences = artifactsRootPreferences.node(child);
                MavenArtifact mavenArtifact = new MavenArtifact(child);
                mavenArtifact.setPath(artifactPreferences.get(PreferencesRecordArtifact.PATH, null));
                mavenArtifact.setDependencies(artifactPreferences.get(PreferencesRecordArtifact.DEPENDENCIES, null));
                mavenArtifact.setFilter(artifactPreferences.get(PreferencesRecordArtifact.FILTER, null));
                final PreferencesRecordArtifact recordArtifact = new PreferencesRecordArtifact(
                        artifactsRootPreferences, mavenArtifact);
                mavenPreferences.addRecordArtifact(child, recordArtifact);
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(PreferencesControllerBase.class.getName()).log(Level.SEVERE, null, ex);
        }

        // repositories
        repositoryPreferences = new RepositoryPreferences();

        // create initial map of existing repositories
        try {
            final String[] childrenNames = repositoriesRootPreferences.childrenNames();
            for (String child : childrenNames) {
                Preferences rp = repositoriesRootPreferences.node(child);
                Repository repository = new Repository(rp.get(PreferencesRecordRepository.REPO_ID, null),
                        rp.get(PreferencesRecordRepository.REPO_TYPE, null),
                        rp.get(PreferencesRecordRepository.REPO_URL, null),
                        rp.get(PreferencesRecordRepository.REPO_USER, null),
                        rp.get(PreferencesRecordRepository.REPO_PASS, null));
                final PreferencesRecordRepository recordRepository = new PreferencesRecordRepository(
                        artifactsRootPreferences, repository);
                repositoryPreferences.addRecordRepository(child, recordRepository);
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(PreferencesControllerBase.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    public PreferencesRecordGlobalBase getRecordGlobal() {
        return recordGlobal;
    }

    public MavenPreferences getMavenPreferences() {
        return mavenPreferences;
    }

    public PreferencesRecordArtifact getRecordArtifact(MavenArtifact mavenArtifact) {
        PreferencesRecordArtifact recordArtifact = mavenPreferences.getRecordArtifact(mavenArtifact.getCoordinates());
        if (recordArtifact == null) {
            recordArtifact = new PreferencesRecordArtifact(artifactsRootPreferences, mavenArtifact);
            mavenPreferences.addRecordArtifact(mavenArtifact.getCoordinates(), recordArtifact);
        }
        return recordArtifact;
    }

    public void removeArtifact(String coordinates) {
        if (coordinates != null && !coordinates.isEmpty() &&
                mavenPreferences.getRecordArtifact(coordinates) != null) {
            Preferences node = artifactsRootPreferences.node(coordinates);
            try {
                node.removeNode();
                mavenPreferences.removeRecordArtifact(coordinates);
            } catch (BackingStoreException ex) {
                Logger.getLogger(PreferencesControllerBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public RepositoryPreferences getRepositoryPreferences() {
        return repositoryPreferences;
    }

    public PreferencesRecordRepository getRecordRepository(Repository repository) {
        PreferencesRecordRepository recordRepository = repositoryPreferences.getRecordRepository(repository.getId());
        if (recordRepository == null) {
            recordRepository = new PreferencesRecordRepository(repositoriesRootPreferences, repository);
            repositoryPreferences.addRecordRepository(repository.getId(), recordRepository);
        }
        return recordRepository;
    }

    public void removeRepository(String id) {
        if (id != null && !id.isEmpty() &&
                repositoryPreferences.getRecordRepository(id) != null) {
            Preferences node = repositoriesRootPreferences.node(id);
            try {
                node.removeNode();
                repositoryPreferences.removeRecordRepository(id);
            } catch (BackingStoreException ex) {
                Logger.getLogger(PreferencesControllerBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
