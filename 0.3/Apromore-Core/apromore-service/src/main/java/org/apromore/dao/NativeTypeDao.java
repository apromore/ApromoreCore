package org.apromore.dao;

import org.apromore.dao.model.NativeType;

import java.util.List;

/**
 * Interface domain model Data access object NativeType.
 *
 * @see org.apromore.dao.model.NativeType
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 */
public interface NativeTypeDao {

    /**
     * Find the native types.
     * @return the list of nativeType
     */
    List<NativeType> findAllFormats();

    /**
     * Find a particular Native Type.
     * @param nativeType the type to find
     * @return the nativeType record
     */
    NativeType findNativeType(String nativeType);

}
