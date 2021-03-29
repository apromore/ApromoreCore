package org.apromore.storage.config;

import org.apromore.storage.StorageClient;
import org.apromore.storage.factory.StorageManagementFactory;
import org.apromore.storage.factory.StorageManagementFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
	
	
	@Bean
	public StorageManagementFactory<StorageClient> storageManagementFactory()
	{
		return new StorageManagementFactoryImpl<StorageClient>();
		
	}

}
