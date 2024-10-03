/*
 * Copyright (c) 2017, 2024, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor.job.atomic;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.editor.job.Job;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMPropertyC;

/**
 *
 */
public class AddPropertyValueJob extends Job {

    private final FXOMObject value;
    private final FXOMPropertyC targetProperty;
    private final int targetIndex;
    
    public AddPropertyValueJob(FXOMObject value, FXOMPropertyC targetProperty, 
            int targetIndex, EditorController editorController) {
        super(editorController);
        
        assert value != null;
        assert targetProperty != null;
        assert targetIndex >= -1;
        
        this.value = value;
        this.targetProperty = targetProperty;
        this.targetIndex = targetIndex;
    }

    /*
     * AddPropertyValueJob
     */
    
    @Override
    public boolean isExecutable() {
        return (value.getParentProperty() == null)
                && (value.getParentCollection() == null);
    }

    @Override
    public void execute() {
        assert targetIndex <= targetProperty.getValues().size();
        redo();
    }

    @Override
    public void undo() {
        assert value.getParentProperty() == targetProperty;
        assert value.getParentCollection() == null;
        
        getEditorController().getFxomDocument().beginUpdate();
        value.removeFromParentProperty();
        getEditorController().getFxomDocument().endUpdate();
        
        assert value.getParentProperty() == null;
        assert value.getParentCollection() == null;
    }

    @Override
    public void redo() {
        assert value.getParentProperty() == null;
        assert value.getParentCollection() == null;
        
        getEditorController().getFxomDocument().beginUpdate();
        value.addToParentProperty(targetIndex, targetProperty);
        getEditorController().getFxomDocument().endUpdate();

        if (value.isClassFromExternalPlugin()) {
            EditorPlatform.showThemeAlert(getEditorController().getOwnerWindow(), getEditorController().getTheme(), t -> getEditorController().setTheme(t));
        }

        assert value.getParentProperty() == targetProperty;
        assert value.getParentCollection() == null;
    }

    @Override
    public String getDescription() {
        // Should normally not reach the user
        return getClass().getSimpleName();
    }
    
}
