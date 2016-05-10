package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.preset;

import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.Repository;
import java.util.Arrays;
import java.util.List;

public class MavenPresets {
    
    public static final String MAVEN = "Maven Central";
    public static final String JCENTER = "Jcenter";
    public static final String SONATYPE = "Sonatype";
    public static final String GLUON_NEXUS = "Gluon Nexus";
    public static final String LOCAL = "Local";
    
    private static final List<Repository> REPOSITORIES = Arrays.asList(
            new Repository(MAVEN, "default", "https://repo1.maven.org/maven2/"),
            new Repository(JCENTER, "default", "https://jcenter.bintray.com"),
            new Repository(SONATYPE + " (snapshots)", "default", "https://oss.sonatype.org/content/repositories/snapshots"),
            new Repository(SONATYPE + " (releases)", "default", "https://oss.sonatype.org/content/repositories/releases"),
            new Repository(GLUON_NEXUS + " (releases)", "default", "http://nexus.gluonhq.com/nexus/content/repositories/releases"));
    
    public static List<Repository> getPresetRepositories() {
        return REPOSITORIES;
    }
    
}
