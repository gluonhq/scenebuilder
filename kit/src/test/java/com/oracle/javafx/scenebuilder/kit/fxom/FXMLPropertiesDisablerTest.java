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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform.OS;

public class FXMLPropertiesDisablerTest {

    private FXMLPropertiesDisabler classUnderTest;
 
    @Test
    public void that_property_value_is_set_to_false_on_MacOS() throws Exception {
        classUnderTest = new FXMLPropertiesDisabler(OS.MAC);
        String fxmlText = readResourceText("ContainerWithMenuSystemMenuBarEnabled.fxml");
        assertTrue(fxmlText.contains("<MenuBar useSystemMenuBar=\"true\" VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\">"),
                "ensures that test resource is correct");
        String modfiedFxmlText = classUnderTest.disableProperties(fxmlText);
        assertTrue(modfiedFxmlText.contains("<MenuBar useSystemMenuBar=\"false\" VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\">"));
    }

    @Test
    public void that_property_value_is_not_modified_on_Windows() throws Exception {
        classUnderTest = new FXMLPropertiesDisabler(OS.WINDOWS);
        String fxmlText = readResourceText("ContainerWithMenuSystemMenuBarEnabled.fxml");
        assertTrue(fxmlText.contains("<MenuBar useSystemMenuBar=\"true\" VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\">"),
                "ensures that test resource is correct");
        String modfiedFxmlText = classUnderTest.disableProperties(fxmlText);
        assertTrue(modfiedFxmlText.contains("<MenuBar useSystemMenuBar=\"true\" VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\">"));
    }

    @Test
    public void that_property_value_is_not_modified_on_LINUX() throws Exception {
        classUnderTest = new FXMLPropertiesDisabler(OS.LINUX);
        String fxmlText = readResourceText("ContainerWithMenuSystemMenuBarEnabled.fxml");
        assertTrue(fxmlText.contains("<MenuBar useSystemMenuBar=\"true\" VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\">"),
                "ensures that test resource is correct");
        String modfiedFxmlText = classUnderTest.disableProperties(fxmlText);
        assertTrue(modfiedFxmlText.contains("<MenuBar useSystemMenuBar=\"true\" VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\">"));
    }

    private String readResourceText(String resourceName) throws Exception {
        File fxmlFileName = new File(getClass().getResource(resourceName).toURI());
        return Files.readString(fxmlFileName.toPath());
    }
}
