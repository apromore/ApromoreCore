/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.toolbox.clustering.tasksim;

public class SimilarPair {
	
	private int vid1;
	private int vid2;
	private double sim;
	
	public SimilarPair(int vid1, int vid2, double sim) {
		this.vid1 = vid1;
		this.vid2 = vid2;
		this.sim = sim;
	}
	
	public int getVid1() {
		return vid1;
	}
	
	public int getVid2() {
		return vid2;
	}

	public double getSim() {
		return sim;
	}

	@Override
	public int hashCode() {
		return vid1 - vid2;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj == null || !(obj instanceof SimilarPair)) {
			return false;
		}
		
		SimilarPair otherPair = (SimilarPair) obj;
		
		if (vid1 == otherPair.getVid1() && vid2 == otherPair.getVid2()) {
			return true;
		}
		
//		if (vid1 == otherPair.getVid2() && vid2 == otherPair.getVid1()) {
//			return true;
//		}
		
		return false;
	}

	@Override
	public String toString() {
		return vid1 + " - " + vid2 + " : " + sim;
	}
}
