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
package com.oracle.javafx.scenebuilder.app.preferences;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

public class AppVersionTest {

    @Test
    public void that_comparision_yields_0_in_case_of_equal_versions() {
        AppVersion old = new AppVersion(8, 5, 0);
        AppVersion moreRecent = new AppVersion(8, 5, 0);
        int result = moreRecent.compareTo(old);
        assertTrue(result == 0);
    }

    @Test
    public void that_new_major_version_is_larger_than_older_major_version() {
        AppVersion old = new AppVersion(8, 5, 0);
        AppVersion moreRecent = new AppVersion(16, 0, 1);
        int result = moreRecent.compareTo(old);
        assertTrue(result > 0);
    }

    @Test
    public void that_minor_version_is_used_when_major_is_equal() {
        AppVersion old = new AppVersion(16, 1, 0);
        AppVersion moreRecent = new AppVersion(16, 2, 0);
        int result = moreRecent.compareTo(old);
        assertTrue(result > 0);
    }

    @Test
    public void that_patch_version_is_used_when_major_and_minor_are_equal() {
        AppVersion old = new AppVersion(16, 0, 0);
        AppVersion moreRecent = new AppVersion(16, 0, 1);
        int result = moreRecent.compareTo(old);
        assertTrue(result > 0);
    }

    @Test
    public void that_a_version_can_be_parsed_from_string() {
        String validVersion = "42.12.12";
        AppVersion expected = new AppVersion(42, 12, 12);
        Optional<AppVersion> parsedVersion = AppVersion.fromString(validVersion);
        assertTrue(parsedVersion.isPresent());
        assertEquals(expected, parsedVersion.get());
    }

    @Test
    public void that_2digit_versions_are_parsed() {
        assertTrue(AppVersion.fromString("2.0").isPresent());
        assertTrue(AppVersion.fromString("2.0.").isPresent());
    }

    @Test
    public void that_useful_toString_exists() {
        assertEquals("1.0", new AppVersion(1, 0).toString());
        assertEquals("1.2", new AppVersion(1, 2).toString());
        assertEquals("1.0.0", new AppVersion(1, 0, 0).toString());
        assertEquals("1.0.1", new AppVersion(1, 0, 1).toString());
    }

    @Test
    public void that_empty_optionals_are_returned_from_illegal_version_strings() {
        assertFalse(AppVersion.fromString("-1.-1.-1").isPresent());
        assertFalse(AppVersion.fromString("-1.10.10").isPresent());
        assertFalse(AppVersion.fromString("1.-10.-1").isPresent());
        assertFalse(AppVersion.fromString("1.1.-100").isPresent());
        assertFalse(AppVersion.fromString("-1.-1.-1").isPresent());
        assertFalse(AppVersion.fromString(".1.1.1.").isPresent());
        assertFalse(AppVersion.fromString("1..1").isPresent());
        assertFalse(AppVersion.fromString("11").isPresent());
        assertFalse(AppVersion.fromString("/t").isPresent());
        assertFalse(AppVersion.fromString(" . . ").isPresent());
        assertFalse(AppVersion.fromString("1.2.a").isPresent());
        assertFalse(AppVersion.fromString("-1.-2").isPresent());
        assertFalse(AppVersion.fromString("2.-1").isPresent());
    }
    
    @Test
    public void that_node_name_is_created_with_prefix() {
        assertEquals("SB_16.0.1", new AppVersion(16, 0, 1).nodeNameWithPrefix("SB_"));
        assertEquals("TEST_16.0", new AppVersion(16, 0).nodeNameWithPrefix("TEST_"));
        assertEquals("16.0", new AppVersion(16, 0).nodeNameWithPrefix(""));
        assertEquals("16.0", new AppVersion(16, 0).nodeNameWithPrefix(null));
    }

    @Test
    public void that_descending_sort_by_major_minor_patch_works() {
        List<AppVersion> versions = List.of(
                new AppVersion(16, 0, 1),
                new AppVersion(8, 5, 0),
                new AppVersion(16, 0, 2),
                new AppVersion(17, 0, 1),
                new AppVersion(11, 2, 0),
                new AppVersion(8, 5, 0),
                new AppVersion(2, 0, 0),
                new AppVersion(17, 0, 0),
                new AppVersion(8, 5, 0),
                new AppVersion(11, 1, 3),
                new AppVersion(17, 2, 1),
                new AppVersion(16, 0, 0),
                new AppVersion(16, 0));

        List<AppVersion> sorted = versions.stream().sorted(AppVersion.descending()).toList();

        List<AppVersion> expectedOrder = List.of(
                new AppVersion(17, 2, 1),
                new AppVersion(17, 0, 1),
                new AppVersion(17, 0, 0),
                new AppVersion(16, 0, 2),
                new AppVersion(16, 0, 1),
                new AppVersion(16, 0, 0),
                new AppVersion(16, 0),
                new AppVersion(11, 2, 0),
                new AppVersion(11, 1, 3),
                new AppVersion(8, 5, 0),
                new AppVersion(8, 5, 0),
                new AppVersion(8, 5, 0),
                new AppVersion(2, 0, 0));

        assertEquals(expectedOrder, sorted);
    }
}
