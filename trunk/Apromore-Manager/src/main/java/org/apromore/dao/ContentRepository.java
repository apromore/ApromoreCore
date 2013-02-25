package org.apromore.dao;

import org.apromore.dao.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Content.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Content
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Integer>, ContentRepositoryCustom {

    /**
     * Finds the Content item from the fragment Version.
     * @param fragVersionId the fragment version
     * @return the found content record or null
     */
    @Query("SELECT c FROM Content c, FragmentVersion fv WHERE fv.content.id = c.id AND fv.id = ?1")
    Content getContentByFragmentVersion(Integer fragVersionId);

    /**
     * Finds the Content record by the has code.
     * @param code the fragment version
     * @return the found content record or null
     */
    @Query("SELECT c FROM Content c WHERE c.code = ?1")
    Content getContentByCode(String code);

}
