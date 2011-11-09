package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * IssuedContentIds generated by hbm2java
 */
@Entity
@Table(name = "issued_content_ids")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("issued_content_ids")
public class IssuedContentIds implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -9072912504638485548L;

    private int issuedId;


    public IssuedContentIds() { }


    @Id
    @Column(name = "issued_id", unique = true, nullable = false)
    public int getIssuedId() {
        return this.issuedId;
    }

    public void setIssuedId(int issuedId) {
        this.issuedId = issuedId;
    }


}


