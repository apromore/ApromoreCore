package org.processmining.stagemining.models.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;
import org.processmining.stagemining.utils.ConnectedComponentSearch;
import org.processmining.stagemining.utils.GraphUtils;

import com.google.common.collect.BiMap;

public class WeightedDirectedGraph extends AbstractDirectedGraph<WeightedDirectedEdge<IVertex>,Vertex> {
	
	private IVertex source = null;
	private IVertex sink = null;
	private Vertex2 cutPoint = null;
	
//	private XLog log = null;
	
	/**
	 * Create a new graph based on the original graph and the cut point
	 * @param oriGraph: the original graph
	 * @param nodes: set of nodes in the new graph
	 * @param source: the new source node of the graph
	 * @param sink: the new sink node of the grahp
	 * @param cutPoint: the cut point on the old graph
	 */
	public WeightedDirectedGraph(WeightedDirectedGraph oriGraph, Set<IVertex> nodes, IVertex source, IVertex sink, IVertex cutPoint) {
		super();
		this.source = source;
		this.sink = sink;
		this.addVertex((Vertex)source);
		this.addVertex((Vertex)sink);
		for (IVertex v : nodes) { 
			this.addVertex((Vertex)v); //the source and sink would not be added again.
		}
		for (WeightedDirectedEdge<IVertex> e : oriGraph.getEdges()) {
			//Edges within the new graph
			if (this.getVertices().contains(e.getSource()) && this.getVertices().contains(e.getTarget())) {
				this.addEdge(e.getSource(), e.getTarget(), e.getWeight());
			}
			
			// Edges on the cutSet 
			IVertex endPoint = null;
			if (cutPoint == source) {
				endPoint = source;
			}
			else if (cutPoint == sink) {
				endPoint = sink;
			}
			// Connect these edges to the source/sink if it is the cut point
			Set<WeightedDirectedEdge<IVertex>> cutSet = ((Vertex2)cutPoint).getMinCutSet();
			for (WeightedDirectedEdge<IVertex> ec : cutSet) {
				if (nodes.contains(ec.getTarget())) {
					this.addEdge((Vertex)endPoint, ec.getTarget(), ec.getWeight());
				}
				else if (nodes.contains(ec.getSource())) {
					this.addEdge(ec.getSource(), (Vertex)endPoint, ec.getWeight());
				}
			}
		}
		
	}
	
	public WeightedDirectedGraph() {
//		this.log = log;
		super();
	}
	
	public WeightedDirectedEdge<IVertex> addEdge(Vertex s, Vertex t, float weight) {
		if (s == null || t == null) return null;
		Collection<WeightedDirectedEdge<IVertex>> es = this.getEdgesWithSourceAndTarget(s, t);
		if (es.size()>0) {
			Iterator<WeightedDirectedEdge<IVertex>> i = es.iterator();
			while (i.hasNext()) {
				WeightedDirectedEdge<IVertex> e = i.next();
				if (e.getVertices().size()==2)
					return null;
			}
		}
		
		WeightedDirectedEdge<IVertex> e = new WeightedDirectedEdge<IVertex>(this, s, t, weight);
		return e;
	}
	
	public void setSource(Vertex source) {
		this.source = source;
	}
	
	public IVertex getSource() {
		return this.source;
	}

	public void setSink(Vertex sink) {
		this.sink = sink;
	}
	
	public IVertex getSink() {
		return this.sink;
	}	
	
	public IVertex getVertexByName(String name) {
		for (IVertex v : this.getVertices()) {
			if (v.getName().toLowerCase().equals(name)) {
				return v;
			}
		}
		return null;
	}
	
	public Collection<Vertex> getActivityVertices() {
		Collection<Vertex> activitySet = new HashSet<Vertex>();
		activitySet = this.getVertices();
		activitySet.remove(this.getSource());
		activitySet.remove(this.getSink());
		return activitySet;
	}
	
	public float getTotalFlow() {
		float total = 0;
		for (WeightedDirectedEdge<IVertex> e : this.getEdgesWithSource((Vertex)this.source)) {
			total += e.getWeight();
		}
		return total;
	}
	
