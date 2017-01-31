/*
 * Copyright (c) 2016, Gluon and/or its affiliates.
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

import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.images.ImageUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Model for the list cell in the JAR/FXML Library dialog.
 */
public class RepositoryManagerListCell extends ListCell<RepositoryListItem> {

    private RepositoryListItem listItem;

    public RepositoryManagerListCell() {
        super();
    }

    @Override
    public void updateItem(RepositoryListItem item, boolean empty) {
        this.listItem = item;

        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setPrefWidth(0);
            setGraphic(createCellContent());
        }
    }

    private HBox createCellContent() {
        HBox cellContent = new HBox();
        cellContent.setAlignment(Pos.CENTER_LEFT);
        String name = listItem.getRepository().getId();
        Label fileName = new Label(name);
        fileName.setTooltip(new Tooltip(name + " [" + listItem.getRepository().getURL() + "]"));
        HBox.setHgrow(fileName, Priority.ALWAYS);
        HBox buttonContent = createButtonCellContent();
        HBox.setHgrow(buttonContent, Priority.ALWAYS);
        cellContent.getChildren().addAll(fileName, buttonContent);
        return cellContent;
    }

    private HBox createButtonCellContent() {
        HBox buttonContent = new HBox();
        buttonContent.setAlignment(Pos.CENTER_RIGHT);
        buttonContent.setSpacing(5);
        if (listItem instanceof CustomRepositoryListItem) {
            Button editButton = new Button("", new ImageView(ImageUtils.getEditIconImage()));
            editButton.getStyleClass().add("image-view-button");
            editButton.setOnMouseClicked(event -> listItem.getRepositoryManagerController().edit(listItem));
            editButton.setTooltip(new Tooltip(I18N.getString("repository.manager.button.edit.tooltip")));
            Button deleteButton = new Button("", new ImageView(ImageUtils.getDeleteIconImage()));
            deleteButton.setOnMouseClicked(event -> listItem.getRepositoryManagerController().delete(listItem));
            deleteButton.getStyleClass().add("image-view-button");
            deleteButton.setTooltip(new Tooltip(I18N.getString("repository.manager.button.delete.tooltip")));
            buttonContent.getChildren().addAll(editButton, deleteButton);
        } 
        return buttonContent;
    }
}