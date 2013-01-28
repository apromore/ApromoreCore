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
     * Find the native record by the branch it is associated with.
     * @param processId   the processId
     * @param versionNumber the processModelVersion version
     * @return the native, a list of them for all the different canonical versions.
     */
    @Query("SELECT n FROM Native n JOIN n.processModelVersion pmv JOIN pmv.processBranch pb JOIN pb.process p " +
            "WHERE p.id = ?1 AND pmv.versionNumber = ?2")
    List<Native> findNativeByCanonical(final Integer processId, final Double versionNumber);

    /**
     * Find the Native record by the branch and native type.
     * @param processId  the processId of the Canonical format.
     * @param version    the version of the canonical format
     * @param nativeType the native type (XPDL, BPMN)
     * @return the XML as a string
     */
    @Query("SELECT n FROM Native n JOIN n.processModelVersion pmv JOIN pmv.processBranch pb JOIN pb.process p JOIN n.nativeType nt " +
            "WHERE p.id = ?1 AND pmv.versionNumber = ?2 AND nt.natType = ?3")
    Native getNative(final Integer processId, final Double version, final String nativeType);

}
