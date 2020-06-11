/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2019 The University of Tartu.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.service.csvexporter.impl;

import org.apromore.service.csvexporter.CSVExporterLogic;
import org.deckfour.xes.model.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CSVExporterLogicImpl implements CSVExporterLogic {

    private List<String> columnNames;

    public String exportCSV(XLog myLog) {
        List<LogModel> log = createModel(myLog);
        return writeCSVFile(log);
    }

    private static String CASEID = "Case ID";
    private static String ACTIVITY = "Activity";

    private List<LogModel> createModel(List<XTrace> traces){

        HashMap<String, String> attributeList;
        HashMap<String, String> eventAttributes;

        List<LogModel> logData = new ArrayList<LogModel>();
        String attributeValue;

        Set<String> listOfAttributes = new LinkedHashSet<String>();
        columnNames = new ArrayList<String>();
        columnNames.add(CASEID);
        columnNames.add(ACTIVITY);

        for (XTrace myTrace: traces) {
            listOfAttributes.addAll(myTrace.getAttributes().keySet());

            attributeList = new HashMap<String, String>();

            for (Map.Entry<String, XAttribute> tAtt : myTrace.getAttributes().entrySet()){

                attributeValue = getAttributeValue(tAtt.getValue());
                if(tAtt.getKey().equals("concept:name")){
                    attributeList.put(CASEID, attributeValue);
                }else{
                    attributeList.put(tAtt.getKey(), attributeValue);
                }
            }

            for (XEvent myEvent: myTrace) {
                eventAttributes = new HashMap<String, String>();
                eventAttributes.putAll(attributeList);
                listOfAttributes.addAll(myEvent.getAttributes().keySet());

                for (Map.Entry<String, XAttribute> eAtt : myEvent.getAttributes().entrySet()){

                    attributeValue = getAttributeValue(eAtt.getValue());
                    if(eAtt.getKey().equals("concept:name")){
                        eventAttributes.put(ACTIVITY, attributeValue);
                    }else{
                        eventAttributes.put(eAtt.getKey(), attributeValue);
                    }
                }

                logData.add(new LogModel(eventAttributes));
            }
        }

        if(listOfAttributes.contains("concept:name")){
            listOfAttributes.remove("concept:name");
        }
        columnNames.addAll(new ArrayList<String>(listOfAttributes));

    return  logData;
    }


    private String getAttributeValue(XAttribute myAttribute){

        if(myAttribute instanceof XAttributeLiteral){
            String theValue = ((XAttributeLiteral)myAttribute).getValue();
            if(theValue.contains(",")) return "\"" + theValue + "\"";
            return  theValue;
        }else if (myAttribute instanceof XAttributeTimestamp){

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return df.format(((XAttributeTimestamp)myAttribute).getValue());
        }
        else if (myAttribute instanceof XAttributeBoolean){
            return String.valueOf(((XAttributeBoolean)myAttribute).getValue());
        }
        else if (myAttribute instanceof XAttributeDiscrete){
            return String.valueOf(((XAttributeDiscrete)myAttribute).getValue());
        }
        else if (myAttribute instanceof XAttributeContinuous){
            return  String.valueOf(((XAttributeContinuous)myAttribute).getValue());
        }
        return "";

    }


    private String writeCSVFile(List<LogModel> log){

        StringBuilder sb = new StringBuilder();

        String prefix = "";
        for(String one : columnNames){
            sb.append(prefix);
            prefix = ",";
            sb.append(one);
        }
        sb.append('\n');

        String columnValue;
        for (LogModel row : log) {
            prefix = "";
            for(String one : columnNames){
                sb.append(prefix);
                prefix = ",";

                columnValue = row.getAttributeList().get(one);
                if(columnValue != null && columnValue.trim().length() !=0){
                    sb.append(columnValue);
                }else{
                    sb.append("");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
