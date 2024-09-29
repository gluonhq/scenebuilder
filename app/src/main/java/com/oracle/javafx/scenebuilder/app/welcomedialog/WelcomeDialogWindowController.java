/*
 * Copyright (c) 2017, 2024, Gluon and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oracle.javafx.scenebuilder.app.welcomedialog;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.app.SceneBuilderApp;
import com.oracle.javafx.scenebuilder.app.i18n.I18N;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal;
import com.oracle.javafx.scenebuilder.app.util.AppSettings;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AbstractModalDialog.ButtonID;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AlertDialog;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.ErrorDialog;
import com.oracle.javafx.scenebuilder.kit.template.Template;
import com.oracle.javafx.scenebuilder.kit.template.TemplatesBaseWindowController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WelcomeDialogWindowController extends TemplatesBaseWindowController {

    private static final Logger LOGGER = Logger.getLogger(WelcomeDialogWindowController.class.getName());

    @FXML
    private BorderPane contentPane;

    @FXML
    private VBox recentDocuments;

    @FXML
    private Button emptyApp;

    @FXML
    private VBox masker;

    @FXML
    private ProgressIndicator progress;

    private static WelcomeDialogWindowController instance;

    private final SceneBuilderApp sceneBuilderApp;

    WelcomeDialogWindowController() {
        super(WelcomeDialogWindowController.class.getResource("WelcomeWindow.fxml"), //NOI18N
                I18N.getBundle(),
                null); // We want it to be a top level window so we're setting the owner to null.

        sceneBuilderApp = SceneBuilderApp.getSingleton();
    }

    @Override
    public void onCloseRequest(WindowEvent event) {
        getStage().hide();
    }

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
        assert getRoot().getScene().getWindow() != null;

        getStage().setTitle(I18N.getString("welcome.title"));
        getStage().initModality(Modality.APPLICATION_MODAL);
    }
    
    @FXML
    void handleFileDraggedOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    void handleDroppedFiles(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            new WelcomeDialogFilesDropHandler(event.getDragboard().getFiles())
                .withSupportedFiles(fileNames->Platform.runLater(()->handleOpen(fileNames)))
                .withUnsupportedFiles(unsupported->notifyUserWhenDroppedUnsupportedFiles(unsupported))
                .run();
        }
    }

    private void notifyUserWhenDroppedUnsupportedFiles(List<String> unsupported) {
        ErrorDialog dialog = new ErrorDialog(getStage());
        dialog.setTitle(I18N.getString("welcome.loading.when.dropped.error.title"));
        dialog.setMessage(I18N.getString("welcome.loading.when.dropped.error.message"));
        dialog.setDetailsTitle(I18N.getString("welcome.loading.when.dropped.error.title"));
        dialog.setDetails(I18N.getString("welcome.loading.when.dropped.error.detail.explanation"));
        
        String debugInfo = unsupported.stream()
                                      .collect(Collectors.joining(System.lineSeparator()));
        
        dialog.setDebugInfo(debugInfo);
        
        Platform.runLater(()->dialog.showAndWait());
    }

    @Override
    protected void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert recentDocuments != null;

        loadAndPopulateRecentItemsInBackground();

        setOnTemplateChosen(this::fireSelectTemplate);
    }

    private void loadAndPopulateRecentItemsInBackground() {
        Label loadingRecentItems = new Label(I18N.getString("welcome.recent.items.loading"));
        loadingRecentItems.getStyleClass().add("no-recent-items-label");

        recentDocuments.getChildren().add(loadingRecentItems);

        var t = new Thread(() -> {
            PreferencesRecordGlobal preferencesRecordGlobal = PreferencesController
                    .getSingleton().getRecordGlobal();
            List<String> recentItems = preferencesRecordGlobal.getRecentItems();

            Platform.runLater(() -> recentDocuments.getChildren().clear());

            if (recentItems.size() == 0) {
                Label noRecentItems = new Label(I18N.getString("welcome.recent.items.no.recent.items"));
                noRecentItems.getStyleClass().add("no-recent-items-label");

                Platform.runLater(() -> {
                    recentDocuments.getChildren().add(noRecentItems);
                });
            }

            List<Button> recentDocumentButtons = new ArrayList<>();

            for (int row = 0; row < preferencesRecordGlobal.getRecentItemsSize(); ++row) {
                if (recentItems.size() < row + 1) {
                    break;
                }

                String recentItem = recentItems.get(row);
                File recentItemFile = new File(recentItems.get(row));
                String recentItemTitle = recentItemFile.getName();
                Button recentDocument = new Button(recentItemTitle);
                recentDocument.getStyleClass().add("recent-document");
                recentDocument.setMaxWidth(Double.MAX_VALUE);
                recentDocument.setAlignment(Pos.BASELINE_LEFT);
                recentDocument.setOnAction(event -> fireOpenRecentProject(event, recentItem));
                recentDocument.setTooltip(new Tooltip(recentItem));
                /* if MnemonicParsing is enabled, file names with underscores are displayed incorrectly */
                recentDocument.setMnemonicParsing(false);
                recentDocumentButtons.add(recentDocument);
            }

            Platform.runLater(() -> {
                recentDocumentButtons.forEach(btn -> recentDocuments.getChildren().add(btn));
            });
        }, "Recent Items Loading Thread");
        t.setDaemon(true);
        t.start();
    }

    public static WelcomeDialogWindowController getInstance() {
        if (instance == null) {
            instance = new WelcomeDialogWindowController();
            var stage = instance.getStage();
            stage.setMinWidth(800);
            stage.setMinHeight(650);
            AppSettings.setWindowIcon(stage);
        }
        return instance;
    }

    private void fireSelectTemplate(Template template) {
        if (sceneBuilderApp.startupTasksFinishedBinding().get()) {
            sceneBuilderApp.performNewTemplate(template);
            getStage().hide();
        } else {
            showMasker(() -> {
                sceneBuilderApp.performNewTemplate(template);
                getStage().hide();
            });
        }
    }

    private void fireOpenRecentProject(ActionEvent event, String projectPath) {
        handleOpen(List.of(projectPath));
    }

    @FXML
    private void openDocument() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(I18N.getString("file.filter.label.fxml"), "*.fxml")
        );
        fileChooser.setInitialDirectory(EditorController.getNextInitialDirectory());
        List<File> fxmlFiles = fileChooser.showOpenMultipleDialog(getStage());

        // no file was selected, so nothing to do
        if (fxmlFiles == null)
            return;

        List<String> paths = fxmlFiles
                .stream()
                .map(File::toString)
                .collect(Collectors.toList());

        handleOpen(paths);
    }
    
    protected static AlertDialog questionMissingFilesCleanup(Stage stage, List<String> missingFiles) {
        String withPath = missingFiles.stream()
                                      .collect(Collectors.joining(System.lineSeparator()));
        
        AlertDialog question = new AlertDialog(stage);
        question.setDefaultButtonID(ButtonID.CANCEL);
        question.setShowDefaultButton(true);
        question.setOKButtonTitle(I18N.getString("alert.welcome.file.not.found.okay"));
        question.setTitle(I18N.getString("alert.welcome.file.not.found.title"));
        question.setMessage(I18N.getString("alert.welcome.file.not.found.question"));
        question.setCancelButtonTitle(I18N.getString("alert.welcome.file.not.found.no"));
        question.setDetails(I18N.getString("alert.welcome.file.not.found.message") + withPath);
        return question;
    }
    
    boolean filePathExists(String filePath) {
        return Files.exists(Path.of(filePath));
    }

    /**
     * Attempts to open files in filePaths. Scene Builder will only attempt to load
     * files which exist. If a file does not exist, Scene Builder will ask the user
     * to remove this file from recent files.
     * 
     * @param filePaths List of file paths to project files to be opened by Scene
     *                  Builder.
     */
    private void handleOpen(List<String> filePaths) {
        handleOpen(filePaths, 
                   this::askUserToRemoveMissingRecentFiles,
                   this::attemptOpenExistingFiles);
    }

    private void askUserToRemoveMissingRecentFiles(List<String> missingFiles) {
        if (!missingFiles.isEmpty()) {
            var questionDialog = questionMissingFilesCleanup(getStage(), missingFiles);
            if (questionDialog.showAndWait() == AlertDialog.ButtonID.OK) {
                removeMissingFilesFromPrefs(missingFiles);
                loadAndPopulateRecentItemsInBackground();
            }
        }
    }

    private void attemptOpenExistingFiles(List<String> paths) {
        if (sceneBuilderApp.startupTasksFinishedBinding().get()) {
            openFilesAndHideStage(paths);
        } else {
            showMasker(() -> openFilesAndHideStage(paths));
        }
    }

    private void openFilesAndHideStage(List<String> files) {
        sceneBuilderApp.handleOpenFilesAction(files, () -> getStage().hide());
    }

    /**
     * Attempts to open files in filePaths.
     * In case of files are missing, a special procedure is applied to handle missing files.
     *  
     * @param filePaths List of file paths to project files to be opened by Scene Builder.
     * @param missingFilesHandler Determines how missing files are handled.
     * @param fileLoader Determines how files are loaded.
     */
    void handleOpen(List<String> filePaths, 
                    Consumer<List<String>> missingFilesHandler,
                    Consumer<List<String>> fileLoader) {
        LOGGER.log(Level.INFO, "Attempting to open files: {0}", filePaths);
        if (filePaths.isEmpty()) {
            return;
        }

        List<String> existingFiles = new ArrayList<>();
        List<String> missingFiles = new ArrayList<>();
        filePaths.forEach(file -> {
            if (filePathExists(file)) {
                existingFiles.add(file);
            } else {
                missingFiles.add(file);
            }
        });
        
        missingFilesHandler.accept(missingFiles);

        if (existingFiles.isEmpty()) {
            return;
        }

        fileLoader.accept(existingFiles);
    }

    private void removeMissingFilesFromPrefs(List<String> missingFiles) {
        missingFiles.forEach(fxmlFileName -> LOGGER.log(Level.INFO, "Removing missing file from recent items: {0}", fxmlFileName));
        PreferencesRecordGlobal preferencesRecordGlobal = PreferencesController.getSingleton().getRecordGlobal();
        preferencesRecordGlobal.removeRecentItems(missingFiles);
    }

    private void showMasker(Runnable onEndAction) {
        contentPane.setDisable(true);
        masker.setVisible(true);

        // force progress indicator to render
        progress.setProgress(-1);

        sceneBuilderApp.startupTasksFinishedBinding().addListener((o, old, isFinished) -> {
            if (isFinished) {
                Platform.runLater(() -> {
                    onEndAction.run();
                    // restore state in case welcome dialog is opened again
                    contentPane.setDisable(false);
                    masker.setVisible(false);
                });
            }
        });
    }
}

