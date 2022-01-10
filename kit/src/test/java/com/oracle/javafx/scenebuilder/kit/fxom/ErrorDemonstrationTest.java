/*
 * Copyright (c) 2022, Gluon and/or its affiliates.
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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.FutureTask;

import org.junit.BeforeClass;
import org.junit.Test;

import com.oracle.javafx.scenebuilder.kit.JfxInitializer;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

public class ErrorDemonstrationTest {
    
    @BeforeClass
    public static void init() {
        JfxInitializer.initialize();
    }
    
    @Test
    public void that_FXML_with_dynamic_screen_size_is_loaded_without_error() throws Exception {
        URL resource = getClass().getResource("DynamicScreenSize.fxml");
        String validFxmlText = FXOMDocument.readContentFromURL(resource);
        invokeAndWait(()->{
            try {
                FXOMDocument classUnderTest = new FXOMDocument(validFxmlText, resource, null, null);
            } catch (Exception anyError) {
                anyError.printStackTrace();
            }
        });
        
    }
    
    @Test
    public void that_FXMLLoader_with_dynamic_screen_size_is_loaded_without_error() throws Exception {
        URL resource = getClass().getResource("DynamicScreenSize.fxml");
        FXMLLoader loader = new FXMLLoader();
        invokeAndWait(()->{
            try {
                Object node = loader.load(resource.openStream());
                assertNotNull(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    void invokeAndWait(Runnable r) throws Exception {
        FutureTask<?> task = new FutureTask<>(r, null);
        Platform.runLater(task);
        task.get();
    }
}
