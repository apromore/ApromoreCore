package org.apromore.service.helper;

import org.apromore.graph.canonical.INode;

/**
 * @author Chathura Ekanayake
 */
public class PocketLocator {

    private INode preset;
    private INode postset;
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

    public INode getPreset() {
        return preset;
    }

    public void setPreset(INode preset) {
        this.preset = preset;
    }

    public INode getPostset() {
        return postset;
    }

    public void setPostset(INode postset) {
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
