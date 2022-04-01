/*-
 * #%L This file is part of "Apromore Core". %% Copyright (C) 2018 - 2022 Apromore Pty Ltd. %% This
 * program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>. #L%
 */
package org.apromore.dao.config;

import org.apromore.dao.FolderInfoRepository;
import org.apromore.script.FolderParentChainPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "org.apromore.dao",
    repositoryImplementationPostfix = "CustomImpl")
@EntityScan(basePackages = "org.apromore.dao.model")
@EnableTransactionManagement
public class DatabaseConfig {

  @Autowired
  FolderInfoRepository folderInfoRepository;

  @Bean(initMethod = "init")
  public FolderParentChainPopulator folderParentChainPopulator() {
    FolderParentChainPopulator folderParentChainPopulator = new FolderParentChainPopulator();

    folderParentChainPopulator.setFolderInfoRepository(folderInfoRepository);
    return folderParentChainPopulator;
  }

}
