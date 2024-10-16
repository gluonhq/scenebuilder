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
package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.preset.MavenPresets;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.dialog.RepositoryDialogController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
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

    private final EditorController editorController;
    private final Stage owner;

    private ObservableList<RepositoryListItem> listItems;

    private final String userM2Repository;
    private final PreferencesControllerBase preferencesControllerBase;
    
    public RepositoryManagerController(EditorController editorController, String userM2Repository,
                                       PreferencesControllerBase preferencesControllerBase,
                                       Stage owner) {
        super(LibraryPanelController.class.getResource("RepositoryManager.fxml"), I18N.getBundle(), owner); //NOI18N
        this.owner = owner;
        this.editorController = editorController;
        this.userM2Repository = userM2Repository;
        this.preferencesControllerBase = preferencesControllerBase;
    }

    @Override
    protected void controllerDidCreateStage() {
        if (this.owner == null) {
            // Dialog will be application modal
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
        listItems.addAll(preferencesControllerBase.getRepositoryPreferences().getRepositories()
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
        RepositoryDialogController repositoryDialogController = new RepositoryDialogController(editorController,
                userM2Repository, preferencesControllerBase, getStage());
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
        preferencesControllerBase.removeRepository(item.getRepository().getId());
        loadRepositoryList();
    }
    
    private void logInfoMessage(String key, Object... args) {
        editorController.getMessageLog().logInfoMessage(key, I18N.getBundle(), args);
    }
    
}