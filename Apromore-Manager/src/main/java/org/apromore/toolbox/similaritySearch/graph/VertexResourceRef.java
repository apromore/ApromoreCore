package org.apromore.toolbox.similaritySearch.graph;

import java.util.HashSet;

public class VertexResourceRef {

    private String resourceID;
    private String qualifier;
    private HashSet<String> models = new HashSet<String>();

    public VertexResourceRef(String resourceID, String qualifier, HashSet<String> models) {
        this.resourceID = resourceID;
        this.qualifier = qualifier;
        this.models = models;
    }


    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
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
        return (this.qualifier == null && other.qualifier == null ||
                        this.qualifier != null && other.qualifier != null && this.qualifier.equals(other.qualifier));
    }
}
