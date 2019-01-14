/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package org.apromore.service.bebop;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.apromore.model.ExportFormatResultType;
import org.apromore.graph.canonical.Canonical;
import org.json.JSONArray;
import org.json.JSONObject;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
/**
 * Created by Fabrizio Fornari on 18/05/2017.
 */
public interface BebopService {

    ArrayList <String> checkGuidelines(
              String modelName,
              ExportFormatResultType exportedProcess,
              Canonical cpfDiagram
    );

    ArrayList <String> checkGuidelinesBPMNDiagram(
              BPMNDiagram bpmnDiagram
    );

    HashMap<Integer,JSONObject> checkGuidelinesBPMNDiagramJson(
            BPMNDiagram bpmnDiagram
    );

    ArrayList <String> checkGuidelinesBPMN(
            String model, BPMNDiagram bpmnDiagram
    );

    HashMap<Integer,JSONObject> checkGuidelinesBPMNJson(
            String model
    );

}
