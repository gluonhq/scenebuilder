/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.skeleton;

import com.oracle.javafx.scenebuilder.kit.i18n.I18N;

import java.lang.reflect.TypeVariable;
import java.util.Map;

public class SkeletonCreatorJava {

    private static final String NL = System.lineSeparator();
    private static final String INDENT = "    "; //NOI18N
    private static final String FXML_ANNOTATION = "@FXML";

    static String createFrom(SkeletonContext context) {
        final StringBuilder sb = new StringBuilder();

        appendHeaderComment(context, sb);
        appendPackage(context, sb);
        appendImports(context, sb);
        appendClass(context, sb);

        return sb.toString();
    }

    private static void appendHeaderComment(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isWithComments()) {
            return;
        }

        final String title = I18N.getString("skeleton.window.title", context.getDocumentName());
        sb.append("/**").append(NL); //NOI18N
        sb.append(" * ").append(title).append(NL); //NOI18N
        sb.append(" */").append(NL); //NOI18N
        sb.append(NL);
    }

    private static void appendPackage(SkeletonContext context, StringBuilder sb) {
        String controller = context.getFxController();

        if (controller != null && controller.contains(".") && !controller.contains("$")) { //NOI18N
            sb.append("package "); //NOI18N
            sb.append(controller, 0, controller.lastIndexOf('.')); //NOI18N
            sb.append(";").append(NL).append(NL); //NOI18N
        }
    }

    private static void appendImports(SkeletonContext context, StringBuilder sb) {
        for (String importStatement : context.getImports()) {
            sb.append(importStatement);
        }
    }

    private static void appendClass(SkeletonContext context, StringBuilder sb) {
        String controller = context.getFxController();

        sb.append(NL).append("public "); //NOI18N
        if (controller != null && controller.contains("$")) { //NOI18N
            sb.append("static "); //NOI18N
        }

        sb.append("class "); //NOI18N

        if (controller != null && !controller.isEmpty()) {
            String simpleName = controller.replace("$", "."); //NOI18N
            int dot = simpleName.lastIndexOf('.');
            if (dot > -1) {
                simpleName = simpleName.substring(dot + 1);
            }
            sb.append(simpleName);
        } else {
            sb.append("PleaseProvideControllerClassName"); //NOI18N
        }

        sb.append(" {").append(NL).append(NL); //NOI18N

        appendFields(context, sb);

        appendMethods(context, sb);

        sb.append("}").append(NL); //NOI18N
    }

    private static void appendFields(SkeletonContext context, StringBuilder sb) {
        appendFieldsResourcesAndLocation(context, sb);

        appendFieldsWithFxId(context, sb);
    }

    private static void appendFieldsResourcesAndLocation(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isFull()) {
            return;
        }

        sb.append(INDENT).append(FXML_ANNOTATION);
        if (context.getSettings().isWithComments()) {
            sb.append(" // ResourceBundle that was given to the FXMLLoader"); //NOI18N
        }
        sb.append(NL);
        sb.append(INDENT).append("private ResourceBundle resources;").append(NL).append(NL); //NOI18N

        sb.append(INDENT).append(FXML_ANNOTATION);
        if (context.getSettings().isWithComments()) {
            sb.append(" // URL location of the FXML file that was given to the FXMLLoader"); //NOI18N
        }
        sb.append(NL);
        sb.append(INDENT).append("private URL location;").append(NL).append(NL); //NOI18N
    }

    private static void appendFieldsWithFxId(SkeletonContext context, StringBuilder sb) {
        for (Map.Entry<String, Class<?>> variable : context.getVariables().entrySet()) {
            sb.append(INDENT).append(FXML_ANNOTATION);
            if (context.getSettings().isWithComments()) {
                sb.append(" // fx:id=\"").append(variable.getKey()).append("\""); //NOI18N
            }
            sb.append(NL);

            sb.append(INDENT).append("private ").append(variable.getValue().getSimpleName()); //NOI18N
            appendFieldParameters(sb, variable);

            sb.append(" ").append(variable.getKey()).append(";");
            if (context.getSettings().isWithComments()) {
                sb.append(" // Value injected by FXMLLoader"); //NOI18N
            }
            sb.append(NL).append(NL);
        }
    }

    private static void appendFieldParameters(StringBuilder sb, Map.Entry<String, Class<?>> variable) {
        final TypeVariable<? extends Class<?>>[] parameters = variable.getValue().getTypeParameters();
        if (parameters.length > 0) {
            sb.append("<"); //NOI18N
            String sep = ""; //NOI18N
            for (TypeVariable<?> t : parameters) {
                sb.append(sep).append("?"); //NOI18N
                sep = ", "; //NOI18N
                t.getName(); // silly call to silence FindBugs
            }
            sb.append(">"); //NOI18N
        }
    }

    private static void appendMethods(SkeletonContext context, StringBuilder sb) {
        appendEventHandlers(context, sb);

        appendInitialize(context, sb);
    }

    private static void appendEventHandlers(SkeletonContext context, StringBuilder sb) {
        for (Map.Entry<String, String> entry : context.getEventHandlers().entrySet()) {
            String methodName = entry.getKey();
            String eventName = entry.getValue();

            final String methodNamePured = methodName.replace("#", ""); //NOI18N

            sb.append(INDENT).append(FXML_ANNOTATION).append(NL).append(INDENT).append("void "); //NOI18N
            sb.append(methodNamePured);
            sb.append("(").append(eventName).append(" event) {").append(NL).append(NL);
            sb.append(INDENT).append("}").append(NL).append(NL); //NOI18N
        }
    }

    private static void appendInitialize(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isFull()) {
            return;
        }

        sb.append(INDENT).append(FXML_ANNOTATION);
        if (context.getSettings().isWithComments()) {
            sb.append(" // This method is called by the FXMLLoader when initialization is complete"); //NOI18N
        }
        sb.append(NL);

        sb.append(INDENT).append("void initialize() {").append(NL); //NOI18N
        appendAssertions(context, sb);
        sb.append(NL);
        sb.append(INDENT);
        sb.append("}").append(NL).append(NL); //NOI18N
    }

    private static void appendAssertions(SkeletonContext context, StringBuilder sb) {
        for (String assertion : context.getAssertions()) {
            sb.append(INDENT).append(INDENT)
                .append("assert ").append(assertion).append(" != null : ") //NOI18N
                .append("\"fx:id=\\\"").append(assertion).append("\\\" was not injected: check your FXML file ") //NOI18N
                .append("'").append(context.getDocumentName()).append("'.\";").append(NL); //NOI18N
        }
    }
}