	public double getTotalWeight() {
		double total = 0;
		for (WeightedDirectedEdge<IVertex> e : this.getEdges()) {
			total += e.getWeight();
		}
		return total;
	}
	
	/**
	 * This method removes edge in the edges collection
	 * Not on a copy version like the removeEdge method  
	 * @param edge
	 */
	public void removeEdge2(WeightedDirectedEdge<IVertex> edge) {
		this.edges.remove(edge);
		this.vertices.get(edge.getSource()).remove(edge);
		this.vertices.get(edge.getTarget()).remove(edge);
		
	}
	
	/**
	 * This ensures to remove edges out of the graph
	 * NOTE!!! The key here is always to retrieve edges in the graph
	 * from the source and target nodes, never use the
	 * edges in the input set.
	 * @param edges
	 */
	public void removeEdges(Set<WeightedDirectedEdge<IVertex>> edges) {
		for (WeightedDirectedEdge<IVertex> e : edges) {
			Collection<WeightedDirectedEdge<IVertex>> toRemove = this.getEdgesWithSourceAndTarget(e.getSource(), e.getTarget());
			for (WeightedDirectedEdge<IVertex> edge : toRemove) {
				this.removeEdge(edge);
				this.removeEdge2(edge);
			}
		}
	}
	
	/**
	 * Note that always add new edges through source and target nodes
	 * It does not work if simply call this.getEdges().addEdge(e).
	 * @param edges
	 */
	public void reconnectEdges(Set<WeightedDirectedEdge<IVertex>> edges) {
		for (WeightedDirectedEdge<IVertex> e : edges) {
			if (this.getEdgesWithSourceAndTarget(e.getSource(), e.getTarget()).isEmpty()) {
				this.addEdge(e.getSource(), e.getTarget(), e.getWeight());
			}
		}
	}
	
	/**
	 * Compute the minimum cut point
	 * This method is executed only once for the graph
	 * The next call will take the min cut found from the last call
	 * The min-cut can be zero if there is a node that if it is 
	 * removed, the graph becomes disconnected.
	 * @throws Exception 
	 */
    public SortedSet<Vertex2> searchCutPoints() throws Exception {
		//-------------------------------
		// Compute min cut value for every vertex
		// using Ford-Fulkerson (based on max flow)
		// Then, they are sorted by the min-cut value
		// Note that both the bidirectional matrix and object-oriented representation
		// must maintain similar graph
		//-------------------------------
		BiMap<IVertex,Integer> vertexMap = GraphUtils.buildVertexMap(this);
//		System.out.println("Vertex Map: " + vertexMap);
		int[][] bidiAdjacentMatrix = GraphUtils.buildBidiAdjacencyMatrix(this, vertexMap, null);
//		System.out.println("Bi-directional Adjacency Matrix: ");
//		GraphUtils.printMatrix(bidiAdjacentMatrix);
		
		SortedSet<Vertex2> sortedVertices = new TreeSet<Vertex2>( //to sort the nodes by their min-cut values
                new Comparator<Vertex2>() {
                    @Override
                    public int compare(Vertex2 v1, Vertex2 v2) {
                    	if (v1.getMinCut() <= v2.getMinCut()) {
                    		return -1;
                    	}
                    	else {
                    		return +1;
                    	}
                    }
                });  
		
		// Take the cut for prioritized nodes
		for (IVertex v : this.getVertices()) {
			// Never cut on the source and the sink
			// or cut on the node with strong inward and outward flows on single edge 
			//if (v == this.getSource() || v == this.getSink() || this.checkStrongSingleEdgeFlow(v)) { 
			if (v == this.getSource() || v == this.getSink()) {
				continue;
			}
			
			Vertex2 v2 = (Vertex2)v;
			ArrayList<String> cutList = GraphUtils.computeMinCut(bidiAdjacentMatrix, vertexMap.get(v2));
			
			//Extract set of edges as the min-cut set
			//Note that the cut set might be empty if there are no edges since
			//the node could be the only connection point between two phases. 
			Set<WeightedDirectedEdge<IVertex>> cutSet = new HashSet<WeightedDirectedEdge<IVertex>>();
			if (cutList.size()>1) { // if the cutset has some edges 
				for (int i=1;i<cutList.size();i++) {
					String[] indexA = cutList.get(i).split(",");
					Vertex va = (Vertex)vertexMap.inverse().get(Integer.valueOf(indexA[0].trim()));
					Vertex vb = (Vertex)vertexMap.inverse().get(Integer.valueOf(indexA[1].trim()));
					cutSet.addAll(this.getEdgesWithSourceAndTarget(va, vb));
					cutSet.addAll(this.getEdgesWithSourceAndTarget(vb, va));
				}
			}
			
			//Double minCut = 1.0*Double.valueOf(cutList.get(0))/this.getUnweightedNodeDegree(v2);
			//Double minCut = 1.0*Double.valueOf(cutList.get(0))/this.getWeightedNodeDegree(v2);
			//Double minCut = 1.0*Double.valueOf(cutSize)/this.getWeightedNodeDegree(v2);
			Double minCut = Double.valueOf(cutList.get(0));
//			System.out.println("Vertex " + v.getName() + ": Min-cut=" + minCut + ", Cut-set: " + cutSet.toString());
			
			v2.setMinCut(minCut);
			v2.setMinCutSet(cutSet);
			
			if (minCut < this.getTotalFlow()) sortedVertices.add(v2);
			
		}
		
		return sortedVertices;
    }
   
    
    public Vertex2 getCutPoint() {
    	return this.cutPoint;
    }
    
