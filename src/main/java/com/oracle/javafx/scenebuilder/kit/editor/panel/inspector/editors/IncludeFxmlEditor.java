package com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors;

import com.oracle.javafx.scenebuilder.app.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

/**
 *
 */
public class IncludeFxmlEditor extends InlineListEditor {

    private final StackPane root = new StackPane();
    private EditorController editorController;
    private Parent rootInitialBt;
    @FXML
    private Button includeFxmlButton;
    @FXML
    private TextField includeFxmlField;

    public IncludeFxmlEditor(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses, EditorController editorController) {
        super(propMeta, selectedClasses);
        this.editorController = editorController;
        initialize();
    }

    private void initialize() {
        rootInitialBt = EditorUtils.loadFxml("IncludeFXMLButton.fxml", this); //NOI18N
        root.getChildren().add(rootInitialBt);
        if (editorController.getIncludedFile() != null) {
            includeFxmlField.setText(editorController.getIncludedFile().getAbsolutePath());
        }
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(Object value) {

    }

    @Override
    public void requestFocus() {

    }

    @Override
    public Node getValueEditor() {
        return super.handleGenericModes(root);
    }

    private void switchToInitialButton() {
        // Replace the item list (vbox) by initial button
        root.getChildren().clear();
        root.getChildren().add(rootInitialBt);
    }

    @FXML
    void chooseFxml(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        final FileChooser.ExtensionFilter f
                = new FileChooser.ExtensionFilter(I18N.getString("file.filter.label.fxml"),
                "*.fxml"); //NOI18N

        setInitialDirectory(fileChooser);
        File fxmlFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (fxmlFile != null) {
            // See DTL-5948: on Linux we anticipate an extension less path.
            final String path = fxmlFile.getPath();
            if (!path.endsWith(".fxml")) { //NOI18N
                fxmlFile = new File(path + ".fxml"); //NOI18N
            }
            // Keep track of the user choice for next time
            EditorController.updateNextInitialDirectory(fxmlFile);
            editorController.performIncludeFxml(fxmlFile);
            includeFxmlField.setText(fxmlFile.getAbsolutePath());
        }
    }

    private void setInitialDirectory(FileChooser fileChooser) {
        if (editorController.getIncludedFile() != null) {
            File file = editorController.getIncludedFile();
            final Path chosenFolder = file.toPath().getParent();
            fileChooser.setInitialDirectory(chosenFolder.toFile());
        } else {
            fileChooser.setInitialDirectory(EditorController.getNextInitialDirectory());
        }
    }

}
