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

package ee.ut.bpmn.replayer;

import java.util.LinkedList;
import java.util.Set;

public class Trace<T> {
	LinkedList<T> log = new LinkedList<>();

	public Trace() {
		log = new LinkedList<>();
	}

	public Trace(LinkedList<T> strongCauses) {
		this.log = strongCauses;
	}
	
	public void addStrongCause(T s) {
		log.add(s);
	}
	
	public void addAllStrongCauses(Set<T> set) {
		log.addAll(set);
	}

	public Trace<T> clone(){
		return new Trace<>(new LinkedList<T>(log));
	}
	
	public LinkedList<T> getLog(){
		return log;
	}
}
