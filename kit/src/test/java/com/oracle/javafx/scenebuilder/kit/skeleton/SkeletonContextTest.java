/*
 * Copyright (c) 2025, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.skeleton;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.oracle.javafx.scenebuilder.kit.JfxInitializer;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.skeleton.SkeletonContext.Builder;
import com.oracle.javafx.scenebuilder.kit.skeleton.SkeletonSettings.FORMAT_TYPE;

class SkeletonContextTest {
    
    @BeforeAll
    public static void init() {
        JfxInitializer.initialize();
    }

    @Test
    void that_fxinclude_with_fxid_is_added_to_skeleton_context_without_error() throws IOException {
        // SETUP
        String documentName = "fxinclude_with_fxid_outer.fxml";       
        FXOMDocument doc = createDocument(documentName);
        Builder builder = SkeletonContext.builder()
                                         .withDocumentName(documentName)
                                         .withSettings(new SkeletonSettings());
                
        /* Obtain the SceneGraph object reference and add it to the context */
        FXOMObject object = doc.searchWithFxId("includedPane");
        
        // TEST
        assertDoesNotThrow(() -> builder.addFxId(object));

        SkeletonContext classUnderTest = builder.build();       
        assertAll(
            () -> assertTrue(object instanceof FXOMIntrinsic),
            () -> assertTrue(classUnderTest.getImports().contains("import javafx.scene.layout.Pane")),
            () -> assertTrue(classUnderTest.getVariables().get("includedPane").equals(javafx.scene.layout.Pane.class)),
            () -> assertTrue(classUnderTest.getAssertions().contains("includedPane"))
        );
    }
    
    @Test
    void that_label_with_fxid_is_only_added_once() throws IOException {
        // SETUP
        String documentName = "fxinclude_with_fxid_outer.fxml";
        FXOMDocument doc = createDocument(documentName);
        Builder builder = SkeletonContext.builder()
                                         .withDocumentName(documentName)
                                         .withSettings(new SkeletonSettings().withFormat(FORMAT_TYPE.FULL));
        
        /* Obtain the SceneGraph object reference and add it to the context */
        FXOMObject object = doc.searchWithFxId("myLabel");
        
        // TEST
        assertDoesNotThrow(() -> builder.addFxId(object));
                
        SkeletonContext classUnderTest = builder.build();       
        assertAll(
            () -> assertFalse(object instanceof FXOMIntrinsic),
            () -> assertTrue(classUnderTest.getImports().contains("import javafx.scene.control.Label")),
            () -> assertTrue(classUnderTest.getVariables().get("myLabel").equals(javafx.scene.control.Label.class)),
            () -> assertTrue(classUnderTest.getAssertions().contains("myLabel"))
        );
    }

    private FXOMDocument createDocument(String documentName) throws IOException {
        final URL fxmlURL = SkeletonBufferJavaTest.class.getResource(documentName);
        String document = FXOMDocument.readContentFromURL(fxmlURL);
        ClassLoader classLoader = null;
        ResourceBundle resourceBundle = null;
        FXOMDocument doc = new FXOMDocument(document, fxmlURL, classLoader, resourceBundle);
        return doc;
    }
    

}
