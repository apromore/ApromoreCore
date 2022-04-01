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

package org.apromore.dao.jpa.process;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.apromore.config.BaseTestClass;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.jpa.folder.FolderBuilder;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Process;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Transactional
class ProcessManagementUnitTest extends BaseTestClass {

    FolderBuilder folderBuilder;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    ProcessRepository processRepository;

    @BeforeEach
    void setUp() {
        folderBuilder = new FolderBuilder();
    }

    @Test
    void testSaveProcess() {
        //	Given

        Folder folder1 = folderBuilder.withFolder("Parent1", "Parent1").build();
        folder1 = folderRepository.saveAndFlush(folder1);

        Process process1 = new Process();
        process1.setName("test1");
        process1.setFolder(folder1);
        process1 = processRepository.saveAndFlush(process1);

        Process process2 = new Process();
        process2.setName("test2");
        process2.setFolder(folder1);
        process2 = processRepository.saveAndFlush(process2);

        Folder folder2 = folderBuilder.withFolder("Parent2", "Parent1").build();
        folder2 = folderRepository.saveAndFlush(folder2);

        Process process3 = new Process();
        process3.setName("test3");
        process3.setFolder(folder2);
        process3 = processRepository.saveAndFlush(process3);

        //	When
        List<Process> processes = processRepository.findByFolderIdIn(Arrays.asList(folder1.getId()));

        //	Then
        assertThat(processes).hasSize(2);
        assertThat(processes).contains(process1, process2);

    }

    @Test
    void testSaveProcess1() {
        //	Given

        Folder folder1 = folderBuilder.withFolder("Parent1", "Parent1").build();
        folder1 = folderRepository.saveAndFlush(folder1);

        Process process1 = new Process();
        process1.setName("test1");
        process1.setFolder(folder1);
        process1 = processRepository.saveAndFlush(process1);

        Process process2 = new Process();
        process2.setName("test2");
        process2.setFolder(folder1);
        process2 = processRepository.saveAndFlush(process2);

        Folder folder2 = folderBuilder.withFolder("Parent2", "Parent2").build();
        folder2 = folderRepository.saveAndFlush(folder2);

        Process process3 = new Process();
        process3.setName("test3");
        process3.setFolder(folder2);
        process3 = processRepository.saveAndFlush(process3);

        //	When
        List<Process> processes = processRepository.findByFolderIdIn(Arrays.asList(folder1.getId(), folder2.getId()));

        //	Then
        assertThat(processes).hasSize(3);
        assertThat(processes).contains(process1, process2, process3);

    }

}