    /**
     * Cut the graph into two components based on the cut point
     * Return a list of two components as the result of the cut
     * The first component contains the source, the second contains the sink
     * Other components (dangling components) are associated with the cut-point
     * @return: list of two components
     * @throws Exception
     */
    public List<Set<IVertex>> cut(Vertex2 cutPoint) throws Exception {
		
		//---------------------------------------
		// Remove the vertex and its associated edges to find connected components
		// The graph after this edge removal become disconnected
		//---------------------------------------_
		Set<WeightedDirectedEdge<IVertex>> vertexEdges = new HashSet<WeightedDirectedEdge<IVertex>>();
		vertexEdges.addAll(this.getEdgesWithSource(cutPoint));
		vertexEdges.addAll(this.getEdgesWithTarget(cutPoint));
		
		this.removeEdges(vertexEdges);
		this.removeEdges(cutPoint.getMinCutSet());
		this.removeVertex(cutPoint);
		
		//---------------------------------------
		// Find disconnected components, each is a phase
		//---------------------------------------
		ConnectedComponentSearch search = new ConnectedComponentSearch(this);
		Set<Set<IVertex>> connectedComponents = search.findConnectedComponents();
		
		List<Set<IVertex>> components = new ArrayList<Set<IVertex>>(connectedComponents);
		Set<IVertex> firstComponent = new HashSet<IVertex>();
		Set<IVertex> lastComponent = new HashSet<IVertex>();
		
		// If more than two components returned from the cut
		// those components are called dangling components
		Set<IVertex> danglingComponents = new HashSet<IVertex>();
		for (Set<IVertex> component : components) {
			if (component.contains(this.getSource())) {
				firstComponent.addAll(component);
			}
			else if (component.contains(this.getSink())) {
				lastComponent.addAll(component);
			}
			else {
				danglingComponents.addAll(component);
			}
		}
		
			// Some components may be disconnected since they are only connected with the cut point 
			// They will be stored to be added to the phase between this cut point
			// and the next one.
		if (!danglingComponents.isEmpty()) {
			cutPoint.getDanglingComponents().add(danglingComponents);
		}
		
		if (firstComponent.isEmpty() || lastComponent.isEmpty()) {
			throw new Exception("Invalid cut occurred due to empty cut components!");
		}
		
		List<Set<IVertex>> result = new ArrayList<Set<IVertex>>();
		result.add(firstComponent);
		result.add(lastComponent);
		
		//---------------------------------------
		// Reconnect the vertex and its associated edges
		//---------------------------------------
		this.addVertex(cutPoint);
		this.reconnectEdges(vertexEdges);
		this.reconnectEdges(cutPoint.getMinCutSet());
		
		return result;
    }
    
