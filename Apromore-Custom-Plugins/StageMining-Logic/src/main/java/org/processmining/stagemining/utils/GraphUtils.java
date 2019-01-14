package org.processmining.stagemining.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.algo.tree.rpst.IRPST;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;
import org.processmining.stagemining.models.graph.Vertex2;
import org.processmining.stagemining.models.graph.WeightedDirectedEdge;
import org.processmining.stagemining.models.graph.WeightedDirectedGraph;

import java.text.ParseException;
import java.util.*;

public class GraphUtils {
    /**
     * Build JBPT graph from XLog
     * Events within a case in the log must be ordered by timestamps
     * Only take complete events into account. The relation is directly followed
     * The assumption is an activity is always completed before the next activity is started.
     * NOTE: ALL TRACES IN THE INPUT LOG MUST HAVE BEEN ADDED A COMMON START (named "start") AND END EVENT (named "end")
     * @param log
     * @throws ParseException 
     */
	public static WeightedDirectedGraph buildGraph(XLog log) throws ParseException {
		WeightedDirectedGraph graph = new WeightedDirectedGraph();
		
		//-------------------------------
		// Build the dependency matrix
		// Note that two default events are start and end event of every case.
		// The first index (row/column) of the matrix is for the start event
		// The last index is for the end event
		//-------------------------------
		Map<String,Integer> eventNameCountMap = new HashMap<String,Integer>();
		Map<Integer,String> countEventNameMap = new HashMap<Integer,String>();
		int index = 1; //must start from 1 since index 0 is for the start event 
		for (XTrace trace : log) {
			for (XEvent evt : trace) {
				String eventName = LogUtilites.getConceptName(evt).toLowerCase();
				if (eventName.equals("start") || eventName.equals("end")) continue;
				if (!eventNameCountMap.containsKey(eventName)) {
					eventNameCountMap.put(eventName, index);
					countEventNameMap.put(index, eventName);
					index++;
				}
			}
		}
		
		//--------------------------------
		// Create start/end event
		//--------------------------------
//		DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
//		XFactory factory = new XFactoryNaiveImpl();
//		
//		XEvent startEvent = factory.createEvent();
//    	XAttributeMap startEventMap = factory.createAttributeMap();
//    	startEventMap.put("concept:name", factory.createAttributeLiteral("concept:name", "start", null));
//    	startEventMap.put("lifecycle:transition", factory.createAttributeLiteral("lifecycle:transition", "complete", null));
//    	startEventMap.put("time:timestamp", factory.createAttributeTimestamp("time:timestamp", df.parse("01/01/1970"), null));
//    	startEvent.setAttributes(startEventMap);	
    	eventNameCountMap.put("start", 0);
    	countEventNameMap.put(0, "start");
//    	
//		XEvent endEvent = factory.createEvent();
//    	XAttributeMap endEventMap = factory.createAttributeMap();
//    	endEventMap.put("concept:name", factory.createAttributeLiteral("concept:name", "end", null));
//    	endEventMap.put("lifecycle:transition", factory.createAttributeLiteral("lifecycle:transition", "complete", null));
//    	endEventMap.put("time:timestamp", factory.createAttributeTimestamp("time:timestamp", df.parse("01/01/2020"), null));
//    	endEvent.setAttributes(endEventMap);	
    	eventNameCountMap.put("end", eventNameCountMap.size());
    	countEventNameMap.put(countEventNameMap.size(), "end");
		
		if (eventNameCountMap.size() == 0) return null;
		
		int[][] dependency = new int[eventNameCountMap.size()][eventNameCountMap.size()];
		
		for (XTrace trace : log) {
			XEvent preEvt = null;
//			trace.add(0, startEvent);
//			trace.add(endEvent);
			for (XEvent evt : trace) {
				if (!LogUtilites.getLifecycleTransition(evt).toLowerCase().equals("complete")) {
					continue;
				}
				String eventName = LogUtilites.getConceptName(evt).toLowerCase();
				if (preEvt != null) {
					String preEventName = LogUtilites.getConceptName(preEvt).toLowerCase();
					int preIndex = eventNameCountMap.get(preEventName);
					int curIndex = eventNameCountMap.get(eventName); //curIndex can be equal to preIndex for one-activity loop
					// This is absolute frequency.
					// For case frequency, need to keep track of cell [preIndex][curIndex] within a trace
					dependency[preIndex][curIndex] = dependency[preIndex][curIndex] + 1;
					
					//Take care of different events with the same timestamp
//					if (preEventTime.isBefore(eventTime) || preEvt == startEvent) {
//						preEvt = evt;
//					}
					preEvt = evt;
				}
				else {
					preEvt = evt;
				}
			}
			
		}
		
//		System.out.println(eventNameCountMap);
//		printMatrix(dependency);

		//-------------------------------
		// Make graph from dependency matrix
		//-------------------------------
		Vertex2[] arrVertex = new Vertex2[eventNameCountMap.size()];
		for (String eventName : eventNameCountMap.keySet()) {
			Vertex2 v = new Vertex2(eventName);
			arrVertex[eventNameCountMap.get(eventName)] = v;
			graph.addVertex(v);
			if (eventName.equals("start")) graph.setSource(v);
			if (eventName.equals("end")) graph.setSink(v);
		}
		
		for (int i=0;i<eventNameCountMap.size();i++) {
			for (int j=0;j<eventNameCountMap.size();j++) { 
				if (dependency[i][j] > 0) {
					graph.addEdge(arrVertex[i], arrVertex[j], dependency[i][j]);
				}
			}
		}
		
		return graph;
	}
	
