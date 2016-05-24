package com.oracle.javafx.scenebuilder.app.preferences;

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
