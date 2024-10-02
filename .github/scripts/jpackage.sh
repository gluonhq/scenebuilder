jdeps_modules=$(jdeps --module-path $JAVAFX_HOME --print-module-deps --ignore-missing-deps app/target/lib/scenebuilder-$APP_VERSION-all.jar)
JAVAFX_MODULES=javafx.fxml,javafx.media,javafx.swing,javafx.web

$JAVA_HOME/bin/jlink \
--module-path $JAVAFX_HOME \
--add-modules $jdeps_modules,$JAVAFX_MODULES \
--output app/target/runtime \
--strip-debug --compress zip-6 --no-header-files --no-man-pages

$JPACKAGE_HOME/bin/jpackage \
--app-version $APP_VERSION \
--input app/target/lib \
--license-file LICENSE.txt \
--main-jar scenebuilder-$APP_VERSION-all.jar \
--main-class $MAIN_CLASS \
--name SceneBuilder \
--description "Scene Builder" \
--vendor Gluon \
--verbose \
--runtime-image app/target/runtime \
--dest $INSTALL_DIR \
"$@"
