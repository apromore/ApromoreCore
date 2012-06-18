package org.apromore.service;

import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.service.model.CanonisedProcess;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.List;

/**
 * Interface for the Format Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface FormatService {

    /**
     * Finds all the native Types (formats) in the system that are supported.
     * @return a List of formats in the system.
     */
    List<NativeType> findAllFormats();

    /**
     * Find a particular Native Type.
     * @param nativeType the type to find
     * @return the nativeType record
     */
    NativeType findNativeType(String nativeType);

    /**
     * Store the Native XML in the DB.
     * @param procName the name of the process being imported.
     * @param version the version of the Process
     * @param processId the id of the process we are importing
     * @param cpf the inputStream of the Process to import (the actual process model)
     * @param created the time created
     * @param lastUpdate the time last updated
     * @param user the user doing the updates
     * @param nativeType the native Type
     * @throws JAXBException if it fails....
     * @param cp
     */
    void storeNative(String procName, String version, ProcessModelVersion processId, InputStream cpf, String created, String lastUpdate, User user, NativeType nativeType, CanonisedProcess cp)
            throws JAXBException;
}
