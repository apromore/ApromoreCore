package org.apromore.canoniser.bpmn;

// Java 2 Standard packges
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.AnnotationType;
//import com.processconfiguration.bpmn.visitor.BaseVisitor;
import org.apromore.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.Definitions;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TInclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TRootElement;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TTask;
import org.omg.spec.dd._20100524.di.DiagramElement;
import org.omg.spec.dd._20100524.di.Plane;

/**
 * BPMN 2.0 object model with canonisation methods.
 *
 * This also supports extensions to BPMN for configurable models.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.3
 */
@XmlRootElement
public class CanoniserDefinitions extends Definitions {

    /**
     * Logger.  Named after the class.
     */
    private static final Logger logger = Logger.getLogger(CanoniserDefinitions.class.getCanonicalName());

    public AnnotationsType getANF() { return null; }
    public CanonicalProcessType getCPF() { return null; }

    /**
     * @return a pair of CPF/ANF documents expressing an equivalent model to this one
     */
/*
    Pair<CanonicalProcess, Annotations> toCanonical() {
        final CanonicalProcess cpf = new CanonicalProcess();
        final Annotations anf = new Annotations();

        // Traverse diagram
        logger.info("Traversing diagrams");
        for (BPMNDiagram diagram : getBPMNDiagrams()) {
            logger.info("Annotating a diagram " + ((Plane) diagram.getBPMNPlane()).getDiagramElements());
            for (JAXBElement<? extends DiagramElement> element : diagram.getBPMNPlane().getDiagramElements()) {
                logger.info("Annotating an element " + element);
                element.getValue().accept(new BaseVisitor() {
                    @Override
                    public void visit(final BPMNEdge edge) {
                        logger.info("Annotating an edge");
                        AnnotationType annotation = new AnnotationType();
                        annotation.setCpfId(edge.getBpmnElement().toString());
                        anf.getAnnotations().add(annotation);
                    }
                    @Override
                    public void visit(final BPMNShape shape) {
                        logger.info("Annotating a shape");
                        AnnotationType annotation = new AnnotationType();
                        annotation.setCpfId(shape.getBpmnElement().toString());
                        anf.getAnnotations().add(annotation);
                    }
                });
            }
        }

        // Traverse processes
        logger.info("Traversing processes");
        for (JAXBElement<? extends TRootElement> rootElement : getRootElements()) {
            rootElement.getValue().accept(new BaseVisitor() {
                @Override
                public void visit(final TProcess process) {
                    final NetType net = new NetType();
                    net.setId(process.getId());
                    cpf.getNets().add(net);

                    for (JAXBElement<? extends TFlowElement> flowElement : process.getFlowElements()) {
                        flowElement.getValue().accept(new BaseVisitor() {
                            @Override
                            public void visit(final TEndEvent endEvent) {
                                NodeType node = new NodeType();
                                node.setId(endEvent.getId());
                                net.getNodes().add(node);
                            }
                            @Override
                            public void visit(final TInclusiveGateway inclusiveGateway) {
                                NodeType node = new NodeType();
                                node.setId(inclusiveGateway.getId());
                                net.getNodes().add(node);
                            }
                            @Override
                            public void visit(final TStartEvent startEvent) {
                                NodeType node = new NodeType();
                                node.setId(startEvent.getId());
                                net.getNodes().add(node);
                            }
                            @Override
                            public void visit(final TSequenceFlow sequenceFlow) {
                                EdgeType edge = new EdgeType();
                                edge.setId(sequenceFlow.getId());
                                edge.setSourceId(((TFlowNode) sequenceFlow.getSourceRef()).getId());
                                edge.setTargetId(((TFlowNode) sequenceFlow.getTargetRef()).getId());
                                net.getEdges().add(edge);
                            }
                            @Override
                            public void visit(final TTask task) {
                                NodeType node = new NodeType();
                                node.setId(task.getId());
                                net.getNodes().add(node);
                            }
                        });
                    }
                }
            });
        }

        // Fake return value
        return new Pair(cpf, anf);
    };
*/
}
