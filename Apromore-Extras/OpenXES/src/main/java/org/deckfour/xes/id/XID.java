/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.id;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Implements a unique ID based on UUID.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XID implements Cloneable, Comparable<XID> {

	/**
	 * Parses an XID object from its text representation.
	 * 
	 * @param idString
	 *            Text representation of an XID.
	 * @return The parsed XID.
	 */
	public static XID parse(String idString) {
		UUID uuid = UUID.fromString(idString);
		return new XID(uuid);
	}

	/**
	 * Reads a binary-serialized XID from a data input stream.
	 * 
	 * @param dis
	 *            Data input stream to read XID from.
	 * @return The read XID object.
	 */
	public static XID read(DataInputStream dis) throws IOException {
		long msb = dis.readLong();
		long lsb = dis.readLong();
		return new XID(msb, lsb);
	}

	/**
	 * Reads a binary-serialized XID from a data input.
	 * 
	 * @param in
	 *            Data input to read XID from.
	 * @return The read XID object.
	 */
	public static XID read(DataInput in) throws IOException {
		long msb = in.readLong();
		long lsb = in.readLong();
		return new XID(msb, lsb);
	}

	/**
	 * Serializes an XID object binarily to a data output stream.
	 * 
	 * @param id
	 *            XID to be serialized.
	 * @param dos
	 *            Data output stream to store XID serialization.
	 */
	public static void write(XID id, DataOutputStream dos) throws IOException {
		dos.writeLong(id.uuid.getMostSignificantBits());
		dos.writeLong(id.uuid.getLeastSignificantBits());
	}

	/**
	 * Serializes an XID object binarily to a data output stream.
	 * 
	 * @param id
	 *            XID to be serialized.
	 * @param out
	 *            Data output to store XID serialization.
	 */
	public static void write(XID id, DataOutput out) throws IOException {
		out.writeLong(id.uuid.getMostSignificantBits());
		out.writeLong(id.uuid.getLeastSignificantBits());
	}

	/**
	 * UUID implementation of XID identity.
	 */
	private final UUID uuid;

	/**
	 * Creates a new XID object.
	 */
	public XID() {
		this.uuid = UUID.randomUUID();
	}

	/**
	 * Creates a new XID object.
	 * 
	 * @param msb
	 *            Most significant bits of an UUID
	 * @param lsb
	 *            Least significant bits of an UUID
	 */
	public XID(long msb, long lsb) {
		this.uuid = new UUID(msb, lsb);
	}

	/**
	 * Creates a new XID object.
	 * 
	 * @param uuid
	 *            The UUID implementing XID uniqueness.
	 */
	public XID(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Tests XID object for equality.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof XID) {
			XID other = (XID) obj;
			return uuid.equals(other.uuid);
		} else {
			return false;
		}
	}

	/**
	 * Returns the string representation of an XID instance.
	 */
	public String toString() {
		return uuid.toString().toUpperCase();
	}

	/**
	 * Creates a clone of this ID.
	 */
	public Object clone() {
		XID clone;
		try {
			clone = (XID) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			clone = null;
		}
		return clone;
	}

	/**
	 * Returns a hash code for this XID.
	 */
	public int hashCode() {
		return uuid.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(XID o) {
		return uuid.compareTo(o.uuid);
	}

}
