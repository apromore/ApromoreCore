package org.apromore.service.utils;

import java.util.Set;

import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.Node;
import org.apromore.service.model.RFragment2;
import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;

public class MutableTreeContructor {

    public static RFragment2 construct(RPST<Edge, Node> rpst) {
        IRPSTNode<Edge, Node> root = rpst.getRoot();
        return constructTree(root, null, rpst);
    }

    private static RFragment2 constructTree(IRPSTNode<Edge, Node> f, RFragment2 parent, RPST<Edge, Node> rpst) {
        RFragment2 rf = new RFragment2();
        rf.setType(f.getType());

        if (parent != null) {
            parent.getChildren().add(rf);
            rf.setParent(parent);
        }

        for (Edge e : f.getFragment()) {
            rf.addEdge(e.getSource(), e.getTarget());
        }

        rf.setEntry(f.getEntry());
        rf.setExit(f.getExit());

        Set<IRPSTNode<Edge, Node>> cs = rpst.getChildren(f);
        for (IRPSTNode<Edge, Node> c : cs) {
            if (!c.getFragment().isEmpty()) {
                constructTree(c, rf, rpst);
            }
        }

        return rf;
    }
}
