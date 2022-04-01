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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * <b>Movie</b> is a sequence of ({@link Frame} recorded at a certain frame rate. 
 * In other words, it is the animation movie to be played back.
 * 
 * @see FrameRecorder
 * 
 * @author Bruce Nguyen
 *
 */
public class Movie extends ArrayList<Frame> {
	private AnimationContext animateContext;
	
	public Movie(AnimationContext animateContext, List<AnimationIndex> animationIndexes) {
		this.animateContext = animateContext;
		for (int frameIndex=0; frameIndex<animateContext.getMaxNumberOfFrames(); frameIndex++) {
            add(new Frame(frameIndex, animationIndexes));
        }
	}
	
	public AnimationContext getAnimationContext() {
		return this.animateContext;
	}
	
	/**
	 * Generate JSON for a chunk of frames identified by a starting frame index (inclusive) and a chunk size (number of frames).
	 * It uses the frame skip parameter in the {@link AnimationContext} to skip frames and thus increases the playback speed.
	 * @param startFrameIndex
	 * @param chunkSize
	 * @return JSON
	 * @throws JSONException
	 */
	public JSONArray getChunkJSON(int startFrameIndex, int chunkSize) throws JSONException {
		JSONArray json = new JSONArray();
		if (startFrameIndex < 0 || startFrameIndex >= this.size() || chunkSize <= 0) {
			return json;
		}
		
		int step = animateContext.getFrameSkip() + 1;
		for (int i=0; i < chunkSize; i++) {
		    int frameIndex = startFrameIndex + i*step;
		    frameIndex = frameIndex < this.size() ? frameIndex : this.size()-1;
		    json.put(this.get(frameIndex).getJSON());
		    if (frameIndex >= this.size()-1) break;
		}
		return json;
	}
}
