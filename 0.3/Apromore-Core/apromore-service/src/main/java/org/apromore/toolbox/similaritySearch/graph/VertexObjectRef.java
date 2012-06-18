package org.apromore.toolbox.similaritySearch.graph;

import java.util.HashSet;

public class VertexObjectRef {
	
	public enum InputOutput {
		Input,
		Output
	}
	
	private boolean optional;
	private InputOutput inputOutput;
	private String objectID;
	private HashSet<String> models = new HashSet<String>();
	private Boolean consumed;

	public VertexObjectRef(boolean optional,
                           String objectID,
			Boolean consumed,
			InputOutput io,
			HashSet<String> models) {
		this.optional = optional;
		this.objectID = objectID;
		this.models = models;
		this.consumed = consumed;
		this.inputOutput = io;
	}

	public boolean isOptional() {
		return optional;
	}
	
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public HashSet<String> getLabels() {
		return models;
	}

	public HashSet<String> getModels() {
		return models;
	}

	public void addModels(HashSet<String> models) {
		this.models.addAll(models);
	}
	
	public void addModel(String model) {
		models.add(model);
	}
	
	public InputOutput getInputOutput() {
		return inputOutput;
	}

	public void setInputOutput(InputOutput inputOutput) {
		this.inputOutput = inputOutput;
	}

	public Boolean getConsumed() {
		return consumed;
	}

	public void setConsumed(Boolean consumed) {
		this.consumed = consumed;
	}

	public boolean canMerge(VertexObjectRef other) {
		return this.optional == other.optional &&
			   this.consumed == other.consumed &&
			   (this.inputOutput == null && other.inputOutput == null ||
					   this.inputOutput != null && other.inputOutput != null && 
					   this.inputOutput.equals(other.inputOutput));
	}
}
