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

package com.oracle.javafx.scenebuilder.app.template;

import java.net.URL;

import static com.oracle.javafx.scenebuilder.app.template.Type.DESKTOP;
import static com.oracle.javafx.scenebuilder.app.template.Type.PHONE;

public enum Template {

    EMPTY_APP(DESKTOP, null),
    BASIC_DESKTOP_APP (DESKTOP, "BasicDesktopApplication.fxml"),
    COMPLEX_DESKTOP_APP (DESKTOP, "ComplexDesktopApplication.fxml"),
    EMPTY_PHONE_APP (PHONE, "EmptyPhoneApplication.fxml"),
    BASIC_PHONE_APP (PHONE, "BasicPhoneApplication.fxml");

    private Type type;
    private String fxmlFileName;

    Template(Type type, String fxmlFileName) {
        this.type = type;
        this.fxmlFileName = fxmlFileName;
    }

    public Type getType() {
        return type;
    }

    public String getFXMLFileName() {
        return fxmlFileName;
    }

    public URL getFXMLURL() {
        final String name = getFXMLFileName();
        if (name == null) {
            return null;
        }
        return Template.class.getResource(name);
    }
}
