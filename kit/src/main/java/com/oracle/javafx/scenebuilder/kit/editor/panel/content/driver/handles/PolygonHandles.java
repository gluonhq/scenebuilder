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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles;

import java.util.List;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import static com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles.AbstractHandles.SELECTION_HANDLES_SIZE;
import static com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles.AbstractHandles.SELECTION_WIRE;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.AbstractGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.EditCurveGesture;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

/**
 *
 * 
 */
public class PolygonHandles extends AbstractCurveHandles<Polygon> {

    private final List<Circle> verticesHandle = new ArrayList<>();
    private final List<Line> linesHandle = new ArrayList<>();
    
    public PolygonHandles(ContentPanelController contentPanelController,
            FXOMInstance fxomInstance) {
        super(contentPanelController, fxomInstance, Polygon.class);
        
        final List<Node> rootNodeChildren = getRootNode().getChildren();
        
        setupHandles(rootNodeChildren);
    }

    public FXOMInstance getFxomInstance() {
        return (FXOMInstance) getFxomObject();
    }
    
    /*
     * AbstractCurveHandles
     */
    @Override
    protected void layoutDecoration() {
        final Polygon l = getSceneGraphObject();
        final boolean snapToPixel = true;
        
        if (l.getPoints().size() != verticesHandle.size() * 2) {
            setupHandles(getRootNode().getChildren());
        }
        if (l.getPoints().size() % 2 != 0) {
            return;
        }
        AtomicInteger counter = new AtomicInteger();
        IntStream.range(0, l.getPoints().size() / 2)
            .mapToObj(i -> l.getPoints().subList(i * 2, 2 * (i + 1)))
            .map(list -> sceneGraphObjectToDecoration(list.get(0), list.get(1), snapToPixel))
            .forEach(p -> {
                Circle c = verticesHandle.get(counter.getAndIncrement());
                c.setCenterX(p.getX());
                c.setCenterY(p.getY());
            });
        IntStream.range(0, verticesHandle.size())
                .forEach(i -> {
                    Circle c1 = verticesHandle.get(i);
                    Circle c2 = verticesHandle.get(i + 1 == verticesHandle.size() ? 0 : i + 1);
                    Line line = linesHandle.get(i);
                    line.setStartX(c1.getCenterX());
                    line.setStartY(c1.getCenterY());
                    line.setEndX(c2.getCenterX());
                    line.setEndY(c2.getCenterY());
                });
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        super.startListeningToSceneGraphObject();
        
        final Polygon l = getSceneGraphObject();
        l.getPoints().addListener(pointsListener);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        super.stopListeningToSceneGraphObject();
        
        final Polygon l = getSceneGraphObject();
        l.getPoints().removeListener(pointsListener);
    }

    @Override
    public AbstractGesture findGesture(Node node) {
        final EditCurveGesture result;
        
        if (node instanceof Circle && verticesHandle.contains((Circle) node)) {
            result = new EditCurveGesture(getContentPanelController(), getFxomInstance(), EditCurveGesture.Tunable.VERTEX);
            result.getTunableMap().put(EditCurveGesture.Tunable.VERTEX, verticesHandle.indexOf((Circle) node));
        } else if (node instanceof Line && linesHandle.contains((Line) node)) {
            result = new EditCurveGesture(getContentPanelController(), getFxomInstance(), EditCurveGesture.Tunable.SIDE);
            result.getTunableMap().put(EditCurveGesture.Tunable.SIDE, linesHandle.indexOf((Line) node));
        } else {
            result = null;
        }
        
        return result;
    }

    @Override
    public void enabledDidChange() {
        verticesHandle.forEach(this::setupHandleState);
    }
    
    /*
     * Private
     */
    
    private void setupHandles(final List<Node> rootNodeChildren) {
        verticesHandle.clear();
        linesHandle.clear();
        rootNodeChildren.clear();
        final Polygon l = getSceneGraphObject();
        IntStream.range(0, l.getPoints().size() / 2)
                .mapToObj(i -> new Line())
                .forEach(line -> {
                    linesHandle.add(line);
                    line.getStyleClass().add(SELECTION_PIPE);
                    line.setCursor(Cursor.CROSSHAIR);
                    setupHandles(line);
                    rootNodeChildren.add(line);
                });
        IntStream.range(0, l.getPoints().size() / 2)
                .mapToObj(i -> new Circle(SELECTION_HANDLES_SIZE / 2.0))
                .forEach(c -> {
                    verticesHandle.add(c);
                    setupHandleState(c);
                    setupHandles(c);
                    rootNodeChildren.add(c);
                });
    }
    
    private void setupHandleState(Circle handleCircle) {
        
        final String styleClass = isEnabled() ? SELECTION_HANDLES : SELECTION_HANDLES_DIM;
        final Cursor cursor = isEnabled() ? Cursor.OPEN_HAND : Cursor.DEFAULT;
        
        handleCircle.getStyleClass().add(styleClass);
        handleCircle.setCursor(cursor);
    }
    
    
    /* 
     * Wraper to avoid the 'leaking this in constructor' warning emitted by NB.
     */
    private void setupHandles(Node node) {
        attachHandles(node, this);
    }
}
