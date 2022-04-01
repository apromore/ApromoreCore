/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
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

package org.apromore.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.junit.jupiter.api.Test;

/**
 * Unit test the Stream Util.
 */
class StreamUtilUnitTest {

    @Test
    void testConvertStreamToString() {
        String str = "InputStream Test String";
        InputStream stream = new ByteArrayInputStream(str.getBytes());

        String result = StreamUtil.convertStreamToString(stream);

        assertThat(result, equalTo(str));

        String str2 = "InputStream \n Test String";
        InputStream stream2 = new ByteArrayInputStream(str2.getBytes());

        String result2 = StreamUtil.convertStreamToString(stream2);

        assertThat(result2, equalTo(str2));
    }

    @Test
    void testConvertStreamToStringExpectException() {
        InputStream stream = null;
        String result = StreamUtil.convertStreamToString(stream);
        assertThat(result, equalTo(""));
    }

    @Test
    void testConvertStreamToStringDataHandler() throws IOException {
        String str = "InputStream Test String";
        DataSource source_native = new ByteArrayDataSource(str, "text/xml");

        String result = StreamUtil.convertStreamToString(new DataHandler(source_native));

        assertThat(result, equalTo(str));
    }

    @Test
    void testConvertStreamToStringDataHandlerExceptionThrown() throws IOException {
        DataHandler source = createMock(DataHandler.class);

        expect(source.getInputStream()).andThrow(new IOException(""));
        replay();

        String result = StreamUtil.convertStreamToString(source);

        verify();

        assertThat(result, equalTo(""));
    }

    @Test
    void testConvertStreamToStringDataSource() throws IOException {
        String str = "InputStream Test String";
        DataSource source_native = new ByteArrayDataSource(str, "text/xml");

        String result = StreamUtil.convertStreamToString(source_native);

        assertThat(result, equalTo(str));
    }

    @Test
    void testConvertStreamToStringDataSourceExceptionThrown() throws IOException {
        DataSource source = createMock(DataSource.class);

        expect(source.getInputStream()).andThrow(new IOException(""));
        replay();

        String result = StreamUtil.convertStreamToString(source);

        verify();

        assertThat(result, equalTo(""));
    }

}
