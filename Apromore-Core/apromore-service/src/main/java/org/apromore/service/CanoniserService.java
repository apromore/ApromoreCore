package org.apromore.service;

import java.io.InputStream;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.SerializationException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.DecanonisedProcess;

/**
 * Interface for the Canoniser Service. Defines all the methods that will do the majority of the work for the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface CanoniserService {
    
    /**
     * Lists all installed Canonisers for given native process format.
     *
     * @param nativeType that the Canoniser supports
     * @return Set of Canoniser that support the native process format
     * @throws PluginNotFoundException
     */
    Set<Canoniser> listByNativeType(String nativeType) throws PluginNotFoundException;

    /**
     * Finds first Canoniser with given native type
     *
     * @param nativeType that the Canoniser supports
     * @return Canoniser that support the native process format
     * @throws PluginNotFoundException
     */
    Canoniser findByNativeType(String nativeType) throws PluginNotFoundException;

    /**
     * Finds first Canoniser with given native type with given namen and specified version.
     *
     * @param nativeType that the Canoniser supports
     * @param name
     * @param version
     * @return Canoniser that support the native process format
     * @throws PluginNotFoundException
     */
    Canoniser findByNativeTypeAndNameAndVersion(String nativeType, String name, String version) throws PluginNotFoundException;

    /**
     * Canonise a process. Generate CPF and ANF from native XML which is specified in language nativeType. If cpfUri is equal to 0, take it from
     * nativeXml
     *
     * @param nativeType
     *            the native type
     * @param processXml
     *            the processXML to canonise
     * @param canoniserProperties
     * @return CanonisedProcess populated with the CPF and ANF and messages from the Canoniser
     * @throws CanoniserException
     *             something failed
     */
    CanonisedProcess canonise(String nativeType, InputStream nativeXml, Set<RequestParameterType<?>> canoniserProperties)
            throws CanoniserException;

    /**
     * DeCanonise a process.
     *
     * @param processId
     *            the processId of the Canonical format.
     * @param version
     *            the version of the canonical format
     * @param nativeType
     *            the processes original format
     * @param canonicalFormat
     *            the Canonical format
     * @param annotationFormat
     *            the annotation format data source
     * @param canoniserProperties
     * @return DecanonisedProcess containing the native format and messages from the Canoniser
     * @throws CanoniserException
     *             something failed
     */
    DecanonisedProcess deCanonise(final Integer processId, final String version, final String nativeType, final CanonicalProcessType canonicalFormat,
            final AnnotationsType annotationFormat, Set<RequestParameterType<?>> canoniserProperties) throws CanoniserException;

    /**
     * Convert the CPF Graph to XML.
     *
     * @param cpt
     *            the CPT
     * @return the CPF as a String
     */
    String CPFtoString(CanonicalProcessType cpt) throws JAXBException;

    /**
     * Serializes a Graph to a cpf.
     *
     * @param graph
     *            The process Model Graph.
     * @return the the CPF format of the process Model Graph.
     * @throws SerializationException
     *             if the conversion from a CPF to graph fails.
     */
    CanonicalProcessType serializeCPF(CPF graph) throws SerializationException;

    /**
     * De-serializes a CPF to a Process Model Graph.
     *
     * @param cpf
     *            the CPF Process Model to convert.
     * @return the process Model Graph of the conversion
     * @throws SerializationException
     *             if the conversion from a CPF to graph fails.
     */
    CPF deserializeCPF(CanonicalProcessType cpf) throws SerializationException;


}
