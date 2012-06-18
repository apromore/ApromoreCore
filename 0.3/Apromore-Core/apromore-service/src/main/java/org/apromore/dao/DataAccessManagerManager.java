package org.apromore.dao;

import org.apromore.model.DeleteEditSessionInputMsgType;
import org.apromore.model.DeleteEditSessionOutputMsgType;
import org.apromore.model.DeleteProcessVersionsInputMsgType;
import org.apromore.model.DeleteProcessVersionsOutputMsgType;
import org.apromore.model.EditProcessDataInputMsgType;
import org.apromore.model.EditProcessDataOutputMsgType;
import org.apromore.model.ReadEditSessionInputMsgType;
import org.apromore.model.ReadEditSessionOutputMsgType;
import org.apromore.model.WriteEditSessionInputMsgType;
import org.apromore.model.WriteEditSessionOutputMsgType;

/**
 * 
 */
public interface DataAccessManagerManager {

    public DeleteProcessVersionsOutputMsgType deleteProcessVersions(DeleteProcessVersionsInputMsgType payload);

    public DeleteEditSessionOutputMsgType deleteEditSession(DeleteEditSessionInputMsgType payload);

    public ReadEditSessionOutputMsgType readEditSession(ReadEditSessionInputMsgType payload);

    public WriteEditSessionOutputMsgType writeEditSession(WriteEditSessionInputMsgType payload);

//    public ReadCanonicalAnfOutputMsgType readCanonicalAnf(ReadCanonicalAnfInputMsgType payload);
//
//    public ReadFormatOutputMsgType readFormat(ReadFormatInputMsgType payload);

    public EditProcessDataOutputMsgType editProcessData(EditProcessDataInputMsgType payload);

}
