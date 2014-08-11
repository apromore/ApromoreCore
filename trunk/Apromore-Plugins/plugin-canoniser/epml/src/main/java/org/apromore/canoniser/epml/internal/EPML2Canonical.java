/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

/**
 * EPML2Canonical is a class for converting an TypeEPML object
 * into a CanonicalProcessType object.
 * A EPML2Canonical object encapsulates the state of the two main
 * components resulted from the canonization process.  This
 * state information includes:
 * <ul>
 * <li>CanonicalProcessType object
 * <li>AnnotationsType object
 * </ul>
 * <p>
 *
 *

 @author Abdul
 *

 @version     %I%, %G%
 *

 @since 1.0
 */
package org.apromore.canoniser.epml.internal;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apromore.anf.AnnotationsType;
import org.apromore.anf.FillType;
import org.apromore.anf.FontType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.LineType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SizeType;
import org.apromore.canoniser.exception.CanoniserException;
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
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;

import de.epml.TEpcElement;
import de.epml.TExtensibleElements;
import de.epml.TypeAND;
import de.epml.TypeArc;
import de.epml.TypeEPC;
import de.epml.TypeEPML;
import de.epml.TypeEvent;
import de.epml.TypeFunction;
import de.epml.TypeMove2;
import de.epml.TypeOR;
import de.epml.TypeObject;
import de.epml.TypeProcessInterface;
import de.epml.TypeRANGE;
import de.epml.TypeRole;
import de.epml.TypeXOR;

public class EPML2Canonical {

    Map<BigInteger, String> id_map = new HashMap<>();
    List<String> flow_source_id_list = new LinkedList<>();
    List<TypeAND> and_list = new LinkedList<>();
    List<TypeOR> or_list = new LinkedList<>();
    List<TypeXOR> xor_list = new LinkedList<>();
    Map<BigInteger, String> def_ref = new HashMap<>();
    Map<BigInteger, TypeRole> role_ref = new HashMap<>();
    Map<BigInteger, TypeObject> obj_ref = new HashMap<>();
    List<TaskType> subnet_list = new LinkedList<>();
    List<BigInteger> range_ids = new LinkedList<>();
    List<String> event_ids = new LinkedList<>();
    List<TypeArc> range_flow = new LinkedList<>();
    List<TypeArc> range_relation = new LinkedList<>();

    private final CanonicalProcessType cproc = new CanonicalProcessType();
    private final AnnotationsType annotations = new AnnotationsType();
    private long ids = 1;

    public CanonicalProcessType getCPF() {
        return cproc;
    }

    public AnnotationsType getANF() {
        return annotations;
    }

    /**
     * The constructor receives the header then does the canonization process in order to allow the user to retrieve the produced process again into
     * the canonical format. The user also will be able to retrieve the annotation element which stores the annotation data for the canonized modelass
     * isolated from the process flow.
     * 
     * @param epml the header for an EPML (EPC Markup Language) which is file format for EPC diagrams.
     * @throws org.apromore.canoniser.exception.CanoniserException
     * @since 1.0
     */
    public EPML2Canonical(final TypeEPML epml) throws CanoniserException {
        main(epml);
    }

    public EPML2Canonical(final TypeEPML epml, final long id) throws CanoniserException {
        this.ids = id;
        main(epml);
    }

    void main(TypeEPML epml) throws CanoniserException {
        epml = removeFakes(epml);

        TypeAttribute att = new TypeAttribute();
        att.setName("IntialFormat");
        att.setValue("EPML 2.0");
        cproc.getAttribute().add(att);

        if (epml.getDirectory() != null && epml.getDirectory().size() > 0) {
            for (int i = 0; i < epml.getDirectory().size(); i++) {
                for (TExtensibleElements epc : epml.getDirectory().get(i).getEpcOrDirectory()) {
                    if (epc instanceof TypeEPC) {
                        TypeEPC typeEPC = (TypeEPC) epc;
                        cproc.setName(typeEPC.getName());
                        cproc.setUri(typeEPC.getEpcId().toString());
                        annotations.setUri(cproc.getUri());
                        // TODO what version to add here?
                        cproc.setVersion("1.0");
                        NetType net = new NetType();
                        net.setName(typeEPC.getName());
                        translateEpc(net, typeEPC);
                        id_map.put(typeEPC.getEpcId(), String.valueOf(ids));
                        net.setId(String.valueOf(ids++));
                        cproc.getNet().add(net);
                    }
                }
                for (TaskType task : subnet_list) {
                    task.setSubnetId(id_map.get(new BigInteger(task.getSubnetId())));
                }
                subnet_list.clear();
            }
        } else {
            // The EPML element doesn't have any directory
            //throw new CanoniserException("EPML file is missing a 'directory' invalid!");
            for (TypeEPC epc : epml.getEpcs()) {
                NetType net = new NetType();
                translateEpc(net, epc);
                id_map.put(epc.getEpcId(), String.valueOf(ids));
                net.setId(String.valueOf(ids++));
                cproc.getNet().add(net);
            }
            for (TaskType task : subnet_list) {
                task.setSubnetId(id_map.get(new BigInteger(task.getSubnetId())));
            }
            subnet_list.clear();
        }
    }

