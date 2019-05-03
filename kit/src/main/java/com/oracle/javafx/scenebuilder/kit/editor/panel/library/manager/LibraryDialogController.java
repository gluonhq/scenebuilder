/*
 * Copyright (c) 2016, 2017 Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.kit.editor.panel.library.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.ImportWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryLocationEnum;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryUtil;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenDialogController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.RepositoryManagerController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search.SearchMavenDialogController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AbstractModalDialog;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.ErrorDialog;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.library.user.UserLibrary;
import com.oracle.javafx.scenebuilder.kit.library.user.ws.UserLibraryWorkspace;
import com.oracle.javafx.scenebuilder.kit.library.user.ws.UserWorkspaceLibraryItem;
import com.oracle.javafx.scenebuilder.kit.preferences.MavenPreferences;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferencesRecordArtifact;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Controller for the JAR/FXML Library dialog.
 */
public class LibraryDialogController extends AbstractFxmlWindowController {

    @FXML
    private ListView<DialogListItem> libraryListView;
    @FXML
    private ListView<DialogListItem> workspaceLibraryListView;
    @FXML
    private Hyperlink classesLink;
    @FXML
    private Hyperlink workspaceFolderLink;
    @FXML
    private Label actionsForWorkspaceLabel;

    
    private final EditorController editorController;
    private final UserLibrary userLibrary;
    private final Stage owner;
    
    private ObservableList<DialogListItem> listItems;
    private ObservableList<DialogListItem> workspaceListItems;

    private Runnable onAddJar;
    private Runnable onAddFolder;
    private Runnable onLinkJar;
    private Runnable onLinkFolder;
    private Consumer<Path> onEditFXML;

    private String userM2Repository;
    private String tempM2Repository;

    private final PreferencesControllerBase preferencesControllerBase;
    
    public LibraryDialogController(EditorController editorController, String userM2Repository, String tempM2Repository,
                                   PreferencesControllerBase preferencesController, Stage owner) {
        super(LibraryPanelController.class.getResource("LibraryDialog.fxml"), I18N.getBundle(), owner); //NOI18N
        this.owner = owner;
        this.editorController = editorController;
        this.userLibrary = (UserLibrary) editorController.getLibrary();
        this.userM2Repository = userM2Repository;
        this.tempM2Repository = tempM2Repository;
        this.preferencesControllerBase = preferencesController;
    }

    @Override
    protected void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();

        this.classesLink.setTooltip(new Tooltip(I18N.getString("library.dialog.hyperlink.tooltip")));
        
