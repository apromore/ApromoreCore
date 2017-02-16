package org.processmining.stagemining.models.graph;

import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;

public class WeightedDirectedEdge<V extends IVertex> extends DirectedEdge {
	private float weight;
	private EdgeTypeEnum type = EdgeTypeEnum.TREE;
	
	public WeightedDirectedEdge(AbstractMultiDirectedGraph<?, Vertex> g, Vertex source, Vertex target, float weight) {
		 super(g, source, target);
		 this.weight = weight;
	}
	
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public float getWeight() {
		return this.weight;
	}
	
	public void setEdgeType(EdgeTypeEnum type) {
		this.type = type;
	}
	
	public EdgeTypeEnum getEdgeType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		return String.format("%s->%s(%s)", this.source, this.target, this.weight);
	}
}
