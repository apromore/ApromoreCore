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

import org.apache.commons.io.FilenameUtils;
import org.apromore.etlplugin.logic.services.FileHandlerService;
import org.apromore.etlplugin.logic.services.Transaction;
import org.apromore.etlplugin.logic.services.impl.IllegalFileTypeException;
import org.apromore.etlplugin.portal.ETLPluginPortal;
import org.apromore.etlplugin.portal.models.sidePanelModel.FileMetaData;
import org.apromore.etlplugin.portal.models.templateTableModel.TemplateTableBean;
import org.jooq.conf.ParamType;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Messagebox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.*;

/**
 * Model for the upload view.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class FileUploadViewModel {
    private static final String NULL_UPLOAD_MESSAGE = "No file is selected";
    private static final String ERROR = "Error";
    private static final Integer MAX_FILES_NUMBER = 10;
    private Boolean noFilesCheck;

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
        if(transaction == null || fileHandlerService == null) {
            System.out.println("Bad FileUpload view");
        } else {
            System.out.println("Good FileUpload view");
        }
        noFilesCheck = true;
    }

    /**
     * Describes the actions taken when a file is uploaded.
     */
    @NotifyChange("noFilesCheck")
    @Command("onFileUpload")
    public void onFileUpload() {

        Media[] medias = Fileupload.get(MAX_FILES_NUMBER);

        if (medias != null && medias.length > 0 && medias.length <= 10) {
            String returnMessage;

            try {
                returnMessage = fileHandlerService.writeFiles(medias);

                // If the file was written then load in impala and get snippet
                if (returnMessage.equals("Upload Success")) {
                    List<List<String>> resultsList = null;

                    for (int i = 0; i < medias.length; i++) {
                        Media media = medias[i];
                        try {

                            transaction.addTable(media.getName());

                            resultsList = transaction.executeQuery(
                                select(field("*"))
                                    .from(FilenameUtils
                                        .removeExtension(media.getName()))
                                    .limit(50)
                                    .getSQL(ParamType.INLINED),
                                false
                            );

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        // Prevent the same file from appearing
                        // in the list twice
                        if (!fileMetaData.getFileMetaMap().containsKey(
                                media.getName())) {

                            //Store file metadata (name and column names)
                            fileMetaData.putNewFile(
                                FilenameUtils.removeExtension(media.getName()),
                                    resultsList
                            );
                            BindUtils.postNotifyChange(
                                    null,
                                    null,
                                    fileMetaData,
                                    "fileMetaMap");
                            HashMap<String, List<String>> newInputFileMeta =
                                    new HashMap<>();

                            if (fileMetaData.getFileMetaMap().keySet()
                                    .size() == 1) {

                                templateTableBean.setTable(
                                    table((String) fileMetaData
                                        .getFileMetaMap()
                                        .keySet()
                                        .toArray()[0])
                                );

                                try {
                                    newInputFileMeta.put(FilenameUtils
                                            .removeExtension(media.getName()),
                                            resultsList.get(0));
                                    fileMetaData.setInputFileMeta(
                                            newInputFileMeta);
                                    BindUtils.postNotifyChange(null, null,
                                            fileMetaData, "inputFileMeta");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (!fileMetaData.getJoinDone()) {
                                fileMetaData.setInputFileMeta(
                                        newInputFileMeta);
                                BindUtils.postNotifyChange(null, null,
                                        fileMetaData, "inputFileMeta");

                                templateTableBean.removeAllColumns();

                                templateTableBean.updateTemplateTable();
                                BindUtils.postNotifyChange(
                                        null,
                                        null,
                                        templateTableBean,
                                        "*"
                                );
                            }

                            noFilesCheck = false;
                        }
                    }
                }

                Messagebox.show(returnMessage);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalFileTypeException e) {
                Messagebox.show(e.getMessage());
            }

        } else {
            Messagebox.show(
                NULL_UPLOAD_MESSAGE,
                ERROR,
                Messagebox.OK,
                Messagebox.ERROR);
        }
    }

    /**
     * Get no files uploaded boolean.
     *
     * @return boolean value for checking if no files are uploaded
     */
    public Boolean getNoFilesCheck() {
        return noFilesCheck;
    }

}
