/*
 * Copyright (c) 2023, 2024, Gluon and/or its affiliates.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkeletonCreatorJRuby implements SkeletonConverter {

    static final String NL = System.lineSeparator();
    static final String INDENT = "    "; //NOI18N

    SkeletonCreatorJRuby() {
        // no-op
    }

    public String createFrom(SkeletonContext context) {

        final StringBuilder sb = new StringBuilder();
        appendHeaderComment(context, sb);
        // Ruby supports packages... but we ignore it here because Java package != Ruby Module and
        // equating the two can cause confusion. Let the user fix it up themselves
        appendClass(context, sb);

        return sb.toString();
    }

    public String createApplicationFrom(SkeletonContext context) {

        return "require 'jrubyfx'\n" +
                "\n" +
                createFrom(context) +
                "\n" +
                "fxml_root File.dirname(__FILE__) # or wherever you save the fxml file to\n" +
                "\n" +
                "class " + makeClassName(context) + "Application < JRubyFX::Application\n" +
                "  def start(stage)\n" +
                "    " + makeClassName(context) + ".load_into(stage)\n" +
                "    #stage.title = \"" + makeClassName(context) + "\"\n" +
                "    stage.show\n" +
                "  end\n" +
                "  launch\n" +
                "end\n";
    }

    static Pattern importExtractor = Pattern.compile("import (([^.]+)\\..*)");

    void appendImports(SkeletonContext context, StringBuilder sb) {
        boolean output = false;
        // Optional, really, as JRubyFX imports them by default
        // Only "import" non-javafx ones in a comment
        for (String importStatement : context.getImports()) {
            Matcher matcher = importExtractor.matcher(importStatement);
            matcher.matches();
            String rootName = matcher.group(2);
            if (rootName.equals("javafx"))
                continue; // JRubyFX already imports these
            sb.append(INDENT).append("# java_import '").append(matcher.group(1)).append("'").append(NL);
            output = true;
        }
        if (output)
            sb.append(NL);
    }


    void appendHeaderComment(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isWithComments()) {
            return;
        }

        final String title = I18N.getString("skeleton.window.title", context.getDocumentName());
        sb.append("# ").append(title).append(NL); //NOI18N
    }


    void appendClass(SkeletonContext context, StringBuilder sb) {

        String controllerClassName = makeClassName(context);

        sb.append("class ").append(controllerClassName); //NOI18N

        sb.append(NL);
        sb.append(INDENT).append("include JRubyFX::Controller").append(NL).append(NL); //NOI18N

        appendImports(context, sb);

        if (context.getSettings().isWithComments()) {
            sb.append(INDENT).append("# Marks this class as being a controller for the given fxml document").append(NL); //NOI18N
            sb.append(INDENT).append("# This creates @instance_variables for all fx:id").append(NL); //NOI18N
        }
        String documentName = context.getDocumentName();
        if (!documentName.contains(".fxml")) {
            documentName += ".fxml";
        }
        sb.append(INDENT).append("fxml '").append(documentName).append("'").append(NL).append(NL); //NOI18N


        if (context.getSettings().isWithComments()) {
            sb.append(INDENT).append("# These @instance_variables will be injected by FXMLLoader & JRubyFX").append(NL); //NOI18N
        }

        appendFieldsWithFxId(context, sb);

        appendFieldsResourcesAndLocation(context, sb);

        appendInitialize(context, sb);

        appendEventHandlers(context, sb);

        sb.append("end").append(NL); //NOI18N
    }

    private String makeClassName(SkeletonContext context) {
        String controllerClassName = "PleaseProvideControllerClassName";

        if (hasController(context)) {
            controllerClassName = getControllerClassName(context);
        }
        return controllerClassName;
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

    void appendFieldParameters(StringBuilder sb, Class<?> fieldClazz) {
        final TypeVariable<? extends Class<?>>[] parameters = fieldClazz.getTypeParameters();
        if (parameters.length > 0) {
            sb.append("<"); //NOI18N
            String sep = ""; //NOI18N
            for (TypeVariable<?> ignored : parameters) {
                sb.append(sep);
                sb.append("?"); //NOI18N
                sep = ", "; //NOI18N
            }
            sb.append(">"); //NOI18N
        }
    }


    void appendFieldsResourcesAndLocation(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isFull()) {
            return;
        }

        // these aren't built into JRubyFX's fxml_helper.rb, so just manually add the fields for reification
        if (context.getSettings().isWithComments()) {
            sb.append(INDENT).append("# ResourceBundle that was given to the FXMLLoader. Access as self.resources, or @resources if instance_variable is true").append(NL); //NOI18N
        }
        sb.append(INDENT);
        sb.append("java_field '@javafx.fxml.FXML java.util.ResourceBundle resources', instance_variable: true");

        if (context.getSettings().isWithComments()) {
            sb.append(NL).append(NL).append(INDENT).append("# URL location of the FXML file that was given to the FXMLLoader. Access as self.location, or @location if instance_variable is true"); //NOI18N
        }
        sb.append(NL).append(INDENT);
        sb.append("java_field '@javafx.fxml.FXML java.net.URL location', instance_variable: true");
        sb.append(NL).append(NL);
    }


    void appendFieldsWithFxId(SkeletonContext context, StringBuilder sb) {
        for (Map.Entry<String, Class<?>> variable : context.getVariables().entrySet()) {
            sb.append(INDENT).append("# @").append(variable.getKey()).append(": \t").append(variable.getValue().getSimpleName()); //NOI18N
            appendFieldParameters(sb, variable.getValue()); // just for reference
            sb.append(NL);
        }
        sb.append(NL);
    }

    void appendInitialize(SkeletonContext context, StringBuilder sb) {
        if (!context.getSettings().isFull()) {
            return;
        }
        if (context.getSettings().isWithComments()) {
            sb.append(INDENT).append("# Called by JRubyFX after FXML loading is complete. Different from Java, same as normal Ruby"); //NOI18N
            sb.append(NL);
        }

        sb.append(INDENT);
        sb.append("def initialize()"); //NOI18N
        sb.append(NL);
        appendAssertions(context, sb);
        sb.append(NL);
        sb.append(INDENT);
        sb.append("end").append(NL).append(NL); //NOI18N
    }

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

    void appendEventHandler(String methodName, String eventClassName, StringBuilder sb) {
        sb.append("def "); //NOI18N
        sb.append(methodName);
        sb.append("(").append("event) # event: ").append(eventClassName).append(NL).append(NL); //NOI18N
        sb.append(INDENT).append("end"); //NOI18N
    }

    void appendAssertions(SkeletonContext context, StringBuilder sb) {
        for (String assertion : context.getAssertions()) {
            sb.append(INDENT).append(INDENT)
                    .append("raise 'fx:id=\"").append(assertion).append("\" was not injected: check your FXML file ") //NOI18N
                    .append("\"").append(context.getDocumentName()).append("\".' if ") //NOI18N
                    .append("@").append(assertion).append(".nil?").append(NL); //NOI18N
        }
    }

}
