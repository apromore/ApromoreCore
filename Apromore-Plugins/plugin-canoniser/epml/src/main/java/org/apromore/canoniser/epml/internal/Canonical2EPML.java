/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.epml.internal;

import de.epml.ObjectFactory;
import de.epml.TEpcElement;
import de.epml.TExtensibleElements;
import de.epml.TypeAND;
import de.epml.TypeArc;
import de.epml.TypeAttrType;
import de.epml.TypeAttrTypes;
import de.epml.TypeCoordinates;
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
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.utils.ExtensionUtils;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.ConditionExpressionType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.InputOutputType;
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
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

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
public class Canonical2EPML {

    private static final Logger LOGGER = LoggerFactory.getLogger(Canonical2EPML.class);

    private static ObjectFactory EPML_FACTORY = new ObjectFactory();

    Map<String, BigInteger> id_map = new HashMap<>();
    List<String> event_list = new LinkedList<>();
    Map<String, NodeType> nodeRefMap = new HashMap<>();
    Map<String, EdgeType> edgeRefMap = new HashMap<>();
    Set<String> objectSet = new HashSet<>();
    Set<String> arcSet = new HashSet<>();
    Map<BigInteger, Object> epcRefMap = new HashMap<>();
    Map<String, ObjectRefType> objectRefMap = new HashMap<>();
    List<TEpcElement> eventFuncList = new LinkedList<>();
    List<String> object_res_list = new LinkedList<>();
    Map<BigInteger, List<String>> role_map = new HashMap<>();
    List<TypeFunction> subnet_list = new LinkedList<>();
    List<TypeProcessInterface> pi_list = new LinkedList<>();

    List<TypeFlow> flow_list = new LinkedList<>();
    Map<String, BigInteger> cpfIdMap = new HashMap<>();

    private final TypeEPML epml = new TypeEPML();
    private final TypeDirectory dir = new TypeDirectory();
    private long ids = 1;
    private long defIds = 1;

    public TypeEPML getEPML() {
        return epml;
    }


