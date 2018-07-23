package ee.ut.eventstr.folding;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;

public class OverGeneralizationTest {
	private String model = "cycle10";
	private String fileNameTemplate = "logs/%s.bpmn.mxml.gz";

	@Test
	public void test() throws Exception {
//		XLog log = XLogReader.openLog(String.format(fileNameTemplate, model));		
//		AlphaRelations alphaRelations = new AlphaRelations(log);
//		
//		File target = new File("target");
//		if (!target.exists())
//			target.mkdirs();
//		
//	    long time = System.nanoTime();
//		PORuns runs = new PORuns();
//
//		for (XTrace trace: log) {
//			PORun porun = new PORun(alphaRelations, trace);
//			runs.add(porun);
//		}
//		
//		IOUtils.toFile(model + "_prefix.dot", runs.toDot());
//		runs.mergePrefix();
//		IOUtils.toFile(model + "_merged.dot", runs.toDot());
//
//		PESSemantics<Integer> pes = new PESSemantics<Integer>(PORuns2PES.getPrimeEventStructure(runs, model));
//
//		SuffixMerger merger = new SuffixMerger(pes);
		
		PESSemantics<Integer> pes = getLogPES();
		SuffixMerger merger = new SuffixMerger(pes);
	}
	
	public PESSemantics<Integer> getLogPES() throws Exception {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		
		adj.put(8, 9);
		adj.put(9, 0);
		
		adj.put(0, 1);
		adj.put(0, 2);
		adj.put(1, 3);
		adj.put(3, 5);

		adj.put(2, 4);
		adj.put(4, 6);
		adj.put(4, 7);

		adj.put(5, 10);
		adj.put(10, 11);

		adj.put(6, 12);
		adj.put(12, 13);
		
		adj.put(7, 14);
		adj.put(14, 15);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		
		List<Integer> sinks = Arrays.asList(11,13,15);
		
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(8), sinks, 
					Arrays.asList("A", "B", "C", "D", "D", "E", "E", "F", "_0_", "start", "end", "_1_", "end", "_1_", "end", "_1_"), "nested");

		IOUtils.toFile("examplepn.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}

}
