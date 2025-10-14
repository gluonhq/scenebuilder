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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.oracle.javafx.scenebuilder.kit.JfxInitializer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

public class TypeLookupTest {

    private Object source;

    private Optional<Class<?>> result;

    @BeforeAll
    public static void initialize() {
        JfxInitializer.initialize();
    }

    @Test
    void that_observable_array_list_type_is_detected_in_javafx_collections() {
        source = FXCollections.observableArrayList("A", "B");
        result = TypeLookup.findFXTypes(source);
        assertTrue(result.isPresent());
        assertEquals(javafx.collections.ObservableList.class, result.get());
    }

    @Test
    void that_empty_observable_array_list_is_detected_in_javafx_collections() {
        source = FXCollections.emptyObservableList();
        result = TypeLookup.findFXTypes(source);
        assertTrue(result.isPresent());
        assertEquals(javafx.collections.ObservableList.class, result.get());
    }

    @Test
    void a_that_empty_observable_array_list_is_detected_in_javafx_collections() {
        source = FXCollections.observableFloatArray(new float[] { 1f, 2f, 3f });
        result = TypeLookup.findFXTypes(source);
        assertTrue(result.isPresent());
        assertEquals(javafx.collections.ObservableFloatArray.class, result.get());
    }

    @Test
    void that_lambda_type_is_not_provided() {
        Runnable x = () -> {
            System.out.println(2);
        };
        result = TypeLookup.findFXTypes(x);
        assertFalse(result.isPresent());
    }

    @Test
    void that_ComboBox_class_is_provided_when_placed_in_javafx_package() {
        result = TypeLookup.findFXTypes(new ComboBox<>());
        assertTrue(result.isPresent());
        assertEquals(javafx.scene.control.ComboBox.class, result.get());
    }

    @Test
    void that_Pane_class_is_provided_when_placed_in_javafx_package() {
        result = TypeLookup.findFXTypes(new Pane());
        assertTrue(result.isPresent());
        assertEquals(javafx.scene.layout.Pane.class, result.get());
    }

    @Test
    void that_Binding_class_is_provided_when_placed_in_javafx_package() {
        IntegerProperty a = new SimpleIntegerProperty(1);
        IntegerProperty b = new SimpleIntegerProperty(1);
        source = Bindings.add(a, b);
        result = TypeLookup.findFXTypes(source);
        assertTrue(result.isPresent());
        assertEquals(javafx.beans.binding.NumberBinding.class, result.get());
    }

    @Test
    void that_Styleable_interface_is_detected_for_anonymous_class() {
        source = new Styleable() {

            @Override
            public String getTypeSelector() { return null; }

            @Override
            public Styleable getStyleableParent() { return null; }

            @Override
            public ObservableList<String> getStyleClass() { return null; }

            @Override
            public String getStyle() { return null; }

            @Override
            public ObservableSet<PseudoClass> getPseudoClassStates() { return null; }

            @Override
            public String getId() { return null; }

            @Override
            public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() { return null; }
        };

        result = TypeLookup.findFXTypes(source);
        assertTrue(result.isPresent());
        assertEquals(javafx.css.Styleable.class, result.get());

    }

    @Test
    void that_Styleable_interface_is_detected_for_inner_class() {
        source = new CustomJavaFxType();
        result = new TypeLookup("").findFirstPublicInterfaceOrSuperclass(source);
        assertTrue(result.isPresent());
        assertEquals(javafx.css.Styleable.class, result.get());
    }

    class CustomJavaFxType implements Styleable {

        @Override
        public String getTypeSelector() { return null; }

        @Override
        public Styleable getStyleableParent() { return null; }

        @Override
        public ObservableList<String> getStyleClass() { return null; }

        @Override
        public String getStyle() { return null; }

        @Override
        public ObservableSet<PseudoClass> getPseudoClassStates() { return null; }

        @Override
        public String getId() { return null; }

        @Override
        public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() { return null; }

    }

}
