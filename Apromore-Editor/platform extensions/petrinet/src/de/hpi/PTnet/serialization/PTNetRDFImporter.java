package de.hpi.PTnet.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetFactory;
import de.hpi.petrinet.*;

/**
 * main method: loadPTNet() 
 * 
 * @author matthias.kunze
 * @comment most of it copied from de.hpi.interactionnet.serialization.InteractionNetRDFImporter
 *
 */
public class PTNetRDFImporter {

	protected Document doc;
	protected PTNetFactory factory;
	
	protected class ImportContext {
		private PTNet net;
		private Map<String,de.hpi.petrinet.Node> objects; // key = resource id, value = diagram object
		private Map<String,de.hpi.petrinet.Node> connections; // key = to resource id, value = from node
		
		// generated getters and setters
		protected void setNet(PTNet net) {
			this.net = net;
		}
		public PTNet getNet() {
			return net;
		}
		protected void setObjects(Map<String,de.hpi.petrinet.Node> objects) {
			this.objects = objects;
		}
		public Map<String,de.hpi.petrinet.Node> getObjects() {
			return objects;
		}
		protected void setConnections(Map<String,de.hpi.petrinet.Node> connections) {
			this.connections = connections;
		}
		public Map<String,de.hpi.petrinet.Node> getConnections() {
			return connections;
		}
	}
	
	public PTNetRDFImporter(Document doc) {
		this.doc = doc;
	}
	
	@SuppressWarnings("unchecked")
	public PTNet loadPTNet() {
		Node root = getRootNode(doc);
		if (root == null) return null;
		
		factory = new PTNetFactory();
		
		ImportContext c = new ImportContext();
		c.setNet(factory.createPetriNet());
//		Map map = new HashMap();		
		c.setObjects(new HashMap()); // key = resource id, value = node
		c.setConnections(new HashMap()); // key = to resource id, value = from node
		
		List<Node> edges = new ArrayList();
		
		// handle nodes
		for (Node node=root.getFirstChild(); node != null; node=node.getNextSibling()) {
			if (node instanceof Text) continue;
			
			String type = getType(node);
			if (type == null) continue;
			
			if (type.equals("Place")) {
				addPlace(node, c);
			} else if (type.equals("Transition")) {
				addTransition(node, c);
//			} else if (type.equals("ControlledSilentTransition")) {
//				addControlledSilentTransition(node, c);
			} else if (type.equals("VerticalEmptyTransition")) {
				addSilentTransition(node, c);
			} else if (type.equals("Arc")) {
				edges.add(node);
			}
		}
		
		// handle edges (except undirected associations)
		for (Node node: edges) {
			addArc(node, c);
		}
		
		return c.getNet();
	}
	
	protected void addPlace(Node node, ImportContext c) {
		Place p = factory.createPlace();
		c.getNet().getPlaces().add(p);
		c.getObjects().put(getResourceId(node), p);
		
		for (Node n=node.getFirstChild(); n != null; n=n.getNextSibling()) {
			if (n instanceof Text) continue;
			String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':')+1);
			
