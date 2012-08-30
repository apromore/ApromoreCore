/**
 *
 */
package org.apromore.dao.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The Cluster Assignment Id.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Embeddable
public class ClusterAssignmentId implements Serializable {

    private String fragmentId;
    private String clusterId;

    /**
     * Public Default Constructor.
     */
    public ClusterAssignmentId() { }

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

    /**
     * The equals standard method to test if the Fragment Version Dag entity is the same.
     * @param obj the other ID object
     * @return true if the same otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        Boolean result = false;

        if (obj instanceof ClusterAssignmentId) {
            ClusterAssignmentId other = (ClusterAssignmentId) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(getClusterId(), other.getClusterId());
            builder.append(getFragmentId(), other.getFragmentId());
            result = builder.isEquals();
        }

        return result;
    }

    /**
     * Determines the hashcode of the object.
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getClusterId());
        builder.append(getFragmentId());
        return builder.toHashCode();
    }
}
