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
package org.apromore.script;

import java.util.ArrayList;
import java.util.List;

import org.apromore.dao.FolderInfoRepository;
import org.apromore.dao.model.FolderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FolderParentChainPopulator {

    FolderInfoRepository folderInfoRepository;

    private static final Logger logger = LoggerFactory.getLogger(FolderParentChainPopulator.class);

    public FolderParentChainPopulator() {
	super();
    }

    public FolderInfoRepository getFolderInfoRepository() {
	return folderInfoRepository;
    }

    public void setFolderInfoRepository(FolderInfoRepository folderInfoRepository) {
	this.folderInfoRepository = folderInfoRepository;
    }

    public void init() {

	int count = folderInfoRepository.countByparentFolderChain("-1");
	logger.info("Number of folders with -1 parentchain =" + count);
	if (count != 0) {
	    List<FolderInfo> folders = folderInfoRepository.findByParentIdNullOr0();
	    updateParentChain(folders);
	}
//	add assertt to check count

    }

    private void updateParentChain(List<FolderInfo> folders) {

	List<FolderInfo> folderInfos = new ArrayList<FolderInfo>();

	for (FolderInfo folderInfo : folders) {
	    if (folderInfo.getParentFolderInfo() == null) {
		folderInfo.setParentFolderChain("0");
	    } else {
		folderInfo.setParentFolderChain(
			folderInfo.getParentFolderInfo().getParentFolderChain() + "_"
				+ folderInfo.getParentFolderInfo().getId());
	    }
	    folderInfo = folderInfoRepository.saveAndFlush(folderInfo);
	    folderInfos.addAll(folderInfo.getSubFolders());
	}

	if (!folderInfos.isEmpty()) {
	    logger.info("Updating next generation with size =" + folderInfos.size());
	    updateParentChain(folderInfos);
	}

    }

}
