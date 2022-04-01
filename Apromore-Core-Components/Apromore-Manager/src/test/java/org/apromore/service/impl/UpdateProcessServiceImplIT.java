/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.common.Constants;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.portal.helper.Version;
import org.apromore.service.FormatService;
import org.apromore.service.ProcessService;
import org.apromore.service.SecurityService;
import org.apromore.service.model.ProcessData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import org.springframework.transaction.annotation.Transactional;

/**
 * Unit test the ProcessService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Disabled
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@ExtendWith(SpringExtension.class)
@Transactional
@Rollback
@TestExecutionListeners(value = DependencyInjectionTestExecutionListener.class)
class UpdateProcessServiceImplIT {

    @Inject
    private ProcessService pSrv;
    @Inject
    private FormatService fSrv;
    @Inject
    private SecurityService sSrv;


    @Test
    @Rollback(true)
    @Disabled
    void testImportUpdateThenDeleteModel() throws Exception {
        String natType = "EPML 2.0";
        String name = "AudioTest2";
        String branch = "MAIN";
        Version initialVersion = new Version(1,0);
        Version updatedVersion = new Version(1,1);

        NativeType nativeType = fSrv.findNativeType(natType);

        // Insert Process
        DataHandler stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/Audio.epml"), "text/xml"));
        //CanonisedProcess cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>(0));
        String username = "james";
        String domain = "Tests";
        String lastUpdate = "12/12/2011";
        String created = "12/12/2011";
        ProcessModelVersion pst = pSrv.importProcess(username, 0, name, initialVersion, natType, stream.getInputStream(), domain, "", created, lastUpdate, true);
        assertThat(pst, notNullValue());

        // Update process
        stream = new DataHandler(new ByteArrayDataSource(ClassLoader.getSystemResourceAsStream("EPML_models/Audio.epml"), "text/xml"));
        //cp = cSrv.canonise(natType, stream.getInputStream(), new HashSet<RequestParameterType<?>>(0));
        User user = sSrv.getUserByName("james");
        pSrv.createProcessModelVersion(pst.getId(), "testBranch", updatedVersion, initialVersion, user, Constants.LOCKED, nativeType, stream.getInputStream());

        // Delete Process
        List<ProcessData> deleteList = new ArrayList<>();
        deleteList.add(new ProcessData(pst.getId(), updatedVersion));
        pSrv.deleteProcessModel(deleteList, user);
    }

}
