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
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.*;
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
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
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
        String log1Name = "/Volumes/RaffaeleCode/Deviance Mining - SEPSIS Correct.xes.gz";
        String log2Name = "/Volumes/RaffaeleCode/Deviance Mining - SEPSIS Deviant.xes.gz";
//        String log1Name = "/Volumes/RaffaeleCode/Deviance Mining - Correct.xes.gz";
//        String log2Name = "/Volumes/RaffaeleCode/Deviance Mining - Deviant.xes.gz";
//        String log1Name = "/Volumes/RaffaeleCode/test1.xes";
//        String log2Name = "/Volumes/RaffaeleCode/test2.xes";

		ApromoreCompareLL compare = new ApromoreCompareLL();
		try {
			XLog log1 = XLogReader.openLog(log1Name);
			XLog log2 = XLogReader.openLog(log2Name);

			ApromoreCompareLL compareLL = new ApromoreCompareLL();

			for(Triplet<String, Set<XTrace>, Set<XTrace>> triplet : compareLL.getDifferencesTripletsAStar(log1, "normal behaviour", log2, "deviant behaviour")){
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
		
		PESSemantics<Integer> fullLogPesSem1 = new PESSemantics<>(logpes1);
		PESSemantics<Integer> fullLogPesSem2 = new PESSemantics<>(logpes2);
		DiffLLVerbalizer<Integer> verbalizer = new DiffLLVerbalizer<>(fullLogPesSem1, fullLogPesSem2);

		int mincost;
		int curcost;
		int cursink = -1;
		List<Operation> bestOp;
		Set<Integer> unusedsinks = new HashSet<>(logpes2.getSinks());
        IntObjectHashMap<IntObjectHashMap<LogBasedPartialSynchronizedProduct<Integer>>> map = new IntObjectHashMap<>();

        for (int sink1: logpes1.getSinks()) {
            IntObjectHashMap<LogBasedPartialSynchronizedProduct<Integer>> map1 = new IntObjectHashMap<>();
            map.put(sink1, map1);

			logpessem1 = new SinglePORunPESSemantics<>(logpes1, sink1);
			
			mincost = Integer.MAX_VALUE;
			bestOp = new ArrayList<>();
			
			for (int sink2: logpes2.getSinks()) {
				logpessem2 = new SinglePORunPESSemantics<>(logpes2, sink2);
		       	psp = new LogBasedPartialSynchronizedProduct<>(logpessem1, logpessem2, sink2, 0);
                psp = psp.perform(mincost);
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
			logpessem2 = new SinglePORunPESSemantics<>(logpes2, sink2);
			mincost = Integer.MAX_VALUE;
			bestOp = new ArrayList<>();
			
			for (int sink1: logpes1.getSinks()) {
                IntObjectHashMap<LogBasedPartialSynchronizedProduct<Integer>> map1 = map.get(sink1);
                psp = map1.get(sink2);
                if(psp == null) {
                    logpessem1 = new SinglePORunPESSemantics<>(logpes1, sink1);
                    psp = new LogBasedPartialSynchronizedProduct<>(logpessem1, logpessem2, sink2, 0);
                    psp = psp.perform(mincost);
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

    public Set<Triplet<String, Set<XTrace>, Set<XTrace>>> getDifferencesTripletsAStar(XLog log1, String logName1, XLog log2, String logName2) {
        PrimeEventStructure<Integer> logpes1 = getLogPES(log1, "log 1");
        PrimeEventStructure<Integer> logpes2 = getLogPES(log2, "log 2");

        PESSemantics<Integer> fullLogPesSem1 = new PESSemantics<>(logpes1);
        PESSemantics<Integer> fullLogPesSem2 = new PESSemantics<>(logpes2);

        DiffLLVerbalizerTriplet<Integer> verbalizer = new DiffLLVerbalizerTriplet<>(fullLogPesSem1, logName1, fullLogPesSem2, logName2);

        SinglePORunPESSemantics<Integer> logpessem1;
        SinglePORunPESSemantics<Integer> logpessem2;
        LogBasedPartialSynchronizedProduct<Integer> psp;

        int mincost;
        IntObjectHashMap<SinglePORunPESSemantics> pes1 = new IntObjectHashMap<>();
        IntObjectHashMap<SinglePORunPESSemantics> pes2 = new IntObjectHashMap<>();

        List<Integer> sinks1 = logpes1.getSinks();
        Collections.sort(sinks1, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                BitSet s1a = fullLogPesSem1.getLocalConfiguration(o1);
                BitSet s1b = fullLogPesSem1.getLocalConfiguration(o2);
                return Integer.compare(s1b.cardinality(), s1a.cardinality());
            }
        });

        List<Integer> sinks2 = logpes2.getSinks();
        Collections.sort(sinks2, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                BitSet s2a = fullLogPesSem2.getLocalConfiguration(o1);
                BitSet s2b = fullLogPesSem2.getLocalConfiguration(o2);
                return Integer.compare(s2b.cardinality(), s2a.cardinality());
            }
        });

        BitSet bitSetSink1 = fullLogPesSem1.getLocalConfiguration(sinks1.get(sinks1.size() - 1));
        int minCardinalitySink1 = bitSetSink1.cardinality();
        BitSet bitSetSink2 = fullLogPesSem2.getLocalConfiguration(sinks2.get(sinks2.size() - 1));
        int minCardinalitySink2 = bitSetSink2.cardinality();

        IntIntHashMap matches = new IntIntHashMap();
        IntObjectHashMap<Integer> minCosts = new IntObjectHashMap();
        for (int sink1: sinks1) {
            int cardinalitySink1 = fullLogPesSem1.getLocalConfiguration(sink1).cardinality();
            logpessem1 = getLogPESSem(pes1, logpes1, sink1);
            mincost = cardinalitySink1 + minCardinalitySink2;

            Queue<LogBasedPartialSynchronizedProduct<Integer>> queue = new PriorityQueue<>();
            for (int sink2: sinks2) {
                if(Math.abs(fullLogPesSem2.getLocalConfiguration(sink2).cardinality() - cardinalitySink1) > mincost) continue;
                int guess = getMultiset(fullLogPesSem1.getLocalConfiguration(sink1), fullLogPesSem1, fullLogPesSem2.getLocalConfiguration(sink2), fullLogPesSem2).size();
                if(guess > mincost) continue;
                psp = computePSP(logpessem1, getLogPESSem(pes2, logpes2, sink2), sink2, guess);
                queue.offer(psp);
            }
            psp = runAStar(queue, mincost);
            verbalizer.addPSP(psp.getOperationSequence());
            matches.put(sink1, psp.sink);
            Integer cost = minCosts.get(psp.sink);
            if(cost == null || cost > mincost) minCosts.put(psp.sink, mincost);
        }

        for (int sink2: sinks2) {
            int cardinalitySink2 = fullLogPesSem2.getLocalConfiguration(sink2).cardinality();
            logpessem2 = getLogPESSem(pes2, logpes2, sink2);
            Integer cost = minCosts.get(sink2);
            mincost = cardinalitySink2 + minCardinalitySink1;
            if(cost != null && cost < mincost) mincost = cost;

            Queue<LogBasedPartialSynchronizedProduct<Integer>> queue = new PriorityQueue<>();
            for (int sink1: sinks1) {
                if(Math.abs(fullLogPesSem1.getLocalConfiguration(sink1).cardinality() - cardinalitySink2) > mincost) continue;
                int guess = getMultiset(fullLogPesSem1.getLocalConfiguration(sink1), fullLogPesSem1, fullLogPesSem2.getLocalConfiguration(sink2), fullLogPesSem2).size();
                if(guess > mincost) continue;
                psp = computePSP(getLogPESSem(pes1, logpes1, sink1), logpessem2, sink1, guess);
                queue.offer(psp);
            }
            psp = runAStar(queue, mincost);
            if(matches.get(psp.sink) != sink2) verbalizer.addPSP(psp.getOperationSequence());
        }
        return verbalizer.verbalize();
    }

    private LogBasedPartialSynchronizedProduct<Integer> runAStar(Queue<LogBasedPartialSynchronizedProduct<Integer>> queue, int mincost) {
	    LogBasedPartialSynchronizedProduct<Integer> psp = null;
        while (!queue.isEmpty()) {
            psp = queue.poll();
            if(psp.analyzeNextAStarState(mincost) != null) {
                if(psp.matchings == null) queue.offer(psp);
                else {
                    queue.clear();
                }
            }
        }
        return psp;
    }

//	public Set<Triplet<String, Set<XTrace>, Set<XTrace>>> getDifferencesTriplets(XLog log1, String logName1, XLog log2, String logName2) {
//        PrimeEventStructure<Integer> logpes1 = getLogPES(log1, "log 1");
//        PrimeEventStructure<Integer> logpes2 = getLogPES(log2, "log 2");
//
//        PESSemantics<Integer> fullLogPesSem1 = new PESSemantics<>(logpes1);
//        PESSemantics<Integer> fullLogPesSem2 = new PESSemantics<>(logpes2);
//
//        DiffLLVerbalizerTriplet<Integer> verbalizer = new DiffLLVerbalizerTriplet<>(fullLogPesSem1, logName1, fullLogPesSem2, logName2);
//
//        SinglePORunPESSemantics<Integer> logpessem1;
//        SinglePORunPESSemantics<Integer> logpessem2;
//        LogBasedPartialSynchronizedProduct<Integer> psp;
//
//        int mincost;
//        int curcost;
//        int cursink = -1;
//        List<Operation> bestOp = null;
//        Set<Integer> unusedsinks = new HashSet<>(logpes2.getSinks());
//        IntObjectHashMap<IntObjectHashMap<LogBasedPartialSynchronizedProduct<Integer>>> map = new IntObjectHashMap<>();
//        IntObjectHashMap<SinglePORunPESSemantics> pes1 = new IntObjectHashMap<>();
//        IntObjectHashMap<SinglePORunPESSemantics> pes2 = new IntObjectHashMap<>();
//
//        List<Integer> sinks1 = logpes1.getSinks();
//        Collections.sort(sinks1, new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                BitSet s1a = fullLogPesSem1.getLocalConfiguration(o1);
//                BitSet s1b = fullLogPesSem1.getLocalConfiguration(o2);
//                return Integer.compare(s1b.cardinality(), s1a.cardinality());
//            }
//        });
//
//        List<Integer> sinks2 = logpes2.getSinks();
//        Collections.sort(sinks2, new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                BitSet s2a = fullLogPesSem2.getLocalConfiguration(o1);
//                BitSet s2b = fullLogPesSem2.getLocalConfiguration(o2);
//                return Integer.compare(s2b.cardinality(), s2a.cardinality());
//            }
//        });
//
//        int a = sinks1.size();
//        BitSet bitSetSink1 = fullLogPesSem1.getLocalConfiguration(sinks1.get(sinks1.size() - 1));
//        int minCardinalitySink1 = bitSetSink1.cardinality();
//        BitSet bitSetSink2 = fullLogPesSem2.getLocalConfiguration(sinks2.get(sinks2.size() - 1));
//        int minCardinalitySink2 = bitSetSink2.cardinality();
//        for (int sink1: sinks1) {
//            bestOp = null;
//            int cardinalitySink1 = fullLogPesSem1.getLocalConfiguration(sink1).cardinality();
//            System.out.print(a * (sinks2.size()) + " " + cardinalitySink1);
//
//            int cardinalityBestSink2 = 0;
//            Collections.sort(sinks2, new Comparator<Integer>() {
//                @Override
//                public int compare(Integer o1, Integer o2) {
//                    return compareSinks(
//                            fullLogPesSem1.getLocalConfiguration(sink1),
//                            fullLogPesSem1,
//                            fullLogPesSem2.getLocalConfiguration(o1),
//                            fullLogPesSem2.getLocalConfiguration(o2),
//                            fullLogPesSem2
//                    );
//                }
//            });
//
//            IntObjectHashMap<LogBasedPartialSynchronizedProduct<Integer>> map1 = new IntObjectHashMap<>();
//		    map.put(sink1, map1);
//            logpessem1 = getLogPESSem(pes1, logpes1, sink1);
//            Multiset<String> set1 = getMultiset(fullLogPesSem1.getLocalConfiguration(sink1), fullLogPesSem1, bitSetSink2, fullLogPesSem2);
//            mincost = cardinalitySink1 + minCardinalitySink2;
////            mincost = set1.size();
////            mincost -= ((cardinalitySink1 + minCardinalitySink2) - mincost);
////            if(mincost == 0) mincost = cardinalitySink1 + minCardinalitySink2;
//            System.out.print(" " + mincost);
//
//            for (int sink2: sinks2) {
//                int cardinalitySink2 = fullLogPesSem2.getLocalConfiguration(sink2).cardinality();
//
//                if(Math.abs(cardinalitySink1 - cardinalitySink2) > mincost) continue;
//				if((psp = computePSP(logpessem1, getLogPESSem(pes2, logpes2, sink2), mincost)) == null) continue;
//
//				map1.put(sink2, psp);
//				curcost = psp.getStates().get(psp.getStates().size() - 1).cost;
//
//				if (curcost <= mincost) {
//					mincost = curcost;
//					bestOp = psp.getOperationSequence();
//					cursink = sink2;
//                    cardinalityBestSink2 = cardinalitySink2;
//                    if(curcost == 0) break;
//                }
//            }
//            if(bestOp == null) {
//                System.out.println("error");
//                mincost = cardinalitySink1 + minCardinalitySink2;
//                System.out.print(" " + mincost);
//                for (int sink2: sinks2) {
//                    int cardinalitySink2 = fullLogPesSem2.getLocalConfiguration(sink2).cardinality();
//
//                    if(Math.abs(cardinalitySink1 - cardinalitySink2) > mincost) continue;
//                    if((psp = computePSP(logpessem1, getLogPESSem(pes2, logpes2, sink2), mincost)) == null) continue;
//
//                    map1.put(sink2, psp);
//                    curcost = psp.getStates().get(psp.getStates().size() - 1).cost;
//
//                    if (curcost < mincost) {
//                        mincost = curcost;
//                        bestOp = psp.getOperationSequence();
//                        cursink = sink2;
//                        cardinalityBestSink2 = cardinalitySink2;
//                        if(curcost == 0) break;
//                    }
//                }
//            }
//            verbalizer.addPSP(bestOp);
//            unusedsinks.remove(cursink);
//            map1.remove(cursink);
//            a--;
//            System.out.println(" " + cardinalityBestSink2 + " " + mincost);
//        }
//
//		a = unusedsinks.size();
//        for (int sink2: unusedsinks) {
//            bestOp = null;
//            int cardinalitySink2 = fullLogPesSem2.getLocalConfiguration(sink2).cardinality();
//            int cardinalityBestSink1 = 0;
//            Collections.sort(sinks1, new Comparator<Integer>() {
//                @Override
//                public int compare(Integer o1, Integer o2) {
//                    return compareSinks(
//                            fullLogPesSem2.getLocalConfiguration(sink2),
//                            fullLogPesSem2,
//                            fullLogPesSem1.getLocalConfiguration(o1),
//                            fullLogPesSem1.getLocalConfiguration(o2),
//                            fullLogPesSem1
//                    );
//                }
//            });
//
//            logpessem2 = getLogPESSem(pes2, logpes2, sink2);
//            mincost = cardinalitySink2 + minCardinalitySink1;
//            for (int sink1: sinks1) {
//                int cardinalitySink1 = fullLogPesSem1.getLocalConfiguration(sink1).cardinality();
//                if(Math.abs(cardinalitySink1 - cardinalitySink2) > mincost) continue;
//
//                if((psp = map.get(sink1).get(sink2)) == null) {
//                    if((psp = computePSP(getLogPESSem(pes1, logpes1, sink1), logpessem2, mincost)) == null) continue;
//                }
//
//				curcost = psp.getStates().get(psp.getStates().size() - 1).cost;
//
//				if (curcost < mincost) {
//					mincost = curcost;
//					bestOp = psp.getOperationSequence();
//                    cardinalityBestSink1 = cardinalitySink1;
//                    if(curcost == 0) break;
//				}
//			}
//            if(bestOp == null) System.out.println("error");
//			verbalizer.addPSP(bestOp);
//            a--;
//            System.out.println(a * sinks1.size() + " " + cardinalitySink2 + " " + cardinalityBestSink1 + " " + mincost);
//        }
//
//		return verbalizer.verbalize();
//	}

    private LogBasedPartialSynchronizedProduct<Integer> computePSP(
            SinglePORunPESSemantics<Integer> logpessem1,
            SinglePORunPESSemantics<Integer> logpessem2,
            int sink,
            int guess
    ) {
        LogBasedPartialSynchronizedProduct<Integer> psp = new LogBasedPartialSynchronizedProduct<>(logpessem1, logpessem2, sink, guess);
        psp.initializeAStar();
        return psp;
    }

//	private LogBasedPartialSynchronizedProduct<Integer> computePSP(
//	        SinglePORunPESSemantics<Integer> logpessem1,
//            SinglePORunPESSemantics<Integer> logpessem2,
//            int minCost
//    ) {
//        LogBasedPartialSynchronizedProduct<Integer> psp = new LogBasedPartialSynchronizedProduct<>(logpessem1, logpessem2);
//        if(psp.perform2(minCost) == null) return null;
//        return psp.prune();
//    }

	private int compareSinks(BitSet bitSet1, PESSemantics<Integer> fullLogPesSem1, BitSet bitSet2a, BitSet bitSet2b, PESSemantics<Integer> fullLogPesSem2) {
        Multiset<String> set1 = getMultiset(bitSet1, fullLogPesSem1, bitSet2a, fullLogPesSem2);
        Multiset<String> set2 = getMultiset(bitSet1, fullLogPesSem1, bitSet2b, fullLogPesSem2);

        Integer i1 = set1.size();
        Integer i2 = set2.size();
        if(i1 == i2) return Integer.compare(Math.abs(bitSet1.cardinality() - bitSet2a.cardinality()), Math.abs(bitSet1.cardinality() - bitSet2b.cardinality()));
        return i1.compareTo(i2);
    }

    private Multiset<String> getMultiset(BitSet bitSet1, PESSemantics<Integer> fullLogPesSem1, BitSet bitSet2, PESSemantics<Integer> fullLogPesSem2) {
        Multiset<String> e1 = getEventList(bitSet1, fullLogPesSem1);
        Multiset<String> e2 = getEventList(bitSet2, fullLogPesSem2);
        return Multisets.union(Multisets.difference(e1, e2), Multisets.difference(e2, e1));
    }

    private Multiset<String> getEventList(BitSet bitSet, PESSemantics<Integer> fullLogPesSem) {
        Multiset<String> set = HashMultiset.create();
        for (int ev = bitSet.nextSetBit(0); ev >= 0; ev = bitSet.nextSetBit(ev + 1)) {
            set.add(fullLogPesSem.getLabel(ev));
        }
        return set;
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
