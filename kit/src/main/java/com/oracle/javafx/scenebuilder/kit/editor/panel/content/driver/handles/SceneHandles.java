package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles;

import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;

public class SceneHandles extends AbstractGenericHandles<Scene> {
    private Node sceneGraphObject;

    public SceneHandles(ContentPanelController contentPanelController,
                        FXOMInstance fxomInstance) {
        super(contentPanelController, fxomInstance, Scene.class);

        final DesignHierarchyMask designHierarchyMask = new DesignHierarchyMask(getFxomObject());
        final FXOMObject root = designHierarchyMask.getAccessory(DesignHierarchyMask.Accessory.ROOT);
        assert root != null;
        assert root instanceof FXOMInstance;
        assert root.getSceneGraphObject() instanceof Node;
        sceneGraphObject = (Node) root.getSceneGraphObject();
    }

    @Override
    public Bounds getSceneGraphObjectBounds() {
        return sceneGraphObject.getLayoutBounds();
    }

    @Override
    public Node getSceneGraphObjectProxy() {
        return sceneGraphObject;
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        startListeningToLayoutBounds(sceneGraphObject);
        startListeningToLocalToSceneTransform(sceneGraphObject);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        stopListeningToLayoutBounds(sceneGraphObject);
        stopListeningToLocalToSceneTransform(sceneGraphObject);
    }

}
