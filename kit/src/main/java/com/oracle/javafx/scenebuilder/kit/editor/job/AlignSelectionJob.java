/*
 * Copyright (c) 2026, Gluon and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 */
package com.oracle.javafx.scenebuilder.kit.editor.job;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.RelocateNodeJob;
import com.oracle.javafx.scenebuilder.kit.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.metadata.Metadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

/** Aligns or sizes a multi-selection relative to the last hit item. */
public final class AlignSelectionJob extends BatchDocumentJob {

    public enum Mode {
        ALIGN_LEFT, ALIGN_HORIZONTAL_CENTER, ALIGN_RIGHT,
        ALIGN_TOP, ALIGN_VERTICAL_CENTER, ALIGN_BOTTOM,
        DISTRIBUTE_HORIZONTAL, DISTRIBUTE_VERTICAL,
        SAME_WIDTH, SAME_HEIGHT, SAME_SIZE
    }

    private final Mode mode;

    public AlignSelectionJob(EditorController editorController, Mode mode) {
        super(editorController);
        this.mode = mode;
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();
        if (!(getEditorController().getSelection().getGroup() instanceof ObjectSelectionGroup)) {
            return result;
        }
        final ObjectSelectionGroup selection = (ObjectSelectionGroup)
                getEditorController().getSelection().getGroup();
        if (selection.getItems().size() < 2 || !selection.hasSingleParent()) {
            return result;
        }
        if (isPositionMode() && !new DesignHierarchyMask(selection.getAncestor())
                .isFreeChildPositioning()) {
            return result;
        }
        final FXOMObject referenceObject = selection.getHitItem();
        if (!(referenceObject instanceof FXOMInstance)
                || !(referenceObject.getSceneGraphObject() instanceof Node)) {
            return result;
        }
        final Node reference = (Node) referenceObject.getSceneGraphObject();
        final Bounds referenceBounds = reference.getBoundsInParent();

        if (isDistributionMode()) {
            return makeDistributionJobs(selection);
        }

        for (FXOMObject item : selection.getItems()) {
            if (item == referenceObject || !(item instanceof FXOMInstance)
                    || !(item.getSceneGraphObject() instanceof Node)) {
                continue;
            }
            final FXOMInstance instance = (FXOMInstance) item;
            final Node node = (Node) item.getSceneGraphObject();
            final Bounds bounds = node.getBoundsInParent();
            if (isPositionMode()) {
                final double x = adjustedX(node, bounds, referenceBounds);
                final double y = adjustedY(node, bounds, referenceBounds);
                final RelocateNodeJob job = new RelocateNodeJob(instance, x, y,
                        getEditorController());
                if (job.isExecutable()) {
                    result.add(job);
                }
            } else {
                addSizeJobs(result, instance, node, reference);
            }
        }
        return result;
    }

    private boolean isPositionMode() {
        return mode.ordinal() <= Mode.DISTRIBUTE_VERTICAL.ordinal();
    }

    private boolean isDistributionMode() {
        return mode == Mode.DISTRIBUTE_HORIZONTAL
                || mode == Mode.DISTRIBUTE_VERTICAL;
    }

    private double adjustedX(Node node, Bounds bounds, Bounds reference) {
        switch (mode) {
            case ALIGN_LEFT:
                return node.getLayoutX() + reference.getMinX() - bounds.getMinX();
            case ALIGN_HORIZONTAL_CENTER:
                return node.getLayoutX() + centerX(reference) - centerX(bounds);
            case ALIGN_RIGHT:
                return node.getLayoutX() + reference.getMaxX() - bounds.getMaxX();
            default:
                return node.getLayoutX();
        }
    }

    private double adjustedY(Node node, Bounds bounds, Bounds reference) {
        switch (mode) {
            case ALIGN_TOP:
                return node.getLayoutY() + reference.getMinY() - bounds.getMinY();
            case ALIGN_VERTICAL_CENTER:
                return node.getLayoutY() + centerY(reference) - centerY(bounds);
            case ALIGN_BOTTOM:
                return node.getLayoutY() + reference.getMaxY() - bounds.getMaxY();
            default:
                return node.getLayoutY();
        }
    }

