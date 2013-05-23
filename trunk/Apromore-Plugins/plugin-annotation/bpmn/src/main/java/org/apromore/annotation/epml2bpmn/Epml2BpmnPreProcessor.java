package org.apromore.annotation.epml2bpmn;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SizeType;
import org.apromore.annotation.DefaultAbstractAnnotationProcessor;
import org.apromore.annotation.exception.AnnotationProcessorException;
import org.apromore.annotation.model.AnnotationData;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.PluginResultImpl;
import org.apromore.plugin.message.PluginMessageImpl;
import org.springframework.stereotype.Component;

/**
 * EPML to BPMN Post Processor.
 * Used to manipulate the ANF of the BPMN output when the input process langauge was EPML.
 * Used to change the size of the shapes as each language has different sizes elements.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Component("epml2bpmnPreAnnotationProcessor")
public class Epml2BpmnPreProcessor extends DefaultAbstractAnnotationProcessor {

    private static final BigDecimal divisor = new BigDecimal(2.0);
    private static final BigDecimal newEventHeight = new BigDecimal(30.0);
    private static final BigDecimal newEventWidth = new BigDecimal(30.0);
    private static final BigDecimal newTaskHeight = new BigDecimal(80.0);
    private static final BigDecimal newTaskWidth = new BigDecimal(100.0);
    private static final BigDecimal newGatewayHeight = new BigDecimal(40.0);
    private static final BigDecimal newGatewayWidth = new BigDecimal(40.0);

    @Override
    @SuppressWarnings("unchecked")
    public PluginResult processAnnotation(CanonicalProcessType canonisedFormat, AnnotationsType annotationFormat)
            throws AnnotationProcessorException {
        PluginResult pluginResult = new PluginResultImpl();

        if (canonisedFormat == null) {
            pluginResult.getPluginMessage().add(new PluginMessageImpl("Canonised model passed into the Post Processor is Empty."));
        } else {
            try {
                Map<String, AnnotationData> annotations = new HashMap<>();

                manipulateBPMNShapes(canonisedFormat, annotationFormat, annotations);
                manipulateBPMNEdges(canonisedFormat, annotationFormat, annotations);
            } catch (Exception e) {
                throw new AnnotationProcessorException("Failed to execute the Post Processing.", e);
            }
        }

        return pluginResult;
    }


    /* loop through the list of nodes and process each one. */
    private void manipulateBPMNShapes(CanonicalProcessType cpf, AnnotationsType anf, Map<String, AnnotationData> annotations) {
        NodeType node;
        GraphicsType graphicsType;

        for (AnnotationType annType : anf.getAnnotation()) {
            if (annType instanceof GraphicsType) {
                graphicsType = (GraphicsType) annType;
                node = findCPFNode(cpf, annType.getCpfId());
                if (node != null) {
                    if (node instanceof EventType) {
                        manipulateEventSize(graphicsType, node, annotations);
                    } else if (node instanceof TaskType) {
                        manipulateTaskSize(graphicsType, node, annotations);
                    } else if (node instanceof RoutingType) {
                        manipulateGatewaySize(graphicsType, node, annotations);
                    }
                }
            }
        }
    }

    /* loop through the list of edges and process each one. */
    private void manipulateBPMNEdges(CanonicalProcessType cpf, AnnotationsType anf, Map<String, AnnotationData> annotations) {
        EdgeType edge;
        GraphicsType graphicsType;

        for (AnnotationType annType : anf.getAnnotation()) {
            if (annType instanceof GraphicsType) {
                graphicsType = (GraphicsType) annType;
                edge = findCPFEdge(cpf, annType.getCpfId());
                if (edge != null) {
                    manipulateEdgePosition(graphicsType, edge, annotations);
                }
            }
        }
    }



    /* Changes the position on the Edges From and Two coordinates. */
    private void manipulateEdgePosition(GraphicsType graphicsType, EdgeType edge, Map<String, AnnotationData> annotations) {
        AnnotationData annData;
        if (annotations.containsKey(edge.getSourceId())) {
            annData = annotations.get(edge.getSourceId());
            changeLocation(graphicsType, annData);
        }
        if (annotations.containsKey(edge.getTargetId())) {
            annData = annotations.get(edge.getTargetId());
            changeLocation(graphicsType, annData);
        }
    }

    /* Changes the position of the edges X and Y. */
    private void changeLocation(GraphicsType graphicsType, AnnotationData annData) {
        PositionType newPos = new PositionType();
        Map<PositionType, PositionType> replace = new HashMap<>();

        for (PositionType pos : graphicsType.getPosition()) {
            if ((pos.getX().compareTo(annData.getOldX()) >= 0) &&
                    (pos.getX().compareTo(annData.getOldX().add(annData.getOldW())) <= 0) &&
                    (pos.getY().compareTo(annData.getOldY()) >= 0) &&
                    (pos.getY().compareTo(annData.getOldY().add(annData.getOldH())) <= 0)) {
                newPos.setX(annData.getNewX().add(annData.getNewH()).divide(divisor));
                newPos.setY(annData.getNewY().add(annData.getNewH()).divide(divisor));

                replace.put(pos, newPos);
            }
        }

        for (Map.Entry<PositionType, PositionType> pos : replace.entrySet()) {
            graphicsType.getPosition().remove(pos.getKey());
            graphicsType.getPosition().add(pos.getValue());
        }
    }


    /* Find a node in the CPF using the cpfId */
    private NodeType findCPFNode(CanonicalProcessType cpf, String cpfId) {
        NodeType result = null;

        for (NetType net : cpf.getNet()) {
            for (NodeType node : net.getNode()) {
                if (node.getId().equals(cpfId)) {
                    result = node;
                    break;
                }
            }
        }

        return result;
    }

    /* Find an edge in the CPF using the cpfId */
    private EdgeType findCPFEdge(CanonicalProcessType cpf, String cpfId) {
        EdgeType result = null;

        for (NetType net : cpf.getNet()) {
            for (EdgeType edge : net.getEdge()) {
                if (edge.getId().equals(cpfId)) {
                    result = edge;
                    break;
                }
            }
        }

        return result;
    }



    /* Changes the size of the Gateway Node. */
    private void manipulateGatewaySize(GraphicsType annType, NodeType node, Map<String, AnnotationData> annotations) {
        BigDecimal oldH = annType.getSize().getHeight();
        BigDecimal oldW = annType.getSize().getWidth();
        BigDecimal oldX = annType.getPosition().get(0).getX();
        BigDecimal oldY = annType.getPosition().get(0).getY();

        BigDecimal newX = oldW.subtract(newGatewayHeight).divide(divisor).add(oldX);
        BigDecimal newY = oldH.subtract(newGatewayHeight).divide(divisor).add(oldY);

        SizeType size = new SizeType();
        size.setHeight(newGatewayHeight);
        size.setWidth(newGatewayWidth);
        PositionType position = new PositionType();
        position.setX(newX);
        position.setY(newY);

        annType.setSize(size);
        annType.getPosition().remove(0);
        annType.getPosition().add(position);

        annotations.put(node.getId(), new AnnotationData(node.getId(), oldX, oldY, newX, newY, oldH, oldW, newGatewayHeight, newGatewayWidth));
    }

    /* Changes the size of the Task Node. */
    private void manipulateTaskSize(GraphicsType annType, NodeType node, Map<String, AnnotationData> annotations) {
        BigDecimal oldH = annType.getSize().getHeight();
        BigDecimal oldW = annType.getSize().getWidth();
        BigDecimal oldX = annType.getPosition().get(0).getX();
        BigDecimal oldY = annType.getPosition().get(0).getY();

        BigDecimal newX = oldW.subtract(newTaskWidth).divide(divisor).add(oldX);
        BigDecimal newY = oldH.subtract(newTaskHeight).divide(divisor).add(oldY);

        SizeType size = new SizeType();
        size.setHeight(newTaskHeight);
        size.setWidth(newTaskWidth);
        PositionType position = new PositionType();
        position.setX(newX);
        position.setY(newY);

        annType.setSize(size);
        annType.getPosition().remove(0);
        annType.getPosition().add(position);

        annotations.put(node.getId(), new AnnotationData(node.getId(), oldX, oldY, newX, newY, oldH, oldW, newTaskHeight, newTaskWidth));
    }

    /* Changes the size of the Event Node. */
    private void manipulateEventSize(GraphicsType annType, NodeType node, Map<String, AnnotationData> annotations) {
        BigDecimal oldH = annType.getSize().getHeight();
        BigDecimal oldW = annType.getSize().getWidth();
        BigDecimal oldX = annType.getPosition().get(0).getX();
        BigDecimal oldY = annType.getPosition().get(0).getY();

        BigDecimal newX = oldW.subtract(newEventWidth).divide(divisor).add(oldX);
        BigDecimal newY = oldH.subtract(newEventHeight).divide(divisor).add(oldY);

        SizeType size = new SizeType();
        size.setHeight(newEventHeight);
        size.setWidth(newEventWidth);
        PositionType position = new PositionType();
        position.setX(newX);
        position.setY(newY);

        annType.setSize(size);
        annType.getPosition().remove(0);
        annType.getPosition().add(position);

        annotations.put(node.getId(), new AnnotationData(node.getId(), oldX, oldY, newX, newY, oldH, oldW, newEventHeight, newEventWidth));
    }
}


