/*
 * Copyright (c) 2018, 2024, Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.kit.editor.panel.content.util;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;

public class DistanceUtils {

    DistanceUtils() {
        // no-op
    }

    /**
     * Calculates distance from point to line
     * 
     * @param point target point
     * @param line target line
     * @return distance
     */
    public static double getDistFromPointToLine(Point2D point, Line line) {
        double x = point.getX();
        double y = point.getY();
        
        double x0 = line.getStartX();
        double y0 = line.getStartY();
        double x1 = line.getEndX();
        double y1 = line.getEndY();
        
        double dot0 = dot(x - x0, y - y0, x1 - x0, y1 - y0);
        if (dot0 < 0) {
            return dist(x, y, x0, y0);
        }
        
        double dot1 = dot(x - x1, y - y1, x0 - x1, y0 - y1);
        if (dot1 < 0) {
            return dist(x, y, x1, y1);
        }
        
        return Math.abs((y0 - y1) * x + (x1 - x0) * y + (x0 * y1 - x1 * y0)) / dist(x0, y0, x1, y1);
    }
    
    private static double dot(double x1, double y1, double x2, double y2) {
        return x1 * x2 + y1 * y2;
    }
    
    private static double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
}
