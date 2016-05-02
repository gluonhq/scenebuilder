package com.oracle.javafx.scenebuilder.kit.editor.panel.library.manager;

import com.oracle.javafx.scenebuilder.app.DocumentWindowController;
import com.oracle.javafx.scenebuilder.app.SceneBuilderApp;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordArtifact;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;
import com.oracle.javafx.scenebuilder.app.preferences.MavenPreferences;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.ImportWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenDialogController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.preset.MavenPresets;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.RepositoryManagerController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AbstractModalDialog;
import com.oracle.javafx.scenebuilder.kit.library.user.UserLibrary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.stage.Modality;
import javafx.util.StringConverter;

/**
 * Controller for the JAR/FXML Library dialog.
 */
public class LibraryDialogController extends AbstractFxmlWindowController {

    private enum TYPE {
        LATEST("library.dialog.install.artifact.latest"),
        MANUAL("library.dialog.install.artifact.manually"),
        FILE("library.dialog.install.jarFxml"),
        FOLDER("library.dialog.install.folder");
        
        String i18n;
        
        TYPE(String i18n) {
            this.i18n = i18n;
        }
        
        public String getI18n() {
            return i18n;
        }
    }
    
    @FXML
    private ListView<DialogListItem> libraryListView;
    
    @FXML
    private Button installButton;

    @FXML
    private Button manageButton;

    @FXML
    private ComboBox<TYPE> installCombo;

    private final DocumentWindowController documentWindowController;

    private final EditorController editorController;
    private final UserLibrary userLibrary;
    private final Window owner;
    
    private ObservableList<DialogListItem> listItems;
    
    public LibraryDialogController(EditorController editorController, DocumentWindowController documentWindowController, Window owner) {
        super(LibraryPanelController.class.getResource("LibraryDialog.fxml"), I18N.getBundle(), owner); //NOI18N
        this.documentWindowController = documentWindowController;
        this.owner = owner;
        this.editorController = editorController;
        this.userLibrary = (UserLibrary) editorController.getLibrary();
    }

    @Override
    protected void controllerDidCreateStage() {
        if (this.owner == null) {
            // Dialog will be appliation modal
            getStage().initModality(Modality.APPLICATION_MODAL);
        } else {
            // Dialog will be window modal
            getStage().initOwner(this.owner);
            getStage().initModality(Modality.WINDOW_MODAL);
        }
    }
    
    @Override
    public void onCloseRequest(WindowEvent event) {
        close();
    }

