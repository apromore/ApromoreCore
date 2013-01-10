package org.apromore.util;

import org.apromore.common.Constants;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.exception.PocketMappingException;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.service.model.FragmentNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chathura Ekanayake
 */
public class FragmentUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FragmentUtil.class);


    public static String getFragmentType(final FragmentNode f) {
        String nodeType = "UNKNOWN";
        if (f != null) {
            if (TCType.POLYGON.equals(f.getType())) {
                nodeType = "P";
            } else if (TCType.BOND.equals(f.getType())) {
                nodeType = "B";
            } else if (TCType.RIGID.equals(f.getType())) {
                nodeType = "R";
            } else if (TCType.TRIVIAL.equals(f.getType())) {
                nodeType = "T";
            }
        }
        return nodeType;
    }

    @SuppressWarnings("unchecked")
    public static void removeEdges(final FragmentNode f, final Collection<CPFEdge> cfEdges) {
        Collection<CPFEdge> fEdges = f.getEdges();

        for (CPFEdge fe : fEdges) {
            for (CPFEdge cfe : cfEdges) {
                if (fe.getSource().getId().equals(cfe.getSource().getId()) && fe.getTarget().getId().equals(cfe.getTarget().getId())) {
                    f.removeEdge(fe);
                }
            }
        }
    }

    public static void removeEdges(final Collection<CPFEdge> es1, final Collection<CPFEdge> es2) {
        Collection<CPFEdge> toBeRemoved = new ArrayList<CPFEdge>();
        for (CPFEdge fe : es1) {
            for (CPFEdge cfe : es2) {
                if (fe.getSource().getId().equals(cfe.getSource().getId()) && fe.getTarget().getId().equals(cfe.getTarget().getId())) {
                    toBeRemoved.add(fe);
                }
            }
        }
        es1.removeAll(toBeRemoved);
    }

    public static Collection<CPFEdge> getIncomingEdges(final CPFNode v, final Collection<CPFEdge> es) {
        Collection<CPFEdge> incomingEdges = new ArrayList<CPFEdge>();
        if (v != null) {
            for (CPFEdge e : es) {
                if (e.getTarget().getId().equals(v.getId())) {
                    incomingEdges.add(e);
                }
            }
        }
        return incomingEdges;
    }

    public static Collection<CPFEdge> getOutgoingEdges(final CPFNode v, final Collection<CPFEdge> es) {
        Collection<CPFEdge> outgoingEdges = new ArrayList<CPFEdge>();
        if (v != null) {
            for (CPFEdge e : es) {
                if (e.getSource().getId().equals(v.getId())) {
                    outgoingEdges.add(e);
                }
            }
        }
        return outgoingEdges;
    }

    public static List<CPFNode> getPreset(final CPFNode v, final Collection<CPFEdge> es) {
        List<CPFNode> preset = new ArrayList<CPFNode>(0);
        if (v != null) {
            for (CPFEdge e: es) {
                if (e.getTarget().getId().equals(v.getId())) {
                    preset.add(e.getSource());
                }
            }
        }
        return preset;
    }

    public static List<CPFNode> getPostset(final CPFNode v, final Collection<CPFEdge> es) {
        List<CPFNode> postset = new ArrayList<CPFNode>(0);
        if (v != null) {
            for (CPFEdge e: es) {
                if (e.getSource().getId().equals(v.getId())) {
                    postset.add(e.getTarget());
                }
            }
        }
        return postset;
    }

    public static CPFNode getFirstNode(final Collection<CPFNode> vertices) {
        return vertices.iterator().next();
    }

    public static CPFEdge getFirstEdge(final Collection<CPFEdge> c) {
        return c.iterator().next();
    }

    /**
     * Creates a new child mapping by replacing pocket ids of fragment by their corresponding pockets ids of content.
     * @param childMappings map pocketId -> childId
     * @param pocketMappings map fragment pocket Id -> content pocket Id
     */
    public static Map<String, String> remapChildren(final List<FragmentVersionDag> childMappings, final Map<String, String> pocketMappings)
            throws PocketMappingException {
        Map<String, String> newChildMapping = new HashMap<String, String>(0);
        for (FragmentVersionDag fvd : childMappings) {
            String o = pocketMappings.get(fvd.getPocketId());
            if (o != null) {
                String mappedPocketId = pocketMappings.get(fvd.getPocketId());
                newChildMapping.put(mappedPocketId, fvd.getChildFragmentVersion().getId().toString());
            } else {
                String msg = "Mapping of pocket " + fvd.getPocketId() + " is null.";
                LOGGER.error(msg);
                throw new PocketMappingException(msg);
            }
        }
        return newChildMapping;
    }

    @SuppressWarnings("unchecked")
    public static void cleanFragment(final FragmentNode f) {
        Collection<CPFEdge> es = f.getEdges();
        Collection<CPFNode> vs = f.getNodes();
        Collection<CPFEdge> removableEdges = new ArrayList<CPFEdge>();

        for (CPFEdge e : es) {
            if (!vs.contains(e.getSource()) || !vs.contains(e.getTarget())) {
                removableEdges.add(e);
            }
        }

        f.removeEdges(removableEdges);
    }

    @SuppressWarnings("unchecked")
    public static void reconnectBoundary1(final FragmentNode f, final CPFNode oldB1, final CPFNode newB1) {
        CPFNode b1 = f.getEntry();
        CPFNode b2 = f.getExit();

        if (b1 != null && b1.equals(oldB1)) {
            f.setEntry(newB1);
            reconnectNodes(b1, newB1, f);
            f.addNode(newB1);
            f.removeNode(b1);

            Collection<FragmentNode> childFragments = f.getChildren();
            for (FragmentNode childFragment : childFragments) {
                if (childFragment.getType() != TCType.TRIVIAL) {
                    reconnectBoundary1(childFragment, oldB1, newB1);
                }
            }
        }

        if (b2 != null && b2.equals(oldB1)) {
            LOGGER.debug("b2 = oldB1 in fragment: " + fragmentToString(f));
        }
    }

    @SuppressWarnings("unchecked")
    public static void reconnectBoundary2(final FragmentNode f, final CPFNode oldB2, final CPFNode newB2) {
        CPFNode b2 = f.getExit();

        if (b2 != null && b2.equals(oldB2)) {
            f.setExit(newB2);
            reconnectNodes(b2, newB2, f);
            f.addNode(newB2);
            f.removeNode(b2);

            Collection<FragmentNode> childFragments = f.getChildren();
            for (FragmentNode childFragment : childFragments) {
                if (childFragment.getType() != TCType.TRIVIAL) {
                    reconnectBoundary2(childFragment, oldB2, newB2);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void reconnectNodes(final CPFNode oldVertex, final CPFNode newVertex, final FragmentNode f) {
        Collection<CPFEdge> edges = f.getEdges();
        for (CPFEdge edge : edges) {
            if (edge.getSource().getId().equals(oldVertex.getId())) {
                edge.setSource(newVertex);
            } else if (edge.getTarget().getId().equals(oldVertex.getId())) {
                edge.setTarget(newVertex);
            }
        }
    }

    public static CPFNode duplicateNode(final CPFNode v, final Canonical og) {
        CPFNode newV = (CPFNode) v.clone();
        String type = og.getNodeProperty(v.getId(), Constants.TYPE);

        og.addNode(newV);
        og.setNodeProperty(newV.getId(), Constants.TYPE, type);
        return newV;
    }

    public static String getType(final INode node) {
        String type = null;
        if (node != null) {
            if (node.getNodeType() != null) {
                if (node.getNodeType().equals(NodeTypeEnum.TASK)) {
                    type = Constants.FUNCTION;
                } else if (node.getNodeType().equals(NodeTypeEnum.EVENT)) {
                    type = Constants.EVENT;
                } else if (node.getNodeType().equals(NodeTypeEnum.MESSAGE)) {
                    type = Constants.EVENT;
                } else if (node.getNodeType().equals(NodeTypeEnum.TIMER)) {
                    type = Constants.EVENT;
                } else {
                    if (node.getNodeType().equals(NodeTypeEnum.ORJOIN) || node.getNodeType().equals(NodeTypeEnum.XORJOIN) ||
                            node.getNodeType().equals(NodeTypeEnum.ANDJOIN) || node.getNodeType().equals(NodeTypeEnum.ORSPLIT) ||
                            node.getNodeType().equals(NodeTypeEnum.XORSPLIT) || node.getNodeType().equals(NodeTypeEnum.ANDSPLIT) ||
                            node.getNodeType().equals(NodeTypeEnum.STATE)) {
                        type = Constants.CONNECTOR;
                    } else {
                        String nodeName = node.getName();
                        if (nodeName != null && (nodeName.equals("OrJoin") || nodeName.equals("XOrJoin") ||
                                nodeName.equals("AndJoin") || nodeName.equals("OrSplit") || nodeName.equals("XOrSplit") ||
                                nodeName.equals("AndSplit") || nodeName.equals("State"))) {
                            type = Constants.CONNECTOR;
                        }
                    }
                }
            } else {
                LOGGER.warn("Unable to determine Node Type, Type is NULL (Could be a Pocket). " + node.getId());
            }
        } else {
            LOGGER.warn("Unable to determine Node Type, Node is NULL. ");
        }
        return type;
    }

//    @SuppressWarnings("unchecked")
//    public static String fragmentToString(final FragmentNode f, final Canonical g) {
//        StringBuilder fs = new StringBuilder(0);
//        Collection<CPFNode> vs = f.getNodes();
//        for (CPFNode v : vs) {
//            String label = v.getName();
//            fs.append('\n').append(v).append(" : ").append(label);
//        }
//        fs.append("\nBoundary 1: ").append(f.getEntry().getId());
//        fs.append("\nBoundary 2: ").append(f.getExit().getId());
//        return fs.toString();
//    }

    @SuppressWarnings("unchecked")
    public static String fragmentToString(final FragmentNode f) {
        StringBuilder fs = new StringBuilder(0);
        Collection<CPFNode> vs = f.getNodes();
        for (CPFNode v : vs) {
            fs.append(v).append(", ");
        }
        return fs.toString();
    }

    public static void removeNodes(final FragmentNode canoncial, final FragmentNode cf) {
        List<CPFNode> toBeRemoved = new ArrayList<CPFNode>();
        for (CPFNode pn : canoncial.getNodes()) {
            for (CPFNode cn : cf.getNodes()) {
                if (pn.getId().equals(cn.getId())) {
                    toBeRemoved.add(pn);
                }
            }
        }
        canoncial.removeNodes(toBeRemoved);
    }

}
