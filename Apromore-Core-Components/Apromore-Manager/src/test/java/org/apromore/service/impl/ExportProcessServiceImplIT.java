/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.service.impl;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.service.ProcessService;
import org.apromore.util.StreamUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@Disabled
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@ExtendWith(SpringExtension.class)
@Transactional
@Rollback
@TestExecutionListeners(value = DependencyInjectionTestExecutionListener.class)
class ExportProcessServiceImplIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportProcessServiceImplIT.class);

    @Inject
    private ProcessService pSrv;

    private String epmlNativeType = "EPML 2.0";
    private String username = "james";
    private Version version = new Version(1, 0);
    private String domain = "Tests";
    private String created = "12/12/2011";
    private String lastUpdate = "12/12/2011";


    @Test
    void testExportProcessWithSingleEdgeInEPML() throws Exception {
        String name = "Test EPML 1";

        DataHandler startStream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/test1.epml"), "text/xml"));
        ProcessModelVersion pst = pSrv.importProcess(username, 0, name, version, epmlNativeType, startStream.getInputStream(), domain, "", created, lastUpdate, true);

        ExportFormatResultType result = pSrv.exportProcess(name, pst.getId(), pst.getProcessBranch().getBranchName(),
                version, epmlNativeType, "");
        DataHandler endStream = result.getNative();
        LOGGER.debug(StreamUtil.convertStreamToString(endStream));
    }

}