    /**
     * This method for removing the fake functions and events in case the model has them.
     * 
     * @param epml
     *            the header for an EPML
     * @return epml the header for the EPML modelass after modification
     * @throws CanoniserException
     * @since 1.0
     */
    private TypeEPML removeFakes(final TypeEPML epml) throws CanoniserException {
        List<TEpcElement> remove_list = new LinkedList<>();
        List<TypeArc> arc_remove_list = new LinkedList<>();

        if (epml.getDirectory() != null && epml.getDirectory().size() > 0) {
            for (int i = 0; i < epml.getDirectory().size(); i++) {
                for (TExtensibleElements epc : epml.getDirectory().get(i).getEpcOrDirectory()) {
                    if (epc instanceof TypeEPC) {
                        for (Object element : ((TypeEPC) epc).getEventAndFunctionAndRole()) {
                            if (element instanceof TypeFunction || element instanceof TypeEvent) {
                                QName typeRef = new QName("typeRef");
                                String str = ((TEpcElement) element).getOtherAttributes().get(typeRef);
                                if (str != null && str.equals("fake")) {
                                    remove_list.add((TEpcElement) element);
                                }
                            }
                        }
                        for (TEpcElement element : remove_list) {
                            for (Object arc : ((TypeEPC) epc).getEventAndFunctionAndRole()) {
                                if (arc instanceof TypeArc) {
                                    if (((TypeArc) arc).getFlow() != null) {
                                        if (((TypeArc) arc).getFlow().getSource().equals(element.getId())) {
                                            for (Object arc2 : ((TypeEPC) epc).getEventAndFunctionAndRole()) {
                                                if (arc2 instanceof TypeArc) {
                                                    if (((TypeArc) arc2).getFlow().getTarget().equals(element.getId())) {
                                                        ((TypeArc) arc2).getFlow().setTarget(((TypeArc) arc).getFlow().getTarget());
                                                    }
                                                }
                                            }
                                            arc_remove_list.add((TypeArc) arc);
                                        }
                                    }
                                }
                            }
                            for (TypeArc arc : arc_remove_list) {
                                ((TypeEPC) epc).getEventAndFunctionAndRole().remove(arc);
                            }
                            ((TypeEPC) epc).getEventAndFunctionAndRole().remove(element);
                        }
                    }
                }
            }
        } else {
            //throw new CanoniserException("Invalid EPC no 'directory'!");
            for (TypeEPC epc : epml.getEpcs()) {
                for (Object element : epc.getEventAndFunctionAndRole()) {
                    if (element instanceof TypeFunction || element instanceof TypeEvent) {
                        QName typeRef = new QName("typeRef");
                        if (((TEpcElement) element).getOtherAttributes().get(typeRef).equals("fake")) {
                            remove_list.add((TEpcElement) element);
                        }
                    }
                }
                for (TEpcElement element : remove_list) {
                    for (Object arc : epc.getEventAndFunctionAndRole()) {
                        if (arc instanceof TypeArc) {
                            if (((TypeArc) arc).getFlow() != null) {
                                if (((TypeArc) arc).getFlow().getSource().equals(element.getId())) {
                                    for (Object arc2 : epc.getEventAndFunctionAndRole()) {
                                        if (arc2 instanceof TypeArc) {
                                            if (((TypeArc) arc2).getFlow().getTarget().equals(element.getId())) {
                                                ((TypeArc) arc2).getFlow().setTarget(((TypeArc) arc).getFlow().getTarget());
                                            }
                                        }
                                    }
                                    arc_remove_list.add((TypeArc) arc);
                                }
                            }
                        }
                    }
                    for (TypeArc arc : arc_remove_list) {
                        epc.getEventAndFunctionAndRole().remove(arc);
                    }
                    epc.getEventAndFunctionAndRole().remove(element);
                }
            }
        }

        return epml;
    }

