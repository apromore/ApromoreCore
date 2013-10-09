package org.apromore.dao.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 20/06/12
 * Time: 10:29 PM
 * To change this template use File | Settings | File Templates.
 */
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
