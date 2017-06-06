package nl.rug.ds.bpm.verification.converter;

import nl.rug.ds.bpm.specification.jaxb.Condition;
import nl.rug.ds.bpm.verification.comparator.StringComparator;
import nl.rug.ds.bpm.verification.model.kripke.Kripke;
import nl.rug.ds.bpm.verification.model.kripke.State;
import nl.rug.ds.bpm.verification.stepper.Marking;
import nl.rug.ds.bpm.verification.stepper.Stepper;
import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.verification.map.IDMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class KripkeConverter {
    private EventHandler eventHandler;
	private Stepper parallelStepper;
    private Kripke kripke;
    private Set<String> conditions;
    private IDMap idMap;

    public KripkeConverter(EventHandler eventHandler, Stepper paralelStepper, List<Condition> conditions, IDMap idMap) {
        this.eventHandler = eventHandler;
        this.parallelStepper = paralelStepper;
        this.conditions = new HashSet<>();
        
        for (Condition condition: conditions)
            this.conditions.add(condition.getCondition());
        
        this.idMap = new IDMap("t", idMap.getIdToAp(), idMap.getApToId());
        
        State.resetStateId();
    }

    public Kripke convert() {
        kripke = new Kripke();

        Marking marking = parallelStepper.initialMarking();
		for (Set<String> enabled: parallelStepper.parallelActivatedTransitions(marking)) {
            State found = new State(marking.toString(), mapAp(enabled));
            kripke.addInitial(found);
            
            for (String transition: enabled)
                for (Marking step: parallelStepper.fireTransition(marking, transition, conditions)) {
                    ConverterAction converterAction = new ConverterAction(eventHandler, kripke, parallelStepper, idMap, step, found, conditions);
                    converterAction.compute();
                }
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
                eventHandler.logVerbose("Mapping " + id + " to " + idMap.getAP(id));
        }
        
        return aps;
    }
}
