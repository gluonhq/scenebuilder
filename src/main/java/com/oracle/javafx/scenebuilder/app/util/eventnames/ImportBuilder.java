package com.oracle.javafx.scenebuilder.app.util.eventnames;

import java.util.Map;
import java.util.TreeMap;

/**
 * Builds an import statement as a key/value pair.
 */
public class ImportBuilder {

    public static final String IMPORT_STATEMENT = "import ";
    public static final String INPUT_PACKAGE = "javafx.scene.input.";
    public static final String EVENT_PACKAGE = "javafx.event.";
    public static final String FXML_PACKAGE = "javafx.fxml.FXML";

    private static Map<String, String> imports = new TreeMap<>();

    // should be used in a static way
    private ImportBuilder() {
    }

    /**
     * Adds an imported class with its package.
     *
     * @param importedPackage package the class comes from
     * @param importedClass imported class
     */
    public static void add(String importedPackage, String importedClass) {
        imports.put(importedPackage, importedClass);
    }

    /**
     * This methods deletes all previously set events imports.
     */
    public static void reset() {
        imports.clear();
    }

    /**
     * Returns true if no imports are set, otherwise false.
     *
     * @return
     */
    public static boolean isEmpty() {
        return imports.isEmpty();
    }

    /**
     * Builds a new import statement. The key may be the "import" keyword, or the "import" keyword combined with a
     * "javafx.scene.input." package name. The value may be a full class name (e.g. with package) or an input event name.
     *
     * @return new import statement
     */
    public static String build() {
        StringBuilder sb = new StringBuilder();
        imports.forEach((key, value) -> {
            sb.append(key);
            sb.append(value);
            sb.append(";\n");
        });
        return sb.toString();
    }
}