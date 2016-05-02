package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository;

import com.oracle.javafx.scenebuilder.app.DocumentWindowController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordRepository;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.preset.MavenPresets;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.dialog.RepositoryDialogController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.stage.Modality;

/**
 * Controller for the JAR/FXML Library dialog.
 */
public class RepositoryManagerController extends AbstractFxmlWindowController {

    @FXML
    private ListView<RepositoryListItem> repositoryListView;
    
    private final DocumentWindowController documentWindowController;

    private final EditorController editorController;
    private final Window owner;
    
    private ObservableList<RepositoryListItem> listItems;
    
    public RepositoryManagerController(EditorController editorController, DocumentWindowController documentWindowController, Window owner) {
        super(LibraryPanelController.class.getResource("RepositoryManager.fxml"), I18N.getBundle(), owner); //NOI18N
        this.documentWindowController = documentWindowController;
        this.owner = owner;
        this.editorController = editorController;
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
        super.getStage().setTitle(I18N.getString("repository.manager.title"));
        loadRepositoryList();
    }

    private void loadRepositoryList() {
        if (listItems == null) {
            listItems = FXCollections.observableArrayList();
        }
        listItems.clear();
        repositoryListView.setItems(listItems);
        repositoryListView.setCellFactory(param -> new RepositoryManagerListCell());
        
        // custom repositories
        listItems.addAll(PreferencesController.getSingleton().getRepositoryPreferences().getRepositories()
                .stream()
                .map(r -> new CustomRepositoryListItem(this, r))
                .collect(Collectors.toList()));
        
        // preset on top
        listItems.addAll(0, MavenPresets.getPresetRepositories()
            .stream()
            .map(r -> new RepositoryListItem(this, r))
            .collect(Collectors.toList()));
    }

    @FXML
    private void close() {
        repositoryListView.getItems().clear();
        closeWindow();
    }

    @FXML
    private void addRepository() {
        repositoryDialog(null);
    }

    private void repositoryDialog(Repository repository) {
        RepositoryDialogController repositoryDialogController = new RepositoryDialogController(editorController, getStage());
        repositoryDialogController.openWindow();
        repositoryDialogController.setRepository(repository);
        repositoryDialogController.getStage().showingProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (!repositoryDialogController.getStage().isShowing()) {
                    loadRepositoryList();
                    repositoryDialogController.getStage().showingProperty().removeListener(this);
                }
            }
        });
    }
    
    public void edit(RepositoryListItem item) {
        repositoryDialog(item.getRepository());
    }
    
    public void delete(RepositoryListItem item) {
        // Remove repository
        logInfoMessage("log.user.repository.removed", item.getRepository().getId());
        PreferencesController.getSingleton().removeRepository(item.getRepository().getId());
        loadRepositoryList();
    }
    
    private void logInfoMessage(String key, Object... args) {
        editorController.getMessageLog().logInfoMessage(key, I18N.getBundle(), args);
    }
    
}