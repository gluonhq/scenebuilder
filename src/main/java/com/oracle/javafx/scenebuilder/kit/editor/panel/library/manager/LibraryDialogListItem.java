package com.oracle.javafx.scenebuilder.kit.editor.panel.library.manager;

import java.nio.file.Path;

/**
 * List cell item in the JAR/FXML Library dialog.
 */
public class LibraryDialogListItem implements DialogListItem {

    private final LibraryDialogController libraryDialogController;
    private final Path filePath;

    public LibraryDialogListItem(LibraryDialogController libraryDialogController, Path filePath) {
        this.libraryDialogController = libraryDialogController;
        this.filePath = filePath;
    }

    @Override
    public LibraryDialogController getLibraryDialogController() {
        return libraryDialogController;
    }

    public Path getFilePath() {
        return filePath;
    }
}