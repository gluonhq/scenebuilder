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
package com.oracle.javafx.scenebuilder.kit.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class DocumentationUrls {
    
    private static DocumentationUrls instance = null;
    
    public static DocumentationUrls getInstance() {
        if (instance == null) {
            instance = new DocumentationUrls();
        } 
        return instance;
    }
    
    public static boolean isAvailable(DocumentationItem item) {
        return getInstance().getOptionalUrl(item).isPresent();
    }
    
    private final Map<DocumentationItem,String> configuredUrls;
    
    DocumentationUrls() {
        this("documentation_urls.properties");
    }
    
    DocumentationUrls(String resourceName) {
        configuredUrls = new EnumMap<>(DocumentationItem.class);
        loadProperties(resourceName);
    }

    private void loadProperties(String resourceName) {
        Properties docProps = new Properties();
        try (InputStream in = getClass().getResourceAsStream(resourceName)) {
            assert in != null;
            docProps.load(in);
            for (DocumentationItem item : DocumentationItem.values()) {
                String url = docProps.getProperty(item.getKey());
                if (null != url && !url.isBlank()) {
                    configuredUrls.put(item, url);
                } else if (item.defaultUrl != null) {                        
                    configuredUrls.put(item, item.defaultUrl);
                }
            }
        } catch (IOException cause) {
            throw new UncheckedIOException(cause);
        }
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
    
    String getAsMandatoryValue(DocumentationItem item) {
        String url = configuredUrls.get(item);
        assert url != null;
        return url;
    }
    
    public Optional<String> getOptionalUrl(DocumentationItem item) {
        return Optional.ofNullable(configuredUrls.get(item));
    }
    
    /**
     * The enum item name is used as properties key, therefore all "_"
     * are replaced with "." and the string is turned lowercase.
     * 
     * For some items defaults are defined as those are required
     * in various places inside SceneBuilder.
     * 
     * The items without default are optional. So removing them
     * from the resource will also lead to removal of these items
     * from GUI.
     * 
     */
    public enum DocumentationItem {
        /*
         * TODO: Clarify if documentation shall point to most recent JavaFX
         * release or to last LTS.
         * 
         * Proposal: per default point towards most recent releas BUT 
         * additionally offer link to LTS.
         *  
         */
        JAVADOC_HOME("https://openjfx.io/javadoc/11/"),
        ORACLE_DOCUMENTATION("https://docs.oracle.com/javafx/index.html"),
        
        GLUON_JAVADOC_HOME("https://docs.gluonhq.com/charm/javadoc/latest/"),
        GLUON_SCENEBUILDER_HOME("https://gluonhq.com/products/scene-builder/"),
        
        OPENJFX_GETTING_STARTED("https://openjfx.io/openjfx-docs/"),
        OPENJFX_JAVADOC_HOME("https://openjfx.io/javadoc/16/"),
        OPENJFX_CSS_REFERENCE("https://openjfx.io/javadoc/16/javafx.graphics/javafx/scene/doc-files/cssref.html"),
        OPENJFX_FXML_REFERENCE("https://openjfx.io/javadoc/16/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html"),
        
        COMMUNITY_CONTRIBUTE_SCENEBUILDER,
        COMMUNITY_OPENJFX_HOME("https://openjfx.io/index.html#fh5co-work"),
        COMMUNITY_DOCUMENTATION_FXDOCS,
        COMMUNITY_FRAMEWORKS_TESTFX,
        COMMUNITY_TUTORIALS_JENKOV,
        COMMUNITY_TUTORIALS_ALMASBAIM;
        
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
