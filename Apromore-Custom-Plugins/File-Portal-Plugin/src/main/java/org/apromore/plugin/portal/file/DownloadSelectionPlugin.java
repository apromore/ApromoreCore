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
package org.apromore.plugin.portal.file;

import static org.apromore.plugin.portal.PortalContexts.getPageDefinition;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.inject.Inject;

import org.apromore.apmlog.APMLog;
import org.apromore.exception.UserNotFoundException;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalContexts;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.portal.model.ExportLogResultType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.service.csvexporter.CSVExporterLogic;
import org.apromore.zk.label.LabelSupplier;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

@Component
public class DownloadSelectionPlugin extends DefaultPortalPlugin implements LabelSupplier {

  private static Logger LOGGER = PortalLoggerFactory.getLogger(DownloadSelectionPlugin.class);

  private static final String PARQUET_DOWNLOAD="PARQUET";
  private String label = "Download";
  @Inject
  EventLogService eventLogService;
  @Inject
  ProcessService processService;

  @Inject
  private CSVExporterLogic csvExporterLogic;

  Listbox selectedEncoding;
  Radiogroup format;

  @Override
  public String getBundleName() {
    return "file";
  }

  public void setEventLogService(EventLogService eventLogService) {
    this.eventLogService = eventLogService;
  }

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

  public void setCsvExporterLogic(CSVExporterLogic csvExporterLogic) {
    this.csvExporterLogic = csvExporterLogic;
  }

  // PortalPlugin overrides

  @Override
  public String getLabel(Locale locale) {
    return Labels.getLabel("plugin_file_download_text", label);
  }

  @Override
  public String getIconPath() {
    return "download.svg";
  }

  @Override
  public void execute(PortalContext portalContext) {
      try {
          MainController mainController = (MainController) portalContext.getMainController();
          if (mainController.getSelectedElements().size() == 0) {
              Notification.info(getLabel("selectMinimumOneFile"));
          } else if (mainController.getSelectedElements().size() == 1) {
              SummaryType summaryType = mainController.getSelectedElements().iterator().next();
              if (summaryType instanceof LogSummaryType) {
                  exportLog(mainController, portalContext, (LogSummaryType) summaryType);
              } else if (summaryType instanceof ProcessSummaryType) {
                  exportProcessModel(mainController, portalContext);
              }
          } else {
              if (mainController.getSelectedElements().stream()
                      .anyMatch(summaryType -> summaryType instanceof LogSummaryType)) {
                  exportSelectedLogsAndProcessModel(mainController, portalContext);
              } else {
                  exportFiles(mainController,"","");
              }
          }
      } catch (Exception e) {
          LOGGER.error("Unable to download selection", e);
          Notification.error(getLabel("unableDownload"));
      }
  }

  /**
   * Export all selected process versions, each of which in a native format to be chosen by the user
   *
   * @throws InterruptedException
   * @throws SuspendNotAllowedException
   * @throws ExceptionFormats
   */
  protected void exportProcessModel(MainController mainC, PortalContext portalContext)
      throws SuspendNotAllowedException, InterruptedException, ExceptionFormats, ParseException {
    mainC.eraseMessage();

    Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions =
        mainC.getSelectedElementsAndVersions();
    if (selectedProcessVersions.size() == 1) {
      ProcessSummaryType model =
          (ProcessSummaryType) selectedProcessVersions.keySet().iterator().next();
      VersionSummaryType version = selectedProcessVersions.get(model).get(0);
      try {
          if (processService.hasLinkedProcesses(model.getId(), UserSessionManager.getCurrentUser().getUsername())) {
              Map<String, Object> args = new HashMap<>();
              args.put("process", model);
              args.put("version", version);

              Window downloadBPMNPrompt = (Window) Executions.createComponents(
                  getPageDefinition("static/bpmneditor/downloadBPMN.zul"), null, args);
              downloadBPMNPrompt.doModal();
          } else {
              ExportFormatResultType exportResult = mainC.getManagerService().exportFormat(model.getId(),
                  model.getName(), version.getName(), version.getVersionNumber(),
                  model.getOriginalNativeType(), UserSessionManager.getCurrentUser().getUsername());
              InputStream nativeStream = exportResult.getNative().getInputStream();
              Filedownload.save(nativeStream, "text/xml", model.getName() + ".bpmn");
              LOGGER.info("User {} downloaded process model \"{}\" (id {}, version {}/{})",
                  UserSessionManager.getCurrentUser().getUsername(), model.getName(), model.getId(),
                  version.getName(), version.getVersionNumber());
          }
      } catch (Exception e) {
        LOGGER.error("Export process model failed", e);
        Messagebox.show(getLabel("unableDownloadModel"), "Error", Messagebox.OK,
            Messagebox.ERROR);
      }
    } else {
      Notification.error("Please select one process model");
    }
  }

