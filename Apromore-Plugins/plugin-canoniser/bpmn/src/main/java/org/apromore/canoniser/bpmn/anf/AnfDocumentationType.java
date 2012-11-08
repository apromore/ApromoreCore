package org.apromore.canoniser.bpmn.anf;

// Local packages
import org.apromore.anf.DocumentationType;
import org.apromore.canoniser.bpmn.IdFactory;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TDocumentation;

/**
 * ANF 0.3 documentation annotation element.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class AnfDocumentationType extends DocumentationType {

    /** No-arg constructor. */
    public AnfDocumentationType() { }

    /**
     * Construct an ANF documentation annotation for a BPMN Documentation element.
     *
     * @param bpmnDocumentation  a BPMN Documentation element
     * @param parent  the parent element of <code>bpmnDocumentation</code>
     * @param anfIdFactory  generator for identifiers
     */
    public AnfDocumentationType(final TDocumentation bpmnDocumentation,
                                final TBaseElement   parent,
                                final IdFactory      anfIdFactory) {

        setId(anfIdFactory.newId(bpmnDocumentation.getId()));
        setCpfId(parent.getId());  // TODO - process through cpfIdFactory instead

        getDocumentation().addAll(bpmnDocumentation.getContent());
    }
}
