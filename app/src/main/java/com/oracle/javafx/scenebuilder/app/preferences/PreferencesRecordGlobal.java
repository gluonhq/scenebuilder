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
package com.oracle.javafx.scenebuilder.app.preferences;

import com.gluonhq.scenebuilder.plugins.editor.GluonEditorPlatform;
import com.oracle.javafx.scenebuilder.kit.ToolTheme;
import com.oracle.javafx.scenebuilder.app.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.AbstractHierarchyPanelController.DisplayOption;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController.DISPLAY_MODE;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferencesRecordGlobalBase;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesController.*;

/**
 * Defines preferences global to the SB application.
 */
public class PreferencesRecordGlobal extends PreferencesRecordGlobalBase {

    /***************************************************************************
     *                                                                         *
     * Support Classes                                                         *
     *                                                                         *
     **************************************************************************/

    public enum CSSAnalyzerColumnsOrder {

        DEFAULTS_FIRST {

            @Override
            public String toString() {
                return I18N.getString("prefs.cssanalyzer.columns.defaults.first");
            }
        },
        DEFAULTS_LAST {

            @Override
            public String toString() {
                return I18N.getString("prefs.cssanalyzer.columns.defaults.last");
            }
        }
    }

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/

    // Default values
    static final double DEFAULT_ROOT_CONTAINER_HEIGHT = 400;
    static final double DEFAULT_ROOT_CONTAINER_WIDTH = 600;

    static final ToolTheme DEFAULT_TOOL_THEME = ToolTheme.DEFAULT;
    static final DISPLAY_MODE DEFAULT_LIBRARY_DISPLAY_OPTION
            = DISPLAY_MODE.SECTIONS;
    static final DisplayOption DEFAULT_HIERARCHY_DISPLAY_OPTION
            = DisplayOption.INFO;
    static final boolean DEFAULT_CSS_TABLE_COLUMNS_ORDERING_REVERSED = false;

    static final int DEFAULT_RECENT_ITEMS_SIZE = 15;
    static final boolean DEFAULT_ACCORDION_ANIMATION = true;
    static final boolean DEFAULT_WILDCARD_IMPORTS = false;
    static final boolean DEFAULT_ALTERNATE_TEXT_INPUT_PASTE = EditorPlatform.IS_MAC;

    static final EditorPlatform.Theme DEFAULT_GLUON_SWATCH = GluonEditorPlatform.DEFAULT_GLUON_SWATCH;
    static final EditorPlatform.Theme DEFAULT_GLUON_THEME = GluonEditorPlatform.DEFAULT_GLUON_THEME;

    private EditorPlatform.Theme gluonSwatch = DEFAULT_GLUON_SWATCH;
    private EditorPlatform.Theme gluonTheme = DEFAULT_GLUON_THEME;
    /***************************************************************************
     *                                                                         *
     * Instance fields                                                         *
     *                                                                         *
     **************************************************************************/

    // Global preferences
    private ToolTheme toolTheme = DEFAULT_TOOL_THEME;
    private DISPLAY_MODE libraryDisplayOption = DEFAULT_LIBRARY_DISPLAY_OPTION;
    private DisplayOption hierarchyDisplayOption = DEFAULT_HIERARCHY_DISPLAY_OPTION;
    private boolean cssTableColumnsOrderingReversed = DEFAULT_CSS_TABLE_COLUMNS_ORDERING_REVERSED;
    private int recentItemsSize = DEFAULT_RECENT_ITEMS_SIZE;
    private boolean accordionAnimation = DEFAULT_ACCORDION_ANIMATION;
    private boolean wildcardImports = DEFAULT_WILDCARD_IMPORTS;
    private final List<String> recentItems = new ArrayList<>();

    private LocalDate showUpdateDialogDate = null;
    private String ignoreVersion = null;

    private String[] importedGluonJars = new String[0];

    private String registrationHash = null;
    private String registrationEmail = null;
    private boolean registrationOptIn = false;

    private LocalDate lastSentTrackingInfoDate = null;

    private boolean alternatePasteBehavior = true;

