/*
 * Copyright (c) 2017, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
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

import com.oracle.javafx.scenebuilder.app.util.MessageBox;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import static com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform.IS_LINUX;
import static com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform.IS_MAC;
import static com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform.IS_WINDOWS;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;

/**
 *
 */
public class AppPlatform {
    
    private static String applicationDataFolder;
    private static String userLibraryFolder;
    private static String messageBoxFolder;
    private static String logsFolder;
    private static MessageBox<MessageBoxMessage> messageBox;
    
    public static synchronized String getApplicationDataFolder() {
        
        if (applicationDataFolder == null) {
            final String appName = "Scene Builder"; //NOI18N
            
            if (IS_WINDOWS) {
                applicationDataFolder 
                        = System.getenv("APPDATA") + "\\" + appName; //NOI18N
            } else if (IS_MAC) {
                applicationDataFolder 
                        = System.getProperty("user.home") //NOI18N
                        + "/Library/Application Support/" //NOI18N
                        + appName;
            } else if (IS_LINUX) {
                applicationDataFolder
                        = System.getProperty("user.home") + "/.scenebuilder"; //NOI18N
            }
        }
        
        assert applicationDataFolder != null;
        
        return applicationDataFolder;
    }
    
    
    public static synchronized String getUserLibraryFolder() {
        
        if (userLibraryFolder == null) {
            userLibraryFolder = getApplicationDataFolder() + "/Library"; //NOI18N
        }
        
        return userLibraryFolder;
    }

    /**
     * Returns the directory path for logs. Default path is "${user.home}/.scenebuilder/logs/".
     * @return Directory path for Scene Builder logs
     */
    public static synchronized String getLogFolder() {
        if (logsFolder == null) {
            logsFolder = Paths.get(System.getProperty("user.home"), ".scenebuilder", "logs").toString(); //NOI18N
        }
        return logsFolder;
    }

    public static boolean requestStart(
            AppNotificationHandler notificationHandler, Application.Parameters parameters)  
    throws IOException {
        if (EditorPlatform.isAssertionEnabled()) {
            // Development mode : we do not delegate to the existing instance
            notificationHandler.handleLaunch(parameters.getUnnamed());
            return true;
        } else {
            return requestStartGeneric(notificationHandler, parameters);
        }
    }
    
    public interface AppNotificationHandler {
        public void handleLaunch(List<String> files);
        public void handleOpenFilesAction(List<String> files);
        public void handleMessageBoxFailure(Exception x);
        public void handleQuitAction();
    }
    
    
    /*
     * Private (requestStartGeneric)
     */
    
    private static synchronized boolean requestStartGeneric(
            AppNotificationHandler notificationHandler, Application.Parameters parameters) 
    throws IOException {
        assert notificationHandler != null;
        assert parameters != null;
        assert messageBox == null;
        
        try {
            Files.createDirectories(Paths.get(getMessageBoxFolder()));
            Files.createDirectories(Paths.get(getLogFolder()));
        } catch(FileAlreadyExistsException x) {
            // Fine
        }
        
        final boolean result;
        messageBox = new MessageBox<>(getMessageBoxFolder(), MessageBoxMessage.class, 1000 /* ms */);
        // Fix Start: Github Issue #301
        final List<String> parametersUnnamed = new ArrayList<>(parameters.getUnnamed());
        if (IS_MAC) {
            parametersUnnamed.removeIf(p -> p.startsWith("-psn"));
        }
        // Fix End
        if (messageBox.grab(new MessageBoxDelegate(notificationHandler))) {
            notificationHandler.handleLaunch(parametersUnnamed);
            result = true;
        } else {
            result = false;
            final MessageBoxMessage unamedParameters 
                    = new MessageBoxMessage(parametersUnnamed);
            try {
                messageBox.sendMessage(unamedParameters);
            } catch(InterruptedException x) {
                throw new IOException(x);
            }
        }
        
        return result;
    }
    
    private static String getMessageBoxFolder() {
        if (messageBoxFolder == null) {
            messageBoxFolder = getApplicationDataFolder() + "/MB"; //NOI18N
        }
        
        return messageBoxFolder;
    }
    
    private static class MessageBoxMessage extends ArrayList<String> {
        static final long serialVersionUID = 10;
        public MessageBoxMessage(List<String> strings) {
            super(strings);
        };
    };
    
    private static class MessageBoxDelegate implements MessageBox.Delegate<MessageBoxMessage> {

        private final AppNotificationHandler eventHandler;
        
        public MessageBoxDelegate(AppNotificationHandler eventHandler) {
            assert eventHandler != null;
            this.eventHandler = eventHandler;
        }
        
        /*
         * MessageBox.Delegate
         */
        
        @Override
        public void messageBoxDidGetMessage(MessageBoxMessage message) {
            assert Platform.isFxApplicationThread() == false;
            Platform.runLater(() -> eventHandler.handleOpenFilesAction(message));
        }

        @Override
        public void messageBoxDidCatchException(Exception x) {
            assert Platform.isFxApplicationThread() == false;
            Platform.runLater(() -> eventHandler.handleMessageBoxFailure(x));
        }
        
    } 
}
