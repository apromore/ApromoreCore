package org.apromore.canoniser.bpmn.anf;

// Java 2 Standard packages
import javax.xml.bind.annotation.XmlRegistry;

/**
 * Element factory for an ANF 0.3 object model with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@XmlRegistry
public class ObjectFactory extends org.apromore.anf.ObjectFactory {

    @Override
    public AnfAnnotationsType createAnnotationsType() {
        return new AnfAnnotationsType();
    }

    @Override
    public AnfDocumentationType createDocumentationType() {
        return new AnfDocumentationType();
    }

    @Override
    public AnfGraphicsType createGraphicsType() {
        return new AnfGraphicsType();
    }

    @Override
    public AnfPositionType createPositionType() {
        return new AnfPositionType();
    }

    @Override
    public AnfSizeType createSizeType() {
        return new AnfSizeType();
    }
}
