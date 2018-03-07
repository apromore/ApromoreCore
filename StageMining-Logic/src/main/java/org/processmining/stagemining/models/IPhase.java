package org.processmining.stagemining.models;

import java.util.ArrayList;
import java.util.Set;

import org.jbpt.hypergraph.abs.IVertex;
import org.processmining.stagemining.models.graph.WeightedDirectedEdge;

public interface IPhase {
	
//	public PhaseModel getPhaseModel();
//	
//	//Indicate this is a source phase (a phase containing only the source vertex)
//	public boolean isSource();
//	
//	///Indicate this is a sink phase (a phase containing only the sink vertex)
//	public boolean isSink();
//	
//	public IPhase getNext();
//	
//	public IPhase getPrevious();	
//	
//	public Set<IVertex> getVertices();
//	
//	public Set<WeightedDirectedEdge<IVertex>> getEdges();
//	
//	// Check if this phase is trivial (too simple)
//	public boolean isTrivial();
//	
//	
//	//Cohesion based on the density of edges connecting different vertices
//	public double getControlFlowCohesion() throws Exception;
//	
//	//Cohesion based on the semantic similarity of activity labels within one phase
//	public double getActLabelCohesion() throws Exception;
//	
//	//public double getResourcePropertyCohesion();
//	
//	public ArrayList<IPhase> getSubPhases();
//	
////	public IVertex getEntry();
////	
////	public IVertex getExit();
//	
//	public IVertex getEndingVertex();
//	
//	public void setEndingVertex(IVertex endVertex);
//	
//	public void print() throws Exception;
}