    private void addSizeJobs(List<Job> jobs, FXOMInstance instance, Node node,
            Node reference) {
        final boolean width = mode == Mode.SAME_WIDTH || mode == Mode.SAME_SIZE;
        final boolean height = mode == Mode.SAME_HEIGHT || mode == Mode.SAME_SIZE;
        final double referenceWidth = geometricWidth(reference);
        final double referenceHeight = geometricHeight(reference);
        if (node instanceof Region) {
            if (width) {
                addPropertyJob(jobs, instance, "prefWidth", referenceWidth);
            }
            if (height) {
                addPropertyJob(jobs, instance, "prefHeight", referenceHeight);
            }
        } else if (node instanceof Circle) {
            final double diameter = width && height
                    ? Math.min(referenceWidth, referenceHeight)
                    : width ? referenceWidth : referenceHeight;
            addPropertyJob(jobs, instance, "radius", diameter / 2.0);
        } else if (node instanceof Ellipse) {
            if (width) {
                addPropertyJob(jobs, instance, "radiusX", referenceWidth / 2.0);
            }
            if (height) {
                addPropertyJob(jobs, instance, "radiusY", referenceHeight / 2.0);
            }
        } else if (node instanceof Rectangle) {
            if (width) {
                addPropertyJob(jobs, instance, "width", referenceWidth);
            }
            if (height) {
                addPropertyJob(jobs, instance, "height", referenceHeight);
            }
        }
    }

    private static double geometricWidth(Node node) {
        if (node instanceof Circle circle) {
            return circle.getRadius() * 2.0;
        } else if (node instanceof Ellipse ellipse) {
            return ellipse.getRadiusX() * 2.0;
        } else if (node instanceof Rectangle rectangle) {
            return rectangle.getWidth();
        } else if (node instanceof Region region) {
            return region.getWidth();
        }
        return node.getLayoutBounds().getWidth();
    }

    private static double geometricHeight(Node node) {
        if (node instanceof Circle circle) {
            return circle.getRadius() * 2.0;
        } else if (node instanceof Ellipse ellipse) {
            return ellipse.getRadiusY() * 2.0;
        } else if (node instanceof Rectangle rectangle) {
            return rectangle.getHeight();
        } else if (node instanceof Region region) {
            return region.getHeight();
        }
        return node.getLayoutBounds().getHeight();
    }

    private List<Job> makeDistributionJobs(ObjectSelectionGroup selection) {
        final List<FXOMInstance> items = new ArrayList<>();
        for (FXOMObject item : selection.getItems()) {
            if (!(item instanceof FXOMInstance)
                    || !(item.getSceneGraphObject() instanceof Node)) {
                return new ArrayList<>();
            }
            items.add((FXOMInstance) item);
        }
        if (items.size() < 3) {
            return new ArrayList<>();
        }
        final boolean horizontal = mode == Mode.DISTRIBUTE_HORIZONTAL;
        items.sort(Comparator.comparingDouble(item -> min(item, horizontal)));
        final double start = min(items.get(0), horizontal);
        final double end = max(items.get(items.size() - 1), horizontal);
        double occupied = 0.0;
        for (FXOMInstance item : items) {
            occupied += size(item, horizontal);
        }
        final double gap = (end - start - occupied) / (items.size() - 1);
        double cursor = start + size(items.get(0), horizontal) + gap;
        final List<Job> jobs = new ArrayList<>();
        for (int i = 1; i < items.size() - 1; i++) {
            final FXOMInstance item = items.get(i);
            final Node node = (Node) item.getSceneGraphObject();
            final double delta = cursor - min(item, horizontal);
            final double x = node.getLayoutX() + (horizontal ? delta : 0.0);
            final double y = node.getLayoutY() + (horizontal ? 0.0 : delta);
            jobs.add(new RelocateNodeJob(item, x, y, getEditorController()));
            cursor += size(item, horizontal) + gap;
        }
        return jobs;
    }

    private static double min(FXOMInstance item, boolean horizontal) {
        final Bounds bounds = ((Node) item.getSceneGraphObject()).getBoundsInParent();
        return horizontal ? bounds.getMinX() : bounds.getMinY();
    }

    private static double max(FXOMInstance item, boolean horizontal) {
        final Bounds bounds = ((Node) item.getSceneGraphObject()).getBoundsInParent();
        return horizontal ? bounds.getMaxX() : bounds.getMaxY();
    }

    private static double size(FXOMInstance item, boolean horizontal) {
        final Bounds bounds = ((Node) item.getSceneGraphObject()).getBoundsInParent();
        return horizontal ? bounds.getWidth() : bounds.getHeight();
    }

    private void addPropertyJob(List<Job> jobs, FXOMInstance instance,
            String name, double value) {
        final ValuePropertyMetadata metadata = Metadata.getMetadata()
                .queryValueProperty(instance, new PropertyName(name));
        if (metadata != null) {
            final ModifyObjectJob job = new ModifyObjectJob(instance, metadata, value,
                    getEditorController());
            if (job.isExecutable()) {
                jobs.add(job);
            }
        }
    }

    private static double centerX(Bounds bounds) {
        return (bounds.getMinX() + bounds.getMaxX()) / 2.0;
    }

    private static double centerY(Bounds bounds) {
        return (bounds.getMinY() + bounds.getMaxY()) / 2.0;
    }

    @Override
    protected String makeDescription() {
        return "Align selection: " + mode;
    }
}
