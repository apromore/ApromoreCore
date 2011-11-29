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
    public List<NativeType> findAllFormats();


    /**
     * Save the nativeType.
     * @param nativeType the nativeType to persist
     */
    void save(NativeType nativeType);

    /**
     * Update the nativeType.
     * @param nativeType the nativeType to update
     */
    void update(NativeType nativeType);

    /**
     * Remove the nativeType.
     * @param nativeType the nativeType to remove
     */
    void delete(NativeType nativeType);

}