    /**
     * Validating EPCs models against the Event-Function rule. The fake functions and events will be added as needed. The algorithm will also
     * minimized them as much as possible. It verifies the functions and events elements one by one until the last element. This method will only be
     * called if the addFakes boolean value is defined and true.
     * @param epc the header for an EPCs modelass
     * @since 1.0
     */
    private void validate_model(final TypeEPC epc) {
        List<TEpcElement> successors;
        int events, funcs;
        for (Object obj : epc.getEventAndFunctionAndRole()) {
            if (obj instanceof JAXBElement<?>) {
                JAXBElement<?> element = (JAXBElement<?>) obj;
                if (element.getValue() instanceof TypeFunction || element.getValue() instanceof TypeEvent) {
                    eventFuncList.add((TEpcElement) element.getValue());
                }
            }
        }

        for (TEpcElement element : eventFuncList) {
            if (element instanceof TypeEvent) {
                successors = retrieve_successors(element, epc);
                events = funcs = 0;
                for (TEpcElement obj : successors) {
                    if (obj instanceof TypeFunction) {
                        funcs++;
                    } else {
                        events++;
                    }
                }
                if (events == 0 && funcs == 0) {
                    // do nothing ???
                } else if (events == 0) { // && funcs != 0
                    // do nothing ???
                } else if (funcs == 0) { // && events != 0
                    // Add fake function after the current event
                    TypeFunction func = new TypeFunction();
                    TypeArc arc1, arc2 = new TypeArc();
                    TypeFlow flow = new TypeFlow();
                    arc2.setFlow(flow);
                    arc1 = find_post_arc(element, epc);
                    add_fake(arc1, EPML_FACTORY.createTypeEPCFunction(func), arc2, epc);
                } else {
                    if (events > funcs) {
                        // Add fake function after the current event
                        TypeFunction func = new TypeFunction();
                        TypeArc arc1, arc2 = new TypeArc();
                        TypeFlow flow = new TypeFlow();
                        arc2.setFlow(flow);

                        arc1 = find_pre_arc(element, epc);
                        add_fake(arc1, EPML_FACTORY.createTypeEPCFunction(func), arc2, epc);

                        // Add fake event before each successor function
                        for (TEpcElement tepc : successors) {
                            if (tepc instanceof TypeFunction) {
                                TypeEvent event = new TypeEvent();
                                TypeArc arc11, arc22 = new TypeArc();
                                TypeFlow flow1 = new TypeFlow();
                                arc22.setFlow(flow1);

                                arc11 = find_pre_arc(element, epc);
                                add_fake(arc11, EPML_FACTORY.createTypeEPCEvent(event), arc22, epc);
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
                                add_fake(arc11, EPML_FACTORY.createTypeEPCFunction(func), arc22, epc);
                            }
                        }
                    }
                }
            } else if (element instanceof TypeFunction) {
                successors = retrieve_successors(element, epc);
                events = funcs = 0;
                for (TEpcElement obj : successors) {
                    if (obj instanceof TypeFunction) {
                        funcs++;
                    } else {
                        events++;
                    }
                }

                if (funcs == 0 && events == 0) {
                    // Do Nothing
                }
                if (funcs == 0) {
                    // DO NOTHING
                } else if (events == 0) {
                    TypeEvent event = new TypeEvent();
                    TypeArc arc1, arc2 = new TypeArc();
                    TypeFlow flow = new TypeFlow();
                    arc2.setFlow(flow);

                    arc1 = find_post_arc(element, epc);
                    add_fake(arc1, EPML_FACTORY.createTypeEPCEvent(event), arc2, epc);
                } else {
                    if (funcs > events) {
                        TypeEvent event = new TypeEvent();
                        TypeArc arc1, arc2 = new TypeArc();
                        TypeFlow flow = new TypeFlow();
                        arc2.setFlow(flow);

                        arc1 = find_post_arc(element, epc);
                        add_fake(arc1, EPML_FACTORY.createTypeEPCEvent(event), arc2, epc);

                        for (TEpcElement tepc : successors) {
                            if (tepc instanceof TypeEvent) {
                                TypeFunction func = new TypeFunction();
                                TypeArc arc11, arc22 = new TypeArc();
                                TypeFlow flow1 = new TypeFlow();
                                arc22.setFlow(flow1);

                                arc11 = find_pre_arc(element, epc);
                                add_fake(arc11, EPML_FACTORY.createTypeEPCFunction(func), arc22, epc);
                            }
                        }
                    } else {
                        for (TEpcElement tepc : successors) {
                            if (tepc instanceof TypeFunction) {
                                TypeEvent event = new TypeEvent();
                                TypeArc arc11, arc22 = new TypeArc();
                                TypeFlow flow1 = new TypeFlow();
                                arc22.setFlow(flow1);
                                arc11 = find_pre_arc(element, epc);
                                add_fake(arc11, EPML_FACTORY.createTypeEPCEvent(event), arc22, epc);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds a fake element between the received two arcs.
     * 
     * @param epc
     *            the header for an EPCs modelass element arc1 arc2
     * @since 1.0
     */
    private void add_fake(final TypeArc arc1, final JAXBElement<? extends TEpcElement> element, final TypeArc arc2, final TypeEPC epc) {
        element.getValue().setId(BigInteger.valueOf(ids++));
        QName typeRef = new QName("typeRef");
        element.getValue().getOtherAttributes().put(typeRef, "fake");
        element.getValue().setName("");
        arc2.setId(BigInteger.valueOf(ids++));
        arc2.getFlow().setSource(element.getValue().getId());
        arc2.getFlow().setTarget(arc1.getFlow().getTarget());
        arc1.getFlow().setTarget(element.getValue().getId());

        epc.getEventAndFunctionAndRole().add(element);
        epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCArc(arc2));
    }

    /**
     * It take two parameters and returns the previous arc element for the received EPC element if it is exist.
     * 
     * @param epc
     *            the header for an EPCs modelass element the element from this epc intended to retrieve its previous arc
     * @since 1.0
     */
    private TypeArc find_pre_arc(final TEpcElement element, final TypeEPC epc) {
        for (Object object : epc.getEventAndFunctionAndRole()) {
            if (object instanceof JAXBElement<?>) {
                JAXBElement<?> obj = (JAXBElement<?>) object;
                if (obj.getValue() instanceof TypeArc) {
                    TypeArc arc = (TypeArc) obj.getValue();
                    if (arc != null && arc.getFlow() != null && arc.getFlow().getTarget() != null && element != null) {
                        if (arc.getFlow().getTarget().equals(element.getId())) {
                            return arc;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * It take two parameters and returns the post arc element for the received EPC element if it is exist.
     * 
     * @param epc
     *            the header for an EPCs modelass element the element from this epc intended to retrieve its post arc
     * @since 1.0
     */
    private TypeArc find_post_arc(final TEpcElement element, final TypeEPC epc) {
        for (Object object : epc.getEventAndFunctionAndRole()) {
            if (object instanceof JAXBElement<?>) {
                JAXBElement<?> obj = (JAXBElement<?>) object;
                if (obj.getValue() instanceof TypeArc) {
                    TypeArc arc = (TypeArc) obj.getValue();            
                    if (arc.getFlow() != null && arc.getFlow().getSource() != null && element != null) {
                        if (arc.getFlow().getSource().equals(element.getId())) {
                            return arc;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * It take two parameters and returns all the successors elements for the received EPC element.
     * 
     * @param epc
     *            the header for an EPCs modelass element the element from this epc intended to retrieve its successors
     * @since 1.0
     */
    private List<TEpcElement> retrieve_successors(final TEpcElement element, final TypeEPC epc) {
        List<Object> elements = new LinkedList<>();
        List<TEpcElement> successors = new LinkedList<>();
        elements.add(element);
        boolean flag = false;

        while (!elements.isEmpty()) {
            Object obj = elements.get(0);
            elements.remove(obj);
            if (flag && (obj instanceof TypeEvent || obj instanceof TypeFunction)) {
                successors.add((TEpcElement) obj);
            } else {
                flag = true;
                for (Object object : epc.getEventAndFunctionAndRole()) {
                    if (object instanceof JAXBElement<?>) {
                        JAXBElement<?> jElement = ((JAXBElement<?>) object);
                        if (jElement.getValue() instanceof TypeArc) {
                            if (((TypeArc) jElement.getValue()).getFlow() != null) {
                                TypeFlow flow = ((TypeArc) jElement.getValue()).getFlow();
                                if (flow.getSource() != null) {
                                    if (flow.getSource().equals(((TEpcElement) obj).getId())) {
                                        elements.add(epcRefMap.get(flow.getTarget()));
                                        break;
                                    }
                                } else {
                                    LOGGER.error("Flow has a null source.");
                                }
                            }
                        }
                    }
                }
            }

        }

        return successors;
    }

    /**
     * Constructor for de-canonizing CPF & ANF files. The fake feature will be set to false as a default value.
     * 
     * @param cproc the header for a CPF modelass
     * @param annotations the header for an ANF modelass
     * @throws CanoniserException
     */
    public Canonical2EPML(final CanonicalProcessType cproc, final AnnotationsType annotations) throws CanoniserException {
        main(cproc, false);
        mapNodeAnnotations(annotations);
        mapEdgeAnnotations(annotations);
    }

    /**
     * Constructor for de-canonizing CPF file without annotations. The fake feature will be set to false as a default value.
     * 
     * @param cproc the header for a CPF modelass
     * @throws CanoniserException
     */
    public Canonical2EPML(final CanonicalProcessType cproc) throws CanoniserException {
        main(cproc, false);
    }

    /**
     * Constructor for de-canonizing CPF & ANF files.
     * 
     * @param cproc The header for a CPF modelass
     * @param annotations The header for an ANF modelass
     * @param addFakes Boolean value to either add fake elements or not.
     * @throws CanoniserException
     */
    public Canonical2EPML(final CanonicalProcessType cproc, final AnnotationsType annotations, final boolean addFakes) throws CanoniserException {
        main(cproc, addFakes);
        mapNodeAnnotations(annotations);
        mapEdgeAnnotations(annotations);
    }

    /**
     * Constructor for de-canonizing CPF file without annotations.
     * 
     * @param cproc the header for a CPF modelass
     * @param addFakes Boolean value to either add fake elements or not.
     * @throws CanoniserException
     */
    public Canonical2EPML(final CanonicalProcessType cproc, final Boolean addFakes) throws CanoniserException {
        main(cproc, addFakes);
    }

    /**
     * This main method to be reused by all the constructors for all cases.
     * 
     * @param cproc the header for a CPF modelass
     * @param addFakes Boolean value to either add fake elements or not.
     * @throws CanoniserException
     * @since 1.0
     */
    private void main(final CanonicalProcessType cproc, final boolean addFakes) throws CanoniserException {
        // Added as the EPML schema requires Coordinates
        TypeCoordinates coord = new TypeCoordinates();
        coord.setXOrigin("leftToRight");
        coord.setYOrigin("topToBottom");
        epml.setCoordinates(coord);

        // Register the "nodetype" and "roletype" attributes
        TypeAttrTypes attrTypes = new TypeAttrTypes();
        TypeAttrType attrType = new TypeAttrType();
        attrType.setTypeId("nodetype");
        attrTypes.getAttributeType().add(attrType);
        attrType = new TypeAttrType();
        attrType.setTypeId("roletype");
        attrTypes.getAttributeType().add(attrType);
        epml.getAttributeTypes().add(attrTypes);

        epml.getDirectory().add(dir);
        epml.setDefinitions(new TypeDefinitions());

        for (NetType net : cproc.getNet()) {
            TypeEPC epc = new TypeEPC();
            epc.setEpcId(BigInteger.valueOf(ids++));
            cpfIdMap.put(net.getId(), epc.getEpcId());
            if (net.getName() != null) {
                epc.setName(net.getName());
            } else {
                epc.setName(" ");
            }

            translateNet(epc, net);

            for (ResourceTypeType resT : cproc.getResourceType()) {
                //if (object_res_list.contains(resT.getId())) {
                    translateResource(resT, epc);
                    objectSet.add(resT.getId());
                //}
            }
            createRelationArc(epc, net);
            object_res_list.clear();
            if (addFakes) {
                validate_model(epc);
            }
            epml.getDirectory().get(0).getEpcOrDirectory().add(epc);
        }

        for (TypeFunction func : subnet_list) {
            if (func.getToProcess() != null) {
                func.getToProcess().setLinkToEpcId(id_map.get(func.getToProcess().getLinkToEpcId().toString()));
            }
        }
        /* TODO dummy value set elsewhere is not adequate!  Must reinstate this code somehow.
        for (TypeProcessInterface pi : pi_list) {
            pi.getToProcess().setLinkToEpcId(id_map.get(pi.getToProcess().getLinkToEpcId().toString()));
        }
        */
    }

    private void translateNet(final TypeEPC epc, final NetType net) throws CanoniserException {
        for (NodeType node : net.getNode()) {
            if (node instanceof TaskType) {
                translateTask(epc, (TaskType) node);
            } else if (node instanceof EventType) {
                translateEvent(epc, (EventType) node);
            }

            if (node instanceof WorkType) {
                for (ObjectRefType ref : ((WorkType) node).getObjectRef()) {
                    object_res_list.add(ref.getObjectId());
                }

                List<String> ll = new LinkedList<>();
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
            if (convertConditionExpression(edge) == null) {
                flag = true;
            } else if (convertConditionExpression(edge).equals("EPMLEPML")) {
                flag = false;
            }

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
                    epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCArc(arc));
                    epcRefMap.put(arc.getId(), arc);
                } else {
                    id_map.put(edge.getTargetId(), id_map.get(edge.getSourceId()));
                }
            }
        }

        for (ObjectType obj : net.getObject()) {
            //if (object_res_list.contains(obj.getId())) {
                translateObject(obj, epc);
                objectSet.add(obj.getId());
            //}
        }
    }

    private void createRelationArc(final TypeEPC epc, final NetType net) {
        for (NodeType node : net.getNode()) {
            if (node instanceof WorkType) {
                for (ObjectRefType ref : ((WorkType) node).getObjectRef()) {
                    if (ref.getObjectId() != null) {
                        TypeArc arc = new TypeArc();
                        TypeRelation rel = new TypeRelation();
                        id_map.put(ref.getId(), BigInteger.valueOf(ids));
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
                        for (TypeAttribute att : ref.getAttribute()) {
                            if (att.getName().equals("RefID")) {
                                String l = att.getValue();
                                objectRefMap.put(l, ref);
                                id_map.put(l, arc.getId());
                            }
                        }
                        arc.setRelation(rel);
                        epcRefMap.put(arc.getId(), arc);
                        epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCArc(arc));
                        arcSet.add(ref.getId());
                    }
                }

                for (ResourceTypeRefType ref : ((WorkType) node).getResourceTypeRef()) {
                    if (ref.getResourceTypeId() != null) {
                        TypeArc arc = new TypeArc();
                        TypeRelation rel = new TypeRelation();
                        id_map.put(ref.getId(), BigInteger.valueOf(ids));
                        arc.setId(BigInteger.valueOf(ids++));
                        rel.setSource(id_map.get(node.getId()));
                        rel.setTarget(id_map.get(ref.getResourceTypeId()));
                        rel.setType("role");
                        arc.setRelation(rel);
                        epcRefMap.put(arc.getId(), arc);
                        epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCArc(arc));
                        arcSet.add(ref.getId());
                    }
                }
            }
        }
    }

    private void translateTask(final TypeEPC epc, final TaskType task) {
        if (task.getName() == null && task.getSubnetId() != null) {  // TODO this is probably incorrect and should be removed
            TypeProcessInterface pi = new TypeProcessInterface();
            pi.setToProcess(new TypeToProcess());
            if (cpfIdMap.get(task.getSubnetId()) != null) {
                pi.getToProcess().setLinkToEpcId(cpfIdMap.get(task.getSubnetId()));
            }
            pi_list.add(pi);
        } else {
            TypeFunction func = new TypeFunction();
            id_map.put(task.getId(), BigInteger.valueOf(ids));
            func.setId(BigInteger.valueOf(ids++));
            func.setName(task.getName());
            func.setDefRef(find_def_id("function", func.getName()));
            if (task.getSubnetId() != null) {
                if (cpfIdMap.get(task.getSubnetId()) != null) {
                    if (func.getToProcess() != null) {
                        func.getToProcess().setLinkToEpcId(cpfIdMap.get(task.getSubnetId()));
                    }
                }
                subnet_list.add(func);
            }
            for (TypeAttribute cpfAttribute : task.getAttribute()) {
                if ("systemAction".equals(cpfAttribute.getName())) {
                    de.epml.TypeAttribute attribute = new de.epml.TypeAttribute();
                    attribute.setTypeRef("nodetype");
                    attribute.setValue("System Function");
                    func.getAttribute().add(attribute);
                }
            }
            epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCFunction(func));
            epcRefMap.put(func.getId(), func);
        }
    }

    private void translateEvent(final TypeEPC epc, final EventType node) {
        if (ExtensionUtils.hasExtension(node.getAttribute(), "processInterface")) {
            TypeProcessInterface pi = new TypeProcessInterface();
            pi.setToProcess(new TypeToProcess());
            pi.getToProcess().setLinkToEpcId(BigInteger.valueOf(1));  // TODO dummy, need to set the destination process properly
            pi_list.add(pi);

            id_map.put(node.getId(), BigInteger.valueOf(ids));
            pi.setId(BigInteger.valueOf(ids++));
            pi.setName(node.getName());
            pi.setDefRef(find_def_id("function", pi.getName()));
            epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCProcessInterface(pi));
            epcRefMap.put(pi.getId(), pi);
        } else {
            TypeEvent event = new TypeEvent();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            event.setId(BigInteger.valueOf(ids++));
            event.setName(node.getName());
            // TODO getName could be NULL, will not work as lookup key!
            event.setDefRef(find_def_id("event", event.getName()));
            epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCEvent(event));
            epcRefMap.put(event.getId(), event);
        }
    }

    private void translateObject(final ObjectType obj, final TypeEPC epc) {
        TypeObject object = new ObjectFactory().createTypeObject();
        id_map.put(obj.getId(), BigInteger.valueOf(ids));
        object.setId(BigInteger.valueOf(ids++));
        object.setName(obj.getName());
        // TODO getName could be NULL, will not work as lookup key!
        object.setDefRef(find_def_id("object", object.getName()));
        object.setFinal(obj.isConfigurable());
        object.setType("output");
        epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCObject(object));
        epcRefMap.put(object.getId(), object);
    }

    private void translateResource(final ResourceTypeType resT, final TypeEPC epc) {
        TypeRole role = new TypeRole();
        id_map.put(resT.getId(), BigInteger.valueOf(ids));
        role.setId(BigInteger.valueOf(ids++));
        role.setName(resT.getName());
        // TODO getName could be NULL, will not work as lookup key!
        role.setDefRef(find_def_id("role", role.getName()));
        if (resT instanceof HumanType && ((HumanType) resT).getType() != null) {
            switch (((HumanType) resT).getType()) {
            case UNIT:
                de.epml.TypeAttribute attribute = new de.epml.TypeAttribute();
                attribute.setTypeRef("roletype");
                attribute.setValue("Organizational Unit");
                role.getAttribute().add(attribute);
                break;
            }
        } else if (resT instanceof NonhumanType) {
            de.epml.TypeAttribute attribute = new de.epml.TypeAttribute();
            attribute.setTypeRef("roletype");
            attribute.setValue("IT system");
            role.getAttribute().add(attribute);
        }
        epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCRole(role));
        epcRefMap.put(role.getId(), role);

        List<TypeArc> arcs_list = new LinkedList<>();
        for (Object obj : epc.getEventAndFunctionAndRole()) {
            List<String> ll;
            JAXBElement<?> element = (JAXBElement<?>) obj;
            if (element.getValue() instanceof TypeArc) {
                ll = role_map.get(((TypeArc) element.getValue()).getId());
            } else {
                ll = role_map.get(((TEpcElement) element.getValue()).getId());
            }

            if (ll != null) {
                if (element.getValue() instanceof TypeFunction) {
                    if (ll.contains(resT.getId())) {
                        TypeArc arc1 = new TypeArc();
                        TypeRelation rel = new TypeRelation();
                        rel.setSource(role.getId());
                        rel.setTarget(((TypeFunction) element.getValue()).getId());
                        arc1.setRelation(rel);
                        arcs_list.add(arc1);
                    }
                }
            }
        }

        for (TypeArc arc : arcs_list) {
            epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCArc(arc));
        }
    }

    private void translateGateway(final TypeEPC epc, final NodeType node) throws CanoniserException {
        if (node instanceof ANDSplitType) {
            TypeAND and = new TypeAND();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            and.setId(BigInteger.valueOf(ids++));
            and.setName(node.getName());
            epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCAnd(and));
            epcRefMap.put(and.getId(), and);
        } else if (node instanceof ANDJoinType) {
            TypeAND and = new TypeAND();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            and.setId(BigInteger.valueOf(ids++));
            and.setName(node.getName());
            epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCAnd(and));
            epcRefMap.put(and.getId(), and);
        } else if (node instanceof XORSplitType) {
            TypeXOR xor = new TypeXOR();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            xor.setId(BigInteger.valueOf(ids++));
            xor.setName(node.getName());
            epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCXor(xor));
            epcRefMap.put(xor.getId(), xor);
        } else if (node instanceof XORJoinType) {
            TypeXOR xor = new TypeXOR();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            xor.setId(BigInteger.valueOf(ids++));
            xor.setName(node.getName());
            epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCXor(xor));
            epcRefMap.put(xor.getId(), xor);
        } else if (node instanceof ORSplitType) {
            TypeOR or = new TypeOR();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            or.setId(BigInteger.valueOf(ids++));
            or.setName(node.getName());
            epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCOr(or));
            epcRefMap.put(or.getId(), or);
        } else if (node instanceof ORJoinType) {
            TypeOR or = new TypeOR();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            or.setId(BigInteger.valueOf(ids++));
            or.setName(node.getName());
            epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCOr(or));
            epcRefMap.put(or.getId(), or);
        } else if (node instanceof StateType) {
            // Not Supported
            //throw new CanoniserException("State is not supported by EPC!");

            // TODO: Need to let the user know we put an event in the place of a State join.
            TypeEvent event = new TypeEvent();
            id_map.put(node.getId(), BigInteger.valueOf(ids));
            event.setId(BigInteger.valueOf(ids++));
            event.setName(node.getName());
            event.setDefRef(find_def_id("event", event.getName()));
            epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCEvent(event));
            epcRefMap.put(event.getId(), event);
        }
    }

    private void createEvent(final TypeEPC epc, final NetType net) {
        BigInteger n;

        for (String id : event_list) {
            for (EdgeType edge : net.getEdge()) {
                if (edge.getSourceId().equals(id)) {
                    n = BigInteger.valueOf(ids++);
                    TypeEvent event = new TypeEvent();
                    event.setName(convertConditionExpression(edge));
                    event.setId(n);

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

                    ConditionExpressionType condExpr = new ConditionExpressionType();
                    condExpr.setDescription("EPMLEPML");
                    edge.setConditionExpr(condExpr);

                    epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCArc(arc));
                    epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCEvent(event));
                    epc.getEventAndFunctionAndRole().add(EPML_FACTORY.createTypeEPCArc(arc2));
                    epcRefMap.put(arc.getId(), arc);
                    epcRefMap.put(arc2.getId(), arc2);
                    epcRefMap.put(event.getId(), event);
                }
            }
        }

