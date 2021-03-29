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
@EnableJpaRepositories(basePackages = "org.apromore.dao", repositoryImplementationPostfix = "CustomImpl")
@EntityScan(basePackages = "org.apromore.dao.model")
@EnableTransactionManagement
public class DatabaseConfig {

	@Autowired
	FolderInfoRepository folderInfoRepository;
	
	@Bean(initMethod = "init")
	public FolderParentChainPopulator folderParentChainPopulator() {
		FolderParentChainPopulator folderParentChainPopulator=new  FolderParentChainPopulator();
		
		folderParentChainPopulator.setFolderInfoRepository(folderInfoRepository);
		return folderParentChainPopulator;
	}

}