	/**
	 * Build a graph from an input graph to be used for Ford Fulkerson
	 * The main idea is to run the Ford-Fulkerson algo on a directed graph
	 * as if it is an undirected graph. To apply Ford-Fulkerson for an undirected
	 * graph, every edge between two vertices v1,v2 must be divided into two 
	 * directed edges v1v2 and v2v1 with the same weight as the original edge.
	 * 
	 * The new graph has the same vertices as the input graph. Only the
	 * edges are different. For any two connected vertices v1, v2 in the input graph,
	 * assume that there are two edges connecting v1, v2, called v1v2 and v2v1.
	 * Two corresponding edges v1v2 and v2v1 are created in the new graph. 
	 * They have the same weight w, where w = w(v1v2) + w(v2v1) of the input graph. 
	 * The new graph does not have self-loop edges.
	 * @param g
	 * @return
	 */
//	public static WeightedDirectedGraph buildBidirectionalGraph(WeightedDirectedGraph g) {
//		WeightedDirectedGraph newGraph = new WeightedDirectedGraph();
//		newGraph.addVertices(g.getVertices());
//		
//		for (WeightedDirectedEdge<IVertex> e : g.getEdges()) {
//			Vertex v1 = e.getSource();
//			Vertex v2 = e.getTarget();
//			if (v2 != v1 && newGraph.getEdges(v1,v2).isEmpty()) {
//				float totalWeight = 0;
//				for (WeightedDirectedEdge<IVertex> e2 : g.getEdges(v1, v2)) { //edges connecting v1,v2 in both directions
//					totalWeight += e2.getWeight();
//				}
//				newGraph.addEdge(v1, v2, totalWeight);
//				newGraph.addEdge(v2, v1, totalWeight);
//			}
//		}
//		
//		return newGraph;
//	}
	
	/**
	 * Similar to buildBidirectionalGraph but the return graph has a matrix form
	 * The matrix has equal number of rows and columns, each row/column represents one vertex
	 * A cell represents an edge from the row (source) to the column (target)
	 * The first index is used for the source vertex
	 * The last index is for the sink vertex
	 * The cell that has an edge between the row and column is assigned the total weight between them.
	 * The cell that has no edge between the row and column is assigned 0.
	 * NOTE: the new graph representation has all exit edges removed except one 
	 * that is on the path from the start to the sink with statistically highest 
	 * number of distinct events per trace.
	 * @param g: the input graph
	 * @param vertexMap: to map from a vertex to the matrix column/row index
	 * @return: matrix-based graph
	 */
	public static int[][] buildBidiAdjacencyMatrix(WeightedDirectedGraph g, Map<IVertex,Integer> vertexMap, IVertex lastExitVertex) {
		int size = g.getVertices().size();
		int[][] matrix = new int[size][size];
		
		//Initialize with all -1 cell. The actual values are assigned below.
//		for (int i=0;i<size;i++) {
//			for (int j=0;i<size;i++) {
//				matrix[i][j] = -1;
//			}
//		}
		
		for (WeightedDirectedEdge<IVertex> e : g.getEdges()) {
			Vertex v1 = e.getSource();
			Vertex v2 = e.getTarget();
			
//			if (v1 == g.getSource()) { 
//				matrix[vertexMap.get(v1)][vertexMap.get(v2)] = Integer.MAX_VALUE;
//			}
//			if (v2 == g.getSink()) {
//				matrix[vertexMap.get(v1)][vertexMap.get(v2)] = Integer.MAX_VALUE;
//			}
//			else 
			if (v2 != v1) { // don't include self-loop
				int totalWeight = 0;
				for (WeightedDirectedEdge<IVertex> edge : g.getEdgesWithSourceAndTarget(v1, v2)) { 
					totalWeight += edge.getWeight();
				}
				for (WeightedDirectedEdge<IVertex> edge : g.getEdgesWithSourceAndTarget(v2, v1)) {
					totalWeight += edge.getWeight();
				}
				matrix[vertexMap.get(v1)][vertexMap.get(v2)] = totalWeight;
				matrix[vertexMap.get(v2)][vertexMap.get(v1)] = totalWeight;
			}
		}
		
		//Remove exit edges not recognized as a mainstream edge
		if (lastExitVertex != null) {
			int last = vertexMap.get(lastExitVertex);
			for (int i=0; i<matrix.length; i++) {
				if (i != last) {
					matrix[i][matrix.length-1] = 0;
					matrix[matrix.length-1][i] = 0;
				}
				else {
					matrix[i][matrix.length-1] = Integer.MAX_VALUE;
					matrix[matrix.length-1][i] = Integer.MAX_VALUE;
				}
			}
		}
		
		return matrix;
	}
	
