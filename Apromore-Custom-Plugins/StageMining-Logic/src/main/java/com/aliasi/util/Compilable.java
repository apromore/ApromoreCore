/*
 * LingPipe v. 4.1.0
 * Copyright (C) 2003-2011 Alias-i
 *
 * This program is licensed under the Alias-i Royalty Free License
 * Version 1 WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Alias-i
 * Royalty Free License Version 1 for more details.
 * 
 * You should have received a copy of the Alias-i Royalty Free License
 * Version 1 along with this program; if not, visit
 * http://alias-i.com/lingpipe/licenses/lingpipe-license-1.txt or contact
 * Alias-i, Inc. at 181 North 11th Street, Suite 401, Brooklyn, NY 11211,
 * +1 (718) 290-9170.
 */

package com.aliasi.util;

import java.io.IOException;
import java.io.ObjectOutput;

/**
 * The <code>Compilable</code> interface specifies a general way in
 * which an object may be compiled to an object output.  The class of
 * the object read from the corresponding object input is determined
 * by the implementation and not usually the same as the compilable's
 * class.
 *
 * <P>The class {@link AbstractExternalizable} provides a helper class
 * for implementing compilable objects.  It also contains the static
 * utility method {@link AbstractExternalizable#compile(Compilable)},
 * which provides an in-memory compilation of an object, as well as
 * static methods to read and write from files.
 *
 * @author  Bob Carpenter
 * @version 2.0
 * @since   LingPipe2.0
 */
public interface Compilable {

    /**
     * Compile this object to the specified object output. 
     *
     * @param objOut Object output to which this object is compiled.
     * @throws IOException If there is an I/O error compiling the
     * object.
     */
    public void compileTo(ObjectOutput objOut) throws IOException;

}
