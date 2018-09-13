java -version

echo "Tag version"
echo %VERSION%

sed -i -e "s/VERSION/%VERSION%/g" app\assets\windows\SceneBuilder-x64.iss

mkdir package\windows
copy app\assets\windows\SceneBuilder-setup-icon.bmp package\windows\SceneBuilder-setup-icon.bmp
copy app\assets\windows\SceneBuilder-x64.iss package\windows\SceneBuilder.iss

"%JAVA_HOME%\bin\javapackager.exe" -createjar^
 -appclass com.oracle.javafx.scenebuilder.app.SceneBuilderApp^
 -nocss2bin^
 -srcfiles app\build\libs\scenebuilder-%VERSION%-all.jar^
 -outdir dist^
 -outfile dist.jar

copy app\src\main\resources\LICENSE dist\LICENSE

"%JAVA_HOME%\bin\javapackager.exe" -deploy^
 -native exe^
 -outdir dist^
 -outfile dist^
 -vendor Gluon^
 -description "Scene Builder"^
 -appclass com.oracle.javafx.scenebuilder.app.SceneBuilderApp^
 -name SceneBuilder^
 -srcdir dist^
 -srcfiles dist.jar^
 -srcfiles dist.jar;LICENSE^
 -BappVersion=%VERSION%^
 -BlicenseFile=LICENSE^
 -Bicon=app\assets\windows\icon-windows.ico

"%signtool%" sign /tr http://timestamp.comodoca.com /td sha256 /fd sha256 /f app\assets\windows\codesign.p12 /p %key_secret% dist\bundles\*.exe"

echo "Bundles directory contains..."
dir dist\bundles\

md install\windows
copy dist\bundles\*.exe install\windows
