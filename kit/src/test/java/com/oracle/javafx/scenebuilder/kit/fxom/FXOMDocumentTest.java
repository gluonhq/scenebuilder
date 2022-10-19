/*
 * Copyright (c) 2021, 2022, Gluon and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
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
package com.oracle.javafx.scenebuilder.kit.fxom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.xml.sax.SAXParseException;

import com.oracle.javafx.scenebuilder.kit.JfxInitializer;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument.FXOMDocumentSwitch;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;

public class FXOMDocumentTest {

    private FXOMDocument classUnderTest;

    private String fxmlName;
    private URL fxmlUrl;
    private String fxmlText;
    private ClassLoader loader;
    private ResourceBundle resourceBundle;

    @BeforeAll
    public static void init() {
        JfxInitializer.initialize();
    }

    @BeforeEach
    public void prepareTest() throws Exception {
        fxmlName = "ContainerWithMenuSystemMenuBarEnabled.fxml";
        fxmlUrl = getResourceUrl(fxmlName);
        fxmlText = readResourceText(fxmlName);
        loader = this.getClass().getClassLoader();
        resourceBundle = null;
    }

    @Test
    public void that_FOR_PREVIEW_useSystemMenuBarProperty_is_enabled() throws Exception {
        classUnderTest = new FXOMDocument(fxmlText, fxmlUrl, loader, resourceBundle, FXOMDocumentSwitch.FOR_PREVIEW);

        FXOMObject fxomObject = classUnderTest.searchWithFxId("theMenuBar");

        assertTrue(fxomObject.getSceneGraphObject() instanceof MenuBar);
        assertTrue(((MenuBar) fxomObject.getSceneGraphObject()).useSystemMenuBarProperty().get(),
                "for preview, useSystemMenu is expected to be enabled");

        String generatedFxml = classUnderTest.getFxmlText(false);
        assertTrue(generatedFxml.contains("useSystemMenuBar=\"true\""));
    }

    @EnabledOnOs(value=OS.MAC)
    @Test
    public void that_useSystemMenuBarProperty_is_disabled_on_MacOS() throws Exception {
        classUnderTest = new FXOMDocument(fxmlText, fxmlUrl, loader, resourceBundle);

        FXOMObject fxomObject = classUnderTest.searchWithFxId("theMenuBar");

        assertTrue(fxomObject.getSceneGraphObject() instanceof MenuBar);
        assertFalse(((MenuBar) fxomObject.getSceneGraphObject()).useSystemMenuBarProperty().get(),
                "for preview, useSystemMenu is expected to be enabled");

        String generatedFxml = classUnderTest.getFxmlText(false);
        assertTrue(generatedFxml.contains("useSystemMenuBar=\"true\""));
    }

    @EnabledOnOs(value= {OS.LINUX, OS.WINDOWS})
    @Test
    public void that_useSystemMenuBarProperty_not_modified_on_Linux_and_Windows() throws Exception {
        classUnderTest = new FXOMDocument(fxmlText, fxmlUrl, loader, resourceBundle);

        FXOMObject fxomObject = classUnderTest.searchWithFxId("theMenuBar");

        assertTrue(fxomObject.getSceneGraphObject() instanceof MenuBar);
        assertTrue(((MenuBar) fxomObject.getSceneGraphObject()).useSystemMenuBarProperty().get(),
                "for preview, useSystemMenu is expected to be enabled");

        String generatedFxml = classUnderTest.getFxmlText(false);
        assertTrue(generatedFxml.contains("useSystemMenuBar=\"true\""));
    }

    @Test
    public void that_normalization_is_applied_only_when_NORMALIZED_is_set() throws Exception {
        fxmlText = readResourceText("NonNormalizedAccordion.fxml");
        fxmlUrl = getResourceUrl("NonNormalizedAccordion.fxml");
        classUnderTest = new FXOMDocument(fxmlText, fxmlUrl, loader, resourceBundle, FXOMDocumentSwitch.NORMALIZED);

        String generatedFxml = extractContentsOfFirstChildrenTag(classUnderTest.getFxmlText(false));
        String expectedFxml = extractContentsOfFirstChildrenTag(readResourceText("NormalizedAccordion.fxml"));
        assertEquals(expectedFxml, generatedFxml);
    }

    @Test
    public void that_normalization_is_disabled_when_NORMALIZED_is_not_set() throws Exception {
        fxmlText = readResourceText("NonNormalizedAccordion.fxml");
        fxmlUrl = getResourceUrl("NonNormalizedAccordion.fxml");
        classUnderTest = new FXOMDocument(fxmlText, fxmlUrl, loader, resourceBundle);

        String generatedFxml = extractContentsOfFirstChildrenTag(classUnderTest.getFxmlText(false));
        String expectedFxml = extractContentsOfFirstChildrenTag(readResourceText("NonNormalizedAccordion.fxml"));
        assertEquals(expectedFxml, generatedFxml);
    }

    private String readResourceText(String resourceName) throws Exception {
        File fxmlFileName = new File(getResourceUrl(resourceName).toURI());
        return useOnlyNewLine(Files.readString(fxmlFileName.toPath()));
    }

    private URL getResourceUrl(String resourceName) {
        return getClass().getResource(resourceName);
    }

    private String useOnlyNewLine(String source) {
        return source.replace("\r\n", "\n");
    }
    
    private String extractContentsOfFirstChildrenTag(String source) {
        String openingTag = "<children>";
        String closingTag = "</children>";
        int open = source.indexOf(openingTag);
        int close = source.lastIndexOf(closingTag) + closingTag.length();
        return source.substring(open, close);
    }

    @Test
    public void that_IOException_is_thrown_in_case_FXMLLoader_error() throws Exception {
        URL resource = getClass().getResource("BrokenByUserData.fxml");
        String fxmlText = FXOMDocument.readContentFromURL(resource);
        Throwable t = assertThrows(IOException.class, () -> new FXOMDocument(fxmlText, resource, null, null));
        String message = t.getMessage();
        assertTrue(message.startsWith("javafx.fxml.LoadException:"));
    }

    @Test
    public void that_illegal_null_value_for_fxmlText_raises_AssertionError() throws Exception {
        URL resource = getClass().getResource("BrokenByUserData.fxml");
        String fxmlText = null;
        assertThrows(AssertionError.class, () -> new FXOMDocument(fxmlText, resource, null, null));
    }

    @Test
    public void that_exception_in_case_of_broken_XML_is_captured() throws Exception {
        URL resource = getClass().getResource("IncompleteXml.fxml");
        String fxmlText = FXOMDocument.readContentFromURL(resource);
        Throwable t = assertThrows(IOException.class, () -> new FXOMDocument(fxmlText, resource, null, null));
        String message = t.getMessage();
        assertTrue(message.startsWith("org.xml.sax.SAXParseException;"));

        Throwable cause = t.getCause();
        assertTrue(cause instanceof SAXParseException);
    }

    @Test
    public void that_no_exception_is_created_with_empty_FXML() throws Exception {
        URL resource = getClass().getResource("Empty.fxml");
        String fxmlText = "";

        // FXOM will only apply normalization when FXOMDocumentSwitch.NORMALIZED is set 
        FXOMDocument classUnderTest = new FXOMDocument(fxmlText, resource, null, null);
        assertNotNull(classUnderTest);
    }

    @Test
    public void that_FXOMDocument_is_created_for_valid_FXML() throws Exception {
        URL validResource = getClass().getResource("ValidFxml.fxml");
        String validFxmlText = FXOMDocument.readContentFromURL(validResource);
        FXOMDocument classUnderTest = new FXOMDocument(validFxmlText, validResource, null, null);
        assertNotNull(classUnderTest);
    }

    @Test
    public void that_wildcard_imports_are_built_on_demand() throws Exception {
        URL validResource = getClass().getResource("PublicStaticImport.fxml");
        String validFxmlText = FXOMDocument.readContentFromURL(validResource);
        FXOMDocument classUnderTest = new FXOMDocument(validFxmlText, validResource, null, null);
        boolean withWildCardImports = true;

        String javaFxVersion = FXMLLoader.JAVAFX_VERSION;
        String generatedFxmlText = classUnderTest.getFxmlText(withWildCardImports);
        String expectedFxmlText = 
                  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n"
                + "<?import javafx.scene.effect.*?>\n"
                + "<?import javafx.scene.layout.*?>\n"
                + "<?import javafx.scene.text.*?>\n\n"
                + "<StackPane xmlns=\"http://javafx.com/javafx/"+javaFxVersion+"\" xmlns:fx=\"http://javafx.com/fxml/1\">\n"
                + "   <children>\n"
                + "      <Text stroke=\"BLACK\" text=\"Some simple text\">\n"
                + "         <effect>\n"
                + "            <Lighting diffuseConstant=\"2.0\" specularConstant=\"0.9\" specularExponent=\"10.5\" surfaceScale=\"9.3\">\n"
                + "               <light>\n"
                + "                  <Light.Distant />\n"
                + "               </light>\n"
                + "            </Lighting>\n"
                + "         </effect>\n"
                + "      </Text>\n"
                + "   </children>\n"
                + "</StackPane>\n";
        assertEquals(expectedFxmlText, generatedFxmlText);
    }

    @Test
    public void that_generated_FXML_text_is_empty_for_empty_FXOMDocument() throws Exception {
        FXOMDocument classUnderTest = new FXOMDocument();
        boolean withWildCardImports = false;
        String generatedFxmlText = classUnderTest.getFxmlText(withWildCardImports);

        assertEquals("", generatedFxmlText);
    }

    @Test
    public void that_fxml_with_defines_loads_without_error_without_normalization() throws Exception {
        URL resource = getClass().getResource("DynamicScreenSize.fxml");
        String validFxmlText = FXOMDocument.readContentFromURL(resource);

        // Non-normalizing by default
        FXOMDocument classUnderTest = waitFor(() -> new FXOMDocument(validFxmlText, resource, null, null));
        String beforeNormalization = classUnderTest.getFxmlText(false);
        assertFalse(beforeNormalization.isBlank());
    }

    @Test
    public void that_missing_imports_during_defines_resolution_cause_exception() throws Exception {
        URL resource = getClass().getResource("DynamicScreenSize.fxml");
        String validFxmlText = FXOMDocument.readContentFromURL(resource);

        Throwable t = assertThrows(Throwable.class,
                       () -> waitFor(() -> new FXOMDocument(validFxmlText, resource, null, null, FXOMDocumentSwitch.NORMALIZED)));
        
        if (t.getCause() != null) {
            t = t.getCause();
        }
         
        assertEquals(IllegalStateException.class, t.getClass());
        assertTrue(t.getMessage().contains("Bug in FXOMRefresher"));
    }

    private <T> T waitFor(Callable<T> callable) throws Exception {
        FutureTask<T> task = new FutureTask<T>(callable);
        if (Platform.isFxApplicationThread()) {
            return callable.call();
        } else {
            Platform.runLater(()->task.run());
            return task.get();
        }
    }
}
