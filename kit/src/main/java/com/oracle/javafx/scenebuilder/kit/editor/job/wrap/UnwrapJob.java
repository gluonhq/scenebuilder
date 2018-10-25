/*
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
package com.oracle.javafx.scenebuilder.kit.editor.job.wrap;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.kit.editor.job.Job;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.ModifyFxControllerJob;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.kit.editor.job.SetDocumentRootJob;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.ToggleFxRootJob;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.AddPropertyValueJob;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.RemovePropertyJob;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.RemovePropertyValueJob;
import com.oracle.javafx.scenebuilder.kit.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.kit.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.kit.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * Main class used for the unwrap jobs.
 */
public class UnwrapJob extends BatchSelectionJob {

    private FXOMInstance oldContainer, newContainer;
    private List<FXOMObject> oldContainerChildren;

    public UnwrapJob(EditorController editorController) {
        super(editorController);
    }

    protected boolean canUnwrap() {
        final Selection selection = getEditorController().getSelection();
        if (selection.isEmpty()) {
            return false;
        }
        final AbstractSelectionGroup asg = selection.getGroup();
        if ((asg instanceof ObjectSelectionGroup) == false) {
            return false;
        }
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
        if (osg.getItems().size() != 1) {
            return false;
        }
        final FXOMObject container = osg.getItems().iterator().next();
        if (container instanceof FXOMInstance == false) {
            return false;
        }
        final FXOMInstance containerInstance = (FXOMInstance) container;

        // Unresolved custom type
        if (container.getSceneGraphObject() == null) {
            return false;
        }

        // Can unwrap ALL classes supporting wrapping
        boolean isAssignableFrom = false;
        for (Class<?> clazz : EditorController.getClassesSupportingWrapping()) {
            isAssignableFrom |= clazz.isAssignableFrom(
                    containerInstance.getDeclaredClass());
        }
        if (isAssignableFrom == false) {
            return false;
        }

        // Retrieve the children of the container to unwrap
        final List<FXOMObject> children = getChildren(containerInstance);
        int childrenCount = children.size();
        // If the container to unwrap has no childen, it cannot be unwrapped
        if (childrenCount == 0) {
            return false;
        }

        // Retrieve the parent of the container to unwrap
        final FXOMObject parentContainer = container.getParentObject();
        // Unwrap the root node
        if (parentContainer == null) {
            return childrenCount == 1;
        } else {
            // Check that the num and type of children can be added to the parent container
            final DesignHierarchyMask parentContainerMask
                    = new DesignHierarchyMask(parentContainer);
            if (parentContainerMask.isAcceptingSubComponent()) {
                return childrenCount >= 1;
            } else {
                assert parentContainerMask.isAcceptingAccessory(Accessory.CONTENT)
                        || parentContainerMask.isAcceptingAccessory(Accessory.GRAPHIC)
                        || parentContainerMask.isAcceptingAccessory(Accessory.ROOT)
                        || parentContainerMask.isAcceptingAccessory(Accessory.SCENE)
                        || parentContainerMask.getFxomObject().getSceneGraphObject() instanceof BorderPane
                        || parentContainerMask.getFxomObject().getSceneGraphObject() instanceof DialogPane;
                if (childrenCount != 1) {
                    return false;
                }

                final FXOMObject child = children.iterator().next();
                if (parentContainerMask.isAcceptingAccessory(Accessory.SCENE)) {
                    return parentContainerMask.isAcceptingAccessory(Accessory.SCENE, child);
                } else {
                    return true;
                }
            }
        }
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        if (canUnwrap()) { // (1)

            final Selection selection = getEditorController().getSelection();
            final AbstractSelectionGroup asg = selection.getGroup();
            assert asg instanceof ObjectSelectionGroup; // Because of (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
            assert osg.getItems().size() == 1; // Because of (1)

            // Retrieve the old container (container to unwrap)
            oldContainer = (FXOMInstance) osg.getItems().iterator().next();
            // Retrieve the children of the old container
            oldContainerChildren = getChildren(oldContainer);
            // Retrieve the old container property name in use
            final PropertyName oldContainerPropertyName
                    = WrapJobUtils.getContainerPropertyName(oldContainer, oldContainerChildren);
            // Retrieve the old container property (already defined and not null)
            final FXOMPropertyC oldContainerProperty
                    = (FXOMPropertyC) oldContainer.getProperties().get(oldContainerPropertyName);
            assert oldContainerProperty != null
                    && oldContainerProperty.getParentInstance() != null;

            // Retrieve the parent of the old container (aka new container)
            newContainer = (FXOMInstance) oldContainer.getParentObject();

            // Remove the old container property from the old container instance
            final Job removePropertyJob = new RemovePropertyJob(
                    oldContainerProperty,
                    getEditorController());
            result.add(removePropertyJob);

            // Remove the children from the old container property
            final List<Job> removeChildrenJobs
                    = removeChildrenJobs(oldContainerProperty, oldContainerChildren);
            result.addAll(removeChildrenJobs);

            //------------------------------------------------------------------
            // If the target object is NOT the FXOM root :
            // - we update the new container bounds and add it to the old container
            // - we update the children bounds and remove them from the old container
            //------------------------------------------------------------------
            if (newContainer != null) {

                // Retrieve the new container property name in use
                final List<FXOMObject> newContainerChildren = new ArrayList<>();
                newContainerChildren.add(oldContainer);
                final PropertyName newContainerPropertyName
                        = WrapJobUtils.getContainerPropertyName(newContainer, newContainerChildren);
                // Retrieve the new container property (already defined and not null)
                final FXOMPropertyC newContainerProperty
                        = (FXOMPropertyC) newContainer.getProperties().get(newContainerPropertyName);
                assert newContainerProperty != null
                        && newContainerProperty.getParentInstance() != null;

                // Update children bounds before adding them to the new container
                result.addAll(modifyChildrenJobs(oldContainerChildren));

                // Add the children to the new container
                int index = oldContainer.getIndexInParentProperty();
                final List<Job> addChildrenJobs
                        = addChildrenJobs(newContainerProperty, index, oldContainerChildren);
                result.addAll(addChildrenJobs);

                // Remove the old container from the new container property
                final Job removeValueJob = new RemovePropertyValueJob(
                        oldContainer,
                        getEditorController());
                result.add(removeValueJob);
            } //
            //------------------------------------------------------------------
            // If the target object is the FXOM root :
            // - we update the document root with the single child of the root node
            //------------------------------------------------------------------
            else {
                assert oldContainerChildren.size() == 1; // Because of (1)
                boolean isFxRoot = oldContainer.isFxRoot();
                final String fxController = oldContainer.getFxController();
                // First remove the fx:controller/fx:root from the old root object
                if (isFxRoot) {
                    final ToggleFxRootJob fxRootJob = new ToggleFxRootJob(getEditorController());
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final ModifyFxControllerJob fxControllerJob
                            = new ModifyFxControllerJob(oldContainer, null, getEditorController());
                    result.add(fxControllerJob);
                }
                // Then set the new container as root object            
                final FXOMObject child = oldContainerChildren.iterator().next();
                final Job setDocumentRoot = new SetDocumentRootJob(
                        child, getEditorController());
                result.add(setDocumentRoot);
                // Finally add the fx:controller/fx:root to the new root object
                if (isFxRoot) {
                    final ToggleFxRootJob fxRootJob = new ToggleFxRootJob(getEditorController());
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final ModifyFxControllerJob fxControllerJob
                            = new ModifyFxControllerJob(child, fxController, getEditorController());
                    result.add(fxControllerJob);
                }
            }
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        return "Unwrap";
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        return new ObjectSelectionGroup(oldContainerChildren, oldContainerChildren.iterator().next(), null);
    }

    protected List<Job> addChildrenJobs(
            final FXOMPropertyC containerProperty,
            final int start,
            final List<FXOMObject> children) {

        final List<Job> jobs = new ArrayList<>();
        int index = start;
        for (FXOMObject child : children) {
            assert child instanceof FXOMInstance;
            final Job addValueJob = new AddPropertyValueJob(
                    child,
                    containerProperty,
                    index++,
                    getEditorController());
            jobs.add(addValueJob);
        }
        return jobs;
    }

    protected List<Job> removeChildrenJobs(
            final FXOMPropertyC containerProperty,
            final List<FXOMObject> children) {

        final List<Job> jobs = new ArrayList<>();
        for (FXOMObject child : children) {
            assert child instanceof FXOMInstance;
            final Job removeValueJob = new RemovePropertyValueJob(
                    child,
                    getEditorController());
            jobs.add(removeValueJob);
        }
        return jobs;
    }

    /**
     * Used to modify the specified children.
     *
     * @param children The children to be modified.
     * @return A list of jobs.
     */
    protected List<Job> modifyChildrenJobs(final List<FXOMObject> children) {

        final List<Job> jobs = new ArrayList<>();
        final DesignHierarchyMask newContainerMask = new DesignHierarchyMask(newContainer);

        assert oldContainer.getSceneGraphObject() instanceof Node;
        final Node oldContainerNode = (Node) oldContainer.getSceneGraphObject();

        for (FXOMObject child : children) {
            assert child.getSceneGraphObject() instanceof Node;

            final Node childNode = (Node) child.getSceneGraphObject();
            final double currentLayoutX = childNode.getLayoutX();
            final double currentLayoutY = childNode.getLayoutY();

            // Modify child LAYOUT bounds
            if (newContainerMask.isFreeChildPositioning()) {
                final Point2D nextLayoutXY = oldContainerNode.localToParent(
                        currentLayoutX, currentLayoutY);

                final ModifyObjectJob modifyLayoutX = WrapJobUtils.modifyObjectJob(
                        (FXOMInstance) child, "layoutX", nextLayoutXY.getX(), getEditorController());
                jobs.add(modifyLayoutX);
                final ModifyObjectJob modifyLayoutY = WrapJobUtils.modifyObjectJob(
                        (FXOMInstance) child, "layoutY", nextLayoutXY.getY(), getEditorController());
                jobs.add(modifyLayoutY);
            } else {
                final ModifyObjectJob modifyLayoutX = WrapJobUtils.modifyObjectJob(
                        (FXOMInstance) child, "layoutX", 0.0, getEditorController());
                jobs.add(modifyLayoutX);
                final ModifyObjectJob modifyLayoutY = WrapJobUtils.modifyObjectJob(
                        (FXOMInstance) child, "layoutY", 0.0, getEditorController());
                jobs.add(modifyLayoutY);
            }

            // Remove static properties from child
            if (child instanceof FXOMInstance) {
                final FXOMInstance fxomInstance = (FXOMInstance) child;
                for (FXOMProperty p : fxomInstance.getProperties().values()) {
                    final Class<?> residentClass = p.getName().getResidenceClass();
                    if (residentClass != null
                            && residentClass != newContainer.getDeclaredClass()) {
                        jobs.add(new RemovePropertyJob(p, getEditorController()));
                    }
                }
            }
        }
        return jobs;
    }

    private List<FXOMObject> getChildren(final FXOMInstance container) {
        final DesignHierarchyMask mask = new DesignHierarchyMask(container);
        final List<FXOMObject> result = new ArrayList<>();
        if (mask.isAcceptingSubComponent()) {
            // TabPane => unwrap first Tab CONTENT
            if (TabPane.class.isAssignableFrom(container.getDeclaredClass())) {
                final List<FXOMObject> tabs = mask.getSubComponents();
                if (tabs.size() >= 1) {
                    final FXOMObject tab = tabs.get(0);
                    final DesignHierarchyMask tabMask = new DesignHierarchyMask(tab);
                    assert tabMask.isAcceptingAccessory(Accessory.CONTENT);
                    if (tabMask.getAccessory(Accessory.CONTENT) != null) {
                        result.add(tabMask.getAccessory(Accessory.CONTENT));
                    }
                }
            } else {
                result.addAll(mask.getSubComponents());
            }
        } else {
            // BorderPane => unwrap CENTER accessory
            if (mask.isAcceptingAccessory(Accessory.CENTER)
                    && mask.getAccessory(Accessory.CENTER) != null) {
                result.add(mask.getAccessory(Accessory.CENTER));
            } // DialogPane => unwrap DP_CONTENT accessory
            else if (mask.isAcceptingAccessory(Accessory.DP_CONTENT)
                    && mask.getAccessory(Accessory.DP_CONTENT) != null) {
                result.add(mask.getAccessory(Accessory.DP_CONTENT));
            } // ScrollPane => unwrap CONTENT accessory
            else if (mask.isAcceptingAccessory(Accessory.CONTENT)
                    && mask.getAccessory(Accessory.CONTENT) != null) {
                result.add(mask.getAccessory(Accessory.CONTENT));
            } // Scene => unwrap ROOT accessory
            else if (mask.isAcceptingAccessory(Accessory.ROOT)
                    && mask.getAccessory(Accessory.ROOT) != null) {
                result.add(mask.getAccessory(Accessory.ROOT));
            } // Window => unwrap SCENE accessory
            else if (mask.isAcceptingAccessory(Accessory.SCENE)
                    && mask.getAccessory(Accessory.SCENE) != null) {
                result.add(mask.getAccessory(Accessory.SCENE));
            }
        }
        return result;
    }
}
