/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.common;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;

import java.util.Set;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 2/07/12
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderTreeModel extends DefaultTreeModel {
    /**
     *
     */
    private static final long serialVersionUID = -5513180500300189445L;

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(FolderTreeModel.class);

    DefaultTreeNode _root;

    public FolderTreeModel(FolderTreeNode contactTreeNode, FolderTreeNode currentFolder) {
        super(contactTreeNode);
        _root = contactTreeNode;
        if (currentFolder != null) {
            Set<FolderTreeNode> selectedFolder = new HashSet<FolderTreeNode>();
            selectedFolder.add(currentFolder);
            this.setSelection(selectedFolder);
        }
    }

    /**
     * remove the nodes which parent is <code>parent</code> with indexes
     * <code>indexes</code>
     *
     * @param parent
     *            The parent of nodes are removed
     * @param indexFrom
     *            the lower index of the change range
     * @param indexTo
     *            the upper index of the change range
     * @throws IndexOutOfBoundsException
     *             - indexFrom < 0 or indexTo > number of parent's children
     */
    public void remove(DefaultTreeNode parent, int indexFrom, int indexTo) throws IndexOutOfBoundsException {
        for (int i = indexTo; i >= indexFrom; i--)
            try {
                parent.getChildren().remove(i);
            } catch (Exception exp) {
                LOGGER.error("Unable to remove node " + i, exp);
            }
    }

    public void remove(DefaultTreeNode target) throws IndexOutOfBoundsException {
        int index;
        DefaultTreeNode parent;
        // find the parent and index of target
        parent = dfSearchParent(_root, target);
        for (index = 0; index < parent.getChildCount(); index++) {
            if (parent.getChildAt(index).equals(target)) {
                break;
            }
        }
        remove(parent, index, index);
    }

    /**
     * insert new nodes which parent is <code>parent</code> with indexes
     * <code>indexes</code> by new nodes <code>newNodes</code>
     *
     * @param parent
     *            The parent of nodes are inserted
     * @param indexFrom
     *            the lower index of the change range
     * @param indexTo
     *            the upper index of the change range
     * @param newNodes
     *            New nodes which are inserted
     * @throws IndexOutOfBoundsException
     *             - indexFrom < 0 or indexTo > number of parent's children
     */
    public void insert(DefaultTreeNode parent, int indexFrom, int indexTo, DefaultTreeNode[] newNodes)
            throws IndexOutOfBoundsException {
        for (int i = indexFrom; i <= indexTo; i++) {
            try {
                parent.getChildren().add(i, newNodes[i - indexFrom]);
            } catch (Exception exp) {
                throw new IndexOutOfBoundsException("Out of bound: " + i + " while size=" + parent.getChildren().size());
            }
        }
    }

    /**
     * append new nodes which parent is <code>parent</code> by new nodes
     * <code>newNodes</code>
     *
     * @param parent
     *            The parent of nodes are appended
     * @param newNodes
     *            New nodes which are appended
     */
    public void add(DefaultTreeNode parent, DefaultTreeNode[] newNodes) {
        DefaultTreeNode stn = (DefaultTreeNode) parent;

        for (DefaultTreeNode newNode : newNodes) {
            stn.getChildren().add(newNode);
        }
    }

    private DefaultTreeNode dfSearchParent(DefaultTreeNode node, DefaultTreeNode target) {
        if (node.getChildren() != null && node.getChildren().contains(target)) {
            return node;
        } else {
            int size = getChildCount(node);
            for (int i = 0; i < size; i++) {
                DefaultTreeNode parent = dfSearchParent((DefaultTreeNode) getChild(node, i), target);
                if (parent != null) {
                    return parent;
                }
            }
        }
        return null;
    }

}
