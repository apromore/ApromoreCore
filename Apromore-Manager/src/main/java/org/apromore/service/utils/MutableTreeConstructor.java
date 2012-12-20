package org.apromore.service.utils;

import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.service.model.FragmentNode;
import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;

import java.util.Set;

/**
 * Constructs a tree of the Fragments used by the RPST.
 */
public class MutableTreeConstructor {

    public FragmentNode construct(RPST<CPFEdge, CPFNode> rpst) {
        return constructTree(rpst.getRoot(), null, rpst);
    }

    private FragmentNode constructTree(IRPSTNode<CPFEdge, CPFNode> root, FragmentNode parent, RPST<CPFEdge, CPFNode> rpst) {
        FragmentNode rf = new FragmentNode();
        rf.setType(root.getType());

        if (parent != null) {
            parent.getChildren().add(rf);
            rf.setParent(parent);
        }

        for (CPFEdge e : root.getFragment()) {
            rf.addEdge(e.getSource(), e.getTarget());
        }

        rf.setEntry(root.getEntry());
        rf.setExit(root.getExit());

        Set<IRPSTNode<CPFEdge, CPFNode>> cs = rpst.getChildren(root);
        for (IRPSTNode<CPFEdge, CPFNode> c : cs) {
            if (!c.getFragment().isEmpty()) {
                constructTree(c, rf, rpst);
            }
        }

        return rf;
    }

}
