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

package de.hpi.bpmn2_0.factory.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A configuration holds information about linked diagrams, custom defined
 * attributes and the editor version.
 *
 * @author Sven Wagner-Boysen
 */
public class Configuration {
    /* Attributes */

    private String editorVersion;
    private Map<String, Set<String>> metaData;
    private Map<String, LinkedModel> linkedModels;

    public static boolean ensureSignavioStyleDefault = false;

    /*
      * Flag to enforce a check on IDs of style 'sid-...'
      */
    public boolean ensureSignavioStyle = ensureSignavioStyleDefault;


    /* Constructors */
    public Configuration() {
        super();
    }

    @SuppressWarnings("unchecked")
    public Configuration(Map<String, Object> configuration) {
        super();

        setEditorVersion((String) configuration.get("editorVersion"));

        if (configuration.get("metaData") != null && configuration.get("metaData") instanceof Map<?, ?>) {
            getMetaData().putAll((Map<? extends String, ? extends Set<String>>) configuration.get("metaData"));
        }

        if (configuration.get("linkedModels") != null && configuration.get("linkedModels") instanceof List<?>) {
            extractLinkedModels((List<Map<String, Object>>) configuration.get("linkedModels"));
        }

    }

    private void extractLinkedModels(List<Map<String, Object>> linkedModelsList) {
        for (Map<String, Object> linkedModelMap : linkedModelsList) {
            LinkedModel model = new LinkedModel(linkedModelMap);
            getLinkedModels().put(model.getId(), model);
        }
    }

    /* Getter & Setter */

    public String getEditorVersion() {
        return editorVersion;
    }


    public void setEditorVersion(String editorVersion) {
        this.editorVersion = editorVersion;
    }


    public Map<String, Set<String>> getMetaData() {
        if (metaData == null) {
            metaData = new HashMap<String, Set<String>>();
        }
        return metaData;
    }


    public Map<String, LinkedModel> getLinkedModels() {
        if (linkedModels == null) {
            linkedModels = new HashMap<String, LinkedModel>();
        }

        return linkedModels;
    }
}
