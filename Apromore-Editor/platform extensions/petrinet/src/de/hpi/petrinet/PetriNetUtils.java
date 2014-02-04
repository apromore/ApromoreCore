package de.hpi.petrinet;

import java.util.UUID;

public class PetriNetUtils {

    public static String getId() {
        return UUID.randomUUID().toString();
    }

    public static SilentTransition addSilentTransition(PetriNet net) {
        SilentTransition newT = net.getFactory().createSilentTransition();
        newT.setId(getId());
        net.getTransitions().add(newT);
        return newT;
    }

    public static Place addPlace(PetriNet net) {
        Place newP = net.getFactory().createPlace();
        newP.setId(getId());
        net.getPlaces().add(newP);
        return newP;
    }

    public static FlowRelationship addFlowRelationship(PetriNet net, Node from, Node to) {
        FlowRelationship fr = net.getFactory().createFlowRelationship();
        fr.setSource(from);
        fr.setTarget(to);
        net.getFlowRelationships().add(fr);
        return fr;
    }
}
