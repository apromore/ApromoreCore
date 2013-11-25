package org.apromore.service.model;

import org.apromore.graph.canonical.Canonical;
import org.apromore.util.IDGenerator;
import org.jbpt.algo.tree.tctree.TCType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class FragmentNode extends Canonical {

    private String fragmentId;
    private TCType type = null;
    private Collection<FragmentNode> children = new ArrayList<>();
    private FragmentNode parent = null;
    private String fragmentCode;

    public FragmentNode() {
        super();
        this.setUri(UUID.randomUUID().toString());
        fragmentId = "F" + IDGenerator.generateID();
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(String fragmentId) {
        this.fragmentId = fragmentId;
    }

    public String getFragmentCode() {
        return fragmentCode;
    }

    public void setFragmentCode(String fragmentCode) {
        this.fragmentCode = fragmentCode;
    }

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
