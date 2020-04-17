/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011, 2012 , 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.service.impl.models;

/**
 * Interface for test to have access to XML canonical model and annotation.
 *
 * Easier in a interface than reading from a file.
 */
public interface CanonicalWithAnnotationModel {

    public static final String CANONICAL_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<ns2:CanonicalProcess xmlns:ns2=\"http://www.apromore.org/CPF\" uri=\"2011072813324000\" version=\"0.1\" name=\"SAP_1\" author=\"fauvet\" creationDate=\"2011/07/28 13:31:47\" modificationDate=\"\">\n" +
            "    <Net id=\"2011072813324015\">\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:EventType\" id=\"2011072813324000\">\n" +
            "            <name>A</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:EventType\" id=\"2011072813324001\">\n" +
            "            <name>Deliveries need to be planned</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:EventType\" id=\"2011072813324002\">\n" +
            "            <name>P</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:EventType\" id=\"2011072813324003\">\n" +
            "            <name>Shipment is complete</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TaskType\" id=\"2011072813324004\">\n" +
            "            <name>B</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TaskType\" id=\"2011072813324005\">\n" +
            "            <name>Transporation planning and processing</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:EventType\" id=\"2011072813324007\">\n" +
            "            <name>Delivery is relevant for shipment</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:ANDJoinType\" id=\"2011072813324006\"/>\n" +
            "        <Edge id=\"2011072813324008\" sourceId=\"2011072813324000\" targetId=\"2011072813324004\"/>\n" +
            "        <Edge id=\"2011072813324009\" sourceId=\"2011072813324004\" targetId=\"2011072813324007\"/>\n" +
            "        <Edge id=\"2011072813324010\" sourceId=\"2011072813324001\" targetId=\"2011072813324006\"/>\n" +
            "        <Edge id=\"2011072813324011\" sourceId=\"2011072813324002\" targetId=\"2011072813324006\"/>\n" +
            "        <Edge id=\"2011072813324012\" sourceId=\"2011072813324007\" targetId=\"2011072813324006\"/>\n" +
            "        <Edge id=\"2011072813324013\" sourceId=\"2011072813324006\" targetId=\"2011072813324005\"/>\n" +
            "        <Edge id=\"2011072813324014\" sourceId=\"2011072813324005\" targetId=\"2011072813324003\"/>\n" +
            "    </Net>\n" +
            "    <attribute name=\"IntialFormat\" value=\"EPML\"/>\n" +
            "</ns2:CanonicalProcess>";

    public static final String ANNOTATION_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<ns2:Annotations xmlns:ns2=\"http://www.apromore.org/ANF\">\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324000\">\n" +
            "        <position x=\"310\" y=\"100\"/>\n" +
            "        <size width=\"81\" height=\"41\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324001\">\n" +
            "        <position x=\"165\" y=\"220\"/>\n" +
            "        <size width=\"81\" height=\"41\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324002\">\n" +
            "        <position x=\"35\" y=\"270\"/>\n" +
            "        <size width=\"81\" height=\"41\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324003\">\n" +
            "        <position x=\"165\" y=\"465\"/>\n" +
            "        <size width=\"81\" height=\"41\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324004\">\n" +
            "        <position x=\"310\" y=\"170\"/>\n" +
            "        <size width=\"81\" height=\"41\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324005\">\n" +
            "        <position x=\"165\" y=\"395\"/>\n" +
            "        <size width=\"81\" height=\"41\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324006\">\n" +
            "        <position x=\"195\" y=\"335\"/>\n" +
            "        <size width=\"21\" height=\"21\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324007\">\n" +
            "        <position x=\"310\" y=\"270\"/>\n" +
            "        <size width=\"81\" height=\"41\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324008\">\n" +
            "        <position x=\"350\" y=\"141\"/>\n" +
            "        <position x=\"350\" y=\"170\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324009\">\n" +
            "        <position x=\"350\" y=\"211\"/>\n" +
            "        <position x=\"350\" y=\"270\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324010\">\n" +
            "        <position x=\"205\" y=\"261\"/>\n" +
            "        <position x=\"205\" y=\"335\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324011\">\n" +
            "        <position x=\"75\" y=\"311\"/>\n" +
            "        <position x=\"195\" y=\"335\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324012\">\n" +
            "        <position x=\"350\" y=\"311\"/>\n" +
            "        <position x=\"216\" y=\"335\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324013\">\n" +
            "        <position x=\"205\" y=\"356\"/>\n" +
            "        <position x=\"205\" y=\"395\"/>\n" +
            "    </Annotation>\n" +
            "    <Annotation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:GraphicsType\" cpfId=\"2011072813324014\">\n" +
            "        <position x=\"205\" y=\"436\"/>\n" +
            "        <position x=\"205\" y=\"465\"/>\n" +
            "    </Annotation>\n" +
            "</ns2:Annotations>";
}
