/*
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
package com.oracle.javafx.scenebuilder.kit.editor.job;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.job.reference.ObjectDeleter;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;

/**
 *
 */
public class DeleteObjectJob extends InlineDocumentJob {

    private final FXOMObject targetFxomObject;

    public DeleteObjectJob(FXOMObject fxomObject, EditorController editorController) {
        super(editorController);

        assert fxomObject != null;

        this.targetFxomObject = fxomObject;
    }

    @Override
    public boolean isExecutable() {
        final boolean result;

        if (targetFxomObject == targetFxomObject.getFxomDocument().getFxomRoot()) {
            // targetFxomObject is the root
            result = true;
        } else if (targetFxomObject.getSceneGraphObject() instanceof Axis) {
            // Axis cannot be deleted from their parent Chart
            result = false;
        } else if (targetFxomObject.getParentObject() != null &&
                targetFxomObject.getParentObject().getSceneGraphObject() instanceof Scene) {
            // Scene root cannot be deleted
            result = false;
        } else {
            result = (targetFxomObject.getParentProperty() != null);
        }

        return result;
    }

    @Override
    protected List<Job> makeAndExecuteSubJobs() {

        final List<Job> result = new ArrayList<>();
        if ((targetFxomObject.getParentProperty() == null) &&
            (targetFxomObject.getParentCollection() == null)) {
            /*
             * targetFxomObject is the root object
             * => we reset the root object to null
             */
            final Job setRootJob = new SetDocumentRootJob(null, getEditorController());
            setRootJob.execute();
            result.add(setRootJob);

        } else {
            
            /*
             * targetFxomObject is not the root object
             * => we delegate to ObjectDeleter
             * => this class will take care of references
             */
            
            final ObjectDeleter deleter = new ObjectDeleter(getEditorController());
            deleter.delete(targetFxomObject);
            result.addAll(deleter.getExecutedJobs());
        }
        
        return result;
    }

    @Override
    protected String makeDescription() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Delete ");

        if (targetFxomObject instanceof FXOMInstance) {
            final Object sceneGraphObject = targetFxomObject.getSceneGraphObject();
            if (sceneGraphObject != null) {
                sb.append(sceneGraphObject.getClass().getSimpleName());
            } else {
                sb.append("Unresolved Object");
            }
        } else if (targetFxomObject instanceof FXOMCollection) {
            sb.append("Collection");
        } else if (targetFxomObject instanceof FXOMIntrinsic) {
            sb.append(targetFxomObject.getGlueElement().getTagName());
        } else {
            assert false;
            sb.append(targetFxomObject.getClass().getSimpleName());
        }

        return sb.toString();
    }
    
    FXOMObject getTargetFxomObject() {
        return targetFxomObject;
    }
}
