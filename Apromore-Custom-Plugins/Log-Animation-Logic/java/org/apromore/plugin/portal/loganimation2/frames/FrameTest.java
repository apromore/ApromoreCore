/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.plugin.portal.loganimation2.frames;

import java.util.Arrays;

import org.apromore.plugin.portal.loganimation2.OldBpmnModelMapping;
import org.apromore.plugin.portal.loganimation2.model.AnimationContext;
import org.apromore.plugin.portal.loganimation2.model.AnimationIndex;
import org.apromore.plugin.portal.loganimation2.model.Frame;
import org.apromore.plugin.portal.loganimation2.model.FrameRecorder;
import org.apromore.plugin.portal.loganimation2.model.Movie;
import org.apromore.service.loganimation.AnimationResult;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class FrameTest extends TestDataSetup {
    protected Movie createAnimationMovie_OneTraceAndCompleteEvents_Graph() throws Exception {
        AnimationResult result = this.animate_OneTraceAndCompleteEvents_Graph();
        AnimationContext animationContext = new AnimationContext(result.getAnimationLogs(), 60, 600);
        OldBpmnModelMapping modelMapping = new OldBpmnModelMapping(result.getModel());
        AnimationIndex animationIndex = new AnimationIndex(result.getAnimationLogs().get(0), modelMapping, animationContext);
        return FrameRecorder.record(result.getAnimationLogs(), Arrays.asList(animationIndex), animationContext);
    }
    
    protected Movie createAnimationMovie_TwoTraceAndCompleteEvents_Graph() throws Exception {
        AnimationResult result = this.animate_TwoTraceAndCompleteEvents_Graph();
        AnimationContext animationContext = new AnimationContext(result.getAnimationLogs(), 60, 600);
        OldBpmnModelMapping modelMapping = new OldBpmnModelMapping(result.getModel());
        AnimationIndex animationIndex = new AnimationIndex(result.getAnimationLogs().get(0), modelMapping, animationContext);
        return FrameRecorder.record(result.getAnimationLogs(), Arrays.asList(animationIndex), animationContext);
    }
    
    protected Movie createAnimationMovie_TwoLogs() throws Exception {
        AnimationResult result = this.animate_TwoLogs_With_BPMNDiagram();
        AnimationContext animationContext = new AnimationContext(result.getAnimationLogs(), 60, 600);
        OldBpmnModelMapping modelMapping = new OldBpmnModelMapping(result.getModel());
        return FrameRecorder.record(result.getAnimationLogs(),
                                    createAnimationIndexes(result.getAnimationLogs(), modelMapping, animationContext),
                                    animationContext);
    }
    
    @Test
    public void test_FrameData_OneTraceLog() throws Exception {
        Movie animationMovie = createAnimationMovie_OneTraceAndCompleteEvents_Graph();
        
        Frame frame0 = animationMovie.get(0);
        Assert.assertEquals(0, frame0.getIndex());
        Assert.assertArrayEquals(new int[] {0}, frame0.getCaseIndexes(0));
        Assert.assertArrayEquals(new int[] {13}, frame0.getElementIndexes(0));
        Assert.assertArrayEquals(new int[] {0}, frame0.getTokenIndexes(0));
        Assert.assertArrayEquals(new int[] {}, frame0.getTokenIndexesByElement(0,0));
        Assert.assertArrayEquals(new int[] {0}, frame0.getTokenIndexesByElement(0, 13));
        
        Frame frame299 = animationMovie.get(299);
        Assert.assertEquals(299, frame299.getIndex());
        Assert.assertArrayEquals(new int[] {0}, frame299.getCaseIndexes(0));
        Assert.assertArrayEquals(new int[] {13}, frame299.getElementIndexes(0));
        Assert.assertArrayEquals(new int[] {0}, frame299.getTokenIndexes(0));
        Assert.assertArrayEquals(new int[] {}, frame299.getTokenIndexesByElement(0,0));
        Assert.assertArrayEquals(new int[] {0}, frame299.getTokenIndexesByElement(0,13));
       
        Frame frame35999 = animationMovie.get(35999);
        Assert.assertEquals(35999, frame35999.getIndex());
        Assert.assertArrayEquals(new int[] {0}, frame35999.getCaseIndexes(0));
        Assert.assertArrayEquals(new int[] {11}, frame35999.getElementIndexes(0));
        Assert.assertArrayEquals(new int[] {3}, frame35999.getTokenIndexes(0));
        Assert.assertArrayEquals(new int[] {}, frame35999.getTokenIndexesByElement(0,0));
        Assert.assertArrayEquals(new int[] {3}, frame35999.getTokenIndexesByElement(0,11));
    }
    
    @Test
    public void test_FrameJSON_OneTraceLog() throws Exception {
        Movie animationMovie = createAnimationMovie_OneTraceAndCompleteEvents_Graph();
        
        JSONObject frame0 = animationMovie.get(0).getJSON();
        JSONObject frame0Expect = this.readFrame_OneTraceAndCompleteEvents(0);
        Assert.assertEquals(true, frame0Expect.similar(frame0));
        
        JSONObject frame299 = animationMovie.get(299).getJSON();
        JSONObject frame299Expect = this.readFrame_OneTraceAndCompleteEvents(299);
        Assert.assertEquals(true, frame299Expect.similar(frame299));
        
        JSONObject frame35990 = animationMovie.get(35990).getJSON();
        JSONObject frame35990Expect = this.readFrame_OneTraceAndCompleteEvents(35990);
        Assert.assertEquals(true, frame35990Expect.similar(frame35990));
        
        JSONObject frame35999 = animationMovie.get(35999).getJSON();
        JSONObject frame35999Expect = this.readFrame_OneTraceAndCompleteEvents(35999);
        Assert.assertEquals(true, frame35999Expect.similar(frame35999));
    }
    
    @Test
    public void test_FrameData_TwoLogs() throws Exception {
        Movie animationMovie = createAnimationMovie_TwoLogs();
        
        // This frame only has one token for the 2nd log
        Frame firstFrame = animationMovie.get(0);
        Assert.assertEquals(0, firstFrame.getIndex());
        Assert.assertEquals(true, firstFrame.getTokenIndexes(0).length > 0);
        Assert.assertEquals(true, firstFrame.getTokenIndexes(1).length == 0); // no token for the 2nd log in this frame
        
        // This frame has one token for both logs
        Frame frameTwoTokens = animationMovie.get(19036);
        Assert.assertEquals(19036, frameTwoTokens.getIndex());
        Assert.assertEquals(true, frameTwoTokens.getTokenIndexes(0).length > 0);
        Assert.assertEquals(true, frameTwoTokens.getTokenIndexes(1).length > 0);
       
        // This frame only has one token for the 1st log
        Frame lastFrame = animationMovie.get(35999);
        Assert.assertEquals(35999, lastFrame.getIndex());
        Assert.assertEquals(true, lastFrame.getTokenIndexes(1).length > 0);
        Assert.assertEquals(true, lastFrame.getTokenIndexes(0).length == 0); // no token for the 1st log in this frame
    }
    
    @Test
    public void test_FrameJSON_TwoLogs() throws Exception {
        Movie animationMovie = createAnimationMovie_TwoLogs();
        
        JSONObject testFrame = animationMovie.get(19036).getJSON();
        JSONObject expectedFrame = this.readFrame_TwoLogs(19036);
        Assert.assertEquals(true, expectedFrame.similar(testFrame));
    }
}
