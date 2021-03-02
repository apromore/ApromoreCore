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

import static org.powermock.api.easymock.PowerMock.createMock;

import java.io.FileInputStream;
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

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apromore.common.ConfigBean;
import org.apromore.dao.CustomCalendarRepository;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupLogRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.GroupUsermetadataRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.StorageRepository;
import org.apromore.dao.UsermetadataLogRepository;
import org.apromore.dao.UsermetadataRepository;
import org.apromore.dao.UsermetadataTypeRepository;
import org.apromore.service.EventLogFileService;
import org.apromore.service.UserMetadataService;
import org.apromore.service.UserService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.impl.EventLogServiceImpl;
import org.apromore.service.impl.TemporaryCacheService;
import org.apromore.storage.StorageClient;
import org.apromore.storage.factory.StorageManagementFactory;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.util.XRuntimeUtils;
import org.deckfour.xes.util.XTimer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class EventLogServiceImplTest {

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
    private UsermetadataRepository userMetadataRepo;
    private TemporaryCacheService temporaryCacheService;
    private StorageManagementFactory<StorageClient> storageFactory;
    private EventLogFileService logFileService;
    private StorageRepository storageRepository;
    private CustomCalendarRepository calendarRepository;


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
        userMetadataRepo = createMock(UsermetadataRepository.class);
        temporaryCacheService=createMock(TemporaryCacheService.class);
        storageFactory=createMock(StorageManagementFactory.class);
        logFileService=createMock(EventLogFileService.class);
        storageRepository=createMock(StorageRepository.class);
	calendarRepository = createMock(CustomCalendarRepository.class);
        ConfigBean config = new ConfigBean();

        eventLogService = new EventLogServiceImpl(logRepository, groupRepository, groupLogRepository, folderRepo,
                userSrv, config,
                userMetadataRepo,temporaryCacheService,storageFactory,logFileService,storageRepository, calendarRepository);
    }

    @Test
    @Ignore
    public void getOpenXesVersion() {
        System.out.println("OPENXES_VERSION: " + XRuntimeUtils.OPENXES_VERSION);
//        XFactoryNaiveImpl xFactoryNaive = new XFactoryNaiveImpl();
//        System.out.println(xFactoryNaive.isUseInterner());
    }

    @Test
    public void validateXLogImport() {

        try (Stream<Path> paths = Files.walk(Paths.get(ClassLoader.getSystemResource("XES_logs/").getPath()))) {
            paths.filter(Files::isRegularFile)
                    .forEach(this::getXlog);
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
        for (ListIterator<Set<Integer>> iterator = lists.listIterator(1); iterator.hasNext(); ) {
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

}
