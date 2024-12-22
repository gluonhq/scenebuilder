/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.app.preferences;

import com.gluonhq.scenebuilder.plugins.editor.GluonEditorPlatform;
import com.oracle.javafx.scenebuilder.app.SceneBuilderApp;
import com.oracle.javafx.scenebuilder.kit.ToolTheme;
import com.oracle.javafx.scenebuilder.app.i18n.I18N;

import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase.ALIGNMENT_GUIDES_COLOR;
import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase.BACKGROUND_IMAGE;
import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase.ROOT_CONTAINER_HEIGHT;
import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase.ROOT_CONTAINER_WIDTH;
import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase.HIERARCHY_DISPLAY_OPTION;
import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase.LIBRARY_DISPLAY_OPTION;
import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase.PARENT_RING_COLOR;
import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesControllerBase.THEME;

import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesController.ACCORDION_ANIMATION;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesController.CSS_TABLE_COLUMNS_ORDERING_REVERSED;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesController.GLUON_SWATCH;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesController.GLUON_THEME;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesController.RECENT_ITEMS;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesController.RECENT_ITEMS_SIZE;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesController.TOOL_THEME;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesController.WILDCARD_IMPORT;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesController.ALTERNATE_TEXT_INPUT_PASTE;

import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesRecordGlobalBase.DEFAULT_ALIGNMENT_GUIDES_COLOR;
import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesRecordGlobalBase.DEFAULT_BACKGROUND_IMAGE;
import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesRecordGlobalBase.DEFAULT_PARENT_RING_COLOR;
import static com.oracle.javafx.scenebuilder.kit.preferences.PreferencesRecordGlobalBase.DEFAULT_THEME;

import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal.DEFAULT_TOOL_THEME;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal.DEFAULT_GLUON_SWATCH;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal.recentItemsSizes;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal.DEFAULT_HIERARCHY_DISPLAY_OPTION;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal.DEFAULT_LIBRARY_DISPLAY_OPTION;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal.DEFAULT_RECENT_ITEMS_SIZE;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal.DEFAULT_ROOT_CONTAINER_HEIGHT;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal.DEFAULT_ROOT_CONTAINER_WIDTH;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal.DEFAULT_ACCORDION_ANIMATION;
import static com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal.DEFAULT_WILDCARD_IMPORTS;

import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.AbstractHierarchyPanelController.DisplayOption;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors.DoubleField;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController.DISPLAY_MODE;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferencesRecordGlobalBase.BackgroundImage;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal.CSSAnalyzerColumnsOrder;
import com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.PaintPicker;
import com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.PaintPicker.Mode;

import java.util.Arrays;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Preferences window controller.
 */
public class PreferencesWindowController extends AbstractFxmlWindowController {

    @FXML
    private DoubleField rootContainerHeight;
    @FXML
    private DoubleField rootContainerWidth;
    @FXML
    private ChoiceBox<BackgroundImage> backgroundImage;
    @FXML
    private ChoiceBox<ToolTheme> scenebuilderTheme;
    @FXML
    private ChoiceBox<DISPLAY_MODE> libraryDisplayOption;
    @FXML
    private ChoiceBox<DisplayOption> hierarchyDisplayOption;
    @FXML
    private ChoiceBox<CSSAnalyzerColumnsOrder> cssAnalyzerColumnsOrder;
    @FXML
    private MenuButton alignmentGuidesButton;
    @FXML
    private MenuButton parentRingButton;
    @FXML
    private CustomMenuItem alignmentGuidesMenuItem;
    @FXML
    private CustomMenuItem parentRingMenuItem;
    @FXML
    private Rectangle alignmentGuidesGraphic;
    @FXML
    private Rectangle parentRingGraphic;
    @FXML
    private ChoiceBox<Integer> recentItemsSize;
    @FXML
    private ChoiceBox<EditorPlatform.Theme> themes;
    @FXML
    private ChoiceBox<EditorPlatform.Theme> gluonSwatch;
    @FXML
    private CheckBox animateAccordion;
    @FXML
    private CheckBox wildcardImports;
    @FXML
    private CheckBox alternatePasteBehavior;
    @FXML
    private Label alternatePasteBehaviorLabel;
    @FXML
    private Pane alternatePasteBehaviorPane;

