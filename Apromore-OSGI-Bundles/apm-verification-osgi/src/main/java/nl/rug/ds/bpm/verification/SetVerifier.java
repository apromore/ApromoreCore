package nl.rug.ds.bpm.verification;

import nl.rug.ds.bpm.exception.CheckerException;
import nl.rug.ds.bpm.exception.ConverterException;
import nl.rug.ds.bpm.log.LogEvent;
import nl.rug.ds.bpm.log.Logger;
import nl.rug.ds.bpm.specification.jaxb.*;
import nl.rug.ds.bpm.verification.checker.Checker;
import nl.rug.ds.bpm.verification.comparator.StringComparator;
import nl.rug.ds.bpm.verification.converter.KripkeConverter;
import nl.rug.ds.bpm.verification.map.GroupMap;
import nl.rug.ds.bpm.verification.map.IDMap;
import nl.rug.ds.bpm.verification.model.kripke.Kripke;
import nl.rug.ds.bpm.verification.model.kripke.State;
import nl.rug.ds.bpm.verification.optimizer.propositionOptimizer.PropositionOptimizer;
import nl.rug.ds.bpm.verification.optimizer.stutterOptimizer.StutterOptimizer;
import nl.rug.ds.bpm.verification.stepper.Stepper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Created by Heerko Groefsema on 07-Apr-17.
 */
public class SetVerifier {
	private Kripke kripke;
	private Stepper stepper;
	private IDMap specIdMap;
	private GroupMap groupMap;
	private BPMSpecification specification;
	private SpecificationSet specificationSet;
	private List<Specification> specifications;
	private List<Condition> conditions;

	public SetVerifier(Stepper stepper, BPMSpecification specification, SpecificationSet specificationSet) {
		this.stepper = stepper;
		this.specification = specification;
		this.specificationSet = specificationSet;

		specifications = specificationSet.getSpecifications();
		conditions = specificationSet.getConditions();

		Set<String> conds = new HashSet<>();
		for (Condition condition : conditions)
			conds.add(condition.getCondition());
		stepper.setConditions(conds);

		Logger.log("Loading specification set", LogEvent.INFO);

		Logger.log("Conditions: ", LogEvent.VERBOSE);
		for(Condition condition: conditions)
			Logger.log("\t" + condition.getCondition(), LogEvent.VERBOSE);

		specIdMap = getIdMap();
		groupMap = getGroupMap(specIdMap);
	}

	public void buildKripke(boolean reduce) throws ConverterException {
		KripkeConverter converter = new KripkeConverter(stepper, specIdMap);

		Logger.log("Calculating Kripke structure", LogEvent.INFO);
		long t0 = System.currentTimeMillis();
		kripke = converter.convert();
		//		System.out.println(kripke);
		long t1 = System.currentTimeMillis();

		Logger.log("Calculated Kripke structure with " + kripke.stats() + " in " + (t1 - t0) + " ms", LogEvent.INFO);
		if (Logger.getLogLevel() <= LogEvent.DEBUG)
			Logger.log("\n" + kripke.toString(), LogEvent.DEBUG);

		Logger.log("Reducing Kripke structure", LogEvent.INFO);
		Logger.log("Removing unused atomic propositions", LogEvent.VERBOSE);
		Set<String> unusedAP = new HashSet<>(kripke.getAtomicPropositions());
		TreeSet<String> unknownAP = new TreeSet<>(new StringComparator());

		unusedAP.removeAll(specIdMap.getAPKeys());

		unknownAP.addAll(specIdMap.getAPKeys());
		unknownAP.removeAll(kripke.getAtomicPropositions());

		if (reduce) {
			PropositionOptimizer propositionOptimizer = new PropositionOptimizer(kripke, unusedAP);
			Logger.log("\n" + propositionOptimizer.toString(true), LogEvent.VERBOSE);

			Logger.log("Reducing state space", LogEvent.VERBOSE);
			t0 = System.currentTimeMillis();
			StutterOptimizer stutterOptimizer = new StutterOptimizer(kripke);
			Logger.log("Partitioning states into stutter blocks", LogEvent.VERBOSE);
			//stutterOptimizer.linearPreProcess();
			stutterOptimizer.treeSearchPreProcess();
			stutterOptimizer.optimize();
			t1 = System.currentTimeMillis();

			Logger.log("Reduced Kripke structure to " + kripke.stats() + " in " + (t1 - t0) + " ms", LogEvent.INFO);
			if (Logger.getLogLevel() <= LogEvent.DEBUG) {
				Logger.log("\n" + stutterOptimizer.toString(), LogEvent.DEBUG);
				Logger.log("\n" + kripke.toString(), LogEvent.DEBUG);
			}
		}

		//Add ghost state with unknown AP for checker safety
		State ghost = new State("ghost", unknownAP);
		ghost.addNext(ghost);
		ghost.addPrevious(ghost);

		kripke.addState(ghost);
	}

	public void verify(Checker checker) throws CheckerException {
		Logger.log("Collecting specifications", LogEvent.INFO);
		for (Specification specification: specifications)
			for (Formula formula: specification.getSpecificationType().getFormulas())
				checker.addFormula(formula, specification, specIdMap, groupMap);

		Logger.log("Generating model checker input", LogEvent.VERBOSE);
		checker.createModel(kripke);

		if (Logger.getLogLevel() <= LogEvent.DEBUG)
			Logger.log("\n" + checker.getInputChecker(), LogEvent.DEBUG);

		Logger.log("Calling Model Checker", LogEvent.INFO);
		checker.checkModel();
		if(!checker.getOutputChecker().isEmpty())
			throw new CheckerException("Model checker error\n" + checker.getOutputChecker());
	}

	private IDMap getIdMap() {
		IDMap idMap = new IDMap();

		for (Specification s: specificationSet.getSpecifications())
			for (InputElement inputElement: s.getInputElements()) {
				idMap.addID(inputElement.getElement());
				Logger.log("Mapping " + inputElement.getElement() + " to " + idMap.getAP(inputElement.getElement()), LogEvent.VERBOSE);
				//inputElement.setElement(idMap.getAP(inputElement.getElement()));
			}

		for (Group group: specification.getGroups()) {
			//group.setId(idMap.getAP(group.getId()));
			for (Element element : group.getElements()) {
				idMap.addID(element.getId());
				Logger.log("Mapping " + element.getId() + " to " + idMap.getAP(element.getId()), LogEvent.VERBOSE);
				//element.setId(idMap.getAP(element.getId()));
			}
		}

		return idMap;
	}

	public GroupMap getGroupMap(IDMap idMap) {
		GroupMap groupMap = new GroupMap();

		for (Group group: specification.getGroups()) {
			idMap.addID(group.getId());
			groupMap.addGroup(idMap.getAP(group.getId()));
			Logger.log("New group " + group.getId() + " as " + idMap.getAP(group.getId()), LogEvent.VERBOSE);
			for (Element element: group.getElements()) {
				groupMap.addToGroup(idMap.getAP(group.getId()), idMap.getAP(element.getId()));
				Logger.log("\t " + element.getId(), LogEvent.VERBOSE);
			}
		}
		return groupMap;
	}
}
