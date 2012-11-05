package org.apromore.canoniser.bpmn.cpf;

/**
 * Values for the {@link TypeAttribute#getName} field encoding otherwise-unrepresentable BPMN 2.0 content into CPF.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public interface ExtensionConstants {

    /** Prefix for all {@link TypeAttribute} names from this canoniser. */
    String BPMN_CPF_NS = "bpmn_cpf";

    /** Extension name for <code>bpmn:extensionElements</code> content. */
    String EXTENSION_ELEMENTS = "extensions";
}
