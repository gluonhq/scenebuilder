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
package com.oracle.javafx.scenebuilder.kit.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Documentation URLs are provided in a resource
 * {@code documentation_urls.properties}. These URLs point to OpenJFX pages such
 * as Javadoc API / CSS or FXML references. Those are meant to be used in the
 * Scene Builder help menu to eventually provide quick and easy access to
 * essential JavaFX developer documentation.
 */
public class DocumentationUrls {

    private static final String UNKNOWN = "unknown";

    private static DocumentationUrls instance = null;

    public static DocumentationUrls getInstance() {
        if (instance == null) {
            instance = new DocumentationUrls();
        }
        return instance;
    }

    /**
     * Verifies if the desired {@link DocumentationItem} is available.
     * 
     * @param item {@link DocumentationItem} (usually an URL)
     * @return true when available in corresponding resource
     */
    public static boolean isAvailable(DocumentationItem item) {
        return getInstance().getOptionalUrl(item).isPresent();
    }

    /**
     * Detects the used JavaFX version from system properties using
     * {@code System.getProperty("javafx.version")}. In case this property is
     * undefined, the String "unknown" is provided.
     * 
     * @return JavaFX version number or "unknown" when property is undefined
     */
    protected static String getJavaFxVersion() {
        return getJavaFxVersion(System.getProperties());
    }

    /**
     * Detects the used JavaFX version from given properties instance using property
     * key {@code "javafx.version"}. In case this property is undefined, the String
     * "unknown" is provided.
     * 
     * @return JavaFX version number or "unknown" when property is undefined
     */
    protected static String getJavaFxVersion(Properties systemProperties) {
        Objects.requireNonNull(systemProperties);
        String fxVersion = systemProperties.getProperty("javafx.version");
        if (fxVersion == null) {
            return UNKNOWN;
        }
        return fxVersion;
    }

    /**
     * @param version JavaFX version string
     * @return Provides the major version of the given version String.
     */
    protected static String getMajorJavaFxVersion(String version) {
        if (version == null || version.isBlank()) {
            return UNKNOWN;
        }
        int firstDot = version.indexOf('.');
        if (firstDot > 1) {
            return version.substring(0, firstDot);
        } else {
            return version;
        }
    }

    private final Map<DocumentationItem, String> configuredUrls;

    private final String javaFxVersion;

    private DocumentationUrls() {
        this(getJavaFxVersion(), "documentation_urls.properties");
    }

    DocumentationUrls(String javaFxVersion, String resourceName) {
        Objects.requireNonNull(resourceName, "resourceName must not be null");
        this.configuredUrls = new EnumMap<>(DocumentationItem.class);
        this.javaFxVersion = Objects.requireNonNull(javaFxVersion, "javaFxVersion must not be null");
        loadProperties(resourceName);
    }

    private void loadProperties(String resourceName) {
        String majorJavaFxVersion = getMajorJavaFxVersion(javaFxVersion);
        Properties docProps = new Properties();
        try (InputStream in = getClass().getResourceAsStream(resourceName)) {
            assert in != null;
            docProps.load(in);
            for (DocumentationItem item : DocumentationItem.values()) {
                String url = docProps.getProperty(item.getKey());
                if (null != url && !url.isBlank()) {
                    String resolvedUrl = resolveJavaFxVersion(url, majorJavaFxVersion);
                    configuredUrls.put(item, resolvedUrl);
                } else if (item.defaultUrl != null) {
                    configuredUrls.put(item, item.defaultUrl);
                }
            }
        } catch (IOException cause) {
            throw new UncheckedIOException(cause);
        }
    }

    private String resolveJavaFxVersion(String url, String majorFxVersion) {
        String placeHolder = "{javafx.version.major}";
        if (url.contains(placeHolder)) {
            return url.replace(placeHolder, majorFxVersion);
        }
        return url;
    }

    public String getJavadocHome() {
        return getAsMandatoryValue(DocumentationItem.JAVADOC_HOME);
    }

    public String getOracleDocumentation() {
        return getAsMandatoryValue(DocumentationItem.ORACLE_DOCUMENTATION);
    }

    public String getGluonJavadocHome() {
        return getAsMandatoryValue(DocumentationItem.GLUON_JAVADOC_HOME);
    }

    public String getOpenjfxGettingStarted() {
        return getAsMandatoryValue(DocumentationItem.OPENJFX_GETTING_STARTED);
    }

    public String getOpenjfxJavadocHome() {
        return getAsMandatoryValue(DocumentationItem.OPENJFX_JAVADOC_HOME);
    }

    public String getOpenjfxCssReference() {
        return getAsMandatoryValue(DocumentationItem.OPENJFX_CSS_REFERENCE);
    }

    public String getOpenjfxFxmlReference() {
        return getAsMandatoryValue(DocumentationItem.OPENJFX_FXML_REFERENCE);
    }

    public String getGluonScenebuilderHome() {
        return getAsMandatoryValue(DocumentationItem.GLUON_SCENEBUILDER_HOME);
    }

    public String getGettingStartedWithKotlin() {
        return getAsMandatoryValue(DocumentationItem.KOTLIN_GETTING_STARTED);
    }

    String getAsMandatoryValue(DocumentationItem item) {
        String url = configuredUrls.get(item);
        assert url != null;
        return url;
    }

    public Optional<String> getOptionalUrl(DocumentationItem item) {
        return Optional.ofNullable(configuredUrls.get(item));
    }

    /**
     * The enum item name is used as properties key, therefore all "_" are replaced
     * with "." and the string is turned lowercase.
     * 
     * For some items defaults are defined as those are required in various places
     * inside SceneBuilder.
     * 
     * The items without default are optional. So removing them from the resource
     * will also lead to removal of these items from GUI.
     * 
     */
    public enum DocumentationItem {
        JAVADOC_HOME("https://openjfx.io/javadoc/11/"),
        ORACLE_DOCUMENTATION("https://docs.oracle.com/javafx/index.html"),
        GLUON_JAVADOC_HOME("https://docs.gluonhq.com/charm/javadoc/latest/"),
        GLUON_SCENEBUILDER_HOME("https://gluonhq.com/products/scene-builder/"),

        OPENJFX_GETTING_STARTED("https://openjfx.io/openjfx-docs/"),
        OPENJFX_JAVADOC_HOME("https://openjfx.io/javadoc/16/"),
        OPENJFX_CSS_REFERENCE("https://openjfx.io/javadoc/16/javafx.graphics/javafx/scene/doc-files/cssref.html"),
        OPENJFX_FXML_REFERENCE("https://openjfx.io/javadoc/16/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html"),

        KOTLIN_GETTING_STARTED("https://kotlinlang.org/docs/getting-started.html"),

        GLUON_SCENEBUILDER_CONTRIBUTE;

        private final String defaultUrl;
        
        private DocumentationItem() {
            this(null);
        }

        private DocumentationItem(String defaultValue) {
            if (defaultValue != null) {
                assert !defaultValue.isBlank();
            }
            defaultUrl = defaultValue;
        }
        private String getKey() {
            return this.name().replace('_', '.').toLowerCase();
        }
    }
}
