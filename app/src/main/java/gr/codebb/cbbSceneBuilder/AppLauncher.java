/*
 * Copyright (c) 2020, codebb.gr and/or its affiliates.
 * Check license.txt for license
 */
/**
 * Launcher Class
 * Upon closer inspection you will notice that the scripts are not creating packages
 * and executables for App (which extends the standard JavaFX Application class) but
 * for AppLauncher. When an Application class gets launched then JavaFX will check
 * whether the JavaFX modules are present on the module path. But since we are placing
 * them on the classpath the application can not launch. As a work-around we are starting
 * a standard Java class with a main method in it. This prevents the module path check
 * and the application will launch just fine.
 */
package gr.codebb.cbbSceneBuilder;

import com.oracle.javafx.scenebuilder.app.SceneBuilderApp;

public class AppLauncher {

    public static void main(String[] args) {
        SceneBuilderApp.main(args);
    }
}
