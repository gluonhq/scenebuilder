#!/bin/bash

chmod +x .ci/osx-add-key.sh
sh .ci/osx-add-key.sh

export JAVA_HOME=$(/usr/libexec/java_home)
echo $JAVA_HOME

${JAVA_HOME}/bin/java -version

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
 -Bicon=app/assets/osx/icon-mac.icns \
 -BlicenseFile=LICENSE \
 -BlicenseType=BSD \
 -Bcopyright="Copyright (c) 2012, 2014, Oracle and/or its affiliates, 2016, 2018, Gluon." \
 -Bmac.category=Education \
 -Bmac.CFBundleIdentifier=com.gluonhq.scenebuilder \
 -Bmac.CFBundleName="Scene Builder" \
 -Bmac.CFBundleVersion=${VERSION} \
 -Bmac.signing-key-developer-id-app="Developer ID Application: Gluon Software BVBA (S7ZR395D8U)" \
 -Bmac.bundle-id-signing-prefix=S7ZR395D8U
