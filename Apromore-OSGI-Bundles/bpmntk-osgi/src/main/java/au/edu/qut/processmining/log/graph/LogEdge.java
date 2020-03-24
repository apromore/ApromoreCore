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

package au.edu.qut.processmining.log.graph;

import java.util.UUID;

/**
 * Created by Adriano on 15/06/2016.
 */

public class LogEdge implements Comparable {
    protected String id;
    protected String label;
    protected LogNode source;
    protected LogNode target;

    public LogEdge() {
        id = UUID.randomUUID().toString();
        source = null;
        target = null;
    }

    public LogEdge(LogNode source, LogNode target){
        id = UUID.randomUUID().toString();
        this.source = source;
        this.target = target;
    }
    public LogEdge(LogNode source, LogNode target, String label){
        id = UUID.randomUUID().toString();
        this.source = source;
        this.target = target;
        this.label = label;
    }

    public String getID() { return id; }

    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }

    public void setSource(LogNode source){ this.source = source; }
    public LogNode getSource(){ return source; }
    public int getSourceCode() { return source.getCode(); }


    public void setTarget(LogNode target) { this.target = target; }
    public LogNode getTarget(){ return target; }
    public int getTargetCode() { return target.getCode(); }

    @Override
    public int compareTo(Object o) {
        if( o instanceof LogEdge) return id.compareTo(((LogEdge)o).getID());
        else return -1;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof LogEdge) return id.equals(((LogEdge)o).getID());
        else return false;
    }
}
