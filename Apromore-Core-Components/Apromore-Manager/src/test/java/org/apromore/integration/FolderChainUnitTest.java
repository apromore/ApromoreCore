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
