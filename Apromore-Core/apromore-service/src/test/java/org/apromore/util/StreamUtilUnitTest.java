package org.apromore.util;

import org.apromore.TestData;
import org.junit.Ignore;
import org.junit.Test;
import org.wfmc._2008.xpdl2.PackageType;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.powermock.api.easymock.PowerMock.*;

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
        replayAll();

        String result = StreamUtil.convertStreamToString(source);

        verifyAll();

        assertThat(result, equalTo("error in readin the DataHandler: java.io.IOException: "));
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
        replayAll();

        String result = StreamUtil.convertStreamToString(source);

        verifyAll();

        assertThat(result, equalTo("error in readin the DataSource: java.io.IOException: "));
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

    @Ignore
    @Test
    public void testCopyParam2NPFXPDL() throws Exception {
        //TODO FM, i think that is not needed anymore
        
        String nativeType = "XPDL 2.1";
        String name = "bob43";
        String version = "999.9";
        String username = "Osama";
        String created = "12/12/2012";
        String updated = "12/12/2012";

        InputStream stream = new ByteArrayInputStream(TestData.XPDL2.getBytes());
        InputStream stream2 = StreamUtil.copyParam2NPF(stream, nativeType, name, version, username, created, updated);
        String result = StreamUtil.convertStreamToString(stream2);

        assertThat(result, containsString(name));
        assertThat(result, containsString(version));
        assertThat(result, containsString(username));
        assertThat(result, containsString(created));
        assertThat(result, containsString(updated));
    }

    @Test
    public void testCopyParam2NPFEPML() throws Exception {
        String nativeType = "EPML 2.0";
        String name = "bob43";
        String version = "999.9";
        String username = "Osama";
        String created = "12/12/2012";
        String updated = "12/12/2012";

        InputStream stream = new ByteArrayInputStream(TestData.XPDL.getBytes());
        InputStream stream2 = StreamUtil.copyParam2NPF(stream, nativeType, name, version, username, created, updated);

        assertThat(stream2, equalTo(stream));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCopyParam2XPDLNullHeader() throws Exception {
        InputStream stream = new ByteArrayInputStream(TestData.XPDL.getBytes());
        
        // Get the package so I can change some data for the tests
        JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
        Unmarshaller u = jc.createUnmarshaller();
        JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(stream);
        PackageType pkg = rootElement.getValue();
        
        pkg.setRedefinableHeader(null);
        pkg.setPackageHeader(null);

        StreamUtil.copyParam2XPDL(pkg, null, null, null, null, null);

        assertThat(pkg.getRedefinableHeader(), notNullValue());
        assertThat(pkg.getPackageHeader(), notNullValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCopyParam2XPDLNullHeaderVersionAuthor() throws Exception {
        InputStream stream = new ByteArrayInputStream(TestData.XPDL.getBytes());

        // Get the package so I can change some data for the tests
        JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
        Unmarshaller u = jc.createUnmarshaller();
        JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(stream);
        PackageType pkg = rootElement.getValue();

        pkg.getRedefinableHeader().setAuthor(null);
        pkg.getRedefinableHeader().setVersion(null);
        pkg.getPackageHeader().setCreated(null);
        pkg.getPackageHeader().setModificationDate(null);
        pkg.getPackageHeader().setDocumentation(null);

        StreamUtil.copyParam2XPDL(pkg, null, null, null, null, null);

        assertThat(pkg.getRedefinableHeader().getAuthor(), notNullValue());
        assertThat(pkg.getRedefinableHeader().getVersion(), notNullValue());
        assertThat(pkg.getPackageHeader().getCreated(), notNullValue());
        assertThat(pkg.getPackageHeader().getModificationDate(), notNullValue());
        assertThat(pkg.getPackageHeader().getDocumentation(), notNullValue());
    }

    @Ignore
    @Test
    public void testCopyParam2NPFPMNL() throws Exception {
        String nativeType = "PMNL 2.0";
        String name = "bob43";
        String version = "999.9";
        String username = "Osama";
        String created = "12/12/2012";
        String updated = "12/12/2012";

        InputStream stream = new ByteArrayInputStream("test".getBytes());
        InputStream stream2 = StreamUtil.copyParam2NPF(stream, nativeType,  name, version, username, created, updated);

        //TODO why should it be NULL?
        assertThat(stream2, equalTo(null));
    }
}
