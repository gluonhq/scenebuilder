/*
 * Copyright (c) 2024, Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.app.welcomedialog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class WelcomeDialogFilesDropHandlerTest {

    private WelcomeDialogFilesDropHandler classUnderTest;

    @Test
    void that_fxml_file_is_detected_properly(@TempDir Path directory) {
        List<File> droppedFiles = List.of();
        classUnderTest = new WelcomeDialogFilesDropHandler(droppedFiles);

        assertFalse(classUnderTest.isFxml(directory.toFile()), "directories are not FXML");
        assertFalse(classUnderTest.isFxml(new File("SomeImage.png")), "FXML files must have fxml file name extension");
        assertTrue(classUnderTest.isFxml(new File("View.fxml")), "FXML files must have fxml file name extension");
        assertTrue(classUnderTest.isFxml(new File("View.FxMl")), "FXML extension detection must not be case sensitive");
    }

    @Test
    void that_exception_is_raised_with_incomplete_configuration() {
        classUnderTest = new WelcomeDialogFilesDropHandler(List.of());
        assertThrows(IllegalStateException.class, ()->classUnderTest.run());

        classUnderTest.withSupportedFiles(files->System.out.println(files));
        assertThrows(IllegalStateException.class, ()->classUnderTest.run());
        
        classUnderTest.withUnsupportedFiles(unsupported->System.out.println(unsupported));
        assertDoesNotThrow(()->classUnderTest.run());
    }

    @Test
    void that_list_of_fxml_files_will_passed_to_open_action() {
        List<File> droppedFiles = List.of(new File("MainView.fxml"), new File("SubView.fxml"));
        
        // Action handler for opening files
        List<String> fileOpenResults = new ArrayList<>();
        Consumer<List<String>> openFilesAction = files->{
            for (String file : files) {
                fileOpenResults.add("opened " + new File(file).getName());
            }
        };
        
        // Action handler to notify user on unsupported items
        List<String> unsupportedFiles = new ArrayList<>();
        Consumer<List<String>> unsupportedFileHandling = unsupported->unsupportedFiles.addAll(unsupported);
        
        classUnderTest = new WelcomeDialogFilesDropHandler(droppedFiles)
                    .withSupportedFiles(openFilesAction)
                    .withUnsupportedFiles(unsupportedFileHandling);
        
        assertDoesNotThrow(()->classUnderTest.run());
        assertEquals(2, fileOpenResults.size());
        assertEquals("opened MainView.fxml", fileOpenResults.get(0));
        assertTrue(unsupportedFiles.isEmpty());
    }

    @Test
    void that_an_attempt_to_handle_unsupported_files_triggers_appropriate_action(@TempDir Path emptyDir) throws Exception {
        List<File> droppedFiles = List.of(new File("Image.png"), emptyDir.toFile());

        // Action handler for opening files
        List<String> fileOpenResults = new ArrayList<>();
        Consumer<List<String>> openFilesAction = files->fileOpenResults.addAll(files);
        
        // Action handler to notify user on unsupported items
        List<String> unsupportedFiles = new ArrayList<>();
        Consumer<List<String>> unsupportedFileHandling = unsupported->{
            for (String file : unsupported) {
                var item = new File(file);
                if (item.isDirectory()) {
                    unsupportedFiles.add(new File(file).getName() + "(dir is empty)");
                } else {
                    unsupportedFiles.add(new File(file).getName());
                }
            }
        };

        classUnderTest = new WelcomeDialogFilesDropHandler(droppedFiles)
                .withSupportedFiles(openFilesAction)
                .withUnsupportedFiles(unsupportedFileHandling);
        classUnderTest.run();

        assertTrue(fileOpenResults.isEmpty());
        assertEquals(2, unsupportedFiles.size());
        assertEquals("Image.png", unsupportedFiles.get(0));
        assertTrue(unsupportedFiles.get(1).endsWith("(dir is empty)"));
    }

    @Test
    void that_dropped_subdirectories_are_searched_for_fxml_in_first_level(@TempDir Path fxmlDir) {
        List<File> droppedFiles = List.of(new File("src/main/resources/com/oracle/javafx/scenebuilder/app/welcomedialog"),
                                          new File("src/main/resources/com/oracle/javafx/scenebuilder/app/DocumentWindow.fxml"),
                                          new File("src/main/resources/com/oracle/javafx/scenebuilder/app/SceneBuilderLogo_32.png"));

        // Action handler for opening files
        Set<String> fileOpenResults = new HashSet<>();
        Consumer<List<String>> openFilesAction = files->{
            for (String file : files) {
                fileOpenResults.add("opened " + new File(file).getName());
            }
        };

        // Action handler to notify user on unsupported items
        List<String> unsupportedFiles = new ArrayList<>();
        Consumer<List<String>> unsupportedFileHandling = unsupported->unsupportedFiles.addAll(unsupported);

        classUnderTest = new WelcomeDialogFilesDropHandler(droppedFiles)
                    .withSupportedFiles(openFilesAction)
                    .withUnsupportedFiles(unsupportedFileHandling);
        
        classUnderTest.run();
        assertTrue(fileOpenResults.contains("opened WelcomeWindow.fxml"), "FXML from dropped directory");
        assertTrue(fileOpenResults.contains("opened DocumentWindow.fxml"), "FXML file dropped");
        assertTrue(unsupportedFiles.isEmpty());
    }

}
