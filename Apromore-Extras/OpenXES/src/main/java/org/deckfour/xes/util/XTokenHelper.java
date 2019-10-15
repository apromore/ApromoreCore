/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2012 Christian W. Guenther (christian@fluxicon.com)
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
 * christian@fluxicon.com
 * 
 */
package org.deckfour.xes.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Christian W. Guenther (christian@fluxicon.com)
 *
 */
public class XTokenHelper {
	
	public static String formatTokenString(List<String> tokens) {
		if(tokens.size() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append(formatToken(tokens.get(0)));
			for(int i=1; i<tokens.size(); i++) {
				sb.append(' ');
				sb.append(formatToken(tokens.get(i)));
			}
			return sb.toString();
		} else {
			return "";
		}
	}
	
	private static String formatToken(String token) {
		token = token.trim();
		if(token.indexOf(' ') >= 0 || token.indexOf('\t') >= 0) {
			// needs escaping
			StringBuffer sb = new StringBuffer();
			token = token.replaceAll("'", "\\\'");
			sb.append('\'');
			sb.append(token);
			sb.append('\'');
			return sb.toString();
		} else {
			return token;
		}
	}
	
	public static List<String> extractTokens(String tokenString) {
		List<String> tokens = new ArrayList<String>();
		boolean isEscaped = false;
		boolean isQuoted = false;
		StringBuffer sb = new StringBuffer();
		for(char c : tokenString.toCharArray()) {
			if(c == ' ' && !isQuoted && !isEscaped) {
				// separator, record value
				String token = sb.toString().trim();
				if(token.length() > 0) {
					tokens.add(token);
				}
				// initialize token buffer
				sb = new StringBuffer();
			} else if(c == '\\') {
				isEscaped = true;
			} else if(c == '\'') {
				if(!isEscaped) {
					isQuoted = !isQuoted;
				} else {
					sb.append(c);
				}
				isEscaped = false;
			} else {
				sb.append(c);
				isEscaped = false;
			}
		}
		// add last token
		String token = sb.toString().trim();
		if(token.length() > 0) {
			tokens.add(token);
		}
		return Collections.unmodifiableList(tokens);
	}

}
