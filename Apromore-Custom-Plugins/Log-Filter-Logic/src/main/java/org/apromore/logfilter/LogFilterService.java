/*
 * Copyright © 2019-2020 The University of Melbourne.
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
package org.apromore.logfilter;

import java.util.List;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.deckfour.xes.model.XLog;

/**
 * @author Bruce Hoang Nguyen (11/07/2019)
 */
public interface LogFilterService {
	public XLog filter(XLog log, List<LogFilterCriterion> criteria);
}
