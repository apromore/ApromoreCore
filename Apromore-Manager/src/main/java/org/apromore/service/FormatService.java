package org.apromore.service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.service.model.CanonisedProcess;

/**
 * Interface for the Format Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface FormatService {

    /**
     * Finds all the native Types (formats) in the system that are supported.
     *
     * @return a List of formats in the system.
     */
    List<NativeType> findAllFormats();

    /**
     * Find a particular Native Type.
     *
     * @param nativeType the type to find
     * @return the nativeType record
     */
    NativeType findNativeType(String nativeType);

    /**
     * Store the Native XML in the DB.
     *
     * @param procName   the name of the process being imported.
     * @param processId  the id of the process we are importing
     * @param created    the time created
     * @param lastUpdate the time last updated
     * @param user       the user doing the updates
     * @param nativeType the native Type
     * @param AnnotationVerion the Annotations identifier name
     * @param cp the canonical process format, cpf and anf.
     * @throws JAXBException if it fails....
     * @throws IOException is resetting the input streams fails.
     */
    void storeNative(String procName, ProcessModelVersion processId, String created, String lastUpdate, User user,
        NativeType nativeType, String AnnotationVerion, CanonisedProcess cp) throws JAXBException, IOException;
}
