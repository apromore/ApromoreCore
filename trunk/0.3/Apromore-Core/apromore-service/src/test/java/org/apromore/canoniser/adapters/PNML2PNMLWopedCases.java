package org.apromore.canoniser.adapters;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PNML2PNMLWopedCases extends TestCase {
	
	public void testBallgame() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/01_Ballgame.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/01_Ballgame.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	public void testCapacityPlaning() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/02_CapacityPlanning.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/02_CapacityPlanning.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }

	public void testExample() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/03_Example.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/03_Example.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	public void testExampleWorkflow() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/04_Example-Workflow.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/04_Example-Workflow.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	
	public void testInsurance() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/05_Insurance.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/05_Insurance.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	public void testLoanApplication() throws IOException {
		String actual = ConvertToString.readFileAsString("PNML_models/woped_cases_mapped_pnml/06_LoanApplication.pnml");
		String expected = ConvertToString.readFileAsString("PNML_testcases/woped_cases_expected_pnml/06_LoanApplication.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	public void testLoanApplicationResources() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/07_LoanApplicationResources.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/07_LoanApplicationResources.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	public void testMailbox() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/08_Mailbox.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/08_Mailbox.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	public void testMailboxBounded() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/09_MailboxBounded.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/09_MailboxBounded.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	public void testMailboxUnbounded() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/10_MailboxUnbounded.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/10_MailboxUnbounded.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }

	
	public void testTwoTrafficLightsSafeFair() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/11_TwoTrafficLightsSafeFair.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/11_TwoTrafficLightsSafeFair.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	public void testVendingMachine() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/12_VendingMachine.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/12_VendingMachine.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	public void testAndSplitJoin() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/13_AndSplitJoin.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/13_AndSplitJoin.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	public void testAndJoinXorSplit() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/14_AndJoinXorSplit.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/14_AndJoinXorSplit.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }

	
	public void testXorSplitJoin() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/15_XorSplitJoin.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/15_XorSplitJoin.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
	public void testXorJoinAndSplit() throws IOException {
		String actual = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml/16_XorJoinAndSplit.pnml");
		String expected = ConvertToString.readFileAsString("Apromore-Core/apromore-service/src/test/resources/PNML_testcases/woped_cases_expected_pnml/16_XorJoinAndSplit.pnml");
		assertEquals("Produced Basic.pnml file is not correct",expected,actual);
    }
	
}
