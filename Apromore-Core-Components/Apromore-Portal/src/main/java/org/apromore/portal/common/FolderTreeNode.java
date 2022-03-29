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

import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.SummaryType;
import org.zkoss.zul.DefaultTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 2/07/12
 * Time: 6:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderTreeNode extends DefaultTreeNode {

    private boolean open = false;
    private FolderTreeNodeTypes type = FolderTreeNodeTypes.Folder;

    public FolderTreeNode(FolderType data) {
        super(data);
    }

    public FolderTreeNode(FolderType data, DefaultTreeNode[] children) {
        super(data, children);
    }

    public FolderTreeNode(FolderType data, DefaultTreeNode[] children, boolean open) {
        super(data, children);
        setOpen(open);
    }

    public FolderTreeNode(FolderType data, DefaultTreeNode[] children, boolean open, FolderTreeNodeTypes type) {
        super(data, children);
        setOpen(open);
        setType(type);
    }

    public FolderTreeNode(SummaryType data) {
        super(data);
    }

    public FolderTreeNode(SummaryType data, DefaultTreeNode[] children) {
        super(data, children);
    }

    public FolderTreeNode(SummaryType data, DefaultTreeNode[] children, boolean open) {
        super(data, children);
        setOpen(open);
    }

    public FolderTreeNode(SummaryType data, DefaultTreeNode[] children, boolean open, FolderTreeNodeTypes type) {
        super(data, children);
        setOpen(open);
        setType(type);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public FolderTreeNodeTypes getType() {
        return type;
    }

    public void setType(FolderTreeNodeTypes newType) {
        this.type = newType;
    }

}
