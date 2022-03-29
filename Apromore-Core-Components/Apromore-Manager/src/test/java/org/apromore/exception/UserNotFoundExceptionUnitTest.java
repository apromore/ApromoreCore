/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011, 2012, 2015 - 2017 Queensland University of Technology.
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

package org.apromore.exception;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test the User Not Found Exception POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
class UserNotFoundExceptionUnitTest {

    @Test
    void testException() {
        UserNotFoundException exception = new UserNotFoundException();
        MatcherAssert.assertThat(exception, Matchers.notNullValue());

        exception = new UserNotFoundException("Error");
        MatcherAssert.assertThat(exception, Matchers.notNullValue());
        MatcherAssert.assertThat(exception.getMessage(), Matchers.equalTo("Error"));

        exception = new UserNotFoundException(new Exception());
        MatcherAssert.assertThat(exception, Matchers.notNullValue());
        MatcherAssert.assertThat(exception.getCause(), Matchers.notNullValue());

        exception = new UserNotFoundException("Error", new Exception());
        MatcherAssert.assertThat(exception, Matchers.notNullValue());
        MatcherAssert.assertThat(exception.getMessage(), Matchers.equalTo("Error"));
        MatcherAssert.assertThat(exception.getCause(), Matchers.notNullValue());
    }
}
