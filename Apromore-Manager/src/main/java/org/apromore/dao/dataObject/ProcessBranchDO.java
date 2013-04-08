package org.apromore.dao.dataObject;

import javax.persistence.Cacheable;
import java.io.Serializable;

import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;

/**
 * Stores the process branch in apromore.
 * <p/>
 * NOTE: this is for the JDBC template calls only.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Cacheable(true)
@org.eclipse.persistence.annotations.Cache(type = CacheType.WEAK, isolation = CacheIsolationType.SHARED, expiry = 60000, size = 300, alwaysRefresh = true, disableHits = true, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class ProcessBranchDO implements Serializable {

    private Integer id;
    private String name;
    private Integer processId;
    private String creationDate;
    private String lastUpdateDate;
    private String ranking;
    private Integer sourceProcessModelVersionId;
    private Integer currentProcessModelVersionId;


    /**
     * Default constructor.
     */
    public ProcessBranchDO() {
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


    /**
     * Get the Name for the Object.
     *
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the Name for the Object.
     *
     * @param newName The name to set.
     */
    public void setName(final String newName) {
        this.name = newName;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public Integer getSourceProcessModelVersionId() {
        return sourceProcessModelVersionId;
    }

    public void setSourceProcessModelVersionId(Integer sourceProcessModelVersionId) {
        this.sourceProcessModelVersionId = sourceProcessModelVersionId;
    }

    public Integer getCurrentProcessModelVersionId() {
        return currentProcessModelVersionId;
    }

    public void setCurrentProcessModelVersionId(Integer currentProcessModelVersionId) {
        this.currentProcessModelVersionId = currentProcessModelVersionId;
    }
}
