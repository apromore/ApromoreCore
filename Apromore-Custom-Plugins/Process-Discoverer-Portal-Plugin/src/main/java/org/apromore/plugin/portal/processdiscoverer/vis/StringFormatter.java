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

package org.apromore.plugin.portal.processdiscoverer.vis;

public class StringFormatter {
    private static final int MINLEN = 2;
    private static final int MAXLEN = 60;
    private static final int MAXWORDLEN = 15;
    private static final int MAXHALFWIDTHLINELEN = 19;
    private static final int MAXLINES = 3;
    private static final String DEFAULT = "*";
    private static final String REPLACE_REGEXP = "[\\-_]";
    private static final String ELLIPSIS = "...";

    public String escapeChars(String value) {
        return value.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
    }

    public String fixCutName(String name, int len) {
        name = name.replaceAll(REPLACE_REGEXP, " ");

        if (len <= 0) {
            len = MAXLEN;
        }
        if (name.length() > len) {
            name = name.substring(0, len);
            name += "...";
        }
        return name;
    }

    public String shortenName(String originalName, int len) {
        String name = originalName;

        if (name == null || name.length() == 0) {
            return DEFAULT;
        }
        if (name.length() <= MINLEN) {
            return name;
        }
        if (len <= 0) {
            len = MAXLEN;
        }
        try {
            name = name.replaceAll(REPLACE_REGEXP, " ");
            String[] parts = name.split(" ");
            if (parts.length >= 2) {
                int toCheck = parts.length - 1; // first ... last
                // int toCheck = 1; // first second ...
                if (parts[0].length() > MAXWORDLEN || parts[toCheck].length() > MAXWORDLEN) {
                    name = parts[0].substring(0, Math.min(MAXWORDLEN, parts[0].length()));
                    return name + "...";
                } else if (parts.length > 2) {
                    if (name.length() > len) {
                        name = parts[0] + " ... " + parts[toCheck]; // first ... last
                        // name = parts[0] + " " + parts[toCheck] + "..."; // first second ...
                        if (name.length() > len) {
                            return name.substring(0, len) + "...";
                        }
                        return name;
                    } else {
                        return name;
                    }
                }
            } else if (parts[0].length() > MAXWORDLEN) {
                name = parts[0].substring(0, Math.min(MAXWORDLEN, parts[0].length()));
                return name + "...";
            }
        } catch (Error e) {
            name = originalName;
        }
        return name;
    }

    /**
     * Break the name into a number of lines at spaces or in the middle of long words.
     * If the formatted name were to overflow past the maximum allowed lines,
     * replace the overflow with ellipsis.
     * @param originalName the original name, unformatted.
     * @param maxLines The maximum number of lines to break the name into.
     * @return the name formatted into a number of lines.
     */
    public String wrapName(String originalName, int maxLines) {
        String name = originalName;

        if (name == null || name.length() == 0) {
            return DEFAULT;
        }
        if (name.length() <= MINLEN) {
            return name;
        }
        if (maxLines <= 0) {
            maxLines = MAXLINES;
        }

        //Full width characters need more space so less chars per line
        int maxLineLength = containsFullWidthCharacter(name) ? MAXHALFWIDTHLINELEN / 2 : MAXHALFWIDTHLINELEN;

        try {
            name = name.replaceAll(REPLACE_REGEXP, " ");
            String[] parts = name.split(" ");
            StringBuilder formattedName = new StringBuilder();

            int lines = 0;
            int lastLineLength = 0;
            for (String part : parts) {
                if (part.length() > maxLineLength) {

                    //Split the word into chunks of size maxLineLength and add each chunk to a new line
                    for (int i = 0; i <= part.length() / maxLineLength; i++) {
                        String chunk = part.substring(i * maxLineLength,
                            Math.min((i + 1) * maxLineLength, part.length()));

                        //Add the chunk to a new line if the current line already has text
                        if (lastLineLength > 0) {
                            if (++lines < maxLines) {
                                formattedName.append("\\n");
                            } else {
                                formattedName.replace(formattedName.length() - 3, formattedName.length(), ELLIPSIS);
                                return formattedName.toString();
                            }
                        }

                        formattedName.append(chunk);
                        lastLineLength = chunk.length();
                    }

                } else if (lastLineLength + part.length() > maxLineLength) {

                    if (++lines < maxLines) {
                        //Add the word to a new line
                        formattedName.append("\\n");
                        formattedName.append(part);
                        lastLineLength = part.length();
                    } else {
                        //If we still have some space on the last line,
                        //add some characters of the last word followed by ellipsis
                        if (maxLineLength > lastLineLength + 1) {
                            formattedName.append(" ");
                            formattedName.append(part, 0, Math.min(maxLineLength - lastLineLength - 1, part.length()));
                        }
                        formattedName.replace(formattedName.length() - 3, formattedName.length(), ELLIPSIS);
                        return formattedName.toString();
                    }

                } else {
                    //Add the word to the same line
                    if (lastLineLength != 0) {
                        formattedName.append(" ");
                        lastLineLength++;
                    }
                    formattedName.append(part);
                    lastLineLength += part.length();
                }

            }
            return formattedName.toString();
        } catch (Exception e) {
            name = originalName;
        }
        return name;
    }

    /**
     * Check if a string contains a full width character.
     * @param string the string to check for full width characters in.
     * @return true if the string contains at least one full width character.
     */
    private boolean containsFullWidthCharacter(String string) {
        return string != null && string.chars().anyMatch(c -> !isHalfWidth((char) c));
    }

    /**
     * Check if the character is a half width character.
     * @param c the character to check the size of.
     * @return true if c is a half-width character, false if c is a full-width character.
     */
    private boolean isHalfWidth(char c) {
        return c <= '\u00FF'
            || '\uFF61' <= c && c <= '\uFFDC'
            || '\uFFE8' <= c && c <= '\uFFEE';
    }
}
