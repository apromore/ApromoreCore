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
package org.apromore.prodrift.config;

public class RelationFrequency {

	
	private int refWinFreq = 0;
	private int detWinFreq = 0;
	private BehaviorRelation relationType_detWin = null;
	private BehaviorRelation relationType_refWin = null;
	
	
	public int getRefWinFreq() {
		return refWinFreq;
	}
	public void setRefWinFreq(int refWinFreq) {
		this.refWinFreq = refWinFreq;
	}
	public int getDetWinFreq() {
		return detWinFreq;
	}
	public void setDetWinFreq(int detWinFreq) {
		this.detWinFreq = detWinFreq;
	}
	public BehaviorRelation getRelationType_detWin() {
		return relationType_detWin;
	}
	public void setRelationType_detWin(BehaviorRelation relationType_detWin) {
		this.relationType_detWin = relationType_detWin;
	}
	public BehaviorRelation getRelationType_refWin() {
		return relationType_refWin;
	}
	public void setRelationType_refWin(BehaviorRelation relationType_refWin) {
		this.relationType_refWin = relationType_refWin;
	}
	
	
	
}
