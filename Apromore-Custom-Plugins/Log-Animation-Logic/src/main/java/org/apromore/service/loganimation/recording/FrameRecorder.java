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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>Frame Recorder</b> is used to record an animation movie from an <b>AnimationLog</b><br>
 * 
 * AnimationLog is the result of alignment between a log and a model. It is a collection of <b>ReplayTrace</b>.
 * Each ReplayTrace is a sequence of replay elements. Each replay element is an instance of a modelling element
 * on a model (<b>TraceNode</b> as nodes and <b>SequenceFlow</b> as arcs) with a starting time and ending time.<br>
 * 
 * Visually, a simple AnimationLog with three ReplayTrace can be seen as follows:<br>
 * Each segment |------------| is a replay element (node or arc on the model) marked by a starting and ending time.<br>
 * 
 * AnimationLog with three replay traces:
 * #1: |---------------|--------|--------------------------|
 * #2:      |-------|----------------|---------------------------|-----------|
 * #3:  |------------------|--------------------|----------------------|
 * 
 * The FrameRecorder will scan the AnimationLog over time to generate animation movie ({@link Movie}). Each frame is
 * a snapshot (a cut) of the AnimationLog at a point in time, i.e. visually, it cuts the animation log vertically at a point in time.
 * Each cut results in a frame which contain some replay elements. Depending on the resolution of the animation, each
 * cut can be done every few milliseconds in time. As a result, a <b>Movie</b> contains a large collection of frames which can be
 * played back on the browser. Each frame in the movie is given a sequential index, e.g. from 1 to 36,000.<br>
 * 
 * Each Frame contains a (different) number of replay elements. Each occurrence of one replay element in a frame is called
 * a <b>token</b>, visualized as a dot in the animation. Thus, each token is identified by: a modelling element, a replay trace,
 * and a frame index.<br>
 * 
 * Given a cut at a point in time and an animation log, a question is what replay elements will be cut and thus included into the frame.
 * It is inefficient to check all the replay traces and in each replay trace check each replay element to see whether its starting and
 * ending time contain the cut.<br>
 * 
 * <b>AnimationIndex</b> is an indexed data structure used to support this operation. An AnimationIndex is created by
 * scanning the animation log and creating indexes from each replay element to the modelling elements, replay traces and the frame
 * indexes. Once an AnimationIndex has been created from an animation log, given a cut in time, it is possible to query what replay
 * elements (a segment above) are cut.<br>
 * 
 * As an AnimationIndex is a collection of intervals (i.e. segments), a binary interval tree is created to support
 * fast finding the cut intervals. Note that each interval above is marked by a starting and ending time. These points in time can be
 * converted (approximately) to a frame index. Thus, each interval is marked by starting and ending frame indexes. Each cut in time correponds
 * to a frame index, thus, given a frame index, it is to search the interval tree to find the cut intervals containing that index. As each
 * cut interval corresponds to a set of modelling elements and traces, it it possible to identify information of all tokens in the frame.
 * 
 * @author Bruce Nguyen
 *
 */
public class FrameRecorder {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrameRecorder.class.getCanonicalName());
	public static Movie record(List<AnimationIndex> animationIndexes, AnimationContext animationContext) {
		Movie movie = new Movie(animationContext, animationIndexes);
		movie.parallelStream().forEach(frame -> {
		    for (int logIndex=0; logIndex < animationIndexes.size(); logIndex++) {
		        int[] tokenIndexes = animationIndexes.get(logIndex).getReplayElementIndexesByFrame(frame.getIndex());
	            frame.addTokens(logIndex, tokenIndexes);
		    }
		});
		
		long timer = System.currentTimeMillis();
		movie.parallelStream().forEach(frame -> {
		    for (int logIndex=0; logIndex < animationIndexes.size(); logIndex++) {
		        frame.clusterTokens(logIndex);
		    }
		});
		LOGGER.debug("Clustering tokens: " + (System.currentTimeMillis() - timer)/1000 + " seconds.");
		
		return movie;
	}

}
