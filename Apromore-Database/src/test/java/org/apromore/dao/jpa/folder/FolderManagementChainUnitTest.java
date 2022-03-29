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

package org.apromore.dao.jpa.folder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apromore.config.BaseTestClass;
import org.apromore.dao.FolderInfoRepository;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.FolderInfo;
import org.apromore.script.FolderParentChainPopulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class FolderManagementChainUnitTest extends BaseTestClass {

    @Autowired
    FolderRepository folderRepo;

    @Autowired
    FolderInfoRepository folderInfoRepository;

    FolderBuilder folderBuilder;

    @Autowired
    FolderParentChainPopulator folderParentChainPopulator;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void setUp() {
        folderBuilder = new FolderBuilder();
    }

    @Test
    void testGetFolderChain() {
        //	Given
        Folder folder1 = new Folder();
        folder1.setName("folder1");
        folder1.setParentFolderChain("0_1");

        Folder folder2 = new Folder();
        folder2.setName("folder2");
        folder2.setParentFolderChain("0_1_2");

        Folder folder3 = new Folder();
        folder3.setName("folder3");
        folder3.setParentFolderChain("0_12_5");

        folder1 = folderRepo.saveAndFlush(folder1);
        folder2 = folderRepo.saveAndFlush(folder2);
        folder3 = folderRepo.saveAndFlush(folder3);

        //	When
        List<Folder> folders01 = folderRepo.findByParentFolderIdOrParentFolderChainLike(1, "0\\_1\\_%");
        List<Folder> folders = folderRepo.findAll();


        //	Then
        assertThat(folders.size()).isEqualTo(3);
        assertThat(folders01.size()).isEqualTo(1);
        assertThat(folders01).contains(folder2);

    }

    //    Check if jpa query works for parentId
    @Test
    void testGetFolderChain1() {
        //	Given
        Folder folder1 = new Folder();
        folder1.setName("folder1");
        folder1.setParentFolderChain("0_1");

        Folder folder2 = new Folder();
        folder2.setName("folder2");
        folder2.setParentFolderChain("0_1_2");

        Folder folder3 = new Folder();
        folder3.setName("folder3");
        folder3.setParentFolderChain("0_12_5");

        folder1 = folderRepo.saveAndFlush(folder1);
        folder2.setParentFolder(folder1);
        folder2 = folderRepo.saveAndFlush(folder2);
        folder3 = folderRepo.saveAndFlush(folder3);

        //	When
        List<Folder> folders01 = folderRepo.findByParentFolderIdOrParentFolderChainLike(folder1.getId(), "1");
        List<Folder> folders = folderRepo.findAll();

        //	Then
        assertThat(folders.size()).isEqualTo(3);
        assertThat(folders01.size()).isEqualTo(1);
        assertThat(folders01).contains(folder2);

    }

    @Test
    void testFolderParentChain() {
        //	Given
        Folder folder1 = folderBuilder.withFolder("Parent1", "Parent1").build();

        Folder folder2 = folderBuilder.withFolder("Parent2", "Parent2").build();

        folder1 = folderRepo.saveAndFlush(folder1);
        folder2 = folderRepo.saveAndFlush(folder2);
        assertThat(folder1.getId()).isNotNull();
        assertThat(folder2.getId()).isNotNull();

        folderRepo.saveAndFlush(folderBuilder.withFolder("child11", "child11").withParent(folder1).build());
        folderRepo.saveAndFlush(folderBuilder.withFolder("child12", "child12").withParent(folder1).build());
        folderRepo.saveAndFlush(folderBuilder.withFolder("child21", "child21").withParent(folder2).build());
        folderRepo.saveAndFlush(folderBuilder.withFolder("child22", "child22").withParent(folder2).build());

        //	When
        int count = folderInfoRepository.countByparentFolderChain("-1");
        folderParentChainPopulator.init();
        List<FolderInfo> folders = folderInfoRepository.findAll();

        //	Then
        assertThat(count).isEqualTo(6);

        for (FolderInfo folder : folders) {
            if (folder.getName().startsWith("Parent")) {
                assertThat(folder.getParentFolderChain()).isEqualTo("0");
            } else if (folder.getName().startsWith("child1")) {
                assertThat(folder.getParentFolderChain()).isEqualTo("0_" + folder1.getId());
            } else if (folder.getName().startsWith("child2")) {
                assertThat(folder.getParentFolderChain()).isEqualTo("0_" + folder2.getId());
            }
        }

    }

    @Test
    void testFolderParentChainMultiple() {
        //	Given
        Folder folder1 = folderBuilder.withFolder("Parent1", "Parent1").build();

        folder1 = folderRepo.saveAndFlush(folder1);
        Folder folder2 = folderRepo
            .saveAndFlush(folderBuilder.withFolder("Child1", "Child1").withParent(folder1).build());
        Folder folder3 = folderRepo
            .saveAndFlush(folderBuilder.withFolder("Child2", "Child2").withParent(folder2).build());

        assertThat(folder1.getId()).isNotNull();
        assertThat(folder2.getId()).isNotNull();
        assertThat(folder3.getId()).isNotNull();

        //	When
        int count = folderInfoRepository.countByparentFolderChain("-1");
        folderParentChainPopulator.init();
        FolderInfo foldersExpected1 = folderInfoRepository.findById(folder1.getId()).get();
        FolderInfo foldersExpected2 = folderInfoRepository.findById(folder2.getId()).get();
        FolderInfo foldersExpected3 = folderInfoRepository.findById(folder3.getId()).get();

        //	Then
        assertThat(count).isEqualTo(3);
        assertThat(foldersExpected1.getParentFolderChain()).isEqualTo("0");
        assertThat(foldersExpected2.getParentFolderChain()).isEqualTo("0_" + folder1.getId());
        assertThat(foldersExpected3.getParentFolderChain()).isEqualTo("0_" + folder1.getId() + "_" + folder2.getId());

    }

    @Test
    void testInQuery() {
        //	Given
        Folder folder1 = new Folder();
        folder1.setName("folder1");
        folder1.setParentFolderChain("0_1");

        Folder folder2 = new Folder();
        folder2.setName("folder2");
        folder2.setParentFolderChain("0_1_2");

        Folder folder3 = new Folder();
        folder3.setName("folder3");
        folder3.setParentFolderChain("0_12_5");

        folder1 = folderRepo.saveAndFlush(folder1);
        folder2 = folderRepo.saveAndFlush(folder2);
        folder3 = folderRepo.saveAndFlush(folder3);

        //	When
        List<Folder> folders = folderRepo.findByIdIn(Arrays.asList(folder1.getId(), folder2.getId()));

//	Then
        assertThat(folders.size()).isEqualTo(2);
        assertThat(folders).extracting("id").doesNotContain(folder3.getId());

    }

}
