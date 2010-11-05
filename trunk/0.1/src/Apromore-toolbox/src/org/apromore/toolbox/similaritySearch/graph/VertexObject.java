package org.apromore.toolbox.similaritySearch.graph;

import java.math.BigInteger;
import java.util.HashSet;


public class VertexObject {

	private String name;
	private Boolean configurable;
	private SoftHart softhard;
	private BigInteger id;
	private HashSet<String> models = new HashSet<String>();
	
	public VertexObject(BigInteger id, String name, Boolean configurable, SoftHart softhard) {
		this.name = name;
		this.configurable = configurable == null ? false : configurable;
		this.softhard = softhard;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isConfigurable() {
		return configurable;
	}
	public void setConfigurable(Boolean configurable) {
		this.configurable = configurable;
	}
	public SoftHart getSofthard() {
		return softhard;
	}
	public void setSofthard(SoftHart softhard) {
		this.softhard = softhard;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}
	public BigInteger getId() {
		return id;
	}

	public enum SoftHart {
		Soft,
		Hard,
		Other
	}
	
	public HashSet<String> getModels() {
		return models;
	}

	public void addModels(HashSet<String> labels) {
		this.models.addAll(labels);
	}

	public void addModel(String label) {
		models.add(label);
	}	
}
