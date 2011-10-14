package org.apromore.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.Serializable;

/**
 * Stores the process in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "processes",
       uniqueConstraints = {@UniqueConstraint(columnNames = { "processId" }) }
)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries( {
        @NamedQuery(name = Processes.GET_PROCESSES, query = "SELECT pr FROM Processes pr")
})
@Configurable("processes")
public class Processes implements Serializable {

    public static final String GET_PROCESSES = "pr.getProcesses";

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353656404638485548L;

    private long processId;
    private String name;
    private String domain;
    private String owner;
    private String originalType;

    /**
     * Default constructor.
     */
    public Processes() {
        super();
    }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
    @Id @Column(name = "processId", unique = true, nullable = false, precision = 11, scale = 0)
    public long getProcessId() {
        return processId;
    }

    /**
     * Set the Primary Key for the Object.
     * @param newProcessId The id to set.
     */
    public void setProcessId(long newProcessId) {
        this.processId = newProcessId;
    }


    /**
     * Get the Name for the Object.
     * @return Returns the name.
     */
    @Column(name = "name", unique = true, nullable = false, length = 100)
    public String getName() {
        return name;
    }

    /**
     * Set the Name for the Object.
     * @param newName The name to set.
     */
    public void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Get the User for the Object.
     * @return Returns the domain.
     */
    @Column(name = "domain", unique = true, nullable = false, length = 40)
    public String getDomain() {
        return domain;
    }

    /**
     * Set the domain for the Object.
     * @param newDomain The domain to set.
     */
    public void setDomain(final String newDomain) {
        this.domain = newDomain;
    }

    /**
     * Get the Owner for the Object.
     * @return Returns the owner.
     */
    @Column(name = "owner", unique = true, nullable = false, length = 10)
    public String getOwner() {
        return owner;
    }

    /**
     * Set the owner for the Object.
     * @param newOwner The owner to set.
     */
    public void setOwner(final String newOwner) {
        this.owner = newOwner;
    }

    /**
     * Get the original type for the Object.
     * @return Returns the original type.
     */
    @Column(name = "original_type", unique = true, nullable = false, length = 20)
    public String getOriginalType() {
        return originalType;
    }

    /**
     * Set the original type for the Object.
     * @param newOriginalType The original type to set.
     */
    public void setOriginalType(final String newOriginalType) {
        this.originalType = newOriginalType;
    }

}
