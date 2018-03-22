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

package au.edu.qut.bpmn.structuring.core;

import au.edu.qut.bpmn.structuring.graph.Graph;
import au.edu.qut.bpmn.structuring.graph.Move;

import java.util.*;

/**
 * Created by Adriano on 29/02/2016.
 */
public class StructuringState implements Comparable<StructuringState> {
    private Graph rigid;
    private int cost;
    private boolean solved;
    private boolean dead;
    private boolean fake;
    private boolean valid;
    private boolean jsWarning;

    public StructuringState() {
        dead = true;
        solved = false;
        cost = Integer.MIN_VALUE;
        rigid = null;
        fake = true;
    }

    public StructuringState(Graph rigid, int cost) {
        this.rigid = rigid;
        this.cost = cost;
        this.solved = (rigid.getAlivePaths().size() == 1);
        this.dead = false;
        this.fake = false;
        this.valid = rigid.isValid();
        this.jsWarning = rigid.containsJoinSplit();

        if( jsWarning && solved ) {
            dead = true;
            solved = false;
        }
    }

    public PriorityQueue<StructuringState> generateChildren() {
        PriorityQueue<StructuringState> children = new PriorityQueue<>();
        StructuringState nextState;
        Graph evolution;

        String entry = rigid.getEntry();
        String exit = rigid.getExit();
        String gate = rigid.getEntry();
        String child;

        LinkedList<String> toVisit = new LinkedList<>();
        HashSet<String> visited = new HashSet<>();

        List<Integer> outgoing;
        List<Integer> incoming;

        int iSize;
        int oSize;

        int next;
        int prev;

        Move possibleMove;
        ArrayList<Move> moves;
        int mCounter;
        int totalCost;
        boolean apply;

        toVisit.addFirst(gate);
        visited.add(exit);

        while( toVisit.size() != 0 ) {

            gate = toVisit.removeFirst();
            visited.add(gate);

            outgoing = rigid.getOutgoing(gate);
            incoming = rigid.getIncoming(gate);

            oSize = outgoing.size();
            iSize = incoming.size();

            for( int pid : outgoing ) {
                child = rigid.getPath(pid).getExit();
                if( !visited.contains(child) && !toVisit.contains(child) ) toVisit.addLast(child);
            }

            //if( gate.equals(entry) || gate.equals(exit) ) continue;

            //System.out.println("DEBUG - visiting: " + gate + " (i: " + iSize + " o: " + oSize + ")");

                /* NOTE: these case not need evaluation
                 * (iSize == 1) && (oSize == 1) impossible
                 * (iSize == 0) && (oSize == 0) impossible
                 * (iSize == 0) && (oSize == 1) possible only for main entry
                 * (iSize == 1) && (oSize == 0) possible only for main exit
                 */

            if( (iSize > 1) && (oSize > 1) ) continue;

            if( (oSize == 1) && (iSize != 0) && !gate.equals(exit) ) {
                //this means iSize > 1
                evolution = new Graph(rigid);
                moves = new ArrayList<>();
                next = outgoing.get(0); //the only outgoing path;

                for( int pid : incoming ) {
                    if( !rigid.isMoveValid(pid, next, Move.MoveType.PUSHDOWN) ) continue;
                    possibleMove = new Move(pid, next, Move.MoveType.PUSHDOWN);
                    possibleMove.setCost(rigid.getPath(next).getWeight());
                    moves.add(possibleMove);
                }

                apply = false;
                totalCost = this.cost;
                mCounter = moves.size();
                for( Move m : moves ) {
                    if( mCounter == 1 ) break; //we cannot apply all the moves, but all minus one.
                    if( evolution.applyMove(m, gate) ) {
                        totalCost+=m.getCost();
                        apply = true;
                        mCounter--;
                    }
                }

                if( apply ) {
                    evolution.simplify();
                    nextState = new StructuringState(evolution, totalCost);
                    children.add(nextState);
                }
                continue; //we do not need to analise the pull-up case
            }

            if( (oSize != 0) && (iSize == 1) && !gate.equals(entry) ) {
                //this means oSize > 1
                evolution = new Graph(rigid);
                moves = new ArrayList<>();
                prev = incoming.get(0); //the only incoming path;

                for( int pid : outgoing ) {
                    if( !rigid.isMoveValid(pid, prev, Move.MoveType.PULLUP) ) continue;
                    possibleMove = new Move(pid, prev, Move.MoveType.PULLUP);
                    possibleMove.setCost(rigid.getPath(prev).getWeight());
                    moves.add(possibleMove);
                }

                apply = false;
                totalCost = this.cost;
                mCounter = moves.size();
                for( Move m : moves ) {
                    if( mCounter == 1 ) break; //we cannot apply all the moves, but all minus one.
                    if( evolution.applyMove(m, gate) ) {
                        totalCost+=m.getCost();
                        apply = true;
                        mCounter--;
                    }
                }

                if( apply ) {
                    evolution.simplify();
                    nextState = new StructuringState(evolution, totalCost);
                    children.add(nextState);
                }
            }
        }

        if( children.size() == 0 ) {
            //System.out.println("WARNING - no more move allowed. new zombie.");
            this.kill();
            children.add(this);
        }

        return children;
    }

    public int getCost() { return cost; }
    public boolean isSolved() { return solved; }
    public boolean isDead() { return dead; }
    public boolean isFake() { return fake; }
    public Graph getGraph() { return rigid; }
    public void kill() { this.dead = !solved; }
    public boolean isValid() { return valid; }

    @Override
    public boolean equals(Object o) {
        if( !(o instanceof  StructuringState) ) return false;
        else return ((cost == ((StructuringState) o).cost) && (jsWarning == jsWarning) &&
                    ((solved == ((StructuringState) o).solved) || (dead == ((StructuringState) o).dead)));
    }

    @Override
    public int compareTo(StructuringState s) {
        if( dead && s.dead && !jsWarning && !s.jsWarning ) {
            if( s.rigid.getAlivePaths().size() == rigid.getAlivePaths().size() ) return s.cost - this.cost;
            return (s.rigid.getAlivePaths().size() - rigid.getAlivePaths().size());
        }

        if( jsWarning && !s.jsWarning ) return 1;
        if( !jsWarning && s.jsWarning ) return -1;

        if( this.cost == s.cost ) return s.rigid.getMove() - this.rigid.getMove();
        return (this.cost - s.cost);
    }

}