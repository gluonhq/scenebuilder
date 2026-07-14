/*
 * Copyright (c) 2026, Gluon and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 */
package com.oracle.javafx.scenebuilder.kit.editor.job;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController.EditAction;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AlignSelectionJobTest {

    @Test
    public void everyAlignmentActionHasAMatchingMode() {
        final EnumSet<EditAction> actions = EnumSet.of(
                EditAction.ALIGN_LEFT,
                EditAction.ALIGN_HORIZONTAL_CENTER,
                EditAction.ALIGN_RIGHT,
                EditAction.ALIGN_TOP,
                EditAction.ALIGN_VERTICAL_CENTER,
                EditAction.ALIGN_BOTTOM,
                EditAction.DISTRIBUTE_HORIZONTAL,
                EditAction.DISTRIBUTE_VERTICAL,
                EditAction.SAME_WIDTH,
                EditAction.SAME_HEIGHT,
                EditAction.SAME_SIZE);

        for (EditAction action : actions) {
            assertDoesNotThrow(() -> AlignSelectionJob.Mode.valueOf(action.name()),
                    () -> "Missing alignment mode for " + action);
        }
    }
}
