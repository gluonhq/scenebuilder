/*
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
package com.oracle.javafx.scenebuilder.app.preferences;

import com.oracle.javafx.scenebuilder.app.DocumentWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.Repository;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Defines preferences for Scene Builder.
 */
public class PreferencesController {

    // JAVA PREFERENCES KEYS DEFINITIONS
    static final String SB_RELEASE_NODE = "SB_2.0"; //NOI18N

    // GLOBAL PREFERENCES
    static final String ROOT_CONTAINER_HEIGHT = "ROOT_CONTAINER_HEIGHT"; //NOI18N
    static final String ROOT_CONTAINER_WIDTH = "ROOT_CONTAINER_WIDTH"; //NOI18N

    static final String BACKGROUND_IMAGE = "BACKGROUND_IMAGE"; //NOI18N
    static final String ALIGNMENT_GUIDES_COLOR = "ALIGNMENT_GUIDES_COLOR"; //NOI18N
    static final String PARENT_RING_COLOR = "PARENT_RING_COLOR"; //NOI18N

    static final String TOOL_THEME = "TOOL_THEME"; //NOI18N
    static final String LIBRARY_DISPLAY_OPTION = "LIBRARY_DISPLAY_OPTION"; //NOI18N
    static final String HIERARCHY_DISPLAY_OPTION = "HIERARCHY_DISPLAY_OPTION"; //NOI18N
    static final String CSS_TABLE_COLUMNS_ORDERING_REVERSED = "CSS_TABLE_COLUMNS_ORDERING_REVERSED"; //NOI18N

    static final String RECENT_ITEMS = "RECENT_ITEMS"; //NOI18N
    static final String RECENT_ITEMS_SIZE = "RECENT_ITEMS_SIZE"; //NOI18N

    static final String DOCUMENTS = "DOCUMENTS"; //NOI18N
    
    static final String ARTIFACTS = "ARTIFACTS"; //NOI18N
    
    static final String REPOSITORIES = "REPOSITORIES"; //NOI18N

    static final String REGISTRATION_HASH = "REGISTRATION_HASH"; //NOI18N
    static final String REGISTRATION_EMAIL = "REGISTRATION_EMAIL"; //NOI18N
    static final String REGISTRATION_OPT_IN = "REGISTRATION_OPT_IN"; //NOI18N

    static final String UPDATE_DIALOG_DATE = "UPDATE_DIALOG_DATE";
    static final String IGNORE_VERSION = "IGNORE_VERSION";

    static final String LAST_SENT_TRACKING_INFO_DATE = "LAST_SENT_TRACKING_INFO_DATE";

    // DOCUMENT SPECIFIC PREFERENCES
    static final String PATH = "path"; //NOI18N
    static final String X_POS = "X"; //NOI18N
    static final String Y_POS = "Y"; //NOI18N
    static final String STAGE_HEIGHT = "height"; //NOI18N
    static final String STAGE_WIDTH = "width"; //NOI18N
    static final String BOTTOM_VISIBLE = "bottomVisible";//NOI18N
    static final String LEFT_VISIBLE = "leftVisible"; //NOI18N
    static final String RIGHT_VISIBLE = "rightVisible"; //NOI18N
    static final String LIBRARY_VISIBLE = "libraryVisible"; //NOI18N
    static final String DOCUMENT_VISIBLE = "documentVisible"; //NOI18N
    static final String INSPECTOR_SECTION_ID = "inspectorSectionId"; //NOI18N
    static final String LEFT_DIVIDER_HPOS = "leftDividerHPos"; //NOI18N
    static final String RIGHT_DIVIDER_HPOS = "rightDividerHPos"; //NOI18N
    static final String BOTTOM_DIVIDER_VPOS = "bottomDividerVPos"; //NOI18N
    static final String LEFT_DIVIDER_VPOS = "leftDividerVPos"; //NOI18N
    static final String SCENE_STYLE_SHEETS = "sceneStyleSheets"; //NOI18N
    static final String I18N_RESOURCE = "I18NResource"; //NOI18N