	/*
	 * The first index is used for the source vertex
	 * The last index is for the sink vertex
	 */
	public static BiMap<IVertex,Integer> buildVertexMap(WeightedDirectedGraph g) {
		BiMap<IVertex,Integer> map = HashBiMap.create();
		map.put(g.getSource(), 0);
		map.put(g.getSink(), g.getVertices().size()-1);
		
		int index = 1;
		for (IVertex v : g.getVertices()) {
			if (v == g.getSource() || v == g.getSink()) continue;
			map.put(v, index);
			index++;
		}
		
		return map;
	}
	
	/**
	 * Remove back edges using Breadth-First-Search traversal
	 * Note that all the edges connecting nodes at the same level (siblings) or
	 * from a lower level to a higher level will be removed because they are back-edges
	 * in the sense of BFS.
	 * @param graph
	 * @return set of edges which have been removed
	 */
	public static Set<WeightedDirectedEdge<IVertex>> removeBackEdges(WeightedDirectedGraph graph) {
		// Contains visited vertices and their level (source node is level 1) 
        Map<IVertex, Integer> visited = new HashMap<IVertex, Integer>();
//		Set<IVertex> visited = new HashSet<IVertex>();
        Set<WeightedDirectedEdge<IVertex>> removedEdges = new HashSet<WeightedDirectedEdge<IVertex>>();
 
        // Create a queue for BFS
        LinkedList<IVertex> queue = new LinkedList<IVertex>();
 
        // Start from the source, mark the current node as visited and enqueue it
        IVertex s = graph.getSource();
        visited.put(s,1);
        queue.add(s);
        while (queue.size() != 0) {
            // Dequeue a vertex from queue and print it
            IVertex v = queue.poll();
            System.out.print(v.getName()+"->");
 
            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
//            Iterator<Vertex> i = graph.getAdjacent((Vertex)s).iterator();
            int level = visited.get(v) + 1;
            for (Vertex n : graph.getDirectSuccessors((Vertex)v)) {
            	if (n==graph.getSink()) continue;
            	
                if (!visited.containsKey(n)) {
                	visited.put(n, level);
                    queue.add(n);
                }
                else { // if (visited.get(n) < visited.get(v)) { //v->n is a back-edge from lower to higher level
                	WeightedDirectedEdge backEdge = graph.getDirectedEdge((Vertex)v, (Vertex)n);
                	removedEdges.add(backEdge);
                }
            }
        }
        
        for (WeightedDirectedEdge e : removedEdges) {
        	graph.removeEdge(e);
        	graph.removeEdge2(e);
        }
        
        return removedEdges;
        
	}
	
	public static Set<WeightedDirectedEdge<IVertex>> removeForwardEdges(WeightedDirectedGraph graph) {
		// Contains visited vertices and their level (source node is level 1) 
        Map<IVertex, Integer> visited = new HashMap<IVertex, Integer>();

        Set<WeightedDirectedEdge<IVertex>> removedEdges = new HashSet<WeightedDirectedEdge<IVertex>>();
 
        // Create a queue for BFS
        LinkedList<IVertex> queue = new LinkedList<IVertex>();
 
        // Start from the SINK, mark the current node as visited and enqueue it
        IVertex s = graph.getSink();
        visited.put(s,1);
        queue.add(s);
        while (queue.size() != 0) {
            // Dequeue a vertex from queue and print it
            IVertex v = queue.poll();
            System.out.print(v.getName()+"<-");
 
            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
            int level = visited.get(v) + 1;
            for (Vertex p : graph.getDirectPredecessors((Vertex)v)) {
                if (!visited.containsKey(p)) {
                	visited.put(p, level);
                    queue.add(p);
                }
            }
            
            for (Vertex p : graph.getDirectPredecessors((Vertex)v)) {
                if (visited.get(p) == visited.get(v)) {
                	continue;
                }
//                else if (visited.get(n) < visited.get(v)) { //n->v is a forward-edge from lower to higher level
//                	WeightedDirectedEdge forwardEdge = graph.getDirectedEdge((Vertex)v, (Vertex)n);
//                	removedEdges.add(forwardEdge);
//                }
                for (Vertex u : graph.getDirectSuccessors(p)) {
                	if (visited.containsKey(u) && visited.get(u) < visited.get(v)) { // p->u is a forward-edge
                		removedEdges.add(graph.getDirectedEdge(p, u));
                	}
                }
            }
        }
        
        for (WeightedDirectedEdge e : removedEdges) {
        	graph.removeEdge(e);
        	graph.removeEdge2(e);
        }
        
        return removedEdges;
	}
	
