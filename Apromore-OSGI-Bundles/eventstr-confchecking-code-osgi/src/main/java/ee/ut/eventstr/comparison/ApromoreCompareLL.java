package ee.ut.eventstr.comparison;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.eventstr.comparison.LogBasedPartialSynchronizedProduct;
import ee.ut.eventstr.comparison.LogBasedPartialSynchronizedProduct.*;
import ee.ut.eventstr.comparison.DiffLLVerbalizer;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;

/**
 * @author Nick van Beest
 * @date 10/11/2016
 */
public class ApromoreCompareLL {
	
	public static final String version = "0.2";
	
	
	public Set<String> getDifferences(XLog log1, XLog log2) {
		return getDifferences(log1, log2, true);
	}
	
	public Set<String> getDifferences(XLog log1, XLog log2, Boolean removeIdentifiers) {
		try {
			if (removeIdentifiers) {
				// cleanStatements removes the numbers between parentheses in the verbalisation
				return new HashSet<String>(cleanStatements(getStatements(log1, log2)));
			}
			else {
				return new HashSet<String>(getStatements(log1, log2));
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		return new HashSet<String>();
	}
	
	private List<String> getStatements(XLog log1, XLog log2) {
		SinglePORunPESSemantics<Integer> logpessem1;
		SinglePORunPESSemantics<Integer> logpessem2;
		LogBasedPartialSynchronizedProduct<Integer> psp;
		
		PrimeEventStructure<Integer> logpes1 = getLogPES(log1, "log 1");
		PrimeEventStructure<Integer> logpes2 = getLogPES(log2, "log 2");
		
		PESSemantics<Integer> fullLogPesSem1 = new PESSemantics<Integer>(logpes1);
		PESSemantics<Integer> fullLogPesSem2 = new PESSemantics<Integer>(logpes2);
		DiffLLVerbalizer<Integer> verbalizer = new DiffLLVerbalizer<Integer>(fullLogPesSem1, fullLogPesSem2);
		
		List<String> statements = new ArrayList<String>();
		
		int mincost;
		int curcost;
		int cursink = -1;
		List<Operation> bestOp;
		Set<Integer> unusedsinks = new HashSet<Integer>(logpes2.getSinks());
		
		for (int sink1: logpes1.getSinks()) {
			logpessem1 = new SinglePORunPESSemantics<Integer>(logpes1, sink1); 
			
			mincost = Integer.MAX_VALUE;
			bestOp = new ArrayList<Operation>();
			
			for (int sink2: logpes2.getSinks()) {
				logpessem2 = new SinglePORunPESSemantics<Integer>(logpes2, sink2);
		       	psp = new LogBasedPartialSynchronizedProduct<Integer>(logpessem1, logpessem2);
					
				psp.perform().prune();
				
				curcost = psp.getStates().get(psp.getStates().size() - 1).cost;
				
				if (curcost < mincost) {
					mincost = curcost;
					bestOp = psp.getOperationSequence();
					cursink = sink2;
				}
			}
			verbalizer.addPSP(bestOp);
			unusedsinks.remove(cursink);
		}
		
		for (int sink2: unusedsinks) {
			logpessem2 = new SinglePORunPESSemantics<Integer>(logpes2, sink2);
			mincost = Integer.MAX_VALUE;
			bestOp = new ArrayList<Operation>();
			
			for (int sink1: logpes1.getSinks()) {
				logpessem1 = new SinglePORunPESSemantics<Integer>(logpes1, sink1); 
		       	psp = new LogBasedPartialSynchronizedProduct<Integer>(logpessem1, logpessem2);

		       	psp.perform().prune();
				
				curcost = psp.getStates().get(psp.getStates().size() - 1).cost;
				
				if (curcost < mincost) {
					mincost = curcost;
					bestOp = psp.getOperationSequence();
				}
			}
			verbalizer.addPSP(bestOp);
		}
		
		PrintStream stdout = System.out;
		ByteArrayOutputStream stats = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(stats);
		System.setOut(ps);
		
		verbalizer.verbalize();
		
		System.setOut(stdout);
		
		statements = new ArrayList<String>(Arrays.asList(stats.toString().split("\n")));
		
		return statements;
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
		PORun porun;
		
		for (XTrace trace: log) {
			porun = new PORun(alphaRelations, trace);
			
			runs.add(porun);
		}
		runs.mergePrefix();
		
//		System.out.println(runs.toDot());
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, name);
		
		return pes;
	}

}
