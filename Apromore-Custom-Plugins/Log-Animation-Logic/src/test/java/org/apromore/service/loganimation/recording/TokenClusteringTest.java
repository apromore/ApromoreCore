/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