	public static Set<WeightedDirectedEdge<IVertex>> removeExitEdges(WeightedDirectedGraph graph) throws Exception {
		Set<WeightedDirectedEdge<IVertex>> removedEdges = new HashSet<WeightedDirectedEdge<IVertex>>();
		
		//Identify the exit edge that is on the longest path
//		List<IVertex> longestPath = findLongestPath(graph, graph.getSource(), graph.getSink());
//		System.out.println("Longest path: " + longestPath.toString());
//		IVertex avoidVertex = null;
//		int index = longestPath.indexOf(graph.getSink());
//		if (index >= 1) {
//			avoidVertex = longestPath.get(index-1);
//		}
//		else {
//			throw new Exception("No longest path from the source and the sink of the graph is found");
//		}
		
		IVertex sink = graph.getSink();
		for (Vertex v : graph.getDirectPredecessors((Vertex)sink)) {
			//if (v == avoidVertex) continue; //do not remove the exit edge from this vertex
			
			Collection<Vertex> successors = graph.getDirectSuccessors(v);
			//Direct successors can be the same vertex (self-loop)
			if ((successors.size() >= 2 && successors.contains(sink) && !successors.contains(v)) ||
				(successors.size() >= 3 && successors.contains(sink) && successors.contains(v))) {
				WeightedDirectedEdge exitEdge = graph.getDirectedEdge((Vertex)v, (Vertex)sink);
				removedEdges.add(exitEdge);
			}
        }
		
        for (WeightedDirectedEdge e : removedEdges) {
        	graph.removeEdge(e);
        	graph.removeEdge2(e);
        }
        
		return removedEdges;
		
	}
	
	public static Set<WeightedDirectedEdge<IVertex>> removeSelfLoops(WeightedDirectedGraph graph) {
		Set<WeightedDirectedEdge<IVertex>> removedEdges = new HashSet<WeightedDirectedEdge<IVertex>>();
		
		for (Vertex v : graph.getVertices()) {
			WeightedDirectedEdge selfLoop = graph.getDirectedEdge((Vertex)v, (Vertex)v);
			if (selfLoop != null) removedEdges.add(selfLoop);
		}
		
        for (WeightedDirectedEdge e : removedEdges) {
        	graph.removeEdge(e);
        	graph.removeEdge2(e);
        }
        
		return removedEdges;
	}
	
	/**
	 * Note that all nodes of removed edges exist in the graph
	 * @param graph
	 * @param edges
	 */
	public static void reconnectEdges(WeightedDirectedGraph graph, Set<WeightedDirectedEdge<IVertex>> edges) {
		for (WeightedDirectedEdge<IVertex> edge : edges) {
			graph.addEdge(edge.getSource(), edge.getTarget(), edge.getWeight());
		}
	}
	
	public static void resetRemovedEdges(Map<WeightedDirectedEdge<IVertex>,Double> removedEdgeMap) {
		for (WeightedDirectedEdge<IVertex> e : removedEdgeMap.keySet()) {
			e.setWeight(removedEdgeMap.get(e).floatValue());
		}
	}
	
	public static IRPST<IDirectedEdge<IVertex>, IVertex> decompose(WeightedDirectedGraph graph) {
		return new RPST(graph);
	}
	
	
	/**
	 * Return a matrix with two rows.
	 * The first row is the original row in the matrix-based graph 
	 * The second row is the original column in the matrix-based graph 
	 * @param g: the matrix-based graph
	 * @param v: the index of the vertex
	 * @return
	 */
	public static int[][] removeVertex(int[][] g, int v) {
		int[][] removes = new int[g[0].length][g[0].length];
		for (int i=0;i<g[0].length;i++) {
			removes[0][i] = g[v][i];
			removes[1][i] = g[i][v];
			g[v][i] = 0;
			g[i][v] = 0;
		}
		return removes;
	}
	
	/**
	 * Reconnect a vertex v and edges e to a graph
	 * @param g
	 * @param v
	 * @param e: two rows, first is the original row, second is the column
	 */
	public static void reconnect(int[][] g, int v, int[][] e) {
		for (int i=0;i<g[0].length;i++) {
			g[v][i] = e[0][i];
			g[i][v] = e[1][i];
		}
	}
	
	/**
	 * Assume the vertex v on graph g is removed, this method
	 * computes the min-cut on g after removing v based on max flow (Ford-Fulkerson)
	 * The matrix-based graph g must have the first index (0) as
	 * the source and the last index as the sink.
	 * @param bidiAdjacentMatrix: the bi-directional graph (without loops) for min-cut finding
	 * @param v: the vertex in consideration
	 * @return: first element of cutList is the min-cut value,
	 * other elements (if any) are string containing pair of vertices of edges 
	 * delimited by a comma.
	 */
	public static ArrayList<String> computeMinCut(int[][] bidiAdjacentMatrix, int v) {
		ArrayList<String> cutList = null;
		MaxFlow maxFlow = new MaxFlow(bidiAdjacentMatrix[0].length);
		int[][] reBidi = GraphUtils.removeVertex(bidiAdjacentMatrix, v);
		cutList = maxFlow.getMinCut(bidiAdjacentMatrix, 0, bidiAdjacentMatrix[0].length-1);
		GraphUtils.reconnect(bidiAdjacentMatrix, v, reBidi);
		
		return cutList;
	}
	
	public static void printMatrix(double[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
		    for (int j = 0; j < matrix[0].length; j++) {
		        System.out.print(String.format("%1$,.2f",matrix[i][j]) + "   ");
		    }
		    System.out.print("\n");
		}		
	}
	
	public static void printMatrix(int[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
		    for (int j = 0; j < matrix[0].length; j++) {
		        System.out.print(matrix[i][j] + "   ");
		    }
		    System.out.print("\n");
		}		
	}
	
