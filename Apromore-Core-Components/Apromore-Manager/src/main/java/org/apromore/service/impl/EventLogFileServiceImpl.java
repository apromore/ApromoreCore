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
package org.apromore.service.impl;

import org.apache.commons.io.FileUtils;
import org.apromore.commons.config.ConfigBean;
import org.apromore.service.EventLogFileService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class EventLogFileServiceImpl implements EventLogFileService {
    private String directoryPath;

    @Inject
    public EventLogFileServiceImpl(final ConfigBean configBean) {
        this.directoryPath = configBean.getLogsDir();
    }


    // Only work for "xes.gz" extension
    @Override
    public void copyFile(String sourceFileName, String targetFileName) throws Exception {
        File currentFile = new File(directoryPath + "/" + sourceFileName);
        File newFile = new File(directoryPath + "/" + targetFileName);
        FileUtils.copyFile(currentFile, newFile);
    }

    @Override
    public void deleteFileIfExist(String fileFullName) throws Exception {
        FileUtils.deleteQuietly(new File(fileFullName));
    }

    @Override
    public void copyFile(InputStream sourceFile, OutputStream targetFile) throws Exception {
        byte[] buf = new byte[8192];
        int length;
        while ((length = sourceFile.read(buf)) > 0) {
            targetFile.write(buf, 0, length);
        }

        sourceFile.close();
        targetFile.close();

    }
}
