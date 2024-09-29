# Gluon Scene Builder #

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

Scene Builder makes use of the Maven Wrapper to build and run the project. So there is no need to install Maven on the developers machine. To utilize Maven Wrapper, instead of calling `mvn`, one can run `./mvnw` on Linux or macOS or `mvnw` on Windows instead.

To build the Scene Builder services, on the project's root, run:

`mvn clean package`

Alternatively, utilizing the Maven Wrapper, one can run:

`./mvnw clean package`

It will create a partial shadow cross-platform jar under `app/target/lib/scenebuilder-$version.jar`, that doesn't include the JavaFX dependencies.

### How to run Scene Builder ###

Before starting the app, all dependencies must be installed locally.
This is achieved by:

`mvn install`

Then Scene Builder can be started with Maven:

`mvn javafx:run -f app`

Alternatively, you can run the partial shadow jar, providing you have downloaded the JavaFX SDK from [here](https://gluonhq.com/products/javafx/):

```
java 
--module-path /path/to/javafx-sdk-$javafxVersion/lib \
--add-modules javafx.web,javafx.fxml,javafx.swing,javafx.media \
--add-opens=javafx.fxml/javafx.fxml=ALL-UNNAMED \
-cp app/target/lib/scenebuilder-$version.jar \
com.oracle.javafx.scenebuilder.app.SceneBuilderApp
```

## Scene Builder Kit ##

To build and install the Scene Builder Kit in your local repository, run:

`mvn clean install -f kit`

The custom controls of the Scene Builder kit can be used in your project.
You can add it as a regular dependency to the build of your app:

```
<dependency>
  <groupId>com.gluonhq.scenebuilder</groupId>
  <artifactId>kit</artifactId>
  <version>$version</version>
</dependency>
```

## Code Style

To ensure that new code formatting matches the requirements for Pull Requests,
the Maven Checkstyle plugin can be used to create a report listing possibly coding 
style violations.

Contributors can check for code-style violations in their code by running the Checkstyle Maven goal. The checkstyle configuration is currently in a very early stage and only checks for empty blocks, extra white space, padding and empty lines.

To run the plugin:

```
mvn checkstyle:checkstyle
```

There will be a report for each sub-project, one for `app` and one for `kit`.

* Kit: `kit/target/site/checkstyle.html`
* App: `app/target/site/checkstyle.html`

This project makes use of [EditorConfig](https://editorconfig.org/) which is [directly supported](https://editorconfig.org/#pre-installed) by IntelliJ IDEA. There are plugins for NetBeans, Eclipse and Visual Studio and [more](https://editorconfig.org/#download). EditorConfig ensures via configuration in `.editorconfig` file, that the proper indentation is used.