    @SuppressWarnings("unchecked")
    private void translateEpc(final NetType net, final TypeEPC epc) throws CanoniserException {
        Map<String, String> role_names = new HashMap<>();

        for (Object obj : epc.getEventAndFunctionAndRole()) {
            if (obj instanceof JAXBElement) {
                JAXBElement<?> element = (JAXBElement<?>) obj;
                if (element.getValue() instanceof TypeEvent) {
                    translateEvent(net, (TypeEvent) element.getValue());
                    addNodeAnnotations(element.getValue());
                } else if (element.getValue() instanceof TypeFunction) {
                    translateFunction(net, ((JAXBElement<TypeFunction>) obj).getValue());
                    addNodeAnnotations(element.getValue());
                } else if (element.getValue() instanceof TypeAND) {
                    id_map.put(((TEpcElement) element.getValue()).getId(), String.valueOf(ids));
                    addNodeAnnotations(element.getValue());
                    ((TEpcElement) element.getValue()).setId(BigInteger.valueOf(ids++));
                    and_list.add((TypeAND) element.getValue());
                } else if (element.getValue() instanceof TypeOR) {
                    id_map.put(((TEpcElement) element.getValue()).getId(), String.valueOf(ids));
                    addNodeAnnotations(element.getValue());
                    ((TEpcElement) element.getValue()).setId(BigInteger.valueOf(ids++));
                    or_list.add((TypeOR) element.getValue());
                } else if (element.getValue() instanceof TypeXOR) {
                    id_map.put(((TEpcElement) element.getValue()).getId(), String.valueOf(ids));
                    addNodeAnnotations(element.getValue());
                    ((TEpcElement) element.getValue()).setId(BigInteger.valueOf(ids++));


                    xor_list.add((TypeXOR) element.getValue());
                } else if (element.getValue() instanceof TypeRole) {
                    if (!role_names.containsKey(((TypeRole) element.getValue()).getName())) {
                        translateRole((TypeRole) element.getValue());
                        addNodeAnnotations(element.getValue());
                        role_names.put(((TypeRole) element.getValue()).getName(), String.valueOf(ids - 1));
                    } else {
                        id_map.put(((TypeRole) element.getValue()).getId(), role_names.get(((TypeRole) element.getValue()).getName()));
                    }
                } else if (element.getValue() instanceof TypeObject) {
                    translateObject(net, (TypeObject) element.getValue());
                    addNodeAnnotations(element.getValue());
                } else if (element.getValue() instanceof TypeRANGE) {
                    range_ids.add(((TypeRANGE) element.getValue()).getId());
                } else if (element.getValue() instanceof TypeProcessInterface) {
                    translatePI(net, (TypeProcessInterface) element.getValue());
                    addNodeAnnotations(element.getValue());
                }
            }
        }

        // Convert Arcs after all Elements are converted, because we need the IDs of all Elements
        for (Object obj : epc.getEventAndFunctionAndRole()) {
            if (obj instanceof JAXBElement) {
                JAXBElement<?> element = (JAXBElement<?>) obj;
                if (element.getValue() instanceof TypeArc) {
                    TypeArc arc = (TypeArc) element.getValue();
                    if (arc.getFlow() != null) {
                        if (range_ids.contains(arc.getFlow().getSource()) || range_ids.contains(arc.getFlow().getTarget())) {
                            range_flow.add(arc);
                        } else {
                            translateArc(net, arc);
                        }
                        addEdgeAnnotation(arc);
                    } else if (arc.getRelation() != null) {
                        if (range_ids.contains(arc.getRelation().getSource()) || range_ids.contains(arc.getRelation().getTarget())) {
                            range_relation.add(arc);
                        } else {
                            translateArc(net, arc);
                        }
                        addEdgeAnnotation(arc);
                    }
                }
            }
        }

        for (TypeArc arc : range_flow) {
            if (range_ids.contains(arc.getFlow().getSource())) {
                for (TypeArc arc2 : range_flow) {
                    if (range_ids.contains(arc2.getFlow().getTarget())) {
                        arc.getFlow().setSource(arc2.getFlow().getSource());
                        translateArc(net, arc);
                        addEdgeAnnotation(arc);
                    }
                }
            }
        }

        for (TypeArc arc : range_relation) {
            if (range_ids.contains(arc.getRelation().getSource())) {
                for (TypeArc arc2 : range_relation) {
                    if (range_ids.contains(arc2.getRelation().getTarget())) {
                        arc.getRelation().setSource(arc2.getRelation().getSource());
                        translateArc(net, arc);
                        addEdgeAnnotation(arc);
                    }
                }
            }
        }

        int counter;
        for (TypeAND and : and_list) {
            counter = 0;
            BigInteger n = and.getId();
            for (String s : flow_source_id_list) {
                if (n.toString().equals(s)) {
                    counter++;
                }
            }
            if (counter <= 1) {
                ANDJoinType andJ = new ANDJoinType();
                andJ.setId(String.valueOf(and.getId()));
                andJ.setName(and.getName());
                net.getNode().add(andJ);
            } else {
                ANDSplitType andS = new ANDSplitType();
                andS.setId(String.valueOf(and.getId()));
                andS.setName(and.getName());
                net.getNode().add(andS);
            }
        }
        and_list.clear();

        for (TypeOR or : or_list) {
            counter = 0;
            BigInteger n = or.getId();
            for (String s : flow_source_id_list) {
                if (n.toString().equals(s)) {
                    counter++;
                }
            }
            if (counter <= 1) {
                ORJoinType orJ = new ORJoinType();
                orJ.setId(String.valueOf(or.getId()));
                orJ.setName(or.getName());
                net.getNode().add(orJ);
            } else {
                ORSplitType orS = new ORSplitType();
                orS.setId(String.valueOf(or.getId()));
                orS.setName(or.getName());
                net.getNode().add(orS);
                //processUnrequiredEvents(net, or.getId()); // after creating the split node ,, delete the event
            }
        }
        or_list.clear();

        for (TypeXOR xor : xor_list) {
            counter = 0;
            BigInteger n = xor.getId();
            for (String s : flow_source_id_list) {
                if (n.toString().equals(s)) {
                    counter++;
                }
            }

            if (counter <= 1) {
                XORJoinType xorJ = new XORJoinType();
                xorJ.setId(String.valueOf(xor.getId()));
                xorJ.setName(xor.getName());

                if(xor.getConfigurableConnector()!=null) {
                    xorJ.setConfigurable(true);

                }
                net.getNode().add(xorJ);
            } else {
                XORSplitType xorS = new XORSplitType();
                xorS.setId(String.valueOf(xor.getId()));
                xorS.setName(xor.getName());
                if(xor.getConfigurableConnector()!=null) {
                    xorS.setConfigurable(true);

                }
                net.getNode().add(xorS);
                //processUnrequiredEvents(net, xor.getId());
            }
        }
        xor_list.clear();
    }

