package org.apromore.canoniser.yawl.cpf2yawl.reallife;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLTest;
import org.apromore.canoniser.yawl.utils.TestUtils;

public class PaymentSubProcessTest extends BaseCPF2YAWLTest {

    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/PaymentSubnet.yawl.cpf");
    }

    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/PaymentSubnet.yawl.anf");
    }

}
