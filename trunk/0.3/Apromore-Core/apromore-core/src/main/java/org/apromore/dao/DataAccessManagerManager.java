package org.apromore.dao;

import org.apromore.model.DeleteEditSessionInputMsgType;
import org.apromore.model.DeleteEditSessionOutputMsgType;
import org.apromore.model.DeleteProcessVersionsInputMsgType;
import org.apromore.model.DeleteProcessVersionsOutputMsgType;
import org.apromore.model.EditProcessDataInputMsgType;
import org.apromore.model.EditProcessDataOutputMsgType;
import org.apromore.model.ReadAllUsersInputMsgType;
import org.apromore.model.ReadAllUsersOutputMsgType;
import org.apromore.model.ReadCanonicalAnfInputMsgType;
import org.apromore.model.ReadCanonicalAnfOutputMsgType;
import org.apromore.model.ReadDomainsInputMsgType;
import org.apromore.model.ReadDomainsOutputMsgType;
import org.apromore.model.ReadEditSessionInputMsgType;
import org.apromore.model.ReadEditSessionOutputMsgType;
import org.apromore.model.ReadFormatInputMsgType;
import org.apromore.model.ReadFormatOutputMsgType;
import org.apromore.model.ReadNativeTypesInputMsgType;
import org.apromore.model.ReadNativeTypesOutputMsgType;
import org.apromore.model.ReadProcessSummariesInputMsgType;
import org.apromore.model.ReadProcessSummariesOutputMsgType;
import org.apromore.model.ReadUserInputMsgType;
import org.apromore.model.ReadUserOutputMsgType;
import org.apromore.model.WriteEditSessionInputMsgType;
import org.apromore.model.WriteEditSessionOutputMsgType;
import org.apromore.model.WriteUserInputMsgType;
import org.apromore.model.WriteUserOutputMsgType;

/**
 * 
 */
public interface DataAccessManagerManager {

    public DeleteProcessVersionsOutputMsgType deleteProcessVersions(DeleteProcessVersionsInputMsgType payload);

//    public WriteUserOutputMsgType writeUser(WriteUserInputMsgType payload);

    public ReadNativeTypesOutputMsgType readNativeTypes(ReadNativeTypesInputMsgType payload);

    public DeleteEditSessionOutputMsgType deleteEditSession(DeleteEditSessionInputMsgType payload);

    public ReadEditSessionOutputMsgType readEditSession(ReadEditSessionInputMsgType payload);

    public ReadDomainsOutputMsgType readDomains(ReadDomainsInputMsgType payload);

    public WriteEditSessionOutputMsgType writeEditSession(WriteEditSessionInputMsgType payload);

//    public ReadAllUsersOutputMsgType readAllUsers(ReadAllUsersInputMsgType payload);

    public ReadCanonicalAnfOutputMsgType readCanonicalAnf(ReadCanonicalAnfInputMsgType payload);

//    public ReadUserOutputMsgType readUser(ReadUserInputMsgType payload);

    public ReadFormatOutputMsgType readFormat(ReadFormatInputMsgType payload);

    public EditProcessDataOutputMsgType editProcessData(EditProcessDataInputMsgType payload);

    public ReadProcessSummariesOutputMsgType readProcessSummaries(ReadProcessSummariesInputMsgType payload);
}
