package com.oracle.javafx.scenebuilder.app.preferences;

import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositoryPreferences {
    
    private final Map<String, PreferencesRecordRepository> recordRepositories;

    public RepositoryPreferences() {
        this.recordRepositories = new HashMap<>();
    }
    
    public PreferencesRecordRepository getRecordRepository(String id) {
        return recordRepositories.get(id);
    }
    
    public void addRecordRepository(String key, PreferencesRecordRepository object) {
        recordRepositories.put(key, object);
    }
    
    public void removeRecordRepository(String id) {
        recordRepositories.remove(id);
    }
    
    public List<Repository> getRepositories() {
        return recordRepositories.values()
                .stream()
                .map(p -> p.getRepository())
                .collect(Collectors.toList());
    }
    
}
