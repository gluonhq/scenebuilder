package com.oracle.javafx.scenebuilder.kit.util.eventnames;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
     * Builds the list of import statements. The key may be the "import" keyword, or the "import" keyword combined with a
     * "javafx.scene.input." package name. The value may be a full class name (e.g. with package) or an input event name.
     *
     * @return list of new import statements
     */
    public static List<String> build() {
        return imports.entrySet()
               .stream()
               .map(imprt->imprt.getKey()+imprt.getValue())
               .collect(Collectors.toList());
    }
}