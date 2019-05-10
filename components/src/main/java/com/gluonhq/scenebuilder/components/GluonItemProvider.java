package com.gluonhq.scenebuilder.components;

import java.util.HashMap;
import java.util.Map;

import com.oracle.javafx.scenebuilder.kit.library.BuiltinLibrary;
import com.oracle.javafx.scenebuilder.kit.library.IExternalLibraryItemProvider;

public class GluonItemProvider implements IExternalLibraryItemProvider {

	@Override
	public Map<Class<?>, String> getItems() {
		Map<Class<?>, String> items = new HashMap<>();
		items.put(com.gluonhq.charm.glisten.control.AppBar.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.AutoCompleteTextField.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.Avatar.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.BottomNavigation.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.BottomNavigationButton.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.CardPane.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.CharmListView.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.Chip.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.ExpansionPanel.CollapsedPanel.class, BuiltinLibrary.TAG_GLUON);
//		items.put(com.gluonhq.charm.glisten.control.Dialog.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.DropdownButton.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.ExpansionPanel.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.ExpansionPanel.ExpandedPanel.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.ExpansionPanelContainer.class, BuiltinLibrary.TAG_GLUON);
//		items.put(com.gluonhq.charm.glisten.layout.layer.FloatingActionButton.class, BuiltinLibrary.TAG_GLUON);
//		items.put(com.gluonhq.charm.glisten.layout.responsive.grid.GridLayout.class, BuiltinLibrary.TAG_GLUON);
//		items.put(com.gluonhq.charm.glisten.layout.responsive.grid.GridRow.class, BuiltinLibrary.TAG_GLUON);
//		items.put(com.gluonhq.charm.glisten.layout.responsive.grid.GridSpan.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.Icon.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.layout.Layer.class, BuiltinLibrary.TAG_GLUON);
//		items.put(com.gluonhq.charm.glisten.control.ListTile.class, BuiltinLibrary.TAG_GLUON);
//		items.put(com.gluonhq.charm.glisten.layout.layer.MenuPopupView.class, BuiltinLibrary.TAG_GLUON);
//		items.put(com.gluonhq.charm.glisten.layout.layer.MenuSidePopupView.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.NavigationDrawer.class, BuiltinLibrary.TAG_GLUON);
//		items.put(com.gluonhq.charm.glisten.layout.layer.PopupView.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.ProgressBar.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.ProgressIndicator.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.SettingsPane.class, BuiltinLibrary.TAG_GLUON);
//		items.put(com.gluonhq.charm.glisten.layout.layer.SidePopupView.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.mvc.SplashView.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.TextField.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.control.ToggleButtonGroup.class, BuiltinLibrary.TAG_GLUON);
		items.put(com.gluonhq.charm.glisten.mvc.View.class, BuiltinLibrary.TAG_GLUON);
		return items;
	}

}
