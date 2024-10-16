/*
 * Copyright (c) 2022, 2024, Gluon and/or its affiliates.
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.oracle.javafx.scenebuilder.kit.JfxInitializer;

public class FXOMLoaderTest {

    @BeforeAll
    public static void init() {
        JfxInitializer.initialize();
    }

    @Test
    public void that_LoadException_caused_by_XMLStreamException_is_handled() throws Exception {
        String invalidXmlText = FXOMDocument.readContentFromURL(getClass().getResource("IncompleteXml.fxml"));
        URL validResource = getClass().getResource("CompleteFxml.fxml");
        String validFxmlText = FXOMDocument.readContentFromURL(validResource);
        FXOMDocument document = new FXOMDocument(validFxmlText, validResource, null, null);

        // When there are exceptions, then the error handler should store these here
        Map<Class<?>, Throwable> handledErrors = new HashMap<>();
        
        // In Scene Builder, the error is displayed in an error dialog.
        // For testing, this custom error handler replaces the dialog.
        Consumer<Exception> errorHandler = ex -> {
            handledErrors.put(ex.getClass(), ex);
            if (ex.getCause() != null) {
                handledErrors.put(ex.getCause().getClass(), ex.getCause());
            }
        };

        FXOMLoader classUnderTest = new FXOMLoader(document, errorHandler);
        assertDoesNotThrow(()->classUnderTest.load(invalidXmlText));

        assertTrue(handledErrors.values().stream().anyMatch(v -> v instanceof XMLStreamException));
        assertTrue(handledErrors.containsKey(javafx.fxml.LoadException.class));
    }
}
