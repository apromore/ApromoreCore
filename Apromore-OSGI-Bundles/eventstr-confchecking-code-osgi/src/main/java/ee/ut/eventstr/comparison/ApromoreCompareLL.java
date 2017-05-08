/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package ee.ut.eventstr.comparison;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.eventstr.comparison.LogBasedPartialSynchronizedProduct.Operation;

/**
 * @author Nick van Beest
 * @date 23/11/2016
 */
public class ApromoreCompareLL {
	
	public static final String version = "0.3";
	
	public Set<String> getDifferences(XLog log1, XLog log2) {
		SinglePORunPESSemantics<Integer> logpessem1;
		SinglePORunPESSemantics<Integer> logpessem2;
		LogBasedPartialSynchronizedProduct<Integer> psp;
		
		PrimeEventStructure<Integer> logpes1 = getLogPES(log1, "log 1");
		PrimeEventStructure<Integer> logpes2 = getLogPES(log2, "log 2");
		
		PESSemantics<Integer> fullLogPesSem1 = new PESSemantics<Integer>(logpes1);
		PESSemantics<Integer> fullLogPesSem2 = new PESSemantics<Integer>(logpes2);
		DiffLLVerbalizer<Integer> verbalizer = new DiffLLVerbalizer<Integer>(fullLogPesSem1, fullLogPesSem2);
				
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

		return verbalizer.verbalize();
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
				
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, name);
		
		return pes;
	}

}
