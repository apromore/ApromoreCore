package org.apromore.toolbox.similaritySearch.graph;

import java.util.HashSet;

public class VertexResourceRef {

	private boolean optional;
	private String resourceID;
	private String qualifier;
	private HashSet<String> models = new HashSet<String>();
	
	public VertexResourceRef(boolean optional, String resourceID,
			String qualifier, HashSet<String> models) {
		this.optional = optional;
		this.resourceID = resourceID;
		this.qualifier = qualifier;
		this.models = models;
	}
	
	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public String getresourceID() {
		return resourceID;
	}

	public void setresourceID(String resourceID) {
		this.resourceID = resourceID;
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

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getQualifier() {
		return qualifier;
	}
	
	public boolean canMerge(VertexResourceRef other) {
		return this.optional == other.optional &&
			   (this.qualifier == null && other.qualifier == null ||
					   this.qualifier != null && other.qualifier != null && this.qualifier.equals(other.qualifier));
	}
}
