package ee.ut.bpmn.utils;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import utilities.FES.ComparatorFreq;
import utilities.FES.FreqEventStructure;
import utilities.FES.ReducedPrimeEventStructure;
import utilities.FES.Unfolding2ReducedPES;
import ee.ut.bpmn.BPMNProcess;
import ee.ut.eventstr.freq.FrequencyAwarePrimeEventStructure;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.fpes.PORuns2FPES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;

public class FPESExample1Test {
	private String model = 
			//"cycle10.bpmn"
			//"simpleloop_short"
			//"motorlog_short"
			//"SyncLoopModel1a"
			//"RealExampleGenerated"
			//"unbalancedDiagnosisNO"
			"Windscreen-GIOManual"
			;
	private String fileNameTemplate = 
			//"E:/JavaProjects/workspace/eventstr-confcheck/eventstr-confchecking/logs/%s.mxml.gz"
			//"E:/JavaProjects/workspace/eventstr-confcheck/eventstr-confchecking/models/RunningExample/%s.mxml"//.gz"
			//"E:/Documents/NICTA/Experimental Logs/Additional logs/%s.mxml"
			//"E:/Documents/NICTA/Experimental Logs/BPI-DCC/%s.mxml"
			"E:/Documents/NICTA/Experimental Logs/SClogs/%s.xes"
			;

	@Test
	public void test() throws Exception {
		//ReducedPrimeEventStructure<Integer> rpes = getUnfoldingPESExample().getRPES();
		
		//printBRMatrix(rpes);
		
		//FreqEventStructure festest = new FreqEventStructure(rpes, rpes.getLabels().toArray(new String[0])); //printBRMatrix(getUnfoldingPESExample().getRPES());
		
		//printFreqMatrix(festest);
		
		FrequencyAwarePrimeEventStructure<Integer> pes1 = getLogPESExample();
		
		System.out.println("PES1 created");
		
		model = 
				//"simpleloop_short2"
				//"motorlog_long"
				//"SyncLoopModel1"
				//"RealExampleGenerated2"
				//"unbalancedDiagnosisYES"
				"Windscreen-GIOSkin"
				;
		FrequencyAwarePrimeEventStructure<Integer> pes2 = getLogPESExample();
		//changeFPES(pes2);
		System.out.println("PES2 created");
		 
		FreqEventStructure fes1 = new FreqEventStructure(pes1.getFreqMatrix(), pes1.getLabels().toArray(new String[0]));
		FreqEventStructure fes2 = new FreqEventStructure(pes2.getFreqMatrix(), pes2.getLabels().toArray(new String[0]));
		
		System.out.println("FES1 and 2 created");
		
		//printMatrix(pes1);
		//System.out.println();
		//printFreqMatrix(pes2);
		
		ComparatorFreq diff = new ComparatorFreq(fes1, fes2);
		diff.setZeroThreshold(0.05); //combined values should be at least 5%
		diff.setDiffThreshold(0.2);    //0.1 means that there should be at least 10% differences 
										   //in the reported frequencies, in order to be counted as a difference
		diff.combineValues();
			
		System.out.println(diff.getDifferences(true)); 
		System.out.println("Comparison done");
		
		IOUtils.toFile("fpes.dot", pes1.toDot());
	}
	
	public FrequencyAwarePrimeEventStructure<Integer> getLogPESExample() throws Exception {
		XLog log = XLogReader.openLog(String.format(fileNameTemplate, model));		
		AlphaRelations alphaRelations = new AlphaRelations(log);
		
		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
		PORuns runs = new PORuns();
		
		for (XTrace trace: log) {
			PORun porun = new PORun(alphaRelations, trace);
			runs.add(porun);
		}
		
		IOUtils.toFile(model + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(model + "_merged.dot", runs.toDot());

		return PORuns2FPES.getPrimeEventStructure(runs, model);
	}
	
	public Unfolding2ReducedPES getUnfoldingPESExample() throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("eventstr-confchecking/models/AtomicLoopTest/baseLoop.bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		Set<String> labels = new HashSet<>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ONEUNFOLDING);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2ReducedPES pes = new Unfolding2ReducedPES(unfolder.getSys(), unfolder.getBP(), labels);
		IOUtils.toFile("bpmnpes.dot", pes.getPES().toDot());
		
		return pes;
	}
	
	public void changeFPES(FrequencyAwarePrimeEventStructure<Integer> pes) {
		for (int i = 0; i < pes.getFreqMatrix().length; i++) {
			for (int j = 0; j < pes.getFreqMatrix().length; j++) {
				if ((pes.getFreqMatrix()[i][j] > 0) && (pes.getFreqMatrix()[i][j] < 1)) {
					if (pes.getFreqMatrix()[i][j] > 0.5) {
						pes.getFreqMatrix()[i][j] -= 0.3;
					}
					else {
						pes.getFreqMatrix()[i][j] += 0.3;
					}
				}
			}
		}
	}
	
	public void printNet(Unfolder_PetriNet uf) {
		for (int i = 0; i < uf.getBP().getBranchingProcess().allEvents.size(); i++) {
			//uf.getBP().getBranchingProcess().allEvents.get(i).post
			System.out.print(uf.getSys().properNames[uf.getBP().getBranchingProcess().allEvents.get(i).id] + " ");
		}
		System.out.println();
	}
	
	public void printBRMatrix(ReducedPrimeEventStructure<Integer> pes) {
		System.out.println(pes.getLabels());
		for (int i = 0; i < pes.getBRelMatrix().length; i++) {
			for (int j = 0; j < pes.getBRelMatrix().length; j++) {
				System.out.print(pes.getBRelMatrix()[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public void printFreqMatrix(FrequencyAwarePrimeEventStructure<Integer> pes) {
		System.out.println(pes.getLabels());
		for (int i = 0; i < pes.getFreqMatrix().length; i++) {
			for (int j = 0; j < pes.getFreqMatrix().length; j++) {
				System.out.print(pes.getFreqMatrix()[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public void printFreqMatrix(FreqEventStructure fes) {
		for (int i = 0; i < fes.getLabels().length; i++) {
			System.out.print(fes.getLabels()[i] + " ");	
		}
		System.out.println();
		
		for (int i = 0; i < fes.getMatrix().length; i++) {
			for (int j = 0; j < fes.getMatrix().length; j++) {
				System.out.print(fes.getMatrix()[i][j] + " ");
			}
			System.out.println();
		}
	}
}
