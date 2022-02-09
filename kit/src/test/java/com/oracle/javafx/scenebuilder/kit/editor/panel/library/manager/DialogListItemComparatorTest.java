/*
 * Copyright (c) 2021, 2022, Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.kit.editor.panel.library.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class DialogListItemComparatorTest {

    private DialogListItemComparator classUnderTest = new DialogListItemComparator();

    @Test
    public void testSortingOrArifactItems() {

        DialogListItem artifactItem = new ArtifactDialogListItem(null, "net.somegroup.package:myArtifact:0.1.17");
        DialogListItem actuallySameItem = new ArtifactDialogListItem(null, "net.somegroup.package:myArtifact:0.1.17");

        DialogListItem otherRevisionItem = new ArtifactDialogListItem(null, "net.somegroup.package:myArtifact:0.2.17");
        DialogListItem commercialItem = new ArtifactDialogListItem(null, "com.acme.main:business:4.0");

        assertTrue(classUnderTest.compare(artifactItem, otherRevisionItem) < 0);
        assertTrue(classUnderTest.compare(artifactItem, commercialItem) > 0);
        assertEquals(0, classUnderTest.compare(artifactItem, actuallySameItem));

    }

    @Test
    public void testLibraryDirectoriesPreceedFiles() throws Exception {

        URL resource = getClass().getClassLoader().getResource("libraryManager/Empty-Dummy-Library.jar");
        Path libFile = Paths.get(resource.toURI());
        Path libDir = libFile.getParent();

        DialogListItem libraryFileItem = new LibraryDialogListItem(null, libFile);
        DialogListItem libraryDirItem = new LibraryDialogListItem(null, libDir);

        assertTrue(classUnderTest.compare(libraryDirItem, libraryFileItem) < 0);
        assertTrue(classUnderTest.compare(libraryFileItem, libraryDirItem) > 0);

    }

    @Test
    public void testLibraryFilesPreceedArtifacts() {

        DialogListItem artifactItem = new ArtifactDialogListItem(null, "net.somegroup.package:myArtifact:0.1.17");
        DialogListItem libraryFileItem = new LibraryDialogListItem(null,
                Paths.get("c:/mylibrary/my-special-controls.jar"));

        assertEquals(1, classUnderTest.compare(artifactItem, libraryFileItem));
        assertEquals(-1, classUnderTest.compare(libraryFileItem, artifactItem));

    }

    @Test
    public void testLibraryDirectoriesAreSortedCorrectly() throws Exception {

        URL resource = getClass().getClassLoader().getResource("libraryManager/Empty-Dummy-Library.jar");
        Path libDir = Paths.get(resource.toURI()).getParent();
        Path parentLibDir = libDir.getParent();

        DialogListItem secondDir = new LibraryDialogListItem(null, libDir);
        DialogListItem firstDir = new LibraryDialogListItem(null, parentLibDir);

        assertTrue(classUnderTest.compare(secondDir, firstDir) > 0);
        assertTrue(classUnderTest.compare(firstDir, secondDir) < 0);

    }

    @Test
    public void testLibraryFilesAreSortedCorrectly() throws Exception {

        URL resource = getClass().getClassLoader().getResource("libraryManager/Empty-Dummy-Library.jar");
        Path libFile = Paths.get(resource.toURI());

        DialogListItem fileItem = new LibraryDialogListItem(null, libFile);

        assertEquals(0, classUnderTest.compare(fileItem, fileItem));

    }

}
