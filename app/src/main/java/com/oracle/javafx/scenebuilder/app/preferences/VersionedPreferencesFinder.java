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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.oracle.javafx.scenebuilder.app.util.AppSettings;

/**
 * A utility which searches for version specific {@link Preferences} nodes belonging to Scene Builder.
 * 
 */
final class VersionedPreferencesFinder {
    
    private final Preferences applicationPreferences;
    
    private final String nodePrefix;
    
    public VersionedPreferencesFinder(String nodePrefix, Preferences rootNode) {
        this.applicationPreferences = Objects.requireNonNull(rootNode);
        this.nodePrefix = Objects.requireNonNull(nodePrefix);
    }
    
    /**
     * Collects the versions where application settings for Scene Builder exists into a list, starting with the most recent version.
     * In case of errors while accessing the Preferences store, an empty list is returned.
     * @return List with {@link AppVersion} starting with most recent version as first element.
     */
    public List<AppVersion> getDetectedVersions() {
        try {
            String[] children = applicationPreferences.childrenNames();
            return Arrays.stream(children)
                         .map(this::removePrefixFromVersionedNode)
                         .map(AppVersion::fromString)
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .sorted(AppVersion.descending())
                         .toList();
        } catch (BackingStoreException ex) {
            Logger.getLogger(PreferencesController.class.getName()).log(Level.SEVERE,
                    "Failed to detect possibly existing settings of other versions!", ex);
        }
        return Collections.emptyList();
    }
    
    private String removePrefixFromVersionedNode(String nodeName) {
        if (nodeName.startsWith(nodePrefix)) {
            return nodeName.substring(nodePrefix.length());
        }
        return nodeName;
    }
    
    /**
     * Provides the list of all preferences nodes with a version assigned to.
     * Will not work with snapshot versions.
     * 
     * @return List of preferences nodes (as {@link VersionedPreferences}) belonging to an older version of Scene Builder 
     */
    public List<VersionedPreferences> getPreviousVersions() {
        var currentVersion = AppVersion.fromString(AppSettings.getSceneBuilderVersion());
        if (currentVersion.isPresent()) {
            var current = currentVersion.get();
            return getDetectedVersions().stream()
                                        .filter(other->other.compareTo(current) < 0)
                                        .sorted(AppVersion.descending())
                                        .map(this::buildNode)
                                        .toList();
        }
        return Collections.emptyList();
    }
    
    /**
     * If an older version of Scene Builder was used, there might be a preferences
     * node for this older version. If this node exists, the optional will carry a
     * {@link VersionedPreferences} providing the {@link AppVersion} and the
     * {@link Preferences} node.
     * 
     * @return Optional a {@link VersionedPreferences} in case an older version of Scene
     *         Builder was installed. When there are no settings, the optional will
     *         be empty.
     */
    public Optional<VersionedPreferences> previousVersionPrefs() {
        List<VersionedPreferences> previousVersions = getPreviousVersions();
        if (previousVersions.isEmpty()) {
            Logger.getLogger(VersionedPreferencesFinder.class.getName()).log(Level.FINE,
                    "No previous versions preferences found!");
            return Optional.empty();
        }
        return Optional.of(previousVersions.get(0));
    }
    
    private VersionedPreferences buildNode(AppVersion version) {
        String node = version.nodeNameWithPrefix(nodePrefix);
        return new VersionedPreferences(version, applicationPreferences.node(node));
    };
}
