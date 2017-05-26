package nl.rug.ds.bpm.variability;

import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.pnml.PNMLReader;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.JDOMException;

import nl.rug.ds.bpm.eventStructure.CombinedEventStructure;

public class VariabilitySpecification {

	private CombinedEventStructure ces;

	public VariabilitySpecification(String folder, List<String> filenames) {
		this(folder, filenames, "");
	}

	public VariabilitySpecification(String folder, List<String> filenames, String silentPrefix) {
		ces = new CombinedEventStructure();
		try {
			for (String fn: filenames) {
				ces.addPES(getUnfoldingPES(folder, fn, silentPrefix));
			}
			ces.findMutualRelations();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VariabilitySpecification(List<String> fullfilenames, String silentPrefix) {
		ces = new CombinedEventStructure();
		try {
			for (String fn: fullfilenames) {
				ces.addPES(getUnfoldingPES(fn, silentPrefix));
			}
			ces.findMutualRelations();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VariabilitySpecification(PetriNet[] nets, String silentPrefix) {
		ces = new CombinedEventStructure();
		try {
			for (PetriNet net: nets) {
				ces.addPES(getUnfoldingPES(net, silentPrefix));
			}
			ces.findMutualRelations();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Unfolding2PES getUnfoldingPES(String folder, String filename, String silentPrefix) throws JDOMException, IOException {
		return getUnfoldingPES(folder + filename, silentPrefix);
	}

	private Unfolding2PES getUnfoldingPES(String fullfilename, String silentPrefix) throws JDOMException, IOException {
		PetriNet net = PNMLReader.parse(new File(fullfilename));
		return getUnfoldingPES(net, silentPrefix);
	}

	private Unfolding2PES getUnfoldingPES(PetriNet net, String silentPrefix) {
		PrintStream blackhole = new PrintStream(new ByteArrayOutputStream());
		PrintStream stdout = System.out;
		System.setOut(blackhole);

		Set<String> labels = new HashSet<>();
		for (Transition t: net.getTransitions()) {
			if ((!t.getName().startsWith(silentPrefix)) || (silentPrefix.equals(""))) {
				labels.add(t.getName());
			}
		}

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA, new HashSet<>());
		unfolder.computeUnfolding();

        HashMap<String, String> originalLabels = new HashMap<>();
        for(Transition ts : net.getTransitions())
            originalLabels.put(ts.getName(), ts.getName());

		Unfolding2PES pes = new Unfolding2PES(unfolder, labels, originalLabels);

		System.setOut(stdout);

		return pes;
	}

	public CombinedEventStructure getCES() {
		return ces;
	}

	public List<String> getAllLabels() {
		return ces.getAllLabels();
	}

//	======================================================================================================
//	CTL specs
//	======================================================================================================

	// set of immediate response CTL specifications
	public List<String> getViresp() {
		List<String> ctls = new ArrayList<String>();
		Map<Integer, Set<Integer>> resp = new HashMap<Integer, Set<Integer>>();

		String spec;

		BitSet syncevents = ces.getSyncEvents();
		BitSet syncpreds = new BitSet();

		int source;
		for (BitSet r: ces.getDirectCausals()) {
			source = r.nextSetBit(0);

			if (syncevents.get(r.previousSetBit(r.length())))
				syncpreds.set(source);

			if (!resp.containsKey(source))
				resp.put(source, new HashSet<Integer>());

			resp.get(source).add(r.nextSetBit(source + 1));
		}

		for (BitSet r: ces.getInvDirectCausals()) {
			source = r.previousSetBit(r.length() + 1);

			if (syncevents.get(r.nextSetBit(0)))
				syncpreds.set(source);

			if (!resp.containsKey(source))
				resp.put(source, new HashSet<Integer>());

			resp.get(source).add(r.previousSetBit(source - 1));
		}

		for (int e1: resp.keySet()) {
			if (!ces.getSilents().contains(e1)) {
				spec = "";
				for (int e2: resp.get(e1)) {
					if (!ces.getSilents().contains(e2))
						spec += "{" + ces.getLabel(e2) + "}" + " | ";
				}

				if (spec.length() > 0) {
					spec = spec.substring(0, spec.length() - 3);
					if (resp.get(e1).size() > 1) spec = "(" + spec + ")";

					if (syncpreds.get(e1)) {
						spec = "AG(" + "{" + ces.getLabel(e1) + "}" + " -> AF " + spec + ")";
					}
					else {
						spec = "AG(" + "{" + ces.getLabel(e1) + "}" + " -> A[(" + "{" + ces.getLabel(e1) + "}" + " | silent) U " + spec + "])";
					}

					if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) ctls.add(spec);
				}
			}
		}

		return ctls;
	}

	// set of precedence CTL specifications
	public List<String> getViprec() {
		List<String> ctls = new ArrayList<String>();
		Map<Integer, Set<Integer>> prec = new HashMap<Integer, Set<Integer>>();
		String spec;

		int source;
		for (BitSet r: ces.getDirectCausals()) {
			source = r.previousSetBit(r.length());

			if (!prec.containsKey(source))
				prec.put(source, new HashSet<Integer>());

			prec.get(source).add(r.previousSetBit(source - 1));
		}

		for (BitSet r: ces.getInvDirectCausals()) {
			source = r.nextSetBit(0);

			if (!prec.containsKey(source))
				prec.put(source, new HashSet<Integer>());

			prec.get(source).add(r.nextSetBit(source + 1));
		}



		for (int e2: prec.keySet()) {
			if (!ces.getSilents().contains(e2)) {
				spec = "";
				for (int e1: prec.get(e2)) {
					if (!ces.getSilents().contains(e1))
						spec += "{" + ces.getLabel(e1) + "}" + " | ";
				}

				if (spec.length() > 0) {
					spec = spec.substring(0, spec.length() - 3);
					if (prec.get(e2).size() > 1) spec = "(" + spec + ")";
					spec = "!E[!" + spec + " U " + "{" + ces.getLabel(e2) + "}" + "]";

					if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) ctls.add(spec);
				}
			}
		}

		return ctls;
	}

	// set of exists immediate response ctls specifications
	public List<String> getVeiresp() {
		Set<String> ctls = new HashSet<String>();
		ctls.addAll(getVeirespNrml());
		ctls.addAll(getVeirespInv());
		return new ArrayList<String>(ctls);
	}

	private List<String> getVeirespNrml() {
		List<String> ctls = new ArrayList<String>();
		Set<BitSet> dc = ces.getImmediateResponses();
		String spec;

		int source, target;
		for (BitSet r: dc) {
			source = r.nextSetBit(0);
			target = r.nextSetBit(source + 1);

			if ((!ces.getSilents().contains(source)) && (!ces.getSilents().contains(target)) &&
					(ces.occursInAllPESs(target))) {
				spec = "AG(" + "{" + ces.getLabel(source) + "}" + " -> E[(" + "{" + ces.getLabel(source) + "}" + " | silent) U " +
					"{" + ces.getLabel(target) + "}" + "])";
				if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) ctls.add(spec);
			}
		}

		return ctls;
	}

	private List<String> getVeirespInv() {
		List<String> ctls = new ArrayList<String>();
		Set<BitSet> dc = ces.getInvImmediateResponses();
		String spec;

		int source, target;
		for (BitSet r: dc) {
			source = r.previousSetBit(r.length());
			target = r.previousSetBit(source - 1);

			if ((!ces.getSilents().contains(source)) && (!ces.getSilents().contains(target)) &&
					(ces.occursInAllPESs(target))) {
				spec = "AG(" + "{" + ces.getLabel(source) + "}" + " -> E[(" + "{" + ces.getLabel(source) + "}" + " | silent) U " +
					"{" + ces.getLabel(target) + "}" + "])";
				if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) ctls.add(spec);
			}
		}

		return ctls;
	}

	//  set of exists response ctls specifications
	public List<String> getVeresp() {
		List<String> ctls = new ArrayList<String>();
		ctls.addAll(getVerespNrml());
		ctls.addAll(getVerespInv());
		return ctls;
	}

	private List<String> getVerespNrml() {
		List<String> ctls = new ArrayList<String>();
		Set<BitSet> tc = ces.getMutualExistCausals();
		String spec;

		int source, target;
		for (BitSet r: tc) {
			source = r.nextSetBit(0);
			target = r.nextSetBit(source + 1);

			if ((!ces.getSilents().contains(source)) && (!ces.getSilents().contains(target)) &&
					(ces.occursInAllPESs(target))) {
				spec = "AG(" + "{" + ces.getLabel(source) + "}" + " -> EF " + "{" + ces.getLabel(target) + "}" + ")";
				if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) ctls.add(spec);
			}
		}

		return ctls;
	}

