package org.apromore.toolbox.similaritySearch.graph;

import java.util.HashSet;

public class Edge {
	
	private Graphics fromG;
	public Graphics getFromG() {
		return fromG;
	}

	public void setFromG(Graphics fromG) {
		this.fromG = fromG;
	}

	public void printLabels() {
		System.out.print("\n\t");
		for (String l : labels) {
			System.out.print(l+",");
		}
		System.out.print("\n");
	}
	
	public Graphics getToG() {
		return toG;
	}

	public void setToG(Graphics toG) {
		this.toG = toG;
	}

	private Graphics toG;
	
	// the starting point
	private String fromVertex;
	// end point
	private String toVertex;
	
	private String id;
	
	private boolean labelAddedToModel = false;
	
	public static Edge copyEdge(Edge e) {
		
		Edge toReturn = new Edge(e.getFromVertex(), e.getToVertex(), e.id);
		toReturn.labelAddedToModel = e.labelAddedToModel;
		return toReturn;
	}
	
	public boolean isLabelAddedToModel() {
		return labelAddedToModel;
	}

	public void addLabelToModel() {
		labelAddedToModel = true;
	}
	
	public void removeLabelFromModel() {
		labelAddedToModel = false;
	}

	private HashSet<String> labels = new HashSet<String>();
	
	public HashSet<String> getLabels() {
		return labels;
	}

	public void addLabels(HashSet<String> labels) {
		this.labels.addAll(labels);
	}

	public void addLabel(String label) {
		labels.add(label);
	}

	/**
	 * The constructor for new edge
	 * @param fromVertex the starting point of the edge (the identifier of the node)
	 * @param toVertex the ending point of the edge (the identifier of the node)
	 */
	public Edge(String fromVertex, String toVertex, String id){
		this.fromVertex = fromVertex;
		this.toVertex   = toVertex;
		this.id = id;
	}
	
	// for greedy algorithm
	public Edge(String fromVertex, String toVertex){
		this.fromVertex = fromVertex;
		this.toVertex   = toVertex;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	/**
	 * Metgod for getting stating point of the edge
	 * @return the identifier of the ending point of an edge
	 */
	public String getToVertex() {
		return toVertex;
	}

	/**
	 * Method for getting starting point of an edge.
	 * @return an identifier of the starting point of an egge
	 */
	public String getFromVertex() {
		return fromVertex;
	}

	/**
	 * Method for setting the identifier of an ending vertex of an edge.
	 * @param newToVertex the identifier of an new ending point edge
	 */
	public void setToVertex(String newToVertex) {
		toVertex = newToVertex;
	}

	/**
	 * Method for setting the identifier of an starting vertex of an edge.
	 * @param newFromVertex the identifier of an new starting point edge
	 */
	public void setFromVertex(String newFromVertex) {
		fromVertex = newFromVertex;
	}

	public int hashCode(){
		return fromVertex.hashCode() + toVertex.hashCode();
	}
	
	public boolean equals(Object pair2) {
		return pair2 instanceof Edge ? (fromVertex.equals(((Edge) pair2).fromVertex) && toVertex.equals(((Edge) pair2).toVertex)) : false;
	}
	
	public String toString(){
		return "Edge(" + fromVertex + " -> " + toVertex +")";
	}
}
	
