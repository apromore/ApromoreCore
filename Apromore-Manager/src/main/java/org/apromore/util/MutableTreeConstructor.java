package org.apromore.util;

import org.apromore.exception.RepositoryException;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.service.model.FragmentNode;
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

    public FragmentNode construct(RPST<CPFEdge, CPFNode> rpst) throws RepositoryException {
        return constructTree(rpst.getRoot(), null, rpst);
    }

    private FragmentNode constructTree(IRPSTNode<CPFEdge, CPFNode> root, FragmentNode parent, RPST<CPFEdge, CPFNode> rpst)
            throws RepositoryException {
        FragmentNode rf;
        if (root != null) {
            rf = new FragmentNode();
            rf.setType(root.getType());

            if (parent != null) {
                parent.getChildren().add(rf);
                rf.setParent(parent);
            }

            for (CPFEdge e : root.getFragment()) {
                CPFEdge newEdge = rf.addEdge(e.getOriginalId(), e.getSource(), e.getTarget());
                newEdge.setId(e.getId());
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
            throw new RepositoryException("Can not construct tree with a null node.");
        }

        return rf;
    }

}
