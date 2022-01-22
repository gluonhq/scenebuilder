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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.oracle.javafx.scenebuilder.kit.skeleton.SkeletonSettings.LANGUAGE;

public class SkeletonFileNameProposalTest {

    private SkeletonFileNameProposal classUnderTest;

    @Test
    public void that_default_java_file_is_created_on_new_documents() {
        classUnderTest = new SkeletonFileNameProposal(LANGUAGE.JAVA);
        File result = classUnderTest.create(null, null);
        File expected = new File(System.getProperty("user.home"), "PleaseProvideControllerClassName.java");

        assertEquals(expected, result);
    }

    @Test
    public void that_default_kotlin_file_is_created_on_new_documents() {
        classUnderTest = new SkeletonFileNameProposal(LANGUAGE.KOTLIN);
        File result = classUnderTest.create(null, null);
        File expected = new File(System.getProperty("user.home"), "PleaseProvideControllerClassName.kt");

        assertEquals(expected, result);
    }

    @Test
    public void that_controllerName_is_used_when_available() {
        classUnderTest = new SkeletonFileNameProposal(LANGUAGE.JAVA);
        String fxControllerName = "com.oracle.javafx.scenebuilder.kit.skeleton.SkeletonTest$SkeletonTestController";
        File result = classUnderTest.create(null, fxControllerName);
        File expected = new File(System.getProperty("user.home"), "SkeletonTestController.java");

        assertEquals(expected, result);
    }

    @Test
    public void that_controllerName_is_preferred_over_fxmlLocation_and_directory_is_used_from_fxml() throws Exception {
        classUnderTest = new SkeletonFileNameProposal(LANGUAGE.JAVA);
        String fxControllerName = "com.oracle.javafx.scenebuilder.kit.skeleton.SkeletonTest$SkeletonTestController";
        URL fxmlLocation = new File("src/test/resources/com/oracle/javafx/scenebuilder/kit/fxom/Empty.fxml").toURI()
                .toURL();
        File result = classUnderTest.create(fxmlLocation, fxControllerName);
        File expected = new File("src/test/resources/com/oracle/javafx/scenebuilder/kit/fxom",
                "SkeletonTestController.java").getAbsoluteFile();

        assertEquals(expected, result);
    }

    @Test
    public void that_fxmlLocation_in_resources_dir_is_changed_to_java_specific_directory() throws Exception {
        classUnderTest = new SkeletonFileNameProposal(LANGUAGE.JAVA);
        String fxControllerName = "com.oracle.javafx.scenebuilder.kit.skeleton.SkeletonTest$SkeletonTestController";
        URL fxmlLocation = new File("src/main/resources/com/oracle/javafx/scenebuilder/kit/skeleton/SkeletonWindow.fxml").toURI().toURL();
        File result = classUnderTest.create(fxmlLocation, fxControllerName);
        File expected = new File("src/main/java/com/oracle/javafx/scenebuilder/kit/skeleton",
                "SkeletonTestController.java").getAbsoluteFile();

        assertEquals(expected, result);
    }

    @Test
    public void that_fxmlLocation_in_resources_dir_is_changed_to_kotlin_specific_directory(@TempDir Path temporaryDirectory) throws Exception {

        String sourceFolder = "com/oracle/javafx/scenebuilder/kit/skeleton";
        Path resourcesDir = temporaryDirectory.resolve("src/main/resources").resolve(sourceFolder);
        Path kotlinDir = temporaryDirectory.resolve("src/main/kotlin").resolve(sourceFolder);

        Files.createDirectories(resourcesDir);
        Files.createDirectories(kotlinDir);

        classUnderTest = new SkeletonFileNameProposal(LANGUAGE.KOTLIN);
        String fxControllerName = "com.oracle.javafx.scenebuilder.kit.skeleton.SkeletonTest$SkeletonTestController";
        URL fxmlLocation = resourcesDir.resolve("SkeletonWindow.fxml").toFile().toURI().toURL();

        File result = classUnderTest.create(fxmlLocation, fxControllerName);
        File expected = kotlinDir.resolve("SkeletonTestController.kt").toFile();

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void that_controllerName_is_preferred_over_fxmlLocation_in_user_directory() throws Exception {
        classUnderTest = new SkeletonFileNameProposal(LANGUAGE.JAVA);
        String fxControllerName = "com.oracle.javafx.scenebuilder.kit.skeleton.SkeletonTest$SkeletonTestController";
        URL fxmlLocation = new File("not-existing-location/Empty.fxml").toURI().toURL();
        File result = classUnderTest.create(fxmlLocation, fxControllerName);
        File expected = new File(System.getProperty("user.home"), "SkeletonTestController.java");

        assertEquals(expected, result);
    }

    @Test
    public void that_fxmlLocations_is_used_when_controllerName_not_exists() throws Exception {
        classUnderTest = new SkeletonFileNameProposal(LANGUAGE.JAVA);
        URL fxmlLocation = new File("Empty.fxml").getAbsoluteFile().toURI().toURL();
        File result = classUnderTest.create(fxmlLocation, null);
        File expected = new File("EmptyController.java").getAbsoluteFile();

        assertEquals(expected.getAbsolutePath(), result.getAbsolutePath());
    }

    @Test
    public void that_incorrectly_named_fxml_can_be_handled() throws Exception {
        classUnderTest = new SkeletonFileNameProposal(LANGUAGE.JAVA);
        URL fxmlLocation = new File("EmptyFxml").getAbsoluteFile().toURI().toURL();
        File result = classUnderTest.create(fxmlLocation, null);
        File expected = new File("EmptyFxmlController.java").getAbsoluteFile();

        assertEquals(expected.getAbsolutePath(), result.getAbsolutePath());
    }

    @Test
    public void that_fxmllocation_is_used_when_language_specific_resource_dir_not_exists(@TempDir Path temporaryDirectory) throws Exception {
        File resourcesDir = temporaryDirectory.resolve("src/main/resources").toFile();
        File javaDir = temporaryDirectory.resolve("src/main/java").toFile();
        File kotlinDir = temporaryDirectory.resolve("src/main/kotlin").toFile();
        URL fxmlLocation = new File(resourcesDir.toString(), "SkeletonWindow.fxml").toURI().toURL();

        Files.createDirectories(javaDir.toPath());
        assertTrue(javaDir.exists());
        assertFalse(kotlinDir.exists());
        assertFalse(resourcesDir.exists());

        // JAVA
        classUnderTest = new SkeletonFileNameProposal(LANGUAGE.JAVA);
        File result = classUnderTest.create(fxmlLocation, null);
        File expected = new File(javaDir, "SkeletonWindowController.java");
        assertEquals(expected.toString(), result.toString());

        // KOTLIN - the kotlin folder does not exist, thus the originally used resource folder is returned
        classUnderTest = new SkeletonFileNameProposal(LANGUAGE.KOTLIN);
        result = classUnderTest.create(fxmlLocation, null);
        expected = new File(resourcesDir, "SkeletonWindowController.kt");
        assertEquals(expected.toString(), result.toString());
    }
}
