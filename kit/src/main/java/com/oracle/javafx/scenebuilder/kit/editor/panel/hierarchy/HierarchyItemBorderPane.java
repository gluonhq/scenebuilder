/*
 * Copyright (c) 2024, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy;

import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.images.ImageUtils;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask.Accessory;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import javafx.scene.image.Image;

/**
 * Object representing the data contained within the hierarchy TreeItems for the
 * BorderPane top/right/bottom/left/center properties.
 *
 * @treatAsPrivate
 */
public class HierarchyItemBorderPane extends HierarchyItem {

    private final Accessory position;
    // The accessory owner. Used for the equals method.
    private final DesignHierarchyMask owner;

    /**
     * Creates a hierarchy item.
     *
     * @param owner The accessory owner
     * @param fxomObject The FX object represented by this item
     * @param position The position of the FX object within the BorderPane
     */
    public HierarchyItemBorderPane(
            final DesignHierarchyMask owner,
            final FXOMObject fxomObject,
            final Accessory position) {
        assert owner != null;
        this.owner = owner;
        // fxomObject can be null for place holder items
        this.mask = fxomObject == null ? null : new DesignHierarchyMask(fxomObject);
        this.position = position;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HierarchyItemBorderPane item = (HierarchyItemBorderPane) obj;
        if (!isEmpty()) {
            // If the place holder is not empty, we compare the fxom object
            assert getFxomObject() != null;
            return getFxomObject().equals(item.getFxomObject());
        } else {
            // If the place holder is empty, we compare the position + owner
            return getOwner().equals(item.getOwner())
                    && getPosition().equals(item.getPosition());
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.mask);
        hash = 37 * hash + Objects.hashCode(this.owner);
        hash = 37 * hash + Objects.hashCode(this.position);
        return hash;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return mask == null;
    }

    /**
     * Returns the DesignHierarchyMask owner of this accessory. Cannot be null.
     *
     * @return the DesignHierarchyMask owner
     */
    public DesignHierarchyMask getOwner() {
        return owner;
    }

    /**
     * Returns the BorderPane position represented by this item.
     *
     * @return the BorderPane position represented by this item.
     */
    public Accessory getPosition() {
        return this.position;
    }

    @Override
    public Image getPlaceholderImage() {
        return ImageUtils.getNodeIcon("BorderPane-" + position.name().toLowerCase(Locale.ROOT) + ".png"); //NOI18N
    }

    @Override
    public String getPlaceholderInfo() {
        return (mask != null ? null : I18N.getString("hierarchy.placeholder.insert", position.name().toUpperCase(Locale.ROOT)));
    }

    @Override
    public Image getClassNameIcon() {
        return (mask == null ? null : mask.getClassNameIcon());
    }

    @Override
    public URL getClassNameIconURL() {
        return (mask == null ? null : mask.getClassNameIconURL());
    }

    @Override
    public String getClassNameInfo() {
        return (mask == null ? null : mask.getClassNameInfo());
    }
}
