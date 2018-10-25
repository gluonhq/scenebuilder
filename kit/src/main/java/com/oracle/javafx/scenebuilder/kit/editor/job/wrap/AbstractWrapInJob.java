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
import com.oracle.javafx.scenebuilder.kit.editor.job.JobUtils;
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
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.kit.metadata.Metadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.control.Accordion;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * Main class used for the wrap jobs.
 */
public abstract class AbstractWrapInJob extends BatchSelectionJob {

    protected Class<?> newContainerClass;
    protected FXOMInstance oldContainer, newContainer;

    public AbstractWrapInJob(EditorController editorController) {
        super(editorController);
    }

    public static AbstractWrapInJob getWrapInJob(
            EditorController editorController,
            Class<?> wrappingClass) {

        assert EditorController.getClassesSupportingWrapping().contains(wrappingClass);
        final AbstractWrapInJob job;
        if (wrappingClass == javafx.scene.layout.AnchorPane.class) {
            job = new WrapInAnchorPaneJob(editorController);
        } else if (wrappingClass == javafx.scene.layout.BorderPane.class) {
            job = new WrapInBorderPaneJob(editorController);
        } else if (wrappingClass == javafx.scene.control.ButtonBar.class) {
            job = new WrapInButtonBarJob(editorController);
        } else if (wrappingClass == javafx.scene.control.DialogPane.class) {
            job = new WrapInDialogPaneJob(editorController);
        } else if (wrappingClass == javafx.scene.layout.FlowPane.class) {
            job = new WrapInFlowPaneJob(editorController);
        } else if (wrappingClass == javafx.scene.layout.GridPane.class) {
            job = new WrapInGridPaneJob(editorController);
        } else if (wrappingClass == javafx.scene.Group.class) {
            job = new WrapInGroupJob(editorController);
        } else if (wrappingClass == javafx.scene.layout.HBox.class) {
            job = new WrapInHBoxJob(editorController);
        } else if (wrappingClass == javafx.scene.layout.Pane.class) {
            job = new WrapInPaneJob(editorController);
        } else if (wrappingClass == javafx.scene.control.ScrollPane.class) {
            job = new WrapInScrollPaneJob(editorController);
        } else if (wrappingClass == javafx.scene.control.SplitPane.class) {
            job = new WrapInSplitPaneJob(editorController);
        } else if (wrappingClass == javafx.scene.layout.StackPane.class) {
            job = new WrapInStackPaneJob(editorController);
        } else if (wrappingClass == javafx.scene.control.TabPane.class) {
            job = new WrapInTabPaneJob(editorController);
        } else if (wrappingClass == javafx.scene.text.TextFlow.class) {
            job = new WrapInTextFlowJob(editorController);
        } else if (wrappingClass == javafx.scene.layout.TilePane.class) {
            job = new WrapInTilePaneJob(editorController);
        } else if (wrappingClass == javafx.scene.control.TitledPane.class) {
            job = new WrapInTitledPaneJob(editorController);
        } else if (wrappingClass == javafx.scene.control.ToolBar.class) {
            job = new WrapInToolBarJob(editorController);
        } else if (wrappingClass == javafx.scene.Scene.class) {
            job = new WrapInSceneJob(editorController);
        } else if (wrappingClass == javafx.stage.Stage.class) {
            job = new WrapInStageJob(editorController);
        } else {
            assert wrappingClass == javafx.scene.layout.VBox.class; // Because of (1)
            job = new WrapInVBoxJob(editorController);
        }
        return job;
    }

