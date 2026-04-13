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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class FXOMImportsRemoverTest {
    
    private FXOMImportsRemover classUnderTest;

    @Test
    void that_the_given_imports_are_removed() {
        Set<String> detectedUnresolvableTypes = new HashSet<>();
        classUnderTest = new FXOMImportsRemover(detectedUnresolvableTypes::add);
        
        String sourceFxmlText = """
                <?xml version="1.0" encoding="UTF-8"?>
                <?import javafx.scene.control.*?>
                <?import another.unresolvable.Dependency?>
                <?import also.an.unresolvable.Dependency?>
                <?import this.namespace.is.unknown.*?>
                <AnchorPane>
                    <children>
                        <Button layoutX="302.0" layoutY="27.0" text="Button" />
                        <ComboBox layoutX="46.0" layoutY="175.0" prefWidth="150.0" />
                        <TextField layoutX="345.0" layoutY="264.0" />
                        <Button layoutX="84.0" layoutY="252.0" text="Button" />
                        <UnknownElement layoutX="84.0" layoutY="87.0" text="Some Content" />
                    </children>
                </AnchorPane>
                """;
        
        List<String> importsToRemove = List.of("another.unresolvable.Dependency", "also.an.unresolvable.Dependency");
        String cleanedFxmlText = classUnderTest.removeImports(sourceFxmlText, importsToRemove);
        
        String expectedFxmlText = """
                <?xml version="1.0" encoding="UTF-8"?>
                <?import javafx.scene.control.*?>
                <?import this.namespace.is.unknown.*?>
                <AnchorPane>
                    <children>
                        <Button layoutX="302.0" layoutY="27.0" text="Button" />
                        <ComboBox layoutX="46.0" layoutY="175.0" prefWidth="150.0" />
                        <TextField layoutX="345.0" layoutY="264.0" />
                        <Button layoutX="84.0" layoutY="252.0" text="Button" />
                        <UnknownElement layoutX="84.0" layoutY="87.0" text="Some Content" />
                    </children>
                </AnchorPane>
                """;
        
        assertTrue(detectedUnresolvableTypes.contains("another.unresolvable.Dependency"));
        assertTrue(detectedUnresolvableTypes.contains("also.an.unresolvable.Dependency"));
        assertEquals(expectedFxmlText.lines().limit(4).toList(), cleanedFxmlText.lines().limit(4).toList());
    }
    
    @Test
    void that_fxml_is_not_modified_when_nothing_is_to_be_removed() {
        String sourceFxmlText = """
                <?xml version="1.0" encoding="UTF-8"?>
                <?import javafx.scene.control.*?>
                <?import this.namespace.is.unknown.*?>
                <AnchorPane>
                    <children>
                        <Button layoutX="302.0" layoutY="27.0" text="Button" />
                        <ComboBox layoutX="46.0" layoutY="175.0" prefWidth="150.0" />
                        <TextField layoutX="345.0" layoutY="264.0" />
                        <Button layoutX="84.0" layoutY="252.0" text="Button" />
                        <UnknownElement layoutX="84.0" layoutY="87.0" text="Some Content" />
                    </children>
                </AnchorPane>
                """;
        
        classUnderTest = new FXOMImportsRemover();
        String cleanedFxmlText = classUnderTest.removeImports(sourceFxmlText, List.of());
        
        assertEquals(sourceFxmlText, cleanedFxmlText);
    }

}
