/*
 * Copyright (c) 2023, Gluon and/or its affiliates.
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
package com.example.app.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CustomNodeWithInsets extends StackPane {

    private final StackPane innerPane;
    
    private final StackPane centerPane;

    /**
     * A control with various nested insets to demonstrate the insets editing capability for custom controls. 
     */
    public CustomNodeWithInsets() {
        setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        innerPane = new StackPane();
        innerPane.setBackground(new Background(new BackgroundFill(Color.ORANGE, new CornerRadii(10), Insets.EMPTY)));
        innerPane.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
        innerPane.setMinSize(100, 100);
        innerPane.setMaxSize(590, 590);
        innerPane.setPrefSize(590, 380);
        innerPane.paddingProperty().bind(innerFrameProperty());

        centerPane = new StackPane();
        centerPane.setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, new CornerRadii(10), Insets.EMPTY)));
        centerPane.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
        centerPane.setMinSize(100, 100);
        centerPane.setMaxSize(580, 570);
        centerPane.setPrefSize(580, 360);

        Label label = new Label("Custom Control Insets Demo");
        label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(4))));
        label.paddingProperty().bind(textPaddingProperty());
        label.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        label.setFont(Font.font(label.getFont().getFamily(), FontWeight.BLACK, 14));
        centerPane.getChildren().add(label);
        innerPane.getChildren().add(centerPane);

        getChildren().add(innerPane);
        setMinSize(100, 100);
        setMaxSize(600, 600);
        setPrefSize(598, 396);
        setWidth(600);
        setHeight(400);

        paddingProperty().bind(outerFrameProperty());
    }

    private ObjectProperty<Insets> outerFrame;

    public void setOuterFrame(Insets value) {
        this.outerFrameProperty().setValue(value);
    }

    public Insets getOuterFrame() {
        return this.outerFrame == null ? new Insets(90, 40, 30, 160) : outerFrameProperty().getValue();
    }

    public ObjectProperty<Insets> outerFrameProperty() {
        if (this.outerFrame != null) {
            return this.outerFrame;
        }
        this.outerFrame = new SimpleObjectProperty<>(new Insets(90, 40, 30, 160));
        return this.outerFrame;
    };

    private ObjectProperty<Insets> innerFrame;

    public void setInnerFrame(Insets value) {
        this.innerFrameProperty().setValue(value);
    }

    public Insets getInnerFrame() {
        return this.innerFrame == null ? new Insets(20) : innerFrameProperty().getValue();
    }

    public ObjectProperty<Insets> innerFrameProperty() {
        if (this.innerFrame != null) {
            return this.innerFrame;
        }

        this.innerFrame = new SimpleObjectProperty<>(new Insets(20));
        return this.innerFrame;
    };
    
    private ObjectProperty<Insets> textPadding;

    public void setTextPadding(Insets value) {
        this.textPaddingProperty().setValue(value);
    }

    public Insets getTextPadding() {
        return this.textPadding == null ? new Insets(15, 40, 15, 40) : textPaddingProperty().getValue();
    }

    public ObjectProperty<Insets> textPaddingProperty() {
        if (this.textPadding != null) {
            return this.textPadding;
        }
        this.textPadding = new SimpleObjectProperty<>(new Insets(15, 40, 15, 40));
        return this.textPadding;
    };
}