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

package com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class EditCurveGuideController {

    private final double MATCH_DISTANCE = 6.0;

    private final List<Point2D> curvePoints = new ArrayList<>();
    
    private final PointIndex pointIndex = new PointIndex();
    private final HorizontalLineIndex horizontalLineIndex = new HorizontalLineIndex();
    private final VerticalLineIndex verticalLineIndex = new VerticalLineIndex();
    
    public EditCurveGuideController() {
    }

    public void addCurvePoint(Point2D pointInScene) {
        assert pointInScene != null;
        curvePoints.add(pointInScene);
        pointIndex.addPoint(pointInScene);
    }

    public void addSampleBounds(Node node) {
        assert node != null;
        assert node.getScene() != null;

        final Bounds layoutBounds = node.getLayoutBounds();
        final Bounds boundsInScene = node.localToScene(layoutBounds, true /* rootScene */);
        addSampleBounds(boundsInScene, true);
    }

    public void addSampleBounds(Bounds boundsInScene, boolean addMiddle) {
        final double minX = boundsInScene.getMinX();
        final double minY = boundsInScene.getMinY();
        final double maxX = boundsInScene.getMaxX();
        final double maxY = boundsInScene.getMaxY();

        pointIndex.addPoint(new Point2D(minX, minY));
        pointIndex.addPoint(new Point2D(minX, maxY));
        pointIndex.addPoint(new Point2D(maxX, minY));
        pointIndex.addPoint(new Point2D(maxX, maxY));
        
        if (addMiddle) {
            final double midX = (minX + maxX) / 2.0;
            final double midY = (minY + maxY) / 2.0;

            pointIndex.addPoint(new Point2D(midX, midY));
            pointIndex.addPoint(new Point2D(midX, minY));
            pointIndex.addPoint(new Point2D(midX, maxY));
            pointIndex.addPoint(new Point2D(minX, midY));
            pointIndex.addPoint(new Point2D(maxX, midY));
        }

        horizontalLineIndex.addLine(new HorizontalSegment(minX, maxX, minY));
        horizontalLineIndex.addLine(new HorizontalSegment(minX, maxX, maxY));
        verticalLineIndex.addLine(new VerticalSegment(minX, minY, maxY));
        verticalLineIndex.addLine(new VerticalSegment(maxX, minY, maxY));
    }

    public Point2D correct(Point2D point) {
        assert point != null;
        
        double x = point.getX();
        double y = point.getY();

        final List<Point2D> matchedPoints = pointIndex.match(point, MATCH_DISTANCE);
        final List<HorizontalSegment> horizontalMatchingLines = horizontalLineIndex.matchPoint(point, MATCH_DISTANCE);
        final List<VerticalSegment> verticalMatchedLines = verticalLineIndex.matchPoint(point, MATCH_DISTANCE);
        
        if (!matchedPoints.isEmpty()) {
            return matchedPoints.get(0);
        }
        if (!horizontalMatchingLines.isEmpty()) {
            final HorizontalSegment line = horizontalMatchingLines.get(0);
            y = line.getY1();
        }
        if (!verticalMatchedLines.isEmpty()) {
            final VerticalSegment line = verticalMatchedLines.get(0);
            x = line.getX1();
        }

        for (Point2D curvePoint : curvePoints) {
            if (Math.abs(point.getX() - curvePoint.getX()) < MATCH_DISTANCE) {
                x = curvePoint.getX();
            }
            if (Math.abs(point.getY() - curvePoint.getY()) < MATCH_DISTANCE) {
                y = curvePoint.getY();
            }
        }
        return new Point2D(x, y);
    }

    public Point2D makeStraightAngles(Point2D point) {
        assert point != null;

        double x = point.getX();
        double y = point.getY();

        for (Point2D curvePoint : curvePoints) {
            if (Math.abs(point.getX() - curvePoint.getX()) > Math.abs(point.getY() - curvePoint.getY())) {
                y = curvePoint.getY();
            } else {
                x = curvePoint.getX();
            }
        }
        return new Point2D(x, y);
    }
    
}