    private PaintPicker alignmentColorPicker;
    private PaintPicker parentRingColorPicker;

    private Stage ownerWindow;

    public PreferencesWindowController(Stage ownerWindow) {
        super(PreferencesWindowController.class.getResource("Preferences.fxml"), //NOI18N
                I18N.getBundle(), ownerWindow);
        this.ownerWindow = ownerWindow;
    }

    /*
     * AbstractModalDialog
     */
    @Override
    protected void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();

        final PreferencesController preferencesController
                = PreferencesController.getSingleton();
        final PreferencesRecordGlobal recordGlobal
                = preferencesController.getRecordGlobal();

        // Root container size
        rootContainerHeight.setText(String.valueOf(recordGlobal.getRootContainerHeight()));
        rootContainerHeight.setOnAction(t -> {
            final String value = rootContainerHeight.getText();
            recordGlobal.setRootContainerHeight(Double.valueOf(value));
            rootContainerHeight.selectAll();
            // Update preferences
            recordGlobal.writeToJavaPreferences(ROOT_CONTAINER_HEIGHT);
            // Update UI
//            recordGlobal.refreshRootContainerHeight();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshRootContainerHeight(recordGlobal));
        });
        rootContainerWidth.setText(String.valueOf(recordGlobal.getRootContainerWidth()));
        rootContainerWidth.setOnAction(t -> {
            final String value = rootContainerWidth.getText();
            recordGlobal.setRootContainerWidth(Double.valueOf(value));
            rootContainerWidth.selectAll();
            // Update preferences
            recordGlobal.writeToJavaPreferences(ROOT_CONTAINER_WIDTH);
            // Update UI
//            recordGlobal.refreshRootContainerWidth();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshRootContainerWidth(recordGlobal));
        });

        // Background image
        backgroundImage.getItems().setAll(Arrays.asList(BackgroundImage.class.getEnumConstants()));
        backgroundImage.setValue(recordGlobal.getBackgroundImage());
        backgroundImage.getSelectionModel().selectedItemProperty().addListener(new BackgroundImageListener());

        // PaintPicker delegate shared by alignmentColorPicker and parentRingColorPicker
        final PaintPicker.Delegate delegate = new PaintPickerDelegate();

        // Alignment guides color
        final Color alignmentColor = recordGlobal.getAlignmentGuidesColor();
        alignmentColorPicker = new PaintPicker(delegate, Mode.COLOR);
        alignmentGuidesGraphic.setFill(alignmentColor);
        alignmentGuidesMenuItem.setContent(alignmentColorPicker);
        alignmentColorPicker.setPaintProperty(alignmentColor);
        alignmentColorPicker.paintProperty().addListener(
                new AlignmentGuidesColorListener(alignmentGuidesGraphic));

        // Parent ring color
        final Color parentRingColor = recordGlobal.getParentRingColor();
        parentRingColorPicker = new PaintPicker(delegate, Mode.COLOR);
        parentRingGraphic.setFill(parentRingColor);
        parentRingMenuItem.setContent(parentRingColorPicker);
        parentRingColorPicker.setPaintProperty(parentRingColor);
        parentRingColorPicker.paintProperty().addListener(
                new ParentRingColorListener(parentRingGraphic));

        // Tool theme
        scenebuilderTheme.getItems().setAll(Arrays.asList(ToolTheme.class.getEnumConstants()));
        scenebuilderTheme.setValue(recordGlobal.getToolTheme());
        scenebuilderTheme.getSelectionModel().selectedItemProperty().addListener(new ToolThemeListener());

