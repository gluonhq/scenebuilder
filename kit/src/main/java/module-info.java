module com.oracle.javafx.scenebuilder.kit {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;
    requires javafx.swing;

    requires java.logging;
    requires java.prefs;
    requires java.desktop;
    
    requires com.gluonhq.charm.glisten;
    requires org.apache.maven.resolver;
    requires org.apache.maven.resolver.util;
    requires org.apache.maven.resolver.impl;
    requires org.apache.maven.resolver.connector.basic;
    requires org.apache.maven.resolver.spi;
    requires org.apache.maven.resolver.transport.file;
    requires org.apache.maven.resolver.transport.http;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.commons.codec;
    requires org.apache.commons.lang3;
    requires jakarta.json;
    requires org.apache.commons.io;

    opens com.oracle.javafx.scenebuilder.kit to javafx.fxml;

    exports com.oracle.javafx.scenebuilder.kit;
    exports com.oracle.javafx.scenebuilder.kit.editor;
    exports com.oracle.javafx.scenebuilder.kit.editor.job;
    exports com.oracle.javafx.scenebuilder.kit.editor.job.reference;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog;
    exports com.oracle.javafx.scenebuilder.kit.util;
    exports com.oracle.javafx.scenebuilder.kit.alert;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.css;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.info;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.inspector;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.library;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.library.manager;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.util;
    exports com.oracle.javafx.scenebuilder.kit.editor.util;
    exports com.oracle.javafx.scenebuilder.kit.editor.selection;
    exports com.oracle.javafx.scenebuilder.kit.fxom;
    exports com.oracle.javafx.scenebuilder.kit.library;
    exports com.oracle.javafx.scenebuilder.kit.library.user;
    exports com.oracle.javafx.scenebuilder.kit.preview;
    exports com.oracle.javafx.scenebuilder.kit.selectionbar;
    exports com.oracle.javafx.scenebuilder.kit.editor.search;
    exports com.oracle.javafx.scenebuilder.kit.skeleton;
    exports com.oracle.javafx.scenebuilder.kit.preferences;
    exports com.oracle.javafx.scenebuilder.kit.library.util;
    exports com.oracle.javafx.scenebuilder.kit.template;
    exports com.oracle.javafx.scenebuilder.kit.metadata;
    exports com.oracle.javafx.scenebuilder.kit.util.control.effectpicker;
    exports com.oracle.javafx.scenebuilder.kit.metadata.property;
    exports com.oracle.javafx.scenebuilder.kit.editor.messagelog;
    exports com.oracle.javafx.scenebuilder.kit.metadata.klass;
    exports com.oracle.javafx.scenebuilder.kit.metadata.property.value;
    exports com.oracle.javafx.scenebuilder.kit.metadata.util;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.util;
    exports com.oracle.javafx.scenebuilder.kit.i18n;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors;
    exports com.oracle.javafx.scenebuilder.kit.util.control.paintpicker;

}
