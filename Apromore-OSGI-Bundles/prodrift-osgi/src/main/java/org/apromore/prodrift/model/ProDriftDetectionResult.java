/*
 * Copyright  2009-2018 The Apromore Initiative.
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

import java.awt.Image;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;

public class ProDriftDetectionResult {

	
	private Image pValuesDiagram;
	private JFreeChart lineChart;
	private List<BigInteger> driftPoints;
	private List<BigInteger> lastReadTrace;
	private List<BigInteger> startOfTransitionPoints;
	private List<BigInteger> endOfTransitionPoints;
	private List<Date> driftDates = new ArrayList<>();
	private List<String> driftStatements = new ArrayList<String>();
	private Map<BigInteger, List<String>> characterizationMap = new HashMap<>();
	private List<Boolean> isGradual = new ArrayList<Boolean>();
	
	
	public Image getpValuesDiagram() {
		return pValuesDiagram;
	}
	public void setpValuesDiagram(Image pValuesDiagram) {
		this.pValuesDiagram = pValuesDiagram;
	}
	public List<BigInteger> getDriftPoints() {
		return driftPoints;
	}
	public void setDriftPoints(List<BigInteger> driftPoints) {
		this.driftPoints = driftPoints;
	}
	public List<BigInteger> getLastReadTrace() {
		return lastReadTrace;
	}
	public void setLastReadTrace(List<BigInteger> lastReadTrace) {
		this.lastReadTrace = lastReadTrace;
	}
	public List<BigInteger> getStartOfTransitionPoints() {
		return startOfTransitionPoints;
	}
	public void setStartOfTransitionPoints(List<BigInteger> startOfTransitionPoints) {
		this.startOfTransitionPoints = startOfTransitionPoints;
	}
	public List<BigInteger> getEndOfTransitionPoints() {
		return endOfTransitionPoints;
	}
	public void setEndOfTransitionPoints(List<BigInteger> endOfTransitionPoints) {
		this.endOfTransitionPoints = endOfTransitionPoints;
	}
	public List<Date> getDriftDates() {
		return driftDates;
	}
	public void setDriftDates(List<Date> driftDates) {
		this.driftDates = driftDates;
	}
	public List<String> getDriftStatements() {
		return driftStatements;
	}
	public void setDriftStatements(List<String> driftStatements) {
		this.driftStatements = driftStatements;
	}
	public Map<BigInteger, List<String>> getCharacterizationMap() {
		return characterizationMap;
	}
	public void setCharacterizationMap(Map<BigInteger, List<String>> characterizationMap) {
		this.characterizationMap = characterizationMap;
	}
	public List<Boolean> getIsGradual() {
		return isGradual;
	}
	public void setIsGradual(List<Boolean> isGradual) {
		this.isGradual = isGradual;
	}
	public JFreeChart getLineChart() {
		return lineChart;
	}
	public void setLineChart(JFreeChart lineChart) {
		this.lineChart = lineChart;
	}
	
	
	
}
