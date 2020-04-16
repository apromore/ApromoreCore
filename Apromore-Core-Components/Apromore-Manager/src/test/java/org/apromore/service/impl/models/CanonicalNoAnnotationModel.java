/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011, 2012 , 2015 - 2017 Queensland University of Technology.
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

package org.apromore.service.impl.models;

/**
 * Interface for test to have access to XML canonical model and annotation.
 *
 * Easier in a interface than reading from a file.
 */
public interface CanonicalNoAnnotationModel {

    public static final String CANONICAL_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<ns2:CanonicalProcess xmlns:ns2=\"http://www.apromore.org/CPF\" uri=\"20110726160417505\" version=\"0.1\" name=\"model1\" author=\"arthur\" creationDate=\"2011/07/26 16:04:05\" modificationDate=\"\">\n" +
            "    <Net id=\"20110726160417505\">\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:XORSplitType\" id=\"20110726160417506\"/>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:XORSplitType\" id=\"20110726160417507\"/>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:XORSplitType\" id=\"20110726160417508\"/>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:XORJoinType\" id=\"20110726160417509\"/>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:ANDSplitType\" id=\"20110726160417510\"/>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:ANDJoinType\" id=\"20110726160417511\"/>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:MessageType\" id=\"20110726160417512\">\n" +
            "            <name>Phone call</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:EventType\" id=\"20110726160417513\">\n" +
            "            <name></name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TaskType\" id=\"20110726160417514\">\n" +
            "            <name>Check if sufficient information is available</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TaskType\" id=\"20110726160417515\">\n" +
            "            <name>Register claim</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TaskType\" id=\"20110726160417516\">\n" +
            "            <name>Determine likelihood of claim</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TaskType\" id=\"20110726160417517\">\n" +
            "            <name>Assess claim</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TaskType\" id=\"20110726160417518\">\n" +
            "            <name>Advice claimant on reimbursement</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TaskType\" id=\"20110726160417519\">\n" +
            "            <name>Initiate payment</name>\n" +
            "        </Node>\n" +
            "        <Node xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TaskType\" id=\"20110726160417520\">\n" +
            "            <name>Close claim</name>\n" +
            "        </Node>\n" +
            "        <Edge id=\"20110726160417521\" sourceId=\"20110726160417512\" targetId=\"20110726160417514\"/>\n" +
            "        <Edge id=\"20110726160417522\" sourceId=\"20110726160417514\" targetId=\"20110726160417506\"/>\n" +
            "        <Edge id=\"20110726160417523\" condition=\"Information available\" sourceId=\"20110726160417506\" targetId=\"20110726160417515\"/>\n" +
            "        <Edge id=\"20110726160417524\" sourceId=\"20110726160417515\" targetId=\"20110726160417516\"/>\n" +
            "        <Edge id=\"20110726160417525\" sourceId=\"20110726160417516\" targetId=\"20110726160417507\"/>\n" +
            "        <Edge id=\"20110726160417526\" condition=\"liable\" sourceId=\"20110726160417507\" targetId=\"20110726160417517\"/>\n" +
            "        <Edge id=\"20110726160417527\" sourceId=\"20110726160417517\" targetId=\"20110726160417508\"/>\n" +
            "        <Edge id=\"20110726160417528\" condition=\"Claim&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;accepted\" sourceId=\"20110726160417508\" targetId=\"20110726160417510\"/>\n" +
            "        <Edge id=\"20110726160417529\" sourceId=\"20110726160417510\" targetId=\"20110726160417518\"/>\n" +
            "        <Edge id=\"20110726160417530\" sourceId=\"20110726160417510\" targetId=\"20110726160417519\"/>\n" +
            "        <Edge id=\"20110726160417531\" sourceId=\"20110726160417519\" targetId=\"20110726160417511\"/>\n" +
            "        <Edge id=\"20110726160417532\" sourceId=\"20110726160417518\" targetId=\"20110726160417511\"/>\n" +
            "        <Edge id=\"20110726160417533\" sourceId=\"20110726160417511\" targetId=\"20110726160417520\"/>\n" +
            "        <Edge id=\"20110726160417534\" condition=\"Not enough information\" sourceId=\"20110726160417506\" targetId=\"20110726160417509\"/>\n" +
            "        <Edge id=\"20110726160417535\" sourceId=\"20110726160417520\" targetId=\"20110726160417509\"/>\n" +
            "        <Edge id=\"20110726160417536\" sourceId=\"20110726160417509\" targetId=\"20110726160417513\"/>\n" +
            "        <Edge id=\"20110726160417537\" condition=\"Claim rejected\" sourceId=\"20110726160417508\" targetId=\"20110726160417509\"/>\n" +
            "        <Edge id=\"20110726160417538\" condition=\"non liable\" sourceId=\"20110726160417507\" targetId=\"20110726160417509\"/>\n" +
            "    </Net>\n" +
            "</ns2:CanonicalProcess>";
}
