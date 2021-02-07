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
package com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.popupeditors;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.util.ColorEncoder;
import com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.PaintPicker;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Rectangle;

/**
 * Popup editor for the Paint property.
 */
public class PaintPopupEditor extends PopupEditor {

    private final Rectangle graphic = new Rectangle(20, 10);
    private EditorController editorController;
    protected PaintPicker paintPicker;

    private final ChangeListener<Paint> paintChangeListener = (ov, oldValue, newValue) -> {
        // If live update, do not commit the value
        if (paintPicker.isLiveUpdate()) {
            userUpdateTransientValueProperty(newValue);
            popupMb.setText(getPreviewString(newValue));
        } else {
            commitValue(newValue);
        }
        graphic.setFill(newValue);
    };

    private final ChangeListener<Boolean> liveUpdateListener = (ov, oldValue, newValue) -> {
        if (!paintPicker.isLiveUpdate()) {
            commitValue(paintPicker.getPaintProperty());
        }
    };

    public PaintPopupEditor(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses, EditorController editorController) {
        super(propMeta, selectedClasses);
        initialize(editorController);
    }
    
    private void initialize(EditorController editorController) {
        this.editorController = editorController;
    }

    //
    // Interface from PopupEditor.
    // Methods called by PopupEditor.
    //
    @Override
    public void initializePopupContent() {
        final PaintPicker.Delegate delegate = (warningKey, arguments) -> editorController.getMessageLog().logWarningMessage(warningKey, arguments);
        paintPicker = new PaintPicker(delegate);
    }

    @Override
    public String getPreviewString(Object value) {
        if (value == null) {
            return null;
        }
        assert value instanceof Paint;
        if (value instanceof LinearGradient
                || value instanceof RadialGradient
                || value instanceof ImagePattern) {
            return value.getClass().getSimpleName();
        }
        assert value instanceof Color;
        return ColorEncoder.encodeColor((Color) value);
    }

    @Override
    public void setPopupContentValue(Object value) {
        assert value == null || value instanceof Paint;
        paintPicker.paintProperty().removeListener(paintChangeListener);
        paintPicker.liveUpdateProperty().removeListener(liveUpdateListener);
        if (value != null) {
            final Paint paint = (Paint) value;
            paintPicker.setPaintProperty(paint);
        }
        paintPicker.paintProperty().addListener(paintChangeListener);
        paintPicker.liveUpdateProperty().addListener(liveUpdateListener);
        // !! exception in case of null
        graphic.setFill((Paint) value);
    }

    @Override
    public Node getPopupContentNode() {
        return paintPicker;
    }

    public Node getPreviewGraphic(Object value) {
        Paint paintVal;
        if (value == null) {
            paintVal = null;
        } else {
            paintVal = (Paint) value;
        }
        graphic.setFill(paintVal);
        return graphic;
    }
}
