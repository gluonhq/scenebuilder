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

class FXOMImportsRemover {
    
    private static final Logger LOGGER = Logger.getLogger(FXOMImportsRemover.class.getName());

    private final Consumer<String> removedTypeConsumer;
    
    FXOMImportsRemover() {
        this(_ -> {
            /* no operation here by default  */
        });
    }
    
    FXOMImportsRemover(Consumer<String> matchingTypeConsumer) {
        this.removedTypeConsumer = matchingTypeConsumer;
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
