package org.apromore.dao.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Stores the native type in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "native_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("nativeType")
public class NativeType implements Serializable {

    /**
     * Hard coded for interoperability.
     */
    private static final long serialVersionUID = -2353311738938485548L;

    private Integer id;
    private String natType;
    private String extension;

    private Set<Native> natives = new HashSet<Native>(0);
    private Set<Process> processes = new HashSet<Process>(0);


    /**
     * Default Constructor.
     */
    public NativeType() { }


    /**
     * returns the Id of this Object.
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
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
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
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
     *
     * @return Returns the extension.
     */
    @Column(name = "extension", length = 10)
    public String getExtension() {
        return this.extension;
    }

    /**
     * Set the extension for the Object.
     *
     * @param newExtension The extension to set.
     */
    public void setExtension(final String newExtension) {
        this.extension = newExtension;
    }

    /**
     * Get the natives for the Object.
     *
     * @return Returns the natives.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nativeType")
    public Set<Native> getNatives() {
        return this.natives;
    }

    /**
     * Set the natives for the Object.
     *
     * @param newNatives The natives to set.
     */
    public void setNatives(final Set<Native> newNatives) {
        this.natives = newNatives;
    }

    /**
     * Get the processes for the Object.
     *
     * @return Returns the processes.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nativeType")
    public Set<Process> getProcesses() {
        return this.processes;
    }

    /**
     * Set the processes for the Object.
     *
     * @param newProcesses The processes to set.
     */
    public void setProcesses(final Set<Process> newProcesses) {
        this.processes = newProcesses;
    }

}
