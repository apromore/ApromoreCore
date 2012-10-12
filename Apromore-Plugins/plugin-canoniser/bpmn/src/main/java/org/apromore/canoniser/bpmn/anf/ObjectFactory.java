package org.apromore.canoniser.bpmn.anf;

// Java 2 Standard packages
import javax.xml.bind.annotation.XmlRegistry;

// Local packages
import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;

/**
 * Element factory for an ANF 0.3 object model with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@XmlRegistry
public class ObjectFactory extends org.apromore.anf.ObjectFactory {

    @Override
    public AnnotationsType createAnnotationsType() {
        return new AnfAnnotationsType();
    }

    @Override
    public AnnotationType createAnnotationType() {
        return new AnfAnnotationType();
    }
}
