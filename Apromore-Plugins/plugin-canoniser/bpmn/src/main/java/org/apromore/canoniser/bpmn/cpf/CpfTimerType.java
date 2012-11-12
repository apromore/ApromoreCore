package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.TimerExpressionType;
import org.apromore.cpf.TimerType;
import org.omg.spec.bpmn._20100524.model.TBoundaryEvent;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TFormalExpression;
import org.omg.spec.bpmn._20100524.model.TIntermediateThrowEvent;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TTimerEventDefinition;

/**
 * CPF 1.0 timer event with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfTimerType extends TimerType implements CpfEventType {

    /** Logger. */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

    /** Second superclass. */
    private final CpfEventTypeImpl super2 = new CpfEventTypeImpl();

    // Constructors

    /** No-arg constructor. */
    public CpfTimerType() { }

    /**
     * Construct a CPF Timer corresponding to a BPMN Boundary Event.
     *
     * @param endEvent  a BPMN Boundary Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TBoundaryEvent boundaryEvent, final Initializer initializer) throws CanoniserException {
        super2.construct(this, boundaryEvent, initializer);

        assert boundaryEvent.getEventDefinition().get(0).getValue() instanceof TTimerEventDefinition;
        TTimerEventDefinition ted = (TTimerEventDefinition) boundaryEvent.getEventDefinition().get(0).getValue();

        DatatypeFactory datatypeFactory;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) { throw new CanoniserException(e); }

        if (ted.getTimeDate() != null) {
            if (ted.getTimeDate() instanceof TFormalExpression) {
                TFormalExpression fe = (TFormalExpression) ted.getTimeDate();

                Set<QName> supportedFormats = new HashSet<QName>();  // TODO - use diamond operator
                supportedFormats.add(DatatypeConstants.DATE);
                supportedFormats.add(DatatypeConstants.DATETIME);
                supportedFormats.add(DatatypeConstants.TIME);

                if (supportedFormats.contains(fe.getEvaluatesToTypeRef())) {
                    if (fe.getContent().size() == 1 && fe.getContent().get(0) instanceof String) {
                        setTimeDate(datatypeFactory.newXMLGregorianCalendar((String) fe.getContent().get(0)));
                    } else { logger.info("Timer date content is not a single String"); }
                } else { logger.info("Timer date is not a recognized type"); }
            } else { logger.info("Timer date is not formal"); }
        }

        if (ted.getTimeDuration() != null) {
            if (ted.getTimeDuration() instanceof TFormalExpression) {
                TFormalExpression fe = (TFormalExpression) ted.getTimeDuration();

                if (fe.getContent().size() == 1 && fe.getContent().get(0) instanceof String) {
                    setTimeDuration(datatypeFactory.newDuration((String) fe.getContent().get(0)));
                } else { logger.info("Timer duration content is not a single String"); }
            } else { logger.info("Timer duration is not formal"); }
        }

        if (ted.getTimeCycle() != null) {
            if (ted.getTimeCycle() instanceof TFormalExpression) {
                TFormalExpression fe = (TFormalExpression) ted.getTimeCycle();

                if (fe.getContent().size() == 1 && fe.getContent().get(0) instanceof String) {
                    TimerExpressionType te = new TimerExpressionType();
                    te.setExpression((String) fe.getContent().get(0));
                    setTimeExpression(te);
                } else { logger.info("Timer cycle content is not a single String"); }
            } else { logger.info("Timer cycle is not formal"); }
        }

        // TODO - ensure that no more than one of the time fields gets populated
    }

    /**
     * Construct a CPF Timer corresponding to a BPMN End Event.
     *
     * @param endEvent  a BPMN End Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TEndEvent endEvent, final Initializer initializer) throws CanoniserException {
        super2.construct(this, endEvent, initializer);

        assert endEvent.getEventDefinition().get(0).getValue() instanceof TTimerEventDefinition;
        TTimerEventDefinition ted = (TTimerEventDefinition) endEvent.getEventDefinition().get(0).getValue();

        if (ted.getTimeDate() instanceof TFormalExpression) {
            TFormalExpression e = (TFormalExpression) ted.getTimeDate();
            QName XSD_DATETIME = new QName("http://www.w3.org/2001/XMLSchema#", "dateTime");
            if (XSD_DATETIME.equals(e.getEvaluatesToTypeRef())) {
                logger.info("Timer content " + e.getContent());
            } else { logger.info("Timer is not a recognized type"); }
        } else { logger.info("Timer is not formal"); }

        // Only one of the following fields may be populated
        /*
        setTimeDate((XMLGregorianCalendar) ted.getTimeDate());
        setTimeDuration((Duration) ted.getTimeDuration());
        setTimeExpression((TimerExpressionType) ted.getTimeCycle());
        */
    }

    /**
     * Construct a CPF Timer corresponding to a BPMN Intermediate Throw Event.
     *
     * @param endEvent  a BPMN Intermediate Throw Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TIntermediateThrowEvent intermediateThrowEvent, final Initializer initializer) throws CanoniserException {
        super2.construct(this, intermediateThrowEvent, initializer);
    }

    /**
     * Construct a CPF Timer corresponding to a BPMN Start Event.
     *
     * @param startEvent  a BPMN Start Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TStartEvent startEvent, final Initializer initializer) throws CanoniserException {
        super2.construct(this, startEvent, initializer);
    }

    // Second superclass methods

    /** {@inheritDoc} */
    public Set<EdgeType> getIncomingEdges() {
        return super2.getIncomingEdges();
    }

    /** {@inheritDoc} */
    public Set<EdgeType> getOutgoingEdges() {
        return super2.getOutgoingEdges();
    }

    /** {@inheritDoc} */
    public JAXBElement<? extends TFlowNode> toBpmn(final org.apromore.canoniser.bpmn.bpmn.Initializer initializer) throws CanoniserException {
        return CpfEventTypeImpl.toBpmn(this, initializer);
    }
}
