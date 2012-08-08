/**
 *
 */
package org.apromore.dao.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The Cluster Assignment Id.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Embeddable
public class ClusterAssignmentId implements Serializable {

    private String fragmentId;
    private String clusterId;


    /**
     * Constructor for the Id.
     * @param fragmentVersionId the fragment version
     * @param clusterId the cluster id.
     */
    public ClusterAssignmentId(final String fragmentVersionId, final String clusterId) {
        this.fragmentId = fragmentVersionId;
        this.clusterId = clusterId;
    }


    @Column(name = "fragment_version_id", nullable = false, length = 40)
    public String getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(String newFragmentId) {
        this.fragmentId = newFragmentId;
    }

    @Column(name = "cluster_id", nullable = false, length = 40)
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String newClusterId) {
        this.clusterId = newClusterId;
    }

    @Override
    public int hashCode() {
        return fragmentId.hashCode() + clusterId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ClusterAssignmentId)) {
            return false;
        }

        ClusterAssignmentId otherCAID = (ClusterAssignmentId) obj;
        if (getFragmentId().equals(otherCAID.getFragmentId()) && getClusterId().equals(otherCAID.getClusterId())) {
            return true;
        }

        return false;
    }
}
