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

package org.apromore.plugin.portal.predictivemonitor;

// Third party packages
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

/**
 * Predictors are Kafka processors which generate annotations to log events.
 */
interface Predictor {

    /**
     * Generate the command line which will create the predictor.
     *
     * @param pythonCommand  the Python command line interpreter
     * @param kafkaHost  the URL of the Kafka broker, e.g. <code>localhost:9092</code>
     * @param prefixesTopic  name of the source topic providing case prefixes
     * @param predictionsTopic  name of the sink topic accepting predictions
     * @param tag  log identifier, e.g. <code>bpi_12</code>
     * @return list of command line arguments to execute a predictor
     */
    String[] getArgs(String pythonCommand, String kafkaHost, String prefixesTopic, String predictionsTopic, String tag);

    /**
     * Add any required table columns for this predictor's per-event statistics.
     *
     * @param head  add {@link Listheader} children to add required columns
     */
    void addHeaders(Listhead head);

    /**
     * This renderer will be used to add this predictor's columns to table rows.
     *
     * The columns should correspond to those added by the {@link #addHeaders} method.
     *
     * @param item  add {@link Listcell} children to add required colummns
     */
    void addCells(Listitem item, DataflowEvent event);
}
