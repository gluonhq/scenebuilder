for /F %%i in ('%JAVA_HOME%\bin\jdeps --module-path %JAVAFX_HOME% --print-module-deps --ignore-missing-deps %GITHUB_WORKSPACE%/app/target/lib/scenebuilder-%APP_VERSION%-all.jar') do SET JDEPS_MODULES=%%i

REM jdeps doesn't include JavaFX modules in Windows
set JAVAFX_MODULES=javafx.fxml,javafx.media,javafx.swing,javafx.web
REM set MODULES=java.desktop,java.logging,java.naming,java.prefs,java.security.jgss,java.sql,java.xml,javafx.fxml,javafx.media,javafx.swing,javafx.web,jdk.unsupported

%JAVA_HOME%\bin\jlink ^
--module-path %JAVAFX_HOME% ^
--add-modules %JDEPS_MODULES%,%JAVAFX_MODULES% ^
--output app/target/runtime ^
--strip-debug --compress zip-6 --no-header-files --no-man-pages

%JPACKAGE_HOME%\bin\jpackage ^
--app-version %APP_VERSION% ^
--input app/target/lib ^
--license-file LICENSE.txt ^
--main-jar scenebuilder-%APP_VERSION%-all.jar ^
--main-class %MAIN_CLASS% ^
--name SceneBuilder ^
--description "Scene Builder" ^
--vendor Gluon ^
--verbose ^
--runtime-image app/target/runtime ^
--dest %INSTALL_DIR% ^
--type msi ^
--java-options "-Djavafx.allowjs=true" ^
--java-options "--add-opens=javafx.fxml/javafx.fxml=ALL-UNNAMED" ^
--java-options "--enable-native-access=javafx.graphics" ^
--java-options "--sun-misc-unsafe-memory-access=allow" ^
--java-options "-Djava.library.path=runtime\bin;runtime\lib" ^
--icon app/assets/windows/icon-windows.ico ^
--win-dir-chooser ^
--win-menu ^
--win-menu-group "Scene Builder" ^
--win-per-user-install ^
--win-shortcut
