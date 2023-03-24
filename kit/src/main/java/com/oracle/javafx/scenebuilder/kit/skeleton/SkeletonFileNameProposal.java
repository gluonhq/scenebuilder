/*
 * Copyright (c) 2021, 2023, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.skeleton;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 
 * Provides logic to create a proposal for a controller skeleton file for a
 * given language.
 * 
 * The proposal is created based on the actual fxmlLocation and the 
 * fxController name.
 *
 * <ol>
 * <li>undefined fxmlLocation and fxControllerName: - user directory with
 * PleaseProvideControllerClassName.java or PleaseProvideControllerClassName.kt.
 * 
 * <li>undefined fxControllerName but known fxmlLocation: - the name of the FXML
 * file (e.g. MyView.fxml) changed to MyViewController.fxml.
 * 
 * <li>undefined fxControllerName but known fxmlLocation, FXML stored in
 * src/main/resources: - for MyView.fxml and Java, the result would be
 * src/main/java/MyViewController.java, for Kotlin it would be
 * src/main/kotlin/MyViewController.kt
 *   
 * <li>controller name (e.g. MyController) defined and known fxmlLocation: - the
 * file would be MyController.java or MyController.kt at the place where the
 * FXML is stored - the proposal is also adjusted to reside in src/main/language
 * if possible.
 * <li>A provided controller name is always preferred over an adjusted FXML file
 * name.
 * </ol>
 * 
 * If the language specific directory does not exist, the original location is 
 * used with an adjusted file name.
 * 
 */
class SkeletonFileNameProposal {

    private static final String DEFAULT_CONTROLLER_CLASS_NAME = "PleaseProvideControllerClassName";
    private final SkeletonSettings.LANGUAGE language;

    public SkeletonFileNameProposal(SkeletonSettings.LANGUAGE language) {
        this.language = language;
    }

    /**
     * Creates a controller skeleton file name proposal depending on values for
     * fxmlLocation and fxControllerName.
     * 
     * @param fxmlLocation     The location of the FXML file in works. In case the
     *                         file has not been saved, this location will be null.
     * @param fxControllerName This is usually the name of the controller as defined
     *                         in the FXML file. If no controller name has been
     *                         given, this value might be null
     * @return file name proposal
     */
    public File create(URL fxmlLocation, String fxControllerName) {
        if (fxControllerName == null || fxControllerName.isBlank()) {
            if (null != fxmlLocation) {
                File controllerAtFxmlLocation = createFileAccordingToFxml(fxmlLocation);
                return adjustToSrcMainDirWhenPossible(controllerAtFxmlLocation);
            }
        } else {
            File fromControllerName = createFileFromControllerName(fxmlLocation, fxControllerName);
            return adjustToSrcMainDirWhenPossible(fromControllerName);
        }
        return createFileInUserDir();
    }

    private File adjustToSrcMainDirWhenPossible(File controllerAtFxmlLocation) {
        String location = controllerAtFxmlLocation.toPath().toString().replace('\\', '/');
        List<Path> sourcePackages = List.of(Paths.get("src/main"));
        for (Path sourcePackage : sourcePackages) {
            String resources = resolvePath(sourcePackage, "resources");
            String java = resolvePath(sourcePackage, "java");
            String kotlin = resolvePath(sourcePackage, "kotlin");
            if (location.contains(resources)) {
                switch (language) {
                case JAVA:
                    location = location.replace(resources, java);
                    break;
                case KOTLIN:
                    location = location.replace(resources, kotlin);
                    break;
                case JRUBY:
                    // use default location in "resources"
                    break;
                }
            }
        }
        File adjustedLocation = new File(location); 
        if (Files.exists(adjustedLocation.toPath().getParent())) {
            return adjustedLocation;
        }
        return controllerAtFxmlLocation;
    }
    
    private String resolvePath(Path source, String child) {
        return source.resolve(child)
                     .toString()
                     .replace('\\', '/');
    }

    private File createFileFromControllerName(URL fxmlLocation, String fxControllerName) {
        String directory = obtainUserDirectory();
        if (null != fxmlLocation) {
            URI uri = resolveURI(fxmlLocation);
            Path location = Paths.get(uri).toAbsolutePath().getParent();
            if (Files.exists(location)) {
                directory = location.toString();
            }
        }
        String simpleControllerClassName = extractSimpleControllerName(fxControllerName);
        String controllerFileName = simpleControllerClassName + language.getExtension();
        return new File(directory, controllerFileName);
    }

    private URI resolveURI(URL fxmlLocation) {
        try {
            return fxmlLocation.toURI();
        } catch (URISyntaxException e) {
            File userDir = new File(obtainUserDirectory()).getAbsoluteFile();
            return userDir.toURI();
        }
    }

    private File createFileAccordingToFxml(URL fxmlLocation) {
        URI  uri = resolveURI(fxmlLocation);
        Path fxmlFile = Paths.get(uri);
        String fxmlResource = fxmlFile.toString();
        String controllerSuffix = "Controller" + language.getExtension();
        int lastDot = fxmlResource.lastIndexOf('.');
        if (lastDot > -1) {
            String newFileName = fxmlResource.substring(0, lastDot) + controllerSuffix;
            return Paths.get(newFileName).toFile();
        }

        return Paths.get(fxmlResource + controllerSuffix).toFile();

    }

    private File createFileInUserDir() {
        String fileName = DEFAULT_CONTROLLER_CLASS_NAME + language.getExtension();
        String userDirectory = obtainUserDirectory();
        return buildFileName(userDirectory, fileName);
    }

    private String obtainUserDirectory() {
        return System.getProperty("user.home");
    }

    private File buildFileName(String userDirectory, String fileName) {
        return Paths.get(userDirectory, fileName).normalize().toAbsolutePath().toFile();
    }

    /*
     * TODO: Consider moving this into a shared place as this code snippet is used multiple times.
     * 
     */
    private String extractSimpleControllerName(String fxControllerName) {
        String simpleName = fxControllerName.replace("$", "."); // NOI18N
        int dot = simpleName.lastIndexOf('.');
        if (dot > -1) {
            simpleName = simpleName.substring(dot + 1);
        }
        return simpleName;
    }

}
