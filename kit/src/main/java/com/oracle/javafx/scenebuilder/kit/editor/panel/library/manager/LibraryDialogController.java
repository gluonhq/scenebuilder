/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.ImportWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryUtil;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenDialogController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.RepositoryManagerController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search.SearchMavenDialogController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AbstractModalDialog;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.library.user.UserLibrary;
import com.oracle.javafx.scenebuilder.kit.preferences.MavenPreferences;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferencesRecordArtifact;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
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
    private Hyperlink classesLink;
    
    private final EditorController editorController;
    private final UserLibrary userLibrary;
    private final Stage owner;
    
    private ObservableList<DialogListItem> listItems;

    private Runnable onAddJar;
    private Runnable onAddFolder;
    private Consumer<Path> onEditFXML;

    private final String userM2Repository;

    private final PreferencesControllerBase preferencesControllerBase;
    
    public LibraryDialogController(EditorController editorController, String userM2Repository,
                                   PreferencesControllerBase preferencesController, Stage owner) {
        super(LibraryPanelController.class.getResource("LibraryDialog.fxml"), I18N.getBundle(), owner); //NOI18N
        this.owner = owner;
        this.editorController = editorController;
        this.userLibrary = (UserLibrary) editorController.getLibrary();
        this.userM2Repository = userM2Repository;
        this.preferencesControllerBase = preferencesController;
    }

    @Override
    protected void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();

        this.classesLink.setTooltip(new Tooltip(I18N.getString("library.dialog.hyperlink.tooltip")));
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
        
    }

    void loadLibraryList() {
        if (listItems == null) {
            listItems = FXCollections.observableArrayList();
        }
        listItems.clear();
        
        SortedList<DialogListItem> sortedItems = listItems.sorted(new DialogListItemComparator());
        libraryListView.setItems(sortedItems);
        libraryListView.setCellFactory(param -> new LibraryDialogListCell());
        
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
        
        libraryListView.getSelectionModel().selectFirst();
        libraryListView.requestFocus();
    }

    @FXML
    private void close() {
        listItems.clear();
        closeWindow();
    }

    @FXML
    private void manage() {
        RepositoryManagerController repositoryDialogController = new RepositoryManagerController(editorController,
                userM2Repository, preferencesControllerBase, getStage());
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
                userM2Repository, preferencesControllerBase, getStage());
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
                preferencesControllerBase, getStage());
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
                    false, filter);
            iwc.setToolStylesheet(editorController.getToolStylesheet());
            AbstractModalDialog.ButtonID userChoice = iwc.showAndWait();
            if (userChoice == AbstractModalDialog.ButtonID.OK) {
                mavenArtifact.setFilter(iwc.getNewExcludedItems());
                updatePreferences(mavenArtifact);
                logInfoMessage("log.user.maven.updated", mavenArtifact.getCoordinates());
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
}