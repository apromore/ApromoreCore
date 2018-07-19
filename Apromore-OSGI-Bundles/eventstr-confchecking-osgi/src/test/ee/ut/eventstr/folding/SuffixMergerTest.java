package ee.ut.eventstr.folding;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.junit.Test;

import ee.ut.eventstr.PESSemantics;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;

public class SuffixMergerTest {
	private String model = "cycle10";
	private String fileNameTemplate = "logs/%s.bpmn.mxml.gz";

	@Test
	public void test() throws Exception {
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
		
		IOUtils.toFile(model + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(model + "_merged.dot", runs.toDot());

		PESSemantics<Integer> pes = new PESSemantics<Integer>(PORuns2PES.getPrimeEventStructure(runs, model));

		SuffixMerger merger = new SuffixMerger(pes);
	}
}
