/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2016 Reina Uba.
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

package org.apromore.similaritysearch.planarGraphMathing;

import org.apromore.similaritysearch.common.Settings;
import org.apromore.similaritysearch.common.VertexPair;
import org.apromore.similaritysearch.common.similarity.AssingmentProblem;
import org.apromore.similaritysearch.common.stemmer.SnowballStemmer;
import org.apromore.similaritysearch.graph.Graph;
import org.apromore.similaritysearch.graph.Vertex;
import org.apromore.similaritysearch.graph.Vertex.Type;

import java.util.ArrayList;

public class PlanarGraphMathing {

    ArrayList<VertexPair> nodesToVisit = new ArrayList<VertexPair>();
    static ArrayList<VertexPair> mappings = new ArrayList<VertexPair>();

    /**
     * Finds matching regions. Also adds gateways, if in one modelass, the gateway exist and in other modelass does not
     * exist. Also matches gateways of different types.
     *
     * @param g1
     * @param g2
     * @param threshold
     * @param stemmer
     * @return
     */
    public static MappingRegions findMatchWithGWAdding(Graph g1, Graph g2, double threshold, SnowballStemmer stemmer) {

        ArrayList<VertexPair> process = new ArrayList<VertexPair>();
        ArrayList<VertexPair> processed = new ArrayList<VertexPair>();

        MappingRegions map = new MappingRegions();

        if (stemmer == null) {
            stemmer = Settings.getEnglishStemmer();
        }

        ArrayList<VertexPair> mappings = AssingmentProblem.getMappingsGraph(g1, g2, threshold, stemmer);

        if (mappings == null || mappings.size() == 0) {
            return map;
        }

        VertexPair v = mappings.remove(0);

        process.clear();
        processed.clear();

        process.add(v);

        while (true) {
            ArrayList<VertexPair> mapRegion = new ArrayList<VertexPair>();
            if (process.size() == 0) {
                break;
            }

            // map parents
            while (true) {
                if (process.size() == 0) {
                    break;
                }

                VertexPair toProcess = process.get(0);

                // match parents
                ArrayList<Vertex> leftParents = removeVertices((ArrayList<Vertex>) toProcess.getLeft().getParentsList());
                ArrayList<Vertex> rightParents = removeVertices((ArrayList<Vertex>) toProcess.getRight().getParentsList());

                ArrayList<VertexPair> nodeMappings = AssingmentProblem.getMappingsVetrex(leftParents, rightParents, threshold, stemmer, 1);
                for (VertexPair vp : nodeMappings) {
                    if (!hasProcessed(processed, vp) && !hasProcessed(process, vp)) {
                        process.add(vp);
                    }
                }

                // match children
                ArrayList<Vertex> leftChildren = removeVertices((ArrayList<Vertex>) toProcess.getLeft().getChildrenList());
                ArrayList<Vertex> rightChildren = removeVertices((ArrayList<Vertex>) toProcess.getRight().getChildrenList());

                nodeMappings = AssingmentProblem.getMappingsVetrex(leftChildren, rightChildren, threshold, stemmer, 2);
                for (VertexPair vp : nodeMappings) {
                    if (!hasProcessed(processed, vp) && !hasProcessed(process, vp)) {
                        process.add(vp);
                    }
                }
                process.remove(toProcess);
                processed.add(toProcess);
                mapRegion.add(toProcess);
            }

            if (mapRegion.size() > 0) {
                map.addRegion(mapRegion);
            }

            ArrayList<VertexPair> mappingsCopy = new ArrayList<VertexPair>(mappings);

            for (VertexPair v1 : mappingsCopy) {
                // this has already processed
                if (hasProcessed(processed, v1)) {
                    mappings.remove(v1);
                } else {
                    process.add(v1);
                    break;
                }
            }
        }
        return map;
    }


    static ArrayList<Vertex> removeVertices(ArrayList<Vertex> vList) {
        ArrayList<Vertex> toReturn = new ArrayList<Vertex>();

        if (vList == null) {
            return toReturn;
        }

        for (Vertex v : vList) {
            if ((Settings.considerGateways || (!Settings.considerGateways && v.getType() != Type.gateway))
                    && (Settings.considerEvents || (!Settings.considerEvents && v.getType() != Type.event))) {
                toReturn.add(v);
            }
        }

        return toReturn;
    }

    static double calculateWeight(ArrayList<VertexPair> processedVertices) {
        double result = 0;

        for (VertexPair vp : processedVertices) {
            result += vp.getWeight();
        }
        return result;
    }

    static boolean hasProcessed(ArrayList<VertexPair> processedVertices, VertexPair vp) {

        for (VertexPair processed : processedVertices) {
            if (processed.getLeft().getID() == vp.getLeft().getID() || processed.getRight().getID() == vp.getRight().getID()) {
                return true;
            }
        }
        return false;
    }

    public static class MappingRegions {

        private ArrayList<ArrayList<VertexPair>> regions = new ArrayList<ArrayList<VertexPair>>();

        public void addRegion(ArrayList<VertexPair> region) {
            regions.add(region);
        }

        public ArrayList<ArrayList<VertexPair>> getRegions() {
            return regions;
        }
    }

}
