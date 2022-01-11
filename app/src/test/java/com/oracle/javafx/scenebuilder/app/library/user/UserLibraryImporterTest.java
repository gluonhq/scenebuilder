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
package com.oracle.javafx.scenebuilder.app.library.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.oracle.javafx.scenebuilder.app.ApplicationDirectories;
import com.oracle.javafx.scenebuilder.app.PlatformSpecificDirectories;
import com.oracle.javafx.scenebuilder.app.OperatingSystem;
import com.oracle.javafx.scenebuilder.app.preferences.AppVersion;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesImporter;
import com.oracle.javafx.scenebuilder.app.preferences.PrefsHelper;

public class UserLibraryImporterTest {

    private static Preferences testNode;
    private UserLibraryImporter classUnderTest;
    private ApplicationDirectories appDirectories;
    
    @BeforeClass
    public static void setup() {
        testNode = Preferences.userRoot()
                              .node("USR_LIB_IMPORTER_TEST")
                              .node("com/oracle/javafx/scenebuilder/app/preferences");
        PrefsHelper.removeAllChildNodes(testNode);
    }
    
       @AfterClass
    public static void cleanup() throws Exception {
        Preferences.userRoot().node("USR_LIB_IMPORTER_TEST").removeNode();
    }
    
    @Test
    public void that_exception_is_thrown_when_null_argument_is_used() {
        appDirectories = forLinux("17.0.0-SNAPSHOT");
        classUnderTest = new UserLibraryImporter(AppVersion.fromString("17.0.0-SNAPSHOT"), appDirectories, testNode);
        assertThrows(NullPointerException.class,
                ()->classUnderTest.previousVersionUserLibraryPath(null));
    }
    @Test
    public void that_empty_optional_is_provided_when_current_version_has_unsupported_format() {
        appDirectories = forLinux("17.0.0-SNAPSHOT");
        classUnderTest = new UserLibraryImporter(AppVersion.fromString("17.0.0-SNAPSHOT"), appDirectories, testNode);
        List<Path> candidates = List.of(Paths.get("Scene Builder"),
                                        Paths.get("Scene Builder-19.0.1")); 
        assertTrue(classUnderTest.previousVersionUserLibraryPath(candidates).isEmpty());
    }
    
    @Test
    public void that_empty_optional_is_provided_when_old_library_path_not_exists() {
        appDirectories = forLinux("17.0.0-SNAPSHOT");
        classUnderTest = new UserLibraryImporter(AppVersion.fromString("17.0.0"), appDirectories, testNode);
        List<Path> candidates = List.of(Paths.get("Scene Builder"),
                                        Paths.get("Scene Builder-19.0.1")); 
        assertTrue(classUnderTest.previousVersionUserLibraryPath(candidates).isEmpty());
    }
    @Test
    public void that_user_library_paths_are_detected_for_LINUX() {
        appDirectories = forLinux("17.0.0-SNAPSHOT");
        classUnderTest = new UserLibraryImporter(AppVersion.fromString("17.0.0"), appDirectories, testNode);
        List<Path> candidates = List.of(
                Paths.get(".scenebuilder-15.0.0"),
                Paths.get("Scene Builder"),
                Paths.get(".scenebuilder-7.0-"),
                Paths.get(".scenebuilder-8.5.0"),
                Paths.get(".scenebuilder-8.5.0-SNAPSHOT"),
                Paths.get("Scene Builder-19.0.1")); 
        
        Optional<Path> x = classUnderTest.previousVersionUserLibraryPath(candidates);
        
        assertFalse(x.isEmpty());
        assertEquals(Paths.get(".scenebuilder-15.0.0"), x.get());
    }
    
