package org.apromore.canoniser.adapters;

import org.junit.*;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

import junit.framework.TestCase;

@Ignore
public class EPML2CPF extends TestCase {

	@Test
	public void testBasic() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_models/Basic.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_testcases/Basic.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	@Test
	public void testAnd() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_models/and.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_testcases/and.cpf");
		assertEquals("Produced and.cpf file is not correct",expected,actual);
    }
	
	@Test
	public void testOr() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_models/or.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_testcases/or.cpf");
		assertEquals("Produced or.cpf file is not correct",expected,actual);
    }
	
	@Test
	public void testXor() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_models/xor.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_testcases/xor.cpf");
		assertEquals("Produced xor.cpf file is not correct",expected,actual);
    }
	
	@Test
	public void testRole() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_models/role.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_testcases/role.cpf");
		assertEquals("Produced role.cpf file is not correct",expected,actual);
    }
	
	@Test
	public void testTwoRoles() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_models/role_2.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_testcases/role_2.cpf");
		assertEquals("Produced role_2.cpf file is not correct",expected,actual);
    }
	
	@Test
	public void testObject() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_models/object.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_testcases/object.cpf");
		assertEquals("Produced object.cpf file is not correct",expected,actual);
    }
	
	@Test
	public void testRange() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_models/range.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/EPML_testcases/range.cpf");
		assertEquals("Produced range.cpf file is not correct",expected,actual);
    }
	
}
