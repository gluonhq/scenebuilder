[![Gluon](.github/assets/gluon_logo.svg)](https://gluonhq.com)

# Gluon Scene Builder #

[![Build](https://github.com/gluonhq/scenebuilder/actions/workflows/early-access.yml/badge.svg)](https://github.com/gluonhq/scenebuilder/actions/workflows/early-access.yml)
[![License](https://img.shields.io/badge/license-BSD-green)](./LICENSE)

#### Scene Builder Kit ####

[![Maven Central](https://img.shields.io/maven-central/v/com.gluonhq.scenebuilder/kit)](https://search.maven.org/#search|ga|1|com.gluonhq.scenebuilder)
[![javadoc](https://javadoc.io/badge2/com.gluonhq.scenebuilder/kit/javadoc.svg?color=blue)](https://javadoc.io/doc/com.gluonhq.scenebuilder/kit)

Gluon [Scene Builder](http://gluonhq.com/products/scene-builder/) is a drag and drop UI designer tool allowing rapid desktop and mobile app development.
Scene Builder separates design from logic, allowing team members to quickly and easily focus on their specific aspect of application development.

Scene Builder works with the JavaFX ecosystem – official controls, community projects, and Gluon offerings including
[Gluon Mobile](http://gluonhq.com/products/mobile),
[Gluon Desktop](http://gluonhq.com/products/desktop), and
[Gluon CloudLink](http://gluonhq.com/products/cloudlink).

Scene Builder is open source, and it is freely licensed under the BSD license.
[Gluon](http://gluonhq.com) can provide [custom consultancy](http://gluonhq.com/services/consulting/), [training](http://gluonhq.com/services/training/), and open source [commercial support](http://gluonhq.com/services/commercial-support/).

## Getting started ##

The best way to get started with Gluon Scene Builder is by downloading and installing on your developer machine the latest 
[Scene Builder release](http://gluonhq.com/products/scene-builder/#download).

See the [documentation](http://docs.gluonhq.com/scenebuilder/) about the new features recently included.

For community support, go to [StackOverflow](https://stackoverflow.com/questions/tagged/scenebuilder).

### Requirements on Linux ###

On Linux systems, Scene Builder uses the `xdg-open` command to reveal files in the system's default file system browser or to open URLs in the default web browser. Most modern Linux Desktop Environments already provide the [xdg-utils](https://freedesktop.org/wiki/Software/xdg-utils/) package. If it is missing, it can be installed using the respective Linux package management tool such as `yum`, `apt-get`, `dnf` or `pacman`. The `xdg-utils` package is usually available on KDE based systems, some Arch based systems may require manual installations.

## Issues and Contributions ##

Issues can be reported to the [Issue tracker](https://github.com/gluonhq/scenebuilder/issues/)

Contributions can be submitted via [Pull requests](https://github.com/gluonhq/scenebuilder/pulls/), 
providing you have signed the [Gluon Individual Contributor License Agreement (CLA)](https://cla.gluonhq.com). Please check the [contribution guide](CONTRIBUTING.md) for more details.

## Building Scene Builder ##

### Requisites ###

Gluon Scene Builder is frequently released, and this is only required in case you want to fork and build your local version of Scene Builder.

These are the requisites:

* A recent version of [JDK 21 or later](https://www.oracle.com/technetwork/java/javase/downloads/index.html) for building 'master' branch
* A recent version of [JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) for building '8u-dev' branch

### How to build Scene Builder ###

Scene Builder uses Maven Wrapper to build and run the project. Run `./mvnw` on Linux or macOS and `mvnw` on Windows.

To build the Scene Builder services, on the project's root, run:

`mvn clean package`

Alternatively, utilizing the Maven Wrapper, one can run:

`./mvnw clean package`

It will create a partial shadow cross-platform jar under `app/target/lib/scenebuilder-$version-all.jar`, that doesn't include the JavaFX dependencies.

### How to run Scene Builder ###

Before starting the app, all dependencies must be installed locally.
This is achieved by:

`./mvnw install`

Then Scene Builder can be started with Maven:

`./mvnw javafx:run -f app`

Alternatively, you can run the partial shadow jar in the classpath, providing you have downloaded the JavaFX SDK from [here](https://gluonhq.com/products/javafx/):

```
java \ 
--module-path /path/to/javafx-sdk-$javafxVersion/lib \
--add-modules javafx.web,javafx.fxml,javafx.swing,javafx.media \
--add-opens=javafx.fxml/javafx.fxml=ALL-UNNAMED \
-cp app/target/lib/scenebuilder-$version-all.jar \
com.oracle.javafx.scenebuilder.app.SceneBuilderApp
```

## Scene Builder components ##

The Scene Builder project has three main components defined by three modules (that is, three Java modules defined in three Maven modules subprojects):

- Scene Builder App
- Scene Builder Kit
- Gluon plugin

### Scene Builder App ###

Contains the JavaFX main application that embeds the Scene Builder Kit, and includes menus, preferences and dialogs to interact with it.

### Scene Builder Kit ###

Scene Builder Kit is the core of the project and defines three main areas: 

- Left: Library of custom and built-in controls, Hierarchy and Controller of the FXML layout being edited
- Center: Workspace area for displaying the content of the FXML layout that is being designed
- Right: Inspector with properties, layout and event handlers of the components of the FXML layout.

Scene Builder Kit contains an API that allows these components and their functionality to be integrated in other applications or IDEs. Scene Builder App is the best example of such integration. Another basic example can be found here: [EmbeddedSceneBuilderDemo](https://github.com/gluonhq/EmbeddedSceneBuilderDemo). You can also use the controls available in Scene Builder Kit in your project.

Scene Builder Kit is published to Maven Central, and you can add it as a regular dependency to the build of your app:

```
<dependency>
  <groupId>com.gluonhq.scenebuilder</groupId>
  <artifactId>kit</artifactId>
  <version>$version</version>
</dependency>
```

If you want to build and install the Scene Builder Kit in your local repository, run:

`./mvnw clean install -f kit`

### Gluon plugin ###

The Gluon section in the Library allows adding [Gluon Mobile](http://gluonhq.com/products/mobile) controls to the FXML layout, and setting the stylesheets from the Gluon themes and swatch colors.

An easy way to get started is by selecting the Mobile Basic Screen from the available templates in the welcome dialog.

## Code Style

To ensure that new code formatting matches the requirements for Pull Requests,
the Maven Checkstyle plugin can be used to create a report listing possibly coding 
style violations.

Contributors can check for code-style violations in their code by running the Checkstyle Maven goal. The checkstyle configuration is currently in a very early stage and only checks for empty blocks, extra white space, padding and empty lines.

To run the plugin:

```
./mvnw checkstyle:checkstyle
```

There will be a report for each sub-project:

* Kit: `kit/target/reports/checkstyle.html`
* App: `app/target/reports/checkstyle.html`
* Gluon-plugin: `gluon-plugin/target/reports/checkstyle.html`

This project makes use of [EditorConfig](https://editorconfig.org/) which is [directly supported](https://editorconfig.org/#pre-installed) by IntelliJ IDEA. There are plugins for NetBeans, Eclipse and Visual Studio and [more](https://editorconfig.org/#download). EditorConfig ensures via configuration in `.editorconfig` file, that the proper indentation is used.
