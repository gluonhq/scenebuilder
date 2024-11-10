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
package com.gluonhq.scenebuilder.plugins;

import com.gluonhq.charm.glisten.control.BottomNavigation;
import com.gluonhq.charm.glisten.control.DropdownButton;
import com.gluonhq.charm.glisten.control.ExpansionPanel;
import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import com.gluonhq.scenebuilder.plugins.hierarchy.HierarchyItemExpandedPanel;
import com.gluonhq.scenebuilder.plugins.hierarchy.HierarchyItemExpansionPanel;
import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.HierarchyItem;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.metadata.util.ExternalDesignHierarchyMaskProvider;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class GluonDesignHierarchyMaskProvider implements ExternalDesignHierarchyMaskProvider {

    // ExpansionPanel
    private static final DesignHierarchyMask.Accessory EXPANDED_CONTENT =
        new DesignHierarchyMask.Accessory("EXPANDED_CONTENT", new PropertyName("expandedContent"), javafx.scene.Node.class, o -> true);
    private static final DesignHierarchyMask.Accessory COLLAPSED_CONTENT =
        new DesignHierarchyMask.Accessory("COLLAPSED_CONTENT", new PropertyName("collapsedContent"), javafx.scene.Node.class, o -> true);
    // ExpansionPanel.ExpandedPanel
    private static final DesignHierarchyMask.Accessory EX_CONTENT =
        new DesignHierarchyMask.Accessory("CONTENT", new PropertyName("content"), javafx.scene.Node.class, o -> true);

    @Override
    public List<Class<?>> getExternalNonResizableItems() {
        return List.of(
            BottomNavigation.class,
            DropdownButton.class,
            ExpansionPanel.CollapsedPanel.class,
            ExpansionPanel.ExpandedPanel.class,
            ToggleButtonGroup.class);
    }

    @Override
    public List<DesignHierarchyMask.Accessory> getExternalAccessories() {
        return List.of(EXPANDED_CONTENT, COLLAPSED_CONTENT, EX_CONTENT);
    }

    @Override
    public Predicate<Object> isExternalAccepting(DesignHierarchyMask.Accessory accessory) {
        if (accessory == DesignHierarchyMask.Accessory.CONTENT || accessory == DesignHierarchyMask.Accessory.GRAPHIC ||
            accessory == DesignHierarchyMask.Accessory.DP_CONTENT || accessory == DesignHierarchyMask.Accessory.DP_GRAPHIC) {
            // For these accessories, we accept every object except an ExpandedPanel
            return o -> !(o instanceof ExpansionPanel.ExpandedPanel);
        } else if (accessory == EX_CONTENT) {
            // For this accessory, we accept only an ExpandedPanel
            return o -> o instanceof ExpansionPanel.ExpandedPanel;
        } else if (accessory == DesignHierarchyMask.Accessory.EXPANDABLE_CONTENT || accessory == COLLAPSED_CONTENT) {
            // For these accessories, we accept only an ExpansionPanel
            return o -> o instanceof ExpansionPanel;
        }
        return o -> true;
    }

    @Override
    public Map<DesignHierarchyMask.Accessory, BiFunction<DesignHierarchyMask, FXOMObject, HierarchyItem>> getExternalHierarchyItemGeneratorMap() {
        return Map.of(
            EXPANDED_CONTENT, (mask, fxom) -> new HierarchyItemExpansionPanel(mask, fxom, EXPANDED_CONTENT),
            COLLAPSED_CONTENT, (mask, fxom) -> new HierarchyItemExpansionPanel(mask, fxom, COLLAPSED_CONTENT),
            EX_CONTENT, (mask, fxom) -> new HierarchyItemExpandedPanel(mask, fxom, EX_CONTENT)
        );
    }

    @Override
    public Optional<DesignHierarchyMask.Accessory> getExternalAccessoryForHierarchyItem(HierarchyItem hierarchyItem) {
        if (hierarchyItem instanceof HierarchyItemExpandedPanel expandedPanel) {
            return Optional.of(expandedPanel.getAccessory());
        } else if (hierarchyItem instanceof HierarchyItemExpansionPanel expansionPanel) {
            return Optional.of(expansionPanel.getAccessory());
        }
        return Optional.empty();
    }
}