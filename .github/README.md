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

Contributions can be submitted via [Pull requests](https://github.com/gluonhq/scenebuilder/pulls/)


## Building Scene Builder ##

### Requisites ###

Gluon Scene Builder is frequently released, and this is only required in case you want to fork and build your local version of Scene Builder.

These are the requisites:

* A recent version of [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) for building 'master' branch
* A recent version of [JDK 9 or later](http://www.oracle.com/technetwork/java/javase/downloads/index.html) for building 'integration-9' branch

### How to build Scene Builder ###

To build the Scene Builder services, on the project's root, run:

`./gradlew clean build`

It will create an executable jar under `app/build/libs/scenebuilder-$version-all.jar`.
