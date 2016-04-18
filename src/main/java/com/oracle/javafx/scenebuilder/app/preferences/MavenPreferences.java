package com.oracle.javafx.scenebuilder.app.preferences;

import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MavenPreferences {
    
    private final Map<String, PreferencesRecordArtifact> recordArtifacts;

    public MavenPreferences() {
        this.recordArtifacts = new HashMap<>();
    }
    
    public PreferencesRecordArtifact getRecordArtifact(String coordinates) {
        return recordArtifacts.get(coordinates);
    }
    
    public void addRecordArtifact(String key, PreferencesRecordArtifact object) {
        recordArtifacts.put(key, object);
    }
    
    public void removeRecordArtifact(String coordinates) {
        recordArtifacts.remove(coordinates);
    }
    
    /*
     * Single Artifact
     */
    
    private String getArtifactJarPath(MavenArtifact artifact) {
        return recordArtifacts.get(artifact.getCoordinates()).getPath();
    }
    
    private List<String> getArtifactJarDependencies(MavenArtifact artifact) {
        String dep = recordArtifacts.get(artifact.getCoordinates()).getMavenArtifact().getDependencies();
        if (dep != null && !dep.isEmpty()) {
                return Stream.of(dep.split(":")).collect(Collectors.toList());
        } 
        return new ArrayList<>();
    }
    
    public File getArtifactFile(MavenArtifact artifact) {
        String path = getArtifactJarPath(artifact);
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
    
    public List<File> getArtifactFileWithDependencies(MavenArtifact artifact) {
        String path = getArtifactJarPath(artifact);
        if (path != null && !path.isEmpty()) {
        
            List<File> jarPaths = new ArrayList<>();
            jarPaths.add(getArtifactFile(artifact));
            
            jarPaths.addAll(getArtifactJarDependencies(artifact)
                    .stream()
                    .filter(d -> d != null && !d.isEmpty())
                    .map(File::new)
                    .filter(File::exists)
                    .collect(Collectors.toList()));
            return jarPaths;
        }
        return null;
    }
    
    public List<String> getArtifactFilter(MavenArtifact artifact) {
        String filter = recordArtifacts.get(artifact.getCoordinates()).getMavenArtifact().getFilter();
        if (filter != null && !filter.isEmpty()) {
                return Stream.of(filter.split(":")).collect(Collectors.toList());
        } 
        return new ArrayList<>();
    }
    
    /*
     * All Artifacts 
     */
    
    private List<String> getArtifactsJarsPaths() {
        return recordArtifacts.values()
                .stream()
                .map(PreferencesRecordArtifact::getPath)
                .distinct()
                .collect(Collectors.toList());
    }
    
    private List<String> getArtifactsJarsDependencies() {
        return recordArtifacts.values()
                .stream()
                .map(p -> p.getMavenArtifact().getDependencies())
                .filter(d -> d != null && !d.isEmpty())
                .flatMap(d -> Stream.of(d.split(":")))
                .distinct()
                .collect(Collectors.toList());
    }
    
    public List<File> getArtifactsFiles() {
        return getArtifactsJarsPaths().stream()
                .filter(s -> s != null && !s.isEmpty())
                .map(File::new)
                .filter(File::exists)
                .collect(Collectors.toList());
    }
    
    public List<File> getArtifactsFilesWithDependencies() {
        List<String> jarsPaths = getArtifactsJarsPaths();
        jarsPaths.addAll(getArtifactsJarsDependencies());
        return jarsPaths.stream()
                .filter(s -> s != null && !s.isEmpty())
                .map(File::new)
                .filter(File::exists)
                .collect(Collectors.toList());
    }
    
    public List<Path> getArtifactsPaths() {
        return getArtifactsFiles()
                .stream()
                .map(File::toPath)
                .collect(Collectors.toList());
    }
    public List<Path> getArtifactsPathsWithDependencies() {
        return getArtifactsFilesWithDependencies()
                .stream()
                .map(File::toPath)
                .collect(Collectors.toList());
    }
    
    public List<String> getArtifactsFilter() {
        return recordArtifacts.values()
                .stream()
                .map(p -> p.getMavenArtifact().getFilter())
                .filter(f -> f != null && !f.isEmpty())
                .flatMap(f -> Stream.of(f.split(":")))
                .distinct()
                .collect(Collectors.toList());
    }
    
    public List<String> getArtifactsCoordinates() {
        return recordArtifacts.entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
}
