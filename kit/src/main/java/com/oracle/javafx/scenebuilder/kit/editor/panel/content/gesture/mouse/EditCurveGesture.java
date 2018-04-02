/*
 * Copyright (c) 2018, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.HudWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.curve.AbstractCurveEditor;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles.AbstractHandles;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides.EditCurveGuideController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.util.CardinalPoint;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.metadata.Metadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import java.util.ArrayList;
import java.util.EnumMap;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 *
 * 
 */
public class EditCurveGesture extends AbstractMouseGesture {
    
    private final FXOMInstance fxomInstance;
    
    private final AbstractCurveEditor<?> editor;
    private EditCurveGuideController controller;

    private boolean straightAnglesMode = false;
    
    private static final PropertyName POINTS_NAME = new PropertyName("points"); //NOI18N
    private static final int MAX_POINTS_HUD = 24;
    
    public enum Tunable {
        START,
        END,
        CONTROL1,
        CONTROL2,
        VERTEX,
        SIDE;
    }
    
    private final EnumMap<Tunable, Integer> tunableMap = new EnumMap<>(Tunable.class);

    public EditCurveGesture(ContentPanelController contentPanelController, FXOMInstance fxomInstance, Tunable tunable) {
        super(contentPanelController);
        assert contentPanelController.lookupDriver(fxomInstance) != null;
        assert fxomInstance.getSceneGraphObject() instanceof Node;
        this.fxomInstance = fxomInstance;
        tunableMap.put(tunable, -1);
        editor = contentPanelController.lookupDriver(fxomInstance).makeCurveEditor(fxomInstance);
    }

    public EnumMap<Tunable, Integer> getTunableMap() {
        return tunableMap;
    }
    
    /*
     * AbstractMouseGesture
     */
    
    private boolean inserted, removed;
    private Point2D insertionPoint;
    
    @Override
    protected void mousePressed() {
        final MouseEvent mousePressedEvent = getMousePressedEvent();
        inserted = false;
        removed = false;
        if (tunableMap.containsKey(Tunable.SIDE) && mousePressedEvent.isShortcutDown()) {
            final double hitX = mousePressedEvent.getSceneX();
            final double hitY = mousePressedEvent.getSceneY();
            insertionPoint = editor.getSceneGraphObject().sceneToLocal(hitX, hitY, true);
            inserted = true;
        } else if (tunableMap.containsKey(Tunable.VERTEX) && mousePressedEvent.isShortcutDown()) {
            removed = true;
        } 
        updateHandle(true);
    }

    @Override
    protected void mouseDragStarted() {
        assert editor != null;
        assert editor.getSceneGraphObject() == fxomInstance.getSceneGraphObject();

        controller = editor.createController(tunableMap);

        final double hitX = getLastMouseEvent().getSceneX();
        final double hitY = getLastMouseEvent().getSceneY();
        final Set<FXOMObject> pickExcludes = new HashSet<>();
        pickExcludes.add(fxomInstance);

        FXOMObject hitParent = contentPanelController.pick(hitX, hitY, pickExcludes);
        if (hitParent == null) {
            final FXOMDocument fxomDocument
                    = contentPanelController.getEditorController().getFxomDocument();
            hitParent = fxomDocument.getFxomRoot();
        }

        assert hitParent != null;

        DesignHierarchyMask hitParentMask = new DesignHierarchyMask(hitParent);
        assert hitParentMask.isFreeChildPositioning();

        for (int i = 0, c = hitParentMask.getSubComponentCount(); i < c; i++) {
            final FXOMObject child = hitParentMask.getSubComponentAtIndex(i);
            final boolean isNode = child.getSceneGraphObject() instanceof Node;
            if (isNode && child != fxomInstance) {
                final Node childNode = (Node) child.getSceneGraphObject();
                controller.addSampleBounds(childNode);
            }
        }

        assert hitParent.getSceneGraphObject() instanceof Node;
        final Node hitParentNode = (Node) hitParent.getSceneGraphObject();
        controller.addSampleBounds(hitParentNode);
        
        setupAndOpenHudWindow();
        
        mouseDragged();
    }

    @Override
    protected void mouseDragged() {
        contentPanelController.getHudWindowController().updatePopupLocation();
        updateCurvePosition();
    }

    @Override
    protected void mouseDragEnded() {
        final Map<PropertyName, Object> changeMap = editor.getChangeMap();
        List<Double> points = null;
        if (editor.getPoints() != null) {
            points = new ArrayList<>(editor.getPoints());
        }
        userDidCancel();

        final Metadata metadata = Metadata.getMetadata();
        final Map<ValuePropertyMetadata, Object> metaValueMap = new HashMap<>();
        for (Map.Entry<PropertyName,Object> e : changeMap.entrySet()) {
            final ValuePropertyMetadata vpm = metadata.queryValueProperty(fxomInstance, e.getKey());
            assert vpm != null;
            metaValueMap.put(vpm, e.getValue());
        }
        if (!changeMap.isEmpty()) {
            final EditorController editorController
                    = contentPanelController.getEditorController();
            for (Map.Entry<ValuePropertyMetadata, Object> e : metaValueMap.entrySet()) {
                final ModifyObjectJob job = new ModifyObjectJob(
                        fxomInstance,
                        e.getKey(),
                        e.getValue(),
                        editorController,
                        "Edit");
                if (job.isExecutable()) {
                    editorController.getJobManager().push(job);
                }
            }
        }
        
        if (points != null) {
            final EditorController editorController
                    = contentPanelController.getEditorController();
            final ValuePropertyMetadata pointsMeta 
                = metadata.queryValueProperty(fxomInstance, POINTS_NAME);
            final ModifyObjectJob job = new ModifyObjectJob(fxomInstance,
                        pointsMeta,
                        points,
                        editorController);
            if (job.isExecutable()) {
                editorController.getJobManager().push(job);
            }
        }
    }

