package org.apromore.toolbox.service;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.model.CanonicalType;
import org.apromore.model.MergeProcessesInputMsgType;
import org.apromore.model.MergeProcessesOutputMsgType;
import org.apromore.model.ParameterType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdType;
import org.apromore.model.ProcessVersionType;
import org.apromore.model.ProcessVersionsType;
import org.apromore.model.ResultType;
import org.apromore.model.VersionSummaryType;
import org.apromore.toolbox.da.ToolboxDataAccessClient;
import org.apromore.toolbox.similaritySearch.tools.MergeProcesses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ToolBox Service.
 *
 * Handles the requests that come to the toolbox.
 */
@Service("ToolboxManager")
public class ToolboxManagerImpl implements ToolboxManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolboxManagerImpl.class.getName());

    private ToolboxDataAccessClient client;

//    public MergeProcessesOutputMsgType mergeProcesses(MergeProcessesInputMsgType payload) {
//        LOGGER.info("Executing operation mergeProcesses");
//        MergeProcessesOutputMsgType res = new MergeProcessesOutputMsgType();
//        ResultType result = new ResultType();
//        res.setResult(result);
//        try {
//            String processName = payload.getProcessName();
//            String versionName = payload.getVersionName();
//            String domain = payload.getDomain();
//            String userName = payload.getUsername();
//            ProcessVersionsType idsDa = new ProcessVersionsType();
//            for (ProcessVersionIdType cpf : payload.getProcessVersionIds().getProcessVersionId()) {
//                ProcessVersionType idDa = new ProcessVersionType();
//                idsDa.getProcessVersion().add(idDa);
//                idDa.setProcessId(cpf.getProcessId());
//                idDa.setVersionName(cpf.getVersionName());
//            }
//
//            // Send message to DA to get selected canonicals
//            List<CanonicalType> canonicals = client.ReadCanonicals(idsDa, true);
//
//            // Process merge of returned canonicals
//            String algorithm = payload.getAlgorithm();
//            boolean removeEntanglements = false;
//            double modelthreshold = 0;
//            double labelthreshold = 0;
//            double contextthreshold = 0;
//            double skipnweight = 0;
//            double subnweight = 0;
//            double skipeweight = 0;
//            for (ParameterType p : payload.getParameters().getParameter()) {
//                if ("modelthreshold".equals(p.getName())) {
//                    modelthreshold = p.getValue();
//                } else if ("labelthreshold".equals(p.getName())) {
//                    labelthreshold = p.getValue();
//                } else if ("contextthreshold".equals(p.getName())) {
//                    contextthreshold = p.getValue();
//                } else if ("skipnweight".equals(p.getName())) {
//                    skipnweight = p.getValue();
//                } else if ("subnweight".equals(p.getName())) {
//                    subnweight = p.getValue();
//                } else if ("skipeweight".equals(p.getName())) {
//                    skipeweight = p.getValue();
//                } else if ("removeent".equals(p.getName())) {
//                    removeEntanglements = p.getValue() == 1 ? true : false;
//                }
//            }
//
//            LinkedList<CanonicalProcessType> toMerge = new LinkedList<CanonicalProcessType>();
//
//            JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
//            Unmarshaller u = jc.createUnmarshaller();
//            // mergedSources will be given to DA
//            Map<Integer, String> mergedSources = new HashMap<Integer, String>();
//
//            for (CanonicalType canonical : canonicals) {
//                // search canonical model
//                DataHandler document_cpf = canonical.getCpf();
//                InputStream document_is = document_cpf.getInputStream();
//                JAXBElement<CanonicalProcessType> documentRootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(document_is);
//                CanonicalProcessType documentCpf = documentRootElement.getValue();
//                toMerge.add(documentCpf);
//                mergedSources.put(canonical.getProcessId(), canonical.getVersionName());
//            }
//            CanonicalProcessType merged =
//                    MergeProcesses.mergeProcesses(
//                            toMerge, removeEntanglements, algorithm,
//                            modelthreshold, labelthreshold, contextthreshold, skipnweight, subnweight, skipeweight);
//
//            Marshaller m = jc.createMarshaller();
//            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//            JAXBElement<CanonicalProcessType> rootcpf = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(merged);
//            ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();
//            m.marshal(rootcpf, cpf_xml);
//            InputStream cpf_is = new ByteArrayInputStream(cpf_xml.toByteArray());
//
//            // Send message to DA to save merged process
//            ProcessSummaryType process = client.StoreCpf(processName, versionName, domain, userName, cpf_is, mergedSources);
//            ProcessSummaryType processM = new ProcessSummaryType();
//            VersionSummaryType versionM = new VersionSummaryType();
//
//            processM.getVersionSummaries().add(versionM);
//            processM.setId(process.getId());
//            processM.setLastVersion(process.getLastVersion());
//            processM.setName(process.getName());
//            processM.setDomain(process.getDomain());
//            processM.setOwner(process.getOwner());
//            versionM.setCreationDate(process.getVersionSummaries().get(0).getCreationDate());
//            versionM.setLastUpdate(process.getVersionSummaries().get(0).getLastUpdate());
//            versionM.setName(process.getVersionSummaries().get(0).getName());
//            versionM.setRanking(process.getVersionSummaries().get(0).getRanking());
//            res.setProcessSummary(processM);
//            result.setCode(0);
//            result.setMessage("");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            result.setCode(-1);
//            result.setMessage(ex.getMessage());
//        }
//        return res;
//    }
//


    public void setClient(ToolboxDataAccessClient client) {
        this.client = client;
    }

}