        this.workspaceFolderLink.setTooltip(new Tooltip(I18N.getString("library.dialog.hyperlink.tooltipworkspace")));
        this.actionsForWorkspaceLabel.setTooltip(new Tooltip(I18N.getString("library.dialog.label.installworkspace.tooltip")));
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
        loadWorkspaceLibraryList();
    }

    void loadLibraryList() {
        if (listItems == null) {
            listItems = FXCollections.observableArrayList();
        }
        listItems.clear();
        libraryListView.setItems(listItems);
        libraryListView.setCellFactory(param -> new LibraryDialogListCell(false));
        
        final Path folder = Paths.get(this.userLibrary.getPath());
        if (folder != null && folder.toFile().exists()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
                for (Path entry : stream) {
                    if (LibraryUtil.isJarPath(entry) || LibraryUtil.isFxmlPath(entry)) {
                        listItems.add(new LibraryDialogListItem(this, entry));
                    } else if (LibraryUtil.isFolderMarkerPath(entry)) {
                        // open folders marker file: every line should be a single folder entry
                        // we scan the file and add the path to currentJarsOrFolders
                        List<Path> folderPaths = LibraryUtil.getFolderPaths(entry);
                        for (Path f : folderPaths) {
                            listItems.add(new LibraryDialogListItem(this, f));
                        }
                    }
                }
            } catch (IOException x) {
                Logger.getLogger(LibraryDialogController.class.getName()).log(Level.SEVERE, "Error while getting a new directory stream.", x);
            }
        }
        
        // main artifacts
        listItems.addAll(preferencesControllerBase.getMavenPreferences().getArtifactsCoordinates()
                .stream()
                .map(c -> new ArtifactDialogListItem(this, c))
                .collect(Collectors.toList()));
    }
    
    void loadWorkspaceLibraryList() {
        if (workspaceListItems == null) {
            workspaceListItems = FXCollections.observableArrayList();
        }
        workspaceListItems.clear();
        workspaceLibraryListView.setItems(workspaceListItems);
        workspaceLibraryListView.setCellFactory(param -> new LibraryDialogListCell(true));
        
        Path workspacePath = Paths.get(userLibrary.getPath(), LibraryUtil.WORKSPACE_LIBRARY_FILENAME);
        if (Files.exists(workspacePath)) {
            UserLibraryWorkspace workspace = userLibrary.getUserWorkspace();

            for (UserWorkspaceLibraryItem item : workspace.getItems()) {

                Path entry = Paths.get(item.getPath());

                if (LibraryUtil.isJarPath(entry) || LibraryUtil.isFxmlPath(entry)) {
                    workspaceListItems.add(new LibraryDialogListItem(this, entry));
                } else if (Files.isDirectory(entry)) {
                    workspaceListItems.add(new LibraryDialogListItem(this, entry));
                }
            }
        }
    }

    @FXML
    private void close() {
        libraryListView.getItems().clear();
        workspaceLibraryListView.getItems().clear();
        closeWindow();
    }

    @FXML
    private void manage() {
        RepositoryManagerController repositoryDialogController = new RepositoryManagerController(editorController,
                userM2Repository, tempM2Repository, preferencesControllerBase, getStage());
        repositoryDialogController.openWindow();
    }
    
    @FXML
    private void addJar() {
//        documentWindowController.onImportJarFxml(getStage());
        if (onAddJar != null) {
            onAddJar.run();
        }
        loadLibraryList();
    }
    
    @FXML
    private void addFolder() {
        if (onAddFolder != null) {
            onAddFolder.run();
        }
        loadLibraryList();
    }

    @FXML
    private void addRelease() {
        SearchMavenDialogController mavenDialogController = new SearchMavenDialogController(editorController,
                userM2Repository, tempM2Repository, preferencesControllerBase, getStage());
        mavenDialogController.openWindow();
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
    
    @FXML
    private void addManually() {
        MavenDialogController mavenDialogController = new MavenDialogController(editorController, userM2Repository,
                tempM2Repository, preferencesControllerBase, getStage());
        mavenDialogController.openWindow();
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

    @FXML
    private void exportWorkspace(ActionEvent event) {

        final FileChooser fileChooser = new FileChooser();
        final ExtensionFilter f = new ExtensionFilter(I18N.getString("file.filter.label.xml"), "*.xml"); //NOI18N
        fileChooser.getExtensionFilters().add(f);
        fileChooser.setInitialDirectory(EditorController.getNextInitialDirectory());

        File xmlFile = fileChooser.showSaveDialog(getStage());
        
        if (xmlFile != null) {
            UserLibraryWorkspace workspace = userLibrary.getUserWorkspace();
            
            try {
                Files.write(xmlFile.toPath(), workspace.toXml().getBytes());
            }
            catch (IOException e) {
                Logger.getLogger(LibraryDialogController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                
                ErrorDialog errorDialog = new ErrorDialog(null);
                errorDialog.setTitle(I18N.getString("library.dialog.exportfail"));
                errorDialog.setMessage(e.getLocalizedMessage());
                errorDialog.setDetails(e.getLocalizedMessage());
                errorDialog.setDebugInfoWithThrowable(e);
                errorDialog.showAndWait();
            }
        }
    }

    @FXML
    private void importReplaceWorkspace(ActionEvent event) {
        importWorkspace(false);
    }
    @FXML
    private void importMergeWorkspace(ActionEvent event) {
        importWorkspace(true);
    }

    protected void importWorkspace(boolean merge) {
        
        final FileChooser fileChooser = new FileChooser();
        final ExtensionFilter f = new ExtensionFilter(I18N.getString("file.filter.label.xml"), "*.xml"); //NOI18N
        fileChooser.getExtensionFilters().add(f);
        fileChooser.setInitialDirectory(EditorController.getNextInitialDirectory());

        File xmlFile = fileChooser.showOpenDialog(getStage());
        
        if (xmlFile != null) {
            try {
                UserLibrary library = (UserLibrary) editorController.getLibrary();
                UserLibraryWorkspace userWorkspace = library.getUserWorkspace();
                
                // first we verify that the workspace is ok, by reading as an xml
                UserLibraryWorkspace workspace = UserLibraryWorkspace.fromXml(xmlFile.toURI().toURL());
                
                if (merge) {
                    // in case of merging, we need to copy current entries to the workspace
                    userWorkspace.mergeWith(workspace);
                }
                else {
                    // in case of replacing, just replace it
                    library.setUserWorkspace(workspace);
                }
                
                // refresh the itemList
                loadWorkspaceLibraryList();
                
                // Importing a workspace does write the current workspace xml content to the library-workspace.xml file inside userlib folder.
                // By doing just that, the watchservice on that folder will act as a refresher on the library.
                Files.write(Paths.get(userLibrary.getPath(), LibraryUtil.WORKSPACE_LIBRARY_FILENAME), library.getUserWorkspace().toXml().getBytes());
            }
            catch (IOException e) {
                Logger.getLogger(LibraryDialogController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                
                ErrorDialog errorDialog = new ErrorDialog(null);
                errorDialog.setTitle(I18N.getString("library.dialog.importfail"));
                errorDialog.setMessage(e.getLocalizedMessage());
                errorDialog.setDetails(e.getLocalizedMessage());
                errorDialog.setDebugInfoWithThrowable(e);
                errorDialog.showAndWait();
            }
        }
    }

    @FXML
    private void linkFolder(ActionEvent event) {
        if (onLinkFolder != null) {
            onLinkFolder.run();
        }
        loadWorkspaceLibraryList();
    }

    @FXML
    private void linkJar(ActionEvent event) {
        if (onLinkJar != null) {
            onLinkJar.run();
        }
        loadWorkspaceLibraryList();
    }

    /*
    If the file is an fxml, we don't need to stop the library watcher.
    Else we have to stop it first:
    1) We stop the library watcher, so that all related class loaders will be closed and the jar can be deleted.
    2) Then, if the file exists, the jar or fxml file will be deleted from the library.
    3) After the jar or fxml is removed, the library watcher is started again.
     */
    public void processJarFXMLFolderDelete(DialogListItem dialogListItem) {
        if (dialogListItem instanceof LibraryDialogListItem &&
            LibraryUtil.isFxmlPath(((LibraryDialogListItem) dialogListItem).getFilePath())) {
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

    /*
    1) We stop the library watcher, so that all related class loaders will be closed and the workspace can be edited for deletion.
    2) Then, we update the workspace, removing the entry.
    3) After the workspace update, the library watcher is started again.
     */
    public void processWorkspaceJarFXMLFolderDelete(DialogListItem dialogListItem) throws IOException {
        
        //1)
        userLibrary.stopWatching();
        
        //2)
        LibraryDialogListItem item = ((LibraryDialogListItem) dialogListItem);
        Path path = item.getFilePath();
        
        userLibrary.getUserWorkspace().removeItem(path);
        
        try {
            // saving the workspace to disk can fail: in that case, we restart the watcher anyway and let the exception go to the caller
            userLibrary.saveWorkspace();

            workspaceListItems.remove(dialogListItem);
        } finally {
            //3)
            userLibrary.startWatching();
        }
    }
    
    private void deleteFile(DialogListItem dialogListItem) {
        try {
            if (dialogListItem instanceof LibraryDialogListItem) {
                LibraryDialogListItem item = (LibraryDialogListItem) dialogListItem;
                Path path = item.getFilePath();

                if (Files.exists(path)) {
                    if (Files.isDirectory(path)) {
                        // we need to remove the entry from the folder list in the placeholder marker
                        String libraryPath = ((UserLibrary) editorController.getLibrary()).getPath();

                        Path foldersPath = Paths.get(libraryPath, LibraryUtil.FOLDERS_LIBRARY_FILENAME);
                        if (Files.exists(foldersPath)) {

                            List<String> lines = Files.readAllLines(foldersPath);

                            for (Iterator<String> it = lines.iterator(); it.hasNext();) {
                                String line = (String) it.next();
                                if (line.equals(path.toString()))
                                    it.remove();
                            }

                            Files.write(foldersPath, lines);
                        }
                    }
                    else {
                        Files.delete(path);
                        listItems.remove(item);
                    }
                }
            } else if (dialogListItem instanceof ArtifactDialogListItem) {
                preferencesControllerBase.removeArtifact(((ArtifactDialogListItem) dialogListItem).getCoordinates());
                listItems.remove(dialogListItem);
            }
        } catch (IOException x) {
            Logger.getLogger(LibraryDialogController.class.getName()).log(Level.SEVERE, "Error while deleting the file.", x);
        }
        loadLibraryList();
    }
    
    public void processJarFXMLFolderEdit(DialogListItem dialogListItem) {
        if (dialogListItem instanceof LibraryDialogListItem) {
            LibraryDialogListItem item = (LibraryDialogListItem) dialogListItem;
            if (Files.exists(item.getFilePath())) {
                if (LibraryUtil.isJarPath(item.getFilePath()) || Files.isDirectory(item.getFilePath())) {
                    final ImportWindowController iwc = new ImportWindowController(
                            new LibraryPanelController(editorController, preferencesControllerBase.getMavenPreferences()),
                            Arrays.asList(item.getFilePath().toFile()), preferencesControllerBase.getMavenPreferences(),
                            getStage());
                    iwc.setToolStylesheet(editorController.getToolStylesheet());
                    // See comment in OnDragDropped handle set in method startListeningToDrop.
                    AbstractModalDialog.ButtonID userChoice = iwc.showAndWait();
                    if (userChoice == AbstractModalDialog.ButtonID.OK) {
                        logInfoMessage("log.user.maven.updated", item);
                    }
                } else {
//                    if (SceneBuilderApp.getSingleton().lookupUnusedDocumentWindowController() != null) {
//                        closeWindow();
//                    }
//                    SceneBuilderApp.getSingleton().performOpenRecent(documentWindowController,
//                            item.getFilePath().toFile());
                    if (onEditFXML != null) {
                        onEditFXML.accept(item.getFilePath());
                    }
                } 
            }
        } else if (dialogListItem instanceof ArtifactDialogListItem) {
            MavenPreferences mavenPreferences = preferencesControllerBase.getMavenPreferences();
            MavenArtifact mavenArtifact = mavenPreferences
                    .getRecordArtifact(((ArtifactDialogListItem) dialogListItem).getCoordinates())
                    .getMavenArtifact();
            List<File> files = mavenPreferences.getArtifactFileWithDependencies(mavenArtifact);
            List<String> filter = mavenPreferences.getArtifactFilter(mavenArtifact);

            final ImportWindowController iwc = new ImportWindowController(
                        new LibraryPanelController(editorController, preferencesControllerBase.getMavenPreferences()),
                                files, preferencesControllerBase.getMavenPreferences(), getStage(),
                                LibraryLocationEnum.MAVEN_ARTIFACT, filter);
            iwc.setToolStylesheet(editorController.getToolStylesheet());
            AbstractModalDialog.ButtonID userChoice = iwc.showAndWait();
            if (userChoice == AbstractModalDialog.ButtonID.OK) {
                mavenArtifact.setFilter(iwc.getNewExcludedItems());
                updatePreferences(mavenArtifact);
                logInfoMessage("log.user.maven.updated", mavenArtifact.getCoordinates());
            }
        }
    }
    
    public void processWorkspaceJarFXMLFolderEdit(DialogListItem dialogListItem) {
        LibraryDialogListItem item = (LibraryDialogListItem) dialogListItem;
        if (Files.exists(item.getFilePath())) {
            if (LibraryUtil.isJarPath(item.getFilePath()) || Files.isDirectory(item.getFilePath())) {
                final ImportWindowController iwc = new ImportWindowController(
                        new LibraryPanelController(editorController, preferencesControllerBase.getMavenPreferences()),
                        Arrays.asList(item.getFilePath().toFile()), preferencesControllerBase.getMavenPreferences(),
                        getStage(), LibraryLocationEnum.WORKSPACE_LIBRARY, new ArrayList<>());
                iwc.setToolStylesheet(editorController.getToolStylesheet());
                // See comment in OnDragDropped handle set in method startListeningToDrop.
                AbstractModalDialog.ButtonID userChoice = iwc.showAndWait();
                if (userChoice == AbstractModalDialog.ButtonID.OK) {
                    logInfoMessage("log.user.maven.updated", item);
                }
            } else {
//                if (SceneBuilderApp.getSingleton().lookupUnusedDocumentWindowController() != null) {
//                    closeWindow();
//                }
//                SceneBuilderApp.getSingleton().performOpenRecent(documentWindowController,
//                        item.getFilePath().toFile());
                if (onEditFXML != null) {
                    onEditFXML.accept(item.getFilePath());
                }
            } 
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
        final PreferencesRecordArtifact recordArtifact = preferencesControllerBase.
                getRecordArtifact(mavenArtifact);
        recordArtifact.writeToJavaPreferences();

        userLibrary.startWatching();
        
    }

    public void setOnAddJar(Runnable onAddJar) {
        this.onAddJar = onAddJar;
    }

    public void setOnEditFXML(Consumer<Path> onEditFXML) {
        this.onEditFXML = onEditFXML;
    }
    
    public void setOnAddFolder(Runnable onAddFolder) {
        this.onAddFolder = onAddFolder;
    }
    
    public void setOnLinkFolder(Runnable onLinkFolder) {
        this.onLinkFolder = onLinkFolder;
    }
    
    public void setOnLinkJar(Runnable onLinkJar) {
        this.onLinkJar = onLinkJar;
    }
}