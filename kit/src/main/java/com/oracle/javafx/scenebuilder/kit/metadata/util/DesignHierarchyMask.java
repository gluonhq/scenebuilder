/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.metadata.util;

import com.oracle.javafx.scenebuilder.kit.editor.images.ImageUtils;
import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.HierarchyItem;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.kit.library.ExternalSectionProvider;
import com.oracle.javafx.scenebuilder.kit.metadata.Metadata;
import com.oracle.javafx.scenebuilder.kit.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.util.Deprecation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 */
public class DesignHierarchyMask {

    /**
     * An accessory is defined by its class name, a string name, a propertyName, a class, and a predicate
     * @param name
     * @param propertyName
     * @param classForAccessory
     * @param isAccepting
     */
    public record Accessory(String name, PropertyName propertyName, Class<?> classForAccessory, Predicate<Object> isAccepting) {

        @Override
        public String toString() {
            return name;
        }

        // Accessories
        public static final Accessory PLACEHOLDER =
            new Accessory("PLACEHOLDER", new PropertyName("placeholder"), javafx.scene.Node.class, o -> true);
        public static final Accessory TOOLTIP =
            new Accessory("TOOLTIP", new PropertyName("tooltip"), javafx.scene.control.Tooltip.class, o -> true);
        public static final Accessory CONTEXT_MENU =
            new Accessory("CONTEXT_MENU", new PropertyName("contextMenu"), javafx.scene.control.ContextMenu.class, o -> true);
        public static final Accessory CLIP =
            new Accessory("CLIP", new PropertyName("clip"), javafx.scene.Node.class, o -> true);
        public static final Accessory ROOT =
            new Accessory("ROOT", new PropertyName("root"), javafx.scene.Node.class, o -> true);
        public static final Accessory SCENE =
            new Accessory("SCENE", new PropertyName("scene"), javafx.scene.Scene.class, o -> true);
        public static final Accessory TOP =
            new Accessory("TOP", new PropertyName("top"), javafx.scene.Node.class, o -> true);
        public static final Accessory BOTTOM =
            new Accessory("BOTTOM", new PropertyName("bottom"), javafx.scene.Node.class, o -> true);
        public static final Accessory LEFT =
            new Accessory("LEFT", new PropertyName("left"), javafx.scene.Node.class, o -> true);
        public static final Accessory RIGHT =
            new Accessory("RIGHT", new PropertyName("right"), javafx.scene.Node.class, o -> true);
        public static final Accessory CENTER =
            new Accessory("CENTER", new PropertyName("center"), javafx.scene.Node.class, o -> true);
        public static final Accessory XAXIS =
            new Accessory("XAXIS", new PropertyName("xAxis"), javafx.scene.chart.Axis.class, o -> true);
        public static final Accessory YAXIS =
            new Accessory("YAXIS", new PropertyName("xAxis"), javafx.scene.chart.Axis.class, o -> true);
        public static final Accessory TREE_COLUMN =
            new Accessory("TREE_COLUMN", new PropertyName("treeColumn"), javafx.scene.control.TreeTableColumn.class, o -> true);
        public static final Accessory EXPANDABLE_CONTENT =
            new Accessory("EXPANDABLE_CONTENT", new PropertyName("expandableContent"), javafx.scene.Node.class, o -> true);
        public static final Accessory HEADER =
            new Accessory("HEADER", new PropertyName("header"), javafx.scene.Node.class, o -> true);

        // content and graphic accept every object except a DialogPane
        public static final Accessory CONTENT =
            new Accessory("CONTENT", new PropertyName("content"), javafx.scene.Node.class, o -> !(o instanceof DialogPane));
        public static final Accessory GRAPHIC =
            new Accessory("GRAPHIC", new PropertyName("graphic"), javafx.scene.Node.class, o -> !(o instanceof DialogPane));
        // dp_content and dp_graphic accept only a DialogPane
        public static final Accessory DP_CONTENT =
            new Accessory("CONTENT", new PropertyName("content"), javafx.scene.Node.class, o -> o instanceof DialogPane);
        public static final Accessory DP_GRAPHIC =
            new Accessory("GRAPHIC", new PropertyName("graphic"), javafx.scene.Node.class, o -> o instanceof DialogPane);
    }

