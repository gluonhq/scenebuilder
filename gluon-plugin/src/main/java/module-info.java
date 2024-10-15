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
module com.gluonhq.scenebuilder.gluon.plugin {
    requires transitive com.gluonhq.scenebuilder.kit;
    requires java.logging;

    requires com.gluonhq.charm.glisten;
    requires com.gluonhq.attach.display;

    opens com.gluonhq.scenebuilder.plugins.css;
    opens com.gluonhq.scenebuilder.plugins.editor.images.nodeicons to com.gluonhq.scenebuilder.kit;
    opens com.gluonhq.scenebuilder.plugins.hierarchy to com.gluonhq.scenebuilder.kit;
    opens com.gluonhq.scenebuilder.plugins.library.builtin to com.gluonhq.scenebuilder.kit;

    provides com.oracle.javafx.scenebuilder.kit.editor.ExternalThemeProvider with com.gluonhq.scenebuilder.plugins.GluonExternalThemeProvider;
    provides com.oracle.javafx.scenebuilder.kit.library.ExternalSectionProvider with com.gluonhq.scenebuilder.plugins.GluonSectionProvider;
    provides com.oracle.javafx.scenebuilder.kit.metadata.ExternalMetadataProvider with com.gluonhq.scenebuilder.plugins.GluonMetadataProvider;
    provides com.oracle.javafx.scenebuilder.kit.metadata.util.ExternalDesignHierarchyMaskProvider with com.gluonhq.scenebuilder.plugins.GluonDesignHierarchyMaskProvider;

    exports com.gluonhq.scenebuilder.plugins;
    exports com.gluonhq.scenebuilder.plugins.editor;
    exports com.gluonhq.scenebuilder.plugins.alert;
}