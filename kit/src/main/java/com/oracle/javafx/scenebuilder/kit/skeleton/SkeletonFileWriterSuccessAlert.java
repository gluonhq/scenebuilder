/*
 * Copyright (c) 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.skeleton;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.oracle.javafx.scenebuilder.kit.alert.SBAlert;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
/**
 * When a controller skeleton file has been successfully saved as file,
 * this class provides an alert which shows the file name (incl. path)
 * inside a text field. A button with a copy to clip board icon 
 * is placed right next to the text field. If clicked, the content of
 * the text field is copied into the system clip board.
 *  
 */
final class SkeletonFileWriterSuccessAlert implements Consumer<File> {

    private final Supplier<Stage> stageSupplier;

    /**
     * Creates a consumer which accepts the written file so
     * that an appropriate notification can be presented
     * to the user.
     *  
     * @param stageSupplier Reference to the parent JavaFX window
     */
    SkeletonFileWriterSuccessAlert(Supplier<Stage> stageSupplier) {
        this.stageSupplier = Objects.requireNonNull(stageSupplier);
    }

    @Override
    public void accept(File fileWritten) {
        var alert = prepareSuccessAlert();
        DialogPane dialogPane = alert.getDialogPane();
        TextField textField = new TextField(fileWritten.toString());
        textField.setEditable(false);
        Button copyButton = createCopyToClipboardButton(textField);
        HBox hbox = new HBox(textField,copyButton);
        HBox.setHgrow(textField, Priority.ALWAYS);
        dialogPane.setContent(hbox);
        Platform.runLater(() -> alert.showAndWait());
    }

    private SBAlert prepareSuccessAlert() {
        var alert = new SBAlert(AlertType.INFORMATION, stageSupplier.get());
        alert.setTitle(I18N.getString("alert.skeleton.title"));
        alert.setHeaderText(I18N.getString("alert.skeleton.header.success"));
        return alert;
    }

    private Button createCopyToClipboardButton(TextField textField) {
        SVGPath clipboardIcon = new SVGPath();
        clipboardIcon.setContent("M11.983,1.973L11.983,12.001L3.935,12.001L3.935,1.973L3.319,1.973C2.577,1.973 1.975,2.575 1.975,3.317L1.975,12.684C1.975,13.426 2.577,14.029 3.319,14.029L12.644,14.029C13.386,14.029 13.989,13.426 13.989,12.684L13.989,3.317C13.989,2.575 13.386,1.973 12.644,1.973L11.983,1.973ZM10.959,0.997L4.965,0.997L4.965,5.018L10.959,5.018L10.959,0.997ZM10.021,3.948L10.038,3.948L10.038,1.932L6.016,1.932L6.016,3.948L6.034,3.948L6.034,4.005L10.021,4.005L10.021,3.948Z");
        Button copyButton = new Button(null, clipboardIcon);
        copyButton.setTooltip(new Tooltip(I18N.getString("alert.skeleton.copy2clipboard")));
        copyButton.setOnAction(event -> Clipboard.getSystemClipboard()
                  .setContent(Map.of(DataFormat.PLAIN_TEXT, textField.getText())));
        return copyButton;
    }

}
