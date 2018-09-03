package ee.ut.bpmn.utils;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.junit.Test;

import ee.ut.eventstr.freq.FrequencyAwarePrimeEventStructure;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.fpes.PORuns2FPES;
import ee.ut.mining.log.poruns.pes.PORuns2PES;

public class LogReaderTest {
	private String model = "Road_Traffic_Fine_Management_Process";
	private String fileNameTemplate = "logs/%s.xes.gz";

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
		
//		IOUtils.toFile(model + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
//		IOUtils.toFile(model + "_merged.dot", runs.toDot());

		FrequencyAwarePrimeEventStructure<Integer> fpes = PORuns2FPES.getPrimeEventStructure(runs, model);
		
		IOUtils.toFile(model + "_fpes.dot", fpes.toDot());
		
	    System.out.println("Overall time: " + (System.nanoTime() - time) / 1000000000.0);

	}
}
