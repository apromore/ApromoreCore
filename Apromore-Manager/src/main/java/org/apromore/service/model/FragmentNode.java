package org.apromore.service.model;

import org.apromore.graph.canonical.Canonical;
import org.jbpt.algo.tree.tctree.TCType;

import java.util.ArrayList;
import java.util.Collection;

public class FragmentNode extends Canonical {

    private TCType type = null;
    private Collection<FragmentNode> children = new ArrayList<FragmentNode>();
    private FragmentNode parent = null;

    public TCType getType() {
        return type;
    }

    public void setType(TCType type) {
        this.type = type;
    }

    public Collection<FragmentNode> getChildren() {
        return children;
    }

    public void setChildren(Collection<FragmentNode> children) {
        this.children = children;
    }

    public FragmentNode getParent() {
        return parent;
    }

    public void setParent(FragmentNode parent) {
        this.parent = parent;
    }

}
