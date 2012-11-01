package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import javax.xml.bind.annotation.XmlRegistry;

// Local packages
import org.omg.spec.bpmn._20100524.model.ObjectFactory;

/**
 * Element factory for a BPMN 2.0 object model with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@XmlRegistry
public class BpmnObjectFactory extends ObjectFactory {

    @Override
    public BpmnAssociation createTAssociation() {
        return new BpmnAssociation();
    }

    @Override
    public BpmnCallActivity createTCallActivity() {
        return new BpmnCallActivity();
    }

    @Override
    public BpmnDefinitions createTDefinitions() {
        return new BpmnDefinitions();
    }

    @Override
    public BpmnLane createTLane() {
        return new BpmnLane();
    }

    @Override
    public BpmnParticipant createTParticipant() {
        return new BpmnParticipant();
    }

    @Override
    public BpmnProcess createTProcess() {
        return new BpmnProcess();
    }

    @Override
    public BpmnSubProcess createTSubProcess() {
        return new BpmnSubProcess();
    }

    @Override
    public BpmnSequenceFlow createTSequenceFlow() {
        return new BpmnSequenceFlow();
    }

    @Override
    public BpmnTask createTTask() {
        return new BpmnTask();
    }
}
