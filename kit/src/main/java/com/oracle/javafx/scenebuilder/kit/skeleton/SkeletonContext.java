/*
 * Copyright (c) 2021, 2024, Gluon and/or its affiliates.
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

import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.kit.util.eventnames.EventNames;
import com.oracle.javafx.scenebuilder.kit.util.eventnames.FindEventNamesUtil;
import com.oracle.javafx.scenebuilder.kit.util.eventnames.ImportBuilder;
import javafx.fxml.FXML;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class SkeletonContext {

    private final String fxController;
    private final String documentName;
    private final SkeletonSettings settings;
    private final SortedSet<String> imports;
    private final SortedMap<String, Class<?>> variables;
    private final SortedMap<String, String> eventHandlers;
    private final SortedSet<String> assertions;

    private SkeletonContext(
        String fxController,
        String documentName,
        SkeletonSettings settings,
        SortedSet<String> imports,
        SortedMap<String, Class<?>> variables,
        SortedMap<String, String> eventHandlers,
        SortedSet<String> assertions
    ) {
        this.fxController = fxController;
        this.documentName = documentName;
        this.settings = Objects.requireNonNull(settings);
        this.imports = Collections.unmodifiableSortedSet(imports);
        this.variables = Collections.unmodifiableSortedMap(variables);
        this.eventHandlers = Collections.unmodifiableSortedMap(eventHandlers);
        this.assertions = Collections.unmodifiableSortedSet(assertions);
    }

    static Builder builder() {
        return new Builder();
    }

    String getFxController() {
        return fxController;
    }

    String getDocumentName() {
        return documentName;
    }

    SkeletonSettings getSettings() {
        return settings;
    }

    Set<String> getImports() {
        return imports;
    }

    Map<String, Class<?>> getVariables() {
        return variables;
    }

    Map<String, String> getEventHandlers() {
        return eventHandlers;
    }

    Set<String> getAssertions() {
        return assertions;
    }

    static class Builder {

        private String fxController;
        private String documentName;
        private SkeletonSettings settings;

        private final SortedSet<String> imports = new TreeSet<>();
        private final SortedMap<String, Class<?>> variables = new TreeMap<>();
        private final SortedMap<String, String> eventHandlers = new TreeMap<>();
        private final SortedSet<String> assertions = new TreeSet<>();

        Builder withFxController(String fxController) {
            this.fxController = fxController;
            return this;
        }

        Builder withDocumentName(String documentName) {
            this.documentName = documentName;
            return this;
        }

        Builder withSettings(SkeletonSettings settings) {
            this.settings = settings;
            return this;
        }

        public void addFxId(FXOMObject value) {
            String fxId = value.getFxId();
            Class<?> type = value.getSceneGraphObject().getClass();

            addImportsFor(FXML.class, type);

            variables.put(fxId, type);
            assertions.add(fxId);
        }

        public void addEventHandler(FXOMPropertyT eventHandler) {
            String eventName = FindEventNamesUtil.findEventName(eventHandler.getName().getName());

            eventHandlers.put(eventHandler.getValue(), eventName);
            addImportsForEvents(eventName);
        }

        private void addImportsForEvents(String eventName) {
            if (EventNames.ACTION_EVENT.equals(eventName)) {
                ImportBuilder.add(ImportBuilder.IMPORT_STATEMENT.concat(ImportBuilder.EVENT_PACKAGE), eventName);
            } else {
                ImportBuilder.add(ImportBuilder.IMPORT_STATEMENT.concat(ImportBuilder.INPUT_PACKAGE), eventName);
            }
            buildAndCollectImports();
        }

        /**
         * Constructs import statements for other classes (like URL, ResourceBundle).
         *
         * @param classes other classes the statement should be built.
         */
        void addImportsFor(Class<?>... classes) {
            for (Class<?> c : classes) {
                ImportBuilder.add(ImportBuilder.IMPORT_STATEMENT, c.getName().replace("$", "."));
                buildAndCollectImports();
            }
            // need an import statement for @FXML, too
            ImportBuilder.add(ImportBuilder.IMPORT_STATEMENT, ImportBuilder.FXML_PACKAGE);
            buildAndCollectImports();
        }

        private void buildAndCollectImports() {
            imports.addAll(ImportBuilder.build());
            ImportBuilder.reset();
        }

        SkeletonContext build() {
            return new SkeletonContext(fxController, documentName, settings, imports, variables, eventHandlers, assertions);
        }
    }
}
