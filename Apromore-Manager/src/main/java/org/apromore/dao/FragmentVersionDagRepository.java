package org.apromore.dao;

import org.apromore.dao.model.FragmentVersionDag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * Returns all the child mappings for the FragmentId.
     * @param parentUri the fragment uri
     * @return the list of child fragments
     */
    @Query("SELECT fvd FROM FragmentVersionDag fvd WHERE fvd.fragmentVersion.uri = ?1")
    List<FragmentVersionDag> getChildMappingsByURI(String parentUri);

    /**
     * Finds all the DAG entries greater than a min size.
     * @param minimumChildFragmentSize min fragment child size.
     * @return list of DAG entries
     */
    @Query("SELECT fvd FROM FragmentVersionDag fvd, FragmentVersion f WHERE fvd.childFragmentVersion.id = f.id AND f.fragmentSize > ?1")
    List<FragmentVersionDag> getAllDAGEntriesBySize(int minimumChildFragmentSize);

}
