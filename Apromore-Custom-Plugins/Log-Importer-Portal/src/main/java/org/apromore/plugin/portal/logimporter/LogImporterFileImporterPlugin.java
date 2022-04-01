/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.logimporter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apromore.dao.model.Log;
import org.apromore.dao.model.Usermetadata;
import org.apromore.exception.UserNotFoundException;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalContexts;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.logimporter.listener.LogImportListener;
import org.apromore.service.UserMetadataService;
import org.apromore.service.logimporter.exception.EmptyHeaderException;
import org.apromore.service.logimporter.exception.UnsupportedSeparatorException;
import org.apromore.service.logimporter.services.ParquetFactoryProvider;
import org.apromore.service.logimporter.services.legacy.LogImporterProvider;
import org.apromore.util.UserMetadataTypeEnum;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.util.media.Media;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import com.opencsv.CSVReader;

@Component
public class LogImporterFileImporterPlugin implements FileImporterPlugin {

  private static Logger LOGGER = PortalLoggerFactory.getLogger(LogImporterFileImporterPlugin.class);

  @Autowired
  private ParquetFactoryProvider parquetFactoryProvider;
  @Autowired
  private LogImporterProvider logImporterProvider;
  @Autowired
  private UserMetadataService userMetadataService;

  private static String getMediaFormat(Media media) throws Exception {
    if (media.getName().lastIndexOf('.') < 0)
      throw new Exception("Can't read file format");
    return media.getName().substring(media.getName().lastIndexOf('.') + 1);
  }

  public ParquetFactoryProvider getParquetFactoryProvider() {
    return parquetFactoryProvider;
  }

  public void setParquetFactoryProvider(ParquetFactoryProvider parquetFactoryProvider) {
    this.parquetFactoryProvider = parquetFactoryProvider;
  }

  public LogImporterProvider getLogImporterProvider() {
    return logImporterProvider;
  }

  public void setLogImporterProvider(LogImporterProvider logImporterProvider) {
    this.logImporterProvider = logImporterProvider;
  }

  public ResourceBundle getLabels() {
    Locale locale = (Locale) Sessions.getCurrent().getAttribute(Attributes.PREFERRED_LOCALE);
    return ResourceBundle.getBundle(PluginMeta.PLUGIN_ID, locale,
            LogImporterController.class.getClassLoader());
  }

  // Implementation of FileImporterPlugin

  public void setUserMetadataService(UserMetadataService newUserMetadataService) {
    LOGGER.info("Injected CSV importer logic {}", newUserMetadataService);
    this.userMetadataService = newUserMetadataService;
  }

