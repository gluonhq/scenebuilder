/*
 * Copyright (c) 2024 Gluon and/or its affiliates.
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
import com.oracle.javafx.scenebuilder.kit.metadata.ExternalMetadataProvider;
import com.oracle.javafx.scenebuilder.kit.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.BooleanPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.EventHandlerPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.StringPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import javafx.scene.control.SelectionMode;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.oracle.javafx.scenebuilder.kit.metadata.Metadata.ButtonBaseMetadata;
import static com.oracle.javafx.scenebuilder.kit.metadata.Metadata.ControlMetadata;
import static com.oracle.javafx.scenebuilder.kit.metadata.Metadata.MenuItemMetadata;
import static com.oracle.javafx.scenebuilder.kit.metadata.Metadata.NodeMetadata;
import static com.oracle.javafx.scenebuilder.kit.metadata.Metadata.RegionMetadata;
import static com.oracle.javafx.scenebuilder.kit.metadata.Metadata.ToggleButtonMetadata;

public class GluonMetadataProvider implements ExternalMetadataProvider {

    // Abstract Component Classes
    private final ComponentClassMetadata BottomNavigationMetadata =
        new ComponentClassMetadata(com.gluonhq.charm.glisten.control.BottomNavigation.class, ControlMetadata);
    private final ComponentClassMetadata CardPaneMetadata =
        new ComponentClassMetadata(com.gluonhq.charm.glisten.control.CardPane.class, ControlMetadata);
    private final ComponentClassMetadata CollapsedPanelMetadata =
        new ComponentClassMetadata(com.gluonhq.charm.glisten.control.ExpansionPanel.CollapsedPanel.class, RegionMetadata);
    private final ComponentClassMetadata DropdownButtonMetadata =
        new ComponentClassMetadata(com.gluonhq.charm.glisten.control.DropdownButton.class, ControlMetadata);
    private final ComponentClassMetadata ExpandedPanelMetadata =
        new ComponentClassMetadata( com.gluonhq.charm.glisten.control.ExpansionPanel.ExpandedPanel.class, RegionMetadata);
    private final ComponentClassMetadata ExpansionPanelContainerMetadata =
        new ComponentClassMetadata(com.gluonhq.charm.glisten.control.ExpansionPanelContainer.class, ControlMetadata);
    private final ComponentClassMetadata ExpansionPanelMetadata =
        new ComponentClassMetadata( com.gluonhq.charm.glisten.control.ExpansionPanel.class, ControlMetadata);
    private final ComponentClassMetadata SettingsPaneMetadata =
        new ComponentClassMetadata(com.gluonhq.charm.glisten.control.SettingsPane.class, ControlMetadata);
    private final ComponentClassMetadata OptionMetadata =
        new ComponentClassMetadata(com.gluonhq.charm.glisten.control.settings.Option.class, null);
    private final ComponentClassMetadata ToggleButtonGroupMetadata =
        new ComponentClassMetadata(com.gluonhq.charm.glisten.control.ToggleButtonGroup.class, ControlMetadata);

    private static final PropertyName actionItemsName = new PropertyName("actionItems");
    private static final PropertyName togglesName = new PropertyName("toggles");
    private static final PropertyName titleNodesName = new PropertyName("titleNodes");
    private static final PropertyName optionsName = new PropertyName("options");
    private static final PropertyName itemsName = new PropertyName("items");

    public GluonMetadataProvider() {
        // Property Names
        final PropertyName bottomNavigationTypeName = new PropertyName("type");
        final PropertyName buttonsName = new PropertyName("buttons");
        final PropertyName collapsedContentName = new PropertyName("collapsedContent");
        final PropertyName contentName = new PropertyName("content");
        final PropertyName expandedContentName = new PropertyName("expandedContent");
        final PropertyName expandedName = new PropertyName("expanded");
        final PropertyName onPullToRefreshName = new PropertyName("onPullToRefresh");
        final PropertyName searchBoxVisibleName = new PropertyName("searchBoxVisible");
        final PropertyName selectionTypeName = new PropertyName("selectionType");
        final PropertyName titleFilterName = new PropertyName("titleFilter");

        // Property Metadata
        final ComponentPropertyMetadata actionItems_Node_PropertyMetadata =
            new ComponentPropertyMetadata(
                actionItemsName,
                NodeMetadata,
                true); /* collection */
        final EnumerationPropertyMetadata bottomNavigationTypePropertyMetadata =
            new EnumerationPropertyMetadata(
                bottomNavigationTypeName,
                BottomNavigation.Type.class,
                true, /* readWrite */
                BottomNavigation.Type.FIXED, /* defaultValue */
                new InspectorPath("Properties", "Specific", 0));
        final ComponentPropertyMetadata buttons_EXPANDEDPANEL_PropertyMetadata =
            new ComponentPropertyMetadata(
                buttonsName,
                ButtonBaseMetadata,
                true); /* collection */
        final ComponentPropertyMetadata collapsedContentPropertyMetadata =
            new ComponentPropertyMetadata(
                collapsedContentName,
                NodeMetadata,
                false /* collection */
            );
        final ComponentPropertyMetadata content_EXPANDEDPANEL_PropertyMetadata =
            new ComponentPropertyMetadata(
                contentName,
                NodeMetadata,
                false); /* collection */
        final ComponentPropertyMetadata expandedContentPropertyMetadata =
            new ComponentPropertyMetadata(
                expandedContentName,
                NodeMetadata,
                false /* collection */
            );
        final BooleanPropertyMetadata expandedPropertyMetadata =
            new BooleanPropertyMetadata(
                expandedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 0)
            );
        final ComponentPropertyMetadata items_ExpansionPanel_PropertyMetadata =
            new ComponentPropertyMetadata(
                itemsName,
                ExpansionPanelMetadata,
                true); /* collection */
        final ComponentPropertyMetadata items_MenuItem_PropertyMetadata =
            new ComponentPropertyMetadata(
                itemsName,
                MenuItemMetadata,
                true); /* collection */
        final ComponentPropertyMetadata items_Node_PropertyMetadata =
            new ComponentPropertyMetadata(
                itemsName,
                NodeMetadata,
                true); /* collection */
        final ValuePropertyMetadata onPullToRefreshPropertyMetadata =
            new EventHandlerPropertyMetadata(
                onPullToRefreshName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Specific", 0)
            );
        final ComponentPropertyMetadata options_Option_PropertyMetadata =
            new ComponentPropertyMetadata(
                optionsName,
                OptionMetadata,
                true); /* collection */
        final ValuePropertyMetadata searchBoxVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                searchBoxVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 0));
        final ValuePropertyMetadata selectionTypePropertyMetadata =
            new EnumerationPropertyMetadata(
                selectionTypeName,
                javafx.scene.control.SelectionMode.class,
                true, /* readWrite */
                SelectionMode.SINGLE,
                new InspectorPath("Properties", "Specific", 0));
        final ValuePropertyMetadata titleFilterPropertyMetadata =
            new StringPropertyMetadata(
                titleFilterName,
                true, /* readWrite */
                "",
                new InspectorPath("Properties", "Specific", 1));
        final ComponentPropertyMetadata titleNodes_Node_PropertyMetadata =
            new ComponentPropertyMetadata(
                titleNodesName,
                NodeMetadata,
                true); /* collection */
        final ComponentPropertyMetadata toggles_ToggleButton_PropertyMetadata =
            new ComponentPropertyMetadata(
                togglesName,
                ToggleButtonMetadata,
                true /* collection */
            );

        // ComponentMetadata -> PropertyMetadata
        BottomNavigationMetadata.getProperties().add(bottomNavigationTypePropertyMetadata);
        BottomNavigationMetadata.getProperties().add(actionItems_Node_PropertyMetadata);

        CardPaneMetadata.getProperties().add(items_Node_PropertyMetadata);
        CardPaneMetadata.getProperties().add(onPullToRefreshPropertyMetadata);

        CollapsedPanelMetadata.getProperties().add(titleNodes_Node_PropertyMetadata);

        DropdownButtonMetadata.getProperties().add(items_MenuItem_PropertyMetadata);

        ExpandedPanelMetadata.getProperties().add(content_EXPANDEDPANEL_PropertyMetadata);
        ExpandedPanelMetadata.getProperties().add(buttons_EXPANDEDPANEL_PropertyMetadata);

        ExpansionPanelContainerMetadata.getProperties().add(items_ExpansionPanel_PropertyMetadata);

        ExpansionPanelMetadata.getProperties().add(expandedContentPropertyMetadata);
        ExpansionPanelMetadata.getProperties().add(collapsedContentPropertyMetadata);
        ExpansionPanelMetadata.getProperties().add(expandedPropertyMetadata);

        SettingsPaneMetadata.getProperties().add(searchBoxVisiblePropertyMetadata);
        SettingsPaneMetadata.getProperties().add(titleFilterPropertyMetadata);
        SettingsPaneMetadata.getProperties().add(options_Option_PropertyMetadata);

        ToggleButtonGroupMetadata.getProperties().add(toggles_ToggleButton_PropertyMetadata);
        ToggleButtonGroupMetadata.getProperties().add(selectionTypePropertyMetadata);
    }

    @Override
    public List<ComponentClassMetadata> getExternalItems() {
        return Arrays.asList(BottomNavigationMetadata, CardPaneMetadata,
            CollapsedPanelMetadata, DropdownButtonMetadata,
            ExpandedPanelMetadata, ExpansionPanelContainerMetadata,
            ExpansionPanelMetadata, OptionMetadata,
            SettingsPaneMetadata, ToggleButtonGroupMetadata);
    }

    @Override
    public Optional<PropertyName> getExternalSubComponentProperty(Class<?> componentClass) {
        PropertyName result = null;
        if (componentClass == com.gluonhq.charm.glisten.control.BottomNavigation.class) {
            result = actionItemsName;
        } else if (componentClass == com.gluonhq.charm.glisten.control.CardPane.class ||
            componentClass == com.gluonhq.charm.glisten.control.DropdownButton.class ||
            componentClass == com.gluonhq.charm.glisten.control.ExpansionPanelContainer.class) {
            result = itemsName;
        } else if (componentClass == com.gluonhq.charm.glisten.control.ToggleButtonGroup.class) {
            result = togglesName;
        } else if (componentClass == com.gluonhq.charm.glisten.control.ExpansionPanel.CollapsedPanel.class) {
            result = titleNodesName;
        } else if (componentClass == com.gluonhq.charm.glisten.control.SettingsPane.class) {
            result = optionsName;
        }
        return Optional.ofNullable(result);
    }
}