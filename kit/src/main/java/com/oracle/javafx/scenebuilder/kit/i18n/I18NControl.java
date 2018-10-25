package com.oracle.javafx.scenebuilder.kit.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class I18NControl extends ResourceBundle.Control {
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) {
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        try (InputStream is = loader.getResourceAsStream(resourceName);
             InputStreamReader isr = new InputStreamReader(is, "UTF-8");
             BufferedReader reader = new BufferedReader(isr)) {
            return new PropertyResourceBundle(reader);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
