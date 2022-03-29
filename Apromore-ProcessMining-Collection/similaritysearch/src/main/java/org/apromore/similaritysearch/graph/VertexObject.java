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
