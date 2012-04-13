package org.apromore.canoniser.adapters;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class XPDL2EPML extends TestCase {

	@Test
	public void testBasic() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_models/_Basic.epml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_testcases/_Basic.epml");
		assertEquals("Produced Basic.epml file is not correct",expected,actual);
    }
	
	@Test
	public void testEventSeq() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_models/_Event_seq.epml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_testcases/_Event_seq.epml");
		assertEquals("Produced Event_seq.epml file is not correct",expected,actual);
    }
	
	@Test
	public void testBasic2() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_models/_Basic2.epml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_testcases/_Basic2.epml");
		assertEquals("Produced Basic2.epml file is not correct",expected,actual);
    }
	
	@Test
	public void testAnd() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_models/_and.epml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_testcases/_and.epml");
		assertEquals("Produced and.epml file is not correct",expected,actual);
    }
	
	@Test
	public void testOr() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_models/_or.epml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_testcases/_or.epml");
		assertEquals("Produced or.epml file is not correct",expected,actual);
    }
	
	@Test
	public void testXor() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_models/_xor.epml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_testcases/_xor.epml");
		assertEquals("Produced xor.epml file is not correct",expected,actual);
    }
	
	@Test
	public void testPool() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_models/_pool.epml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_testcases/_pool.epml");
		assertEquals("Produced pool.epml file is not correct",expected,actual);
    }
	
	@Test
	public void testLanes() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_models/_lane.epml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_testcases/_lane.epml");
		assertEquals("Produced lane.epml file is not correct",expected,actual);
    }
	
	@Test
	public void testObject() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_models/_object.epml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/XPDL_testcases/_object.epml");
		assertEquals("Produced object.epml file is not correct",expected,actual);
    }
	
}
