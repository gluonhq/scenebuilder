
## Scene Builder Kit ##

To build and install the Scene Builder Kit in your local repository, run:

`./gradlew clean :kit:install`

The custom controls of the Scene Builder kit can be used in your project. 
You can add it as a regular dependency to the build of your app:

```
repositories {
    mavenLocal()
}

dependencies {
    implementation ('com.gluonhq.scenebuilder:scenebuilder-kit:$version') { transitive = false }
}
```


```
--module-path
"C:\android\javafx-sdk-15\lib"
--add-modules
javafx.controls,javafx.fxml
--add-opens
javafx.controls/javafx.scene.control.skin=ALL-UNNAMED
--add-opens
javafx.fxml/javafx.fxml=ALL-UNNAMED
--add-opens
javafx.graphics/javafx.scene=ALL-UNNAMED
--add-opens
javafx.graphics/javafx.css=ALL-UNNAMED
--add-opens
javafx.graphics/com.sun.javafx.css=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.prism=ALL-UNNAMED  
--add-exports
javafx.graphics/com.sun.glass.ui =ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.glass.utils=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.font=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene.traversal=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene.input=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene.layout=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.prism=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.util=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.iio=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.iio.common=ALL-UNNAMED
--add-exports
javafx.base/com.sun.javafx.event=ALL-UNNAMED
--add-exports
javafx.base/com.sun.javafx.logging=ALL-UNNAMED
--add-opens
javafx.controls/javafx.scene.control.skin=ALL-UNNAMED
--add-opens
javafx.graphics/com.sun.javafx.css=ALL-UNNAMED
--add-opens
javafx.controls/javafx.scene.control.skin=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.prism=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.geom.transform=ALL-UNNAMED
--add-exports
javafx.base/com.sun.javafx=ALL-UNNAMED
--add-exports
javafx.base/com.sun.javafx.collections=ALL-UNNAMED
--add-exports
javafx.base/com.sun.javafx.binding=ALL-UNNAMED
--add-exports
javafx.base/com.sun.javafx.runtime=ALL-UNNAMED
--add-exports
javafx.base/com.sun.javafx.reflect=ALL-UNNAMED
--add-exports
javafx.base/com.sun.javafx.beans=ALL-UNNAMED
--add-exports
javafx.control/com.sun.javafx.scene.control=ALL-UNNAMED
```

