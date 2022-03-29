/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.service.loganimation.recording;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.primitive.IntIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.tuple.primitive.IntDoublePair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.roaringbitmap.RoaringBitmap;

/**
 * A <b>Frame</b> is an animation frame to be played back. It contains a set of <b>tokens</b>. Each token is
 * identified by a modelling element, replay trace (or case). So, it is possible to query all tokens, modelling elements
 * and replay traces from a frame.<br>
 * 
 * As the number of tokens in the animation can be very large, a compressed bitmap is used to store tokens.
 * 
 * @see FrameRecorder
 * 
 * @author Bruce Nguyen
 *
 */
public class Frame {
    private int frameIndex;
    
    // One index for each log
    private List<AnimationIndex> animationIndexes;
    
    // One bitmap for each log
    // Each bitmap: a true value at index ith is a replay element on this frame, also called a token index
    private List<RoaringBitmap> replayElementMaps = new ArrayList<>();
    
    // One count map for each log
    // Each map: map from a token index to the number of tokens in a cluster that it represents
    private List<MutableIntIntMap> tokenClusterMap = new ArrayList<>();
    
    private final double TOKEN_CLUSTERING_GAP = 0.01;
    
    public Frame(int frameIndex, List<AnimationIndex> animationIndexes) {
        this.frameIndex = frameIndex;
        this.animationIndexes = animationIndexes;
        animationIndexes.forEach(animationIndex -> {
            replayElementMaps.add(new RoaringBitmap());
            tokenClusterMap.add(IntIntMaps.mutable.empty());
        });
    }
    
    public void addTokens(int logIndex, int[] tokenIndexes) {
        if (!isValidLogIndex(logIndex)) return;
        replayElementMaps.get(logIndex).add(tokenIndexes);
    }
    
    public int getIndex() {
        return this.frameIndex;
    }
    
    private boolean isValidLogIndex(int logIndex) {
        return logIndex >= 0 && logIndex < animationIndexes.size();
    }
    
    public int[] getLogIndexes() {
        return IntStream.range(0, animationIndexes.size()).toArray();
    }

    public int[] getOriginalTokens(int logIndex) {
        if (!isValidLogIndex(logIndex)) return new int[] {};
        return replayElementMaps.get(logIndex).toArray();
    }
    
    public int[] getOriginalTokensByElement(int logIndex, int elementIndex) {
        if (!isValidLogIndex(logIndex)) return new int[] {};
        return Arrays.stream(getOriginalTokens(logIndex))
                .filter(tokenIndex -> animationIndexes.get(logIndex).getElementIndex(tokenIndex) == elementIndex)
                .toArray();
    }
    
    public int[] getElementIndexes(int logIndex) {
        if (!isValidLogIndex(logIndex)) return new int[] {};
        return Arrays.stream(getOriginalTokens(logIndex))
                .map(token -> animationIndexes.get(logIndex).getElementIndex(token))
                .distinct().toArray();
    }
    
    public int[] getCaseIndexes(int logIndex) {
        if (!isValidLogIndex(logIndex)) return new int[] {};
        return Arrays.stream(getOriginalTokens(logIndex))
                .map(tokenIndex -> animationIndexes.get(logIndex).getTraceIndex(tokenIndex))
                .distinct().toArray();
    }
    
    /**
     * This is the percentage from the start of the element (0..1) based on frame indexes
     * This distance is suitable to be used as relative position of tokens on a modeling element
     */
    private double getRelativeTokenDistance(int logIndex, int token) {
        if (!isValidLogIndex(logIndex)) return 0;
        int startFrameIndex = animationIndexes.get(logIndex).getStartFrameIndex(token);
        int endFrameIndex = animationIndexes.get(logIndex).getEndFrameIndex(token);
        int maxLength = endFrameIndex - startFrameIndex;
        return (maxLength == 0) ? 0 : (double)(frameIndex - startFrameIndex)/maxLength;
    }
    
    /**
     * Get JSON representation of a frame.
     * A sample of frame JSON:
     * {
     *  index: 100,
     *  elements: [
     *      {elementIndex1: [{caseIndex1:[0.1]}, {caseIndex2:[0.2]}, {caseIndex3:[0.1]}]},
     *      {elementIndex2: [{caseIndex1:[0.2]}, {caseIndex2:[0.5]}]},
     *      {elementIndex3: [{caseIndex4:[0.1]}]}
     *  ]
     * }
     */
    public JSONObject getJSON() throws JSONException {
        JSONObject frameJSON = new JSONObject();
        frameJSON.put("index", frameIndex);
        JSONArray elementsJSON = new JSONArray();
        for (int logIndex: getLogIndexes()) {
            for (int elementIndex : getElementIndexes(logIndex)) {
                JSONArray casesJSON = new JSONArray();
                for (int token : getClustersByElement(logIndex, elementIndex)) {
                    casesJSON.put((new JSONObject()).put("0", getClusterJSON(logIndex, token))); // Case index is unused at this stage
                }
                elementsJSON.put((new JSONObject()).put(String.valueOf(elementIndex), casesJSON));
            }
        }
        frameJSON.put("elements", elementsJSON);
        return frameJSON;
    }
    
    
    