	/**
	 * 
	 * Use GraphEditor object to save a graph to file without showing the editor
	 * The file saved is PNG format
	 * The real graph XML is compressed and saved inside the image file
	 * @param graph
	 * @throws IOException
	 */
	// NOTE: UNCOMMENT THIS METHOD FOR DEBUGGING ONLY
//	public static void saveGraph(String filename, mxGraph graph) throws IOException
//	{
//		GraphEditor editor = new GraphEditor();
//		editor.setVisible(false);
//		editor.createFrame(new EditorMenuBar(editor)).setVisible(false);
//		mxGraphComponent graphComponent = editor.getGraphComponent();
//		//mxGraph graph = graphComponent.getGraph();
//
//		// Creates the image for the PNG file
//		BufferedImage image = mxCellRenderer.createBufferedImage(graph,
//				null, 1, Color.WHITE, graphComponent.isAntiAlias(), null,
//				graphComponent.getCanvas());
//
//		// Creates the URL-encoded XML data
//		mxCodec codec = new mxCodec();
//		String xml = URLEncoder.encode(mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
//		mxPngEncodeParam param = mxPngEncodeParam.getDefaultEncodeParam(image);
//		param.setCompressedText(new String[] { "mxGraphModel", xml });
//
//		// Saves as a PNG file
//		FileOutputStream outputStream = new FileOutputStream(new File(filename));
//		try
//		{
//			mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
//					param);
//
//			if (image != null)
//			{
//				encoder.encode(image);
//				editor.setModified(false);
//				editor.setCurrentFile(new File(filename));
//			}
//			else
//			{
//				JOptionPane.showMessageDialog(graphComponent,
//						mxResources.get("noImageData"));
//			}
//		}
//		finally
//		{
//			outputStream.close();
//		}
//	}
	
	/**
	 * Convert a graph to jGraphX graph
	 */
//	public static mxGraph convertGraph(WeightedDirectedGraph graph) {
//		mxGraph res = new mxGraph();
//		
//		//Mapping from graph vertex to the corresponding mxGraph vertex if they play as a source vertex
//		Map<Vertex, Object> mapVertices = new HashMap<Vertex, Object>(); 
//		
//		//Mapping from graph vertex to the corresponding mxGraph vertex if they play as a target vertex
//		//Map<Vertex, Object> mapTargets = new HashMap<Vertex, Object>();
//		//Map<Vertex, Vertex> mapSourceTarget = new HashMap<Vertex, Vertex>();
//		
//		for (WeightedDirectedEdge<IVertex> e : graph.getEdges()) {
//			Vertex s = e.getSource();
//			Vertex t = e.getTarget();
//			
//			Object sCell, tCell;
//			Object parent = res.getDefaultParent();
//			
//			//Insert source if not exist			
//			if (!mapVertices.containsKey(s)) {
//				sCell = res.insertVertex(parent, s.getId(), s.getLabel(), 20, 20, 20, 20);
//				mapVertices.put(s, sCell);
//			}
//			else {
//				sCell = mapVertices.get(s);
//			}
//			
//			//Insert target if not exist
//			if (!mapVertices.containsKey(t)) {
//				tCell = res.insertVertex(parent, t.getId(), t.getLabel(), 20, 20, 20, 20);
//				mapVertices.put(t, tCell);
//			}
//			else {
//				tCell = mapVertices.get(t);
//			}			
//			
//			
//			//Insert edge: the edge weight is stored as the value of the edge
//			mxCell edge = (mxCell) res.insertEdge(parent, e.getId(), e.getWeight(), sCell, tCell);
//			edge.setAttribute("label", e.getLabel());
//			edge.setAttribute("description", e.getDescription());
//			edge.setAttribute("name", e.getName());
//			edge.setAttribute("weight", String.valueOf(e.getWeight()));
//		}
//		
//		return res;
//	}
	
	/**
	 * Check a directed graph to see weather it is connected as if it is an undirected graph
	 * An adjacency matrix is used as a representation for the diretec graph (not symmetrix)
	 * But it will be checked as if it is undirected.
	 * This is done by traversing the graph with BFS, if all nodes can be 
	 * visited then the graph is connected, else not connected.
	 * @param matrix: adjacency matrix
	 * @param source: the index of the source node in the matrix
	 * @return true/false
	 */
	public static boolean isUndirectedConnected (int adjacency_matrix[][], int source, int removed)
    {
		Queue<Integer> queue = new LinkedList<Integer>();
        int maxNodeIndex = adjacency_matrix[source].length - 1;
        int[] visited = new int[maxNodeIndex + 1];
        int i, element;
        visited[source] = 1;
        queue.add(source);
        
        while (!queue.isEmpty())
        {
            element = queue.remove();
            i = 0;
            while (i <= maxNodeIndex)
            {
                if ((adjacency_matrix[element][i] >=1 || adjacency_matrix[i][element] >= 1) && visited[i] == 0)
                {
                    queue.add(i);
                    visited[i] = 1;
                }
                i++;
            }
        }  
        
        boolean connected = true; 
        for (int vertex = 0; vertex <= maxNodeIndex; vertex++)
        {
            if (vertex != removed && visited[vertex] == 0) {
                connected = false;
                break;
            }
        }
 
        return connected;
    }
	
