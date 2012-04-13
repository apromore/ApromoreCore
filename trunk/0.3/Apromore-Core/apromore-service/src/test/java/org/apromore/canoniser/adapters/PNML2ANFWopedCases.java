package org.apromore.canoniser.adapters;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PNML2ANFWopedCases extends TestCase {

	public void testBallgame() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/01_Ballgame.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/01_Ballgame.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	public void testCapacityPlaning() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/02_CapacityPlanning.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/02_CapacityPlanning.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }

	public void testExample() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/03_Example.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/03_Example.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	public void testExampleWorkflow() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/04_Example-Workflow.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/04_Example-Workflow.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	
	public void testInsurance() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/05_Insurance.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/05_Insurance.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	public void testLoanApplication() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/06_LoanApplication.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/06_LoanApplication.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	public void testLoanApplicationResources() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/07_LoanApplicationResources.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/07_LoanApplicationResources.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	public void testMailbox() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/08_Mailbox.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/08_Mailbox.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	public void testMailboxBounded() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/09_MailboxBounded.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/09_MailboxBounded.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	public void testMailboxUnbounded() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/10_MailboxUnbounded.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/10_MailboxUnbounded.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }

	
	public void testTwoTrafficLightsSafeFair() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/11_TwoTrafficLightsSafeFair.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/11_TwoTrafficLightsSafeFair.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	public void testVendingMachine() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/12_VendingMachine.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/12_VendingMachine.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	public void testAndSplitJoin() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/13_AndSplitJoin.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/13_AndSplitJoin.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	public void testAndJoinXorSplit() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/14_AndJoinXorSplit.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/14_AndJoinXorSplit.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }

	
	public void testXorSplitJoin() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/15_XorSplitJoin.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/15_XorSplitJoin.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	public void testXorJoinAndSplit() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf/16_XorJoinAndSplit.anf");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_anf/16_XorJoinAndSplit.anf");
		assertEquals("Produced Basic.anf file is not correct",expected,actual);
    }
	
	
}
