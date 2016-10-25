package com.oracle.javafx.scenebuilder.app;

import com.oracle.javafx.scenebuilder.app.i18n.I18N;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal;
import com.oracle.javafx.scenebuilder.app.util.SBSettings;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

public class UpdateSceneBuilderDialog extends Dialog {

    public UpdateSceneBuilderDialog() {
        setTitle(I18N.getString("download_scene_builder.title"));
        Label header = new Label(I18N.getString("download_scene_builder.header.label"));
        Label currentVersionTextLabel = new Label(I18N.getString("download_scene_builder.current_version.label"));
        Label latestVersionTextLabel = new Label(I18N.getString("download_scene_builder.last_version_number.label"));
        Label currentVersionLabel = new Label(SBSettings.getSceneBuilderVersion());
        Label latestVersionLabel = new Label(SBSettings.getLatestVersion());
        GridPane gridPane = new GridPane();
        gridPane.add(currentVersionTextLabel, 0, 0);
        gridPane.add(currentVersionLabel, 1, 0);
        gridPane.add(latestVersionTextLabel, 0, 1);
        gridPane.add(latestVersionLabel, 1, 1);
        gridPane.getColumnConstraints().add(new ColumnConstraints(100));
        VBox contentContainer = new VBox();
        contentContainer.getChildren().addAll(header, gridPane);
        BorderPane mainContainer = new BorderPane();
        mainContainer.setCenter(contentContainer);
        ImageView imageView = new ImageView(UpdateSceneBuilderDialog.class.getResource("417792 - computer download.png").toExternalForm());
        mainContainer.setRight(imageView);

        getDialogPane().setContent(mainContainer);

        mainContainer.getStyleClass().add("main-container");
        contentContainer.getStyleClass().add("content-container");
        getDialogPane().getStyleClass().add("download_scenebuilder-dialog");
        header.getStyleClass().add("header");

        ButtonType downloadButton = new ButtonType(I18N.getString("download_scene_builder.download.label"), ButtonBar.ButtonData.OK_DONE);
        ButtonType ignoreThisUpdate = new ButtonType(I18N.getString("download_scene_builder.ignore.label"));
        ButtonType remindLater = new ButtonType(I18N.getString("download_scene_builder.remind_later.label"), ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(downloadButton, ignoreThisUpdate, remindLater);

        getDialogPane().getStylesheets().add(SceneBuilderApp.class.getResource("css/UpdateSceneBuilderDialog.css").toString());

        resultProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == downloadButton) {
                URI uri = null;
                try {
                    uri = new URI("http://gluonhq.com/labs/scene-builder/#download");
                    Desktop.getDesktop().browse(uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (newValue == remindLater) {
                LocalDate now = LocalDate.now();
                LocalDate futureDate = now.plusWeeks(1);
                PreferencesController pc = PreferencesController.getSingleton();
                PreferencesRecordGlobal recordGlobal = pc.getRecordGlobal();
                recordGlobal.setShowUpdateDialogAfter(futureDate);
            } else if (newValue == ignoreThisUpdate) {
                String latestVersion = SBSettings.getLatestVersion();
                PreferencesController pc = PreferencesController.getSingleton();
                PreferencesRecordGlobal recordGlobal = pc.getRecordGlobal();
                recordGlobal.setIgnoreVersion(latestVersion);
            }
        });
    }
}
