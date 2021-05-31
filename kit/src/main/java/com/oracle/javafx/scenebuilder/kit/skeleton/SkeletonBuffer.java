/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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
package com.oracle.javafx.scenebuilder.kit.skeleton;

import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.util.eventnames.FindEventNamesUtil;

import java.net.URL;
import java.util.ResourceBundle;

class SkeletonBuffer {

    private final FXOMDocument document;
    private final String documentName;

    private final SkeletonSettings settings = new SkeletonSettings();

    private final SkeletonCreator skeletonCreator = new SkeletonCreator();

    SkeletonBuffer(FXOMDocument document, String documentName) {
        assert document != null;
        this.document = document;
        this.documentName = documentName;
    }

    void setLanguage(SkeletonSettings.LANGUAGE language) {
        settings.setLanguage(language);
    }

    void setTextType(SkeletonSettings.TEXT_TYPE type) {
        settings.setTextType(type);
    }

    void setFormat(SkeletonSettings.FORMAT_TYPE format) {
        settings.setFormat(format);
    }

    private boolean isFull() {
        return settings.isFull();
    }

    @Override
    public String toString() {
        if (document.getFxomRoot() == null) {
            return I18N.getString("skeleton.empty");
        } else {
            SkeletonContext.Builder builder = SkeletonContext.builder()
                .withFxController(document.getFxomRoot().getFxController())
                .withDocumentName(documentName)
                .withSettings(settings);

            construct(builder);

            return skeletonCreator.createFrom(builder.build());
        }
    }

    private void construct(SkeletonContext.Builder builder) {
        constructFxIds(builder);
        constructEventHandlers(builder);
        constructAdditionalImports(builder);
    }

    private void constructFxIds(SkeletonContext.Builder builder) {
        for (FXOMObject value : document.collectFxIds().values()) {
            builder.addFxId(value);
        }
    }

    private void constructEventHandlers(SkeletonContext.Builder builder) {
        // need to initialize the internal events map
        FindEventNamesUtil.initializeEventsMap();

        for (FXOMPropertyT eventHandler : document.getFxomRoot().collectEventHandlers()) {
            builder.addEventHandler(eventHandler);
        }
    }

    private void constructAdditionalImports(SkeletonContext.Builder builder) {
        if (isFull()) {
            builder.addImportsFor(URL.class, ResourceBundle.class);
        }
    }
}
