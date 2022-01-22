/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.fxom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link FXOMIntrinsic#createFxomInstanceFromIntrinsic()}
 */
public class CreateFxomInstanceFromIntrinsicTest {

    private static FXOMIntrinsic fxomIntrinsic;
    private static FXOMDocument fxomDocument;

    @BeforeAll
    public static void initialize() {
        prepareTestData();
    }

    private static void prepareTestData() {
        final String sourceFile = "test_include.fxml";
        fxomDocument = new FXOMDocument();
        fxomIntrinsic = new FXOMIntrinsic(fxomDocument, FXOMIntrinsic.Type.FX_INCLUDE, sourceFile);
    }

    private FXOMInstance callService() {
        return fxomIntrinsic.createFxomInstanceFromIntrinsic();
    }

    @Test
    public void testCreateFxomInstance() {
        FXOMInstance fxomInstance = callService();
        assertThat(fxomInstance).isNotNull();
        assertThat(fxomInstance.getFxomDocument()).isEqualTo(fxomIntrinsic.getFxomDocument());
        assertThat(fxomInstance.getGlueElement()).isEqualTo(fxomIntrinsic.getGlueElement());
        assertThat(fxomInstance.getSceneGraphObject()).isEqualTo(fxomIntrinsic.getSourceSceneGraphObject());
        assertThat(fxomInstance.getDeclaredClass()).isEqualTo(fxomIntrinsic.getClass());
    }

    @Test
    public void testCreateFxomInstanceWithProperties() {
        // add at least one property (source)
        fxomIntrinsic.addIntrinsicProperty(fxomDocument);
        FXOMInstance fxomInstance = callService();
        assertThat(fxomInstance).isNotNull();
        assertThat(fxomInstance.getProperties()).isNotEmpty();
    }
}