package org.apromore.toolbox.clustering.dissimilarity.model;

public class TwoVertices implements Comparable<TwoVertices> {
	public Integer v1;
	public Integer v2;

	public TwoVertices(Integer v1, Integer v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	public String toString(){
		return "("+v1+","+v2+")";
	}

	public boolean equals(Object pair2){
		return pair2 instanceof TwoVertices?(v1.equals(((TwoVertices)pair2).v1) && v2.equals(((TwoVertices)pair2).v2)):false;
	}

	public int hashCode(){
		return v1.hashCode() + v2.hashCode();
	}

	public int compareTo(TwoVertices o) {
		return equals(o) ? 0 : 1;
	}
}