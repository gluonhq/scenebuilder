/*
 * Copyright (c) 2017, Gluon and/or its affiliates.
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

import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.kit.util.JavaLanguage;
import com.oracle.javafx.scenebuilder.kit.util.URLUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.Scene;

/**
 *
 * 
 */
public abstract class FXOMObject extends FXOMNode {
    
    private final GlueElement glueElement;
    private FXOMPropertyC parentProperty;
    private FXOMCollection parentCollection;
    private Object sceneGraphObject;

    FXOMObject(FXOMDocument fxomDocument, GlueElement glueElement, Object sceneGraphObject) {
        super(fxomDocument);
        
        assert glueElement != null;
        assert glueElement.getDocument() == fxomDocument.getGlue();
        
        this.glueElement = glueElement;
        this.sceneGraphObject = sceneGraphObject;
    }

    FXOMObject(FXOMDocument fxomDocument, String tagName) {
        super(fxomDocument);        
        this.glueElement = new GlueElement(fxomDocument.getGlue(), tagName);
    }

    public GlueElement getGlueElement() {
        return glueElement;
    }

    public FXOMPropertyC getParentProperty() {
        return parentProperty;
    }

    public FXOMCollection getParentCollection() {
        return parentCollection;
    }
    
    public void addToParentProperty(int index, FXOMPropertyC newParentProperty) {
 
        assert newParentProperty != null;
        assert -1 <= index;
        assert index <= newParentProperty.getValues().size();

        if (parentProperty != null) {
            if (parentProperty.getValues().size() == 1) {
                // it's the last value -> we remove the whole property
                if (parentProperty.getParentInstance() != null) {
                    parentProperty.removeFromParentInstance();
                }
            } else {
                removeFromParentProperty();
            }
        } else if (parentCollection != null) {
            removeFromParentCollection();
        }
        
        parentProperty = newParentProperty;
        newParentProperty.addValue(index, this);
        
        final GlueElement newParentElement = parentProperty.getGlueElement();
        glueElement.addToParent(index, newParentElement);
        
        // May be this object was a root : properties like fx:controller must
        // be reset to preserve FXML validity.
        resetRootProperties();
    }
    
    public void removeFromParentProperty() {
        assert parentProperty != null;
        assert parentProperty.getParentInstance() == null
                || parentProperty.getValues().size() >= 2;
        
        assert glueElement.getParent() == parentProperty.getGlueElement();
        glueElement.removeFromParent();
        
        final FXOMPropertyC keepParentProperty = parentProperty;
        parentProperty = null;
        keepParentProperty.removeValue(this);
    }
    
    public int getIndexInParentProperty() {
        final int result;
        
        if (parentProperty == null) {
            result = -1;
        } else {
            result = parentProperty.getValues().indexOf(this);
            assert result != -1;
        }
        
        return result;
    }
    
    
    public void addToParentCollection(int index, FXOMCollection newParentCollection) {
 
        assert newParentCollection != null;
        assert -1 <= index;
        assert index <= newParentCollection.getItems().size();

        if (parentProperty != null) {
            removeFromParentProperty();
        } else if (parentCollection != null) {
            removeFromParentCollection();
        }
        
        parentCollection = newParentCollection;
        newParentCollection.addValue(index, this);
        
        final GlueElement newParentElement = parentCollection.getGlueElement();
        glueElement.addToParent(index, newParentElement);
        
        // May be this object was a root : properties like fx:controller must
        // be reset to preserve FXML validity.
        resetRootProperties();
    }
    
    public void removeFromParentCollection() {
        assert parentCollection != null;
        
        assert glueElement.getParent() == parentCollection.getGlueElement();
        glueElement.removeFromParent();
        
        final FXOMCollection keepParentCollection = parentCollection;
        parentCollection = null;
        keepParentCollection.removeValue(this);
    }
    
    public int getIndexInParentCollection() {
        final int result;
        
        if (parentCollection == null) {
            result = -1;
        } else {
            result = parentCollection.getItems().indexOf(this);
            assert result != -1;
        }
        
        return result;
    }
    
    public Object getSceneGraphObject() {
        return sceneGraphObject;
    }

    public void setSceneGraphObject(Object sceneGraphObject) {
        this.sceneGraphObject = sceneGraphObject;
    }
    
    public FXOMObject getNextSlibing() {
        final FXOMObject result;
        
        if (parentProperty != null) {
            final int index = getIndexInParentProperty();
            assert index != -1;
            if (index+1 < parentProperty.getValues().size()) {
                result = parentProperty.getValues().get(index+1);
            } else {
                result = null;
            }
        } else if (parentCollection != null) {
            final int index = getIndexInParentCollection();
            assert index != -1;
            if (index+1 < parentCollection.getItems().size()) {
                result = parentCollection.getItems().get(index+1);
            } else {
                result = null;
            }
        } else {
            result = null;
        }
        
        return result;
    }
    
