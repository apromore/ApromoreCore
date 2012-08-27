package org.apromore.service.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.common.Constants;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfAndGateway;
import org.apromore.graph.JBPT.CpfEvent;
import org.apromore.graph.JBPT.CpfMessage;
import org.apromore.graph.JBPT.CpfNode;
import org.apromore.graph.JBPT.CpfObject;
import org.apromore.graph.JBPT.CpfOrGateway;
import org.apromore.graph.JBPT.CpfResource;
import org.apromore.graph.JBPT.CpfTask;
import org.apromore.graph.JBPT.CpfTimer;
import org.apromore.graph.JBPT.CpfXorGateway;
import org.apromore.graph.JBPT.ICpfObject;
import org.apromore.graph.JBPT.ICpfResource;
import org.apromore.util.FragmentUtil;
import org.jbpt.pm.FlowNode;

/**
 * GraphToCPFHelper. Used to help build and deconstruct a Graph from the CPF format.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CPFtoGraphHelper {

    /* Private Constructor so it can't be instantiated */
    private CPFtoGraphHelper() { }


    /**
     * Builds a graph from the CPF XSD Format.
     * @param cpf the cpf format from the canoniser.
     * @return the JBpt CPF Graph representation
     */
    public static CPF createGraph(CanonicalProcessType cpf) {
        CPF g = new CPF();

        addProperties(cpf, g);
        List<ICpfResource> res = buildResourcesList(cpf);
        List<ICpfObject> obj = buildObjectsList(cpf);
        buildNodeList(cpf, g, res, obj);

        return g;
    }

    /* populate the Graph with attributes. */
    private static void addProperties(CanonicalProcessType cpf, CPF g) {
        List<TypeAttribute> ats = cpf.getAttribute();
        for (TypeAttribute attr : ats) {
            g.setProperty(attr.getTypeRef(), attr.getValue());
        }
    }

    /* Add Resources to the Graph.  TODO: Missing the specialisedId's */
    private static List<ICpfResource> buildResourcesList(CanonicalProcessType cpf) {
        ICpfResource rec;
        List<ResourceTypeType> rty = cpf.getResourceType();
        List<ICpfResource> attribs = new ArrayList<ICpfResource>(0);
        for (ResourceTypeType resource : rty) {
            rec = new CpfResource();
            rec.setId(resource.getId());
            rec.setName(resource.getName());
            rec.setOriginalId(resource.getOriginalID());
            if (resource instanceof HumanType) {
                rec.setResourceType(CpfResource.ResourceType.HUMAN);
            } else {
                rec.setResourceType(CpfResource.ResourceType.NONHUMAN);
            }
            for (TypeAttribute type : resource.getAttribute()) {
                rec.addAttribute(type.getTypeRef(), type.getValue());
            }
            attribs.add(rec);
        }
        return attribs;
    }

    /* Add Objects to the Graph */
    private static List<ICpfObject> buildObjectsList(CanonicalProcessType cpf) {
        ICpfObject obj;
        List<ObjectType> obt = cpf.getObject();
        List<ICpfObject> objs = new ArrayList<ICpfObject>(0);
        for (ObjectType object : obt) {
            obj = new CpfObject();
            obj.setId(object.getId());
            obj.setName(object.getName());
            if (object instanceof SoftType) {
                obj.setObjectType(CpfObject.ObjectType.SOFT);
            } else {
                obj.setObjectType(CpfObject.ObjectType.HARD);
            }
            for (TypeAttribute type : object.getAttribute()) {
                obj.addAttribute(type.getTypeRef(), type.getValue());
            }
            objs.add(obj);
        }
        return objs;
    }


    /* Add a node to the graph, could be of any of the types */
    private static void buildNodeList(CanonicalProcessType cpf, CPF g, List<ICpfResource> res, List<ICpfObject> obj) {
        Map<String, FlowNode> flow = new HashMap<String, FlowNode>(0);
        for (NetType net : cpf.getNet()) {
            flow.putAll(buildNodeListFromNet(net.getNode(), res, obj));
            buildEdges(net.getEdge(), g, flow);
        }
    }

    /* Build the Node list for a single Net */
    private static Map<String, FlowNode> buildNodeListFromNet(List<NodeType> nodes, List<ICpfResource> res, List<ICpfObject> obj) {
        Map<String, FlowNode> flow = new HashMap<String, FlowNode>(0);
        for (NodeType node : nodes) {
            if (node instanceof MessageType) {
                CpfMessage type = new CpfMessage(node.getName());
                type.setId(node.getId());
                addAttributes(type, node);
                addObjects(type, (MessageType) node, obj);
                addResources(type, (MessageType) node, res);
                flow.put(node.getId(), type);
            } else if (node instanceof TimerType) {
                CpfTimer type = new CpfTimer(node.getName());
                type.setId(node.getId());
                addAttributes(type, node);
                addObjects(type, (TimerType) node, obj);
                addResources(type, (TimerType) node, res);
                flow.put(node.getId(), type);
            } else if (node instanceof TaskType) {
                CpfTask type = new CpfTask(node.getName());
                type.setId(node.getId());
                addAttributes(type, node);
                addObjects(type, (TaskType) node, obj);
                addResources(type, (TaskType) node, res);
                flow.put(node.getId(), type);
            } else if (node instanceof EventType) {
                CpfEvent type = new CpfEvent(node.getName());
                type.setId(node.getId());
                addAttributes(type, node);
                addObjects(type, (EventType) node, obj);
                addResources(type, (EventType) node, res);
                flow.put(node.getId(), type);
            } else if (node instanceof ORSplitType || node instanceof ORJoinType) {
                CpfOrGateway og = new CpfOrGateway("OR");
                og.setId(node.getId());
                addAttributes(og, node);
                flow.put(node.getId(), og);
            } else if (node instanceof XORSplitType || node instanceof XORJoinType) {
                CpfXorGateway xog = new CpfXorGateway("XOR");
                xog.setId(node.getId());
                addAttributes(xog, node);
                flow.put(node.getId(), xog);
            } else if (node instanceof ANDSplitType || node instanceof ANDJoinType) {
                CpfAndGateway ag = new CpfAndGateway("AND");
                ag.setId(node.getId());
                addAttributes(ag, node);
                flow.put(node.getId(), ag);
            }
        }
        return flow;
    }

    /* Builds the list of Edges for this Model */
    private static void buildEdges(List<EdgeType> edgeTypes, CPF graph, Map<String, FlowNode> nodes) {
        for (EdgeType edge : edgeTypes) {
            FlowNode source = nodes.get(edge.getSourceId());
            FlowNode target = nodes.get(edge.getTargetId());
            if (source != null && target != null) {
                graph.addControlFlow(source, target);

                graph.setVertexProperty(source.getId(), Constants.TYPE, FragmentUtil.getType(source));
                graph.setVertexProperty(target.getId(), Constants.TYPE, FragmentUtil.getType(target));
            }
        }
    }

    /* Adds the Objects to the Node */
    private static void addObjects(CpfNode n, WorkType node, List<ICpfObject> objs) {
        CpfObject o;
        for (ObjectRefType ort : node.getObjectRef()) {
            for (ICpfObject obj : objs) {
                if (ort.getObjectId() != null && ort.getObjectId().equals(obj.getId())) {
                    o = new CpfObject();
                    o.setId(ort.getId());
                    o.setName(obj.getName());
                    o.setOptional(obj.getOptional());
                    o.setConsumed(obj.getConsumed());
                    o.setConfigurable(obj.isConfigurable());
                    o.setOriginalId(ort.getOriginalID());
                    o.setObjectId(ort.getObjectId());
                    o.setType(ort.getType().toString());
                    o.setAttributes(buildCombinedAttributeList(obj.getAttributes(), ort.getAttribute()));
                    n.addObject(o);
                }
            }
        }
    }

    /* Adds the Resources to the Node */
    private static void addResources(CpfNode n, WorkType node, List<ICpfResource> reses) {
        CpfResource r;
        for (ResourceTypeRefType ort : node.getResourceTypeRef()) {
            for (ICpfResource res : reses) {
                if (ort.getResourceTypeId() != null && ort.getResourceTypeId().equals(res.getId())) {
                    r = new CpfResource();
                    r.setId(ort.getId());
                    r.setName(res.getId());
                    r.setConfigurable(res.isConfigurable());
                    r.setOptional(Boolean.valueOf(ort.isOptional()));
                    r.setOriginalId(res.getOriginalId());
                    r.setQualifier(ort.getQualifier());
                    r.setResourceTypeId(ort.getResourceTypeId());
                    r.setResourceType(res.getResourceType());
                    r.setAttributes(buildCombinedAttributeList(res.getAttributes(), ort.getAttribute()));
                    n.addResource(r);
                }
            }
        }
    }

    private static Map<String, String> buildCombinedAttributeList(Map<String, String> attr1, List<TypeAttribute> attr2) {
        Map<String, String> results = new HashMap<String, String>(0);
        results.putAll(attr1);
        for (TypeAttribute typAtt : attr2) {
            results.put(typAtt.getTypeRef(), typAtt.getValue());
        }
        return results;
    }


    /* Adds the Attributes to the Node */
    private static void addAttributes(CpfNode n, NodeType node) {
        for (TypeAttribute attr : node.getAttribute()) {
            n.addAttribute(attr.getTypeRef(), attr.getValue());
        }
    }

}
