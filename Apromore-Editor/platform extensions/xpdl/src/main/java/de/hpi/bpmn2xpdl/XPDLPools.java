/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Pools")
public class XPDLPools extends XMLConvertible {

    @Element("Pool")
    protected ArrayList<XPDLPool> pools;

    public void add(XPDLPool newPool) {
        initializePools();

        getPools().add(newPool);
    }

    public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
        if (getPools() != null) {
            for (XPDLPool pool : getPools()) {
                pool.setResourceIdToObject(mapping);
                String id = pool.getId();
                mapping.put(id, pool);
                pool.createAndDistributeMapping(mapping);
            }
        }
    }

    public ArrayList<XPDLPool> getPools() {
        return pools;
    }

    public void readJSONpoolsunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "poolsunknowns");
    }

    public void setPools(ArrayList<XPDLPool> pool) {
        this.pools = pool;
    }

    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
        ArrayList<XPDLPool> poolsList = getPools();
        if (poolsList != null) {
            initializeChildShapes(modelElement);

            JSONArray childShapes = modelElement.getJSONArray("childShapes");
            for (int i = 0; i < poolsList.size(); i++) {
                XPDLPool convertPool = poolsList.get(i);

                if (convertPool.getMainPool()) {
                    convertPool.writeMainPool(modelElement);
                } else {
                    JSONObject newPool = new JSONObject();
                    convertPool.write(newPool);
                    childShapes.put(newPool);
                }
            }
        }
    }

    public void writeJSONpoolsunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "poolsunknowns");
    }

    protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
        if (modelElement.optJSONArray("childShapes") == null) {
            modelElement.put("childShapes", new JSONArray());
        }
    }

    protected void initializePools() {
        if (getPools() == null) {
            setPools(new ArrayList<XPDLPool>());
        }
    }
}
