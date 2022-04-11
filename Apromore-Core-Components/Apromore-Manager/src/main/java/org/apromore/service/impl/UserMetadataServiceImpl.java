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
package org.apromore.service.impl;

import org.apromore.dao.*;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.*;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.AuthorizationService;
import org.apromore.service.UserMetadataService;
import org.apromore.service.UserService;
import org.apromore.util.AccessType;
import org.apromore.util.UserMetadataTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service("userMetadataService")
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor =
        Exception.class)
public class UserMetadataServiceImpl implements UserMetadataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMetadataServiceImpl.class);

    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private LogRepository logRepo;
    private GroupLogRepository groupLogRepo;
    private UserService userSrv;
    private GroupUsermetadataRepository groupUsermetadataRepo;
    private UsermetadataRepository userMetadataRepo;
    private UsermetadataTypeRepository usermetadataTypeRepo;
    private UsermetadataLogRepository usermetadataLogRepo;
    private UsermetadataProcessRepository usermetadataProcessRepo;
    private GroupRepository groupRepo;
    private AuthorizationService authorizationService;

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
                                   final UsermetadataLogRepository usermetadataLogRepo,
                                   final UsermetadataProcessRepository usermetadataProcessRepo,
                                   final GroupRepository groupRepository,
                                   final AuthorizationService authorizationService) {
        this.logRepo = logRepository;
        this.groupLogRepo = groupLogRepository;
        this.userSrv = userSrv;
        this.groupUsermetadataRepo = groupUserMetadataRepo;
        this.userMetadataRepo = userMetadataRepo;
        this.usermetadataTypeRepo = usermetadataTypeRepo;
        this.usermetadataLogRepo = usermetadataLogRepo;
        this.usermetadataProcessRepo = usermetadataProcessRepo;
        this.groupRepo = groupRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    @Transactional
    public Usermetadata saveUserMetadata(String userMetadataName, String userMetadataContent, UserMetadataTypeEnum userMetadataTypeEnum,
                                               String username,
                                               Integer logId) throws UserNotFoundException {

        ArrayList<Integer> logIds = new ArrayList<>(Collections.singletonList(logId));

        return saveUserMetadata(userMetadataName, userMetadataContent, userMetadataTypeEnum, username, logIds);
    }

    @Override
    @Transactional
    public Usermetadata saveUserMetadata(String userMetadataName, String userMetadataContent, UserMetadataTypeEnum userMetadataTypeEnum, String username,
                                 List<Integer> logIds) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);
        Usermetadata userMetadata = new Usermetadata();

        Set<Log> logs = new HashSet<>();
        for (Integer logId : logIds) {
            logs.add(logRepo.findUniqueByID(logId));
        }

        // Assemble Usermetadata
        userMetadata.setUsermetadataType(usermetadataTypeRepo.findById(userMetadataTypeEnum.getUserMetadataTypeId()).get());
        userMetadata.setIsValid(true);
        userMetadata.setCreatedBy(user.getRowGuid());
        userMetadata.setCreatedTime(dateFormat.format(new Date()));
        userMetadata.setContent(userMetadataContent);
        userMetadata.setName(userMetadataName);
        userMetadata.setLogs(logs);

        // Persist Usermetadata and UsermetadataLog
        userMetadata = userMetadataRepo.saveAndFlush(userMetadata);
        LOGGER.info("User: {} create user metadata ID: {} TYPE: {}.", username, userMetadata.getId(),
                userMetadataTypeEnum.toString());

        return userMetadata;
    }

    /**
     *
     * Find UserMetadata that linked to specified Log and Group
     * @param logId logId
     * @param groupRowGuid GroupId
     * @return Set of GroupUsermetadata
     */
    private  Set<GroupUsermetadata> findByLogAndGroup(Integer logId, String groupRowGuid) {

        // TODO change logic here

        Group group = groupRepo.findByRowGuid(groupRowGuid);
        Set<GroupUsermetadata> result = new HashSet<>();

        // All the user metadata that linked to this log
        Set<UsermetadataLog> usermetadataLogSet =
                new HashSet<>(usermetadataLogRepo.findByLog(logRepo.findUniqueByID(logId)));

    
            for (UsermetadataLog usermetadataLog : usermetadataLogSet) {
                Usermetadata u = usermetadataLog.getUsermetadata();

                // Get all the user metadata that can be accessed by group
                List<GroupUsermetadata> groupUsermetadataList = groupUsermetadataRepo.findByUsermetadataId(u.getId());

                // Remove permissions assigned to specified group, and metadata itself
                for (GroupUsermetadata g : groupUsermetadataList) {
                    if (g.getGroup().equals(group)) {
                        result.add(g);
                    }
                }
            }

        return result;
    }

    @Override
    @Transactional
    public void saveUserMetadataAccessRightsByLogAndGroup(Integer logId, String groupRowGuid, AccessType accessType) {

    // Disabled from UI side, since restricted share is not allowed in release 7.19

        Group group = groupRepo.findByRowGuid(groupRowGuid);

        // Assign specified group with the same permission to all the user metadata that linked to the specified log
        if (accessType.isRead() || accessType.isWrite() || accessType.isOwner()) {

            // All the user metadata that linked to this log
            Set<UsermetadataLog> usermetadataLogSet =
                    new HashSet<>(usermetadataLogRepo.findByLog(logRepo.findUniqueByID(logId)));

            if (usermetadataLogSet.size() != 0) {

                for (UsermetadataLog usermetadataLog : usermetadataLogSet) {
                    Usermetadata u = usermetadataLog.getUsermetadata();
                    GroupUsermetadata g = groupUsermetadataRepo.findByGroupAndUsermetadata(group, u);

                    // Inherit permission from log
                    if (g == null) {
                        g = new GroupUsermetadata(group,
                                u, accessType.isRead(), accessType.isWrite(), accessType.isOwner());
                        u.getGroupUserMetadata().add(g);
                    } else {
                        g.setAccessRights(new AccessRights(accessType.isRead(), accessType.isWrite(), accessType.isOwner()));

                    }
                    groupUsermetadataRepo.save(g);
                    userMetadataRepo.save(u);
                }
            }

        }
    }

    @Override
    public void shareSimulationMetadata(Integer logId, String groupRowGuid, AccessType accessType) {

        Group group = groupRepo.findByRowGuid(groupRowGuid);

        // Assign specified group with the same permission to the simulation metadata
        if (accessType.isRead() || accessType.isWrite() || accessType.isOwner()) {

            // All the user metadata that linked to this log
            Set<UsermetadataLog> usermetadataLogSet =
                    new HashSet<>(usermetadataLogRepo.findByLog(logRepo.findUniqueByID(logId)));

            if (usermetadataLogSet.size() != 0) {

                for (UsermetadataLog usermetadataLog : usermetadataLogSet) {
                    Usermetadata u = usermetadataLog.getUsermetadata();

                    if (UserMetadataTypeEnum.SIMULATOR.getUserMetadataTypeId().equals(u.getUsermetadataType().getId())) {

                        GroupUsermetadata g = groupUsermetadataRepo.findByGroupAndUsermetadata(group, u);

                        // Inherit permission from log
                        if (g == null) {
                            g = new GroupUsermetadata(group,
                                    u, accessType.isRead(), accessType.isWrite(), accessType.isOwner());
                            u.getGroupUserMetadata().add(g);
                        } else {
                            g.setAccessRights(new AccessRights(accessType.isRead(), accessType.isWrite(), accessType.isOwner()));

                        }
                        groupUsermetadataRepo.save(g);
                        userMetadataRepo.save(u);
                    }
                }
            }
        }
    }

    @Override
    public void shareUserMetadataWithLog(Integer logId, String groupRowGuid, AccessType accessType) {

        Group group = groupRepo.findByRowGuid(groupRowGuid);

        // Assign specified group with the same permission to the simulation metadata
        if (accessType.isRead() || accessType.isWrite() || accessType.isOwner()) {

            // All the user metadata that linked to this log
            Set<Usermetadata> usermetadataSet = logRepo.findUniqueByID(logId).getUsermetadataSet();

            if (usermetadataSet.size() != 0) {

                for (Usermetadata u : usermetadataSet) {

                    Set<Log> logs = u.getLogs();
                    AccessType at;

                    if (logs.size() == 0) { // null-log user metadata
                        return;

                    } else if (logs.size() == 1) { // single-log user metadata
                        at = accessType;

                    } else { // multi-log user metadata

                        // Identify group's access rights on this user metadata, which is the most restrictive of the
                        // access rights across all the logs associated to this user metadata.
                        List<AccessType> accessTypes = new ArrayList<>();

                        for (Log l : logs) {
                            accessTypes.add(AccessType.getAccessType(groupLogRepo.findByGroupAndLog(group, l).getAccessRights()));
                        }

                        if (accessTypes.contains(AccessType.RESTRICTED)) {
                            at = AccessType.RESTRICTED;
                        } else if (accessTypes.contains(AccessType.VIEWER)) {
                            at = AccessType.VIEWER;
                        } else if (accessTypes.contains(AccessType.EDITOR)) {
                            at = AccessType.EDITOR;
                        } else {
                            at = AccessType.OWNER;
                        }
                    }

                    GroupUsermetadata gu = groupUsermetadataRepo.findByGroupAndUsermetadata(group, u);

                    // Inherit permission from log
                    if (gu == null) {
                        gu = new GroupUsermetadata(group, u, at.isRead(), at.isWrite(), at.isOwner());
                        u.getGroupUserMetadata().add(gu);
                    } else {
                        gu.setAccessRights(new AccessRights(accessType.isRead(), accessType.isWrite(),
                                accessType.isOwner()));
                    }
                    groupUsermetadataRepo.save(gu);
                    userMetadataRepo.save(u);
                }
            }
        }
    }

    @Override
    @Transactional
    public void removeUserMetadataAccessRightsByLogAndGroup(Integer logId, String groupRowGuid, String username) throws UserNotFoundException {

        Set<GroupUsermetadata> groupUsermetadataSet;

        groupUsermetadataSet = findByLogAndGroup(logId, groupRowGuid);

        for (GroupUsermetadata g : groupUsermetadataSet) {

            if(g.getUsermetadata().getGroupUserMetadata().size() == 1) { // if this metadata will be an orphan
                deleteUserMetadata(g.getUsermetadata().getId(), username);
            } else {
                groupUsermetadataRepo.delete(g);
            }
        }
    }

    @Override
    @Transactional
    public Usermetadata updateUserMetadata(Usermetadata userMetadata, String username, String content) throws UserNotFoundException, JpaOptimisticLockingFailureException {

        User user = userSrv.findUserByLogin(username);

        // Usermetadata userMetadata = userMetadataRepo.findOne(usermetadataId);
        userMetadata.setContent(content);
        userMetadata.setUpdatedBy(user.getRowGuid());
        userMetadata.setUpdatedTime(dateFormat.format(new Date()));

        // Persist Usermetadata
        LOGGER.info("User: {} update user metadata ID: {}.", username, userMetadata.getId());
        return userMetadataRepo.saveAndFlush(userMetadata);
    }

    @Override
    public Usermetadata updateUserMetadataName(Integer userMetadataId, String username, String name) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        // Optimistic locking version check is not necessary here since consistency should be guaranteed by EventQueue
        Usermetadata userMetadata = userMetadataRepo.findById(userMetadataId).get();
        userMetadata.setName(name);
        userMetadata.setUpdatedBy(user.getRowGuid());
        userMetadata.setUpdatedTime(dateFormat.format(new Date()));

        // Persist Usermetadata
        LOGGER.info("User: {} update user metadata ID: {}.", username, userMetadata.getId());
        return userMetadataRepo.saveAndFlush(userMetadata);
    }

    @Override
    @Transactional
    public void deleteUserMetadata(Integer usermetadataId, String username) {
        userMetadataRepo.deleteById(usermetadataId);
        LOGGER.info("User: {} Delete user metadata ID: {}.", username, usermetadataId);
    }

    @Override
    public Set<Usermetadata> getUserMetadataByUser(String username, UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        // Get all the user metadata that can be accessed by groups that contain specified user
        Set<Usermetadata> umSet = new HashSet<>();

        for (Group group : user.getGroups()) {
            List<GroupUsermetadata> guList = groupUsermetadataRepo.findByGroup(group);

            for (GroupUsermetadata gu : guList) {
                Usermetadata u = gu.getUsermetadata();
                if (u.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId()) && u.getIsValid()) {
                    umSet.add(u);
                }
            }
        }
        return umSet;
    }

    @Override
    public List<Usermetadata> getUserMetadataListByUser(String username, UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        return user.getGroups().stream()
                .flatMap(x -> groupUsermetadataRepo.findByGroup(x).stream()
                        .filter(gu -> gu.getUsermetadata().getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId())
                                && gu.getUsermetadata().getIsValid()).map(GroupUsermetadata::getUsermetadata))
                .collect(Collectors.toList()).stream()
                .collect(Collectors.groupingBy(Usermetadata::getId)).values().stream()
                .map(v -> v.get(0)).collect(Collectors.toList());
    }

    @Override
    public Set<Usermetadata> getUserMetadataByLogs(List<Integer> logIds, UserMetadataTypeEnum userMetadataTypeEnum) {

        Set<Usermetadata> result = new HashSet<>();

        if (null == logIds || logIds.size() == 0) {
            return result;
        }

        // Get all the user metadata that linked to specified logs
        List<Set<Usermetadata>> lists = new ArrayList<>();
        for (Integer logId : logIds) {
            lists.add(getUserMetadataByLog(logId, userMetadataTypeEnum));
        }
        // Find intersection of user metadata lists that get from specified logIds
        for (Set<Usermetadata> umSet : lists) {
            for (Usermetadata u : umSet) {
                int count = 0;
                Set<Log> logs = u.getLogs();
                if (logs.size() == logIds.size()) {  // May have duplicated UsermetadataLog umlSet.size()
                    for (Log l : logs) {
                        if (logIds.contains(l.getId())) {
                            count += 1;
                        }
                    }
                    if (count == logIds.size()) {
                        result.add(u);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Set<Usermetadata> getUserMetadataByUserAndLogs(String username, List<Integer> logIds,
                                                   UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException {
        Set<Usermetadata> result = new HashSet<>();

        if (null == logIds || logIds.size() == 0) {
            return result;
        }

        User user = userSrv.findUserByLogin(username);

        // Get all the user metadata that linked to specified logs, and the specified user has access to
        List<Set<Usermetadata>> lists = new ArrayList<>();
        for (Integer logId : logIds) {
            AccessType accessType = authorizationService.getLogAccessTypeByUser(logId, user);
            if (accessType == null) {
                return result; // If specified user doesn't have access to one log, return empty result
            }
            if (accessType == AccessType.RESTRICTED) {
                lists.add(getUserMetadataWithRestrictedViewer(user, logId, userMetadataTypeEnum));

            } else {
                lists.add(getUserMetadataByLog(logId, userMetadataTypeEnum));
            }
        }
        // Find intersection of user metadata lists that get from specified logIds
        for (Set<Usermetadata> umSet : lists) {
            for (Usermetadata u : umSet) {
                int count = 0;
                Set<Log> logs = u.getLogs();
                if (logs.size() == logIds.size()) {  // May have duplicated UsermetadataLog umlSet.size()
                    for (Log l : logs) {
                        if (logIds.contains(l.getId())) {
                            count += 1;
                        }
                    }
                    if (count == logIds.size()) {
                        result.add(u);
                    }
                }
            }
        }

        return result;
    }

    private Set<Usermetadata> getUserMetadataWithRestrictedViewer(User user, Integer logId,
                                                                  UserMetadataTypeEnum userMetadataTypeEnum ) {

        // Get all the user metadata that can be accessed by groups that contain specified user
        Set<Usermetadata> umSet = new HashSet<>();

        for (Group group : user.getGroups()) {
            List<GroupUsermetadata> guList = groupUsermetadataRepo.findByGroup(group);

            for (GroupUsermetadata gu : guList) {
                Usermetadata u = gu.getUsermetadata();
                if (u.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId()) && u.getIsValid()) {
                    umSet.add(u);
                }
            }
        }

        // Get all the user metadata that associated with specified log
        Set<Usermetadata> umSet2 = new HashSet<>();

        for (Usermetadata u : logRepo.findUniqueByID(logId).getUsermetadataSet()) {
            if (u.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId()) && u.getIsValid()) {
                umSet2.add(u);
            }
        }

        // Get intersection of two result set
        umSet.retainAll(umSet2);

        return umSet;
    }

    @Override
    public Set<Usermetadata> getUserMetadataWithRestrictedViewer(Group group, Integer logId,
                                                                 UserMetadataTypeEnum userMetadataTypeEnum) {

        // Get all the user metadata that can be accessed by groups that contain specified user
        Set<Usermetadata> umSet = new HashSet<>();

        List<GroupUsermetadata> guList = groupUsermetadataRepo.findByGroup(group);

        for (GroupUsermetadata gu : guList) {
            Usermetadata u = gu.getUsermetadata();
            if (u.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId()) && u.getIsValid()) {
                umSet.add(u);
            }
        }

        // Get all the user metadata that associated with specified log
        Set<Usermetadata> umSet2 = new HashSet<>();

        for (Usermetadata u : logRepo.findUniqueByID(logId).getUsermetadataSet()) {
            if (u.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId()) && u.getIsValid()) {
                umSet2.add(u);
            }
        }

        // Get intersection of two result set
        umSet.retainAll(umSet2);

        return umSet;
    }



    @Override
    public Set<Usermetadata> getUserMetadataByLog(Integer logId, UserMetadataTypeEnum userMetadataTypeEnum) {

        Set<Usermetadata> umSet = new HashSet<>();
        Set<Usermetadata> usermetadataSet = logRepo.findUniqueByID(logId).getUsermetadataSet();
        for (Usermetadata u : usermetadataSet) {
            if (u.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId()) && u.getIsValid()) {
                umSet.add(u);
            }
        }

        return umSet;
    }

    @Override
    public Set<Usermetadata> getUserMetadataByUserAndLog(String username, Integer logId,
                                                         UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException {

        return getUserMetadata(username, Collections.singletonList(logId), userMetadataTypeEnum);
    }

    @Override
    public Set<Usermetadata> getUserMetadata(String username, List<Integer> logIds,
                                             UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException {

        // Don't need to consider Restricted-viewer for multi-log user metadata
        // Only used in Dash and Filter
        return getUserMetadataByUserAndLogs(username, logIds, userMetadataTypeEnum);
    }


    @Override
    public boolean canUserEditMetadata(String username, Integer usermetadataId) throws UserNotFoundException {

        for (GroupUsermetadata gl : groupUsermetadataRepo.findByLogAndUser(usermetadataId,
                userSrv.findUserByLogin(username).getRowGuid())) {
            if (gl.getHasOwnership()) { // Only owner can edit
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canUserCreateMetadata(String username, Integer logId) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        List<AccessType> accessTypes = new ArrayList<>();
        for (Group g : user.getGroups()) {
            accessTypes.add(AccessType.getAccessType(groupLogRepo.findByGroupAndLog(g,
                    logRepo.findUniqueByID(logId)).getAccessRights()));
        }

        return getLeastRestrictiveAccessType(accessTypes) == AccessType.OWNER
                || getLeastRestrictiveAccessType(accessTypes) == AccessType.EDITOR;
    }

    private AccessType getLeastRestrictiveAccessType(List<AccessType> accessTypes) {

        if (accessTypes == null || accessTypes.size() == 0) {
            return null;
        }

        if (accessTypes.contains(AccessType.OWNER)) {
            return AccessType.OWNER;
        } else if (accessTypes.contains(AccessType.EDITOR)) {
            return AccessType.EDITOR;
        } else if (accessTypes.contains(AccessType.VIEWER)) {
            return AccessType.VIEWER;
        } else {
            return AccessType.RESTRICTED;
        }
    }

    private AccessType getMostRestrictiveAccessType(Set<Log> logs, Group group) {

        AccessType at;

        // Identify group's access rights on this user metadata, which is the most restrictive of the
        // access rights across all the logs associated to this user metadata.
        List<AccessType> accessTypes = new ArrayList<>();

        for (Log l : logs) {
            accessTypes.add(AccessType.getAccessType(groupLogRepo.findByGroupAndLog(group, l).getAccessRights()));
        }

        if (accessTypes.contains(AccessType.RESTRICTED)) {
            at = AccessType.RESTRICTED;
        } else if (accessTypes.contains(AccessType.VIEWER)) {
            at = AccessType.VIEWER;
        } else if (accessTypes.contains(AccessType.EDITOR)) {
            at = AccessType.EDITOR;
        } else {
            at = AccessType.OWNER;
        }

        return at;
    }

    @Override
    public AccessType getUserMetadataAccessType(Group group, Usermetadata usermetadata) {

        // TODO change logic if necessary

        GroupUsermetadata gu = groupUsermetadataRepo.findByGroupAndUsermetadata(group, usermetadata);

        if (gu != null) {
            return AccessType.getAccessType(gu.getHasRead(), gu.getHasWrite(), gu.getHasOwnership());
        }
        return null;
    }

    @Override
    public AccessType getUserMetadataAccessTypeByUser(Integer usermetadataId, User user) throws UserNotFoundException {

        return authorizationService.getUserMetadataAccessTypeByUser(usermetadataId, user);
    }

    @Override
    @Transactional
    public void saveUserMetadataWithoutLog(String content, UserMetadataTypeEnum userMetadataTypeEnum,
                                           String username) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);
        Usermetadata userMetadata = buildUserMetadataFromContent(content, userMetadataTypeEnum, user);
        userMetadataRepo.saveAndFlush(userMetadata);
        LOGGER.info("User: {} create user metadata ID: {} TYPE: {}.", username,
                userMetadata.getId(), userMetadataTypeEnum);
    }

    @Override
    @Transactional
    public List<Usermetadata> saveAllUserMetadataWithoutLog(List<String> contentList, UserMetadataTypeEnum userMetadataTypeEnum,
                                           String username) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);
        List<Usermetadata> usermetadataList = new ArrayList<>();

        for (String content : contentList) {
            usermetadataList.add(buildUserMetadataFromContent(content, userMetadataTypeEnum, user));
        }

        return userMetadataRepo.saveAll(usermetadataList);

    }

    private Usermetadata buildUserMetadataFromContent(String content, UserMetadataTypeEnum userMetadataTypeEnum,
                                                      User user) {
        Usermetadata userMetadata = new Usermetadata();

        Set<GroupUsermetadata> groupUserMetadataSet = userMetadata.getGroupUserMetadata();
        Set<Log> logs = userMetadata.getLogs();

        // Assign OWNER permission to the user's personal group
        groupUserMetadataSet.add(new GroupUsermetadata(user.getGroup(), userMetadata, true, true, true));

        // Assign READ permission to all the groups that contain specified user
        for (Group group : user.getGroups()) {
            groupUserMetadataSet.add(new GroupUsermetadata(group, userMetadata, true, false, false));
        }

        // Assemble Usermetadata
        userMetadata.setGroupUserMetadata(groupUserMetadataSet);
        userMetadata.setLogs(logs);
        userMetadata.setUsermetadataType(usermetadataTypeRepo.findById(userMetadataTypeEnum.getUserMetadataTypeId()).get());
        userMetadata.setIsValid(true);
        userMetadata.setCreatedBy(user.getRowGuid());
        userMetadata.setCreatedTime(dateFormat.format(new Date()));
        userMetadata.setContent(content);

        return userMetadata;
    }

    @Override
    public Set<Usermetadata> getUserMetadataWithoutLog(UserMetadataTypeEnum userMetadataTypeEnum, String username) throws UserNotFoundException {

        // Currently for CSV_IMPORTER only. Get all the artifacts that are associated with the logs this specified
        // user has access to.

        Set<Usermetadata> usermetadataSet = new HashSet<>();

        User user = userSrv.findUserByLogin(username);
        List<GroupLog> groupLogList = groupLogRepo.findLogsByUser(user.getRowGuid());

        // Get all the user metadata that can be accessed by groups that contain specified user
        for(GroupLog gl : groupLogList) {
            usermetadataSet.addAll(getUserMetadataByLog(gl.getLog().getId(), userMetadataTypeEnum));
        }

        return usermetadataSet.size() > 0 ? usermetadataSet : null;
    }

    @Override
    public void deleteUserMetadataByLog(Log log, User user) throws UserNotFoundException {

        Set<UsermetadataLog> usermetadataLogSet =
                new HashSet<>(usermetadataLogRepo.findByLog(log));
        for (UsermetadataLog usermetadataLog : usermetadataLogSet) {
            deleteUserMetadata(usermetadataLog.getUsermetadata().getId(), user.getUsername());
        }
    }

    @Override
    public User findUserByRowGuid(String rowGuid) throws UserNotFoundException {
        return userSrv.findUserByRowGuid(rowGuid);
    }

    @Override
    public List<GroupUsermetadata> getGroupUserMetadata(Integer userMetadataId) {
        return groupUsermetadataRepo.findByUsermetadataId(userMetadataId);
    }

    @Override
    @Transactional
    public void saveUserMetadataAccessRights(Integer userMetadataId, String groupRowGuid, boolean hasRead,
                                             boolean hasWrite, boolean hasOwnership) {
        Group group = groupRepo.findByRowGuid(groupRowGuid);
        Usermetadata usermetadata = userMetadataRepo.findById(userMetadataId).get();
        AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
        GroupUsermetadata gu =
                groupUsermetadataRepo.findByGroupAndUsermetadata(group, usermetadata);

        if (gu == null) {
            gu = new GroupUsermetadata(group, usermetadata, accessRights);
        } else {
            gu.setAccessRights(accessRights);
        }
        groupUsermetadataRepo.save(gu);

        // Assign the same access right to logs if there is no access. Do nothing if there's already access.
        if (!canAccessAssociatedLog(userMetadataId, groupRowGuid)) {

            Set<Log> logs = findById(userMetadataId).getLogs();
            for (Log l : logs) {
                Set<Group> groups = new HashSet<>();

                for (GroupLog gl : l.getGroupLogs()) {
                    groups.add(gl.getGroup());
                }

                if(!groups.contains(group)) {
                    groupLogRepo.save(new GroupLog(group, l, accessRights));
                }
            }
        }
    }

    @Override
    public void saveUserMetadataAccessType(Integer userMetadataId, String groupRowGuid, AccessType accessType) {

        saveUserMetadataAccessRights(userMetadataId, groupRowGuid, accessType.isRead(), accessType.isWrite(),
                accessType.isOwner());
    }

    @Override
    public void removeUserMetadataAccessRights(Integer userMetadataId, String groupRowGuid) {

        Group group = groupRepo.findByRowGuid(groupRowGuid);
        Usermetadata usermetadata = userMetadataRepo.findById(userMetadataId).get();

        groupUsermetadataRepo.delete(groupUsermetadataRepo.findByGroupAndUsermetadata(group, usermetadata));
    }

    @Override
    public Usermetadata findById(Integer id) {
        return userMetadataRepo.findById(id).get();
    }

    @Override
    public boolean canDeleteUserMetadata(Integer userMetadataId, String groupRowGuid) {

        List<GroupUsermetadata> groupUsermetadataList = getGroupUserMetadata(userMetadataId);
        List<GroupUsermetadata> ownerList = new ArrayList<>();

        for (GroupUsermetadata g :groupUsermetadataList) {
            if(g.getAccessRights().isOwnerShip()) {
                ownerList.add(g);
            }
        }

        return (ownerList.size() == 1 && ownerList.get(0).getGroup().getRowGuid().equals(groupRowGuid));
    }

    @Override
    public boolean canAccessAssociatedLog(Integer userMetadataId, String groupRowGuid) {

        Set<Log> logs = findById(userMetadataId).getLogs();

        // If this metadata doesn't link to log, then turn true.
        if(logs.size() == 0) {
            return true;
        }

        Set<Group> groups = new HashSet<>();
        Set<Group> intersection = new HashSet<>();
        for (Log l : logs) {
            // Assume groups can't be empty since this log is shown in UI, which means at least one user/group have
            // access to it.
            for (GroupLog gl : l.getGroupLogs()) {
                groups.add(gl.getGroup());
            }

            if (!intersection.isEmpty()) {
                groups.retainAll(intersection);
                intersection.clear();
            }

            intersection.addAll(groups);
            groups.clear();
        }

        return intersection.contains(groupRepo.findByRowGuid(groupRowGuid));
    }

    @Override
    public List<Log> getDependentLog(Usermetadata usermetadata) {
        Set<Log> logSet = usermetadata.getLogs();

        return new ArrayList<>(logSet);
    }

    @Override
    public List<Process> getDependentProcess(Usermetadata usermetadata) {
        List<Process> processes = new ArrayList<>();
        Set<UsermetadataProcess> usermetadataProcessSet = usermetadata.getUsermetadataProcess();

        for (UsermetadataProcess up : usermetadataProcessSet) {
            processes.add(up.getProcess());
        }

        return processes;
    }

    @Override
    public Optional<Usermetadata> getUserMetadataById(Integer id) {
        return userMetadataRepo.findById(id);
    }


}
