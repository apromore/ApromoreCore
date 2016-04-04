/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package de.hbrs.oryx.yawl.util;

import java.util.HashMap;
import java.util.Map;

import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YMultiInstanceAttributes;
import org.yawlfoundation.yawl.elements.YTask;

/**
 * Converts the MultiInstance attributes of both a AtomicTask and CompositeTask. Would have needed multiple inheritance or another inheritance tree to
 * integrate this class in the Handler logic.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class MultiInstanceConverter {

    public static Map<String, String> convert(final YTask task) {
        HashMap<String, String> map = new HashMap<String, String>();

        if (task.isMultiInstance()) {
            YMultiInstanceAttributes m = task.getMultiInstanceAttributes();
            map.put("minimum", String.valueOf(m.getMinInstances()));
            map.put("maximum", String.valueOf(m.getMaxInstances()));
            map.put("threshold", String.valueOf(m.getThreshold()));
            map.put("creationmode", m.getCreationMode());
            map.put("miinputexpression", task.getPreSplittingMIQuery());
            map.put("miinputsplittingexpression", m.getMISplittingQuery());
            map.put("miinputformalinputparam", m.getMIFormalInputParam());
            if (m.getMIFormalOutputQuery() != null) {
                map.put("mioutputformaloutputexpression", m.getMIFormalOutputQuery());
                map.put("mioutputoutputjoiningexpression", m.getMIJoiningQuery());
                map.put("mioutputresultappliedtolocalvariable", task.getMIOutputAssignmentVar(m.getMIFormalOutputQuery()));
            }
        }

        return map;
    }

    public static void convert(final BasicShape shape, final YTask task) {
        String min = shape.getProperty("minimum");
        String max = shape.getProperty("maximum");
        String threshold = shape.getProperty("threshold");
        String creationMode = shape.getProperty("creationmode");

        task.setUpMultipleInstanceAttributes(min, max, threshold, creationMode);

        String preSplittingMIQuery = shape.getProperty("miinputexpression");
        String miSplittingQuery = shape.getProperty("miinputsplittingexpression");
        String miFormalInputParam = shape.getProperty("miinputformalinputparam");
        String miFormalOutputQuery = shape.getProperty("mioutputformaloutputexpression");
        String miJoiningQuery = shape.getProperty("mioutputoutputjoiningexpression");
        String mioutputAssignmentVar = shape.getProperty("mioutputresultappliedtolocalvariable");

        YMultiInstanceAttributes m = task.getMultiInstanceAttributes();
        m.setMIFormalInputParam(miFormalInputParam);
        m.setMIFormalOutputQuery(miFormalOutputQuery);

        m.setUniqueInputMISplittingQuery(miSplittingQuery);
        m.setUniqueOutputMIJoiningQuery(miJoiningQuery);

        task.setDataBindingForInputParam(preSplittingMIQuery, miFormalInputParam);
        task.setDataBindingForOutputExpression(miFormalOutputQuery, mioutputAssignmentVar);
    }

}
