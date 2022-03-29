/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2016 Technical University of Eindhoven, Reina Uba.
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.similaritysearch.graph;

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
