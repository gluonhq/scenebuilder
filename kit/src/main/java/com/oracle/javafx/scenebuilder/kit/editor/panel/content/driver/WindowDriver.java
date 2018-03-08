package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver;

import com.oracle.javafx.scenebuilder.kit.editor.drag.target.AbstractDropTarget;
import com.oracle.javafx.scenebuilder.kit.editor.drag.target.AccessoryDropTarget;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.curve.AbstractCurveEditor;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles.AbstractHandles;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles.WindowHandles;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.pring.AbstractPring;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.pring.NodePring;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.AbstractResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.tring.AbstractTring;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

public class WindowDriver extends AbstractDriver {

    public WindowDriver(ContentPanelController contentPanelController) {
        super(contentPanelController);
    }

    @Override
    public AbstractHandles<?> makeHandles(FXOMObject fxomObject) {
        assert fxomObject.getSceneGraphObject() instanceof Window;
        assert fxomObject instanceof FXOMInstance;
        return new WindowHandles(contentPanelController, (FXOMInstance) fxomObject);
    }

    @Override
    public AbstractTring<?> makeTring(AbstractDropTarget dropTarget) {
        return null;
    }

    @Override
    public AbstractPring<?> makePring(FXOMObject fxomObject) {
        assert fxomObject.getSceneGraphObject() instanceof Window;
        DesignHierarchyMask windowDesignHierarchyMask = new DesignHierarchyMask(fxomObject);
        FXOMObject scene = windowDesignHierarchyMask.getAccessory(DesignHierarchyMask.Accessory.SCENE);
        assert scene != null : "makePring should have only been called if the Window has a scene";
        assert scene.getSceneGraphObject() instanceof Scene;
        assert scene instanceof FXOMInstance;
        DesignHierarchyMask sceneDesignHierarchyMask = new DesignHierarchyMask(scene);
        FXOMObject root = sceneDesignHierarchyMask.getAccessory(DesignHierarchyMask.Accessory.ROOT);
        assert root != null;
        assert root.getSceneGraphObject() instanceof Node;
        assert root instanceof FXOMInstance;
        return new NodePring(contentPanelController, (FXOMInstance) root);
    }

    @Override
    public AbstractResizer<?> makeResizer(FXOMObject fxomObject) {
        // Resize gesture does not apply to Windows
        return null;
    }

    @Override
    public AbstractCurveEditor<?> makeCurveEditor(FXOMObject fxomObject) {
        return null;
    }

    @Override
    public FXOMObject refinePick(Node hitNode, double sceneX, double sceneY, FXOMObject fxomObject) {
        return fxomObject;
    }

    @Override
    public AbstractDropTarget makeDropTarget(FXOMObject fxomObject, double sceneX, double sceneY) {
        assert fxomObject instanceof FXOMInstance;
        return new AccessoryDropTarget((FXOMInstance) fxomObject, DesignHierarchyMask.Accessory.SCENE);
    }

    @Override
    public Node getInlineEditorBounds(FXOMObject fxomObject) {
        return null;
    }

    @Override
    public boolean intersectsBounds(FXOMObject fxomObject, Bounds bounds) {
        assert fxomObject.getSceneGraphObject() instanceof Window;
        DesignHierarchyMask windowDesignHierarchyMask = new DesignHierarchyMask(fxomObject);
        FXOMObject scene = windowDesignHierarchyMask.getAccessory(DesignHierarchyMask.Accessory.SCENE);
        if (scene == null) {
            return false;
        }
        assert scene.getSceneGraphObject() instanceof Scene;
        assert scene instanceof FXOMInstance;
        DesignHierarchyMask sceneDesignHierarchyMask = new DesignHierarchyMask(scene);
        FXOMObject root = sceneDesignHierarchyMask.getAccessory(DesignHierarchyMask.Accessory.ROOT);
        assert root != null;
        assert root.getSceneGraphObject() instanceof Node;
        Node rootNode = (Node) root.getSceneGraphObject();
        Bounds rootNodeBounds = rootNode.localToScene(rootNode.getLayoutBounds(), true /* rootScene */);
        return rootNodeBounds.intersects(bounds);
    }
}
