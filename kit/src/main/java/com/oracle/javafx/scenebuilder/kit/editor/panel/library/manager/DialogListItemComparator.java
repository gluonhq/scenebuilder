/*
 * Copyright (c) 2021, Gluon and/or its affiliates.
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

class DialogListItemComparator implements Comparator<DialogListItem> {

    @Override
    public int compare(DialogListItem a, DialogListItem b) {

        if (a instanceof ArtifactDialogListItem && b instanceof ArtifactDialogListItem) {            
            return compareUsingArtifactCoordinates((ArtifactDialogListItem) a, (ArtifactDialogListItem) b);
        }

        if (a instanceof LibraryDialogListItem && b instanceof LibraryDialogListItem) {            
            return compareUsingPaths((LibraryDialogListItem) a, (LibraryDialogListItem) b);
        }

        if (a instanceof LibraryDialogListItem) {
            return -1;            
        } else {            
            return 1;
        }
    }

    private int compareUsingArtifactCoordinates(ArtifactDialogListItem a, ArtifactDialogListItem b) {
        return a.getCoordinates().compareTo(b.getCoordinates());
    }

    private int compareUsingPaths(LibraryDialogListItem a, LibraryDialogListItem b) {

        Path first = a.getFilePath();
        Path second = b.getFilePath();

        if (Files.isDirectory(first) && Files.isRegularFile(second)) {            
            return -1;
        }

        if (Files.isRegularFile(first) && Files.isDirectory(second)) {            
            return 1;
        }

        return first.compareTo(second);
    }

}
