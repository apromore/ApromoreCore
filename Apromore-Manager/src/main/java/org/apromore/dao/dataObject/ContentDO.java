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
public class ContentDO {

    private Integer id;
    private String boundaryS;
    private String boundaryE;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer contentId) {
        this.id = contentId;
    }

    public String getBoundaryE() {
        return boundaryE;
    }

    public void setBoundaryE(final String boundary1) {
        this.boundaryE = boundary1;
    }

    public String getBoundaryS() {
        return boundaryS;
    }

    public void setBoundaryS(final String boundary2) {
        this.boundaryS = boundary2;
    }
}
