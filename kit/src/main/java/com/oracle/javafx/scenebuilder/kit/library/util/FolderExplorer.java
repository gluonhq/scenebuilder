/*
 * Copyright (c) 2017, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.library.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.library.util.JarReportEntry.Status;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class FolderExplorer {

    private final Path rootFolderPath;

    public FolderExplorer(Path folderPath) {
        assert folderPath != null;
        assert folderPath.isAbsolute();

        this.rootFolderPath = folderPath;
    }

    public JarReport explore(ClassLoader classLoader) throws IOException {
        final JarReport result = new JarReport(rootFolderPath);

        try (Stream<Path> stream = Files.walk(rootFolderPath).filter(p -> !p.toFile().isDirectory())) {
            stream.forEach(p -> {
                JarReportEntry explored = exploreEntry(rootFolderPath, p, classLoader);
                if (explored.getStatus() != Status.IGNORED)
                    result.getEntries().add(explored);
            });
        };

        return result;
    }

    public static String makeFxmlText(Class<?> klass) {
        final StringBuilder result = new StringBuilder();

        /*
         * <?xml version="1.0" encoding="UTF-8"?> //NOI18N
         * 
         * <?import a.b.C?>
         * 
         * <C/>
         */

        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); //NOI18N

        result.append("<?import "); //NOI18N
        result.append(klass.getCanonicalName());
        result.append("?>"); //NOI18N
        result.append("<"); //NOI18N
        result.append(klass.getSimpleName());
        result.append("/>\n"); //NOI18N

        return result.toString();
    }


    public static Object instantiateWithFXMLLoader(Class<?> klass, ClassLoader classLoader) throws IOException {
        Object result;

        final String fxmlText = makeFxmlText(klass);
        final byte[] fxmlBytes = fxmlText.getBytes(Charset.forName("UTF-8")); //NOI18N

        final FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.setClassLoader(classLoader);
            result = fxmlLoader.load(new ByteArrayInputStream(fxmlBytes));
        } catch(IOException x) {
            throw x;
        } catch(RuntimeException|Error x) {
            throw new IOException(x);
        }

        return result;
    }

    /*
     * Private
     */

    private JarReportEntry exploreEntry(Path rootpath, Path path, ClassLoader classLoader) {
        JarReportEntry.Status status;
        Throwable entryException;
        Class<?> entryClass = null;
        String className;

        File file = path.toFile();

        if (file.isDirectory()) {
            status = JarReportEntry.Status.IGNORED;
            entryClass = null;
            entryException = null;
            className = null;
        } else {
            Path relativepath = rootpath.relativize(path);

            className = makeClassName(relativepath.toString());
            // Filtering out what starts with com.javafx. is bound to DTL-6378.
            if (className == null || className.startsWith("java.") //NOI18N
                    || className.startsWith("javax.") || className.startsWith("javafx.") //NOI18N
                    || className.startsWith("com.oracle.javafx.scenebuilder.") //NOI18N
                    || className.startsWith("com.javafx.")
                    || className.startsWith(EditorPlatform.GLUON_PACKAGE)) { //NOI18N
                status = JarReportEntry.Status.IGNORED;
                entryClass = null;
                entryException = null;
            } else {
                try {
                    // Some reading explaining why using Class.forName is not appropriate:
                    // http://blog.osgi.org/2011/05/what-you-should-know-about-class.html
                    // http://blog.bjhargrave.com/2007/09/classforname-caches-defined-class-in.html
                    // http://stackoverflow.com/questions/8100376/class-forname-vs-classloader-loadclass-which-to-use-for-dynamic-loading
                    entryClass = classLoader.loadClass(className); // Note: static intializers of entryClass are not run, this doesn't seem to be an issue

                    if (Modifier.isAbstract(entryClass.getModifiers())
                            || !Node.class.isAssignableFrom(entryClass)) {
                        status = JarReportEntry.Status.IGNORED;
                        entryClass = null;
                        entryException = null;
                    } else {
                        instantiateWithFXMLLoader(entryClass, classLoader);
                        status = JarReportEntry.Status.OK;
                        entryException = null;
                    }
                } catch (RuntimeException | IOException x) {
                    status = JarReportEntry.Status.CANNOT_INSTANTIATE;
                    entryException = x;
                } catch (Error | ClassNotFoundException x) {
                    status = JarReportEntry.Status.CANNOT_LOAD;
                    entryClass = null;
                    entryException = x;
                }
            }
        }

        return new JarReportEntry(file.getName(), status, entryException, entryClass, className);
    }


    private String makeClassName(String filename) {
        final String result;

        if (filename.endsWith(".class") == false) { //NOI18N
            result = null;
        } else if (filename.contains("$")) { //NOI18N
            // We skip inner classes for now
            result = null;
        } else {
            final int endIndex = filename.length()-6; // ".class" -> 6 //NOI18N
            result = filename.substring(0, endIndex).replace(File.separator, "."); //NOI18N
        }

        return result;
    }
}
