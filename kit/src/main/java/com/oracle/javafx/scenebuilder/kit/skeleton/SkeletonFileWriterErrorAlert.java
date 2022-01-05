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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.oracle.javafx.scenebuilder.kit.alert.SBAlert;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * This class provides an alert for the case that a controller skeleton
 * cannot be written into a file.
 * 
 * The alert provides the actual file name, the error message and the
 * exception stack trace inside the expandable pane.
 *
 */
final class SkeletonFileWriterErrorAlert implements BiConsumer<File, Exception> {

    private final Supplier<Stage> stageSupplier;

    /**
     * Creates a bi-consumer which accepts a file and an exception
     * so that an appropriate error message can be presented to the
     * user.
     *  
     * @param stageSupplier Reference to the parent JavaFX window
     */
    SkeletonFileWriterErrorAlert(Supplier<Stage> stageSupplier) {
        this.stageSupplier = Objects.requireNonNull(stageSupplier);
    }

    @Override
    public void accept(File skeletonFile, Exception error) {
        SBAlert alert = prepareErrorAlert(skeletonFile, error);
        String exceptionDetails = collectExceptionDetails(error);
        TextArea textArea = new TextArea(exceptionDetails);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        alert.getDialogPane().setExpandableContent(textArea);
        Platform.runLater(()->alert.showAndWait());
    }

    private SBAlert prepareErrorAlert(File skeletonFile, Exception error) {
        var alert = new SBAlert(AlertType.ERROR, stageSupplier.get());
        alert.setTitle(I18N.getString("alert.skeleton.title"));
        alert.setHeaderText(I18N.getString("alert.skeleton.header.failed"));
        alert.setContentText(I18N.getString("alert.skeleton.saving.failed")+"\n"
                            + skeletonFile + "\n\n" 
                            + error.getLocalizedMessage());
        return alert;
    }

    private String collectExceptionDetails(Exception error) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        error.printStackTrace(printWriter);
        return stringWriter.toString();
    }

}