  public void exportLog(MainController mainC, PortalContext portalContext,
      LogSummaryType logSummary) {
    try {
      Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(),
          "zul/downloadLog.zul", null, null);
        window.setTitle(MessageFormat.format(
            Labels.getLabel("file_downloadLog_text"), mainC.getSelectedElements().size()));
      Button downloadButton = (Button) window.getFellow("downloadButton");
      Row rowEncoding = (Row) window.getFellow("rowEncoding");

      selectedEncoding = (Listbox) window.getFellow("selectEncoding");
      format = (Radiogroup) window.getFellow("format");

      format.addEventListener("onCheck", new EventListener<Event>() {
        @Override
        public void onEvent(Event event) throws Exception {
          if (format.getSelectedItem().getLabel().equals("CSV")) {
            rowEncoding.setVisible(true);
          } else {
            rowEncoding.setVisible(false);
          }
        }
      });

      downloadButton.addEventListener("onClick", new EventListener<Event>() {
        @Override
        public void onEvent(Event event) throws Exception {
          if (format.getSelectedItem().getLabel().equals("CSV")) {
            exportCSV(logSummary, selectedEncoding.getSelectedItem().getValue());
          } else if (format.getSelectedItem().getLabel().equals("XES")){
            exportXES(logSummary, mainC);
          }else if (format.getSelectedItem().getLabel().equals(PARQUET_DOWNLOAD)){
              callParquetLogsToDownload(mainC,null); //encoding with selected later
          }
          LOGGER.info("User {} downloaded log \"{}\" (id {}) in format {}",
              UserSessionManager.getCurrentUser().getUsername(), logSummary.getName(),
              logSummary.getId(), format.getSelectedItem().getLabel());
          window.invalidate();
          window.detach();
        }
      });

      Button cancelButton = (Button) window.getFellow("cancelButton");
      cancelButton.addEventListener("onClick", new EventListener<Event>() {
        @Override

        public void onEvent(Event event) throws Exception {
          window.invalidate();
          window.detach();
        }

      });
      window.doModal();
    } catch (IOException e) {
      LOGGER.error("Failed to read");
    }
  }

    private void callParquetLogsToDownload(MainController mainController, String encoding) {
        try {
            List<Integer> selectedLogs =
                mainController.getSelectedElements().stream().map(SummaryType::getId).collect(Collectors.toList());
            Sessions.getCurrent().setAttribute("logParquetDownload", selectedLogs);
            Sessions.getCurrent().setAttribute("encodingLogParquet", encoding);
            PortalPlugin plugin = PortalPluginResolver.getPortalPluginMap().get(PluginCatalog.PLUGIN_PARQUET_DOWNLOAD);
            if (plugin != null) {
                plugin.execute(PortalContexts.getActivePortalContext());
            }
        } catch (Exception e) {
            LOGGER.error(Labels.getLabel("Failed to open ParquetExporter window"), e);
        }
    }

    public void exportSelectedLogsAndProcessModel(MainController mainController, PortalContext portalContext) {
        try {
          Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(),
              "zul/downloadLog.zul", null, null);
          window.setTitle(MessageFormat.format(
                Labels.getLabel("file_downloadLog_text"), mainController.getSelectedElements().size()));
          Button downloadButton = (Button) window.getFellow("downloadButton");
          Row rowEncoding = (Row) window.getFellow("rowEncoding");

          selectedEncoding = (Listbox) window.getFellow("selectEncoding");
          format = (Radiogroup) window.getFellow("format");
            if (mainController.getSelectedElements().stream()
                .anyMatch( summaryType -> !(summaryType instanceof LogSummaryType))) {
                Radio parquetRadio = (Radio) format.getFellowIfAny("parquetDownload");
                if (parquetRadio != null) {
                    parquetRadio.setVisible(false);
                }
            }
            format.addEventListener("onCheck", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
              if (!(format.getSelectedItem().getLabel().equals("CSV")||format.getSelectedItem().getLabel().equals(PARQUET_DOWNLOAD))) {
                rowEncoding.setVisible(false);
              }
            }
          });

          downloadButton.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (format.getSelectedItem().getLabel().equals("CSV")) {
                    exportFiles(mainController, format.getSelectedItem().getLabel(),
                        selectedEncoding.getSelectedItem().getValue());
                } else if (format.getSelectedItem().getLabel().equals(PARQUET_DOWNLOAD)) {
                    callParquetLogsToDownload(mainController, selectedEncoding.getSelectedItem().getValue());
                } else {
                    exportFiles(mainController, format.getSelectedItem().getLabel(), "");
                }

              LOGGER.info("User {} downloaded  in format {}",
                  UserSessionManager.getCurrentUser().getUsername(), format.getSelectedItem().getLabel());
              window.invalidate();
              window.detach();
            }
          });

          Button cancelButton = (Button) window.getFellow("cancelButton");
          cancelButton.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
              window.invalidate();
              window.detach();
            }
          });
          window.doModal();
        } catch (IOException e) {
          LOGGER.error("Failed to read");
        }
      }

  /**
   * Export all selected process versions, each of which in a native format to be chosen by the user
   *
   * @throws InterruptedException
   * @throws SuspendNotAllowedException
   * @throws ExceptionFormats
   */
  protected void exportXES(LogSummaryType logSummary, MainController mainController)
      throws Exception {
    String filename = logSummary.getName().replace('.', '-');
    ExportLogResultType exportResult =
        mainController.getManagerService().exportLog(logSummary.getId(), filename);
    try (InputStream native_is = exportResult.getNative().getInputStream()) {
      mainController.showPluginMessages(exportResult.getMessage());
      Filedownload.save(native_is, "application/x-gzip", filename + ".xes.gz");
    }
  }

  public void exportCSV(LogSummaryType logSummary, String encoding) throws Exception {
    try {
      String filename = logSummary.getName().replace('.', '-');
      APMLog apmLog = eventLogService.getAggregatedLog(logSummary.getId());
      Path path = csvExporterLogic.exportCSV(apmLog, encoding);
      LOGGER.info("Export log {} as CSV using {} to {}", filename, encoding, path);
      byte[] finalbytes = Files.readAllBytes(path);
      Filedownload.save(finalbytes, "application/x-gzip", filename + ".csv.gz");
      Files.delete(path);
    } catch (RuntimeException e) {
      LOGGER.error("Unable to export log as CSV", e);
      Messagebox.show(getLabel("unableExportCSV"), "Server error", Messagebox.OK,
          Messagebox.ERROR);
    }
  }

   private Path getCSVFile(LogSummaryType summaryType) {
      APMLog apmLog = eventLogService.getAggregatedLog(summaryType.getId());
      return csvExporterLogic.generateCSV(apmLog);
   }

   private Path getXESFile(LogSummaryType logSummary, MainController mainController) throws Exception {
       String filename = logSummary.getName().replace('.', '-');
       ExportLogResultType exportResult = mainController.getManagerService().exportLog(logSummary.getId(), filename);
       Path tempPath = Files.createTempFile(null, ".xes.gz");
       tempPath.toFile().deleteOnExit();
       writeToFile(tempPath,exportResult.getNative());
       return tempPath;
   }

   private void writeToFile(Path tempPath, DataHandler data) throws Exception {
       try (GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(tempPath.toFile()));
               InputStream native_is = data.getInputStream()) {
           byte[] buffer = new byte[1024];
           int len;
           while ((len = native_is.read(buffer)) > 0) {
               gos.write(buffer, 0, len);
           }
       }
   }

    private void writeToFileWithoutZip(Path tempPath, DataHandler data) throws IOException  {
        try (OutputStream os = (new FileOutputStream(tempPath.toFile()));
             InputStream inputStream = data.getInputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
        }
    }

   private Path getProcessModelFile(ProcessSummaryType model, VersionSummaryType version, MainController mainController,
                                    boolean includeLinkedSubprocesses)
           throws Exception {
       ExportFormatResultType exportResult = mainController.getManagerService().exportFormat(model.getId(),
               model.getName(), version.getName(), version.getVersionNumber(), model.getOriginalNativeType(),
               UserSessionManager.getCurrentUser().getUsername(), includeLinkedSubprocesses);
       Path tempPath = Files.createTempFile(null, ".bpmn");
       tempPath.toFile().deleteOnExit();
       writeToFileWithoutZip(tempPath, exportResult.getNative());
       return tempPath;
   }

    private void exportFiles(MainController mainController, String format, String encoding) {
        // Prompt user to decide whether to include linked subprocesses if one or more of the
        // selected items is a model with a linked subprocess
        if (mainController.getSelectedElements().stream()
            .anyMatch(summaryType -> {
                try {
                    return summaryType instanceof ProcessSummaryType
                        && processService.hasLinkedProcesses(summaryType.getId(),
                        UserSessionManager.getCurrentUser().getUsername());
                } catch (UserNotFoundException e) {
                    return false;
                }
            })
        ) {
            Messagebox.show("One or more selected models are linked to another process. Include linked subprocesses?", "Download BPMN Models",
                Messagebox.YES | Messagebox.NO,
                Messagebox.QUESTION,
                (Event e) -> {
                    if (Messagebox.ON_YES.equals(e.getName())) {
                        exportFiles(mainController, format, encoding, true);
                    } else if (Messagebox.ON_NO.equals(e.getName())) {
                        exportFiles(mainController, format, encoding, false);
                    }
                });
        } else {
            exportFiles(mainController, format, encoding, false);
        }
    }

    //Exports files to zip
    private void exportFiles(MainController mainController, String format, String encoding, boolean includeLinkedSubprocesses) {
        Map<String, String> filesToBeDownloaded = new HashMap<>();

        mainController.getSelectedElements().stream().forEach(item -> {
            try {
                Path path = null;
                String currentFileName = "";
                if (item instanceof LogSummaryType) {
                    LogSummaryType logSummaryType = (LogSummaryType) item;
                    if ("CSV".equals(format)) {
                        path = getCSVFile(logSummaryType);
                        LOGGER.info("Export log {} as CSV using {} to {}", item.getName(), encoding, path);
                    }  else {
                        path = getXESFile(logSummaryType, mainController);
                        LOGGER.info("Export log {} as XES using {} to {}", item.getName(), encoding, path);
                    }
                    currentFileName = logSummaryType.getName();

                } else if (item instanceof ProcessSummaryType) {
                    ProcessSummaryType model = (ProcessSummaryType) item;
                    VersionSummaryType version = null;
                    for (VersionSummaryType summaryType : model.getVersionSummaries()) {
                        if (summaryType.getVersionNumber().compareTo(model.getLastVersion()) == 0) {
                            version = summaryType;
                            break;
                        }
                    }
                    path = getProcessModelFile((ProcessSummaryType) item, version, mainController, includeLinkedSubprocesses);
                    LOGGER.info("User {} downloaded process model \"{}\" (id {}, version {}/{})",
                        UserSessionManager.getCurrentUser().getUsername(), model.getName(), model.getId(),
                        version.getName(), version.getVersionNumber());
                    currentFileName = model.getName();
                }
                if (filesToBeDownloaded.get(currentFileName) == null) {
                    filesToBeDownloaded.put(currentFileName, path.toFile().getAbsolutePath());
                } else {
                    int i = 1;
                    while (filesToBeDownloaded.get(currentFileName + "_" + i) != null) {
                        i++;
                    }
                    filesToBeDownloaded.put(currentFileName + "_" + i, path.toFile().getAbsolutePath());
                }

            } catch (Exception e) {
                cleanTempFiles(filesToBeDownloaded);
                filesToBeDownloaded.clear();
                LOGGER.error("Export process model/log failed", e);
                Notification.error(getLabel("unableDownloadModel"));
            }
        });

        if (!filesToBeDownloaded.isEmpty()) {
            try {
                byte[] zipFiles = makeZipFile(filesToBeDownloaded);
                Filedownload.save(zipFiles, "application/zip", "download.zip");
            } catch (Exception e) {
                cleanTempFiles(filesToBeDownloaded);
                LOGGER.error("Export process model/log failed", e);
                Notification.error(getLabel("unableDownloadModel"));
            }
        }

    }
 
   private void cleanTempFiles(Map<String, String> filesToBeDownloaded) {
       for (String filePath : filesToBeDownloaded.values()) {
           try {
               Files.delete(Path.of(filePath));
           } catch (IOException e) {
           }
       }

   }

   private byte[] makeZipFile(Map<String, String> filesDownloaded) throws IOException {
       try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {
           byte[] bytes = new byte[2048];
           for (String fileName : filesDownloaded.keySet()) {
               String absoluteFilePath = filesDownloaded.get(fileName);
               try (FileInputStream fis = new FileInputStream(absoluteFilePath);
                       BufferedInputStream bis = new BufferedInputStream(fis)) {
                   zos.putNextEntry(new ZipEntry(fileName + absoluteFilePath.substring(absoluteFilePath.indexOf("."))));
                   int bytesRead;
                   while ((bytesRead = bis.read(bytes)) != -1) {
                       zos.write(bytes, 0, bytesRead);
                   }
                   zos.closeEntry();
               }
               Files.delete(Path.of(absoluteFilePath));
           }
           zos.close();
           return baos.toByteArray();
       }
   }

}
