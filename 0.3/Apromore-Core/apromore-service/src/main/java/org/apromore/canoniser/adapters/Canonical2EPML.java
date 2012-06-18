/**
 * Canonical2EPML is a class for converting an CanonicalProcessType
 *  object into a TypeEPML object.
 * A Canonical2EPML object encapsulates the state of the main
 * component resulted from the canonization process.  This
 * state information includes the TypeEpml object which hold a header
 * for the rest of the EPML elements.
 * <p>
 *
 * @author Abdul
 * @version     %I%, %G%
 * @since 1.0
 */

package org.apromore.canoniser.adapters;

import de.epml.TEpcElement;
import de.epml.TExtensibleElements;
import de.epml.TypeAND;
import de.epml.TypeArc;
import de.epml.TypeDefinition;
import de.epml.TypeDefinitions;
import de.epml.TypeDirectory;
import de.epml.TypeEPC;
import de.epml.TypeEPML;
import de.epml.TypeEvent;
import de.epml.TypeFill;
import de.epml.TypeFlow;
import de.epml.TypeFont;
import de.epml.TypeFunction;
import de.epml.TypeGraphics;
import de.epml.TypeLine;
import de.epml.TypeMove;
import de.epml.TypeMove2;
import de.epml.TypeOR;
import de.epml.TypeObject;
import de.epml.TypePosition;
import de.epml.TypeProcessInterface;
import de.epml.TypeRelation;
import de.epml.TypeRole;
import de.epml.TypeToProcess;
import de.epml.TypeXOR;
import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;

