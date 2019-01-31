/*
 * Copyright (c) 2016, 2019, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.fxom;

import com.oracle.javafx.scenebuilder.kit.fxom.glue.GlueDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.glue.GlueInstruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.fxml.FXMLLoader;

/**
 *
 * 
 */
class FXOMSaver {

    private boolean wildcardImports;

    /**
     * Creates a new instance of FXOMSaver
     */
    FXOMSaver() {

    }

    /**
     * Creates a new instance of FXOMSaver
     * @param wildcardImports If set to true, uses wildcard imports while saving FXML
     */
    FXOMSaver(boolean wildcardImports) {
        this.wildcardImports = wildcardImports;
    }
    
    public String save(FXOMDocument fxomDocument) {
        
        assert fxomDocument != null;
        assert fxomDocument.getGlue() != null;
        
        if (fxomDocument.getFxomRoot() != null) {
            updateNameSpace(fxomDocument);
            updateImportInstructions(fxomDocument);
        }

        return fxomDocument.getGlue().toString();
    }
    
    
    /*
     * Private
     */
    
    private static final String NAME_SPACE_FX = "http://javafx.com/javafx/" + FXMLLoader.JAVAFX_VERSION;
    private static final String NAME_SPACE_FXML = "http://javafx.com/fxml/1";
    
    private void updateNameSpace(FXOMDocument fxomDocument) {
        assert fxomDocument.getFxomRoot() != null;
        
        final FXOMObject fxomRoot = fxomDocument.getFxomRoot();
        final String currentNameSpaceFX = fxomRoot.getNameSpaceFX();
        final String currentNameSpaceFXML = fxomRoot.getNameSpaceFXML();
        
        if ((currentNameSpaceFX == null) 
                || (!currentNameSpaceFX.equals(NAME_SPACE_FX))) {
            fxomRoot.setNameSpaceFX(NAME_SPACE_FX);
        }
        
        if ((currentNameSpaceFXML == null) 
                || (!currentNameSpaceFXML.equals(NAME_SPACE_FXML))) {
            fxomRoot.setNameSpaceFXML(NAME_SPACE_FXML);
        }
        
        
    }
        
    private void updateImportInstructions(FXOMDocument fxomDocument) {
        assert fxomDocument.getFxomRoot() != null;

        // gets list of the imports to be added to the FXML document.
        List<GlueInstruction> importList = getHeaderIncludes(fxomDocument);

        // synchronizes the glue with the list of glue instructions
        synchronizeHeader(fxomDocument.getGlue(), importList);
    }

    private List<GlueInstruction> getHeaderIncludes(FXOMDocument fxomDocument) {
        // TODO: When wildcardImport is true, add package name only when no of classes
        //  which belong to the same package exceed 3

        // constructs the set of classes to be imported. No duplicates allowed.
        final Set<String> imports = new TreeSet<>(); // Sorted

        //gets list of declared classes, declared classes are the ones directly used as a Node.
        //Example: <Button/> ; classname = javafx.scene.control.Button
        fxomDocument.getFxomRoot().collectDeclaredClasses().forEach(dc -> imports.add(wildcardImports ? dc.getPackageName() + ".*" : dc.getCanonicalName()));

        FXOMObject root = fxomDocument.getFxomRoot();

        imports.addAll(findPropertyClasses(root.getChildObjects().toArray(FXOMObject[]::new)));
        imports.addAll(findPropertyClasses(root));

        return createGlueInstructionsForImports(fxomDocument, imports);
    }

    private Set<String> findPropertyClasses(FXOMObject... fxomObjects) {
        return Arrays.stream(fxomObjects)
            .map(FXOMObject::collectPropertiesT) //list of lists containing FXOMProperties
            .flatMap(List::stream) // add all to one list of FXOMProperties
            .map(FXOMProperty::getName) // list of all PropertyNames
            .filter(prop -> prop.getResidenceClass() != null) // filter for ResidenceClass (used for static methods example: HBox.hgrow="..")
            .map(prop -> wildcardImports ? prop.getResidenceClass().getPackageName() + ".*" : prop.getResidenceClass().getName()) // list of classes
            .collect(Collectors.toSet());
    }

    // Creates a List of glue instruction for all imported classes.
    private List<GlueInstruction> createGlueInstructionsForImports(FXOMDocument fxomDocument, Set<String> imports) {
        List<GlueInstruction> importsList = new ArrayList<>();
        imports.forEach(name -> {
            final GlueInstruction instruction = new GlueInstruction(fxomDocument.getGlue(), "import", name);
            importsList.add(instruction);
        });
        return importsList;
    }

    private void synchronizeHeader(GlueDocument glue, List<GlueInstruction> importList) {
        synchronized (this) {
            // find out where the first import instruction is located
            final int firstImportIndex;
            List<GlueInstruction> existingImports = glue.collectInstructions("import");
            if (existingImports.isEmpty()) {
                firstImportIndex = 0;
            } else {
                GlueInstruction firstImport = existingImports.get(0);
                firstImportIndex = glue.getHeader().indexOf(firstImport);
            }

            // remove previously defined imports and leave all other things (like comments and such) intact
            glue.getHeader().removeIf(glueAuxiliary ->
                    glueAuxiliary instanceof GlueInstruction &&
                    "import".equals(((GlueInstruction) glueAuxiliary).getTarget())
            );

            // insert the import instructions at the first import index
            glue.getHeader().addAll(firstImportIndex, importList);
        }
    }
}
