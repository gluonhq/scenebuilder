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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.curve.AbstractCurveEditor;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides.EditCurveGuideController;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.metadata.Metadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * 
 */
public class EditCurveGesture extends AbstractMouseGesture {
    
    private final FXOMInstance fxomInstance;
    
    private AbstractCurveEditor<?> editor;
    private EditCurveGuideController controller;

    private boolean straightAnglesMode = false;
    
    public enum Tunable {
        START,
        END,
        CONTROL1,
        CONTROL2
    }
    
    private final Tunable tunable;

    public EditCurveGesture(ContentPanelController contentPanelController, FXOMInstance fxomInstance, Tunable tunable) {
        super(contentPanelController);
        assert contentPanelController.lookupDriver(fxomInstance) != null;
        assert fxomInstance.getSceneGraphObject() instanceof Node;
        this.fxomInstance = fxomInstance;
        this.tunable = tunable;
    }

    public Tunable getTunable() {
        return tunable;
    }
    
    /*
     * AbstractMouseGesture
     */
    
    @Override
    protected void mousePressed() {
        // Everthing is done in mouseDragStarted
    }

    @Override
    protected void mouseDragStarted() {
        editor = contentPanelController.lookupDriver(fxomInstance).makeCurveEditor(fxomInstance);
        assert editor != null;
        assert editor.getSceneGraphObject() == fxomInstance.getSceneGraphObject();

        controller = editor.createController(tunable);

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

        mouseDragged();
    }

    @Override
    protected void mouseDragged() {
        updateCurvePosition();
    }

    @Override
    protected void mouseDragEnded() {
        final Map<PropertyName, Object> changeMap = editor.getChangeMap();

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
    }

    @Override
    protected void mouseReleased() {
        // Everything is done in mouseDragEnded
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
    }
    
    private void updateCurvePosition() {
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
        editor.moveTunable(tunable, current.getX(), current.getY());
        sceneGraphObject.getParent().layout();
    }
    
}
