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

public class InformationGain {

	private Float DCG = 0f;
	private Float nDCG = 0f;
	
	
	public InformationGain(Float dCG, Float nDCG) {
		super();
		DCG = dCG;
		this.nDCG = nDCG;
	}
	public Float getDCG() {
		return DCG;
	}
	public void setDCG(Float dCG) {
		DCG = dCG;
	}
	public Float getnDCG() {
		return nDCG;
	}
	public void setnDCG(Float nDCG) {
		this.nDCG = nDCG;
	}
	@Override
	public String toString() {
		return "InformationGain [DCG=" + DCG + ", nDCG=" + nDCG + "]";
	}
	
	
	
	
	
}
