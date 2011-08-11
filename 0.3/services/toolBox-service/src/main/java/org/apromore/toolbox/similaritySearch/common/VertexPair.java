package org.apromore.toolbox.similaritySearch.common;

import org.apromore.toolbox.similaritySearch.graph.Vertex;

public class VertexPair{
	Vertex left;
	Vertex right;
	boolean visited = false;
	double weight;
	public double ed;
	public double sem;
	public double syn;
	public double struct;
	public double parent;

	
	public VertexPair(Vertex left, Vertex right) {
		this.left = left;
		this.right = right;
	}
	
	public VertexPair(Vertex left, Vertex right, double weight) {
		this.left = left;
		this.right = right;
		this.weight = weight;
	}

	public VertexPair(Vertex first, Vertex second, double weight, double ed, double sem, double syn, double struct, double parent) {
		left = first;
		right = second;
		this.weight = weight;
		this.ed = ed;
		this.sem = sem;
		this.syn = syn; 
		this.struct = struct;
		this.parent = parent;
	}

	
	public Vertex getLeft(){
		return left;
	}
	
	public Vertex getRight(){
		return right;
	}
	
	public double getWeight(){
		return weight;
	}

}
