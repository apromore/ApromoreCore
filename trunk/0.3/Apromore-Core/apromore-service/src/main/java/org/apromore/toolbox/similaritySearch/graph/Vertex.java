package org.apromore.toolbox.similaritySearch.graph;

import org.apromore.toolbox.similaritySearch.common.Settings;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


public class Vertex {

	private Graphics graphics;
	public HashSet<String> dominance;
	private HashMap<String, String> annotationMap = new HashMap<String, String>();
	public HashSet<VertexObjectRef> objectRefs = new HashSet<VertexObjectRef>();
	public HashSet<VertexResourceRef> resourceRefs = new HashSet<VertexResourceRef>();
	
	public HashMap<String, String> getAnnotationMap() {
		return annotationMap;
	}
	
	public void addAnnotationsForGw(HashSet<String> edgeLabels) {
		for (String model : edgeLabels) {
			if (!annotationMap.containsKey(model)) {
				annotationMap.put(model, "xor");
			}
		}
	}
	
	public void mergeAnnotationsForGw(Vertex v) {
		if (!v.initialGW) {
			return;
		}
		annotationMap = v.getAnnotationMap();
//		HashMap<String, String> annotationMapGw = v.getAnnotationMap();
//		for (Entry<String, String> annotation : annotationMapGw.entrySet()) {
//			if (!annotationMap.containsKey(annotation.getKey())) {
//				annotationMap.put(annotation.getKey(), annotation.getValue());
//			} else {
//				// merge annotations - they are not the same type 
//				if (!annotation.getValue().equals(annotationMap.get(annotation.getValue())))  {
//					annotationMap.put(annotation.getKey(), "or");
//				}
//			}
//		}
	}
	
	public void addAnnotations(HashMap<String, String> annotationMap1) {
		annotationMap.putAll(annotationMap1);
	}	
	
	public Graphics getGraphics() {
		return graphics;
	}

	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}

	public enum Type{
		node,
		function,
		gateway,
		event,
		state
	}
	
	public enum GWType {
		or,
		xor,
		and,
		place // petri nets
	}

	
	private String ID;
	// node type
	private Type vertexType;
	// label
	private String label;
	private GWType gwType;
	private LinkedList<Vertex> childNodes = new LinkedList<Vertex>();
	private LinkedList<Vertex> parentNodes = new LinkedList<Vertex>();
	boolean isProcessed = false;
	boolean processedGW = false;
//	LinkedList<Node> toAddConfigurable = new LinkedList<Node>();
	
