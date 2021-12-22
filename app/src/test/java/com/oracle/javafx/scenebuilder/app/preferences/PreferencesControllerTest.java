/*
 * Copyright (c) 2021, Gluon and/or its affiliates.
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

import org.junit.Test;

import com.oracle.javafx.scenebuilder.app.util.AppSettings;

public class PreferencesControllerTest {
    
    @Test
    public void that_preferences_are_stored_per_version() {
        String appVersion = AppSettings.getSceneBuilderVersion();
        String versionSpecificNode = "SB_"+appVersion;
        assertEquals(versionSpecificNode, PreferencesController.SB_RELEASE_NODE);
    }

    @Test
    public void that_prefs_root_node_is_version_specific() {
        String prefsNodeUsed = PreferencesController.getSingleton()
                                                    .getEffectiveUsedRootNode();
        String appVersion = AppSettings.getSceneBuilderVersion();
        String versionSpecificNode = "SB_"+appVersion;
        String expectedPrefsNode = "/com/oracle/javafx/scenebuilder/app/preferences/"+versionSpecificNode;
        assertEquals(expectedPrefsNode, prefsNodeUsed);
    }
}
