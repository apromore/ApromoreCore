package org.apromore.service;

import java.util.List;

/**
 * Interface for the Domains Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface DomainService {

    /**
     * Finds all the unique Domains in the system that are supported.
     * @return a List of domains in the system.
     */
    List<String> findAllDomains();

}
