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

import org.apromore.etlplugin.logic.services.FileHandlerService;
import org.apromore.etlplugin.logic.services.Transaction;
import org.apromore.etlplugin.portal.ETLPluginPortal;
import org.apromore.etlplugin.portal.models.joinTableModel.Join;
import org.apromore.etlplugin.portal.models.joinTableModel.JoinQueryModel;
import org.apromore.etlplugin.portal.models.joinTableModel.JoinType;
import org.apromore.etlplugin.portal.models.sidePanelModel.FileMetaData;
import org.apromore.etlplugin.portal.models.templateTableModel.TemplateTableBean;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.*;

/**
 * Model for the join panel.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class JoinPanelViewModel {

    private List<JoinQueryModel> joinQueryModels;
    private List<String> joins;
    private static final int LIMIT = 50;
    private Transaction transaction;
    private FileHandlerService fileHandlerService;

    @WireVariable
    private FileMetaData fileMetaData;

    @WireVariable
    private TemplateTableBean templateTableBean;

    @WireVariable
    private Join join;

    /**
     * Initialise.
     */
    @Init
    public void init() {
        fileHandlerService = (FileHandlerService) ((Map) Sessions.getCurrent()
            .getAttribute(ETLPluginPortal.SESSION_ATTRIBUTE_KEY))
            .get("fileHandlerService");
        transaction = (Transaction) ((Map) Sessions.getCurrent()
            .getAttribute(ETLPluginPortal.SESSION_ATTRIBUTE_KEY))
            .get("transaction");
        joinQueryModels = new ArrayList<JoinQueryModel>();
        joins = new ArrayList<String>();
        for (JoinType type: JoinType.values()) {
            joins.add(type.toString());
        }

        joinQueryModels.add(new JoinQueryModel());
    }

    /**
     * onSelect command for table A being selected.
     * @param index Index of join query model.
     */
    @Command("onTableASelected")
    @NotifyChange("joinQueryModels")
    public void onTableASelected(@BindingParam("index") int index) {
        String selectedTableAName = joinQueryModels
            .get(index)
            .getSelectedTableA();

        try {
            List<String> keys = fileMetaData
                .getFileMetaMap()
                .get(selectedTableAName);
            joinQueryModels.get(index).setTableAKeys(keys);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * onSelect command for table B being selected.
     * @param index Index of join query model.
     */
    @Command("onTableBSelected")
    @NotifyChange("joinQueryModels")
    public void onTableBSelected(@BindingParam("index") int index) {
        String selectedTableBName = joinQueryModels
            .get(index)
            .getSelectedTableB();

        try {
            List<String> keys = fileMetaData
                .getFileMetaMap()
                .get(selectedTableBName);
            joinQueryModels.get(index).setTableBKeys(keys);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Submit join query.
     *
     */
    @Command("submitQuery")
    public void submitQuery() {
        List<List<String>> totalJoinQuery = new ArrayList<>();
        HashMap<String, List<String>> tableMetaData =
            new HashMap<String, List<String>>();

        for (int i = 0; i < joinQueryModels.size(); i++) {
            JoinQueryModel jqModel = joinQueryModels.get(i);
            List<String> joinQueryAttributes = jqModel.submit();
            totalJoinQuery.add(joinQueryAttributes);

            // Adding tables and columns into tables meta data
            if (!tableMetaData.containsKey(jqModel.getSelectedTableA())) {
                tableMetaData.put(
                    jqModel.getSelectedTableA(),
                    jqModel.getTableAKeys()
                );
            }
            if (!tableMetaData.containsKey(jqModel.getSelectedTableB())) {
                tableMetaData.put(
                    jqModel.getSelectedTableB(),
                    jqModel.getTableBKeys()
                );
            }

            // Handle empty row
            if (!jqModel.isComplete()) {
                Messagebox.show(
                    "Row " + (i + 1) + " is incomplete.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR
                );
                return;
            }
        }

        List<List<String>> resultsList = null;

        try {
            // Get table
            Table<?> joinTable = join.getTable(totalJoinQuery);
            templateTableBean.setTable(joinTable);

            resultsList = transaction.executeQuery(
                select(field("*"))
                    .from(joinTable)
                    .limit(LIMIT)
                    .getSQL(ParamType.INLINED),
                false
            );

            Map<String, Object> args = new HashMap<String, Object>();
            args.put("resultsList", resultsList);
            BindUtils.postGlobalCommand(null, null, "onTableClick", args);

            fileMetaData.setInputFileMeta(tableMetaData);
            BindUtils.postNotifyChange(
                null,
                null,
                fileMetaData,
                "inputFileMeta"
            );
            fileMetaData.setJoinDone(true);
            BindUtils.postNotifyChange(null, null, fileMetaData, "joinDone");

            for (String tableName : templateTableBean.getTablesNames()) {
                if (!tableName.equals("-placeholder-") &&
                    !fileMetaData
                        .getInputFileMeta()
                        .containsKey(tableName)
                ) {
                    for (
                        String columnName : fileMetaData
                            .getFileMetaMap()
                            .get(tableName)
                    ) {
                        templateTableBean.removeColumn(tableName, columnName);
                    }
                }
            }
            templateTableBean.updateTemplateTable();
            BindUtils.postNotifyChange(
                    null,
                    null,
                    templateTableBean,
                    "*"
            );

        } catch (SQLException e) {
            Messagebox.show(
                "Most likely incompatible key types",
                "Error",
                Messagebox.OK,
                Messagebox.ERROR
            );
            e.printStackTrace();
        } catch (NullPointerException e) {
            Messagebox.show(
                "An error occurred with selected tables",
                "Error",
                Messagebox.OK,
                Messagebox.ERROR
            );
            e.printStackTrace();
        }
    }

    /**
     * Add a new joinQueryModel.
     *
     */
    @Command("addJoinQuery")
    @NotifyChange("joinQueryModels")
    public void addJoinQuery() {
        joinQueryModels.add(new JoinQueryModel());
    }

    /**
     * Remove the specified joinQueryModel.
     *
     * @param index Index of joinQueryModel in joniQueryModels to remove
     */
    @Command("removeJoinQuery")
    @NotifyChange("joinQueryModels")
    public void removeJoinQuery(@BindingParam("index") int index) {
        if (joinQueryModels.size() > 1) {
            joinQueryModels.remove(index);
        }
    }

    /**
     * Get joinQueryModels.
     *
     * @return joinQueryModels joinQueryModels
     */
    public List<JoinQueryModel> getJoinQueryModels() {
        return joinQueryModels;
    }

    /**
     * Set joinQueryModels.
     *
     * @param joinQueryModels joinQueryModels
     */
    public void setJoinQueryModels(List<JoinQueryModel> joinQueryModels) {
        this.joinQueryModels = joinQueryModels;
    }

    /**
     * Get joins.
     *
     * @return joins joins
     */
    public List<String> getJoins() {
        return joins;
    }

    /**
     * Set joins.
     *
     * @param joins joins
     */
    public void setJoins(List<String> joins) {
        this.joins = joins;
    }
}
