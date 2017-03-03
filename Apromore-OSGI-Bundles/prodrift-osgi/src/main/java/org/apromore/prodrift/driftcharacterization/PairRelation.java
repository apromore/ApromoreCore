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

import org.apromore.prodrift.config.BehaviorRelation;
import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.Pair;

public class PairRelation 
{
	
	Pair<XEventClass, XEventClass> pair = null;
	BehaviorRelation behaviourRelation;
	
	public PairRelation(){}
	
	public PairRelation(Pair<XEventClass, XEventClass> pair, 
			BehaviorRelation behaviourRelation) {
		
		this.pair = pair;
		this.behaviourRelation = behaviourRelation;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((behaviourRelation == null) ? 0 : behaviourRelation
						.hashCode());
		result = prime * result + ((pair == null) ? 0 : pair.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PairRelation other = (PairRelation) obj;
		if (behaviourRelation != other.behaviourRelation)
			return false;
		if (pair == null) {
			if (other.pair != null)
				return false;
		} else if (!pair.equals(other.pair))
			return false;
		return true;
	}
	
	
	
	public Pair<XEventClass, XEventClass> getPair() {
		return pair;
	}
	public void setPair(Pair<XEventClass, XEventClass> pair) {
		this.pair = pair;
	}
	public BehaviorRelation getBehaviourRelation() {
		return behaviourRelation;
	}
	public void setBehaviourRelation(BehaviorRelation behaviourRelation) {
		this.behaviourRelation = behaviourRelation;
	}
	@Override
	public String toString() {
		return pair.getFirst().getId() + "_" + behaviourRelation + "_" + pair.getSecond().getId();
	}
	
	
	

}
