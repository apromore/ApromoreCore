package ee.ut.bpmn.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ee.ut.bpmn.BPMNProcess;
import hub.top.petrinet.Node;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

public class Petrifier <T> {
	private BPMNProcess<T> model;
	private Set<Integer> labeledElements;
	private  Map<Integer, Node> map;
	
	public Petrifier(BPMNProcess<T> model) {
		this.model = model;
		this.labeledElements = model.getVisibleNodes();
        this.map = new HashMap<Integer, Node>();
	}
	
    private Node getNode(Integer node, PetriNet net, Map<Integer, Node> map) {
            Node res = map.get(node);
            if (res==null) {
                    if (model.isXORGateway(node) || model.isORGateway(node))
                            res = net.addPlace(model.getName(node));
                    else
                            res = net.addTransition(model.getName(node));                        
                    map.put(node, res);
            }
            return res;
    }

    public PetriNet petrify(Integer _entry, Integer _exit) {
            Node entry = null, exit = null;
            PetriNet net = new PetriNet();

            for (Entry<Integer,Integer> edge : model.getEdges()) {
                    Integer src = edge.getKey();
                    Integer tgt = edge.getValue();
                    
                    if (labeledElements.contains(src) || model.isANDGateway(src)) {
                            if (labeledElements.contains(tgt) || model.isANDGateway(tgt)) {
                                    Transition psrc = (Transition)getNode(src, net, map);
                                    Transition ptgt = (Transition)getNode(tgt, net, map);
                                    Place p = net.addPlace(psrc.getName() + "_" + ptgt.getName());
                                    net.addArc(psrc, p);
                                    net.addArc(p, ptgt);
                            } else if (model.isXORGateway(tgt)) {
                                    Transition psrc = (Transition)getNode(src, net, map);                                   
                                    Place ptgt = (Place)getNode(tgt, net, map);
                                    net.addArc(psrc, ptgt);
                            }
                    } else if (model.isXORGateway(src)) {
                            if (labeledElements.contains(tgt) || model.isANDGateway(tgt)) {
                                    Place psrc = (Place)getNode(src, net, map);
                                    Transition ptgt = (Transition)getNode(tgt, net, map);

                                    Place pintp = net.addPlace(psrc.getName() + "_p_" + ptgt.getName());
                                    Transition pintt = net.addTransition(psrc.getName() + "_t_" + ptgt.getName());
                                    net.addArc(psrc, pintt);
                                    net.addArc(pintt, pintp);
                                    net.addArc(pintp, ptgt);
                            } else if (model.isXORGateway(tgt)) {
                                    Place psrc = (Place)getNode(src, net, map);
                                    Place ptgt = (Place)getNode(tgt, net, map);
                                    Transition inter = net.addTransition(psrc.getName() + "_" + ptgt.getName());
                                    net.addArc(psrc, inter);
                                    net.addArc(inter, ptgt);
                            }
                    }
            }

            // fix entry/exit
            entry = getNode(_entry, net, map);
            exit = getNode(_exit, net, map);

            if (entry instanceof Transition) {
                    Place p = net.addPlace("_entry_");
                    net.addArc(p, (Transition)entry);
                    net.setTokens(p, 1);
//            }
//            else if (hasInternalIncoming(_entry)) {
//                    Place p = net.addPlace("_entry_");
//                    Transition t = net.addTransition("_from_entry_");
//
//                    net.addArc(p, t);
//                    net.addArc(t, (Place)entry);
//                    net.setTokens(p, 1);
            } else
                    net.setTokens((Place)entry, 1);

            if (exit instanceof Transition) {
                    Place p = net.addPlace("_exit_");
                    net.addArc((Transition)exit, p);
            }

//            if (exit instanceof Place && model.isXORGateway(_exit) && hasInternalOutgoing(_exit)) {
//                    Transition t = net.addTransition("_to_exit_");
//                    Place p = net.addPlace("_exit_");
//                    net.addArc((Place)exit, t);
//                    net.addArc(t, p);
//            }

            return net;
    }
    
    public Node getNode(int node) {
    	return map.get(node);
    }
}
