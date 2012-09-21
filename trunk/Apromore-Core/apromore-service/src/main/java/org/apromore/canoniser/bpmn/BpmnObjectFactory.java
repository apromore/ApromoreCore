package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import javax.xml.bind.annotation.XmlRegistry;

// Local packages
import org.omg.spec.bpmn._20100524.model.ObjectFactory;

/**
 * Element factory for a BPMN 2.0 object model with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.3
 */
@XmlRegistry
public class BpmnObjectFactory extends ObjectFactory {

    @Override
    public CanoniserDefinitions createDefinitions() {
        return new CanoniserDefinitions();
    }
}
