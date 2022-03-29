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

public class VertexObjectRef {

    public enum InputOutput {
        Input,
        Output
    }

    private boolean optional;
    private InputOutput inputOutput;
    private String objectID;
    private HashSet<String> models = new HashSet<String>();
    private Boolean consumed;

    public VertexObjectRef(boolean optional,
                           String objectID,
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

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
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

    public boolean canMerge(VertexObjectRef other) {
        return this.optional == other.optional &&
                this.consumed == other.consumed &&
                (this.inputOutput == null && other.inputOutput == null ||
                        this.inputOutput != null && other.inputOutput != null &&
                                this.inputOutput.equals(other.inputOutput));
    }
}
