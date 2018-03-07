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

package au.edu.qut.bpmn.structuring.graph;

/**
 * Created by Adriano on 29/02/2016.
 */
public class Move {

    public enum MoveType {PULLUP, PUSHDOWN}

    private int cost;
    private MoveType type;
    private int toExtend;
    private int extension;

    public Move(int toExtend, int extension, MoveType type) {
        this.toExtend = toExtend;
        this.extension = extension;
        this.type = type;
        cost = Integer.MAX_VALUE;
    }

    public void setCost(int cost) { this.cost = cost; }

    public int getCost() { return cost; }
    public MoveType getType() { return type; }

    public int getExtension() { return extension; }
    public int getToExtend() { return toExtend; }


}
