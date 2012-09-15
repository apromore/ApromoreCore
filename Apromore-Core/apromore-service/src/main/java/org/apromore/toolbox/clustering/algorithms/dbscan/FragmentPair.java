package org.apromore.toolbox.clustering.algorithms.dbscan;


import org.apromore.dao.model.FragmentVersion;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class FragmentPair {

    private FragmentVersion fid1;
    private FragmentVersion fid2;

    public FragmentPair(FragmentVersion fid1, FragmentVersion fid2) {
        this.fid1 = fid1;
        this.fid2 = fid2;
    }

    public FragmentVersion getFid1() {
        return fid1;
    }

    public void setFid1(FragmentVersion fid1) {
        this.fid1 = fid1;
    }

    public FragmentVersion getFid2() {
        return fid2;
    }

    public void setFid2(FragmentVersion fid2) {
        this.fid2 = fid2;
    }

    public boolean hasFragment(Integer fid) {
        return fid1.equals(fid) || fid2.equals(fid);
    }

    @Override
    public int hashCode() {
        int hashCode = fid1.hashCode() + fid2.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof FragmentPair)) {
            return false;
        }

        FragmentPair fpair = (FragmentPair) obj;
        if (fid1.equals(fpair.getFid1()) && fid2.equals(fpair.getFid2()) ||
                fid1.equals(fpair.getFid2()) && fid2.equals(fpair.getFid1())) {
            return true;
        } else {
            return false;
        }
    }
}
