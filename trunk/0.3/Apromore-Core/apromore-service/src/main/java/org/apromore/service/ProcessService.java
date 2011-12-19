package org.apromore.service;

import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBException;

import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.User;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.Format;

/**
 * Interface for the Process Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ProcessService {

    /**
     * Loads all the process Summaries. It will either get all or use the keywords parameter
     * to load a subset of the processes.
     * @param searchExpression the search expression to limit the search.
     * @return The ProcessSummariesType used for Webservices.
     */
    ProcessSummariesType readProcessSummaries(final String searchExpression);

    /**
     * Export a BMP Model but in a particular format.
     * @param processId the process model id
     * @param version the version of the process model
     * @param nativeType the format of the model
     * @return the XML but as a datasource object
     * @throws ExportFormatException if for some reason the process model can not be found.
     */
    DataSource exportFormat(final long processId, final String version, final String nativeType) throws ExportFormatException;

    /**
     * Reads the Canonical (ANF).
     * @param processId the processId of the Canonical format.
     * @param version the version of the canonical format
     * @param withAnn Do we return the Canonical with annotations or not.
     * @param annName if we do return Annotations, what annotation to return
     * @return the format VO containing the canonical and annotation if requested
     * @throws ExportFormatException if for some reason the process model can not be found.
     */
    Format getCanonicalAnf(final long processId, final String version, final boolean withAnn, final String annName)
            throws ExportFormatException;

    /**
     * Import a Process.
     * @param username The user doing the importing.
     * @param processName the name of the process being imported.
     * @param cpfURI the Canonical URI
     * @param versionName the version of the Process
     * @param nativeType the native process format type
     * @param cpf the dataHandler of the Process to import (the actual process model)
     * @param domain the domain of the model
     * @param documentation any documentation that is required
     * @param created the time created
     * @param lastUpdate the time last updated
     * @return the processSummaryType
     * @throws ImportException if the import process failed for any reason.
     */
    ProcessSummaryType importProcess(String username, String processName, String cpfURI, String versionName, String nativeType,
            DataHandler cpf, String domain, String documentation, String created, String lastUpdate) throws ImportException;


    /**
     * Store the Native and CPF and there annotations in the DB.
     * @param processName the name of the process being imported.
     * @param versionName the version of the Process
     * @param cpfURI the cpf URI
     * @param cpf the inputStream of the Process to import (the actual process model)
     * @param domain the domain of the model
     * @param created the time created
     * @param lastUpdate the time last updated
     * @param cp the Canonical Process as InputStream and the annotations
     * @param user the user doing the updates
     * @param nativeType the native Type
     * @throws JAXBException if it fails....
     */
    ProcessSummaryType storeNativeAndCpf(String processName, String versionName, String cpfURI, InputStream cpf, String domain,
            String created, String lastUpdate, CanonisedProcess cp, User user, NativeType nativeType) throws JAXBException;
}
