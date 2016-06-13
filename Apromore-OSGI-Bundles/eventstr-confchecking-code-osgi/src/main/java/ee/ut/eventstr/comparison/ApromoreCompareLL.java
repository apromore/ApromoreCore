package ee.ut.eventstr.comparison;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;

/**
 * @author Nick van Beest
 * @date 10/06/2016
 */
public class ApromoreCompareLL {
	
	public static final String version = "0.1";
	
	
	public Set<String> getDifferences(XLog log1, XLog log2) {
		return getDifferences(log1, log2, true);
	}
	
	public Set<String> getDifferences(XLog log1, XLog log2, Boolean removeIdentifiers) {
		try {
			if (removeIdentifiers) {
				// cleanStatements removes the numbers between parentheses in the verbalisation
//				return new HashSet<String>(cleanStatements(getStatements(log1, log2)));
				return new HashSet<String>(cleanStatements(new LinkedList<>(getStatements(log1, log2))));
			}
			else {
				return getStatements(log1, log2);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		return new HashSet<String>();
	}
	
	
	public Set<String> getStatements2(XLog log1, XLog log2) {
		PrimeEventStructure<Integer> logpes1 = getLogPES(log1, "log 1");
		PrimeEventStructure<Integer> logpes2 = getLogPES(log2, "log 2");
		
		PESSemantics<Integer> fullLogPesSem1 = new PESSemantics<Integer>(logpes1);
		PESSemantics<Integer> fullLogPesSem2 = new PESSemantics<Integer>(logpes2);
		
		PartialSynchronizedProduct<Integer> psp = new PartialSynchronizedProduct<>(fullLogPesSem1, fullLogPesSem2);		
		PartialSynchronizedProduct<Integer> pruned = psp.perform();

		HashSet<String> commonLabels = new HashSet<>(logpes1.getLabels());
		commonLabels.retainAll(logpes2.getLabels());
		
		DiffMMVerbalizer<Integer> verbalizer = new DiffMMVerbalizer<Integer>(fullLogPesSem1, fullLogPesSem2,commonLabels, new HashSet<String>(logpes1.getLabels()), new HashSet<String>(logpes2.getLabels()));
		pruned.setVerbalizer(verbalizer);
		pruned.prune();
		
		verbalizer.verbalize();

		return verbalizer.getStatements();
	}
	
	private Set<String> getStatements(XLog log1, XLog log2) {
		SinglePORunPESSemantics<Integer> logpessem1;
		SinglePORunPESSemantics<Integer> logpessem2;
		LogBasedPartialSynchronizedProduct<Integer> psp;
		
		PrimeEventStructure<Integer> logpes1 = getLogPES(log1, "log 1");
		PrimeEventStructure<Integer> logpes2 = getLogPES(log2, "log 2");
		
		PESSemantics<Integer> fullLogPesSem1 = new PESSemantics<Integer>(logpes1);
		PESSemantics<Integer> fullLogPesSem2 = new PESSemantics<Integer>(logpes2);
		DiffLLVerbalizer<Integer> verbalizer = new DiffLLVerbalizer<Integer>(fullLogPesSem1, fullLogPesSem2);
		
//		List<String> statements = new ArrayList<String>();
		
		for (int sink1: logpes1.getSinks()) {
			logpessem1 = new SinglePORunPESSemantics<Integer>(logpes1, sink1); 
			for (int sink2: logpes2.getSinks()) {
				logpessem2 = new SinglePORunPESSemantics<Integer>(logpes2, sink2);
		       	psp = new LogBasedPartialSynchronizedProduct<Integer>(logpessem1, logpessem2);
				psp.perform().prune();
				
				verbalizer.addPSP(psp.getOperationSequence());
			}
		}
		
//		PrintStream stdout = System.out;
//		ByteArrayOutputStream stats = new ByteArrayOutputStream();
//		PrintStream ps = new PrintStream(stats);
//		System.setOut(ps);
		
		verbalizer.verbalize();
		
//		System.setOut(stdout);
//		statements = new ArrayList<String>(Arrays.asList(stats.toString().split("\n")));
		
		return verbalizer.getStatements();
	}
	
	private List<String> cleanStatements(List<String> statements) {
		String temp;
		int s, e;
		
		for (int i = 0; i < statements.size(); i++) {
			temp = statements.get(i);
			
			e = 1;
			s = temp.indexOf("(", e);
				
			while (s > -1) {
				e = temp.indexOf(")", s);
				temp = temp.substring(0, s) + temp.substring(e + 1);
				s = temp.indexOf("(", s + 1);
			}
			statements.set(i, temp);
		}
		
		return statements;
	}
	
	private PrimeEventStructure<Integer> getLogPES(XLog log, String name) {				
		AlphaRelations alphaRelations = new AlphaRelations(log);
		
		PORuns runs = new PORuns();

		for (XTrace trace: log) {
			PORun porun = new PORun(alphaRelations, trace);
			
			runs.add(porun);
		}
		runs.mergePrefix();
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, name);
		
		return pes;
	}

}
