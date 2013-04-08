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
public class FragmentVersionDO {

    private Integer id;
    private String uri;
    private Integer fragmentId;
    private Integer contentId;
    private Integer clusterId;
    private String childMappingCode;
    private Integer derivedFromFragment;
    private Integer lockStatus;
    private Integer lockCount;
    private Integer fragmentSize;
    private String fragmentType;
    private String newestNeighbor;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(Integer fragmentId) {
        this.fragmentId = fragmentId;
    }

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public String getChildMappingCode() {
        return childMappingCode;
    }

    public void setChildMappingCode(String childMappingCode) {
        this.childMappingCode = childMappingCode;
    }

    public Integer getDerivedFromFragment() {
        return derivedFromFragment;
    }

    public void setDerivedFromFragment(Integer derivedFromFragment) {
        this.derivedFromFragment = derivedFromFragment;
    }

    public Integer getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(Integer lockStatus) {
        this.lockStatus = lockStatus;
    }

    public Integer getLockCount() {
        return lockCount;
    }

    public void setLockCount(Integer lockCount) {
        this.lockCount = lockCount;
    }

    public Integer getFragmentSize() {
        return fragmentSize;
    }

    public void setFragmentSize(Integer fragmentSize) {
        this.fragmentSize = fragmentSize;
    }

    public String getFragmentType() {
        return fragmentType;
    }

    public void setFragmentType(String fragmentType) {
        this.fragmentType = fragmentType;
    }

    public String getNewestNeighbor() {
        return newestNeighbor;
    }

    public void setNewestNeighbor(String newestNeighbor) {
        this.newestNeighbor = newestNeighbor;
    }
}