	/**
	 * Check if the cut-set will divide the graph into two parts, one of which contain
	 * the source or the sink only.
	 * @param v
	 * @param cutSet
	 * @param g
	 * @return
	 */
	public static boolean isSourceOnlyAfterCut(IVertex v, Set<WeightedDirectedEdge<IVertex>> cutSet, WeightedDirectedGraph g) {
		Set<IVertex> cutSetNodes = new HashSet<IVertex>();
		//Get all target vertices from the cutset
		cutSetNodes.add(v);
		for (WeightedDirectedEdge<IVertex> e : cutSet) {
			if (e.getTarget() != g.getSource()) cutSetNodes.add(e.getTarget());
			if (e.getSource() != g.getSource()) cutSetNodes.add(e.getSource());
		}
		//Get all connected vertices of the source
		Set<IVertex> sourceConnectedNodes = new HashSet<IVertex>(g.getDirectSuccessors((Vertex)g.getSource()));
		sourceConnectedNodes.addAll(g.getDirectPredecessors((Vertex)g.getSource()));
		
		//Return true if they are the same
		return (cutSetNodes.containsAll(sourceConnectedNodes) && sourceConnectedNodes.containsAll(cutSetNodes));
	}
	

	public static boolean isSinkOnlyAfterCut(IVertex v, Set<WeightedDirectedEdge<IVertex>> cutSet, WeightedDirectedGraph g) {
		Set<IVertex> cutSetNodes = new HashSet<IVertex>();
		
		//Get all sourc vertices from the cutset
		cutSetNodes.add(v);
		for (WeightedDirectedEdge<IVertex> e : cutSet) {
			if (e.getTarget() != g.getSink()) cutSetNodes.add(e.getTarget());
			if (e.getSource() != g.getSink()) cutSetNodes.add(e.getSource());
		}
		//Get all target vertices of the sink
		Set<IVertex> sinkConnectedNodes = new HashSet<IVertex>(g.getDirectPredecessors((Vertex)g.getSink()));
		sinkConnectedNodes.addAll(g.getDirectSuccessors((Vertex)g.getSink()));
		
		//Return true if they are the same
		return (cutSetNodes.containsAll(sinkConnectedNodes) && sinkConnectedNodes.containsAll(cutSetNodes));
	}
	
	
	public static Set<WeightedDirectedEdge<IVertex>> removeExitEdges(WeightedDirectedGraph g, IVertex lastExitVertex) {
		Set<WeightedDirectedEdge<IVertex>> removedExitEdges = new HashSet<WeightedDirectedEdge<IVertex>>();
		for (Vertex v : g.getDirectPredecessors((Vertex)g.getSink())) {
			if (v != lastExitVertex) {
				removedExitEdges.addAll(g.getEdgesWithSourceAndTarget(v, (Vertex)g.getSink()));
			}
		}
		g.removeEdges(removedExitEdges);
		return removedExitEdges;
	}
	
	public static Set<WeightedDirectedEdge<IVertex>> removeStartEdges(WeightedDirectedGraph g, IVertex firstStartVertex) {
		Set<WeightedDirectedEdge<IVertex>> removedExitEdges = new HashSet<WeightedDirectedEdge<IVertex>>();
		for (Vertex v : g.getDirectSuccessors((Vertex)g.getSource())) {
			if (v != firstStartVertex) {
				removedExitEdges.addAll(g.getEdgesWithSourceAndTarget((Vertex)g.getSource(), v));
			}
		}
		g.removeEdges(removedExitEdges);
		return removedExitEdges;
	}
	
	/**
	 * Different from removeExitEdges, that only set the weight of the edge to zero 
	 * @param g
	 * @param lastExitVertex
	 * @return
	 */
	public static Map<WeightedDirectedEdge<IVertex>,Double> removeExitEdgesFake(WeightedDirectedGraph g, IVertex lastExitVertex) {
		Set<WeightedDirectedEdge<IVertex>> removedExitEdges = new HashSet<WeightedDirectedEdge<IVertex>>();
		for (Vertex v : g.getDirectPredecessors((Vertex)g.getSink())) {
			if (v != lastExitVertex) {
				removedExitEdges.addAll(g.getEdgesWithSourceAndTarget(v, (Vertex)g.getSink()));
			}
		}
		
		Map<WeightedDirectedEdge<IVertex>,Double> result = new HashMap<WeightedDirectedEdge<IVertex>, Double>(); 
		for (WeightedDirectedEdge<IVertex> e : removedExitEdges) {
			result.put(e, Double.valueOf(e.getWeight()));
			e.setWeight(1);
		}
		return result;
	}
	
	public static double computeControlFlowGoodness(WeightedDirectedGraph g, List<Set<IVertex>> phases) {
//		int totalEdgeNum = g.getEdges().size();
		double totalEdgeNum = g.getTotalWeight();
		//Compute edges between phases
		double edgeNumBetweenPhases = 0.0;
		for (int i=0;i<phases.size();i++) {
			for (int j=i+1;j<phases.size();j++) {
				//edgeNumBetweenPhases += GraphUtils.getEdges(g, phases.get(i), phases.get(j)).size();
				edgeNumBetweenPhases += GraphUtils.getConnectionWeight(g, phases.get(i), phases.get(j));
			}
		}
		
		return 1.0*(edgeNumBetweenPhases*(totalEdgeNum - edgeNumBetweenPhases))/(totalEdgeNum*totalEdgeNum);
	}
	
