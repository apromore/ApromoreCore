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
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package au.qut.eventstr.comparison.differences;

import java.util.LinkedList;

/**
 * Collection of runs associated to a difference. 
 * This is a wrapper used for generating the 
 * JSON file retrieved by the REST service. 
 */
public class Runs {
	LinkedList<Run> runs;

	public Runs(){
		runs = new LinkedList<Run>();
	}
	
	public void addRun(Run run) {
		runs.add(run);
	}
	
	public LinkedList<Run> getRuns() {
		return runs;
	}

	public void setRuns(LinkedList<Run> runs) {
		this.runs = runs;
	}
}
