package nl.rug.ds.bpm.verification.converter;

import nl.rug.ds.bpm.exception.ConverterException;
import nl.rug.ds.bpm.log.LogEvent;
import nl.rug.ds.bpm.log.Logger;
import nl.rug.ds.bpm.verification.comparator.StringComparator;
import nl.rug.ds.bpm.verification.map.IDMap;
import nl.rug.ds.bpm.verification.model.kripke.Kripke;
import nl.rug.ds.bpm.verification.model.kripke.State;
import nl.rug.ds.bpm.verification.stepper.Marking;
import nl.rug.ds.bpm.verification.stepper.Stepper;

import java.util.Set;
import java.util.TreeSet;

public class KripkeConverter {
    private Stepper parallelStepper;
    private Kripke kripke;
    private IDMap idMap;

    public KripkeConverter(Stepper parallelStepper, IDMap idMap) {
        this.parallelStepper = parallelStepper;
        this.idMap = new IDMap("t", idMap.getIdToAp(), idMap.getApToId());
        State.resetStateId();
    }

    public Kripke convert() throws ConverterException {
        kripke = new Kripke();

        Marking marking = parallelStepper.initialMarking();
        for (Set<String> enabled: parallelStepper.parallelActivatedTransitions(marking)) {
            State found = new State(marking.toString(), mapAp(enabled));
            kripke.addInitial(found);

            for (String transition: enabled)
                for (Marking step : parallelStepper.fireTransition(marking, transition)) {
                    ConverterAction converterAction = new ConverterAction(kripke, parallelStepper, idMap, step, found);
                    converterAction.compute();
                }
        }

        if (kripke.getStateCount() >= Kripke.getMaximumStates()) {
            throw new ConverterException("Maximum state space reached (at " + Kripke.getMaximumStates() + " states)");
        }

        return kripke;
    }

    private TreeSet<String> mapAp(Set<String> ids) {
        TreeSet<String> aps = new TreeSet<String>(new StringComparator());

        for (String id: ids) {
            boolean exist = idMap.getIdToAp().containsKey(id);

            idMap.addID(id);
            aps.add(idMap.getAP(id));

            if(!exist)
                Logger.log("Mapping " + id + " to " + idMap.getAP(id), LogEvent.VERBOSE);
        }

        return aps;
    }
}
