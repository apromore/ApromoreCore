/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

import java.util.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.utilities.Triplet;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.eventstr.comparison.LogBasedPartialSynchronizedProduct.Operation;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

/**
 * @author Nick van Beest
 * @date 23/11/2016
 */
public class ApromoreCompareLL {
	
	public static final String version = "0.3";

	public static void main(String[] args){
//		String log1Name = "/Users/abelarmas/Work/Research/Code/LogGenerator/nets/Net3.pnml_log.xes";
//		String log2Name = "/Users/abelarmas/Work/Research/Code/LogGenerator/nets/Net5.pnml_log.xes";

//        String log1Name = "/Users/abelarmas/Dropbox/BPM2015-VarianceAnalysis/tool/ProDelta/Logs/base.MXML";
//        String log2Name = "/Users/abelarmas/Dropbox/BPM2015-VarianceAnalysis/tool/ProDelta/Logs/par_seq.mxml";
        String log1Name = "/Users/raffaele/Downloads/Deviance Mining - SEPSIS Correct.xes.gz";
        String log2Name = "/Users/raffaele/Downloads/Deviance Mining - SEPSIS Deviant.xes.gz";
//		String log1Name = "/Users/raffaele/Downloads/Deviance Mining - Correct.xes.gz";
//        String log2Name = "/Users/raffaele/Downloads/Deviance Mining - Deviant.xes.gz";

		ApromoreCompareLL compare = new ApromoreCompareLL();
		try {
			XLog log1 = XLogReader.openLog(log1Name);
			XLog log2 = XLogReader.openLog(log2Name);

			ApromoreCompareLL compareLL = new ApromoreCompareLL();

			for(Triplet<String, Set<XTrace>, Set<XTrace>> triplet : compareLL.getDifferencesTriplets(log1, "normal behaviour", log2, "deviant behaviour")){
					System.out.println(triplet.getA());
//                	System.out.println(compare.translateTraces((Set<XTrace>)triplet.getB()));
//                	System.out.println(compare.translateTraces((Set<XTrace>)triplet.getC()));
            }

		}catch(Exception e){ e.printStackTrace(); }
	}

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
        IntObjectHashMap<IntObjectHashMap<LogBasedPartialSynchronizedProduct<Integer>>> map = new IntObjectHashMap<>();

