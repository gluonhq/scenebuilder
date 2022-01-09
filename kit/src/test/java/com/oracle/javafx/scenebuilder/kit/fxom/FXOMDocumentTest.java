/*
 * Copyright (c) 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.fxom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.oracle.javafx.scenebuilder.kit.JfxInitializer;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument.FXOMDocumentSwitch;

import javafx.scene.control.MenuBar;

public class FXOMDocumentTest {

    private FXOMDocument classUnderTest;

    private String fxmlName;
    private URL fxmlUrl;
    private String fxmlText;
    private ClassLoader loader;
    private ResourceBundle resourceBundle;

    @BeforeClass
    public static void init() {
        JfxInitializer.initialize();
    }

    @Before
    public void prepareTest() throws Exception {
        fxmlName = "ContainerWithMenu_SystemMenuBarEnabled.fxml";
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
        assertTrue("for preview, useSystemMenu is expected to be enabled",
                ((MenuBar) fxomObject.getSceneGraphObject()).useSystemMenuBarProperty().get());

        String generatedFxml = classUnderTest.getFxmlText(false);
        assertTrue(generatedFxml.contains("useSystemMenuBar=\"true\""));
    }

    @Test
    public void that_useSystemMenuBarProperty_is_disabled_on_MacOS() throws Exception {
        boolean isMacOS = EditorPlatform.IS_MAC;
        if (isMacOS) {
            classUnderTest = new FXOMDocument(fxmlText, fxmlUrl, loader, resourceBundle);

            FXOMObject fxomObject = classUnderTest.searchWithFxId("theMenuBar");

            assertTrue(fxomObject.getSceneGraphObject() instanceof MenuBar);
            assertFalse("for preview, useSystemMenu is expected to be enabled",
                    ((MenuBar) fxomObject.getSceneGraphObject()).useSystemMenuBarProperty().get());

            String generatedFxml = classUnderTest.getFxmlText(false);
            assertTrue(generatedFxml.contains("useSystemMenuBar=\"true\""));
        }
    }
    
    @Test
    public void that_useSystemMenuBarProperty_not_modified_on_Linux_and_Windows() throws Exception {
        boolean isWinOrLinux = EditorPlatform.IS_WINDOWS | EditorPlatform.IS_LINUX;
        if (isWinOrLinux) {
            classUnderTest = new FXOMDocument(fxmlText, fxmlUrl, loader, resourceBundle);

            FXOMObject fxomObject = classUnderTest.searchWithFxId("theMenuBar");

            assertTrue(fxomObject.getSceneGraphObject() instanceof MenuBar);
            assertTrue("for preview, useSystemMenu is expected to be enabled",
                    ((MenuBar) fxomObject.getSceneGraphObject()).useSystemMenuBarProperty().get());

            String generatedFxml = classUnderTest.getFxmlText(false);
            assertTrue(generatedFxml.contains("useSystemMenuBar=\"true\""));
        }
    }


    @Test
    public void that_normalization_is_applied_only_when_NORMALIZED_is_set() throws Exception {
        fxmlText = readResourceText("NonNormalized_Accordion.fxml");
        fxmlUrl = getResourceUrl("NonNormalized_Accordion.fxml");
        classUnderTest = new FXOMDocument(fxmlText, fxmlUrl, loader, resourceBundle, FXOMDocumentSwitch.NORMALIZED);

        String generatedFxml = extractContentsOfFirstChildrenTag(classUnderTest.getFxmlText(false));
        String expectedFxml = extractContentsOfFirstChildrenTag(readResourceText("Normalized_Accordion.fxml"));
        assertEquals(expectedFxml, generatedFxml);
    }

    @Test
    public void that_normalization_is_disabled_when_NORMALIZED_is_not_set() throws Exception {
        fxmlText = readResourceText("NonNormalized_Accordion.fxml");
        fxmlUrl = getResourceUrl("NonNormalized_Accordion.fxml");
        classUnderTest = new FXOMDocument(fxmlText, fxmlUrl, loader, resourceBundle);

        String generatedFxml = extractContentsOfFirstChildrenTag(classUnderTest.getFxmlText(false));
        String expectedFxml = extractContentsOfFirstChildrenTag(readResourceText("NonNormalized_Accordion.fxml"));
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
}
