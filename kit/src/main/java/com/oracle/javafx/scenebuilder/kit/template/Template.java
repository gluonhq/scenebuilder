/*
 * Copyright (c) 2017, 2022, Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.kit.template;

import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import javafx.scene.image.Image;

import java.net.URL;

import static com.oracle.javafx.scenebuilder.kit.template.Type.DESKTOP;
import static com.oracle.javafx.scenebuilder.kit.template.Type.PHONE;

public enum Template {

    EMPTY_APP(
            DESKTOP,
            "EmptyApplication.fxml",
            "empty_desktop.png",
            "template.title.new.empty.app",
            "template.description.new.empty.app"
    ),

    BASIC_DESKTOP_APP(
            DESKTOP,
            "BasicDesktopApplication.fxml",
            "basic_desktop.png",
            "template.title.new.basic.desktop.app",
            "template.description.new.basic.desktop.app"
    ),

    COMPLEX_DESKTOP_APP(
            DESKTOP,
            "ComplexDesktopApplication.fxml",
            "complex_desktop.png",
            "template.title.new.complex.desktop.app",
            "template.description.new.complex.desktop.app"
    ),

    EMPTY_PHONE_APP(
            PHONE,
            "EmptyPhoneApplication.fxml",
            "empty_mobile.png",
            "template.title.new.empty.phone.app",
            "template.description.new.empty.phone.app"
    ),

    BASIC_PHONE_APP(
            PHONE,
            "BasicPhoneApplication.fxml",
            "basic_mobile.png",
            "template.title.new.basic.phone.app",
            "template.description.new.basic.phone.app"
    );

    private Type type;
    private String fxmlFileName;
    private String imageFileName;
    private String uiNameKey;
    private String descriptionKey;

    Template(Type type, String fxmlFileName, String imageFileName, String uiNameKey, String descriptionKey) {
        this.type = type;
        this.fxmlFileName = fxmlFileName;
        this.imageFileName = imageFileName;
        this.uiNameKey = uiNameKey;
        this.descriptionKey = descriptionKey;
    }

    public Type getType() {
        return type;
    }

    public boolean isDesktop() {
        return type == DESKTOP;
    }

    public URL getFXMLURL() {
        return Template.class.getResource(fxmlFileName);
    }

    public Image getImage() {
        return new Image(Template.class.getResource(imageFileName).toExternalForm());
    }

    public String getUiName() {
        return I18N.getString(uiNameKey);
    }

    public String getDescription() {
        return I18N.getString(descriptionKey);
    }
}
