/*
 * Copyright (c) 2019, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.library.user.ws;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.StaxDriver;

@XStreamAlias("LibraryWorkspace")
public class UserLibraryWorkspace {

    @XStreamAlias("Items") private Set<UserWorkspaceLibraryItem> items = new LinkedHashSet<>();
    @XStreamAlias("Filters") private Set<UserLibraryFilter> filters = new LinkedHashSet<>();
    
    
    public Set<UserWorkspaceLibraryItem> getItems() {
        return items;
    }
    public Set<UserLibraryFilter> getFilters() {
        return filters;
    }
    
    public void removeItem(Path path) {
        for (Iterator<UserWorkspaceLibraryItem> it = items.iterator(); it.hasNext();) {
            UserWorkspaceLibraryItem item = it.next();
            String itemPath = item.getPath();
            
            if (path.toString().equals(itemPath))
                it.remove();
        }
    }
    
    protected static XStream createXStream() {
        XStream xs = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xs);
        xs.autodetectAnnotations(true);
        xs.allowTypesByWildcard(new String[] { UserLibraryWorkspace.class.getPackage().getName() + ".**" }); // see https://stackoverflow.com/questions/44698296/security-framework-of-xstream-not-initialized-xstream-is-probably-vulnerable
        xs.alias("LibraryWorkspace", UserLibraryWorkspace.class);
        xs.alias("Item", UserWorkspaceLibraryItem.class);
        xs.alias("Filter", UserLibraryFilter.class);
        return xs;
    }
    
    public static UserLibraryWorkspace fromXml(String xml) {
        return (UserLibraryWorkspace) createXStream().fromXML(xml);
    }
    public static UserLibraryWorkspace fromXml(Reader reader) {
        return (UserLibraryWorkspace) createXStream().fromXML(reader);
    }
    public static UserLibraryWorkspace fromXml(InputStream is) {
        return (UserLibraryWorkspace) createXStream().fromXML(is);
    }
    public static UserLibraryWorkspace fromXml(URL url) {
        return (UserLibraryWorkspace) createXStream().fromXML(url);
    }
    
    public String toXml() {
        return formatXml(createXStream().toXML(this));
    }
    
    protected static String formatXml(String xml) {
        try {
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();

            serializer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$

            Source xmlSource = new SAXSource(new InputSource(new StringReader(xml)));
            StreamResult res =  new StreamResult(new ByteArrayOutputStream());

            serializer.transform(xmlSource, res);

            return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void mergeWith(UserLibraryWorkspace workspace) {
        items.addAll(workspace.items);
        filters.addAll(workspace.filters);
    }
    
}
