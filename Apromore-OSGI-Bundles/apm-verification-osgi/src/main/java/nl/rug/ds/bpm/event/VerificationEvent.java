package nl.rug.ds.bpm.event;

import nl.rug.ds.bpm.specification.jaxb.*;
import nl.rug.ds.bpm.verification.checker.CheckerFormula;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Created by Heerko Groefsema on 10-Apr-17.
 */
public class VerificationEvent {
    private boolean eval;
    private CheckerFormula formula;
    private Specification specification;

    public VerificationEvent(Specification specification, CheckerFormula formula, boolean eval) {
        this.formula = formula;
        this.eval = eval;
        this.specification = specification;
    }

    public String getId() {
        return formula.getSpecification().getId();
    }

    public String getType() {
        return formula.getSpecification().getType();
    }

    public String getFormulaString() {
        return formula.getOriginalFormula();
    }

    public boolean getVerificationResult() {
        return eval;
    }

    public CheckerFormula getFormula() {
        return formula;
    }

    public String getMessage() {
        Message message = formula.getSpecification().getSpecificationType().getMessage();
        return (message == null ? getFormulaString() : mapInputs((eval ? message.getHold() : message.getFail())) + ".");
    }

    public String toString() {
        return "Specification " + formula.getSpecification().getId() + (eval ? " holds" : " failed") + ": " + getMessage();
    }

    private String mapInputs(String s) {
        String r = s;

        for (Input input: formula.getSpecification().getSpecificationType().getInputs()) {
            List<InputElement> elements = formula.getSpecification().getInputElements().stream().filter(element -> element.getTarget().equals(input.getValue())).collect(Collectors.toList());

            String APBuilder = "";
            if (elements.size() == 1) {
                APBuilder = elements.get(0).getElement();
            } else if (elements.size() > 1) {
                Iterator<InputElement> inputElementIterator = elements.iterator();
                APBuilder = inputElementIterator.next().getElement();
                while (inputElementIterator.hasNext()) {
                    APBuilder = APBuilder + (input.getType().equalsIgnoreCase("and") ? " and " : " or ") + inputElementIterator.next().getElement();
                }
            }
            r = r.replaceAll(Matcher.quoteReplacement(input.getValue()), APBuilder.toString());
        }
        return r;
    }

    public String getUserFriendlyFeedback(Map<String, Group> groupMap) {
        return verbalizeEvaluation(groupMap);
    }

    public String verbalizeEvaluation(Map<String, Group> groupMap){
        String sentence = "";

        String evalString = eval ? "is" : "is not";
        String evalString2 = eval ? "a" : "no";
        String evalString3 = eval ? "never" : "can";
        String evalString4 = eval ? "are" : "are not";

        switch (specification.getType()){
            case "AlwaysResponse":
                sentence = translateGroup(specification.getInputElements().get(0).getElement(), groupMap) + " " + evalString + " always eventually followed by " + translateGroup(specification.getInputElements().get(1).getElement(), groupMap) + " --- " + specification.getType();
                break;
            case "AlwaysImmediateResponse":
                sentence = translateGroup(specification.getInputElements().get(0).getElement(),groupMap) + " " + evalString + " always directly followed by " + translateGroup(specification.getInputElements().get(1).getElement(), groupMap) + /* " (with silent \"" + specification.getInputElements().get(2).getElement() + "\")" + */" --- " + specification.getType();
                break;
            case "AlwaysImmediatePrecedence":
                sentence = translateGroup(specification.getInputElements().get(0).getElement(), groupMap) + " " + evalString + " always directly preceded by " + translateGroup(specification.getInputElements().get(1).getElement(), groupMap) + " --- " + specification.getType();
                break;
            case "ExistImmediateResponse":
                sentence = "there exists " + evalString2 + " path where " + translateGroup(specification.getInputElements().get(0).getElement(), groupMap) + " is directly followed by " + translateGroup(specification.getInputElements().get(1).getElement(), groupMap) + /*" (accounting for silent steps \"" + specification.getInputElements().get(2).getElement() + "\")" +*/ " --- " + specification.getType();
                break;
            case "ExistResponse":
                sentence = "there exists " + evalString2 + " path where " + translateGroup(specification.getInputElements().get(0).getElement(), groupMap) + " is eventually followed by " + translateGroup(specification.getInputElements().get(1).getElement(), groupMap) + " --- " + specification.getType();
                break;
            case "AlwaysConflict":
                sentence = translateGroup(specification.getInputElements().get(0).getElement(), groupMap) + " and " + translateGroup(specification.getInputElements().get(1).getElement(), groupMap) + " " +evalString3+ " occur together in the same path"  + " --- " + specification.getType();
                break;
            case "AlwaysParallel":
                sentence = "the activities in group " + translateGroup(specification.getInputElements().get(0).getElement(), groupMap) + " " + evalString4 + " concurrent" + " --- " + specification.getType();
                break;
            default:
                sentence = "Specification " + specification.getId() + " evaluated " + eval + " for " + specification.getType() + "(";
                for (InputElement e: specification.getInputElements()) {
                    sentence += translateGroup(e.getElement(), groupMap) + ", ";
                }
                sentence = sentence.substring(0, sentence.length() - 2) + ")";
        }

        return sentence;
    }

    public String translateGroup(String element, Map<String, Group> groupMap){
        if (groupMap.containsKey(element)) {
            if (specification.getType().equals("AlwaysParallel")) {
                return getGroupString(groupMap.get(element), "&") + " ";
            }
            else {
                return getGroupString(groupMap.get(element), "|") + " ";
            }
        }

        return element;
    }

    private String getGroupString(Group group, String separator) {
        String grpstr = "(";

        for (Element e: group.getElements()) {
            grpstr += e.getId() + " " + separator + " ";
        }

        grpstr = grpstr.substring(0, grpstr.length() - 2 - separator.length()) + ")";

        return grpstr;
    }
}
