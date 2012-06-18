package org.apromore.service;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.CanoniserException;
import org.apromore.exception.SerializationException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.service.model.CanonisedProcess;
import org.xml.sax.SAXException;

import javax.activation.DataSource;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for the Canoniser Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface CanoniserService {

    /**
     * Generate cpf and anf from process_xml which is specified in language nativeType.
     * If cpf_uri is equal to 0, take it from process_xml
     *
     * @param nativeType the native type
     * @param cpf_uri the cpf uri
     * @param process_xml the processXML to canonise
     * @return CanonisedProcess populated with the CPF and ANF details.
     * @throws org.apromore.exception.CanoniserException something failed
     */
    CanonisedProcess canonise(String nativeType, String cpf_uri, InputStream process_xml) throws CanoniserException,
            IOException, JAXBException, SAXException;

    /**
     * DeCanonise a process.
     * @param processId the processId of the Canonical format.
     * @param version the version of the canonical format
     * @param nativeType the processes original format
     * @param anf_is the annotation inputStream
     * @return the DeCanonised model inputStream
     */
    DataSource deCanonise(final Integer processId, final String version, final String nativeType,
            final CanonicalProcessType canType, final DataSource anf_is);


    /**
     * Serializes a Graph to a cpf.
     * @param graph The process Model Graph.
     * @return the the CPF format of the process Model Graph.
     * @throws SerializationException if the conversion from a CPF to graph fails.
     */
    CanonicalProcessType serializeCPF(CPF graph) throws SerializationException;

    /**
     * De-serializes a CPF to a Process Model Graph.
     * @param cpf the CPF Process Model to convert.
     * @return the process Model Graph of the conversion
     * @throws SerializationException if the conversion from a CPF to graph fails.
     */
    CPF deserializeCPF(CanonicalProcessType cpf) throws SerializationException;

}
