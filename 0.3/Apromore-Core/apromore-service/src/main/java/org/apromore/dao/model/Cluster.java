/**
 *
 */
package org.apromore.dao.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * The Cluster Data Object.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Entity
@Table(name = "cluster")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Configurable("cluster")
public class Cluster implements Serializable {

    private static final long serialVersionUID = -2353656404638485586L;

    private String clusterId;
    private int size = 0;
    private float avgFragmentSize = 0;
    private String medoidId = null;
    private double standardizingEffort = 0;
    private double BCR = 0;
    private int refactoringGain = 0;

    private Set<ClusterAssignment> clusterAssignments = new HashSet<ClusterAssignment>(0);

    /**
     * Public Constructor.
     */
    public Cluster() { }


    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "cluster_id")
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(final String newClusterId) {
        this.clusterId = newClusterId;
    }

    @Column(name = "size")
    public int getSize() {
        return size;
    }

    public void setSize(final int newSize) {
        this.size = newSize;
    }

    @Column(name = "avg_fragment_size")
    public float getAvgFragmentSize() {
        return avgFragmentSize;
    }

    public void setAvgFragmentSize(final float newAvgFragmentSize) {
        this.avgFragmentSize = newAvgFragmentSize;
    }

    @Column(name = "medoid_id")
    public String getMedoidId() {
        return medoidId;
    }

    public void setMedoidId(final String newMdoidId) {
        this.medoidId = newMdoidId;
    }

    @Column(name = "benifit_cost_ratio")
    public double getBCR() {
        return BCR;
    }

    public void setBCR(final double newBCR) {
        this.BCR = newBCR;
    }

    @Column(name = "std_effort")
    public double getStandardizingEffort() {
        return standardizingEffort;
    }

    public void setStandardizingEffort(final double newStandardizingEffort) {
        this.standardizingEffort = newStandardizingEffort;
    }

    @Column(name = "refactoring_gain")
    public int getRefactoringGain() {
        return refactoringGain;
    }

    public void setRefactoringGain(final int newRefactoringGain) {
        this.refactoringGain = newRefactoringGain;
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cluster")
    public Set<ClusterAssignment> getClusterAssignments() {
        return this.clusterAssignments;
    }

    public void setClusterAssignments(final Set<ClusterAssignment> newClusterAssignment) {
        this.clusterAssignments = newClusterAssignment;
    }

    public void addClusterAssignment(final ClusterAssignment newClusterAssignment) {
        this.clusterAssignments.add(newClusterAssignment);
    }



    @Override
    public String toString() {
        return clusterId + " | " + size + " | " + avgFragmentSize + " | " + BCR;
    }
}
