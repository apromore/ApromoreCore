package org.apromore.toolbox.clustering.algorithm.dbscan;


/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class FragmentPair {

    private Integer fid1;
    private Integer fid2;
    private Double distance;

    public FragmentPair() {
    }

    public FragmentPair(Integer fid1, Integer fid2) {
        this.fid1 = fid1;
        this.fid2 = fid2;
    }

    public Integer getFid1() {
        return fid1;
    }

    public void setFid1(Integer fid1) {
        this.fid1 = fid1;
    }

    public Integer getFid2() {
        return fid2;
    }

    public void setFid2(Integer fid2) {
        this.fid2 = fid2;
    }

    public boolean hasFragment(Integer fid) {
        return fid1.equals(fid) || fid2.equals(fid);
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }


    @Override
    public int hashCode() {
        return fid1.hashCode() + fid2.hashCode();
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
