package org.apromore.canoniser.bpmn.anf;

// Java 2 Standard packages
import javax.xml.bind.JAXBElement;
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.BaseVisitor;
import org.omg.spec.dd._20100524.di.DiagramElement;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.bpmn.IdFactory;

/**
 * ANF 0.3 top-level document element.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class AnfAnnotationsType extends AnnotationsType {

    /** No-arg constructor. */
    public AnfAnnotationsType() {
        super();
    }

    /**
     * Construct an ANF document corresponding to a BPMNDI Diagram element.
     *
     * The resulting document must have its <code>uri</code> element set in order to be schema-legal.
     *
     * @param diagram  a BPMNDI Diagram element
     */
    public AnfAnnotationsType(final BPMNDiagram diagram) {

       // Generator for identifiers scoped to this ANF document
       final IdFactory anfIdFactory = new IdFactory();

       // Add an ANF Annotation for each BPMNDI DiagramElement
       for (JAXBElement<? extends DiagramElement> element : diagram.getBPMNPlane().getDiagramElement()) {
            element.getValue().accept(new BaseVisitor() {
                @Override
                public void visit(final BPMNEdge edge) {
                    getAnnotation().add(new AnfAnnotationType(edge, anfIdFactory));
                }

                @Override
                public void visit(final BPMNShape shape) {
                    getAnnotation().add(new AnfAnnotationType(shape, anfIdFactory));
                }
            });
        }
    }
}
