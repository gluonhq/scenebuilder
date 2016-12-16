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

package com.oracle.javafx.scenebuilder.app;

import com.oracle.javafx.scenebuilder.app.i18n.I18N;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.util.SBSettings;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class WelcomeDialog extends Dialog {
    private static final String HEADER_IMAGE = WelcomeDialog.class.getResource("gluon_scene_builder.png").toString();

    private static final String NEW_DESKTOP_PROJECT_ICON = WelcomeDialog.class.getResource("earthGlobalSolu.png").toString();
//    private static final String NEW_DESKTOP_AND_MOBILE_PROJ_ICON = WelcomeDialog.class.getResource("checkListDevice.png").toString();
    private static final String OPEN_PROJECT_ICON = WelcomeDialog.class.getResource("open_document.png").toString();

    private static final int NUMBER_OF_ITEMS_WITHOUT_SCROLLPANE = 8;

    private static WelcomeDialog instance;

    SceneBuilderApp sceneBuilderApp;

    private WelcomeDialog() {
        sceneBuilderApp = SceneBuilderApp.getSingleton();

        setTitle(I18N.getString("welcome.title"));
        // We want an empty header text but we don't want the graphic to go to the left which is what happens
        // if you don't provide a header text.
        setHeaderText(" ");
        setGraphic(new ImageView(HEADER_IMAGE));

        HBox mainContainer = new HBox();
        VBox actionsContainer = new VBox();
        VBox recentItemsContainer = new VBox();
        mainContainer.getChildren().addAll(recentItemsContainer, new Separator(Orientation.VERTICAL), actionsContainer);

        actionsContainer.getStyleClass().add("actions-container");
        recentItemsContainer.getStyleClass().add("recent-items-container");
        mainContainer.getStyleClass().add("main-container");

        Label recentItemsTitle = new Label(I18N.getString("welcome.recentitems.header"));
        recentItemsContainer.getChildren().add(recentItemsTitle);

        Label newProjectTitle = new Label(I18N.getString("welcome.actions.header"));
        actionsContainer.getChildren().add(newProjectTitle);
        Hyperlink desktopProject = new Hyperlink(I18N.getString("welcome.desktopproject.label"));
//        Hyperlink desktopAndMobileProj = new Hyperlink(I18N.getString("welcome.desktopandmobile.label"));
        Hyperlink openExistingProj = new Hyperlink(I18N.getString("welcome.openproject.label"));

        desktopProject.setOnAction(this::fireNewDesktopProject);
        openExistingProj.setOnAction(this::fireOpenProject);

        HBox desktopProjectContainer = new HBox();
        HBox desktopAndMobileProjContainer = new HBox();
        HBox openExistingProjContainer = new HBox();
        desktopProjectContainer.getStyleClass().add("action-option-container");
        desktopAndMobileProjContainer.getStyleClass().add("action-option-container");
        openExistingProjContainer.getStyleClass().add("action-option-container");
        ImageView imageView = new ImageView(NEW_DESKTOP_PROJECT_ICON);
        HBox imageViewHBox = new HBox(imageView);
        imageViewHBox.setAlignment(Pos.CENTER_LEFT);
        desktopProjectContainer.getChildren().addAll(imageViewHBox, desktopProject);
        /*imageView = new ImageView(NEW_DESKTOP_AND_MOBILE_PROJ_ICON);
        imageViewHBox = new HBox(imageView);
        imageViewHBox.setAlignment(Pos.CENTER_LEFT);
        desktopAndMobileProjContainer.getChildren().addAll(imageViewHBox, desktopAndMobileProj);*/
        imageView = new ImageView(OPEN_PROJECT_ICON);
        imageViewHBox = new HBox(imageView);
        imageViewHBox.setAlignment(Pos.CENTER_LEFT);
        openExistingProjContainer.getChildren().addAll(imageViewHBox, openExistingProj);
        VBox actionOptions = new VBox();
        actionOptions.getChildren().addAll(desktopProjectContainer, desktopAndMobileProjContainer, openExistingProjContainer);

        actionsContainer.getChildren().add(actionOptions);

        List<String> recentItems = PreferencesController.getSingleton().getRecordGlobal().getRecentItems();

        VBox recentItemsOptions = new VBox();
        recentItemsOptions.getStyleClass().add("recent-items-options");
        if (recentItems.size() == 0) {
            recentItemsOptions.getChildren().add(new Label(I18N.getString("welcome.recentitems.empty")));
        }
        for (int row = 0; row < PreferencesController.getSingleton().getRecordGlobal().getRecentItemsSize(); ++row) {
            if (recentItems.size() < row + 1) {
                break;
            }

            String recentItem = recentItems.get(row);
            File recentItemFile = new File(recentItems.get(row));
            String recentItemTitle = recentItemFile.getName();
            Hyperlink titleLabel = new Hyperlink(recentItemTitle);
            recentItemsOptions.getChildren().add(titleLabel);

            titleLabel.getStyleClass().add("recent-item-title");
            titleLabel.setOnAction(event -> fireOpenRecentProject(event, recentItem));
            titleLabel.setTooltip(new Tooltip(recentItem));
        }
        if (recentItems.size() > NUMBER_OF_ITEMS_WITHOUT_SCROLLPANE) {
            ScrollPane scrollPane = new ScrollPane(recentItemsOptions);
            VBox scrollPaneContainer = new VBox(scrollPane);
            scrollPaneContainer.getStyleClass().add("scroll-pane-container");
            recentItemsContainer.getChildren().add(scrollPaneContainer);
        } else {
            recentItemsContainer.getChildren().add(recentItemsOptions);
        }

        ButtonType closeButton = new ButtonType(I18N.getString("welcome.close"), ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(closeButton);
        getDialogPane().setContent(mainContainer);

        getDialogPane().getStyleClass().add("welcome-dialog");
        recentItemsTitle.getStyleClass().add("header");
        newProjectTitle.getStyleClass().add("header");
        actionOptions.getStyleClass().add("actions-options");


        getDialogPane().getStylesheets().add(SceneBuilderApp.class.getResource("css/WelcomeScreen.css").toString());
    }

    private void fireNewDesktopProject(ActionEvent event) {
        close();
    }

    private void fireOpenProject(ActionEvent event) {
        // Right now there is only one window open by default
        DocumentWindowController documentWC = sceneBuilderApp.getDocumentWindowControllers().get(0);
        sceneBuilderApp.performControlAction(SceneBuilderApp.ApplicationControlAction.OPEN_FILE, documentWC);
        close();
    }

    private void fireOpenRecentProject(ActionEvent event, String projectPath) {
        sceneBuilderApp.handleOpenFilesAction(Arrays.asList(projectPath));
        close();
    }

    public static WelcomeDialog getInstance() {
        if (instance == null){
            instance = new WelcomeDialog();
            SBSettings.setWindowIcon((Stage)instance.getDialogPane().getScene().getWindow());
        }
        return instance;
    }
}
