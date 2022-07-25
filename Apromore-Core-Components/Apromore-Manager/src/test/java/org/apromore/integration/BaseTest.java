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
package org.apromore.integration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Storage;
import org.apromore.dao.model.User;
import org.apromore.integration.config.TestConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes  = { TestConfig.class})
public abstract class BaseTest {

    public Log createLog(User user, Folder folder, Storage storage) {
        Log log = new Log();
        log.setName("LogName");
        log.setDomain("Domain");
        log.setRanking("Ranking");
        log.setFilePath("FileTimestamp");
        log.setUser(user);
        log.setFolder(folder);
        log.setStorage(storage);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String now = dateFormat.format(new Date());
        log.setCreateDate(now);
        return log;
    }

}
