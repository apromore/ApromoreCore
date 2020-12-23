/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.dao.jpa.log;

import org.apromore.config.BaseTestClass;
import org.apromore.dao.LogRepository;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Storage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

public class LogManagementUnitTest extends BaseTestClass {
    
    @Autowired
    LogRepository logRepository;
    
    
    @Test
    public void testSaveLogWithStorage()
    {
	Log log=new Log();
	log.setDomain("testDomain");
	log.setName("testName");
	log.setFilePath("testFilePath");
	
	Storage logStorage= new Storage();
	logStorage.setPrefix("log");
	logStorage.setKey("20201222183534890_CallcenterExample.xes.gz");
	logStorage.setStoragePath("S3::nolan-testdata-bucket-sydney::ap-southeast-2::https://s3.ap-southeast-2.amazonaws.com");
	log.setStorage(logStorage);
	log=logRepository.saveAndFlush(log);
	
	assertThat(log.getId()).isNotNull();
	assertThat(log.getStorage().getId()).isNotNull();
	
	
    }

}
