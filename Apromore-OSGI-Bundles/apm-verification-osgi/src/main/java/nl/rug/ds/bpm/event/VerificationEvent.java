package nl.rug.ds.bpm.event;

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