    @Override
    protected void mouseReleased() {
        updateHandle(false);
        if (removed || inserted) {
            if (removed) {
                editor.removePoint(tunableMap);
            } else if (inserted) {
                editor.addPoint(tunableMap, insertionPoint.getX(), insertionPoint.getY());
            }

            List<Double> points = null;
            if (editor.getPoints() != null) {
                points = editor.getPoints().stream().collect(Collectors.toList());
            }
            userDidCancel();
            
            final Metadata metadata = Metadata.getMetadata();
            if (points != null) {
                final EditorController editorController
                        = contentPanelController.getEditorController();
                final ValuePropertyMetadata pointsMeta 
                    = metadata.queryValueProperty(fxomInstance, POINTS_NAME);
                final ModifyObjectJob job = new ModifyObjectJob(fxomInstance,
                            pointsMeta,
                            points,
                            editorController);
                if (job.isExecutable()) {
                    editorController.getJobManager().push(job);
                }
            }
        }
    }
    
    @Override
    protected void keyEvent(KeyEvent e) {
        if (e.getCode() == KeyCode.SHIFT) {
            if (e.getEventType() == KeyEvent.KEY_PRESSED) {
                straightAnglesMode = true;
            } else if (e.getEventType() == KeyEvent.KEY_RELEASED) {
                straightAnglesMode = false;
            }
            mouseDragged();
        }
   }

    @Override
    protected void userDidCancel() {
        editor.revertToOriginalState();
        editor.getSceneGraphObject().getParent().layout();
        contentPanelController.getHudWindowController().closeWindow();
    }
    
    private void updateCurvePosition() {
        if (editor == null || controller == null) {
            return;
        }
        final Node sceneGraphObject = editor.getSceneGraphObject();
        sceneGraphObject.getParent().layout();

        final double currentSceneX = getLastMouseEvent().getSceneX();
        final double currentSceneY = getLastMouseEvent().getSceneY();
        Point2D current = new Point2D(currentSceneX, currentSceneY);
        
        if (straightAnglesMode) {
            current = controller.makeStraightAngles(current);
        } else {
            current = controller.correct(current);
        }

        current = sceneGraphObject.sceneToLocal(current.getX(), current.getY(), true);
        editor.moveTunable(tunableMap, current.getX(), current.getY());
        sceneGraphObject.getParent().layout();
        
        updateHudWindow();
    }
    
    private void updateHandle(boolean value) {
        Node hitNode = (Node) getMousePressedEvent().getTarget();
        AbstractHandles<?> hitHandles = AbstractHandles.lookupHandles(hitNode);
        while (hitHandles == null && hitNode.getParent() != null) {
            hitNode = hitNode.getParent();
            hitHandles = AbstractHandles.lookupHandles(hitNode);
        }
        if (hitNode instanceof Circle) {
            if (removed) {
                hitNode.setCursor(value ? Cursor.CROSSHAIR : Cursor.OPEN_HAND);
            } else {
                hitNode.setCursor(value ? Cursor.CLOSED_HAND : Cursor.OPEN_HAND);
            }
        } 
    }
    
    private void setupAndOpenHudWindow() {
        final HudWindowController hudWindowController = contentPanelController.getHudWindowController();
        
        final int propertiesCount = editor.getPropertyNames().size();
        final int pointsCount = editor.getPoints() != null ? Math.min(MAX_POINTS_HUD, editor.getPoints().size()) : 0;
        hudWindowController.setRowCount(propertiesCount + pointsCount);
        
        final List<PropertyName> sizePropertyNames = editor.getPropertyNames();
        for (int i = 0; i < propertiesCount; i++) {
            final PropertyName pn = sizePropertyNames.get(i);
            hudWindowController.setNameAtRowIndex(pn.getName() + ":", i);
        }
        
        for (int i = 0; i < pointsCount / 2; i++) {
            hudWindowController.setNameAtRowIndex("" + (i + 1) + ".X:", 2 * i + propertiesCount);
            hudWindowController.setNameAtRowIndex("" + (i + 1) + ".Y:", 2 * i + 1 + propertiesCount);
        }
        
        updateHudWindow();
        
        hudWindowController.setRelativePosition(CardinalPoint.E);
        hudWindowController.openWindow(editor.getSceneGraphObject());
    }
    
    private void updateHudWindow() {
        final HudWindowController hudWindowController = contentPanelController.getHudWindowController();
        final List<PropertyName> sizePropertyNames = editor.getPropertyNames();
        final int propertiesCount = sizePropertyNames.size();
        
        for (int i = 0; i < propertiesCount; i++) {
            final PropertyName pn = sizePropertyNames.get(i);
            final String value = String.valueOf(editor.getValue(pn));
            hudWindowController.setValueAtRowIndex(value, i);
        }
        
        // Limit added points to grid
        // TODO: Add another column to the grid
        final int pointsCount = editor.getPoints() != null ? Math.min(MAX_POINTS_HUD, editor.getPoints().size()) : 0;
        if (pointsCount > 0) {
            for (int i = 0; i < pointsCount; i++) {
                hudWindowController.setValueAtRowIndex(String.format("%.3f", editor.getPoints().get(i)), i + propertiesCount);
            }
        }
        
    }
    
}