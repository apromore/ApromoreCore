/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

package org.apromore.test.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.apromore.AbstractTest;
import org.apromore.calendar.service.CustomCalendarService;
import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.CustomCalendarRepository;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupLogRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.StorageRepository;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Usermetadata;
import org.apromore.dao.model.Workspace;
import org.apromore.exception.EventLogException;
import org.apromore.exception.UserMetadataException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.AuthorizationService;
import org.apromore.service.EventLogFileService;
import org.apromore.service.UserMetadataService;
import org.apromore.service.UserService;
import org.apromore.service.impl.EventLogServiceImpl;
import org.apromore.service.impl.TemporaryCacheService;
import org.apromore.storage.StorageClient;
import org.apromore.storage.factory.StorageManagementFactory;
import org.apromore.util.UserMetadataTypeEnum;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.util.XRuntimeUtils;
import org.deckfour.xes.util.XTimer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Stream;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventLogServiceImplTest extends AbstractTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventLogServiceImplTest.class);
  // inject EntityManager for simple test
  private static EntityManagerFactory emf;
  @Rule
  public ExpectedException exception = ExpectedException.none();
  private LogRepository logRepository;
  private GroupRepository groupRepository;
  private GroupLogRepository groupLogRepository;
  private FolderRepository folderRepo;
  private UserService userSrv;
  private EventLogServiceImpl eventLogService;
  private UserMetadataService userMetadataService;
  private TemporaryCacheService temporaryCacheService;
  private StorageManagementFactory<StorageClient> storageFactory;
  private EventLogFileService logFileService;
  private StorageRepository storageRepository;
  private CustomCalendarRepository calendarRepository;
  private CustomCalendarService calendarService;
  private AuthorizationService authorizationService;

  private Group group1;
  private Group group2;
  private Group group3;
  private Group group4;
  private Role role;
  private User user;
  private Workspace wp;
  private NativeType nativeType;

  private static void walkLog(XLog log) {
    walkAttributes(log);
    for (XTrace trace : log) {
      walkTrace(trace);
    }
  }

  private static void walkTrace(XTrace trace) {
    walkAttributes(trace);
    for (XEvent event : trace) {
      walkAttributes(event);
    }
  }

  private static void walkAttributes(XAttributable attributable) {
    XAttributeMap attributeMap = attributable.getAttributes();
    for (XAttribute attribute : attributeMap.values()) {
      String key = attribute.getKey();
      String value = attribute.toString();
      key.trim();
      value.trim();
      walkAttributes(attribute);
    }
  }

  public EntityManagerFactory getEntityManagerFactory() {
    if (emf == null) {
      emf = Persistence.createEntityManagerFactory("TESTApromore");
    }
    return emf;
  }

  @Before
  public final void setUp() {
    logRepository = createMock(LogRepository.class);
    groupRepository = createMock(GroupRepository.class);
    groupLogRepository = createMock(GroupLogRepository.class);
    folderRepo = createMock(FolderRepository.class);
    userSrv = createMock(UserService.class);
    userMetadataService = createMock(UserMetadataService.class);
    temporaryCacheService = createMock(TemporaryCacheService.class);
    storageFactory = createMock(StorageManagementFactory.class);
    logFileService = createMock(EventLogFileService.class);
    storageRepository = createMock(StorageRepository.class);
    calendarRepository = createMock(CustomCalendarRepository.class);
    calendarService = createMock(CustomCalendarService.class);
    authorizationService = createMock(AuthorizationService.class);
    ConfigBean config = new ConfigBean();

    eventLogService = new EventLogServiceImpl(logRepository, groupRepository, groupLogRepository,
        folderRepo, userSrv, config, userMetadataService, temporaryCacheService, storageFactory,
        logFileService, storageRepository, calendarRepository, calendarService,
        authorizationService);

    // Set up test data
    group1 = createGroup(1, Group.Type.GROUP);
    group2 = createGroup(2, Group.Type.GROUP);
    group3 = createGroup(3, Group.Type.USER);
    group4 = createGroup(4, Group.Type.USER);

    role = createRole(createSet(createPermission()));
    user = createUser("userName1", group1, createSet(group1, group3), createSet(role));

    wp = createWorkspace(user);

    nativeType = createNativeType();
  }

  @Test
  public void testOpenXesVersion() {
    XFactoryNaiveImpl xFactoryNaive = new XFactoryNaiveImpl();
    assertEquals("2.27", XRuntimeUtils.OPENXES_VERSION);
    assertTrue(xFactoryNaive.isUseInterner());
  }

  @Test
  public void validateXLogImport() {

    try (Stream<Path> paths =
        Files.walk(Paths.get(ClassLoader.getSystemResource("XES_logs/").getPath()))) {
      paths.filter(Files::isRegularFile).forEach(this::getXlog);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void getXlog(Path path) {
    XTimer timer = new XTimer();
    List<XLog> parsedLog;
    XFactory factory = XFactoryRegistry.instance().currentDefault();
    XesXmlParser parser = new XesXmlParser(factory);
    try {
      parsedLog = parser.parse(new FileInputStream(path.toFile()));
      walkLog(parsedLog.iterator().next());
    } catch (Exception e) {
      e.printStackTrace();
    }
    timer.stop();

    System.out.println("Imported and walked log: " + path.getFileName());
    System.out.println("Duration: " + timer.getDurationString());
  }

  @Test
  public void eventLogValidationTest() {
    InputStream is = getClass().getClassLoader().getResourceAsStream("XES_logs/A1_overlap_no_waiting_time.xes");
    XLog xLog = fetchXlog(is);

    assert xLog != null;
    EventLogServiceImpl.validateLog(xLog);
    assertEquals(4, xLog.size());
    int totalEventSize = 0;
    Set<String> uniqueActs = new HashSet<>();
    for (XTrace trace : xLog) {
      totalEventSize += trace.size();
      for (XEvent event : trace) {
        uniqueActs.add(event.getAttributes().get("concept:name").toString());
      }
    }
    assertEquals(17, totalEventSize);
    assertEquals(5, uniqueActs.size());
  }

  private XLog fetchXlog(InputStream is) {
    List<XLog> parsedLog;
    XFactory factory = XFactoryRegistry.instance().currentDefault();
    XesXmlParser parser = new XesXmlParser(factory);
    try {
      parsedLog = parser.parse(is);
      return parsedLog.iterator().next();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  @Test
  public void intersectionTest() {
    Set set1 = new HashSet(Arrays.asList(1, 3, 5));
    Set set2 = new HashSet(Arrays.asList(1, 6, 7, 9, 3));
    Set set3 = new HashSet(Arrays.asList(1, 3, 10, 11));

    List<Set<Integer>> lists = new ArrayList<>();
    lists.add(set1);
    lists.add(set2);
    lists.add(set3);

    List<Integer> commons = new ArrayList<Integer>();
    commons.addAll(lists.get(1));
    for (ListIterator<Set<Integer>> iterator = lists.listIterator(1); iterator.hasNext();) {
      commons.retainAll(iterator.next());
    }

    System.out.println(commons);

    System.out.println(intersection(lists));
  }


  public <T> Set<T> intersection(List<T>... list) {
    Set<T> result = Sets.newHashSet(list[0]);
    for (List<T> numbers : list) {
      result = Sets.intersection(result, Sets.newHashSet(numbers));
    }
    return result;
  }

  private XLog readXESFile(String fullFilePath) throws Exception {
    XesXmlParser parser = new XesXmlParser();
    return parser.parse(new File(fullFilePath)).get(0);
  }

  @Test
  public void testGetPerspectiveTagByLog() throws UserMetadataException {

    // Set up test data
    Integer logId = 1;
    Log log = createLogWithId(logId, user, createFolder("testFolder", null, wp));
    Set<Log> logs = new HashSet<>();
    logs.add(log);

    Usermetadata perspective = createUserMetadata(1, "[\"concept:name\",\"org:resource\",\"lifecycle:transition\"]", logs);
    Set<Usermetadata> usermetadataSet = new HashSet<>();
    usermetadataSet.add(perspective);

    // Mock recording
    expect(userMetadataService.getUserMetadataByLog(logId,
            UserMetadataTypeEnum.PERSPECTIVE_TAG)).andReturn(usermetadataSet);
    replayAll();

    // Mock call
    List<String> perspectives = eventLogService.getPerspectiveTagByLog(logId);

    List<String> result = new ArrayList<>();
    result.add("concept:name");
    result.add("org:resource");
    result.add("lifecycle:transition");

    assertEquals(perspectives, result);
  }

  @Test
  public void testGetPerspectiveTagByLog_WithoutMetadata() throws UserMetadataException {

    // Set up test data
    Integer logId = 1;
    Log log = createLogWithId(logId, user, createFolder("testFolder", null, wp));
    Set<Log> logs = new HashSet<>();
    logs.add(log);

    Usermetadata perspective = createUserMetadata(1, "[\"concept:name\",\"org:resource\",\"lifecycle:transition\"]", logs);
    Set<Usermetadata> usermetadataSet = new HashSet<>();

    // Mock recording
    expect(userMetadataService.getUserMetadataByLog(logId,
            UserMetadataTypeEnum.PERSPECTIVE_TAG)).andReturn(usermetadataSet);
    replayAll();

    // Mock call
    List<String> perspectives = eventLogService.getPerspectiveTagByLog(logId);

    List<String> result = new ArrayList<>();

    assertEquals(perspectives, result);
  }

  @Test(expected = UserMetadataException.class)
  public void testGetPerspectiveTagByLog_InvalidJson() throws UserMetadataException {

    // Set up test data
    Integer logId = 1;
    Log log = createLogWithId(logId, user, createFolder("testFolder", null, wp));
    Set<Log> logs = new HashSet<>();
    logs.add(log);
    String invalidJSONString = "[concept:name\",\"org:resource\",\"lifecycle:transition\"]";
    Usermetadata perspective = createUserMetadata(1, invalidJSONString, logs);
    Set<Usermetadata> usermetadataSet = new HashSet<>();
    usermetadataSet.add(perspective);

    // Mock recording
    expect(userMetadataService.getUserMetadataByLog(logId,
            UserMetadataTypeEnum.PERSPECTIVE_TAG)).andReturn(usermetadataSet);
    replayAll();

    // Mock call
    List<String> perspectives = eventLogService.getPerspectiveTagByLog(logId);

    // Then throw UserMetadataException
  }

  @Test
  public void testsavePerspectiveByLog() throws UserMetadataException, UserNotFoundException {

    // Set up test data
    List<String> perspectives = new ArrayList<>();
    perspectives.add("concept:name");
    perspectives.add("org:resource");

    ObjectMapper objectMapper = new ObjectMapper();
    String perspectivesJsonStr = null;
    try {
      perspectivesJsonStr = objectMapper.writeValueAsString(perspectives);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    Integer logId = 1;
    Log log = createLogWithId(logId, user, createFolder("testFolder", null, wp));
    Set<Log> logs = new HashSet<>();
    logs.add(log);

    Usermetadata usermetadata = createUserMetadata(1, "[\"concept:name\",\"org:resource\",\"lifecycle:transition\"]", logs);

    // Mock recording
    expect(userMetadataService.saveUserMetadata("Default Perspective Tag", perspectivesJsonStr,
            UserMetadataTypeEnum.PERSPECTIVE_TAG, "admin", logId)).andReturn(usermetadata);
    replayAll();

    // Mock call
    Usermetadata result = eventLogService.savePerspectiveByLog(perspectives, logId, "admin");

    assertEquals(usermetadata, result);
  }

  @Test(expected = UserNotFoundException.class)
  public void testSavePerspectiveByLog_InvalidUsername() throws UserMetadataException, UserNotFoundException {

    // Set up test data
    List<String> perspectives = new ArrayList<>();
    perspectives.add("concept:name");
    perspectives.add("org:resource");

    ObjectMapper objectMapper = new ObjectMapper();
    String perspectivesJsonStr = null;
    try {
      perspectivesJsonStr = objectMapper.writeValueAsString(perspectives);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    Integer logId = 1;
    Log log = createLogWithId(logId, user, createFolder("testFolder", null, wp));
    Set<Log> logs = new HashSet<>();
    logs.add(log);

    Usermetadata usermetadata = createUserMetadata(1, "[\"concept:name\",\"org:resource\",\"lifecycle:transition\"]", logs);

    // Mock recording
    expect(userMetadataService.saveUserMetadata("Default Perspective Tag", perspectivesJsonStr,
            UserMetadataTypeEnum.PERSPECTIVE_TAG, "admin", logId)).andThrow(new UserNotFoundException());
    replayAll();

    // Mock call
    Usermetadata result = eventLogService.savePerspectiveByLog(perspectives, logId, "admin");
  }

  @Test
  public void testGetDefaultPerspectiveFromLog() throws EventLogException {

    // Set up test data
    Integer logId = 1;
    Log log = createLogWithId(logId, user, createFolder("testFolder", null, wp));

    XLog xLog = fetchXlog(getClass().getClassLoader().getResourceAsStream("XES_logs/P1_perspective_default.xes"));

    // Mock recording
    expect(logRepository.findUniqueByID(1)).andReturn(log);
    expect(temporaryCacheService.getProcessLog(log, null)).andReturn(xLog);
    expect(userMetadataService.getUserMetadataByLog(logId, UserMetadataTypeEnum.PERSPECTIVE_TAG)).andReturn(new HashSet<>());
    replayAll();

    // Mock call
    List<String> perspectives = eventLogService.getDefaultPerspectiveFromLog(logId);

    List<String> result = new ArrayList<>();
    result.add("concept:name");
    result.add("org:resource");

    assertEquals(perspectives, result);
  }

  @Test
  public void testGetDefaultPerspectiveFromLog_NoResource() throws EventLogException {

    // Set up test data
    Integer logId = 1;
    Log log = createLogWithId(logId, user, createFolder("testFolder", null, wp));

    XLog xLog = fetchXlog(getClass().getClassLoader().getResourceAsStream("XES_logs/P1_perspective_no_Resource.xes"));

    // Mock recording
    expect(logRepository.findUniqueByID(1)).andReturn(log);
    expect(temporaryCacheService.getProcessLog(log, null)).andReturn(xLog);
    expect(userMetadataService.getUserMetadataByLog(logId, UserMetadataTypeEnum.PERSPECTIVE_TAG)).andReturn(new HashSet<>());
    replayAll();

    // Mock call
    List<String> perspectives = eventLogService.getDefaultPerspectiveFromLog(logId);

    List<String> result = new ArrayList<>();
    result.add("concept:name");

    assertEquals(perspectives, result);
  }

  @Test
  public void testGetDefaultPerspectiveFromLog_AlreadyHasPerspective() throws EventLogException {

    // Set up test data
    Integer logId = 1;
    Log log = createLogWithId(logId, user, createFolder("testFolder", null, wp));

    XLog xLog = fetchXlog(getClass().getClassLoader().getResourceAsStream("XES_logs/P1_perspective_default.xes"));

    Set<Usermetadata> usermetadataSet = new HashSet<>();
    usermetadataSet.add(new Usermetadata());

    exception.expect(EventLogException.class);
    exception.expectMessage("Found existing perspective list for event log with Id: " + logId);

    // Mock recording
    expect(logRepository.findUniqueByID(1)).andReturn(log);
    expect(temporaryCacheService.getProcessLog(log, null)).andReturn(xLog);
    expect(userMetadataService.getUserMetadataByLog(logId, UserMetadataTypeEnum.PERSPECTIVE_TAG)).andReturn(usermetadataSet);
    replayAll();

    // Mock call
    List<String> perspectives = eventLogService.getDefaultPerspectiveFromLog(logId);

  }

  @Test
  public void testGetDefaultPerspectiveFromLog_NullXLog() throws EventLogException {

    // Set up test data
    Integer logId = 1;
    Log log = createLogWithId(logId, user, createFolder("testFolder", null, wp));

    XLog xLog = null;
    exception.expect(EventLogException.class);
    exception.expectMessage("Failed to get event log with Id: " + logId);

    // Mock recording
    expect(logRepository.findUniqueByID(1)).andReturn(log);
    expect(temporaryCacheService.getProcessLog(log, null)).andReturn(xLog);
    expect(userMetadataService.getUserMetadataByLog(logId, UserMetadataTypeEnum.PERSPECTIVE_TAG)).andReturn(new HashSet<>());
    replayAll();

    // Mock call
    List<String> perspectives = eventLogService.getDefaultPerspectiveFromLog(logId);

  }

  @Test
  public void testGetDefaultPerspectiveFromLog_EmptyXLog() throws EventLogException {

    // Set up test data
    Integer logId = 1;
    Log log = createLogWithId(logId, user, createFolder("testFolder", null, wp));

    XLog xLog = new XLogImpl();
    exception.expect(EventLogException.class);
    exception.expectMessage("Found empty event log with Id: " + logId);

    // Mock recording
    expect(logRepository.findUniqueByID(1)).andReturn(log);
    expect(temporaryCacheService.getProcessLog(log, null)).andReturn(xLog);
    expect(userMetadataService.getUserMetadataByLog(logId, UserMetadataTypeEnum.PERSPECTIVE_TAG)).andReturn(new HashSet<>());
    replayAll();

    // Mock call
    List<String> perspectives = eventLogService.getDefaultPerspectiveFromLog(logId);

  }
}
