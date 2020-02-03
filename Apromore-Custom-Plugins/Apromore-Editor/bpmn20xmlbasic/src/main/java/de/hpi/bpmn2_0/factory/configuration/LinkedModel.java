/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

/**
 *
 */
package de.hpi.bpmn2_0.factory.configuration;

import org.json.JSONException;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.oryxeditor.server.diagram.generic.GenericDiagram;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A LinkedModel contains the JSON representation of the diagram, custom
 * defined attributes and models linked to this model.
 *
 * @author Sven Wagner-Boysen
 */
public class LinkedModel {
    /*
      * Attributes
      */

    private Map<String, LinkedModel> linkedModels;
    private Map<String, Set<String>> metaData;
    private GenericDiagram diagram;
    private String id;


    @SuppressWarnings("unchecked")
    public LinkedModel(Map<String, Object> modelMetaData) {
        if (modelMetaData.get("modelJSON") != null && modelMetaData.get("modelJSON") instanceof String) {
            GenericDiagram diagram = null;
            try {
                diagram = BasicDiagramBuilder.parseJson((String) modelMetaData.get("modelJSON"));
            } catch (JSONException e) {
                diagram = new BasicDiagram("");
            }

            setDiagram(diagram);
        }

        if (modelMetaData.get("metaData") != null && modelMetaData.get("metaData") instanceof Map<?, ?>) {
            getMetaData().putAll((Map<? extends String, ? extends Set<String>>) modelMetaData.get("metaData"));
        }

        if (modelMetaData.get("linkedModels") != null && modelMetaData.get("linkedModels") instanceof List<?>) {
            extractLinkedModels((List<Map<String, Object>>) modelMetaData.get("linkedModels"));
        }

        if (modelMetaData.get("id") != null && modelMetaData.get("id") instanceof String) {
            setId((String) modelMetaData.get("id"));
        }
    }

    private void extractLinkedModels(List<Map<String, Object>> linkedModelsList) {
        for (Map<String, Object> linkedModelMap : linkedModelsList) {
            LinkedModel model = new LinkedModel(linkedModelMap);
            getLinkedModels().put(model.getId(), model);
        }
    }


    /* Getter & Setter */

    public Map<String, LinkedModel> getLinkedModels() {
        if (linkedModels == null) {
            linkedModels = new HashMap<String, LinkedModel>();
        }
        return linkedModels;
    }


    public Map<String, Set<String>> getMetaData() {
        if (metaData == null) {
            metaData = new HashMap<String, Set<String>>();
        }
        return metaData;
    }

    public void setDiagram(GenericDiagram diagram) {
        this.diagram = diagram;
    }

    public GenericDiagram getDiagram() {
        return diagram;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
