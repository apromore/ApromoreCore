package org.apromore.toolbox.clustering.algorithms.dbscan;


/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class FragmentPair {

    private String fid1;
    private String fid2;

    public FragmentPair(String fid1, String fid2) {
        this.fid1 = fid1;
        this.fid2 = fid2;
    }

    public String getFid1() {
        return fid1;
    }

    public void setFid1(String fid1) {
        this.fid1 = fid1;
    }

    public String getFid2() {
        return fid2;
    }

    public void setFid2(String fid2) {
        this.fid2 = fid2;
    }

    public boolean hasFragment(String fid) {
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
