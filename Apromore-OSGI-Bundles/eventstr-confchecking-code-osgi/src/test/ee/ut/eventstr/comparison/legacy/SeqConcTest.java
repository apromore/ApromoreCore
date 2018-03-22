package ee.ut.eventstr.comparison.legacy;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.junit.Test;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.bpmn.utils.BPMN2Reader;
import ee.ut.bpmn.utils.Petrifier;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.comparison.NewOpenPartialSynchronizedProduct;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;

public class SeqConcTest {
	
	@Test
	public void test() {
		try {
			String folder1 = 
//					"models/elementary/"
//					"models/RunningExample/"
					"models/AtomicTest/"
					;
			
			String folder2 = 
					folder1
					;
			
			String process1 = 
//					"conflict"
//					"CP_LGB"
					"base"
//					"I1"
//					"I2"
//					"I3"
//					"R1"
//					"R2"
//					"O1"
//					"O2"
//					"O3"
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
					"O2"
//					"O3"
					;
			
			compare(folder1, folder2, process1, process2, MODE.ONEUNFOLDING, MODE.ESPARZA);
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void compare(String folder1, String folder2, String process1, String process2, MODE mode1, MODE mode2) throws Exception {
		PESSemantics<Integer> pessem1 = new PESSemantics<>(getUnf2PES(folder1, process1, mode1).getPES());
				
		Unfolding2PES unfpes2 = getUnf2PES(folder2, process2, mode2);
		NewUnfoldingPESSemantics<Integer> pessem2 = new NewUnfoldingPESSemantics<Integer>(unfpes2.getPES(), unfpes2);
		
		NewOpenPartialSynchronizedProduct<Integer> psp = new NewOpenPartialSynchronizedProduct<Integer>(pessem1, pessem2);
		psp.perform()
			.prune()
		;
		
		IOUtils.toFile(process1.substring(0, Integer.min(3, process1.length())) + "_" + process2.substring(0, Integer.min(3, process2.length())) + ".dot", psp.toDot());
		
		psp.verbalize(new HashMap<>());
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
		
		IOUtils.toFile(filename + "_bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		
		IOUtils.toFile(filename + "_pes.dot", pes.getPES().toDot());
		
		return pes;

	}
}
