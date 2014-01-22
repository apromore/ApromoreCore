package org.apromore.dao;

import org.apromore.dao.model.Native;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
     * Find the Native record by the branch and native type.
     * @param processId  the processId of the Canonical format.
     * @param branchName the branch name we are trying to get the native format.
     * @param version    the version of the canonical format
     * @param nativeType the native type (XPDL, BPMN)
     * @return the XML as a string
     */
    @Query("SELECT n FROM Native n JOIN n.processModelVersion pmv JOIN pmv.processBranch pb JOIN pb.process p JOIN n.nativeType nt " +
            "WHERE p.id = ?1 AND pb.branchName = ?2 AND pmv.versionNumber = ?3 AND nt.natType = ?4")
    Native getNative(final Integer processId, final String branchName, final String version, final String nativeType);

}
