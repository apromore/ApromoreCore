package ee.ut.eventstr;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.BitSet;
import java.util.List;

public class PrimeEventStructure <T> {
	BitSet[] causality;
	protected BitSet[] dcausality;
	BitSet[] invcausality;
	BitSet[] concurrency;
	BitSet[] conflict;
	protected List<String> labels;
	List<Integer> sources;
	List<Integer> sinks;
	
	BehaviorRelation[][] matrix;

	public PrimeEventStructure(List<String> labels, BitSet[] causality, BitSet[] dcausality,
			BitSet[] invcausality, BitSet[] concurrency, BitSet[] conflict, List<Integer> sources, List<Integer> sinks) {
		this.causality = causality;
		this.dcausality = dcausality;
		this.invcausality = invcausality;
		this.concurrency = concurrency;
		this.conflict = conflict;
		this.labels = labels;
		this.sources = sources;
		this.sinks = sinks;
	}
	
	public BehaviorRelation[][] getBRelMatrix() {
		if (matrix == null) {
			int size = labels.size();
			matrix = new BehaviorRelation[size][size];
			
			for (int i = 0; i < size; i++) {
				matrix[i][i] = BehaviorRelation.CONCURRENCY;
				for (int j = i + 1; j < size; j++) {
					if (causality[i].get(j)) {
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
		return matrix;
	}
	
	public BitSet[] getDirectCausalRelations() {
		return dcausality;
	}
	
	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		out.println("\tnode[shape=box];");
		for (int i = 0; i < labels.size(); i++)
			out.printf("\tn%d [label=\"%s(%d)\"];\n", i, labels.get(i), i);
		
		for (int src = 0; src < labels.size(); src++)
			for (int tgt = dcausality[src].nextSetBit(0); tgt >= 0; tgt = dcausality[src].nextSetBit(tgt+1))
				out.printf("\tn%d -> n%d;\n", src, tgt);
		
		out.println("}");
		
		return str.toString();
	}

	public String toDot(BitSet conf, BitSet hidings) {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		out.println("\tnode[shape=box];");
		for (int e = conf.nextSetBit(0); e >= 0; e = conf.nextSetBit(e+1)) {
			if (!hidings.get(e))
				out.printf("\tn%d [label=\"%s\"];\n", e, labels.get(e));
		}

		out.println("\tnode[shape=box,style=filled,color=red];");
		for (int e = hidings.nextSetBit(0); e >= 0; e = hidings.nextSetBit(e+1)) {
				out.printf("\tn%d [label=\"%s\"];\n", e, labels.get(e));			
		}
		
		for (int src = conf.nextSetBit(0); src >= 0; src = conf.nextSetBit(src+1))
			for (int tgt = conf.nextSetBit(0); tgt >= 0; tgt = conf.nextSetBit(tgt+1))
				if (dcausality[src].get(tgt))
					out.printf("\tn%d -> n%d;\n", src, tgt);
		
		out.println("}");
		
		return str.toString();
	}

	
	public List<String> getLabels() {
		return labels;
	}

	public List<Integer> getSinks() {
		return sinks;
	}
	
	public void printBRelMatrix(PrintStream out) {
		if (matrix == null)
			getBRelMatrix();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				out.printf("%s ", getCharacter(matrix[i][j]));
			}
			out.println();
		}
	}

	private String getCharacter(BehaviorRelation behaviorRelation) {
		switch (behaviorRelation) {
		case CAUSALITY:
			return "<";
		case INV_CAUSALITY:
			return ".";
		case CONFLICT:
			return "#";
		case CONCURRENCY:
			return "|";
		case ASYM_CONFLICT:
			return "/";
		case INV_ASYM_CONFLICT:
			return ".";
		}
		return null;
	}
}
