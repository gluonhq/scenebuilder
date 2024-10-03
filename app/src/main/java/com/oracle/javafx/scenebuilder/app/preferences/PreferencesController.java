/*
 * Copyright (c) 2016, 2024 Gluon and/or its affiliates.
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
import com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Defines preferences for Scene Builder App.
 */
public class PreferencesController extends PreferencesControllerBase {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/

    // PREFERENCES NODE NAME
    static final String SB_RELEASE_NODE = "SB_2.0"; //NOI18N

    // GLOBAL PREFERENCES
    static final String TOOL_THEME = "TOOL_THEME"; //NOI18N
    static final String CSS_TABLE_COLUMNS_ORDERING_REVERSED = "CSS_TABLE_COLUMNS_ORDERING_REVERSED"; //NOI18N

    static final String RECENT_ITEMS = "RECENT_ITEMS"; //NOI18N
    static final String RECENT_ITEMS_SIZE = "RECENT_ITEMS_SIZE"; //NOI18N

    static final String REGISTRATION_HASH = "REGISTRATION_HASH"; //NOI18N
    static final String REGISTRATION_EMAIL = "REGISTRATION_EMAIL"; //NOI18N
    static final String REGISTRATION_OPT_IN = "REGISTRATION_OPT_IN"; //NOI18N

    static final String UPDATE_DIALOG_DATE = "UPDATE_DIALOG_DATE";
    static final String IGNORE_VERSION = "IGNORE_VERSION";

    static final String IMPORTED_GLUON_JARS = "IMPORTED_GLUON_JARS";

    static final String LAST_SENT_TRACKING_INFO_DATE = "LAST_SENT_TRACKING_INFO_DATE";

    // DOCUMENT SPECIFIC PREFERENCES
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

    static final String GLUON_SWATCH = "gluonSwatch";
    static final String GLUON_THEME = "gluonTheme";

    private static PreferencesController singleton;

    /***************************************************************************
     *                                                                         *
     * Instance fields                                                         *
     *                                                                         *
     **************************************************************************/

    private final Map<DocumentWindowController, PreferencesRecordDocument> recordDocuments = new HashMap<>();

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    private PreferencesController() {
        super(SB_RELEASE_NODE, new PreferencesRecordGlobal());

        // Cleanup document preferences at start time : 
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
                    }
                }
            } catch (BackingStoreException ex) {
                Logger.getLogger(PreferencesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }



    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    public static synchronized PreferencesController getSingleton() {
        if (singleton == null) {
            singleton = new PreferencesController();
            singleton.getRecordGlobal().readFromJavaPreferences();
        }
        return singleton;
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

    @Override
    public PreferencesRecordGlobal getRecordGlobal() {
        return (PreferencesRecordGlobal) recordGlobal;
    }
}
