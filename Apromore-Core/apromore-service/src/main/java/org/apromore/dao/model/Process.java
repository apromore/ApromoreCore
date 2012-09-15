package org.apromore.dao.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Stores the process in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "process",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"})
        }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("process")
public class Process implements Serializable {

    /**
     * Hard coded for interoperability.
     */
    private static final long serialVersionUID = -2353656404638485548L;

    private Integer id;
    private String name;
    private String domain;

    private User user;
    private NativeType nativeType;

    private Set<ProcessBranch> processBranches = new HashSet<ProcessBranch>(0);
    private Set<EditSession> editSessions = new HashSet<EditSession>(0);
    private Set<TempVersion> tempVersions = new HashSet<TempVersion>(0);

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
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * Set the Primary Key for the Object.
     * @param newId The id to set.
     */
    public void setId(final Integer newId) {
        this.id = newId;
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
     * Get the editSessions for the Object.
     * @return Returns the editSessions.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "process")
    public Set<EditSession> getEditSessions() {
        return this.editSessions;
    }

    /**
     * Set the editSessions for the Object.
     * @param newEditSessions The editSessions to set.
     */
    public void setEditSessions(final Set<EditSession> newEditSessions) {
        this.editSessions = newEditSessions;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "process")
    public Set<TempVersion> getTempVersions() {
        return this.tempVersions;
    }

    public void setTempVersions(final Set<TempVersion> tempVersions) {
        this.tempVersions = tempVersions;
    }
}
