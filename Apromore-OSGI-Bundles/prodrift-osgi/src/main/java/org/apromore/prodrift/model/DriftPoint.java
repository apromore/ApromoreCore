/*
 * Copyright  2009-2017 The Apromore Initiative.
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
package org.apromore.prodrift.model;

import java.io.BufferedWriter;
import java.io.IOException;

public class DriftPoint {

	private int driftPointActual;
	private long driftTimeActual; //ms
	private int IntraTraceDriftAreaStartPoint;
	private int IntraTraceDriftAreaEndPoint;
	private int driftPointDetected = 0;
	private long driftTimeDetected = 0; //ms
	private int truePositive = 0;
	private int falsePositive = 0;
	private int detectionDelay = 0;
	
	
	
	public DriftPoint(int driftPointActual,
			long driftTimeActual, int intraTraceDriftAreaStartPoint,
			int intraTraceDriftAreaEndPoint, int driftPointDetected,
			long driftTimeDetected, int truePositive, int falsePositive, int detectionDelay) {
		super();
		this.driftPointActual = driftPointActual;
		this.driftTimeActual = driftTimeActual;
		this.IntraTraceDriftAreaStartPoint = intraTraceDriftAreaStartPoint;
		this.IntraTraceDriftAreaEndPoint = intraTraceDriftAreaEndPoint;
		this.driftPointDetected = driftPointDetected;
		this.driftTimeDetected = driftTimeDetected;
		this.truePositive = truePositive;
		this.falsePositive = falsePositive;
		this.detectionDelay = detectionDelay;
		
	}

	
	public DriftPoint()
	{
		
	}
	
	public void writeToFile(BufferedWriter writer)
	{
		
		try 
		{
			
			writer.write(driftPointActual + "," + driftTimeActual + "," + IntraTraceDriftAreaStartPoint + "," + IntraTraceDriftAreaEndPoint + ","
					 + driftPointDetected + "," + driftTimeDetected + "," + truePositive + "," + falsePositive + ","
					 + detectionDelay + ",");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public int getDriftPointActual() {
		return driftPointActual;
	}



	public void setDriftPointActual(int driftPointActual) {
		this.driftPointActual = driftPointActual;
	}



	public long getDriftTimeActual() {
		return driftTimeActual;
	}



	public void setDriftTimeActual(long driftTimeActual) {
		this.driftTimeActual = driftTimeActual;
	}



	public int getIntraTraceDriftAreaStartPoint() {
		return IntraTraceDriftAreaStartPoint;
	}



	public void setIntraTraceDriftAreaStartPoint(int intraTraceDriftAreaStartPoint) {
		IntraTraceDriftAreaStartPoint = intraTraceDriftAreaStartPoint;
	}



	public int getIntraTraceDriftAreaEndPoint() {
		return IntraTraceDriftAreaEndPoint;
	}



	public void setIntraTraceDriftAreaEndPoint(int intraTraceDriftAreaEndPoint) {
		IntraTraceDriftAreaEndPoint = intraTraceDriftAreaEndPoint;
	}



	public int getDriftPointDetected() {
		return driftPointDetected;
	}



	public void setDriftPointDetected(int driftPointDetected) {
		this.driftPointDetected = driftPointDetected;
	}



	public long getDriftTimeDetected() {
		return driftTimeDetected;
	}



	public void setDriftTimeDetected(long driftTimeDetected) {
		this.driftTimeDetected = driftTimeDetected;
	}



	public int getTruePositive() {
		return truePositive;
	}



	public void setTruePositive(int truePositive) {
		this.truePositive = truePositive;
	}



	public int getFalsePositive() {
		return falsePositive;
	}



	public void setFalsePositive(int falsePositive) {
		this.falsePositive = falsePositive;
	}


	public int getDetectionDelay() {
		return detectionDelay;
	}


	public void setDetectionDelay(int detectionDelay) {
		this.detectionDelay = detectionDelay;
	}
	
	
	
	
	
}
