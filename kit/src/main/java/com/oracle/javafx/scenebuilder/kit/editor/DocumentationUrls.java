/*
 * Copyright (c) 2022, 2025, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor;

/**
 * Provides URLs which are supposed to be used in Scene Builder help menu. These
 * URLs point to OpenJFX pages such as Javadoc API / CSS or FXML references.
 * Some of the pre configured URLs may contain a placeholder for the major
 * JavaFX version ({@code {javafx.version.major}}). This placeholder is
 * substituted at runtime with the detected JavaFX version and if no JavaFX is
 * found, the string unknown is used.
 */
public enum DocumentationUrls {

    /**
     * Javadoc home (for Inspector and CSS Analyzer properties)
     */
    JAVADOC_HOME("https://openjfx.io/javadoc/24/"),
    
    /**
     * This URL is where you go when the user takes Scene Builder Help action (shortcut F1)
     */
    ORACLE_DOCUMENTATION("https://docs.oracle.com/javafx/index.html"),
    
    GLUON_SCENEBUILDER_HOME("https://gluonhq.com/products/scene-builder/"),

    OPENJFX_GETTING_STARTED("https://openjfx.io/openjfx-docs/"),
    OPENJFX_JAVADOC_HOME("https://openjfx.io/javadoc/{javafx.version.major}/"),
    OPENJFX_CSS_REFERENCE("https://openjfx.io/javadoc/{javafx.version.major}/javafx.graphics/javafx/scene/doc-files/cssref.html"),
    OPENJFX_FXML_REFERENCE("https://openjfx.io/javadoc/{javafx.version.major}/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html"),

    GLUON_SCENEBUILDER_CONTRIBUTE("https://github.com/gluonhq/scenebuilder"),
    
    /**
     * 3rd Party 
     */
    JFXCENTRAL_HOMEPAGE("https://www.jfx-central.com");

    private static final String SAFE_DEFAULT_VERSION = "21";

    private static String javaFxMajorVersion = null;
    
    private final String url;
    
    private DocumentationUrls(String defaultValue) {
        url = defaultValue;
    }
    
    /**
     * Provides the desired URL with place holders resolved to their values.
     */
    @Override
    public String toString() {
        if (javaFxMajorVersion == null) {
            javaFxMajorVersion = getMajorJavaFxVersion();
        }
        return resolveJavaFxVersion(url, javaFxMajorVersion);
    }

    String getConfiguredValue() {
        return url;
    }

    private static String resolveJavaFxVersion(String url, String majorFxVersion) {
        String placeHolder = "{javafx.version.major}";
        if (url.contains(placeHolder)) {
            return url.replace(placeHolder, majorFxVersion);
        }
        return url;
    }

    /**
     * Detects the used JavaFX version and provides the major version number.
     * If JavaFX is not detected, a safe value for the major version is returned.
     * 
     * @return Provides the major version of the given version String
     */
    static String getMajorJavaFxVersion() {
        return getMajorJavaFxVersion(System.getProperty("javafx.version",SAFE_DEFAULT_VERSION));
    }

    /**
     * @param version JavaFX version string
     * @return Provides the major version of the given version String.
     */
    static String getMajorJavaFxVersion(String version) {
        int firstDot = version.indexOf('.');
        if (firstDot > 1) {
            return version.substring(0, firstDot);
        } else {
            return version;
        }
    }
}
