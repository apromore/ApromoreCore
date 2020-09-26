/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.etlplugin.portal.models.joinTableModel;

import org.jooq.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.jooq.impl.DSL.*;

/**
 * Join is used to create Join String using TableJoin Graph and running breadth
 * first search on it.
 */
@Component
public class Join {
    private List<JoinTable> tables;

    /**
     * Get Joining string for all the table inputs.
     *
     * @param joinTables List of all the join table pairs
     * @return The join step for all the tables
     */
    public Table<?> getTable(
        List<List<String>> joinTables
    ) {
        createJoinTables(joinTables);
        return buildJoins();
    }

    /**
     * Create the Join Table Graph.
     *
     * @param joinTables List of the tables to join
     */
    private void createJoinTables(List<List<String>> joinTables) {
        tables = new ArrayList<>();

        // Init table list
        for (List<String> row : joinTables) {
            String table1 = row.get(0);
            String key1 = row.get(1);
            String table2 = row.get(2);
            String key2 = row.get(3);
            String joinType = row.get(4);

            addTablePair(table1, key1, table2, key2, joinType);
        }
    }

    /**
     * Create the Join Table or add parameters to the existing ones.
     *
     * @param tableName        Table name
     * @param key              Key name
     * @param forwardTableName Next table to join name
     * @return Join Table node for graph
     */
    private JoinTable getJoinTable(
            String tableName,
            String key,
            String forwardTableName
    ) {
        JoinTable joinTable = null;

        // If the joinTable already exists in the graph
        for (JoinTable table : tables) {
            if (table.getTableName().equals(tableName)) {
                joinTable = table;
                break;
            }
        }

        // Create a new JoinTable node in the graph
        if (joinTable == null) {
            joinTable = new JoinTable(tableName, key, forwardTableName);
            tables.add(joinTable);
        } else {
            joinTable.addKey(forwardTableName, key);
        }

        return joinTable;
    }

    /**
     * Add the table pain in the Join Table Graph.
     *
     * @param table1   TableA
     * @param key1     TableA key that connects TableB
     * @param table2   TableB
     * @param key2     TableB key that connects TableA
     * @param joinType Type of join for the table
     */
    private void addTablePair(
            String table1,
            String key1,
            String table2,
            String key2,
            String joinType
    ) {
        // Create tables graph
        JoinTable joinTable1 = getJoinTable(table1, key1, table2);
        JoinTable joinTable2 = getJoinTable(table2, key2, table1);

        // Add child and joinType
        joinTable1.addChild(joinTable2);
        joinTable2.setJoinType(joinType);
    }

    /**
     * Find the starting JoinTable of the Graph.
     *
     * @return Starting node of Join Table
     */
    private JoinTable findStartingTable() {

        for (JoinTable table : tables) {
            if (table.getJoinType() == null) {
                return table;
            }
        }

        return null;
    }

    /**
     * Get the preceding JoinTable node.
     *
     * @param table Join table
     * @return preceding Join table
     */
    private JoinTable getPrecedingTable(JoinTable table) {

        for (JoinTable jTable : tables) {
            List<JoinTable> childTables = jTable.getChildTables();
            if (childTables != null) {
                for (JoinTable jt : childTables) {
                    if (table.equals(jt)) {
                        return jTable;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Apply BFS and find the sequence of the Join traversal.
     *
     * @return JoinTable sequence to traverse
     */
    private ArrayList<JoinTable> calculateJoinSequenceBfs() {
        Queue<JoinTable> queue = new LinkedList<>();
        ArrayList<JoinTable> joinSequence = new ArrayList<>();
        JoinTable startTable = findStartingTable();

        queue.add(startTable);

        // Breath First Search
        while (!queue.isEmpty()) {
            JoinTable current = queue.remove();
            List<JoinTable> childTables = current.getChildTables();

            if (childTables != null) {
                queue.addAll(childTables);
            }

            joinSequence.add(current);
        }

        return joinSequence;
    }

    /**
     * Build the query based on the JoinTable sequence.
     *
     * @return The query after joins have been applied
     */
    private Table<?> buildJoins() {
        ArrayList<JoinTable> joinSequence = calculateJoinSequenceBfs();

        Table<?> joinStep = table(String.format(
                "`%s`",
                joinSequence.get(0).getTableName())
        );

        // Create the Join tables query string
        for (int i = 1; i < joinSequence.size(); i++) {
            JoinTable rightTable = joinSequence.get(i);
            String rightTableName = String.format(
                "`%s`",
                rightTable.getTableName()
            );

            JoinTable leftTable = getPrecedingTable(rightTable);

            String joinType = rightTable.getJoinType();

            TableOnStep<?> onStep = null;

            if (joinType.equals(JoinType.INNER_JOIN.toString())) {
                onStep = joinStep.innerJoin(table(rightTableName));
            } else if (joinType.equals(JoinType.RIGHT_JOIN.toString())) {
                onStep = joinStep.rightJoin(table(rightTableName));
            } else if (joinType.equals(JoinType.LEFT_JOIN.toString())) {
                onStep = joinStep.leftJoin(table(rightTableName));
            } else if (joinType.equals(JoinType.FULL_OUTER_JOIN.toString())) {
                onStep = joinStep.fullOuterJoin(table(rightTableName));
            }

            if (onStep != null) {
                joinStep = onStep
                    .on(field(leftTable.getJoinKey(rightTable))
                        .eq(field(rightTable.getJoinKey(leftTable))));
            }
        }

        return joinStep;
    }
}