    private void addEdgeAnnotation(final TypeArc arc) throws CanoniserException {
        LineType line = new LineType();
        GraphicsType graph = new GraphicsType();
        FontType font = new FontType();

        if (arc.getGraphics() != null) {
            if (id_map.get(arc.getId()) == null && range_relation.contains(arc)) {
                Logger.getAnonymousLogger().info("Arc " + arc.getId() + " discarded");
                return;
            }
            graph.setCpfId(id_map.get(arc.getId()));
            graph.setId(graph.getCpfId());
            if (arc.getGraphics().size() > 0) {
                if (arc.getGraphics().get(0) != null) {
                    if (arc.getGraphics().get(0).getFont() != null) {
                        font.setColor(arc.getGraphics().get(0).getFont().getColor());
                        font.setDecoration(arc.getGraphics().get(0).getFont().getDecoration());
                        font.setFamily(arc.getGraphics().get(0).getFont().getFamily());
                        font.setHorizontalAlign(arc.getGraphics().get(0).getFont().getHorizontalAlign());
                        font.setRotation(arc.getGraphics().get(0).getFont().getRotation());
                        font.setSize(arc.getGraphics().get(0).getFont().getSize());
                        font.setStyle(arc.getGraphics().get(0).getFont().getStyle());
                        font.setVerticalAlign(arc.getGraphics().get(0).getFont().getVerticalAlign());
                        font.setWeight(arc.getGraphics().get(0).getFont().getWeight());
                        graph.setFont(font);
                    }
                    if (arc.getGraphics().get(0).getLine() != null) {
                        line.setColor(arc.getGraphics().get(0).getLine().getColor());
                        line.setShape(arc.getGraphics().get(0).getLine().getShape());
                        line.setStyle(arc.getGraphics().get(0).getLine().getStyle());
                        line.setWidth(arc.getGraphics().get(0).getLine().getWidth());
                        graph.setLine(line);
                    }

                    for (TypeMove2 mov2 : arc.getGraphics().get(0).getPosition()) {
                        PositionType pos = new PositionType();
                        pos.setX(mov2.getX());
                        pos.setY(mov2.getY());
                        graph.getPosition().add(pos);
                    }
                    annotations.getAnnotation().add(graph);
                }
            }
        }
    }

