/*
 * Copyright (c) 2025, Gluon and/or its affiliates.
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

import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * Utility class to provide lookup of Types within a given package name space.
 */
class TypeLookup {

    private String packageNamePrefix;

    TypeLookup(String packageNamePrefix) {
        this.packageNamePrefix = packageNamePrefix;
    }

    /**
     * Attempts to identify the first public interface in any of the JavaFX
     * packages. If no public interface is found, the first public super class in
     * JavaFX packages will be identified.
     * 
     * If the given class itself is already member of any JavaFX package, this class
     * is provided in the result. Exceptions here are Lambdas, which are ignore. For
     * anonymous and inner classes the next public interface or the next public
     * super class in JavaFX package space are looked up.
     * 
     * Lambdas are ignored and will yield empty result values.
     * 
     * @param source Any object where a related JavaFX type is expected in type
     *               hierarchy.
     * @return Optional holding the JavaFX related class (if present).
     */
    public static Optional<Class<?>> findFXTypes(Object source) {
        if (null == source) {
            return Optional.empty();
        }
        return new TypeLookup("javafx.").findFirstPublicInterfaceOrSuperclass(source);
    }

    public Optional<Class<?>> findFirstPublicInterfaceOrSuperclass(Object obj) {
        if (obj == null || packageNamePrefix == null) {
            return Optional.empty();
        }

        Class<?> clazz = obj.getClass();

        /* Lambdas are not usable in FXML documents, hence will be ignored */
        if (clazz.getName().contains("$$")) {
            return Optional.empty();
        }

        /*
         * For top level classes, no further investigation is needed as long these
         * classes are already members of the package space.
         */
        if (clazz.getPackageName().startsWith(packageNamePrefix)
                && !clazz.getName().contains("$")
                && Modifier.isPublic(clazz.getModifiers())) {
            return Optional.of(clazz);
        }

        Class<?> intrface = findFirstPublicInterface(clazz);
        if (intrface != null) {
            return Optional.of(intrface);
        }

        return findFirstPublicSuperclass(clazz);
    }

    private Class<?> findFirstPublicInterface(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return null;
        }

        for (Class<?> intrface : clazz.getInterfaces()) {
            if (Modifier.isPublic(intrface.getModifiers()) && intrface.getPackageName().startsWith(packageNamePrefix)) {
                return intrface;
            }

            Class<?> found = findFirstPublicInterface(intrface);
            if (found != null) {
                return found;
            }
        }

        return findFirstPublicInterface(clazz.getSuperclass());
    }

    private Optional<Class<?>> findFirstPublicSuperclass(Class<?> clazz) {
        Class<?> current = clazz.getSuperclass();
        while (current != null && current != Object.class) {
            if (Modifier.isPublic(current.getModifiers()) && current.getPackageName().startsWith(packageNamePrefix)) {
                return Optional.of(current);
            }
            current = current.getSuperclass();
        }
        return Optional.of(clazz);
    }
}
