package com.oracle.javafx.scenebuilder.kit.preferences;

import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.prefs.Preferences;

public abstract class PreferencesRecordGlobalBase {

    /***************************************************************************
     *                                                                         *
     * Support Classes                                                         *
     *                                                                         *
     **************************************************************************/

    public enum BackgroundImage {

        BACKGROUND_01 {

            @Override
            public String toString() {
                return I18N.getString("prefs.background.value1");
            }
        },
        BACKGROUND_02 {

            @Override
            public String toString() {
                return I18N.getString("prefs.background.value2");
            }
        },
        BACKGROUND_03 {

            @Override
            public String toString() {
                return I18N.getString("prefs.background.value3");
            }
        }
    }

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/

    protected static final BackgroundImage DEFAULT_BACKGROUND_IMAGE
            = BackgroundImage.BACKGROUND_03;
    protected static final Color DEFAULT_ALIGNMENT_GUIDES_COLOR = Color.RED;
    protected static final Color DEFAULT_PARENT_RING_COLOR = Color.rgb(238, 168, 47);
    public static final EditorPlatform.Theme DEFAULT_THEME = EditorPlatform.DEFAULT_THEME;
    public static final EditorPlatform.GluonSwatch DEFAULT_SWATCH = EditorPlatform.DEFAULT_SWATCH;
    public static final EditorPlatform.GluonTheme DEFAULT_GLUON_THEME = EditorPlatform.DEFAULT_GLUON_THEME;

    /***************************************************************************
     *                                                                         *
     * Instance fields                                                         *
     *                                                                         *
     **************************************************************************/

    protected Preferences applicationRootPreferences;

    protected double rootContainerHeight;
    protected double rootContainerWidth;
    protected BackgroundImage backgroundImage = DEFAULT_BACKGROUND_IMAGE;
    protected Color alignmentGuidesColor = DEFAULT_ALIGNMENT_GUIDES_COLOR;
    protected Color parentRingColor = DEFAULT_PARENT_RING_COLOR;

