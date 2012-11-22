package org.apromore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apromore.common.Constants;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.exception.PocketMappingException;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.Event;
import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.Node;
import org.apromore.graph.canonical.Routing;
import org.apromore.graph.canonical.Task;
import org.apromore.service.model.RFragment2;
import org.jbpt.algo.tree.tctree.TCType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class FragmentUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FragmentUtil.class);


    public static String getFragmentType(final RFragment2 f) {
        String nodeType = "UNKNOWN";
        if (TCType.POLYGON.equals(f.getType())) {
            nodeType = "P";
        } else if (TCType.BOND.equals(f.getType())) {
            nodeType = "B";
        } else if (TCType.RIGID.equals(f.getType())) {
            nodeType = "R";
        } else if (TCType.TRIVIAL.equals(f.getType())) {
            nodeType = "T";
        }
        return nodeType;
    }

    @SuppressWarnings("unchecked")
    public static void removeEdges(final RFragment2 f, final Collection<Edge> cfEdges) {
        Collection<Edge> fEdges = f.getEdges();

        for (Edge fe : fEdges) {
            for (Edge cfe : cfEdges) {
                if ((fe.getSource() != null && fe.getSource().getId().equals(cfe.getSource().getId())) &&
                        (fe.getTarget() != null && fe.getTarget().getId().equals(cfe.getTarget().getId()))) {
                    f.removeEdge(fe);
                }
            }
        }
    }

    public static void removeEdges(final Collection<Edge> es1, final Collection<Edge> es2) {
        Collection<Edge> toBeRemoved = new ArrayList<Edge>();
        for (Edge fe : es1) {
            for (Edge cfe : es2) {
                if ((fe.getSource() != null && fe.getSource().getId().equals(cfe.getSource().getId())) &&
                        (fe.getTarget() != null && fe.getTarget().getId().equals(cfe.getTarget().getId()))) {
                    toBeRemoved.add(fe);
                }
            }
        }
        es1.removeAll(toBeRemoved);
    }

    public static Collection<Edge> getIncomingEdges(final Node v, final Collection<Edge> es) {
        Collection<Edge> incomingEdges = new ArrayList<Edge>();
        for (Edge e : es) {
            if (e.getTarget() != null && e.getTarget().getId().equals(v.getId())) {
                incomingEdges.add(e);
            }
        }
        return incomingEdges;
    }

    public static Collection<Edge> getOutgoingEdges(final Node v, final Collection<Edge> es) {
        Collection<Edge> outgoingEdges = new ArrayList<Edge>();
        for (Edge e : es) {
            if (e.getSource() != null && e.getSource().getId().equals(v.getId())) {
                outgoingEdges.add(e);
            }
        }
        return outgoingEdges;
    }

    public static List<Node> getPreset(final Node v, final Collection<Edge> es) {
        List<Node> preset = new ArrayList<Node>(0);
        if (v != null) {
            for (Edge e: es) {
                if (e.getTarget() != null && e.getTarget().getId().equals(v.getId())) {
                    preset.add(e.getSource());
                }
            }
        }
        return preset;
    }

    public static List<Node> getPostset(final Node v, final Collection<Edge> es) {
        List<Node> postset = new ArrayList<Node>(0);
        if (v != null) {
            for (Edge e: es) {
                if (e.getSource() != null && e.getSource().getId().equals(v.getId())) {
                    postset.add(e.getTarget());
                }
            }
        }
        return postset;
    }

    public static Node getFirstNode(final Collection<Node> vertices) {
        return vertices.iterator().next();
    }

    public static Edge getFirstEdge(final Collection<Edge> c) {
        return c.iterator().next();
    }

    /**
     * Creates a new child mapping by replacing pocket ids of fragment by their corresponding pockets ids of content.
     * @param childMappings map pocketId -> childId
     * @param pocketMappings map fragment pocket Id -> content pocket Id
     */
    public static Map<String, String> remapChildren(final Map<String, String> childMappings, final Map<String, String> pocketMappings)
            throws PocketMappingException {
        Map<String, String> newChildMapping = new HashMap<String, String>();
        for (Entry<String, String> stringStringEntry : childMappings.entrySet()) {
            String o = pocketMappings.get(stringStringEntry.getKey());
            if (o != null) {
                String mappedPocketId = pocketMappings.get(stringStringEntry.getKey());
                String childId = stringStringEntry.getValue();
                newChildMapping.put(mappedPocketId, childId);
            } else {
                String msg = "Mapping of pocket " + stringStringEntry.getKey() + " is null.";
                LOGGER.error(msg);
                throw new PocketMappingException(msg);
            }
        }
        return newChildMapping;
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
                newChildMapping.put(mappedPocketId, fvd.getChildFragmentVersionId().getId().toString());
            } else {
                String msg = "Mapping of pocket " + fvd.getPocketId() + " is null.";
                LOGGER.error(msg);
                throw new PocketMappingException(msg);
            }
        }
        return newChildMapping;
    }

    @SuppressWarnings("unchecked")
    public static void cleanFragment(final RFragment2 f) {
        Collection<Edge> es = f.getEdges();
        Collection<Node> vs = f.getNodes();
        Collection<Edge> removableEdges = new ArrayList<Edge>();

        for (Edge e : es) {
            if (!vs.contains(e.getSource()) || !vs.contains(e.getTarget())) {
                removableEdges.add(e);
            }
        }

        f.removeEdges(removableEdges);
    }

    @SuppressWarnings("unchecked")
    public static void reconnectBoundary1(final RFragment2 f, final Node oldB1, final Node newB1) {
        Node b1 = f.getEntry();
        Node b2 = f.getExit();

        if (b1.equals(oldB1)) {
            f.setEntry(newB1);
            reconnectNodes(b1, newB1, f);
            f.addNode(newB1);
            f.removeNode(b1);

            Collection<RFragment2> childFragments = f.getChildren();
            for (RFragment2 childFragment : childFragments) {
                if (childFragment.getType() != TCType.TRIVIAL) {
                    reconnectBoundary1(childFragment, oldB1, newB1);
                }
            }
        }

        if (b2.equals(oldB1)) {
            LOGGER.debug("b2 = oldB1 in fragment: " + fragmentToString(f));
        }
    }

    @SuppressWarnings("unchecked")
    public static void reconnectBoundary2(final RFragment2 f, final Node oldB2, final Node newB2) {
        Node b2 = f.getExit();

        if (b2.equals(oldB2)) {
            f.setExit(newB2);
            reconnectNodes(b2, newB2, f);
            f.addNode(newB2);
            f.removeNode(b2);

            Collection<RFragment2> childFragments = f.getChildren();
            for (RFragment2 childFragment : childFragments) {
                if (childFragment.getType() != TCType.TRIVIAL) {
                    reconnectBoundary2(childFragment, oldB2, newB2);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void reconnectNodes(final Node oldVertex, final Node newVertex, final RFragment2 f) {
        Collection<Edge> edges = f.getEdges();
        for (Edge edge : edges) {
            if (edge.getSource().getId().equals(oldVertex.getId())) {
                edge.setSource(newVertex);
            } else if (edge.getTarget().getId().equals(oldVertex.getId())) {
                edge.setTarget(newVertex);
            }
        }
    }

    public static Node duplicateNode(final Node v, final Canonical og) {
        Node newV = (Node) v.clone();
        String type = og.getNodeProperty(v.getId(), Constants.TYPE);

        og.addNode(newV);
        og.setNodeProperty(newV.getId(), Constants.TYPE, type);
        return newV;
    }

    public static String getType(final Node node) {
        String type = null;
        if (node instanceof Task) {
            type = Constants.FUNCTION;
        } else if (node instanceof Event) {
            type = Constants.EVENT;
        } else {
            if (node instanceof Routing) {
                type = Constants.CONNECTOR;
            } else {
                String nodeName = node.getName();
                if ("OR".equals(nodeName) || "XOR".equals(nodeName) || "AND".equals(nodeName)) {
                    type = Constants.CONNECTOR;
                }
            }
        }
        return type;
    }

    public static String getType(final INode node) {
        String type = null;
        if (node instanceof Task) {
            type = Constants.FUNCTION;
        } else if (node instanceof Event) {
            type = Constants.EVENT;
        } else {
            if (node instanceof Routing) {
                type = Constants.CONNECTOR;
            } else {
                String nodeName = node.getName();
                if (nodeName.equals("OR") || nodeName.equals("XOR") || nodeName.equals("AND")) {
                    type = Constants.CONNECTOR;
                }
            }
        }
        return type;
    }

    @SuppressWarnings("unchecked")
    public static String fragmentToString(final RFragment2 f, final Canonical g) {
        StringBuilder fs = new StringBuilder(0);
        Collection<Node> vs = f.getNodes();
        for (Node v : vs) {
            String label = v.getName();
            fs.append('\n').append(v).append(" : ").append(label);
        }
        fs.append("\nBoundary 1: ").append(f.getEntry().getId());
        fs.append("\nBoundary 2: ").append(f.getExit().getId());
        return fs.toString();
    }

    @SuppressWarnings("unchecked")
    public static String fragmentToString(final RFragment2 f) {
        StringBuilder fs = new StringBuilder(0);
        Collection<Node> vs = f.getNodes();
        for (Node v : vs) {
            fs.append(v).append(", ");
        }
        return fs.toString();
    }

    public static void removeNodes(final RFragment2 Canoncial, final RFragment2 cf) {
        List<Node> toBeRemoved = new ArrayList<Node>();
        for (Node pn : Canoncial.getNodes()) {
            for (Node cn : cf.getNodes()) {
                if (pn.getId().equals(cn.getId())) {
                    toBeRemoved.add(pn);
                }
            }
        }
        Canoncial.removeNodes(toBeRemoved);
    }

}
