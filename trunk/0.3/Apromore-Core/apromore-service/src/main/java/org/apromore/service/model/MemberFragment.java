/**
 *
 */
package org.apromore.service.model;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class MemberFragment {

    private String fragmentId;
    private int fragmentSize;
    private double distance;

    private List<ProcessAssociation> processAssociations = new ArrayList<ProcessAssociation>();


    /**
     * Default Constructor.
     */
    public MemberFragment() { }


    public MemberFragment(String fragmentId) {
        this.fragmentId = fragmentId;
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(String fragmentId) {
        this.fragmentId = fragmentId;
    }

    public int getFragmentSize() {
        return fragmentSize;
    }

    public void setFragmentSize(int fragmentSize) {
        this.fragmentSize = fragmentSize;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<ProcessAssociation> getProcessAssociations() {
        return processAssociations;
    }

    public void setProcessAssociations(List<ProcessAssociation> processAssociations) {
        this.processAssociations = processAssociations;
    }
}
