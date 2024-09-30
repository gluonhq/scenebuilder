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

import com.oracle.javafx.scenebuilder.kit.library.ExternalSectionProvider;

import java.util.ArrayList;
import java.util.List;

public class GluonSectionProvider implements ExternalSectionProvider {

    private static final String TAG_GLUON = "Gluon";

    @Override
    public int getExternalSectionPosition() {
        return 2;
    }

    @Override
    public String getExternalSectionName() {
        return TAG_GLUON;
    }

    @Override
    public List<Class<?>> getExternalSectionItems() {
        List<Class<?>> items = new ArrayList<>();
        items.add(com.gluonhq.charm.glisten.control.AppBar.class);
        items.add(com.gluonhq.charm.glisten.control.AutoCompleteTextField.class);
        items.add(com.gluonhq.charm.glisten.control.Avatar.class);
        items.add(com.gluonhq.charm.glisten.control.BottomNavigation.class);
        items.add(com.gluonhq.charm.glisten.control.BottomNavigationButton.class);
        items.add(com.gluonhq.charm.glisten.control.CardPane.class);
        items.add(com.gluonhq.charm.glisten.control.CharmListView.class);
        items.add(com.gluonhq.charm.glisten.control.Chip.class);
        items.add(com.gluonhq.charm.glisten.control.ExpansionPanel.CollapsedPanel.class);
//		items.add(com.gluonhq.charm.glisten.control.Dialog.class);
        items.add(com.gluonhq.charm.glisten.control.DropdownButton.class);
        items.add(com.gluonhq.charm.glisten.control.ExpansionPanel.class);
        items.add(com.gluonhq.charm.glisten.control.ExpansionPanel.ExpandedPanel.class);
        items.add(com.gluonhq.charm.glisten.control.ExpansionPanelContainer.class);
//		items.add(com.gluonhq.charm.glisten.layout.layer.FloatingActionButton.class);
//		items.add(com.gluonhq.charm.glisten.layout.responsive.grid.GridLayout.class);
//		items.add(com.gluonhq.charm.glisten.layout.responsive.grid.GridRow.class);
//		items.add(com.gluonhq.charm.glisten.layout.responsive.grid.GridSpan.class);
        items.add(com.gluonhq.charm.glisten.control.Icon.class);
        items.add(com.gluonhq.charm.glisten.layout.Layer.class);
//		items.add(com.gluonhq.charm.glisten.control.ListTile.class);
//		items.add(com.gluonhq.charm.glisten.layout.layer.MenuPopupView.class);
//		items.add(com.gluonhq.charm.glisten.layout.layer.MenuSidePopupView.class);
        items.add(com.gluonhq.charm.glisten.control.NavigationDrawer.class);
//		items.add(com.gluonhq.charm.glisten.layout.layer.PopupView.class);
        items.add(com.gluonhq.charm.glisten.control.ProgressBar.class);
        items.add(com.gluonhq.charm.glisten.control.ProgressIndicator.class);
        items.add(com.gluonhq.charm.glisten.control.SettingsPane.class);
//		items.add(com.gluonhq.charm.glisten.layout.layer.SidePopupView.class);
        items.add(com.gluonhq.charm.glisten.mvc.SplashView.class);
        items.add(com.gluonhq.charm.glisten.control.TextField.class);
        items.add(com.gluonhq.charm.glisten.control.ToggleButtonGroup.class);
        items.add(com.gluonhq.charm.glisten.mvc.View.class);
        return items;
    }

    @Override
    public String getItemsFXMLPath() {
        return "library/builtin";
    }

    @Override
    public String getItemsIconPath() {
        return "editor/images/nodeicons";
    }
}
