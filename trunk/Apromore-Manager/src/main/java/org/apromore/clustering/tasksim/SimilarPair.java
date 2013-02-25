package org.apromore.clustering.tasksim;

public class SimilarPair {
	
	private int vid1;
	private int vid2;
	private double sim;
	
	public SimilarPair(int vid1, int vid2, double sim) {
		this.vid1 = vid1;
		this.vid2 = vid2;
		this.sim = sim;
	}
	
	public int getVid1() {
		return vid1;
	}
	
	public int getVid2() {
		return vid2;
	}

	public double getSim() {
		return sim;
	}

	@Override
	public int hashCode() {
		return vid1 - vid2;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj == null || !(obj instanceof SimilarPair)) {
			return false;
		}
		
		SimilarPair otherPair = (SimilarPair) obj;
		
		if (vid1 == otherPair.getVid1() && vid2 == otherPair.getVid2()) {
			return true;
		}
		
//		if (vid1 == otherPair.getVid2() && vid2 == otherPair.getVid1()) {
//			return true;
//		}
		
		return false;
	}

	@Override
	public String toString() {
		return vid1 + " - " + vid2 + " : " + sim;
	}
}
