package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import com.oracle.javafx.scenebuilder.app.DocumentWindowController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordArtifact;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.ImportWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenRepositorySystem;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AbstractModalDialog.ButtonID;
import com.oracle.javafx.scenebuilder.kit.library.user.UserLibrary;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;


/**
 * Controller for the JAR Maven dialog.
 */
public class SearchMavenDialogController extends AbstractFxmlWindowController {

    @FXML
    private TextField searchTextfield;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private ListView<Artifact> resultsListView;

    @FXML
    private ProgressIndicator progress;
    
    @FXML
    private Button installButton;

    private final DocumentWindowController documentWindowController;

    private final UserLibrary userLibrary;
    
    private MavenRepositorySystem maven;
    private final SearchService searchService;
    private final Service<MavenArtifact> installService;
    private final Window owner;
    
    public SearchMavenDialogController(EditorController editorController, DocumentWindowController documentWindowController, 
            Window owner) {
        super(LibraryPanelController.class.getResource("SearchMavenDialog.fxml"), I18N.getBundle(), owner); //NOI18N
        this.documentWindowController = documentWindowController;
        this.userLibrary = (UserLibrary) editorController.getLibrary();
        this.owner = owner;
        
        maven = new MavenRepositorySystem(true); // only releases
        
        searchService = new SearchService();
        searchService.getResult().addListener((ListChangeListener.Change<? extends Artifact> c) -> {
            while (c.next()) {
                resultsListView.getItems().setAll(searchService.getResult()
                        .stream()
                        .sorted((a1, a2) -> a1.toString().compareTo(a2.toString()))
                        .collect(Collectors.toList()));
            }
        });
        
        installService = new Service<MavenArtifact>() {
            @Override
            protected Task<MavenArtifact> createTask() {
                return new Task<MavenArtifact>() {
                    @Override
                    protected MavenArtifact call() throws Exception {
                        return resolveArtifacts();
                    }
                };
            }
        };
        
        installService.stateProperty().addListener((obs, ov, nv) -> {
            if (nv.equals(Worker.State.SUCCEEDED)) {
                final MavenArtifact mavenArtifact = installService.getValue();
                final PreferencesController pc = PreferencesController.getSingleton();
    
                List<File> files = new ArrayList<>();
                files.add(new File(mavenArtifact.getPath()));
                if (!mavenArtifact.getDependencies().isEmpty()) {
                    files.addAll(Stream
                            .of(mavenArtifact.getDependencies().split(":"))
                            .map(File::new)
                            .collect(Collectors.toList()));
                }
                
                final ImportWindowController iwc
                        = new ImportWindowController(
                            new LibraryPanelController(editorController), 
                            files, installButton.getScene().getWindow(), false,
                            pc.getMavenPreferences().getArtifactsFilter());
                iwc.setToolStylesheet(editorController.getToolStylesheet());
                ButtonID userChoice = iwc.showAndWait();
                if (userChoice == ButtonID.OK) {
                    mavenArtifact.setFilter(iwc.getNewExcludedItems());
                    updatePreferences(mavenArtifact);
                    logInfoMessage("log.user.maven.installed", getArtifactCoordinates());
                }
                this.onCloseRequest(null);
            } else if (nv.equals(Worker.State.CANCELLED) || nv.equals(Worker.State.FAILED)) {
                logInfoMessage("log.user.maven.failed", getArtifactCoordinates());
            }
        });
        
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
        cancel();
    }

    @Override
    public void openWindow() {
        super.openWindow();
        super.getStage().setTitle(I18N.getString("search.maven.dialog.title"));
        installButton.disableProperty().bind(resultsListView.getSelectionModel().selectedIndexProperty().lessThan(0));
        installButton.setTooltip(new Tooltip(I18N.getString("search.maven.dialog.install.tooltip")));
        
        searchButton.disableProperty().bind(searchTextfield.textProperty().isEmpty());
        searchTextfield.setOnAction(e -> searchButton.fire());
        searchButton.setOnAction(e -> {
            if (progress.isVisible()) {
                searchService.cancelSearch();
            } else {
                searchService.setQuery(searchTextfield.getText());
                searchService.restart();
            }
        });
        
        resultsListView.setCellFactory(p -> new ListCell<Artifact>() {
            @Override
            protected void updateItem(Artifact item, boolean empty) {
                super.updateItem(item, empty); 
                if (item != null && !empty) {
                    setText(item.getGroupId() + ":" + item.getArtifactId() + ":" + item.getVersion());
                } else {
                    setText(null);
                }
            }
            
        });
        
        searchButton.textProperty().bind(Bindings.when(progress.visibleProperty())
                .then(I18N.getString("search.maven.dialog.button.cancel"))
                .otherwise(I18N.getString("search.maven.dialog.button.search")));
        searchButton.tooltipProperty().bind(Bindings.when(progress.visibleProperty())
                .then(new Tooltip(I18N.getString("search.maven.dialog.button.cancel.tooltip")))
                .otherwise(new Tooltip(I18N.getString("search.maven.dialog.button.search.tooltip"))));
        
        progress.visibleProperty().bind(installService.runningProperty()
                        .or(searchService.searchingProperty()));
    }

    @FXML
    private void installJar() {
        installService.restart();
    }
    
    @FXML
    private void cancel() {
        installButton.disableProperty().unbind();
        progress.visibleProperty().unbind();
        
        searchTextfield.clear();
        resultsListView.getItems().clear();
        
        closeWindow();
    }
    
    private MavenArtifact resolveArtifacts() {
        String[] coordinates = getArtifactCoordinates().split(":");
        Artifact jarArtifact = new DefaultArtifact(coordinates[0], 
                coordinates[1], "", "jar", coordinates[2]);

        Artifact javadocArtifact = new DefaultArtifact(coordinates[0], 
                coordinates[1], "javadoc", "jar", coordinates[2]);

        Artifact pomArtifact = new DefaultArtifact(coordinates[0], 
                coordinates[1], "", "pom", coordinates[2]);

        MavenArtifact mavenArtifact = new MavenArtifact(getArtifactCoordinates());
        mavenArtifact.setPath(maven.resolveArtifacts(null, jarArtifact, javadocArtifact, pomArtifact));
        mavenArtifact.setDependencies(maven.resolveDependencies(null, jarArtifact));
        
        return mavenArtifact;
    }

    private void logInfoMessage(String key, Object... args) {
        documentWindowController.getEditorController().getMessageLog().logInfoMessage(key, I18N.getBundle(), args);
    }
    
    private String getArtifactCoordinates() {
        final Artifact item = resultsListView.getSelectionModel().getSelectedItem();
        return item.getGroupId() + ":" + item.getArtifactId() + ":" + item.getVersion();
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