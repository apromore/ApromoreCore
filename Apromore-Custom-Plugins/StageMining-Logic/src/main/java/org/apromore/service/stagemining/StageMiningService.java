/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.stagemining;

import org.deckfour.xes.model.XLog;
import org.processmining.stagemining.models.DecompositionTree;

/**
 * Interface for the Process Performance Mining Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author barca
 */
public interface StageMiningService {

    public DecompositionTree mine(XLog log, int minStageSize) throws Exception;

}
