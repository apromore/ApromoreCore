/**
 *
 */
package org.apromore.service.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura C. Ekanayake
 */
public class Cluster {

    private org.apromore.dao.model.Cluster cluster;
    private List<MemberFragment> fragments = new ArrayList<MemberFragment>();

    public org.apromore.dao.model.Cluster getCluster() {
        return cluster;
    }

    public void setCluster(org.apromore.dao.model.Cluster cluster) {
        this.cluster = cluster;
    }

    public void addFragment(MemberFragment fragment) {
        fragments.add(fragment);
    }

    public List<MemberFragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<MemberFragment> fragments) {
        this.fragments = fragments;
    }
}
