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
import org.apromore.plugin.portal.loganimation2.model.FrameRecorder;
import org.apromore.plugin.portal.loganimation2.model.Movie;
import org.apromore.service.loganimation.AnimationResult;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

public class MovieTest extends TestDataSetup {
    
    @Test
    public void test_getChunkJSON_OneLog() throws Exception {
        AnimationResult result = this.animate_OneTraceAndCompleteEvents_BPMNDiagram();
        AnimationContext animationContext = new AnimationContext(result.getAnimationLogs(), 60, 600);
        OldBpmnModelMapping modelMapping = new OldBpmnModelMapping(result.getModel());
        AnimationIndex animationIndex = new AnimationIndex(result.getAnimationLogs().get(0), modelMapping, animationContext);
        Movie movie = FrameRecorder.record(result.getAnimationLogs(), Arrays.asList(animationIndex), animationContext);
        
        Assert.assertEquals(36000, movie.size());
        
        JSONArray firstChunk = movie.getChunkJSON(0, 300); // first chunk
        JSONArray firstChunkExpect = this.readChunk_OneTraceAndCompleteEvents(0);
        Assert.assertEquals(true, firstChunk.similar(firstChunkExpect));
        
        JSONArray lastChunk = movie.getChunkJSON(35817, 300); // last chunk
        JSONArray lastChunkExpect = this.readChunk_OneTraceAndCompleteEvents(35817);
        Assert.assertEquals(true, lastChunk.similar(lastChunkExpect));
    }
    
    @Test
    public void test_getChunkJSON_TwoLogs() throws Exception {
        AnimationResult result = this.animate_TwoLogs_With_BPMNDiagram();
        AnimationContext animationContext = new AnimationContext(result.getAnimationLogs(), 60, 600);
        OldBpmnModelMapping modelMapping = new OldBpmnModelMapping(result.getModel());
        Movie movie = FrameRecorder.record(result.getAnimationLogs(),
                                        createAnimationIndexes(result.getAnimationLogs(), modelMapping, animationContext),
                                        animationContext);
        
        Assert.assertEquals(36000, movie.size());
        
        JSONArray firstChunk = movie.getChunkJSON(0, 300);
        JSONArray firstChunkExpect = this.readChunk_TwoLogs(0);
        Assert.assertEquals(true, firstChunk.similar(firstChunkExpect));
        
        JSONArray lastChunk = movie.getChunkJSON(35744, 300);
        JSONArray lastChunkExpect = this.readChunk_TwoLogs(0);
        Assert.assertEquals(true, firstChunk.similar(firstChunkExpect));
    }
}
