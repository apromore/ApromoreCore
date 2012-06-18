package org.apromore.toolbox.similaritySearch.graph;

import java.util.HashSet;


public class VertexObject {

	private String name;
	private Boolean configurable;
	private SoftHart softhard;
	private String id;
	private HashSet<String> models = new HashSet<String>();
	
	public VertexObject(String id, String name, Boolean configurable, SoftHart softhard) {
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

	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
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
		for (String l : labels) {
			addModel(l);
		}
	}

	public void addModel(String label) {
		if (!models.contains(label)) {
			models.add(label);
		}
	}		
	
	public boolean canMerge(VertexObject other) {
		return this.name != null && other.name != null 
					&& this.name.trim().toLowerCase().replaceAll("\n", " ").replaceAll("\r", "").replaceAll("\\s+", " ").equals(other.name.trim().toLowerCase().replaceAll("\n", " ").replaceAll("\r", "").replaceAll("\\s+", " ")) 
				&& (this.softhard == null && other.softhard == null || 
						this.softhard != null && other.softhard != null && this.softhard.equals(other.softhard));
	}
}
