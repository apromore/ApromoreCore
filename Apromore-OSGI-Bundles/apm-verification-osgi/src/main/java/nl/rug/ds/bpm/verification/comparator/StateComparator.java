package nl.rug.ds.bpm.verification.comparator;

import nl.rug.ds.bpm.verification.model.kripke.State;

import java.util.Comparator;

public class StateComparator implements Comparator<State>
{
	@Override
	public int compare(State a, State b)
	{
		return a.compareTo(b);
	}
}
