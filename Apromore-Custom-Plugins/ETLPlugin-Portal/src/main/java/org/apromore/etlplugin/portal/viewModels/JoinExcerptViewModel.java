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
package org.apromore.etlplugin.portal.viewModels;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Join View Model to show except of the Join data.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class JoinExcerptViewModel {

    private List<List<String>> resultList = new ArrayList<>();
    private List<String> columnsList = new ArrayList<>();
    private Boolean showJoinExcerptGrid = false;

    /**
     * When the table clicks.
     *
     * @param resultsList Results results that is join result
     */
    @NotifyChange({"resultList", "columnsList", "showJoinExcerptGrid"})
    @GlobalCommand
    public void onTableClick(@BindingParam("resultsList")
                                         List<List<String>> resultsList) {
        this.resultList = resultsList;
        this.columnsList = resultsList.get(0);
        this.resultList.remove(0);
        showJoinExcerptGrid = true;
    }

    /**
     * Get result list.
     *
     * @return a list of list containing the excerpt table
     */
    public List<List<String>> getResultList() {
        return resultList;
    }

    /**
     * Get columns list.
     *
     * @return a list of all columns
     */
    public List<String> getColumnsList() {
        return columnsList;
    }

    /**
     * Get show excerpt boolean.
     *
     * @return boolean value for showing excerpt
     */
    public Boolean getShowJoinExcerptGrid() {
        return showJoinExcerptGrid;
    }
}
