package org.apromore.service;

import org.apromore.exception.CanoniserException;
import org.apromore.exception.ExceptionImport;
import org.apromore.exception.ImportException;
import org.apromore.model.ProcessSummaryType;
import org.apromore.service.model.CanonisedProcess;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for the Canoniser Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface CanoniserService {

    public static final String ANF_CONTEXT = "org.apromore.anf";
    public static final String CPF_CONTEXT = "org.apromore.cpf";
    public static final String XPDL2_CONTEXT = "org.wfmc._2008.xpdl2";
    public static final String EPML_CONTEXT = "de.epml";
    public static final String PNML_CONTEXT = "org.apromore.pnml";

    /**
     * DeCanonise a process.
     * @param processId the processId of the Canonical format.
     * @param version the version of the canonical format
     * @param nativeType the processes original format
     * @param cpf_is the Canonical format inputStream
     * @param anf_is the annotation inputStream
     * @return the DeCanonised model inputStream
     */
    DataSource deCanonise(final long processId, final String version, final String nativeType, final DataSource cpf_is, final DataSource anf_is);

    /**
     * Generate cpf_xml and anf_xml from process_xml which is specified in language nativeType.
     * If cpf_uri is equal to 0, take it from process_xml
     * @param process_xml the processXML to canonise
     * @param nativeType the native type
     * @param anf_xml the anf xml, maybe needed
     * @param cpf_xml the cpf xml
     * @param cpf_uri the cpf uri
     * @throws org.apromore.exception.CanoniserException something failed
     */
    public void canonise(String cpf_uri, InputStream process_xml, String nativeType, ByteArrayOutputStream anf_xml,
            ByteArrayOutputStream cpf_xml) throws CanoniserException, IOException, JAXBException, SAXException;
}
