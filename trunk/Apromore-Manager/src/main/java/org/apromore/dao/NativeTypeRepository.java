package org.apromore.dao;

import org.apromore.dao.model.NativeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object NativeType.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.NativeType
 */
@Repository
public interface NativeTypeRepository extends JpaRepository<NativeType, Integer> {

    /**
     * Find a particular Native Type.
     * @param nativeType the type to find
     * @return the nativeType record
     */
    @Query("SELECT n FROM NativeType n WHERE n.natType = ?1")
    NativeType findNativeType(String nativeType);

}
