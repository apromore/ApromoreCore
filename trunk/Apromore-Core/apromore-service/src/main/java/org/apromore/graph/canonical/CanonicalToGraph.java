package org.apromore.graph.canonical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.common.Constants;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.util.FragmentUtil;

/**
 * GraphToCanonicalHelper. Used to help build and deconstruct a Graph from the CPF format.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CanonicalToGraph {

    /* private Constructor so it can't be instantiated */
    private CanonicalToGraph() { }


    /**
     * Builds a graph from the CPF XSD Format.
     * @param cpf the cpf format from the canoniser.
     * @return the JBpt CPF Graph representation
     */
    public static Canonical convert(final CanonicalProcessType cpf) {
        Canonical g = new Canonical();
        g.setName(cpf.getName());

        addProperties(cpf, g);
        List<IResource> res = buildResourcesList(cpf);
        buildNodeList(cpf, g, res);

        return g;
    }

    /* populate the Graph with attributes. */
    private static void addProperties(final CanonicalProcessType cpf, final Canonical g) {
        List<TypeAttribute> ats = cpf.getAttribute();
        for (TypeAttribute attr : ats) {
            g.setProperty(attr.getName(), attr.getValue(), attr.getAny());
        }
    }

    /* Add Resources to the Graph.  TODO: Missing the specialisedId's */
    private static List<IResource> buildResourcesList(final CanonicalProcessType cpf) {
        IResource rec;
        List<ResourceTypeType> rty = cpf.getResourceType();
        List<IResource> resources = new ArrayList<IResource>(0);
        for (ResourceTypeType resource : rty) {
            rec = new Resource();
            rec.setId(resource.getId());
            rec.setName(resource.getName());
            rec.setOriginalId(resource.getOriginalID());
            if (resource instanceof HumanType) {
                rec.setResourceType(IResource.ResourceTypeEnum.HUMAN);
            } else {
                rec.setResourceType(IResource.ResourceTypeEnum.NONHUMAN);
            }
            for (TypeAttribute type : resource.getAttribute()) {
                rec.addAttribute(type.getName(), type.getValue(), type.getAny());
            }
            resources.add(rec);
        }
        return resources;
    }


    /* Add a node to the graph, could be of any of the types */
    private static void buildNodeList(final CanonicalProcessType cpf, final Canonical g, final List<IResource> res) {
        Map<String, Node> flow = new HashMap<String, Node>(0);
        for (NetType net : cpf.getNet()) {
            List<IObject> obj = buildObjectsList(net);
            flow.putAll(buildNodeListFromNet(net.getNode(), res, obj));
            buildEdges(net.getEdge(), g, flow);
        }
    }

    /* Add Objects to the Graph */
    private static List<IObject> buildObjectsList(final NetType net) {
        IObject obj;
        List<ObjectType> obt = net.getObject();
        List<IObject> objs = new ArrayList<IObject>(0);
        for (ObjectType object : obt) {
            obj = new CanonicalObject();
            obj.setId(object.getId());
            obj.setName(object.getName());
            if (object instanceof SoftType) {
                obj.setObjectType(CanonicalObject.ObjectTypeEnum.SOFT);
            } else {
                obj.setObjectType(CanonicalObject.ObjectTypeEnum.HARD);
            }
            for (TypeAttribute type : object.getAttribute()) {
                obj.addAttribute(type.getName(), type.getValue(), type.getAny());
            }
            objs.add(obj);
        }
        return objs;
    }


    /* Build the Node list for a single Net */
    private static Map<String, Node> buildNodeListFromNet(final List<NodeType> nodes, final List<IResource> res, final List<IObject> obj) {
        Node output = null;
        Map<String, Node> flow = new HashMap<String, Node>(0);
        for (NodeType node : nodes) {
            if (node instanceof MessageType) {
                output = constructMessage(res, obj, flow, (MessageType) node);
            } else if (node instanceof TimerType) {
                output = constructTimer(res, obj, flow, (TimerType) node);
            } else if (node instanceof EventType) {
                output = constructEvent(res, obj, flow, (EventType) node);
            } else if (node instanceof TaskType) {
                output = constructTask(res, obj, flow, (TaskType) node);
            } else if (node instanceof StateType) {
                output = constructState(res, obj, flow, (StateType) node);
            } else if (node instanceof ORSplitType || node instanceof ORJoinType) {
                constructOrNode(flow, node);
            } else if (node instanceof XORSplitType || node instanceof XORJoinType) {
                constructXOrNode(flow, node);
            } else if (node instanceof ANDSplitType || node instanceof ANDJoinType) {
                constructAndNode(flow, node);
            }

            if (node instanceof WorkType) {
                addWorkExpressions((Work) output, (WorkType) node);
            }
        }
        return flow;
    }



    /* Builds the list of Edges for this Model */
    private static void buildEdges(final List<EdgeType> edgeTypes, final Canonical graph, final Map<String, Node> nodes) {
        Node source;
        Node target;
        Edge newEdge;

        int i = 0;
        for (EdgeType edge : edgeTypes) {
            source = nodes.get(edge.getSourceId());
            target = nodes.get(edge.getTargetId());
            if (source != null && target != null) {
                newEdge = graph.addEdge(source, target);
                if (newEdge != null) {
                    newEdge.setId(edge.getId());
                    newEdge.setOriginalId(edge.getOriginalID());
                    newEdge.setDefault(edge.isDefault());

                    if (edge.getConditionExpr() != null) {
                        Expression expr = new Expression();
                        expr.setDescription(edge.getConditionExpr().getDescription());
                        expr.setExpression(edge.getConditionExpr().getExpression());
                        expr.setLanguage(edge.getConditionExpr().getLanguage());
                        expr.setReturnType(edge.getConditionExpr().getReturnType());
                        newEdge.setConditionExpr(expr);
                    }

                    graph.setNodeProperty(source.getId(), Constants.TYPE, FragmentUtil.getType(source));
                    graph.setNodeProperty(target.getId(), Constants.TYPE, FragmentUtil.getType(target));
                }
            } else {
                System.out.println("One with either or both source and target is null");
            }
        }
    }

    /* Adds the Objects to the Node */
    private static void addObjects(final Node n, final WorkType node, final List<IObject> objs) {
        CanonicalObject o;
        for (ObjectRefType ort : node.getObjectRef()) {
            for (IObject obj : objs) {
                if (ort.getObjectId() != null && ort.getObjectId().equals(obj.getId())) {
                    o = new CanonicalObject();
                    o.setId(ort.getId());
                    o.setName(obj.getName());
                    o.setOptional(obj.getOptional());
                    o.setConsumed(obj.getConsumed());
                    o.setConfigurable(obj.isConfigurable());
                    o.setOriginalId(ort.getObjectId());
                    o.setObjectId(ort.getObjectId());
                    o.setType(ort.getType().toString());
                    o.setAttributes(buildCombinedAttributeList(obj.getAttributes(), ort.getAttribute()));
                    n.addObject(o);
                }
            }
        }
    }

    /* Adds the Resources to the Node */
    private static void addResources(final Node n, final WorkType node, final List<IResource> reses) {
        Resource r;
        for (ResourceTypeRefType ort : node.getResourceTypeRef()) {
            for (IResource res : reses) {
                if (ort.getResourceTypeId() != null && ort.getResourceTypeId().equals(res.getId())) {
                    r = new Resource();
                    r.setId(ort.getId());
                    r.setName(res.getId());
                    r.setConfigurable(res.isConfigurable());
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


    private static Map<String, IAttribute> buildCombinedAttributeList(final Map<String, IAttribute> map, final List<TypeAttribute> attr2) {
        Map<String, IAttribute> results = new HashMap<String, IAttribute>(0);
        results.putAll(map);
        for (TypeAttribute typAtt : attr2) {
            results.put(typAtt.getName(), new Attribute(typAtt.getValue(), typAtt.getAny()));
        }
        return results;
    }


    /* Adds the Attributes to the Node */
    private static void addAttributes(final Node n, final NodeType node) {
        for (TypeAttribute attr : node.getAttribute()) {
            n.addAttribute(attr.getName(), attr.getValue(), attr.getAny());
        }
    }

    /* Adds the Input Expressions to the Node */
    private static void addInputExpression(final Work n, final WorkType node) {
        Expression input;
        for (InputExpressionType inExpr : node.getInputExpr()) {
            input = new Expression();
            input.setExpression(inExpr.getExpression());
            input.setLanguage(inExpr.getLanguage());
            input.setDescription(inExpr.getDescription());
            input.setReturnType(inExpr.getReturnType());
            n.addInputExpr(input);
        }
    }

    /* Adds the Input Expressions to the Node */
    private static void addOutputExpression(final Work n, final WorkType node) {
        Expression output;
        for (OutputExpressionType outExpr : node.getOutputExpr()) {
            output = new Expression();
            output.setExpression(outExpr.getExpression());
            output.setLanguage(outExpr.getLanguage());
            output.setDescription(outExpr.getDescription());
            output.setReturnType(outExpr.getReturnType());
            n.addOutputExpr(output);
        }
    }


    private static void addCancelNodes(Work work, WorkType node) {
        for (CancellationRefType canType : node.getCancelNodeId()) {
            work.addCancelNode(canType.getRefId());
        }
    }

    private static void addCancelEdges(Work work, WorkType node) {
        for (CancellationRefType canType : node.getCancelNodeId()) {
            work.addCancelEdge(canType.getRefId());
        }
    }



    /* Add The expressions that are apart of the Work Node. */
    private static void addWorkExpressions(Work work, WorkType node) {
        if (node.isTeamWork() != null) {
            work.setTeamWork(node.isTeamWork());
        }
        if (node.getAllocationStrategy() != null) {
            work.setAllocation(IWork.AllocationStrategyEnum.fromValue(node.getAllocationStrategy().value()));
        }

        if (node.getFilterByDataExpr() != null) {
            Expression resDataExpr = new Expression();
            resDataExpr.setDescription(node.getFilterByDataExpr().getDescription());
            resDataExpr.setExpression(node.getFilterByDataExpr().getExpression());
            resDataExpr.setLanguage(node.getFilterByDataExpr().getLanguage());
            resDataExpr.setReturnType(node.getFilterByDataExpr().getReturnType());
            work.setResourceDataExpr(resDataExpr);
        }

        if (node.getFilterByRuntimeExpr() != null) {
            Expression resRunExpr = new Expression();
            resRunExpr.setDescription(node.getFilterByRuntimeExpr().getDescription());
            resRunExpr.setExpression(node.getFilterByRuntimeExpr().getExpression());
            resRunExpr.setLanguage(node.getFilterByRuntimeExpr().getLanguage());
            resRunExpr.setReturnType(node.getFilterByRuntimeExpr().getReturnType());
            work.setResourceDataExpr(resRunExpr);
        }

        addInputExpression(work, node);
        addOutputExpression(work, node);
        addCancelNodes(work, node);
        addCancelEdges(work, node);
    }


    private static State constructState(List<IResource> res, List<IObject> obj, Map<String, Node> flow, StateType node) {
        State state = new State(node.getName());
        state.setId(node.getId());
        state.setLabel(node.getName());
        state.setOriginalId(node.getOriginalID());
        if (node.isConfigurable() != null) {
            state.setConfigurable(node.isConfigurable());
        }

        addAttributes(state, node);
        flow.put(node.getId(), state);

        return state;
    }

    private static Task constructTask(List<IResource> res, List<IObject> obj, Map<String, Node> flow, TaskType node) {
        Task typ = new Task(node.getName());
        typ.setId(node.getId());
        typ.setLabel(node.getName());
        typ.setOriginalId(node.getOriginalID());
        if (node.isConfigurable() != null) {
            typ.setConfigurable(node.isConfigurable());
        }
        if (node.getSubnetId() != null) {
            typ.setSubNetId(Integer.parseInt(node.getSubnetId()));
            typ.setExternal(true);
        }

        addAttributes(typ, node);
        addObjects(typ, node, obj);
        addResources(typ, node, res);

        flow.put(node.getId(), typ);

        return typ;
    }

    private static Event constructEvent(List<IResource> res, List<IObject> obj, Map<String, Node> flow, EventType node) {
        Event typ = new Event(node.getName());
        typ.setId(node.getId());
        typ.setLabel(node.getName());
        typ.setOriginalId(node.getOriginalID());
        if (node.isConfigurable() != null) {
            typ.setConfigurable(node.isConfigurable());
        }

        addAttributes(typ, node);
        addObjects(typ, node, obj);
        addResources(typ, node, res);

        flow.put(node.getId(), typ);

        return typ;
    }

    private static Timer constructTimer(List<IResource> res, List<IObject> obj, Map<String, Node> flow, TimerType node) {
        Timer typ = new Timer(node.getName());
        typ.setId(node.getId());
        typ.setLabel(node.getName());
        typ.setOriginalId(node.getOriginalID());
        if (node.isConfigurable() != null) {
            typ.setConfigurable(node.isConfigurable());
        }
        if (node.getTimeDuration() != null) {
            typ.setTimeDuration(node.getTimeDuration().toString());
        }
        if (node.getTimeDate() != null) {
            typ.setTimeDate(node.getTimeDate().toGregorianCalendar());
        }

        if (node.getTimeExpression() != null) {
            Expression expr = new Expression();
            expr.setDescription(node.getTimeExpression().getDescription());
            expr.setExpression(node.getTimeExpression().getExpression());
            expr.setLanguage(node.getTimeExpression().getLanguage());
            expr.setReturnType(node.getTimeExpression().getReturnType());
            typ.setTimeExpression(expr);
        }

        addAttributes(typ, node);
        addObjects(typ, node, obj);
        addResources(typ, node, res);
        flow.put(node.getId(), typ);

        return typ;
    }

    private static Message constructMessage(List<IResource> res, List<IObject> obj, Map<String, Node> flow, MessageType node) {
        Message typ = new Message(node.getName());
        typ.setId(node.getId());
        typ.setLabel(node.getName());
        typ.setOriginalId(node.getOriginalID());
        if (node.isConfigurable() != null) {
            typ.setConfigurable(node.isConfigurable());
        }
        if (node.getDirection() != null) {
            typ.setDirection(IMessage.DirectionEnum.valueOf(node.getDirection().value()));
        }

        addAttributes(typ, node);
        addObjects(typ, node, obj);
        addResources(typ, node, res);

        flow.put(node.getId(), typ);

        return typ;
    }

    private static void constructAndNode(Map<String, Node> flow, NodeType node) {
        if (node instanceof ANDSplitType) {
            AndSplit andSplit = new AndSplit();
            andSplit.setId(node.getId());
            andSplit.setLabel(node.getName());
            andSplit.setOriginalId(node.getOriginalID());
            if (node.isConfigurable() != null) {
                andSplit.setConfigurable(node.isConfigurable());
            }
            addAttributes(andSplit, node);
            flow.put(node.getId(), andSplit);
        } else {
            AndJoin andJoin = new AndJoin();
            andJoin.setId(node.getId());
            andJoin.setLabel(node.getName());
            andJoin.setOriginalId(node.getOriginalID());
            if (node.isConfigurable() != null) {
                andJoin.setConfigurable(node.isConfigurable());
            }
            addAttributes(andJoin, node);
            flow.put(node.getId(), andJoin);
        }
    }

    private static void constructXOrNode(Map<String, Node> flow, NodeType node) {
        if (node instanceof XORSplitType) {
            XOrSplit xorSplit = new XOrSplit();
            xorSplit.setId(node.getId());
            xorSplit.setLabel(node.getName());
            xorSplit.setOriginalId(node.getOriginalID());
            if (node.isConfigurable() != null) {
                xorSplit.setConfigurable(node.isConfigurable());
            }
            addAttributes(xorSplit, node);
            flow.put(node.getId(), xorSplit);
        } else {
            XOrJoin xorJoin = new XOrJoin();
            xorJoin.setId(node.getId());
            xorJoin.setLabel(node.getName());
            xorJoin.setOriginalId(node.getOriginalID());
            if (node.isConfigurable() != null) {
                xorJoin.setConfigurable(node.isConfigurable());
            }
            addAttributes(xorJoin, node);
            flow.put(node.getId(), xorJoin);
        }
    }

    private static void constructOrNode(Map<String, Node> flow, NodeType node) {
        if (node instanceof ORSplitType) {
            OrSplit orSplit = new OrSplit();
            orSplit.setId(node.getId());
            orSplit.setLabel(node.getName());
            orSplit.setOriginalId(node.getOriginalID());
            if (node.isConfigurable() != null) {
                orSplit.setConfigurable(node.isConfigurable());
            }
            addAttributes(orSplit, node);
            flow.put(node.getId(), orSplit);
        } else {
            OrJoin orJoin = new OrJoin();
            orJoin.setId(node.getId());
            orJoin.setLabel(node.getName());
            orJoin.setOriginalId(node.getOriginalID());
            if (node.isConfigurable() != null) {
                orJoin.setConfigurable(node.isConfigurable());
            }
            addAttributes(orJoin, node);
            flow.put(node.getId(), orJoin);
        }
    }

}
