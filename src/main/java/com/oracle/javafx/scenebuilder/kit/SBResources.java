package com.oracle.javafx.scenebuilder.kit;

import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class SBResources {

    public static final String BASE = SBResources.class.getResource("css/Base.css").toExternalForm();
    public static final String THEME_DARK_STYLESHEET = SBResources.class.getResource("css/ThemeDark.css").toExternalForm();
    public static final String THEME_DEFAULT_STYLESHEET = SBResources.class.getResource("css/ThemeDefault.css").toExternalForm();

    public static String getToolStylesheet(ToolTheme theme) {
        switch(theme) {
            case DARK:
                return THEME_DARK_STYLESHEET;
            case DEFAULT:
                return THEME_DEFAULT_STYLESHEET;
        }
        return null;
    }

    public static void setToolTheme(ToolTheme theme, Scene scene) {
        setToolTheme(theme, scene.getStylesheets());
    }

    public static void setToolTheme(ToolTheme theme, Parent parent) {
        setToolTheme(theme, parent.getStylesheets());
    }

    private static void setToolTheme(ToolTheme theme, ObservableList<String> stylesheets) {
        if (!stylesheets.contains(BASE)) {
            stylesheets.add(BASE);
        }
        String themeStylesheet = getToolStylesheet(theme);
        if (!stylesheets.contains(themeStylesheet)) {
            stylesheets.add(themeStylesheet);
        }
    }
}
