package org.apromore.canoniser.adapters;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PNML2CPFWopedCases extends TestCase {

	public void testBallgame() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/01_Ballgame.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/01_Ballgame.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	public void testCapacityPlaning() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/02_CapacityPlanning.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/02_CapacityPlanning.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }

	public void testExample() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/03_Example.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/03_Example.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	public void testExampleWorkflow() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/04_Example-Workflow.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/04_Example-Workflow.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	
	public void testInsurance() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/05_Insurance.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/05_Insurance.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	public void testLoanApplication() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/06_LoanApplication.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/06_LoanApplication.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	public void testLoanApplicationResources() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/07_LoanApplicationResources.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/07_LoanApplicationResources.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	public void testMailbox() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/08_Mailbox.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/08_Mailbox.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	public void testMailboxBounded() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/09_MailboxBounded.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/09_MailboxBounded.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	public void testMailboxUnbounded() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/10_MailboxUnbounded.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/10_MailboxUnbounded.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }

	
	public void testTwoTrafficLightsSafeFair() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/11_TwoTrafficLightsSafeFair.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/11_TwoTrafficLightsSafeFair.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	public void testVendingMachine() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/12_VendingMachine.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/12_VendingMachine.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	public void testAndSplitJoin() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/13_AndSplitJoin.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/13_AndSplitJoin.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	public void testAndJoinXorSplit() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/14_AndJoinXorSplit.cpf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_cpf/14_AndJoinXorSplit.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }

	
	public void testXorSplitJoin() throws IOException {
		String actual = ConvertToString.readFileAsString("PNML_models/woped_cases_mapped_cpf_anf/15_XorSplitJoin.cpf");
		String expected = ConvertToString.readFileAsString("PNML_testcases/woped_cases_expected_cpf/15_XorSplitJoin.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	public void testXorJoinSplit() throws IOException {
		String actual = ConvertToString.readFileAsString("PNML_models/woped_cases_mapped_cpf_anf/16_XorJoinAndSplit.cpf");
		String expected = ConvertToString.readFileAsString("PNML_testcases/woped_cases_expected_cpf/16_XorJoinAndSplit.cpf");
		assertEquals("Produced Basic.cpf file is not correct",expected,actual);
    }
	
	
}
