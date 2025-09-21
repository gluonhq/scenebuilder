/*
 * Copyright (c) 2025, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.metadata.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javafx.fxml.FXMLLoader;

class PrefixedValueTest {

    private PrefixedValue classUnderTest;

    @ParameterizedTest
    @CsvSource({
        "$HelloWorld",  /* Expression Prefix */
        "%HelloWorld",  /* Resource Key Prefix */
        "@HelloWorld",  /* Relative Path Prefix */
        "\\HelloWorld", /* Escape Prefix */
    })
    void that_escape_prefix_is_added_when_expression_prefix_is_found(String valueWithPrefix) {
        classUnderTest = new PrefixedValue(PrefixedValue.Type.PLAIN_STRING, valueWithPrefix);

        var expected = FXMLLoader.ESCAPE_PREFIX + valueWithPrefix;

        assertEquals(expected, classUnderTest.toString());
        assertEquals(valueWithPrefix, classUnderTest.getSuffix());
    }

    @Test
    void that_escape_prefix_is_not_added_to_plain_strings() {
        var plainText = "HelloWorld";
        classUnderTest = new PrefixedValue(PrefixedValue.Type.PLAIN_STRING, plainText);

        assertEquals(plainText, classUnderTest.toString());
        assertEquals(plainText, classUnderTest.getSuffix());
    }

    @ParameterizedTest
    @CsvSource({
        "$HelloWorld",  /* Expression Prefix */
        "%HelloWorld",  /* Resource Key Prefix */
        "@HelloWorld",  /* Relative Path Prefix */
        "\\HelloWorld", /* Escape Prefix */
    })
    void that_escape_prefix_is_not_added_to_existing_prefixes(String value) {
        // Add escape prefix once
        var escapedValue = new PrefixedValue(PrefixedValue.Type.PLAIN_STRING, value).toString();

        // escapedValue must remain unchanged
        classUnderTest = new PrefixedValue(PrefixedValue.Type.PLAIN_STRING, escapedValue);

        assertEquals(escapedValue, classUnderTest.toString());
        assertEquals(value, classUnderTest.getSuffix());
    }

}
