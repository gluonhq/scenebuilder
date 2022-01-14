/*
 * Copyright (c) 2016, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.preferences;

import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Defines artifacts preferences global to the application.
 */
public class PreferencesRecordArtifact {
    
    private final Preferences artifactsRootPreferences;
    private Preferences artifactPreferences;
    private final static String GROUPID  = "groupID";
    private final static String ARTIFACTID  = "artifactId";
    private final static String VERSION  = "version";
    public final static String DEPENDENCIES = "dependencies";
    public final static String FILTER = "filter";
    public final static String PATH = "path";
    
    private String groupId;
    private String artifactId;
    private String version;
    private final MavenArtifact mavenArtifact;
    
    public PreferencesRecordArtifact(Preferences artifactsRootPreferences, MavenArtifact mavenArtifact) {
        this.artifactsRootPreferences = artifactsRootPreferences;
        this.mavenArtifact = mavenArtifact;
    }
    
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return mavenArtifact.getPath();
    }

    public MavenArtifact getMavenArtifact() {
        return mavenArtifact;
    }
    /**
     * Read data from the java preferences DB and initialize properties.
     */
    public void readFromJavaPreferences() {

        assert artifactPreferences == null;
        
        // Check if there are some preferences for this artifact
        try {
            final String[] childrenNames = artifactsRootPreferences.childrenNames();
            for (String child : childrenNames) {
                if (child.equals(mavenArtifact.getCoordinates())) {
                    artifactPreferences = artifactsRootPreferences.node(child);
                }
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(PreferencesRecordArtifact.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        if (artifactPreferences == null) {
            return;
        }
        
        setGroupId(artifactPreferences.get(GROUPID, null));
        setArtifactId(artifactPreferences.get(ARTIFACTID,null));
        setVersion(artifactPreferences.get(VERSION, null));
        mavenArtifact.setDependencies(artifactPreferences.get(DEPENDENCIES, null));
        mavenArtifact.setFilter(artifactPreferences.get(FILTER, null));
        mavenArtifact.setPath(artifactPreferences.get(PATH, null));
    }
    
    /**
     * Write the properties data to the java preferences DB.
     */
    public void writeToJavaPreferences() {
        assert artifactsRootPreferences != null;
        assert mavenArtifact.getCoordinates() != null;
        
        if (artifactPreferences == null) {
            try {
                assert artifactsRootPreferences.nodeExists(mavenArtifact.getCoordinates()) == false;
                // Create a new document preference node under the document root node
                artifactPreferences = artifactsRootPreferences.node(mavenArtifact.getCoordinates());
            } catch(BackingStoreException ex) {
                Logger.getLogger(PreferencesRecordArtifact.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        assert artifactPreferences != null;
            
        String[] items = mavenArtifact.getCoordinates().split(":");
        artifactPreferences.put(GROUPID, items[0]);
        artifactPreferences.put(ARTIFACTID, items[1]);
        artifactPreferences.put(VERSION, items[2]);
        artifactPreferences.put(DEPENDENCIES, mavenArtifact.getDependencies());
        artifactPreferences.put(FILTER, mavenArtifact.getFilter());
        artifactPreferences.put(PATH, mavenArtifact.getPath());
    }
}