    public FXOMObject getPreviousSlibing() {
        final FXOMObject result;
        
        if (parentProperty != null) {
            final int index = getIndexInParentProperty();
            assert index != -1;
            if (index-1 >= 0) {
                result = parentProperty.getValues().get(index-1);
            } else {
                result = null;
            }
        } else if (parentCollection != null) {
            final int index = getIndexInParentCollection();
            assert index != -1;
            if (index-1 >= 0) {
                result = parentCollection.getItems().get(index-1);
            } else {
                result = null;
            }
        } else {
            result = null;
        }
        
        return result;
    }
    
    public void moveBeforeSibling(FXOMObject sibling) {
        assert sibling != this;
        assert (parentProperty != null) || (parentCollection != null);

        if (parentProperty != null) {
            assert (sibling == null) || (sibling.getParentProperty() == parentProperty);
            
            final FXOMPropertyC oldParentProperty = parentProperty;
            removeFromParentProperty();
            assert parentProperty == null;
            
            final int index;
            if (sibling == null) {
                index = -1;
            } else {
                index = sibling.getIndexInParentProperty();
            }
            addToParentProperty(index, oldParentProperty);

        } else if (parentCollection != null) {
            assert (sibling == null) || (sibling.getParentCollection() == parentCollection);

            final FXOMCollection oldParentCollection = parentCollection;
            removeFromParentCollection();
            assert parentCollection == null;
            
            final int index;
            if (sibling == null) {
                index = -1;
            } else {
                index = sibling.getIndexInParentCollection();
            }
            addToParentCollection(index, oldParentCollection);
        } else {
            assert false;
        }
    }
    
    public Scene getScene() {
        final Scene result;
        
        if (sceneGraphObject instanceof Node) {
            final Node sceneGraphNode = (Node) sceneGraphObject;
            result = sceneGraphNode.getScene();
        } else  {
            result = null;
        }
        
        return result;
    }
    
    public FXOMObject getFirstAncestorWithNonNullScene() {
        FXOMObject result = this;
        
        while ((result != null) && (result.getScene() == null)) {
            result = result.getParentObject();
        }
        
        return result;
    }
    
    /*
     * Utilities
     */
    
    public FXOMObject searchWithSceneGraphObject(Object sceneGraphObject) {
        final FXOMObject result;
        
        if (this.sceneGraphObject == sceneGraphObject) {
            result = this;
        } else {
            result = null;
        }
        
        return result;
    }
    
    public FXOMObject searchWithFxId(String fxId) {
        final FXOMObject result;
        
        assert fxId != null;
        
        if (fxId.equals(getFxId())) {
            result = this;
        } else {
            result = null;
        }
        
        return result;
    }
    
    public Set<Class<?>> collectDeclaredClasses() {
        final Set<Class<?>> result = new HashSet<>();
        
        collectDeclaredClasses(result);
        
        return result;
    }
    
    protected abstract void collectDeclaredClasses(Set<Class<?>> result);
    
    
    public List<FXOMProperty> collectProperties(PropertyName propertyName) {
        final List<FXOMProperty> result = new ArrayList<>();
        
        collectProperties(propertyName, result);
        
        return result;
    }
    
    protected abstract void collectProperties(PropertyName propertyName, List<FXOMProperty> result);
    
    
    public List<FXOMPropertyT> collectNullProperties() {
        final List<FXOMPropertyT> result = new ArrayList<>();
        
        collectNullProperties(result);
        
        return result;
    }
    
    protected abstract void collectNullProperties(List<FXOMPropertyT> result);
    
    
    public List<FXOMPropertyT> collectPropertiesT() {
        final List<FXOMPropertyT> result = new ArrayList<>();
        
        collectPropertiesT(result);
        
        return result;
    }
    
    protected abstract void collectPropertiesT(List<FXOMPropertyT> result);
    
    
    public List<FXOMIntrinsic> collectReferences(String source) {
        final List<FXOMIntrinsic> result = new ArrayList<>();
        
        collectReferences(source, result);
        
        return result;
    }
    
    protected abstract void collectReferences(String source, List<FXOMIntrinsic> result);
    
    public List<FXOMNode> collectReferences(String source, FXOMObject scope) {
        assert source != null;
        
        final List<FXOMNode> result = new ArrayList<>();
        
        collectReferences(source, scope, result);
        
        return result;
    }
    
    protected abstract void collectReferences(String source, FXOMObject scope, List<FXOMNode> result);
    
    public List<FXOMIntrinsic> collectIncludes(String source) {
        final List<FXOMIntrinsic> result = new ArrayList<>();
        
        collectIncludes(source, result);
        
        return result;
    }
    
    protected abstract void collectIncludes(String source, List<FXOMIntrinsic> result);

