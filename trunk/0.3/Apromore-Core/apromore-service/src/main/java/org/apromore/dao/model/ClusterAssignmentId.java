/**
 *
 */
package org.apromore.dao.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Chathura C. Ekanayake
 */
@Embeddable
public class ClusterAssignmentId implements Serializable {

    private String fragmentId;
    private String clusterId;

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