    private void addNodeAnnotations(final Object obj) {
        GraphicsType graphT = new GraphicsType();
        LineType line = new LineType();
        FillType fill = new FillType();
        PositionType pos = new PositionType();
        SizeType size = new SizeType();
        FontType font = new FontType();
        String cpfId;

        TEpcElement element = (TEpcElement) obj;
        cpfId = id_map.get(element.getId());

        if (element.getGraphics() != null) {
            if (element.getGraphics().getFill() != null) {
                fill.setColor(element.getGraphics().getFill().getColor());
                fill.setGradientColor(element.getGraphics().getFill().getGradientColor());
                fill.setGradientRotation(element.getGraphics().getFill().getGradientRotation());
                fill.setImage(element.getGraphics().getFill().getImage());
                graphT.setFill(fill);
            }

            if (element.getGraphics().getPosition() != null) {
                size.setHeight(element.getGraphics().getPosition().getHeight());
                size.setWidth(element.getGraphics().getPosition().getWidth());
                graphT.setSize(size);
                pos.setX(element.getGraphics().getPosition().getX());
                pos.setY(element.getGraphics().getPosition().getY());
                graphT.getPosition().add(pos);
            }

            if (element.getGraphics().getLine() != null) {
                line.setColor(element.getGraphics().getLine().getColor());
                line.setShape(element.getGraphics().getLine().getShape());
                line.setStyle(element.getGraphics().getLine().getStyle());
                line.setWidth(element.getGraphics().getLine().getWidth());
                graphT.setLine(line);
            }

            if (element.getGraphics().getFont() != null) {
                font.setColor(element.getGraphics().getFont().getColor());
                font.setDecoration(element.getGraphics().getFont().getDecoration());
                font.setFamily(element.getGraphics().getFont().getFamily());
                font.setHorizontalAlign(element.getGraphics().getFont().getHorizontalAlign());
                font.setRotation(element.getGraphics().getFont().getRotation());
                font.setSize(element.getGraphics().getFont().getSize());
                font.setStyle(element.getGraphics().getFont().getStyle());
                font.setVerticalAlign(element.getGraphics().getFont().getVerticalAlign());
                font.setWeight(element.getGraphics().getFont().getWeight());
                graphT.setFont(font);
            }

            graphT.setCpfId(cpfId);
            graphT.setId(cpfId);
            annotations.getAnnotation().add(graphT);
        }
    }

    // should be in the end

