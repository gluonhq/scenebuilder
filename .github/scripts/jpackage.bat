for /F %%i in ('%JAVA_HOME%\bin\jdeps --module-path %JAVAFX_HOME% --print-module-deps --ignore-missing-deps app/target/lib/SceneBuilder-%TAG%-all.jar') do SET JDEPS_MODULES=%%i

REM jdeps doesn't include JavaFX modules in Windows
set JAVAFX_MODULES=javafx.fxml,javafx.media,javafx.swing,javafx.web

%JAVA_HOME%\bin\jlink ^
--module-path %JAVAFX_HOME% ^
--add-modules %JDEPS_MODULES%,%JAVAFX_MODULES% ^
--output app/target/runtime ^
--strip-debug --compress 2 --no-header-files --no-man-pages

%JPACKAGE_HOME%\bin\jpackage ^
--app-version %VERSION% ^
--input app/target/lib ^
--license-file LICENSE.txt ^
--main-jar SceneBuilder-%TAG%-all.jar ^
--main-class %MAIN_CLASS% ^
--name SceneBuilder ^
--description "Scene Builder" ^
--vendor Gluon ^
--verbose ^
--runtime-image app/target/runtime ^
--dest app/target ^
--type msi ^
--java-options "--add-opens javafx.fxml/javafx.fxml=ALL-UNNAMED" ^
--icon app/assets/windows/icon-windows.ico ^
--win-dir-chooser ^
--win-menu ^
--win-menu-group "Scene Builder" ^
--win-per-user-install ^
--win-shortcut