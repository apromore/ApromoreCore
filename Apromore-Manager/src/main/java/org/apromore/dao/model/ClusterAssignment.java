/**
 *
 */
package org.apromore.dao.model;

import org.springframework.beans.factory.annotation.Configurable;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * The Cluster Assignment.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Entity
@Table(name = "cluster_assignment")
@Configurable("clusterAssignment")
public class ClusterAssignment implements Serializable {

    private Integer id;
    private String cloneId;
    private Boolean maximal;
    private Integer coreObjectNb;

    private Cluster cluster;
    private FragmentVersion fragment;


    /**
     * Public Constructor.
     */
    public ClusterAssignment() { }



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



    @ManyToOne
    @JoinColumn(name = "fragmentVersionId", insertable = false, updatable = false)
    public FragmentVersion getFragment() {
        return this.fragment;
    }

    public void setFragment(FragmentVersion newFragment) {
        this.fragment = newFragment;
    }

    @ManyToOne
    @JoinColumn(name = "clusterId", insertable = false, updatable = false)
    public Cluster getCluster() {
        return this.cluster;
    }

    public void setCluster(Cluster newClusters) {
        this.cluster = newClusters;
    }

    @Column(name = "clone_id", length = 40)
    public String getCloneId() {
        return this.cloneId;
    }

    public void setCloneId(String newCloneId) {
        this.cloneId = newCloneId;
    }

    @Column(name = "maximal")
    public Boolean getMaximal() {
        return this.maximal;
    }

    public void setMaximal(Boolean newMaximal) {
        this.maximal = newMaximal;
    }

    @Column(name = "core_object_nb")
    public Integer getCoreObjectNb() {
        return this.coreObjectNb;
    }

    public void setCoreObjectNb(Integer newCoreObjectNb) {
        this.coreObjectNb = newCoreObjectNb;
    }
}
