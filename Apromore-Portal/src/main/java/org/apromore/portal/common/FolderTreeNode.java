package org.apromore.portal.common;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
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

    public FolderTreeNode(ProcessSummaryType data) {
        super(data);
    }

    public FolderTreeNode(ProcessSummaryType data, DefaultTreeNode[] children) {
        super(data, children);
    }

    public FolderTreeNode(ProcessSummaryType data, DefaultTreeNode[] children, boolean open) {
        super(data, children);
        setOpen(open);
    }

    public FolderTreeNode(ProcessSummaryType data, DefaultTreeNode[] children, boolean open, FolderTreeNodeTypes type) {
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
