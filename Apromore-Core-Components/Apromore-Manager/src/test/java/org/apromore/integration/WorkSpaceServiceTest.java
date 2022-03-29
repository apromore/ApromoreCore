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

import java.util.List;

import org.apromore.builder.FolderBuilder;
import org.apromore.builder.UserManagementBuilder;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.RoleRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.service.WorkspaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
class WorkSpaceServiceTest extends BaseTest {

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    RoleRepository roleRepository;

    FolderBuilder folderBuilder;

    @Autowired
    WorkspaceService workspaceService;

    @Autowired
    UserRepository userRepository;

    UserManagementBuilder userbuilder;

    @BeforeEach
    void setUp() {
        folderBuilder = new FolderBuilder();
        userbuilder = new UserManagementBuilder();
    }

    /*
     * This is a test to check if we get relevant subfolders based on db query.
     */
    @Test
    void testUpdateFolderChain() {
        //	Given
        Folder folder1 = folderBuilder.withFolder("parent1", "parent1").build();

        folder1 = folderRepository.saveAndFlush(folder1);

        Folder folder2 = folderBuilder.withFolder("child1", "child1").withParent(folder1).build();
        folder2 = folderRepository.saveAndFlush(folder2);

        Folder folder3 = folderBuilder.withFolder("child2", "child2").withParent(folder2).build();
        folder3 = folderRepository.saveAndFlush(folder3);

        Folder newFolder = folderBuilder.withFolder("parent2", "parent2").build();
        newFolder.setParentFolder(null);
        newFolder.setParentFolderChain("0");

        newFolder = folderRepository.saveAndFlush(newFolder);
//	Create User
        Group group = groupRepository.saveAndFlush(userbuilder.withGroup("testGroup1", "USER").buildGroup());
        Role role = roleRepository.saveAndFlush(userbuilder.withRole("testRole").buildRole());
        User user = userbuilder.withGroup(group).withRole(role).withMembership("n@t.com")
            .withUser("TestUser", "first", "last", "org").buildUser();

        User savedUSer = userRepository.saveAndFlush(user);

//	When
//	int folderId=workspaceService.createFolderWithSubFolders(savedUSer.getRowGuid(), folder1.getId(), newFolder.getId());

        List<Folder> folders = folderRepository.findByParentFolderIdOrParentFolderChainLike(newFolder.getId(),
            getEscapedString(newFolder.getParentFolderChain() + "_" + newFolder.getId() + "_%"));

//	Then
        assertThat(folders).hasSize(3);
        folder1 = folders.stream().filter((f -> f.getName().equals("parent1"))).findFirst().get();
        assertThat(folder1.getParentFolder().getId()).isEqualTo(newFolder.getId());
        assertThat(folder1.getParentFolderChain())
            .isEqualTo(newFolder.getParentFolderChain() + "_" + newFolder.getId());

        folder2 = folders.stream().filter((f -> f.getName().equals("child1"))).findFirst().get();
        assertThat(folder2.getParentFolder().getId()).isEqualTo(folder1.getId());
        assertThat(folder2.getParentFolderChain()).isEqualTo(folder1.getParentFolderChain() + "_" + folder1.getId());

        folder3 = folders.stream().filter((f -> f.getName().equals("child2"))).findFirst().get();
        assertThat(folder3.getParentFolder().getId()).isEqualTo(folder2.getId());
        assertThat(folder3.getParentFolderChain()).isEqualTo(folder2.getParentFolderChain() + "_" + folder2.getId());

//	assertThat(folderId).isEqualTo(folder1);

        assertThat(folderRepository.findAll()).hasSize(7);

    }

    private String getEscapedString(String prefix) {
        return prefix.replaceAll("\\_", "\\\\_");
    }

}
