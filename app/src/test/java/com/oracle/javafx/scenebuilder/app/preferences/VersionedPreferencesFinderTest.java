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
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class VersionedPreferencesFinderTest {

    private static Preferences testNode;
    
    private static VersionedPreferencesFinder classUnderTest;
    
    @BeforeAll
    public static void setup() {
        testNode = Preferences.userRoot()
                              .node("SBTEST")
                              .node("com/oracle/javafx/scenebuilder/app/preferences");

        PrefsHelper.removeAllChildNodes(testNode);
        classUnderTest = new VersionedPreferencesFinder("SB_", testNode);
    }
    
    @Test
    public void that_other_existing_version_preferences_are_detected() {
        PrefsHelper.removeAllNonReleaseNodes(testNode);
        testNode.node("SB_2.0").put("testkey", "testvalue");
        testNode.node("SB_8.5").put("testkey", "testvalue");
        testNode.node("SB_16.1.2").put("testkey", "testvalue");
        List<AppVersion> detectedVersions = classUnderTest.getDetectedVersions();
        assertTrue(detectedVersions.size() >= 3);
    }
    
    @Test
    public void that_an_empty_list_provided_when_no_other_prefs_versions_exist() throws Exception {
        PrefsHelper.removeAllChildNodes(testNode);
        List<AppVersion> detectedVersions = classUnderTest.getDetectedVersions();
        assertTrue(detectedVersions.isEmpty());
    }
    
    @Test
    public void that_previous_versions_are_properly_detected() {
        PrefsHelper.removeAllNonReleaseNodes(testNode);
        testNode.node("SB_8.5").put("testkey", "testvalue");
        testNode.node("SB_2.0").put("testkey", "testvalue");
        List<VersionedPreferences> previousVersionPrefs = classUnderTest.getPreviousVersions();
        List<AppVersion> previousVersions = previousVersionPrefs.stream()
                                                                .map(VersionedPreferences::version)
                                                                .toList();
        assertEquals(List.of(new AppVersion(8,5),new AppVersion(2,0)), previousVersions);
    }
    
    @Test
    public void that_most_recent_previous_version_is_detected() {
        PrefsHelper.removeAllNonReleaseNodes(testNode);
        testNode.node("SB_8.5").put("testkey", "testvalue");
        testNode.node("SB_2.0").put("testkey", "testvalue");
        Optional<VersionedPreferences> mostRecentPreviousVersion = classUnderTest.previousVersionPrefs();
        assertTrue(mostRecentPreviousVersion.isPresent());
        assertEquals(new AppVersion(8, 5), mostRecentPreviousVersion.get().version());
        assertEquals("SB_8.5", mostRecentPreviousVersion.get().node().name());
    }
    
    @Test
    public void that_no_version_is_detected_in_case_that_only_newer_versions_exist() {
        PrefsHelper.removeAllNonReleaseNodes(testNode);
        testNode.node("SB_199.199.199").put("testkey", "testvalue");
        Optional<VersionedPreferences> mostRecentPreviousVersion = classUnderTest.previousVersionPrefs();
        assertTrue(mostRecentPreviousVersion.isEmpty());
    }
}
