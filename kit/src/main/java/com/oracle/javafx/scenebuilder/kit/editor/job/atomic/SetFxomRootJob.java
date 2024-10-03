/*
 * Copyright (c) 2017, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2014, Oracle and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor.job.atomic;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.editor.job.Job;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;

/**
 */
public class SetFxomRootJob extends Job {

    private final FXOMObject newRoot;
    private FXOMObject oldRoot;

    public SetFxomRootJob(FXOMObject newRoot, EditorController editorController) {
        super(editorController);

        assert editorController.getFxomDocument() != null;
        assert (newRoot == null) || (newRoot.getFxomDocument() == editorController.getFxomDocument());

        this.newRoot = newRoot;
    }

    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        return newRoot != getEditorController().getFxomDocument().getFxomRoot();
    }

    @Override
    public void execute() {
        assert oldRoot == null;

        // Saves the current root
        EditorController editorController = getEditorController();
        final FXOMDocument fxomDocument = editorController.getFxomDocument();
        oldRoot = fxomDocument.getFxomRoot();

        fxomDocument.beginUpdate();
        fxomDocument.setFxomRoot(newRoot);
        fxomDocument.endUpdate();

        if (newRoot != null && newRoot.isClassFromExternalPlugin()) {
            EditorPlatform.showThemeAlert(editorController.getOwnerWindow(), editorController.getTheme(), editorController::setTheme);
        }
    }

    @Override
    public void undo() {
        final FXOMDocument fxomDocument = getEditorController().getFxomDocument();
        assert fxomDocument.getFxomRoot() == newRoot;

        fxomDocument.beginUpdate();
        fxomDocument.setFxomRoot(oldRoot);
        fxomDocument.endUpdate();

        assert fxomDocument.getFxomRoot() == oldRoot;
    }

    @Override
    public void redo() {
        final FXOMDocument fxomDocument = getEditorController().getFxomDocument();
        assert fxomDocument.getFxomRoot() == oldRoot;

        fxomDocument.beginUpdate();
        fxomDocument.setFxomRoot(newRoot);
        fxomDocument.endUpdate();

        assert fxomDocument.getFxomRoot() == newRoot;
    }

    @Override
    public String getDescription() {
        // Not expected to reach the user
        return getClass().getSimpleName();
    }
}
