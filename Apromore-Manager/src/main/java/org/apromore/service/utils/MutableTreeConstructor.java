package org.apromore.service.utils;

import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.service.model.FragmentNode;
import org.apromore.util.FragmentUtil;
import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Constructs a tree of the Fragments used by the RPST.
 */
public class MutableTreeConstructor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MutableTreeConstructor.class);

    public FragmentNode construct(RPST<CPFEdge, CPFNode> rpst) {
//        String value = "";
//        LOGGER.debug("RPST.getChildren():");
//        for (IRPSTNode<CPFEdge,CPFNode> node : rpst.getRPSTNodes()) {
//            value = node.getName() + ": ";
//            for (IRPSTNode<CPFEdge,CPFNode> child : rpst.getChildren(node)) {
//                value += child.getName() + " ";
//            }
//            LOGGER.debug(value);
//        }
//        LOGGER.debug("");
//
//        LOGGER.debug("RPST.getPolygonChildren():");
//        for (IRPSTNode<CPFEdge,CPFNode> node : rpst.getRPSTNodes()) {
//            value = node.getName() + ": ";
//            for (IRPSTNode<CPFEdge,CPFNode> child : rpst.getPolygonChildren(node)) {
//                value += child.getName() + " ";
//            }
//            LOGGER.debug(value);
//        }
//        LOGGER.debug("");

        //FragmentNode canonicalRPST = constructTree(rpst.getRoot(), null, rpst);

//        LOGGER.debug("CanonicalRPST:");
//        LOGGER.debug(FragmentUtil.fragmentToString(canonicalRPST));
//        for (FragmentNode child : canonicalRPST.getChildren()) {
//            LOGGER.debug(FragmentUtil.fragmentToString(child));
//        }
//        LOGGER.debug("");

        return constructTree(rpst.getRoot(), null, rpst);
    }

    private FragmentNode constructTree(IRPSTNode<CPFEdge, CPFNode> root, FragmentNode parent, RPST<CPFEdge, CPFNode> rpst) {
        FragmentNode rf = null;
        if (root != null) {
            rf = new FragmentNode();
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
        } else {
            LOGGER.error("Can not construct tree with a null node.");
        }

        return rf;
    }

}
