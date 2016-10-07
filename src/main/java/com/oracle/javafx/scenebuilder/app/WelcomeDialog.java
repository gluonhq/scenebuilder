package com.oracle.javafx.scenebuilder.app;

import com.oracle.javafx.scenebuilder.app.i18n.I18N;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
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

import java.util.Arrays;
import java.util.List;

public class WelcomeDialog extends Dialog {
    private static final String NEW_DESKTOP_PROJECT_ICON = WelcomeDialog.class.getResource("363991 - earth global solu.png").toString();
    private static final String NEW_DESKTOP_AND_MOBILE_PROJ_ICON = WelcomeDialog.class.getResource("417750 - check list device.png").toString();
    private static final String OPEN_PROJECT_ICON = WelcomeDialog.class.getResource("open_document.png").toString();

    SceneBuilderApp sceneBuilderApp;

    public WelcomeDialog() {
        sceneBuilderApp = SceneBuilderApp.getSingleton();

        setTitle(I18N.getString("welcome.title"));
        // We want an empty header text but we don't want the graphic to go to the left which is what happens
        // if you don't provide a header text.
        setHeaderText(" ");
        setGraphic(new ImageView(SceneBuilderApp.class.getResource("welcome_screen.png").toString()));

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
        Hyperlink desktopAndMobileProj = new Hyperlink(I18N.getString("welcome.desktopandmobile.label"));
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
        imageView = new ImageView(NEW_DESKTOP_AND_MOBILE_PROJ_ICON);
        imageViewHBox = new HBox(imageView);
        imageViewHBox.setAlignment(Pos.CENTER_LEFT);
        desktopAndMobileProjContainer.getChildren().addAll(imageViewHBox, desktopAndMobileProj);
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
            String[] recentItemPath = recentItem.split("\\\\");
            String recentItemTitle = recentItemPath[recentItemPath.length - 1].split("\\.")[0];
            Hyperlink titleLabel = new Hyperlink(recentItemTitle);
            recentItemsOptions.getChildren().add(titleLabel);

            titleLabel.getStyleClass().add("recent-item-title");
            titleLabel.setOnAction(event -> fireOpenRecentProject(event, recentItem));
            titleLabel.setTooltip(new Tooltip(recentItem));
        }
        if (recentItems.size() > 8) {
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
}