    final static Integer[] recentItemsSizes = {5, 10, 15, 20};

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public PreferencesRecordGlobal() {
    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    public ToolTheme getToolTheme() {
        return toolTheme;
    }
    
    public void setToolTheme(ToolTheme value) {
        toolTheme = value;
    }

    public EditorPlatform.Theme getSwatch() { return gluonSwatch; }

    public void setSwatch(EditorPlatform.Theme swatch) { this.gluonSwatch = swatch; }

    public EditorPlatform.Theme getGluonTheme() { return gluonTheme; }

    public void setGluonTheme(EditorPlatform.Theme theme) { this.gluonTheme = theme; }
    
    public DISPLAY_MODE getLibraryDisplayOption() {
        return libraryDisplayOption;
    }

    public void setLibraryDisplayOption(DISPLAY_MODE value) {
        libraryDisplayOption = value;
    }

    public void updateLibraryDisplayOption(DISPLAY_MODE value) {
        libraryDisplayOption = value;
        writeToJavaPreferences(PreferencesControllerBase.LIBRARY_DISPLAY_OPTION);
    }

    public DisplayOption getHierarchyDisplayOption() {
        return hierarchyDisplayOption;
    }

    public void setHierarchyDisplayOption(DisplayOption value) {
        hierarchyDisplayOption = value;
    }

    public void updateHierarchyDisplayOption(DisplayOption value) {
        hierarchyDisplayOption = value;
        writeToJavaPreferences(PreferencesControllerBase.HIERARCHY_DISPLAY_OPTION);
    }

    public CSSAnalyzerColumnsOrder getDefaultCSSAnalyzerColumnsOrder() {
        if (DEFAULT_CSS_TABLE_COLUMNS_ORDERING_REVERSED) {
            return CSSAnalyzerColumnsOrder.DEFAULTS_LAST;
        } else {
            return CSSAnalyzerColumnsOrder.DEFAULTS_FIRST;
        }
    }
    
    public CSSAnalyzerColumnsOrder getCSSAnalyzerColumnsOrder() {
        if (isCssTableColumnsOrderingReversed()) {
            return CSSAnalyzerColumnsOrder.DEFAULTS_LAST;
        } else {
            return CSSAnalyzerColumnsOrder.DEFAULTS_FIRST;
        }
    }

    public void setCSSAnalyzerColumnsOrder(CSSAnalyzerColumnsOrder value) {
        switch (value) {
            case DEFAULTS_FIRST:
                setCssTableColumnsOrderingReversed(false);
                break;
            case DEFAULTS_LAST:
                setCssTableColumnsOrderingReversed(true);
                break;
            default:
                assert false;
        }
    }

    public boolean isCssTableColumnsOrderingReversed() {
        return cssTableColumnsOrderingReversed;
    }

    public void setCssTableColumnsOrderingReversed(boolean value) {
        cssTableColumnsOrderingReversed = value;
    }

    public int getRecentItemsSize() {
        return recentItemsSize;
    }

    public void setRecentItemsSize(int value) {
        recentItemsSize = value;
        // Remove last items depending on the size
        while (recentItems.size() > recentItemsSize) {
            recentItems.remove(recentItems.size() - 1);
        }
    }

    public List<String> getRecentItems() {
        return recentItems;
    }

    public boolean containsRecentItem(File file) {
        final String path = file.getPath();
        return recentItems.contains(path);
    }

    public boolean containsRecentItem(URL url) {
        final File fxmlFile;
        try {
            fxmlFile = new File(url.toURI());
            return containsRecentItem(fxmlFile);
        } catch (URISyntaxException ex) {
            Logger.getLogger(PreferencesRecordGlobal.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void addRecentItem(File file) {
        final List<File> files = new ArrayList<>();
        files.add(file);
        addRecentItems(files);
    }

    public void addRecentItem(URL url) {
        final File fxmlFile;
        try {
            fxmlFile = new File(url.toURI());
            addRecentItem(fxmlFile);
        } catch (URISyntaxException ex) {
            Logger.getLogger(PreferencesRecordGlobal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addRecentItems(List<File> files) {
        for (File file : files) {
            final String path = file.getPath();
            if (recentItems.contains(path)) {
                recentItems.remove(path);
            }
            // Add the specified file to the recent items at first position
            recentItems.add(0, path);
        }
        // Remove last items depending on the size
        while (recentItems.size() > recentItemsSize) {
            recentItems.remove(recentItems.size() - 1);
        }
        writeToJavaPreferences(RECENT_ITEMS);
    }

    public void removeRecentItems(List<String> filePaths) {
        // Remove the specified files from the recent items
        for (String filePath : filePaths) {
            recentItems.remove(filePath);
        }
        writeToJavaPreferences(RECENT_ITEMS);
    }

    public void clearRecentItems() {
        recentItems.clear();
        writeToJavaPreferences(RECENT_ITEMS);
    }

    public void updateRegistrationFields(String hash, String email, Boolean optIn) {
        registrationHash = hash;
        writeToJavaPreferences(REGISTRATION_HASH);

        if (email != null) {
            registrationEmail = email;
            writeToJavaPreferences(REGISTRATION_EMAIL);
        }

        if (optIn != null) {
            registrationOptIn = optIn;
            writeToJavaPreferences(REGISTRATION_OPT_IN);
        }
    }

    public String getRegistrationHash() {
        return registrationHash;
    }

    public void setRegistrationHash(String registrationHash) {
        this.registrationHash = registrationHash;
    }

    public String getRegistrationEmail() {
        return registrationEmail;
    }

    public void setRegistrationEmail(String registrationEmail) {
        this.registrationEmail = registrationEmail;
    }

    public boolean isRegistrationOptIn() {
        return registrationOptIn;
    }

    public void setRegistrationOptIn(boolean registrationOptIn) {
        this.registrationOptIn = registrationOptIn;
    }

    public void setShowUpdateDialogAfter(LocalDate showUpdateDialogDate) {
        this.showUpdateDialogDate = showUpdateDialogDate;
        writeToJavaPreferences(UPDATE_DIALOG_DATE);
    }

    public LocalDate getShowUpdateDialogDate() {
        return showUpdateDialogDate;
    }

    public void setIgnoreVersion(String ignoreVersion) {
        this.ignoreVersion = ignoreVersion;
        writeToJavaPreferences(IGNORE_VERSION);
    }

    public String getIgnoreVersion() {
        return ignoreVersion;
    }

    public void setImportedGluonJars(String[] importedJars) {
        this.importedGluonJars = importedJars;
        writeToJavaPreferences(IMPORTED_GLUON_JARS);
    }

    public String[] getImportedGluonJars() {
        return importedGluonJars;
    }

    public LocalDate getLastSentTrackingInfoDate() {
        return lastSentTrackingInfoDate;
    }

    public void setLastSentTrackingInfoDate(LocalDate date) {
        lastSentTrackingInfoDate = date;
        writeToJavaPreferences(LAST_SENT_TRACKING_INFO_DATE);
    }

    public boolean isAccordionAnimation() {
        return accordionAnimation;
    }

    public void setAccordionAnimation(boolean accordionAnimation) {
        this.accordionAnimation = accordionAnimation;
    }

    public boolean isWildcardImports() {
        return wildcardImports;
    }

    public void setWildcardImports(boolean wildcardImports) {
        this.wildcardImports = wildcardImports;
    }

    public boolean isAlternateTextInputControlPaste() {
        return alternatePasteBehavior;
    }
    
    public void setAlternateTextInputControlPaste(boolean alternatePasteBehavior) {
        this.alternatePasteBehavior = alternatePasteBehavior;
    }

    /**
     * Read data from the java preferences DB and initialize properties.
     */
    public void readFromJavaPreferences() {
        super.readFromJavaPreferences();

        // Document size
        if (getRootContainerHeight() == -1) {
            setRootContainerHeight(DEFAULT_ROOT_CONTAINER_HEIGHT);
        }

        if (getRootContainerWidth() == -1) {
            setRootContainerWidth(DEFAULT_ROOT_CONTAINER_WIDTH);
        }

        // Gluon themes
        String swatchName = applicationRootPreferences.get(PreferencesController.GLUON_SWATCH, DEFAULT_GLUON_SWATCH.name());
        gluonSwatch = GluonEditorPlatform.swatchValueOf(swatchName);
        String gluonThemeName = applicationRootPreferences.get(PreferencesController.GLUON_THEME, DEFAULT_GLUON_THEME.name());
        gluonTheme = EditorPlatform.Theme.valueOf(gluonThemeName);

        // Tool Theme
        final String tool_theme = applicationRootPreferences.get(TOOL_THEME,
                DEFAULT_TOOL_THEME.name());
        setToolTheme(ToolTheme.valueOf(tool_theme));

        // Library display option
        final String library_DisplayOption = applicationRootPreferences.get(PreferencesControllerBase.LIBRARY_DISPLAY_OPTION,
                DEFAULT_LIBRARY_DISPLAY_OPTION.name());
        setLibraryDisplayOption(DISPLAY_MODE.valueOf(library_DisplayOption));

        // Hierarchy display option
        final String hierarchy_DisplayOption = applicationRootPreferences.get(PreferencesControllerBase.HIERARCHY_DISPLAY_OPTION,
                DEFAULT_HIERARCHY_DISPLAY_OPTION.name());
        setHierarchyDisplayOption(DisplayOption.valueOf(hierarchy_DisplayOption));

        // CSS analyzer column order
        final boolean reversed = applicationRootPreferences.getBoolean(
                CSS_TABLE_COLUMNS_ORDERING_REVERSED, DEFAULT_CSS_TABLE_COLUMNS_ORDERING_REVERSED);
        setCssTableColumnsOrderingReversed(reversed);

        // Recent items size
        final int size = applicationRootPreferences.getInt(
                RECENT_ITEMS_SIZE, DEFAULT_RECENT_ITEMS_SIZE);
        setRecentItemsSize(size);

        // Recent items list
        final String items = applicationRootPreferences.get(RECENT_ITEMS, null);
        assert recentItems.isEmpty();
        if (items != null && !items.isEmpty()) {
            final String[] itemsArray = items.split(File.pathSeparator); //NOI18N
            assert itemsArray.length <= recentItemsSize;
            recentItems.addAll(Arrays.asList(itemsArray));
        }

        // Registration information
        final String registrationHash = applicationRootPreferences.get(REGISTRATION_HASH, null);
        setRegistrationHash(registrationHash);
        final String registrationEmail = applicationRootPreferences.get(REGISTRATION_EMAIL, null);
        setRegistrationEmail(registrationEmail);
        final boolean registrationOptIn = applicationRootPreferences.getBoolean(REGISTRATION_OPT_IN, false);
        setRegistrationOptIn(registrationOptIn);

        // Update dialog
        String updateDialogDate = applicationRootPreferences.get(UPDATE_DIALOG_DATE, null);
        if (updateDialogDate == null) {
            showUpdateDialogDate = null;
        } else {
            showUpdateDialogDate = LocalDate.parse(updateDialogDate);
        }
        ignoreVersion = applicationRootPreferences.get(IGNORE_VERSION, null);

        String dateString = applicationRootPreferences.get(LAST_SENT_TRACKING_INFO_DATE, null);
        if (dateString == null) {
            lastSentTrackingInfoDate = null;
        } else {
            lastSentTrackingInfoDate = LocalDate.parse(dateString);
        }

        // Import Gluon Controls Alert
        final String importedGluonJarsString = applicationRootPreferences.get(IMPORTED_GLUON_JARS, null);
        if (importedGluonJarsString == null) {
            this.importedGluonJars = new String[0];
        } else {
            this.importedGluonJars= importedGluonJarsString.split(",");
        }

        // Accordion animation
        setAccordionAnimation(applicationRootPreferences.getBoolean(ACCORDION_ANIMATION, DEFAULT_ACCORDION_ANIMATION));

        // Wildcard imports
        setWildcardImports(applicationRootPreferences.getBoolean(WILDCARD_IMPORT, DEFAULT_WILDCARD_IMPORTS));

        // Alternate paste behavior for Text Input Controls
        setAlternateTextInputControlPaste(applicationRootPreferences.getBoolean(ALTERNATE_TEXT_INPUT_PASTE, DEFAULT_ALTERNATE_TEXT_INPUT_PASTE));
    }

    public void writeToJavaPreferences(String key) {

        assert applicationRootPreferences != null;
        assert key != null;
        switch (key) {
            case TOOL_THEME:
                applicationRootPreferences.put(TOOL_THEME, getToolTheme().name());
                break;
            case PreferencesController.GLUON_SWATCH:
                applicationRootPreferences.put(PreferencesController.GLUON_SWATCH, getSwatch().name());
                break;
            case PreferencesController.GLUON_THEME:
                applicationRootPreferences.put(PreferencesController.GLUON_THEME, getGluonTheme().name());
                break;
            case PreferencesControllerBase.LIBRARY_DISPLAY_OPTION:
                applicationRootPreferences.put(PreferencesControllerBase.LIBRARY_DISPLAY_OPTION, getLibraryDisplayOption().name());
                break;
            case PreferencesControllerBase.HIERARCHY_DISPLAY_OPTION:
                applicationRootPreferences.put(PreferencesControllerBase.HIERARCHY_DISPLAY_OPTION, getHierarchyDisplayOption().name());
                break;
            case CSS_TABLE_COLUMNS_ORDERING_REVERSED:
                applicationRootPreferences.putBoolean(CSS_TABLE_COLUMNS_ORDERING_REVERSED, isCssTableColumnsOrderingReversed());
                break;
            case RECENT_ITEMS_SIZE:
                applicationRootPreferences.putInt(RECENT_ITEMS_SIZE, getRecentItemsSize());
                break;
            case RECENT_ITEMS:
                final StringBuilder sb = new StringBuilder();
                for (String recentItem : getRecentItems()) {
                    sb.append(recentItem);
                    sb.append(File.pathSeparator);
                }
                applicationRootPreferences.put(RECENT_ITEMS, sb.toString());
                break;
            case REGISTRATION_HASH:
                applicationRootPreferences.put(REGISTRATION_HASH, getRegistrationHash());
                break;
            case REGISTRATION_EMAIL:
                applicationRootPreferences.put(REGISTRATION_EMAIL, getRegistrationEmail());
                break;
            case REGISTRATION_OPT_IN:
                applicationRootPreferences.putBoolean(REGISTRATION_OPT_IN, isRegistrationOptIn());
                break;
            case UPDATE_DIALOG_DATE:
                applicationRootPreferences.put(UPDATE_DIALOG_DATE, getShowUpdateDialogDate().toString());
                break;
            case IGNORE_VERSION:
                applicationRootPreferences.put(IGNORE_VERSION, getIgnoreVersion());
                break;
            case LAST_SENT_TRACKING_INFO_DATE:
                applicationRootPreferences.put(LAST_SENT_TRACKING_INFO_DATE, getLastSentTrackingInfoDate().toString());
                break;
            case IMPORTED_GLUON_JARS:
                if (importedGluonJars.length == 0) {
                    applicationRootPreferences.put(IMPORTED_GLUON_JARS, "");
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s : importedGluonJars) {
                        stringBuilder.append(s);
                        stringBuilder.append(",");
                    }
                    applicationRootPreferences.put(IMPORTED_GLUON_JARS, stringBuilder.toString());
                }
                break;
            case ACCORDION_ANIMATION:
                applicationRootPreferences.putBoolean(ACCORDION_ANIMATION, isAccordionAnimation());
                break;
            case WILDCARD_IMPORT:
                applicationRootPreferences.putBoolean(WILDCARD_IMPORT, isWildcardImports());
                break;
            case ALTERNATE_TEXT_INPUT_PASTE:
                applicationRootPreferences.putBoolean(ALTERNATE_TEXT_INPUT_PASTE,isAlternateTextInputControlPaste());
                break;
            default:
                super.writeToJavaPreferences(key);
                break;
        }
    }

//    private static Image getShadowImage() {
//        final URL url = PreferencesRecordGlobal.class.getResource("background-shadow.png"); //NOI18N
//        return new Image(url.toExternalForm());
//    }
}
