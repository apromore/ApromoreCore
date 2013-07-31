/*******************************************************************************
 * Copyright © 2006-2011, www.processconfiguration.com
 *   
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *      Marcello La Rosa - initial API and implementation, subsequent revisions
 *      Florian Gottschalk - individualizer for YAWL
 *      Possakorn Pitayarojanakul - integration with Configurator and Individualizer
 ******************************************************************************/
/**
 * Copyright © 2006-2009, Marcello La Rosa (marcello.larosa@gmail.com)
 *   
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *      Marcello La Rosa - initial API and implementation
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