  @Override
  public Set<String> getFileExtensions() {
    return new HashSet<>(Arrays.asList("csv", "parquet", "xlsx"));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void importFile(Media media, boolean isLogPublic) {

    // Configure the arguments to pass to the CSV importer view
    Map arg = new HashMap<>();
    arg.put("parquetFactoryProvider", parquetFactoryProvider);
    arg.put("logImporterProvider", logImporterProvider);
    arg.put("media", media);
    Sessions.getCurrent().setAttribute(LogImporterController.SESSION_ATTRIBUTE_KEY, arg);
    PortalContext portalContext = PortalContexts.getActivePortalContext();
    LogImportListener logImportListener = new LogImportListener(arg,
        (String) Sessions.getCurrent().getAttribute("fileimportertarget"), null, null);
    // Only works for CSV
    try {
      if ("csv".equals(getMediaFormat(media))) {
        String username = portalContext.getCurrentUser().getUsername();
        // Get header from imported CSV
        List<String> header = new ArrayList<>();
        String fileEncoding = "UTF-8";
        CSVFileReader csvFileReader = new CSVFileReader();
        try (CSVReader csvReader = csvFileReader.newCSVReader(media, fileEncoding)) {
          header = Arrays.asList(csvReader.readNext());
        } catch (EmptyHeaderException e) {
          Messagebox.show(getLabels().getString("failedImportHeader"));
          return;
        } catch (UnsupportedSeparatorException e) {
          Messagebox.show(getLabels().getString("unsupportedSeparator"));
          return;
        } catch (IOException e) {
          Messagebox.show(getLabels().getString("failedImport"));
          LOGGER.error("Unable to read CSV", e);
          return;
        }
        // Get saved schema mapping from DB
        List<Usermetadata> mappingJSONList;
        Set<Usermetadata> usermetadataSet;

        try {
          usermetadataSet = userMetadataService
              .getUserMetadataWithoutLog(UserMetadataTypeEnum.CSV_IMPORTER, username);
        } catch (UserNotFoundException e) {
          LOGGER.error("Unable to find user " + username, e);
          return;
        }

        if (usermetadataSet != null) {
          mappingJSONList = new ArrayList<>(usermetadataSet);
        } else
          mappingJSONList = new ArrayList<>();

        // Sort by Usermetadata object Id, since a new csv schema mapping is created each time.
        mappingJSONList.sort(Comparator.comparing(Usermetadata::getId));

        if (mappingJSONList.size() != 0) {

          // Matching from the latest record
          for (int i = mappingJSONList.size() - 1; i >= 0; i--) {

            Usermetadata usermetadata = mappingJSONList.get(i);

            JSONObject jsonObject = (JSONObject) JSONValue.parse(usermetadata.getContent());

            assert jsonObject != null;
            LOGGER.debug("Trying to match with stored schema: {}",
                JSONValue.parse(jsonObject.get("header").toString()));

            List<String> sampleHeader = (List<String>) jsonObject.get("header");

            // Create a popup window on top of Importer window to prompt matched schema
            if (sampleHeader != null && sampleHeader.equals(header))
              try {

                LOGGER.debug("Found matched schema: {} : {}", sampleHeader, header);
                Map<String, Object> arg2 = new HashMap<>();
                arg2.put("labels", getLabels());
                Window matchedMappingPopUp =
                    (Window) portalContext.getUI().createComponent(getClass().getClassLoader(),
                        "zul/matchedMapping.zul", null, arg2);
                matchedMappingPopUp.doModal();

                Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                    .parse(usermetadata.getCreatedTime());
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
                String formattedTime = new SimpleDateFormat("HH:mm:ss").format(date);

                Label fileNameLabel = (Label) matchedMappingPopUp.getFellow("fileNameLabel");
                Label fileTimeLabel = (Label) matchedMappingPopUp.getFellow("fileTimeLabel");
                Set<Log> logs = usermetadata.getLogs();

                for (Log log : logs) {
                  fileNameLabel.setValue(log.getName());
                  fileTimeLabel
                      .setValue(" , uploaded at " + formattedTime + " on " + formattedDate);
                }

                Button uploadWithMatchedMappingBtn =
                    (Button) matchedMappingPopUp.getFellow("uploadWithMatchedMapping");
                Button uploadAsNewBtn = (Button) matchedMappingPopUp.getFellow("uploadAsNew");
                uploadWithMatchedMappingBtn.addEventListener("onClick", event -> {
                  arg.put("mappingJSON", jsonObject);
                  matchedMappingPopUp.detach();

                  // Create a CSV importer view
                  switch ((String) Sessions.getCurrent().getAttribute("fileimportertarget")) {
                    case "page": // create the view in its own page
                      Executions.getCurrent().sendRedirect("import-csv/csvimporter.zul", "_blank");
                      break;

                    case "modal":
                    default: // create the view in a modal popup within the current page
                      try {
                        Window window = (Window) portalContext.getUI().createComponent(
                            getClass().getClassLoader(), "import-csv/csvimporter.zul", null, arg);
                        window.doModal();

                      } catch (IOException e) {
                        LOGGER.error("Unable to create window", e);
                      }
                      break;
                  }
                });
                uploadAsNewBtn.addEventListener("onClick", event -> {
                  arg.put("mappingJSON", null);
                  matchedMappingPopUp.detach();

                  // Create a CSV importer view
                  switch ((String) Sessions.getCurrent().getAttribute("fileimportertarget")) {
                    case "page": // create the view in its own page
                      Executions.getCurrent().sendRedirect("import-csv/csvimporter.zul", "_blank");
                      break;

                    case "modal":
                    default: // create the view in a modal popup within the current page
                      try {
                        Window window = (Window) portalContext.getUI().createComponent(
                            getClass().getClassLoader(), "import-csv/csvimporter.zul", null, arg);
                        window.doModal();

                      } catch (IOException e) {
                        LOGGER.error("Unable to create window", e);
                      }
                      break;
                  }
                });
                // only match the last schema mapping if there are multiple
                return;

              } catch (IOException | ParseException e) {
                LOGGER.error("Unable to import CSV", e);
              }

          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("Can't read file format ", e);
    }
    // can't find match in JSONList or no mapping record
    arg.put("mappingJSON", null);

    // Create a CSV importer view
    switch ((String) Sessions.getCurrent().getAttribute("fileimportertarget")) {
      case "page": // create the view in its own page
        Executions.getCurrent().sendRedirect("import-csv/csvimporter.zul", "_blank");
        break;

      case "modal":
      default: // create the view in a modal popup within the current page

        try {
          Window window =
              (Window) portalContext.getUI().createComponent(getClass().getClassLoader(),
                  "import-csv/csvimporter.zul", null, arg);
          window.doModal();

        } catch (IOException e) {
          LOGGER.error("Unable to create window", e);
        }
        break;
    }
  }
}
