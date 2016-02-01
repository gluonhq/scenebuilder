/*
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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javafx.fxml.FXMLLoader;

/**
 *
 * 
 */
class FXOMSaver {
    
    
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
    
    private static final Logger LOG = Logger.getLogger(FXOMSaver.class.getName());
    private static final String NAME_SPACE_FX = "http://javafx.com/javafx/" + FXMLLoader.JAVAFX_VERSION;
    private static final String NAME_SPACE_FXML = "http://javafx.com/fxml/1";
    
    private void updateNameSpace(FXOMDocument fxomDocument) {
        assert fxomDocument.getFxomRoot() != null;
        
        final FXOMObject fxomRoot = fxomDocument.getFxomRoot();
        final String currentNameSpaceFX = fxomRoot.getNameSpaceFX();
        final String currentNameSpaceFXML = fxomRoot.getNameSpaceFXML();
        
        if ((currentNameSpaceFX == null) 
                || (currentNameSpaceFX.equals(NAME_SPACE_FX) == false)) {
            fxomRoot.setNameSpaceFX(NAME_SPACE_FX);
        }
        
        if ((currentNameSpaceFXML == null) 
                || (currentNameSpaceFXML.equals(NAME_SPACE_FXML) == false)) {
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
        // constructs the set of classes to be imported. No doubles allowed.
        final Set<String> imports = new TreeSet<>(); // Sorted   
        
        //gets list of declared classes, declared classes are the ones directly used as a Node. 
        //Example: <Button/> ; classname = javafx.scene.control.Button
        fxomDocument.getFxomRoot().collectDeclaredClasses().forEach(dc -> imports.add(dc.getName()));
        
        //gets the current list of imports in the FXML document
        List<GlueInstruction> instructions = fxomDocument.getGlue().collectInstructions("import");
       
        //filters all not declared imports
        List<String> importsToValidate = instructions.stream()
                .map(instruction-> instruction.getData())
                .filter(data -> !imports.contains(data))
                .collect(Collectors.toList());
        
        //if there are any non delcared imports, then validate them if they are being used. 
        //It will only look at attributes and not at the tag name, since tag names are already in the imports list.
        if(!importsToValidate.isEmpty()){
            imports.addAll(getValidImports(importsToValidate, fxomDocument.getGlue().toString()));
        }
        
        return createGlueInstructionsForImports(fxomDocument, imports);
    }
    
    // Creates a List of glue instruction for all imported classes.
    private List<GlueInstruction> createGlueInstructionsForImports(FXOMDocument fxomDocument, Set<String> imports) {
        List<GlueInstruction> importsList = new ArrayList<>();
        imports.forEach(className -> {
            final GlueInstruction instruction = new GlueInstruction(fxomDocument.getGlue(), "import", className);
            importsList.add(instruction);
        });
        return importsList;
    }

    private void synchronizeHeader(GlueDocument glue, List<GlueInstruction> importList) {
        synchronized (this) {                 
            glue.getHeader().clear();
            glue.getHeader().addAll(importList);
        }

    }

    private List<String> getValidImports(List<String> importsToValidate, String fxml) {
        // looks through the FXML if the import is actually being used as an attribute. Example: <Pane HBox.hgrow="ALWAYS"/>
        List<String> validImports = new ArrayList<>();
        try{
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(fxml)));
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        importsToValidate.forEach(i -> {
            String name = i.substring(i.lastIndexOf('.') + 1); //example: javafx.scene.layout.HBox -> HBox
            String expression = "//@*[starts-with(name(.), '" + name + ".')]"; // example: looks in the Document for attribute 'HBox.'
            if(isExpressionPresent(xpath, expression, doc)){
                validImports.add(i);
            }else{
                LOG.log(Level.INFO, "No usage for the import  " + i + " was found, it will be removed");
            }
        });
        
            List<String> wildcardImports = getWildcardImports(importsToValidate);
            if (!wildcardImports.isEmpty()) {
                Set<String> staticClasses = getAllStaticProperties(xpath, doc);
                validImports.addAll(getWildCardClasses(wildcardImports, staticClasses));
            }
        
        } catch (SAXException | IOException | ParserConfigurationException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }  
        
        
        
        return validImports;
    }
    
    
    private Boolean isExpressionPresent(XPath xpath, String expression, Document doc){
        try {
          //if there are nodes with the attribute it will add the import to the list of imports
            return(((NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET)).getLength() > 0);
        } catch (XPathExpressionException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;     
    }
    
    private Set<String> getAllStaticProperties(XPath xpath, Document doc) {
        Set<String> classNameList = new TreeSet<>();
        String expression = "//@*[contains(name(.), '.')]";
        try {
            NodeList nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String className = node.getNodeName();
                className = className.substring(0, className.indexOf("."));
                classNameList.add(className);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return classNameList;
    }

    private List<String> getWildcardImports(List<String> importsToValidate) {
        List<String> wildcardImports = new ArrayList<>();

        importsToValidate.forEach(i -> {
            if (i.endsWith(".*")) {
                String wildcardImport = i.substring(0, i.lastIndexOf(".*"));
                wildcardImports.add(wildcardImport);
            }
        });
        return wildcardImports;
    }

    private ArrayList<String> getWildCardClasses(List<String> packages, Set<String> classes) {
        ArrayList<String> validClasses = new ArrayList<>();
        classes.forEach(c -> {
            String theClass = validateClass(packages, c);
            if (theClass != null) {
                validClasses.add(theClass);
            }
        });
        return validClasses;
    }

    private String validateClass(List<String> packages, String className) {
        for (int i = 0; i < packages.size(); i++) {
            String p = packages.get(i);
            try {
                Class<?> theClass = Class.forName(p + "." + className);
                return theClass.getName();
            } catch (ClassNotFoundException e) {
                continue;
            }
        }

        return null;
    }
}
