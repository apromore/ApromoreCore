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

package org.apromore.plugin.portal.predictivemonitor;

// Java 2 Standard Edition
import java.util.List;

// Third party packages
import org.zkoss.zul.ListModelList;

/**
 * Placeholder for database-backed entities.
 */
class Persistent {

    final static List<Dataflow> persistentDataflowList = new java.util.ArrayList<>();

    /** This is static only because I've been too lazy to implement proper persistence for it yet. */
    final static ListModelList<Dataflow> dataflows = new ListModelList<>(persistentDataflowList, true);

    /** This is static only because I've been too lazy to implement proper persistence for it yet. */
    final static ListModelList<Predictor> predictors = new ListModelList<>();

    static {
        dataflows.setMultiple(true);
        predictors.setMultiple(true);
    }
}
