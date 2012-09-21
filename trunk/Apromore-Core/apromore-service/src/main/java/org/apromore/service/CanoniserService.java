package org.apromore.service;

import java.io.InputStream;

import javax.activation.DataSource;
import javax.xml.bind.JAXBException;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.SerializationException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.service.model.CanonisedProcess;

/**
 * Interface for the Canoniser Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface CanoniserService {

    /**
     * Convert the CPF Graph to XML.
     * @param cpt the CPT
     * @return the CPF as a String
     */
    String CPFtoString(CanonicalProcessType cpt) throws JAXBException;

    /**
     * Generate cpf and anf from process_xml which is specified in language nativeType.
     * If cpf_uri is equal to 0, take it from process_xml
     *
     * @param nativeType  the native type
     * @param cpf_uri     the cpf uri
     * @param process_xml the processXML to canonise
     * @return CanonisedProcess populated with the CPF and ANF details.
     * @throws org.apromore.exception.CanoniserException
     *          something failed
     * @throws org.apromore.canoniser.exception.CanoniserException
     */
    CanonisedProcess canonise(String nativeType, String cpf_uri, InputStream process_xml) throws CanoniserException;

    /**
     * DeCanonise a process.
     *
     * @param processId  the processId of the Canonical format.
     * @param version    the version of the canonical format
     * @param nativeType the processes original format
     * @param cpf_is     the Canonical format inputStream
     * @param anf_is     the annotation inputStream
     * @return the DeCanonised model inputStream
     * @throws org.apromore.exception.CanoniserException
     *          something failed
     */
    DataSource deCanonise(final Integer processId, final String version, final String nativeType,
                          final CanonicalProcessType canType, final DataSource anf_is) throws CanoniserException;


    /**
     * Serializes a Graph to a cpf.
     *
     * @param graph The process Model Graph.
     * @return the the CPF format of the process Model Graph.
     * @throws SerializationException if the conversion from a CPF to graph fails.
     */
    CanonicalProcessType serializeCPF(CPF graph) throws SerializationException;

    /**
     * De-serializes a CPF to a Process Model Graph.
     *
     * @param cpf the CPF Process Model to convert.
     * @return the process Model Graph of the conversion
     * @throws SerializationException if the conversion from a CPF to graph fails.
     */
    CPF deserializeCPF(CanonicalProcessType cpf) throws SerializationException;

}