        for (int sink1: logpes1.getSinks()) {
            IntObjectHashMap<LogBasedPartialSynchronizedProduct<Integer>> map1 = new IntObjectHashMap<>();
            map.put(sink1, map1);

			logpessem1 = new SinglePORunPESSemantics<Integer>(logpes1, sink1); 
			
			mincost = Integer.MAX_VALUE;
			bestOp = new ArrayList<Operation>();
			
			for (int sink2: logpes2.getSinks()) {
				logpessem2 = new SinglePORunPESSemantics<Integer>(logpes2, sink2);
		       	psp = new LogBasedPartialSynchronizedProduct<Integer>(logpessem1, logpessem2, mincost);
                psp = psp.perform();
                if(psp == null) {
                    continue;
                }

                psp = psp.prune();

                map1.put(sink2, psp);

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
                IntObjectHashMap<LogBasedPartialSynchronizedProduct<Integer>> map1 = map.get(sink1);
                psp = map1.get(sink2);
                if(psp == null) {
                    logpessem1 = new SinglePORunPESSemantics<Integer>(logpes1, sink1);
                    psp = new LogBasedPartialSynchronizedProduct<Integer>(logpessem1, logpessem2, mincost);
                    psp = psp.perform();
                    if(psp == null) {
                        continue;
                    }
                }

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

	public Set<Triplet<String, Set<XTrace>, Set<XTrace>>> getDifferencesTriplets(XLog log1, String logName1, XLog log2, String logName2) {
        SinglePORunPESSemantics<Integer> logpessem1;
        SinglePORunPESSemantics<Integer> logpessem2;
        LogBasedPartialSynchronizedProduct<Integer> psp;

        PrimeEventStructure<Integer> logpes1 = getLogPES(log1, "log 1");
        PrimeEventStructure<Integer> logpes2 = getLogPES(log2, "log 2");

        PESSemantics<Integer> fullLogPesSem1 = new PESSemantics<Integer>(logpes1);
        PESSemantics<Integer> fullLogPesSem2 = new PESSemantics<Integer>(logpes2);
        DiffLLVerbalizerTriplet<Integer> verbalizer = new DiffLLVerbalizerTriplet<>(fullLogPesSem1, logName1, fullLogPesSem2, logName2);

        int mincost;
		int curcost;
		int cursink = -1;
		List<Operation> bestOp = null;
		Set<Integer> unusedsinks = new HashSet<>(logpes2.getSinks());
		IntObjectHashMap<IntObjectHashMap<LogBasedPartialSynchronizedProduct<Integer>>> map = new IntObjectHashMap<>();
        IntObjectHashMap<SinglePORunPESSemantics> pes1 = new IntObjectHashMap<>();
        IntObjectHashMap<SinglePORunPESSemantics> pes2 = new IntObjectHashMap<>();

        List<Integer> sinks1 = logpes1.getSinks();
		Collections.sort(sinks1, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                Integer i1 = fullLogPesSem1.getLocalConfiguration(o1).cardinality();
                Integer i2 = fullLogPesSem1.getLocalConfiguration(o2).cardinality();
                return i1.compareTo(i2);
            }
        });

        List<Integer> sinks2 = logpes2.getSinks();
        Collections.sort(sinks2, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                Integer i1 = fullLogPesSem2.getLocalConfiguration(o1).cardinality();
                Integer i2 = fullLogPesSem2.getLocalConfiguration(o2).cardinality();
                return i1.compareTo(i2);
            }
        });

        for (int sink1: sinks1) {
            IntObjectHashMap<LogBasedPartialSynchronizedProduct<Integer>> map1 = new IntObjectHashMap<>();
		    map.put(sink1, map1);

            logpessem1 = getLogPESSem(pes1, logpes1, sink1);

			mincost = Integer.MAX_VALUE;

            for (int sink2: sinks2) {
                logpessem2 = getLogPESSem(pes2, logpes2, sink2);
				psp = new LogBasedPartialSynchronizedProduct<>(logpessem1, logpessem2, mincost);
                psp = psp.perform();
				if(psp == null) {
				    continue;
                }
				psp = psp.prune();
				map1.put(sink2, psp);

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
            int b = logpes1.getSinks().size();

            logpessem2 = getLogPESSem(pes2, logpes2, sink2);
			mincost = Integer.MAX_VALUE;

            for (int sink1: sinks1) {
                IntObjectHashMap<LogBasedPartialSynchronizedProduct<Integer>> map1 = map.get(sink1);
                psp = map1.get(sink2);
                if(psp == null) {
                    logpessem1 = getLogPESSem(pes1, logpes1, sink1);
                    psp = new LogBasedPartialSynchronizedProduct<>(logpessem1, logpessem2, mincost);
                    psp = psp.perform();
                    if(psp == null) {
                        continue;
                    }
                    psp = psp.prune();
                }

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

    private SinglePORunPESSemantics<Integer> getLogPESSem(IntObjectHashMap<SinglePORunPESSemantics> pes, PrimeEventStructure<Integer> logpes, int sink) {
        SinglePORunPESSemantics<Integer> logpessem = pes.get(sink);
        if(logpessem == null) {
            logpessem = new SinglePORunPESSemantics<>(logpes, sink);
            pes.put(sink, logpessem);
        }
        return logpessem;
    }

    private PrimeEventStructure<Integer> getLogPES(XLog log, String name) {
		AlphaRelations alphaRelations = new AlphaRelations(log);

		BiMap<String, XTrace> mapTraces = HashBiMap.create();

		PORuns runs = new PORuns();
		PORun porun;
		int i =0;

		for (XTrace trace: log) {
			mapTraces.put(i + "",trace);
			porun = new PORun(alphaRelations, trace, (i++) + "");
			
			runs.add(porun);
		}
		runs.mergePrefix();
				
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, name);
		pes.setIdTracesMap(mapTraces);
		
		return pes;
	}

	public String translateTraces(Set<XTrace> traces){
	    String result = "";

	    for(XTrace trace : traces)
	        result += translateTrace(trace) + "\n";

	    return result;
    }

    public String translateTrace(XTrace trace){
	    String labels = "<";

        for (XEvent e: trace)
            if (isCompleteEvent(e) && e.getAttributes().get(XConceptExtension.KEY_NAME) != null)
                labels += getEventName(e) + ",";

        return labels.substring(0, labels.length() - 1) + ">";
    }

    private String getEventName(XEvent e) {
        return e.getAttributes().get(XConceptExtension.KEY_NAME).toString();
    }

    private boolean isCompleteEvent(XEvent e) {
        XAttributeMap amap = e.getAttributes();
        if (amap.get(XLifecycleExtension.KEY_TRANSITION) != null)
            return (amap.get(XLifecycleExtension.KEY_TRANSITION).toString().toLowerCase().equals("complete"));
        else
            return false;
    }

}
