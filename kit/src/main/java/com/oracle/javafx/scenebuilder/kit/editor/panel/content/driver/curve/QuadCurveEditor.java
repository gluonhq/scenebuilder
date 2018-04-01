/*
 * Copyright (c) 2018, Gluon and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND VERTEX EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR VERTEX DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON VERTEX
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN VERTEX WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.curve;

import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.EditCurveGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides.EditCurveGuideController;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.kit.util.MathUtils;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.shape.QuadCurve;

public class QuadCurveEditor extends AbstractCurveEditor<QuadCurve> {

    private final double originalStartX;
    private final double originalStartY;
    private final double originalControlX;
    private final double originalControlY;
    private final double originalEndX;
    private final double originalEndY;
    
    private final PropertyName startXName = new PropertyName("startX"); //NOI18N
    private final PropertyName startYName = new PropertyName("startY"); //NOI18N
    private final PropertyName controlXName = new PropertyName("controlX"); //NOI18N
    private final PropertyName controlYName = new PropertyName("controlY"); //NOI18N
    private final PropertyName endXName = new PropertyName("endX"); //NOI18N
    private final PropertyName endYName = new PropertyName("endY"); //NOI18N
    private final List<PropertyName> propertyNames = new ArrayList<>();

    public QuadCurveEditor(QuadCurve sceneGraphObject) {
        super(sceneGraphObject);

        originalStartX = sceneGraphObject.getStartX();
        originalStartY = sceneGraphObject.getStartY();
        originalControlX = sceneGraphObject.getControlX();
        originalControlY = sceneGraphObject.getControlY();
        originalEndX = sceneGraphObject.getEndX();
        originalEndY = sceneGraphObject.getEndY();

        propertyNames.add(startXName);
        propertyNames.add(startYName);
        propertyNames.add(controlXName);
        propertyNames.add(controlYName);
        propertyNames.add(endXName);
        propertyNames.add(endYName);
    }
    
    @Override
    public EditCurveGuideController createController(EnumMap<EditCurveGesture.Tunable, Integer> tunableMap) {
        final EditCurveGuideController result;
        if (tunableMap.containsKey(EditCurveGesture.Tunable.START)) {
            result = new EditCurveGuideController();
            Point2D point = sceneGraphObject.localToScene(sceneGraphObject.getControlX(), sceneGraphObject.getControlY(), true);
            result.addCurvePoint(point);
            point = sceneGraphObject.localToScene(sceneGraphObject.getEndX(), sceneGraphObject.getEndY(), true);
            result.addCurvePoint(point);
        } else if (tunableMap.containsKey(EditCurveGesture.Tunable.CONTROL1)) {
            result = new EditCurveGuideController();
            Point2D point = sceneGraphObject.localToScene(sceneGraphObject.getStartX(), sceneGraphObject.getStartY(), true);
            result.addCurvePoint(point);
            point = sceneGraphObject.localToScene(sceneGraphObject.getEndX(), sceneGraphObject.getEndY(), true);
            result.addCurvePoint(point);
        } else if (tunableMap.containsKey(EditCurveGesture.Tunable.END)) {
            result = new EditCurveGuideController();
            Point2D point = sceneGraphObject.localToScene(sceneGraphObject.getStartX(), sceneGraphObject.getStartY(), true);
            result.addCurvePoint(point);
            point = sceneGraphObject.localToScene(sceneGraphObject.getControlX(), sceneGraphObject.getControlY(), true);
            result.addCurvePoint(point);
        } else {
            // Emergency code
            result = null;
        }

        return result;
    }
    
    @Override
    public void moveTunable(EnumMap<EditCurveGesture.Tunable, Integer> tunableMap, double newX, double newY) {
        if (tunableMap.containsKey(EditCurveGesture.Tunable.START)) {
            sceneGraphObject.setStartX(newX);
            sceneGraphObject.setStartY(newY);            
        } else if (tunableMap.containsKey(EditCurveGesture.Tunable.CONTROL1)) {
            sceneGraphObject.setControlX(newX);
            sceneGraphObject.setControlY(newY);            
        } else if (tunableMap.containsKey(EditCurveGesture.Tunable.END)) {
            sceneGraphObject.setEndX(newX);
            sceneGraphObject.setEndY(newY);            
        }
    }

    @Override
    public void revertToOriginalState() {
        sceneGraphObject.setStartX(originalStartX);
        sceneGraphObject.setStartY(originalStartY);
        sceneGraphObject.setControlX(originalControlX);
        sceneGraphObject.setControlY(originalControlY);
        sceneGraphObject.setEndX(originalEndX);
        sceneGraphObject.setEndY(originalEndY);
    }

    @Override
    public List<PropertyName> getPropertyNames() {
        return propertyNames;
    }

    @Override
    public Object getValue(PropertyName propertyName) {
        assert propertyName != null;
        assert propertyNames.contains(propertyName);

        final Object result;
        if (propertyName.equals(startXName)) {
            result = sceneGraphObject.getStartX();
        } else if (propertyName.equals(startYName)) {
            result = sceneGraphObject.getStartY();
        } else if (propertyName.equals(controlXName)) {
            result = sceneGraphObject.getControlX();
        } else if (propertyName.equals(controlYName)) {
            result = sceneGraphObject.getControlY();
        } else if (propertyName.equals(endXName)) {
            result = sceneGraphObject.getEndX();
        } else if (propertyName.equals(endYName)) {
            result = sceneGraphObject.getEndY();
        } else {
            // Emergency code
            result = null;
        }

        return result;
    }

    @Override
    public Map<PropertyName, Object> getChangeMap() {
        final Map<PropertyName, Object> result = new HashMap<>();
        if (!MathUtils.equals(sceneGraphObject.getStartX(), originalStartX)) {
            result.put(startXName, sceneGraphObject.getStartX());
        }
        if (!MathUtils.equals(sceneGraphObject.getStartY(), originalStartY)) {
            result.put(startYName, sceneGraphObject.getStartY());
        }
        if (!MathUtils.equals(sceneGraphObject.getControlX(), originalControlX)) {
            result.put(controlXName, sceneGraphObject.getControlX());
        }
        if (!MathUtils.equals(sceneGraphObject.getControlY(), originalControlY)) {
            result.put(controlYName, sceneGraphObject.getControlY());
        }
        if (!MathUtils.equals(sceneGraphObject.getEndX(), originalEndX)) {
            result.put(endXName, sceneGraphObject.getEndX());
        }
        if (!MathUtils.equals(sceneGraphObject.getEndY(), originalEndY)) {
            result.put(endYName, sceneGraphObject.getEndY());
        }
        return result;
    }

    @Override
    public List<Double> getPoints() {
        return null;
    }

    @Override
    public void addPoint(EnumMap<EditCurveGesture.Tunable, Integer> tunableMap, double newX, double newY) {
    }

    @Override
    public void removePoint(EnumMap<EditCurveGesture.Tunable, Integer> tunableMap) {
    }

}