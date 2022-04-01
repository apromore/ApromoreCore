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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.apromore.service.loganimation.AnimationResult;
import org.apromore.service.loganimation.modelmapping.OldBpmnModelMapping;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MovieTest extends TestDataSetup {
    
    @Test
    void test_getChunkJSON_OneLog() throws Exception {
        AnimationResult result = this.animate_OneTraceAndCompleteEvents_BPMNDiagram();
        AnimationContext animationContext = new AnimationContext(result.getAnimationLogs(), 60, 600);
        ModelMapping modelMapping = new OldBpmnModelMapping(result.getModel());
        AnimationIndex animationIndex = new AnimationIndex(result.getAnimationLogs().get(0), modelMapping, animationContext);
        Movie movie = FrameRecorder.record(Arrays.asList(animationIndex), animationContext);
        
        assertEquals(36000, movie.size());
        
        JSONArray firstChunk = movie.getChunkJSON(0, 300); // first chunk
        JSONArray firstChunkExpect = this.readChunk_OneTraceAndCompleteEvents(0);
        assertEquals(true, firstChunk.similar(firstChunkExpect));
        
        JSONArray lastChunk = movie.getChunkJSON(35817, 300); // last chunk
        JSONArray lastChunkExpect = this.readChunk_OneTraceAndCompleteEvents(35817);
        assertEquals(true, lastChunk.similar(lastChunkExpect));
    }
    
    @Test
    void test_getChunkJSON_TwoLogs() throws Exception {
        AnimationResult result = this.animate_TwoLogs_With_BPMNDiagram();
        AnimationContext animationContext = new AnimationContext(result.getAnimationLogs(), 60, 600);
        ModelMapping modelMapping = new OldBpmnModelMapping(result.getModel());
        Movie movie = FrameRecorder.record(createAnimationIndexes(result.getAnimationLogs(), modelMapping, animationContext),
                                        animationContext);
        
        assertEquals(36000, movie.size());
        
        JSONArray firstChunk = movie.getChunkJSON(0, 300);
        JSONArray firstChunkExpect = this.readChunk_TwoLogs(0);
        assertEquals(true, firstChunk.similar(firstChunkExpect));
        
        JSONArray lastChunk = movie.getChunkJSON(35744, 300);
        JSONArray lastChunkExpect = this.readChunk_TwoLogs(0);
        assertEquals(true, firstChunk.similar(firstChunkExpect));
    }
}
