/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import java.io.InputStream;
import java.io.OutputStream;

import org.apromore.storage.exception.ObjectCreationException;
import org.apromore.storage.exception.ObjectNotFoundException;

public interface StorageClient {
    
    String getStorageType();
    
    InputStream getInputStream(String prefix,String key) throws ObjectNotFoundException;

    OutputStream getOutputStream(String prefix,String key) throws ObjectCreationException;

    boolean delete(String prefix, String key);
    
    default String getValidPrefix(String prefix) {
   	prefix = prefix == null ? "" : prefix;
   	return prefix;
       }
    
   

}
