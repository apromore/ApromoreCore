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

import org.apromore.dao.*;
import org.apromore.dao.model.*;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.UserMetadataService;
import org.apromore.service.UserService;
import org.apromore.util.UserMetadataTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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
    private GroupLogRepository groupLogRepo;
    private UserService userSrv;
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
    public UserMetadataServiceImpl(final LogRepository logRepository,
                                   final GroupLogRepository groupLogRepository,
                                   final UserService userSrv,
                                   final GroupUsermetadataRepository groupUserMetadataRepo,
                                   final UsermetadataRepository userMetadataRepo,
                                   final UsermetadataTypeRepository usermetadataTypeRepo,
                                   final UsermetadataLogRepository usermetadataLogRepo) {
        this.logRepo = logRepository;
        this.groupLogRepo = groupLogRepository;
        this.userSrv = userSrv;
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

        List<Integer> logIds = new LinkedList<>();
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
                if (gl.getHasRead() && !gl.getGroup().getName().equals(username)) { // exclude owner
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
        LOGGER.info("Create user metadata ID: {} TYPE: {}.", userMetadata.getId(), userMetadataTypeEnum.toString());

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
        LOGGER.info("Update user metadata ID: {}.", userMetadata.getId());

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
        LOGGER.info("Delete user metadata ID: {}.", userMetadata.getId());
    }

    @Override
    public Set<Usermetadata> getUserMetadata(String username, List<Integer> logIds,
                                             UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException {

        if (null == logIds || logIds.size() == 0) {
            return null;
        }

        User user = userSrv.findUserByLogin(username);
        assert user != null;

        // Get all the user metadata that can be accessed by groups that contain specified user
        Set<GroupUsermetadata> groupUsermetadataSet = new HashSet<>();
        for (Group group : user.getGroups()) {
            groupUsermetadataSet.addAll(groupUsermetadataRepo.findByGroup(group));
        }
        Set<Usermetadata> usermetadataList1 = new HashSet<>();
        for (GroupUsermetadata groupUsermetadata : groupUsermetadataSet) {
            Usermetadata u = groupUsermetadata.getUsermetadata();
            if (u.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId()) && u.getIsValid()) {
                usermetadataList1.add(u);
            }
        }

        // ??? Dash Template ???
//        if (logIds == null) {
//            Set<Usermetadata> output = new HashSet<>();
//            for (Usermetadata um : userMetadataRepo.findAll()) {
//                if (um.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId())) {
//                    output.add(um);
//                }
//            }
//            return output.size() > 0 ? output : null;
//        }

        // Get all the user metadata that linked to specified logs
        List<Set<Usermetadata>> lists = new ArrayList<>();
        for (Integer logId : logIds) {
            Set<Usermetadata> usermetadataList2 = new HashSet<>();
            Set<UsermetadataLog> usermetadataLogSet =
                    new HashSet<>(usermetadataLogRepo.findByLog(logRepo.findUniqueByID(logId)));
            for (UsermetadataLog usermetadataLog : usermetadataLogSet) {
                usermetadataList2.add(usermetadataLog.getUsermetadata());
            }
            lists.add(usermetadataList2);
        }
        // Find intersection of user metadata lists that get from specified logIds
        Set<Usermetadata> result = new HashSet<>();
        for (Set<Usermetadata> umSet : lists) {
            for (Usermetadata u : umSet) {
                if (u.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId()) && u.getIsValid()) {
                    int count = 0;
                    Set<UsermetadataLog> umlSet = u.getUsermetadataLog();
                    if (umlSet.size() == logIds.size()) {
                        for (UsermetadataLog uml : umlSet) {
                            if (logIds.contains(uml.getLog().getId())) {
                                count += 1;
                            }
                        }
                        if (count == logIds.size()) {
                            result.add(u);
                        }
                    }
                }
            }
        }

        // Find intersection of user metadata lists that get from 2 linked tables (log, group)
        result.retainAll(usermetadataList1);

        return result.size() > 0 ? result : null;
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

    @Override
    @Transactional
    public void saveDashTemplate(String content, String username) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        Usermetadata userMetadata = new Usermetadata();

        Set<GroupUsermetadata> groupUserMetadataSet = userMetadata.getGroupUserMetadata();
        Set<UsermetadataLog> usermetadataLogSet = userMetadata.getUsermetadataLog();

        // Assign OWNER permission to the user's personal group
        groupUserMetadataSet.add(new GroupUsermetadata(user.getGroup(), userMetadata, true, true, true));

        // Assign READ permission to all the groups that contain specified user
        for (Group group : user.getGroups()) {
            groupUserMetadataSet.add(new GroupUsermetadata(group, userMetadata, true, false, false));
        }

//        usermetadataLogSet.add(new UsermetadataLog(userMetadata, null));

        // Assemble Usermetadata
        userMetadata.setGroupUserMetadata(groupUserMetadataSet);
        userMetadata.setUsermetadataLog(usermetadataLogSet);
        userMetadata.setUsermetadataType(usermetadataTypeRepo.findOne(UserMetadataTypeEnum.DASH_TEMPLATE.getUserMetadataTypeId()));
        userMetadata.setIsValid(true);
        userMetadata.setCreatedBy(user.getRowGuid());
        userMetadata.setCreatedTime(now);
        userMetadata.setContent(content);

        userMetadataRepo.saveAndFlush(userMetadata);
        LOGGER.info("Create user metadata ID: {} TYPE: {}.",
                userMetadata.getId(), UserMetadataTypeEnum.DASH_TEMPLATE.toString());
    }

    @Override
    public Set<Usermetadata> getDashTemplate(String username) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);
        assert user != null;

        // Get all the user metadata that can be accessed by groups that contain specified user
        Set<GroupUsermetadata> groupUsermetadataSet = new HashSet<>();
        for (Group group : user.getGroups()) {
            groupUsermetadataSet.addAll(groupUsermetadataRepo.findByGroup(group));
        }
        Set<Usermetadata> usermetadataList = new HashSet<>();
        for (GroupUsermetadata groupUsermetadata : groupUsermetadataSet) {
            Usermetadata u = groupUsermetadata.getUsermetadata();
            if (u.getUsermetadataType().getId().equals(UserMetadataTypeEnum.DASH_TEMPLATE.getUserMetadataTypeId()) && u.getIsValid()) {
                usermetadataList.add(u);
            }
        }
        return usermetadataList.size() > 0 ? usermetadataList : null;
    }

}
