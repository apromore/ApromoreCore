package au.ltl.domain;

import java.util.HashMap;

import hub.top.petrinet.Node;
import hub.top.petrinet.PetriNet;

public class Subnet {
    PetriNet net ;
    HashMap<Node, Node> map = new HashMap<>();
	public PetriNet getNet() {
		return net;
	}
	public HashMap<Node, Node> getMap() {
		return map;
	}
	public Subnet(PetriNet net, HashMap<Node, Node> map) {
		super();
		this.net = net;
		this.map = map;
	}
    
    
}
