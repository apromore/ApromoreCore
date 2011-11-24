package org.apromore.dao.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 */
@Entity
@Table(name = "head_version")
public class HeadVersion implements Serializable {


    private HeadVersionId id;

    /**
     * Default Constructor.
     */
    public HeadVersion() { }


    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "processId", column = @Column(name = "processId")),
            @AttributeOverride(name = "version", column = @Column(name = "version", length = 20))})
    public HeadVersionId getId() {
        return this.id;
    }

    public void setId(HeadVersionId id) {
        this.id = id;
    }


}