    public Map<String, FXOMObject> collectFxIds() {
        final Map<String, FXOMObject> result = new LinkedHashMap<>();
        
        collectFxIds(result);
        
        return result;
    }
    
    protected abstract void collectFxIds(Map<String, FXOMObject> result);
    
    public List<FXOMObject> collectObjectWithSceneGraphObjectClass(Class<?> sceneGraphObjectClass) {
        final List<FXOMObject> result = new ArrayList<>();
        
        collectObjectWithSceneGraphObjectClass(sceneGraphObjectClass, result);
        
        return result;
    }
    
    protected abstract void collectObjectWithSceneGraphObjectClass(Class<?> sceneGraphObjectClass, List<FXOMObject> result);
    
    public List<FXOMPropertyT> collectEventHandlers() {
        final List<FXOMPropertyT> result = new ArrayList<>();
        
        collectEventHandlers(result);
        
        return result;
    }
    
    protected abstract void collectEventHandlers(List<FXOMPropertyT> result);
    
    /*
     * Utilities
     */

    public FXOMObject getParentObject() {
        final FXOMObject result;
        if (parentProperty != null) {
            assert parentCollection == null;
            result = parentProperty.getParentInstance();
        } else if (parentCollection != null) {
            result = parentCollection;
        } else {
            result = null;
        }
        return result;
    }
    
    public void removeFromParentObject() {
        if (parentProperty != null) {
            if (parentProperty.getValues().size() == 1) {
                // This object is the last value of its parent property
                // We remove the property from the parent instance
                if (parentProperty.getParentInstance() != null) {
                    parentProperty.removeFromParentInstance();
                }
            } else {
                removeFromParentProperty();
            }
        } else if (parentCollection != null) {
            removeFromParentCollection();
       }
    }
    
    public abstract List<FXOMObject> getChildObjects();
    
    public boolean isDescendantOf(FXOMObject other) {
        final boolean result;
        
        if (other == null) {
            result = true;
        } else {
            FXOMObject ancestor = getParentObject();
            while ((ancestor != other) && (ancestor != null)) {
                ancestor = ancestor.getParentObject();
            }
            result = (ancestor != null);
        }
        
        return result;
    }
    
    public boolean isNode() {
        return sceneGraphObject instanceof Node;
    }
    
    public FXOMObject getClosestNode() {
        FXOMObject result;
        
        result = this;
        while ((result.isNode() == false) && (result.getParentObject() != null)) {
            result = result.getParentObject();
        }
        
        return result.isNode() ? result : null;
    }
    
    public String getFxId() {
        return glueElement.getAttributes().get("fx:id");
    }
    
    
    public void setFxId(String fxId) {
        assert (fxId == null) || JavaLanguage.isIdentifier(fxId);
        if (fxId == null) {
            glueElement.getAttributes().remove("fx:id");
        } else {
            glueElement.getAttributes().put("fx:id", fxId);
        }
    }
        
    
    public String getFxValue() {
        return glueElement.getAttributes().get("fx:value");
    }
    
    public void setFxValue(String fxValue) {
        if (fxValue == null) {
            glueElement.getAttributes().remove("fx:value");
        } else {
            glueElement.getAttributes().put("fx:value", fxValue);
        }
    }
    
    
    public String getFxConstant() {
        return glueElement.getAttributes().get("fx:constant");
    }
    
    public void setFxConstant(String fxConstant) {
        if (fxConstant == null) {
            glueElement.getAttributes().remove("fx:constant");
        } else {
            glueElement.getAttributes().put("fx:constant", fxConstant);
        }
    }
    
    public String getFxController() {
        return glueElement.getAttributes().get("fx:controller");
    }
    
    public void setFxController(String fxController) {
        if (fxController == null) {
            glueElement.getAttributes().remove("fx:controller");
        } else {
            glueElement.getAttributes().put("fx:controller", fxController);
        }
    }
    
    public String getFxFactory() {
        return glueElement.getAttributes().get("fx:factory");
    }
    
    public void setFxFactory(String fxFactory) {
        if (fxFactory == null) {
            glueElement.getAttributes().remove("fx:factory");
        } else {
            glueElement.getAttributes().put("fx:factory", fxFactory);
        }
    }
    
    public String getNameSpaceFX() {
        return glueElement.getAttributes().get("xmlns");
    }
    
    public void setNameSpaceFX(String nameSpace) {
        if (nameSpace == null) {
            glueElement.getAttributes().remove("xmlns");
        } else {
            glueElement.getAttributes().put("xmlns", nameSpace);
        }
    }
    
    public String getNameSpaceFXML() {
        return glueElement.getAttributes().get("xmlns:fx");
    }
    
    public void setNameSpaceFXML(String nameSpace) {
        if (nameSpace == null) {
            glueElement.getAttributes().remove("xmlns:fx");
        } else {
            glueElement.getAttributes().put("xmlns:fx", nameSpace);
        }
    }