	/*
	 * Get all edges connecting between set1 and set2
	 * Note that all edges in both directions, from set1 to set2 and vice versa.
	 * If set1 = set2, return all edges with source and target within set1.
	 */
	public static Set<WeightedDirectedEdge<IVertex>> getEdges(WeightedDirectedGraph g, Set<IVertex> set1, Set<IVertex> set2) {
		Set<WeightedDirectedEdge<IVertex>> result = new HashSet<WeightedDirectedEdge<IVertex>>();
		for (WeightedDirectedEdge<IVertex> e : g.getEdges()) {
			if ((set1.contains(e.getSource()) && set2.contains(e.getTarget())) || 
				(set1.contains(e.getTarget()) && set2.contains(e.getSource()))) {
				result.add(e);
			}
		}
		return result;
	}
	
	/**
	 * Get directed edge from set1 to set2
	 * @param g
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static Set<WeightedDirectedEdge<IVertex>> getDirectedEdges(WeightedDirectedGraph g, Set<IVertex> set1, Set<IVertex> set2) {
		Set<WeightedDirectedEdge<IVertex>> result = new HashSet<WeightedDirectedEdge<IVertex>>();
		for (WeightedDirectedEdge<IVertex> e : g.getEdges()) {
			if (set1.contains(e.getSource()) && set2.contains(e.getTarget())) {
				result.add(e);
			}
		}
		return result;
	}
	
	public static double getConnectionSize(WeightedDirectedGraph g, Set<IVertex> set1, Set<IVertex> set2) {
		return GraphUtils.getEdges(g, set1, set2).size();
	}
	
	/**
	 * Return the total weight on all edges between two set of vertices
	 * @param g
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static double getConnectionWeight(WeightedDirectedGraph g, Set<IVertex> set1, Set<IVertex> set2) {
		Set<WeightedDirectedEdge<IVertex>> edges = GraphUtils.getEdges(g, set1, set2);
		double weight = 0.0;
		for (WeightedDirectedEdge<IVertex> e : edges) {
			weight += e.getWeight();
		}
		return weight;
	}
	
	public static double getDirectedConnectionWeight(WeightedDirectedGraph g, Set<IVertex> set1, Set<IVertex> set2) {
		Set<WeightedDirectedEdge<IVertex>> edges = GraphUtils.getDirectedEdges(g, set1, set2);
		double weight = 0.0;
		for (WeightedDirectedEdge<IVertex> e : edges) {
			weight += e.getWeight();
		}
		return weight;
	}
	
	public static double getDirectedConnectionWeightFromSource (WeightedDirectedGraph g, IVertex source, Set<IVertex> stage) {
		double weight = 0.0;
		for (IVertex v : stage) {
			for (WeightedDirectedEdge<IVertex> e : g.getEdgesWithSourceAndTarget((Vertex)source, (Vertex)v)) {
				weight += e.getWeight();
			}
		}
		return weight;
	}
	
	public static double getDirectedConnectionWeightToSink (WeightedDirectedGraph g, Set<IVertex> stage, IVertex sink) {
		double weight = 0.0;
		for (IVertex v : stage) {
			for (WeightedDirectedEdge<IVertex> e : g.getEdgesWithSourceAndTarget((Vertex)v, (Vertex)sink)) {
				weight += e.getWeight();
			}
		}
		return weight;
	}
	
	public static double getDirectedConnectionNoWeight(WeightedDirectedGraph g, Set<IVertex> set1, Set<IVertex> set2) {
		Set<WeightedDirectedEdge<IVertex>> edges = GraphUtils.getDirectedEdges(g, set1, set2);
		return edges.size();
	}
	
	/**
	 * Remember: self-loops have been removed in g
	 * @param g
	 * @param phases
	 * @return
	 */
	public static double computeModularity(WeightedDirectedGraph g, List<Set<IVertex>> phases) {
		// e[i][j] is the number of edges between phase i and phase j
		// e[i][i] is the number of edges within phase i
		double [][] e = new double[phases.size()][phases.size()];
		double graphTotalWeight = g.getTotalWeight();
		for (int i=0;i<e.length;i++) {
			for (int j=0;j<e.length;j++) {
				e[i][j] = 1.0*GraphUtils.getConnectionWeight(g, phases.get(i), phases.get(j));
			}
		}
		GraphUtils.printMatrix(e);
		
		// Compute modularity
		double mod = 0;
		for (int i=0;i<e.length;i++) { //for every stage
			double a_i = 0;
			for (int j=0;j<e.length;j++) {
//				if (j==i) continue;
				a_i += e[j][i];
			}
			mod += 1.0*(e[i][i]/graphTotalWeight - (a_i*a_i)/(graphTotalWeight*graphTotalWeight)); 
		}
		
		return mod;
	}
	
