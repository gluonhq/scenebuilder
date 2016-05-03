package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import java.util.List;
import java.util.Map;
import org.eclipse.aether.artifact.DefaultArtifact;

public interface Search {
    
    Map<String, List<DefaultArtifact>> getCoordinates(String query);
    
}