    @Test
    public void that_legacy_library_path_is_detected_for_LINUX() {
        appDirectories = forLinux("17.0.0-SNAPSHOT");
        classUnderTest = new UserLibraryImporter(AppVersion.fromString("17.0.0"), appDirectories, testNode);
        List<Path> candidates = List.of(
                Paths.get("Scene Builder"),
                Paths.get("Scene Builder-19.0.1"),
                Paths.get(".scenebuilder")); 
        
        Optional<Path> x = classUnderTest.previousVersionUserLibraryPath(candidates);
        
        assertFalse(x.isEmpty());
        assertEquals(Paths.get(".scenebuilder"), x.get());
    }
    
    @Test
    public void that_user_library_paths_are_detected_for_WINDOWS() {
        appDirectories = forWindows("17.0.0-SNAPSHOT");
        classUnderTest = new UserLibraryImporter(AppVersion.fromString("17.0.0"), appDirectories, testNode);
        List<Path> candidates = List.of(
                Paths.get("Scene Builder-17.0.0"),
                Paths.get("Scene Builder"),
                Paths.get("Scene Builder-8.5.0"),
                Paths.get("Scene Builder-19.0.1"),
                Paths.get(".scenebuilder")); 
        
        Optional<Path> x = classUnderTest.previousVersionUserLibraryPath(candidates);
        
        assertFalse(x.isEmpty());
        assertEquals(Paths.get("Scene Builder-8.5.0"), x.get());
    }
    
    @Test
    public void that_legacy_library_path_is_detected_for_WINDOWS() {
        appDirectories = forWindows("17.0.0-SNAPSHOT");
        classUnderTest = new UserLibraryImporter(AppVersion.fromString("17.0.0"), appDirectories, testNode);
        List<Path> candidates = List.of(
                Paths.get("Scene Builder"),
                Paths.get("Scene Builder-19.0.1"),
                Paths.get(".scenebuilder")); 
        
        Optional<Path> x = classUnderTest.previousVersionUserLibraryPath(candidates);
        
        assertFalse(x.isEmpty());
        assertEquals(Paths.get("Scene Builder"), x.get());
    }
    
    @Test
    public void that_user_library_paths_are_detected_for_MACOS() {
        appDirectories = forMacOS("17.0.0-SNAPSHOT");
        classUnderTest = new UserLibraryImporter(AppVersion.fromString("17.0.0"), appDirectories, testNode);
        List<Path> candidates = List.of(
                Paths.get("Scene Builder-17.0.0"),
                Paths.get("Scene Builder"),
                Paths.get("Scene Builder-8.5.0"),
                Paths.get("Scene Builder-19.0.1"),
                Paths.get(".scenebuilder")); 
        
        Optional<Path> x = classUnderTest.previousVersionUserLibraryPath(candidates);
        
        assertFalse(x.isEmpty());
        assertEquals(Paths.get("Scene Builder-8.5.0"), x.get());
    }
    
    @Test
    public void that_legacy_library_path_is_detected_for_MACOS() {
        appDirectories = forMacOS("17.0.0-SNAPSHOT");
        classUnderTest = new UserLibraryImporter(AppVersion.fromString("17.0.0"), appDirectories, testNode);
        List<Path> candidates = List.of(
                Paths.get("Scene Builder"),
                Paths.get("Scene Builder-19.0.1"),
                Paths.get(".scenebuilder")); 
        
        Optional<Path> x = classUnderTest.previousVersionUserLibraryPath(candidates);
        
        assertFalse(x.isEmpty());
        assertEquals(Paths.get("Scene Builder"), x.get());
    }
    
