/*
 * Copyright (c) 2024, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

public class CmdTest {

    private Cmd classUnderTest = new Cmd();

    private final long timeoutSeconds = 2L;

    private Path workingDir = Path.of("./src/test/resources/com/oracle/javafx/scenebuilder/kit/editor/")
                                  .normalize()
                                  .toAbsolutePath();

    @Test
    void that_exception_is_created_with_not_existing_command() {
        var cmdLine = List.of("this", "command", "should", "not", "exist");

        Throwable t = assertThrows(IOException.class, 
                                   () -> classUnderTest.exec(cmdLine, workingDir.toFile(), timeoutSeconds));

        assertTrue(t.getMessage().startsWith("Cannot run program \"this\""));
    }

    @EnabledOnOs(value = {OS.WINDOWS})
    @Test
    void that_exit_code_from_program_is_collected_on_Windows() {
        var cmdLine = List.of(workingDir.resolve("exit-error.cmd").toString());

        Integer result = assertDoesNotThrow(() -> classUnderTest.exec(cmdLine, workingDir.toFile(), timeoutSeconds));

        assertEquals(1, result, "Exit code must be 1");
    }

    @EnabledOnOs(value = {OS.LINUX, OS.MAC})
    @Test
    void that_exit_code_is_collected_on_Linux_and_Mac() {
        var cmdLine = List.of("/bin/sh", workingDir.resolve("exit-error.sh").toString());

        Integer result = assertDoesNotThrow(() -> classUnderTest.exec(cmdLine, workingDir.toFile(), timeoutSeconds));

        assertEquals(1, result, "Exit code must be 1");
    }

    @EnabledOnOs(value = {OS.WINDOWS})
    @Test
    void that_process_does_not_run_indefinitevely_on_Windows() {
        var cmdLine = List.of(workingDir.resolve("timeout.cmd").toString());

        Throwable t = assertThrows(IOException.class, 
                                   () -> classUnderTest.exec(cmdLine, workingDir.toFile(), timeoutSeconds));

        assertEquals(t.getMessage(), "Process timed out after 2 seconds!");
    }

    @EnabledOnOs(value = {OS.LINUX, OS.MAC})
    @Test
    void that_process_does_not_run_indefinitevely_on_Linux_and_Mac() {
        var cmdLine = List.of("/bin/sh", workingDir.resolve("timeout.sh").toString());

        Throwable t = assertThrows(IOException.class, 
                                   () -> classUnderTest.exec(cmdLine, workingDir.toFile(), timeoutSeconds));

        assertEquals(t.getMessage(), "Process timed out after 2 seconds!");
    }

}
