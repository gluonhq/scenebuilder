package com.oracle.javafx.scenebuilder.kit.fxom;

import javafx.scene.control.Button;

/**
 * Needed for the
 * {@link FXOMSaverUpdateImportInstructionsTest#testCustomButton()}
 *
 */
public class TestCustomButton extends Button {

    public TestCustomButton() {
        this.setText("CustomButton");
        this.setOnAction((ActionEvent) -> {
            System.out.println("Custom Button clicked!");
        });
    }
}