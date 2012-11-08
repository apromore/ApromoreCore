package org.apromore.canoniser.bpmn;

/**
 * JAXB properties.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public interface JAXBConstants {

    /** Property name for use with {@link Unmarshaller#setProperty} to configure a {@link com.sun.xml.bind.IDResolver}. */
    String ID_RESOLVER = "com.sun.xml.bind.IDResolver";

    /** Property name for use with {@link Unmarshaller#setProperty} to configure an alternate JAXB ObjectFactory. */
    String OBJECT_FACTORY = "com.sun.xml.bind.ObjectFactory";
}
