/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.apql.Apql.controller;

// Java 2 Standard Edition packages
import java.awt.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

// Third party packages
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

// Local classes
import com.apql.Apql.QueryText;
import com.apql.Apql.highlight.Keywords;
import com.apql.Apql.popup.FolderLabel;
import com.apql.Apql.popup.KeywordLabel;
import com.apql.Apql.popup.PopupPanel;

/**
 * Test suite for {@link ContextController}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Ignore("Test disabled until it can be configured via OSGi")
public class ContextControllerUnitTest {

    private final QueryText queryText = new QueryText();
    private final ContextController contextController = ContextController.getContextController();

    @Before public void setup() {
        contextController.setProcessLabels(Arrays.asList("foo", "bar", "baz", "quux"));
        QueryController.getQueryController().settextPane(queryText);
    }

    /**
     * The "WHERE" keyword ought to appear as a suggestion.
     */
    @Test public void testFindContext_Empty() {
        testFindContext("", "", Arrays.asList("SELECT"));
    }

    @Test public void testFindContext_NeitherSelectFromNorWhere() {
        testFindContext("", "random", Arrays.asList("SELECT"));
        testFindContext("ran", "dom", Arrays.asList(""));
    }

    @Test public void testFindContext_SelectWithWord() {
        testFindContext("SELECT *", "", Arrays.asList("*"));
        testFindContext("SELECT f", "", Arrays.asList("FROM"));
        testFindContext("SELECT nimrod", "", Arrays.asList("name"));
    }

    @Test public void testFindContext_EmptySelectClause() {
        testFindContext("SELECT ", "*", Arrays.asList("*", "domain", "id", "language", "owner", "name", "ranking", "FROM"));
    }

    @Test public void testFindContext_FromWithWord_HomeSlash() {
        testFindContext("SELECT * FROM \"Home/", "\"", null);
    }

    @Test public void testFindContext_FromWithWord_NoHome() {
        testFindContext("SELECT * FROM \"", "", Arrays.asList("Home"));
        testFindContext("SELECT * FROM \"Home", "", Arrays.asList("Home"));
    }

    @Test public void testFindContext_FromWithWord_IdNetsNode() {
        testFindContext("SELECT * FROM foo", "", Collections.<String>emptyList());
    }

    @Test public void testFindContext_FromWithWord_Home() {
        testFindContext("SELECT * FROM \"Hx", "", null);
    }

    @Test public void testFindContext_FromWithWord_NoTokens() {
        testFindContext("SELECT * FROM foo", "", Collections.<String>emptyList());
    }

    @Test public void testFindContext_EmptyFromClause() {
        testFindContext("SELECT * FROM ", "foo", Arrays.asList("Home", "WHERE"));
    }

    @Test public void testFindContext_WhereContext_WithWord() {
        testFindContext("SELECT * FROM * WHERE foo", "", Arrays.asList((String) null));
        testFindContext("SELECT * FROM * WHERE f", "oo", Arrays.asList("FALSE", "FROM"));
    }

    @Test public void testFindContext_WhereContext_WithSpace() {
        testFindContext("SELECT * FROM * WHERE 123", "45", Arrays.asList((String) null));
    }

    @Test public void testFindContext_WhereContext_WithQuotes() {
        testFindContext("SELECT * FROM * WHERE function(\"", "foo\")", Arrays.asList("bar", "baz", "foo", "quux"));
        testFindContext("SELECT * FROM * WHERE CanOccur(\"", "foo\")", Arrays.asList("bar", "baz", "foo", "quux"));
    }

    @Test public void testFindContext_WhereContext_Without() {
        testFindContext("SELECT * FROM * WHERE sqrt(", "9)", Arrays.asList((String) null));
    }

    @Test public void testFindContext_EmptyWhereClause() {
        testFindContext("SELECT * FROM * WHERE ", "", Arrays.asList("ALL", "AND", "ANY", "AlwaysOccurs", "CanConflict", "CanCooccur", "CanOccur", "EACH", "EQUALS", "EXCEPT", "FALSE", "GetTasks", "IN", "INTERSECT", "IS", "NOT", "OF", "OR", "OVERLAPS", "PROPER", "SUBSET", "TRUE", "TotalCausal", "UNION", "UNIVERSE", "UNKNOWN", "WITH"));
    }

    @Test public void testFindContext_Else() {
        testFindContext("FROM foo", "", Arrays.asList((String) null));
        testFindContext("FROM un", "", Arrays.asList("UNION", "UNIVERSE", "UNKNOWN"));
    }

    @Test public void testFindProcess() {
        assertEquals("", contextController.findProcess("", 0));
        assertEquals("pql/1.p", contextController.findProcess("\"pql/1.pnml\" \"pql/2.pnml\"", 8));
        assertEquals("pql/1.pnml", contextController.findProcess("\"pql/1.pnml\" \"pql/2.pnml\"", 11));
        assertEquals("", contextController.findProcess("\"pql/1.pnml\" \"pql/2.pnml\"", 12));
        assertEquals("pql/2.p", contextController.findProcess("\"pql/1.pnml\" \"pql/2.pnml\"", 21));
        assertEquals("foo ba", contextController.findProcess("\"foo bar\" \"baz quux\"", 7));
        assertEquals("foo bar", contextController.findProcess("\"foo bar\" \"baz quux\"", 8));
        assertEquals("", contextController.findProcess("\"foo bar\" \"baz quux\"", 9));
        assertEquals("", contextController.findProcess("\"foo bar\" \"baz quux\"", 10));
        assertEquals("", contextController.findProcess("\"foo bar\" \"baz quux\"", 11));
        assertEquals("b", contextController.findProcess("\"foo bar\" \"baz quux\"", 12));
        assertEquals("baz qu", contextController.findProcess("\"foo bar\" \"baz quux\"", 17));
        assertEquals("ba", contextController.findProcess("\"foo\"bar\"quux\"", 7));
        assertEquals(",", contextController.findProcess("\"foo\", \"quux\"", 6));
    }

    @Test public void testFindWord() {
        assertEquals("", contextController.findWord("foo bar", 0));
        assertEquals("f", contextController.findWord("foo bar", 1));
        assertEquals("fo", contextController.findWord("foo bar", 2));
        assertEquals("foo", contextController.findWord("foo bar", 3));
        assertEquals("", contextController.findWord("foo bar", 4));
        assertEquals("b", contextController.findWord("foo bar", 5));
        assertEquals("ba", contextController.findWord("foo bar", 6));
        assertEquals("bar", contextController.findWord("foo bar", 7));
    }

    // Local methods

    /**
     * The generic unit test for the {@link ContextController#findContext} method.
     *
     * Passing the caret position in numerically would be more obvious, but splitting the input is less error-prone.
     *
     * @param beforeCaret  the input text before the caret
     * @param afterCaret  the input text after the caret
     * @param expectedSuggestions  the suggestion list required to pass the test
     * @throws AssertionError if {@link ContextController#findContext} doesn't return the <var>expectedSuggestions</var>
     */
    private void testFindContext(String beforeCaret, String afterCaret, List<String> expectedSuggestions) {
        queryText.setText(beforeCaret + afterCaret);
        QueryController.getQueryController().setCaretPosition(beforeCaret.length());
        assertEquals(expectedSuggestions, toSuggestionList(contextController.findContext()));
    }

    /**
     * @param context  the result of {@link ContextController#getContextController}
     * @return the suggestions displayed within the <var>context</var>, or <code>null</code> if <var>context</var> is <code>null</code>
     */
    private List<String> toSuggestionList(JScrollPane context) {

        if (context == null) {
             return null;
        }

        PopupPanel panel = (PopupPanel) context.getViewport().getView();
        List<String> result = new ArrayList<>(panel.resultsNumber());
        for (int i = 0; i < panel.resultsNumber(); i++) {
            Component c = panel.getComponent(i);
            if (c instanceof KeywordLabel) {
                KeywordLabel label = (KeywordLabel) c;
                result.add(label.getText());
            }
            else if (c instanceof FolderLabel) {
                FolderLabel label = (FolderLabel) c;
                result.add(label.getText());
            }
            else {
                throw new AssertionError("Unexpected suggestion class: " + c);
            }
        }
        return result;
    }
}
