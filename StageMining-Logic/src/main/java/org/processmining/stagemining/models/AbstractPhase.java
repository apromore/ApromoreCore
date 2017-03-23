
package org.processmining.stagemining.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.jbpt.hypergraph.abs.IVertex;
import org.processmining.stagemining.models.graph.WeightedDirectedEdge;


public class AbstractPhase implements IPhase {
//	protected PhaseModel model = null;
//	protected String name;
//	protected boolean isSource = false;
//	protected boolean isSink = false;
//	protected IVertex endVertex = null; //the ending vertex marking the milestone event of a phase
//	//Note: vertices and edges are linked to the vertices and edges of the original graph
//	protected Set<IVertex> vertices = new HashSet<IVertex>();
//	protected Set<WeightedDirectedEdge<IVertex>> edges = new HashSet<WeightedDirectedEdge<IVertex>>();
//	protected ArrayList<IPhase> subPhases = new ArrayList<IPhase>();
//	protected final static String  MINING_PROCESS = "HierarchicalAgglomerative.rmp";	
//
//	public AbstractPhase(PhaseModel model) {
//		this.model = model;
//	}
//	
//	public AbstractPhase(IPhase phase, boolean fromBeginning) {
//		this.model = phase.getPhaseModel();
//		int index = model.indexOf(phase);
//		
//		if (fromBeginning) {
//			for (int i=0; i<=index; i++) {
//				AbstractPhase p = (AbstractPhase)model.get(i);
//				this.subPhases.add(p);
//				vertices.addAll(p.getVertices());
//			}
//			
//			for (WeightedDirectedEdge<IVertex> edge : this.model.getGraph().getEdges()) {
//				if (vertices.contains(edge.getSource()) && vertices.contains(edge.getTarget())) {
//					edges.add(edge);
//				}
//			}
//			
//			this.endVertex = phase.getEndingVertex();
//		}
//		else {
//			for (int i=index; i<model.size(); i++) {
//				AbstractPhase p = (AbstractPhase)model.get(i);
//				this.subPhases.add(p);
//				vertices.addAll(p.getVertices());
//			}
//			
//			for (WeightedDirectedEdge<IVertex> edge : this.model.getGraph().getEdges()) {
//				if (vertices.contains(edge.getSource()) && vertices.contains(edge.getTarget())) {
//					edges.add(edge);
//				}
//			}
//		}
//	}
//	
//	public PhaseModel getPhaseModel() {
//		return model;
//	}
//	
//	public IPhase getNext() {
//		int i = model.indexOf(this);
//		if (i>0 && i<(model.size()-1)) {
//			return model.get(i+1);
//		}
//		else {
//			return null;
//		}
//	}
//
//	public IPhase getPrevious() {
//		int i = model.indexOf(this);
//		if (i>0) {
//			return model.get(i-1);
//		}
//		else {
//			return null;
//		}
//	}	
//	
//	public boolean isSource() {
//		return isSource;
//	}
//	
//	public boolean isSink() {
//		return isSink;
//	}
//	
//	@Override
//	/**
//	 * Note that the self-loop edges are not counted
//	 * since there can be many self-loop edges and the connectivity
//	 * of the phase is still very sparse.
//	 */
//	public double getControlFlowCohesion() throws Exception {
//		//Count the number of edges without self-loops
//		int nonSelfLoopEdgesNum = 0;
//		for (WeightedDirectedEdge<IVertex> e : edges) {
//			if (e.getSource() != e.getTarget()) {
//				nonSelfLoopEdgesNum++;
//			}
//		}
//		
//		if (vertices.isEmpty()) {
//			throw new Exception("The block has no vertices");
//		}
//		else if (vertices.size() == 1) {
//			return 0;
//		}
//		else {
//			return 1.0*nonSelfLoopEdgesNum/(vertices.size()*(vertices.size() - 1));
//		}
//	}
//	
//	@Override
//	public ArrayList<IPhase> getSubPhases() {
//		return this.subPhases;
//	}
//	
//	@Override
//	public double getActLabelCohesion() throws Exception {
//		//----------------------------------------------
//		// Build Example Set
//		//----------------------------------------------
//		ExampleSet exampleSet = this.createExampleSet(this.getVertexLabels());
//		if (exampleSet.size() <= 1) {
//			return 0;
//		}
//		
//		//-------------------------------------------------
//		// Run RapidMiner process with Hierarchical Agglomerative Clustering
//		//-------------------------------------------------
//		System.out.println("Calling to Hierarchical Agglomerative Clustering in RapidMiner");		
//		String miningProcess = System.getProperty("user.dir") + "\\" + MINING_PROCESS;
//		com.rapidminer.Process process = new com.rapidminer.Process(new File(miningProcess));
////		Operator op = process.getOperator("Clustering");
//		IOContainer ioInput = new IOContainer(new IOObject[] {exampleSet});
//		IOContainer ioResult = process.run(ioInput);
//		HierarchicalClusterModel clusterModel = (HierarchicalClusterModel)ioResult.getElementAt(0);
//		
//		//----------------------------------------------
//		// Select the set of clusters that have distance above a threshold
//		// Note that the hierarchical tree has increasing distance from bottom up
//		// So we traverse this tree from top down with breadth first search  
//		// to a level that all clusters have distance equal or less than the threshold
//		//----------------------------------------------
//		int clusterSize = this.computeClusteringSize(clusterModel, model.getActLabelDistanceThreshold());
//		int exampleSize = exampleSet.size();
//		double cohesion = 1.0*(exampleSize - clusterSize)/(exampleSize - 1);
//		
//		return cohesion;
//	}
//	
//	/**
//	 * Use depth first search to traverse the hierarchical clustering tree
//	 * Traverse down a node if it has higher distance than the threshold
//	 * Stop at a node if it is either a leaf node or has an equal or lower distance
//	 * than the threshold. Those nodes are called selected nodes.
//	 * Count the total number of selected nodes.
//	 * @param clusterModel
//	 * @return The number of selected nodes (clusters)
//	 * @throws Exception 
//	 */
//	private int computeClusteringSize(HierarchicalClusterModel clusterModel, double distanceThreshold) throws Exception {
//		Set<HierarchicalClusterNode> visited = new HashSet<HierarchicalClusterNode>();
//		Set<HierarchicalClusterNode> selected = new HashSet<HierarchicalClusterNode>();
//		Stack<HierarchicalClusterNode> stack = new Stack<HierarchicalClusterNode> ();
//		HierarchicalClusterNode root = clusterModel.getRootNode();
//
//		if (root.getDistance() <= model.getActLabelDistanceThreshold()) {
//			selected.add(root);
//		}
//		else { //start traversing from root node
//			stack.push(root);
//	        visited.add(root);
//	        while(!stack.isEmpty()) {
//	        	HierarchicalClusterNode node = (HierarchicalClusterNode)stack.pop();
//	        	Collection<HierarchicalClusterNode> childs = node.getSubNodes();
//	        	for (HierarchicalClusterNode child : childs) { 
//	        		if (!visited.contains(child)) {
//	        			if (child instanceof HierarchicalClusterLeafNode || 
//	        					child.getDistance() <= model.getActLabelDistanceThreshold()) {
//	        				selected.add(child); //no need to traverse down this node
//	        			}
//	        			else {
//	            			stack.add(child);
//	            			visited.add(child);
//	        			}
//	        		}
//	        		else {
//	        			throw new Exception("Invalid tree: loop back to visited nodes found.");
//	        		}
//	        	}
//	        }
//		}
//		
//        return selected.size();
//	}
//	
//	private Set<String> getVertexLabels() {
//		Set<String> labels = new HashSet<String>();
//		for (IVertex v : vertices) {
//			labels.add(v.getLabel());
//		}
//		return labels;
//	}
//	
//	/**
//	 * Create an example set with one Nominal attribute
//	 * This field is set as regular attribute and named as "Label"
//	 * The values are provided in the input parameter.
//	 * @return ExampleSet
//	 */
//	private ExampleSet createExampleSet(Set<String> values) {
//		// create a table
//		MemoryExampleTable table = new MemoryExampleTable();
//
//		// ------------------------------
//		// Create and add attributes
//		// ------------------------------
//		Attribute nominalAttr = AttributeFactory.createAttribute("Label", Ontology.NOMINAL);
//		table.addAttribute(nominalAttr);
//		//Attribute integerAttr = AttributeFactory.createAttribute("att2", Ontology.INTEGER);
//		//table.addAttribute(integerAttr);
//
//		//------------------------------
//		// Add data rows
//		//------------------------------
//		for (String value : values) {
//			DataRow row = new DoubleSparseArrayDataRow();
//			row.set(nominalAttr, nominalAttr.getMapping().mapString(value));
//			table.addDataRow(row);
//			
//			// For numerical nothing special to do
//			//row.set(integerAttr, 1);			
//		}
//	
//		//------------------------------
//		// create an ExampleSet from the underlying table
//		//------------------------------
//		ExampleSet exampleSet = table.createExampleSet();
//		
//		return exampleSet;
//	}
//
//	@Override
//	public Set<IVertex> getVertices() {
//		return vertices;
//	}
//	
//	@Override
//	public Set<WeightedDirectedEdge<IVertex>> getEdges() {
//		return edges;
//	}
//	
//
//	@Override
//	public boolean isTrivial() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
////	@Override
////	public IVertex getEntry() {
////		// TODO Auto-generated method stub
////		return null;
////	}
////
////	@Override
////	public IVertex getExit() {
////		// TODO Auto-generated method stub
////		return null;
////	}
//	
//	@Override
//	public IVertex getEndingVertex() {
//		return endVertex;
//	}
//	
//	@Override
//	public void setEndingVertex(IVertex endVertex) {
//		this.endVertex = endVertex;
//	}
//
//	@Override
//	public void print() throws Exception {
//		// TODO Auto-generated method stub
//		
//	}
}
