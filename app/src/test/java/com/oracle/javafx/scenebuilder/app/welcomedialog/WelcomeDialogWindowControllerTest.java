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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.oracle.javafx.scenebuilder.app.JfxInitializer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class WelcomeDialogWindowControllerTest {
    
    private final WelcomeDialogWindowController classUnderTest = new WelcomeDialogWindowController();
    
    @BeforeAll
    public static void initialize() {
        JfxInitializer.initialize();
    }
    
    @Test
    void that_missing_files_are_detected_and_handled_and_existing_files_are_loaded() throws Exception {
        String expectedExistingFile = getResource("WelcomeWindow.fxml").toString();
        List<String> filesToLoad = List.of(
                    "k:/folder/test/notExisting.fxml",
                    expectedExistingFile);
        
        List<String> filesMissing = new ArrayList<>();
        List<String> filesLoaded = new ArrayList<>();
        
        Consumer<List<String>> missingFilesHandler = missing -> filesMissing.addAll(missing);
        Consumer<List<String>> existingFilesHandler = existing -> filesLoaded.addAll(existing);
        
        assertDoesNotThrow(() -> classUnderTest.handleOpen(filesToLoad, 
                                                         missingFilesHandler, 
                                                         existingFilesHandler));
        
        assertEquals(1, filesMissing.size());
        assertEquals(1, filesLoaded.size());
        assertTrue(filesLoaded.contains(expectedExistingFile));
    }
    
    @Test
    void that_no_actions_are_performed_on_empty_list() {
        List<String> filesToLoad = Collections.emptyList();
        
        Set<String> actionsPerformed = new HashSet<>();
        Consumer<List<String>> filesHandler = listOffiles -> actionsPerformed.add("some action performed");
        classUnderTest.handleOpen(filesToLoad, filesHandler, filesHandler);
        
        assertTrue(actionsPerformed.isEmpty());
    }
    
    @Test
    void that_file_loader_is_not_called_when_all_files_are_missing() {
        List<String> filesToLoad = List.of(
                "k:/folder/test/notExisting.fxml",
                "o:\\otherLocation\\another_missing.fxml");
        
        List<String> filesMissing = new ArrayList<>();
        List<String> filesLoaded = new ArrayList<>();
        
        Consumer<List<String>> missingFilesHandler = missing -> filesMissing.addAll(missing);
        Consumer<List<String>> existingFilesHandler = existing -> filesLoaded.addAll(existing);
        classUnderTest.handleOpen(filesToLoad, missingFilesHandler, existingFilesHandler);
        
        assertTrue(filesLoaded.isEmpty());
        assertEquals(2, filesMissing.size());
    }
    
    private Path getResource(String resourceName) throws Exception {
        return Path.of(getClass().getResource(resourceName).toURI());
    }
}
