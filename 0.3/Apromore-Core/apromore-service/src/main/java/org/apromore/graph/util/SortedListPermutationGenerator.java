package org.apromore.graph.util;

import org.apromore.graph.QueueEntry;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Stack;

public class SortedListPermutationGenerator {

	private Stack<ElementPermutationGenerator<QueueEntry>> currentPermutation = new Stack<ElementPermutationGenerator<QueueEntry>>();
    private Stack<ElementPermutationGenerator<QueueEntry>> tmp = new Stack<ElementPermutationGenerator<QueueEntry>>();
	private boolean hasMoreCombinations = true;


    public SortedListPermutationGenerator (LinkedList<QueueEntry> elements) {
        BigInteger nrCombinations = BigInteger.ONE;
        LinkedList<QueueEntry> current = new LinkedList<QueueEntry>();
		QueueEntry tmp = null;

        for (QueueEntry entry : elements) {
			// first element
			if (tmp == null) {
				tmp = entry;
				current.add(tmp);
				continue;
			}
			// this is from the same set
			if (entry.getLabel().equals(tmp.getLabel())) {
				current.add(entry);
			} else {
				ElementPermutationGenerator<QueueEntry> toAdd = new ElementPermutationGenerator<QueueEntry>(current);
				nrCombinations = nrCombinations.multiply(toAdd.getTotal());
				currentPermutation.push(toAdd);
				tmp = entry;
				current = new LinkedList<QueueEntry>();
				current.add(tmp);
			}
		}
		if (current.size() > 0) {
			ElementPermutationGenerator<QueueEntry> toAdd = new ElementPermutationGenerator<QueueEntry>(current);
			//nrCombinations = nrCombinations.multiply(toAdd.getTotal());
			currentPermutation.push(toAdd);
		}
	}
	
	
	public LinkedList<QueueEntry> getNextCombination() {
		LinkedList<QueueEntry> combination = new LinkedList<QueueEntry>();
		
		for (int i = 0; i < currentPermutation.size(); i++) {
			ElementPermutationGenerator<QueueEntry> permutation = currentPermutation.elementAt(i);
			combination.addAll(permutation.getCurrentCombination());
		}
		
		while (!currentPermutation.isEmpty()) {
			ElementPermutationGenerator<QueueEntry> permutation = currentPermutation.pop();
			// there is no more permutation for this element
			if (!permutation.hasMore()) {
				tmp.push(permutation);
			}
			// put the next permutation of this element 
			else {
				permutation.setNext();
				currentPermutation.push(permutation);
				while (!tmp.isEmpty()) {
					ElementPermutationGenerator<QueueEntry> tmpPerm = tmp.pop();
					tmpPerm.reset();
					currentPermutation.push(tmpPerm);
				}
				break;
			}
		}
		if (currentPermutation.size() == 0) {
			hasMoreCombinations = false;
		}
		return combination;
	}
	
	public boolean hasMoreConbinations() {
		return hasMoreCombinations;
	}
	
}
