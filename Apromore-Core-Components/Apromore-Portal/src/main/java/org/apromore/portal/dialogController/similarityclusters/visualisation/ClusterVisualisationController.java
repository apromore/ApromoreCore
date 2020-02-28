/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.portal.dialogController.similarityclusters.visualisation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apromore.model.ClusterSummaryType;
import org.apromore.model.FragmentData;
import org.apromore.model.PairDistanceType;
import org.apromore.portal.dialogController.BaseController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

/**
 * Controlling the visualisation window, that contains the D3 javascript.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class ClusterVisualisationController extends BaseController {

    private static final NumberFormat NUMBER_INSTANCE = NumberFormat.getNumberInstance(Locale.ENGLISH);

    static {
        NUMBER_INSTANCE.setMaximumFractionDigits(3);
    }

    public static final String CLUSTER_RESULT_ATTRIBUTE_NAME = "org.apromore.portal.clusterResult";
    public static final String PAIRWISE_FILTER_ATTRIBUTE_NAME = "org.apromore.portal.pairwiseFilter";

    private List<ClusterSummaryType> clusterResult;
    private Map<Integer, Long> indexMap;

    /**
     * Gets the current similarity cluster result set from the Session and
     * stores it, to be rendered on the index.zul file.
     *
     * @throws InterruptedException in case of Error displaying the Messagebox
     */
    @SuppressWarnings("unchecked")
    public ClusterVisualisationController() throws InterruptedException {
        super();

        Session session = Sessions.getCurrent();

        this.clusterResult = (List<ClusterSummaryType>) session.getAttribute(CLUSTER_RESULT_ATTRIBUTE_NAME);
        this.indexMap = new HashMap<>();
        this.addEventListener("onShowSelection", new EventListener() {

            @Override
            public void onEvent(final Event event) throws InterruptedException {
                Set<String> pairwiseFilter = new HashSet<>();
                pairwiseFilter.clear();
                if (event.getData() != null) {
                    Object[] idList = (Object[]) event.getData();
                    for (Object anIdList : idList) {
                        pairwiseFilter.add((anIdList).toString());
                    }
                    try {
                        Clients.evalJavaScript("clusterVisualisation.refreshData(" + writeUpdatedEdges(pairwiseFilter) + ");");
                    } catch (JSONException e) {
                        Messagebox.show("Error generating JSON " + e.getMessage());
                    }
                }
            }
        });

        if (this.clusterResult != null) {
            Map<String, String> param = new HashMap<>();
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                writeVisualisationJSON(os);
                param.put("visualisationJSON", os.toString("UTF-8"));
            } catch (JSONException e) {
                Messagebox.show("Error generating JSON " + e.getMessage());
            } catch (IOException e) {
                Messagebox.show("Error writing JSON " + e.getMessage());
            }

            Executions.getCurrent().pushArg(param);
        } else {
            Messagebox.show("The page or component you request is no longer available. This is normally caused by timeout, opening too many " +
                    "Web pages, or rebooting the server.");
        }

    }

    /**
     * Returns D3 JSON for all edges between the fragments mentioned in nodeFilter and all other nodes.
     *
     * @param nodeFilter contains ids of the fragments for which edges should be drawn.
     * @return JSON in D3 format
     * @throws JSONException
     */
    private String writeUpdatedEdges(final Set<String> nodeFilter) throws JSONException {
        JSONObject updateObject = new JSONObject();
        updateObject.put("nodes", new JSONArray());

        JSONArray edgesArray = new JSONArray();
        updateObject.put("edges", edgesArray);

        List<PairDistanceType> pairwiseMedoidDistances = getService().getPairwiseDistances(new ArrayList<Integer>(indexMap.keySet()));
        for (PairDistanceType pairDistance : pairwiseMedoidDistances) {
            if (nodeFilter.contains(Integer.toString(pairDistance.getFragmentId1())) ||
                    nodeFilter.contains(Integer.toString(pairDistance.getFragmentId2()))) {
                Integer fragment1Id = pairDistance.getFragmentId1();
                Integer fragment2Id = pairDistance.getFragmentId2();
                if (indexMap.containsKey(fragment1Id)&& indexMap.containsKey(fragment2Id)) {
                    JSONObject medoidEdgeObj = buildEdgeObject(
                            indexMap.get(fragment1Id), indexMap.get(fragment2Id), pairDistance.getDistance(), true);
                    edgesArray.put(medoidEdgeObj);
                }
            }
        }

        return updateObject.toString();
    }

    private void writeVisualisationJSON(final OutputStream os) throws JSONException, IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os, "UTF-8");
        JSONWriter jsonWriter = new JSONWriter(outputStreamWriter);
        JSONArray edgesArray = new JSONArray();

        jsonWriter.object().key("nodes").array();

        // Current index of the node
        long index = 0;

        for (ClusterSummaryType clusterInfo : clusterResult) {
            Integer clusterId = clusterInfo.getClusterId();
            Integer medoidId = clusterInfo.getMedoidId();
            writeNode(jsonWriter, clusterId, clusterInfo.getMedoidId(),
                    clusterInfo.getClusterLabel(), true, clusterInfo.getClusterSize());

            // We need this index to build the edges afterwards
            indexMap.put(medoidId, index);
            index++;

            List<FragmentData> fragments = getService().getCluster(clusterInfo.getClusterId()).getFragments();
            for (FragmentData fragmentData : fragments) {
                if (!isMedoid(medoidId, fragmentData.getFragmentId())) {

                    // TODO: MAKE SURE
                    if (fragmentData.getDistance() > 0) {
                        writeNode(jsonWriter, clusterId, fragmentData.getFragmentId(), fragmentData.getFragmentLabel(), false,
                            fragmentData.getFragmentSize());

                        // Omit all negative distances, as they are the medoids
//                      if (fragmentData.getDistance() > 0) {
                            JSONObject edgeObject = buildEdgeObject(indexMap.get(medoidId), index, fragmentData.getDistance(), false);
                            edgesArray.put(edgeObject);
//                      }
                        index++;
                    }
                }
            }

        }

        jsonWriter.endArray().key("edges").value(edgesArray);
        jsonWriter.endObject();
        outputStreamWriter.flush();
    }

    private boolean isMedoid(final Integer medoidId, final int fragmentId) {
        return medoidId.equals(fragmentId);
    }

    /**
     * Constructs a Edge JSONObject with the medoidIndex as 'target' and the
     * fragmentIndex as 'source'.
     *
     * @param medoidIndex        within the JSONArray
     * @param fragmentIndex      within the JSONArray
     * @param distanceToMedoid   between 0 and 1
     * @param isInterClusterEdge
     * @return
     * @throws JSONException
     */
    private JSONObject buildEdgeObject(final long medoidIndex, final long fragmentIndex, double distanceToMedoid, final boolean isInterClusterEdge)
            throws JSONException {
        JSONObject edgeObject = new JSONObject();
        edgeObject.put("source", fragmentIndex);
        edgeObject.put("target", medoidIndex);
        edgeObject.put("isInterClusterEdge", isInterClusterEdge);
        if (distanceToMedoid > 1.0d) {
            distanceToMedoid = 1.0d;
        }
        edgeObject.put("value", NUMBER_INSTANCE.format(distanceToMedoid * 100d));
        return edgeObject;
    }

    /**
     * Directly writes the JSONObject of a D3 node of a Fragment or a Medoid.
     *
     * @param jsonWriter
     * @param clusterId
     * @param id           of the Fragment
     * @param name         of the Fragment
     * @param isMedoid
     * @param fragmentSize
     * @throws JSONException
     */
    private void writeNode(final JSONWriter jsonWriter, final Integer clusterId, final int id, final String name, final boolean isMedoid,
            final int fragmentSize) throws JSONException {
        jsonWriter.object();
        jsonWriter.key("id");
        jsonWriter.value(id);
        jsonWriter.key("group");
        jsonWriter.value(clusterId);
        jsonWriter.key("name");
        jsonWriter.value(name);
        jsonWriter.key("size");
        jsonWriter.value(fragmentSize);
        jsonWriter.key("isMedoid");
        jsonWriter.value(isMedoid);
        jsonWriter.endObject();
    }

}
