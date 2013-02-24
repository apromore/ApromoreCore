package org.apromore.dao;

/**
 * Interface domain model Data access object FragmentDistance Custom Methods.
 *
 * @author <a href="mailto:cam.james@gmail.com">Igor Goldobin</a>
 * @version 1.0
 * @see org.apromore.dao.model.FragmentDistance
 */
public interface FragmentDistanceRepositoryCustom {

    /**
     * ** SPECIAL method for fast access to Fragment Distances.
     *
     * @param FragmentId1 the first fragment version id
     * @param FragmentId2 the second fragment id
     * @return the distance found
     */
    Double getDistance(Integer FragmentId1, Integer FragmentId2);

}
