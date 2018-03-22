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

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Adriano on 28/02/2016.
 */
public class Path {
//    private static final Logger LOGGER = LoggerFactory.getLogger(Path.class);

    private int PID;
    private String entry, exit;
    private int weight;
    private Set<Integer> brothers;
    private Set<Integer> reverseBrothers;
    private LinkedList<Integer> chain;
    private boolean loop;

    private boolean hasBrothers;

    public Path(int PID, int oPID, String entry, String exit, int weight, boolean loop) {
        //this wraps an originalPath(that is a sequence of tasks) into a Path
        this.PID = PID;
        this.entry = new String(entry);
        this.exit = new String(exit);
        this.weight = weight;
        brothers = new HashSet<>();
        reverseBrothers = new HashSet<>();
        hasBrothers = false;
        chain = new LinkedList<>();

        chain.addLast(oPID);

        this.loop = loop;
    }

    public Path(int PID, Path first, Path second) {
        //this creates a new path concatenating two other paths: first and second
        this.PID = PID;
        this.entry = new String(first.entry);
        this.exit = new String(second.exit);
        this.weight = first.weight + second.weight;
        brothers = new HashSet<>();
        reverseBrothers = new HashSet<>();
        hasBrothers = false;
        chain = new LinkedList<>();

        chain.addLast(first.PID);
        chain.addLast(second.PID);

        loop = (first.loop || second.loop);

    }

    public Path(int PID, Path mould) {
        //duplication of a path, to call before extend a path
        //this creates a new path equal to the given mould
        this.PID = PID;
        this.entry = new String(mould.entry);
        this.exit = new String(mould.exit);
        this.weight = mould.weight;
        brothers = new HashSet<>(mould.brothers);
        reverseBrothers = new HashSet<>(mould.reverseBrothers);
        hasBrothers = ((brothers.size() != 0) || (reverseBrothers.size() != 0)) ;
        chain = new LinkedList<>();

        int length = mould.chain.size();
        for(int i = 0; i < length; i++) chain.addLast(mould.chain.get(i));

        loop = mould.loop;
    }

    public int getPID() { return PID; }
    public int getWeight() { return weight; }
    public String getEntry() { return entry; }
    public String getExit() { return exit; }
    public void setEntry(String entry) { this.entry = entry; }
    public void setExit(String exit) { this.exit = exit; }
    public List<Integer> getChain() { return chain; }
    public Set<Integer> getBrothers() { return brothers; }
    public Set<Integer> getReverseBrothers() { return reverseBrothers; }

    public boolean isLoop() { return loop; }
    public void setLoop() { loop = true; }

    public boolean addBrother(Path brother) {
        //double check brotherhood

        if( brother.entry.equals(this.entry) && brother.exit.equals(this.exit) ) {
            brothers.add(brother.PID);
            this.weight += brother.weight;
            hasBrothers = true;
            //System.out.println("DEBUG - " + PID + ": got a new brother (" + brother.getPID() + ")");
            return true;
        } else {
            System.out.println("ERROR - attempt to merge two NO brothers: " + this.PID + " - " + brother.PID);
            return false;
        }
    }

    public boolean addReverseBrother(Path reverseBrother) {
        //double check brotherhood
        if( reverseBrother.entry.equals(this.exit) && reverseBrother.exit.equals(this.entry) ) {
            reverseBrothers.add(reverseBrother.PID);
            this.weight += reverseBrother.weight;
            hasBrothers = true;
            //System.out.println("DEBUG - " + PID + ": got a new reverse brother (" + reverseBrother.getPID() + ")");
            return true;
        } else {
            System.out.println("ERROR - attempt to merge two NO reverse brothers: " + this.PID + " - " + reverseBrother.PID);
            return false;
        }
    }

    public boolean canConcat() { return !hasBrothers; }

    public boolean hasLoop() { return (!reverseBrothers.isEmpty() || loop); }

    public boolean concat(Path next) {
        //concat this with next
        if( this.exit.equals(next.entry) && !hasBrothers ) {
            chain.addLast(next.PID);
            this.exit = next.exit;
            this.weight += next.weight;
            loop = (next.isLoop() || loop);
            return true;
        } else {
            System.out.println("ERROR - attempt to concatenate two NO consecutive paths: " + this.PID + " - " + next.PID);
            return false;
        }
    }

    public String reverse() {
        String entry = this.entry;
        String exit = this.exit;
        Set<Integer> brothers = this.brothers;
        Set<Integer> reverseBrothers = this.reverseBrothers;

        this.entry = exit;
        this.exit = entry;

        this.reverseBrothers = brothers;
        this.brothers = reverseBrothers;

        Collections.reverse(chain);

        return this.exit;
    }


}
