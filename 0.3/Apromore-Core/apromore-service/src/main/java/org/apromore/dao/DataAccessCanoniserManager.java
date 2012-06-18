package org.apromore.dao;

import org.apromore.model.GetCpfUriInputMsgType;
import org.apromore.model.GetCpfUriOutputMsgType;
import org.apromore.model.StoreNativeInputMsgType;
import org.apromore.model.StoreNativeOutputMsgType;
import org.apromore.model.StoreVersionInputMsgType;
import org.apromore.model.StoreVersionOutputMsgType;
import org.apromore.model.WriteAnnotationInputMsgType;
import org.apromore.model.WriteAnnotationOutputMsgType;

/**
 * 
 */
public interface DataAccessCanoniserManager {

    public GetCpfUriOutputMsgType getCpfUri(GetCpfUriInputMsgType payload);

//    public StoreNativeCpfOutputMsgType storeNativeCpf(StoreNativeCpfInputMsgType payload);

    public StoreVersionOutputMsgType storeVersion(StoreVersionInputMsgType payload);

    public StoreNativeOutputMsgType storeNative(StoreNativeInputMsgType payload);

    public WriteAnnotationOutputMsgType writeAnnotation(WriteAnnotationInputMsgType payload);
}
