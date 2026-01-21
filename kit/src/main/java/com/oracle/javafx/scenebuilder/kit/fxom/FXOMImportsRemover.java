/*
 * Copyright (c) 2025, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.fxom;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class provides the functionality to remove import declarations from a
 * given FXML text by comparing the FXML text to the collection of fully
 * qualified type names provided. <br>
 * <br>
 * Important, this class only remove such import statements, it does not verify
 * if an import declaration is valid and a type exists. <br>
 * <br>
 * Example:
 * 
 *
 * <pre>{@code
 * Set<String> detectedUnresolvableTypes = new HashSet<>();
 *
 * String sourceFxmlText = """
 *         <?xml version="1.0" encoding="UTF-8"?>
 *         <?import javafx.scene.control.*?>
 *         <?import another.unresolvable.Dependency?>
 *         <?import also.an.unresolvable.Dependency?>
 *         <?import this.namespace.is.unknown.*?>
 *         <AnchorPane>
 *             <children>
 *                 <Button layoutX="302.0" layoutY="27.0" text="Button" />
 *                 <ComboBox layoutX="46.0" layoutY="175.0" prefWidth="150.0" />
 *                 <TextField layoutX="345.0" layoutY="264.0" />
 *                 <Button layoutX="84.0" layoutY="252.0" text="Button" />
 *                 <UnknownElement layoutX="84.0" layoutY="87.0" text="Some Content" />
 *             </children>
 *         </AnchorPane>
 *         """;
 *
 * List<String> importsToRemove = List.of("another.unresolvable.Dependency", 
 *                                        "also.an.unresolvable.Dependency");
 *
 * FXOMImportsRemover remover = new FXOMImportsRemover(detectedUnresolvableTypes::add);
 * String cleanedFxmlText = remover.removeImports(sourceFxmlText, importsToRemove);
 *
 * }</pre>
 * 
 */
class FXOMImportsRemover {
    
    private static final Logger LOGGER = Logger.getLogger(FXOMImportsRemover.class.getName());

    private final Consumer<String> removedTypeConsumer;

    /**
     * By default, FXOMImportsRemover instances will remove imports which cannot 
     * be resolved without triggering additional actions. If a different behavior
     * is desired, one can use the constructor which accepts a {@code Consumer<String>} 
     * process the declared types (fully qualified name). 
     */
    FXOMImportsRemover() {
        this(_ -> {
            /* no operation here by default  */
        });
    }

    /**
     * This constructor allows to assign a custom handler which is notified for each
     * type which is about to removed from the FXML text which is about to be
     * processed.
     * 
     * @param unresolvedTypeHandler This consumer of String must not be null. It
     *                              will be notified for each type (fully qualified
     *                              name) which cannot be resolved and is about to
     *                              be ignored.
     * @throws NullPointerException if {@code unresolvedTypeHandler} is {@code null}
     */
    FXOMImportsRemover(Consumer<String> unresolvedTypeHandler) {
        this.removedTypeConsumer = unresolvedTypeHandler;
    }
    
    /**
     * Removes all imports from a given FXML for the given type names.
     * 
     * @param sourceFxml The source FXML.
     * @param typesToRemove Type names which shall be removed. 
     * @return FXML text without the previously removed types
     */
    public String removeImports(String sourceFxml, Collection<String> typesToRemove) {
        var predicate = new IgnoreImportPredicate(removedTypeConsumer,typesToRemove);
        if (typesToRemove.isEmpty()) {
            return sourceFxml;
        }
        
        LOGGER.log(Level.FINE, "Removing FXML imports.");
        
        /* 
         * The predicate detects specific imports.
         * In this case here, all lines which do not match the given types shall be kept.
         */
        return sourceFxml.lines()
                         .filter(predicate)
                         .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * This predicate will yield true for all FXML lines to keep and false for all
     * FXML lines to ignore. Lines to be ignored are import statements with the
     * given type names.
     * <br>
     * <br>
     * Regex description
     * <pre>
     *  opening import declaration: &lt;?import
     *  at least one whitespace, more are possible: (\\s\\s*)
     *  the unresolved type name 
     *  optional whitespace: (\\s*)
     *  closing import declaration: [?]&gt;
     * </pre>
     */
    private static class IgnoreImportPredicate implements Predicate<String> {

        private final Map<String, Pattern> typesToRemove;
        
        private final Consumer<String> matchingTypeConsumer;

        IgnoreImportPredicate(Consumer<String> unresolvedTypeConsumer, Collection<String> unresolvedTypes) {
            this.matchingTypeConsumer = unresolvedTypeConsumer;
            this.typesToRemove = unresolvedTypes.stream()
                                                .collect(Collectors.toMap(String::valueOf, 
                                                                 IgnoreImportPredicate::createRegex));
        }

        private static Pattern createRegex(String type) {
            return Pattern.compile( "<[?]import(\\s*)" + type.replace(".", "[.]") + "(\\s*)[?]>");
        }

        @Override
        public boolean test(String lineToTest) {
            for (Entry<String,Pattern> entry : typesToRemove.entrySet()) {
                var pattern = entry.getValue().matcher(lineToTest);
                if (pattern.matches()) {
                    LOGGER.log(Level.FINE, "Ignoring FXML import: {0}", lineToTest);
                    matchingTypeConsumer.accept(entry.getKey());
                    return false;
                }
            }
            return true;
        }

    }
}
