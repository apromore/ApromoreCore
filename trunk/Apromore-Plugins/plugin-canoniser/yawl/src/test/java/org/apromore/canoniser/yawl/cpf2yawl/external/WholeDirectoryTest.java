package org.apromore.canoniser.yawl.cpf2yawl.external;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLTest;

public abstract class WholeDirectoryTest extends BaseCPF2YAWLTest {

    public WholeDirectoryTest(final File testCPFFile, final File testANFFile) {
        super();
        this.testANFFile = testANFFile;
        this.testCPFFile = testCPFFile;
        System.out.println("Testing file CPF " + testCPFFile.getName());
    }

    private final File testANFFile;
    private final File testCPFFile;

    @Override
    protected File getCPFFile() {
        return testCPFFile;
    }

    @Override
    protected File getANFFile() {
        return testANFFile;
    }

}
