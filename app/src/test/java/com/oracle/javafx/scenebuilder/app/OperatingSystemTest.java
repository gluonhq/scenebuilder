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
package com.oracle.javafx.scenebuilder.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.junit.jupiter.api.Test;

public class OperatingSystemTest {

    private OperatingSystem classUnderTest;
    
    @Test
    public void that_MacOS_is_properly_detected() {
        assumeTrue("MacOS", System.getProperty("os.name").toLowerCase().contains("mac"));
        classUnderTest = OperatingSystem.get();
        assertEquals(OperatingSystem.MACOS, classUnderTest);
    }

    @Test
    public void that_Windows_is_properly_detected() {
        assumeTrue("Windows", System.getProperty("os.name").toLowerCase().contains("windows"));
        classUnderTest = OperatingSystem.get();
        assertEquals(OperatingSystem.WINDOWS, classUnderTest);
    }

    @Test
    public void that_Linux_is_properly_detected() {
        assumeTrue("Linux", System.getProperty("os.name").toLowerCase().contains("linux"));
        classUnderTest = OperatingSystem.get();
        assertEquals(OperatingSystem.LINUX, classUnderTest);
    }

    @Test
    public void that_other_is_returned_for_unknown_OS() {
        classUnderTest = OperatingSystem.get("BeOS");
        assertEquals(OperatingSystem.OTHER, classUnderTest);
    }
}
