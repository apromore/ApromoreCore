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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.plugins.bpmn.plugins.BpmnImportPlugin;

public class TestDataSetup {
    private BpmnImportPlugin bpmnImport = new BpmnImportPlugin();
    
    protected BPMNDiagram readBPMNDiagram(String fullFilePath) throws FileNotFoundException, Exception {
        return bpmnImport.importFromStreamToDiagram(new FileInputStream(new File(fullFilePath)), fullFilePath);
    }
    
    public BPMNDiagram read_one_task_A() throws Exception {
        return this.readBPMNDiagram("src/test/data/one_task_A.bpmn");
    }
    
    public BPMNDiagram read_one_task_B() throws Exception {
        return this.readBPMNDiagram("src/test/data/one_task_B.bpmn");
    }
    
    public BPMNDiagram read_one_task_C() throws Exception {
        return this.readBPMNDiagram("src/test/data/one_task_C.bpmn");
    }    
    
    public BPMNDiagram read_two_tasks_sequence_AB() throws Exception {
        return this.readBPMNDiagram("src/test/data/two_tasks_sequence_AB.bpmn");
    }
    
    public BPMNDiagram read_two_tasks_sequence_BA() throws Exception {
        return this.readBPMNDiagram("src/test/data/two_tasks_sequence_BA.bpmn");
    }
    
    public BPMNDiagram read_two_tasks_sequence_XY() throws Exception {
        return this.readBPMNDiagram("src/test/data/two_tasks_sequence_XY.bpmn");
    }
    
    public BPMNDiagram read_two_tasks_xor() throws Exception {
        return this.readBPMNDiagram("src/test/data/two_tasks_xor.bpmn");
    }
    
    public BPMNDiagram read_two_tasks_and() throws Exception {
        return this.readBPMNDiagram("src/test/data/two_tasks_and.bpmn");
    }
    
    public BPMNDiagram read_two_tasks_or() throws Exception {
        return this.readBPMNDiagram("src/test/data/two_tasks_or.bpmn");
    }
    
    public BPMNDiagram read_three_tasks_sequence() throws Exception {
        return this.readBPMNDiagram("src/test/data/three_tasks_sequence.bpmn");
    }
    
    public BPMNDiagram read_three_tasks_xor_sequence() throws Exception {
        return this.readBPMNDiagram("src/test/data/three_tasks_xor_sequence.bpmn");
    }
    
    public BPMNDiagram read_three_tasks_sequence_xor() throws Exception {
        return this.readBPMNDiagram("src/test/data/three_tasks_sequence_xor.bpmn");
    }
    
    public BPMNDiagram read_xor_branch_A() throws Exception {
        return this.readBPMNDiagram("src/test/data/xor_branch_A.bpmn");
    }
    
    public BPMNDiagram read_xor_branch_B() throws Exception {
        return this.readBPMNDiagram("src/test/data/xor_branch_B.bpmn");
    }
}
