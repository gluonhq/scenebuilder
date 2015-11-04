package com.oracle.javafx.scenebuilder.kit.fxom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;

/**
 * Unit test for {@link FXOMSaver#updateImportInstructions(FXOMDocument)}.
 */
public class FXOMSaverUpdateImportInstructionsTest {

    private static FXOMDocument fxomDocument;
    private static FXOMSaver serviceUnderTest;

    @BeforeClass
    public static void initialize() {
        new JFXPanel();
    }

    @Test
    public void testEmptyFXML() throws IOException {
        setupTestCase(1);

        assertTrue("fxml is empty", fxomDocument.getFxmlText().isEmpty());
    }

    @Test
    public void testNoWildcard() {
        setupTestCase(2);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collectDeclaredClasses().forEach(dc -> {
            imports.add(dc.getName());
        });

        Set<String> givenImports = new TreeSet<>();
        givenImports.add("javafx.scene.control.Button");
        givenImports.add("javafx.scene.control.ComboBox");
        givenImports.add("javafx.scene.control.TextField");
        givenImports.add("javafx.scene.layout.AnchorPane");
        assertTrue(imports.containsAll(givenImports));

    }

    @Test
    public void testUnusedImports() {
        setupTestCase(3);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collectDeclaredClasses().forEach(dc -> {
            imports.add(dc.getName());
        });

        Set<String> unusedImports = new TreeSet<>();
        unusedImports.add("java.util.Date");
        unusedImports.add("java.math.*");
        unusedImports.add("java.util.Set");
        unusedImports.add("org.junit.Test");
        assertFalse("unused imports are not present", imports.containsAll(unusedImports));
    }

    @Test
    public void testWithWildcard() {
        setupTestCase(4);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collectDeclaredClasses().forEach(dc -> {
            imports.add(dc.getName());
        });

        assertFalse("fxml import does not contain  javafx.scene.*", imports.contains("javafx.scene.*"));

    }

    @Test
    public void testWithMoreWildcards() {
        setupTestCase(5);

        Set<String> imports = new TreeSet<>();
        // java.lang.* is not a declared class, therefore not in the imports Set
        fxomDocument.getFxomRoot().collectDeclaredClasses().forEach(dc -> {
            imports.add(dc.getName());
        });

        assertFalse("fxml import does not contain javafx.scene.*", imports.contains("javafx.scene.*"));
        assertFalse("fxml import does not contain javafx.scene.layout.*", imports.contains("javafx.control.*"));
    }

    @Test
    public void testDuplicates() {
        setupTestCase(6);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collectDeclaredClasses().forEach(dc -> {
            imports.add(dc.getName());
        });

        // java.lang.* is not a declared class, therefore not in the imports Set
        assertTrue("fxml contain only 4 imports", (imports.size() == 4));
    }

    @Test
    public void testWildcardsAndDuplicates() {
        setupTestCase(7);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collectDeclaredClasses().forEach(dc -> {
            imports.add(dc.getName());
        });

        // java.lang.* is not a declared class, therefore not in the imports Set
        imports.forEach(i -> {
            assertFalse("fxml does not contain .*", i.contains(".*"));
        });
        assertTrue("fxml contains only 4 imports", (imports.size() == 4));

    }

    @Test
    public void testCustomButton() {
        setupTestCase(8);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collectDeclaredClasses().forEach(dc -> {
            imports.add(dc.getName());
        });

        assertTrue("fxml has import com.oracle.javafx.scenebuilder.kit.fxom.TestCustomButton",
                imports.contains("com.oracle.javafx.scenebuilder.kit.fxom.TestCustomButton"));
        assertFalse("fxml does not contain com.oracle.javafx.scenebuilder.*",
                imports.contains("com.oracle.javafx.scenebuilder.*"));

        // java.lang.* is not a declared class, therefore not in the imports Set
        imports.forEach(i -> {
            assertFalse("fxml does not contain .*", i.contains(".*"));
        });

    }

    private String callService() {
        return serviceUnderTest.save(fxomDocument);
    }

    private void setupTestCase(int n) {
        String fileName = null;
        switch (n) {
        case 1:
            fileName = "Empty";
            break;
        case 2:
            fileName = "NoWildCard";
            break;
        case 3:
            fileName = "UnusedImports";
            break;
        case 4:
            fileName = "WithWildcard";
            break;
        case 5:
            fileName = "WithMoreWildcards";
            break;
        case 6:
            fileName = "Duplicates";
            break;
        case 7:
            fileName = "WildcardsAndDuplicates";
            break;
        case 8:
            fileName = "CustomButton";
            break;
        default:
            fileName = "NoFXMLFound";
            break;
        }
        Path pathToFXML = Paths.get("src/test/resources/com/oracle/javafx/scenebuilder/kit/fxom/" + fileName + ".fxml");
        Path pathToTestFXML = Paths.get("src/test/resources/com/oracle/javafx/scenebuilder/kit/fxom/testerFXML.fxml");
        try {
            Files.deleteIfExists(pathToTestFXML);

            // Setup for the fxomDocument from the FXML file that will be tested
            setupFXOMDocument(pathToFXML);

            String savedFXML = callService();

            // Creates new FXML file with the new output
            Files.write(pathToTestFXML, savedFXML.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // setup for the FXOMDocument that will be tested
    private void setupFXOMDocument(Path relativePath) {
        // relativePath =
        // "src/test/resources/com/oracle/javafx/scenebuilder/kit/fxom/[fxmlname].fxml"
        File fxmlTesterFile = new File(relativePath.getParent() + "/testerFXML.fxml");
        serviceUnderTest = new FXOMSaver();
        try {
            URL location = fxmlTesterFile.toURI().toURL();
            String fxmlString = getFxmlAsString(relativePath);

            fxomDocument = new FXOMDocument(fxmlString, location, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // reads FXML file and gives it back as a String
    private static String getFxmlAsString(Path path) throws IOException {
        List<String> fxml = null;
        fxml = Files.readAllLines(path, StandardCharsets.UTF_8);

        StringBuilder sb = new StringBuilder();
        fxml.forEach(line -> {
            sb.append(line + "\n");
        });

        return sb.toString();
    }

}