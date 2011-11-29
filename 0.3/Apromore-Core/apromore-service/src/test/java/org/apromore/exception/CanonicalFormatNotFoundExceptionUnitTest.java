package org.apromore.exception;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test the Canonical Format Not Found Exception POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CanonicalFormatNotFoundExceptionUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(CanonicalFormatNotFoundException.class);
    }

    @Test
    public void testException() {
        CanonicalFormatNotFoundException exception = new CanonicalFormatNotFoundException();
        MatcherAssert.assertThat(exception, Matchers.notNullValue());

        exception = new CanonicalFormatNotFoundException("Error");
        MatcherAssert.assertThat(exception, Matchers.notNullValue());
        MatcherAssert.assertThat(exception.getMessage(), Matchers.equalTo("Error"));

        exception = new CanonicalFormatNotFoundException(new Exception());
        MatcherAssert.assertThat(exception, Matchers.notNullValue());
        MatcherAssert.assertThat(exception.getCause(), Matchers.notNullValue());

        exception = new CanonicalFormatNotFoundException("Error", new Exception());
        MatcherAssert.assertThat(exception, Matchers.notNullValue());
        MatcherAssert.assertThat(exception.getMessage(), Matchers.equalTo("Error"));
        MatcherAssert.assertThat(exception.getCause(), Matchers.notNullValue());
    }
}
