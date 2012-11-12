package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import javax.xml.datatype.DatatypeConstants;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.canoniser.bpmn.cpf.CpfTimerType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TFormalExpression;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TTimerEventDefinition;

/**
 * BPMN Start Event with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnStartEvent extends TStartEvent {

    /** No-arg constructor. */
    public BpmnStartEvent() { }

    /**
     * Construct a BPMN Start Event corresponding to a CPF Event.
     *
     * @param cpfEvent  a CPF event
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the data object can't be constructed
     */
    public BpmnStartEvent(final CpfEventType cpfEvent, final Initializer initializer) throws CanoniserException {

        initializer.populateFlowNode(this, cpfEvent);

        if (cpfEvent instanceof CpfTimerType) {
            CpfTimerType cpfTimer = (CpfTimerType) cpfEvent;

            if (cpfTimer.getTimeDate() != null) {
                TTimerEventDefinition ted = new TTimerEventDefinition();
                TFormalExpression fe = new TFormalExpression();
                try {
                    fe.setEvaluatesToTypeRef(cpfTimer.getTimeDate().getXMLSchemaType());
                } catch (IllegalStateException e) { /* skip @evaluatesToTypeRef if no XSD datatype matches */ }
                fe.getContent().add(cpfTimer.getTimeDate().toXMLFormat());
                ted.setTimeDate(fe);
                getEventDefinition().add(initializer.getFactory().createTimerEventDefinition(ted));
            }

            if (cpfTimer.getTimeDuration() != null) {
                TTimerEventDefinition ted = new TTimerEventDefinition();
                TFormalExpression fe = new TFormalExpression();
                try {
                    fe.setEvaluatesToTypeRef(cpfTimer.getTimeDuration().getXMLSchemaType());
                } catch (IllegalStateException e) { /* skip @evaluatesToTypeRef if no XSD datatype matches */ }
                fe.getContent().add(cpfTimer.getTimeDuration().toString());
                ted.setTimeDuration(fe);
                getEventDefinition().add(initializer.getFactory().createTimerEventDefinition(ted));
            }

            if (cpfTimer.getTimeExpression() != null) {
                TTimerEventDefinition ted = new TTimerEventDefinition();
                TFormalExpression fe = new TFormalExpression();
                fe.getContent().add(cpfTimer.getTimeExpression().getExpression());
                ted.setTimeCycle(fe);
                getEventDefinition().add(initializer.getFactory().createTimerEventDefinition(ted));
            }
        }
    }
}