    private static PreferencesController singleton;

    private final Preferences applicationRootPreferences;
    private final Preferences documentsRootPreferences;
    private final Preferences artifactsRootPreferences;
    private final Preferences repositoriesRootPreferences;
    private final PreferencesRecordGlobal recordGlobal;
    private final MavenPreferences mavenPreferences;
    private final RepositoryPreferences repositoryPreferences;
    private final Map<DocumentWindowController, PreferencesRecordDocument> recordDocuments = new HashMap<>();

    private PreferencesController() {
        applicationRootPreferences = Preferences.userNodeForPackage(
                PreferencesController.class).node(SB_RELEASE_NODE);

        // Preferences global to the SB application
        recordGlobal = new PreferencesRecordGlobal(applicationRootPreferences);

        // Preferences specific to the document
        // Create the root node for all documents preferences
        documentsRootPreferences = applicationRootPreferences.node(DOCUMENTS);

        // Preferences specific to the maven artifacts
        // Create the root node for all artifacts preferences
        artifactsRootPreferences = applicationRootPreferences.node(ARTIFACTS);
        
        // Preferences specific to the repositories
        // Create the root node for all repositories preferences
        repositoriesRootPreferences = applicationRootPreferences.node(REPOSITORIES);
        
        // Cleanup document preferences at start time : 
        // We keep only document preferences for the documents defined in RECENT_ITEMS
        final String items = applicationRootPreferences.get(RECENT_ITEMS, null); //NOI18N
        if (items != null && items.isEmpty() == false) {
            // Remove document preferences node if needed
            try {
                final String[] childrenNames = documentsRootPreferences.childrenNames();
                // Check among the document root chidlren if there is a child
                // which path matches the specified one
                for (String child : childrenNames) {
                    final Preferences documentPreferences = documentsRootPreferences.node(child);
                    final String nodePath = documentPreferences.get(PATH, null);
                    // Each document node defines a path
                    // If path is null or empty, this means preferences DB has been corrupted
                    if (nodePath == null || nodePath.isEmpty()) {
                        documentPreferences.removeNode();
                    } else if (items.contains(nodePath) == false) {
                        documentPreferences.removeNode();
                    }
                }
            } catch (BackingStoreException ex) {
                Logger.getLogger(PreferencesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
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
            Logger.getLogger(PreferencesController.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PreferencesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static synchronized PreferencesController getSingleton() {
        if (singleton == null) {
            singleton = new PreferencesController();
            singleton.getRecordGlobal().readFromJavaPreferences();
        }
        return singleton;
    }

    public PreferencesRecordGlobal getRecordGlobal() {
        return recordGlobal;
    }

    public PreferencesRecordDocument getRecordDocument(final DocumentWindowController dwc) {
        final PreferencesRecordDocument recordDocument;
        if (recordDocuments.containsKey(dwc)) {
            recordDocument = recordDocuments.get(dwc);
        } else {
            recordDocument = new PreferencesRecordDocument(documentsRootPreferences, dwc);
            recordDocuments.put(dwc, recordDocument);
        }
        return recordDocument;
    }

    public void clearRecentItems() {
        // Clear RECENT ITEMS global preferences
        getRecordGlobal().clearRecentItems();
        // Clear individual DOCUMENTS preferences
        try {
            // Remove nodes from the DOCUMENTS root preference
            for (String child : documentsRootPreferences.childrenNames()) {
                final Preferences documentPreferences = documentsRootPreferences.node(child);
                documentPreferences.removeNode();
            }
            // Reset the PreferencesRecordDocuments
            for (PreferencesRecordDocument prd : recordDocuments.values()) {
                prd.resetDocumentPreferences();
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(PreferencesController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                Logger.getLogger(PreferencesController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(PreferencesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