    /*
     * FXOMNode
     */
    
    @Override
    public void moveToFxomDocument(FXOMDocument destination) {
        assert destination != null;
        assert destination != getFxomDocument();
        assert (parentProperty == null) 
                || (parentProperty.getParentInstance() == null)
                || (parentProperty.getValues().size() >= 2);
        
        if (URLUtils.equals(getFxomDocument().getLocation(), destination.getLocation()) == false) {
            documentLocationWillChange(destination.getLocation());
        }
        
        final Map<String, FXOMObject> destinationFxIds = destination.collectFxIds();
        final Map<String, FXOMObject> importedFxIds = collectFxIds();
        final FXOMFxIdMerger merger = new FXOMFxIdMerger(destinationFxIds.keySet(), importedFxIds.keySet());
        for (Map.Entry<String, FXOMObject> e : importedFxIds.entrySet()) {
            final String originalFxId = e.getKey();
            final FXOMObject fxomObject = e.getValue();
            assert originalFxId.equals(fxomObject.getFxId());
            final String renamedFxId = merger.getRenamedFxId(originalFxId);
            assert renamedFxId != null;
            
            if (renamedFxId.equals(originalFxId) == false) {
                /*
                 * Apply the renaming:
                 * 1) the declaration 
                 *      <Button fx:id="toto" .../>
                 * 2) expressions that reference the fx:id
                 *      ... labelFor="$toto" ...
                 * 3) intrinsics that reference the fx:id
                 *      <fx:reference source="toto"/>
                 */

                // #1
                fxomObject.setFxId(renamedFxId);
                // #2
                final PrefixedValue pv = new PrefixedValue(PrefixedValue.Type.EXPRESSION, renamedFxId);
                final String newValue = pv.toString();
                for (FXOMPropertyT p : FXOMNodes.collectReferenceExpression(this, originalFxId)) {
                    p.setValue(newValue);
                }
                // #3
                for (FXOMObject o : FXOMNodes.serializeObjects(this)) {
                    if (o instanceof FXOMIntrinsic) {
                        final FXOMIntrinsic i = (FXOMIntrinsic) o;
                        switch(i.getType()) {
                            case FX_REFERENCE:
                            case FX_COPY:
                                if (i.getSource().equals(originalFxId)) {
                                    i.setSource(renamedFxId);
                                }
                                break;
                            default:
                                // "source" does not contain an fx:id
                                break;
                        }
                    }
                }
            }
        }
        
        if (parentProperty != null) {
            assert parentProperty.getFxomDocument() == getFxomDocument();
            removeFromParentProperty();
        } else if (parentCollection != null) {
            assert parentCollection.getFxomDocument() == getFxomDocument();
            removeFromParentCollection();
        } else if (getFxomDocument().getFxomRoot() == this) {
            getFxomDocument().setFxomRoot(null);
        }
        
        assert parentProperty == null;
        assert parentCollection == null;
        assert glueElement.getParent() == null;
        
        glueElement.moveToDocument(destination.getGlue());
        changeFxomDocument(destination);
    }

    @Override
    protected void changeFxomDocument(FXOMDocument destination) {
        assert destination != null;
        assert destination != getFxomDocument();
        assert destination.getGlue() == glueElement.getDocument();
        assert (parentProperty   == null) || (destination == parentProperty.getFxomDocument());
        assert (parentCollection == null) || (destination == parentCollection.getFxomDocument());
        
        super.changeFxomDocument(destination);
    }

    
    /*
     * Object
     */
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        
        result.append(getClass().getSimpleName());
        result.append("[tagName=");
        result.append(glueElement.getTagName());
        if (getFxId() != null) {
            result.append(",fx:id=");
            result.append(getFxId());
        }
        result.append(']');
        
        return result.toString();
    }
    
    /*
     * Package
     */
    
    /* For FXOMPropertyC constructor private use */
    void setParentProperty(FXOMPropertyC newParentProperty) {
        assert parentProperty == null;
        assert parentCollection == null;
        assert newParentProperty.getValues().contains(this);
        parentProperty = newParentProperty;
    }
    
    /* For FXOMCollection constructor private use */
    void setParentCollection(FXOMCollection newParentCollection) {
        assert parentProperty == null;
        assert parentCollection == null;
        assert newParentCollection.getItems().contains(this);
        parentCollection = newParentCollection;
    }
    
    public boolean isGluon() {
        return sceneGraphObject != null && sceneGraphObject.getClass().getName().startsWith(EditorPlatform.GLUON_PACKAGE);
    }

    
    /*
     * Private
     */
    
    private void resetRootProperties() {
        setFxController(null);
        setNameSpaceFX(null);
        setNameSpaceFXML(null);
    }
}
