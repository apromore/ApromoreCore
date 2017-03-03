/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

public class RelationImpact {

	private Float coefficient = 0f;
	private Integer coefficient_ori = 0;
	private int sumFreqBeforeDrift = 0;
	private int sumFreqAfterDrift = 0;
	private int repFreqBeforeDrift = 0;
	private int repFreqAfterDrift = 0;
	private boolean amongTopRelations = false;
	private int rank = 0;
	
	private int avgFreqBeforeDrift = 0;
	private int avgFreqAfterDrift = 0;
	private int minFreqBeforeDrift = Integer.MAX_VALUE;
	private int minFreqAfterDrift = Integer.MAX_VALUE;
	private int maxFreqBeforeDrift = Integer.MIN_VALUE;
	private int maxFreqAfterDrift = Integer.MIN_VALUE;
	
	private Float testStatistic = 0f;
	private Float testStatistic_ori = 0f;
	private Float p_value = 0f;
	
	private Float relChangeMag = 0f; // relative change magnitude
	
	
	public Float getCoefficient() {
		return coefficient;
	}
	public void setCoefficient(Float coefficient) {
		this.coefficient = coefficient;
	}
	public int getSumFreqBeforeDrift() {
		return sumFreqBeforeDrift;
	}
	public void setSumFreqBeforeDrift(int sumFreqBeforeDrift) {
		this.sumFreqBeforeDrift = sumFreqBeforeDrift;
	}
	public int getSumFreqAfterDrift() {
		return sumFreqAfterDrift;
	}
	public void setSumFreqAfterDrift(int sumFreqAfterDrift) {
		this.sumFreqAfterDrift = sumFreqAfterDrift;
	}
	public int getRepFreqBeforeDrift() {
		return repFreqBeforeDrift;
	}
	public void setRepFreqBeforeDrift(int repFreqBeforeDrift) {
		this.repFreqBeforeDrift = repFreqBeforeDrift;
	}
	public int getRepFreqAfterDrift() {
		return repFreqAfterDrift;
	}
	public void setRepFreqAfterDrift(int repFreqAfterDrift) {
		this.repFreqAfterDrift = repFreqAfterDrift;
	}
	public Integer getCoefficient_ori() {
		return coefficient_ori;
	}
	public void setCoefficient_ori(Integer coefficient_ori) {
		this.coefficient_ori = coefficient_ori;
	}
	public boolean isAmongTopRelations() {
		return amongTopRelations;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public void setAmongTopRelations(boolean amongTopRelations) {
		this.amongTopRelations = amongTopRelations;
	}
	public int getAvgFreqBeforeDrift() {
		return avgFreqBeforeDrift;
	}
	public void setAvgFreqBeforeDrift(int avgFreqBeforeDrift) {
		this.avgFreqBeforeDrift = avgFreqBeforeDrift;
	}
	public int getAvgFreqAfterDrift() {
		return avgFreqAfterDrift;
	}
	public void setAvgFreqAfterDrift(int avgFreqAfterDrift) {
		this.avgFreqAfterDrift = avgFreqAfterDrift;
	}
	public int getMinFreqBeforeDrift() {
		return minFreqBeforeDrift;
	}
	public void setMinFreqBeforeDrift(int minFreqBeforeDrift) {
		this.minFreqBeforeDrift = minFreqBeforeDrift;
	}
	public int getMinFreqAfterDrift() {
		return minFreqAfterDrift;
	}
	public void setMinFreqAfterDrift(int minFreqAfterDrift) {
		this.minFreqAfterDrift = minFreqAfterDrift;
	}
	public int getMaxFreqBeforeDrift() {
		return maxFreqBeforeDrift;
	}
	public void setMaxFreqBeforeDrift(int maxFreqBeforeDrift) {
		this.maxFreqBeforeDrift = maxFreqBeforeDrift;
	}
	public int getMaxFreqAfterDrift() {
		return maxFreqAfterDrift;
	}
	public void setMaxFreqAfterDrift(int maxFreqAfterDrift) {
		this.maxFreqAfterDrift = maxFreqAfterDrift;
	}
	
	public Float getTestStatistic() {
		return testStatistic;
	}
	public void setTestStatistic(Float testStatistic) {
		this.testStatistic = testStatistic;
	}
	public Float getP_value() {
		return p_value;
	}
	public void setP_value(Float p_value) {
		this.p_value = p_value;
	}
	public Float getTestStatistic_ori() {
		return testStatistic_ori;
	}
	public void setTestStatistic_ori(Float testStatistic_ori) {
		this.testStatistic_ori = testStatistic_ori;
	}
	public Float getRelChangeMag() {
		return relChangeMag;
	}
	public void setRelChangeMag(Float relChangeMag) {
		this.relChangeMag = relChangeMag;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		String toStr = "c:" + this.coefficient + " c_ori:" + this.coefficient_ori + " b:" + (int)this.avgFreqBeforeDrift + 
				" a:" + (int)this.avgFreqAfterDrift + /*" b:" + "(min:" + minFreqBeforeDrift +
				", max:" + maxFreqBeforeDrift + ")" + " a:" + "(min:" + minFreqAfterDrift +
				", max:" + maxFreqAfterDrift + ")" + */ " top:" + amongTopRelations;
		return toStr;
	}
	
	public String toString2() {
		// TODO Auto-generated method stub
		
		String toStr = "relChMag:" + this.relChangeMag + " ts_ori:" + this.testStatistic_ori + " p-value:" + this.p_value + " b:" + (int)this.avgFreqBeforeDrift + 
				" a:" + (int)this.avgFreqAfterDrift + /*" b:" + "(min:" + minFreqBeforeDrift +
				", max:" + maxFreqBeforeDrift + ")" + " a:" + "(min:" + minFreqAfterDrift +
				", max:" + maxFreqAfterDrift + ")" + */ " top:" + amongTopRelations;
		return toStr;
	}
	
	

}
