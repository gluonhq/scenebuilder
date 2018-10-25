#!/bin/bash

java -version

sudo apt-get update
sudo apt-get install fakeroot # deb
sudo apt-get install rpm      # rpm

${JAVA_HOME}/bin/javapackager -createjar -v \
 -appclass com.oracle.javafx.scenebuilder.app.SceneBuilderApp \
 -nocss2bin \
 -srcfiles app/build/libs/scenebuilder-${VERSION}-all.jar \
 -outdir dist \
 -outfile dist.jar

cp app/src/main/resources/LICENSE dist

${JAVA_HOME}/bin/javapackager -deploy -v \
 -native \
 -outdir dist \
 -outfile dist \
 -vendor Gluon \
 -description "Scene Builder" \
 -appclass com.oracle.javafx.scenebuilder.app.SceneBuilderApp \
 -name SceneBuilder \
 -srcdir dist \
 -srcfiles dist.jar:LICENSE \
 -BappVersion=${VERSION} \
 -Bcategory=Development \
 -Bemail=support@gluonhq.com \
 -Bicon=app/assets/linux/icon-linux.png \
 -BlicenseFile=LICENSE \
 -BlicenseType=BSD \
 -Bcopyright="Copyright (c) 2012, 2014, Oracle and/or its affiliates, 2016, Gluon."

echo "Linux packager successfully created"

# Rename bundles
mv dist/bundles/*-${VERSION}.deb dist/bundles/SceneBuilder-${VERSION}.deb
mv dist/bundles/*-${VERSION}-*.rpm dist/bundles/SceneBuilder-${VERSION}.rpm

echo "Bundles directory contains..."
ls dist/bundles/
