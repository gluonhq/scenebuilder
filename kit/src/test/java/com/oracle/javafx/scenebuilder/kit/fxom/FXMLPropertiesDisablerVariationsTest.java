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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform.OS;

@RunWith(Parameterized.class)
public class FXMLPropertiesDisablerVariationsTest {

    @Parameters(name = "{index}: fxml({0})={1}")
    public static List<String[]> data() {
        return Arrays.asList(new String[][] { 
                 { "1", "MAC", "<MenuBar useSystemMenuBar=\"true\" VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\"  "
                             + "useSystemMenuBar=\"true\">", 
                             "<MenuBar useSystemMenuBar=\"false\" VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\"  "
                           + "useSystemMenuBar=\"false\">"}, 
                 
                 { "2", "MAC","<MenuBar useSystemMenuBar=\t\"true\" VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\""
                             + " otherProperty=\"true\">",
                             "<MenuBar useSystemMenuBar=\"false\" VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\""
                             + " otherProperty=\"true\">"},
                 
                 { "3", "MAC","<MenuBar useSystemMenuBar=\"true\"\\t VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\">",
                              "<MenuBar useSystemMenuBar=\"false\"\\t VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\">"}, 
                 
                 { "4", "MAC","<MenuBar  useSystemMenuBar  =  \"true\"\\t VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\">",
                              "<MenuBar  useSystemMenuBar=\"false\"\\t VBox.vgrow=\"NEVER\" fx:id=\"theMenuBar\">"},
                 
                 { "5", "WINDOWS","<MenuBar    useSystemMenuBar  =  \"true\"\\t fx:id=\"theMenuBar\">",
                                  "<MenuBar    useSystemMenuBar  =  \"true\"\\t fx:id=\"theMenuBar\">"},
                 
                 { "6", "LINUX",  "<MenuBar    useSystemMenuBar  =  \"true\"\\t fx:id=\"theMenuBar\">",
                                  "<MenuBar    useSystemMenuBar  =  \"true\"\\t fx:id=\"theMenuBar\">"},
           });
    }

    private String id;
    private OS os;
    private String input;
    private String expected;

    public FXMLPropertiesDisablerVariationsTest(String id, String osName, String input, String expected) {
        this.id = id;
        this.input = input;
        this.expected = expected;
        this.os = OS.valueOf(osName);
    }
    
    @Test
    public void that_property_is_disabled_when_required() {
        FXMLPropertiesDisabler classUnderTest = new FXMLPropertiesDisabler(os);
        String modified = classUnderTest.disableProperties(input);
        assertEquals(id, expected, modified);
    }
}
