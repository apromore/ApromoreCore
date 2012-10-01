package org.apromore.service.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.HardType;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.NonhumanType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfAndGateway;
import org.apromore.graph.JBPT.CpfEvent;
import org.apromore.graph.JBPT.CpfMessage;
import org.apromore.graph.JBPT.CpfNode;
import org.apromore.graph.JBPT.CpfOrGateway;
import org.apromore.graph.JBPT.CpfTask;
import org.apromore.graph.JBPT.CpfTimer;
import org.apromore.graph.JBPT.CpfXorGateway;
import org.apromore.graph.JBPT.ICpfAttribute;
import org.apromore.graph.JBPT.ICpfObject;
import org.apromore.graph.JBPT.ICpfResource;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GraphToCPFHelper. Used to help build and deconstruct a Graph from the CPF format.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class GraphToCPFHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphToCPFHelper.class);

    /* Private Constructor so it can't be instantiated */
    private GraphToCPFHelper() { }


    /**
     * Builds a Canonical Format Type from the graph.
     *
     * @param graph the cpf format from the canoniser.
     * @return the CPF Ty[pe from the Graph
     */
    public static CanonicalProcessType createCanonicalProcess(final CPF graph) {
        CanonicalProcessType c = new CanonicalProcessType();
        c.getAttribute().addAll(new ArrayList<TypeAttribute>(0));
        //c.getObject().addAll(new ArrayList<ObjectType>(0));
        c.getResourceType().addAll(new ArrayList<ResourceTypeType>(0));

        // Create the Nets / Nodes / Edges  - > return Objects, Resources
        buildNets(graph, c);
        addResourcesForCpf(findResourceList(graph), c);
        addPropertiesForCpf(graph, c);

        return c;
    }


    /* Build the Nets used in the Canonical Format from the graph. */
    private static void buildNets(final CPF graph, final CanonicalProcessType c) {
        NetType net = new NetType();
        net.setId(graph.getId());
        net.getObject().addAll(new ArrayList<ObjectType>(0));

        addObjectsForCpf(findObjectList(graph), net);
        for (FlowNode node : graph.getFlowNodes()) {
            if (node instanceof CpfNode) {
                buildNodesForNet((CpfNode) node, net);
            } else {
                LOGGER.warn("Unkown FlowNode type: {}", node.toString());
            }
        }
        for (ControlFlow<FlowNode> cf : graph.getControlFlow()) {
            buildEdgesForNet(cf, net);
        }
        c.getNet().add(net);
    }

    /* From the FlowNode, Build the Nodes used in the Canonical Format. */
    private static void buildNodesForNet(final CpfNode n, final NetType net) {
        if (n instanceof CpfMessage) {
            MessageType typ = new MessageType();
            typ.setName(n.getName());
            typ.setId(n.getId());
            typ.getAttribute().addAll(addAttributes(n));
            typ.getObjectRef().addAll(addObjectRef(n));
            typ.getResourceTypeRef().addAll(addResourceRef(n));
            net.getNode().add(typ);
        } else if (n instanceof CpfTimer) {
            TimerType typ = new TimerType();
            typ.setName(n.getName());
            typ.setId(n.getId());
            typ.getAttribute().addAll(addAttributes(n));
            typ.getObjectRef().addAll(addObjectRef(n));
            typ.getResourceTypeRef().addAll(addResourceRef(n));
            net.getNode().add(typ);
        } else if (n instanceof CpfEvent) {
            EventType typ = new EventType();
            typ.setName(n.getName());
            typ.setId(n.getId());
            typ.getAttribute().addAll(addAttributes(n));
            typ.getObjectRef().addAll(addObjectRef(n));
            typ.getResourceTypeRef().addAll(addResourceRef(n));
            net.getNode().add(typ);
        } else if (n instanceof CpfTask) {
            TaskType typ = new TaskType();
            typ.setName(n.getName());
            typ.setId(n.getId());
            typ.getAttribute().addAll(addAttributes(n));
            typ.getObjectRef().addAll(addObjectRef(n));
            typ.getResourceTypeRef().addAll(addResourceRef(n));
            net.getNode().add(typ);
        } else if (n instanceof CpfOrGateway) {
            RoutingType typ;
            if (((CpfOrGateway) n).isJoin()) {
                typ = new ORJoinType();
            } else {
                typ = new ORSplitType();
            }
            typ.setName("OR");
            typ.setId(n.getId());
            typ.getAttribute().addAll(addAttributes(n));
            net.getNode().add(typ);
        } else if (n instanceof CpfAndGateway) {
            RoutingType typ;
            if (((CpfAndGateway) n).isJoin()) {
                typ = new ANDJoinType();
            } else {
                typ = new ANDSplitType();
            }
            typ.setName("AND");
            typ.setId(n.getId());
            typ.getAttribute().addAll(addAttributes(n));
            net.getNode().add(typ);
        } else if (n instanceof CpfXorGateway) {
            RoutingType typ;
            if (((CpfXorGateway) n).isJoin()) {
                typ = new XORJoinType();
            } else {
                typ = new XORSplitType();
            }
            typ.setName("XOR");
            typ.setId(n.getId());
            typ.getAttribute().addAll(addAttributes(n));
            net.getNode().add(typ);
        } else if (n != null) {
            NodeType typ = new NodeType();
            typ.setName(n.getName());
            typ.setId(n.getId());
            net.getNode().add(typ);
        }
    }

    private static List<ICpfResource> findResourceList(final CPF graph) {
        List<ICpfResource> resources = new ArrayList<ICpfResource>(0);
        Collection<FlowNode> nodes = graph.getFlowNodes();
        for (FlowNode node : nodes) {
            if (node instanceof CpfNode) {
                resources.addAll(((CpfNode) node).getResource());
            }
        }
        return resources;
    }

    private static List<ICpfObject> findObjectList(final CPF graph) {
        List<ICpfObject> resources = new ArrayList<ICpfObject>(0);
        Collection<FlowNode> nodes = graph.getFlowNodes();
        for (FlowNode node : nodes) {
            if (node instanceof CpfNode) {
                resources.addAll(((CpfNode) node).getObjects());
            }
        }
        return resources;
    }

    private static Collection<? extends TypeAttribute> addAttributes(final CpfNode n) {
        TypeAttribute typAtt;
        List<TypeAttribute> atts = new ArrayList<TypeAttribute>(0);
        for (Entry<String, ICpfAttribute> att : n.getAttributes().entrySet()) {
            typAtt = new TypeAttribute();
            typAtt.setName(att.getKey());
            typAtt.setValue(att.getValue().getValue());
            typAtt.setAny(att.getValue().getAny());
        }
        return atts;
    }

    private static Collection<ObjectRefType> addObjectRef(final CpfNode n) {
        ObjectRefType object;
        List<ObjectRefType> objs = new ArrayList<ObjectRefType>(0);
        for (ICpfObject obj : n.getObjects()) {
            object = new ObjectRefType();
            object.setId(obj.getId());
            object.setObjectId(obj.getObjectId());
            object.setConsumed(obj.getConsumed());
            object.setOptional(obj.getOptional());
            object.setType(InputOutputType.valueOf(obj.getType()));
            object.getAttribute().addAll(buildAttributeList(obj.getAttributes()));
            objs.add(object);
        }
        return objs;
    }

    private static Collection<ResourceTypeRefType> addResourceRef(final CpfNode n) {
        ResourceTypeRefType resource;
        List<ResourceTypeRefType> ress = new ArrayList<ResourceTypeRefType>(0);
        for (ICpfResource res : n.getResource()) {
            resource = new ResourceTypeRefType();
            resource.setId(res.getId());
            resource.setQualifier(res.getQualifier());
            //TODO removed option in CPF schema
            //resource.setOptional(res.getOptional());
            resource.setResourceTypeId(res.getResourceTypeId());
            resource.getAttribute().addAll(buildAttributeList(res.getAttributes()));
            ress.add(resource);
        }
        return ress;
    }


    /* From the flowNodes but the Edges used in the Canonical Format */
    private static void buildEdgesForNet(final ControlFlow<FlowNode> edge, final NetType net) {
        EdgeType e = new EdgeType();
        e.setId(edge.getId());
        e.setSourceId(edge.getSource().getId());
        e.setTargetId(edge.getTarget().getId());
        net.getEdge().add(e);
    }

    /* Add objects for the Canonical Format */
    private static void addObjectsForCpf(final List<ICpfObject> cpfObject, final NetType net) {
        ObjectType typ;
        for (ICpfObject obj : cpfObject) {
            if (obj.getObjectType().equals(ICpfObject.ObjectType.HARD)) {
                typ = new HardType();
            } else {
                typ = new SoftType();
            }
            typ.setId(obj.getId());
            typ.setName(obj.getName());
            typ.getAttribute().addAll(buildAttributeList(obj.getAttributes()));
            net.getObject().add(typ);
        }
    }

    /* Add attributes for the Canonical Format */
    private static void addResourcesForCpf(final List<ICpfResource> cpfResource, final CanonicalProcessType c) {
        ResourceTypeType typ;
        for (ICpfResource obj : cpfResource) {
            if (obj.getResourceType().equals(ICpfResource.ResourceType.HUMAN)) {
                typ = new HumanType();
            } else {
                typ = new NonhumanType();
            }
            typ.setId(obj.getId());
            typ.setName(obj.getName());
            typ.setConfigurable(obj.isConfigurable());
            typ.setOriginalID(obj.getOriginalId());
            typ.getAttribute().addAll(buildAttributeList(obj.getAttributes()));

            c.getResourceType().add(typ);
        }
    }

    /* Add properties for the Canonical Format */
    private static void addPropertiesForCpf(final CPF graph, final CanonicalProcessType c) {
        TypeAttribute typeA;
        for (String propName : graph.getProperties().keySet()) {
            typeA = new TypeAttribute();
            typeA.setName(propName);
            typeA.setValue(graph.getProperty(propName).getValue());
            typeA.setAny(graph.getProperty(propName).getAny());
            c.getAttribute().add(typeA);
        }
    }


    private static List<TypeAttribute> buildAttributeList(final Map<String, ICpfAttribute> attributes) {
        TypeAttribute typAtt;
        List<TypeAttribute> atts = new ArrayList<TypeAttribute>(0);
        for (Entry<String, ICpfAttribute> e : attributes.entrySet()) {
            typAtt = new TypeAttribute();
            typAtt.setName(e.getKey());
            typAtt.setValue(e.getValue().getValue());
            typAtt.setAny(e.getValue().getAny());
            atts.add(typAtt);
        }
        return atts;
    }


}
