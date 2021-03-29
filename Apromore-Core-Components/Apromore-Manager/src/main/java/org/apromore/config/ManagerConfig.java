package org.apromore.config;

import java.util.Properties;

import org.apromore.apmlog.APMLogService;
import org.apromore.cache.ehcache.CacheRepository;
import org.apromore.commons.config.ConfigBean;
import org.apromore.service.impl.TemporaryCacheService;
import org.apromore.storage.StorageClient;
import org.apromore.storage.factory.StorageManagementFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "org.apromore.security", "org.apromore.service", "org.apromore.toolbox",
		"org.apromore.manager" })
@EnableConfigurationProperties(ConfigBean.class)
public class ManagerConfig {

	@Value("${logs.dir}")
	String logDir;

	@Value("${cache.numOfEvent}")
	String numOfEvent;

	@Value("${cache.numOfTrace}")
	String numOfTrace;

	@Value("${storage.path}")
	String storagePath;

	@Autowired
	public ConfigBean config;
	

	@Bean
	public TemporaryCacheService tempCacheService(@Autowired APMLogService apmLogService,
			@Autowired StorageManagementFactory<StorageClient> storageManagementFactory,
			@Autowired CacheRepository cacheRepository) {
		TemporaryCacheService tempCacheService = new TemporaryCacheService();
		tempCacheService.setApmLogService(apmLogService);
		tempCacheService.setStorageFactory(storageManagementFactory);
		tempCacheService.setCacheRepo(cacheRepository);
		tempCacheService.setConfig(config);
		return tempCacheService;
	}

	@Bean
	public JavaMailSenderImpl mailSender(@Value("${mail.host}") String serverHost,
			@Value("${mail.port}") Integer serverport, @Value("${mail.password}") String serverpassword,
			@Value("${mail.username}") String serverUsername) {

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setDefaultEncoding("UTF-8");
		mailSender.setHost(serverHost);
		mailSender.setPort(serverport);
		mailSender.setPassword(serverpassword);
		mailSender.setUsername(serverUsername);

		Properties properties = new Properties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		mailSender.setJavaMailProperties(properties);
		return mailSender;
	}

}