import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Canonical2EPML {
    Map<String, BigInteger> id_map = new HashMap<String, BigInteger>();
    List<String> event_list = new LinkedList<String>();
    Map<String, NodeType> nodeRefMap = new HashMap<String, NodeType>();
    Map<String, EdgeType> edgeRefMap = new HashMap<String, EdgeType>();
    Map<BigInteger, Object> epcRefMap = new HashMap<BigInteger, Object>();
    Map<String, ObjectRefType> objectRefMap = new HashMap<String, ObjectRefType>();
    List<TEpcElement> eventFuncList = new LinkedList<TEpcElement>();
    List<String> object_res_list = new LinkedList<String>();
    Map<BigInteger, List<String>> role_map = new HashMap<BigInteger, List<String>>();
    List<TypeFunction> subnet_list = new LinkedList<TypeFunction>();
    List<TypeProcessInterface> pi_list = new LinkedList<TypeProcessInterface>();
    List<Object> temp_list = new LinkedList<Object>();

    List<TypeFlow> flow_list = new LinkedList<TypeFlow>();

    private TypeEPML epml = new TypeEPML();
    private TypeDirectory dir = new TypeDirectory();
    private long ids = System.currentTimeMillis();
    ;
    private long defIds = 1;

    public TypeEPML getEPML() {
        return epml;
    }

    /**
     * Validating EPCs modelass against the Event-Function rule. The fake functions and events
     * will be added as needed. The algorithm will also minimized them as much as possible.
     * It verifies the functions and events elements one by one until the last element.
     * This method will only be called if the addFakes boolean value is defined and true.
     * <p/>
     *
     * @param epc the header for an EPCs modelass
     * @since 1.0
     */
    private void validate_model(TypeEPC epc) {
        List<TEpcElement> successors = new LinkedList<TEpcElement>();
        int events, funcs;
        for (Object obj : epc.getEventOrFunctionOrRole())
            if (obj instanceof TypeFunction || obj instanceof TypeEvent)
                eventFuncList.add((TEpcElement) obj);

        for (TEpcElement element : eventFuncList) {
            if (element instanceof TypeEvent) {
                successors = retrieve_successors(element, epc);
                events = funcs = 0;
                for (TEpcElement obj : successors)
                    if (obj instanceof TypeFunction)
                        funcs++;
                    else
                        events++;
                if (events == 0 && funcs == 0) {
                    // Do Nothing
                } else if (events == 0 && funcs != 0) {
                    // DO NOTHING
                } else if (funcs == 0 && events != 0) {
                    // Add fake function after the current event
                    TypeFunction func = new TypeFunction();
                    TypeArc arc1, arc2 = new TypeArc();
                    TypeFlow flow = new TypeFlow();
                    arc2.setFlow(flow);

                    arc1 = find_post_arc(element, epc);
                    add_fake(arc1, func, arc2, epc);

                } else {
                    if (events > funcs) {
                        //Add fake function after the current event
                        TypeFunction func = new TypeFunction();
                        TypeArc arc1, arc2 = new TypeArc();
                        TypeFlow flow = new TypeFlow();
                        arc2.setFlow(flow);

                        arc1 = find_pre_arc(element, epc);
                        add_fake(arc1, func, arc2, epc);

                        //Add fake event before each successor function
                        for (TEpcElement tepc : successors) {
                            if (tepc instanceof TypeFunction) {
                                TypeEvent event = new TypeEvent();
                                TypeArc arc11, arc22 = new TypeArc();
                                TypeFlow flow1 = new TypeFlow();
                                arc22.setFlow(flow1);

                                arc11 = find_pre_arc(element, epc);
                                add_fake(arc11, event, arc22, epc);
                            }
                        }
                    } else {
                        // Add fake function before each successor event
                        for (TEpcElement tepc : successors) {
                            if (tepc instanceof TypeEvent) {
                                TypeFunction func = new TypeFunction();
                                TypeArc arc11, arc22 = new TypeArc();
                                TypeFlow flow1 = new TypeFlow();
                                arc22.setFlow(flow1);

                                arc11 = find_pre_arc(element, epc);
                                add_fake(arc11, func, arc22, epc);
                            }
                        }
                    }
                }

            } else if (element instanceof TypeFunction) {
                successors = retrieve_successors((TEpcElement) element, epc);
                events = funcs = 0;
                for (TEpcElement obj : successors)
                    if (obj instanceof TypeFunction)
                        funcs++;
                    else
                        events++;

                if (funcs == 0 && events == 0) {
                    // Do Nothing
                }
                if (funcs == 0) {
                    // DO NOTHING
                } else if (events == 0) {
                    // Add fake event after the current function
                    TypeEvent event = new TypeEvent();
                    TypeArc arc1, arc2 = new TypeArc();
                    TypeFlow flow = new TypeFlow();
                    arc2.setFlow(flow);

                    arc1 = find_post_arc(element, epc);
                    add_fake(arc1, event, arc2, epc);
                } else {
                    if (funcs > events) {
                        //Add fake event after the current function
                        TypeEvent event = new TypeEvent();
                        TypeArc arc1, arc2 = new TypeArc();
                        TypeFlow flow = new TypeFlow();
                        arc2.setFlow(flow);

                        arc1 = find_post_arc(element, epc);
                        add_fake(arc1, event, arc2, epc);

                        //Add fake function before each successor event
                        for (TEpcElement tepc : successors) {
                            if (tepc instanceof TypeEvent) {
                                TypeFunction func = new TypeFunction();
                                TypeArc arc11, arc22 = new TypeArc();
                                TypeFlow flow1 = new TypeFlow();
                                arc22.setFlow(flow1);

                                arc11 = find_pre_arc(element, epc);
                                add_fake(arc11, func, arc22, epc);
                            }
                        }

                    } else {
                        // Add fake event before each successor function
                        for (TEpcElement tepc : successors) {
                            if (tepc instanceof TypeFunction) {
                                TypeEvent event = new TypeEvent();
                                TypeArc arc11, arc22 = new TypeArc();
                                TypeFlow flow1 = new TypeFlow();
                                arc22.setFlow(flow1);

                                arc11 = find_pre_arc(element, epc);
                                add_fake(arc11, event, arc22, epc);
                            }
                        }
                    }
                }

            }
        }

    }

    /**
     * Adds a fake element between the received two arcs.
     * <p/>
     *
     * @param epc the header for an EPCs modelass
     *            element
     *            arc1
     *            arc2
     * @since 1.0
     */
    private void add_fake(TypeArc arc1,
                          TEpcElement element, TypeArc arc2, TypeEPC epc) {

        element.setId(BigInteger.valueOf(ids++));
        TExtensibleElements ex;
        QName typeRef = new QName("typeRef");
        element.getOtherAttributes().put(typeRef, "fake");
        element.setName("");
        arc2.setId(BigInteger.valueOf(ids++));
        arc2.getFlow().setSource(element.getId());
        arc2.getFlow().setTarget(arc1.getFlow().getTarget());
        arc1.getFlow().setTarget(element.getId());

        epc.getEventOrFunctionOrRole().add(element);
        epc.getEventOrFunctionOrRole().add(arc2);
    }

    /**
     * It take two parameters and returns the previous arc element for
     * the received EPC element if it is exist.
     * <p/>
     *
     * @param epc the header for an EPCs modelass
     *            element   the element from this epc intended to retrieve its previous arc
     * @since 1.0
     */
    private TypeArc find_pre_arc(TEpcElement element, TypeEPC epc) {
        for (Object obj : epc.getEventOrFunctionOrRole()) {
            if (obj != null &&
                    ((TypeArc) obj).getFlow() != null &&
                    ((TypeArc) obj).getFlow().getTarget() != null
                    && element != null)
                if (obj instanceof TypeArc)
                    if (((TypeArc) obj).getFlow().getTarget()
                            .equals(element.getId()))
                        return (TypeArc) obj;
        }
        return null;
    }

    /**
     * It take two parameters and returns the post arc element for
     * the received EPC element if it is exist.
     * <p/>
     *
     * @param epc the header for an EPCs modelass
     *            element   the element from this epc intended to retrieve its post arc
     * @since 1.0
     */
    private TypeArc find_post_arc(TEpcElement element, TypeEPC epc) {
        for (Object obj : epc.getEventOrFunctionOrRole()) {
            if (obj != null && obj instanceof TypeArc &&
                    ((TypeArc) obj).getFlow() != null &&
                    ((TypeArc) obj).getFlow().getSource() != null
                    && element != null)
                if (obj instanceof TypeArc)
                    if (((TypeArc) obj).getFlow().getSource()
                            .equals(element.getId()))
                        return (TypeArc) obj;
        }
        return null;
    }

    /**
     * It take two parameters and returns all the successors elements for
     * the received EPC element.
     * <p/>
     *
     * @param epc the header for an EPCs modelass
     *            element   the element from this epc intended to retrieve its successors
     * @since 1.0
     */
    private List<TEpcElement> retrieve_successors(TEpcElement element, TypeEPC epc) {
        List<Object> elements = new LinkedList<Object>();
        List<TEpcElement> successors = new LinkedList<TEpcElement>();
        elements.add(element);
        boolean flag = false;

        while (!elements.isEmpty()) {
            Object obj = elements.get(0);
            elements.remove(obj);
            if (flag && (obj instanceof TypeEvent || obj instanceof TypeFunction)) {
                successors.add((TEpcElement) obj);
            } else {
                flag = true;
                for (Object object : epc.getEventOrFunctionOrRole())
                    if (object instanceof TypeArc) {
                        if (((TypeArc) object).getFlow() != null) {
                            TypeFlow flow = ((TypeArc) object).getFlow();
                            if (flow.getSource().equals(((TEpcElement) obj).getId())) {
                                elements.add(epcRefMap.get(flow.getTarget()));
                            }
                        }
                    }
            }

        }

        return successors;
    }

    /**
     * Constructor for de-canonizing CPF & ANF files. The fake
     * feature will be set to false as a default value.
     * <p/>
     *
     * @param cproc the header for a CPF modelass
     *              annotations       the header for an ANF modelass
     * @throws
     * @since 1.0
     */
    public Canonical2EPML(CanonicalProcessType cproc, AnnotationsType annotations) {
        main(cproc, false);
        mapNodeAnnotations(annotations);
        mapEdgeAnnotations(annotations);
    }

    /**
     * Constructor for de-canonizing CPF file without
     * annotations. The fake feature will be set to false
     * as a default value.
     * <p/>
     *
     * @param cproc the header for a CPF modelass
     * @throws
     * @since 1.0
     */
    public Canonical2EPML(CanonicalProcessType cproc) {
        main(cproc, false);
    }


    /**
     * Constructor for de-canonizing CPF & ANF files.
     * <p/>
     *
     * @param cproc the header for a CPF modelass
     *              annotations       the header for an ANF modelass
     *              addFake           Boolean value to either add fake elements or not.
     * @throws
     * @since 1.0
     */
    public Canonical2EPML(CanonicalProcessType cproc, AnnotationsType annotations, boolean addFakes) {
        main(cproc, addFakes);
        mapNodeAnnotations(annotations);
        mapEdgeAnnotations(annotations);
    }

    /**
     * Constructor for de-canonizing CPF file without annotation.
     * <p/>
     *
     * @param cproc the header for a CPF modelass
     *              addFake           Boolean value to either add fake elements or not.
     * @throws
     * @since 1.0
     */
    public Canonical2EPML(CanonicalProcessType cproc, boolean addFakes) {
        main(cproc, addFakes);
    }

    /**
     * This main method to be reused by all the constructors
     * for all cases.
     * <p/>
     *
     * @param cproc the header for a CPF modelass
     *              addFake           Boolean value to either add fake elements or not.
     * @since 1.0
     */
    private void main(CanonicalProcessType cproc, boolean addFakes) {
        epml.getDirectory().add(dir);
        epml.setDefinitions(new TypeDefinitions());

        for (NetType net : cproc.getNet()) {
            TypeEPC epc = new TypeEPC();
            epc.setEpcId(BigInteger.valueOf(ids++));
            epc.setName(" ");
            translateNet(epc, net);
            for (ObjectType obj : cproc.getObject()) {
                if (object_res_list.contains(obj.getId()))
                    translateObject(obj, epc);
            }
            for (ResourceTypeType resT : cproc.getResourceType()) {
                if (object_res_list.contains(resT.getId()))
                    translateResource(resT, epc);
            }
            createRelationArc(epc, net);
            object_res_list.clear();
            if (addFakes)
                validate_model(epc);
            epml.getDirectory().get(0).getEpcOrDirectory().add(epc);
        }

        for (TypeFunction func : subnet_list)
            func.getToProcess().setLinkToEpcId(id_map.get(func.getToProcess().getLinkToEpcId()));
        for (TypeProcessInterface pi : pi_list)
            pi.getToProcess().setLinkToEpcId(id_map.get(pi.getToProcess().getLinkToEpcId()));
    }

    private void translateNet(TypeEPC epc, NetType net) {
        for (NodeType node : net.getNode()) {
            if (node instanceof TaskType || node instanceof EventType) {
                if (node instanceof TaskType) {
                    translateTask(epc, (TaskType) node);
                } else if (node instanceof EventType) {
                    translateEvent(epc, node);
                }
                for (ObjectRefType ref : ((WorkType) node).getObjectRef()) {
                    object_res_list.add(ref.getObjectId());
                }
                List<String> ll = new LinkedList<String>();
                for (ResourceTypeRefType ref : ((WorkType) node).getResourceTypeRef()) {
                    object_res_list.add(ref.getResourceTypeId());
                    ll.add(ref.getResourceTypeId());
                }
                role_map.put(epc.getEpcId(), ll);

            } else if (node instanceof RoutingType) {
                translateGateway(epc, node);
            }
            nodeRefMap.put(node.getId(), node);
        }

        createEvent(epc, net);

        for (EdgeType edge : net.getEdge()) {
            boolean flag = true;
            if (edge.getCondition() == null)
                flag = true;
            else if (edge.getCondition().equals("EPMLEPML"))
                flag = false;

            edgeRefMap.put(edge.getId(), edge);
            if (flag) {

                if (id_map.get(edge.getTargetId()) != null) {
                    TypeArc arc = new TypeArc();
                    TypeFlow flow = new TypeFlow();
                    id_map.put(edge.getId(), BigInteger.valueOf(ids));
                    arc.setId(BigInteger.valueOf(ids++));
                    flow.setSource(id_map.get(edge.getSourceId()));
                    flow.setTarget(id_map.get(edge.getTargetId()));
                    flow_list.add(flow);
                    arc.setFlow(flow);
                    epc.getEventOrFunctionOrRole().add(arc);
                    epcRefMap.put(arc.getId(), arc);
                } else {
                    id_map.put(edge.getTargetId(), id_map.get(edge.getSourceId()));
                }

            }
        }


    }

    private void createRelationArc(TypeEPC epc, NetType net) {
        for (NodeType node : net.getNode()) {
            if (node instanceof WorkType) {
                for (ObjectRefType ref : ((WorkType) node).getObjectRef()) {
                    if (ref.getObjectId() != null) {
                        TypeArc arc = new TypeArc();
                        TypeRelation rel = new TypeRelation();
                        arc.setId(BigInteger.valueOf(ids++));
                        if (ref.getType().equals(InputOutputType.OUTPUT)) {
                            rel.setSource(id_map.get(node.getId()));
                            rel.setTarget(id_map.get(ref.getObjectId()));
                            BigInteger id = id_map.get(ref.getObjectId());
                            TypeObject o = (TypeObject) epcRefMap.get(id);
                            o.setType("output");
                        } else {
                            rel.setTarget(id_map.get(node.getId()));
                            rel.setSource(id_map.get(ref.getObjectId()));
                            BigInteger id = id_map.get(ref.getObjectId());
                            TypeObject o = (TypeObject) epcRefMap.get(id);
                            o.setType("input");
                        }
                        for (TypeAttribute att : ref.getAttribute())
                            if (att.getTypeRef().equals("RefID")) {
                                String l = att.getValue();
                                objectRefMap.put(l, ref);
                                id_map.put(l, arc.getId());
                            }
                        arc.setRelation(rel);
                        epcRefMap.put(arc.getId(), arc);
                        epc.getEventOrFunctionOrRole().add(arc);
                    }
                }

                for (ResourceTypeRefType ref : ((WorkType) node).getResourceTypeRef()) {
                    if (ref.getResourceTypeId() != null) {
                        TypeArc arc = new TypeArc();
                        TypeRelation rel = new TypeRelation();
                        arc.setId(BigInteger.valueOf(ids++));
                        rel.setSource(id_map.get(node.getId()));
                        rel.setTarget(id_map.get(ref.getResourceTypeId()));
                        rel.setType("role");
                        arc.setRelation(rel);
                        epc.getEventOrFunctionOrRole().add(arc);
                    }
                }
            }
        }
    }

    private void translateTask(TypeEPC epc, TaskType task) {
        if (task.getName() == null && task.getSubnetId() != null) {
            TypeProcessInterface pi = new TypeProcessInterface();
            pi.setToProcess(new TypeToProcess());
            pi.getToProcess().setLinkToEpcId(new BigInteger(task.getSubnetId()));
            pi_list.add(pi);
        } else {
            TypeFunction func = new TypeFunction();
            id_map.put(task.getId(), BigInteger.valueOf(ids));
            func.setId(BigInteger.valueOf(ids++));
            func.setName(task.getName());
            func.setDefRef(find_def_id("function", func.getName()));
            if (task.getSubnetId() != null) {
                func.getToProcess().setLinkToEpcId(new BigInteger(task.getSubnetId()));
                subnet_list.add(func);
            }
            epc.getEventOrFunctionOrRole().add(func);
            epcRefMap.put(func.getId(), func);
        }
    }

    private void translateEvent(TypeEPC epc, NodeType node) {
        TypeEvent event = new TypeEvent();
        id_map.put(node.getId(), BigInteger.valueOf(ids));
        event.setId(BigInteger.valueOf(ids++));
        event.setName(node.getName());
        event.setDefRef(find_def_id("event", event.getName()));
        epc.getEventOrFunctionOrRole().add(event);
        epcRefMap.put(event.getId(), event);

    }

    private void translateObject(ObjectType obj, TypeEPC epc) {
        TypeObject object = new TypeObject();
        id_map.put(obj.getId(), BigInteger.valueOf(ids));
        object.setId(BigInteger.valueOf(ids++));
        object.setName(obj.getName());
        object.setDefRef(find_def_id("object", object.getName()));
        object.setFinal(obj.isConfigurable());
        epc.getEventOrFunctionOrRole().add(object);
        epcRefMap.put(object.getId(), object);
    }

    private void translateResource(ResourceTypeType resT, TypeEPC epc) {
        TypeRole role = new TypeRole();
        id_map.put(resT.getId(), BigInteger.valueOf(ids));
        role.setId(BigInteger.valueOf(ids++));
        role.setName(resT.getName());
        role.setDefRef(find_def_id("role", role.getName()));
        epc.getEventOrFunctionOrRole().add(role);

        // Linking the related element

        List<TypeArc> arcs_list = new LinkedList<TypeArc>();

        for (Object obj : epc.getEventOrFunctionOrRole()) {
            List<String> ll;
            if (obj instanceof TypeArc)
                ll = role_map.get(((TypeArc) obj).getId());
            else
                ll = role_map.get(((TEpcElement) obj).getId());

            if (ll != null) {
                if (obj instanceof TypeFunction) {
                    if (ll.contains(resT.getId())) {
                        TypeArc arc1 = new TypeArc();
                        TypeRelation rel = new TypeRelation();
                        rel.setSource(role.getId());
                        rel.setTarget(((TypeFunction) obj).getId());
                        arc1.setRelation(rel);
                        arcs_list.add(arc1);
                    }
                }
                /*else if(obj instanceof TypeEvent)
                    {
                        if(ll.contains(resT.getId()))
                        {
                            TypeArc arc2 = new TypeArc();
                            TypeRelation rel = new TypeRelation();
                            rel.setSource(role.getId());
                            rel.setTarget(((TypeEvent)obj).getId());
                            arc2.setRelation(rel);
                            arcs_list.add(arc2);
                        }
                    }*/
            }
        }

        for (TypeArc arc : arcs_list)
            epc.getEventOrFunctionOrRole().add(arc);

    }

    private void translateGateway(TypeEPC epc, NodeType node) {
        if (node instanceof ANDSplitType) {
            TypeAND and = new TypeAND();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            and.setId(BigInteger.valueOf(ids++));
            and.setName(node.getName());
            epc.getEventOrFunctionOrRole().add(and);
            epcRefMap.put(and.getId(), and);
        } else if (node instanceof ANDJoinType) {
            TypeAND and = new TypeAND();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            and.setId(BigInteger.valueOf(ids++));
            and.setName(node.getName());
            epc.getEventOrFunctionOrRole().add(and);
            epcRefMap.put(and.getId(), and);
        } else if (node instanceof XORSplitType) {
            TypeXOR xor = new TypeXOR();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            xor.setId(BigInteger.valueOf(ids++));
            xor.setName(node.getName());
            epc.getEventOrFunctionOrRole().add(xor);
            epcRefMap.put(xor.getId(), xor);
            event_list.add(node.getId());
        } else if (node instanceof XORJoinType) {
            TypeXOR xor = new TypeXOR();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            xor.setId(BigInteger.valueOf(ids++));
            xor.setName(node.getName());
            epc.getEventOrFunctionOrRole().add(xor);
            epcRefMap.put(xor.getId(), xor);
        } else if (node instanceof ORSplitType) {
            TypeOR or = new TypeOR();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            or.setId(BigInteger.valueOf(ids++));
            or.setName(node.getName());
            epc.getEventOrFunctionOrRole().add(or);
            epcRefMap.put(or.getId(), or);
            event_list.add(node.getId());
        } else if (node instanceof ORJoinType) {
            TypeOR or = new TypeOR();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            or.setId(BigInteger.valueOf(ids++));
            or.setName(node.getName());
            epc.getEventOrFunctionOrRole().add(or);
            epcRefMap.put(or.getId(), or);
        }
    }

    private void createEvent(TypeEPC epc, NetType net) {
        BigInteger n;

        for (String id : event_list)
            for (EdgeType edge : net.getEdge()) {
                if (edge.getSourceId().equals(id)) {
                    //
                    n = BigInteger.valueOf(ids++);
                    TypeEvent event = new TypeEvent();
                    event.setName(edge.getCondition());
                    event.setId(n);
                    //edge.setTargetId(n);

                    TypeArc arc = new TypeArc();
                    TypeFlow flow = new TypeFlow();
                    arc.setId(BigInteger.valueOf(ids++));
                    flow.setSource(n);
                    flow.setTarget(id_map.get(edge.getTargetId()));
                    flow_list.add(flow);
                    arc.setFlow(flow);

                    TypeArc arc2 = new TypeArc();
                    TypeFlow flow2 = new TypeFlow();
                    arc2.setId(BigInteger.valueOf(ids++));
                    flow2.setSource(id_map.get(edge.getSourceId()));
                    flow2.setTarget(n);
                    flow_list.add(flow2);
                    arc2.setFlow(flow2);

                    edge.setCondition("EPMLEPML");

                    epc.getEventOrFunctionOrRole().add(arc);
                    epc.getEventOrFunctionOrRole().add(event);
                    epc.getEventOrFunctionOrRole().add(arc2);
                    epcRefMap.put(arc.getId(), arc);
                    epcRefMap.put(arc2.getId(), arc2);
                    epcRefMap.put(event.getId(), event);
                }
            }

        event_list.clear();
    }

    /// translate the annotations
    private void mapNodeAnnotations(AnnotationsType annotations) {
        for (AnnotationType annotation : annotations.getAnnotation()) {
            if (nodeRefMap.containsKey(annotation.getCpfId())) {
                // TODO: Handle 1-N mappings
                String cid = annotation.getCpfId();

                if (annotation instanceof GraphicsType) {
                    GraphicsType cGraphInfo = (GraphicsType) annotation;
                    TypeGraphics graphics = new TypeGraphics();

                    if (cGraphInfo.getFill() != null) {
                        TypeFill fill = new TypeFill();
                        fill.setColor(cGraphInfo.getFill().getColor());
                        fill.setGradientColor(cGraphInfo.getFill().getGradientColor());
                        fill.setGradientRotation(cGraphInfo.getFill().getGradientRotation());
                        fill.setImage(cGraphInfo.getFill().getImage());
                        graphics.setFill(fill);
                    }
                    if (cGraphInfo.getFont() != null) {
                        TypeFont font = new TypeFont();
                        font.setColor(cGraphInfo.getFont().getColor());
                        font.setDecoration(cGraphInfo.getFont().getDecoration());
                        font.setFamily(cGraphInfo.getFont().getFamily());
                        font.setHorizontalAlign(cGraphInfo.getFont().getHorizontalAlign());
                        font.setRotation(cGraphInfo.getFont().getRotation());
                        font.setSize(cGraphInfo.getFont().getSize());
                        font.setStyle(cGraphInfo.getFont().getStyle());
                        font.setVerticalAlign(cGraphInfo.getFont().getVerticalAlign());
                        font.setWeight(cGraphInfo.getFont().getWeight());
                        graphics.setFont(font);
                    }
                    if (cGraphInfo.getLine() != null) {
                        TypeLine line = new TypeLine();
                        line.setColor(cGraphInfo.getLine().getColor());
                        line.setShape(cGraphInfo.getLine().getShape());
                        line.setStyle(cGraphInfo.getLine().getStyle());
                        line.setWidth(cGraphInfo.getLine().getWidth());
                        graphics.setLine(line);
                    }
                    TypePosition pos = new TypePosition();
                    if (cGraphInfo.getSize() != null) {
                        pos.setHeight(cGraphInfo.getSize().getHeight());
                        pos.setWidth(cGraphInfo.getSize().getWidth());
                    }
                    if (cGraphInfo.getPosition() != null && cGraphInfo.getPosition().size() > 0) {
                        pos.setX(cGraphInfo.getPosition().get(0).getX());
                        pos.setY(cGraphInfo.getPosition().get(0).getY());
                    }
                    graphics.setPosition(pos);

                    Object obj = epcRefMap.get(id_map.get(cid));
                    if (obj != null)
                        ((TEpcElement) obj).setGraphics(graphics);
                }
            }
        }
    }

    private void mapEdgeAnnotations(AnnotationsType annotations) {
        for (AnnotationType annotation : annotations.getAnnotation()) {
            if (edgeRefMap.containsKey(annotation.getCpfId())
                    || objectRefMap.containsKey(annotation.getCpfId())) {
                // TODO: Handle 1-N mappings
                String cid = annotation.getCpfId();
                TypeLine line = new TypeLine();
                TypeFont font = new TypeFont();

                if (annotation instanceof GraphicsType) {
                    GraphicsType cGraphInfo = (GraphicsType) annotation;
                    TypeMove move = new TypeMove();

                    if (cGraphInfo.getFont() != null) {
                        font.setColor(cGraphInfo.getFont().getColor());
                        font.setDecoration(cGraphInfo.getFont().getDecoration());
                        font.setFamily(cGraphInfo.getFont().getFamily());
                        font.setHorizontalAlign(cGraphInfo.getFont().getHorizontalAlign());
                        font.setRotation(cGraphInfo.getFont().getRotation());
                        font.setSize(cGraphInfo.getFont().getSize());
                        font.setStyle(cGraphInfo.getFont().getStyle());
                        font.setVerticalAlign(cGraphInfo.getFont().getVerticalAlign());
                        font.setWeight(cGraphInfo.getFont().getWeight());
                        move.setFont(font);
                    }
                    if (cGraphInfo.getLine() != null) {
                        line.setColor(cGraphInfo.getLine().getColor());
                        line.setShape(cGraphInfo.getLine().getShape());
                        line.setStyle(cGraphInfo.getLine().getStyle());
                        line.setWidth(cGraphInfo.getLine().getWidth());
                        move.setLine(line);
                    }

                    for (PositionType pos : cGraphInfo.getPosition()) {
                        TypeMove2 m = new TypeMove2();
                        m.setX(pos.getX());
                        m.setY(pos.getY());
                        move.getPosition().add(m);
                    }

                    Object obj = epcRefMap.get(id_map.get(cid));
                    if (obj instanceof TypeArc)
                        ((TypeArc) obj).getGraphics().add(move);
                }
            }
        }
    }

    private BigInteger find_def_id(String type, String name) {

        for (TExtensibleElements def : epml.getDefinitions().getDefinitionOrSpecialization()) {
            if (def instanceof TypeDefinition) {
                if (((TypeDefinition) def).getType() == type && ((TypeDefinition) def).getName() == name) {
                    return ((TypeDefinition) def).getDefId();
                }
            }
        }

        TypeDefinition def = new TypeDefinition();
        def.setDefId(BigInteger.valueOf(defIds++));
        def.setType(type);
        def.setName(name);
        epml.getDefinitions().getDefinitionOrSpecialization().add(def);

        return def.getDefId();

    }
}
