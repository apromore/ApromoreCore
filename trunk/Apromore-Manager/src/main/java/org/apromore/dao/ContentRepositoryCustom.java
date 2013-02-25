package org.apromore.dao;

import org.apromore.dao.dataObject.ContentDO;

/**
 * Interface domain model Data access object Content Custom Methods.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Content
 */
public interface ContentRepositoryCustom {

    /**
     * ** SPECIAL method for fast access to Content Records direct to the DB.
     *
     * @param fragVersionId the first fragment version id
     * @return the content Data Object (not attached to the DB.
     */
    ContentDO getContentDOByFragmentVersion(final Integer fragVersionId);

}
