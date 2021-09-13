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
package com.oracle.javafx.scenebuilder.kit.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.oracle.javafx.scenebuilder.kit.editor.DocumentationUrls.DocumentationItem;

public class DocumentationUrlsTest {
    
    private DocumentationUrls classUnderTest;

    @Test
    public void that_error_is_raised_when_resource_file_is_missing() {
        assertThrows(AssertionError.class, 
                () -> new DocumentationUrls("myNotExistingResource.properties"));
    }
    
    @Test
    public void that_defaults_are_used_with_incomplete_properties_file() {
        classUnderTest = new DocumentationUrls("incomplete_doc_urls.properties");
        assertFalse(classUnderTest.getOptionalUrl(DocumentationItem.GLUON_SCENEBUILDER_CONTRIBUTE).isPresent());
        assertThrows(AssertionError.class,
                () -> classUnderTest.getAsMandatoryValue(DocumentationItem.GLUON_SCENEBUILDER_CONTRIBUTE));
    }

    @Test
    public void that_defaults_are_correct_and_useful() {
        classUnderTest = new DocumentationUrls("incomplete_doc_urls.properties");
        
        assertEquals("Javadoc home is used in Editor Platform", 
                     "https://openjfx.io/javadoc/11/", classUnderTest.getJavadocHome());
        
        assertEquals("Gluon Java Doc home is used in Editor Platform", 
                     "https://docs.gluonhq.com/charm/javadoc/latest/", classUnderTest.getGluonJavadocHome());
                
        assertEquals("This is used as F1 help URL", "https://docs.oracle.com/javafx/index.html", 
                     classUnderTest.getOracleDocumentation());
        
        assertEquals("The getting started guide", "https://openjfx.io/openjfx-docs/", 
                     classUnderTest.getOpenjfxGettingStarted());
        
        assertEquals("The getting started guide", "https://docs.tornadofx.io/",
                classUnderTest.getTornadoFxGettingStartedWithKotlin());
        
        assertEquals("Home of Gluon Scenebuilder", "https://gluonhq.com/products/scene-builder/",
                classUnderTest.getGluonScenebuilderHome());
        
        assertEquals("OpenJFX API docs", "https://openjfx.io/javadoc/16/",
                     classUnderTest.getOpenjfxJavadocHome());
        
        assertEquals("OpenJFX CSS Reference",
                     "https://openjfx.io/javadoc/16/javafx.graphics/javafx/scene/doc-files/cssref.html",
                     classUnderTest.getOpenjfxCssReference());
        
        assertEquals("OpenJFX FXML Reference", 
                     "https://openjfx.io/javadoc/16/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html", 
                     classUnderTest.getOpenjfxFxmlReference());
    }
    
    @Test
    public void that_default_properties_are_read_correctly_from_resource() {
        classUnderTest = new DocumentationUrls();
        
        assertEquals("Javadoc home is used in Editor Platform", 
                "https://openjfx.io/javadoc/17/", classUnderTest.getJavadocHome());
        
        assertEquals("This is used as F1 help URL", 
                "https://docs.oracle.com/javase/8/javase-clienttechnologies.htm", 
                classUnderTest.getOracleDocumentation());
    }
    
    @Test
    public void that_by_default_all_items_are_available() {
        classUnderTest = DocumentationUrls.getInstance();
        for (DocumentationItem item : DocumentationItem.values()) {
            assertTrue(DocumentationUrls.isAvailable(item));
        }
    }
}
