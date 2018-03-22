package au.qut.nets.unfolding;

import au.ltl.utils.ModelAbstractions;
import hub.top.petrinet.Node;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by armascer on 9/11/2017.
 */
public class UnfoldingDecomposer {
    ModelAbstractions model;
    PetriNet unfolding;
    private HashSet<PetriNet> subNets;

    public UnfoldingDecomposer(ModelAbstractions model){
        this.model = model;
        this.unfolding = model.getUnfolding();
    }

    public HashSet<PetriNet> getSubNets() {
        HashSet<PetriNet> subNets = new HashSet<>();
        Place finalPlaceNet = getSinkPlace(model.getNet());

        if(finalPlaceNet == null) {
            System.out.println("Not a workflow net");
            System.exit(0);
        }

        for(Place p : unfolding.getPlaces()){
            if(p.getPostSet().isEmpty() && !model.getUnfolder().cutoffs.contains(p))
                subNets.add(extractSubNetFrom(p));
        }

        return subNets;
    }

    private PetriNet extractSubNetFrom(Place p) {
        PetriNet newNet = new PetriNet();

        Queue<Node> toCheck = new LinkedList<>();
        toCheck.add(p);

        HashMap<Node, Node> mapOldNew = new HashMap<>();
        mapOldNew.put(p, newNet.addPlace(p.getName()));

        while(!toCheck.isEmpty()){
            Node current = toCheck.remove();

            if(current instanceof Place){
                for(Transition n : ((Place)current).getPreSet()) {
                    if(!mapOldNew.containsKey(n)) {
                        mapOldNew.put(n, newNet.addTransition(n.getName()));
                        toCheck.add(n);
                    }
                    newNet.addArc((Transition) mapOldNew.get(n), (Place) mapOldNew.get(current));

                }
            }else{
                for(Place n : ((Transition)current).getPreSet()) {
                    if(!mapOldNew.containsKey(n)) {
                        mapOldNew.put(n, newNet.addPlace(n.getName()));
                        toCheck.add(n);
                    }
                    newNet.addArc((Place)mapOldNew.get(n), (Transition) mapOldNew.get(current));
                }
            }
        }

        return newNet;
    }

    private Place getSinkPlace(PetriNet net) {
        for(Place p : net.getPlaces())
            if(p.getPostSet().isEmpty())
                return p;

        return null;
    }
}
