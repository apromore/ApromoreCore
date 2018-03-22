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


package com.processconfiguration.quaestio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class State {

	public TreeMap<String, String> vs=null;//not ordered
	public ArrayList<String> qs=null;
	public HashSet<String> t=null;
	public HashSet<String> f=null;
	
	public State(Set<String> fIDs){//Constructor for the initial state
//		fact valuation
		vs=new TreeMap<String, String>();
		t=new HashSet<String>();
		f=new HashSet<String>();
		for (String fID : fIDs)
			vs.put(fID,"unset");
//		no answered questions
		qs=new ArrayList<String>();
	}
	
	public State(State sIN){//Constructor for the other states
		this.vs=new TreeMap<String, String>(sIN.vs);
		this.qs=new ArrayList<String>(sIN.qs);
		this.t=new HashSet<String>(sIN.t);
		this.f=new HashSet<String>(sIN.f);
	}
}
