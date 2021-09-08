/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
import com.oracle.javafx.scenebuilder.kit.util.ReturningRunnable;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
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

    private ReturningRunnable<Boolean> onAddJar;
    private ReturningRunnable<Boolean> onAddFolder;
    private Consumer<Path> onEditFXML;

    private String userM2Repository;
    private String tempM2Repository;
    private SimpleBooleanProperty changedProperty;

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
        this.changedProperty = new SimpleBooleanProperty(false);
    }

    @Override
    protected void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();

        this.classesLink.setTooltip(new Tooltip(I18N.getString("library.dialog.hyperlink.tooltip")));

        userLibrary.stopExplorer(); // we stop an eventually running explorer before letting the user do something on this dialog.
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
        loadLibraryList(false);
    }
    
    void loadLibraryList(boolean changed) {
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
        
        changedProperty.set(changed);
    }

    @FXML
    private void close() {
        if (changedProperty.get())
            userLibrary.startExplorer();
        
        listItems.clear();
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
            Boolean added = onAddJar.run();
            if (Boolean.TRUE.equals(added))
                loadLibraryList(true);
        }
    }
    
    @FXML
    private void addFolder() {
        if (onAddFolder != null) {
            Boolean added = onAddFolder.run();
            if (Boolean.TRUE.equals(added))
                loadLibraryList(true);
        }
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
                    if (mavenDialogController.isConfirmed())
                        loadLibraryList(true);
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
                    if (mavenDialogController.isConfirmed())
                        loadLibraryList(true);
                    mavenDialogController.getStage().showingProperty().removeListener(this);
                }
            }
        });
    }
     
    /*
     * We can simply delete the item since the library explorer is shut down when opening the dialog.
     */
    public void processJarFXMLFolderDelete(DialogListItem dialogListItem) {
        deleteFile(dialogListItem);
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
        loadLibraryList(true);
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
                        changedProperty.set(true);
                    }
                } else {
//                    if (SceneBuilderApp.getSingleton().lookupUnusedDocumentWindowController() != null) {
//                        closeWindow();
//                    }
//                    SceneBuilderApp.getSingleton().performOpenRecent(documentWindowController,
//                            item.getFilePath().toFile());
                    if (onEditFXML != null) {
                        onEditFXML.accept(item.getFilePath());
                        changedProperty.set(true);
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
                changedProperty.set(true);
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
        
        // Update record artifact
        final PreferencesRecordArtifact recordArtifact = preferencesControllerBase.
                getRecordArtifact(mavenArtifact);
        recordArtifact.writeToJavaPreferences();
    }

    public void setOnAddJar(ReturningRunnable<Boolean> onAddJar) {
        this.onAddJar = onAddJar;
    }

    public void setOnEditFXML(Consumer<Path> onEditFXML) {
        this.onEditFXML = onEditFXML;
    }
    
    public void setOnAddFolder(ReturningRunnable<Boolean> onAddFolder) {
        this.onAddFolder = onAddFolder;
    }
}