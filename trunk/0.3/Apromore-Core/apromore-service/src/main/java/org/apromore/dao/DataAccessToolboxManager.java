package org.apromore.dao;

import org.apromore.model.ReadCanonicalsInputMsgType;
import org.apromore.model.ReadCanonicalsOutputMsgType;
import org.apromore.model.ReadProcessSummaryInputMsgType;
import org.apromore.model.ReadProcessSummariesOutputMsgType;
import org.apromore.model.StoreCpfInputMsgType;
import org.apromore.model.StoreCpfOutputMsgType;

/**
 * 
 */
public interface DataAccessToolboxManager {

    public ReadCanonicalsOutputMsgType readCanonicals(ReadCanonicalsInputMsgType payload);

    public StoreCpfOutputMsgType storeCpf(StoreCpfInputMsgType payload);
}
