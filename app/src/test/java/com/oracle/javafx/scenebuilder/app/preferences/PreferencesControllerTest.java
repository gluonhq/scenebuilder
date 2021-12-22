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
package com.oracle.javafx.scenebuilder.app.preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

import org.junit.BeforeClass;
import org.junit.Test;

import com.oracle.javafx.scenebuilder.app.util.AppSettings;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.Repository;

public class PreferencesControllerTest {
    
    private static Preferences testNode;
    
    private static PreferencesController classUnderTest;
    
    @BeforeClass
    public static void setup() {
        testNode = Preferences.userRoot()
                              .node("SBTEST")
                              .node("com/oracle/javafx/scenebuilder/app/preferences");
        PrefsHelper.removeAllChildNodes(testNode);
        classUnderTest = PreferencesController.getSingleton(testNode);
    }

    @Test
    public void that_preferences_are_stored_per_version() {
        String appVersion = AppSettings.getSceneBuilderVersion();
        String versionSpecificNode = "SB_" + appVersion;
        assertEquals(versionSpecificNode, PreferencesController.SB_RELEASE_NODE);
    }

    @Test
    public void that_prefs_root_node_is_version_specific() {
        String prefsNodeUsed = classUnderTest.getEffectiveUsedRootNode();
        String appVersion = AppSettings.getSceneBuilderVersion();
        String versionSpecificNode = "SB_" + appVersion;
        String expectedPrefsNode = "/SBTEST/com/oracle/javafx/scenebuilder/app/preferences/" + versionSpecificNode;
        assertEquals(expectedPrefsNode, prefsNodeUsed);
    }
    
    @Test
    public void that_most_recent_previous_version_is_detected() {
        PrefsHelper.removeAllNonReleaseNodes(testNode);
        testNode.node("SB_8.5").put("testkey", "testvalue");
        testNode.node("SB_2.0").put("testkey", "testvalue");
        Optional<VersionedPreferences> mostRecentPreviousVersion = classUnderTest.getPreviousVersionSettings();
        assertTrue(mostRecentPreviousVersion.isPresent());
        
        assertEquals(new AppVersion(8, 5), mostRecentPreviousVersion.get().version());
        assertEquals("SB_8.5", mostRecentPreviousVersion.get().node().name());
    }
    
    @Test
    public void that_no_version_is_detected_in_case_that_only_newer_versions_exist() {
        PrefsHelper.removeAllNonReleaseNodes(testNode);
        testNode.node("SB_199.199.199").put("testkey", "testvalue");
        Optional<VersionedPreferences> mostRecentPreviousVersion = classUnderTest.getPreviousVersionSettings();
        assertTrue(mostRecentPreviousVersion.isEmpty());
    }
    
    @Test
    public void that_previous_version_settings_can_be_imported() {
        PrefsHelper.removeAllNonReleaseNodes(testNode);

        // GIVEN
        
        testNode.node("SB_8.9.9").put("RECENT_ITEMS", "/folder/file.fxml");
        
        Preferences mavenLib = testNode.node("SB_8.9.9").node("ARTIFACTS").node("org.name:library:0.0.1");
        mavenLib.put("path",         "/location/of/file.jar");
        mavenLib.put("groupID",      "org.name");
        mavenLib.put("filter",       "");
        mavenLib.put("dependencies", "");
        mavenLib.put("artifactId",   "library");
        mavenLib.put("version",      "0.0.1");
        
        Preferences repository = testNode.node("SB_8.9.9").node("REPOSITORIES").node("custom-repository");
        repository.put("ID",        "custom-repository");
        repository.put("URL",       "http://localhost/myrepo");
        repository.put("type",      "default");
        repository.put("User",      "username");
        repository.put("Password",  "password");
        
        
        // WHEN
        PreferencesImporter importer = classUnderTest.getImporter();
        importer.tryImportingPreviousVersionSettings();
        
        // THEN
        String appVersion = AppSettings.getSceneBuilderVersion();
        String versionSpecificNode = "SB_" + appVersion;
        Preferences targetNode = testNode.node(versionSpecificNode);
        
        // User Decision is memorized
        assertNotNull(targetNode.get(PreferencesImporter.ASKED_FOR_IMPORT, null));
        
        // Maven Repositories are initialized properly
        List<String> artifacts = classUnderTest.getMavenPreferences().getArtifactsCoordinates();
        assertFalse(artifacts.isEmpty());
        assertTrue(artifacts.contains("org.name:library:0.0.1"));
        
        // User Repositories are properly initialized
        List<Repository> repositories = classUnderTest.getRepositoryPreferences().getRepositories();
        assertFalse(repositories.isEmpty());
        assertEquals("http://localhost/myrepo", repositories.get(0).getURL());
        
        // Recent items 
        List<String> recentItems = classUnderTest.getRecordGlobal().getRecentItems();
        assertFalse(recentItems.isEmpty());
        assertEquals("/folder/file.fxml", recentItems.get(0));
    }
}
