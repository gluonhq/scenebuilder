/*
 * Copyright (c) 2017, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors;

import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Editor for setting a charset property of an included element.
 */
public class CharsetEditor extends AutoSuggestEditor {

    private final Map<String, Charset> availableCharsets;

    public CharsetEditor(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses, Map<String, Charset> availableCharsets) {
        super(propMeta, selectedClasses, new ArrayList<>(availableCharsets.keySet()), Type.ALPHA);
        this.availableCharsets = availableCharsets;
        initialize();
    }

    private void initialize() {
        EventHandler<ActionEvent> onActionListener = event -> {
            if (isHandlingError()) {
                // Event received because of focus lost due to error dialog
                return;
            }
            Object value = getValue();
            if ((value != null) && isValidValue((String) value)) {
                userUpdateValueProperty(value);
                getTextField().selectAll();
            } else {
                handleInvalidValue(getTextField().getText());
            }
        };
        setTextEditorBehavior(this, textField, onActionListener);
    }

    @Override
    public Object getValue() {
        String val = getTextField().getText();
        if (val.isEmpty()) {
            val = "";
            getTextField().setText(val);
            return String.valueOf(val);
        }
        Object constantValue = this.availableCharsets.get(val.toUpperCase(Locale.ROOT));
        if (constantValue != null) {
            val = EditorUtils.valAsStr(constantValue);
        }
        return String.valueOf(val);
    }

    @Override
    public void setValue(Object value) {
        Object changedValue = value;
        setValueGeneric(changedValue);
        if (isSetValueDone()) {
            return;
        }
        // Get the corresponding constant if any
        for (Map.Entry<String, Charset> entry : this.availableCharsets.entrySet()) {
            if (changedValue.equals(entry.getValue())) {
                changedValue = entry.getKey();
            }
        }
        getTextField().setText(EditorUtils.valAsStr(changedValue));
    }

    @Override
    public void requestFocus() {
        EditorUtils.doNextFrame(() -> getTextField().requestFocus());
    }

    public void reset(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses,
                      Map<String, Charset> constants) {
        super.reset(propMeta, selectedClasses, new ArrayList<>(constants.keySet()));
    }

    /**
     * For the performance, it is much better to use standard charsets, than Charset.availableCharsets().
     * Need to be static!
     *
     * @return Map with the standard charsets.
     */
    public static Map<String, Charset> getStandardCharsets() {
        Map<String, Charset> charsets = new HashMap<>();
        charsets.put("UTF-8", StandardCharsets.UTF_8);
        charsets.put("UTF-16", StandardCharsets.UTF_16);
        charsets.put("UTF-16BE", StandardCharsets.UTF_16BE);
        charsets.put("UTF-16LE", StandardCharsets.UTF_16LE);
        charsets.put("US-ASCII", StandardCharsets.US_ASCII);
        charsets.put("ISO-8859-1", StandardCharsets.ISO_8859_1);
        return charsets;
    }

    private boolean isValidValue(String value) {
        boolean valid = false;
        if (this.availableCharsets.containsKey(value)) {
            valid = true;
        }
        return valid;
    }
}