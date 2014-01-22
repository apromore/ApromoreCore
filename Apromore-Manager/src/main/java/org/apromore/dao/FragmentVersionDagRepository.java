package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object FragmentVersionDag.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.FragmentVersionDag
 */
@Repository
public interface FragmentVersionDagRepository extends JpaRepository<FragmentVersionDag, Integer>, FragmentVersionDagRepositoryCustom {

    /**
     * Returns a single FragmentVersionDag based on the fragment version uri.
     * @param uri the Fragment uri
     * @return the found FragmentVersionDag
     */
    @Query("SELECT fvd FROM FragmentVersionDag fvd WHERE fvd.fragmentVersion.uri = ?1")
    FragmentVersionDag findFragmentVersionDagByURI(String uri);

    /**
     * Returns all the child mappings for the FragmentId.
     * @param fragmentId the fragment id
     * @return the list of child fragments
     */
    @Query("SELECT fvd FROM FragmentVersionDag fvd WHERE fvd.fragmentVersion.id = ?1")
    List<FragmentVersionDag> getChildMappings(Integer fragmentId);

    /**
     * Delete all the Child relationships for this Fragment Version.
     * @param fragmentVersion the fragment version we want to remove all the children
     */
    @Modifying
    @Query("DELETE FROM FragmentVersionDag fvd WHERE fvd.fragmentVersion = ?1")
    void deleteChildRelationships(FragmentVersion fragmentVersion);
}
