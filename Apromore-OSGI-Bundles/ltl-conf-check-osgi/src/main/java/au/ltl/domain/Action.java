package au.ltl.domain;

public class Action {

	String actionType; // can be either "sync" , "add" or "del"
	String labelToAdd; //Meaningful  only if the actionType is equal to "add"
	
	
	public Action(String actionType, String labelToAdd) {
		super();
		this.actionType = actionType;
		this.labelToAdd = labelToAdd;
	}
	
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getLabelToAdd() {
		return labelToAdd;
	}
	public void setLabelToAdd(String labelToAdd) {
		this.labelToAdd = labelToAdd;
	}

	@Override
	public String toString() {
		return "Action [actionType=" + actionType + ", labelToAdd=" + labelToAdd + "]";
	}
	
	
}
