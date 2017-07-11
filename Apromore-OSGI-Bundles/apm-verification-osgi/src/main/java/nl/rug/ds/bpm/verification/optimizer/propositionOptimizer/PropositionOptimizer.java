package nl.rug.ds.bpm.verification.optimizer.propositionOptimizer;

import nl.rug.ds.bpm.verification.comparator.StringComparator;
import nl.rug.ds.bpm.verification.model.kripke.Kripke;
import nl.rug.ds.bpm.verification.model.kripke.State;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Heerko Groefsema on 06-Mar-17.
 */
public class PropositionOptimizer {
	private Kripke kripke;
	private TreeSet<String> optimizedPropositions;
	
	public PropositionOptimizer(Kripke kripke) {
		this.kripke = kripke;
		optimizedPropositions = new TreeSet<>(new StringComparator());
	}
	
	public PropositionOptimizer(Kripke kripke, Set<String> AP) {
		this(kripke);
		optimize(AP);
	}
		
	public void optimize(Set<String> AP) {
		optimizedPropositions.addAll(AP);
		
		for (State s : kripke.getStates())
			s.removeAP(AP);
		
		kripke.getAtomicPropositions().removeAll(AP);
	}
	
	public String toString(boolean fullOutput) {
		StringBuilder sb = new StringBuilder();
		sb.append("Reduction of " + optimizedPropositions.size() + " propositions ");
		if (fullOutput) {
			sb.append("(");
			for (String s : optimizedPropositions)
				sb.append(s.toString() + " ");
			sb.append(")");
		}
		
		return sb.toString();
	}
	
	public String toString() {
		return toString(true);
	}
}
