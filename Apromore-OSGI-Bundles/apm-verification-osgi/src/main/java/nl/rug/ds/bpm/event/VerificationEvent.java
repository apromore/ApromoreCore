package nl.rug.ds.bpm.event;

import nl.rug.ds.bpm.specification.jaxb.InputElement;
import nl.rug.ds.bpm.specification.jaxb.Specification;

/**
 * Created by Heerko Groefsema on 10-Apr-17.
 */
public class VerificationEvent {
	private boolean eval;
	private String formula;
	private Specification specification;
	
	public VerificationEvent(Specification specification, String formula, boolean eval) {
		this.specification = specification;
		this.formula = formula;
		this.eval = eval;
	}

	public String getUserFriendlyFeedback() {
		return verbalizeEvaluation();
	}

    public String verbalizeEvaluation(){
        String sentence = "";

        String evalString = eval ? "is" : "is not";
        String evalString2 = eval ? "a" : "no";
        String evalString3 = eval ? "never" : "can";
        String evalString4 = eval ? "are" : "are not";

        switch (specification.getType()){
            case "AlwaysResponse":
                sentence = specification.getInputElements().get(0).getElement() + " " + evalString + " always eventually followed by " + specification.getInputElements().get(1).getElement() + " --- " + specification.getType();
                break;
            case "AlwaysImmediateResponse":
                sentence = specification.getInputElements().get(0).getElement() + " " + evalString + " always directly followed by " + specification.getInputElements().get(1).getElement() + /* " (with silent \"" + specification.getInputElements().get(2).getElement() + "\")" + */" --- " + specification.getType();
                break;
            case "AlwaysImmediatePrecedence":
                sentence = specification.getInputElements().get(0).getElement() + " " + evalString + " always directly preceded by " + specification.getInputElements().get(1).getElement() + " --- " + specification.getType();
                break;
            case "ExistImmediateResponse":
                sentence = "there exists " + evalString2 + " path where " + specification.getInputElements().get(0).getElement() + " is directly followed by " + specification.getInputElements().get(1).getElement() + /*" (accounting for silent steps \"" + specification.getInputElements().get(2).getElement() + "\")" +*/ " --- " + specification.getType();
                break;
            case "ExistResponse":
                sentence = "there exists " + evalString2 + " path where " + specification.getInputElements().get(0).getElement() + " is eventually followed by " + specification.getInputElements().get(1).getElement() + " --- " + specification.getType();
                break;
            case "AlwaysConflict":
                sentence = specification.getInputElements().get(0).getElement() + " and " + specification.getInputElements().get(0).getElement() + " " +evalString3+ " occur together in the same path"  + " --- " + specification.getType();
                break;
            case "AlwaysParallel":
                sentence = "the activities in group " + specification.getInputElements().get(0).getElement() + " " + evalString4 + " concurrent" + " --- " + specification.getType();
                break;
            default:
                sentence = "Specification " + specification.getId() + " evaluated " + eval + " for " + specification.getType() + "(";
                for (InputElement e: specification.getInputElements()) {
                    sentence += e.getElement() + ", ";
                }
                sentence = sentence.substring(0, sentence.length() - 2) + ")";
        }

        return sentence;
    }
	
	public String getId() {
		return specification.getId();
	}
	
	public String getType() {
		return specification.getType();
	}
	
	public String getFormula() { return formula; }
	
	public boolean getVerificationResult() {
		return eval;
	}
	
	public Specification getSpecification() {
		return specification;
	}
	
	public String toString() {
		return "Specification " + specification.getId() + " evaluated " + eval + " for " + formula;
	}
}
