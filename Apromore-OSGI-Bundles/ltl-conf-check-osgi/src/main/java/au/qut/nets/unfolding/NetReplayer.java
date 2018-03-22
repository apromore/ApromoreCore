package au.qut.nets.unfolding;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by armascer on 9/11/2017.
 */
public class NetReplayer {
    private HashSet<Execution> executions;
    PetriNet net;

    public NetReplayer(PetriNet net){
        this.net = net;
    }

    public HashSet<LinkedList<String>> getTraces() {
        Execution exec = new Execution();
        exec.setMarking(getInitialMarking());

        Queue<Execution> toAnalyze = new LinkedList<>();
        toAnalyze.add(exec);

        HashSet<LinkedList<String>> traces = new HashSet<>();
        int i=0;

        while(!toAnalyze.isEmpty()){
            Execution current = toAnalyze.remove();
            HashSet<Transition> enabled = getEnabledTs(current.marking);

            if(enabled.isEmpty()) {
                traces.add(current.getTrace());
                System.out.println("aaaa "+current.getTrace());
                //Qui potrei mettere il mapping perchè ci arriva solo a traccia finita
            }


            for(Transition t : enabled){
                Execution c1 = current.clone();
                c1.addFired(t);

                toAnalyze.add(c1);
            }
        }

        return traces;
    }

    private HashSet<Transition> getEnabledTs(LinkedList<Place> marking) {
        HashSet<Transition> enabled = new HashSet<>();
        for(Transition t : net.getTransitions())
            if(marking.containsAll(t.getPreSet()))
                enabled.add(t);

        return enabled;
    }

    private LinkedList<Place> getInitialMarking() {
        LinkedList<Place> marking = new LinkedList<>();

        for(Place p : net.getPlaces())
            if(p.getPreSet().isEmpty())
                marking.add(p);

        return marking;
    }
}
