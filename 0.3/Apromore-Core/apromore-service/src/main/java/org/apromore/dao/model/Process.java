package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Stores the process in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "process",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "processId" })
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("process")
public class Process implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353656404638485548L;

    private Integer processId;
    private String name;
    private String domain;

    private User user;
    private NativeType nativeType;

    private Set<ProcessBranch> processBranches = new HashSet<ProcessBranch>(0);
    private Set<EditSessionMapping> editSessionMappings = new HashSet<EditSessionMapping>(0);

    /**
     * Default constructor.
     */
    public Process() {
        super();
    }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "processId", unique = true, nullable = false)
    public Integer getProcessId() {
        return processId;
    }

    /**
     * Set the Primary Key for the Object.
     * @param newProcessId The id to set.
     */
    public void setProcessId(Integer newProcessId) {
        this.processId = newProcessId;
    }


    /**
     * Get the Name for the Object.
     * @return Returns the name.
     */
    @Column(name = "name", unique = false, nullable = false, length = 100)
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
    @Column(name = "domain", unique = false, nullable = false, length = 40)
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
     * Get the nativeType for the Object.
     * @return Returns the nativeType.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_type")
    public NativeType getNativeType() {
        return this.nativeType;
    }

    /**
     * Set the nativeType for the Object.
     * @param newNativeType The nativeType to set.
     */
    public void setNativeType(final NativeType newNativeType) {
        this.nativeType = newNativeType;
    }

    /**
     * Get the nativeType for the Object.
     * @return Returns the nativeType.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    public User getUser() {
        return this.user;
    }

    /**
     * Set the user for the Object.
     * @param newUser The user to set.
     */
    public void setUser(final User newUser) {
        this.user = newUser;
    }


    /**
     * Get the editSessionMappings for the Object.
     * @return Returns the editSessionMappings.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "process")
    public Set<EditSessionMapping> getEditSessionMappings() {
        return this.editSessionMappings;
    }

    /**
     * Set the editSessionMappings for the Object.
     * @param newEditSessionMappings The editSessionMappings to set.
     */
    public void setEditSessionMappings(final Set<EditSessionMapping> newEditSessionMappings) {
        this.editSessionMappings = newEditSessionMappings;
    }

    /**
     * Get the process branches for the Object.
     * @return Returns the process branches.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "process")
    public Set<ProcessBranch> getProcessBranches() {
        return this.processBranches;
    }

    /**
     * Set the process Branches for the Object.
     * @param newProcessBranches The process Branches to set.
     */
    public void setProcessBranches(final Set<ProcessBranch> newProcessBranches) {
        this.processBranches = newProcessBranches;
    }
}
