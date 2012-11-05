package org.apromore.canoniser.bpmn;

/**
 * BPMN canoniser constants
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public interface Constants {

    /**
     * Apromore URI.
     *
     * This is used for the definitions/@exporter attribute.
     */
    final String APROMORE_URI = "http://apromore.org";

    /**
     * Apromore version.
     *
     * This is used for the definitions/@exporterVersion attribute.
     */
    final String APROMORE_VERSION = "0.4";

    /**
     * BPMN 2.0 namespace.
     */
    final String BPMN_NS = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    /**
     * Location of the BPMN 2.0 schema within the classpath.
     */
    final String BPMN_XSD = "xsd/BPMN20.xsd";
}
