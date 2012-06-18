package org.apromore.dao.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Stores the Sub Clusters?????.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "subcluster")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("subcluster")
public class Subcluster implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -2353318364761081118L;

    private SubclusterId id;
    private Integer fragmentSize;
    private String subclusterId;
    private FragmentVersion fragmentVersion;


    /**
     * Default Constructor.
     */
    public Subcluster() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "fragmentVersionId", column = @Column(name = "fragment_version_id", nullable = false, length = 40)),
            @AttributeOverride(name = "parentClusterId", column = @Column(name = "parent_cluster_id", nullable = false, length = 80))})
    public SubclusterId getId() {
        return this.id;
    }

    /**
     * Set the Primary Key for the Object.
     * @param newId The id to set.
     */
    public void setId(final SubclusterId newId) {
        this.id = newId;
    }

    /**
     * Get the fragment version for the Object.
     * @return Returns the fragment version.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fragment_version_id", nullable = false, insertable = false, updatable = false)
    public FragmentVersion getFragmentVersion() {
        return this.fragmentVersion;
    }

    /**
     * Set the fragment version for the Object.
     * @param newFragmentVersion The fragment version to set.
     */
    public void setFragmentVersion(final FragmentVersion newFragmentVersion) {
        this.fragmentVersion = newFragmentVersion;
    }


    /**
     * Get the fragment size for the Object.
     * @return Returns the fragment size.
     */
    @Column(name = "fragment_size")
    public Integer getFragmentSize() {
        return this.fragmentSize;
    }

    /**
     * Set the fragment size for the Object.
     * @param newFragmentSize The fragment size to set.
     */
    public void setFragmentSize(final Integer newFragmentSize) {
        this.fragmentSize = newFragmentSize;
    }


    /**
     * Get the sub cluster id for the Object.
     * @return Returns the sub cluster id.
     */
    @Column(name = "subcluster_id", length = 80)
    public String getSubclusterId() {
        return this.subclusterId;
    }

    /**
     * Set the sub cluster id for the Object.
     * @param newSubclusterId The sub cluster id to set.
     */
    public void setSubclusterId(final String newSubclusterId) {
        this.subclusterId = newSubclusterId;
    }

}