//    private void processEdges(TDefinitions bpmnDef, Map<String, AnnotationData> annotations) {
//        Point point;
//        BPMNEdge edge;
//        TBaseElement element;
//
//        for (BPMNDiagram diagram : bpmnDef.getBPMNDiagram()) {
//            for (JAXBElement<? extends DiagramElement> bpmnShape : diagram.getBPMNPlane().getDiagramElement()) {
//                if (bpmnShape.getValue() instanceof BPMNEdge) {
//                    edge = (BPMNEdge) bpmnShape.getValue();
//                    element = findBpmnElement(bpmnDef, edge.getId());
//
//                    if (element instanceof TDataAssociation) {
//                        processDataAssociation();
//                    } else if (element instanceof TMessageFlow) {
//                        processMessageFlow();
//                    } else if (element instanceof TSequenceFlow) {
//                        processSequenceFlow();
//                    }
//                }
//            }
//        }
//    }
//
//
//    /* Process Shapes */
//    private void processShapes(TDefinitions bpmnDef, Map<String, AnnotationData> annotations) {
//        Bounds bounds;
//        BPMNShape shape;
//        TBaseElement element;
//
//        for (BPMNDiagram diagram : bpmnDef.getBPMNDiagram()) {
//            for (JAXBElement<? extends DiagramElement> bpmnShape : diagram.getBPMNPlane().getDiagramElement()) {
//                if (bpmnShape.getValue() instanceof BPMNShape) {
//                    shape = (BPMNShape) bpmnShape.getValue();
//                    element = findBpmnElement(bpmnDef, shape.getId());
//
//                    bounds = new Bounds();
//
//                    double oldH = bounds.getHeight();
//                    double oldW = bounds.getWidth();
//                    double oldX = shape.getBounds().getX();
//                    double oldY = shape.getBounds().getY();
//                    if (element instanceof TEvent || element instanceof TActivity) {
//                        bounds.setHeight(newEHeight);
//                        bounds.setWidth(newEWidth);
//                        bounds.setX(oldX + (shape.getBounds().getWidth() - newEWidth) / 2);
//                        bounds.setY(oldY + (shape.getBounds().getHeight() - newEHeight) / 2);
//                    } else if (element instanceof TGateway) {
//                        bounds.setHeight(newRHeight);
//                        bounds.setWidth(newRWidth);
//                        bounds.setX(oldX + (shape.getBounds().getWidth() - newRWidth) / 2);
//                        bounds.setY(oldY + (shape.getBounds().getHeight() - newRHeight) / 2);
//                    }
//
//                    annotations.put(shape.getId(), new AnnotationData(shape.getId(), oldX, oldY,
//                            oldX + (shape.getBounds().getWidth() - newEWidth) / 2, oldY + (shape.getBounds().getHeight() - newEHeight) / 2,
//                            oldH, oldW, bounds.getHeight(), bounds.getWidth()));
//
//                    shape.setBounds(bounds);
//                }
//            }
//        }
//    }
//
//    /* Find the corresponding element for this shapeId */
//    @SuppressWarnings("unchecked")
//    private TBaseElement findBpmnElement(TDefinitions bpmnDef, String shapeId) {
//        TProcess process;
//        TRootElement element;
//        TBaseElement foundElement = null;
//        for (JAXBElement<? extends TRootElement> rootElement : bpmnDef.getRootElement()) {
//            element = rootElement.getValue();
//
//            if (element instanceof TProcess) {
//                process = (TProcess) element;
//                for (JAXBElement<? extends TFlowElement> processElement : process.getFlowElement()) {
//                    foundElement = processElement.getValue();
//                    if (foundElement.getId().equals(shapeId)) {
//                        break;
//                    }
//                }
//            }
//        }
//        return foundElement;
//    }
//
//
//    /* Unmarshal the BPMN Format from the provided InputStream. */
//    @SuppressWarnings("unchecked")
//    private TDefinitions unmarshalBPMN(final ByteArrayOutputStream bpmnFormat) throws JAXBException, SAXException {
//        final JAXBContext jc = getJAXBContext();
//        final Unmarshaller u = jc.createUnmarshaller();
//        return ((JAXBElement<TDefinitions>) u.unmarshal(new ByteArrayInputStream(bpmnFormat.toByteArray()))).getValue();
//    }
//
//    /* builds the JAXB context. */
//    private JAXBContext getJAXBContext() throws JAXBException {
//        return JAXBContext.newInstance(org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
//                org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
//                org.omg.spec.dd._20100524.dc.ObjectFactory.class,
//                org.omg.spec.dd._20100524.di.ObjectFactory.class);
//    }
//}