    public List<Accessory> getAccessoryList() {
        List<Accessory> accessories = new ArrayList<>(List.of(Accessory.PLACEHOLDER, Accessory.TOOLTIP, Accessory.CONTEXT_MENU, Accessory.CLIP,
            Accessory.ROOT, Accessory.SCENE, Accessory.TOP, Accessory.BOTTOM, Accessory.LEFT, Accessory.RIGHT,
            Accessory.CENTER, Accessory.XAXIS, Accessory.YAXIS, Accessory.TREE_COLUMN, Accessory.EXPANDABLE_CONTENT, Accessory.HEADER,
            Accessory.CONTENT, Accessory.GRAPHIC, Accessory.DP_CONTENT, Accessory.DP_GRAPHIC));
        accessories.addAll(getExternalAccessories());
        return accessories;
    }

    private final FXOMObject fxomObject;
    private Map<PropertyName, ComponentPropertyMetadata> propertyMetadataMap; // Initialized lazily

    public DesignHierarchyMask(FXOMObject fxomObject) {
        assert fxomObject != null;
        this.fxomObject = fxomObject;
    }

    public FXOMObject getFxomObject() {
        return fxomObject;
    }

    public FXOMObject getParentFXOMObject() {
        return fxomObject.getParentObject();
    }

    public boolean isFxNode() {
        return fxomObject.getSceneGraphObject() instanceof Node;
    }

    public FXOMObject getClosestFxNode() {
        FXOMObject result = fxomObject;
        DesignHierarchyMask mask = this;

        while ((result != null) && (!mask.isFxNode())) {
            result = mask.getParentFXOMObject();
            mask = (result == null) ? null : new DesignHierarchyMask(result);
        }

        return result;
    }

    public URL getClassNameIconURL() {
        final Object sceneGraphObject;
        
        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic) {
            sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }

