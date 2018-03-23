/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implbebopied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.bprove;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.apromore.model.ExportFormatResultType;
import org.apromore.graph.canonical.Canonical;
import org.json.JSONArray;
import org.json.JSONObject;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import plugin.bpmn.to.maude.handlers.PostMultipleParameters;

/**
 * Created by Fabrizio Fornari on 18/12/2017.
 */
public interface BproveService {


   /* ArrayList <String> checkGuidelinesBPMNDiagram(
              BPMNDiagram bpmnDiagram
    );

    HashMap<Integer,JSONObject> checkGuidelinesBPMNDiagramJson(
            BPMNDiagram bpmnDiagram
    );

    ArrayList <String> checkGuidelinesBPMN(
            String model, BPMNDiagram bpmnDiagram
    );*/

    /*HashMap<Integer,JSONObject> checkGuidelinesBPMNJson(
            String model
    );*/

    public String getParsedModelBprove(
            String modelString
            //BPMNDiagram bpmnDiagram
    );

    public String getMaudeOperation(
        String modelToParse, String parsedModel, String propertyToVerify, 
	String param, String poolName1, String poolName2, String taskName1 , String taskName2 , String msgName
        //BPMNDiagram bpmnDiagram
);

//    public PostMultipleParameters getPropertyVerificationBProVe(
//            PostMultipleParameters model
//    );



//    public void obtainPoolAndTaskList(
//            String parsedModelhigh,
//            ArrayList<String> poolList,
//            ArrayList<String> taskList
//    );

}