    private void processUnrequiredEvents(final NetType net, final BigInteger id) throws CanoniserException {
        List<EdgeType> edge_remove_list = new LinkedList<>();
        List<NodeType> node_remove_list = new LinkedList<>();
        String event_id;
        boolean found = false;
        for (EdgeType edge : net.getEdge()) {
            if (edge.getSourceId() != null) {
                // Test if Edge is from a XOR-split to an Event
                if (edge.getSourceId().equals(id.toString()) && event_ids.contains(edge.getTargetId())) {
                    // Test if the target Event is an Exit Event
                    if (!isExitNode(edge.getTargetId(), net)) {
                        event_id = edge.getTargetId();
                        for (EdgeType edge2 : net.getEdge()) {
                            if (edge2.getSourceId() != null && edge2.getSourceId().equals(event_id)) {
                                edge.setTargetId(edge2.getTargetId());
                                edge_remove_list.add(edge2);
                                found = true;
                            }
                        }
                        // Delete the unrequired event and set its name as a condition for the edge
                        for (NodeType node : net.getNode()) {
                            if (node.getId().equals(event_id)) {
                                if (found) {
                                    if (node.getName() != null) {
                                        edge.setConditionExpr(convertStringExpression(node.getName()));   
                                    }
                                    node_remove_list.add(node);
                                } else {
                                    if (node.getName() != null) {
                                        edge.setConditionExpr(convertStringExpression(node.getName()));   
                                    }                                    
                                    node.setName("");
                                }
                            }
                        }
                    } else {
                        NodeType node = findNodeById(edge.getTargetId(), net);
                        if (node.getName() != null) {
                            edge.setConditionExpr(convertStringExpression(node.getName()));
                        }
                    }
                }
            }
        }

        for (EdgeType edge : edge_remove_list) {
            net.getEdge().remove(edge);
        }
        edge_remove_list.clear();
        for (NodeType node : node_remove_list) {
            net.getNode().remove(node);
        }
        node_remove_list.clear();

    }

    /**
     * @param nodeId of an existing Node
     * @param net that contains a Node with given ID
     * @return true if the Node with the given Id has no successors (i.e. is an exit Node)
     */
    private boolean isExitNode(String nodeId, NetType net) {
        NodeType node = findNodeById(nodeId, net);
        if (node != null) {
            for (EdgeType e: net.getEdge()) {
                  if (node.getId().equals(e.getSourceId())) {
                      return false;
                  }
            }
            return true;
        } else {
            throw new IllegalArgumentException("Could not find Node "+nodeId + " in isExitNode!");
        }
    }

    private NodeType findNodeById(String nodeId, NetType net) {
        NodeType node = null;
        for (NodeType n: net.getNode()) {
            if (nodeId.equals(n.getId())) {
                node = n;
                break;
            }
        }
        return node;
    }

    private ConditionExpressionType convertStringExpression(final String name) {
        ConditionExpressionType expr = new ConditionExpressionType();
        expr.setDescription(name);
        return expr;
    }

    private void translateEvent(final NetType net, final TypeEvent event) {
        EventType node = new EventType();
        id_map.put(event.getId(), String.valueOf(ids));
        event_ids.add(String.valueOf(ids));
        node.setId(String.valueOf(ids++));
        node.setName(event.getName());
        net.getNode().add(node);
    }

    private void translateFunction(final NetType net, final TypeFunction func) {
        TaskType task = new TaskType();
        id_map.put(func.getId(), String.valueOf(ids));
        task.setId(String.valueOf(ids++));
        task.setName(func.getName());
        if (func.getToProcess() != null) {
            if (func.getToProcess().getLinkToEpcId() != null) {
                task.setSubnetId(String.valueOf(func.getToProcess().getLinkToEpcId()));
                subnet_list.add(task);
            }
        }
        net.getNode().add(task);
    }

    private void translatePI(final NetType net, final TypeProcessInterface pi) {
        TaskType task = new TaskType();
        id_map.put(pi.getId(), String.valueOf(ids));
        task.setId(String.valueOf(ids++));
        task.setSubnetId(String.valueOf(pi.getToProcess().getLinkToEpcId()));
        subnet_list.add(task);
        net.getNode().add(task);
    }

