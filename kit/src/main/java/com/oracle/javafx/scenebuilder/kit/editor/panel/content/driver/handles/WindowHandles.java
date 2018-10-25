package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles;

import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

public class WindowHandles extends AbstractGenericHandles<Window> {
    private final Pane dummyPane = new Pane();
    private Node sceneGraphObject;

    public WindowHandles(ContentPanelController contentPanelController,
                         FXOMInstance fxomInstance) {
        super(contentPanelController, fxomInstance, Window.class);

        final DesignHierarchyMask designHierarchyMask = new DesignHierarchyMask(fxomInstance);
        FXOMObject scene = designHierarchyMask.getAccessory(DesignHierarchyMask.Accessory.SCENE);
        if (scene == null) {
            sceneGraphObject = null;
        } else {
            DesignHierarchyMask sceneDesignHierarchyMask = new DesignHierarchyMask(scene);
            FXOMObject root = sceneDesignHierarchyMask.getAccessory(DesignHierarchyMask.Accessory.ROOT);
            assert root != null;
            assert root instanceof FXOMInstance;
            assert root.getSceneGraphObject() instanceof Node;
            sceneGraphObject = (Node) root.getSceneGraphObject();
        }
    }

    @Override
    public Bounds getSceneGraphObjectBounds() {
        if (sceneGraphObject != null) {
            return sceneGraphObject.getLayoutBounds();
        } else {
            return new BoundingBox(0, 0, 0, 0);
        }
    }

    @Override
    public Node getSceneGraphObjectProxy() {
        return sceneGraphObject != null ? sceneGraphObject : dummyPane;
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        if (sceneGraphObject == null) {
            return;
        }
        startListeningToLayoutBounds(sceneGraphObject);
        startListeningToLocalToSceneTransform(sceneGraphObject);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        if (sceneGraphObject == null) {
            return;
        }
        stopListeningToLayoutBounds(sceneGraphObject);
        stopListeningToLocalToSceneTransform(sceneGraphObject);
    }

}
