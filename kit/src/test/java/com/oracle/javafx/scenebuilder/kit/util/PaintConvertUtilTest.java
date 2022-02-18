/*
 * Copyright (c) 2022, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.util;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaintConvertUtilTest {

    @Test
    public void testConvertPaintToCss() {
        //1. convert color to css
        Color c1 = Color.rgb(255, 255, 255);
        String css1 = PaintConvertUtil.convertPaintToCss(c1);
        assertEquals("#ffffff", css1);
        Color c2 = Color.rgb(108, 178, 225, 0.62);
        String css2 = PaintConvertUtil.convertPaintToCss(c2);
        assertEquals("#6cb2e19e", css2);
        //2. convert LinearGradient to css
        LinearGradient lg1 = new LinearGradient(
                0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, new Color(0.14, 0.82, 0.95, 1.0)),
                new Stop(0.5, new Color(0.84, 0.57, 0.98, 1.0)),
                new Stop(1.0, new Color(1.0, 0.48, 0.52, 1.0)));
        String cssLg1 = PaintConvertUtil.convertPaintToCss(lg1);
        assertEquals("linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #24d1f2 0.0%, #d691fa 50.0%, #ff7a85 100.0%)", cssLg1);
        //3. convert RadialGradient to css
        RadialGradient rg1 = new RadialGradient(
                0.0, 0.0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE
                , new Stop(0.0, new Color(0.11, 0.52, 0.93, 1.0)),
                new Stop(1.0, new Color(0.68, 0.05, 0.93, 1.0)));
        String cssRg1 = PaintConvertUtil.convertPaintToCss(rg1);
        assertEquals("radial-gradient(focus-angle 0.0deg, focus-distance 0.0% , center 50.0% 50.0%, radius 50.0%, #1c85ed 0.0%, #ad0ded 100.0%)", cssRg1);
    }

    @Test
    public void testConvertPaintToJavaCode() {
        //1. convert color to java code
        Color c1 = Color.rgb(255, 255, 255, 1.0);
        String code1 = PaintConvertUtil.convertPaintToJavaCode(c1);
        assertEquals("Color paint = new Color(1.0, 1.0, 1.0, 1.0);", code1);
        Color c2 = new Color(0.4235, 0.698, 0.8824, 0.62);
        String code2 = PaintConvertUtil.convertPaintToJavaCode(c2);
        assertEquals("Color paint = new Color(0.4235, 0.698, 0.8824, 0.62);", code2);
        String newLine = System.lineSeparator();
        //2. convert LinearGradient to java code
        LinearGradient lg1 = new LinearGradient(
                0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, new Color(0.14, 0.82, 0.95, 1.0)),
                new Stop(0.5, new Color(0.84, 0.57, 0.98, 1.0)),
                new Stop(1.0, new Color(1.0, 0.48, 0.52, 1.0)));
        String codeLg1 = PaintConvertUtil.convertPaintToJavaCode(lg1);
        assertEquals("LinearGradient paint = new LinearGradient(" + newLine +
                "0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE," + newLine +
                "new Stop(0.0, new Color(0.14, 0.82, 0.95, 1.0))," + newLine +
                "new Stop(0.5, new Color(0.84, 0.57, 0.98, 1.0))," + newLine +
                "new Stop(1.0, new Color(1.0, 0.48, 0.52, 1.0)));", codeLg1);
        //3. convert RadialGradient to java code
        RadialGradient rg1 = new RadialGradient(
                0.0, 0.0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, new Color(0.11, 0.52, 0.93, 1.0)),
                new Stop(1.0, new Color(0.68, 0.05, 0.93, 1.0)));
        String codeRg1 = PaintConvertUtil.convertPaintToJavaCode(rg1);
        assertEquals("RadialGradient paint = new RadialGradient(" + newLine +
                "0.0, 0.0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE," + newLine +
                "new Stop(0.0, new Color(0.11, 0.52, 0.93, 1.0))," + newLine +
                "new Stop(1.0, new Color(0.68, 0.05, 0.93, 1.0)));", codeRg1);
    }
}
