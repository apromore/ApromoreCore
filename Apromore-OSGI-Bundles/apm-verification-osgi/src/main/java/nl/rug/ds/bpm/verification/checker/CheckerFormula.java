package nl.rug.ds.bpm.verification.checker;

import nl.rug.ds.bpm.specification.jaxb.Message;
import nl.rug.ds.bpm.specification.jaxb.Formula;
import nl.rug.ds.bpm.specification.jaxb.Input;
import nl.rug.ds.bpm.specification.jaxb.InputElement;
import nl.rug.ds.bpm.specification.jaxb.Specification;
import nl.rug.ds.bpm.verification.map.GroupMap;
import nl.rug.ds.bpm.verification.map.IDMap;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Created by Heerko Groefsema on 09-Jun-17.
 */
public abstract class CheckerFormula {
	protected IDMap idMap;
	protected GroupMap groupMap;
	protected Formula formula;
	protected Specification specification;

	public CheckerFormula(Formula formula, Specification specification, IDMap idMap, GroupMap groupMap) {
		this.formula = formula;
		this.specification = specification;
		this.idMap = idMap;
		this.groupMap = groupMap;
	}

	public Formula getFormula() {
		return formula;
	}

	public Specification getSpecification() {
		return specification;
	}

	public boolean equals(String outputFormula) {
		return getCheckerFormula().equals(outputFormula);
	}

	public String getCheckerFormula() {
		String mappedFormula = formula.getFormula();

		for (Input input: specification.getSpecificationType().getInputs()) {
			List<InputElement> elements = specification.getInputElements().stream().filter(element -> element.getTarget().equals(input.getValue())).collect(Collectors.toList());

			String APBuilder = "";
			if(elements.size() == 0) {
				APBuilder = "true";
			}
			else if(elements.size() == 1) {
				String mapID = idMap.getAP(elements.get(0).getElement());
				if(groupMap.keySet().contains(mapID))
					mapID = groupMap.toString(mapID);
				APBuilder = mapID;
			}
			else {
				Iterator<InputElement> inputElementIterator = elements.iterator();
				String mapID = idMap.getAP(inputElementIterator.next().getElement());
				if(groupMap.keySet().contains(mapID))
					mapID = groupMap.toString(mapID);
				APBuilder = mapID;
				while (inputElementIterator.hasNext()) {
					mapID = idMap.getAP(inputElementIterator.next().getElement());
					if(groupMap.keySet().contains(mapID))
						mapID = groupMap.toString(mapID);
					APBuilder = "(" + APBuilder + (input.getType().equalsIgnoreCase("and") ? " & " : " | ") + mapID + ")";
				}
			}
			mappedFormula = mappedFormula.replaceAll(Matcher.quoteReplacement(input.getValue()), APBuilder.toString());
		}
		return mappedFormula;
	}

	public String getOriginalFormula() {
		String mappedFormula = formula.getFormula();

		for (Input input: specification.getSpecificationType().getInputs()) {
			List<InputElement> elements = specification.getInputElements().stream().filter(element -> element.getTarget().equals(input.getValue())).collect(Collectors.toList());

			String APBuilder = "";
			if (elements.size() == 0) {
				APBuilder = "true";
			} else if (elements.size() == 1) {
				APBuilder = elements.get(0).getElement();
			} else {
				Iterator<InputElement> inputElementIterator = elements.iterator();
				APBuilder = inputElementIterator.next().getElement();
				;
				while (inputElementIterator.hasNext()) {
					APBuilder = "(" + APBuilder + (input.getType().equalsIgnoreCase("and") ? " & " : " | ") + inputElementIterator.next().getElement() + ")";
				}
			}
			mappedFormula = mappedFormula.replaceAll(Matcher.quoteReplacement(input.getValue()), APBuilder.toString());
		}

		return mappedFormula;
	}
}