        if (sceneGraphObject == null) {
            // For now, handle icons for scenegraph objects only
            return null;
        }
        final URL url;
        switch (sceneGraphObject) {
            case Separator obj -> {
                // Separator orientation
                if (Orientation.HORIZONTAL.equals(obj.getOrientation())) {
                    url = ImageUtils.getNodeIconURL("Separator-h.png"); //NOI18N
                } else {
                    url = ImageUtils.getNodeIconURL("Separator-v.png"); //NOI18N
                }
            }
            case ScrollBar obj -> {
                // ScrollBar orientation
                if (Orientation.HORIZONTAL.equals(obj.getOrientation())) {
                    url = ImageUtils.getNodeIconURL("ScrollBar-h.png"); //NOI18N
                } else {
                    url = ImageUtils.getNodeIconURL("ScrollBar-v.png"); //NOI18N
                }
            }
            case Slider obj -> {
                // Slider orientation
                if (Orientation.HORIZONTAL.equals(obj.getOrientation())) {
                    url = ImageUtils.getNodeIconURL("Slider-h.png"); //NOI18N
                } else {
                    url = ImageUtils.getNodeIconURL("Slider-v.png"); //NOI18N
                }
            }
            case SplitPane obj -> {
                // SplitPane orientation
                if (Orientation.HORIZONTAL.equals(obj.getOrientation())) {
                    url = ImageUtils.getNodeIconURL("SplitPane-h.png"); //NOI18N
                } else {
                    url = ImageUtils.getNodeIconURL("SplitPane-v.png"); //NOI18N
                }
            }
            default -> {
                // Default
                Class<?> componentClass = sceneGraphObject.getClass();
                URL externalURL = findExternalItemImage(componentClass);
                if (externalURL == null) {
                    String fileName = componentClass.getSimpleName();
                    url = ImageUtils.getNodeIconURL(fileName + ".png"); //NOI18N
                } else {
                    url = externalURL;
                }
            }
        }
        return url;
    }

    public Image getClassNameIcon() {
        final URL resource = getClassNameIconURL();
        return ImageUtils.getImage(resource);
    }

    public String getClassNameInfo() {
        final Object sceneGraphObject;
        final String classNameInfo;
        String prefix = "", suffix = ""; //NOI18N

        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic fxomIntrinsic) {
            sceneGraphObject = fxomIntrinsic.getSourceSceneGraphObject();
            if (fxomIntrinsic.getType() == FXOMIntrinsic.Type.FX_INCLUDE) {
                // Add FXML prefix for included FXML file
                prefix += "FXML "; //NOI18N
            }
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }

        if (sceneGraphObject == null) {
            classNameInfo = prefix + fxomObject.getGlueElement().getTagName() + suffix;
        } else {
            if (sceneGraphObject instanceof Node node) {

                // GridPane : add num rows x num columns
                if (node instanceof GridPane) {
                    int columnsSize = getColumnsSize();
                    int rowsSize = getRowsSize();
                    suffix += " (" + columnsSize + " x " + rowsSize + ")"; //NOI18N
                }

                // GridPane children : add child positioning within the GridPane
                final FXOMObject parentFxomObject = fxomObject.getParentObject();
                if (parentFxomObject != null) {
                    final Object parentSceneGraphObject = parentFxomObject.getSceneGraphObject();
                    if (parentSceneGraphObject instanceof GridPane) {
                        int columnIndex = getColumnIndex();
                        int rowIndex = getRowIndex();
                        suffix += " (" + columnIndex + ", " + rowIndex + ")"; //NOI18N
                    }
                }
            }
            classNameInfo = prefix + sceneGraphObject.getClass().getSimpleName() + suffix;
        }

        return classNameInfo;
    }

    /**
     * Returns the string value for this FXOM object description property.
     * If the value is internationalized, the returned value is the resolved one.
     *
     * @return
     */
    public String getDescription() {
        if (hasDescription()) { // (1)
            final PropertyName propertyName = getPropertyNameForDescription();
            assert propertyName != null; // Because of (1)
            assert fxomObject instanceof FXOMInstance; // Because of (1)
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final ValuePropertyMetadata vpm
                    = Metadata.getMetadata().queryValueProperty(fxomInstance, propertyName);
            final Object description = vpm.getValueInSceneGraphObject(fxomInstance); // resolved value
            return description == null ? null : description.toString();
        }
        return null;
    }

    /**
     * Returns a single line description for this FXOM object.
     *
     * @return
     */
    public String getSingleLineDescription() {
        String result = getDescription();
        if (result != null && containsLineFeed(result)) {
            result = result.substring(0, result.indexOf('\n')) + "..."; //NOI18N
        }
        return result;
    }

    /**
     * Returns the object value for this FXOM object node id property.
     *
     * @return
     */
    public Object getNodeIdValue() {
        Object result = null;
        if (fxomObject instanceof FXOMInstance fxomInstance) {
            final PropertyName propertyName = new PropertyName("id"); //NOI18N
            final ValuePropertyMetadata vpm
                    = Metadata.getMetadata().queryValueProperty(fxomInstance, propertyName);
            result = vpm.getValueObject(fxomInstance);
        }
        return result;
    }

    /**
     * Returns the string value for this FXOM object node id property.
     *
     * @return
     */
    public String getNodeId() {
        final Object value = getNodeIdValue();
        String result = null;
        if (value != null) {
            result = value.toString();
        }
        return result;
    }

    public String getFxId() {
        String result = null;
        if (fxomObject instanceof FXOMInstance fxomInstance) { // Can be null for place holder items
            final String fxId = fxomInstance.getFxId();
            result = fxId == null ? "" : fxId; //NOI18N
        }
        return result;
    }

    public boolean hasDescription() {
        final Object sceneGraphObject = fxomObject.getSceneGraphObject();
        if (sceneGraphObject == null) {
            // For now, handle display label for scenegraph objects only
            return false;
        }
        return sceneGraphObject instanceof ComboBox
                || sceneGraphObject instanceof Labeled
                || sceneGraphObject instanceof Menu
                || sceneGraphObject instanceof MenuItem
                || sceneGraphObject instanceof Tab
                || sceneGraphObject instanceof TableColumn
                || sceneGraphObject instanceof Text
                || sceneGraphObject instanceof TextInputControl
                || sceneGraphObject instanceof TitledPane
                || sceneGraphObject instanceof Tooltip
                || sceneGraphObject instanceof TreeTableColumn
                || sceneGraphObject instanceof Stage;
    }
    
    public boolean isResourceKey() {
        if (hasDescription()) { // (1)
            // Retrieve the unresolved description
            final PropertyName propertyName = getPropertyNameForDescription();
            assert propertyName != null; // Because of (1)
            assert fxomObject instanceof FXOMInstance; // Because of (1)
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final ValuePropertyMetadata vpm
                    = Metadata.getMetadata().queryValueProperty(fxomInstance, propertyName);
            final Object description = vpm.getValueObject(fxomInstance); // unresolved value
            final PrefixedValue pv = new PrefixedValue(description.toString());
            return pv.isResourceKey();
        }
        return false;
    }

    public boolean isFreeChildPositioning() {
        boolean result = false;
        if (fxomObject instanceof FXOMInstance fxomInstance) {
            final Class<?> componentClass = fxomInstance.getDeclaredClass();
            result = componentClass == AnchorPane.class
                    || componentClass == Group.class
                    || componentClass == Pane.class;
        }
        return result;
    }

    public boolean isAcceptingAccessory(Accessory accessory) {
        final Object sceneGraphObject = fxomObject.getSceneGraphObject();
        if (!accessory.isAccepting().test(sceneGraphObject) || !isExternalAccepting(accessory, sceneGraphObject)) {
            return false;
        }
        return isAcceptingProperty(accessory.propertyName(), accessory.classForAccessory());
    }

    /**
     * Returns true if this mask accepts the specified fxomObject as accessory.
     *
     * @param accessory
     * @param fxomObject
     * @return
     */
    public boolean isAcceptingAccessory(final Accessory accessory, final FXOMObject fxomObject) {
        final Object sceneGraphObject;
        if (fxomObject instanceof FXOMIntrinsic fxomIntrinsic) {
            sceneGraphObject = fxomIntrinsic.getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }
        return isAcceptingAccessory(accessory) && accessory.classForAccessory().isInstance(sceneGraphObject);
    }

    public FXOMObject getAccessory(Accessory accessory) {
        assert isAcceptingAccessory(accessory);
        assert fxomObject instanceof FXOMInstance;

        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final PropertyName propertyName = getPropertyNameForAccessory(accessory);
        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(propertyName);
        final FXOMObject result;

        if (fxomProperty instanceof FXOMPropertyC fxomPropertyC) {
            assert !fxomPropertyC.getValues().isEmpty() : "accessory=" + accessory;
            result = fxomPropertyC.getValues().get(0);
        } else {
            result = null;
        }

        return result;
    }

    public boolean isAcceptingSubComponent() {
        final PropertyName propertyName = getSubComponentPropertyName();
        return propertyName != null;
    }

    /**
     * Returns true if this mask accepts the specified sub component.
     *
     * @param obj
     * @return
     */
    public boolean isAcceptingSubComponent(FXOMObject obj) {
        final boolean result;

        assert obj != null;

        final PropertyName propertyName = getSubComponentPropertyName();
        if (propertyName == null) {
            result = false;
        } else {
            queryPropertyMetadata();
            final ComponentPropertyMetadata subComponentMetadata
                    = propertyMetadataMap.get(propertyName);
            assert subComponentMetadata != null;
            final Class<?> subComponentClass
                    = subComponentMetadata.getClassMetadata().getKlass();
            final Object sceneGraphObject;
            if (obj instanceof FXOMIntrinsic) {
                sceneGraphObject = ((FXOMIntrinsic) obj).getSourceSceneGraphObject();
            } else {
                sceneGraphObject = obj.getSceneGraphObject();
            }
            result = subComponentClass.isInstance(sceneGraphObject);
        }

        return result;
    }

    /**
     * Returns true if this mask accepts the specified sub components.
     *
     * @param fxomObjects
     * @return
     */
    public boolean isAcceptingSubComponent(final Collection<FXOMObject> fxomObjects) {
        final PropertyName propertyName = getSubComponentPropertyName();
        if (propertyName != null) {
            queryPropertyMetadata();
            final ComponentPropertyMetadata subComponentMetadata
                    = propertyMetadataMap.get(propertyName);
            assert subComponentMetadata != null;
            final Class<?> subComponentClass
                    = subComponentMetadata.getClassMetadata().getKlass();
            for (FXOMObject obj : fxomObjects) {
                final Object sceneGraphObject;
                if (obj instanceof FXOMIntrinsic intrinsicObj) {
                    sceneGraphObject = intrinsicObj.getSourceSceneGraphObject();
                } else {
                    sceneGraphObject = obj.getSceneGraphObject();
                }
                if (!subComponentClass.isInstance(sceneGraphObject)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public PropertyName getSubComponentPropertyName() {
        final Object sceneGraphObject = fxomObject.getSceneGraphObject();
        final PropertyName result;

        if (fxomObject instanceof FXOMCollection) {
            result = null;
        } else if (sceneGraphObject == null) {
            // An unresolved has no subcomponent
            result = null;
        } else {
            final Class<?> componentClass = sceneGraphObject.getClass();
            final ComponentClassMetadata componentClassMetadata
                    = Metadata.getMetadata().queryComponentMetadata(componentClass);
            assert componentClassMetadata != null;
            result = componentClassMetadata.getSubComponentProperty();
        }

        return result;
    }

    public int getSubComponentCount() {
        final PropertyName name = getSubComponentPropertyName();
        return (name == null) ? 0 : getSubComponents().size();
    }

    public FXOMObject getSubComponentAtIndex(int i) {
        assert 0 <= i;
        assert i < getSubComponentCount();
        assert getSubComponentPropertyName() != null;

        return getSubComponents().get(i);
    }
    
    public List<FXOMObject> getSubComponents() {

        assert getSubComponentPropertyName() != null;
        assert fxomObject instanceof FXOMInstance;

        final PropertyName subComponentPropertyName = getSubComponentPropertyName();
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final FXOMProperty fxomProperty
                = fxomInstance.getProperties().get(subComponentPropertyName);

        final List<FXOMObject> result;
        if (fxomProperty instanceof FXOMPropertyC) {
            result = ((FXOMPropertyC) fxomProperty).getValues();
        } else {
            result = Collections.emptyList();
        }

        return result;
    }

    public PropertyName getPropertyNameForDescription() {
        final Object sceneGraphObject = fxomObject.getSceneGraphObject();
        if (sceneGraphObject == null) {
            return null;
        }
        PropertyName propertyName = null;
        if (sceneGraphObject instanceof ComboBox) {
            propertyName = new PropertyName("promptText");
        } else if (sceneGraphObject instanceof Labeled
                || sceneGraphObject instanceof Menu
                || sceneGraphObject instanceof MenuItem
                || sceneGraphObject instanceof Tab
                || sceneGraphObject instanceof TableColumn
                || sceneGraphObject instanceof TextInputControl
                || sceneGraphObject instanceof TitledPane
                || sceneGraphObject instanceof Text
                || sceneGraphObject instanceof Tooltip
                || sceneGraphObject instanceof TreeTableColumn) {
            propertyName = new PropertyName("text");
        } else if (sceneGraphObject instanceof Stage) {
            propertyName = new PropertyName("title");
        }
        return propertyName;
    }

    public PropertyName getPropertyNameForAccessory(Accessory accessory) {
        return accessory.propertyName();
    }

    /*
     * Private
     */
    private boolean isAcceptingProperty(PropertyName propertyName, Class<?> valueClass) {
        final ComponentPropertyMetadata cpm;
        final boolean result;

        queryPropertyMetadata();
        cpm = propertyMetadataMap.get(propertyName);
        if (cpm == null) {
            result = false;
        } else {
            result = valueClass.isAssignableFrom(cpm.getClassMetadata().getKlass());
        }

        return result;
    }

    public FXOMPropertyC getAccessoryProperty(Accessory accessory) {

        assert getPropertyNameForAccessory(accessory) != null;
        assert fxomObject instanceof FXOMInstance;

        final PropertyName accessoryPropertyName = getPropertyNameForAccessory(accessory);
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final FXOMProperty result
                = fxomInstance.getProperties().get(accessoryPropertyName);

        assert (result == null) || (result instanceof FXOMPropertyC);

        return (FXOMPropertyC) result;
    }

    private void queryPropertyMetadata() {
        if (propertyMetadataMap == null) {
            propertyMetadataMap = new HashMap<>();
            if (fxomObject instanceof FXOMInstance fxomInstance) {
                if (fxomInstance.getSceneGraphObject() != null) {
                    final Class<?> componentClass = fxomInstance.getSceneGraphObject().getClass();
                    for (ComponentPropertyMetadata cpm : Metadata.getMetadata().queryComponentProperties(componentClass)) {
                        propertyMetadataMap.put(cpm.getName(), cpm);
                    }
                }
            }
        }

        assert propertyMetadataMap != null;
    }

    /**
     * Returns the number of columns constraints for this GridPane mask.
     *
     * @return the number of columns constraints
     */
    public int getColumnsConstraintsSize() {
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final PropertyName propertyName = new PropertyName("columnConstraints"); //NOI18N
        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(propertyName);
        
        final int result;
        if (fxomProperty == null) {
            result = 0;
        } else {
            assert fxomProperty instanceof FXOMPropertyC; // ie cannot be written as an XML attribute
            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
            result = fxomPropertyC.getValues().size();
        }
        
        return result;
    }

    /**
     * Returns the number of rows constraints for this GridPane mask.
     *
     * @return the number of rows constraints
     */
    public int getRowsConstraintsSize() {
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final PropertyName propertyName = new PropertyName("rowConstraints"); //NOI18N
        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(propertyName);
        
        final int result;
        if (fxomProperty == null) {
            result = 0;
        } else {
            assert fxomProperty instanceof FXOMPropertyC; // ie cannot be written as an XML attribute
            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
            result = fxomPropertyC.getValues().size();
        }
        
        return result;
    }

    /**
     * Returns the number of columns for this GridPane mask.
     * The number of columns for a GridPane is the max of :
     * - the number of column constraints
     * - the max column index defined in this GridPane children + 1
     *
     * @return the number of columns
     */
    public int getColumnsSize() {
        final Object sceneGraphObject;
        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic) {
            sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }
        assert sceneGraphObject instanceof GridPane;
        return Deprecation.getGridPaneColumnCount((GridPane) sceneGraphObject);
    }

    /**
     * Returns the number of rows for this GridPane mask.
     * The number of rows for a GridPane is the max of :
     * - the number of row constraints
     * - the max row index defined in this GridPane children + 1
     *
     * @return the number of rows
     */
    public int getRowsSize() {
        final Object sceneGraphObject;
        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic) {
            sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }
        assert sceneGraphObject instanceof GridPane;
        return Deprecation.getGridPaneRowCount((GridPane) sceneGraphObject);
    }
    
    public List<FXOMObject> getColumnContentAtIndex(int index) {
        assert 0 <= index;
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final List<FXOMObject> result = new ArrayList<>();
        for (int i = 0, count = getSubComponentCount(); i < count; i++) {
            final FXOMObject childObject = getSubComponentAtIndex(i);
            final DesignHierarchyMask childMask = new DesignHierarchyMask(childObject);
            if (childMask.getColumnIndex() == index) {
                result.add(childObject);
            }
        }
        return result;
    }

    public List<FXOMObject> getRowContentAtIndex(int index) {
        assert 0 <= index;
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final List<FXOMObject> result = new ArrayList<>();
        for (int i = 0, count = getSubComponentCount(); i < count; i++) {
            final FXOMObject childObject = getSubComponentAtIndex(i);
            final DesignHierarchyMask childMask = new DesignHierarchyMask(childObject);
            if (childMask.getRowIndex() == index) {
                result.add(childObject);
            }
        }
        return result;
    }

    public FXOMObject getColumnConstraintsAtIndex(int index) {

        assert 0 <= index;
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        FXOMObject result = null;

        // Retrieve the constraints property
        final PropertyName propertyName = new PropertyName("columnConstraints"); //NOI18N
        final FXOMProperty constraintsProperty
                = fxomInstance.getProperties().get(propertyName);

        if (constraintsProperty != null) {
            assert constraintsProperty instanceof FXOMPropertyC;
            final List<FXOMObject> constraintsValues
                    = ((FXOMPropertyC) constraintsProperty).getValues();
            if (index < constraintsValues.size()) {
                result = constraintsValues.get(index);
            }
        }

        return result;
    }

    public FXOMObject getRowConstraintsAtIndex(int index) {

        assert 0 <= index;
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        FXOMObject result = null;

        // Retrieve the constraints property
        final PropertyName propertyName = new PropertyName("rowConstraints"); //NOI18N
        final FXOMProperty constraintsProperty
                = fxomInstance.getProperties().get(propertyName);

        if (constraintsProperty != null) {
            assert constraintsProperty instanceof FXOMPropertyC;
            final List<FXOMObject> constraintsValues
                    = ((FXOMPropertyC) constraintsProperty).getValues();
            if (index < constraintsValues.size()) {
                result = constraintsValues.get(index);
            }
        }

        return result;
    }

    /**
     * Returns the column index for this GridPane child mask.
     *
     * @return the column index
     */
    public int getColumnIndex() {
        int result = 0;
        if (fxomObject instanceof FXOMInstance fxomInstance) {
            assert fxomObject.getSceneGraphObject() != null;
            result = getIndexFromGrid(fxomInstance, "columnIndex");
        } else if(fxomObject instanceof FXOMIntrinsic fxomIntrinsic) {
            FXOMInstance fxomInstance = fxomIntrinsic.createFxomInstanceFromIntrinsic();
            result = getIndexFromGrid(fxomInstance, "columnIndex");
        }
        return result;
    }

    /**
     * Returns the row index for this GridPane child mask.
     *
     * @return the row index
     */
    public int getRowIndex() {
        int result = 0;
        if (fxomObject instanceof FXOMInstance fxomInstance) {
            assert fxomObject.getSceneGraphObject() != null;
            result = getIndexFromGrid(fxomInstance, "rowIndex");
        } else if(fxomObject instanceof FXOMIntrinsic fxomIntrinsic) {
            FXOMInstance fxomInstance = fxomIntrinsic.createFxomInstanceFromIntrinsic();
            result = getIndexFromGrid(fxomInstance, "rowIndex");
        }
        return result;
    }

    private int getIndexFromGrid(final FXOMInstance fxomInstance, final String columnOrRow) {
        int result;
        final FXOMObject parentFxomObject = fxomInstance.getParentObject();
        assert parentFxomObject.getSceneGraphObject() instanceof GridPane;

        final PropertyName propertyName
                = new PropertyName(columnOrRow, GridPane.class); //NOI18N
        final ValuePropertyMetadata vpm
                = Metadata.getMetadata().queryValueProperty(fxomInstance, propertyName);
        final Object value = vpm.getValueObject(fxomInstance);
        // TODO : when DTL-5920 will be fixed, the null check will become unecessary
        if (value == null) {
            result = 0;
        } else {
            assert value instanceof Integer;
            result = ((Integer) value);
        }
        return result;
    }


    // Should be in a shared Utils class ?
    public static boolean containsLineFeed(String str) {
        // LF (\n) is used for files generated on UNIX
        // CR+LF (\r\n) is used for files generated on WINDOWS
        // So in both cases, a file containing multi lines will contain LF
        if (str == null) {
            return false;
        }
        return str.contains("\n"); //NOI18N
    }
    
    /**
     * 
     * @return true if the mask deserves a resizing while used as top element of
     * the layout.
     */
    public boolean needResizeWhenTopElement() {
        Object sceneGraphObject = fxomObject.getSceneGraphObject();
        return (isAcceptingSubComponent()
                || isAcceptingAccessory(Accessory.CONTENT)
                || isAcceptingAccessory(Accessory.ROOT)
                || isAcceptingAccessory(Accessory.SCENE)
                || isAcceptingAccessory(Accessory.CENTER)
                || isAcceptingAccessory(Accessory.TOP)
                || isAcceptingAccessory(Accessory.RIGHT)
                || isAcceptingAccessory(Accessory.BOTTOM)
                || isAcceptingAccessory(Accessory.LEFT))
            && !(sceneGraphObject instanceof MenuButton
                    || sceneGraphObject instanceof MenuBar
                    || sceneGraphObject instanceof ToolBar
                    || isExternalNonResizable(sceneGraphObject));
    }

    // External providers
    private final Collection<ExternalDesignHierarchyMaskProvider> externalDesignHierarchyMaskProviders = getExternalDesignHierarchyMaskProviders();
    private final Collection<ExternalSectionProvider> externalItemProviders = getExternalItemProviders();

    private boolean isExternalNonResizable(Object object) {
        for (ExternalDesignHierarchyMaskProvider provider : externalDesignHierarchyMaskProviders) {
            for (Class<?> item : provider.getExternalNonResizableItems()) {
                if (item.isInstance(object)) {
                    // if we have any match with one external class, object is non-resizable
                    return true;
                }
            }
        }
        // no matches, object is resizable
        return false;
    }

    private List<DesignHierarchyMask.Accessory> getExternalAccessories() {
        List<DesignHierarchyMask.Accessory> result = new ArrayList<>();
        for (ExternalDesignHierarchyMaskProvider provider : externalDesignHierarchyMaskProviders) {
            result.addAll(provider.getExternalAccessories());
        }
        return result;
    }

    private boolean isExternalAccepting(Accessory accessory, Object object) {
        for (ExternalDesignHierarchyMaskProvider provider : externalDesignHierarchyMaskProviders) {
            if (!provider.isExternalAccepting(accessory).test(object)) {
                // if we have any match with one external predicate not accepting the object, then the accessory is not accepting
                return false;
            }
        }
        // no matches, accessory accepts object
        return true;
    }

    public Map<DesignHierarchyMask.Accessory, BiFunction<DesignHierarchyMask, FXOMObject, HierarchyItem>> getExternalHierarchyItemGeneratorMap() {
        Map<DesignHierarchyMask.Accessory, BiFunction<DesignHierarchyMask, FXOMObject, HierarchyItem>> map = new HashMap<>();
        for (ExternalDesignHierarchyMaskProvider provider : externalDesignHierarchyMaskProviders) {
            map.putAll(provider.getExternalHierarchyItemGeneratorMap());
        }
        return map;
    }

    private URL findExternalItemImage(Class<?> clazz) {
        for (ExternalSectionProvider provider : externalItemProviders) {
            for (Class<?> item : provider.getExternalSectionItems()) {
                if (item == clazz) {
                    URL iconURL = provider.getClass().getResource(provider.getItemsIconPath() + "/" + item.getSimpleName() + ".png");
                    assert iconURL != null;
                    return iconURL;
                }
            }
        }
        return null;
    }

    private Collection<ExternalDesignHierarchyMaskProvider> getExternalDesignHierarchyMaskProviders() {
        ServiceLoader<ExternalDesignHierarchyMaskProvider> loader = ServiceLoader.load(ExternalDesignHierarchyMaskProvider.class);
        Collection<ExternalDesignHierarchyMaskProvider> providers = new ArrayList<>();
        loader.iterator().forEachRemaining(providers::add);
        return providers;
    }

    private Collection<ExternalSectionProvider> getExternalItemProviders() {
        ServiceLoader<ExternalSectionProvider> loader = ServiceLoader.load(ExternalSectionProvider.class);
        Collection<ExternalSectionProvider> providers = new ArrayList<>();
        loader.iterator().forEachRemaining(providers::add);
        return providers;
    }
}
