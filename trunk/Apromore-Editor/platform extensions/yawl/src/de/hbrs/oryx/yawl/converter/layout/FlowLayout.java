/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See: http://www.gnu.org/licenses/lgpl-3.0
 * 
 */
package de.hbrs.oryx.yawl.converter.layout;

import java.util.ArrayList;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;


/**
 * Layout information of a Flow
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class FlowLayout {

	private Bounds bounds;
	private ArrayList<Point> dockers;
	private String label;
	private int lineStyle;

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setDockers(ArrayList<Point> dockers) {
		this.dockers = dockers;
	}

	public ArrayList<Point> getDockers() {
		return dockers;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLineStyle(int linestyle) {
		this.lineStyle = linestyle;
	}

	public int getLineStyle() {
		return lineStyle;
	}

}
