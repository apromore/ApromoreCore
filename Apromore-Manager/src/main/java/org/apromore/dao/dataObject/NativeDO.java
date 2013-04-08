package org.apromore.dao.dataObject;

import javax.persistence.Cacheable;
import java.io.Serializable;

import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;

/**
 * Stores the Native types in apromore.
 * <p/>
 * NOTE: this is for the JDBC template calls only.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Cacheable(true)
@org.eclipse.persistence.annotations.Cache(type = CacheType.WEAK, isolation = CacheIsolationType.SHARED, expiry = 60000, size = 10, alwaysRefresh = true, disableHits = true, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class NativeDO implements Serializable {

    private Integer id;
    private String content;
    private Integer natTypeId;
    private Integer processModelVersionId;


    /**
     * Default constructor.
     */
    public NativeDO() {
        super();
    }


    /**
     * returns the Id of this Object.
     *
     * @return the id
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the Id of this Object
     *
     * @param id the new Id.
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getNatTypeId() {
        return natTypeId;
    }

    public void setNatTypeId(Integer natTypeId) {
        this.natTypeId = natTypeId;
    }

    public Integer getProcessModelVersionId() {
        return processModelVersionId;
    }

    public void setProcessModelVersionId(Integer processModelVersionId) {
        this.processModelVersionId = processModelVersionId;
    }
}
