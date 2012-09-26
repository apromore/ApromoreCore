/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See: http://www.gnu.org/licenses/lgpl-3.0
 * 
 */
package de.hbrs.oryx.yawl.converter.exceptions;

public class NoSubnetFoundException extends ConversionException {

    private static final long serialVersionUID = 687413923047557816L;

    public NoSubnetFoundException() {
        super();
    }

    public NoSubnetFoundException(final String arg0, final Throwable arg1) {
        super(arg0, arg1);
    }

    public NoSubnetFoundException(final String arg0) {
        super(arg0);
    }

    public NoSubnetFoundException(final Throwable arg0) {
        super(arg0);
    }

}
