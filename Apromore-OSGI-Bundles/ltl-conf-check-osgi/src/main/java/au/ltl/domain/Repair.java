package au.ltl.domain;

public class Repair {
	
	String repairType; // can be either "del" or "add"
	String labelToAdd; //Meaningful  only if the actionType is equal to "add"
	
	public Repair(String repairType, String labelToAdd) {
		super();
		this.repairType = repairType;
		this.labelToAdd = labelToAdd;
	}
	
	public String getRepairType() {
		return repairType;
	}
	public void setRepairType(String repairType) {
		this.repairType = repairType;
	}
	public String getLabelToAdd() {
		return labelToAdd;
	}
	public void setLabelToAdd(String labelToAdd) {
		this.labelToAdd = labelToAdd;
	}

	@Override
	public String toString() {
		return "Repair [repairType=" + repairType + ", labelToAdd=" + labelToAdd + "]";
	}
	
	
}
