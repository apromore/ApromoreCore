/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.perfmining;

import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Administrator
 */
public class Visualization {
    public static JSONObject createCFDJson(XYDataset ds) throws JSONException {
        JSONObject json = new JSONObject();
        
        //-----------------------------------------
        // For the series
        //-----------------------------------------
        JSONArray jsonSeriesArray = new JSONArray();
        
//        for (int i=0;i<ds.getSeriesCount();i++) {
        for (int i=(ds.getSeriesCount()-1);i>=0;i--) {
            System.out.println("Start series " + ds.getSeriesKey(i).toString());
            //----------------
            //For one series
            //----------------
            JSONObject jsonOneSeries = new JSONObject();
            jsonOneSeries.put("name", ds.getSeriesKey(i).toString());
            jsonOneSeries.put("type", "area");
            
            if (ds.getSeriesKey(i).toString().contains("Exit")) {
                jsonOneSeries.put("color", "grey");
            }
            
            //For data array in one series
            JSONArray jsonOneSeriesData = new JSONArray();
            for (int j=0;j<ds.getItemCount(i);j++) {
                JSONArray jsonDataItem = new JSONArray();
                jsonDataItem.put(ds.getXValue(i, j));
                jsonDataItem.put(ds.getYValue(i, j));
                jsonOneSeriesData.put(jsonDataItem); 
            }
            jsonOneSeries.put("data",jsonOneSeriesData);
            
            jsonSeriesArray.put(jsonOneSeries);
            System.out.println("Finish series " + ds.getSeriesKey(i).toString());
        }
        //jsonSeries.put("series", jsonSeriesArray);
        json.put("series", jsonSeriesArray);
        
        return json;
    }
    
    public static JSONObject createChartJson(XYDataset ds) throws JSONException {
        JSONObject json = new JSONObject();
        
        //-----------------------------------------
        // For the series
        //-----------------------------------------
        JSONArray jsonSeriesArray = new JSONArray();
        
        for (int i=(ds.getSeriesCount()-1);i>=0;i--) {
            System.out.println("Start series " + ds.getSeriesKey(i).toString());
            //----------------
            //For one series
            //----------------
            JSONObject jsonOneSeries = new JSONObject();
            jsonOneSeries.put("name", ds.getSeriesKey(i).toString());
            jsonOneSeries.put("tooltip", "{valueDecimals: 2}");
            
            //For data array in one series
            JSONArray jsonOneSeriesData = new JSONArray();
            for (int j=0;j<ds.getItemCount(i);j++) {
                JSONArray jsonDataItem = new JSONArray();
                jsonDataItem.put(ds.getXValue(i, j));
                jsonDataItem.put(ds.getYValue(i, j));
                jsonOneSeriesData.put(jsonDataItem); 
            }
            jsonOneSeries.put("data",jsonOneSeriesData);
            
            jsonSeriesArray.put(jsonOneSeries);
            System.out.println("Finish series " + ds.getSeriesKey(i).toString());
        }
        //jsonSeries.put("series", jsonSeriesArray);
        json.put("series", jsonSeriesArray);
        
        return json;
        
    }
}