        // Library view option
        final DISPLAY_MODE availableDisplayMode[] = new DISPLAY_MODE[]{
            DISPLAY_MODE.LIST, DISPLAY_MODE.SECTIONS};
        libraryDisplayOption.getItems().setAll(Arrays.asList(availableDisplayMode));
        libraryDisplayOption.setValue(recordGlobal.getLibraryDisplayOption());
        libraryDisplayOption.getSelectionModel().selectedItemProperty().addListener(new LibraryOptionListener());

        // Hierarchy display option
        hierarchyDisplayOption.getItems().setAll(Arrays.asList(DisplayOption.class.getEnumConstants()));
        hierarchyDisplayOption.setValue(recordGlobal.getHierarchyDisplayOption());
        hierarchyDisplayOption.getSelectionModel().selectedItemProperty().addListener(new DisplayOptionListener());

        // CSS analyzer column order
        cssAnalyzerColumnsOrder.getItems().setAll(Arrays.asList(CSSAnalyzerColumnsOrder.class.getEnumConstants()));
        cssAnalyzerColumnsOrder.setValue(recordGlobal.getCSSAnalyzerColumnsOrder());
        cssAnalyzerColumnsOrder.getSelectionModel().selectedItemProperty().addListener(new ColumnOrderListener());

        // Theme and Gluon Theme
        themes.getItems().setAll(EditorPlatform.Theme.getThemeList());
        themes.setValue(recordGlobal.getTheme());
        themes.getSelectionModel().selectedItemProperty().addListener(new ThemesListener());

        gluonSwatch.getItems().setAll(GluonEditorPlatform.getGluonSwatchList());
        gluonSwatch.setValue(recordGlobal.getSwatch());
        gluonSwatch.getSelectionModel().selectedItemProperty().addListener(new SwatchListener());
        
        // Number of open recent items
        recentItemsSize.getItems().setAll(recentItemsSizes);
        recentItemsSize.setValue(recordGlobal.getRecentItemsSize());
        recentItemsSize.getSelectionModel().selectedItemProperty().addListener(new RecentItemsSizeListener());

        // Accordion Animation
        animateAccordion.setSelected(recordGlobal.isAccordionAnimation());
        animateAccordion.selectedProperty().addListener(new AnimationListener());

        // Wildcard Imports
        wildcardImports.setSelected(recordGlobal.isWildcardImports());
        wildcardImports.selectedProperty().addListener(new WildcardImportListener());

