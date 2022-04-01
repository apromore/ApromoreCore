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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TokenClusteringTest extends TestDataSetup {
    protected Movie createAnimationMovie_OneTraceOneEvent_OneTaskGrap() throws Exception {
        AnimationResult result = this.animate_OneTraceOneEvent_OneTaskGraph();
        AnimationContext animationContext = new AnimationContext(result.getAnimationLogs(), 60, 600);
        ModelMapping modelMapping = new OldBpmnModelMapping(result.getModel());
        AnimationIndex animationIndex = new AnimationIndex(result.getAnimationLogs().get(0), modelMapping, animationContext);
        return FrameRecorder.record(Arrays.asList(animationIndex), animationContext);
    }
    
    protected Movie createAnimationMovie_TwoTracesOneEvent_OneTaskGrap() throws Exception {
        AnimationResult result = this.animate_TwoTracesOneEvent_OneTaskGraph();
        AnimationContext animationContext = new AnimationContext(result.getAnimationLogs(), 60, 600);
        ModelMapping modelMapping = new OldBpmnModelMapping(result.getModel());
        AnimationIndex animationIndex = new AnimationIndex(result.getAnimationLogs().get(0), modelMapping, animationContext);
        return FrameRecorder.record(Arrays.asList(animationIndex), animationContext);
    }
    
    @Test
    // No token clustering
    void test_TokenClustering_OneTraceLog() throws Exception {
        Movie animationMovie = createAnimationMovie_OneTraceOneEvent_OneTaskGrap();
        
        Frame frame0 = animationMovie.get(0);
        assertEquals(1, frame0.getClusters(0).length);
        assertEquals(1, frame0.getClusterSize(0, frame0.getClusters(0)[0]), 0.0);
        
        Frame frame299 = animationMovie.get(299);
        assertEquals(1, frame299.getClusters(0).length);
        assertEquals(1, frame299.getClusterSize(0, frame299.getClusters(0)[0]), 0.0);
       
        Frame frame3598 = animationMovie.get(35998);
        assertEquals(1, frame3598.getClusters(0).length);
        assertEquals(1, frame3598.getClusterSize(0, frame3598.getClusters(0)[0]), 0.0);
    }
    
    
    @Test
    // This log has two identical traces.
    // As a result, only one token left on all modelling elements, but its count is 2.
    void test_TokenClustering_TwoTraceLog() throws Exception {
        Movie animationMovie = createAnimationMovie_TwoTracesOneEvent_OneTaskGrap();
        
        Frame frame0 = animationMovie.get(0);
        assertEquals(1, frame0.getClusters(0).length);
        assertEquals(2, frame0.getClusterSize(0, frame0.getClusters(0)[0]), 0.0);
        
        Frame frame299 = animationMovie.get(299);
        assertEquals(1, frame299.getClusters(0).length);
        assertEquals(2, frame299.getClusterSize(0, frame299.getClusters(0)[0]), 0.0);
       
        Frame frame35999 = animationMovie.get(35999);
        assertEquals(1, frame35999.getClusters(0).length);
        assertEquals(2, frame35999.getClusterSize(0, frame35999.getClusters(0)[0]), 0.0);
    }
}
