package org.apromore.canoniser.pnml.internal.pnml2canonical;

// Java 2 Standard Edition
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Collections.emptyList;
import java.util.List;

// Third party libraries
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for {@link RemoveDuplicateListItems}.
 */
public class RemoveDuplicateListItemsUnitTest {

    private static final List<String> empty = emptyList();

    /**
     * Test {@link RemoveDuplicateListItems#transform}.
     */
    @Test
    public void testTransform() throws Exception {
        f(empty, empty);
        f(Arrays.asList("a"), empty);
        f(Arrays.asList("a", "b", "c"), empty);
        f(Arrays.asList("a", "a"), Arrays.asList("a"));
        f(Arrays.asList("a", "a", "a", "a", "a"), Arrays.asList("a", "a", "a", "a"));
        f(Arrays.asList("b", "a", "b", "a", "b"), Arrays.asList("a", "b", "b"));
        f(Arrays.asList("a", "b", "a"), Arrays.asList("a"));
        f(Arrays.asList("a", "a", "b", "c", "c"), Arrays.asList("a", "c"));
        f(Arrays.asList("a", "b", "r", "a", "c", "a", "d", "a", "b", "r", "a"), Arrays.asList("a", "a", "a", "a", "b", "r"));
    }

    // Internal methods

    private void f(List<String> input, List<String> output) {
        List<String> list = new ArrayList(input);
        List<String> expected = new ArrayList(output);
        List<String> result = RemoveDuplicateListItems.transform(list);
        assertEquals(expected, result);
        assertEquals(expected, list);
    }
}
