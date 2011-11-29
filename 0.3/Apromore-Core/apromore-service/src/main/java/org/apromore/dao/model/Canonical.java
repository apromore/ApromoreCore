package org.apromore.dao.model;

// Generated 17/10/2011 9:31:36 PM by Hibernate Tools 3.4.0.CR1

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Stores the canonical format in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "canonical",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "processId", "version_name" })
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries( {
    @NamedQuery(name = Canonical.FIND_BY_PROCESS_ID, query = "SELECT c FROM Canonical c WHERE c.process.processId = :processId"),
    @NamedQuery(name = Canonical.GET_CANONICAL, query = "SELECT c FROM Canonical c WHERE c.process.processId = :processId AND c.versionName = :versionName")
})
@Configurable("canonical")
public class Canonical implements Serializable {

    public static final String FIND_BY_PROCESS_ID = "canonical.findByProcessId";
    public static final String GET_CANONICAL = "pr.getCanonical";

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -9072538404638485548L;

	private String uri;
	private String versionName;
	private String author;
	private String creationDate;
	private String lastUpdate;
	private String ranking;
	private String documentation;
	private String content;

    private Process process;
	private Set<Native> natives = new HashSet<Native>(0);
    private Set<EditSessionMapping> editSessionMappings = new HashSet<EditSessionMapping>(0);

	private Set<Canonical> canonicalsForUriSource = new HashSet<Canonical>(0);
	private Set<Canonical> canonicalsForUriMerged = new HashSet<Canonical>(0);
    private Set<Canonical> canonicalsForUriDerivedVersion = new HashSet<Canonical>(0);
    private Set<Canonical> canonicalsForUriSourceVersion = new HashSet<Canonical>(0);


    /**
     * Default Constructor.
     */
	public Canonical() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
	@Id @Column(name = "uri", unique = true, nullable = false, length = 40)
	public String getUri() {
		return this.uri;
	}

    /**
     * Set the Primary Key for the Object.
     * @param newUri The id to set.
     */
	public void setUri(final String newUri) {
		this.uri = newUri;
	}


    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "processId")
	public Process getProcess() {
		return this.process;
	}

	public void setProcess(final Process newProcess) {
		this.process = newProcess;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "version_name", length = 20)
	public String getVersionName() {
		return this.versionName;
	}

	public void setVersionName(final String newVersionName) {
		this.versionName = newVersionName;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "author", length = 40)
	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(final String newAuthor) {
		this.author = newAuthor;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "creation_date", length = 35)
	public String getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(final String newCreationDate) {
		this.creationDate = newCreationDate;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "last_update", length = 35)
	public String getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(final String newLastUpdate) {
		this.lastUpdate = newLastUpdate;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "ranking", length = 10)
	public String getRanking() {
		return this.ranking;
	}

	public void setRanking(final String newRanking) {
		this.ranking = newRanking;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "documentation", length = 65535)
	public String getDocumentation() {
		return this.documentation;
	}

	public void setDocumentation(final String newDocumentation) {
		this.documentation = newDocumentation;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "content")
	public String getContent() {
		return this.content;
	}

	public void setContent(final String newContent) {
		this.content = newContent;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "canonical")
	public Set<Native> getNatives() {
		return this.natives;
	}

	public void setNatives(final Set<Native> newNatives) {
		this.natives = newNatives;
	}

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "canonical")
    public Set<EditSessionMapping> getEditSessionMappings() {
        return this.editSessionMappings;
    }

    public void setEditSessionMappings(final Set<EditSessionMapping> newEditSessionMappings) {
        this.editSessionMappings = newEditSessionMappings;
    }


    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "merged_version",
            joinColumns = { @JoinColumn(name = "uri_merged", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "uri_source", nullable = false, updatable = false) }
    )
	public Set<Canonical> getCanonicalsForUriSource() {
		return this.canonicalsForUriSource;
	}

	public void setCanonicalsForUriSource(final Set<Canonical> newCanonicalsForUriSource) {
		this.canonicalsForUriSource = newCanonicalsForUriSource;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "merged_version",
            joinColumns = { @JoinColumn(name = "uri_source", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "uri_merged", nullable = false, updatable = false) }
    )
	public Set<Canonical> getCanonicalsForUriMerged() {
		return this.canonicalsForUriMerged;
	}

	public void setCanonicalsForUriMerged(final Set<Canonical> newCanonicalsForUriMerged) {
		this.canonicalsForUriMerged = newCanonicalsForUriMerged;
	}


    /**
     * Get the Source Version Canonicals for the Object.
     * @return Returns the Source Version Canonicals.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "derived_version",
            joinColumns = { @JoinColumn(name = "uri_derived_version", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "uri_source_version", nullable = false, updatable = false) }
    )
    public Set<Canonical> getCanonicalsForUriSourceVersion() {
        return this.canonicalsForUriSourceVersion;
    }

    public void setCanonicalsForUriSourceVersion(final Set<Canonical> newCanonicalsForUriSourceVersion) {
        this.canonicalsForUriSourceVersion = newCanonicalsForUriSourceVersion;
    }

    /**
     * Get the Derived Version Canonicals for the Object.
     * @return Returns the Derived Version Canonicals.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "derived_version",
            joinColumns = { @JoinColumn(name = "uri_source_version", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "uri_derived_version", nullable = false, updatable = false) })
    public Set<Canonical> getCanonicalsForUriDerivedVersion() {
        return this.canonicalsForUriDerivedVersion;
    }

    public void setCanonicalsForUriDerivedVersion(final Set<Canonical> newCanonicalsForUriDerivedVersion) {
        this.canonicalsForUriDerivedVersion = newCanonicalsForUriDerivedVersion;
    }

}
