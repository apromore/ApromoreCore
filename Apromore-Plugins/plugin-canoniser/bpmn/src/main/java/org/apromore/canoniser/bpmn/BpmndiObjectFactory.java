package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import javax.xml.bind.annotation.XmlRegistry;

// Local packages
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.ObjectFactory;

/**
 * Element factory for a BPMNDI 2.0 layout model with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@XmlRegistry
public class BpmndiObjectFactory extends ObjectFactory {

    @Override
    public BPMNDiagram createBPMNDiagram() {
        return new BpmndiDiagram();
    }
}
