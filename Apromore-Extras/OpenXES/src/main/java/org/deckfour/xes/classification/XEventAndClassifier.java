/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

/**
 * Composite event classifier, which can hold any number of lower-level
 * classifiers, concatenated with boolean AND logic.
 * 
 * This classifier will consider two events as equal, if all of its lower-level
 * classifiers consider them as equal.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XEventAndClassifier extends XEventAttributeClassifier {

	/**
	 * Creates a new instance.
	 * 
	 * @param comparators
	 *            Any number of lower-level classifiers, which are evaluated
	 *            with boolean AND logic. If multiple lower-level classifiers
	 *            use the same keys, this key is used only once in this
	 *            classifier.
	 */
	public XEventAndClassifier(XEventClassifier... comparators) {
		super("");

//		Collection<String> keys = new TreeSet<String>();
		Collection<String> keys = new ArrayList<String>();

//		Arrays.sort(comparators);
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(comparators[0].name());

		keys.addAll(Arrays.asList(comparators[0].getDefiningAttributeKeys()));

		for (int i = 1; i < comparators.length; i++) {
			sb.append(" AND ");
			sb.append(comparators[i].name());
			keys.addAll(Arrays
					.asList(comparators[i].getDefiningAttributeKeys()));
		}
		sb.append(")");
		this.name = sb.toString();
		this.keys = keys.toArray(new String[0]);
	}

}