        if (EditorPlatform.IS_MAC) {
            // Alternate paste behavior for Text Input Controls
            alternatePasteBehavior.setSelected(recordGlobal.isAlternateTextInputControlPaste());
            alternatePasteBehavior.selectedProperty().addListener(new AlternatePasteListener());
        } else {
            // This setting is supposed to to be invisible on other platforms.
            alternatePasteBehaviorPane.setDisable(true);
            alternatePasteBehaviorPane.setVisible(false);
            alternatePasteBehaviorPane.setManaged(false);
        }
    }

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
        assert getRoot().getScene().getWindow() != null;

        getStage().setTitle(I18N.getString("prefs.title"));
        getStage().initModality(Modality.APPLICATION_MODAL);
        getStage().initOwner(ownerWindow);
        getStage().setResizable(false);
    }

    @Override
    public void onCloseRequest(WindowEvent event) {
        super.closeWindow();
    }

    @FXML
    void resetToDefaultAction(ActionEvent event) {
        final PreferencesController preferencesController
                = PreferencesController.getSingleton();
        final PreferencesRecordGlobal recordGlobal
                = preferencesController.getRecordGlobal();

        // Root container size
        rootContainerHeight.setText(String.valueOf(DEFAULT_ROOT_CONTAINER_HEIGHT));
        rootContainerHeight.getOnAction().handle(new ActionEvent());
        rootContainerWidth.setText(String.valueOf(DEFAULT_ROOT_CONTAINER_WIDTH));
        rootContainerWidth.getOnAction().handle(new ActionEvent());

        // Background image
        backgroundImage.setValue(DEFAULT_BACKGROUND_IMAGE);

        // Alignment guides color
        alignmentColorPicker.setPaintProperty(DEFAULT_ALIGNMENT_GUIDES_COLOR);

        // Parent ring color
        parentRingColorPicker.setPaintProperty(DEFAULT_PARENT_RING_COLOR);

        // SceneBuilder theme
        scenebuilderTheme.setValue(DEFAULT_TOOL_THEME);

        // Library view option
        libraryDisplayOption.setValue(DEFAULT_LIBRARY_DISPLAY_OPTION);

        // Hierarchy display option
        hierarchyDisplayOption.setValue(DEFAULT_HIERARCHY_DISPLAY_OPTION);

        // CSS analyzer column order
        cssAnalyzerColumnsOrder.setValue(recordGlobal.getDefaultCSSAnalyzerColumnsOrder());

        // Number of open recent items
        recentItemsSize.setValue(DEFAULT_RECENT_ITEMS_SIZE);

        // Default theme
        themes.setValue(DEFAULT_THEME);

        // Default Gluon swatch
        gluonSwatch.setValue(DEFAULT_GLUON_SWATCH);

        // Default Accordion Animation
        animateAccordion.setSelected(DEFAULT_ACCORDION_ANIMATION);

        // Default Wildcard import
        wildcardImports.setSelected(DEFAULT_WILDCARD_IMPORTS);
    }

    /**
     * *************************************************************************
     * Static inner class
     * *************************************************************************
     */
    private static class BackgroundImageListener implements ChangeListener<BackgroundImage> {

        @Override
        public void changed(ObservableValue<? extends BackgroundImage> observable,
                BackgroundImage oldValue, BackgroundImage newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setBackgroundImage(newValue);
            recordGlobal.writeToJavaPreferences(BACKGROUND_IMAGE);
            // Update UI
//            recordGlobal.refreshBackgroundImage();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshBackgroundImage(recordGlobal));
        }
    }

    private static class ToolThemeListener implements ChangeListener<ToolTheme> {

        @Override
        public void changed(ObservableValue<? extends ToolTheme> observable,
                ToolTheme oldValue, ToolTheme newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setToolTheme(newValue);
            recordGlobal.writeToJavaPreferences(TOOL_THEME);
            // Update UI
//            recordGlobal.refreshToolTheme();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshToolTheme(recordGlobal));
        }
    }

    private static class LibraryOptionListener implements ChangeListener<DISPLAY_MODE> {

        @Override
        public void changed(ObservableValue<? extends DISPLAY_MODE> ov, DISPLAY_MODE oldValue, DISPLAY_MODE newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setLibraryDisplayOption(newValue);
            recordGlobal.writeToJavaPreferences(LIBRARY_DISPLAY_OPTION);
            // Update UI
//            recordGlobal.refreshLibraryDisplayOption();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshLibraryDisplayOption(recordGlobal));
        }
    }

    private static class DisplayOptionListener implements ChangeListener<DisplayOption> {

        @Override
        public void changed(ObservableValue<? extends DisplayOption> observable,
                DisplayOption oldValue, DisplayOption newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setHierarchyDisplayOption(newValue);
            recordGlobal.writeToJavaPreferences(HIERARCHY_DISPLAY_OPTION);
            // Update UI
//            recordGlobal.refreshHierarchyDisplayOption();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshHierarchyDisplayOption(recordGlobal));
        }
    }

    private static class ColumnOrderListener implements ChangeListener<CSSAnalyzerColumnsOrder> {

        @Override
        public void changed(ObservableValue<? extends CSSAnalyzerColumnsOrder> observable,
                CSSAnalyzerColumnsOrder oldValue, CSSAnalyzerColumnsOrder newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setCSSAnalyzerColumnsOrder(newValue);
            recordGlobal.writeToJavaPreferences(CSS_TABLE_COLUMNS_ORDERING_REVERSED);
            // Update UI
//            recordGlobal.refreshCSSAnalyzerColumnsOrder();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshCssTableColumnsOrderingReversed(recordGlobal));
        }
    }

    private static class ThemesListener implements ChangeListener<EditorPlatform.Theme> {
        @Override
        public void changed(ObservableValue<? extends EditorPlatform.Theme> observable, EditorPlatform.Theme oldValue, EditorPlatform.Theme newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setTheme(newValue);
            recordGlobal.writeToJavaPreferences(THEME);
            // Update UI
//            recordGlobal.refreshTheme();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshTheme(recordGlobal));
        }
    }

    private static class SwatchListener implements ChangeListener<EditorPlatform.Theme> {
        @Override
        public void changed(ObservableValue<? extends EditorPlatform.Theme> observable, EditorPlatform.Theme oldValue, EditorPlatform.Theme newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setSwatch(newValue);
            recordGlobal.writeToJavaPreferences(GLUON_SWATCH);
            // Update UI
//            recordGlobal.refreshSwatch();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshSwatch(recordGlobal));
        }
    }

    private static class GluonThemeListener implements ChangeListener<EditorPlatform.Theme> {
        @Override
        public void changed(ObservableValue<? extends EditorPlatform.Theme> observable, EditorPlatform.Theme oldValue, EditorPlatform.Theme newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setGluonTheme(newValue);
            recordGlobal.writeToJavaPreferences(GLUON_THEME);
            // Update UI
//            recordGlobal.refreshGluonTheme();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshGluonTheme(recordGlobal));
        }
    }

    private static class RecentItemsSizeListener implements ChangeListener<Integer> {

        @Override
        public void changed(ObservableValue<? extends Integer> observable,
                Integer oldValue, Integer newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setRecentItemsSize(newValue);
            recordGlobal.writeToJavaPreferences(RECENT_ITEMS_SIZE);
            recordGlobal.writeToJavaPreferences(RECENT_ITEMS);
        }
    }

    private static class AlignmentGuidesColorListener implements ChangeListener<Paint> {

        private final Rectangle graphic;

        public AlignmentGuidesColorListener(Rectangle graphic) {
            this.graphic = graphic;
        }

        @Override
        public void changed(ObservableValue<? extends Paint> ov, Paint oldValue, Paint newValue) {
            assert newValue instanceof Color;
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setAlignmentGuidesColor((Color) newValue);
            recordGlobal.writeToJavaPreferences(ALIGNMENT_GUIDES_COLOR);
            // Update UI
//            recordGlobal.refreshAlignmentGuidesColor();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshAlignmentGuidesColor(recordGlobal));
            graphic.setFill(newValue);
        }
    }

    private static class ParentRingColorListener implements ChangeListener<Paint> {

        private final Rectangle graphic;

        public ParentRingColorListener(Rectangle graphic) {
            this.graphic = graphic;
        }

        @Override
        public void changed(ObservableValue<? extends Paint> ov, Paint oldValue, Paint newValue) {
            assert newValue instanceof Color;
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setParentRingColor((Color) newValue);
            recordGlobal.writeToJavaPreferences(PARENT_RING_COLOR);
            // Update UI
//            recordGlobal.refreshParentRingColor();
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.refreshParentRingColor(recordGlobal));
            graphic.setFill(newValue);
        }
    }

    private static class PaintPickerDelegate implements PaintPicker.Delegate {

        @Override
        public void handleError(String warningKey, Object... arguments) {
            // Log a warning in message bar
        }
    }

    private static class AnimationListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setAccordionAnimation(newValue);
            recordGlobal.writeToJavaPreferences(ACCORDION_ANIMATION);
            // Update UI
            SceneBuilderApp.applyToAllDocumentWindows(dwc -> dwc.animateAccordion(newValue));
        }
    }

    private static class WildcardImportListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setWildcardImports(newValue);
            recordGlobal.writeToJavaPreferences(WILDCARD_IMPORT);
        }
    }

    private static class AlternatePasteListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            final PreferencesController preferencesController
                    = PreferencesController.getSingleton();
            final PreferencesRecordGlobal recordGlobal
                    = preferencesController.getRecordGlobal();
            // Update preferences
            recordGlobal.setAlternateTextInputControlPaste(newValue);
            recordGlobal.writeToJavaPreferences(ALTERNATE_TEXT_INPUT_PASTE);
        }
    }
}
