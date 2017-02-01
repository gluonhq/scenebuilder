package com.oracle.javafx.scenebuilder.app.skeleton;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.skeleton.SkeletonBuffer;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link SkeletonBuffer#toString()}.
 */
public class SkeletonBufferTest {

    @Test
    public void testControllerWithoutPackageName() throws IOException {
        EditorController editorController = new EditorController();
        final URL fxmlURL = SkeletonBufferTest.class.getResource("ControllerWithoutPackage.fxml");
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);

        SkeletonBuffer skeletonBuffer = new SkeletonBuffer(editorController.getFxomDocument());
        String skeleton = skeletonBuffer.toString();

        String firstLine = skeleton.substring(0, skeleton.indexOf("\n"));
        assertEquals("", firstLine);
    }

    @Test
    public void testControllerWithSimplePackageName() throws IOException {
        EditorController editorController = new EditorController();
        final URL fxmlURL = SkeletonBufferTest.class.getResource("ControllerWithSimplePackage.fxml");
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);

        SkeletonBuffer skeletonBuffer = new SkeletonBuffer(editorController.getFxomDocument());
        String skeleton = skeletonBuffer.toString();

        String firstLine = skeleton.substring(0, skeleton.indexOf("\n"));
        assertEquals("package com;", firstLine);
    }

    @Test
    public void testControllerWithAdvancedPackageName() throws IOException {
        EditorController editorController = new EditorController();
        final URL fxmlURL = SkeletonBufferTest.class.getResource("ControllerWithAdvancedPackage.fxml");
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);

        SkeletonBuffer skeletonBuffer = new SkeletonBuffer(editorController.getFxomDocument());
        String skeleton = skeletonBuffer.toString();

        String firstLine = skeleton.substring(0, skeleton.indexOf("\n"));
        assertEquals("package com.example.app.view;", firstLine);
    }
}
