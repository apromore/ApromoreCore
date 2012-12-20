package org.apromore.util;

import org.apromore.graph.TreeVisitor;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.service.helper.OperationContext;
import org.apromore.service.model.FragmentNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Chathura Ekanayake
 */
public class HashUtil {

    private static Logger log = LoggerFactory.getLogger(HashUtil.class);

    public static String computeHash(FragmentNode fragment, TCType nodeType, OperationContext op) {
        Canonical graph = op.getGraph();
        TreeVisitor visitor = op.getTreeVisitor();
        int fragmentSize = fragment.getVertices().size();

        log.debug("Computing hash of a fragment with " + fragmentSize + " vertices...");

        Collection<CPFEdge> fEdges = fragment.getEdges();
        Collection<CPFNode> fVertices = fragment.getVertices();
        Set<CPFNode> vertices = new HashSet<CPFNode>(fVertices);
        Set<CPFEdge> edges = new HashSet<CPFEdge>(fEdges);

        String hash = null;
        String type = "None";

        try {
            if (nodeType == TCType.POLYGON) {
                type = "P";
                log.debug("Fragment type: " + type + " | Fragment size: " + fragmentSize);
                hash = visitor.visitSNode(graph, edges, fragment.getEntry());
            } else if (nodeType == TCType.BOND) {
                type = "B";
                log.debug("Fragment type: " + type + " | Fragment size: " + fragmentSize);
                hash = computeBondHash(fragment, op);
            } else if (nodeType == TCType.RIGID) {
                type = "R";
                log.debug("Fragment type: " + type + " | Fragment size: " + fragmentSize);
                if (fragmentSize <= 10) {
                    hash = visitor.visitRNode(graph, edges, vertices, fragment.getEntry(), fragment.getExit());
                } else {
                    hash = null;
                    log.debug("Large fragment. Skipped the hash computation.");
                }
            }
        } catch (StringIndexOutOfBoundsException se) {
            String msg = "Unable to compute hash. " + se.getMessage();
            log.error(msg, se);
            hash = null;
        } catch (Exception e) {
            String msg = "Fragment code computation error. " + e.getMessage();
            log.error(msg, e);
            hash = null;
        }

        log.debug("Hash: " + hash);

        return hash;
    }

    private static String computeBondHash(Canonical fragment, OperationContext op) {
        int oPockets = 0;
        int iPockets = 0;
        int oDirectConnections = 0;
        int iDirectConnections = 0;

        CPFNode entry = fragment.getEntry();
        CPFNode exit = fragment.getExit();
        Collection<CPFNode> successors = FragmentUtil.getPostset(entry, fragment.getEdges());

        for (CPFNode successor : successors) {
            if (exit.equals(successor)) {
                oDirectConnections++;
            } else {
                oPockets++;
            }
        }

        // this is required if the fragment contains loops
        Collection<CPFNode> predecessors = FragmentUtil.getPreset(entry, fragment.getEdges());
        for (CPFNode predecessor : predecessors) {
            if (exit.equals(predecessor)) {
                iDirectConnections++;
            } else {
                iPockets++;
            }
        }

        return "OP" + oPockets + "ODC" + oDirectConnections + "IP" + iPockets + "IDC" + iDirectConnections;
    }
}
