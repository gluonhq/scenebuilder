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

package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.curve;

import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.EditCurveGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides.EditCurveGuideController;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import java.util.EnumMap;
import javafx.scene.Node;

import java.util.List;
import java.util.Map;

public abstract class AbstractCurveEditor<T extends Node> {

    protected final T sceneGraphObject;

    public AbstractCurveEditor(T sceneGraphObject) {
        assert sceneGraphObject != null;
        this.sceneGraphObject = sceneGraphObject;
    }

    public T getSceneGraphObject() {
        return sceneGraphObject;
    }
    
    public abstract EditCurveGuideController createController(EnumMap<EditCurveGesture.Tunable, Integer> tunableMap);
    
    public abstract void moveTunable(EnumMap<EditCurveGesture.Tunable, Integer> tunableMap, double newX, double newY);
    public abstract void revertToOriginalState();

    public abstract List<PropertyName> getPropertyNames();
    public abstract Object getValue(PropertyName propertyName);
    public abstract Map<PropertyName, Object> getChangeMap();
    
    public abstract List<Double> getPoints();
    public abstract void addPoint(EnumMap<EditCurveGesture.Tunable, Integer> tunableMap, double newX, double newY);
    public abstract void removePoint(EnumMap<EditCurveGesture.Tunable, Integer> tunableMap);
}
