package org.apromore.dao.dataObject;

import javax.persistence.Cacheable;
import java.io.Serializable;
import java.util.Date;

import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;

/**
 * Stores the process user in apromore.
 *
 * NOTE: this is for the JDBC template calls only.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Cacheable(true)
@org.eclipse.persistence.annotations.Cache(type = CacheType.WEAK, isolation = CacheIsolationType.SHARED, expiry = 60000, size = 200, alwaysRefresh = true, disableHits = true, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class ProcessUserDO implements Serializable {

    private Integer id;
    private Integer processId;
    private Integer userId;
    private Boolean hasRead;
    private Boolean hasWrite;
    private Boolean hasOwnership;


    /**
     * Default constructor.
     */
    public ProcessUserDO() {
        super();
    }


    /**
     * returns the Id of this Object.
     * @return the id
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the Id of this Object
     * @param id the new Id.
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(Boolean has_read) {
        this.hasRead = has_read;
    }

    public Boolean isHasWrite() {
        return hasWrite;
    }

    public void setHasWrite(Boolean has_write) {
        this.hasWrite = has_write;
    }

    public Boolean isHasOwnership() {
        return hasOwnership;
    }

    public void setHasOwnership(Boolean has_ownership) {
        this.hasOwnership = has_ownership;
    }
}
