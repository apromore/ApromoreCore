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
package org.apromore.dao.jpa.folder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apromore.config.BaseTestClass;
import org.apromore.dao.FolderInfoRepository;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.FolderInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FolderManagementUnitTest extends BaseTestClass {
    
    @Autowired
    FolderRepository folderRepo;
    
    @Autowired
    FolderInfoRepository folderInfoRepo;
    
    @Test
    public void testCountFolderWith2Folders()
    {
//	Given
	Folder folder1 = new Folder();
	folder1.setName("folder1");

	Folder folder2 = new Folder();
	folder2.setName("folder2");

	folder1 = folderRepo.saveAndFlush(folder1);
	folder2 = folderRepo.saveAndFlush(folder2);
	assertThat(folder1.getId()).isNotNull();
	assertThat(folder2.getId()).isNotNull();
	

//	When
	int count = folderInfoRepo.countByparentFolderChain("-1");

//	Then
	assertThat(count).isEqualTo(2);
    }

    @Test
    public void testCountFolderWith2FoldersWithChain() {
//	Given
	Folder folder1 = new Folder();
	folder1.setName("folder1");

	Folder folder2 = new Folder();
	folder2.setName("folder2");
	folder2.setParentFolderChain("0");

	folder1 = folderRepo.saveAndFlush(folder1);
	folder2 = folderRepo.saveAndFlush(folder2);
	assertThat(folder1.getId()).isNotNull();
	assertThat(folder2.getId()).isNotNull();

//	When
	int count = folderInfoRepo.countByparentFolderChain("-1");

//	Then
	assertThat(count).isEqualTo(1);
    }

    @Test
    public void testCountFolderWith2FoldersWithChain1() {
//	Given
	Folder folder1 = new Folder();
	folder1.setName("folder1");

	Folder folder2 = new Folder();
	folder2.setName("folder2");
	folder2.setParentFolderChain("0");

	folder1 = folderRepo.saveAndFlush(folder1);
	folder2 = folderRepo.saveAndFlush(folder2);
	assertThat(folder1.getId()).isNotNull();
	assertThat(folder2.getId()).isNotNull();

//	When
	List<FolderInfo> folders = folderInfoRepo.findByParentIdNullOr0();

//	Then
	assertThat(folders.size()).isEqualTo(2);
    }

    @Test
    public void testFindFoldersByRootParent() {
//	Given
	Folder folder1 = new Folder();
	folder1.setName("folder1");

	folder1 = folderRepo.saveAndFlush(folder1);

	Folder folder2 = new Folder();
	folder2.setName("folder2");
	folder2.setParentFolderChain("0" + "_" + folder1.getId());
	folder2.setParentFolder(folder1);


	folder2 = folderRepo.saveAndFlush(folder2);
	assertThat(folder1.getId()).isNotNull();
	assertThat(folder2.getId()).isNotNull();

//	When
	List<FolderInfo> folders = folderInfoRepo.findByParentIdNullOr0();

//	Then
	assertThat(folders.size()).isEqualTo(1);
    }

}
