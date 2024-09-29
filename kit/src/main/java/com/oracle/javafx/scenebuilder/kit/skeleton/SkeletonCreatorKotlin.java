/*
 * Copyright (c) 2021, 2024, Gluon and/or its affiliates.
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

public class SkeletonCreatorKotlin extends AbstractSkeletonCreator {

    SkeletonCreatorKotlin() {
        // no-op
    }

    @Override
    void appendPackage(SkeletonContext context, StringBuilder sb) {
        String controller = context.getFxController();

        if (controller != null && controller.contains(".") && !controller.contains("$")) { //NOI18N
            sb.append("package "); //NOI18N
            sb.append(controller, 0, controller.lastIndexOf('.')); //NOI18N
            sb.append(NL).append(NL); //NOI18N
        }
    }

    @Override
    void appendImports(SkeletonContext context, StringBuilder sb) {
        for (String importStatement : context.getImports()) {
            sb.append(importStatement).append(NL);
        }
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

    private boolean hasController(SkeletonContext context) {
        return context.getFxController() != null && !context.getFxController().isEmpty();
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
        sb.append("private lateinit var ").append(fieldName).append(": ").append(fieldClass.getSimpleName()); //NOI18N
        appendFieldParameters(sb, fieldClass);
    }

    @Override
    void appendFieldParameterType(StringBuilder sb) {
        sb.append("Any"); //NOI18N
    }

    @Override
    void appendEventHandler(String methodName, String eventClassName, StringBuilder sb) {
        sb.append("fun "); //NOI18N
        sb.append(methodName);
        sb.append("(event: ").append(eventClassName).append(") {").append(NL).append(NL); //NOI18N
        sb.append(INDENT).append("}"); //NOI18N
    }

    @Override
    void appendInitializeMethodPart(StringBuilder sb) {
        sb.append("fun initialize()"); //NOI18N
    }

    @Override
    void appendAssertions(SkeletonContext context, StringBuilder sb) {
        for (String assertion : context.getAssertions()) {
            sb.append(INDENT).append(INDENT)
                .append("assert(").append(assertion).append(" != null) {") //NOI18N
                .append("\"fx:id=\\\"").append(assertion).append("\\\" was not injected: check your FXML file ") //NOI18N
                .append("'").append(context.getDocumentName()).append("'.\" }").append(NL); //NOI18N
        }
    }
}
