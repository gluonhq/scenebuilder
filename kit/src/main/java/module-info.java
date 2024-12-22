/*
 * Copyright (c) 2024, Gluon and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

module com.gluonhq.scenebuilder.kit {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires javafx.swing;
    requires javafx.media;
    requires transitive javafx.web;
    requires java.logging;

    requires java.net.http;
    requires static jakarta.json;
    requires transitive static java.prefs;

    requires static org.apache.maven.resolver;
    requires static org.apache.maven.resolver.spi;
    requires static org.apache.maven.resolver.impl;
    requires static org.apache.maven.resolver.supplier;
    requires static org.apache.maven.resolver.util;

    opens com.oracle.javafx.scenebuilder.kit to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.alert;
    opens com.oracle.javafx.scenebuilder.kit.css;
    opens com.oracle.javafx.scenebuilder.kit.editor;
    opens com.oracle.javafx.scenebuilder.kit.editor.drag to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.drag.source to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.drag.target to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.images to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.job to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.job.atomic to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.job.gridpane to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.job.gridpane.v2 to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.job.reference to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.job.togglegroup to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.job.wrap to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.messagelog to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.curve to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.gridpane to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.outline to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.pring to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.relocater to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.rudder to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.tring to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.key to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.mode to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.content.util to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.css to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.treeview to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.info to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.inspector to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors.util to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.popupeditors to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.library to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.library.manager to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.preset to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.dialog to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.util to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.report to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.search to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.selection to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.editor.util to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.fxom to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.fxom.glue to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.fxom.sampledata to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.glossary to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.i18n to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.i18n.spi to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.library to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.library.user to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.library.util to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.metadata;
    opens com.oracle.javafx.scenebuilder.kit.metadata.klass to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.metadata.property to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.metadata.property.value to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.metadata.util to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.preferences to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.preview to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.selectionbar to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.skeleton to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.template;
    opens com.oracle.javafx.scenebuilder.kit.util to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.util.control.effectpicker to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.util.control.effectpicker.editors to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.util.control.paintpicker to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.colorpicker to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.gradientpicker to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.rotator to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.slider to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.kit.util.eventnames to javafx.fxml;

    provides com.oracle.javafx.scenebuilder.kit.i18n.spi.I18NResourcesProvider with com.oracle.javafx.scenebuilder.kit.i18n.spi.I18NResourcesProviderImpl;
    uses com.oracle.javafx.scenebuilder.kit.editor.ExternalThemeProvider;
    uses com.oracle.javafx.scenebuilder.kit.library.ExternalSectionProvider;
    uses com.oracle.javafx.scenebuilder.kit.metadata.ExternalMetadataProvider;
    uses com.oracle.javafx.scenebuilder.kit.metadata.util.ExternalDesignHierarchyMaskProvider;

    exports com.oracle.javafx.scenebuilder.kit;
    exports com.oracle.javafx.scenebuilder.kit.alert;
    exports com.oracle.javafx.scenebuilder.kit.editor;
    exports com.oracle.javafx.scenebuilder.kit.editor.drag;
    exports com.oracle.javafx.scenebuilder.kit.editor.drag.source;
    exports com.oracle.javafx.scenebuilder.kit.editor.drag.target;
    exports com.oracle.javafx.scenebuilder.kit.editor.images;
    exports com.oracle.javafx.scenebuilder.kit.editor.job;
    exports com.oracle.javafx.scenebuilder.kit.editor.job.atomic;
    exports com.oracle.javafx.scenebuilder.kit.editor.job.gridpane;
    exports com.oracle.javafx.scenebuilder.kit.editor.job.gridpane.v2;
    exports com.oracle.javafx.scenebuilder.kit.editor.job.reference;
    exports com.oracle.javafx.scenebuilder.kit.editor.job.togglegroup;
    exports com.oracle.javafx.scenebuilder.kit.editor.job.wrap;
    exports com.oracle.javafx.scenebuilder.kit.editor.messagelog;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.curve;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.gridpane;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.outline;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.pring;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.relocater;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.rudder;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.tring;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.key;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.mode;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.util;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.css;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.treeview;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.info;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.inspector;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors.util;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.popupeditors;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.library;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.library.manager;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.preset;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.dialog;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.util;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog;
    exports com.oracle.javafx.scenebuilder.kit.editor.report;
    exports com.oracle.javafx.scenebuilder.kit.editor.search;
    exports com.oracle.javafx.scenebuilder.kit.editor.selection;
    exports com.oracle.javafx.scenebuilder.kit.editor.util;
    exports com.oracle.javafx.scenebuilder.kit.fxom;
    exports com.oracle.javafx.scenebuilder.kit.fxom.glue;
    exports com.oracle.javafx.scenebuilder.kit.fxom.sampledata;
    exports com.oracle.javafx.scenebuilder.kit.glossary;
    exports com.oracle.javafx.scenebuilder.kit.i18n;
    exports com.oracle.javafx.scenebuilder.kit.i18n.spi;
    exports com.oracle.javafx.scenebuilder.kit.library;
    exports com.oracle.javafx.scenebuilder.kit.library.user;
    exports com.oracle.javafx.scenebuilder.kit.library.util;
    exports com.oracle.javafx.scenebuilder.kit.metadata;
    exports com.oracle.javafx.scenebuilder.kit.metadata.klass;
    exports com.oracle.javafx.scenebuilder.kit.metadata.property;
    exports com.oracle.javafx.scenebuilder.kit.metadata.property.value;
    exports com.oracle.javafx.scenebuilder.kit.metadata.property.value.list;
    exports com.oracle.javafx.scenebuilder.kit.metadata.property.value.effect;
    exports com.oracle.javafx.scenebuilder.kit.metadata.property.value.effect.light;
    exports com.oracle.javafx.scenebuilder.kit.metadata.property.value.paint;
    exports com.oracle.javafx.scenebuilder.kit.metadata.property.value.keycombination;
    exports com.oracle.javafx.scenebuilder.kit.metadata.util;
    exports com.oracle.javafx.scenebuilder.kit.preferences;
    exports com.oracle.javafx.scenebuilder.kit.preview;
    exports com.oracle.javafx.scenebuilder.kit.selectionbar;
    exports com.oracle.javafx.scenebuilder.kit.skeleton;
    exports com.oracle.javafx.scenebuilder.kit.template;
    exports com.oracle.javafx.scenebuilder.kit.util;
    exports com.oracle.javafx.scenebuilder.kit.util.control.effectpicker;
    exports com.oracle.javafx.scenebuilder.kit.util.control.effectpicker.editors;
    exports com.oracle.javafx.scenebuilder.kit.util.control.paintpicker;
    exports com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.colorpicker;
    exports com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.gradientpicker;
    exports com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.rotator;
    exports com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.slider;
    exports com.oracle.javafx.scenebuilder.kit.util.eventnames;
}