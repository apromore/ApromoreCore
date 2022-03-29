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

package org.apromore.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import org.apromore.dao.model.User;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 20/06/12
 * Time: 10:29 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties({ "depth", "parent", "hasRead", "hasWrite", "hasOwnership", "user" })
public class FolderTreeNode {

    private Integer id;
    private int depth;
    private FolderTreeNode parent;
    private String name;
    private User user;
    private List<FolderTreeNode> subFolders = new ArrayList<>();
    private boolean hasRead;
    private boolean hasWrite;
    private boolean hasOwnership;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer newId) {
        this.id = newId;
    }

    public FolderTreeNode getParent() {
        return parent;
    }

    public void setParent(final FolderTreeNode newParentId) {
        this.parent = newParentId;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(final int newDepth) {
        this.depth = newDepth;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User newUser) {
        this.user = newUser;
    }

    public String getName() {
        return name;
    }

    public void setName(final String newName) {
        this.name = newName;
    }

    public List<FolderTreeNode> getSubFolders() {
        return subFolders;
    }

    public void setSubFolders(final List<FolderTreeNode> newSubFolders) {
        this.subFolders = newSubFolders;
    }

    public boolean getHasRead() {
        return hasRead;
    }

    public void setHasRead(final boolean newHasRead) {
        this.hasRead = newHasRead;
    }

    public boolean getHasWrite() {
        return hasWrite;
    }

    public void setHasWrite(final boolean newHasWrite) {
        this.hasWrite = newHasWrite;
    }

    public boolean getHasOwnership() {
        return hasOwnership;
    }

    public void setHasOwnership(final boolean newHasOwnership) {
        this.hasOwnership = newHasOwnership;
    }

}
