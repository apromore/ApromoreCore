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

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for a join query.
 */
public class JoinQueryModel {
    private String selectedTableA;
    private String selectedTableB;
    private String selectedJoin;
    private String selectedKeyA;
    private String selectedKeyB;

    private List<String> tableAKeys;
    private List<String> tableBKeys;

    /**
     * Constructor.
     */
    public JoinQueryModel() {
        tableAKeys = new ArrayList<String>();
        tableBKeys = new ArrayList<String>();
    }

    /**
     * Send the join query to Impala. Not currently implemented.
     *
     * @return Get the join table Info
     */
    public List<String> submit() {
        List<String> tableQuery = new ArrayList<>();
        tableQuery.add(FilenameUtils.removeExtension(selectedTableA));
        tableQuery.add(selectedKeyA);
        tableQuery.add(FilenameUtils.removeExtension(selectedTableB));
        tableQuery.add(selectedKeyB);
        tableQuery.add(selectedJoin);

        return tableQuery;
    }

    /**
     * Have all fields been assigned? True or false.
     *
     * @return True if all fields have been assigned and false if not.
     */
    public Boolean isComplete() {
        Boolean isComplete;
        if (selectedTableA == null || selectedTableB == null ||
                selectedJoin == null || selectedKeyA == null ||
                selectedKeyB == null) {
            isComplete = false;
        } else {
            isComplete = true;
        }
        return isComplete;
    }

    /**
     * Get selectedTableA.
     *
     * @return selectedTableA
     */
    public String getSelectedTableA() {
        return selectedTableA;
    }

    /**
     * Set selectedTableA.
     *
     * @param selectedTableA selectedTableA
     */
    public void setSelectedTableA(String selectedTableA) {
        this.selectedTableA = selectedTableA;
    }

    /**
     * Get selectedTableB.
     *
     * @return selectedTableB
     */
    public String getSelectedTableB() {
        return selectedTableB;
    }

    /**
     * Set selectedTableB.
     *
     * @param selectedTableB selectedTableB
     */
    public void setSelectedTableB(String selectedTableB) {
        this.selectedTableB = selectedTableB;
    }

    /**
     * Get selectedJoin.
     *
     * @return selectedJoin
     */
    public String getSelectedJoin() {
        return selectedJoin;
    }

    /**
     * Set selectedJoin.
     *
     * @param selectedJoin selectedJoin
     */
    public void setSelectedJoin(String selectedJoin) {
        this.selectedJoin = selectedJoin;
    }

    /**
     * Get selectedKeyA.
     *
     * @return selectedKeyA
     */
    public String getSelectedKeyA() {
        return selectedKeyA;
    }

    /**
     * Set selectedKeyA.
     *
     * @param selectedKeyA selectedKeyA
     */
    public void setSelectedKeyA(String selectedKeyA) {
        this.selectedKeyA = selectedKeyA;
    }

    /**
     * Get selectedKeyB.
     *
     * @return selectedKeyB
     */
    public String getSelectedKeyB() {
        return selectedKeyB;
    }

    /**
     * Set selectedKeyB.
     *
     * @param selectedKeyB selectedKeyB
     */
    public void setSelectedKeyB(String selectedKeyB) {
        this.selectedKeyB = selectedKeyB;
    }

    /**
     * Get tableAKeys.
     *
     * @return tableAKeys
     */
    public List<String> getTableAKeys() {
        return tableAKeys;
    }

    /**
     * Set tableAKeys.
     *
     * @param tableAKeys tableAKeys
     */
    public void setTableAKeys(List<String> tableAKeys) {
        this.tableAKeys = tableAKeys;
    }

    /**
     * Get tableBKeys.
     *
     * @return tableBKeys
     */
    public List<String> getTableBKeys() {
        return tableBKeys;
    }

    /**
     * Set tableBKeys.
     *
     * @param tableBKeys tableBKeys
     */
    public void setTableBKeys(List<String> tableBKeys) {
        this.tableBKeys = tableBKeys;
    }

}
