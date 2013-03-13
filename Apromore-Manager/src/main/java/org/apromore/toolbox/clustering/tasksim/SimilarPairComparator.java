package org.apromore.toolbox.clustering.tasksim;

import java.util.Comparator;

public class SimilarPairComparator implements Comparator<SimilarPair> {

	@Override
	public int compare(SimilarPair o1, SimilarPair o2) {
		if (o1.getSim() > o2.getSim()) {
			return 1;
		} else if (o1.getSim() < o2.getSim()) {
			return -1;
		} else {
			return 0;
		}
	}
}
