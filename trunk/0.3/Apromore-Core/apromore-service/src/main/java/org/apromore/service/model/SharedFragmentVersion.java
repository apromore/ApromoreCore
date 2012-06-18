package org.apromore.service.model;

/**
 * @author Chathura Ekanayake
 */
public class SharedFragmentVersion {

	private int fragmentVersionid;
	private int numberOfUses;


    /**
     * Defualt Constructor.
     */
    public SharedFragmentVersion() { }

    /**
     * Defualt Constructor.
     */
    public SharedFragmentVersion(int versionId, int num) {
        this.fragmentVersionid = versionId;
        this.numberOfUses = num;
    }

    /**
     * Defualt Constructor.
     */
    public SharedFragmentVersion(String versionId, Long num) {
        this.fragmentVersionid = Integer.valueOf(versionId);
        this.numberOfUses = num.intValue();
    }

	public int getFragmentVersionid() {
		return fragmentVersionid;
	}
	
	public void setFragmentVersionid(int fragmentVersionid) {
		this.fragmentVersionid = fragmentVersionid;
	}
	
	public int getNumberOfUses() {
		return numberOfUses;
	}
	
	public void setNumberOfUses(int numberOfUses) {
		this.numberOfUses = numberOfUses;
	}
}
