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
package com.oracle.javafx.scenebuilder.kit.fxom;

import static com.oracle.javafx.scenebuilder.kit.fxom.FXOMTestHelper.createFXOMDocumentFrom;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.oracle.javafx.scenebuilder.kit.JfxInitializer;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument.FXOMDocumentSwitch;

class FXOMSaverTest {

    @BeforeAll
    public static void init() {
        JfxInitializer.initialize();
    }

    @Test
    void that_FXOMDocument_with_unresolvable_imports_can_be_saved_with_all_imports_preserved() throws Exception {
        URL location = FXOMTestHelper.class.getResource("UnresolvableImports.fxml");

        FXOMDocument document = createFXOMDocumentFrom(location, FXOMDocumentSwitch.PRESERVE_UNRESOLVED_IMPORTS);

        FXOMSaver classUnderTest = new FXOMSaver();
        Path outfile = Path.of("FXOMSaverTest_withUnresolvedTypes.fxml");
        document.setLocation(outfile.toUri().toURL());

        String generatedFxml = assertDoesNotThrow(() -> classUnderTest.save(document));
        String generatedImports = generatedFxml.lines()
                                               .filter(l -> l.startsWith("<?import"))
                                               .collect(Collectors.joining("\n"));
        
        String expectedImports = """
                <?import also.an.unresolvable.Dependency?>
                <?import another.unresolvable.Dependency?>
                <?import javafx.scene.control.Button?>
                <?import javafx.scene.control.ComboBox?>
                <?import javafx.scene.control.TextField?>
                <?import javafx.scene.layout.AnchorPane?>""";

        assertEquals(expectedImports, generatedImports);

    }
    
    @Test
    void that_FXOMSaver_preserves_wildcard_imports_if_requested() throws Exception {
        URL location = FXOMTestHelper.class.getResource("UnresolvableImports.fxml");

        FXOMDocument document = createFXOMDocumentFrom(location, FXOMDocumentSwitch.PRESERVE_UNRESOLVED_IMPORTS);
        boolean preserveWildCardImports = true;
        
        FXOMSaver classUnderTest = new FXOMSaver(preserveWildCardImports);
        Path outfile = Path.of("FXOMSaverTest_withUnresolvedTypes.fxml");
        document.setLocation(outfile.toUri().toURL());

        String generatedFxml = assertDoesNotThrow(() -> classUnderTest.save(document));
        String generatedImports = generatedFxml.lines()
                .filter(l -> l.startsWith("<?import"))
                .collect(Collectors.joining("\n"));

        /*
         * Unresolved types are not considered when wildcard imports are allowed.
         */
        String expectedImports = """
                <?import also.an.unresolvable.Dependency?>
                <?import another.unresolvable.Dependency?>
                <?import javafx.scene.control.*?>
                <?import javafx.scene.layout.*?>""";
        
        assertEquals(expectedImports, generatedImports);
    }

}
