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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class FXOMImportsRemover implements BiFunction<String, List<String>, String>{
    
    private static final Logger LOGGER = Logger.getLogger(FXOMImportsRemover.class.getName());

    private Consumer<String> unresolvedTypeConsumer;
    
    FXOMImportsRemover() {
        this(type->{});
    }
    FXOMImportsRemover(Consumer<String> unresolvedTypeConsumer) {
        this.unresolvedTypeConsumer = unresolvedTypeConsumer;
    }
    
    /*
     * TODO: Rework the way how the import tag is detected and how the import statement is removed.
     * This implementation only works fine with one import per line, but actually one can put multiple imports in one line.
     * This approach works as of now but is not robust.
     * 
     */
    @Override
    public String apply(String sourceFxml, List<String> unresolvedTypes) {
        List<String> lines = new ArrayList<>(sourceFxml.lines().toList());
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.matches("^\s?[<]\s?[?]\s?import\s?.*")) {
                for (String unresolvedType : unresolvedTypes) {
                    if (line.contains(unresolvedType)) {
                        LOGGER.log(Level.INFO, "Removing FXML import: ", line);
                        lines.set(i, "");
                        unresolvedTypeConsumer.accept(unresolvedType);
                    }
                }
            }
        }
        String modifiedFxml = lines.stream()
                                   .collect(Collectors.joining(System.lineSeparator()));
        return modifiedFxml;
    }
}
