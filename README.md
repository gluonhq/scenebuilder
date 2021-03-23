# Gluon Scene Builder #

Gluon [Scene Builder](http://gluonhq.com/products/scene-builder/) is a drag and drop UI designer tool allowing rapid desktop and mobile app 
development. Scene Builder separates design from logic, allowing team members to quickly and easily focus on their specific aspect of 
application development.

Scene Builder works with the JavaFX ecosystem – official controls, community projects, and Gluon offerings including 
[Gluon Mobile](http://gluonhq.com/products/mobile), [Gluon Desktop](http://gluonhq.com/products/desktop), and [Gluon CloudLink](http://gluonhq.com/products/cloudlink).

Scene Builder is open source, and it is freely licensed under the BSD license.
[Gluon](http://gluonhq.com) can provide [custom consultancy](http://gluonhq.com/services/consulting/) and [training](http://gluonhq.com/services/training/), and open source [commercial support](http://gluonhq.com/services/commercial-support/).

## Getting started ##

The best way to get started with Gluon Scene Builder is by downloading and installing on your developer machine the latest 
[Scene Builder release](http://gluonhq.com/products/scene-builder/#download).

See the [documentation](http://docs.gluonhq.com/scenebuilder/) about the new features recently included.

For community support, go to [StackOverflow](https://stackoverflow.com/questions/tagged/scenebuilder).

## Issues and Contributions ##

Issues can be reported to the [Issue tracker](https://github.com/gluonhq/scenebuilder/issues/)

Contributions can be submitted via [Pull requests](https://github.com/gluonhq/scenebuilder/pulls/), 
providing you have signed the [Gluon Individual Contributor License Agreement (CLA)](https://docs.google.com/forms/d/16aoFTmzs8lZTfiyrEm8YgMqMYaGQl0J8wA0VJE2LCCY).

## Building Scene Builder ##

### Requisites ###

Gluon Scene Builder is frequently released, and this is only required in case you want to fork and build your local version of Scene Builder.

These are the requisites:

* A recent version of [JDK 11 or later](https://www.oracle.com/technetwork/java/javase/downloads/index.html) for building 'master' branch
* A recent version of [JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) for building '8u-dev' branch

### How to build Scene Builder ###

To build the Scene Builder services, on the project's root, run:

`./gradlew clean build`

It will create a partial shadow cross-platform jar under `app/build/libs/scenebuilder-$version.jar`, that doesn't include the JavaFX dependencies.

### How to run Scene Builder ###

You can run it with Gradle:

`./gradlew run`

or you can run the partial shadow jar, providing you have downloaded the JavaFX SDK from [here](https://gluonhq.com/products/javafx/):

`java --module-path /path/to/javafx-sdk-$javafxVersion/lib --add-modules javafx.web,javafx.fxml,javafx.swing,javafx.media --add-opens=javafx.fxml/javafx.fxml=ALL-UNNAMED -cp app/build/libs/scenebuilder-$version.jar com.oracle.javafx.scenebuilder.app.SceneBuilderApp                                                           

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