    protected EditorPlatform.Theme theme = DEFAULT_THEME;
    protected EditorPlatform.GluonSwatch gluonSwatch = DEFAULT_SWATCH;
    protected EditorPlatform.GluonTheme gluonTheme = DEFAULT_GLUON_THEME;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public PreferencesRecordGlobalBase() {
    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    public void setApplicationRootPreferences(Preferences applicationRootPreferences) {
        this.applicationRootPreferences = applicationRootPreferences;
    }

    public double getRootContainerHeight() {
        return rootContainerHeight;
    }

    public void setRootContainerHeight(double value) {
        rootContainerHeight = value;
    }

    public double getRootContainerWidth() {
        return rootContainerWidth;
    }

    public void setRootContainerWidth(double value) {
        rootContainerWidth = value;
    }

    public BackgroundImage getBackgroundImage() {
        return backgroundImage;
    }

    public Image getBackgroundImageImage() { return getImage(backgroundImage); }

    public void setBackgroundImage(BackgroundImage value) {
        backgroundImage = value;
    }

    public Color getAlignmentGuidesColor() {
        return alignmentGuidesColor;
    }

    public void setAlignmentGuidesColor(Color value) {
        alignmentGuidesColor = value;
    }

    public Color getParentRingColor() {
        return parentRingColor;
    }

    public void setParentRingColor(Color value) {
        parentRingColor = value;
    }

    public EditorPlatform.Theme getTheme() { return theme; }

    public void setTheme(EditorPlatform.Theme theme) { this.theme = theme; }

    public EditorPlatform.GluonSwatch getSwatch() { return gluonSwatch; }

    public void setSwatch(EditorPlatform.GluonSwatch swatch) { this.gluonSwatch = swatch; }

    public EditorPlatform.GluonTheme getGluonTheme() { return gluonTheme; }

    public void setGluonTheme(EditorPlatform.GluonTheme theme) { this.gluonTheme = theme; }

    /**
     * Read data from the java preferences DB and initialize properties.
     */
    public void readFromJavaPreferences() {

        assert applicationRootPreferences != null;

        // Document size
        final double height = applicationRootPreferences.getDouble(PreferencesControllerBase.ROOT_CONTAINER_HEIGHT,
                -1);
        setRootContainerHeight(height);
        final double width = applicationRootPreferences.getDouble(PreferencesControllerBase.ROOT_CONTAINER_WIDTH,
                -1);
        setRootContainerWidth(width);

        // Background image
        final String image = applicationRootPreferences.get(PreferencesControllerBase.BACKGROUND_IMAGE,
                DEFAULT_BACKGROUND_IMAGE.name());
        setBackgroundImage(BackgroundImage.valueOf(image));

        // Alignment guides color
        final String agColor = applicationRootPreferences.get(PreferencesControllerBase.ALIGNMENT_GUIDES_COLOR,
                DEFAULT_ALIGNMENT_GUIDES_COLOR.toString());
        setAlignmentGuidesColor(Color.valueOf(agColor));

        // Parent ring color
        final String prColor = applicationRootPreferences.get(PreferencesControllerBase.PARENT_RING_COLOR,
                DEFAULT_PARENT_RING_COLOR.toString());
        setParentRingColor(Color.valueOf(prColor));

        // Document theme
        String themeName = applicationRootPreferences.get(PreferencesControllerBase.THEME, DEFAULT_THEME.name());
        theme = EditorPlatform.Theme.valueOf(themeName);
        String swatchName = applicationRootPreferences.get(PreferencesControllerBase.GLUON_SWATCH, DEFAULT_SWATCH.name());
        gluonSwatch = EditorPlatform.GluonSwatch.valueOf(swatchName);
        String gluonThemeName = applicationRootPreferences.get(PreferencesControllerBase.GLUON_THEME, DEFAULT_GLUON_THEME.name());
        gluonTheme = EditorPlatform.GluonTheme.valueOf(gluonThemeName);

    }

    public void writeToJavaPreferences(String key) {
        assert applicationRootPreferences != null;
        assert key != null;
        switch (key) {
            case PreferencesControllerBase.ROOT_CONTAINER_HEIGHT:
                applicationRootPreferences.putDouble(PreferencesControllerBase.ROOT_CONTAINER_HEIGHT, getRootContainerHeight());
                break;
            case PreferencesControllerBase.ROOT_CONTAINER_WIDTH:
                applicationRootPreferences.putDouble(PreferencesControllerBase.ROOT_CONTAINER_WIDTH, getRootContainerWidth());
                break;
            case PreferencesControllerBase.BACKGROUND_IMAGE:
                applicationRootPreferences.put(PreferencesControllerBase.BACKGROUND_IMAGE, backgroundImage.name());
                break;
            case PreferencesControllerBase.ALIGNMENT_GUIDES_COLOR:
                applicationRootPreferences.put(PreferencesControllerBase.ALIGNMENT_GUIDES_COLOR, getAlignmentGuidesColor().toString());
                break;
            case PreferencesControllerBase.PARENT_RING_COLOR:
                applicationRootPreferences.put(PreferencesControllerBase.PARENT_RING_COLOR, getParentRingColor().toString());
                break;
            case PreferencesControllerBase.THEME:
                applicationRootPreferences.put(PreferencesControllerBase.THEME, getTheme().name());
                break;
            case PreferencesControllerBase.GLUON_SWATCH:
                applicationRootPreferences.put(PreferencesControllerBase.GLUON_SWATCH, getSwatch().name());
                break;
            case PreferencesControllerBase.GLUON_THEME:
                applicationRootPreferences.put(PreferencesControllerBase.GLUON_THEME, getGluonTheme().name());
                break;
            default:
                assert false;
                break;
        }
    }

    private static Image getImage(BackgroundImage bgi) {
        final URL url;
        switch (bgi) {
            case BACKGROUND_01:
                url = PreferencesRecordGlobalBase.class.getResource("Background-Blue-Grid.png"); //NOI18N
                break;
            case BACKGROUND_02:
                url = PreferencesRecordGlobalBase.class.getResource("Background-Neutral-Grid.png"); //NOI18N
                break;
            case BACKGROUND_03:
                url = ContentPanelController.getDefaultWorkspaceBackgroundURL();
                break;
            default:
                url = null;
                assert false;
                break;
        }
        assert url != null;
        return new Image(url.toExternalForm());
    }

}