	private List<String> getVerespInv() {
		List<String> ctls = new ArrayList<String>();
		Set<BitSet> tc = ces.getMutualInvExistCausals();
		String spec;

		int source, target;
		for (BitSet r: tc) {
			source = r.previousSetBit(r.length());
			target = r.previousSetBit(source - 1);

			if ((!ces.getSilents().contains(source)) && (!ces.getSilents().contains(target)) &&
					(ces.occursInAllPESs(target))) {
				spec = "AG(" + "{" + ces.getLabel(source) + "}" + " -> EF " + "{" + ces.getLabel(target) + "}" + ")";
				if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) ctls.add(spec);
			}
		}

		return ctls;
	}

	// set of exclusive execution, or conflict, ctls specifications
	public List<String> getVconf() {
		List<String> ctls = new ArrayList<String>();
		Set<BitSet> cf = ces.getMutualConflicts();
		String spec;

		int source, target;
		for (BitSet r: cf) {
			source = r.nextSetBit(0);
			target = r.nextSetBit(source + 1);

			if ((!ces.getSilents().contains(source)) && (!ces.getSilents().contains(target))) {
				spec = "AG(" + "{" + ces.getLabel(source) + "}" + " -> AG !" + "{" + ces.getLabel(target) + "}" + ")";
				if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) ctls.add(spec);

				spec = "AG(" + "{" + ces.getLabel(target) + "}" + " -> AG !" + "{" + ces.getLabel(source) + "}" + ")";
				if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) ctls.add(spec);
			}
		}

		return ctls;
	}

	// set of parallel execution ctls specifications
	public List<String> getVpar() {
		List<String> ctls = new ArrayList<String>();
		Set<BitSet> cc = ces.getMutualConcurrents();

		String spec;

		int source, target;
		for (BitSet r: cc) {
			source = r.nextSetBit(0);
			target = r.nextSetBit(source + 1);

			if ((!ces.getSilents().contains(source)) && (!ces.getSilents().contains(target))) {
				spec = "EF(" + "{" + ces.getLabel(source) + "}" + " & " + "{" + ces.getLabel(target) + "}" + ")";
				if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) ctls.add(spec);
			}
		}

		return ctls;
	}

	// set of reduced exists response ctls specifications
	public List<String> getVerespReduced(Boolean removeDirectResponses) {
		List<String> ctls = getVeresp();
		ctls.removeAll(getVerespReduction(removeDirectResponses));
		return ctls;
	}

	private List<String> getVerespReduction(Boolean removeDirectResponses) {
		List<String> reduction = new ArrayList<String>();
		String spec;

		int source, target;

		// Create set Rn normal
		Set<BitSet> nrn = new HashSet<BitSet>();
		for (BitSet r: ces.getMutualDirectCausals()) {
			source = r.nextSetBit(0);
			target = r.nextSetBit(source + 1);

			if ((!ces.getSilents().contains(source)) && (!ces.getSilents().contains(target)) &&
					(ces.occursInAllPESs(target))) {
				nrn.add(r);
			}
		}

		// Create set Rn inv
		Set<BitSet> irn = new HashSet<BitSet>();
		for (BitSet r: ces.getMutualInvDirectCausals()) {
			source = r.previousSetBit(r.length());
			target = r.previousSetBit(source - 1);

			if ((!ces.getSilents().contains(source)) && (!ces.getSilents().contains(target)) &&
					(ces.occursInAllPESs(target))) {
				irn.add(r);
			}
		}

		// Create set Rf normal
		Set<BitSet> nrf = new HashSet<BitSet>();
		for (BitSet r: ces.getMutualExistCausals()) {
			source = r.nextSetBit(0);
			target = r.nextSetBit(source + 1);

			if ((!ces.getSilents().contains(source)) && (!ces.getSilents().contains(target)) &&
					(ces.occursInAllPESs(target))) {
				nrf.add(r);
			}
		}

		// Create set Rf inv
		Set<BitSet> irf = new HashSet<BitSet>();
		for (BitSet r: ces.getMutualInvExistCausals()) {
			source = r.previousSetBit(r.length());
			target = r.previousSetBit(source - 1);

			if ((!ces.getSilents().contains(source)) && (!ces.getSilents().contains(target)) &&
					(ces.occursInAllPESs(target))) {
				irf.add(r);
			}
		}

		// Create Rf U rn normal
		Set<BitSet> nru = new HashSet<BitSet>();
		nru.addAll(nrf);
		nru.addAll(nrn);

		// Create Rf U rn inv
		Set<BitSet> iru = new HashSet<BitSet>();
		iru.addAll(irf);
		iru.addAll(irn);

		// Create reduction set
		BitSet r1 = new BitSet();
		BitSet r2 = new BitSet();
		Boolean b1, b2;

		Set<BitSet> rf = new HashSet<BitSet>();
		rf.addAll(nrf);
		rf.addAll(irf);

		for (BitSet r: nrf) {
			source = r.nextSetBit(0);
			target = r.nextSetBit(source + 1);

			// check for e"
			for (int i = 0; i < ces.getAllLabels().size(); i++) {
				if ((i != source) && (i != target)) {
					r1.clear();
					r2.clear();
					r1.set(source);
					r1.set(i);
					r2.set(i);
					r2.set(target);

					if (source < i)
						b1 = nru.contains(r1);
					else
						b1 = iru.contains(r1);

					if (target < i)
						b2 = iru.contains(r2);
					else
						b2 = nru.contains(r2);

					if (b1 && b2) {
						spec = "AG(" + "{" + ces.getLabel(source) + "}" + " -> EF " + "{" + ces.getLabel(target) + "}" + ")";
						if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) reduction.add(spec);
						i = ces.getAllLabels().size(); // make sure search for e" is ended
					}
				}
			}
		}

		for (BitSet r: irf) {
			source = r.previousSetBit(r.length());
			target = r.previousSetBit(source - 1);

			// check for e"
			for (int i = 0; i < ces.getAllLabels().size(); i++) {
				if ((i != source) && (i != target)) {
					r1.clear();
					r2.clear();
					r1.set(source);
					r1.set(i);
					r2.set(i);
					r2.set(target);

					if (source < i)
						b1 = nru.contains(r1);
					else
						b1 = iru.contains(r1);

					if (target < i)
						b2 = iru.contains(r2);
					else
						b2 = nru.contains(r2);

					if (b1 && b2) {
						spec = "AG(" + "{" + ces.getLabel(source) + "}" + " -> EF " + "{" + ces.getLabel(target) + "}" + ")";
						if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) reduction.add(spec);
						i = ces.getAllLabels().size(); // make sure search for e" is ended
					}
				}
			}
		}

		if (removeDirectResponses) {
			for (BitSet r: nrn) {
				source = r.nextSetBit(0);
				target = r.nextSetBit(source + 1);

				spec = "AG(" + "{" + ces.getLabel(source) + "}" + " -> EF " + "{" + ces.getLabel(target) + "}" + ")";
				if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) reduction.add(spec);
			}

			for (BitSet r: irn) {
				source = r.previousSetBit(r.length());
				target = r.previousSetBit(source - 1);

				spec = "AG(" + "{" + ces.getLabel(source) + "}" + " -> EF " + "{" + ces.getLabel(target) + "}" + ")";
				if ((!spec.contains("_0_")) && (!spec.contains("_1_"))) reduction.add(spec);
			}
		}

		return reduction;
	}
	
}
