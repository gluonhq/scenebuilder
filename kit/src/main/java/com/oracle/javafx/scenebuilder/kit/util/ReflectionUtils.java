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
package com.oracle.javafx.scenebuilder.kit.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;

/**
 * A temporary class created to reflectively call private and protected methods
 * from classes in JDK 9 unless we come up with a permanent solution.
 */
public class ReflectionUtils {

    private static final Map<String, Method> methodMap = new HashMap<>();

    private ReflectionUtils() {}

    public static void setStaticLoad(FXMLLoader loader, boolean staticLoad) {
        Class<?> clazz = loader.getClass();
        Method setStaticLoadMethod = methodMap.computeIfAbsent(clazz.getName(), s -> {
            try {
                Method method = clazz.getDeclaredMethod("setStaticLoad", boolean.class);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.WARNING, "Failed to find method setStaticLoad: ", e);
            }
            return null;
        });
        if (setStaticLoadMethod != null) {
            try {
                setStaticLoadMethod.invoke(loader, staticLoad);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.WARNING, "Failed to invoke method setStaticLoad: ", e);
            }
        }
    }

}
