/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.library;

import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument.FXOMDocumentSwitch;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 */
public class LibraryItem {
    
    private final String name;
    private final String section;
    private final String fxmlText;
    private final URL iconURL;
    private final Library library;

    public LibraryItem(String name, String section, String fxmlText, URL iconURL, Library library) {
        assert name != null;
        assert fxmlText != null;
        assert library != null;
        
        this.name = name;
        this.section = section;
        this.fxmlText = fxmlText;
        this.library = library;
        this.iconURL = iconURL;
    }

    public String getName() {
        return name;
    }

    public String getSection() {
        return section;
    }

    public String getFxmlText() {
        return fxmlText;
    }

    public URL getIconURL() {
        return iconURL;
    }

    public Library getLibrary() {
        return library;
    }
    
    public FXOMDocument instantiate() {
        FXOMDocument result;
        
        try {
            result = new FXOMDocument(fxmlText, null, library.getClassLoader(), null, FXOMDocumentSwitch.NORMALIZED);
        } catch(Error|IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Failed to instantiate a library item: ", e);
            result = null;
        }
        
        return result;
    }
    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.section);
        hash = 67 * hash + Objects.hashCode(this.fxmlText);
        hash = 67 * hash + Objects.hashCode(this.iconURL);
        hash = 67 * hash + Objects.hashCode(this.library);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LibraryItem other = (LibraryItem) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.section, other.section)) {
            return false;
        }
        if (!Objects.equals(this.fxmlText, other.fxmlText)) {
            return false;
        }
        if (!Objects.equals(this.iconURL, other.iconURL)) {
            return false;
        }
        return Objects.equals(this.library, other.library);
    }
    
    /*
     * Object
     */
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        
        result.append(getClass().getSimpleName());
        result.append('[');
        result.append(name);
        result.append(']');
        
        return result.toString();
    }
}
