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

package org.apromore.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apromore.storage.exception.ObjectCreationException;
import org.apromore.storage.exception.ObjectNotFoundException;

@Slf4j
public class FileStorageClient implements StorageClient {

    String baseStorage;

    public FileStorageClient(String baseStorage) {
        this.baseStorage = baseStorage;
    }

    @Override
    public InputStream getInputStream(String prefix, String key) throws ObjectNotFoundException {
        try {
            prefix = getValidPrefix(prefix);
            return new FileInputStream(Paths.get(baseStorage, prefix, key).toFile());
        } catch (FileNotFoundException e) {
            throw new ObjectNotFoundException("File Not Found");
        }
    }

    @Override
    public String getStorageType() {
        return StorageType.FILE.toString();
    }

    @Override
    public OutputStream getOutputStream(String prefix, String key) throws ObjectCreationException {
        prefix = getValidPrefix(prefix);
        String fullPath = Paths.get(baseStorage, prefix, key).toString();
        File file = new File(fullPath);
        file.getParentFile().mkdirs();
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file);
        } catch (IOException e) {
            throw new ObjectCreationException("File cannot be created");
        }
        return outputStream;
    }

    @Override
    public boolean delete(String prefix, String key) {
        prefix = getValidPrefix(prefix);
        String name = Paths.get(baseStorage, prefix, key).toString();
        File file = new File(name);
        return file.delete();
    }

    @Override
    public boolean delete(String prefix) throws IllegalAccessException {
        File file = new File(prefix);
        try {
            FileUtils.deleteDirectory(file);
            return file.delete();
        } catch (IOException e) {
            throw new IllegalAccessException("Cant delete a file");
        }
    }

    @Override
    public List<String> listObjects(String prefix) {
        File directory = new File(prefix);
        File[] files = directory.listFiles(File::isFile);
        List<String> fileList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                fileList.add(file.getAbsolutePath());
            }
        }

        return fileList;
    }

    @Override
    public boolean isFileExists(String prefix, String key) {
        String fullPath = Paths.get(baseStorage, getValidPrefix(prefix), key).toString();
        File file = new File(fullPath);
        return file.exists();
    }
}
