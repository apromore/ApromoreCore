package org.apromore.dao.dataObject;

import javax.persistence.Cacheable;
import java.io.Serializable;

import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;

/**
 * Stores the process in apromore.
 * <p/>
 * NOTE: this is for the JDBC template calls only.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Cacheable(true)
@org.eclipse.persistence.annotations.Cache(type = CacheType.WEAK, isolation = CacheIsolationType.SHARED, expiry = 60000, size = 10000, alwaysRefresh = true, disableHits = true, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class ProcessDO implements Serializable {

    private Integer id;
    private String name;
    private String domain;

    private Integer userId;
    private Integer folderId;
    private Integer nativeTypeId;


    /**
     * Default constructor.
     */
    public ProcessDO() {
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

    /**
     * Get the User for the Object.
     *
     * @return Returns the domain.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Set the domain for the Object.
     *
     * @param newDomain The domain to set.
     */
    public void setDomain(final String newDomain) {
        this.domain = newDomain;
    }

    /**
     * Get the nativeType for the Object.
     *
     * @return Returns the nativeType.
     */
    public Integer getFolderId() {
        return this.folderId;
    }

    /**
     * Set the user for the Object.
     *
     * @param newFolder The user to set.
     */
    public void setFolderId(final Integer newFolder) {
        this.folderId = newFolder;
    }

    /**
     * Get the nativeType for the Object.
     *
     * @return Returns the nativeType.
     */
    public Integer getNativeTypeId() {
        return this.nativeTypeId;
    }

    /**
     * Set the nativeType for the Object.
     *
     * @param newNativeType The nativeType to set.
     */
    public void setNativeTypeId(final Integer newNativeType) {
        this.nativeTypeId = newNativeType;
    }

    /**
     * Get the nativeType for the Object.
     *
     * @return Returns the nativeType.
     */
    public Integer getUserId() {
        return this.userId;
    }

    /**
     * Set the user for the Object.
     *
     * @param newUser The user to set.
     */
    public void setUserId(final Integer newUser) {
        this.userId = newUser;
    }

}
