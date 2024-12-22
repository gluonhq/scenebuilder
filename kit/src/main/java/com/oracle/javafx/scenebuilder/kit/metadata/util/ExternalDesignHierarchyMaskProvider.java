/*
 * Copyright (c) 2024, Gluon and/or its affiliates.
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
 *  - Neither the name of Gluon nor the names of its
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
package com.oracle.javafx.scenebuilder.kit.metadata.util;

import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.HierarchyItem;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface ExternalDesignHierarchyMaskProvider {

    /**
     * List of external classes that their design mask should not
     * resize while used as top element of the layout
     * @return a List of classes
     */
    List<Class<?>> getExternalNonResizableItems();

    /**
     * List of external accessories
     * @return a list with external accessories
     */
    List<DesignHierarchyMask.Accessory> getExternalAccessories();

    /**
     * Returns a predicate for a given accessory, that takes an object
     * and returns true or false based on a given condition
     * @param accessory the accessory
     * @return a predicate to test an object
     */
    Predicate<Object> isExternalAccepting(DesignHierarchyMask.Accessory accessory);

    /**
     * Returns a map with external accessories as keys, and biFunctions that generate a valid HierarchyItem (or subclass)
     * given a mask and an FXOM object, that can be inserted in the Hierarchy Tree, as values for those keys.
     * @return a map of accessories and biFunctions to generate HierarchyItems
     */
    Map<DesignHierarchyMask.Accessory, BiFunction<DesignHierarchyMask, FXOMObject, HierarchyItem>> getExternalHierarchyItemGeneratorMap();

    /**
     * If the hierarchyItem object matches a given condition, gets an optional of the external accessory
     * for such object, else returns empty
     * @param hierarchyItem the HierarchyItem object
     * @return an optional with an accessory or empty
     */
    Optional<DesignHierarchyMask.Accessory> getExternalAccessoryForHierarchyItem(HierarchyItem hierarchyItem);
}