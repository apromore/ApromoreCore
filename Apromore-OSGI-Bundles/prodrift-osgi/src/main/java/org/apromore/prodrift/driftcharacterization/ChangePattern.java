/*
 * Copyright  2009-2018 The Apromore Initiative.
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

import java.util.ArrayList;
import java.util.List;

public class ChangePattern {
	
	private String name;
	private List<RelationChange> relationChanges = new ArrayList<>();
	
	
	
	public ChangePattern(String name, List<RelationChange> relationChanges) {
		super();
		this.name = name;
		this.relationChanges = relationChanges;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<RelationChange> getRelationChanges() {
		return relationChanges;
	}
	public void setRelationChanges(List<RelationChange> relationChanges) {
		this.relationChanges = relationChanges;
	}
	
	

}
