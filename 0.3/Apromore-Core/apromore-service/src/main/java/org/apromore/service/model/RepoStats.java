package org.apromore.service.model;

/**
 * @author Chathura Ekanayake
 */
public class RepoStats {
	
	private int totalVertices;
	private int totalEdges;
	private int storedVertices;
	private int storedEdges;


	public int getTotalVertices() {
		return totalVertices;
	}
	
	public void setTotalVertices(int totalVertices) {
		this.totalVertices = totalVertices;
	}
	
	public int getTotalEdges() {
		return totalEdges;
	}
	
	public void setTotalEdges(int totalEdges) {
		this.totalEdges = totalEdges;
	}
	
	public int getStoredVertices() {
		return storedVertices;
	}
	
	public void setStoredVertices(int storedVertices) {
		this.storedVertices = storedVertices;
	}
	
	public int getStoredEdges() {
		return storedEdges;
	}
	
	public void setStoredEdges(int storedEdges) {
		this.storedEdges = storedEdges;
	}
}
