package org.apromore.canoniser.yawl.cpf2yawl.external;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WholeDirectoryTest extends BaseCPF2YAWLTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WholeDirectoryTest.class);

    public WholeDirectoryTest(final File testCPFFile, final File testANFFile) {
        super();
        this.testANFFile = testANFFile;
        this.testCPFFile = testCPFFile;
        LOGGER.debug("Testing file CPF: {] ", testCPFFile.getName());
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

    protected final boolean isName(final File file, final String name) {
        return file.getName().equals(name);
    }


}
