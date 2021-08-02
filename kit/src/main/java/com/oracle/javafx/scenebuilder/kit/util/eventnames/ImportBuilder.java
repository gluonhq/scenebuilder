/*
 * Copyright (c) 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.util.eventnames;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Builds an import statement as a key/value pair.
 */
public class ImportBuilder {

    public static final String IMPORT_STATEMENT = "import ";
    public static final String INPUT_PACKAGE = "javafx.scene.input.";
    public static final String EVENT_PACKAGE = "javafx.event.";
    public static final String FXML_PACKAGE = "javafx.fxml.FXML";

    private static Map<String, String> imports = new TreeMap<>();

    // should be used in a static way
    private ImportBuilder() {
    }

    /**
     * Adds an imported class with its package.
     *
     * @param importedPackage package the class comes from
     * @param importedClass imported class
     */
    public static void add(String importedPackage, String importedClass) {
        imports.put(importedPackage, importedClass);
    }

    /**
     * This methods deletes all previously set events imports.
     */
    public static void reset() {
        imports.clear();
    }

    /**
     * Returns true if no imports are set, otherwise false.
     *
     * @return
     */
    public static boolean isEmpty() {
        return imports.isEmpty();
    }

    /**
     * Builds the list of import statements. The key may be the "import" keyword, or the "import" keyword combined with a
     * "javafx.scene.input." package name. The value may be a full class name (e.g. with package) or an input event name.
     *
     * @return list of new import statements
     */
    public static List<String> build() {
        return imports.entrySet()
                      .stream()
                      .map(preparedImport -> preparedImport.getKey() + preparedImport.getValue())
                      .collect(Collectors.toList());
    }
}