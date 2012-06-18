package org.apromore.service.helper;

import org.jbpt.hypergraph.abs.IVertex;

/**
 * @author Chathura Ekanayake
 */
public class PocketLocator {

	private IVertex preset;
	private IVertex postset;
	private String presetLabel;
	private String postsetLabel;
	
	
	public PocketLocator() {
		preset = null;
		presetLabel = null;
		postset = null;
		postsetLabel = null;
		
	}
	
	public boolean equivalent(PocketLocator pl) {
		if (preset.getId().equals(pl.getPreset().getId()) && postset.getId().equals(pl.getPostset().getId())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean matches(PocketLocator pl) {
		if (presetLabel == null && pl.getPresetLabel() != null) {
			return false;
		}
		if (postsetLabel == null && pl.getPostsetLabel() != null) {
			return false;
		}
		
		if (((presetLabel == null && pl.getPresetLabel() == null) || presetLabel.equals(pl.getPresetLabel())) 
				&& ((postsetLabel == null && pl.getPostsetLabel() == null) || postsetLabel.equals(pl.getPostsetLabel()))) {
			return true;
		} else {
			return false;
		}
	}

	public IVertex getPreset() {
		return preset;
	}
	
	public void setPreset(IVertex preset) {
		this.preset = preset;
	}
	
	public IVertex getPostset() {
		return postset;
	}
	
	public void setPostset(IVertex postset) {
		this.postset = postset;
	}
	
	public String getPresetLabel() {
		return presetLabel;
	}
	
	public void setPresetLabel(String presetLabel) {
		this.presetLabel = presetLabel;
	}
	
	public String getPostsetLabel() {
		return postsetLabel;
	}
	
	public void setPostsetLabel(String postsetLabel) {
		this.postsetLabel = postsetLabel;
	}
}
