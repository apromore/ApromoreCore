/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2020 University of Tartu
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.apromore.dao.model.Log;
import org.apromore.exception.UserNotFoundException;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalContexts;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.service.EventLogService;
import org.apromore.service.UserMetadataService;
import org.apromore.service.logimporter.exception.InvalidLogMetadataException;
import org.apromore.service.logimporter.model.LogErrorReport;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.model.LogModel;
import org.apromore.service.logimporter.services.MetaDataService;
import org.apromore.service.logimporter.services.MetaDataUtilities;
import org.apromore.service.logimporter.services.ParquetFactoryProvider;
import org.apromore.service.logimporter.services.ParquetImporter;
import org.apromore.service.logimporter.services.ParquetImporterFactory;
import org.apromore.service.logimporter.services.legacy.LogImporter;
import org.apromore.service.logimporter.services.legacy.LogImporterProvider;
import org.apromore.service.logimporter.utilities.FileUtils;
import org.apromore.util.UserMetadataTypeEnum;
import org.apromore.zk.dialog.InputDialog;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.json.JSONObject;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * Controller for <code>csvimporter.zul</code>.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LogImporterController extends SelectorComposer<Window> implements Constants {

    /**
     * Attribute of the ZK session containing this controller's arguments.
     */
    public static final String SESSION_ATTRIBUTE_KEY = "csvimport";
    public static final String NAME_SANITIZER = "[.][^.]+$";
    public static final String DATA_TYPE_ID_PREFIX = "DataType_";
    public static final String ON_SELECT = "onSelect";

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(LogImporterController.class);
    private static final int ROW_INDEX_START_FROM = 1;
    // Get Data layer config
    private final String propertyFile = "datalayer.config";
    protected boolean isModal = true;
    protected Map<String, String> customHeaderMap = new HashMap<>();
    @WireVariable
    private EventLogService eventLogService;
    @WireVariable
    private UserMetadataService userMetadataService;
    // Fields injected from the ZK session
    private Media media =
        (Media) ((Map) Sessions.getCurrent().getAttribute(SESSION_ATTRIBUTE_KEY)).get("media");
    private PortalContext portalContext = PortalContexts.getActivePortalContext();
    private JSONObject mappingJSON =
        (JSONObject) ((Map) Sessions.getCurrent().getAttribute(SESSION_ATTRIBUTE_KEY))
            .get("mappingJSON");
    @WireVariable
    private ParquetFactoryProvider parquetFactoryProvider;
    @WireVariable
    private LogImporterProvider logImporterProvider;
    // Fields injected from csvimporter.zul
    private @Wire("#mainWindow")
    Window window;
    private @Wire("#toXESButton")
    Button toXESButton;
    private @Wire("#toPublicXESButton")
    Button toPublicXESButton;
    private @Wire("#matchedMapping")
    Button matchedMapping;
    private boolean useParquet;
    // Be default, enable Anonymize toggle in Log Importer
    private boolean enableAnonymize = true;
    private File parquetFile;

    private LogMetaData logMetaData;
    private List<List<String>> sampleLog;
    private String encoding;

    private Div popUpBox;
    private Button[] formatBtns;
    private Span[] parsedIcons;
    private Set<Checkbox> maskBtns;
    private List<Listbox> dropDownLists;
    private List<Listbox> dataTypeDropDownLists;

    private ParquetImporterFactory parquetImporterFactory;
    private MetaDataService metaDataService;
    private ParquetImporter parquetImporter;
    private MetaDataUtilities metaDataUtilities;

    private LogImporter logImporter;

    private static String getMediaFormat(Media media) throws Exception {
        if (media.getName().lastIndexOf('.') < 0) {
            throw new Exception("Can't read file format");
        }
        return media.getName().substring(media.getName().lastIndexOf('.') + 1);
    }

    @Override
    public void doFinally() throws Exception {
        super.doFinally();
        // Populate the window

        try {
            parquetImporterFactory = parquetFactoryProvider.getParquetFactory(getMediaFormat(media));
            metaDataService = parquetImporterFactory.getMetaDataService();
            parquetImporter = parquetImporterFactory.getParquetImporter();
            metaDataUtilities = parquetImporterFactory.getMetaDataUtilities();
            logImporter = logImporterProvider.getLogReader(getMediaFormat(media));

            Properties props = new Properties();
            props.load(LogImporterController.class.getClassLoader().getResourceAsStream(propertyFile));

            useParquet = Boolean.parseBoolean(props.getProperty("use.parquet"));

            File parquetDir = new File(props.getProperty("parquet.dir"));
            // make directory if not exist
            parquetDir.mkdirs();

            // Add timestamp to file name
            String fileSuffix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            parquetFile = new File(parquetDir.getPath() + File.separator
                + media.getName().replace("." + getMediaFormat(media), fileSuffix + ".parquet"));

            Combobox setEncoding = (Combobox) window.getFellow(SET_ENCODING_ID);
            setEncoding.setModel(new ListModelList<>(FILE_ENCODING));

            setEncoding.addEventListener(ON_SELECT, event -> {
                try {
                    encoding = getFileEncoding();
                    metaDataService.validateLog(getInputSream(media), encoding);
                    this.logMetaData =
                        metaDataService.extractMetadata(getInputSream(media), encoding, customHeaderMap);
                    this.sampleLog = metaDataService.generateSampleLog(getInputSream(media), LOG_SAMPLE_SIZE,
                        encoding);
                    this.logMetaData = metaDataUtilities.processMetaData(this.logMetaData, this.sampleLog);
                    this.logMetaData.setEncoding(encoding);

                } catch (Exception e) {
                    Messagebox.show(getLabel("failedImportEncoding"), getLabel("error"), Messagebox.OK,
                        Messagebox.ERROR);
                } finally {
                    if (logMetaData != null && !sampleLog.isEmpty()) {
                        setUpUI();
                    }
                }
            });

            Combobox setTimeZone = (Combobox) window.getFellow(SET_TIME_ZONE_ID);

            Calendar cal = Calendar.getInstance();
            TimeZone timeZone = cal.getTimeZone();
            int offset = timeZone.getRawOffset() / 1000;
            int hour = offset / 3600;
            int minutes = (offset % 3600) / 60;
            ListModelList<String> model = getTimeZoneList();
            String defaultValue = String.format("(GMT%+d:%02d) %s", hour, minutes, timeZone.getID());

            model.addToSelection(defaultValue);
            setTimeZone.setModel(model);
            setTimeZone.setValue(defaultValue);

            /*
             * // Disable for now until timezone backend handling is resolved
             * setTimeZone.addEventListener("onSelect", event -> { if (getTimeZone() == null) {
             * this.logMetaData.setTimeZone(defaultValue.split(" ")[1]); } else {
             * this.logMetaData.setTimeZone(getTimeZone()); } });
             */

            setTimeZone.addEventListener("onClientUpdate", event -> {
                JSONObject param = (JSONObject) event.getData();
                String gmtOffset = (String) param.get("offset");
                String tz = (String) param.get("tz");
                String value = String.format("(%s) %s", gmtOffset, tz);
                setTimeZone.setValue(value);
                this.logMetaData.setTimeZone(tz);
            });

            metaDataService.validateLog(getInputSream(media), getFileEncoding());
            LogMetaData tempLogMetaData =
                metaDataService.extractMetadata(getInputSream(media), getFileEncoding(), customHeaderMap);
            this.sampleLog =
                metaDataService.generateSampleLog(getInputSream(media), LOG_SAMPLE_SIZE, getFileEncoding());
            tempLogMetaData = metaDataUtilities.processMetaData(tempLogMetaData, this.sampleLog);

            if (mappingJSON != null) {
                getSchemaMappingFromJson(tempLogMetaData, mappingJSON);
            }

            if (getTimeZone() == null) {
                tempLogMetaData.setTimeZone(defaultValue.split(" ")[1]);
            } else {
                tempLogMetaData.setTimeZone(getTimeZone());
            }
            this.logMetaData = tempLogMetaData;

            setUpUI();
            toXESButton.setDisabled(false);
            toPublicXESButton.setDisabled(false);
            matchedMapping.setDisabled(false);

        } catch (Exception e) {
            LOGGER.error("Failure while creating controller", e);
            Messagebox.show(getLabel("failed_to_read_log"), getLabel("error"), Messagebox.OK,
                Messagebox.ERROR, event -> close());
        }
        // Clients.evalJavaScript("Ap.common.pullClientTimeZone()");
    }

    private void getSchemaMappingFromJson(LogMetaData tempLogMetaData, JSONObject mappingJSON) {

        ObjectMapper objectMapper = new ObjectMapper();
        LogMetaData storedSchemaMapping = null;
        try {
            storedSchemaMapping = objectMapper.readValue(mappingJSON.toJSONString(), LogMetaData.class);
        } catch (JsonProcessingException e) {
            LOGGER.error(
                "Could not deserialize JSON content from given JSON content String: " + mappingJSON.toJSONString(), e);
        }

        if (storedSchemaMapping != null) {
            tempLogMetaData.setCaseIdPos(storedSchemaMapping.getCaseIdPos());
            tempLogMetaData.setActivityPos(storedSchemaMapping.getActivityPos());
            tempLogMetaData.setEndTimestampFormat(storedSchemaMapping.getEndTimestampFormat());
            tempLogMetaData.setEndTimestampPos(storedSchemaMapping.getEndTimestampPos());
            tempLogMetaData.setStartTimestampFormat(storedSchemaMapping.getStartTimestampFormat());
            tempLogMetaData.setStartTimestampPos(storedSchemaMapping.getStartTimestampPos());
            tempLogMetaData.setResourcePos(storedSchemaMapping.getResourcePos());
            tempLogMetaData.setRolePos(storedSchemaMapping.getRolePos());
            tempLogMetaData.getEventAttributesPos().clear();
            tempLogMetaData.getEventAttributesPos().addAll(storedSchemaMapping.getEventAttributesPos());
            tempLogMetaData.getCaseAttributesPos().clear();
            tempLogMetaData.getCaseAttributesPos().addAll(storedSchemaMapping.getCaseAttributesPos());
            tempLogMetaData.getIgnoredPos().clear();
            tempLogMetaData.getIgnoredPos().addAll(storedSchemaMapping.getIgnoredPos());
            tempLogMetaData.getPerspectivePos().clear();
            if (storedSchemaMapping.getPerspectivePos() != null) {
                tempLogMetaData.getPerspectivePos().addAll(storedSchemaMapping.getPerspectivePos());
            }
            tempLogMetaData.getOtherTimestamps().clear();
            tempLogMetaData.getOtherTimestamps().putAll(storedSchemaMapping.getOtherTimestamps());
            tempLogMetaData.setIntegerAttributesPos(storedSchemaMapping.getIntegerAttributesPos());
            tempLogMetaData.setDoubleAttributesPos(storedSchemaMapping.getDoubleAttributesPos());
            tempLogMetaData.setStringAttributesPos(storedSchemaMapping.getStringAttributesPos());
            tempLogMetaData.setTimestampAttributesPos(storedSchemaMapping.getTimestampAttributesPos());
        }
    }

    // Create a dialog to ask for user option regarding matched schema mapping
    private void handleMatchedMapping() throws IOException {
        Map<String, Object> arg = new HashMap<>();
        arg.put("labels", getLabels());
        Window matchedMappingPopUp =
            (Window) portalContext.getUI().createComponent(LogImporterController.class.getClassLoader(),
                "zul/matchedMapping.zul", null, arg);
        matchedMappingPopUp.doModal();
    }

    @Listen("onClick = #cancelButton")
    public void onClickCancelBtn(MouseEvent event) {
        close();
    }

    @Listen("onClick = #setOtherAll")
    public void ignoreToEvent(MouseEvent event) {
        Listbox lb = (Listbox) window.getFellow(String.valueOf(0));
        int eventAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(EVENT_ATTRIBUTE_LABEL));
        int ignoreAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(IGNORE_LABEL));

        for (int pos = 0; pos < logMetaData.getHeader().size(); pos++) {
            lb = (Listbox) window.getFellow(String.valueOf(pos));

            Listbox dataTypeListBox = (Listbox) window.getFellow(DATA_TYPE_ID_PREFIX + pos);
            String dataTypeSelected = dataTypeListBox.getSelectedItem().getValue();

            if (lb.getSelectedIndex() == ignoreAttributeIndex) {
                logMetaData.getIgnoredPos().remove(Integer.valueOf(pos));

                if (TIMESTAMP_TYPE_LABEL.equals(dataTypeSelected)) {
                    timestampSelected(pos, OTHER_TIMESTAMP_LABEL);
                } else {
                    logMetaData.getEventAttributesPos().add(pos);
                }

                lb.setSelectedIndex(eventAttributeIndex);
            }
        }
    }

    @Listen("onClick = #setIgnoreAll")
    public void eventToIgnore(MouseEvent event) {
        Listbox lb = (Listbox) window.getFellow(String.valueOf(0));
        int eventAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(EVENT_ATTRIBUTE_LABEL));
        int ignoreAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(IGNORE_LABEL));

        for (int pos = 0; pos < logMetaData.getHeader().size(); pos++) {
            lb = (Listbox) window.getFellow(String.valueOf(pos));

            Listbox dataTypeListBox = (Listbox) window.getFellow(DATA_TYPE_ID_PREFIX + pos);
            String dataTypeSelected = dataTypeListBox.getSelectedItem().getValue();

            if (lb.getSelectedIndex() == eventAttributeIndex) {

                if (TIMESTAMP_TYPE_LABEL.equals(dataTypeSelected)) {
                    logMetaData.getOtherTimestamps().remove(pos);
                    closePopUpBox(pos);
                    hideFormatBtn(formatBtns[pos]);
                    hideParsedIcon(parsedIcons[pos]);
                } else {
                    logMetaData.getEventAttributesPos().remove(Integer.valueOf(pos));
                }
                logMetaData.getIgnoredPos().add(pos);
                lb.setSelectedIndex(ignoreAttributeIndex);
            }
        }
    }

    @Listen("onClick = #toXESButton; onClick = #toPublicXESButton")
    public void convertToXes(MouseEvent event) {
        boolean isLogPublic = "toPublicXESButton".equals(event.getTarget().getId());
        String name = "untitled";
        if (!useParquet) {
            name = media.getName().replaceFirst(NAME_SANITIZER, "");
            InputDialog.showInputDialog(
                Labels.getLabel("common_saveLog_text"),
                Labels.getLabel("common_saveLog_hint"),
                name,
                Labels.getLabel("common_validNameRegex_text"),
                Labels.getLabel("common_validNameRegex_hint"),
                (Event e) -> {
                    if (e.getName().equals("onOK")) {
                        String newName = (String) e.getData();
                        toXES(newName, isLogPublic);
                    }
                }
            );
        } else {
            toXES(name, isLogPublic);
        }
    }

    public void toXES(String name, boolean isLogPublic) {
        try {
            LogModel logModel = getLogModel(name);
            if (logModel != null) {
                List<LogErrorReport> errorReport = logModel.getLogErrorReport();

                if (errorReport.isEmpty()) {
                    saveXLog(logModel, isLogPublic);
                } else {
                    handleInvalidData(logModel, isLogPublic, name);
                }
            }
        } catch (MissingHeaderFieldsException e) {
            Messagebox.show(getLabel("missing_fields") + System.lineSeparator() + System.lineSeparator()
                    + e.getMessage(), getLabel("error"),
                Messagebox.OK,
                Messagebox.ERROR);
        } catch (Exception e) {
            Messagebox.show(getLabel("failedExportXES"), getLabel("error"), Messagebox.OK,
                Messagebox.ERROR);
            LOGGER.error("Conversion to XES button handler failed", e);
        }
    }

    /**
     * @throws MissingHeaderFieldsException if {@link #validateUniqueAttributes} doesn't pass
     */
    protected void validateHeaderFields() throws MissingHeaderFieldsException {
        StringBuilder headNOTDefined = validateUniqueAttributes();
        if (headNOTDefined.length() != 0) {
            throw new MissingHeaderFieldsException(headNOTDefined.toString());
        }
    }

    /**
     * @return the log as currently configured by this UI
     * @throws MissingHeaderFieldsException if {@link #validateUniqueAttributes} doesn't pass
     * @throws Exception                    if the log can't be obtained otherwise
     */
    protected LogModel getLogModel(String name) throws Exception {
        validateHeaderFields();

        LogModel logModel;
        if (useParquet) {
            logModel = parquetImporter.importParquetFile(getInputSream(media), logMetaData,
                getFileEncoding(), parquetFile, false);
        } else {
            logModel = logImporter.importLog(getInputSream(media), logMetaData, getFileEncoding(), false,
                portalContext.getCurrentUser().getUsername(),
                portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId(),
                name);
        }

        return logModel;
    }

    public void storeMetadataAsJSON(LogMetaData logMetaData, Log log, String username)
        throws UserNotFoundException {
        String logMetadataJsonStr;
        String perspectiveJsonStr;

        // Creating Object of ObjectMapper define in Jakson Api
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            logMetadataJsonStr = objectMapper.writeValueAsString(logMetaData);
            perspectiveJsonStr = objectMapper.writeValueAsString(logMetaData.getPerspectives());

            userMetadataService.saveUserMetadata("Default CSV schema mapping name", logMetadataJsonStr,
                UserMetadataTypeEnum.CSV_IMPORTER, username, log.getId());
            userMetadataService.saveUserMetadata(UserMetadataTypeEnum.PERSPECTIVE_TAG.toString(), perspectiveJsonStr,
                UserMetadataTypeEnum.PERSPECTIVE_TAG, username, log.getId());
        } catch (JsonProcessingException | InvalidLogMetadataException e) {
            LOGGER.error("Unable to convert Log Metadata into JSON or Invalid Log Metadata, give up storing them into" +
                " DB for Log ({}). ", log.getName(), e);
        }
    }

    protected String getLogTag() {
        // Creating Object of ObjectMapper define in Jakson Api
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(logMetaData);
        } catch (IOException e) {
            LOGGER.error("Unable to convert log metadata into JSON; will store an empty string instead",
                e);
        }
        return null;
    }

    public LogMetaData getLogMetaData() {
        return logMetaData;
    }

    public ResourceBundle getLabels() {

        Locale locale = (Locale) Sessions.getCurrent().getAttribute(Attributes.PREFERRED_LOCALE);
        return ResourceBundle.getBundle(PluginMeta.PLUGIN_ID, locale,
            LogImporterController.class.getClassLoader());
    }

    public String getLabel(String key) {
        return getLabels().getString(key);
    }

    // Internal methods handling page setup (doFinally)
    private String getFileEncoding() {
        Combobox setEncoding = (Combobox) window.getFellow(SET_ENCODING_ID);
        return setEncoding.getValue().contains(" ")
            ? setEncoding.getValue().substring(0, setEncoding.getValue().indexOf(' '))
            : setEncoding.getValue();
    }

    private String getTimeZone() {
        Combobox setTimeZone = (Combobox) window.getFellow(SET_TIME_ZONE_ID);
        return setTimeZone.getValue().split(" ")[1];
    }

    private void setUpUI() {
        // Set up window size
        if (logMetaData.getHeader().size() > 8) {
            window.setMaximizable(true);
            window.setMaximized(true);
        } else {
            window.setMaximizable(false);
            int size = INDEX_COLUMN_WIDTH + logMetaData.getHeader().size() * COLUMN_WIDTH + 35;
            window.setWidth(size + "px");
        }
        String title =
            Labels.getLabel("csvImporter_title_text", "Log Importer") + " - " + media.getName();
        window.setTitle(title);

        setDropDownLists();
        setDataTypeDropDownLists();
        setCSVGrid();
        renderGridContent();
        setPopUpFormatBox();
    }

    private void setCSVGrid() {
        Grid myGrid = (Grid) window.getFellow(MY_GRID_ID);

        myGrid.getChildren().clear();
        (new Columns()).setParent(myGrid);
        myGrid.getColumns().getChildren().clear();


        // dropdown lists
        Auxhead optionHead = new Auxhead();
        Auxheader index = new Auxheader();
        optionHead.appendChild(index);
        for (Listbox list : dropDownLists) {
            Auxheader listHeader = new Auxheader();
            listHeader.appendChild(list);
            optionHead.appendChild(listHeader);
        }
        myGrid.appendChild(optionHead);

        // Data type dropdown lists
        Auxhead dataTypeOptionHead = new Auxhead();
        Auxheader dataTypeIndex = new Auxheader();
        dataTypeOptionHead.appendChild(dataTypeIndex);
        for (Listbox list : dataTypeDropDownLists) {
            Auxheader listHeader = new Auxheader();
            listHeader.appendChild(list);
            dataTypeOptionHead.appendChild(listHeader);
        }
        myGrid.appendChild(dataTypeOptionHead);


        // index column
        Column indexCol = new Column();
        indexCol.setWidth(INDEX_COLUMN_WIDTH + "px");
        indexCol.setValue("");
        indexCol.setLabel("#");
        indexCol.setAlign("center");
        myGrid.getColumns().appendChild(indexCol);


        // set columns
        Button[] formatBtns = new Button[logMetaData.getHeader().size()];
        Span[] parsedIcons = new Span[logMetaData.getHeader().size()];
        Set<Checkbox> maskBtns = new HashSet<>();
        for (int pos = 0; pos < logMetaData.getHeader().size(); pos++) {
            Column newColumn = new Column();
            newColumn.setId(HEADER_COLUMN_ID + pos);
            String header = logMetaData.getHeader().get(pos);
            String label = "".equals(header) ? "Column" + (pos + 1) : header;
            newColumn.setValue(label);
            newColumn.setLabel(label);
            int labelLen = (label.length() * 14) + 20;
            if (labelLen < COLUMN_WIDTH) {
                labelLen = COLUMN_WIDTH;
            }
            newColumn.setWidth(labelLen + "px");
            newColumn.setAlign("center");

            Checkbox maskBtn = new Checkbox();
            maskBtn.setId(MASK_CHECKBOX_ID + pos);
            maskBtn.setTooltiptext(getLabels().getString("Anonymize"));
            maskBtn.setMold("switch");
            maskBtn.setSclass("ap-iconized-checkbox");

            Button formatBtn = new Button();
            Span parsedIcon = new Span();

            if (pos == logMetaData.getEndTimestampPos() || pos == logMetaData.getStartTimestampPos()
                || logMetaData.getOtherTimestamps().containsKey(pos)) {
                showFormatBtn(formatBtn);
                showAutoParsedGreenIcon(parsedIcon);
                // No anonymization for timestamps
                hideCheckbox(maskBtn);
            } else {
                hideFormatBtn(formatBtn);
            }

            formatBtn.setIconSclass("z-icon-wrench");

            final int fi = pos;
            formatBtn.addEventListener(Events.ON_CLICK, event -> openPopUpBox(fi));
            maskBtn.addEventListener(Events.ON_CHECK, event -> applyMask(fi));
            formatBtns[pos] = formatBtn;
            parsedIcons[pos] = parsedIcon;
            maskBtns.add(maskBtn);

            newColumn.appendChild(parsedIcon);
            newColumn.appendChild(formatBtn);
            newColumn.appendChild(new Label(" "));
            newColumn.appendChild(maskBtn);
            myGrid.getColumns().appendChild(newColumn);
            myGrid.getColumns().setSizable(true);
        }

        this.formatBtns = formatBtns;
        this.parsedIcons = parsedIcons;
        this.maskBtns = maskBtns;
    }

    private void renderGridContent() {
        Grid myGrid = (Grid) window.getFellow(MY_GRID_ID);

        int index = ROW_INDEX_START_FROM;
        Rows rows = new Rows();
        for (List<String> myLine : sampleLog) {
            Row row = new Row();
            row.appendChild(new Label(index + ""));
            for (String s : myLine) {
                Label lbl = new Label(s);
                lbl.setMultiline(false);
                lbl.setTooltiptext(s);
                row.appendChild(lbl);
            }

            rows.appendChild(row);
            index++;
        }

        if (LOG_SAMPLE_SIZE <= sampleLog.size()) {
            Row dummyRow = new Row();
            dummyRow.appendChild(new Label(".."));
            dummyRow.appendChild(new Label("..."));
            dummyRow.appendChild(new Label("...."));

            rows.appendChild(dummyRow);
        }

        myGrid.appendChild(rows);
    }

    private void setPopUpFormatBox() {
        Div popUPBox = (Div) window.getFellow(POP_UP_DIV_ID);
        if (popUPBox != null) {
            popUPBox.getChildren().clear();
        }
        Popup helpP = (Popup) window.getFellow(POP_UP_HELP_ID);

        for (int pos = 0; pos < logMetaData.getHeader().size(); pos++) {
            Window item = new Window();
            item.setId(POP_UP_FORMAT_WINDOW_ID + pos);
            item.setWidth(COLUMN_WIDTH + "px");
            item.setMinheight(100);
            item.setClass("p-1");
            item.setBorder("normal");
            item.setStyle("visibility: hidden;");

            Button sp = new Button();
            sp.setStyle(
                "margin-right:1px; float: right; line-height: 10px; min-height: 5px; padding:3px;");
            sp.setIconSclass("z-icon-times");

            A hidelink = new A();
            hidelink.appendChild(sp);
            sp.addEventListener(Events.ON_CLICK,
                (Event event) -> item.setStyle(item.getStyle().replace("visible", "hidden")));

            Label popUpLabel = new Label();
            popUpLabel.setId(POP_UP_LABEL_ID + pos);
            if (pos == logMetaData.getEndTimestampPos() || pos == logMetaData.getStartTimestampPos()
                || logMetaData.getOtherTimestamps().containsKey(pos)) {
                setPopUpLabel(pos, Parsed.AUTO, popUpLabel);
            }

            Textbox textbox = new Textbox();
            textbox.setId(POP_UP_TEXT_BOX_ID + pos);
            textbox.setWidth("98%");
            textbox.setPlaceholder("dd-MM-yyyy HH:mm:ss");
            if (pos == logMetaData.getEndTimestampPos()) {
                textbox.setPlaceholder(logMetaData.getEndTimestampFormat());
                textbox.setValue(logMetaData.getEndTimestampFormat());
            } else if (pos == logMetaData.getStartTimestampPos()) {
                textbox.setPlaceholder(logMetaData.getStartTimestampFormat());
                textbox.setValue(logMetaData.getStartTimestampFormat());
            } else if (logMetaData.getOtherTimestamps().containsKey(pos)) {
                textbox.setPlaceholder(logMetaData.getOtherTimestamps().get(pos));
                textbox.setValue(logMetaData.getOtherTimestamps().get(pos));
            }

            textbox.setPopup(helpP);
            textbox.setClientAttribute("spellcheck", "false");
            textbox.setPopupAttributes(helpP, "after_start", "", "", "toggle");

            textbox.addEventListener("onChanging", (InputEvent event) -> {
                int colPos = Integer.parseInt(textbox.getId().replace(POP_UP_TEXT_BOX_ID, ""));
                Listbox box = dropDownLists.get(colPos);
                String selected = box.getSelectedItem().getValue();
                resetSelect(colPos);

                if (StringUtils.isBlank(event.getValue())) {
                    textbox.setValue("");
                    textbox.setPlaceholder(getLabel("specify_timestamp_format"));
                    failedToParse(colPos);
                } else {
                    String format = event.getValue();
                    try {
                        if (metaDataUtilities.isTimestamp(colPos, format, sampleLog)) {
                            parsedManual(colPos, selected, format);
                        } else {
                            failedToParse(colPos);
                        }
                    } catch (IllegalArgumentException e) {
                        failedToParse(colPos);
                        throw e;
                    } catch (Exception e) {
                        failedToParse(colPos);
                        throw new Exception(e.getMessage());
                    }
                }
            });

            item.appendChild(hidelink);
            item.appendChild(popUpLabel);
            item.appendChild(textbox);

            assert popUPBox != null;
            popUPBox.appendChild(item);
        }
        assert popUPBox != null;
        popUPBox.clone();

        this.popUpBox = popUPBox;
    }

    private void setDataTypeDropDownLists() {

        List<Listbox> menuDropDownLists = new ArrayList<>();
        LinkedHashMap<String, String> menuItems = new LinkedHashMap<>();

        menuItems.put(INTEGER_TYPE_LABEL, getLabel("integer"));
        menuItems.put(REAL_TYPE_LABEL, getLabel("real"));
        menuItems.put(STRING_TYPE_LABEL, getLabel("string"));
        menuItems.put(TIMESTAMP_TYPE_LABEL, getLabel("timestamp"));

        for (int pos = 0; pos < logMetaData.getHeader().size(); pos++) {
            Listbox box = new Listbox();
            box.setMold("select");
            box.setId(DATA_TYPE_ID_PREFIX + pos);
            box.setWidth(COLUMN_WIDTH - 12 + "px");

            reassignDataTypeBasedOnAttributeType(pos, box);

            for (Map.Entry<String, String> myItem : menuItems.entrySet()) {
                Listitem item = new Listitem();
                item.setValue(myItem.getKey());
                item.setLabel(myItem.getValue());
                item.setId(myItem.getKey());

                if ((box.getSelectedItem() == null)
                    &&
                    ((myItem.getKey().equals(INTEGER_TYPE_LABEL)
                        && logMetaData.getIntegerAttributesPos().contains(pos))
                        || (myItem.getKey().equals(REAL_TYPE_LABEL)
                        && logMetaData.getDoubleAttributesPos().contains(pos))
                        || (myItem.getKey().equals(STRING_TYPE_LABEL)
                        && logMetaData.getStringAttributesPos().contains(pos))
                        || (myItem.getKey().equals(TIMESTAMP_TYPE_LABEL)
                        && logMetaData.getTimestampAttributesPos().contains(pos)))) {

                    item.setSelected(true);
                }
                box.appendChild(item);
            }

            box.addEventListener(ON_SELECT, (Event event) -> {
                String selected = box.getSelectedItem().getValue();
                int colPos = Integer.parseInt(box.getId().substring(9));

                resetDataTypeSelect(colPos);
                setDataTypeMetaData(selected, colPos);
            });

            menuDropDownLists.add(box);
        }
        this.dataTypeDropDownLists = menuDropDownLists;

    }

    /**
     * The data-type is not relevant for attributes with tags CaseID, Activity, Start/End timestamps,
     * Resource and Role. Because the type of these attributes is pre-determined, as follows:
     * 1. CaseID, Activity, Resource and Role are always of type String
     * 2. Start Timestamp and End Timestamps are always attributes of type Timestamp
     * Reassign data type on the fly since LogMetaData may come from persisted schema mapping
     *
     * @param pos Position of current column
     * @param box ListBox
     */
    private void reassignDataTypeBasedOnAttributeType(int pos, Listbox box) {

        if (logMetaData.getCaseIdPos() == pos || logMetaData.getActivityPos() == pos
            || logMetaData.getResourcePos() == pos || logMetaData.getRolePos() == pos) {
            box.setDisabled(true);
            resetDataTypeSelect(pos);
            logMetaData.getStringAttributesPos().add(pos);
        } else if (logMetaData.getStartTimestampPos() == pos || logMetaData.getEndTimestampPos() == pos) {
            box.setDisabled(true);
            resetDataTypeSelect(pos);
            logMetaData.getTimestampAttributesPos().add(pos);
        }
    }

    private void setDataTypeMetaData(String selected, int colPos) {

        Listbox attributeTypeListBox = (Listbox) window.getFellow(Integer.toString(colPos));
        String attributeTypeSelected = attributeTypeListBox.getSelectedItem().getValue();
        Checkbox maskBtn = (Checkbox) window.getFellow(MASK_CHECKBOX_ID + colPos);

        switch (selected) {
            case INTEGER_TYPE_LABEL:
                logMetaData.getIntegerAttributesPos().add(colPos);
                unassignOtherTimestamp(colPos, attributeTypeSelected, maskBtn);
                break;
            case REAL_TYPE_LABEL:
                logMetaData.getDoubleAttributesPos().add(colPos);
                unassignOtherTimestamp(colPos, attributeTypeSelected, maskBtn);
                break;
            case STRING_TYPE_LABEL:
                logMetaData.getStringAttributesPos().add(colPos);
                unassignOtherTimestamp(colPos, attributeTypeSelected, maskBtn);
                break;
            case TIMESTAMP_TYPE_LABEL:
                // Remove pos from Case/Event attribute list
                if (CASE_ATTRIBUTE_LABEL.equals(attributeTypeSelected)) {
                    logMetaData.getCaseAttributesPos().remove(Integer.valueOf(colPos));
                } else if (EVENT_ATTRIBUTE_LABEL.equals(attributeTypeSelected)) {
                    logMetaData.getEventAttributesPos().remove(Integer.valueOf(colPos));
                }
                logMetaData.getTimestampAttributesPos().add(colPos);
                hideCheckbox(maskBtn);
                break;
            default:
        }
    }

    private void unassignOtherTimestamp(int colPos, String attributeTypeSelected, Checkbox maskBtn) {
        if (logMetaData.getOtherTimestamps().containsKey(colPos)) {

            logMetaData.getOtherTimestamps().remove(colPos);

            if (CASE_ATTRIBUTE_LABEL.equals(attributeTypeSelected)) {
                logMetaData.getCaseAttributesPos().add(colPos);
            } else if (EVENT_ATTRIBUTE_LABEL.equals(attributeTypeSelected)) {
                logMetaData.getEventAttributesPos().add(colPos);
            }
        }

        closePopUpBox(colPos);
        hideFormatBtn(formatBtns[colPos]);
        hideParsedIcon(parsedIcons[colPos]);
        showCheckbox(maskBtn);
    }

    private void setDropDownLists() {

        List<Listbox> menuDropDownLists = new ArrayList<>();
        LinkedHashMap<String, String> menuItems = new LinkedHashMap<>();

        menuItems.put(CASE_ID_LABEL, getLabel("case_id"));
        menuItems.put(ACTIVITY_LABEL, getLabel("activity"));
        menuItems.put(END_TIMESTAMP_LABEL, getLabel("end_timestamp"));
        menuItems.put(START_TIMESTAMP_LABEL, getLabel("start_timestamp"));
        menuItems.put(RESOURCE_LABEL, getLabel("resource"));
        menuItems.put(ROLE_LABEL, getLabel("role"));
        menuItems.put(CASE_ATTRIBUTE_LABEL, getLabel("case_attribute"));
        menuItems.put(EVENT_ATTRIBUTE_LABEL, getLabel("event_attribute"));
        menuItems.put(PERSPECTIVE_LABEL, getLabel("perspective"));
        menuItems.put(IGNORE_LABEL, getLabel("ignore_attribute"));

        for (int pos = 0; pos < logMetaData.getHeader().size(); pos++) {
            final String head = logMetaData.getHeader().get(pos);
            Listbox box = new Listbox();
            // set listBox to select mode
            box.setMold("select");
            // set id of list as column position
            box.setId(String.valueOf(pos));
            box.setWidth(COLUMN_WIDTH - 12 + "px");

            for (Map.Entry<String, String> myItem : menuItems.entrySet()) {
                Listitem item = new Listitem();
                item.setValue(myItem.getKey());
                item.setLabel(myItem.getValue());
                item.setId(myItem.getKey());

                if ((box.getSelectedItem() == null)
                    && ((myItem.getKey().equals(CASE_ID_LABEL) && logMetaData.getCaseIdPos() == pos)
                    || (myItem.getKey().equals(ACTIVITY_LABEL) && logMetaData.getActivityPos() == pos)
                    || (myItem.getKey().equals(END_TIMESTAMP_LABEL) && logMetaData.getEndTimestampPos() == pos))

                    || (myItem.getKey().equals(START_TIMESTAMP_LABEL) && logMetaData.getStartTimestampPos() == pos)

                    || (myItem.getKey().equals(RESOURCE_LABEL) && logMetaData.getResourcePos() == pos)

                    || (myItem.getKey().equals(ROLE_LABEL) && logMetaData.getRolePos() == pos)

                    ||
                    (myItem.getKey().equals(CASE_ATTRIBUTE_LABEL) && logMetaData.getCaseAttributesPos().contains(pos))

                    // When this head is in Perspective tag list, select PERSPECTIVE_LABEL instead of
                    // EVENT_ATTRIBUTE_LABEL
                    ||
                    (myItem.getKey().equals(EVENT_ATTRIBUTE_LABEL) && (logMetaData.getEventAttributesPos().contains(pos)
                        || ((Map<Integer, String>) logMetaData.getOtherTimestamps()).containsKey(pos))
                        && !logMetaData.getPerspectivePos().contains(pos))

                    || (myItem.getKey().equals(IGNORE_LABEL) && logMetaData.getIgnoredPos().contains(pos))

                    || (myItem.getKey().equals(PERSPECTIVE_LABEL) && logMetaData.getPerspectivePos().contains(pos))) {

                    item.setSelected(true);
                }
                box.appendChild(item);
            }

            box.addEventListener(ON_SELECT, (Event event) -> {
                int colPos = Integer.parseInt(box.getId());

                resetSelect(colPos);
                closePopUpBox(colPos);
                hideFormatBtn(formatBtns[colPos]);
                hideParsedIcon(parsedIcons[colPos]);

                String selected = box.getSelectedItem().getValue();

                Listbox dataTypeListBox = (Listbox) window.getFellow(DATA_TYPE_ID_PREFIX + colPos);
                String dataTypeSelected = dataTypeListBox.getSelectedItem().getValue();

                switch (selected) {
                    case CASE_ID_LABEL:
                        resetUniqueAttribute(logMetaData.getCaseIdPos(), colPos, CASE_ID_LABEL);
                        logMetaData.setCaseIdPos(colPos);
                        logMetaData = metaDataUtilities.resetCaseAndEventAttributes(logMetaData, sampleLog);

                        Listbox lb = (Listbox) window.getFellow(String.valueOf(0));
                        int eventAttributeIndex =
                            lb.getIndexOfItem((Listitem) lb.getFellow(EVENT_ATTRIBUTE_LABEL));
                        int caseAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(CASE_ATTRIBUTE_LABEL));

                        for (int caseAttriPos : logMetaData.getCaseAttributesPos()) {
                            lb = (Listbox) window.getFellow(String.valueOf(caseAttriPos));
                            lb.setSelectedIndex(caseAttributeIndex);
                        }

                        lb.getSelectedItem().setStyle("background: #f00 !important;");

                        for (int eventAttriPos : logMetaData.getEventAttributesPos()) {
                            lb = (Listbox) window.getFellow(String.valueOf(eventAttriPos));
                            lb.setSelectedIndex(eventAttributeIndex);
                        }

                        String message =
                            MessageFormat.format(getLabels().getString("reset_event_case_attributes"), head);

                        Notification.info(message);

                        break;
                    case ACTIVITY_LABEL:
                        resetUniqueAttribute(logMetaData.getActivityPos(), colPos, ACTIVITY_LABEL);
                        logMetaData.setActivityPos(colPos);
                        break;
                    case END_TIMESTAMP_LABEL:
                        resetUniqueAttribute(logMetaData.getEndTimestampPos(), colPos, END_TIMESTAMP_LABEL);
                        timestampSelected(colPos, selected);
                        break;
                    case START_TIMESTAMP_LABEL:
                        resetUniqueAttribute(logMetaData.getStartTimestampPos(), colPos, START_TIMESTAMP_LABEL);
                        timestampSelected(colPos, selected);
                        break;
                    case RESOURCE_LABEL:
                        resetUniqueAttribute(logMetaData.getResourcePos(), colPos, RESOURCE_LABEL);
                        logMetaData.setResourcePos(colPos);
                        break;
                    case ROLE_LABEL:
                        resetUniqueAttribute(logMetaData.getRolePos(), colPos, ROLE_LABEL);
                        logMetaData.setRolePos(colPos);
                        break;
                    case OTHER_TIMESTAMP_LABEL:
                        timestampSelected(colPos, selected);
                        break;
                    case CASE_ATTRIBUTE_LABEL:
                        if (TIMESTAMP_TYPE_LABEL.equals(dataTypeSelected)) {
                            timestampSelected(colPos, OTHER_TIMESTAMP_LABEL);
                        } else {
                            logMetaData.getCaseAttributesPos().add(colPos);
                        }
                        resetDatatypeDropDownList(colPos);
                        break;
                    case EVENT_ATTRIBUTE_LABEL:
                        if (TIMESTAMP_TYPE_LABEL.equals(dataTypeSelected)) {
                            timestampSelected(colPos, OTHER_TIMESTAMP_LABEL);
                        } else {
                            logMetaData.getEventAttributesPos().add(colPos);
                        }
                        resetDatatypeDropDownList(colPos);
                        break;
                    case IGNORE_LABEL:
                        logMetaData.getIgnoredPos().add(colPos);
                        resetDatatypeDropDownList(colPos);
                        break;
                    case PERSPECTIVE_LABEL:
                        logMetaData.getEventAttributesPos().add(colPos);
                        logMetaData.getPerspectivePos().add(colPos);
                        resetDatatypeDropDownList(colPos);
                        break;
                    default:
                }
            });

            menuDropDownLists.add(box);
        }

        this.dropDownLists = menuDropDownLists;
    }

    private void resetUniqueAttribute(int oldColPos, int newColPos, String attributeType) {

        Listbox newDataTypeListBox = (Listbox) window.getFellow(DATA_TYPE_ID_PREFIX + newColPos);
        int stringTypeIndex =
            newDataTypeListBox.getIndexOfItem((Listitem) newDataTypeListBox.getFellow(STRING_TYPE_LABEL));
        int timestampTypeIndex =
            newDataTypeListBox.getIndexOfItem((Listitem) newDataTypeListBox.getFellow(TIMESTAMP_TYPE_LABEL));
        int dataTypeIndex = -1;

        switch (attributeType) {
            case CASE_ID_LABEL:
            case ACTIVITY_LABEL:
            case RESOURCE_LABEL:
            case ROLE_LABEL:
                dataTypeIndex = stringTypeIndex;
                break;
            case END_TIMESTAMP_LABEL:
            case START_TIMESTAMP_LABEL:
                dataTypeIndex = timestampTypeIndex;
                break;
            default:
                break;
        }

        if (oldColPos != -1) {
            // reset value of the unique attribute
            resetSelect(oldColPos);
            closePopUpBox(oldColPos);
            hideFormatBtn(formatBtns[oldColPos]);
            hideParsedIcon(parsedIcons[oldColPos]);

            // set it as event attribute
            Listbox lb = (Listbox) window.getFellow(String.valueOf(0));
            int eventAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(EVENT_ATTRIBUTE_LABEL));
            Listbox oldBox = dropDownLists.get(oldColPos);
            oldBox.setSelectedIndex(eventAttributeIndex);
            logMetaData.getEventAttributesPos().add(oldColPos);

            // Enable data type dropdown list for old unique attribute column
            Listbox oldDataTypeListBox = (Listbox) window.getFellow(DATA_TYPE_ID_PREFIX + oldColPos);
            oldDataTypeListBox.setDisabled(false);
        }

        // Set data type accordingly after reset of unique attribute
        if (dataTypeIndex != -1) {
            resetDataTypeSelect(newColPos);
            setDataTypeMetaData(newDataTypeListBox.getItemAtIndex(dataTypeIndex).getValue(), newColPos);
            newDataTypeListBox.setSelectedIndex(dataTypeIndex);
        }
        newDataTypeListBox.setDisabled(true);
    }

    private void resetDatatypeDropDownList(int colPos) {
        Listbox oldDataTypeListBox = (Listbox) window.getFellow(DATA_TYPE_ID_PREFIX + colPos);
        oldDataTypeListBox.setDisabled(false);
    }

    private void resetSelect(int pos) {
        // reset value for old select
        if (logMetaData.getCaseIdPos() == pos) {
            logMetaData.setCaseIdPos(-1);
        } else if (logMetaData.getActivityPos() == pos) {
            logMetaData.setActivityPos(-1);
        } else if (logMetaData.getEndTimestampPos() == pos) {
            logMetaData.setEndTimestampPos(-1);
        } else if (logMetaData.getStartTimestampPos() == pos) {
            logMetaData.setStartTimestampPos(-1);
        } else if (logMetaData.getResourcePos() == pos) {
            logMetaData.setResourcePos(-1);
        } else if (logMetaData.getRolePos() == pos) {
            logMetaData.setRolePos(-1);
        } else if (logMetaData.getOtherTimestamps().containsKey(pos)) {
            logMetaData.getOtherTimestamps().remove(pos);
        } else if (logMetaData.getIgnoredPos().contains(pos)) {
            logMetaData.getIgnoredPos().remove(Integer.valueOf(pos));
        } else if (logMetaData.getCaseAttributesPos().contains(pos)) {
            logMetaData.getCaseAttributesPos().remove(Integer.valueOf(pos));
        } else if (logMetaData.getEventAttributesPos().contains(pos)
            && !logMetaData.getPerspectivePos().contains(pos)) {
            logMetaData.getEventAttributesPos().remove(Integer.valueOf(pos));
        } else if (logMetaData.getPerspectivePos().contains(pos)) {
            logMetaData.getEventAttributesPos().remove(Integer.valueOf(pos));
            logMetaData.getPerspectivePos().remove(Integer.valueOf(pos));
        }
    }

    private void resetDataTypeSelect(int pos) {
        // reset value for old select
        if (logMetaData.getIntegerAttributesPos().contains(pos)) {
            logMetaData.getIntegerAttributesPos().remove(Integer.valueOf(pos));
        } else if (logMetaData.getDoubleAttributesPos().contains(pos)) {
            logMetaData.getDoubleAttributesPos().remove(Integer.valueOf(pos));
        } else if (logMetaData.getStringAttributesPos().contains(pos)) {
            logMetaData.getStringAttributesPos().remove(Integer.valueOf(pos));
        } else if (logMetaData.getTimestampAttributesPos().contains(pos)) {
            logMetaData.getTimestampAttributesPos().remove(Integer.valueOf(pos));
        }
    }

    private void timestampSelected(int colPos, String selected) {
        showFormatBtn(formatBtns[colPos]);
        String possibleFormat = getPopUpFormatText(colPos);
        if (possibleFormat != null && !possibleFormat.isEmpty()
            && metaDataUtilities.isTimestamp(colPos, possibleFormat, sampleLog)) {
            parsedManual(colPos, selected, possibleFormat);
        } else if (metaDataUtilities.isTimestamp(colPos, sampleLog)) {
            parsedAuto(colPos, selected);
        } else {
            failedToParse(colPos);
        }
    }

    private void parsedManual(int colPos, String selected, String format) {
        updateTimestampPos(colPos, selected, format);
        setPopUpLabel(colPos, Parsed.MANUAL, null);
    }

    private void parsedAuto(int colPos, String selected) {
        updateTimestampPos(colPos, selected, null);
        setPopUpLabel(colPos, Parsed.AUTO, null);
        setPopUpFormatText(colPos, "");
    }

    private void failedToParse(int colPos) {
        logMetaData.getEventAttributesPos().add(colPos);
        setPopUpLabel(colPos, Parsed.FAILED, null);
        openPopUpBox(colPos);
    }

    private void updateTimestampPos(int pos, String timestampLabel, String format) {
        switch (timestampLabel) {
            case END_TIMESTAMP_LABEL:
                logMetaData.setEndTimestampPos(pos);
                logMetaData.setEndTimestampFormat(format);
                break;
            case START_TIMESTAMP_LABEL:
                logMetaData.setStartTimestampPos(pos);
                logMetaData.setStartTimestampFormat(format);
                break;
            case OTHER_TIMESTAMP_LABEL:
                logMetaData.getOtherTimestamps().put(pos, format);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + timestampLabel);
        }
    }

    private void openPopUpBox(int pos) {
        Window myPopUp = (Window) popUpBox.getFellow(POP_UP_FORMAT_WINDOW_ID + pos);
        myPopUp.setStyle(myPopUp.getStyle().replace("hidden", "visible"));
        Clients.evalJavaScript("adjustPos(" + pos + ")");
    }

    private void applyMask(int pos) {
        Checkbox checkbox = (Checkbox) window.getFellow(MASK_CHECKBOX_ID + pos);
        List<Integer> maskPos = logMetaData.getMaskPos();
        if (checkbox.isChecked()) {
            if (!maskPos.contains(pos)) {
                maskPos.add(pos);
            }
        } else {
            if (maskPos.contains(pos)) {
                maskPos.remove((Integer) pos);
            }
        }
    }

    private void closePopUpBox(int pos) {
        Window myPopUp = (Window) popUpBox.getFellow(POP_UP_FORMAT_WINDOW_ID + pos);
        myPopUp.setStyle(myPopUp.getStyle().replace("visible", "hidden"));
    }

    private void setPopUpLabel(int pos, Enum type, Label check_lbl) {
        if (check_lbl == null) {
            Window myPopUp = (Window) popUpBox.getFellow(POP_UP_FORMAT_WINDOW_ID + pos);
            check_lbl = (Label) myPopUp.getFellow(POP_UP_LABEL_ID + pos);
        }

        if (type == Parsed.AUTO) {
            check_lbl.setZclass(GREEN_LABEL_CSS);
            check_lbl.setValue(getLabel("timestampParseOverride"));
            check_lbl.setMultiline(true);
            showAutoParsedGreenIcon(parsedIcons[pos]);
        } else if (type == Parsed.MANUAL) {
            check_lbl.setZclass(GREEN_LABEL_CSS);
            check_lbl.setValue(getLabel("timestampParseSuccess"));
            showManualParsedGreenIcon(parsedIcons[pos]);
        } else if (type == Parsed.FAILED) {
            check_lbl.setZclass(RED_LABEL_CSS);
            check_lbl.setValue(getLabel("timestampParseFailed"));
            showParsedRedIcon(parsedIcons[pos]);
        }
    }

    private void showFormatBtn(Button myButton) {
        myButton.setSclass("ap-csv-importer-format-icon");
        myButton.setTooltiptext(getLabel("timestampSpecify"));
    }

    private void hideFormatBtn(Button myButton) {
        myButton.setSclass("ap-csv-importer-format-icon ap-hidden");
    }

    private void hideCheckbox(Checkbox checkbox) {
        checkbox.setSclass("ap-csv-importer-format-icon ap-hidden");
    }

    private void showCheckbox(Checkbox checkbox) {
        checkbox.setSclass("ap-iconized-checkbox");
    }

    protected void enableAnonymizeToggle(boolean isEnable) {
        for (Checkbox maskBtn : maskBtns) {
            maskBtn.setVisible(isEnable);
        }
    }

    private void showAutoParsedGreenIcon(Span parsedIcon) {
        parsedIcon.setSclass("ap-csv-importer-parsed-icon z-icon-check-circle");
        parsedIcon.setTooltip(AUTO_PARSED);
    }

    private void showManualParsedGreenIcon(Span parsedIcon) {
        parsedIcon.setSclass("ap-csv-importer-parsed-icon z-icon-check-circle");
        parsedIcon.setTooltip(MANUAL_PARSED);
    }

    private void showParsedRedIcon(Span parsedIcon) {
        parsedIcon.setSclass("ap-csv-importer-failedParse-icon z-icon-times-circle");
        parsedIcon.setTooltip(ERROR_PARSING);
    }

    private void hideParsedIcon(Span parsedIcon) {
        parsedIcon.setSclass("ap-hidden");
    }

    private String getPopUpFormatText(int pos) {
        Window myPopUp = (Window) popUpBox.getFellow(POP_UP_FORMAT_WINDOW_ID + pos);
        Textbox txt = (Textbox) myPopUp.getFellow(POP_UP_TEXT_BOX_ID + pos);
        return txt.getValue();
    }

    private void setPopUpFormatText(int pos, String text) {
        Window myPopUp = (Window) popUpBox.getFellow(POP_UP_FORMAT_WINDOW_ID + pos);
        Textbox txt = (Textbox) myPopUp.getFellow(POP_UP_TEXT_BOX_ID + pos);
        txt.setValue(text);
    }

    // Internal methods supporting event handlers (@Listen)
    private void close() {
        window.detach();
        window.invalidate();
    }

    private StringBuilder validateUniqueAttributes() {
        StringBuilder importMessage = new StringBuilder();
        String unselectedAttributeFormat = getLabel("unselected_attribute_format");

        if (logMetaData.getCaseIdPos() == -1) {
            importMessage.append(String.format(unselectedAttributeFormat, getLabel("case_id")));
        }
        if (logMetaData.getActivityPos() == -1) {
            if (importMessage.length() == 0) {
                importMessage.append(String.format(unselectedAttributeFormat, getLabel("activity")));
            } else {
                importMessage.append(System.lineSeparator()).append(System.lineSeparator())
                    .append(String.format(unselectedAttributeFormat, getLabel("activity")));
            }
        }
        if (logMetaData.getEndTimestampPos() == -1) {
            if (importMessage.length() == 0) {
                importMessage.append(String.format(unselectedAttributeFormat, getLabel("end_timestamp")));
            } else {
                importMessage.append(System.lineSeparator()).append(System.lineSeparator())
                    .append(String.format(unselectedAttributeFormat, getLabel("end_timestamp")));
            }
        }

        return importMessage;
    }

    private void handleInvalidData(LogModel logModel, boolean isPublic, String name) throws IOException {

        Window errorPopUp =
            (Window) portalContext.getUI().createComponent(LogImporterController.class.getClassLoader(),
                "zul/invalidData.zul", null, null);
        errorPopUp.doModal();

        List<LogErrorReport> errorReport = logModel.getLogErrorReport();

        // Since the log is imported as a stream, errorCount can be predicted at this stage
        Label errorCount = (Label) errorPopUp.getFellow(ERROR_COUNT_LBL_ID);
        Label errorMessage = (Label) errorPopUp.getFellow(ERROR_MESSAGE_LBL_ID);
        errorCount.setValue(String.valueOf(errorReport.size()));
        if (errorReport.size() == 1) {
            errorMessage.setValue(" invalid cell detected.");
        } else {
            errorMessage.setValue(" invalid cells detected.");
        }

        Label columnList = (Label) errorPopUp.getFellow(INVALID_COLUMNS_LIST_LBL_ID);

        Set<String> invColList = new HashSet<String>();
        Set<String> igColList = new HashSet<String>();
        Set<Integer> invTimestampPos = new HashSet<>();

        for (LogErrorReport error : errorReport) {
            if (error.getHeader() != null && !error.getHeader().isEmpty()) {
                invColList.add(error.getHeader());
                if (logMetaData.getOtherTimestamps().containsKey(error.getColumnIndex())) {
                    igColList.add(error.getHeader());
                    invTimestampPos.add(error.getColumnIndex());
                }
            }
        }
        if (!invColList.isEmpty()) {
            columnList
                .setValue(getLabel("the_following_columns_include_errors") + columnList(invColList));
        }

        if (!igColList.isEmpty()) {
            Label ignoredList = (Label) errorPopUp.getFellow(IGNORED_COLUMNS_LIST_LBL_ID);
            Label ignoreLbl = (Label) errorPopUp.getFellow(IGNORE_COL_LBL_ID);
            ignoreLbl.setVisible(true);
            ignoredList.setValue(columnList(igColList));

            Button skipColumns = (Button) errorPopUp.getFellow(SKIP_COLUMNS_BTN_ID);
            skipColumns.setVisible(true);
            skipColumns.addEventListener(Events.ON_CLICK, event -> {
                errorPopUp.invalidate();
                errorPopUp.detach();

                for (int pos : invTimestampPos) {
                    logMetaData.getOtherTimestamps().remove(pos);
                    logMetaData.getIgnoredPos().add(pos);
                }

                LogModel logModelSkippedCol;
                if (useParquet) {
                    logModelSkippedCol = parquetImporter.importParquetFile(getInputSream(media), logMetaData,
                        getFileEncoding(), parquetFile, false);
                } else {
                    logModelSkippedCol = logImporter.importLog(getInputSream(media), logMetaData,
                        getFileEncoding(), true, portalContext.getCurrentUser().getUsername(),
                        portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId(),
                        name);
                }

                if (logModelSkippedCol != null) {
                    if (logModelSkippedCol.getLogErrorReport().isEmpty()) {
                        saveXLog(logModelSkippedCol, isPublic);
                    } else {
                        handleInvalidData(logModelSkippedCol, isPublic, name);
                    }
                }
            });
        }

        Button downloadBtn = (Button) errorPopUp.getFellow(DOWNLOAD_REPORT_BTN_ID);
        downloadBtn.addEventListener(Events.ON_CLICK, event -> downloadErrorLog(errorReport));

        Button skipRows = (Button) errorPopUp.getFellow(SKIP_ROWS_BTN_ID);
        Label skipRowsLbl = (Label) errorPopUp.getFellow(CAN_SKIP_INVALID_ROWS);
        Label notSkipRowsLbl = (Label) errorPopUp.getFellow(CANT_SKIP_INVALID_ROWS);
        notSkipRowsLbl.setVisible(false);

        if (logModel.getXLog() == null || logModel.getXLog().isEmpty()) {
            skipRows.setVisible(false);
            skipRowsLbl.setVisible(false);
            notSkipRowsLbl.setVisible(true);
        }

        skipRows.addEventListener(Events.ON_CLICK, event -> {
            errorPopUp.invalidate();
            errorPopUp.detach();

            LogModel logModelSkippedRow;

            if (useParquet) {
                logModelSkippedRow = parquetImporter.importParquetFile(getInputSream(media), logMetaData,
                    getFileEncoding(), parquetFile, true);

            } else {
                logModelSkippedRow = logImporter.importLog(getInputSream(media), logMetaData,
                    getFileEncoding(), true, portalContext.getCurrentUser().getUsername(),
                    portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId(),
                    name);
            }

            if (logModelSkippedRow != null) {
                saveXLog(logModelSkippedRow, isPublic);
            }
        });

        Button cancelButton = (Button) errorPopUp.getFellow(HANDLE_CANCEL_BTN_ID);
        cancelButton.addEventListener(Events.ON_CLICK, event -> {
            errorPopUp.invalidate();
            errorPopUp.detach();
        });
    }

    private String columnList(Set<String> list) {
        StringBuilder colList = new StringBuilder();
        colList.append(System.getProperty("line.separator"));
        for (String col : list) {
            colList.append("- ").append(col).append(System.getProperty("line.separator"));
        }

        return colList.toString();
    }

    private void downloadErrorLog(List<LogErrorReport> errorReport) throws Exception {

        File tempFile = File.createTempFile("ErrorReport", ".csv");
        try (FileWriter writer = new FileWriter(tempFile);

             CSVWriter csvWriter =
                 new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);) {

            String[] headerRecord = {"#", "Row Index", "Column", "Error message"};
            csvWriter.writeNext(headerRecord);

            int counter = 1;
            for (LogErrorReport error : errorReport) {
                csvWriter.writeNext(new String[] {String.valueOf(counter++),
                    String.valueOf(error.getRowIndex()), error.getHeader(), error.getError()});
            }

            InputStream csvLogStream = new FileInputStream(tempFile);
            Filedownload.save(csvLogStream, "text/csv; charset-UTF-8", "ErrorReport.csv");
        } catch (Exception e) {
            LOGGER.error("Failed to download error report", e);
            Messagebox.show(getLabel("failed_to_download_error_log") + e.getMessage(), "Error",
                Messagebox.OK, Messagebox.ERROR);
        } finally {
            FileUtils.deleteFile(tempFile);

        }
    }

    private void saveXLog(LogModel logModel, boolean isPublic) {

        try {
            storeMetadataAsJSON(logMetaData, logModel.getImportLog(), portalContext.getCurrentUser().getUsername());
            String successMessage;
            if (logModel.isRowLimitExceeded()) {
                successMessage = MessageFormat.format(getLabel("limit_reached"), logModel.getRowsCount());
            } else {
                successMessage =
                    MessageFormat.format(getLabel("successful_upload"), logModel.getRowsCount());
            }
            Messagebox.show(successMessage, new Messagebox.Button[] {Messagebox.Button.OK},
                isModal ? event -> close() : null);
            portalContext.refreshContent();

        } catch (Exception e) {
            LOGGER.error("Failed to save log", e);
            Messagebox.show(getLabel("failed_to_write_log") + e.getMessage(), "Error", Messagebox.OK,
                Messagebox.ERROR);
        }
    }

    private InputStream getInputSream(Media media) {

        return media.isBinary() ? media.getStreamData() : new ByteArrayInputStream(media.getByteData());
    }

    private ListModelList<String> getTimeZoneList() {

        ListModelList<String> listModelList = new ListModelList<>();
        String[] ids = TimeZone.getAvailableIDs();
        for (String id : ids) {
            TimeZone zone = TimeZone.getTimeZone(id);
            int offset = zone.getRawOffset() / 1000;
            int hour = offset / 3600;
            int minutes = (offset % 3600) / 60;
            listModelList.add(String.format("(GMT%+d:%02d) %s", hour, minutes, id));
        }
        return listModelList;
    }

    /**
     * A log model cannot currently be generated because some headers have not been defined.
     */
    public static class MissingHeaderFieldsException extends Exception {

        /**
         * @param message expected to come from {@link #validateUniqueAttributes}
         */
        public MissingHeaderFieldsException(String message) {
            super(message);
        }
    }
}
