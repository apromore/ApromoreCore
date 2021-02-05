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
package org.apromore.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apromore.builder.FolderBuilder;
import org.apromore.dao.FolderInfoRepository;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.model.Folder;
import org.apromore.service.helper.FolderParentChainPopulator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FolderManagementUnitTest extends BaseTest {

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    FolderInfoRepository folderInfoRepository;

    @Autowired
    FolderParentChainPopulator folderParentChainPopulator;

    FolderBuilder folderBuilder;

    @Before
    public void setUp() {
	folderBuilder = new FolderBuilder();
    }

    @Test
    public void testFolderParentChain() {
//	Given
	Folder folder1 = folderBuilder.withFolder("Parent1", "Parent1").build();

	Folder folder2 = folderBuilder.withFolder("Parent2", "Parent2").build();

	folder1 = folderRepository.saveAndFlush(folder1);
	folder2 = folderRepository.saveAndFlush(folder2);
	assertThat(folder1.getId()).isNotNull();
	assertThat(folder2.getId()).isNotNull();

	folderRepository.saveAndFlush(folderBuilder.withFolder("child11", "child11").withParent(folder1).build());
	folderRepository.saveAndFlush(folderBuilder.withFolder("child12", "child12").withParent(folder1).build());
	folderRepository.saveAndFlush(folderBuilder.withFolder("child21", "child21").withParent(folder2).build());
	folderRepository.saveAndFlush(folderBuilder.withFolder("child22", "child22").withParent(folder2).build());

//	When
	int count = folderInfoRepository.countByparentFolderChain("-1");
	folderParentChainPopulator.init();
	List<Folder> folders = folderRepository.findAll();

//	Then
	assertThat(count).isEqualTo(6);

	for (Folder folder : folders) {
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
    public void testFolderParentChainMultiple() {
//	Given
	Folder folder1 = folderBuilder.withFolder("Parent1", "Parent1").build();

	folder1 = folderRepository.saveAndFlush(folder1);
	Folder folder2 = folderRepository
		.saveAndFlush(folderBuilder.withFolder("Child1", "Child1").withParent(folder1).build());
	Folder folder3 = folderRepository
		.saveAndFlush(folderBuilder.withFolder("Child2", "Child2").withParent(folder2).build());

	assertThat(folder1.getId()).isNotNull();
	assertThat(folder2.getId()).isNotNull();
	assertThat(folder3.getId()).isNotNull();


//	When
	int count = folderInfoRepository.countByparentFolderChain("-1");
	folderParentChainPopulator.init();
	Folder foldersExpected1 = folderRepository.findUniqueByID(folder1.getId());
	Folder foldersExpected2 = folderRepository.findUniqueByID(folder2.getId());
	Folder foldersExpected3 = folderRepository.findUniqueByID(folder3.getId());

//	Then
	assertThat(count).isEqualTo(3);
	assertThat(foldersExpected1.getParentFolderChain()).isEqualTo("0");
	assertThat(foldersExpected2.getParentFolderChain()).isEqualTo("0_" + folder1.getId());
	assertThat(foldersExpected3.getParentFolderChain()).isEqualTo("0_" + folder1.getId() + "_" + folder2.getId());

    }

}
