/*
 * Copyright (c) 2022, 2025, Gluon and/or its affiliates.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class DocumentationUrlsTest {

    @Test
    public void that_javafx_version_substitution_works_when_required() {
        DocumentationUrls url = DocumentationUrls.OPENJFX_JAVADOC_HOME;

        // important is, that the configured value contains {javafx.version.major}
        assertEquals("https://openjfx.io/javadoc/{javafx.version.major}/", url.getConfiguredValue());

        // this should be replaced at the end
        assertFalse(url.toString().contains("{javafx.version.major}"));

        String fxVersion = DocumentationUrls.getMajorJavaFxVersion();
        String expectedUrl = "https://openjfx.io/javadoc/{javafx.version.major}/"
                             .replace("{javafx.version.major}", fxVersion);
        assertEquals(expectedUrl, url.toString());
    }
    
    @Test
    public void that_platform_documentation_url_matches_configuration() {
        assertEquals("https://docs.oracle.com/javafx/index.html", 
                DocumentationUrls.ORACLE_DOCUMENTATION.toString());
    }

    @Test
    public void that_platform_javadoc_url_matches_configuration() {
        assertEquals("https://openjfx.io/javadoc/24/",
                DocumentationUrls.JAVADOC_HOME.toString());
    }

    @Test
    public void that_major_version_is_properly_extracted() {
        String javaFxVersion = "17.0.1.a";
        String majorVersion = DocumentationUrls.getMajorJavaFxVersion(javaFxVersion);
        assertEquals("17", majorVersion);
    }

    @Test
    public void that_unsupported_version_schema_yields_full_version_string() {
        String javaFxVersion = "17-0-0-1";
        String majorVersion = DocumentationUrls.getMajorJavaFxVersion(javaFxVersion);
        assertEquals("17-0-0-1", majorVersion);
    }
}
