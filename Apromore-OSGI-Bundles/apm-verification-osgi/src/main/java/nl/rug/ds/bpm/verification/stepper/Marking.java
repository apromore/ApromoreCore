package nl.rug.ds.bpm.verification.stepper;

import nl.rug.ds.bpm.verification.comparator.StringComparator;

import java.util.*;

/**
 * Created by Nick van Beest 26-Apr-17.
 */
public class Marking {
	private static int maximumTokensAtPlaces = 3;
	
	private SortedMap<String, Integer> tokenmap;
	
	public Marking() {
		tokenmap = new TreeMap<String, Integer>(new StringComparator());
	}
	
	public void addTokens(String placeId, int tokens) {
		if (tokens > 0) {
			if (!tokenmap.containsKey(placeId)) {
				tokenmap.put(placeId, tokens);
			}
			else {
				tokens += tokenmap.get(placeId);
				if (tokens > maximumTokensAtPlaces) tokens = maximumTokensAtPlaces;
				tokenmap.put(placeId, tokens);
			}
		}
	}
	
	public void addTokens(Set<String> placeIds, int tokens) {
		for (String placeId: placeIds) {
			addTokens(placeId, tokens);
		}
	}
	
	public void emptyPlace(String placeId) {
		if (tokenmap.containsKey(placeId)) tokenmap.remove(placeId);
	}
	
	public Set<String> getMarkedPlaces() {
		return (tokenmap.keySet());
	}
	
	public Boolean hasTokens(String placeId) {
		return (tokenmap.containsKey(placeId));
	}
	
	public int getTokensAtPlace(String placeId) {
		return tokenmap.get(placeId);
	}
	
	public void consumeToken(String placeId) {
		if (hasTokens(placeId)) {
			int tokens = tokenmap.get(placeId);
			
			if (tokens == 1) {
				emptyPlace(placeId);
			}
			else {
				tokenmap.put(placeId, tokens - 1);
			}
		}
	}
	
	public void consumeTokens(Set<String> placeIds) {
		for (String placeId: placeIds) {
			consumeToken(placeId);
		}
	}
	
	public void copyFromMarking(Marking m) {
		tokenmap = new TreeMap<String, Integer>();
		
		for (String placeId: m.getMarkedPlaces()) {
			tokenmap.put(placeId, m.getTokensAtPlace(placeId));
		}
	}
	
	@Override
	public String toString() {
		String s = "";

		Iterator<String> p = tokenmap.keySet().iterator();
		String placeId;

		while(p.hasNext()) {
			placeId = p.next();
			s = s + "+" + tokenmap.get(placeId) + placeId;
		}
		return (s.length() > 0 ? s.substring(1) : "");
	}

	public Marking clone() {
		Marking marking = new Marking();
		marking.copyFromMarking(this);
		return marking;
	}

	public static void setMaximumTokensAtPlaces(int maximum) {
		maximumTokensAtPlaces = maximum;
	}
	
	public static int getMaximumTokensAtPlaces() {
		return maximumTokensAtPlaces;
	}
}
