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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Cmd allows the execution of a given command line in a defined working directory. The execution is
 * aborted after the timeout.
 */
final class Cmd {
   
    /**
     * Executes a given command line using the working directory and timeout.
     * 
     * @param cmd The command line to be executed defined as a {@link List} of {@link String}
     * @param wDir The working directory, where the process shall be executed within.
     * @param timeoutSec Duration in in seconds after which the execution should be stopped. 
     * @return exit code of the command line as an Integer
     * 
     * @throws IOException - if the command was not found or the program execution exceeds the given timeout duration.
     * @throws InterruptedException - if the current thread is interrupted while waiting
     */
    public final Integer exec(List<String> cmd, File wDir, long timeoutSec) throws IOException, 
                                                                                   InterruptedException {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder = builder.directory(wDir);
            Process proc = builder.start();
            boolean completed = proc.waitFor(timeoutSec, TimeUnit.SECONDS);
            if (completed) {
                return proc.exitValue();
            }
            throw new IOException("Process timed out after %s seconds!".formatted(timeoutSec));
    }

}
