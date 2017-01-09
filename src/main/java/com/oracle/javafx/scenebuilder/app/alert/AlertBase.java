package com.oracle.javafx.scenebuilder.app.alert;

import com.oracle.javafx.scenebuilder.app.SceneBuilderApp;
import com.oracle.javafx.scenebuilder.app.util.SBSettings;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * All SB alerts should extend from this class to have a consistent look and feel
 */
public class AlertBase extends Alert {

    public AlertBase(AlertType alertType) {
        super(alertType);

        getDialogPane().getStyleClass().add("SB-alert");
        getDialogPane().getStylesheets().add(SceneBuilderApp.class.getResource("css/Alert.css").toString());

        SBSettings.setWindowIcon((Stage)getDialogPane().getScene().getWindow());
    }

}
