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
@org.eclipse.persistence.annotations.Cache(type = CacheType.WEAK, isolation = CacheIsolationType.SHARED, expiry = 60000, size = 50, alwaysRefresh = true, disableHits = true, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class AnnotationDO implements Serializable {

    private Integer id;
    private Integer nativeId;
    private Integer processModelVersionId;
    private String name;
    private String content;


    /**
     * Default constructor.
     */
    public AnnotationDO() {
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

    public Integer getNativeId() {
        return nativeId;
    }

    public void setNativeId(Integer nativeId) {
        this.nativeId = nativeId;
    }

    public Integer getProcessModelVersionId() {
        return processModelVersionId;
    }

    public void setProcessModelVersionId(Integer processModelVersionId) {
        this.processModelVersionId = processModelVersionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
