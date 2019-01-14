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

package org.oryxeditor.server.diagram.basic;

import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.generic.GenericDiagramBuilder;

/**
 * Simple extension of {@link GenericDiagramBuilder} to allow for easier usage without having to use generics.
 *
 * @author Philipp Maschke
 */
public class BasicDiagramBuilder extends GenericDiagramBuilder<BasicShape, BasicDiagram, BasicEdge, BasicNode> {

    /**
     * Parse the json string to the diagram model, assumes that the json is hierarchical ordered
     *
     * @param json hierarchical JSON string representing a diagram
     * @return a diagram object with all shapes as defined in json
     * @throws JSONException if the JSON string could not be parsed correctly
     */
    public static BasicDiagram parseJson(String json) throws JSONException {
        if (json == null)
            return null;
        else
            return BasicDiagramBuilder.parseJson(new JSONObject(json));
    }


    /**
     * Parse the json string to the diagram model, assumes that the json is hierarchical ordered
     *
     * @param json hierarchical JSON string representing a diagram
     * @return a diagram object with all shapes as defined in json
     * @throws JSONException if the JSON string could not be parsed correctly
     */
    public static BasicDiagram parseJson(JSONObject json) throws JSONException {
        return (new BasicDiagramBuilder()).parse(json);
    }

    /**
     * Parses the namespace of the stencilset only
     *
     * @param json JSON string representing a diagram
     * @return namespace of the diagram's stencilset
     * @throws JSONException
     */
    public static String parseStencilsetNamespace(JSONObject json) throws JSONException {
        return (new BasicDiagramBuilder()).parseStencilsetNamespaceInternal(json);
    }


    @Override
    public BasicNode createNewNode(String resourceId) {
        return new BasicNode(resourceId);
    }


    @Override
    public BasicEdge createNewEdge(String resourceId) {
        return new BasicEdge(resourceId);
    }


    @Override
    public BasicDiagram createNewDiagram(String resourceId) {
        return new BasicDiagram(resourceId);
    }
}
