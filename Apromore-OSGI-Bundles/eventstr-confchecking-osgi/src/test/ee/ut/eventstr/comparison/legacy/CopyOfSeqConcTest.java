package ee.ut.eventstr.comparison.legacy;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.bpmn.utils.BPMN2Reader;
import ee.ut.bpmn.utils.Petrifier;
import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.eventstr.comparison.DiffVerbalizer;
import ee.ut.eventstr.comparison.NewOpenPartialSynchronizedProduct;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;

public class CopyOfSeqConcTest {
	
	@Test
	public void test() {
		try {
			String folder1 = 
					"models/elementary/"
//					"models/RunningExample/"
//					"models/AtomicTest/"
					;
			
			String folder2 = 
					folder1
					;
			
			String process1 = 
//					"conflict"
//					"CP_LGB"
//					"base"
//					"I1"
//					"I2"
//					"I3"
//					"R1"
//					"R2"
//					"O1"
//					"O2"
//					"O3"
					"conc"
					;
			
			String process2 = 
//					"remove"
//					"CP"
//					"base"
//					"I1"
//					"I2"
//					"I3"
//					"R1"
//					"R2"
//					"O1"
//					"O2"
//					"O3"
					"seq"
					;
			
			compare(folder1, folder2, process1, process2, MODE.ONEUNFOLDING, MODE.ESPARZA);
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void compare(String folder1, String folder2, String process1, String process2, MODE mode1, MODE mode2) throws Exception {
		PrimeEventStructure<Integer> pes1 = getUnf2PES(folder1, process1, mode1).getPES();
		PESSemantics<Integer> fullpessem1 = new PESSemantics<Integer>(pes1);
		Unfolding2PES unfpes2 = getUnf2PES(folder2, process2, mode2);
		NewUnfoldingPESSemantics<Integer> pessem2 = new NewUnfoldingPESSemantics<Integer>(unfpes2.getPES(), unfpes2);
		
		DiffVerbalizer<Integer> verbalizer = new DiffVerbalizer<>(fullpessem1, pessem2);
				
		for (int sink: pes1.getSinks()) {
			SinglePORunPESSemantics<Integer> pessem1 = new SinglePORunPESSemantics<>(pes1, sink);		
			PrunedOpenPartialSynchronizedProduct<Integer> psp = new PrunedOpenPartialSynchronizedProduct<Integer>(pessem1, pessem2);
			psp.perform();
			verbalizer.addPSP(psp.getOperationSequence());
			IOUtils.toFile(process1.substring(0, Integer.min(3, process1.length())) + "_" + process2.substring(0, Integer.min(3, process2.length())) + ".dot", psp.toDot());		
		}
		verbalizer.verbalize();
	}
	
	public Unfolding2PES getUnf2PES(String folder, String filename, MODE mode) throws Exception {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File(folder + filename + ".bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		
		Set<String> labels = new HashSet<String>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		IOUtils.toFile(filename + "_net.dot", net.toDot());
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, mode);
		unfolder.computeUnfolding();
				
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
//		IOUtils.toFile(filename + "_bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		
		IOUtils.toFile(filename + "_pes.dot", pes.getPES().toDot());
		
		return pes;

	}
}
