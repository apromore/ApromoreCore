package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Stores the native type in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "native_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("nativeType")
public class NativeType implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353311738938485548L;

	private String natType;
	private String extension;

	private Set<Native> natives = new HashSet<Native>(0);
	private Set<Process> processes = new HashSet<Process>(0);


    /**
     * Default Constructor.
     */
	public NativeType() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
	@Id
	@Column(name = "nat_type", unique = true, nullable = false, length = 20)
	public String getNatType() {
		return this.natType;
	}

    /**
     * Set the natType for the Object.
     * @param newNatType The natType to set.
     */
	public void setNatType(final String newNatType) {
		this.natType = newNatType;
	}

    /**
     * Get the extension for the Object.
     * @return Returns the extension.
     */
    @Column(name = "extension", length = 10)
	public String getExtension() {
		return this.extension;
	}

    /**
     * Set the extension for the Object.
     * @param newExtension The extension to set.
     */
	public void setExtension(final String newExtension) {
		this.extension = newExtension;
	}

    /**
     * Get the natives for the Object.
     * @return Returns the natives.
     */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "nativeType")
	public Set<Native> getNatives() {
		return this.natives;
	}

    /**
     * Set the natives for the Object.
     * @param newNatives The natives to set.
     */
	public void setNatives(final Set<Native> newNatives) {
		this.natives = newNatives;
	}

    /**
     * Get the processes for the Object.
     * @return Returns the processes.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nativeType")
	public Set<Process> getProcesses() {
		return this.processes;
	}

    /**
     * Set the processes for the Object.
     * @param newProcesses The processes to set.
     */
	public void setProcesses(final Set<Process> newProcesses) {
		this.processes = newProcesses;
	}

}
