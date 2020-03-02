/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.toolbox.clustering.topologicalsort;

import java.util.*;

/**
 * Created by conforti on 16/09/15.
 */
public class TopologicalSortGraph {

    private final HashMap<Node, ArrayList<Node>> adjList = new HashMap<>();
    private final ArrayList<Node> nodes;
    private LinkedList<Node> topoSorted;

    public TopologicalSortGraph(int numberNodes) {
        nodes = new ArrayList<>(numberNodes);
    }

    public void add(Node node) {
        if (!adjList.containsKey(node)) {
            adjList.put(node, new ArrayList<Node>());
            nodes.add(node);
        }
    }

    public void addNeighbor(Node from, Node to) {
        add(from);
        add(to);

        adjList.get(from).add(to);
        to.inDegree++;
        to.inNodes.add(from);
    }

    public void remove(Node node) {
        for (Node n: nodes) {
            for (Node x: adjList.get(n)) {
                if (x.equals(node)) removeNeighbor(n, x);
            }
        }
        adjList.remove(node);
        nodes.remove(node);
    }

    public void removeNeighbor(Node from, Node to) {
        adjList.get(from).remove(to);
        to.inDegree--;
        to.inNodes.remove(from);
    }

    /**
     * for DAGS only
     * @throws Exception
     */
    public int[] topologicalSort() throws Exception {
        /* L <-- Empty list that will contain the sorted elements */
        topoSorted = new LinkedList<>();

        /* Use set to keep track of permanently visited nodes
         * in constant time. Does have pointer overhead */
        HashSet<Node> visited = new HashSet<>();

        /* while there are unmarked nodes do */
        for (Node n: nodes) {

            /* select an unmarked node n
             * visit(n)
             */
            if (!visited.contains(n)) visit(n, visited);
        }

        int[] array = new int[topoSorted.size()];
        int i = 0;
        for(Node t : topoSorted) {
            array[i] = t.value;
            i++;
        }
        return array;
    }

    /* function: visit(node n) */
    public void visit(Node node, HashSet<Node> set) throws Exception {
        /* if n has a temporary mark then stop (not a DAG) */
        if (node.visited) {
            throw new Exception("graph cyclic");

        /* if n is not marked (i.e. has not been visited) then... */
        } else {

            /* mark n temporarily [using boolean field in node]*/
            node.visited = true;

            /* for each node m with an edge n to m do... */
            for (Node m: adjList.get(node)) {

                /* visit(m) */
                if (!set.contains(m)) visit(m, set);
            }

            /* mark n permanently */
            set.add(node);

            /* unmark n temporarily */
            node.visited = false;

            /* add n to head of L */
            topoSorted.addFirst(node);
        }
    }

    public void instantiateGraph() {
        Node seven = new Node(7);
        Node five = new Node(5);
        Node three = new Node(3);
        Node eleven = new Node(11);
        Node eight = new Node(8);
        Node two = new Node(2);
        Node nine = new Node(9);
        Node ten = new Node(10);

        addNeighbor(seven, eleven);
        addNeighbor(seven, eight);
        addNeighbor(five, eleven);
        addNeighbor(three, eight);
        addNeighbor(three, ten);
        addNeighbor(eleven, two);
        addNeighbor(eleven, nine);
        addNeighbor(eleven, ten);
        addNeighbor(eight, nine);
    }

    public void printGraph() {
        for (Node node: nodes) {
            System.out.print("from: " + node.value + " |  to: ");
            for (Node m: adjList.get(node)) {
                System.out.print(m.value + " ");
            }
            System.out.println();
        }
    }

    public class Node {
        private final int value;
        private boolean visited = false;
        private int inDegree = 0;
        private final ArrayList<Node> inNodes = new ArrayList<>();

        public Node (int value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        TopologicalSortGraph g = new TopologicalSortGraph(8);
        g.instantiateGraph();
        g.printGraph();

        try {
            g.topologicalSort();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (Node node: g.topoSorted) {
            System.out.print(node.value + " ");
        }
    }

}
