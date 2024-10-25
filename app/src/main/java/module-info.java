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

module com.gluonhq.scenebuilder.app {
    requires javafx.web;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.swing;
    requires transitive com.gluonhq.scenebuilder.kit;
    requires transitive com.gluonhq.scenebuilder.gluon.plugin;
    requires java.logging;
    requires java.prefs;
    requires jakarta.json;
    requires org.apache.maven.resolver;
    requires org.apache.maven.resolver.spi;
    requires org.apache.maven.resolver.impl;
    requires org.apache.maven.resolver.supplier;
    requires org.apache.maven.resolver.util;

    opens com.oracle.javafx.scenebuilder.app to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.app.about to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.app.i18n to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.app.menubar to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.app.message to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.app.preferences to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.app.registration to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.app.report to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.app.tracking to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.app.util to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.app.welcomedialog;

    uses com.oracle.javafx.scenebuilder.kit.i18n.spi.I18NResourcesProvider;

    exports com.oracle.javafx.scenebuilder.app;
    exports com.oracle.javafx.scenebuilder.app.menubar;
    exports com.oracle.javafx.scenebuilder.app.preferences;
}