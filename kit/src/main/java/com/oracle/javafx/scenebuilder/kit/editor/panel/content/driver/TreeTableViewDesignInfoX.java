/*
 * Copyright (c) 2017 Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver;

import com.oracle.javafx.scenebuilder.kit.util.Deprecation;
import javafx.scene.control.skin.TableColumnHeader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

/**
 * A temporary class that should extend TableViewDesignInfo and adds
 * some additional verbs for managing TableView at design time.
 * This could potentially move to TabDesignInfo some day.
 *
 */
public class TreeTableViewDesignInfoX {


    public Bounds getColumnBounds(TreeTableColumn<?,?> treeTableColumn) {
        final TreeTableView<?> tv = treeTableColumn.getTreeTableView();
        final Bounds tb = tv.getLayoutBounds();
        final Bounds hb = getColumnHeaderBounds(treeTableColumn);
        
        //
        //           x0             x1          
        //
        //     +--------------------------------------+
        // y0  |     +--------------+                 |
        //     |     |    header    |                 |
        //     |     +--------------+                 |
        //     +--------------------------------------+
        //     |                                      |
        //     |          table view content          |
        //     |                                      |
        // y1  +--------------------------------------+
        //
        
        final double x0 = hb.getMinX();
        final double x1 = hb.getMaxX();
        final double y0 = hb.getMinY();
        final double y1 = tb.getMaxY();
        
        return new BoundingBox(x0, y0, x1 - x0, y1 - y0);
    }
    
    
    public Bounds getColumnHeaderBounds(TreeTableColumn<?,?> treeTableColumn) {
        final TreeTableView<?> tv = treeTableColumn.getTreeTableView();
        final Node hn = getColumnNode(treeTableColumn);
        return Deprecation.localToLocal(hn, hn.getLayoutBounds(), tv);
    }
    
    
    public Node getColumnNode(TreeTableColumn<?,?> tableColumn) {
        assert tableColumn != null;
        assert tableColumn.getTreeTableView() != null;
        

        // Looks for the sub nodes which match the .column-header CSS selector
        final TreeTableView<?> tableView = tableColumn.getTreeTableView();
        final Set<Node> set = tableView.lookupAll(".column-header"); //NOI18N
        
        // Searches the result for the node associated to 'tableColumn'.
        // This item has (TableColumn.class, tableColumn) in its property list.
        Node result = null;
        final Iterator<Node> it = set.iterator();
        while ((result == null) && it.hasNext()) {
            Node n = it.next();
            assert n instanceof TableColumnHeader;
            final TableColumnBase<?,?> tc = ((TableColumnHeader)n).getTableColumn();
            if (tc == tableColumn) {
                result = n;
            }
        }

        return result;
    }
    
    
    public <T> TreeTableColumn<T,?> lookupColumn(TreeTableView<T>  tableView, double sceneX, double sceneY) {
        TreeTableColumn<T,?> result = null;
        
        //
        //                     x
        //          +--------------------------------------+
        //          |    +----------------------------+    |
        // #1    y  |    |            header          |    |
        //          |    +----------------------------+    |
        //          |    +--------------+-------------+    |
        // #2    y  |    |    header    |    header   |    |
        //          |    +--------------+-------------+    |
        //          +--------------------------------------+
        //          |                                      |
        // #3    y  |          table view content          |
        //          |                                      |
        //          +--------------------------------------+
        //
        
        // Walk through the column to see if one contains 'x' vertical
        List<TreeTableColumn<T,?>> tableColumns = tableView.getColumns();
        List<TreeTableColumn<T,?>> columnPath = new ArrayList<>();
        while (tableColumns.isEmpty() == false) {
            final TreeTableColumn<T,?> tc = lookupColumn(tableColumns, sceneX);
            if (tc != null) {
                columnPath.add(0, tc);
                tableColumns = tc.getColumns();
            } else {
                tableColumns = Collections.emptyList(); // To stop the loop
            }
        }
        
        if (columnPath.isEmpty()) {
            // No column contains sceneX
            result = null;
        } else {
            // Check if one column in columnPath contains (sceneX, sceneY)
            // => case #1 or #2
            for (TreeTableColumn<T,?> tc : columnPath) {
                final Node headerNode = getColumnNode(tc);
                final Bounds headerBounds = headerNode.getLayoutBounds();
                final Point2D p = headerNode.sceneToLocal(sceneX, sceneY, true /* rootScene */);
                if (headerBounds.contains(p)) {
                    result = tc;
                    break;
                }
            }
            
            if (result == null) {
                // No column in columnPath contains sceneX => case #3
                result = columnPath.get(0);
            }
        }
        
        return result;
    }
    
    
    private <T> TreeTableColumn<T,?> lookupColumn(
            List<TreeTableColumn<T,?>> tableColumns, double sceneX) {
        TreeTableColumn<T,?> result = null;
        
        // Walk through the columns to see if one contains 'x' vertical
        for (TreeTableColumn<T,?> tc : tableColumns) {
            final Node headerNode = getColumnNode(tc);
            if (headerNode != null) {
                final Bounds headerBounds = headerNode.getLayoutBounds();
                final Point2D p = headerNode.sceneToLocal(sceneX, 0, true /* rootScene */);
                if ((headerBounds.getMinX() <= p.getX()) 
                        && (p.getX() < headerBounds.getMaxX())) {
                    result = tc;
                    break;
                }
            }
        }
        
        return result;
    }
}
