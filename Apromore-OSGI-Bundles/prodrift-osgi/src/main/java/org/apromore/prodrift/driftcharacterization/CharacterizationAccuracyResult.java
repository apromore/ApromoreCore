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
package org.apromore.prodrift.driftcharacterization;

import java.util.List;

public class CharacterizationAccuracyResult {

	
	private int numOfCharacterizedDrifts = 0;
	private int TP = 0;
	private int FP = 0;
	private long processingTime_ms = 0;
	
	private List<String> mainActivityNamesList;
	
	
	public CharacterizationAccuracyResult(List<String> mainActivityNamesList) {
		super();
		this.mainActivityNamesList = mainActivityNamesList;
	}
	
	public int getNumOfCharacterizedDrifts() {
		return numOfCharacterizedDrifts;
	}
	public void setNumOfCharacterizedDrifts(int numOfCharacterizedDrifts) {
		this.numOfCharacterizedDrifts = numOfCharacterizedDrifts;
	}
	public int getTP() {
		return TP;
	}
	public void setTP(int tP) {
		TP = tP;
	}
	public int getFP() {
		return FP;
	}
	public void setFP(int fP) {
		FP = fP;
	}
	public long getProcessingTime_ms() {
		return processingTime_ms;
	}
	public void setProcessingTime_ms(long processingTime_ms) {
		this.processingTime_ms = processingTime_ms;
	}
	public List<String> getMainActivityNamesList() {
		return mainActivityNamesList;
	}
	public void setMainActivityNamesList(List<String> mainActivityNamesList) {
		this.mainActivityNamesList = mainActivityNamesList;
	}
	
	
	
	
}