    public double getWeightedNodeDegree(Vertex v) {
    	double degree = 0.0;
    	for (WeightedDirectedEdge<IVertex> e : this.getEdgesWithTarget(v)) {
    		degree += e.getWeight();
    	}
    	for (WeightedDirectedEdge<IVertex> e : this.getEdgesWithSource(v)) {
    		degree += e.getWeight();
    	}
    	return degree;
    }
    
    public double getUnweightedNodeDegree(Vertex v) {
    	double degree = 0.0;
    	for (WeightedDirectedEdge<IVertex> e : this.getEdgesWithTarget(v)) {
    		degree++;
    	}
    	for (WeightedDirectedEdge<IVertex> e : this.getEdgesWithSource(v)) {
    		degree++;
    	}
    	return degree;
    }
	
    /**
     * This method filter the paths on this graph and 
     * only keep those paths that have frequency above the
     * input threshold 
     * @param threshold: the minimum of path frequency value over
     * the total number of paths in the log. Value: from 0 to 1.
     */
    public void filterPaths(double keepThreshold) {
    	double removeThreshold = 1 - keepThreshold;
    	double totalWeight = this.getTotalWeight();
    	SortedSet<WeightedDirectedEdge<IVertex>> sortedEdges = new TreeSet<WeightedDirectedEdge<IVertex>>( //to sort the nodes by their min-cut values
                new Comparator<WeightedDirectedEdge<IVertex>>() {
                    @Override
                    public int compare(WeightedDirectedEdge<IVertex> e1, WeightedDirectedEdge<IVertex> e2) {
                    	if (e1.getWeight() < e2.getWeight()) {
                    		return -1;
                    	}
                    	else {
                    		return +1;
                    	}
                    }
                });  

    	//sortedEdges.addAll(this.getEdges());
    	for (WeightedDirectedEdge<IVertex> e : this.getEdges()) {
    		sortedEdges.add(e);
    	}
    	
    	for (WeightedDirectedEdge<IVertex> e : sortedEdges) {
    		if (1.0*e.getWeight()/totalWeight <= removeThreshold) {
    			IVertex source = e.getSource();
    			IVertex target = e.getTarget();
//    			if (this.getOutgoingEdges((Vertex)source).size() > 1 && 
//    					this.getIncomingEdges((Vertex)target).size() > 1 &&
//    					source != this.getSource() &&
//    					target != this.getSink()) {
        		if (this.getOutgoingEdges((Vertex)source).size() > 1 && 
        					this.getIncomingEdges((Vertex)target).size() > 1) {
    				this.removeEdge(e);
    				this.removeEdge2(e);
    			}
    		}
    	}
    }
    
    /**
     * Return a node clustering from an activity label clustering
     * The activity label clustering string has this format [[a1,a2],[a3,a4,a5],[a6,a7,a8,a9]]
     * Note: activity name (label) cannot contain "," "]", "[" character
     * @param activityClustering
     * @return: node clustering of type List<Set<IVertex>>
     * @throws Exception 
     */
    public List<Set<IVertex>> getNodeClustering (List<Set<String>> listOfLabelSet) throws Exception {
    	List<Set<IVertex>> listOfVertexSet = new ArrayList<Set<IVertex>>();
    	for (Set<String> labelSet : listOfLabelSet) {
    		Set<IVertex> vertexSet = new HashSet<IVertex>();
    		for (String label : labelSet) {
    			IVertex v = this.getVertexByName(label);
    			if (v != null) {
    				vertexSet.add(v);
    			}
    			else {
    				throw new Exception("Cannot find a vertex in the graph with a label = " + label);
    			}
    		}
    		listOfVertexSet.add(vertexSet);
    	}
    	return listOfVertexSet;
    }
    
    
}
