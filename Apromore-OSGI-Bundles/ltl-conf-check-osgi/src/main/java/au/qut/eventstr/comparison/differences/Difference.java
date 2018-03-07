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


/**
 * This is the main container of a difference.
 * It contains a sentence, the runs in model 1 and 2
 * expressing the discovered difference between the 
 * models. 
 */

public class Difference {
	private String sentence;
	private Runs runsM1;
	private Runs runsM2;
	private Match m1;
	private Match m2;
	
	public Difference(){}

	public Difference(Runs runsM1, Runs runsM2) {
		this.runsM1 = runsM1;
		this.runsM2 = runsM2;
	}
	
	public Difference(String sentence, Match m1, Match m2) {
		this.sentence = sentence;
		this.m1 = m1;
		this.m2 = m2;
	}
	
	public Difference(String sentence) {
		this.sentence = sentence;
	}

	public void setSentence(String sentence){
		this.sentence = sentence;
	}
	
	public Runs getRunsM1() {
		return runsM1;
	}

	public void setRunsM1(Runs runsM1) {
		this.runsM1 = runsM1;
	}

	public Runs getRunsM2() {
		return runsM2;
	}

	public void setRunsM2(Runs runsM2) {
		this.runsM2 = runsM2;
	}

	public String getSentence() {
		return sentence;
	}
	
	public Match getM1() {
		return m1;
	}

	public Match getM2() {
		return m2;
	}
	
	public String toString(){
		return sentence;
	}
}
