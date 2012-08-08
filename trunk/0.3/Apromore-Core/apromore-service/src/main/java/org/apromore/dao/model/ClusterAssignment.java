/**
 *
 */
package org.apromore.dao.model;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Cluster Assignment.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Entity
@Table(name = "cluster_assignment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("clusterAssignment")
public class ClusterAssignment implements Serializable {

    private ClusterAssignmentId id;
    private String cloneId;
    private Boolean maximal;
    private Integer coreObjectNb;

    private Cluster cluster;
    private FragmentVersion fragment;


    /**
     * Public Constructor.
     */
    public ClusterAssignment() { }



    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "fragmentId", column = @Column(name = "fragment_version_id", nullable = false, length = 40)),
            @AttributeOverride(name = "clusterId", column = @Column(name = "cluster_id", nullable = false, length = 40))})
    public ClusterAssignmentId getId() {
        return this.id;
    }

    public void setId(ClusterAssignmentId newId) {
        this.id = newId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fragment_version_id", nullable = false, insertable = false, updatable = false)
    public FragmentVersion getFragment() {
        return this.fragment;
    }

    public void setFragment(FragmentVersion newFragment) {
        this.fragment = newFragment;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false, insertable = false, updatable = false)
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
