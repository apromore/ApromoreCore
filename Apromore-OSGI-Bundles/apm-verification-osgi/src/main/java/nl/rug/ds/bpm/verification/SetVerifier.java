package nl.rug.ds.bpm.verification;

import nl.rug.ds.bpm.event.VerificationLogEvent;
import nl.rug.ds.bpm.specification.jaxb.*;
import nl.rug.ds.bpm.verification.comparator.StringComparator;
import nl.rug.ds.bpm.verification.model.kripke.State;
import nl.rug.ds.bpm.verification.stepper.Stepper;
import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.verification.map.GroupMap;
import nl.rug.ds.bpm.verification.map.IDMap;
import nl.rug.ds.bpm.verification.formula.NuSMVFormula;
import nl.rug.ds.bpm.verification.checker.NuSMVChecker;
import nl.rug.ds.bpm.verification.converter.KripkeConverter;
import nl.rug.ds.bpm.verification.optimizer.propositionOptimizer.PropositionOptimizer;
import nl.rug.ds.bpm.verification.optimizer.stutterOptimizer.StutterOptimizer;
import nl.rug.ds.bpm.verification.model.kripke.Kripke;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Created by Heerko Groefsema on 07-Apr-17.
 */
public class SetVerifier {
	private Kripke kripke;
	private EventHandler eventHandler;
	private Stepper stepper;
	private IDMap specIdMap;
	private GroupMap groupMap;
	private List<NuSMVFormula> formulas;
	private BPMSpecification specification;
	private SpecificationSet specificationSet;
	private List<Specification> specifications;
	private List<Condition> conditions;

	public SetVerifier(EventHandler eventHandler, Stepper stepper, BPMSpecification specification, SpecificationSet specificationSet) {
		this.stepper = stepper;
		this.eventHandler = eventHandler;
		this.specification = specification;
		this.specificationSet = specificationSet;

		specifications = specificationSet.getSpecifications();
		conditions = specificationSet.getConditions();
		formulas = new ArrayList<>();

		eventHandler.logInfo("Loading specification set");

		eventHandler.logVerbose("Conditions: ");
		for(Condition condition: conditions)
			eventHandler.logVerbose("\t" + condition.getCondition());

		specIdMap = getIdMap();
		groupMap = getGroupMap(specIdMap);

		eventHandler.logInfo("Collecting specifications");
		mapFormulas();
		for (NuSMVFormula formula: formulas)
			eventHandler.logVerbose("\t" + formula.getFormula());
	}

	public void buildKripke(boolean reduce) {
		KripkeConverter converter = new KripkeConverter(eventHandler, stepper, conditions, specIdMap);

		eventHandler.logInfo("Calculating Kripke structure");
		long t0 = System.currentTimeMillis();
		kripke = converter.convert();
		long t1 = System.currentTimeMillis();

		eventHandler.logInfo("Calculated Kripke structure with " +kripke.stats() + " in " + (t1 - t0) + " ms");
		if(EventHandler.getLogLevel() <= VerificationLogEvent.DEBUG)
			eventHandler.logDebug("\n" + kripke.toString());

		eventHandler.logInfo("Reducing Kripke structure");
		eventHandler.logVerbose("Removing unused atomic propositions");
		Set<String> unusedAP = new HashSet<>(kripke.getAtomicPropositions());
		TreeSet<String> unknownAP = new TreeSet<>(new StringComparator());

		unusedAP.removeAll(specIdMap.getAPKeys());

		unknownAP.addAll(specIdMap.getAPKeys());
		unknownAP.removeAll(kripke.getAtomicPropositions());

		if(reduce) {
			PropositionOptimizer propositionOptimizer = new PropositionOptimizer(kripke, unusedAP);
			eventHandler.logVerbose("\n" + propositionOptimizer.toString(true));

			eventHandler.logVerbose("Reducing state space");
			t0 = System.currentTimeMillis();
			StutterOptimizer stutterOptimizer = new StutterOptimizer(eventHandler, kripke);
			eventHandler.logVerbose("Partitioning states into stutter blocks");
			//stutterOptimizer.linearPreProcess();
			stutterOptimizer.treeSearchPreProcess();
			stutterOptimizer.optimize();
			t1 = System.currentTimeMillis();

			eventHandler.logInfo("Reduced Kripke structure to " + kripke.stats() + " in " + (t1 - t0) + " ms");
			if (EventHandler.getLogLevel() <= VerificationLogEvent.DEBUG) {
				eventHandler.logDebug("\n" + stutterOptimizer.toString());
				eventHandler.logDebug("\n" + kripke.toString());
			}
		}

		//Add ghost state with unknown AP for checker safety
		State ghost = new State("ghost", unknownAP);
		ghost.addNext(ghost);
		ghost.addPrevious(ghost);

		kripke.addState(ghost);
	}

