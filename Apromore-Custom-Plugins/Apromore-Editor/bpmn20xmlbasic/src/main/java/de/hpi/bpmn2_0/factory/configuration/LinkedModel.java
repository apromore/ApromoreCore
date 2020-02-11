
/**
 *
 */
package de.hpi.bpmn2_0.factory.configuration;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
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
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

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
