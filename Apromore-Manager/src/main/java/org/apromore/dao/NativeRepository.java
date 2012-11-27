package org.apromore.dao;

import org.apromore.dao.model.Native;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object Native.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Native
 */
@Repository
public interface NativeRepository extends JpaRepository<Native, Integer> {

    /**
     * Find the annotation record by it's native uri id.
     * @param branchId   the processId
     * @param versionName the version name
     * @return the native, a list of them for all the different canonical versions.
     */
    @Query("SELECT n FROM Native n, ProcessModelVersion p WHERE n.processModelVersion.id = p.id " +
            "AND p.processBranch.id = ?1 AND p.versionName = ?2")
    List<Native> findNativeByCanonical(final Integer branchId, final String versionName);

    /**
     * Get the Canonical format. this is just a string but contains the xml Canonical Format.
     * @param branchId  the processId of the Canonical format.
     * @param version    the version of the canonical format
     * @param nativeType the native type (XPDL, BPMN)
     * @return the XML as a string
     */
    @Query("SELECT n FROM Native n, ProcessModelVersion p WHERE n.processModelVersion.id = p.id " +
            "AND p.processBranch.id = ?1 AND p.versionName = ?2 AND n.nativeType.natType = ?3")
    Native getNative(final Integer branchId, final String version, final String nativeType);

}
