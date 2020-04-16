/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import org.apromore.TestData;
import org.junit.Test;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit test the Stream Util.
 */
public class StreamUtilUnitTest {

    @Test
    public void testConvertStreamToString() {
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
    public void testConvertStreamToStringExpectException() {
        InputStream stream = null;
        String result = StreamUtil.convertStreamToString(stream);
        assertThat(result, equalTo(""));
    }

    @Test
    public void testConvertStreamToStringDataHandler() throws IOException {
        String str = "InputStream Test String";
        DataSource source_native = new ByteArrayDataSource(str, "text/xml");

        String result = StreamUtil.convertStreamToString(new DataHandler(source_native));

        assertThat(result, equalTo(str));
    }

    @Test
    public void testConvertStreamToStringDataHandlerExceptionThrown() throws IOException {
        DataHandler source = createMock(DataHandler.class);

        expect(source.getInputStream()).andThrow(new IOException(""));
        replay();

        String result = StreamUtil.convertStreamToString(source);

        verify();

        assertThat(result, equalTo(""));
    }

    @Test
    public void testConvertStreamToStringDataSource() throws IOException {
        String str = "InputStream Test String";
        DataSource source_native = new ByteArrayDataSource(str, "text/xml");

        String result = StreamUtil.convertStreamToString(source_native);

        assertThat(result, equalTo(str));
    }

    @Test
    public void testConvertStreamToStringDataSourceExceptionThrown() throws IOException {
        DataSource source = createMock(DataSource.class);

        expect(source.getInputStream()).andThrow(new IOException(""));
        replay();

        String result = StreamUtil.convertStreamToString(source);

        verify();

        assertThat(result, equalTo(""));
    }

    @Test
    public void testCopyParam2ANF() throws Exception {
        String name = "bob2";

        InputStream stream = new ByteArrayInputStream(TestData.ANF.getBytes());
        InputStream stream2 = StreamUtil.copyParam2ANF(stream, name);
        String result = StreamUtil.convertStreamToString(stream2);

        assertThat(result, containsString(name));
    }

    @Test
    public void testCopyParam2CPF() throws Exception {
        Integer uri = 12345;
        String name = "bob2";
        String version = "999.9";
        String username = "Osama";
        String created = "12/12/2012";
        String updated = "12/12/2012";
        InputStream stream = new ByteArrayInputStream(TestData.CPF.getBytes());

        InputStream stream2 = StreamUtil.copyParam2CPF(stream, uri, name, version, username, created, updated);
        String result = StreamUtil.convertStreamToString(stream2);

        assertThat(result, containsString(String.valueOf(uri)));
        assertThat(result, containsString(name));
        assertThat(result, containsString(version));
        assertThat(result, containsString(username));
        assertThat(result, containsString(created));
        assertThat(result, containsString(updated));
    }
}
