package nl.rug.ds.bpm.verification.converter;

import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.verification.comparator.StringComparator;
import nl.rug.ds.bpm.verification.map.IDMap;
import nl.rug.ds.bpm.verification.model.kripke.Kripke;
import nl.rug.ds.bpm.verification.model.kripke.State;
import nl.rug.ds.bpm.verification.stepper.Marking;
import nl.rug.ds.bpm.verification.stepper.Stepper;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.RecursiveAction;

/**
 * Created by Heerko Groefsema on 20-May-17.
 */
public class ConverterAction extends RecursiveAction {
	private EventHandler eventHandler;
	private Kripke kripke;
	private Stepper stepper;
	private IDMap idMap;
	private Marking marking;
	private State previous;
	private Set<String> conditions;
	
	public ConverterAction(EventHandler eventHandler, Kripke kripke, Stepper stepper, IDMap idMap, Marking marking, State previous, Set<String> conditions) {
		this.eventHandler = eventHandler;
		this.kripke = kripke;
		this.stepper = stepper;
		this.idMap = idMap;
		this.marking = marking;
		this.previous = previous;
		this.conditions = conditions;
	}
	
	@Override
	protected void compute() {
		if(kripke.getStateCount() >= Kripke.getMaximumStates()) {
			eventHandler.logCritical("Maximum state space reached (at " + Kripke.getMaximumStates() + " states)");
		}
		for (Set<String> enabled: stepper.parallelActivatedTransitions(marking)) {
			State found = new State(marking.toString(), mapAp(enabled));
			State existing = kripke.addNext(previous, found);
			
			if (found == existing) { //if found is a new state
				if (enabled.isEmpty()) { //if state is a sink
					found.addNext(found);
					found.addPrevious(found);
				}
				Set<ConverterAction> nextActions = new HashSet<>();
				for (String transition: enabled)
					for (Marking step: stepper.fireTransition(marking.clone(), transition, conditions))
						nextActions.add(new ConverterAction(eventHandler, kripke, stepper, idMap, step, found, conditions));
				
				invokeAll(nextActions);
			}
		}
	}
	
	private TreeSet<String> mapAp(Set<String> ids) {
		TreeSet<String> aps = new TreeSet<String>(new StringComparator());
		
		for (String id: ids) {
			boolean exist = idMap.getIdToAp().containsKey(id);
			
			if (id.startsWith("silent")) id = "silent"; // this line has to be tested more thoroughly
														// It's a quick fix to handle situations where multiple silents starting with "silent"
			idMap.addID(id);
			
			aps.add(idMap.getAP(id));
			
			if(!exist)
				eventHandler.logVerbose("Mapping " + id + " to " + idMap.getAP(id));
		}
		
		return aps;
	}
}
