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

package org.apromore.plugin.portal.processdiscoverer.vis;

import java.text.DecimalFormat;

import org.apromore.logman.Constants;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;

/**
 * Contains all visual settings for visualizing a process map.
 * 
 * @author Bruce Nguyen
 *
 */
public class VisualSettings {
	private final DecimalFormat decimalFormatter = new DecimalFormat("###,###,###,###,##0.##");
	private final TimeConverter timeConverter = new TimeConverter();
	private final StringFormatter stringFormatter = new StringFormatter();
	private final StandardColorSettings colorSettings = new StandardColorSettings();
	
    private int border_width = 1;
    private int border_width_end = 3;

    private int event_height = 25;
    private int event_width = 25;
    private int gateway_height = 50; 
    private int gateway_width = 50;
    private int activity_width = 160;
    private int activity_height = 70;
    private int text_width = activity_width-5;
    
    private int intra_cell_spacing = 50;
    private int inter_rank_cell_spacing = 100;
    private int parallel_edge_spacing = 30;

    private int activityFontSize = 14; //"10";
    private int activityFontSizeSmall = 12; //"10";

    private int xor_gateway_font_size = 25; //"20"; //(used_bpmn_size) ? "20" : "10";
    private int and_gateway_font_size = 40; //"30"; //(used_bpmn_size) ? "30" : "10";
    
    private String start_name_name = Constants.START_NAME;
    private String end_event_name = Constants.END_NAME;

    private String currency = "USD";

    public VisualSettings(String currency) {
        this.currency = currency;
    }

    public static VisualSettings standard(String currency) {
        return new VisualSettings(currency);
    }
    
    public DecimalFormat getDecimalFormatter() {
    	return decimalFormatter;
    }
    
    public StringFormatter getStringFormatter() {
    	return stringFormatter;
    }
    
    public TimeConverter getTimeConverter() {
    	return timeConverter;
    }
    
    public StandardColorSettings getColorSettings() {
    	return colorSettings;
    }
    
    public int getIntraCellSpacing() {
        return intra_cell_spacing;
    }
    
    public int getInterRankCellSpacing() {
        return inter_rank_cell_spacing;
    }
    
    public int getParallelEdgeSpacing() {
        return parallel_edge_spacing;
    }

    public String getCurrency() {
        return currency;
    }

    public int getBorderWidth() {
    	return border_width;
    }
    
    public int getBorderWidthEnd() {
    	return border_width_end;
    }
    
    public int getEventHeight() {
    	return event_height;
    }
    
    public int getEventWidth() {
    	return event_width;
    }
    
    public int getGatewayHeight() {
    	return gateway_height;
    }
    
    public int getGatewayWidth() {
    	return gateway_width;
    }
    
    public int getActivityWidth() {
    	return activity_width;
    }
    
    public int getActivityHeight() {
    	return activity_height;
    }
    
    public int getTextWidth() {
    	return text_width;
    }
    
    public int getActivityFontSize() {
    	return activityFontSize;
    }
    public int getActivityFontSizeSmall() {
        return activityFontSizeSmall;
    }
    
    public int getXORGatewayFontSize() {
    	return xor_gateway_font_size;
    }
    
    public int getANDGatewayFontSize() {
    	return and_gateway_font_size;
    }
    
    public String getStartEventName() {
    	return start_name_name;
    }
    
    public String getEndEventName() {
    	return end_event_name;
    }
    
    public double[] getCenterXY(BPMNNode node, double x, double y) {
        if (node instanceof Event) {
            x += 1.0*getEventWidth()/2; 
            y += 1.0*getEventHeight()/2;
        }
        else if (node instanceof Gateway) {
            x += 1.0*getGatewayWidth()/2; 
            y += 1.0*getGatewayHeight()/2; 
        }
        else {
            x += 1.0*getActivityWidth()/2; 
            y += 1.0*getActivityHeight()/2; 
        }
        
        return new double[] {x,y};
    }
}
