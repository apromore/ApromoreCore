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
package org.apromore.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apromore.builder.FolderBuilder;
import org.apromore.builder.UserManagementBuilder;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupLogRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.RoleRepository;
import org.apromore.dao.StorageRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.UsermetadataRepository;
import org.apromore.dao.UsermetadataTypeRepository;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.Storage;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Usermetadata;
import org.apromore.dao.model.UsermetadataType;
import org.apromore.service.AuthorizationService;
import org.apromore.util.AccessType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ShareFeatureUnitTest extends BaseTest {

    UserManagementBuilder builder;
    FolderBuilder folderBuilder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UsermetadataTypeRepository usermetadataTypeRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UsermetadataRepository usermetadataRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    ProcessRepository processRepository;

    @Autowired
    LogRepository logRepository;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    AuthorizationService authorizationService;


    @BeforeEach
    final void setUp() {
        builder = new UserManagementBuilder();
        folderBuilder = new FolderBuilder();
    }

    @Test
    @Disabled
    void testSaveUser() {
        // given
        Group group = groupRepository.saveAndFlush(builder.withGroup("testGroup1", "USER").buildGroup());
        Role role = roleRepository.saveAndFlush(builder.withRole("testRole").buildRole());
        User user = builder.withGroup(group).withRole(role).withMembership("n@t.com").withUser("TestUser", "first",
                "last", "org").buildUser();
        // when
        User savedUSer = userRepository.saveAndFlush(user);
        // then
        assertThat(savedUSer.getId()).isNotNull();
        assertThat(savedUSer.getMembership().getEmail()).isEqualTo(user.getMembership().getEmail());
    }

    @Test
    @Disabled
    void insertUsermetadataTest() {
//	 		Given
        Usermetadata um = builder.withUserMetaDataType("test Type", 1).withUserMetaData("Test", "test")
                .buildUserMetaData();

        UsermetadataType type = usermetadataTypeRepository.save(um.getUsermetadataType());
        um.setUsermetadataType(type);

//	        When
        um = usermetadataRepository.saveAndFlush(um);
        Usermetadata umExpected = usermetadataRepository.findById(um.getId()).get();

//	        Then
        assertThat(um.getId()).isNotNull();
        assertThat(umExpected.getId()).isNotNull();
        assertThat(umExpected.getCreatedTime()).isEqualTo(um.getCreatedTime());

    }

    @Test
    void removeFolderPermission_withUnsharedLogAndProcess() {
        //Given
        User user1 = createUser("removeFolderPermission_withUnsharedProcess1");
        User user2 = createUser("removeFolderPermission_withUnsharedProcess2");

        Folder folder = folderBuilder.withFolder("folder", "folder").build();
        folder.setParentFolderChain("0");
        folderRepository.saveAndFlush(folder);

        Process processShared = new Process();
        processShared.setFolder(folder);
        processRepository.saveAndFlush(processShared);

        Process processUnshared = new Process();
        processUnshared.setFolder(folder);
        processRepository.saveAndFlush(processUnshared);

        Storage storage = createStorage("logUnshared");
        Log logUnshared = createLog(user1, folder, storage);
        logRepository.saveAndFlush(logUnshared);

        Storage storage1 = createStorage("logShared");
        Log logShared = createLog(user1, folder, storage1);
        logRepository.saveAndFlush(logShared);

        //User 1 is the owner of the folder
        authorizationService.saveFolderAccessType(folder.getId(), user1.getGroup().getRowGuid(), AccessType.OWNER);

        //Assert user 1 has owner access to all artifacts in folder
        assertEquals(AccessType.OWNER, authorizationService.getFolderAccessTypeByUser(folder.getId(), user1));
        assertEquals(AccessType.OWNER, authorizationService.getProcessAccessTypeByUser(processShared.getId(), user1));
        assertEquals(AccessType.OWNER, authorizationService.getProcessAccessTypeByUser(processUnshared.getId(), user1));
        assertEquals(AccessType.OWNER, authorizationService.getLogAccessTypeByUser(logUnshared.getId(), user1));
        assertEquals(AccessType.OWNER, authorizationService.getLogAccessTypeByUser(logShared.getId(), user1));

        //Share a model in the folder with user 2
        authorizationService.saveProcessAccessType(processShared.getId(), user2.getGroup().getRowGuid(), AccessType.EDITOR);

        //Share a log in the folder with user 2
        authorizationService.saveLogAccessType(logShared.getId(), user2.getGroup().getRowGuid(), AccessType.EDITOR,
            false);

        //Check that the folder is now shared and the unshared model is still not shared
        assertEquals(AccessType.VIEWER, authorizationService.getFolderAccessTypeByUser(folder.getId(), user2));
        assertEquals(AccessType.EDITOR, authorizationService.getProcessAccessTypeByUser(processShared.getId(), user2));
        assertEquals(AccessType.EDITOR, authorizationService.getLogAccessTypeByUser(logShared.getId(), user2));
        assertNull(authorizationService.getProcessAccessTypeByUser(processUnshared.getId(), user2));
        assertNull(authorizationService.getLogAccessTypeByUser(logUnshared.getId(), user2));

        //Remove folder permission
        authorizationService.removeFolderPermissions(folder.getId(), user2.getGroup().getRowGuid(), AccessType.VIEWER);

        //Check that all folder contents are not shared with User2
        assertNull(authorizationService.getFolderAccessTypeByUser(folder.getId(), user2));
        assertNull(authorizationService.getProcessAccessTypeByUser(processShared.getId(), user2));
        assertNull(authorizationService.getProcessAccessTypeByUser(processUnshared.getId(), user2));
        assertNull(authorizationService.getLogAccessTypeByUser(logUnshared.getId(), user2));
        assertNull(authorizationService.getLogAccessTypeByUser(logShared.getId(), user2));

    }

    private User createUser(String username) {
        UserManagementBuilder userManagementBuilder = new UserManagementBuilder();
        Group group = groupRepository.saveAndFlush(userManagementBuilder.withGroup(username, "USER").buildGroup());
        User user = userManagementBuilder.withGroup(group)
            .withMembership(username + "@t.com")
            .withUser(username, "first","last", "org").buildUser();
        return userRepository.saveAndFlush(user);
    }

    private Storage createStorage(String logDisplayName) {
        Storage storageEntity = new Storage();

        storageEntity.setKey(Base64.getEncoder().withoutPadding().encodeToString(logDisplayName.getBytes(
            StandardCharsets.UTF_8)));
        storageEntity.setPrefix("log/prefix");
        storageEntity.setStoragePath("s3://storage/path");

        return storageRepository.saveAndFlush(storageEntity);
    }

}