//	LinkedList<Edge> toAddEdges = new LinkedList<Edge>();
	Vertex prevConfVertex = null;
	Edge toAddEdge = null;
	
	public boolean sourceBefore = false;
	public boolean sinkBefore = false;
	Vertex closeGW;
	private boolean isConfigurable = false;
	
	boolean labelContributed = false;
	
	boolean initialGW = false;
	
	
	public boolean isInitialGW() {
		return initialGW;
	}

	public void setInitialGW() {
		initialGW = true;
	}

	public static Vertex copyVertex(Vertex v) {
		Vertex toReturn = new Vertex(v.getType(), v.getLabel(), v.getID());
		toReturn.gwType = v.gwType;
		toReturn.childNodes = Graph.copyVertices(v.childNodes);
		toReturn.parentNodes = Graph.copyVertices(v.parentNodes);
		toReturn.isConfigurable = v.isConfigurable;
		
		return toReturn;
	}
	
	public Vertex copyVertex() {
		Vertex toReturn = new Vertex(getType(), getLabel(), getID());
		toReturn.gwType = gwType;
		toReturn.isConfigurable = isConfigurable;
		
		return toReturn;
	}
	
	public boolean isAddedGW() {
		return !initialGW;
	}

	public void setAddedGW(boolean isAddedGW) {
		this.initialGW = !isAddedGW;
	}

	public boolean isConfigurable() {
		return isConfigurable;
	}

	public void setConfigurable(boolean isConfigurable) {
		this.isConfigurable = isConfigurable;
//		if (isConfigurable) {
//			initialGW = false;
//		}
	}

	public Vertex(Type type, String label, String ID){
		vertexType = type;
		this.label = label;
		this.ID = ID;
	}
	
	public Vertex(GWType gwType, String ID) {
		vertexType = Type.gateway;
		this.gwType = gwType;
		this.ID = ID;
	}

	
	public void addChild(Vertex child){
		if (!childNodes.contains(child)) 
			childNodes.add(child);
	}
	
	public LinkedList<Vertex> getChildren () {
		return childNodes;
	}
	
	public void removeChildren () {
		childNodes = new LinkedList<Vertex>();
	}
	
	public void removeChild (String id) {
		Vertex toRemove = null;
		for (Vertex v : childNodes) {
			if (v.getID().equals(id)) {
				toRemove = v;
				break;
			}
		}
		if (toRemove != null) {
			childNodes.remove(toRemove);	
		}
	}


	public void removeParent (String id) {
		Vertex toRemove = null;
		for (Vertex v : parentNodes) {
			if (v.getID().equals(id)) {
				toRemove = v;
				break;
			}
		}
		if (toRemove != null) {
			parentNodes.remove(toRemove);	
		}
	}

	public void removeParents () {
		parentNodes = new LinkedList<Vertex>();
	}

	public static void removeParents (LinkedList<Vertex> list) {
		for (Vertex v : list) {
			v.removeParents();
		}
	}

	public static void removeChildren (LinkedList<Vertex> list) {
		for (Vertex v : list) {
			v.removeChildren();
		}
	}

	public LinkedList<Vertex> getChildrenList () {
		LinkedList<Vertex> result = new LinkedList<Vertex>();
				
		for (Vertex v : childNodes) {
			if(v.getType().equals(Type.function)
					|| (Settings.considerEvents && v.getType().equals(Type.event))
					|| (Settings.considerGateways && v.getType().equals(Type.gateway))) {
				result.add(v);
			}
		}
		
		return result;
	}

	public LinkedList<Vertex> getChildrenListAll () {
		LinkedList<Vertex> result = new LinkedList<Vertex>();
				
		for (Vertex v : childNodes) {
			result.add(v);
		}
		
		return result;
	}

	
	public LinkedList<Vertex> getParents () {
		return parentNodes;
	}
	
	public LinkedList<Vertex> getParentsList () {
		LinkedList<Vertex> result = new LinkedList<Vertex>();
		
		if (parentNodes == null) {
			return result;
		}

		
		for (Vertex v : parentNodes) {
			if(v.getType().equals(Type.function) || (Settings.considerEvents && v.getType().equals(Type.event))
					|| (Settings.considerGateways && v.getType().equals(Type.gateway))) {
				result.add(v);
			}
		}
		
		return result;
	}
	
	public LinkedList<Vertex> getParentsListAll () {
		LinkedList<Vertex> result = new LinkedList<Vertex>();
		
		if (parentNodes == null) {
			return result;
		}
		
		for (Vertex v : parentNodes) {
			result.add(v);
		}
		
		return result;
	}

	
	public void addParent(Vertex parent){
		if (parentNodes == null) {
			parentNodes = new LinkedList<Vertex>();
		}
		if (!parentNodes.contains(parent))
			parentNodes.add(parent);
	}
	
	public boolean equals(Vertex v2) {
		if (this.ID.equals(v2.getID())
				){
			return true;
		}
		return false;
		
	}
	
	public Vertex(String gwTypeString, String ID) {
		vertexType = Type.gateway;
		if (gwTypeString.equalsIgnoreCase("xor")) {
			gwType = GWType.xor;
		}
		else if (gwTypeString.equalsIgnoreCase("or")) {
			gwType = GWType.or;
		} 
		else if (gwTypeString.equalsIgnoreCase("and")) {
			gwType = GWType.and;
		} 
		// for petri nets
		else if (gwTypeString.equalsIgnoreCase("place")) {
			gwType = GWType.place;
		} 
		
		this.ID = ID;
	}


	public GWType getGWType() {
		return gwType;
	}
	
	public void setGWType(GWType gwType) {
		this.gwType = gwType;
	}

	
	public void setID(String id) {
		ID = id;
	}

	public String getID() {
		return ID;
	}
	
	
	public void setVertexType(Type vertexType) {
		this.vertexType = vertexType;
	}

	public void setVertexGWType(GWType gwType) {
		this.gwType = gwType;
	}

	
	public Type getType(){
		return vertexType;
	}
	
	public String getLabel(){
		return label;
	}
	
	public LinkedList<Vertex> getAllNonGWParents() {
		LinkedList<Vertex> toReturn = new LinkedList<Vertex>();
		LinkedList<Vertex> toProcesGWs = new LinkedList<Vertex>();
		
		Vertex currentVertex = this;
		while (true) {
			for (Vertex p : currentVertex.getParents()) {
				if (p.getType().equals(Type.gateway)) {
					toProcesGWs.add(p);
				}
				// we have function or event
				else {
					toReturn.add(p);
				}
			}
			if (toProcesGWs.size() > 0) {
				currentVertex = toProcesGWs.removeFirst();
			}
			else {
				break;
			}
		}
		
		return toReturn;
	}

	public LinkedList<Vertex> getAllNonGWChildren() {
		LinkedList<Vertex> toReturn = new LinkedList<Vertex>();
		LinkedList<Vertex> toProcesGWs = new LinkedList<Vertex>();
		
		Vertex currentVertex = this;
		while (true) {
			for (Vertex p : currentVertex.getChildren()) {
				if (p.getType().equals(Type.gateway)) {
					toProcesGWs.add(p);
				}
				// we have function or event
				else {
					toReturn.add(p);
				}
			}
			if (toProcesGWs.size() > 0) {
				currentVertex = toProcesGWs.removeFirst();
			}
			else {
				break;
			}
		}
		return toReturn;
	}

	public int compareTo(Vertex another) {
		return this.getID().compareTo(another.getID());
	}
	
	public String toString() {
		if (getType().equals(Type.event)) {
			return "Event("+getID() +", "+getLabel()+")";
		} else if (getType().equals(Type.function)) {
			return "Function("+getID() +", "+getLabel()+")";
		} else if (getType().equals(Type.gateway)) {
			return "CpfGateway("+getID() +", "+getGWType()/*+ "P("+printList(parentNodes)+"), CH("+printList(childNodes)+"))"*/;
		}
		return "Node("+getID() +", "+getLabel()+")";
	}
}
