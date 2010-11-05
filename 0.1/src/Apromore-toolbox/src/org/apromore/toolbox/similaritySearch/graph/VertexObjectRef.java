package org.apromore.toolbox.similaritySearch.graph;

import java.math.BigInteger;
import java.util.HashSet;

public class VertexObjectRef {
	
	public enum InputOutput {
		Input,
		Output
	}
	
	private boolean optional;
	private InputOutput inputOutput;
	private BigInteger objectID;
	private HashSet<String> models = new HashSet<String>();
	private Boolean consumed;

	public VertexObjectRef(boolean optional, 
			BigInteger objectID,
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

	public BigInteger getObjectID() {
		return objectID;
	}

	public void setObjectID(BigInteger objectID) {
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

}
