package com.oracle.javafx.scenebuilder.app;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SBSettings {
    public static final String APP_ICON_16 = SceneBuilderApp.class.getResource("SceneBuilderLogo_16.png").toString();
    public static final String APP_ICON_32 = SceneBuilderApp.class.getResource("SceneBuilderLogo_32.png").toString();

    public static void setWindowIcon(Stage stage) {
        Image icon16 = new Image(SBSettings.APP_ICON_16);
        Image icon32 = new Image(SBSettings.APP_ICON_32);
        stage.getIcons().addAll(icon16, icon32);
    }
}
