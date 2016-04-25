package com.oracle.javafx.scenebuilder.kit.editor.panel.library;

/**
 * List cell item in the JAR/FXML Library dialog.
 */
public class PresetDialogListItem extends ArtifactDialogListItem {

    private final String[] coordinates;
    
    public PresetDialogListItem(LibraryDialogController libraryDialogController, String coordinates) {
        super(libraryDialogController, coordinates);
        this.coordinates = coordinates.split(":");
    }

    public String getGroupId() {
        return coordinates[0];
    }
    
    public String getArtifactId() {
        return coordinates[1];
    }
}