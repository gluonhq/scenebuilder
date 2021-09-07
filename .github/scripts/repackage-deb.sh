mkdir -p $INSTALL_DIR/extracted_deb/DEBIAN 
mkdir -p $INSTALL_DIR/repackaged_deb
mkdir -p $INSTALL_DIR/jpackage_deb
dpkg-deb -X $INSTALL_DIR/scenebuilder_$VERSION-1_amd64.deb $INSTALL_DIR/extracted_deb
echo StartupWMClass=com.oracle.javafx.scenebuilder.app.SceneBuilderApp >> $INSTALL_DIR/extracted_deb/opt/scenebuilder/lib/scenebuilder-SceneBuilder.desktop
dpkg-deb -e $INSTALL_DIR/scenebuilder_$VERSION-1_amd64.deb $INSTALL_DIR/extracted_deb/DEBIAN
dpkg-deb -Z xz -b $INSTALL_DIR/extracted_deb $INSTALL_DIR/repackaged_deb

