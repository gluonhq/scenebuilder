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

import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.EditCurveGesture.Tunable;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides.EditCurveGuideController;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;

public class PolylineEditor extends AbstractCurveEditor<Polyline> {

    private final List<Double> originalPoints;
    
    private final List<PropertyName> propertyNames = new ArrayList<>();

    private int vertexIndex = -1;
    
    public PolylineEditor(Polyline sceneGraphObject) {
        super(sceneGraphObject);

        originalPoints = new ArrayList<>(sceneGraphObject.getPoints());
    }
    
    @Override
    public EditCurveGuideController createController(EnumMap<Tunable, Integer> tunableMap) {
        final EditCurveGuideController result = new EditCurveGuideController();
        vertexIndex = -1;
        if (tunableMap.containsKey(Tunable.VERTEX)) {
            vertexIndex = tunableMap.get(Tunable.VERTEX);
        }
        final ObservableList<Double> points = sceneGraphObject.getPoints();
        IntStream.range(0, points.size() / 2)
                .filter(i -> i != vertexIndex)
                .mapToObj(i -> points.subList(i * 2, 2 * (i + 1)))
                .map(list -> sceneGraphObject.localToScene(list.get(0), list.get(1), true))
                .forEach(result::addCurvePoint);
        return result;
    }
    
    @Override
    public void moveTunable(EnumMap<Tunable, Integer> tunableMap, double newX, double newY) {
        Integer index = tunableMap.get(Tunable.VERTEX);
        if (index != null && index > -1 && index < sceneGraphObject.getPoints().size() / 2) {
            sceneGraphObject.getPoints().set(2 * index, newX);
            sceneGraphObject.getPoints().set(2 * index + 1, newY);
        }
    }

    @Override
    public void revertToOriginalState() {
        sceneGraphObject.getPoints().setAll(originalPoints);
    }

    @Override
    public List<PropertyName> getPropertyNames() {
        return propertyNames;
    }

    @Override
    public Object getValue(PropertyName propertyName) {
        assert propertyName != null;
        assert propertyNames.contains(propertyName);
        return null;
    }

    @Override
    public Map<PropertyName, Object> getChangeMap() {
        return new HashMap<>();
    }
    
    @Override
    public List<Double> getPoints() {
        return sceneGraphObject.getPoints();
    }

    @Override
    public void addPoint(EnumMap<Tunable, Integer> tunableMap, double newX, double newY) {
        Integer index = tunableMap.get(Tunable.SIDE);
        if (index != null) {
            index += 1;
            if (index > -1 && index < sceneGraphObject.getPoints().size() / 2) {
                sceneGraphObject.getPoints().add(2 * index, newY);
                sceneGraphObject.getPoints().add(2 * index, newX);
            }
        }
    }

    @Override
    public void removePoint(EnumMap<Tunable, Integer> tunableMap) {
        Integer index = tunableMap.get(Tunable.VERTEX);
        if (index != null && index > -1 && index < sceneGraphObject.getPoints().size() / 2) {
            sceneGraphObject.getPoints().remove(2 * index + 1);
            sceneGraphObject.getPoints().remove(2 * index);
        }
    }

}