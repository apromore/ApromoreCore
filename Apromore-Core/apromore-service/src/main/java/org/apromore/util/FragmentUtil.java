package org.apromore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apromore.common.Constants;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.exception.PocketMappingException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfAndGateway;
import org.apromore.graph.JBPT.CpfEvent;
import org.apromore.graph.JBPT.CpfGateway;
import org.apromore.graph.JBPT.CpfNode;
import org.apromore.graph.JBPT.CpfOrGateway;
import org.apromore.graph.JBPT.CpfTask;
import org.apromore.graph.JBPT.CpfXorGateway;
import org.apromore.graph.JBPT.ICpfNode;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.algo.rpst.RPST;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.graph.algo.tctree.TCType;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.IFlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class FragmentUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FragmentUtil.class);

    public static String getFragmentType(final RPSTNode f) {
        String nodeType = "UNKNOWN";
        if (TCType.P.equals(f.getType())) {
            nodeType = "P";
        } else if (TCType.B.equals(f.getType())) {
            nodeType = "B";
        } else if (TCType.R.equals(f.getType())) {
            nodeType = "R";
        } else if (TCType.T.equals(f.getType())) {
            nodeType = "T";
        }
        return nodeType;
    }

    @SuppressWarnings("unchecked")
    public static void removeEdges(final RPSTNode f, final RPSTNode cf) {
        Collection<AbstractDirectedEdge> fEdges = f.getFragmentEdges();
        Collection<AbstractDirectedEdge> cfEdges = cf.getFragmentEdges();

        for (AbstractDirectedEdge fe : fEdges) {
            for (AbstractDirectedEdge cfe : cfEdges) {
                if (fe.getSource().getId().equals(cfe.getSource().getId()) &&
                        fe.getTarget().getId().equals(cfe.getTarget().getId())) {
                    f.getFragment().removeEdge(fe);
                }
            }
        }

    }

    public static Collection<AbstractDirectedEdge> getIncomingEdges(final IFlowNode v, final Collection<AbstractDirectedEdge> es) {
        Collection<AbstractDirectedEdge> incomingEdges = new ArrayList<AbstractDirectedEdge>(0);
        for (AbstractDirectedEdge e : es) {
            if (e.getTarget().getId().equals(v.getId())) {
                incomingEdges.add(e);
            }
        }
        return incomingEdges;
    }

    public static Collection<AbstractDirectedEdge> getOutgoingEdges(final IFlowNode v, final Collection<AbstractDirectedEdge> es) {
        Collection<AbstractDirectedEdge> outgoingEdges = new ArrayList<AbstractDirectedEdge>(0);
        for (AbstractDirectedEdge e : es) {
            if (e.getSource().getId().equals(v.getId())) {
                outgoingEdges.add(e);
            }
        }
        return outgoingEdges;
    }

    public static List<IFlowNode> getPreset(final IFlowNode v, final Collection<AbstractDirectedEdge> es) {
        List<IFlowNode> preset = new ArrayList<IFlowNode>(0);
        for (AbstractDirectedEdge e : es) {
            if (e.getTarget().getId().equals(v.getId())) {
                preset.add((IFlowNode) e.getSource());
            }
        }
        return preset;
    }

    public static List<IFlowNode> getPostset(final IFlowNode v, final Collection<AbstractDirectedEdge> es) {
        List<IFlowNode> postset = new ArrayList<IFlowNode>(0);
        for (AbstractDirectedEdge e : es) {
            if (e.getSource().getId().equals(v.getId())) {
                postset.add((IFlowNode) e.getTarget());
            }
        }
        return postset;
    }

    public static FlowNode getFirstVertex(final Collection<FlowNode> vertices) {
        return vertices.iterator().next();
    }

    public static AbstractDirectedEdge getFirstEdge(final Collection<AbstractDirectedEdge> c) {
        return c.iterator().next();
    }

    /**
     * Creates a new child mapping by replacing pocket ids of fragment by their corresponding pockets ids of content.
     *
     * @param childMappings  map pocketId -> childId
     * @param pocketMappings map fragment pocket Id -> content pocket Id
     */
    public static Map<String, String> remapChildren(final Map<String, String> childMappings, final Map<String, String> pocketMappings)
            throws PocketMappingException {
        Map<String, String> newChildMapping = new HashMap<String, String>(0);
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
     *
     * @param childMappings  map pocketId -> childId
     * @param pocketMappings map fragment pocket Id -> content pocket Id
     */
    public static Map<String, FragmentVersion> remapChildren(final List<FragmentVersionDag> childMappings, final Map<String, String> pocketMappings)
            throws PocketMappingException {
        Map<String, FragmentVersion> newChildMapping = new HashMap<String, FragmentVersion>(0);
        for (FragmentVersionDag fvd : childMappings) {
            String o = pocketMappings.get(fvd.getPocketId());
            if (o != null) {
                String mappedPocketId = pocketMappings.get(fvd.getPocketId());
                newChildMapping.put(mappedPocketId, fvd.getChildFragmentVersionId());
            } else {
                String msg = "Mapping of pocket " + fvd.getPocketId() + " is null.";
                LOGGER.error(msg);
                throw new PocketMappingException(msg);
            }
        }
        return newChildMapping;
    }

    @SuppressWarnings("unchecked")
    public static void cleanFragment(final RPSTNode f) {
        Collection<AbstractDirectedEdge> es = f.getFragmentEdges();
        Collection<FlowNode> vs = f.getFragment().getVertices();
        Collection<AbstractDirectedEdge> removableEdges = new ArrayList<AbstractDirectedEdge>(0);

        for (AbstractDirectedEdge e : es) {
            if (!vs.contains(e.getSource()) || !vs.contains(e.getTarget())) {
                removableEdges.add(e);
            }
        }

        f.getFragment().removeEdges(removableEdges);
    }

    @SuppressWarnings("unchecked")
    public static void reconnectBoundary1(final RPSTNode f, final FlowNode oldB1, final FlowNode newB1, final RPST rpst) {
        FlowNode b1 = (FlowNode) f.getEntry();
        FlowNode b2 = (FlowNode) f.getExit();

        if (b1.getId().equals(oldB1.getId())) {
            f.setEntry(newB1);
            reconnectVertices(b1, newB1, f);
            f.getFragment().addVertex(newB1);
            f.getFragment().removeVertex(b1);

            Collection<RPSTNode> childFragments = rpst.getChildren(f);
            for (RPSTNode childFragment : childFragments) {
                if (childFragment.getType() != TCType.T) {
                    reconnectBoundary1(childFragment, oldB1, newB1, rpst);
                }
            }
        }

        if (b2.equals(oldB1)) {
            LOGGER.debug("b2 = oldB1 in fragment: " + fragmentToString(f));
        }
    }

    @SuppressWarnings("unchecked")
    public static void reconnectBoundary2(final RPSTNode f, final FlowNode oldB2, final FlowNode newB2, final RPST rpst) {
        FlowNode b2 = (FlowNode) f.getExit();

        if (b2.equals(oldB2)) {
            f.setExit(newB2);
            reconnectVertices(b2, newB2, f);
            f.getFragment().addVertex(newB2);
            f.getFragment().removeVertex(b2);

            Collection<RPSTNode> childFragments = rpst.getChildren(f);
            for (RPSTNode childFragment : childFragments) {
                if (childFragment.getType() != TCType.T) {
                    reconnectBoundary2(childFragment, oldB2, newB2, rpst);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void reconnectVertices(final FlowNode oldVertex, final FlowNode newVertex, final RPSTNode f) {
        Collection<AbstractDirectedEdge> edges = f.getFragmentEdges();
        for (AbstractDirectedEdge edge : edges) {
            if (edge.getSource().getId().equals(oldVertex.getId())) {
                edge.setSource(newVertex);
            } else if (edge.getTarget().getId().equals(oldVertex.getId())) {
                edge.setTarget(newVertex);
            }
        }
    }

    public static FlowNode duplicateVertex(final IVertex v, final CPF og) {
        String label = v.getName();
        String type = og.getVertexProperty(v.getId(), Constants.TYPE);

        FlowNode newV;
        if (label.equals("XOR")) {
            newV = new CpfXorGateway(label);
        } else if (label.equals("AND")) {
            newV = new CpfAndGateway(label);
        } else if (label.equals("OR")) {
            newV = new CpfOrGateway(label);
        } else {
            newV = new CpfNode(label);
        }
        og.addVertex(newV);
        og.setVertexProperty(newV.getId(), Constants.TYPE, type);

        return newV;
    }

    @SuppressWarnings("unchecked")
    public static String fragmentToString(final RPSTNode f, final CPF g) {
        StringBuilder fs = new StringBuilder();
        Collection<FlowNode> vs = f.getFragment().getVertices();
        for (FlowNode v : vs) {
            String label = v.getName();
            fs.append('\n').append(v).append(" : ").append(label);
        }
        fs.append("\nBoundary 1: ").append(f.getEntry().getId());
        fs.append("\nBoundary 2: ").append(f.getExit().getId());
        return fs.toString();
    }

    @SuppressWarnings("unchecked")
    public static String fragmentToString(final RPSTNode f) {
        StringBuilder fs = new StringBuilder();
        Collection<FlowNode> vs = f.getFragment().getVertices();
        for (FlowNode v : vs) {
            fs.append(v).append(", ");
        }
        return fs.toString();
    }

    public static String getType(final FlowNode node) {
        String type = null;
        if (node instanceof CpfTask) {
            type = Constants.FUNCTION;
        } else if (node instanceof CpfEvent) {
            type = Constants.EVENT;
        } else {
            if (node instanceof CpfGateway) {
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

    public static String getType(final ICpfNode node) {
        return getType((FlowNode) node);
    }
}
