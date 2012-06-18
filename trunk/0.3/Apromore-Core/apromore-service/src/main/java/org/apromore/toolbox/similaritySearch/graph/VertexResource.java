package org.apromore.toolbox.similaritySearch.graph;

import java.util.HashSet;

public class VertexResource {
	
	public enum Type {
		Human,
		NonHuman,
		Other
	}
	
	private String id;
	private Type type;
	private String name;
	private boolean configurable;
	private HashSet<String> models = new HashSet<String>();;

	public VertexResource(String id, String name,
			Boolean configurable, Type type) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.configurable = configurable == null ? false : configurable;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public void setType(Type type) {
		this.type = type;
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
	public void setConfigurable(boolean configurable) {
		this.configurable = configurable;
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
	
	public boolean canMerge(VertexResource other) {
		return this.name != null && other.name != null && 
					this.name.trim().toLowerCase().replaceAll("\n", " ").replaceAll("\r", "").replaceAll("\\s+", " ").equals(other.name.trim().toLowerCase().replaceAll("\n", " ").replaceAll("\r", "").replaceAll("\\s+", " ")) 
				&& (this.type == null && other.type == null || 
						this.type != null && other.type != null && this.type.equals(other.type));
	}
}
