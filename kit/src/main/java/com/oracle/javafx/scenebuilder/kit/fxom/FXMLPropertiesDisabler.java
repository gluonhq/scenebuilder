package com.oracle.javafx.scenebuilder.kit.fxom;

class FXMLPropertiesDisabler {

    /**
     * 
     * On MacOS, when loading a FXML with a menu bar where useSystemMenuBarProperty()
     * is enabled, the menu in the FXML will hide the menu of SceneBuilder. 
     * In this case, SceneBuilder becomes unusable.
     * 
     * Setting the property here to false has the advantage, that the FXML to be saved
     * will still contain the defined property BUT the SceneBuilder menu bar will remain 
     * visible.
     * 
     * The modification of properties which are not desired to be active while
     * editing must happen before loading the FXML using the FXMLLoader.
     * 
     * Here a disconnect between the FXOM and FXML is created as the state of the
     * useSystemMenuBarProperty is now different in both models.
     * 
     * @param fxmlText FXML source to be modified
     * @return FXML source with all properties disabled (=false) where WYSIWYG editing is not suitable.
     * 
     */
    public String disableNonWysiwygProperties(String fxmlText) {
        return fxmlText.replace("useSystemMenuBar=\"true\"",
                                "useSystemMenuBar=\"false\"");
    }

}
