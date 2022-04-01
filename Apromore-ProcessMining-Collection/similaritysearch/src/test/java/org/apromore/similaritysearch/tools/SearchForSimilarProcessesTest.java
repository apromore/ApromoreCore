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
package org.apromore.similaritysearch.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Bruce Nguyen
 *
 */
class SearchForSimilarProcessesTest extends TestDataSetup {

    @Test
    void testFindProcessesSimilarity_one_task_A_same_process() {
        try {
            BPMNDiagram search = read_one_task_A();
            BPMNDiagram dbDiagram = read_one_task_A();
            double searchResult = SearchForSimilarProcesses.findProcessesSimilarity(search, dbDiagram, 
                                                                    "Greedy", 0.6, 0.75, 1.0, 1.0, 1.0);
            assertEquals(1.0, searchResult, 0.0);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testFindProcessesSimilarity_one_task_A_diff_process() {
        try {
            BPMNDiagram search = read_one_task_A();
            BPMNDiagram dbDiagram = read_one_task_B();
            double searchResult = SearchForSimilarProcesses.findProcessesSimilarity(search, dbDiagram, 
                                                                     "Greedy", 0.6, 0.75, 1.0, 1.0, 1.0);
            assertEquals(0.5, searchResult, 0.1);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testFindProcessesSimilarity_different_processes() {
        try {
            BPMNDiagram search = read_two_tasks_sequence_AB();
            BPMNDiagram dbDiagram = read_two_tasks_sequence_XY();
            double searchResult = SearchForSimilarProcesses.findProcessesSimilarity(search, dbDiagram, 
                                                                     "Greedy", 0.6, 0.75, 1.0, 1.0, 1.0);
            assertEquals(0.5, searchResult, 0.0);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testFindProcessesSimilarity_Hungarian_one_task_A_same_process() {
        try {
            BPMNDiagram search = read_one_task_A();
            BPMNDiagram dbDiagram = read_one_task_A();
            double searchResult = SearchForSimilarProcesses.findProcessesSimilarity(search, dbDiagram, 
                                                                    "Hungarian", 0.6, 0.75, 1.0, 1.0, 1.0);
            assertEquals(1.0, searchResult, 0.0);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testFindProcessesSimilarity_Hungarian_different_processes() {
        try {
            BPMNDiagram search = read_two_tasks_sequence_AB();
            BPMNDiagram dbDiagram = read_two_tasks_sequence_XY();
            double searchResult = SearchForSimilarProcesses.findProcessesSimilarity(search, dbDiagram, 
                                                                     "Hungarian", 0.6, 0.75, 1.0, 1.0, 1.0);
            assertEquals(0.5, searchResult, 0.0);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testFindProcessesSimilarity_Hungarian_one_task_A_diff_process() {
        try {
            BPMNDiagram search = read_one_task_A();
            BPMNDiagram dbDiagram = read_one_task_B();
            double searchResult = SearchForSimilarProcesses.findProcessesSimilarity(search, dbDiagram, 
                                                                     "Hungarian", 0.6, 0.75, 1.0, 1.0, 1.0);
            assertEquals(0.66, searchResult, 0.1);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

}
