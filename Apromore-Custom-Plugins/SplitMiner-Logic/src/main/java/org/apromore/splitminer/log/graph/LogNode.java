/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016, 2017 Adriano Augusto.
 * Copyright (C) 2017 Queensland University of Technology.
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

package org.apromore.splitminer.log.graph;

import java.util.UUID;

/**
 * Created by Adriano on 15/06/2016.
 */
public class LogNode implements Comparable {
    protected String id;
    protected String label;
    protected int code;

    protected int frequency;
    protected int startFrequency;
    protected int endFrequency;

    public LogNode() {
        id = UUID.randomUUID().toString();
        label = "null";
        frequency = 0;
        startFrequency = 0;
        endFrequency = 0;
    }

    public LogNode(String label) {
        id = UUID.randomUUID().toString();
        frequency = 0;
        startFrequency = 0;
        endFrequency = 0;
        this.label = label;
    }
    public LogNode(String label, int code) {
        id = Integer.toString(code);
        frequency = 0;
        startFrequency = 0;
        endFrequency = 0;
        this.label = label;
        this.code = code;
    }

    public String getID() { return id; }

    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }

//    public void setCode(int code) { this.code = code; }
    public int getCode() { return code; }

    public void increaseFrequency() { frequency++; }
    public void increaseFrequency(int amount) { frequency += amount; }

    public int getFrequency(){ return frequency; }

    public void incStartFrequency() { startFrequency++; }
    public void incEndFrequency() { endFrequency++; }

    public int getStartFrequency(){ return startFrequency;}
    public int getEndFrequency(){ return endFrequency;}

    public boolean isStartEvent() { return startFrequency != 0; }
    public boolean isEndEvent() { return endFrequency != 0; }

    @Override
    public int compareTo(Object o) {
        if( o instanceof LogNode) return id.compareTo(((LogNode)o).getID());
        else return -1;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof LogNode) return id.equals(((LogNode)o).getID());
        else return false;
    }

}
