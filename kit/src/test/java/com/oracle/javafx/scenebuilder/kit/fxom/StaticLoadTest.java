/*
 * Copyright (c) 2017, 2022, Gluon and/or its affiliates.
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

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import java.io.IOException;
import java.net.URL;

import com.oracle.javafx.scenebuilder.kit.JfxInitializer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link com.oracle.javafx.scenebuilder.kit.util.Deprecation#setStaticLoad(javafx.fxml.FXMLLoader, boolean) }
 */
public class StaticLoadTest {
    
    private boolean thrown;
    
    @BeforeAll
    public static void initJFX() {
        JfxInitializer.initialize();
    }
    
    @Test
    public void testStaticLoadWithoutEventHandler() throws IOException {
        thrown = false;
        EditorController editorController = new EditorController();
        final URL fxmlURL = StaticLoadTest.class.getResource("TestStaticLoadWithoutEventHandler.fxml");
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);
        } catch (IOException e) {
           thrown = true;
        }

        assertFalse(thrown);
    } 
    
    @Test
    public void testStaticLoad() throws IOException {
        thrown = false;
        EditorController editorController = new EditorController();
        final URL fxmlURL = StaticLoadTest.class.getResource("TestStaticLoad.fxml");
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);
        } catch (IOException e) {
           thrown = true;
        }

        assertFalse(thrown);
    } 
}
