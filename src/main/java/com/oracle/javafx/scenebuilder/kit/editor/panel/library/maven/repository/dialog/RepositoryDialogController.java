package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.dialog;

import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordRepository;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenRepositorySystem;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.Repository;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class RepositoryDialogController extends AbstractFxmlWindowController {
    
    @FXML
    private AnchorPane MavenDialog;

    @FXML
    private TextField nameIDTextfield;

    @FXML
    private TextField typeTextfield;

    @FXML
    private Button searchButton;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Button cancelButton;

    @FXML
    private Button addButton;

    @FXML
    private TextField urlTextfield;

    @FXML
    private CheckBox privateCheckBox;
    
    @FXML
    private Label userLabel;
    
    @FXML
    private TextField userTextfield;

    @FXML
    private Label passwordLabel;
    
    @FXML
    private PasswordField passwordTextfield;
    
    @FXML
    private Button testButton;
    
    @FXML
    private Label resultLabel;
//    
    private final Window owner;
    private final EditorController editorController;
    private MavenRepositorySystem maven;
    private Repository oldRepository;
    private final Service<String> testService;
    
    public RepositoryDialogController(EditorController editorController, Window owner) {
        super(LibraryPanelController.class.getResource("RepositoryDialog.fxml"), I18N.getBundle(), owner); //NOI18N
        this.owner = owner;
        this.editorController = editorController;
        
        testService = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        return maven.validateRepository(new Repository(nameIDTextfield.getText(), 
                            typeTextfield.getText(), urlTextfield.getText(),
                            userTextfield.getText(), passwordTextfield.getText()));
                    }
                };
            }
        };
        
        testService.stateProperty().addListener((obs, ov, nv) -> {
            if (nv.equals(Worker.State.SUCCEEDED)) {
                String result = testService.getValue();
                if (result.isEmpty()) {
                    if (resultLabel.getStyleClass().contains("label-error")) {
                        resultLabel.getStyleClass().remove("label-error");
                    }
                    result = I18N.getString("repository.dialog.result");
                } else {
                    if (!resultLabel.getStyleClass().contains("label-error")) {
                        resultLabel.getStyleClass().add("label-error");
                    }
                }
                resultLabel.setText(result);
                resultLabel.setTooltip(new Tooltip(result));
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
    
    @FXML
    void cancel() {
        closeWindow();
        
        nameIDTextfield.clear();
        typeTextfield.clear();
        urlTextfield.clear();
        userTextfield.clear();
        passwordTextfield.clear();
        privateCheckBox.setSelected(false);
        resultLabel.setText("");
    }

    @FXML
    void addRepository() {
        Repository repository;
        if (privateCheckBox.isSelected()) {
            repository = new Repository(nameIDTextfield.getText(), 
                typeTextfield.getText(), urlTextfield.getText(), 
                userTextfield.getText(), passwordTextfield.getText());
        } else {
            repository = new Repository(nameIDTextfield.getText(), 
                typeTextfield.getText(), urlTextfield.getText());
        }
        if (oldRepository != null) {
            PreferencesController.getSingleton().removeRepository(oldRepository.getId());
        }
        updatePreferences(repository);
        logInfoMessage(oldRepository == null ? "log.user.repository.added" :
                "log.user.repository.updated", repository.getId());
        cancel();
    }
    
    @FXML
    void test() {
        testService.restart();
    }

    @Override
    public void onCloseRequest(WindowEvent event) {
        cancel();
    }

    @Override
    public void openWindow() {
        super.openWindow();
        
        userLabel.disableProperty().bind(privateCheckBox.selectedProperty().not());
        userTextfield.disableProperty().bind(privateCheckBox.selectedProperty().not());
        passwordLabel.disableProperty().bind(privateCheckBox.selectedProperty().not());
        passwordTextfield.disableProperty().bind(privateCheckBox.selectedProperty().not());
        progress.visibleProperty().bind(testService.runningProperty());
        resultLabel.setText("");
        maven = new MavenRepositorySystem(false);
    }
    
    private void logInfoMessage(String key, Object... args) {
        editorController.getMessageLog().logInfoMessage(key, I18N.getBundle(), args);
    }
    
    private void updatePreferences(Repository repository) {
        if (repository == null) {
            return;
        }
        
        // Update record repository
        final PreferencesRecordRepository recordRepository = PreferencesController.getSingleton().
                getRecordRepository(repository);
        recordRepository.writeToJavaPreferences();
        
    }

    public void setRepository(Repository repository) {
        oldRepository = repository;
        if (repository == null) {
            super.getStage().setTitle(I18N.getString("repository.dialog.title.add"));
            addButton.setText(I18N.getString("repository.dialog.add"));
            addButton.setTooltip(new Tooltip(I18N.getString("repository.dialog.add.tooltip")));
        
            addButton.disableProperty().bind(nameIDTextfield.textProperty().isEmpty().or(
                      typeTextfield.textProperty().isEmpty().or(
                      urlTextfield.textProperty().isEmpty())));
        
            return;
        }
        
        nameIDTextfield.setText(repository.getId());
        typeTextfield.setText(repository.getType());
        urlTextfield.setText(repository.getURL());
        
        userTextfield.clear();
        if (repository.getUser() != null) {
            userTextfield.setText(repository.getUser());
            privateCheckBox.setSelected(true);
        } 
        passwordTextfield.clear();
        if (repository.getPassword()!= null) {
            passwordTextfield.setText(repository.getPassword());
            privateCheckBox.setSelected(true);
        } 
        
        addButton.disableProperty().bind(nameIDTextfield.textProperty().isEqualTo(repository.getId()).and(
                  typeTextfield.textProperty().isEqualTo(repository.getType()).and(
                  urlTextfield.textProperty().isEqualTo(repository.getURL()).and(
                  userTextfield.textProperty().isEqualTo(repository.getUser()).and(
                  passwordTextfield.textProperty().isEqualTo(repository.getPassword()))))));
        super.getStage().setTitle(I18N.getString("repository.dialog.title.update"));
        addButton.setText(I18N.getString("repository.dialog.update"));
        addButton.setTooltip(new Tooltip(I18N.getString("repository.dialog.update.tooltip")));
    }
}
