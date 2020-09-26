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
import org.apromore.etlplugin.portal.models.sidePanelModel.FileMetaData;
import org.apromore.etlplugin.portal.models.templateTableModel.TemplateTableBean;
import org.apromore.etlplugin.portal.models.templateTableModel.Column;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Messagebox;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for the transform panel.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TransformPanelViewModel {

    private FileHandlerService fileHandlerService;
    private Transaction transaction;

    @WireVariable
    private FileMetaData fileMetaData;
    @WireVariable
    private TemplateTableBean templateTableBean;

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
    }

    /**
     * updateWholeTable command for the entire table being selected/unselected.
     * @param originTable Table Name.
     * @param columnNames List of Column Names.
     */
    @Command
    @NotifyChange({"transformPanelModel", "joinQueryModels"})
    public void addWholeTable(
        @BindingParam("originTable") String originTable,
        @BindingParam("columnNames") List<String> columnNames
    ) {
        for (String columnName : columnNames) {
            Column column = fileMetaData.getColumnObject(originTable, columnName);

            if (column != null) {
                templateTableBean.addColumn(column);
                BindUtils.postNotifyChange(
                    null,
                    null,
                    templateTableBean,
                    "*"
                );
            }
        }
        templateTableBean.updateTemplateTable();
    }

    /**
     * updateColumn command for column being selected/unselected.
     *
     * @param columnName Column Name to add/remove to/from Template Table.
     * @param originTable Table Name.
     */
    @Command
    @NotifyChange({"transformPanelModel", "joinQueryModels"})
    public void addColumn(
        @BindingParam("columnName") String columnName,
        @BindingParam("originTable") String originTable
    ) {
        Column column = fileMetaData
            .getColumnObject(originTable, columnName);

        if (column != null) {
            templateTableBean.addColumn(column);

            templateTableBean.updateTemplateTable();
            BindUtils.postNotifyChange(
                null,
                null,
                templateTableBean,
                "*"
            );
        }
    }

    /**
     * updateColumn command for column being selected/unselected.
     *
     * @param columnName Column Name to add/remove to/from Template Table.
     * @param originTable Table Name.
     */
    @Command
    @NotifyChange({"transformPanelModel", "joinQueryModels"})
    public void removeColumn(
        @BindingParam("columnName") String columnName,
        @BindingParam("originTable") String originTable
    ) {
        templateTableBean.removeColumn(originTable, columnName);
        templateTableBean.updateTemplateTable();
        BindUtils.postNotifyChange(
            null,
            null,
            templateTableBean,
            "*"
        );
    }

    /**
     * change column name and set column name to used entered new column name.
     *
     * @param newName  User entered new column name.
     * @param originalName  The original column name that remains unchanged.
     */
    @Command
    @NotifyChange({"transformPanelModel", "joinQueryModels"})
    public void setColumnNewName(
        @BindingParam("newName") String newName,
        @BindingParam("originalName") String originalName
    ) {
        System.out.println("original name : " + originalName);
        List<Column> columns = this.templateTableBean.getColumns();
        for (Column c: columns) {
            if (c.getOriginalColumnName().equals(originalName)) {
                c.setColumnName(newName);
            }
        }
        BindUtils.postNotifyChange(null, null, templateTableBean, "columns");
        BindUtils.postNotifyChange(null, null, fileMetaData, "columns");
    }

    /**
     * Export template table output into Parquet file.
     */
    @Command
    public void exportTransformParquet() {
        try {
            transaction.exportQuery(templateTableBean.getQuery(-1));
            String path = System.getProperty("java.io.tmpdir") +
                    System.getenv("DATA_STORE") + "/Exported";
            File file = fileHandlerService.getLastParquet(path);
            Filedownload.save(file, "application/octet-stream");
        } catch (SQLException e) {
            Messagebox.show(
                "Error: SQL query failed. The system failed " +
                    "to execute export.", "Error",
                Messagebox.OK,
                Messagebox.ERROR
            );
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Messagebox.show(
                "An error occurred while exporting", "Error",
                Messagebox.OK,
                Messagebox.ERROR
            );
            e.printStackTrace();
        }
    }
}
