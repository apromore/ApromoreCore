package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
@NamedQueries( {
    @NamedQuery(name = Process.GET_ALL_PROCESSES, query = "SELECT p, coalesce(r.id.ranking, 0) FROM Process p, ProcessRanking r WHERE p.processId = r.id.processId "),
    @NamedQuery(name = Process.GET_All_DOMAINS, query = "SELECT DISTINCT p.domain FROM Process p ORDER by p.domain")
})
@Configurable("process")
public class Process implements Serializable {

    public static final String GET_ALL_PROCESSES = "pr.getAllProcesses";
    public static final String GET_All_DOMAINS = "pr.getAllDomains";


    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353656404638485548L;

    private long processId;
    private String name;
    private String domain;

    private User user;
    private NativeType nativeType;

    private Set<ProcessBranch> processBranches = new HashSet<ProcessBranch>(0);
    private Set<EditSessionMapping> editSessionMappings = new HashSet<EditSessionMapping>(0);
    private Set<Canonical> canonicals = new HashSet<Canonical>(0);

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
     * Get the canonicals for the Object.
     * @return Returns the canonicals.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "process")
    public Set<Canonical> getCanonicals() {
        return this.canonicals;
    }

    /**
     * Set the canonicals for the Object.
     * @param newCanonicals The canonicals to set.
     */
    public void setCanonicals(final Set<Canonical> newCanonicals) {
        this.canonicals = newCanonicals;
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
