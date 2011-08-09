
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.apromore.portal.manager;

import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.2.9
 * Tue May 24 18:17:50 CEST 2011
 * Generated source version: 2.2.9
 * 
 */

@javax.jws.WebService(
                      serviceName = "ManagerPortalService",
                      portName = "ManagerPortal",
                      targetNamespace = "http://www.apromore.org/manager/service_portal",
                      wsdlLocation = "http://localhost:8080/Apromore-manager/services/ManagerPortal?wsdl",
                      endpointInterface = "org.apromore.portal.manager.ManagerPortalPortType")
                      
public class ManagerPortalPortTypeImpl implements ManagerPortalPortType {

    private static final Logger LOG = Logger.getLogger(ManagerPortalPortTypeImpl.class.getName());

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#writeUser(org.apromore.portal.model_manager.WriteUserInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.WriteUserOutputMsgType writeUser(org.apromore.portal.model_manager.WriteUserInputMsgType payload) { 
        LOG.info("Executing operation writeUser");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.WriteUserOutputMsgType _return = new org.apromore.portal.model_manager.WriteUserOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-1676023997");
            _returnResult.setCode(Integer.valueOf(1114469997));
            _return.setResult(_returnResult);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#editProcessData(org.apromore.portal.model_manager.EditProcessDataInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.EditProcessDataOutputMsgType editProcessData(org.apromore.portal.model_manager.EditProcessDataInputMsgType payload) { 
        LOG.info("Executing operation editProcessData");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.EditProcessDataOutputMsgType _return = new org.apromore.portal.model_manager.EditProcessDataOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-445377771");
            _returnResult.setCode(Integer.valueOf(1529905245));
            _return.setResult(_returnResult);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#writeEditSession(org.apromore.portal.model_manager.WriteEditSessionInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.WriteEditSessionOutputMsgType writeEditSession(org.apromore.portal.model_manager.WriteEditSessionInputMsgType payload) { 
        LOG.info("Executing operation writeEditSession");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.WriteEditSessionOutputMsgType _return = new org.apromore.portal.model_manager.WriteEditSessionOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message1148963418");
            _returnResult.setCode(Integer.valueOf(-906740337));
            _return.setResult(_returnResult);
            _return.setEditSessionCode(Integer.valueOf(-382676403));
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#exportFormat(org.apromore.portal.model_manager.ExportFormatInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.ExportFormatOutputMsgType exportFormat(org.apromore.portal.model_manager.ExportFormatInputMsgType payload) { 
        LOG.info("Executing operation exportFormat");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.ExportFormatOutputMsgType _return = new org.apromore.portal.model_manager.ExportFormatOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-2104966656");
            _returnResult.setCode(Integer.valueOf(-724642169));
            _return.setResult(_returnResult);
            javax.activation.DataHandler _returnNative = null;
            _return.setNative(_returnNative);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#readNativeTypes(org.apromore.portal.model_manager.ReadNativeTypesInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.ReadNativeTypesOutputMsgType readNativeTypes(org.apromore.portal.model_manager.ReadNativeTypesInputMsgType payload) { 
        LOG.info("Executing operation readNativeTypes");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.ReadNativeTypesOutputMsgType _return = new org.apromore.portal.model_manager.ReadNativeTypesOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-1081025516");
            _returnResult.setCode(Integer.valueOf(1682448290));
            _return.setResult(_returnResult);
            org.apromore.portal.model_manager.NativeTypesType _returnNativeTypes = new org.apromore.portal.model_manager.NativeTypesType();
            java.util.List<org.apromore.portal.model_manager.FormatType> _returnNativeTypesNativeType = new java.util.ArrayList<org.apromore.portal.model_manager.FormatType>();
            org.apromore.portal.model_manager.FormatType _returnNativeTypesNativeTypeVal1 = new org.apromore.portal.model_manager.FormatType();
            _returnNativeTypesNativeTypeVal1.setFormat("Format2069708908");
            _returnNativeTypesNativeTypeVal1.setExtension("Extension637070079");
            _returnNativeTypesNativeType.add(_returnNativeTypesNativeTypeVal1);
            _returnNativeTypes.getNativeType().addAll(_returnNativeTypesNativeType);
            _return.setNativeTypes(_returnNativeTypes);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#readDomains(org.apromore.portal.model_manager.ReadDomainsInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.ReadDomainsOutputMsgType readDomains(org.apromore.portal.model_manager.ReadDomainsInputMsgType payload) { 
        LOG.info("Executing operation readDomains");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.ReadDomainsOutputMsgType _return = new org.apromore.portal.model_manager.ReadDomainsOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-116132151");
            _returnResult.setCode(Integer.valueOf(1614816023));
            _return.setResult(_returnResult);
            org.apromore.portal.model_manager.DomainsType _returnDomains = new org.apromore.portal.model_manager.DomainsType();
            java.util.List<java.lang.String> _returnDomainsDomain = new java.util.ArrayList<java.lang.String>();
            java.lang.String _returnDomainsDomainVal1 = "_returnDomainsDomainVal-2138507446";
            _returnDomainsDomain.add(_returnDomainsDomainVal1);
            _returnDomains.getDomain().addAll(_returnDomainsDomain);
            _return.setDomains(_returnDomains);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#mergeProcesses(org.apromore.portal.model_manager.MergeProcessesInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.MergeProcessesOutputMsgType mergeProcesses(org.apromore.portal.model_manager.MergeProcessesInputMsgType payload) { 
        LOG.info("Executing operation mergeProcesses");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.MergeProcessesOutputMsgType _return = new org.apromore.portal.model_manager.MergeProcessesOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-1554904588");
            _returnResult.setCode(Integer.valueOf(-1266226045));
            _return.setResult(_returnResult);
            org.apromore.portal.model_manager.ProcessSummaryType _returnProcessSummary = new org.apromore.portal.model_manager.ProcessSummaryType();
            java.util.List<org.apromore.portal.model_manager.VersionSummaryType> _returnProcessSummaryVersionSummaries = new java.util.ArrayList<org.apromore.portal.model_manager.VersionSummaryType>();
            org.apromore.portal.model_manager.VersionSummaryType _returnProcessSummaryVersionSummariesVal1 = new org.apromore.portal.model_manager.VersionSummaryType();
            java.util.List<org.apromore.portal.model_manager.AnnotationsType> _returnProcessSummaryVersionSummariesVal1Annotations = new java.util.ArrayList<org.apromore.portal.model_manager.AnnotationsType>();
            _returnProcessSummaryVersionSummariesVal1.getAnnotations().addAll(_returnProcessSummaryVersionSummariesVal1Annotations);
            _returnProcessSummaryVersionSummariesVal1.setRanking("Ranking2141529043");
            _returnProcessSummaryVersionSummariesVal1.setName("Name-647514571");
            _returnProcessSummaryVersionSummariesVal1.setLastUpdate("LastUpdate-477862412");
            _returnProcessSummaryVersionSummariesVal1.setCreationDate("CreationDate197747132");
            _returnProcessSummaryVersionSummaries.add(_returnProcessSummaryVersionSummariesVal1);
            _returnProcessSummary.getVersionSummaries().addAll(_returnProcessSummaryVersionSummaries);
            _returnProcessSummary.setOriginalNativeType("OriginalNativeType-940249564");
            _returnProcessSummary.setName("Name-731371415");
            _returnProcessSummary.setId(Integer.valueOf(-1114283634));
            _returnProcessSummary.setDomain("Domain-498896543");
            _returnProcessSummary.setRanking("Ranking-909371962");
            _returnProcessSummary.setLastVersion("LastVersion-1702222919");
            _returnProcessSummary.setOwner("Owner831284538");
            _return.setProcessSummary(_returnProcessSummary);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#readUser(org.apromore.portal.model_manager.ReadUserInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.ReadUserOutputMsgType readUser(org.apromore.portal.model_manager.ReadUserInputMsgType payload) { 
        LOG.info("Executing operation readUser");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.ReadUserOutputMsgType _return = new org.apromore.portal.model_manager.ReadUserOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-1102473495");
            _returnResult.setCode(Integer.valueOf(388929637));
            _return.setResult(_returnResult);
            org.apromore.portal.model_manager.UserType _returnUser = new org.apromore.portal.model_manager.UserType();
            java.util.List<org.apromore.portal.model_manager.SearchHistoriesType> _returnUserSearchHistories = new java.util.ArrayList<org.apromore.portal.model_manager.SearchHistoriesType>();
            org.apromore.portal.model_manager.SearchHistoriesType _returnUserSearchHistoriesVal1 = new org.apromore.portal.model_manager.SearchHistoriesType();
            _returnUserSearchHistoriesVal1.setSearch("Search402551494");
            _returnUserSearchHistoriesVal1.setNum(Integer.valueOf(-2078888070));
            _returnUserSearchHistories.add(_returnUserSearchHistoriesVal1);
            _returnUser.getSearchHistories().addAll(_returnUserSearchHistories);
            _returnUser.setFirstname("Firstname-737368663");
            _returnUser.setLastname("Lastname1566155873");
            _returnUser.setEmail("Email-1480814995");
            _returnUser.setUsername("Username1624605500");
            _returnUser.setPasswd("Passwd1522622822");
            _return.setUser(_returnUser);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#writeAnnotation(org.apromore.portal.model_manager.WriteAnnotationInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.WriteAnnotationOutputMsgType writeAnnotation(org.apromore.portal.model_manager.WriteAnnotationInputMsgType payload) { 
        LOG.info("Executing operation writeAnnotation");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.WriteAnnotationOutputMsgType _return = new org.apromore.portal.model_manager.WriteAnnotationOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message975472592");
            _returnResult.setCode(Integer.valueOf(-734659967));
            _return.setResult(_returnResult);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#readAllUsers(org.apromore.portal.model_manager.ReadAllUsersInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.ReadAllUsersOutputMsgType readAllUsers(org.apromore.portal.model_manager.ReadAllUsersInputMsgType payload) { 
        LOG.info("Executing operation readAllUsers");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.ReadAllUsersOutputMsgType _return = new org.apromore.portal.model_manager.ReadAllUsersOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-1711908734");
            _returnResult.setCode(Integer.valueOf(-347042773));
            _return.setResult(_returnResult);
            org.apromore.portal.model_manager.UsernamesType _returnUsernames = new org.apromore.portal.model_manager.UsernamesType();
            java.util.List<java.lang.String> _returnUsernamesUsername = new java.util.ArrayList<java.lang.String>();
            java.lang.String _returnUsernamesUsernameVal1 = "_returnUsernamesUsernameVal1356008392";
            _returnUsernamesUsername.add(_returnUsernamesUsernameVal1);
            _returnUsernames.getUsername().addAll(_returnUsernamesUsername);
            _return.setUsernames(_returnUsernames);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#deleteEditSession(org.apromore.portal.model_manager.DeleteEditSessionInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.DeleteEditSessionOutputMsgType deleteEditSession(org.apromore.portal.model_manager.DeleteEditSessionInputMsgType payload) { 
        LOG.info("Executing operation deleteEditSession");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.DeleteEditSessionOutputMsgType _return = new org.apromore.portal.model_manager.DeleteEditSessionOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message1301690810");
            _returnResult.setCode(Integer.valueOf(-208997564));
            _return.setResult(_returnResult);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#searchForSimilarProcesses(org.apromore.portal.model_manager.SearchForSimilarProcessesInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.SearchForSimilarProcessesOutputMsgType searchForSimilarProcesses(org.apromore.portal.model_manager.SearchForSimilarProcessesInputMsgType payload) { 
        LOG.info("Executing operation searchForSimilarProcesses");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.SearchForSimilarProcessesOutputMsgType _return = new org.apromore.portal.model_manager.SearchForSimilarProcessesOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message194608477");
            _returnResult.setCode(Integer.valueOf(127647709));
            _return.setResult(_returnResult);
            org.apromore.portal.model_manager.ProcessSummariesType _returnProcessSummaries = new org.apromore.portal.model_manager.ProcessSummariesType();
            java.util.List<org.apromore.portal.model_manager.ProcessSummaryType> _returnProcessSummariesProcessSummary = new java.util.ArrayList<org.apromore.portal.model_manager.ProcessSummaryType>();
            org.apromore.portal.model_manager.ProcessSummaryType _returnProcessSummariesProcessSummaryVal1 = new org.apromore.portal.model_manager.ProcessSummaryType();
            java.util.List<org.apromore.portal.model_manager.VersionSummaryType> _returnProcessSummariesProcessSummaryVal1VersionSummaries = new java.util.ArrayList<org.apromore.portal.model_manager.VersionSummaryType>();
            _returnProcessSummariesProcessSummaryVal1.getVersionSummaries().addAll(_returnProcessSummariesProcessSummaryVal1VersionSummaries);
            _returnProcessSummariesProcessSummaryVal1.setOriginalNativeType("OriginalNativeType1501138156");
            _returnProcessSummariesProcessSummaryVal1.setName("Name1074889876");
            _returnProcessSummariesProcessSummaryVal1.setId(Integer.valueOf(1734252044));
            _returnProcessSummariesProcessSummaryVal1.setDomain("Domain1246296552");
            _returnProcessSummariesProcessSummaryVal1.setRanking("Ranking401863035");
            _returnProcessSummariesProcessSummaryVal1.setLastVersion("LastVersion939211267");
            _returnProcessSummariesProcessSummaryVal1.setOwner("Owner1637099147");
            _returnProcessSummariesProcessSummary.add(_returnProcessSummariesProcessSummaryVal1);
            _returnProcessSummaries.getProcessSummary().addAll(_returnProcessSummariesProcessSummary);
            _return.setProcessSummaries(_returnProcessSummaries);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#deleteProcessVersions(org.apromore.portal.model_manager.DeleteProcessVersionsInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.DeleteProcessVersionsOutputMsgType deleteProcessVersions(org.apromore.portal.model_manager.DeleteProcessVersionsInputMsgType payload) { 
        LOG.info("Executing operation deleteProcessVersions");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.DeleteProcessVersionsOutputMsgType _return = new org.apromore.portal.model_manager.DeleteProcessVersionsOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message1199803914");
            _returnResult.setCode(Integer.valueOf(1499796237));
            _return.setResult(_returnResult);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#importProcess(org.apromore.portal.model_manager.ImportProcessInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.ImportProcessOutputMsgType importProcess(org.apromore.portal.model_manager.ImportProcessInputMsgType payload) { 
        LOG.info("Executing operation importProcess");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.ImportProcessOutputMsgType _return = new org.apromore.portal.model_manager.ImportProcessOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-637411517");
            _returnResult.setCode(Integer.valueOf(1128241473));
            _return.setResult(_returnResult);
            org.apromore.portal.model_manager.ProcessSummaryType _returnProcessSummary = new org.apromore.portal.model_manager.ProcessSummaryType();
            java.util.List<org.apromore.portal.model_manager.VersionSummaryType> _returnProcessSummaryVersionSummaries = new java.util.ArrayList<org.apromore.portal.model_manager.VersionSummaryType>();
            org.apromore.portal.model_manager.VersionSummaryType _returnProcessSummaryVersionSummariesVal1 = new org.apromore.portal.model_manager.VersionSummaryType();
            java.util.List<org.apromore.portal.model_manager.AnnotationsType> _returnProcessSummaryVersionSummariesVal1Annotations = new java.util.ArrayList<org.apromore.portal.model_manager.AnnotationsType>();
            _returnProcessSummaryVersionSummariesVal1.getAnnotations().addAll(_returnProcessSummaryVersionSummariesVal1Annotations);
            _returnProcessSummaryVersionSummariesVal1.setRanking("Ranking969048816");
            _returnProcessSummaryVersionSummariesVal1.setName("Name-1355996030");
            _returnProcessSummaryVersionSummariesVal1.setLastUpdate("LastUpdate-1677494771");
            _returnProcessSummaryVersionSummariesVal1.setCreationDate("CreationDate295564993");
            _returnProcessSummaryVersionSummaries.add(_returnProcessSummaryVersionSummariesVal1);
            _returnProcessSummary.getVersionSummaries().addAll(_returnProcessSummaryVersionSummaries);
            _returnProcessSummary.setOriginalNativeType("OriginalNativeType1254835799");
            _returnProcessSummary.setName("Name1512166432");
            _returnProcessSummary.setId(Integer.valueOf(1042606148));
            _returnProcessSummary.setDomain("Domain287708953");
            _returnProcessSummary.setRanking("Ranking-1520294446");
            _returnProcessSummary.setLastVersion("LastVersion-1411264750");
            _returnProcessSummary.setOwner("Owner174798938");
            _return.setProcessSummary(_returnProcessSummary);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#readProcessSummaries(org.apromore.portal.model_manager.ReadProcessSummariesInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.ReadProcessSummariesOutputMsgType readProcessSummaries(org.apromore.portal.model_manager.ReadProcessSummariesInputMsgType payload) { 
        LOG.info("Executing operation readProcessSummaries");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.ReadProcessSummariesOutputMsgType _return = new org.apromore.portal.model_manager.ReadProcessSummariesOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-815000473");
            _returnResult.setCode(Integer.valueOf(-58519605));
            _return.setResult(_returnResult);
            org.apromore.portal.model_manager.ProcessSummariesType _returnProcessSummaries = new org.apromore.portal.model_manager.ProcessSummariesType();
            java.util.List<org.apromore.portal.model_manager.ProcessSummaryType> _returnProcessSummariesProcessSummary = new java.util.ArrayList<org.apromore.portal.model_manager.ProcessSummaryType>();
            org.apromore.portal.model_manager.ProcessSummaryType _returnProcessSummariesProcessSummaryVal1 = new org.apromore.portal.model_manager.ProcessSummaryType();
            java.util.List<org.apromore.portal.model_manager.VersionSummaryType> _returnProcessSummariesProcessSummaryVal1VersionSummaries = new java.util.ArrayList<org.apromore.portal.model_manager.VersionSummaryType>();
            _returnProcessSummariesProcessSummaryVal1.getVersionSummaries().addAll(_returnProcessSummariesProcessSummaryVal1VersionSummaries);
            _returnProcessSummariesProcessSummaryVal1.setOriginalNativeType("OriginalNativeType-15169517");
            _returnProcessSummariesProcessSummaryVal1.setName("Name-1274227725");
            _returnProcessSummariesProcessSummaryVal1.setId(Integer.valueOf(-1338977616));
            _returnProcessSummariesProcessSummaryVal1.setDomain("Domain-1636376377");
            _returnProcessSummariesProcessSummaryVal1.setRanking("Ranking766195046");
            _returnProcessSummariesProcessSummaryVal1.setLastVersion("LastVersion-911020857");
            _returnProcessSummariesProcessSummaryVal1.setOwner("Owner-213798668");
            _returnProcessSummariesProcessSummary.add(_returnProcessSummariesProcessSummaryVal1);
            _returnProcessSummaries.getProcessSummary().addAll(_returnProcessSummariesProcessSummary);
            _return.setProcessSummaries(_returnProcessSummaries);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#readEditSession(org.apromore.portal.model_manager.ReadEditSessionInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.ReadEditSessionOutputMsgType readEditSession(org.apromore.portal.model_manager.ReadEditSessionInputMsgType payload) { 
        LOG.info("Executing operation readEditSession");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.ReadEditSessionOutputMsgType _return = new org.apromore.portal.model_manager.ReadEditSessionOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-2070298387");
            _returnResult.setCode(Integer.valueOf(769866249));
            _return.setResult(_returnResult);
            javax.activation.DataHandler _returnNative = null;
            _return.setNative(_returnNative);
            org.apromore.portal.model_manager.EditSessionType _returnEditSession = new org.apromore.portal.model_manager.EditSessionType();
            _returnEditSession.setUsername("Username-822281268");
            _returnEditSession.setNativeType("NativeType-696666243");
            _returnEditSession.setProcessId(Integer.valueOf(1793807816));
            _returnEditSession.setProcessName("ProcessName-2143719895");
            _returnEditSession.setVersionName("VersionName35050131");
            _returnEditSession.setDomain("Domain1249302391");
            _returnEditSession.setWithAnnotation(Boolean.valueOf(true));
            _returnEditSession.setAnnotation("Annotation1235811032");
            _return.setEditSession(_returnEditSession);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.portal.manager.ManagerPortalPortType#updateProcess(org.apromore.portal.model_manager.UpdateProcessInputMsgType  payload )*
     */
    public org.apromore.portal.model_manager.UpdateProcessOutputMsgType updateProcess(org.apromore.portal.model_manager.UpdateProcessInputMsgType payload) { 
        LOG.info("Executing operation updateProcess");
        System.out.println(payload);
        try {
            org.apromore.portal.model_manager.UpdateProcessOutputMsgType _return = new org.apromore.portal.model_manager.UpdateProcessOutputMsgType();
            org.apromore.portal.model_manager.ResultType _returnResult = new org.apromore.portal.model_manager.ResultType();
            _returnResult.setMessage("Message-1538007310");
            _returnResult.setCode(Integer.valueOf(-290779195));
            _return.setResult(_returnResult);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
