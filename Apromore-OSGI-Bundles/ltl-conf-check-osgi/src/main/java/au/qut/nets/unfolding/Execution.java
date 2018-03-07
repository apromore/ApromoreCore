package au.qut.nets.unfolding;

import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import java.util.LinkedList;

/**
 * Created by armascer on 9/11/2017.
 */
public class Execution {
    LinkedList<Place> marking;
    LinkedList<Transition> firingSeq;
    LinkedList<String> trace;

    public void setMarking(LinkedList<Place> marking) {
        this.marking = marking;
    }

    public Execution clone() {
        Execution clone = new Execution();

        if(this.marking != null)
            clone.setMarking(new LinkedList<Place>(this.marking));

        if(this.firingSeq != null)
            clone.setFiringSeq(new LinkedList<Transition>(firingSeq));

        if(this.trace != null)
            clone.setTrace(new LinkedList<String>(trace));

        return clone;
    }

    public void addFired(Transition t){
        if(firingSeq == null)
            this.firingSeq = new LinkedList<>();

        if(trace == null)
            this.trace = new LinkedList<>();

        if(marking == null)
            this.marking = new LinkedList<>();
        this.firingSeq.add(t);
        this.trace.add(t.getName());
        this.marking.removeAll(t.getPreSet());
        this.marking.addAll(t.getPostSet());
    }

    public void setFiringSeq(LinkedList<Transition> firingSeq) {
        this.firingSeq = firingSeq;
    }

    public void setTrace(LinkedList<String> trace) {
        this.trace = trace;
    }

    public LinkedList<String> getTrace() {
        return trace;
    }
}
