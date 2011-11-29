package org.apromore.exception;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test the Export Format Exception POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class NativeFormatNotFoundExceptionUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(NativeFormatNotFoundException.class);
    }

    @Test
    public void testException() {
        NativeFormatNotFoundException exception = new NativeFormatNotFoundException();
        MatcherAssert.assertThat(exception, Matchers.notNullValue());

        exception = new NativeFormatNotFoundException("Error");
        MatcherAssert.assertThat(exception, Matchers.notNullValue());
        MatcherAssert.assertThat(exception.getMessage(), Matchers.equalTo("Error"));

        exception = new NativeFormatNotFoundException(new Exception());
        MatcherAssert.assertThat(exception, Matchers.notNullValue());
        MatcherAssert.assertThat(exception.getCause(), Matchers.notNullValue());

        exception = new NativeFormatNotFoundException("Error", new Exception());
        MatcherAssert.assertThat(exception, Matchers.notNullValue());
        MatcherAssert.assertThat(exception.getMessage(), Matchers.equalTo("Error"));
        MatcherAssert.assertThat(exception.getCause(), Matchers.notNullValue());
    }
}
