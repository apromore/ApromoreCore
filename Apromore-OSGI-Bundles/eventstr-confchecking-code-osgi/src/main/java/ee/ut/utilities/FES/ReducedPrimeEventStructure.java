package ee.ut.utilities.FES;

import java.util.BitSet;
import java.util.List;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.PrimeEventStructure;

public class ReducedPrimeEventStructure <T> extends PrimeEventStructure<T> {
	BitSet[] causality;
	protected BitSet[] dcausality;
	BitSet[] invcausality;
	BitSet[] concurrency;
	BitSet[] conflict;
	protected List<String> labels;
	List<Integer> sources;
	List<Integer> sinks;
	
	BehaviorRelation[][] matrix;

	public ReducedPrimeEventStructure(List<String> labels, BitSet[] causality, BitSet[] dcausality,
			BitSet[] invcausality, BitSet[] concurrency, BitSet[] conflict, List<Integer> sources, List<Integer> sinks) {
		super(labels, causality, dcausality, invcausality, concurrency, conflict, sources, sinks);
		
		this.causality = causality;
		this.dcausality = dcausality;
		this.invcausality = invcausality;
		this.concurrency = concurrency;
		this.conflict = conflict;
		this.sources = sources;
		this.sinks = sinks;
		
		setupBRelMatrix();
	}
	
	private void setupBRelMatrix() {
		matrix = super.getBRelMatrix();
		int size = matrix.length;
		
		for (int i = 0; i < size; i++) {
			matrix[i][i] = BehaviorRelation.CONCURRENCY;
			for (int j = i + 1; j < size; j++) {
				if (dcausality[i].get(j)) {
					matrix[i][j] = BehaviorRelation.CAUSALITY;
					matrix[j][i] = BehaviorRelation.INV_CAUSALITY;
				} else if (invcausality[i].get(j)) {
					matrix[i][j] = BehaviorRelation.INV_CAUSALITY;
					matrix[j][i] = BehaviorRelation.CAUSALITY;
				} else if (concurrency[i].get(j))
					matrix[i][j] = matrix[j][i] = BehaviorRelation.CONCURRENCY;
				else
					matrix[i][j] = matrix[j][i] = BehaviorRelation.CONFLICT;
			}
		}
	}
	
	public BehaviorRelation[][] getBRelMatrix() {
		return matrix;
	}

}
