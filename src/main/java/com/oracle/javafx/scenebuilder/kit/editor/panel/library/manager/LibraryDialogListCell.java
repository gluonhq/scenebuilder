package com.oracle.javafx.scenebuilder.kit.editor.panel.library.manager;

import com.oracle.javafx.scenebuilder.kit.editor.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.images.ImageUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Model for the list cell in the JAR/FXML Library dialog.
 */
public class LibraryDialogListCell extends ListCell<DialogListItem> {

    private DialogListItem dialogListItem;

    public LibraryDialogListCell() {
        super();
    }

    @Override
    public void updateItem(DialogListItem item, boolean empty) {
        this.dialogListItem = item;

        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setPrefWidth(0);
            setGraphic(createCellContent());
        }
    }

    private HBox createCellContent() {
        HBox cellContent = new HBox();
        cellContent.setAlignment(Pos.CENTER_LEFT);
        String name;
        if (dialogListItem instanceof LibraryDialogListItem) {
            name = ((LibraryDialogListItem) dialogListItem).getFilePath().getFileName().toString();
        } else {
            name = ((ArtifactDialogListItem) dialogListItem).getCoordinates();
        }
        Label fileName = new Label(name);
        HBox.setHgrow(fileName, Priority.ALWAYS);
        HBox buttonContent = createButtonCellContent();
        HBox.setHgrow(buttonContent, Priority.ALWAYS);
        cellContent.getChildren().addAll(fileName, buttonContent);
        return cellContent;
    }

    private HBox createButtonCellContent() {
        HBox buttonContent = new HBox();
        buttonContent.setAlignment(Pos.CENTER_RIGHT);
        buttonContent.setSpacing(5);
        Button editButton = new Button("", new ImageView(ImageUtils.getEditIconImage()));
        editButton.getStyleClass().add("image-view-button");
        editButton.setOnMouseClicked(event -> dialogListItem.getLibraryDialogController().processJarFXMLEdit(dialogListItem));
        editButton.setTooltip(new Tooltip(I18N.getString("library.dialog.button.edit.tooltip")));
        Button deleteButton = new Button("", new ImageView(ImageUtils.getDeleteIconImage()));
        deleteButton.setOnMouseClicked(event -> dialogListItem.getLibraryDialogController().processJarFXMLDelete(dialogListItem));
        deleteButton.getStyleClass().add("image-view-button");
        deleteButton.setTooltip(new Tooltip(I18N.getString("library.dialog.button.delete.tooltip")));
        buttonContent.getChildren().addAll(editButton, deleteButton);
        return buttonContent;
    }
}