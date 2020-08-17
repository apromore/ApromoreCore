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
package org.apromore.service.impl;

import org.apromore.common.ConfigBean;
import org.apromore.dao.*;
import org.apromore.dao.model.*;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.UserMetadataService;
import org.apromore.service.UserService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.util.UserMetadataTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor =
        Exception.class)
public class UserMetadataServiceImpl implements UserMetadataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMetadataServiceImpl.class);

    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String now = dateFormat.format(new Date());


    private LogRepository logRepo;
    private GroupRepository groupRepo;
    private GroupLogRepository groupLogRepo;
    private FolderRepository folderRepo;
    private UserService userSrv;
    private UserInterfaceHelper ui;
    private File logsDir;
    private GroupUsermetadataRepository groupUsermetadataRepo;
    private UsermetadataRepository userMetadataRepo;
    private UsermetadataTypeRepository usermetadataTypeRepo;
    private UsermetadataLogRepository usermetadataLogRepo;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param groupUserMetadataRepo Log repository.
     */
    @Inject
    public UserMetadataServiceImpl(final LogRepository logRepository, final GroupRepository groupRepository,
                                   final GroupLogRepository groupLogRepository, final FolderRepository folderRepo,
                                   final UserService userSrv, final UserInterfaceHelper ui,
                                   final ConfigBean configBean,
                                   final GroupUsermetadataRepository groupUserMetadataRepo,
                                   final UsermetadataRepository userMetadataRepo,
                                   final UsermetadataTypeRepository usermetadataTypeRepo,
                                   final UsermetadataLogRepository usermetadataLogRepo) {
        this.logRepo = logRepository;
        this.groupRepo = groupRepository;
        this.groupLogRepo = groupLogRepository;
        this.folderRepo = folderRepo;
        this.userSrv = userSrv;
        this.ui = ui;
        this.logsDir = new File(configBean.getLogsDir());
        this.groupUsermetadataRepo = groupUserMetadataRepo;
        this.userMetadataRepo = userMetadataRepo;
        this.usermetadataTypeRepo = usermetadataTypeRepo;
        this.usermetadataLogRepo = usermetadataLogRepo;
    }

    @Override
    @Transactional
    public void saveUserMetadataLinkedToOneLog(String userMetadataContent, UserMetadataTypeEnum userMetadataTypeEnum,
                                               String username,
                                               Integer logId) throws UserNotFoundException {

        List logIds = new LinkedList<Integer>();
        logIds.add(logId);

        saveUserMetadata(userMetadataContent, userMetadataTypeEnum, username, logIds);
    }

    @Override
    @Transactional
    public void saveUserMetadata(String userMetadataContent, UserMetadataTypeEnum userMetadataTypeEnum, String username,
                                 List<Integer> logIds) throws UserNotFoundException {

        //TODO:
        // 1. creator singleton group get Owner permission
        // 2. Log's GroupLogs get read permission
        // 3. Use transaction
        // 4. Pass in User directly

        User user = userSrv.findUserByLogin(username);

        Usermetadata userMetadata = new Usermetadata();

        Set<GroupUsermetadata> groupUserMetadataSet = userMetadata.getGroupUserMetadata();
        Set<UsermetadataLog> usermetadataLogSet = userMetadata.getUsermetadataLog();

        // Assign OWNER permission to the user's personal group
        groupUserMetadataSet.add(new GroupUsermetadata(user.getGroup(), userMetadata, true, true, true));

        for (Integer logId : logIds) {
            // Assign READ permission to all groups that have read permission to the linked artifact
            for (GroupLog gl : groupLogRepo.findByLogId(logId)) {
                if (gl.getHasRead()) {
                    groupUserMetadataSet.add(new GroupUsermetadata(gl.getGroup(), userMetadata, true, false, false));
                }
            }

            // Add linked artifact to the UsermetadataLog linked table
            usermetadataLogSet.add(new UsermetadataLog(userMetadata, logRepo.findUniqueByID(logId)));
        }

        // Assemble Usermetadata
        userMetadata.setGroupUserMetadata(groupUserMetadataSet);
        userMetadata.setUsermetadataLog(usermetadataLogSet);
        userMetadata.setUsermetadataType(usermetadataTypeRepo.findOne(userMetadataTypeEnum.getUserMetadataTypeId()));
        userMetadata.setIsValid(true);
        userMetadata.setCreatedBy(user.getRowGuid());
        userMetadata.setCreatedTime(now);
        userMetadata.setContent(userMetadataContent);

        // Persist Usermetadata, GroupUsermetadata and UsermetadataLog
        userMetadataRepo.saveAndFlush(userMetadata);
        LOGGER.info("Create user metadata ID: {0} TYPE: {1}." + userMetadata.getId() + userMetadataTypeEnum.toString());

    }

    @Override
    @Transactional
    public void updateUserMetadata(Integer usermetadataId, String username, String content) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        Usermetadata userMetadata = userMetadataRepo.findOne(usermetadataId);
        userMetadata.setContent(content);
        userMetadata.setUpdatedBy(user.getRowGuid());
        userMetadata.setUpdatedTime(now);

        // Persist Usermetadata
        userMetadataRepo.saveAndFlush(userMetadata);
        LOGGER.info("Update user metadata ID: {0}." + userMetadata.getId());

    }

    @Override
    @Transactional
    public void deleteUserMetadata(Integer usermetadataId, String username) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        Usermetadata userMetadata = userMetadataRepo.findOne(usermetadataId);
        userMetadata.setUpdatedBy(user.getRowGuid());
        userMetadata.setUpdatedTime(now);
        userMetadata.setIsValid(false);

        // Delete all UsermetadataLog
        Set<UsermetadataLog> usermetadataLogSet = userMetadata.getUsermetadataLog();
        usermetadataLogSet.clear();
        userMetadata.setUsermetadataLog(usermetadataLogSet);

        // Delete all GroupUsermetadata
        Set<GroupUsermetadata> groupUserMetadataSet = userMetadata.getGroupUserMetadata();
        groupUserMetadataSet.clear();
        userMetadata.setGroupUserMetadata(groupUserMetadataSet);

        // Invalidate Usermetadata
        userMetadataRepo.saveAndFlush(userMetadata);
        LOGGER.info("Delete user metadata ID: {0}." + userMetadata.getId());
    }

    @Override
    public Set<Usermetadata> getUserMetadata(String username, Integer logId, UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        // Get all the user metadata that can be accessed by groups contain specified user
        Set<GroupUsermetadata> groupUsermetadataSet = new HashSet<>();
        for (Group group : user.getGroups()) {
            groupUsermetadataSet.addAll(groupUsermetadataRepo.findByGroup(group));
        }

        // Lambda is not supported by spring version before 4
//        List<Usermetadata> usermetadataList1 = groupUsermetadataSet.stream()
//                .map(GroupUsermetadata::getUsermetadata)
//                .collect(Collectors.toList());

        Set<Usermetadata> usermetadataList1 = new HashSet<>();
        for (GroupUsermetadata groupUsermetadata : groupUsermetadataSet) {
            usermetadataList1.add(groupUsermetadata.getUsermetadata());
        }

        // Get all the user metadata that linked to specified log
        Set<UsermetadataLog> usermetadataLogSet =
                new HashSet<>(usermetadataLogRepo.findByLog(logRepo.findUniqueByID(logId)));

        Set<Usermetadata> usermetadataList2 = new HashSet<>();
        for (UsermetadataLog usermetadataLog : usermetadataLogSet) {
            usermetadataList2.add(usermetadataLog.getUsermetadata());
        }

        usermetadataList1.retainAll(usermetadataList2);

        // Lambda is not supported by spring version before 4
//        List<Usermetadata> usermetadataList2 = usermetadataLogSet.stream()
//                .map(UsermetadataLog::getUsermetadata)
//                .collect(Collectors.toList());
//
//        Set<Usermetadata> result = usermetadataList1.stream()
//                .distinct()
//                .filter(usermetadataList2::contains)
//                .collect(Collectors.toSet());

        Set<Usermetadata> result = new HashSet<>();
        for (Usermetadata u : usermetadataList1
             ) {
            if (u.getUsermetadataType().getId() == userMetadataTypeEnum.getUserMetadataTypeId()) {
                result.add(u);
            }
        }
        return result;
    }


    @Override
    public boolean canUserEditMetadata(String username, Integer UsermetadataId) throws UserNotFoundException {

        for (GroupUsermetadata gl : groupUsermetadataRepo.findByLogAndUser(UsermetadataId,
                userSrv.findUserByLogin(username).getRowGuid())) {
            if (gl.getHasWrite() || gl.getHasOwnership()) {
                return true;
            }
        }
        return false;
    }
}
