/*
 * Copyright (c) 2024, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor.messagelog;

import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 */
public class MessageLog {

    public MessageLog() {
        // no-op
    }

    private final List<MessageLogEntry> entries = new ArrayList<>();
    private final SimpleIntegerProperty revision = new SimpleIntegerProperty();
    private final SimpleIntegerProperty numOfWarningMessages = new SimpleIntegerProperty();
    private final static String TIMESTAMP_PATTERN = "h:mm a EEEEEEEEE d MMM. yyyy"; //NOI18N
    private static SimpleDateFormat TIMESTAMP_DATE_FORMAT;
    
    
    /*
     * Public
     */
    
    public void logInfoMessage(String infoKey, ResourceBundle bundle, Object... arguments) {
        logMessage(MessageLogEntry.Type.INFO, bundle, infoKey, arguments);
    }
    
    public void logWarningMessage(String warningKey, ResourceBundle bundle, Object... arguments) {
        logMessage(MessageLogEntry.Type.WARNING, bundle, warningKey, arguments);
    }
    
    public void logInfoMessage(String infoKey, Object... arguments) {
        logInfoMessage(infoKey, I18N.getBundle(), arguments);
    }
    
    public void logWarningMessage(String warningKey, Object... arguments) {
        logWarningMessage(warningKey, I18N.getBundle(), arguments);
    }
    
    public IntegerProperty revisionProperty() {
        return revision;
    }
    
    public IntegerProperty numOfWarningMessagesProperty() {
        return numOfWarningMessages;
    }
    
    public List<MessageLogEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    public MessageLogEntry getYoungestEntry() {
        return entries.isEmpty() ? null : entries.get(0);
    }
    
    public int getEntryCount() {
        return entries.size();
    }
    
    public int getWarningEntryCount() {
        int count = 0;
        for (MessageLogEntry entry : entries) {
            if (entry.getType() == MessageLogEntry.Type.WARNING) {
                count++;
            }
        }
        return count;
    }
    
    public void clear() {
        if (entries.isEmpty() == false) {
            entries.clear();
            incrementRevision();
            resetNumOfWarningMessages();
        }
    }
    
    public void clearEntry(MessageLogEntry entry) {
        assert entry != null;
        assert entries.contains(entry);
        entries.remove(entry);
        incrementRevision();
        
        if (entry.getType().equals(MessageLogEntry.Type.WARNING)) {
            decrementNumOfWarningMessages();
        }
    }
    

    /*
     * Private
     */
    
    private synchronized String getTimeStamp() {
        // We create TIMESTAMP_DATE_FORMAT lazily because it seems to be slow
        if (TIMESTAMP_DATE_FORMAT == null) {
            TIMESTAMP_DATE_FORMAT = new SimpleDateFormat(TIMESTAMP_PATTERN);
        }
        return TIMESTAMP_DATE_FORMAT.format(new Date());
    }
    
    private void logMessage(MessageLogEntry.Type messageType, ResourceBundle bundle, String messageKey, Object... arguments) {
        final String messageText = MessageFormat.format(bundle.getString(messageKey), arguments);
        final MessageLogEntry entry = new MessageLogEntry(messageType, messageText, getTimeStamp());
        entries.add(0, entry);
        incrementRevision();
        
        if (messageType.equals(MessageLogEntry.Type.WARNING)) {
            incrementNumOfWarningMessages();
        }
    }
    
    private void incrementRevision() {
        revision.set(revision.get() + 1);
    }
    
    private void incrementNumOfWarningMessages() {
        numOfWarningMessages.set(numOfWarningMessages.get() + 1);
    }
    
    private void decrementNumOfWarningMessages() {
        numOfWarningMessages.set(numOfWarningMessages.get() - 1);
    }
    
    private void resetNumOfWarningMessages() {
        numOfWarningMessages.set(0);
    }
}
