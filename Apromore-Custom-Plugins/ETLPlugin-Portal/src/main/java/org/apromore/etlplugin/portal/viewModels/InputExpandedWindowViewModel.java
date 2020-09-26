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

import org.apromore.etlplugin.logic.services.Transaction;
import org.apromore.etlplugin.portal.ETLPluginPortal;
import org.jooq.conf.ParamType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;

/**
 * Model for the input expanded window view.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class InputExpandedWindowViewModel {

    private String filename = "";
    private List<List<String>> resultList = new ArrayList<>();
    private List<String> columnsList = new ArrayList<>();
    private Transaction transaction;

    /**
     * Initialize the view model.
     * @param filename the filename of the excerpt to display
     */
    @Init
    public void init(@ExecutionArgParam("filename") String filename) {
        try {
            this.filename = filename;
            transaction = (Transaction) ((Map) Sessions.getCurrent()
                .getAttribute(ETLPluginPortal.SESSION_ATTRIBUTE_KEY))
                .get("transaction");
            resultList = transaction.executeQuery(
                    select(field("*"))
                            .from(filename)
                            .limit(50)
                            .getSQL(ParamType.INLINED),
                    false
            );
            columnsList = resultList.get(0);
            resultList.remove(0);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
     * Get result list.
     *
     * @return a list of list containing the excerpt table
     */
    public List<List<String>> getResultList() {
        return resultList;
    }

    /**
     * Get file name.
     *
     * @return name of file for title display
     */
    public String getFilename() {
        return filename;
    }
}