	public void verify(File nusmv2) {
		eventHandler.logInfo("Calling Model Checker");
		NuSMVChecker nuSMVChecker = new NuSMVChecker(eventHandler, nusmv2, kripke, formulas);

		eventHandler.logVerbose("Generating model checker input");
		nuSMVChecker.createInputData();

		if(EventHandler.getLogLevel() <= VerificationLogEvent.DEBUG)
			eventHandler.logDebug("\n" + nuSMVChecker.getInputChecker());

		List<String> resultLines = nuSMVChecker.callModelChecker();
		if(!nuSMVChecker.getOutputChecker().isEmpty())
			eventHandler.logCritical("Model checker error\n" + nuSMVChecker.getOutputChecker());


		eventHandler.logInfo("Collecting results");
		for (String result: resultLines) {
			String formula = result;
			boolean eval = false;
			if (formula.contains("is false")) {
				formula = formula.replace("is false", "");
			} else {
				formula = formula.replace("is true", "");
				eval = true;
			}

			NuSMVFormula nuSMVFormula = null;
			boolean found = false;
			Iterator<NuSMVFormula> nuSMVFormulaIterator = formulas.iterator();
			while (nuSMVFormulaIterator.hasNext() && !found) {
				NuSMVFormula f = nuSMVFormulaIterator.next();
				if(f.equals(formula)) {
					found = true;
					nuSMVFormula = f;
				}
			}

			if(!found) {
				for (String key: specIdMap.getAPKeys())
					formula = formula.replaceAll(Matcher.quoteReplacement(key), specIdMap.getID(key));
				if(eval)
					eventHandler.logWarning("Failed to map " + formula + " to original specification while it evaluated true");
				else
					eventHandler.logError("Failed to map " + formula + " to original specification while it evaluated FALSE");
			}
			else {
				String mappedFormula = nuSMVFormula.getFormula();
				for (String key: specIdMap.getAPKeys())
					mappedFormula = mappedFormula.replaceAll(Matcher.quoteReplacement(key), specIdMap.getID(key));

				eventHandler.fireEvent(nuSMVFormula.getSpecification(), mappedFormula, eval);
				if(eval)
					eventHandler.logInfo("Specification " + nuSMVFormula.getSpecification().getId() + " evaluated true for " + mappedFormula);
				else
					eventHandler.logError("Specification " + nuSMVFormula.getSpecification().getId() + " evaluated FALSE for " + mappedFormula);
				formulas.remove(nuSMVFormula);
			}
		}
	}

	private void mapFormulas() {
		for (Specification specification: specifications) {
			for(Formula formula: specification.getSpecificationType().getFormulas()) {
				String mappedFormula = formula.getFormula();

				for (Input input: specification.getSpecificationType().getInputs()) {
					List<InputElement> elements = specification.getInputElements().stream().filter(element -> element.getTarget().equals(input.getValue())).collect(Collectors.toList());

					String APBuilder = "";
					if(elements.size() == 0) {
						APBuilder = "true";
					}
					else if(elements.size() == 1) {
						String mapID = specIdMap.getAP(elements.get(0).getElement());
						if(groupMap.keySet().contains(mapID))
							mapID = groupMap.toString(mapID);
						APBuilder = mapID;
					}
					else {
						Iterator<InputElement> inputElementIterator = elements.iterator();
						String mapID = specIdMap.getAP(inputElementIterator.next().getElement());
						if(groupMap.keySet().contains(mapID))
							mapID = groupMap.toString(mapID);
						APBuilder = mapID;
						while (inputElementIterator.hasNext()) {
							mapID = specIdMap.getAP(inputElementIterator.next().getElement());
							if(groupMap.keySet().contains(mapID))
								mapID = groupMap.toString(mapID);
							APBuilder = "(" + APBuilder + (input.getType().equalsIgnoreCase("and") ? " & " : " | ") + mapID + ")";
						}
					}
					mappedFormula = mappedFormula.replaceAll(Matcher.quoteReplacement(input.getValue()), APBuilder.toString());
				}
				mappedFormula = formula.getLanguage() + " " + mappedFormula;
				formulas.add(new NuSMVFormula(mappedFormula, specification));
			}
		}
	}

	private IDMap getIdMap() {
		IDMap idMap = new IDMap();

		for (Specification s: specificationSet.getSpecifications())
			for (InputElement inputElement: s.getInputElements()) {
				idMap.addID(inputElement.getElement());
				eventHandler.logVerbose("Mapping " + inputElement.getElement() + " to " + idMap.getAP(inputElement.getElement()));
				//inputElement.setElement(idMap.getAP(inputElement.getElement()));
			}

		for (Group group: specification.getGroups()) {
			//group.setId(idMap.getAP(group.getId()));
			for (Element element : group.getElements()) {
				idMap.addID(element.getId());
				eventHandler.logVerbose("Mapping " + element.getId() + " to " + idMap.getAP(element.getId()));
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
			eventHandler.logVerbose("New group " + group.getId() + " as " + idMap.getAP(group.getId()));
			for (Element element: group.getElements()) {
				groupMap.addToGroup(idMap.getAP(group.getId()), idMap.getAP(element.getId()));
				eventHandler.logVerbose("\t " + element.getId());
			}
		}
		return groupMap;
	}
}