    /////////////////////////////// TOKEN CLUSTERING //////////////////////////////////////////////////
    
    
    public void clusterTokens(int logIndex) {
        if (!isValidLogIndex(logIndex)) return ;
        tokenClusterMap.get(logIndex).clear();
        for (int elementIndex : getElementIndexes(logIndex)) {
            tokenClusterMap.get(logIndex).putAll(clusterTokensOnElement(logIndex, elementIndex));
        }
    }
    
    /**
     * Cluster tokens on the same modelling element. Tokens on different modelling elements (node/arc)
     * cannot be clustered. The token count will be updated.
     * @param elementIndex
     */
    private IntIntMap clusterTokensOnElement(int logIndex, int elementIndex) {
        // Collect tokens and their distances
        MutableList<IntDoublePair> tokenDistances = Lists.mutable.empty();
        for (int token : getOriginalTokensByElement(logIndex, elementIndex)) {
            tokenDistances.add(PrimitiveTuples.pair(token, getRelativeTokenDistance(logIndex, token)));
        }
        tokenDistances.sortThisBy(pair -> pair.getTwo()); // sort by distance
        
        // Group tokens within close distances
        Set<MutableIntList> tokenClusters = new HashSet<>();
        MutableIntList tokenCluster = IntLists.mutable.empty();
        double clusterTotalDist = 0;
        for (IntDoublePair token : tokenDistances) {
            double diff = tokenCluster.isEmpty() ? 0 : Math.abs(token.getTwo() - clusterTotalDist/tokenCluster.size());
            if (diff <= TOKEN_CLUSTERING_GAP) {
                tokenCluster.add(token.getOne());
                clusterTotalDist += token.getTwo();
            }
            else {
                tokenClusters.add(tokenCluster);
                tokenCluster = IntLists.mutable.of(token.getOne());
                clusterTotalDist = token.getTwo();
            }
            if (token == tokenDistances.getLast()) tokenClusters.add(tokenCluster);
        }
        
        // Take the last token as representative token for each cluster
        // Because it will keep the cluster in the same or next position, not go backward from one frame to the next
        MutableIntIntMap clusteredTokens = IntIntMaps.mutable.empty();
        tokenClusters.forEach(cluster -> clusteredTokens.put(cluster.getLast(), cluster.size()));
        
        return clusteredTokens.toImmutable();
    }
    
    // Return IndexOutOfBound if token clustering is not executed
    public int[] getClusters(int logIndex) {
        if (!isValidLogIndex(logIndex)) return new int[] {};
        return tokenClusterMap.get(logIndex).keySet().toArray();
    }
    
    // Return IndexOutOfBound if token clustering is not executed
    public int[] getClustersByElement(int logIndex, int elementIndex) {
        if (!isValidLogIndex(logIndex)) return new int[] {};
        return Arrays.stream(getClusters(logIndex))
                .filter(token -> animationIndexes.get(logIndex).getElementIndex(token) == elementIndex)
                .toArray();
    }
    
    // Return IndexOutOfBound if token clustering is not executed
    public int getClusterSize(int logIndex, int token) {
        if (!isValidLogIndex(logIndex)) return 0;
        return tokenClusterMap.get(logIndex).containsKey(token) ? tokenClusterMap.get(logIndex).get(token) : 0;
    }
    
    // A cluster distance is equal to its representative token distance
    public double getClusterDistance(int logIndex, int token) {
        if (!isValidLogIndex(logIndex)) return 0;
        return getRelativeTokenDistance(logIndex, token);
    }
    
    public JSONArray getClusterJSON(int logIndex, int token) throws JSONException {
        JSONArray attJSON = new JSONArray();
        DecimalFormat df = new DecimalFormat("#.###");
        attJSON.put(logIndex);
        attJSON.put(df.format(getClusterDistance(logIndex, token)));
        attJSON.put(this.getClusterSize(logIndex, token));
        return attJSON;
    }
    
}
