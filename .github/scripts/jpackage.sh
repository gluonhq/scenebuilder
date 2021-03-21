echo ">>>>>>>>>>>>>>"
echo "$@"

jdeps_modules=$(jdeps --module-path $JAVAFX_HOME --print-module-deps --ignore-missing-deps $GITHUB_WORKSPACE/app/target/lib/SceneBuilder-$TAG-all.jar)
$JAVA_HOME/bin/jlink \
--module-path $JAVAFX_HOME \
--add-modules $jdeps_modules \
--output app/target/runtime \
--strip-debug --compress 2 --no-header-files --no-man-pages

$JPACKAGE_HOME/bin/jpackage \
--app-version $VERSION \
--input $GITHUB_WORKSPACE/app/target/lib \
--install-dir /opt \
--license-file LICENSE.txt \
--main-jar SceneBuilder-$TAG-all.jar \
--main-class $MAIN_CLASS \
--name SceneBuilder \
--description "Scene Builder" \
--vendor Gluon \
--verbose \
--runtime-image $GITHUB_WORKSPACE/app/target/runtime \
--dest $GITHUB_WORKSPACE/app/target \
"$@"