package org.apromore.toolbox.similaritySearch.common.algos;

import java.math.BigInteger;

public class TwoVertices {
	public BigInteger v1;
	public BigInteger v2;
	public double weight;

	public TwoVertices(BigInteger v1, BigInteger v2, double weight) {
		this.v1 = v1;
		this.v2 = v2;
		this.weight = weight;
	}

	public String toString(){
		return "("+v1+","+v2+","+weight+")";
	}

	public boolean equals(Object pair2) {
		return pair2 instanceof TwoVertices ? (v1.equals(((TwoVertices)pair2).v1) && v2.equals(((TwoVertices)pair2).v2)) : false;
	}

	public int hashCode() {
		return v1.hashCode() + v2.hashCode();
	}
}