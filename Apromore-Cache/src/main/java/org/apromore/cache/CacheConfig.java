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
package org.apromore.cache;

import org.apromore.cache.ehcache.CacheRepository;
import org.apromore.cache.ehcache.CacheRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class CacheConfig {

	@Value("${manager.ehcache.config.url}")
	Resource managerCacheUrl;

	@Bean
	public CacheRepository cacheRepo()
	{
		CacheRepositoryImpl cacheRepositoryImpl=new CacheRepositoryImpl();
		cacheRepositoryImpl.setCacheName("xlog");	
		cacheRepositoryImpl.setEhCacheCacheManager(ehCacheCacheManager());
		return cacheRepositoryImpl;
	}
	
	@Bean
	public EhCacheCacheManager ehCacheCacheManager()
	{
		EhCacheCacheManager ehCacheCacheManager=new EhCacheCacheManager();
		ehCacheCacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
		return ehCacheCacheManager;
	}
	
	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean()
	{
		EhCacheManagerFactoryBean ehCacheManagerFactoryBean=new EhCacheManagerFactoryBean();
		ehCacheManagerFactoryBean.setConfigLocation(managerCacheUrl);	
		return ehCacheManagerFactoryBean;
	}
	
	
}

