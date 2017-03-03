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

import org.apromore.prodrift.config.BehaviorRelation;
import org.apromore.prodrift.config.FrequencyChange;
import org.apromore.prodrift.config.FrequencyChangeType;

public class RelationChange {

	private String firstActivity;
	private String secondActivity;
	private BehaviorRelation behaviorRelation;
	private FrequencyChange freqChange;
	private FrequencyChangeType freqChangeType;
	private float importance;
	
	
	
	
	public RelationChange(String firstActivity, String secondActivity, BehaviorRelation behaviorRelation,
			FrequencyChange freqChange, FrequencyChangeType freqChangeType, float importance) {
		super();
		this.firstActivity = firstActivity;
		this.secondActivity = secondActivity;
		this.behaviorRelation = behaviorRelation;
		this.freqChange = freqChange;
		this.freqChangeType = freqChangeType;
		this.importance = importance;
	}
	
	
	public String getFirstActivity() {
		return firstActivity;
	}
	public void setFirstActivity(String firstActivity) {
		this.firstActivity = firstActivity;
	}
	public String getSecondActivity() {
		return secondActivity;
	}
	public void setSecondActivity(String secondActivity) {
		this.secondActivity = secondActivity;
	}
	public BehaviorRelation getBehaviorRelation() {
		return behaviorRelation;
	}
	public void setBehaviorRelation(BehaviorRelation behaviorRelation) {
		this.behaviorRelation = behaviorRelation;
	}
	public FrequencyChange getFreqChange() {
		return freqChange;
	}
	public void setFreqChange(FrequencyChange freqChange) {
		this.freqChange = freqChange;
	}
	public FrequencyChangeType getFreqChangeType() {
		return freqChangeType;
	}
	public void setFreqChangeType(FrequencyChangeType freqChangeType) {
		this.freqChangeType = freqChangeType;
	}


	public float getImportance() {
		return importance;
	}


	public void setImportance(float importance) {
		this.importance = importance;
	}
	
	
	
	
}
