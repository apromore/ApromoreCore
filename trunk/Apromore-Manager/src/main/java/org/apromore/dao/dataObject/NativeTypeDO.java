package org.apromore.dao.dataObject;

import javax.persistence.Cacheable;
import java.io.Serializable;

import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;

/**
 * Stores the Native types in apromore.
 *
 * NOTE: this is for the JDBC template calls only.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Cacheable(true)
@org.eclipse.persistence.annotations.Cache(type = CacheType.WEAK, isolation = CacheIsolationType.SHARED, expiry = 60000, size = 10, alwaysRefresh = true, disableHits = true, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class NativeTypeDO implements Serializable {

    private Integer id;
    private String nat_type;
    private String extension;


    /**
     * Default constructor.
     */
    public NativeTypeDO() {
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


    /**
     * Get the native type for the Object.
     * @return Returns the native type.
     */
    public String getNatType() {
        return nat_type;
    }

    /**
     * Set the Native type for the Object.
     * @param newNatType The nat type to set.
     */
    public void setNatType(final String newNatType) {
        this.nat_type = newNatType;
    }

    /**
     * Get the extension for the Object.
     * @return Returns the extension.
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Set the extension for the Object.
     * @param newExtension The extension to set.
     */
    public void setExtension(final String newExtension) {
        this.extension = newExtension;
    }

}
