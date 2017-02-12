package com.oracle.javafx.scenebuilder.kit.preferences;

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

    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    public PreferencesRecordGlobalBase getRecordGlobal() {
        return recordGlobal;
    }


}
