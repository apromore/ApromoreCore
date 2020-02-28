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

/**
 *
 */
package org.apromore.service.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;


/**
 * @author Chathura C. Ekanayake
 */
public class ClusterSettings {

    private String algorithm;
    private float minLuceneSimilarity = 0.5F;
    private float filteringSizePercentage = 0.4F;
    private double maxNeighborGraphEditDistance = 0.4;
    private double gedMatrixThreshold = 0.4;
    private int minPoints = 2;
    private int minClusteringFragmentSize = 2;
    private int maxClusteringFragmentSize = 500;
    private boolean dynamicClustering = false;
    private boolean hierarchyFiltering = true;
    private boolean processBasedFiltering = false;
    private boolean clusterwideMaximalFragmentFiltering = false;
    private boolean clusterwideMaximalFragmentFilteringWithCentroids = false;
    private boolean simpleDecomposition = true;
    private boolean enableGEDMatrix = true;
    private boolean enableClusterOverlapping = true;
    private boolean enableMergingRestriction = true;
    private boolean enableNearestRelativeFiltering = true;
    private List<Integer> constrainedProcessIds = null;
    private boolean ignoreClustersWithExactClones = true;

    /**
     * When searching for the neighbourhood NBHk of k, a fragment b is excluded from NBHk, if b is included in a
     * cluster C, where any ascendant or descendant of k is a member of C.
     * <p/>
     * If this is disabled, hierarchy members filtered out in nearest relative filtering tend to create new clusters
     * with fragments included in their hierarchy clusters.
     */
    private boolean removeHierarchyClusterContainments = true;
    private boolean enableClusterWideNearestRelativeFiltering = false;

    /**
     * Fragments in this collection are not to be included in any cluster.
     * Currently this is used when we execute exact clone detection as a separate functionality, AND if we don't want
     * to mix exact clones with approximate clones. In that scenario, we give all fragments in exact clones as fragments
     * to be avoided.
     * <p/>
     * TODO: currently this is supported only by the DBSCAN clusterer.
     */
    private Collection<String> fidsToAvoid = new HashSet<String>();

    public Collection<String> getFidsToAvoid() {
        return fidsToAvoid;
    }

    private boolean dbscanClustering = true;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public boolean isIgnoreClustersWithExactClones() {
        return ignoreClustersWithExactClones;
    }

    public void setIgnoreClustersWithExactClones(boolean ignoreClustersWithExactClones) {
        this.ignoreClustersWithExactClones = ignoreClustersWithExactClones;
    }

    public List<Integer> getConstrainedProcessIds() {
        return constrainedProcessIds;
    }

    public void setConstrainedProcessIds(List<Integer> constrainedProcessIds) {
        this.constrainedProcessIds = constrainedProcessIds;
    }


    public float getMinLuceneSimilarity() {
        return minLuceneSimilarity;
    }

    public void setMinLuceneSimilarity(float minLuceneSimilarity) {
        this.minLuceneSimilarity = minLuceneSimilarity;
    }

    public float getFilteringSizePercentage() {
        return filteringSizePercentage;
    }

    public void setFilteringSizePercentage(float filteringSizePercentage) {
        this.filteringSizePercentage = filteringSizePercentage;
    }

    public double getGedMatrixThreshold() {
        return gedMatrixThreshold;
    }

    public void setGedMatrixThreshold(double gedMatrixThreshold) {
        this.gedMatrixThreshold = gedMatrixThreshold;
    }

    public double getMaxNeighborGraphEditDistance() {
        return maxNeighborGraphEditDistance;
    }

    public void setMaxNeighborGraphEditDistance(double maxNeighborGraphEditDistance) {
        this.maxNeighborGraphEditDistance = maxNeighborGraphEditDistance;
        this.gedMatrixThreshold = 2 * maxNeighborGraphEditDistance;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }

    public int getMinClusteringFragmentSize() {
        return minClusteringFragmentSize;
    }

