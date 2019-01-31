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
package com.oracle.javafx.scenebuilder.kit.metadata.property.value.paint;

import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.ComplexPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.DoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.util.ColorEncoder;
import com.oracle.javafx.scenebuilder.kit.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import javafx.scene.paint.Color;

/**
 * ColorPropertyMetadata helps resolve Color as a combination of red, green, blue, opacity using
 * the constructor {@link Color#Color(double, double, double, double)}.
 * 
 * The FXML representation of the same is:
 * 
 * <pre>
 * {@code
 * <Color blue="0.5" green="0.3" opacity="0.8" red="0.6" />
 * }</pre>
 */
public class ColorPropertyMetadata extends ComplexPropertyMetadata<Color> {

    private final DoublePropertyMetadata redMetadata
            = new DoublePropertyMetadata(new PropertyName("red"),
            DoublePropertyMetadata.DoubleKind.OPACITY, true, 0.0, InspectorPath.UNUSED);
    private final DoublePropertyMetadata greenMetadata
            = new DoublePropertyMetadata(new PropertyName("green"),
            DoublePropertyMetadata.DoubleKind.OPACITY, true, 0.0, InspectorPath.UNUSED);
    private final DoublePropertyMetadata blueMetadata
            = new DoublePropertyMetadata(new PropertyName("blue"),
            DoublePropertyMetadata.DoubleKind.OPACITY, true, 0.0, InspectorPath.UNUSED);
    private final DoublePropertyMetadata opacityMetadata
            = new DoublePropertyMetadata(new PropertyName("opacity"),
            DoublePropertyMetadata.DoubleKind.OPACITY, true, 1.0, InspectorPath.UNUSED);

    public ColorPropertyMetadata(PropertyName name, boolean readWrite, 
            Color defaultValue, InspectorPath inspectorPath) {
        super(name, Color.class, readWrite, defaultValue, inspectorPath);
    }

    @Override
    public FXOMInstance makeFxomInstanceFromValue(Color value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, Color.class);

        redMetadata.setValue(result, value.getRed());
        greenMetadata.setValue(result, value.getGreen());
        blueMetadata.setValue(result, value.getBlue());
        opacityMetadata.setValue(result, value.getOpacity());

        return result;
    }

    @Override
    public Color makeValueFromString(String string) {
        return Color.valueOf(string);
    }

    @Override
    public String makeStringFromValue(Color value) {
        assert value != null;
        return ColorEncoder.encodeColor(value);
    }
}
