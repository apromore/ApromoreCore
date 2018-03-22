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

/**
 * Kafka processors managed by a {@link Dataflow} instance.
 */
interface DataflowElement {

    /**
     * @param kafkaHost  the URL of the Kafka broker, e.g. <code>localhost:9092</code>
     * @param prefixesTopic  name of the source topic providing case prefixes
     * @param predictionsTopic  name of the sink topic accepting predictions
     */
    void start(String kafkaHost, String prefixesTopic, String predictionsTopic) throws PredictorException;

    /**
     * Removes any instances previously created by the {@link #start} method.
     */
    void stop();

    /**
     * Free resources associated with this instance.
     */
    void delete();
}
