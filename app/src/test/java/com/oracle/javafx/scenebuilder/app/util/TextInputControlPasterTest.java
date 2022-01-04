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
package com.oracle.javafx.scenebuilder.app.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.concurrent.FutureTask;

import org.junit.BeforeClass;
import org.junit.Test;

import com.oracle.javafx.scenebuilder.app.JfxInitializer;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

public class TextInputControlPasterTest {

    private TextInputControlPaster classUnderTest;

    @BeforeClass
    public static void prepare() {
       JfxInitializer.initialize();
    }

    @Test
    public void that_paste_is_not_performed_on_MacOS() throws Exception {
        TextField textField = new TextField("ShouldNotChange");
        classUnderTest = new TextInputControlPaster(textField);

        boolean isNotMacOS = false;
        copyToClipBoard("ShouldNotAppearInInputControl");
        invokeAndWait(() -> classUnderTest.pasteConditionally(isNotMacOS));

        assertEquals("ShouldNotChange", textField.getText());
    }

    @Test
    public void that_paste_is_performed_on_Linux_and_Windows() throws Exception {
        TextField textField = new TextField();
        classUnderTest = new TextInputControlPaster(textField);
        copyToClipBoard("ThisShouldBeTheNewText");

        boolean isWindowsOrLinux = true;
        invokeAndWait(() -> classUnderTest.pasteConditionally(isWindowsOrLinux));
        assertEquals("ThisShouldBeTheNewText", textField.getText());
    }

    void copyToClipBoard(String payload) throws Exception {
        invokeAndWait(() -> Clipboard.getSystemClipboard()
                                   .setContent(Map.of(DataFormat.PLAIN_TEXT, payload)));
    }

    void invokeAndWait(Runnable r) throws Exception {
        FutureTask<?> task = new FutureTask<>(r, null);
        Platform.runLater(task);
        task.get();
    }
}
