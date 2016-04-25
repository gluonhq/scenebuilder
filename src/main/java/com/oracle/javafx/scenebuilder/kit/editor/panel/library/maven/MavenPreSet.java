package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven;

import java.util.Arrays;
import java.util.List;

public class MavenPreSet {
    
    private static final List<String> PRESET = Arrays.asList(
            "org.controlsfx:controlsfx",
            "org.jfxtras:jfxtras-all",
            "eu.hansolo:Medusa",
            "de.jensd:fontawesomefx",
            "com.gluonhq:charm",
            "com.gluonhq:particle");
    
    public static List<String> getPresetArtifacts() { 
        return PRESET;
    }
    
}
