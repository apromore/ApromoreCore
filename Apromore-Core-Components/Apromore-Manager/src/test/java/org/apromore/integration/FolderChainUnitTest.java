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

import java.util.List;

import org.apromore.builder.FolderBuilder;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.model.Folder;
import org.apromore.service.FolderService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FolderChainUnitTest extends BaseTest {

    @Autowired
    FolderRepository folderRepository;

    FolderBuilder folderBuilder;

    @Autowired
    FolderService folderService;

    @Before
    public void setUp() {
	folderBuilder = new FolderBuilder();
    }

    @Test
    public void testUpdateFolderChain() {
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

//	When
	folderService.updateFolderChainForSubFolders(folder1.getId(),
		newFolder.getParentFolderChain() + "_" + newFolder.getId() + "_" + folder1.getId());
	
	List<Folder> folders = folderRepository.findAll();

//	Then
	System.out.println("f");

    }

}
