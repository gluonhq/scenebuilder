/*
 * Copyright (c) 2022, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor;

import com.oracle.javafx.scenebuilder.kit.editor.job.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class JobManagerTest {

    private EditorController editor;
    private JobManager jobManager;
    private DummyJob job;

    @BeforeEach
    void init() throws Exception {
        editor = new EditorController();
        editor.setFxmlText("", false);

        jobManager = new JobManager(editor, 10);
        job = new DummyJob(editor);
    }

    @Test
    public void test_undo_redo() {
        jobManager.push(job);

        jobManager.undo();

        assertThat(job.undos).isEqualTo(1);
        assertThat(jobManager.getRedoDescription()).isEqualTo("DummyJob");
        assertFalse(jobManager.canUndo());
        assertTrue(jobManager.canRedo());
        assertTrue(jobManager.getUndoStack().isEmpty());
        assertFalse(jobManager.getRedoStack().isEmpty());

        jobManager.redo();

        assertThat(job.redos).isEqualTo(1);
        assertThat(jobManager.getUndoDescription()).isEqualTo("DummyJob");
        assertTrue(jobManager.canUndo());
        assertFalse(jobManager.canRedo());
        assertFalse(jobManager.getUndoStack().isEmpty());
        assertTrue(jobManager.getRedoStack().isEmpty());
    }

    @Test
    public void push_executes_job() {
        jobManager.push(job);

        assertThat(job.executes).isEqualTo(1);
    }

    @Test
    public void revision_is_updated() {
        jobManager.push(job);

        assertThat(jobManager.revisionProperty().intValue()).isEqualTo(1);

        jobManager.undo();

        assertThat(jobManager.revisionProperty().intValue()).isEqualTo(2);

        jobManager.redo();

        assertThat(jobManager.revisionProperty().intValue()).isEqualTo(3);
    }

    @Test
    public void getCurrentJob() {
        assertNull(jobManager.getCurrentJob());

        jobManager.push(job);

        assertNotNull(jobManager.getCurrentJob());
    }

    @Test
    public void pushing_job_inside_execute_yields_ISE() {
        var invalidJob = new InvalidJob(editor, jobManager);

        assertThrows(IllegalStateException.class, () -> jobManager.push(invalidJob));
    }

    private static class DummyJob extends Job {

        private int executes = 0;
        private int undos = 0;
        private int redos = 0;

        public DummyJob(EditorController editorController) {
            super(editorController);
        }

        @Override
        public boolean isExecutable() {
            return true;
        }

        @Override
        public void execute() {
            executes++;
        }

        @Override
        public void undo() {
            undos++;
        }

        @Override
        public void redo() {
            redos++;
        }

        @Override
        public String getDescription() {
            return "DummyJob";
        }
    }

    private static class InvalidJob extends DummyJob {

        private JobManager jobManager;

        public InvalidJob(EditorController editorController, JobManager jobManager) {
            super(editorController);
            this.jobManager = jobManager;
        }

        @Override
        public void execute() {
            // not allowed by design
            jobManager.push(new DummyJob(getEditorController()));
        }
    }
}