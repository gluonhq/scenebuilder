package com.oracle.javafx.scenebuilder.kit.skeleton;

import com.oracle.javafx.scenebuilder.kit.util.eventnames.EventNames;
import com.oracle.javafx.scenebuilder.kit.util.eventnames.ImportBuilder;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

class SkeletonContext {

    private final String fxController;
    private final String documentName;
    private final SkeletonSettings settings;
    private final Set<String> imports;
    private final List<Pair<String, Class<?>>> variables;
    private final Map<String, String> eventHandlers;
    private final List<String> assertions;

    private SkeletonContext(
        String fxController,
        String documentName,
        SkeletonSettings settings,
        Set<String> imports,
        List<Pair<String, Class<?>>> variables,
        Map<String, String> eventHandlers,
        List<String> assertions
    ) {
        this.fxController = fxController;
        this.documentName = documentName;
        this.settings = Objects.requireNonNull(settings);
        this.imports = Collections.unmodifiableSet(imports);
        this.variables = Collections.unmodifiableList(variables);
        this.eventHandlers = Collections.unmodifiableMap(eventHandlers);
        this.assertions = Collections.unmodifiableList(assertions);
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

    List<Pair<String, Class<?>>> getVariables() {
        return variables;
    }

    Map<String, String> getEventHandlers() {
        return eventHandlers;
    }

    List<String> getAssertions() {
        return assertions;
    }

    static class Builder {

        private String fxController;
        private String documentName;
        private SkeletonSettings settings;

        private final Set<String> imports = new TreeSet<>();
        private final List<Pair<String, Class<?>>> variables = new ArrayList<>();
        private final Map<String, String> eventHandlers = new TreeMap<>();
        private final List<String> assertions = new ArrayList<>();

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

        void addVariable(Pair<String, Class<?>> variable) {
            variables.add(variable);
        }

        void addEventHandler(String key, String value) {
            eventHandlers.put(key, value);
        }

        /**
         * Constructs import statements for event classes.
         *
         * @param eventName event name, for which a statement should be built.
         */
        void addImportsForEvents(String eventName) {
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
            imports.add(ImportBuilder.build());
            ImportBuilder.reset();
        }

        void addAssertionForFxId(String fxId) {
            assertions.add(fxId);
        }

        SkeletonContext build() {
            return new SkeletonContext(fxController, documentName, settings, imports, variables, eventHandlers, assertions);
        }
    }
}