    protected boolean canWrapIn() {
        final Selection selection = getEditorController().getSelection();
        if (selection.isEmpty()) {
            return false;
        }
        final AbstractSelectionGroup asg = selection.getGroup();
        if ((asg instanceof ObjectSelectionGroup) == false) {
            return false;
        }
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
        if (osg.hasSingleParent() == false) {
            return false;
        }
        if (getEditorController().isSelectionNode() == false) {
            return false;
        }
        // Cannot wrap in Axis nodes
        for (FXOMObject fxomObject : osg.getItems()) {
            if (fxomObject.getSceneGraphObject() instanceof Axis) {
                return false;
            }
        }
        final FXOMObject parent = osg.getAncestor();
        if (parent == null) { // selection == root object
            return true;
        }
        final Object parentSceneGraphObject = parent.getSceneGraphObject();
        if (parentSceneGraphObject instanceof BorderPane
                || parentSceneGraphObject instanceof DialogPane) {
            return osg.getItems().size() == 1;
        }
        return !(parentSceneGraphObject instanceof Accordion) // accepts only TitledPanes
                && !(parentSceneGraphObject instanceof TabPane); // accepts only Tabs
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        if (canWrapIn()) { // (1)

            final Selection selection = getEditorController().getSelection();
            final AbstractSelectionGroup asg = selection.getGroup();
            assert asg instanceof ObjectSelectionGroup; // Because of (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;

            // Retrieve the old container
            oldContainer = (FXOMInstance) osg.getAncestor();

            // Retrieve the children to be wrapped
            final List<FXOMObject> children = osg.getSortedItems();

            // Create the new container
            newContainer = makeNewContainerInstance();
            // Update the new container
            modifyNewContainer(children);

            //==================================================================
            // STEP #1
            //==================================================================
            // If the target object is NOT the FXOM root :
            // - we add the new container to the old container
            // - we remove the children from the old container
            //------------------------------------------------------------------
            if (oldContainer != null) {

                // Retrieve the old container property name in use
                final PropertyName oldContainerPropertyName
                        = WrapJobUtils.getContainerPropertyName(oldContainer, children);
                // Retrieve the old container property (already defined and not null)
                final FXOMPropertyC oldContainerProperty
                        = (FXOMPropertyC) oldContainer.getProperties().get(oldContainerPropertyName);
                assert oldContainerProperty != null
                        && oldContainerProperty.getParentInstance() != null;

                // Add the new container to the old container
                final int newContainerIndex = getIndex(oldContainer, children);
                final Job newContainerAddValueJob = new AddPropertyValueJob(
                        newContainer,
                        oldContainerProperty,
                        newContainerIndex, getEditorController());
                result.add(newContainerAddValueJob);

                // Remove children from the old container
                final List<Job> removeChildrenJobs = removeChildrenJobs(oldContainerProperty, children);
                result.addAll(removeChildrenJobs);
            } //
            //------------------------------------------------------------------
            // If the target object is the FXOM root :
            // - we update the document root with the new container
            //------------------------------------------------------------------
            else {
                assert children.size() == 1; // Wrap the single root node
                final FXOMObject rootObject = children.iterator().next();
                assert rootObject instanceof FXOMInstance;
                boolean isFxRoot = ((FXOMInstance) rootObject).isFxRoot();
                final String fxController = rootObject.getFxController();
                // First remove the fx:controller/fx:root from the old root object
                if (isFxRoot) {
                    final ToggleFxRootJob fxRootJob = new ToggleFxRootJob(getEditorController());
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final ModifyFxControllerJob fxControllerJob
                            = new ModifyFxControllerJob(rootObject, null, getEditorController());
                    result.add(fxControllerJob);
                }
                // Then set the new container as root object
                final Job setDocumentRoot = new SetDocumentRootJob(
                        newContainer, getEditorController());
                result.add(setDocumentRoot);
                // Finally add the fx:controller/fx:root to the new root object
                if (isFxRoot) {
                    final ToggleFxRootJob fxRootJob = new ToggleFxRootJob(getEditorController());
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final ModifyFxControllerJob fxControllerJob
                            = new ModifyFxControllerJob(newContainer, fxController, getEditorController());
                    result.add(fxControllerJob);
                }
            }

            //==================================================================
            // STEP #2
            //==================================================================
            // This step depends on the new container property 
            // (either either the SUB COMPONENT or the CONTENT property)
            //------------------------------------------------------------------
            result.addAll(wrapChildrenJobs(children));
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return "Wrap in " + newContainerClass.getSimpleName();
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        List<FXOMObject> newObjects = new ArrayList<>();
        newObjects.add(newContainer);
        return new ObjectSelectionGroup(newObjects, newObjects.iterator().next(), null);
    }

    /**
     * Used to wrap the specified children in the new container. May use either
     * the SUB COMPONENT or the CONTENT property.
     *
     * @param children The children to be wrapped.
     * @return A list of jobs.
     */
    protected abstract List<Job> wrapChildrenJobs(final List<FXOMObject> children);

    protected List<Job> addChildrenJobs(
            final FXOMPropertyC containerProperty,
            final Collection<FXOMObject> children) {

        final List<Job> jobs = new ArrayList<>();
        int index = 0;
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
        final Bounds unionOfBounds = WrapJobUtils.getUnionOfBounds(children);

        for (FXOMObject child : children) {

            // Modify child LAYOUT bounds
            if (newContainerMask.isFreeChildPositioning()) {
                assert child.getSceneGraphObject() instanceof Node;
                final Node childNode = (Node) child.getSceneGraphObject();
                final Bounds childBounds = childNode.getLayoutBounds();

                final Point2D point = childNode.localToParent(
                        childBounds.getMinX(), childBounds.getMinY());
                double layoutX = point.getX() - unionOfBounds.getMinX();
                double layoutY = point.getY() - unionOfBounds.getMinY();
                final ModifyObjectJob modifyLayoutX = WrapJobUtils.modifyObjectJob(
                        (FXOMInstance) child, "layoutX", layoutX, getEditorController());
                jobs.add(modifyLayoutX);
                final ModifyObjectJob modifyLayoutY = WrapJobUtils.modifyObjectJob(
                        (FXOMInstance) child, "layoutY", layoutY, getEditorController());
                jobs.add(modifyLayoutY);
            } else {
                assert child.getSceneGraphObject() instanceof Node;

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
                    if (residentClass != null) {
                        jobs.add(new RemovePropertyJob(p, getEditorController()));
                    }
                }
            }
        }

        return jobs;
    }

