package com.oracle.javafx.scenebuilder.kit.editor.job.wrap;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;

import javafx.stage.Stage;

/**
 * Job used to wrap selection in a Stage.
 */
public class WrapInStageJob extends AbstractWrapInWindowJob {

    public WrapInStageJob(EditorController editorController) {
        super(editorController);
        newContainerClass = Stage.class;
    }

}
