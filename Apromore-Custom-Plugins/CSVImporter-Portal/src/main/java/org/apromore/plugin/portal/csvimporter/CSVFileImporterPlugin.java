/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
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

package org.apromore.plugin.portal.csvimporter;

import com.google.common.collect.Lists;
import com.opencsv.CSVReader;
import org.apromore.dao.model.Usermetadata;
import org.apromore.exception.UserNotFoundException;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.UserMetadataService;
import org.apromore.service.csvimporter.CSVImporterLogic;
import org.apromore.util.UserMetadataTypeEnum;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

public class CSVFileImporterPlugin implements FileImporterPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(CSVFileImporterPlugin.class);

    // Fields injected from Spring beans/OSGi services
//    private EventLogService eventLogService = (EventLogService) SpringUtil.getBean("eventLogService");
//    @Inject private EventLogService eventLogService;

    private CSVImporterLogic csvImporterLogic;
    private EventLogService eventLogService;
    private UserMetadataService userMetadataService;

    public void setCsvImporterLogic(CSVImporterLogic newCSVImporterLogic) {
        LOGGER.info("Injected CSV importer logic {}", newCSVImporterLogic);
        this.csvImporterLogic = newCSVImporterLogic;
    }

    public void setEventLogService(EventLogService newEventLogService) {
        LOGGER.info("Injected CSV importer logic {}", newEventLogService);
        this.eventLogService = newEventLogService;
    }

    @Override
    public Set<String> getFileExtensions() {
        return Collections.singleton("csv");
    }

    @Override
    public void importFile(Media media, boolean isLogPublic) {

        //TODO: find matches mapping


        // Create a CSV importer view
        String zul = "/org/apromore/plugin/portal/csvimporter/csvimporter.zul";

        PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");

        String userId = portalContext.getCurrentUser().getId();
        String username = portalContext.getCurrentUser().getUsername();

        List<Integer> logList = new ArrayList<>();
        logList.add(2);
        List<Usermetadata> mappingJSONList = new ArrayList<>();

        Set<Usermetadata> usermetadataSet = null;
        try {
            usermetadataSet = userMetadataService.getUserMetadataWithoutLog(UserMetadataTypeEnum.CSV_IMPORTER, username);
        } catch (Exception e) {

        }
        mappingJSONList = new ArrayList<>(usermetadataSet);

        // Configure the arguments to pass to the CSV importer view
        Map arg = new HashMap<>();
        arg.put("csvImporterLogic", csvImporterLogic);
        arg.put("media", media);

        List<String> header = new ArrayList<>();
        String fileEncoding = "UTF-8";

        // Creating Object of ObjectMapper define in Jakson Api
        ObjectMapper Obj = new ObjectMapper();
        JsonFactory jsonF = new JsonFactory();

        CSVFileReader csvFileReader = new CSVFileReader();
        CSVReader csvReader = csvFileReader.newCSVReader(media, fileEncoding);
        try {
            header = Arrays.asList(csvReader.readNext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mappingJSONList.size() > 0) {

            for (int i = mappingJSONList.size() - 1; i >= 0; i--) {
                System.out.println(mappingJSONList.get(i));

                JSONObject jsonObject = (JSONObject) JSONValue.parse(mappingJSONList.get(i).getContent());

                JSONValue.parse(jsonObject.get("header").toString());

                List<String> sampleHeader = (List<String>) jsonObject.get("header");

                if (sampleHeader != null && sampleHeader.equals(header)) {

                    // Attempt 1: try to create a popup window on top of csvImporter window here.
                    try {
                        Window matchedMappingPopUp =
                                (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul" +
                                        "/matchedMapping.zul", null, null);
                        matchedMappingPopUp.doModal();

                        Button uploadWithMatchedMappingBtn = (Button) matchedMappingPopUp.getFellow(
                                "uploadWithMatchedMapping");
                        Button uploadAsNewBtn = (Button) matchedMappingPopUp.getFellow(
                                "uploadAsNew");
                        uploadWithMatchedMappingBtn.addEventListener("onClick", event -> {
                                    LOGGER.info("################## BUTTON uploadWithMatchedMappingBtn CLICKED " +
                                            "***************");
                                    arg.put("mappingJSON", jsonObject);
                                    matchedMappingPopUp.detach();
                                    Window window =
                                            (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), zul, null, arg);
                                    window.doModal();
                                }
                        );
                        uploadAsNewBtn.addEventListener("onClick", event -> {
                                    LOGGER.info("################## BUTTON uploadAsNewBtn CLICKED ***************");
                                    arg.put("mappingJSON", null);
                                    matchedMappingPopUp.detach();
                                    Window window =
                                            (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), zul, null, arg);
                                    window.doModal();
                                }
                        );
                        // only match the last mapping if there are multiple
                        break;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else { // can't find match in JSONList
                    arg.put("mappingJSON", null);
                    Window window = null;
                    try {
                        window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), zul,
                                null, arg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    window.doModal();
                }
            }
        } else { // this user doesn't have mapping stored
            arg.put("mappingJSON", null);
            Window window = null;
            try {
                window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), zul,
                        null, arg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            window.doModal();
        }



        //TODO: create a dialog to provide options to user: “An existing mapping is applicable to this log. Would you
        // like to use it?” and shows an example with the first 5 rows as a table inside the window.
        // 1. Yes -> import directly
        // 2. Edit -> go to importer view and load the existing mapping (the latest one)
        // 3. No -> Import as a new process -> go to importer view and load guessed mapping




        // Create a CSV importer view
//        try {
////        PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
//            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), zul, null, arg);
//            window.doModal();
//
//
//        } catch (IOException e) {
//            LOGGER.error("Unable to create window", e);
//        }
    }
}