	/**
	 * Compute modularity by transforming an original graph into a new graph
     * Every transition node is divided into two nodes
     * All edges connecting from/to the preceding stages are connected to the first node
     * All edges connecting from/to the succeeding stages are connected to the second node
     * The first node is associated with the preceding stage
     * The second node is associated with the succeeding stage
	 * @param oriGraph: the original graph
	 * @param phases: list of sets, each is a set of phase nodes, including transition nodes
	 * @param transitionNodes: the number of transition nodes is the number of phase minus 1 
	 * not including the source and the sink of the graph. 
	 * @return modularity degree
	 */
	public static double computeModularitySplitTransitionNode(WeightedDirectedGraph oriGraph, List<Set<IVertex>> phases, List<IVertex> transitionNodes) {
		List<Set<IVertex>> newPhases = new ArrayList<Set<IVertex>>();
		WeightedDirectedGraph newGraph = new WeightedDirectedGraph();
		
		// Copy the phases
		for (Set<IVertex> phase : phases) {
			Set<IVertex> newPhase = new HashSet<IVertex>();
			newPhase.addAll(phase);
			newPhases.add(newPhase);
		}
		
		// Add all vertices from the original graph
		for (Vertex v : oriGraph.getVertices()) {
				newGraph.addVertex(v);
		}
		
		// Add edges from the original graph
		for (WeightedDirectedEdge<IVertex> e : oriGraph.getEdges()) {
			if (newGraph.getVertices().contains(e.getSource()) && newGraph.getVertices().contains(e.getTarget())) {
				newGraph.addEdge(e.getSource(), e.getTarget(), e.getWeight());
			}
		}
		
		// Split the transition nodes into two nodes
		// Add new nodes to new phases and the new graph 
		// Add edges of the transition nodes to the new graph 
		for (int i=0;i<transitionNodes.size();i++) {
			Vertex transitNode = (Vertex)transitionNodes.get(i);
			
			//Add two new nodes and connect them by an edge with a weight of 0. 
			Vertex2 newNode1 = new Vertex2(transitNode.getName()+".1");
			newPhases.get(i).add(newNode1);
			newGraph.addVertex(newNode1);
			
			Vertex2 newNode2 = new Vertex2(transitNode.getName()+".2");
			newPhases.get(i+1).add(newNode2);
			newGraph.addVertex(newNode2);
			
			newGraph.addEdge(newNode1, newNode2, 0);
			
			//Transfer edges to/from the transition node to the two new nodes
			for (int j=0;j<phases.size();j++) {
				Vertex2 newNode;
				if (j <= i) { // preceding phases of transition node i, including containing phase
					newNode = newNode1;					
				}
				else {
					newNode = newNode2;
				}
				for (WeightedDirectedEdge<IVertex> e : newGraph.getEdgesWithSource(transitNode)) {
					if (newPhases.get(j).contains(e.getTarget())) {
						newGraph.addEdge(newNode, e.getTarget(), e.getWeight());
					}
				}
				for (WeightedDirectedEdge<IVertex> e : newGraph.getEdgesWithTarget(transitNode)) {
					if (newPhases.get(j).contains(e.getSource())) {
						newGraph.addEdge(e.getSource(), newNode, e.getWeight());
					}
				}
			}
			
			//Remove transition nodes and all its edges
			Set<WeightedDirectedEdge<IVertex>> transitEdges = new HashSet<WeightedDirectedEdge<IVertex>>();
			transitEdges.addAll(newGraph.getEdgesWithSource(transitNode));
			transitEdges.addAll(newGraph.getEdgesWithTarget(transitNode));
			newGraph.removeEdges(transitEdges);
			newGraph.removeVertex(transitNode);
			newPhases.get(i).remove(transitNode); 
		}
		
//		System.out.println("Input phases: " + phases.toString());
//		System.out.println("Transition nodes: " + transitionNodes.toString());
//		System.out.println("Total weight of the input graph=" + oriGraph.getTotalWeight());
		
		return GraphUtils.computeDirectedWeightedModularity(newGraph, newPhases);
		
	}
	
	public static double computeDirectedWeightedModularity(WeightedDirectedGraph g, List<Set<IVertex>> phases) {
		// e[i][j] is the number of edges between phase i and phase j
		// e[i][i] is the number of edges within phase i
		double [][] e = new double[phases.size()][phases.size()];
		double graphTotalWeight = g.getTotalWeight();
		//double graphTotalWeight = g.getEdges().size();
		for (int i=0;i<e.length;i++) {
			for (int j=0;j<e.length;j++) {
				e[i][j] = 1.0*GraphUtils.getDirectedConnectionWeight(g, phases.get(i), phases.get(j));
//				System.out.println("e["+i+"]"+"["+j+"]=" + e[i][j]);
			}
		}
		//GraphUtils.printMatrix(e);
		
		// Compute modularity
		double mod = 0;
		for (int i=0;i<e.length;i++) {
			double a_i = 0;
			for (int j=0;j<e.length;j++) {
//				if (j==i) continue;
				a_i += e[j][i];
			}
			mod += 1.0*(e[i][i]/graphTotalWeight  - (a_i*a_i)/(graphTotalWeight*graphTotalWeight));
		}
		
//		System.out.println("TotalWeight=" + graphTotalWeight);
//		System.out.println("Modularity=" + mod);
		
		return mod;
	}
	
	
}