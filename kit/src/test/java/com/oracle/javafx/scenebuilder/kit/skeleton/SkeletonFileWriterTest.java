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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.beans.property.SimpleStringProperty;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class SkeletonFileWriterTest {

    private SkeletonFileWriter classUnderTest;
    private SimpleStringProperty textProperty = new SimpleStringProperty("TheControllerCode!");
    private Supplier<Stage> stageSupplier = () -> null;
    private List<File> filesProposed = new ArrayList<>();
    private List<ExtensionFilter> fileExtensionFilters = new ArrayList<>();

    @TempDir
    public Path temporaryDirectory;

    @Test
    public void that_dafaults_in_public_constructor_are_correct() {

        classUnderTest = new SkeletonFileWriter(stageSupplier, textProperty);

        assertEquals(SkeletonFileWriterErrorAlert.class, 
                classUnderTest.getOnError().apply(stageSupplier).getClass());

        assertEquals(SkeletonFileWriterSuccessAlert.class,
                classUnderTest.getOnSuccess().apply(stageSupplier).getClass());

    }

    @Test
    public void that_alert_is_raised_in_case_of_error() {

        /* GIVEN Scenario:
         * 
         * - The user enters a location which does not exist.
         * - The file chooser accepts this illegal location.
         * - It is expected, that exception and file are 
         *   passed to the error handler
         * 
         */
        String controllerName = "MyCustomControllerName";
        SkeletonSettings.LANGUAGE language = SkeletonSettings.LANGUAGE.JAVA;
        File fileAtIllegalLocation = new File("//notExisting/share/test.java");

        Map<File, Exception> raisedErrors = new HashMap<>();
        Function<Supplier<Stage>, BiConsumer<File, Exception>> errorHandler = stage -> (file, error) -> {
            raisedErrors.put(file, error);
        };

        // WHEN
        classUnderTest = new SkeletonFileWriter(stageSupplier, textProperty, 
                                               (fc, stage) -> fileAtIllegalLocation,
                                                onSuccess(),  errorHandler);

        classUnderTest.run(null, controllerName, language);

        // THEN
        assertEquals(1, raisedErrors.size());
    }

    @Test
    public void that_filename_is_properly_derived_from_URL() throws Exception {

        /* GIVEN Scenario:
         * 
         * - only the FXML name is known and its URL
         * - as no custom controller name is defined,
         *   it is expected that the controller name is 
         *   derived from the URL
         * 
         * - Test is repeated for JAVA and KOTLIN
         *    
         */
        String fxmlName = "MyCustomView.fxml";
        File fxml = temporaryDirectory.resolve(fxmlName).toFile();
        URL url = fxml.toURI().toURL();

        // WHEN
        classUnderTest = new SkeletonFileWriter(stageSupplier, textProperty, 
                                                rejectProposedFile(), onSuccess(), onError());

        // JAVA CASE
        SkeletonSettings.LANGUAGE language = SkeletonSettings.LANGUAGE.JAVA;
        classUnderTest.run(url, null, language);

        assertEquals("MyCustomViewController.java", filesProposed.get(0).getName());

        // KOTLIN CASE
        language = SkeletonSettings.LANGUAGE.KOTLIN;
        classUnderTest.run(url, null, language);

        assertEquals("MyCustomViewController.kt", filesProposed.get(1).getName());
    }

    @Test
    public void that_file_for_newSkeleton_is_not_saved_but_naming_and_directory_are_correct() {

        /* GIVEN Scenario:
         * 
         * - No file has been saved for either language.
         * - A file name proposal will be created and the
         *   file chooser will be configured accordingly.
         * - The user will cancel the file save dialog.
         *  
         */
        String controllerName = "CustomizedController";
        SkeletonSettings.LANGUAGE language = SkeletonSettings.LANGUAGE.JAVA;

        // WHEN
        classUnderTest = new SkeletonFileWriter(stageSupplier, textProperty, 
                                                rejectProposedFile(), onSuccess(), onError());

        classUnderTest.run(null, controllerName, language);

        // THEN
        File expectedProposedFile = new File(System.getProperty("user.home"), "CustomizedController.java");

        assertEquals(expectedProposedFile, filesProposed.get(0));
        assertTrue(classUnderTest.getLastSavedFilesPerLanguage().isEmpty());
        assertEquals("Java", fileExtensionFilters.get(0).getDescription());
        assertEquals("*.java", fileExtensionFilters.get(0).getExtensions().get(0));
    }

    @Test
    public void that_java_and_kotlin_versions_of_skeleton_are_saved_and_remembered() throws Exception {

        /*
         * GIVEN Scenario:
         *  
         * - the same SkeletonFileWriter instance is called multiple times but
         *   with different language settings
         */
        String controllerName = "MyVeryNewController";
        SkeletonSettings.LANGUAGE language = null;

        classUnderTest = new SkeletonFileWriter(stageSupplier, textProperty, 
                                                acceptProposedFile(), onSuccess(), onError());

        /*
         * - Language is KOTLIN
         * - No file has been saved before.
         * - The controller skeleton contents is supposed to be written into a *.kt file
         *   inside the given temporary directory.
         * - The file name is to be derived from the controller name
         * 
         */
        language = SkeletonSettings.LANGUAGE.KOTLIN;
        textProperty.setValue("SomeKotlinCode");
        classUnderTest.run(null, controllerName, language);

        // THEN
        File expectedFile = temporaryDirectory.resolve("MyVeryNewController.kt").toFile();

        assertTrue(expectedFile.exists());
        assertEquals(expectedFile, filesProposed.get(0));
        assertEquals("SomeKotlinCode", Files.readString(expectedFile.toPath()));
        assertEquals(1, classUnderTest.getLastSavedFilesPerLanguage().size());
        assertEquals(expectedFile, classUnderTest.getLastSavedFilesPerLanguage().get(language));
        assertEquals("Kotlin", fileExtensionFilters.get(0).getDescription());
        assertEquals("*.kt", fileExtensionFilters.get(0).getExtensions().get(0));

        /*
         * - For the same setup, a new controller skeleton shall be saved but for JAVA
         * - As no JAVA file has been saved previously, the name shall be derived from
         *   controller name.
         * - The only expected difference here is the file name extension and the 
         *   file chooser configuration.
         * 
         */
        language = SkeletonSettings.LANGUAGE.JAVA;
        textProperty.setValue("MyJavaCode");
        classUnderTest.run(null, controllerName, language);

        // THEN
        expectedFile = temporaryDirectory.resolve("MyVeryNewController.java").toFile();

        assertTrue(expectedFile.exists());
        assertEquals(expectedFile, filesProposed.get(1));
        assertEquals("MyJavaCode", Files.readString(expectedFile.toPath()));
        assertEquals(2, classUnderTest.getLastSavedFilesPerLanguage().size());
        assertEquals(expectedFile, classUnderTest.getLastSavedFilesPerLanguage().get(language));
        assertEquals("Java", fileExtensionFilters.get(1).getDescription());
        assertEquals("*.java", fileExtensionFilters.get(1).getExtensions().get(0));

        /*
         * Now, for JAVA and KOTLIN a file has been saved.
         * After switching back to Kotlin, the expectation here is, that the 
         * previously used and saved Kotlin file name is used again.
         */

        language = SkeletonSettings.LANGUAGE.KOTLIN;
        textProperty.setValue("MyKotlinCode");
        classUnderTest.run(null, controllerName, language);

        // THEN:
        expectedFile = temporaryDirectory.resolve("MyVeryNewController.kt").toFile();

        assertTrue(expectedFile.exists());
        assertEquals(expectedFile, filesProposed.get(0));
        assertEquals("MyKotlinCode", Files.readString(expectedFile.toPath()));
        assertEquals(2, classUnderTest.getLastSavedFilesPerLanguage().size());
        assertEquals(expectedFile, classUnderTest.getLastSavedFilesPerLanguage().get(language));
        assertEquals("Kotlin", fileExtensionFilters.get(2).getDescription());
        assertEquals("*.kt", fileExtensionFilters.get(2).getExtensions().get(0));
    }

    /**
     * Stores the proposed file name and the selected extension filter so that
     * assertion can be created to verify the values.
     * 
     * @return Simulates a user interaction, where the user cancels the file save
     *         dialog.
     */
    private BiFunction<FileChooser, Stage, File> rejectProposedFile() {
        BiFunction<FileChooser, Stage, File> saveDialogIsCancelled = (fc, stage) -> {
            // save the proposed file name
            filesProposed.add(new File(fc.getInitialDirectory(), fc.getInitialFileName()));

            // remember the extension filer which was selected
            if (fc.getSelectedExtensionFilter() != null) {
                fileExtensionFilters.add(fc.getSelectedExtensionFilter());
            }

            // user cancels file save dialog
            return null;
        };
        return saveDialogIsCancelled;
    }

    /**
     * Stores the proposed file name and the selected extension filter so that
     * assertion can be created to verify the values.
     * 
     * @return Simulates a user interaction, where the user accepts the file save
     *         dialog with its defaults.
     */
    private BiFunction<FileChooser, Stage, File> acceptProposedFile() {
        BiFunction<FileChooser, Stage, File> acceptingFileName = (fc, stage) -> {
            // save the proposed file name
            File proposedFile = temporaryDirectory.resolve(fc.getInitialFileName()).toFile();
            filesProposed.add(proposedFile);

            // remember the extension filer which was selected
            if (fc.getSelectedExtensionFilter() != null) {
                fileExtensionFilters.add(fc.getSelectedExtensionFilter());
            }

            // user accepts proposed file name and saves to temporary directory
            return proposedFile;
        };
        return acceptingFileName;
    }

    private Function<Supplier<Stage>, Consumer<File>> onSuccess() {
        return stageSupplier -> file -> {
            /* no handling of successful writing yet */
        };
    }

    private Function<Supplier<Stage>, BiConsumer<File, Exception>> onError() {
        return stageSupplier -> (file, error) -> {
            /* no handling of error yet */
        };
    }
}
