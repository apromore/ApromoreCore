package org.apromore.service.model;

import java.util.ArrayList;
import java.util.Collection;

import org.apromore.graph.canonical.Canonical;
import org.jbpt.algo.tree.tctree.TCType;

public class RFragment2 extends Canonical {

    private TCType type = null;
    private Collection<RFragment2> children = new ArrayList<RFragment2>();
    private RFragment2 parent = null;

    public TCType getType() {
        return type;
    }

    public void setType(TCType type) {
        this.type = type;
    }

    public Collection<RFragment2> getChildren() {
        return children;
    }

    public void setChildren(Collection<RFragment2> children) {
        this.children = children;
    }

    public RFragment2 getParent() {
        return parent;
    }

    public void setParent(RFragment2 parent) {
        this.parent = parent;
    }

}
