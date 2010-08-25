package org.apromore.canoniser.adapters.test;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;


public class EPML2XPDL extends TestCase {

	@Test
	public void testBasic() throws IOException {
		String actual = ConvertToString.readFileAsString("EPML_models/_Basic.xpdl");
		String expected = ConvertToString.readFileAsString("EPML_testcases/_Basic.xpdl");
		assertEquals("Produced Basic.xpdl file is not correct",expected,actual);
    }
	
	@Test
	public void testAnd() throws IOException {
		String actual = ConvertToString.readFileAsString("EPML_models/_and.xpdl");
		String expected = ConvertToString.readFileAsString("EPML_testcases/_and.xpdl");
		assertEquals("Produced and.xpdl file is not correct",expected,actual);
    }
	
	@Test
	public void testOr() throws IOException {
		String actual = ConvertToString.readFileAsString("EPML_models/_or.xpdl");
		String expected = ConvertToString.readFileAsString("EPML_testcases/_or.xpdl");
		assertEquals("Produced or.xpdl file is not correct",expected,actual);
    }
	
	@Test
	public void testXor() throws IOException {
		String actual = ConvertToString.readFileAsString("EPML_models/_xor.xpdl");
		String expected = ConvertToString.readFileAsString("EPML_testcases/_xor.xpdl");
		assertEquals("Produced xor.xpdl file is not correct",expected,actual);
    }
	
	@Test
	public void testRole() throws IOException {
		String actual = ConvertToString.readFileAsString("EPML_models/_role.xpdl");
		String expected = ConvertToString.readFileAsString("EPML_testcases/_role.xpdl");
		assertEquals("Produced role.xpdl file is not correct",expected,actual);
    }
	
	@Test
	public void testTwoRoles() throws IOException {
		String actual = ConvertToString.readFileAsString("EPML_models/_role_2.xpdl");
		String expected = ConvertToString.readFileAsString("EPML_testcases/_role_2.xpdl");
		assertEquals("Produced role_2.xpdl file is not correct",expected,actual);
    }
	
	@Test
	public void testObject() throws IOException {
		String actual = ConvertToString.readFileAsString("EPML_models/_object.xpdl");
		String expected = ConvertToString.readFileAsString("EPML_testcases/_object.xpdl");
		assertEquals("Produced object.xpdl file is not correct",expected,actual);
    }
	
	@Test
	public void testRange() throws IOException {
		String actual = ConvertToString.readFileAsString("EPML_models/_range.xpdl");
		String expected = ConvertToString.readFileAsString("EPML_testcases/_range.xpdl");
		assertEquals("Produced range.xpdl file is not correct",expected,actual);
    }
	
}
