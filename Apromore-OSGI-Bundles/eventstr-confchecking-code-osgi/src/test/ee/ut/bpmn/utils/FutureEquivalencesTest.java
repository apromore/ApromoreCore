package ee.ut.bpmn.utils;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.junit.Test;

import ee.ut.eventstr.FutureEquivalenceAnalyzer;
import ee.ut.eventstr.PESSemantics;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;

public class FutureEquivalencesTest {
	private String model = "cycle10";
	private String fileNameTemplate = "mxml/%s.bpmn.mxml.gz";

	@Test
	public void entryPoint() throws Exception {
		XLog log = XLogReader.openLog(String.format(fileNameTemplate, model));		
		AlphaRelations alphaRelations = new AlphaRelations(log);
		
		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
	    long time = System.nanoTime();
		PORuns runs = new PORuns();

		for (XTrace trace: log) {
			PORun porun = new PORun(alphaRelations, trace);
			runs.add(porun);
		}
		
		IOUtils.toFile("target/" + model + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile("target/" + model + "_merged.dot", runs.toDot());

		PESSemantics<Integer> pes = new PESSemantics<Integer>(PORuns2PES.getPrimeEventStructure(runs, model));
		
		new FutureEquivalenceAnalyzer<Integer>(pes).perform();
		
	    System.out.println("Overall time: " + (System.nanoTime() - time) / 1000000000.0);

	}
}
