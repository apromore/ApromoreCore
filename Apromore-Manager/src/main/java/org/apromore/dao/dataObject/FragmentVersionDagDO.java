package org.apromore.dao.dataObject;

import javax.persistence.Cacheable;

import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;

/**
 * @author Chathura Ekanayake
 */
@Cacheable(true)
@org.eclipse.persistence.annotations.Cache(type = CacheType.WEAK, isolation = CacheIsolationType.SHARED, expiry = 60000, size = 1000, alwaysRefresh = true, disableHits = true, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class FragmentVersionDagDO {

	private Integer id;
    private Integer fragmentVersionId;
	private Integer childFragmentVersionId;
    private String pocketId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFragmentVersionId() {
        return fragmentVersionId;
    }

    public void setFragmentVersionId(Integer fragmentVersionId) {
        this.fragmentVersionId = fragmentVersionId;
    }

    public Integer getChildFragmentVersionId() {
        return childFragmentVersionId;
    }

    public void setChildFragmentVersionId(Integer childFragmentVersionId) {
        this.childFragmentVersionId = childFragmentVersionId;
    }

    public String getPocketId() {
        return pocketId;
    }

    public void setPocketId(String pocketId) {
        this.pocketId = pocketId;
    }
}
