package com.oracle.javafx.scenebuilder.kit.util;

import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;

import java.io.File;
import java.net.URISyntaxException;

public class Utils {
    public static final String makeTitle(FXOMDocument fxomDocument) {
        final String title;

        if (fxomDocument == null) {
            title = I18N.getString("label.no.document");
        } else if (fxomDocument.getLocation() == null) {
            title = I18N.getString("label.untitled");
        } else {
            String name = ""; //NOI18N
            try {
                final File toto = new File(fxomDocument.getLocation().toURI());
                name = toto.getName();
            } catch (URISyntaxException ex) {
                throw new RuntimeException("Bug", ex); //NOI18N
            }
            title = name;
        }

        return title;
    }
}