    @Test
    public void that_import_is_skipped_when_user_opted_out() {
        LocalDateTime timestamp = LocalDateTime.of(2022, 1, 5, 20, 14);
        testNode.put(PreferencesImporter.PREF_ASKED_FOR_IMPORT,
                     timestamp.toString()+PreferencesImporter.DECISION_NO_IMPORT);
        
        String version = "17.0.0";
        OperatingSystem os = OperatingSystem.LINUX;
        ApplicationDirectories appDirectories = new TestAppDirectories(os, version);
        classUnderTest = new UserLibraryImporter(AppVersion.fromString(version), appDirectories, testNode);
        classUnderTest.performImportWhenDesired(timestamp);
        
        String documentedResult = testNode.get(PreferencesImporter.PREF_ASKED_FOR_IMPORT, null);
        
        assertNotNull(documentedResult);
        assertEquals("2022-01-05T20:14-no-import", documentedResult);
        assertNull(new File(appDirectories.getUserLibraryFolder()).listFiles());
    }
    
    
    @Test
    public void that_import_is_skipped_when_done_before() {
        LocalDateTime timestamp = LocalDateTime.of(2022, 1, 5, 20, 14);
        testNode.put(PreferencesImporter.PREF_ASKED_FOR_IMPORT,timestamp.toString()+UserLibraryImporter.USER_LIBRARY_IMPORT_COMPLETE);
        
        String version = "17.0.0";
        OperatingSystem os = OperatingSystem.LINUX;
        ApplicationDirectories appDirectories = new TestAppDirectories(os, version);
        classUnderTest = new UserLibraryImporter(AppVersion.fromString(version), appDirectories, testNode);
        classUnderTest.performImportWhenDesired(timestamp);
        
        String documentedResult = testNode.get(PreferencesImporter.PREF_ASKED_FOR_IMPORT, null);
        
        assertNotNull(documentedResult);
        assertEquals("2022-01-05T20:14-userlib-import-done", documentedResult);
        assertNull(new File(appDirectories.getUserLibraryFolder()).listFiles());
    }
    
    @Test
    public void that_old_files_are_imported() throws Exception {
        LocalDateTime timestamp = LocalDateTime.of(2022, 1, 5, 20, 14);
        testNode.put(PreferencesImporter.PREF_ASKED_FOR_IMPORT,timestamp.toString());

        String version = "17.0.0";
        OperatingSystem os = OperatingSystem.LINUX;
        ApplicationDirectories appDirectories = new TestAppDirectories(os, version);
        
        // Prepare an old library
        String dataRoot = appDirectories.getApplicationDataRoot();
        Path oldLibrary = Paths.get(dataRoot).resolve(".scenebuilder").resolve("Library");
        Files.createDirectories(oldLibrary);
        Path myJar = oldLibrary.resolve("MyCustomArtifact.txt");
        Files.writeString(myJar, "NotaJarButSomeTestContent", StandardOpenOption.CREATE);
        
        Path userLib = Paths.get(appDirectories.getUserLibraryFolder());
        Files.createDirectories(userLib);
        
        classUnderTest = new UserLibraryImporter(AppVersion.fromString(version), appDirectories, testNode);
        classUnderTest.performImportWhenDesired(timestamp);
        
        String documentedResult = testNode.get(PreferencesImporter.PREF_ASKED_FOR_IMPORT, null);
        
        assertNotNull(documentedResult);
        assertEquals("2022-01-05T20:14-userlib-import-done", documentedResult);
        
        File[] importedFiles = new File(appDirectories.getUserLibraryFolder()).listFiles(); 
        assertEquals(1, importedFiles.length);
        assertEquals("MyCustomArtifact.txt", importedFiles[0].getName());
    }
    
    @Test
    public void that_correct_AppDirectories_are_used_by_default() {
       if (testNode.get(PreferencesImporter.PREF_ASKED_FOR_IMPORT, null) !=null) {           
           testNode.remove(PreferencesImporter.PREF_ASKED_FOR_IMPORT);
       }
       classUnderTest = new UserLibraryImporter(testNode);
       assertTrue(classUnderTest.getPlatformDirectories() instanceof PlatformSpecificDirectories);
    }
    
    private static ApplicationDirectories forLinux(String version) {
        return new PlatformSpecificDirectories(OperatingSystem.LINUX, version);
    }
    
    private static ApplicationDirectories forMacOS(String version) {
        return new PlatformSpecificDirectories(OperatingSystem.MACOS, version);
    }
    
    private static ApplicationDirectories forWindows(String version) {
        return new PlatformSpecificDirectories(OperatingSystem.WINDOWS, version);
    }

}
