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

import org.apromore.plugin.portal.loganimation2.model.Frame;
import org.apromore.plugin.portal.loganimation2.model.Movie;
import org.junit.Assert;
import org.junit.Test;


public class TokenClusteringTest extends FrameTest {
    
    @Test
    // No token clustering
    public void test_TokenClustering_OneTraceLog() throws Exception {
        Movie animationMovie = createAnimationMovie_OneTraceAndCompleteEvents_Graph();
        
        Frame frame0 = animationMovie.get(0);
        Assert.assertArrayEquals(new int[] {0}, frame0.getTokenIndexes(0));
        Assert.assertEquals(1, frame0.getTokenCount(0,0), 0.0);
        
        Frame frame299 = animationMovie.get(299);
        Assert.assertArrayEquals(new int[] {0}, frame299.getTokenIndexes(0));
        Assert.assertEquals(1, frame299.getTokenCount(0,0), 0.0);
       
        Frame frame35999 = animationMovie.get(35999);
        Assert.assertArrayEquals(new int[] {3}, frame35999.getTokenIndexes(0));
        Assert.assertEquals(1, frame35999.getTokenCount(0,3), 0.0);
    }
    
    
    @Test
    // This log has two identical traces.
    // As a result, only one token left on all modelling elements, but its count is 2.
    public void test_TokenClustering_TwoTraceLog() throws Exception {
        Movie animationMovie = createAnimationMovie_TwoTraceAndCompleteEvents_Graph();
        
        Frame frame0 = animationMovie.get(0);
        Assert.assertArrayEquals(new int[] {0}, frame0.getTokenIndexes(0));
        Assert.assertEquals(2, frame0.getTokenCount(0,0), 0.0);
        
        Frame frame299 = animationMovie.get(299);
        Assert.assertArrayEquals(new int[] {0}, frame299.getTokenIndexes(0));
        Assert.assertEquals(2, frame299.getTokenCount(0,0), 0.0);
       
        Frame frame35999 = animationMovie.get(35999);
        Assert.assertArrayEquals(new int[] {3}, frame35999.getTokenIndexes(0));
        Assert.assertEquals(2, frame35999.getTokenCount(0,3), 0.0);
    }
}
