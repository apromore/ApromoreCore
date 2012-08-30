package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.NativeType;

/**
 * Interface domain model Data access object NativeType.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.NativeType
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
