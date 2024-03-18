/*
 * Copyright (c) 2023, Gluon and/or its affiliates.
 * Copyright (c) 2014, Oracle and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.app;

import com.oracle.javafx.scenebuilder.app.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.util.FileWatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The file watcher associated with this controller watches:
 *     1) the file holding the FXML document (if any)
 *     2) the resource file set in the Preview menu (if any)
 *     3) the style sheets files set in the Preview menu (if any)
 */
public class DocumentWatchingController implements FileWatcher.Delegate {
    
    private final DocumentWindowController documentWindowController;
    private final EditorController editorController;
    private final ResourceController resourceController;
    private final SceneStyleSheetMenuController sceneStyleSheetMenuController;
    private final FileWatcher fileWatcher 
            = new FileWatcher(2000 /* ms */, this, DocumentWindowController.class.getSimpleName());

    public DocumentWatchingController(DocumentWindowController documentWindowController) {
        this.documentWindowController = documentWindowController;
        this.editorController = documentWindowController.getEditorController();
        this.resourceController = documentWindowController.getResourceController();
        this.sceneStyleSheetMenuController = documentWindowController.getSceneStyleSheetMenuController();
        
        this.editorController.sceneStyleSheetProperty().addListener((ov, t, t1) -> update());
    }
    
    public void start() {
        fileWatcher.start();
    }
    
    public void stop() {
        fileWatcher.stop();
    }

    public void update() {
        List<Path> targets = new ArrayList<>();
        
        // 1)
        editorController.fxomDocument()
                .flatMap(FXOMDocument::locationPath)
                .ifPresent(targets::add);
        
        // 2)
        if (resourceController.getResourceFile() != null) {
            targets.add(resourceController.getResourceFile().toPath());
        }
        
        // 3)
        if (editorController.getSceneStyleSheets() != null) {
            for (File sceneStyleSheet : editorController.getSceneStyleSheets()) {
                targets.add(sceneStyleSheet.toPath());
            }
        }
        
        fileWatcher.setTargets(targets);
    }
    
    public void removeDocumentTarget() {
        editorController.fxomDocument()
                .flatMap(FXOMDocument::locationPath)
                .ifPresent(fileWatcher::removeTarget);
    }

    @Override
    public void fileWatcherDidWatchTargetCreation(Path target) {
        // Ignored
    }

    @Override
    public void fileWatcherDidWatchTargetDeletion(Path target) {
        if (isPathMatchingResourceLocation(target)) {
            // Resource file has disappeared
            resourceController.performRemoveResource(); 
            // Call above has invoked
            //      - FXOMDocument.refreshSceneGraph()
            //      - DocumentWatchingController.update()
            log("log.info.file.deleted", target);

        } else if (isPathMatchingSceneStyleSheet(target)) {
            sceneStyleSheetMenuController.performRemoveSceneStyleSheet(target.toFile());
            // Call above has invoked
            //      - FXOMDocument.reapplyCSS()
            //      - DocumentWatchingController.update()
            log("log.info.file.deleted", target);
        }
        /* 
         * Else it's the document file which has disappeared : 
         * We ignore this event : file will be recreated when user runs
         * the save command.
         */
    }

    @Override
    public void fileWatcherDidWatchTargetModification(Path target) {
        if (isPathMatchingResourceLocation(target)) {
            // Resource file has been modified -> refresh the scene graph
            resourceController.performReloadResource(); 
            // Call above has invoked FXOMDocument.refreshSceneGraph()
            log("log.info.reload", target);
            
        } else if (isPathMatchingDocumentLocation(target)) {
            if (!documentWindowController.isDocumentDirty()) {
                // Try to reload the fxml text on disk
                try {
                    documentWindowController.reload();
                    log("log.info.reload", target);

                } catch(IOException x) {
                    // Here we silently ignore the failure :
                    // loadFromFile() has failed but left the document unchanged.
                }
            }

        } else if (isPathMatchingSceneStyleSheet(target)) {
            editorController.fxomDocument()
                    .ifPresent(doc -> {
                        doc.reapplyCSS(target);
                        log("log.info.reload", target);
                    });
        }
    }

    private void log(String infoKey, Path target) {
        editorController.getMessageLog().logInfoMessage(infoKey, I18N.getBundle(), target.getFileName());
    }

    private boolean isPathMatchingDocumentLocation(Path p) {
        return editorController.fxomDocument()
                .flatMap(FXOMDocument::locationPath)
                .map(p::equals)
                .orElse(false);
    }
    
    private boolean isPathMatchingResourceLocation(Path p) {
        if (resourceController.getResourceFile() != null) {
            return p.equals(resourceController.getResourceFile().toPath());
        }
        
        return false;
    }

    private boolean isPathMatchingSceneStyleSheet(Path p) {
        if (editorController.getSceneStyleSheets() != null) {
            return editorController.getSceneStyleSheets().contains(p.toFile());
        }

        return false;
    }
}
