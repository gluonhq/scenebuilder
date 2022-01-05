/*
 * Copyright (c) 2022, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.fxom;

import java.util.Locale;
import java.util.Objects;

class FXMLPropertiesDisabler {
    private final OperatingSystem os;
    public FXMLPropertiesDisabler() {
        this(OperatingSystem.get());
    }
    public FXMLPropertiesDisabler(OperatingSystem os) {
        this.os = Objects.requireNonNull(os);
    }
    /**
     * In some cases, during FXML Loading, certain properties must be disabled.
     * This method modifies the FXML source accordingly.
     * 
     * @param fxmlText FXML source to be modified
     * @return FXML source with all properties disabled (=false) where WYSIWYG editing is not suitable.
     * @throws NullPointerException in case of fxmlText is null
     */
    public String disableProperties(String fxmlText) {
        Objects.requireNonNull(fxmlText, "fxmlText must not be null");
        String modifiedFxml = disableUseSystemMenuBarProperty(fxmlText);
        return modifiedFxml;
    }
    
    /**
     * On MacOS, when loading a FXML with a menu bar where useSystemMenuBarProperty()
     * is enabled, the menu in the FXML will hide the menu of Scene Builder. 
     * In this case, Scene Builder becomes unusable.
     * 
     * Setting the property here to false has the advantage, that the FXML to be saved
     * will still contain the defined property BUT the Scene Builder menu bar will remain 
     * visible.
     * 
     * The modification of properties which are not desired to be active while
     * editing must happen before loading the FXML using the FXMLLoader.
     * 
     * Here a disconnect between the FXOM and FXML is created as the state of the
     * useSystemMenuBarProperty is now different in both models.
     * 
     * @param fxmlText FXML source to be modified
     * @return FXML source with all properties disabled (=false) where WYSIWYG editing is not suitable.
     * @throws NullPointerException in case of fxmlText is null
     */
    private String disableUseSystemMenuBarProperty(String fxmlText) {
        Objects.requireNonNull(fxmlText, "fxmlText must not be null");
        if (OperatingSystem.MACOS.equals(os)) {
            return fxmlText.replace("useSystemMenuBar=\"true\"",
                                    "useSystemMenuBar=\"false\"");
        }
        return fxmlText;
    }
    
    enum OperatingSystem {
        MACOS,
        WINDOWS,
        LINUX;
    	/**
    	 * Obtains the operating system type from system property os.name.
    	 * @return {@link OperatingSystem}
    	 * @throws UnsupportedOperationException in case of an unknown operating system
    	 */
        public static OperatingSystem get() {
            String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            if (osName.contains("linux")) {
                return OperatingSystem.LINUX;
            }
            if (osName.contains("mac")) {
                return OperatingSystem.MACOS;
            }
            if (osName.contains("windows")) {
                return OperatingSystem.WINDOWS;
            }
            throw new UnsupportedOperationException("Unknown operating system platform!");
        }
    }
}