    public void setMinClusteringFragmentSize(int minClusteringFragmentSize) {
        this.minClusteringFragmentSize = minClusteringFragmentSize;
    }

    public int getMaxClusteringFragmentSize() {
        return maxClusteringFragmentSize;
    }

    public void setMaxClusteringFragmentSize(int maxClusteringFragmentSize) {
        this.maxClusteringFragmentSize = maxClusteringFragmentSize;
    }

    public boolean isDynamicClustering() {
        return dynamicClustering;
    }

    public void setDynamicClustering(boolean dynamicClustering) {
        this.dynamicClustering = dynamicClustering;
    }

    public boolean isHierarchyFiltering() {
        return hierarchyFiltering;
    }

    public void setHierarchyFiltering(boolean hierarchyFiltering) {
        this.hierarchyFiltering = hierarchyFiltering;
    }

    public boolean isProcessBasedFiltering() {
        return processBasedFiltering;
    }

    public void setProcessBasedFiltering(boolean processBasedFiltering) {
        this.processBasedFiltering = processBasedFiltering;
    }

    public boolean isClusterwideMaximalFragmentFiltering() {
        return clusterwideMaximalFragmentFiltering;
    }

    public void setClusterwideMaximalFragmentFiltering(boolean clusterwideMaximalFragmentFiltering) {
        this.clusterwideMaximalFragmentFiltering = clusterwideMaximalFragmentFiltering;
    }

    public boolean isClusterwideMaximalFragmentFilteringWithCentroids() {
        return clusterwideMaximalFragmentFilteringWithCentroids;
    }

    public void setClusterwideMaximalFragmentFilteringWithCentroids(boolean clusterwideMaximalFragmentFilteringWithCentroids) {
        this.clusterwideMaximalFragmentFilteringWithCentroids = clusterwideMaximalFragmentFilteringWithCentroids;
    }

    public boolean isSimpleDecomposition() {
        return simpleDecomposition;
    }

    public void setSimpleDecomposition(boolean simpleDecomposition) {
        this.simpleDecomposition = simpleDecomposition;
    }

    public boolean isEnableGEDMatrix() {
        return enableGEDMatrix;
    }

    public void setEnableGEDMatrix(boolean enableGEDMatrix) {
        this.enableGEDMatrix = enableGEDMatrix;
    }

    public boolean isEnableClusterOverlapping() {
        return enableClusterOverlapping;
    }

    public void setEnableClusterOverlapping(boolean enableClusterOverlapping) {
        this.enableClusterOverlapping = enableClusterOverlapping;
    }

    public boolean isEnableMergingRestriction() {
        return enableMergingRestriction;
    }

    public void setEnableMergingRestriction(boolean enableMergingRestriction) {
        this.enableMergingRestriction = enableMergingRestriction;
    }

    public boolean isDbscanClustering() {
        return dbscanClustering;
    }

    public void setDbscanClustering(boolean dbscanClustering) {
        this.dbscanClustering = dbscanClustering;
    }

    public boolean isEnableNearestRelativeFiltering() {
        return enableNearestRelativeFiltering;
    }

    public void setEnableNearestRelativeFiltering(boolean enableNearestRelativeFiltering) {
        this.enableNearestRelativeFiltering = enableNearestRelativeFiltering;
    }

    public boolean isRemoveHierarchyClusterContainments() {
        return removeHierarchyClusterContainments;
    }

    public void setRemoveHierarchyClusterContainments(boolean removeHierarchyClusterContainments) {
        this.removeHierarchyClusterContainments = removeHierarchyClusterContainments;
    }

    public boolean isEnableClusterWideNearestRelativeFiltering() {
        return enableClusterWideNearestRelativeFiltering;
    }

    public void setEnableClusterWideNearestRelativeFiltering(boolean enableClusterWideNearestRelativeFiltering) {
        this.enableClusterWideNearestRelativeFiltering = enableClusterWideNearestRelativeFiltering;
    }
}
