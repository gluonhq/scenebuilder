/*
 * Copyright (c) 2021, 2023, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.skeleton;

import com.oracle.javafx.scenebuilder.kit.i18n.I18N;

import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

abstract class AbstractSkeletonCreator implements SkeletonConverter {

    static final String NL = System.lineSeparator();
    static final String INDENT = "    "; //NOI18N
    static final String FXML_ANNOTATION = "@FXML";

    public String createFrom(SkeletonContext context) {
        final StringBuilder sb = new StringBuilder();

        appendHeaderComment(context, sb);
        appendPackage(context, sb);
        appendImports(context, sb);
        appendClass(context, sb);

        return sb.toString();
    }

    void appendHeaderComment(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isWithComments()) {
            return;
        }

        final String title = I18N.getString("skeleton.window.title", context.getDocumentName());
        sb.append("/**").append(NL); //NOI18N
        sb.append(" * ").append(title).append(NL); //NOI18N
        sb.append(" */").append(NL); //NOI18N
        sb.append(NL);
    }

    abstract void appendPackage(SkeletonContext context, StringBuilder sb);

    abstract void appendImports(SkeletonContext context, StringBuilder sb);

    void appendClass(SkeletonContext context, StringBuilder sb) {
        sb.append(NL);

        appendClassPart(context, sb);

        sb.append(" {").append(NL).append(NL); //NOI18N

        appendFields(context, sb);

        appendMethods(context, sb);

        sb.append("}").append(NL); //NOI18N
    }

    abstract void appendClassPart(SkeletonContext context, StringBuilder sb);

    void appendFields(SkeletonContext context, StringBuilder sb) {
        appendFieldsResourcesAndLocation(context, sb);

        appendFieldsWithFxId(context, sb);
    }

    void appendFieldsResourcesAndLocation(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isFull()) {
            return;
        }

        sb.append(INDENT).append(FXML_ANNOTATION);
        if (context.getSettings().isWithComments()) {
            sb.append(" // ResourceBundle that was given to the FXMLLoader"); //NOI18N
        }
        sb.append(NL);
        sb.append(INDENT);
        appendField(ResourceBundle.class, "resources", sb); //NOI18N
        sb.append(NL).append(NL);

        sb.append(INDENT).append(FXML_ANNOTATION);
        if (context.getSettings().isWithComments()) {
            sb.append(" // URL location of the FXML file that was given to the FXMLLoader"); //NOI18N
        }
        sb.append(NL);
        sb.append(INDENT);
        appendField(URL.class, "location", sb); //NOI18N
        sb.append(NL).append(NL);
    }

    void appendFieldsWithFxId(SkeletonContext context, StringBuilder sb) {
        for (Map.Entry<String, Class<?>> variable : context.getVariables().entrySet()) {
            sb.append(INDENT).append(FXML_ANNOTATION);
            if (context.getSettings().isWithComments()) {
                sb.append(" // fx:id=\"").append(variable.getKey()).append("\""); //NOI18N
            }
            sb.append(NL);

            sb.append(INDENT);
            appendField(variable.getValue(), variable.getKey(), sb);

            if (context.getSettings().isWithComments()) {
                sb.append(" // Value injected by FXMLLoader"); //NOI18N
            }
            sb.append(NL).append(NL);
        }
    }

    abstract void appendField(Class<?> fieldClass, String fieldName, StringBuilder sb);

    void appendFieldParameters(StringBuilder sb, Class<?> fieldClazz) {
        final TypeVariable<? extends Class<?>>[] parameters = fieldClazz.getTypeParameters();
        if (parameters.length > 0) {
            sb.append("<"); //NOI18N
            String sep = ""; //NOI18N
            for (TypeVariable<?> ignored : parameters) {
                sb.append(sep);
                appendFieldParameterType(sb);
                sep = ", "; //NOI18N
            }
            sb.append(">"); //NOI18N
        }
    }

    abstract void appendFieldParameterType(StringBuilder sb);

    void appendMethods(SkeletonContext context, StringBuilder sb) {
        appendEventHandlers(context, sb);

        appendInitialize(context, sb);
    }

    void appendEventHandlers(SkeletonContext context, StringBuilder sb) {
        for (Map.Entry<String, String> entry : context.getEventHandlers().entrySet()) {
            String methodName = entry.getKey();
            String eventClassName = entry.getValue();

            final String methodNamePured = methodName.replace("#", ""); //NOI18N

            sb.append(INDENT).append(FXML_ANNOTATION).append(NL).append(INDENT);
            appendEventHandler(methodNamePured, eventClassName, sb);
            sb.append(NL).append(NL);
        }
    }

    abstract void appendEventHandler(String methodName, String eventClassName, StringBuilder sb);

    void appendInitialize(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isFull()) {
            return;
        }

        sb.append(INDENT).append(FXML_ANNOTATION);
        if (context.getSettings().isWithComments()) {
            sb.append(" // This method is called by the FXMLLoader when initialization is complete"); //NOI18N
        }
        sb.append(NL);

        sb.append(INDENT);
        appendInitializeMethodPart(sb);
        sb.append(" {").append(NL); //NOI18N
        appendAssertions(context, sb);
        sb.append(NL);
        sb.append(INDENT);
        sb.append("}").append(NL).append(NL); //NOI18N
    }

    abstract void appendInitializeMethodPart(StringBuilder sb);

    abstract void appendAssertions(SkeletonContext context, StringBuilder sb);
}