    /**
     * Used to modify the new container.
     *
     * Note that unlike the modifyChildrenJobs method, we do not use any job
     * here but directly set the properties.
     *
     * @param children The children.
     */
    protected void modifyNewContainer(final List<FXOMObject> children) {
        if (oldContainer != null) {
            final DesignHierarchyMask oldContainerMask = new DesignHierarchyMask(oldContainer);
            if (oldContainerMask.isFreeChildPositioning()) {
                final Bounds unionOfBounds = WrapJobUtils.getUnionOfBounds(children);
                JobUtils.setLayoutX(newContainer, Node.class, unionOfBounds.getMinX());
                JobUtils.setLayoutY(newContainer, Node.class, unionOfBounds.getMinY());
//            JobUtils.setMinHeight(newContainer, Region.class, unionOfBounds.getHeight());
//            JobUtils.setMinWidth(newContainer, Region.class, unionOfBounds.getMinY());
            }
        }

        // Add static properties to the new container
        // (meaningfull for single selection only)
        if (children.size() == 1) {
            final Metadata metadata = Metadata.getMetadata();
            final FXOMObject child = children.get(0);
            if (child instanceof FXOMInstance) {
                final FXOMInstance fxomInstance = (FXOMInstance) child;
                for (FXOMProperty p : fxomInstance.getProperties().values()) {
                    final Class<?> residentClass = p.getName().getResidenceClass();
                    if (residentClass != null) {
                        final ValuePropertyMetadata vpm = metadata.queryValueProperty(fxomInstance, p.getName());
                        final Object value = vpm.getValueObject(fxomInstance);
                        vpm.setValueObject(newContainer, value);
                    }
                }
            }
        }
    }

    protected FXOMInstance makeNewContainerInstance(final Class<?> containerClass) {
        // Create new container instance
        final FXOMDocument newDocument = new FXOMDocument();
        final FXOMInstance result = new FXOMInstance(newDocument, containerClass);
        newDocument.setFxomRoot(result);
        result.moveToFxomDocument(getEditorController().getFxomDocument());

        return result;
    }

    private FXOMInstance makeNewContainerInstance() {
        return AbstractWrapInJob.this.makeNewContainerInstance(newContainerClass);
    }

    /**
     * Returns the index to be used in order to add the new container to the old
     * container.
     *
     * @param container
     * @param fxomObjects
     * @return
     */
    private int getIndex(final FXOMInstance container, final List<FXOMObject> fxomObjects) {
        final DesignHierarchyMask mask = new DesignHierarchyMask(container);
        if (mask.isAcceptingSubComponent() == false) {
            return -1;
        }
        // Use the smaller index of the specified FXOM objects
        final Iterator<FXOMObject> iterator = fxomObjects.iterator();
        assert iterator.hasNext();
        int result = iterator.next().getIndexInParentProperty();
        while (iterator.hasNext()) {
            int index = iterator.next().getIndexInParentProperty();
            if (index < result) {
                result = index;
            }
        }
        return result;
    }
}
