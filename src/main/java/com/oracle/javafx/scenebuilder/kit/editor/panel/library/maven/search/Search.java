package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import java.util.List;
import org.eclipse.aether.artifact.DefaultArtifact;

public interface Search {
    
    public static final String MIN_VERSION = "[0,)";
    
    List<DefaultArtifact> getCoordinates(String query);
    
}
