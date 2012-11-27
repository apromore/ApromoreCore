package org.apromore.service.model;

import org.apromore.graph.canonical.Canonical;
import org.jbpt.algo.tree.tctree.TCType;

import java.util.ArrayList;
import java.util.Collection;

public class fragmentNode extends Canonical {

    private TCType type = null;
    private Collection<fragmentNode> children = new ArrayList<fragmentNode>();
    private fragmentNode parent = null;

    public TCType getType() {
        return type;
    }

    public void setType(TCType type) {
        this.type = type;
    }

    public Collection<fragmentNode> getChildren() {
        return children;
    }

    public void setChildren(Collection<fragmentNode> children) {
        this.children = children;
    }

    public fragmentNode getParent() {
        return parent;
    }

    public void setParent(fragmentNode parent) {
        this.parent = parent;
    }

}
