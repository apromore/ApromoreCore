package org.apromore.dao.jpa.folder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apromore.config.BaseTestClass;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.model.Folder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FolderManagementChainUnitTest extends BaseTestClass {

    @Autowired
    FolderRepository folderRepo;

    @Test
    public void testGetFolderChain() {
//	Given
	Folder folder1 = new Folder();
	folder1.setName("folder1");
	folder1.setParentFolderChain("0_1");

	Folder folder2 = new Folder();
	folder2.setName("folder2");
	folder2.setParentFolderChain("0_1_2");

	Folder folder3 = new Folder();
	folder2.setName("folder3");
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
    public void testGetFolderChain1() {
//	Given
	Folder folder1 = new Folder();
	folder1.setName("folder1");
	folder1.setParentFolderChain("0_1");

	Folder folder2 = new Folder();
	folder2.setName("folder2");
	folder2.setParentFolderChain("0_1_2");

	Folder folder3 = new Folder();
	folder2.setName("folder3");
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

}
