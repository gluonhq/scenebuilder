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

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class SkeletonCreatorJRuby extends AbstractSkeletonCreator {

    @Override
    void appendPackage(SkeletonContext context, StringBuilder sb) {
        // Ruby supports packages... but we ignore it here because Java package != Ruby Module and
        // equating the two can cause confusion. Let the user fix it up themselves
    }

    @Override
    void appendImports(SkeletonContext context, StringBuilder sb) {
        // Optional, really, as JRubyFX imports them by default
        for (String importStatement : context.getImports()) {
            //sb.append("java_import '").append(importStatement).append("'").append(NL);
        }
    }


    void appendHeaderComment(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isWithComments()) {
            return;
        }

        final String title = I18N.getString("skeleton.window.title", context.getDocumentName());
        sb.append("#").append(NL); //NOI18N
        sb.append("# ").append(title).append(NL); //NOI18N
        sb.append("# ").append(NL); //NOI18N
        sb.append(NL);
    }

    @Override
    void appendClassPart(SkeletonContext context, StringBuilder sb) {
        sb.append("class "); //NOI18N

        if (hasController(context)) {
            String controllerClassName = getControllerClassName(context);
            sb.append(controllerClassName);
        } else {
            sb.append("PleaseProvideControllerClassName"); //NOI18N
        }
    }

    @Override
    void appendClass(SkeletonContext context, StringBuilder sb) {
        sb.append(NL);

        appendClassPart(context, sb);

        sb.append(NL);
        sb.append(INDENT).append("include JRubyFX::Controller").append(NL).append(NL); //NOI18N

        if (context.getSettings().isFull()) {
            sb.append(INDENT).append("fxml '").append(context.getDocumentName()).append("'").append(NL).append(NL); //NOI18N
        }else{
            sb.append(INDENT).append("# fxml '").append(context.getDocumentName()).append("'").append(NL).append(NL); //NOI18N
        }

        appendFields(context, sb);

        appendMethods(context, sb);

        sb.append("end").append(NL); //NOI18N
    }

    private boolean hasController(SkeletonContext context) {
        return context.getFxController() != null && !context.getFxController().isEmpty();
    }

    private boolean hasNestedController(SkeletonContext context) {
        return hasController(context) && context.getFxController().contains("$"); //NOI18N
    }

    private String getControllerClassName(SkeletonContext context) {
        String simpleName = context.getFxController().replace("$", "."); //NOI18N
        int dot = simpleName.lastIndexOf('.');
        if (dot > -1) {
            simpleName = simpleName.substring(dot + 1);
        }
        return simpleName;
    }

    @Override
    void appendField(Class<?> fieldClass, String fieldName, StringBuilder sb) {
        sb.append("# @").append(fieldName).append(": ").append(fieldClass.getSimpleName()); //NOI18N
        appendFieldParameters(sb, fieldClass); // just for reference
    }

    @Override
    void appendFieldsResourcesAndLocation(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isFull()) {
            return;
        }
        // TODO: I don't think JRubyFX sets these?
/*
        if (context.getSettings().isWithComments()) {
            sb.append(" # ResourceBundle that was given to the FXMLLoader"); //NOI18N
        }
        sb.append(NL);
        sb.append(INDENT);
        appendField(ResourceBundle.class, "resources", sb); //NOI18N
        sb.append(NL).append(NL);

        if (context.getSettings().isWithComments()) {
            sb.append(" # URL location of the FXML file that was given to the FXMLLoader"); //NOI18N
        }
        sb.append(NL);
        sb.append(INDENT);
        appendField(URL.class, "location", sb); //NOI18N
        sb.append(NL).append(NL);*/
    }


    @Override
    void appendFieldsWithFxId(SkeletonContext context, StringBuilder sb) {
        for (Map.Entry<String, Class<?>> variable : context.getVariables().entrySet()) {
//            if (context.getSettings().isWithComments()) {
//                sb.append(INDENT).append("# fx:id=\"").append(variable.getKey()).append("\""); //NOI18N
//                sb.append(NL);
//            }

            sb.append(INDENT);
            appendField(variable.getValue(), variable.getKey(), sb);

            if (context.getSettings().isWithComments()) {
                sb.append(" (Value injected by FXMLLoader & JRubyFX)"); //NOI18N
            }
            sb.append(NL);
        }
        sb.append(NL);
    }

    @Override
    void appendInitialize(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isFull()) {
            return;
        }
        if (context.getSettings().isWithComments()) {
            sb.append(INDENT).append("# Called by JRubyFX after FXML loading is complete. Different from Java, same as normal Ruby"); //NOI18N
            sb.append(NL);
        }

        sb.append(INDENT);
        appendInitializeMethodPart(sb);
        sb.append(NL);
        appendAssertions(context, sb);
        sb.append(NL);
        sb.append(INDENT);
        sb.append("end").append(NL).append(NL); //NOI18N
    }

    @Override
    void appendEventHandlers(SkeletonContext context, StringBuilder sb) {
        for (Map.Entry<String, String> entry : context.getEventHandlers().entrySet()) {
            String methodName = entry.getKey();
            String eventClassName = entry.getValue();

            final String methodNamePured = methodName.replace("#", ""); //NOI18N

            sb.append(INDENT);
            appendEventHandler(methodNamePured, eventClassName, sb);
            sb.append(NL).append(NL);
        }
    }


    @Override
    void appendFieldParameterType(StringBuilder sb) {
        sb.append("?"); //NOI18N
    }

    @Override
    void appendEventHandler(String methodName, String eventClassName, StringBuilder sb) {
        sb.append("def "); //NOI18N
        sb.append(methodName);
        sb.append("(").append("event) # event: ").append(eventClassName).append(NL).append(NL); //NOI18N
        sb.append(INDENT).append("end"); //NOI18N
    }

    @Override
    void appendInitializeMethodPart(StringBuilder sb) {
        sb.append("def initialize()"); //NOI18N
    }

    @Override
    void appendAssertions(SkeletonContext context, StringBuilder sb) {
        for (String assertion : context.getAssertions()) {
            sb.append(INDENT).append(INDENT)
                .append("raise 'fx:id=\"").append(assertion).append("\" was not injected: check your FXML file ") //NOI18N
                .append("\"").append(context.getDocumentName()).append("\".' if ") //NOI18N
                .append("@").append(assertion).append(".nil?").append(NL); //NOI18N
        }
    }
}
