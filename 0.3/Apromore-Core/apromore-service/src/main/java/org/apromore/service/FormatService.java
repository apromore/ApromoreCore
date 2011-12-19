package org.apromore.service;

import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.User;

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

}