			if (attribute.equals("id")) {
				String id = getContent(n);
				if (id != null)
					p.setId(id);
			} else if (attribute.equals("title")) {
				p.setLabel(getContent(n));
			} else if (attribute.equals("marked")) {
				if ("true".equals(getContent(n)))
					c.getNet().getInitialMarking().addToken(p);
			} else if (attribute.equals("numberoftokens")) {
				//FIX for empty nodes
				String nbr=getContent(n);
				if(nbr!=null){
					int number_of_tokens = Integer.parseInt(nbr);
					for (int i=0; i < number_of_tokens; i++) {
						c.getNet().getInitialMarking().addToken(p);
					}}
			} else if (attribute.equals("outgoing")) {
				c.getConnections().put(getResourceId(getAttributeValue(n, "rdf:resource")), p);
			} 
		}
		if (p.getId() == null)
			p.setId(getResourceId(node));
	}

	protected void addTransition(Node node, ImportContext c) {
		LabeledTransition t = factory.createLabeledTransition();
		c.getNet().getTransitions().add(t);
		c.getObjects().put(getResourceId(node), t);
		
		for (Node n=node.getFirstChild(); n != null; n=n.getNextSibling()) {
			if (n instanceof Text) continue;
			String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':')+1);

			if (attribute.equals("title")) {
				t.setLabel(getContent(n));
			} else if (attribute.equals("outgoing")) {
				c.getConnections().put(getResourceId(getAttributeValue(n, "rdf:resource")), t);
			}else if (attribute.equals("communicationchannel")){
				String channel = getContent(n);
				if (channel != null)
					t.setCommunicationChannel(channel);
			} else if (attribute.equals("communicationtype")){
				String type = getContent(n);
				if (type != null){
					if(type.equals("Default"))
						t.setCommunicationType(CommunicationType.DEFAULT);
					else if (type.equals("Synchron")) {
						t.setCommunicationType(CommunicationType.SYNCHRON);
					}else if (type.equals("Asynchronous Input")) {
						t.setCommunicationType(CommunicationType.ASYNCH_INPUT);
					}else if (type.equals("Asynchronous Output")) {
						t.setCommunicationType(CommunicationType.ASYNCH_OUTPUT);
					}
				}
			}
		}
		if (t.getId() == null)
			t.setId(getResourceId(node));
	}

	protected void addSilentTransition(Node node, ImportContext c) {
		SilentTransition t = factory.createSilentTransition();
		c.getNet().getTransitions().add(t);
		c.getObjects().put(getResourceId(node), t);
		
		for (Node n=node.getFirstChild(); n != null; n=n.getNextSibling()) {
			if (n instanceof Text) continue;
			String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':')+1);
			
			if (attribute.equals("outgoing")) {
				c.getConnections().put(getResourceId(getAttributeValue(n, "rdf:resource")), t);
			}
		}
		if (t.getId() == null)
			t.setId(getResourceId(node));
	}

	protected void addArc(Node node, ImportContext c) {
		FlowRelationship arc = factory.createFlowRelationship();
		c.getNet().getFlowRelationships().add(arc);
		setConnections(arc, node, c);
		
		for (Node n=node.getFirstChild(); n != null; n=n.getNextSibling()) {
			if (n instanceof Text) continue;
			String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':')+1);

			if (attribute.equals("label")) {
				arc.setLabel(getContent(n));
			}
		}
	}

	protected void setConnections(FlowRelationship arc, Node node, ImportContext c) {
		arc.setSource(c.connections.get(getResourceId(node)));
		
		for (Node n=node.getFirstChild(); n != null; n=n.getNextSibling()) {
			if (n instanceof Text) continue;
			String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':')+1);
			
			if (attribute.equals("outgoing")) {
				arc.setTarget(c.getObjects().get(getResourceId(getAttributeValue(n, "rdf:resource"))));
			}
		}
	}

	protected String getContent(Node node) {
		if (node != null && node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		return null;
	}
	
	private String getAttributeValue(Node node, String attribute) {
		Node item = node.getAttributes().getNamedItem(attribute);
		if (item != null)
			return item.getNodeValue();
		else
			return null;
	}

	protected String getType(Node node) {
		String type = getContent(getChild(node, "type"));
		if (type != null)
			return type.substring(type.indexOf('#')+1);
		else
			return null;
	}
	
	protected String getResourceId(Node node) {
		Node item = node.getAttributes().getNamedItem("rdf:about");
		if (item != null)
			return getResourceId(item.getNodeValue());
		else
			return null;
	}
	
	protected String getResourceId(String id) {
		return id.substring(id.indexOf('#')+1);
	}

	protected Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().indexOf(name) >= 0) 
				return node;
		return null;
	}

	protected Node getRootNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("rdf:RDF"))
			return null;
		return node;
	}

}
