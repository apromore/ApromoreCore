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
 * The Fragment Distance Id.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Embeddable
public class FragmentDistanceId implements Serializable {

    private String fragmentId1;
    private String fragmentId2;

    /**
     * Public Default Constructor.
     */
    public FragmentDistanceId() { }

    /**
     * Constructor for the Id.
     * @param newFragmentId1 the fragment id 1
     * @param newFragmentId2 the fragment id 2
     */
    public FragmentDistanceId(final String newFragmentId1, final String newFragmentId2) {
        this.fragmentId1 = newFragmentId1;
        this.fragmentId2 = newFragmentId2;
    }

    @Column(name = "fid1", nullable = false, length = 40)
    public String getFragmentId1() {
        return fragmentId1;
    }

    public void setFragmentId1(String fragmentId1) {
        this.fragmentId1 = fragmentId1;
    }

    @Column(name = "fid2", nullable = false, length = 40)
    public String getFragmentId2() {
        return fragmentId2;
    }

    public void setFragmentId2(String fragmentId2) {
        this.fragmentId2 = fragmentId2;
    }


    /**
     * The equals standard method to test if the Fragment Version Dag entity is the same.
     * @param obj the other ID object
     * @return true if the same otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        Boolean result = false;

        if (obj instanceof FragmentDistanceId) {
            FragmentDistanceId other = (FragmentDistanceId) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(getFragmentId1(), other.getFragmentId1());
            builder.append(getFragmentId2(), other.getFragmentId2());
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
        builder.append(getFragmentId1());
        builder.append(getFragmentId2());
        return builder.toHashCode();
    }

}
