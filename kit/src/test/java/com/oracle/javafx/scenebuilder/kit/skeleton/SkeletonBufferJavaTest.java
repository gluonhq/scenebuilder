/*
 * Copyright (c) 2021, 2022, Gluon and/or its affiliates.
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

import com.oracle.javafx.scenebuilder.kit.JfxInitializer;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class SkeletonBufferJavaTest {

    @BeforeAll
    public static void initialize() {
        JfxInitializer.initialize();
    }

    @Test
    public void skeletonToString_nestedTestFxml() throws IOException {
        // given
        SkeletonBuffer skeletonBuffer = load("TestNested.fxml");

        // when
        String skeleton = skeletonBuffer.toString();

        // then
        assertEqualsFileContent("skeleton_java_nested.txt", skeleton);
    }

    @Test
    public void skeletonToString_testFxml_full_withComments() throws IOException {
        // given
        SkeletonBuffer skeletonBuffer = load("Test.fxml");
        skeletonBuffer.setFormat(SkeletonSettings.FORMAT_TYPE.FULL);
        skeletonBuffer.setTextType(SkeletonSettings.TEXT_TYPE.WITH_COMMENTS);

        // when
        String skeleton = skeletonBuffer.toString();

        // then
        assertEqualsFileContent("skeleton_java_full_comments.txt", skeleton);
    }

    @Test
    public void skeletonToString_testFxml_withComments() throws IOException {
        // given
        SkeletonBuffer skeletonBuffer = load("Test.fxml");
        skeletonBuffer.setTextType(SkeletonSettings.TEXT_TYPE.WITH_COMMENTS);

        // when
        String skeleton = skeletonBuffer.toString();

        // then
        assertEqualsFileContent("skeleton_java_comments.txt", skeleton);
    }

    @Test
    public void skeletonToString_testFxml_fullFormat() throws IOException {
        // given
        SkeletonBuffer skeletonBuffer = load("Test.fxml");
        skeletonBuffer.setFormat(SkeletonSettings.FORMAT_TYPE.FULL);

        // when
        String skeleton = skeletonBuffer.toString();

        // then
        assertEqualsFileContent("skeleton_java_full.txt", skeleton);
    }

    private void assertEqualsFileContent(String fileName, String actual) {
        URL url = this.getClass().getResource(fileName);
        File file = new File(url.getFile());

        try {
            String expectedFileContent = Files.readString(file.toPath());
            assertEquals(expectedFileContent, actual);
        } catch (IOException e) {
            fail("Unable to open file: " + fileName);
        }
    }

    private SkeletonBuffer load(String fxmlFile) throws IOException {
        EditorController editorController = new EditorController();
        final URL fxmlURL = SkeletonBufferJavaTest.class.getResource(fxmlFile);
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);

        SkeletonBuffer skeletonBuffer = new SkeletonBuffer(editorController.getFxomDocument(), "test");
        skeletonBuffer.setLanguage(SkeletonSettings.LANGUAGE.JAVA);
        return skeletonBuffer;
    }
}
