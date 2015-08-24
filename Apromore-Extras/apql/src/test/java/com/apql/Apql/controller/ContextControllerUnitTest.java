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
    }

    @Test public void testFindContext_EmptySelectClause() {
        testFindContext("SELECT ", "*", Arrays.asList("*", "domain", "id", "language", "owner", "name", "ranking", "FROM"));
    }

    @Test public void testFindContext_FromWithWord() {
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

    @Test public void testFindContext_EmptyWhereClause() {
        testFindContext("SELECT * FROM * WHERE ", "", Arrays.asList("ALL", "AND", "ANY", "AlwaysOccurs", "CanConflict", "CanCooccur", "CanOccur", "EACH", "EQUALS", "EXCEPT", "FALSE", "GetTasks", "IN", "INTERSECT", "IS", "NOT", "OF", "OR", "OVERLAPS", "PROPER", "SUBSET", "TRUE", "UNION", "UNIVERSE", "UNKNOWN", "WITH"));
    }

    @Test public void testFindContext_Else() {
        testFindContext("FROM foo", "", Arrays.asList((String) null));
        testFindContext("FROM un", "", Arrays.asList("UNION", "UNIVERSE", "UNKNOWN"));
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
     * @return the suggestions displayed within the <var>context</var>
     */
    private List<String> toSuggestionList(JScrollPane context) {

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
