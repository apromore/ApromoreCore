package org.apromore.service;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.DecanonisedProcess;

import java.io.InputStream;
import java.util.Set;
import javax.xml.bind.JAXBException;

/**
 * Interface for the Canoniser Service. Defines all the methods that will do the majority of the work for the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface CanoniserService {

    /**
     * Lists all installed Canonisers for given native process format.
     * @param nativeType that the Canoniser supports
     * @return Set of Canoniser that support the native process format
     * @throws PluginNotFoundException
     */
    Set<Canoniser> listByNativeType(String nativeType) throws PluginNotFoundException;

    /**
     * Finds first Canoniser with given native type
     * @param nativeType that the Canoniser supports
     * @return Canoniser that support the native process format
     * @throws PluginNotFoundException
     */
    Canoniser findByNativeType(String nativeType) throws PluginNotFoundException;

    /**
     * Finds first Canoniser with given native type with given name and specified version.
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
     * @param nativeType          the native type
     * @param nativeXml           the processXML to canonise
     * @param canoniserProperties
     * @return CanonisedProcess populated with the CPF and ANF and messages from the Canoniser
     * @throws CanoniserException something failed
     */
    CanonisedProcess canonise(String nativeType, InputStream nativeXml, Set<RequestParameterType<?>> canoniserProperties)
            throws CanoniserException;

    /**
     * DeCanonise a process or fragment.
     * @param nativeType          the processes original format
     * @param canonicalFormat     the Canonical format
     * @param annotationFormat    the annotation format data source
     * @param canoniserProperties
     * @return DecanonisedProcess containing the native format and messages from the Canoniser
     * @throws CanoniserException something failed
     */
    DecanonisedProcess deCanonise(final String nativeType, final CanonicalProcessType canonicalFormat,
        final AnnotationsType annotationFormat, Set<RequestParameterType<?>> canoniserProperties) throws CanoniserException;

    /**
     * Convert the CPF Graph to XML.
     * @param cpt the CPT
     * @return the CPF as a String
     */
    String CPFtoString(CanonicalProcessType cpt) throws JAXBException;

    /**
     * Converts XML to CPF.
     * @param xml the xml to convert.
     * @return the Canonical Process Type
     * @throws JAXBException
     */
    CanonicalProcessType XMLtoCPF(String xml) throws JAXBException;
}
