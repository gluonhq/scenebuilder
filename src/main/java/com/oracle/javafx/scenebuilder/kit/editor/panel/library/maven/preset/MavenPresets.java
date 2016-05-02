package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.preset;

import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.Repository;
import java.util.Arrays;
import java.util.List;

public class MavenPresets {
    
    private static final List<String> ARTIFACTS = Arrays.asList(
            "org.controlsfx:controlsfx",
            "org.jfxtras:jfxtras-all",
            "eu.hansolo:Medusa",
            "de.jensd:fontawesomefx",
            "com.gluonhq:charm",
            "com.gluonhq:particle");
    
    public static List<String> getPresetArtifacts() { 
        return ARTIFACTS;
    }
    
    private static final List<Repository> REPOSITORIES = Arrays.asList(
            new Repository("Maven Central", "default", "https://repo1.maven.org/maven2/"),
            new Repository("Jcenter", "default", "https://jcenter.bintray.com"),
            new Repository("Sonatype (snapshots)", "default", "https://oss.sonatype.org/content/repositories/snapshots"),
            new Repository("Sonatype (releases)", "default", "https://oss.sonatype.org/content/repositories/releases"),
            new Repository("Gluon Nexus", "default", "http://nexus.gluonhq.com/nexus/content/repositories/releases"));
    
    public static List<Repository> getPresetRepositories() {
        return REPOSITORIES;
    }
    
}
