package ee.ut.eventstr.freq;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import ee.ut.eventstr.PrimeEventStructure;

public class FrequencyAwarePrimeEventStructure <T> extends PrimeEventStructure<T> {

	private Map<Integer, Integer> occurrences;
	private double[][] fmatrix;

	public FrequencyAwarePrimeEventStructure(List<String> labels, BitSet[] causality, BitSet[] dcausality,
			BitSet[] invcausality, BitSet[] concurrency, BitSet[] conflict, List<Integer> sources, List<Integer> sinks,
			Map<Integer, Integer> occurrences, double[][] fmatrix) {
		super(labels, causality, dcausality, invcausality, concurrency, conflict, sources, sinks);
		this.occurrences = occurrences;
		this.fmatrix = fmatrix;
	}
	
	public double[][] getFreqMatrix() {
		return fmatrix;
	}
	
	public Map<Integer, Integer> getOccurrences() {
		return occurrences;
	}
	
	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		out.println("\tnode[shape=box];");
		for (int i = 0; i < labels.size(); i++)
			out.printf("\tn%d [label=\"%s(%d)\\n%d\"];\n", i, labels.get(i), i, occurrences.get(i));
		
		for (int src = 0; src < labels.size(); src++)
			for (int tgt = dcausality[src].nextSetBit(0); tgt >= 0; tgt = dcausality[src].nextSetBit(tgt+1))
				out.printf("\tn%d -> n%d;\n", src, tgt);
		
		out.println("}");
		
		return str.toString();
	}
}
