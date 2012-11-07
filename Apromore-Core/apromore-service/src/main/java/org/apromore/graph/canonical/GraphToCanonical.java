package org.apromore.graph.canonical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.DirectionEnum;
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
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerExpressionType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GraphToCanonicalHelper. Used to help build and deconstruct a Graph from the CPF format.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class GraphToCanonical {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphToCanonical.class);

    /* private Constructor so it can't be instantiated */
    private GraphToCanonical() { }


    /**
     * Builds a Canonical Format Type from the graph.
     * @param graph the cpf format from the canoniser.
     * @return the CPF Ty[pe from the Graph
     */
    public static CanonicalProcessType convert(final Canonical graph) {
        CanonicalProcessType c = new CanonicalProcessType();

        c.setName(graph.getName());
        c.setUri(graph.getVersion());
        c.setVersion(graph.getVersion());
        c.setAuthor(graph.getAuthor());
        c.setCreationDate(graph.getCreationDate());
        c.setModificationDate(graph.getModifiedDate());

        c.getAttribute().addAll(new ArrayList<TypeAttribute>(0));
        c.getResourceType().addAll(new ArrayList<ResourceTypeType>(0));

        // Create the Nets / Nodes / Edges - > return Objects, Resources
        buildNets(graph, c);
        addResourcesForCpf(constructResourceList(graph), c);
        addPropertiesForCanonical(graph, c);

        return c;
    }


    /* Build the Nets used in the Canonical Format from the graph. */
    private static void buildNets(final Canonical graph, final CanonicalProcessType c) {
        NetType net = new NetType();
        net.setId(graph.getId());
        net.getObject().addAll(new ArrayList<ObjectType>(0));

        addObjectsForCpf(constructObjectList(graph), net);
        for (Node node : graph.getNodes()) {
            buildNodesForNet(node, net);
        }
        for (Edge cf : graph.getEdges()) {
            buildEdgesForNet(cf, net);
        }
        c.getNet().add(net);
    }

    /* From the FlowNode, Build the Nodes used in the Canonical Format. */
    private static void buildNodesForNet(final Node n, final NetType net) {
        if (n instanceof Message) {
            constructMessage((Message) n, net);
        } else if (n instanceof Timer) {
            constructTimer((Timer) n, net);
        } else if (n instanceof Event) {
            constructEvent((Event) n, net);
        } else if (n instanceof Task) {
            constructTask((Task) n, net);
        } else if (n instanceof State) {
            constructState((State) n, net);
        } else if (n instanceof OrJoin || n instanceof OrSplit) {
            constructOrNode(n, net);
        } else if (n instanceof XOrJoin || n instanceof XOrSplit) {
            constructXOrNode(n, net);
        } else if (n instanceof AndJoin || n instanceof AndSplit) {
            constructAndNode(n, net);
        } else if (n != null) {
            NodeType typ = new NodeType();
            typ.setName(n.getName());
            typ.setId(n.getId());
            net.getNode().add(typ);
        }
    }


    private static List<IResource> constructResourceList(final Canonical graph) {
        List<IResource> resources = new ArrayList<IResource>(0);
        Collection<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            resources.addAll((node).getResource());
        }
        return resources;
    }

    private static Collection<IObject> constructObjectList(final Canonical graph) {
        Map<String, IObject> objects = new HashMap<String, IObject>(0);
        Collection<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            for (IObject obj : node.getObjects()) {
                if (!objects.containsKey(obj.getOriginalId())) {
                    objects.put(obj.getOriginalId(), obj);
                }
            }
        }
        return objects.values();
    }

    private static Collection<? extends TypeAttribute> addAttributes(final Node n) {
        TypeAttribute typAtt;
        List<TypeAttribute> atts = new ArrayList<TypeAttribute>(0);
        for (Entry<String, IAttribute> att : n.getAttributes().entrySet()) {
            typAtt = new TypeAttribute();
            typAtt.setName(att.getKey());
            typAtt.setValue(att.getValue().getValue());
            typAtt.setAny(att.getValue().getAny());
        }
        return atts;
    }

    private static Collection<ObjectRefType> addObjectRef(final Node n) {
        ObjectRefType object;
        List<ObjectRefType> objs = new ArrayList<ObjectRefType>(0);
        for (IObject obj : n.getObjects()) {
            object = new ObjectRefType();
            object.setId(obj.getId());
            object.setObjectId(obj.getObjectId());
            object.setConsumed(obj.getConsumed());
            object.setOptional(obj.getOptional());
            if (obj.getType() != null) {
                object.setType(InputOutputType.valueOf(obj.getType()));
            }
            if (obj.getAttributes() != null && !obj.getAttributes().isEmpty()) {
                object.getAttribute().addAll(buildAttributeList(obj.getAttributes()));
            }
            objs.add(object);
        }
        return objs;
    }

    private static Collection<ResourceTypeRefType> addResourceRef(final Node n) {
        ResourceTypeRefType resource;
        List<ResourceTypeRefType> ress = new ArrayList<ResourceTypeRefType>(0);
        for (IResource res : n.getResource()) {
            resource = new ResourceTypeRefType();
            resource.setId(res.getId());
            resource.setQualifier(res.getQualifier());
            resource.setResourceTypeId(res.getResourceTypeId());
            resource.getAttribute().addAll(buildAttributeList(res.getAttributes()));
            ress.add(resource);
        }
        return ress;
    }


    /* From the flowNodes but the Edges used in the Canonical Format */
    private static void buildEdgesForNet(final Edge edge, final NetType net) {
        EdgeType e = new EdgeType();
        e.setId(edge.getId());
        e.setSourceId(edge.getSource().getId());
        e.setTargetId(edge.getTarget().getId());
        net.getEdge().add(e);
    }

    /* Add objects for the Canonical Format */
    private static void addObjectsForCpf(final Collection<IObject> cpfObject, final NetType net) {
        ObjectType typ;
        for (IObject obj : cpfObject) {
            if (obj.getObjectType() != null) {
                if (obj.getObjectType().equals(IObject.ObjectTypeEnum.HARD)) {
                    typ = new HardType();
                } else {
                    typ = new SoftType();
                }
            } else {
                typ = new ObjectType();
            }
            typ.setId(obj.getId());
            typ.setOriginalID(obj.getOriginalId());
            typ.setName(obj.getName());
            typ.getAttribute().addAll(buildAttributeList(obj.getAttributes()));

            net.getObject().add(typ);
        }
    }

    /* Add attributes for the Canonical Format */
    private static void addResourcesForCpf(final List<IResource> cpfResource, final CanonicalProcessType c) {
        ResourceTypeType typ;
        for (IResource obj : cpfResource) {
            if (obj.getResourceType().equals(IResource.ResourceTypeEnum.HUMAN)) {
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
    private static void addPropertiesForCanonical(final Canonical graph, final CanonicalProcessType c) {
        TypeAttribute typeA;
        for (String propName : graph.getProperties().keySet()) {
            typeA = new TypeAttribute();
            typeA.setName(propName);
            typeA.setValue(graph.getProperty(propName).getValue());
            typeA.setAny(graph.getProperty(propName).getAny());
            c.getAttribute().add(typeA);
        }
    }


    private static List<TypeAttribute> buildAttributeList(final Map<String, IAttribute> attributes) {
        TypeAttribute typAtt;
        List<TypeAttribute> atts = new ArrayList<TypeAttribute>(0);
        for (Entry<String, IAttribute> e : attributes.entrySet()) {
            typAtt = new TypeAttribute();
            typAtt.setName(e.getKey());
            typAtt.setValue(e.getValue().getValue());
            typAtt.setAny(e.getValue().getAny());
            atts.add(typAtt);
        }
        return atts;
    }


    private static void constructState(final State n, final NetType net) {
        StateType state = new StateType();
        state.setName(n.getName());
        state.setId(n.getId());
        state.setOriginalID(n.getOriginalId());
        state.setConfigurable(n.isConfigurable());
        net.getNode().add(state);
    }

    private static void constructTask(final Task n, final NetType net) {
        TaskType typ = new TaskType();
        typ.setName(n.getName());
        typ.setId(n.getId());
        typ.setOriginalID(n.getOriginalId());
        typ.setConfigurable(n.isConfigurable());
        typ.setSubnetId(String.valueOf(n.getSubNetId()));

        typ.getAttribute().addAll(addAttributes(n));
        typ.getObjectRef().addAll(addObjectRef(n));
        typ.getResourceTypeRef().addAll(addResourceRef(n));
        net.getNode().add(typ);
    }

    private static void constructEvent(final Event n, final NetType net) {
        EventType typ = new EventType();
        typ.setName(n.getName());
        typ.setId(n.getId());
        typ.setOriginalID(n.getOriginalId());
        typ.setConfigurable(n.isConfigurable());

        typ.getAttribute().addAll(addAttributes(n));
        typ.getObjectRef().addAll(addObjectRef(n));
        typ.getResourceTypeRef().addAll(addResourceRef(n));
        net.getNode().add(typ);
    }

    private static void constructTimer(final Timer n, final NetType net) {
        TimerType typ = new TimerType();
        typ.setName(n.getName());
        typ.setId(n.getId());
        typ.setOriginalID(n.getOriginalId());
        typ.setConfigurable(n.isConfigurable());

        if (n.getTimeDuration() != null) {
            try {
                typ.setTimeDuration(DatatypeFactory.newInstance().newDuration(n.getTimeDuration()));
            } catch (DatatypeConfigurationException e) {
                LOGGER.error("Failed to parse the Timer Duration field.");
            }
        }
        if (n.getTimeDate() != null) {
            try {
                typ.setTimeDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(n.getTimeDate()));
            } catch (DatatypeConfigurationException e) {
                LOGGER.error("Failed to parse the Timer timeDate field.");
            }
        }

        if (n.getTimeExpression() != null) {
            TimerExpressionType expr = new TimerExpressionType();
            expr.setDescription(n.getTimeExpression().getDescription());
            expr.setExpression(n.getTimeExpression().getExpression());
            expr.setLanguage(n.getTimeExpression().getLanguage());
            expr.setReturnType(n.getTimeExpression().getReturnType());
            typ.setTimeExpression(expr);
        }

        typ.getAttribute().addAll(addAttributes(n));
        typ.getObjectRef().addAll(addObjectRef(n));
        typ.getResourceTypeRef().addAll(addResourceRef(n));
        net.getNode().add(typ);
    }

    private static void constructMessage(final Message n, final NetType net) {
        MessageType typ = new MessageType();
        typ.setName(n.getName());
        typ.setId(n.getId());
        typ.setOriginalID(n.getOriginalId());
        typ.setConfigurable(n.isConfigurable());
        if (n.getDirection() != null) {
            typ.setDirection(DirectionEnum.valueOf(n.getDirection().name()));
        }

        typ.getAttribute().addAll(addAttributes(n));
        typ.getObjectRef().addAll(addObjectRef(n));
        typ.getResourceTypeRef().addAll(addResourceRef(n));
        net.getNode().add(typ);
    }

    private static void constructXOrNode(final Node n, final NetType net) {
        RoutingType typ;
        if (n instanceof XOrJoin) {
            typ = new XORJoinType();
        } else {
            typ = new XORSplitType();
        }
        typ.setName(n.getName());
        typ.setId(n.getId());
        typ.setOriginalID(n.getOriginalId());
        typ.setConfigurable(n.isConfigurable());
        typ.getAttribute().addAll(addAttributes(n));
        net.getNode().add(typ);
    }

    private static void constructAndNode(final Node n, final NetType net) {
        RoutingType typ;
        if (n instanceof AndJoin) {
            typ = new ANDJoinType();
        } else {
            typ = new ANDSplitType();
        }
        typ.setName(n.getName());
        typ.setId(n.getId());
        typ.setOriginalID(n.getOriginalId());
        typ.setConfigurable(n.isConfigurable());
        typ.getAttribute().addAll(addAttributes(n));
        net.getNode().add(typ);
    }

    private static void constructOrNode(final Node n, final NetType net) {
        RoutingType typ;
        if (n instanceof OrJoin) {
            typ = new ORJoinType();
        } else {
            typ = new ORSplitType();
        }
        typ.setName(n.getName());
        typ.setId(n.getId());
        typ.setOriginalID(n.getOriginalId());
        typ.setConfigurable(n.isConfigurable());
        typ.getAttribute().addAll(addAttributes(n));
        net.getNode().add(typ);
    }
}