    @Override
    public void openWindow() {
        super.openWindow();
        super.getStage().setTitle(I18N.getString("library.dialog.title"));
        loadLibraryList();
        
        installCombo.getItems().setAll(TYPE.values());
        installCombo.setConverter(new StringConverter<TYPE>() {
            @Override
            public String toString(TYPE object) {
                return I18N.getString(object.getI18n());
            }

            @Override
            public TYPE fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); 
            }
        });
        installCombo.setCellFactory(p -> new ListCell<TYPE>() {
            @Override
            protected void updateItem(TYPE item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(I18N.getString(item.getI18n()));
                } else {
                    setText(null);
                }
            }
            
        });
        installButton.disableProperty().bind(installCombo.getSelectionModel().selectedItemProperty().isNull());
    }

    void loadLibraryList() {
        if (listItems == null) {
            listItems = FXCollections.observableArrayList();
        }
        listItems.clear();
        libraryListView.setItems(listItems);
        libraryListView.setCellFactory(param -> new LibraryDialogListCell());
        
        final Path folder = Paths.get(this.userLibrary.getPath());
        if (folder != null && folder.toFile().exists()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
                for (Path entry : stream) {
                    if (isJarPath(entry) || isFxmlPath(entry)) {
                        listItems.add(new LibraryDialogListItem(this, entry));
                    }
                }
            } catch (IOException x) {
                Logger.getLogger(LibraryDialogController.class.getName()).log(Level.SEVERE, "Error while getting a new directory stream.", x);
            }
        }
        
        // main artifacts
        listItems.addAll(PreferencesController.getSingleton().getMavenPreferences().getArtifactsCoordinates()
                .stream()
                .map(c -> new ArtifactDialogListItem(this, c))
                .collect(Collectors.toList()));
        
        
        // preset on top
        listItems.addAll(0, 
            MavenPresets.getPresetArtifacts()
                .stream()
                .filter(p -> listItems.stream().noneMatch(i -> {
                    final String artifactId = p.split(":")[1];
                    String name;
                    if (i instanceof LibraryDialogListItem) {
                        name = ((LibraryDialogListItem) i).getFilePath().getFileName().toString();
                    } else {
                        name = ((ArtifactDialogListItem) i).getCoordinates();
                    }
                    return (name.contains(artifactId));
                 }))
                .map(p -> new PresetDialogListItem(this, p))
                .collect(Collectors.toList()));

    }

    private static boolean isJarPath(Path path) {
        final String pathString = path.toString().toLowerCase(Locale.ROOT);
        return pathString.endsWith(".jar"); //NOI18N
    }

    private static boolean isFxmlPath(Path path) {
        final String pathString = path.toString().toLowerCase(Locale.ROOT);
        return pathString.endsWith(".fxml"); //NOI18N
    }

    @FXML
    private void close() {
        installCombo.getSelectionModel().clearSelection();
        libraryListView.getItems().clear();
        closeWindow();
    }

    @FXML
    private void installJar() {
        TYPE type = installCombo.getSelectionModel().getSelectedItem();
        switch(type) {
            case FILE: importJar(); break;
            case FOLDER: importJarsInFolder(); break;
            case MANUAL: importArtifact(false); break;
            case LATEST: importArtifact(true); break;
        }

    }
    
    @FXML
    private void manage() {
        RepositoryManagerController repositoryDialogController = new RepositoryManagerController(editorController, documentWindowController, getStage());
        repositoryDialogController.openWindow();
    }
    
    private void importJar() {
        documentWindowController.onImportJarFxml(getStage());
        loadLibraryList();
    }

    private void importJarsInFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(I18N.getString("library.dialog.folder.title"));
        chooser.setInitialDirectory(EditorController.getNextInitialDirectory());
        File selectedDirectory = chooser.showDialog(getStage());
        if (selectedDirectory != null) {
            final List<File> files = Arrays.asList(selectedDirectory.listFiles());
            documentWindowController.onImportJarFxmlFromFolder(files);
            loadLibraryList();
        }
    }

    private void importArtifact(boolean latest) {
        mavenDialog(null, null, latest);
    }
    
    private void mavenDialog(String groupId, String artifactId, boolean latest) {
        MavenDialogController mavenDialogController = new MavenDialogController(editorController, documentWindowController, getStage(), latest);
        mavenDialogController.openWindow();
        if (groupId != null && artifactId != null) {
            mavenDialogController.resolveVersions(groupId, artifactId);
        }
        mavenDialogController.getStage().showingProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (!mavenDialogController.getStage().isShowing()) {
                    loadLibraryList();
                    mavenDialogController.getStage().showingProperty().removeListener(this);
                }
            }
        });
        
    }
    
    /*
    If the file is an fxml, we don't need to stop the library watcher.
    Else we have to stop it first:
    1) We stop the library watcher, so that all related class loaders will be closed and the jar can be deleted.
    2) Then, if the file exists, the jar or fxml file will be deleted from the library.
    3) After the jar or fxml is removed, the library watcher is started again.
     */
    public void processJarFXMLDelete(DialogListItem dialogListItem) {
        if (dialogListItem instanceof LibraryDialogListItem &&
            isFxmlPath(((LibraryDialogListItem) dialogListItem).getFilePath())) {
            deleteFile(dialogListItem);
        } else {
            //1)
            userLibrary.stopWatching();
            
            //2)
            deleteFile(dialogListItem);
            
            //3)
            userLibrary.startWatching();
        }
    }

    private void deleteFile(DialogListItem dialogListItem) {
        try {
            if (dialogListItem instanceof LibraryDialogListItem) {
                LibraryDialogListItem item = (LibraryDialogListItem) dialogListItem;
                if (Files.exists(item.getFilePath())) {
                    Files.delete(item.getFilePath());
                    listItems.remove(item);
                }
            } else if (dialogListItem instanceof ArtifactDialogListItem) {
                PreferencesController.getSingleton()
                        .removeArtifact(((ArtifactDialogListItem) dialogListItem).getCoordinates());
                listItems.remove(dialogListItem);
            }
        } catch (IOException x) {
            Logger.getLogger(LibraryDialogController.class.getName()).log(Level.SEVERE, "Error while deleting the file.", x);
        }
        loadLibraryList();
    }
    
    public void processJarFXMLEdit(DialogListItem dialogListItem) {
        if (dialogListItem instanceof LibraryDialogListItem) {
            LibraryDialogListItem item = (LibraryDialogListItem) dialogListItem;
            if (Files.exists(item.getFilePath())) {
                if (isJarPath(item.getFilePath())) {
                    final ImportWindowController iwc = new ImportWindowController(
                            new LibraryPanelController(editorController), 
                            Arrays.asList(item.getFilePath().toFile()), getStage());
                    iwc.setToolStylesheet(editorController.getToolStylesheet());
                    // See comment in OnDragDropped handle set in method startListeningToDrop.
                    AbstractModalDialog.ButtonID userChoice = iwc.showAndWait();
                    if (userChoice == AbstractModalDialog.ButtonID.OK) {
                        logInfoMessage("log.user.maven.updated", item);
                    }
                } else {
                    if (SceneBuilderApp.getSingleton().lookupUnusedDocumentWindowController() != null) {
                        closeWindow();
                    }
                    SceneBuilderApp.getSingleton().performOpenRecent(documentWindowController, 
                            item.getFilePath().toFile());
                } 
            }
        } else if (dialogListItem instanceof ArtifactDialogListItem) {
            MavenPreferences mavenPreferences = PreferencesController.getSingleton().getMavenPreferences();
            MavenArtifact mavenArtifact = mavenPreferences
                    .getRecordArtifact(((ArtifactDialogListItem) dialogListItem).getCoordinates())
                    .getMavenArtifact();
            List<File> files = mavenPreferences.getArtifactFileWithDependencies(mavenArtifact);
            List<String> filter = mavenPreferences.getArtifactFilter(mavenArtifact);

            final ImportWindowController iwc = new ImportWindowController(
                        new LibraryPanelController(editorController), 
                                files, getStage(), false, filter);
            iwc.setToolStylesheet(editorController.getToolStylesheet());
            AbstractModalDialog.ButtonID userChoice = iwc.showAndWait();
            if (userChoice == AbstractModalDialog.ButtonID.OK) {
                mavenArtifact.setFilter(iwc.getNewExcludedItems());
                updatePreferences(mavenArtifact);
                logInfoMessage("log.user.maven.updated", mavenArtifact.getCoordinates());
            }
        }
    }
    
    public void processJarFXMLInstall(DialogListItem dialogListItem) {
        if (dialogListItem instanceof PresetDialogListItem) {
            mavenDialog(((PresetDialogListItem) dialogListItem).getGroupId(), 
                    ((PresetDialogListItem) dialogListItem).getArtifactId(), true);
        }
    }
    
    private void logInfoMessage(String key, Object... args) {
        editorController.getMessageLog().logInfoMessage(key, I18N.getBundle(), args);
    }
    
    private void updatePreferences(MavenArtifact mavenArtifact) {
        if (mavenArtifact == null) {
            return;
        }
        
        userLibrary.stopWatching();
        
        // Update record artifact
        final PreferencesRecordArtifact recordArtifact = PreferencesController.getSingleton().
                getRecordArtifact(mavenArtifact);
        recordArtifact.writeToJavaPreferences();

        userLibrary.startWatching();
        
    }
}