        event_list.clear();
    }

    private String convertConditionExpression(final EdgeType edge) {
        ConditionExpressionType conditionExpr = edge.getConditionExpr();
        if (conditionExpr != null) {
            if (conditionExpr.getDescription() != null) {
                // Try to use the textual description of this expression
                return conditionExpr.getDescription();
            } else if (conditionExpr.getExpression() != null) {
                // If there is not textual description then just display the formal expression
                return conditionExpr.getExpression();
            } else {
                if (edge.isDefault()) {
                    return "default";
                } else {
                    return null;
                }
            }
        } else {
            if (edge.isDefault()) {
                return "default";
            } else {
                return null;
            }
        }
    }

    // translate the annotations
    private void mapNodeAnnotations(final AnnotationsType annotations) {
        for (AnnotationType annotation : annotations.getAnnotation()) {
            if (nodeRefMap.containsKey(annotation.getCpfId()) || objectSet.contains(annotation.getCpfId())) {
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
                    if (obj != null) {
                        ((TEpcElement) obj).setGraphics(graphics);
                    }
                }
            }
        }
    }

    private void mapEdgeAnnotations(final AnnotationsType annotations) {
        for (AnnotationType annotation : annotations.getAnnotation()) {
            if (edgeRefMap.containsKey(annotation.getCpfId()) || objectRefMap.containsKey(annotation.getCpfId()) || arcSet.contains(annotation.getCpfId())) {
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
                    if (obj instanceof TypeArc) {
                        ((TypeArc) obj).getGraphics().add(move);
                    }
                }
            }
        }
    }

    private BigInteger find_def_id(final String type, final String name) {
        for (TExtensibleElements def : epml.getDefinitions().getDefinitionOrSpecialization()) {
            if (def instanceof TypeDefinition && name != null) {
                if (type.equals(((TypeDefinition) def).getType()) && name.equals(((TypeDefinition) def).getName())) {
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
