package org.apromore.service;

import javax.activation.DataSource;
import java.io.InputStream;

/**
 * Interface for the Canoniser Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface CanoniserService {

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

}
