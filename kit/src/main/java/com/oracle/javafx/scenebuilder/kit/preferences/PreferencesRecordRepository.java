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

import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.Repository;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Defines repository preferences global to the application.
 */
public class PreferencesRecordRepository {
    
    private final Preferences repositoriesRootPreferences;
    private Preferences repositoryPreferences;
    public final static String REPO_ID  = "ID";
    public final static String REPO_TYPE  = "type";
    public final static String REPO_URL  = "URL";
    public final static String REPO_USER = "User";
    public final static String REPO_PASS = "Password";   
    
    private final Repository repository;
    
    public PreferencesRecordRepository(Preferences artifactsRootPreferences, Repository repository) {
        this.repositoriesRootPreferences = artifactsRootPreferences;
        this.repository = repository;
    }
    
    public Repository getRepository() {
        return repository;
    }
    /**
     * Read data from the java preferences DB and initialize properties.
     */
    public void readFromJavaPreferences() {

        assert repositoryPreferences == null;
        
        // Check if there are some preferences for this artifact
        try {
            final String[] childrenNames = repositoriesRootPreferences.childrenNames();
            for (String child : childrenNames) {
                if (child.equals(repository.getId())) {
                    repositoryPreferences = repositoriesRootPreferences.node(child);
                }
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(PreferencesRecordRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        if (repositoryPreferences == null) {
            return;
        }
        
        repository.setId(repositoryPreferences.get(REPO_ID, null));
        repository.setType(repositoryPreferences.get(REPO_TYPE, null));
        repository.setURL(repositoryPreferences.get(REPO_URL, null));
        repository.setUser(repositoryPreferences.get(REPO_USER, null));
        repository.setPassword(repositoryPreferences.get(REPO_PASS, null));
    }
    
    /**
     * Write the properties data to the java preferences DB.
     */
    public void writeToJavaPreferences() {
        assert repositoriesRootPreferences != null;
        assert repository.getId() != null;
        
        if (repositoryPreferences == null) {
            try {
                assert repositoriesRootPreferences.nodeExists(repository.getId()) == false;
                // Create a new document preference node under the document root node
                repositoryPreferences = repositoriesRootPreferences.node(repository.getId());
            } catch(BackingStoreException ex) {
                Logger.getLogger(PreferencesRecordRepository.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        assert repositoryPreferences != null;
            
        repositoryPreferences.put(REPO_ID, repository.getId());
        repositoryPreferences.put(REPO_TYPE, repository.getType());
        repositoryPreferences.put(REPO_URL, repository.getURL());
        if (repository.getUser() != null) {
            repositoryPreferences.put(REPO_USER, repository.getUser());
        }
        if (repository.getPassword() != null) {
            repositoryPreferences.put(REPO_PASS, repository.getPassword());
        }
        
    }
}
