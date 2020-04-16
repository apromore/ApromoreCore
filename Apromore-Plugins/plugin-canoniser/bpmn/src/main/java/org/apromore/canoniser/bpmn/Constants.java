/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.canoniser.bpmn;

/**
 * BPMN canoniser constants.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public interface Constants {

    /**
     * Apromore URI.
     *
     * This is used for the definitions/@exporter attribute.
     */
    String APROMORE_URI = "http://apromore.org";

    /**
     * Apromore version.
     *
     * This is used for the definitions/@exporterVersion attribute.
     */
    String APROMORE_VERSION = "0.4";

    /**
     * BPMN 2.0 namespace.
     */
    String BPMN_NS = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    /**
     * Location of the BPMN 2.0 schema within the classpath.
     */
    String BPMN_XSD = "xsd/BPMN20.xsd";
}
