/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package org.oryxeditor.server.diagram.basic;

/**
 * Copyright (c) 2006
 *
 * Philipp Berger, Martin Czuchra, Gero Decker, Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

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
