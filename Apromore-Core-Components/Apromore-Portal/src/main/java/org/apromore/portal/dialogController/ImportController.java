/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2017 Adriano Augusto.
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

package org.apromore.portal.dialogController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apromore.commons.config.ConfigBean;
import org.apromore.commons.item.ItemNameUtils;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.util.StringUtil;
import org.apromore.zk.dialog.InputDialog;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ImportController extends BaseController {

  public static final String APROMORE = "Apromore";
  private static final Logger LOGGER = PortalLoggerFactory.getLogger(ImportController.class);
  private static final String UTF8_CHARSET = StandardCharsets.UTF_8.toString();
  private static final String MAX_UPLOAD_SIZE = "max-upload-size";

  private static final int CONNECT_TIMEOUT = 10000;
  private static final int READ_TIMEOUT = 10000;
  public static final String XES_GZ = "xes.gz";
  public static final String MXML_GZ = "mxml.gz";
  public static final String ON_CLICK = "onClick";
  public static final String PORTAL_FAILED_IMPORT_MESSAGE = "portal_failedImport_message";
  public static final String FILE_IMPORTER_PLUGINS = "fileImporterPlugins";

  private long maxUploadSize = 100000000L; // default 100MB
  private boolean uploadSizeExceeded = false;

  private MainController mainC;
  private Window importWindow;

  private String ignoredFiles;

  private Media media = null;
  private Label fileNameLabel;
  private Checkbox isPublicCheckbox;

  private Button okButton;

  private Textbox fileUrl;
  private Label fileNameLabelURL;
  private Button okButton_URL;


  private List<ImportOneProcessController> toImportList = new ArrayList<>();
  private List<ImportOneProcessController> importedList = new ArrayList<>();
  private List<FileImporterPlugin> fileImporterPlugins;

  @FunctionalInterface
  public interface NotificationHandler {
    public void show(String string);
  }

  private NotificationHandler note;

  /** Unit testing constructor. */
  public ImportController(MainController mainC, ConfigBean configBean,
      List<FileImporterPlugin> fileImporterPlugins, NotificationHandler note) {
    super(configBean);
    this.fileImporterPlugins = fileImporterPlugins;
    this.note = note;
  }

  public ImportController(MainController mainC) throws DialogException {
    this.ignoredFiles = "";
    this.mainC = mainC;
    this.maxUploadSize = this.mainC.getConfig().getMaxUploadSize();
    this.fileImporterPlugins = (List<FileImporterPlugin>) SpringUtil.getBean(FILE_IMPORTER_PLUGINS);
    // this.note = (message) -> { Messagebox.show(message); };
    this.note = new NotificationHandler() {
      @Override
      public void show(String message) {
        Messagebox.show(message);
      }
    };

    try {
      final Window win = (Window) Executions.createComponents("~./macros/import.zul", null, null);
      this.importWindow = (Window) win.getFellow("importWindow");
      Button uploadButton = (Button) this.importWindow.getFellow("uploadButton");
      uploadButton.setClientDataAttribute(MAX_UPLOAD_SIZE, Long.toString(this.maxUploadSize));
      this.fileUrl = (Textbox) this.importWindow.getFellow("fileUrl");
      Button uploadURLButton = (Button) this.importWindow.getFellow("uploadURLButton");
      Button cancelButton = (Button) this.importWindow.getFellow("cancelButtonImport");
      Button cancelButtonURL = (Button) this.importWindow.getFellow("cancelButtonImportURL");
      okButton = (Button) this.importWindow.getFellow("okButtonImport");
      okButton_URL = (Button) this.importWindow.getFellow("okButtonImportURL");
      this.fileNameLabel = (Label) this.importWindow.getFellow("fileNameLabel");
      this.fileNameLabelURL = (Label) this.importWindow.getFellow("fileNameLabelURL");
      Label supportedExtL = (Label) this.importWindow.getFellow("supportedExt");
      Label supportedExtURL = (Label) this.importWindow.getFellow("supportedExtURL");
      isPublicCheckbox = ((Checkbox) this.importWindow.getFellow("public"));


      // build the list of supported extensions to display
      SortedSet<String> supportedExt = new TreeSet<>();
      Collections.addAll(supportedExt, "xes", XES_GZ, "mxml", MXML_GZ, "zip");
      supportedExt.addAll(this.mainC.getNativeTypes().keySet());
      List<FileImporterPlugin> fileImporterPlugins =
          (List<FileImporterPlugin>) SpringUtil.getBean(FILE_IMPORTER_PLUGINS);
      for (FileImporterPlugin fileImporterPlugin : fileImporterPlugins) {
        supportedExt.addAll(fileImporterPlugin.getFileExtensions());
      }

      String supportedExtS = null;
      for (String aSupportedExt : supportedExt) {
        if (supportedExtS == null) {
          supportedExtS = aSupportedExt;
        } else {
          supportedExtS += ", " + aSupportedExt;
        }
      }
      supportedExtL.setValue(supportedExtS);
      supportedExtURL.setValue(supportedExtS);

      uploadButton.addEventListener(ON_CLICK, new EventListener<Event>() {
        @Override
        public void onEvent(Event event) throws Exception {
          okButton.setDisabled(true);
        }
      });
      uploadButton.addEventListener("onUpload", new EventListener<Event>() {
        @Override
        public void onEvent(Event event) throws Exception {
          uploadFile((UploadEvent) event);
        }
      });
      uploadButton.addEventListener("onSizeCheck", new EventListener<Event>() {
        @Override
        public void onEvent(Event event) throws Exception {
          uploadSizeExceeded = (int) event.getData() != 0;
        }
      });
      uploadURLButton.addEventListener(ON_CLICK, new EventListener<Event>() {
        @Override
        public void onEvent(Event event) throws Exception {
          uploadFileFromURL(fileUrl.getValue());
        }
      });
      okButton.addEventListener(ON_CLICK, new EventListener<MouseEvent>() {
        @Override
        public void onEvent(MouseEvent event) throws Exception {
          importWindow.detach();
          Sessions.getCurrent().setAttribute("fileimportertarget",
              ((event.getKeys() & MouseEvent.META_KEY) != 0) ? "page" : "modal");
          importFile(ImportController.this.media);
        }
      });
      okButton_URL.addEventListener(ON_CLICK, new EventListener<MouseEvent>() {
        @Override
        public void onEvent(MouseEvent event) throws Exception {
          importWindow.detach();
          Sessions.getCurrent().setAttribute("fileimportertarget",
              ((event.getKeys() & MouseEvent.META_KEY) != 0) ? "page" : "modal");
          // uploadFileFromURL(fileUrl.getValue());
          importFile(ImportController.this.media);
        }
      });
      cancelButton.addEventListener(ON_CLICK, new EventListener<Event>() {
        @Override
        public void onEvent(Event event) throws Exception {
          importWindow.detach();
        }
      });
      cancelButtonURL.addEventListener(ON_CLICK, new EventListener<Event>() {
        @Override
        public void onEvent(Event event) throws Exception {
          importWindow.detach();
        }
      });

      win.doModal();
    } catch (Exception e) {
      LOGGER.error("Failed to construct ImportController", e);
      throw new DialogException("Error in importProcesses controller: " + e.getMessage());
    }
  }

  private void uploadFile(UploadEvent event) throws IOException {
    media = event.getMedia();
    fileNameLabel.setStyle("color: blue");
    fileNameLabel.setValue(media.getName());
    String extension = ItemNameUtils.findExtension(media.getName());

    okButton.setDisabled(true);
    List<FileImporterPlugin> fileImporterPlugins =
        (List<FileImporterPlugin>) SpringUtil.getBean(FILE_IMPORTER_PLUGINS);
    for (FileImporterPlugin fileImporterPlugin : fileImporterPlugins) {
      if (fileImporterPlugin.getFileExtensions().contains(extension)) {
        okButton.setDisabled(uploadSizeExceeded);
        return;
      }
    }

    okButton.setDisabled(uploadSizeExceeded);
  }

  /**
   * Controller for uploading file from URL
   * 
   * @param fileUrl - URL string user inputted
   */
  private void uploadFileFromURL(String fileUrl) {

    URL url;
    String filename;

    try {
      if (!StringUtil.isValidCloudStorageURL(fileUrl)) {
        note.show("URL link is not from supported cloud storage or not using valid protocol.");
        return;
      }

      fileUrl = StringUtil.parseFileURL(fileUrl);


      if ("".equals(fileUrl)) {
        note.show("URL link is empty or not correct.");
        return;
      }


      url = new URL(fileUrl.trim());

      // open the connection
      URLConnection con = url.openConnection();
      // get and verify the header field
      String fieldValue = con.getHeaderField("Content-Disposition");

      filename = StringUtil.getFileName(fileUrl, fieldValue);

      if (filename == null) {
        note.show("Couldn't find supported file. ");
        return;
      }

      fileNameLabelURL.setStyle("color: blue");
      fileNameLabelURL.setFocus(false);
      fileNameLabelURL.setValue(filename);

      File testData = new File(filename);
      FileUtils.copyURLToFile(url, testData, CONNECT_TIMEOUT, READ_TIMEOUT);
      long fileSize = testData.length();
      if (fileSize > this.maxUploadSize) {
        Notification.error(Labels.getLabel("portal_fileSizeExceeded_message"));
        return;
      }
      try (InputStream targetStream = new FileInputStream(testData)) {
        media = new MediaImpl(testData.getName(), targetStream, StandardCharsets.UTF_8,
            ItemNameUtils.findExtension(filename));
        this.fileUrl.setValue(fileUrl);
      }

      String extension = ItemNameUtils.findExtension(media.getName());

      List<FileImporterPlugin> fileImporterPlugins =
          (List<FileImporterPlugin>) SpringUtil.getBean(FILE_IMPORTER_PLUGINS);
      for (FileImporterPlugin fileImporterPlugin : fileImporterPlugins) {
        if (fileImporterPlugin.getFileExtensions().contains(extension)) {
          okButton_URL.setDisabled(false);
          return;
        }
      }

      okButton_URL.setDisabled(false);

    } catch (MalformedURLException e) {
      okButton_URL.setDisabled(true);
      note.show("URL link is empty or not correct.");
    } catch (IOException | NullPointerException e) {
      okButton_URL.setDisabled(true);
      note.show("Couldn't find supported file. Please check the URL and try again. ");
    }
  }

  void importFile(Media importedMedia)
      throws InterruptedException, IOException, ExceptionDomains, ExceptionAllUsers, JAXBException {
    String name = importedMedia.getName();
    String extension = ItemNameUtils.findExtension(name);

    // Check whether any of the pluggable file importers can handle this file
    for (FileImporterPlugin fileImporterPlugin : fileImporterPlugins) {
      if (fileImporterPlugin.getFileExtensions().contains(extension)) {
        fileImporterPlugin.importFile(importedMedia, isPublicCheckbox.isChecked());
        return;
      }
    }

    // Check the hardcoded file importer methods
    if (extension == null) {
      // ignoredFiles += (ignoredFiles.isEmpty() ? "" : " ,") + name;
      note.show("Ignoring file with no extension: " + name);
    } else if (extension.toLowerCase().equals("zip")) {
      importZip(importedMedia);
    } else if (name.toLowerCase().endsWith("xes") || name.toLowerCase().endsWith(XES_GZ)
        || name.toLowerCase().endsWith("mxml") || name.toLowerCase().endsWith(MXML_GZ)) {
      importLog(importedMedia);
    } else if (extension.toLowerCase().equals("gz")) {
      importGzip(importedMedia);
    } else if (extension.toLowerCase().equals("bpmn")) {
      importProcess(this.mainC, this, importedMedia.getStreamData(), name.split("\\.")[0], name);
    } else {
      // ignoredFiles += (ignoredFiles.isEmpty() ? "" : " ,") + name;
      note.show("Ignoring file with unsupported extension: " + name);
    }
  }

  private void importLog(Media logMedia) {
    try {
      final Integer folderId = (this.mainC.getPortalSession().getCurrentFolder() != null) ?
        this.mainC.getPortalSession().getCurrentFolder().getId() :
        0;

      String fileName = logMedia.getName();
      String extension = discoverExtension(fileName);
      LOGGER.debug("File name \"{}\", extension \"{}\"", fileName, extension);
      String logFileName = FilenameUtils.removeExtension(fileName);

      InputDialog.showInputDialog(
              Labels.getLabel("common_saveLog_text"),
              Labels.getLabel("common_saveLog_hint"),
              logFileName,
              Labels.getLabel("common_validNameRegex_text"),
              Labels.getLabel("common_validNameRegex_hint"),
              (Event e) -> {
                if (e.getName().equals("onOK")) {
                  String newName = (String)e.getData();
                  mainC.getManagerService().importLog(UserSessionManager.getCurrentUser().getUsername(),
                          folderId, newName, logMedia.getStreamData(), extension, "",
                          DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                          isPublicCheckbox.isChecked());
                  mainC.refresh();
                }
              }
      );

    } catch (Exception e) {
      LOGGER.warn("Import failed for " + logMedia.getName(), e);
      Messagebox.show(Labels.getLabel(PORTAL_FAILED_IMPORT_MESSAGE), APROMORE, Messagebox.OK,
          Messagebox.ERROR);
    } /*
       * finally { closePopup(); }
       */
  }


  /*
   * private void closePopup() { if (importWindow != null) { importWindow.detach(); } }
   */

  private String discoverExtension(String logFileName) {
    if (logFileName.endsWith("mxml")) {
      return "mxml";
    } else if (logFileName.endsWith(MXML_GZ)) {
      return MXML_GZ;
    } else if (logFileName.endsWith("xes")) {
      return "xes";
    } else if (logFileName.endsWith(XES_GZ)) {
      return XES_GZ;
    }
    return null;
  }

  /**
   * Read uploaded file: zip archive or file which contains native description in one of the
   * supported native format. zip or tar: extract files and import each if possible file: import
   * 
   * @throws InterruptedException
   */
  private void importZip(Media zippedMedia) throws InterruptedException {
    try {
      try (ZipInputStream in = zippedMedia instanceof MediaImpl
              ? new ZipInputStream(new FileInputStream(((MediaImpl) zippedMedia).getTempFile()))
              : new ZipInputStream(zippedMedia.getStreamData())) {
        ZipEntry entry;
        while ((entry = in.getNextEntry()) != null) {
          try {
            importFile(new MediaImpl(entry.getName(), in, StandardCharsets.UTF_8,
                    ItemNameUtils.findExtension(entry.getName())));
            break;

          } catch (ExceptionAllUsers | ExceptionDomains e) {
            note.show("Zip component couldn't be loaded: " + e);
          }
        }
      }
    } catch (IOException | JAXBException e) {
      LOGGER.warn("Import failed for " + zippedMedia.getName(), e);
      Messagebox.show(Labels.getLabel(PORTAL_FAILED_IMPORT_MESSAGE), APROMORE, Messagebox.OK,
          Messagebox.ERROR);
    }
  }

  private void importGzip(Media gzippedMedia)
      throws ExceptionAllUsers, ExceptionDomains, IOException, InterruptedException, JAXBException {

    GZIPInputStream in = gzippedMedia instanceof MediaImpl
        ? new GZIPInputStream(new FileInputStream(((MediaImpl) gzippedMedia).getTempFile()))
        : new GZIPInputStream(gzippedMedia.getStreamData());

    importFile(new MediaImpl(ItemNameUtils.findBasename(gzippedMedia.getName()), in,
        StandardCharsets.UTF_8,
        ItemNameUtils.findExtension(ItemNameUtils.findBasename(gzippedMedia.getName()))));
  }

  private void importProcess(MainController mainC, ImportController importC, InputStream xml_is,
      String processName, String filename) throws SuspendNotAllowedException,
      InterruptedException, IOException, ExceptionDomains, ExceptionAllUsers {
    ImportOneProcessController oneImport = new ImportOneProcessController(mainC, importC, xml_is,
        processName, BPMN_2_0, filename, isPublicCheckbox.isChecked());
    this.toImportList.add(oneImport);
  }

  /*
   * cancel all remaining imports
   */
  public void cancelAll() {
    for (ImportOneProcessController aToImportList : this.toImportList) {
      if (aToImportList.getImportOneProcessWindow() != null) {
        this.ignoredFiles += ", " + aToImportList.getFileName();
        aToImportList.getImportOneProcessWindow().detach();
      }
    }
    this.toImportList.clear();
    reportImport();
    importWindow.detach();
  }

  public List<ImportOneProcessController> getImportedList() {
    if (importedList == null) {
      importedList = new ArrayList<>();
    }
    return this.importedList;
  }

  public List<ImportOneProcessController> getToImportList() {
    if (toImportList == null) {
      toImportList = new ArrayList<>();
    }
    return this.toImportList;
  }

  // remove from the list of processes to be imported
  // if the list exhausted, display a message and terminate import
  public void deleteFromToBeImported(ImportOneProcessController importOneProcess) {
    this.toImportList.remove(importOneProcess);

    if (this.toImportList.size() == 0) {
      reportImport();
      importWindow.detach();
    }
  }

  public void reportImport() {
    String report = "Import of " + this.importedList.size();
    if (this.importedList.size() == 0) {
      report += " process.";
    }
    if (this.importedList.size() == 1) {
      report += " process completed.";
    } else if (this.importedList.size() > 1) {
      report += " processes completed.";
    }
    if (this.ignoredFiles.compareTo("") != 0) {
      report += "\n (" + this.ignoredFiles + " ignored).";
    }
    this.mainC.displayMessage(report);
  }

  /*
   * Import all remaining files. Called from ImportOneProcessController after user clicked "OK all"
   * Apply default values to all file still to be imported: - version name - domain
   */
  public void importAllProcess(String domain) throws InterruptedException {
    List<ImportOneProcessController> importAll = new ArrayList<>();
    importAll.addAll(this.toImportList);
    for (ImportOneProcessController importOneProcess : importAll) {
      try {
        importOneProcess.importProcess(domain, UserSessionManager.getCurrentUser().getUsername());
      } catch (IOException e) {
        LOGGER.warn("Import failed in domain " + domain, e);
        Messagebox.show(Labels.getLabel(PORTAL_FAILED_IMPORT_MESSAGE), APROMORE, Messagebox.OK,
            Messagebox.ERROR);
      }
    }
    this.cancelAll();
  }
}