    private void translateArc(final NetType net, final TypeArc arc) throws CanoniserException {
        if (arc.getFlow() != null && id_map.get(arc.getFlow().getSource()) != null && id_map.get(arc.getFlow().getTarget()) != null) {
            EdgeType edge = new EdgeType();
            id_map.put(arc.getId(), String.valueOf(ids));
            edge.setId(String.valueOf(ids++));
            edge.setSourceId(id_map.get(arc.getFlow().getSource()));
            edge.setTargetId(id_map.get(arc.getFlow().getTarget()));
            net.getEdge().add(edge);
            flow_source_id_list.add(edge.getSourceId());
        } else if (arc.getRelation() != null) {
            for (NodeType node : net.getNode()) {
                if (node.getId().equals(id_map.get(arc.getRelation().getSource()))) {
                    if (arc.getRelation().getType() != null && arc.getRelation().getType().equals("role")) {
                        ResourceTypeRefType ref = new ResourceTypeRefType();
                        TypeAttribute att = new TypeAttribute();
                        id_map.put(arc.getId(), String.valueOf(ids));
                        att.setName("RefID");
                        att.setValue(String.valueOf(ids++));
                        ref.getAttribute().add(att);
                        ref.setResourceTypeId(id_map.get(arc.getRelation().getTarget()));
                        if (role_ref.get(arc.getRelation().getSource()) != null) {
                            // TODO optional removed from CPF schema
                            // ref.setOptional(role_ref.get(arc.getRelation().getSource()).isOptional());
                            ref.setQualifier(role_ref.get(arc.getRelation().getSource()).getDescription()); // / update
                        }
                        ((WorkType) node).getResourceTypeRef().add(ref);
                    } else {
                        ObjectRefType ref = new ObjectRefType();
                        TypeAttribute att = new TypeAttribute();
                        id_map.put(arc.getId(), String.valueOf(ids));
                        att.setName("RefID");
                        att.setValue(String.valueOf(ids++));
                        ref.getAttribute().add(att);
                        ref.setObjectId(id_map.get(arc.getRelation().getTarget()));
                        ref.setType(InputOutputType.OUTPUT);
                        if (obj_ref.get(arc.getRelation().getTarget()) != null) {
                            ref.setOptional(obj_ref.get(arc.getRelation().getTarget()).isOptional());
                            ref.setConsumed(obj_ref.get(arc.getRelation().getTarget()).isConsumed());
                        }
                        ((WorkType) node).getObjectRef().add(ref);
                    }
                } else if (node.getId().equals(id_map.get(arc.getRelation().getTarget()))) {
                    if (arc.getRelation().getType() != null && arc.getRelation().getType().equals("role")) {
                        ResourceTypeRefType ref = new ResourceTypeRefType();
                        TypeAttribute att = new TypeAttribute();
                        id_map.put(arc.getId(), String.valueOf(ids));
                        att.setName("RefID");
                        att.setValue(String.valueOf(ids++));
                        ref.getAttribute().add(att);
                        ref.setResourceTypeId(id_map.get(arc.getRelation().getSource()));
                        ((WorkType) node).getResourceTypeRef().add(ref);
                    } else {
                        ObjectRefType ref = new ObjectRefType();
                        TypeAttribute att = new TypeAttribute();
                        id_map.put(arc.getId(), String.valueOf(ids));
                        att.setName("RefID");
                        att.setValue(String.valueOf(ids++));
                        ref.getAttribute().add(att);
                        ref.setObjectId(id_map.get(arc.getRelation().getSource()));
                        ref.setType(InputOutputType.INPUT);
                        ((WorkType) node).getObjectRef().add(ref);
                    }
                }
            }
        } else {
            throw new CanoniserException("Could not find source element or target element for EPC arc '" + arc.getId() + "'!");
        }
    }

    private void translateObject(final NetType net, final TypeObject obj) {
        if (obj.getDefRef() != null && def_ref.get(obj.getDefRef()) != null) {
            id_map.put(obj.getId(), def_ref.get(obj.getDefRef()));
        } else {
            ObjectType object = new ObjectType();
            id_map.put(obj.getId(), String.valueOf(ids));
            object.setId(String.valueOf(ids));
            object.setName(obj.getName());
            net.getObject().add(object);
            def_ref.put(obj.getDefRef(), String.valueOf(ids++));
        }
        obj_ref.put(obj.getId(), obj);
    }

    private void translateRole(final TypeRole role) {
        if (role.getDefRef() != null && def_ref.get(role.getDefRef()) != null) {
            id_map.put(role.getId(), def_ref.get(role.getDefRef()));
        } else {
            HumanType obj = new HumanType();
            id_map.put(role.getId(), String.valueOf(ids));
            obj.setId(String.valueOf(ids));
            obj.setName(role.getName());
            cproc.getResourceType().add(obj);
            def_ref.put(role.getDefRef(), String.valueOf(ids++));
        }
        role_ref.put(role.getId(), role);
    }

}
