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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.apromore.service.loganimation.AnimationResult;
import org.apromore.service.loganimation.modelmapping.OldBpmnModelMapping;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class FrameTest extends TestDataSetup {
    protected Movie createAnimationMovie_OneTraceAndCompleteEvents_Graph() throws Exception {
        AnimationResult result = this.animate_OneTraceAndCompleteEvents_Graph();
        AnimationContext animationContext = new AnimationContext(result.getAnimationLogs(), 60, 600);
        ModelMapping modelMapping = new OldBpmnModelMapping(result.getModel());
        AnimationIndex animationIndex = new AnimationIndex(result.getAnimationLogs().get(0), modelMapping, animationContext);
        return FrameRecorder.record(Arrays.asList(animationIndex), animationContext);
    }
    
    protected Movie createAnimationMovie_TwoLogs() throws Exception {
        AnimationResult result = this.animate_TwoLogs_With_BPMNDiagram();
        AnimationContext animationContext = new AnimationContext(result.getAnimationLogs(), 60, 600);
        ModelMapping modelMapping = new OldBpmnModelMapping(result.getModel());
        return FrameRecorder.record(createAnimationIndexes(result.getAnimationLogs(), modelMapping, animationContext),
                                    animationContext);
    }
    
    @Test
    void test_FrameData_OneTraceLog() throws Exception {
        Movie animationMovie = createAnimationMovie_OneTraceAndCompleteEvents_Graph();
        
        Frame frame0 = animationMovie.get(0);
        assertEquals(0, frame0.getIndex());
        assertArrayEquals(new int[] {0}, frame0.getCaseIndexes(0));
        assertArrayEquals(new int[] {13}, frame0.getElementIndexes(0));
        assertArrayEquals(new int[] {0}, frame0.getOriginalTokens(0));
        assertArrayEquals(new int[] {}, frame0.getOriginalTokensByElement(0,0));
        assertArrayEquals(new int[] {0}, frame0.getOriginalTokensByElement(0, 13));
        
        Frame frame299 = animationMovie.get(299);
        assertEquals(299, frame299.getIndex());
        assertArrayEquals(new int[] {0}, frame299.getCaseIndexes(0));
        assertArrayEquals(new int[] {13}, frame299.getElementIndexes(0));
        assertArrayEquals(new int[] {0}, frame299.getOriginalTokens(0));
        assertArrayEquals(new int[] {}, frame299.getOriginalTokensByElement(0,0));
        assertArrayEquals(new int[] {0}, frame299.getOriginalTokensByElement(0,13));
       
        Frame frame35999 = animationMovie.get(35999);
        assertEquals(35999, frame35999.getIndex());
        assertArrayEquals(new int[] {0}, frame35999.getCaseIndexes(0));
        assertArrayEquals(new int[] {11}, frame35999.getElementIndexes(0));
        assertArrayEquals(new int[] {3}, frame35999.getOriginalTokens(0));
        assertArrayEquals(new int[] {}, frame35999.getOriginalTokensByElement(0,0));
        assertArrayEquals(new int[] {3}, frame35999.getOriginalTokensByElement(0,11));
    }
    
    @Test
    void test_FrameJSON_OneTraceLog() throws Exception {
        Movie animationMovie = createAnimationMovie_OneTraceAndCompleteEvents_Graph();
        
        JSONObject frame0 = animationMovie.get(0).getJSON();
        JSONObject frame0Expect = this.readFrame_OneTraceAndCompleteEvents(0);
        assertEquals(true, frame0Expect.similar(frame0));
        
        JSONObject frame299 = animationMovie.get(299).getJSON();
        JSONObject frame299Expect = this.readFrame_OneTraceAndCompleteEvents(299);
        assertEquals(true, frame299Expect.similar(frame299));
        
        JSONObject frame35990 = animationMovie.get(35990).getJSON();
        JSONObject frame35990Expect = this.readFrame_OneTraceAndCompleteEvents(35990);
        assertEquals(true, frame35990Expect.similar(frame35990));
        
        JSONObject frame35999 = animationMovie.get(35999).getJSON();
        JSONObject frame35999Expect = this.readFrame_OneTraceAndCompleteEvents(35999);
        assertEquals(true, frame35999Expect.similar(frame35999));
    }
    
    @Test
    void test_FrameData_TwoLogs() throws Exception {
        Movie animationMovie = createAnimationMovie_TwoLogs();
        
        // This frame only has one token for the 2nd log
        Frame firstFrame = animationMovie.get(0);
        assertEquals(0, firstFrame.getIndex());
        assertEquals(true, firstFrame.getOriginalTokens(0).length > 0);
        assertEquals(true, firstFrame.getOriginalTokens(1).length == 0); // no token for the 2nd log in this frame
        
        // This frame has one token for both logs
        Frame frameTwoTokens = animationMovie.get(19036);
        assertEquals(19036, frameTwoTokens.getIndex());
        assertEquals(true, frameTwoTokens.getOriginalTokens(0).length > 0);
        assertEquals(true, frameTwoTokens.getOriginalTokens(1).length > 0);
       
        // This frame only has one token for the 1st log
        Frame lastFrame = animationMovie.get(35999);
        assertEquals(35999, lastFrame.getIndex());
        assertEquals(true, lastFrame.getOriginalTokens(1).length > 0);
        assertEquals(true, lastFrame.getOriginalTokens(0).length == 0); // no token for the 1st log in this frame
    }
    
    @Test
    void test_FrameJSON_TwoLogs() throws Exception {
        Movie animationMovie = createAnimationMovie_TwoLogs();
        
        JSONObject testFrame = animationMovie.get(19036).getJSON();
        JSONObject expectedFrame = this.readFrame_TwoLogs(19036);
        assertEquals(true, expectedFrame.similar(testFrame));
    }
}
