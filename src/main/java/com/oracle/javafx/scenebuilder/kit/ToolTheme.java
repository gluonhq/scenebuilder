package com.oracle.javafx.scenebuilder.kit;

import com.oracle.javafx.scenebuilder.app.i18n.I18N;

public enum ToolTheme {

    DEFAULT {
        @Override
        public String toString() {
            return I18N.getString("prefs.tool.theme.default");
        }
    },
    DARK {
        @Override
        public String toString() {
            return I18N.getString("prefs.tool.theme.dark");
        }
    }
}
