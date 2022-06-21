/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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
package org.apromore.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apromore.apmlog.APMLog;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.service.CalendarService;
import org.apromore.common.Constants;
import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.CustomCalendarRepository;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupLogRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.StorageRepository;
import org.apromore.dao.model.CustomCalendar;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupFolder;
import org.apromore.dao.model.GroupLog;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Storage;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Usermetadata;
import org.apromore.exception.EventLogException;
import org.apromore.exception.NotAuthorizedException;
import org.apromore.exception.UserMetadataException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.portal.model.ExportLogResultType;
import org.apromore.portal.model.PluginMessages;
import org.apromore.portal.model.SummariesType;
import org.apromore.service.AuthorizationService;
import org.apromore.service.EventLogFileService;
import org.apromore.service.EventLogService;
import org.apromore.service.UserMetadataService;
import org.apromore.service.UserService;
import org.apromore.storage.StorageClient;
import org.apromore.storage.StorageType;
import org.apromore.storage.factory.StorageManagementFactory;
import org.apromore.util.AccessType;
import org.apromore.util.StringUtil;
import org.apromore.util.UserMetadataTypeEnum;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XMxmlGZIPParser;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of the ProcessService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */

@Service("eventLogService")
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class EventLogServiceImpl implements EventLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventLogServiceImpl.class);
    private LogRepository logRepo;
    private GroupRepository groupRepo;
    private GroupLogRepository groupLogRepo;
    private FolderRepository folderRepo;
    private UserService userSrv;
    private UserMetadataService userMetadataService;
    private TemporaryCacheService tempCacheService;
    private StorageManagementFactory<StorageClient> storageFactory;

    @Getter
    private ConfigBean configBean;
    private EventLogFileService logFileService;
    private StorageRepository storageRepository;
    private CustomCalendarRepository customCalendarRepository;
    private CalendarService calendarService;
    private AuthorizationService authorizationService;

    @Value("${storage.logPrefix}")
    private String logPrefix;

	/**
	 * Default Constructor allowing Spring to Autowire for testing and normal use.
	 *
	 * @param logRepository            LogRepository
	 * @param groupRepository          GroupRepository
	 * @param groupLogRepository       GroupLogRepository
	 * @param folderRepo               FolderRepository
	 * @param userSrv                  UserService
	 * @param configBean               ConfigBean
	 * @param userMetadataSrv          UserMetadataService
	 * @param temporaryCacheService    TemporaryCacheService
	 * @param storageFactory           StorageManagementFactory
	 * @param logFileService           EventLogFileService
	 * @param storageRepository        StorageRepository
	 * @param customCalendarRepository CustomCalendarRepository
	 * @param calendarService          CalendarService
	 * @param authorizationService     AuthorizationService
	 */
    @Inject
    public EventLogServiceImpl(final LogRepository logRepository, final GroupRepository groupRepository,
            final GroupLogRepository groupLogRepository, final FolderRepository folderRepo, final UserService userSrv,
            final ConfigBean configBean, final UserMetadataService userMetadataSrv,
            final TemporaryCacheService temporaryCacheService, final StorageManagementFactory storageFactory,
            final EventLogFileService logFileService, final StorageRepository storageRepository,
            final CustomCalendarRepository customCalendarRepository, final CalendarService calendarService,
            final AuthorizationService authorizationService) {
	this.logRepo = logRepository;
	this.groupRepo = groupRepository;
	this.groupLogRepo = groupLogRepository;
	this.folderRepo = folderRepo;
	this.userSrv = userSrv;
	this.userMetadataService = userMetadataSrv;
	this.tempCacheService = temporaryCacheService;
	this.storageFactory = storageFactory;
	this.configBean = configBean;
	this.logFileService = logFileService;
	this.storageRepository = storageRepository;
	this.customCalendarRepository = customCalendarRepository;
	this.calendarService = calendarService;
	this.authorizationService = authorizationService;
    }

    private static XLog importFromStream(XFactory factory, InputStream is, String extension) throws Exception {
	XParser parser;
	parser = null;
	if (extension.endsWith("mxml")) {
	    parser = new XMxmlParser(factory);
	} else if (extension.endsWith("mxml.gz")) {
	    parser = new XMxmlGZIPParser(factory);
	} else if (extension.endsWith("xes")) {
	    parser = new XesXmlParser(factory);
	} else if (extension.endsWith("xes.gz")) {
	    parser = new XesXmlGZIPParser(factory);
	}

	Collection<XLog> logs;
	try {
	    assert parser != null;
	    logs = parser.parse(is);
	} catch (Exception e) {
	    LOGGER.error("Unable parse logs from stream", e);
	    logs = null;
	}
	if (logs == null) {
	    // try any other parser
	    for (XParser p : XParserRegistry.instance().getAvailable()) {
		if (p == parser) {
		    continue;
		}
		try {
		    logs = p.parse(is);
		    if (logs.size() > 0) {
			break;
		    }
		} catch (Exception e1) {
		    // ignore and move on.
		    logs = null;
		}
	    }
	}

	// log sanity checks;
	// notify user if the log is awkward / does miss crucial information
	if (logs == null || logs.size() == 0) {
	    throw new Exception("No processes contained in log!");
	}

	XLog log = logs.iterator().next();
	if (XConceptExtension.instance().extractName(log) == null) {
	    XConceptExtension.instance().assignName(log, "Anonymous log imported from ");
	}

	if (log.isEmpty()) {
	    throw new Exception("No process instances contained in log!");
	}

	return validateLog(log);

    }

    public static XLog validateLog(XLog log) {
	List<XTrace> tobeRemovedTraces = new ArrayList<>();
	for (XTrace trace : log) {
	    List<XEvent> tobeRemovedEvents = new ArrayList<>();
	    for (XEvent event : trace) {
		if (!event.getAttributes().containsKey("lifecycle:transition")) {
		    event.getAttributes().put("lifecycle:transition",
		            new XAttributeLiteralImpl("lifecycle:transition", "complete", null));
		}
		if (isInvalidEvent(event)) {
		    tobeRemovedEvents.add(event);
		}
	    }
	    if (!tobeRemovedEvents.isEmpty()) {
		trace.removeAll(tobeRemovedEvents);
	    }
	    if (trace.isEmpty()) {
		tobeRemovedTraces.add(trace);
	    }
	}
	if (!tobeRemovedTraces.isEmpty()) {
	    log.removeAll(tobeRemovedTraces);
	}
	return log;
    }

    private static boolean isInvalidEvent(XEvent event) {
	return !event.getAttributes().containsKey("time:timestamp")
	        || !event.getAttributes().containsKey("concept:name")
	        || (!"start".equalsIgnoreCase(event.getAttributes().get("lifecycle:transition").toString())
	                && !"complete".equalsIgnoreCase(event.getAttributes().get("lifecycle:transition").toString()));
    }

    @Override
    public SummariesType readLogSummaries(Integer folderId, String searchExpression) {
	return null;
    }

    /**
     * Import serialisations into Apromore application.
     *
     * @param username       The user doing the importing.
     * @param folderId       The folder we are saving the process in.
     * @param logName        the name of the process being imported.
     * @param inputStreamLog the inputStream of Log
     * @param extension      File extension
     * @param domain         the domain of the model
     * @param created        the time created
     * @param publicModel    is this a public model?
     * @param perspective    whether generate default perspective for this log
     * @return Log
     * @throws UserNotFoundException when a particular user is not found using
     *                               specified username
     * @throws Exception             if importing fails for some other reason
     */
    @Override
    public Log importLog(String username, Integer folderId, String logName, InputStream inputStreamLog,
            String extension, String domain, String created, boolean publicModel, boolean perspective) throws Exception {
	User user = userSrv.findUserByLogin(username);

	XFactory factory = XFactoryRegistry.instance().currentDefault();
	LOGGER.info("Import XES log " + logName + " using " + factory.getClass());
	XLog xLog = importFromStream(factory, inputStreamLog, extension);
	Log log = importLog(folderId, logName, domain, created, publicModel, user, xLog);

	// Generate default perspective list when import from XES
	if (perspective) {
		savePerspectiveByLog(getDefaultPerspectiveFromLog(log.getId()), log.getId(), username);
	}

	return log;
    }

	@Override
	public Log importFilteredLog(String username, Integer folderId, String logName, InputStream inputStreamLog,
								 String extension, String domain, String created, boolean publicModel,
								 boolean perspective, Integer sourceLogId) throws Exception {
		Log filteredLog = importLog(username, folderId, logName, inputStreamLog, extension, domain, created,
				publicModel, perspective);
		deepCopyArtifacts(findLogById(sourceLogId), filteredLog,
				Arrays.asList(UserMetadataTypeEnum.CSV_IMPORTER.getUserMetadataTypeId(),
						UserMetadataTypeEnum.PERSPECTIVE_TAG.getUserMetadataTypeId(),
						UserMetadataTypeEnum.COST_TABLE.getUserMetadataTypeId()), username);
		return filteredLog;
	}

    @Override
    public Log importLog(Integer folderId, String logName, String domain, String created, boolean publicModel,
            User user, XLog xLog) {

	Storage storage = tempCacheService.storeProcessLog(folderId, logName, xLog, user.getId(), domain, created);

	Log log = new Log();
	Folder folder = folderRepo.findUniqueByID(folderId);

	if (folder != null) {
	    AccessType accessType = authorizationService.getFolderAccessTypeByUser(folderId, user);

	    // If user is not the owner of specified folder, then put log in user's home
	    // folder
	    if (accessType != AccessType.OWNER) {
		folder = null;
	    }
	}

	log.setFolder(folder);
	log.setDomain(domain);
	log.setCreateDate(created);
	log.setFilePath("PROXY_PATH");
	log.setStorage(storageRepository.saveAndFlush(storage));

	try {
	    updateLogName(log, logName);
	} catch (Exception e) {
	    throw new RuntimeException("Error while renaming log file " + logName, e);
	}

	log.setRanking("");
	log.setUser(user);

	Set<GroupLog> groupLogs = log.getGroupLogs();

	// Add the user's personal group
	groupLogs.add(new GroupLog(user.getGroup(), log, true, true, true));

	// Unless in the root folder, add access rights of its immediately enclosing
	// folder
	if (folder != null) {
	    Set<GroupFolder> groupFolders = folder.getGroupFolders();
	    for (GroupFolder gf : groupFolders) {
		// Avoid adding operating user twice
		if (!Objects.equals(gf.getGroup().getId(), user.getGroup().getId())) {
		    groupLogs.add(new GroupLog(gf.getGroup(), log, gf.getAccessRights()));
		}
	    }
	}

	// Add the public group
	if (publicModel) {
	    Group publicGroup = groupRepo.findPublicGroup();
	    if (publicGroup == null) {
		LOGGER.warn("No public group present in repository");
	    } else {
		groupLogs.add(new GroupLog(publicGroup, log, true, true, false));
	    }
	}

	// Perform the update
	logRepo.saveAndFlush(log);

	return log;
    }

    @Override
    public void updateLogMetaData(Integer logId, String logName, boolean isPublic) {
	Log log = logRepo.findUniqueByID(logId);

	try {
	    updateLogName(log, logName);
	} catch (Exception e) {
	    throw new RuntimeException("Error while renaming log file");
	}

	Set<GroupLog> groupLogs = log.getGroupLogs();
	Set<GroupLog> publicGroupLogs = filterPublicGroupLogs(groupLogs);

	if (publicGroupLogs.isEmpty() && isPublic) {
	    groupLogs.add(new GroupLog(groupRepo.findPublicGroup(), log, true, true, false));
	    log.setGroupLogs(groupLogs);

	} else if (!publicGroupLogs.isEmpty() && !isPublic) {
	    groupLogs.removeAll(publicGroupLogs);
	    log.setGroupLogs(groupLogs);
	}

	logRepo.saveAndFlush(log);
    }

    private void updateLogName(Log log, String newName) throws Exception {

	if (log.getStorage() == null) {
	    // put extensions in constants
	    try (InputStream inputStream = storageFactory
                .getStorageClient("FILE" + StorageType.STORAGE_PATH_SEPARATOR + configBean.getLogsDir())
                .getInputStream(null, log.getFilePath() + "_" + log.getName() + ".xes.gz")) {

	        Storage storage = new Storage();
	        storage.setStoragePath(configBean.getStoragePath());
	        storage.setKey(log.getFilePath() + "_" + newName + ".xes.gz");
	        storage.setPrefix(logPrefix);

	        try (OutputStream outputStream = storageFactory
                    .getStorageClient(storage.getStoragePath())
                    .getOutputStream(storage.getPrefix(), storage.getKey())) {

                    logFileService.copyFile(inputStream, outputStream);
                }

                log.setStorage(storageRepository.saveAndFlush(storage));
            }
	}

        log.setName(StringUtil.normalizeFilename(newName));
    }

    @Override
    public boolean isPublicLog(Integer logId) {
	return !filterPublicGroupLogs(logRepo.findUniqueByID(logId).getGroupLogs()).isEmpty();
    }

    private Set<GroupLog> filterPublicGroupLogs(Set<GroupLog> groupLogs) {
	Group publicGroup = groupRepo.findPublicGroup();
	if (publicGroup == null) {
	    LOGGER.warn("No public group present in repository");
	    return Collections.emptySet();
	}

	Set<GroupLog> publicGroupLogs = new HashSet<>();
	for (GroupLog groupLog : groupLogs) {
	    if (publicGroup.equals(groupLog.getGroup())) {
		publicGroupLogs.add(groupLog);
	    }
	}

	return publicGroupLogs;
    }

    @Override
    public boolean canUserWriteLog(String username, Integer logId) throws UserNotFoundException {
	User user = userSrv.findUserByLogin(username);
	for (GroupLog gl : groupLogRepo.findByLogAndUser(logId, user.getRowGuid())) {
	    if (gl.getAccessRights().isWriteOnly()) {
		return true;
	    }
	}
	return false;
    }

	@Override
	public boolean canUserReadLog(String username, Integer logId) throws UserNotFoundException {
		User user = userSrv.findUserByLogin(username);
		return groupLogRepo.findByLogAndUser(logId, user.getRowGuid()).stream()
			.anyMatch(gl -> gl.getAccessRights().isReadOnly() || gl.getAccessRights().isOwnerShip());
	}

    @Override
    public ExportLogResultType exportLog(Integer logId) throws Exception {
	Log log = logRepo.findUniqueByID(logId);
	XLog xlog = tempCacheService.getProcessLog(log, null);
	final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	exportToStream(outputStream, xlog);
	ExportLogResultType exportLogResultType = new ExportLogResultType();

	PluginMessages pluginMessages = new PluginMessages();
	exportLogResultType.setMessage(pluginMessages);
	exportLogResultType.setNative(new DataHandler(
	        new ByteArrayDataSource(new ByteArrayInputStream(outputStream.toByteArray()), Constants.GZ_MIMETYPE)));
	return exportLogResultType;
    }

    @Override
    public void cloneLog(String username, Integer folderId, String logName, Integer sourceLogId, String domain,
            String created, boolean publicModel) throws Exception {
	Log log = logRepo.findUniqueByID(sourceLogId);
	XLog xlog = tempCacheService.getProcessLog(log, null);
	User user = userSrv.findUserByLogin(username);
	importLog(folderId, logName, domain, created, publicModel, user, xlog);
    }

    @Override
    public XLog getXLog(Integer logId) {
	return getXLog(logId, null);
    }

    @Override
    public XLog getXLog(Integer logId, String factoryName) {
	Log log = logRepo.findUniqueByID(logId);
	XLog xLog = tempCacheService.getProcessLog(log, factoryName);
	LOGGER.info("Read XLog Id = {}", logId);
	return xLog;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public void deleteLogs(List<Log> logs, User user) throws NotAuthorizedException, UserNotFoundException {
	for (Log log : logs) {
	    if (!canUserWriteLog(user.getUsername(), log.getId())) {
		throw new NotAuthorizedException(
		        "Log with id " + log.getId() + " may not be deleted by " + user.getUsername());
	    }
	    Log realLog = logRepo.findUniqueByID(log.getId());

	    Set<Usermetadata> usermetadataSet = realLog.getUsermetadataSet();

	    // delete associated user metadata
	    for (Usermetadata u : usermetadataSet) {
		userMetadataService.deleteUserMetadata(u.getId(), user.getUsername());
		LOGGER.info("User: {} Delete user metadata ID: {}.", user.getUsername(), u.getId());
	    }

	    logRepo.deleteById(realLog.getId());

	    if (shouldDeleteLogFile(realLog.getStorage())) {
		LOGGER.info("Deleting file: " + realLog.getName());

		if (realLog.getStorage() != null) {
		    storageRepository.deleteById(realLog.getStorage().getId());
		}

		tempCacheService.deleteProcessLog(realLog);

	    }

	    LOGGER.info("Delete XES log " + log.getId() + " from repository.");
	}
    }

    private boolean shouldDeleteLogFile(Storage storage) {
	return storage == null || logRepo.countByStorageId(storage.getId()) == 0;

    }

    @Override
    public void exportToStream(OutputStream outputStream, XLog log) throws Exception {
	XSerializer serializer = new XesXmlGZIPSerializer();
	serializer.serialize(log, outputStream);
    }

    @Override
    public APMLog getAggregatedLog(Integer logId) {
	Log log = logRepo.findUniqueByID(logId);
	return tempCacheService.getAggregatedLog(log);
    }

    @Override
    public void updateCalendarForLog(Integer logId, Long calenderId) {

    calenderId=(Long) Objects.requireNonNullElse(calenderId, 0L);
    Log log = logRepo.findUniqueByID(logId);
	CustomCalendar calendar = customCalendarRepository.findById(calenderId).orElse(null);
	log.setCalendar(calendar);
	logRepo.saveAndFlush(log);

    }

    @Override
    public Long getCalendarIdFromLog(Integer logId) {
	CustomCalendar calendar = logRepo.findUniqueByID(logId).getCalendar();
	return calendar == null ? 0 : calendar.getId();
    }

    @Override
    public CalendarModel getCalendarFromLog(Integer logId) {
	CustomCalendar calendar = logRepo.findUniqueByID(logId).getCalendar();
	return calendar != null ? calendarService.getCalendar(calendar.getId()) : calendarService.getGenericCalendar();
    }


    @Override
    public List<Log> getLogListFromCalendarId(Long calendarId) {
      return logRepo.findByCalendarId(calendarId);
    }

    @Override
    public List<Log> getLogListFromCalendarId(Long calendarId, String username) {
        List<Log> relatedLogs = logRepo.findByCalendarId(calendarId);
        relatedLogs.removeIf(l -> {
            try {
                return !AccessType.OWNER.equals(authorizationService.getLogAccessTypeByUser(l.getId(), username));
            } catch (UserNotFoundException e) {
                LOGGER.error("Could not find user with username {}", username);
            }
            return true;
        });
        return relatedLogs;
    }

    @Override
    public boolean saveFileToVolume(String filename, String prefix, ByteArrayOutputStream baos) throws Exception {

	String ved = configBean.getVolumeExportDir();
	if (ved == null) {
	    throw new Exception("Can not found VolumeExportDir");
	}
	StorageClient newStorage = storageFactory.getStorageClient(ved);

	OutputStream outputStream = newStorage.getOutputStream(prefix, filename);
	baos.writeTo(outputStream);
	outputStream.close();

	return true;
    }

	@Override
	public List<String> getPerspectiveTagByLog(Integer logId) throws UserMetadataException {

		List<String> perspectiveList = new ArrayList<>();

		Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataByLog(logId,
				UserMetadataTypeEnum.PERSPECTIVE_TAG);

		if (usermetadataSet.isEmpty()) {
			LOGGER.info("Log (ID: {}) doesn't have associated perspectives stored in DB.", logId);
			return perspectiveList;
		}

		String jsonString = usermetadataSet.iterator().next().getContent();
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			perspectiveList = objectMapper.readValue(jsonString, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			throw new UserMetadataException("Could not deserialize JSON content from given JSON content String: " + jsonString, e);
		}

		LOGGER.info("Get perspective list for log (ID: {}): {}", logId, perspectiveList);
		return perspectiveList;
	}

	@Override
	public Usermetadata savePerspectiveByLog(List<String> perspectives, Integer logId, String username) throws UserMetadataException, UserNotFoundException {

		String perspectivesJsonStr;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			perspectivesJsonStr = objectMapper.writeValueAsString(perspectives);
			return userMetadataService.saveUserMetadata("Default Perspective Tag", perspectivesJsonStr,
					UserMetadataTypeEnum.PERSPECTIVE_TAG, username, logId);
		} catch (JsonProcessingException e) {
			throw new UserMetadataException("Could not serialize given perspective list: " + perspectives.toString(), e);
		}
	}

	@Override
	public Usermetadata saveCostTablesByLog(String costTables, Integer logId, String username) throws UserNotFoundException {

		Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataByLog(logId,
				UserMetadataTypeEnum.COST_TABLE);
		if (usermetadataSet.isEmpty()) {
			return userMetadataService.saveUserMetadata("Cost tables", costTables,
					UserMetadataTypeEnum.COST_TABLE, username, logId);
		}
		return userMetadataService.updateUserMetadata(usermetadataSet.iterator().next(), username, costTables);
	}

	@Override
	public String getCostTablesByLog(Integer logId) {

		String jsonString = "";

		Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataByLog(logId,
				UserMetadataTypeEnum.COST_TABLE);

		if (usermetadataSet.isEmpty()) {
			LOGGER.info("Log (ID: {}) doesn't have associated cost table stored in DB.", logId);
			return jsonString;
		}
		LOGGER.info("Get cost table for log (ID: {}): {}", logId, jsonString);
		return usermetadataSet.iterator().next().getContent();

	}

	@Override
	public List<String> getDefaultPerspectiveFromLog(Integer logId) throws EventLogException {

		List<String> perspectives;
		boolean hasResource = true;
		XLog xLog = getXLog(logId);

		if (userMetadataService.getUserMetadataByLog(logId, UserMetadataTypeEnum.PERSPECTIVE_TAG).size() != 0) {
			throw new EventLogException("Found existing perspective list for event log with Id: " + logId);
		}

		if (xLog == null) {
			throw new EventLogException("Failed to get event log with Id: " + logId);
		}

		if (xLog.size() != 0) {
			xLog:
			for(XTrace trace : xLog) {
				for(XEvent event : trace) {
					XAttributeMap attributeMap = event.getAttributes();
					if (!attributeMap.containsKey(XOrganizationalExtension.KEY_RESOURCE)) {
						hasResource = false;
						break xLog;
					}
				}
			}
		} else {
			throw new EventLogException("Found empty event log with Id: " + logId);
		}

		perspectives =  hasResource ? Arrays.asList(XConceptExtension.KEY_NAME,
				XOrganizationalExtension.KEY_RESOURCE) :
				List.of(XConceptExtension.KEY_NAME);

		return perspectives;

	}

	@Override
	public boolean hasWritePermissionOnLog(User user, List<Integer> logIds) {
		String username = user.getUsername();
		return logIds.stream().allMatch(logId -> {
			try {
				return canUserWriteLog(username, logId);
			} catch (UserNotFoundException e) {
				return false;
			}
		});
	}

	@Override
	public List<CalendarModel> getAllCustomCalendars(){
		return calendarService.getCalendars();
	}

	@Override
	public List<CalendarModel> getAllCustomCalendars(String username){
		return calendarService.getCalendars(username);
	}

	@Override
	public void shallowCopyArtifacts(Log oldLog, Log newLog, List<Integer> artifactTypes) {

		Set<Usermetadata> usermetadataSet = oldLog.getUsermetadataSet();
		Set<Usermetadata> us = newLog.getUsermetadataSet();
		for (Usermetadata u : usermetadataSet) {
			if (artifactTypes.contains(u.getUsermetadataType().getId())) {
				us.add(u);
				LOGGER.debug("Link user metadata type:{} id:{} to new Log id:{} during copy", u.getUsermetadataType().getType(),
						u.getId(), newLog.getId());
			}
		}
		logRepo.save(newLog);
	}

	@Override
	@Transactional
	public void deepCopyArtifacts(Log oldLog, Log newLog, List<Integer> artifactTypes, String username) throws UserNotFoundException {

		Set<Usermetadata> usermetadataSet = oldLog.getUsermetadataSet();
		for (Usermetadata u : usermetadataSet) {
			if (artifactTypes.contains(u.getUsermetadataType().getId())) {
				userMetadataService.saveUserMetadata(u.getName(), u.getContent(),
						UserMetadataTypeEnum.valueOf(u.getUsermetadataType().getType()), username, newLog.getId());
				LOGGER.debug("Deep copy user metadata type:{} id:{} to new Log id:{} during copy",
						u.getUsermetadataType().getType(),
						u.getId(), newLog.getId());
			}
		}
		logRepo.save(newLog);
	}

	@Override
	public Log findLogById(Integer logId) {
		return logRepo.findUniqueByID(logId);
	}
}
