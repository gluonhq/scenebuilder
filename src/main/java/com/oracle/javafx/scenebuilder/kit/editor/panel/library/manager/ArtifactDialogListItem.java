package com.oracle.javafx.scenebuilder.kit.editor.panel.library.manager;

/**
 * List cell item in the JAR/FXML Library dialog.
 */
public class ArtifactDialogListItem implements DialogListItem {

    private final LibraryDialogController libraryDialogController;
    private final String coordinates;

    public ArtifactDialogListItem(LibraryDialogController libraryDialogController, String coordinates) {
        this.libraryDialogController = libraryDialogController;
        this.coordinates = coordinates;
    }

    @Override
    public LibraryDialogController getLibraryDialogController() {
        return libraryDialogController;
    }

    public String getCoordinates() {
        return coordinates;
    }
}