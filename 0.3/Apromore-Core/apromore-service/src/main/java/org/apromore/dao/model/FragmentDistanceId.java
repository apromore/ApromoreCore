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
public class FragmentDistanceId implements Serializable {

    private String fragmentId1;
    private String fragmentId2;

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

    @Override
    public int hashCode() {
        return fragmentId1.hashCode() + fragmentId2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof FragmentDistanceId)) {
            return false;
        }

        FragmentDistanceId otherFDID = (FragmentDistanceId) obj;
        if (getFragmentId1().equals(otherFDID.getFragmentId1()) && getFragmentId2().equals(otherFDID.getFragmentId2())) {
            return true;
        }

        return false;
    }
}
