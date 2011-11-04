package org.apromore.toolbox.service;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.ExceptionComputeSimilarity;
import org.apromore.exception.ExceptionReadCanonicals;
import org.apromore.model.AnnotationsType;
import org.apromore.model.CanonicalType;
import org.apromore.model.MergeProcessesInputMsgType;
import org.apromore.model.MergeProcessesOutputMsgType;
import org.apromore.model.ParameterType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdType;
import org.apromore.model.ProcessVersionType;
import org.apromore.model.ProcessVersionsType;
import org.apromore.model.ResultType;
import org.apromore.model.SearchForSimilarProcessesInputMsgType;
import org.apromore.model.SearchForSimilarProcessesOutputMsgType;
import org.apromore.model.VersionSummaryType;
import org.apromore.toolbox.da.ToolboxDataAccessClient;
import org.apromore.toolbox.similaritySearch.tools.MergeProcesses;
import org.apromore.toolbox.similaritySearch.tools.SearchForSimilarProcesses;
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

    /**
     * Do the Similarity Search across a number of models.
     *
     * @param payload the search for similar processors input message.
     * @return the result either with data or an error message.
     */
    public SearchForSimilarProcessesOutputMsgType searchForSimilarProcesses(SearchForSimilarProcessesInputMsgType payload) {
        LOGGER.info("Executing operation searchForSimilarProcesses");
        SearchForSimilarProcessesOutputMsgType res = new SearchForSimilarProcessesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            Boolean latestVersions = payload.isLatestVersions();
            ProcessVersionsType idsDa = new ProcessVersionsType();
            // idsDa is an empty list of processVersion, thus ReadCanonicals will return all
            List<CanonicalType> allCanonicals = client.ReadCanonicals(idsDa, latestVersions);
            // among allCanonicals, which one is the query?
            // allCanonicals is a list of tuples <pid, version, content> ordered on processId
            // If latestVersions, then query might not be present in allCanonicals
            // (this is the case when the query is not a latest version)
            // but the latest version of the same process might be in allCanonicals
            CanonicalType query = null;
            int i = 0;
            while (i < allCanonicals.size()
                    && allCanonicals.get(i).getProcessId() < payload.getProcessId()) {
                i++;
            }
            if (i < allCanonicals.size()) {
                // processId found, look for the version
                while (i < allCanonicals.size()
                        && allCanonicals.get(i).getProcessId() == payload.getProcessId()
                        && (allCanonicals.get(i).getVersionName().compareTo(payload.getVersionName()) != 0)) {
                    i++;
                }
            }
            if (i < allCanonicals.size()
                    && allCanonicals.get(i).getProcessId() == payload.getProcessId()
                    && (allCanonicals.get(i).getVersionName().compareTo(payload.getVersionName()) == 0)) {
                query = allCanonicals.get(i);
                // not necessary to have query in the collection to be searched
                // allCanonicals.remove(query);
            } else {

                // query process not found, latestVersions should be true
                if (!latestVersions) throw new ExceptionReadCanonicals("Couldn't read canonicals.");
                ProcessVersionType queryId = new ProcessVersionType();
                queryId.setProcessId(payload.getProcessId());
                queryId.setVersionName(payload.getVersionName());
                idsDa.getProcessVersion().clear();
                idsDa.getProcessVersion().add(queryId);
                List<CanonicalType> queryCanonical = client.ReadCanonicals(idsDa, false);
                // if 0 or more then 1 result occurs, there is something wrong..
                if (queryCanonical.size() != 1) throw new ExceptionReadCanonicals("Couldn't read canonicals.");
                allCanonicals.add(0, queryCanonical.get(0));
                query = queryCanonical.get(0);
            }
            // search canonical model
            JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
            Unmarshaller u = jc.createUnmarshaller();
            DataHandler search_cpf = query.getCpf();
            InputStream search_is = search_cpf.getInputStream();
            JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(search_is);
            CanonicalProcessType searchCpf = rootElement.getValue();

            String algorithm = payload.getAlgorithm();

            double modelthreshold = 0;
            double labelthreshold = 0;
            double contextthreshold = 0;
            double skipnweight = 0;
            double subnweight = 0;
            double skipeweight = 0;
            for (ParameterType p : payload.getParameters().getParameter()) {
                if ("modelthreshold".equals(p.getName())) {
                    modelthreshold = p.getValue();
                } else if ("labelthreshold".equals(p.getName())) {
                    labelthreshold = p.getValue();
                } else if ("contextthreshold".equals(p.getName())) {
                    contextthreshold = p.getValue();
                } else if ("skipnweight".equals(p.getName())) {
                    skipnweight = p.getValue();
                } else if ("subnweight".equals(p.getName())) {
                    subnweight = p.getValue();
                } else if ("skipeweight".equals(p.getName())) {
                    skipeweight = p.getValue();
                }
            }

            ProcessVersionsType similarProcesses = new ProcessVersionsType();
            similarProcesses.getProcessVersion().clear();
            double similarity;
            ProcessVersionType processVersion = null;
            for (int j = 0; j < allCanonicals.size(); j++) {
                try {
                    // the similarity search might fail on a canonical. This latter is ignored so
                    // the search is not interrupted
                    CanonicalType canonical = allCanonicals.get(j);
                    DataHandler document_cpf = canonical.getCpf();
                    InputStream document_is = document_cpf.getInputStream();
                    JAXBElement<CanonicalProcessType> documentRootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(document_is);
                    CanonicalProcessType documentCpf = documentRootElement.getValue();
                    similarity = SearchForSimilarProcesses.findProcessesSimilarity(
                            searchCpf, documentCpf, algorithm, labelthreshold, contextthreshold,
                            skipnweight, subnweight, skipeweight);
                    if (similarity >= modelthreshold) {
                        processVersion = new ProcessVersionType();
                        processVersion.setProcessId(canonical.getProcessId());
                        processVersion.setVersionName(canonical.getVersionName());
                        processVersion.setScore(similarity);
                        similarProcesses.getProcessVersion().add(processVersion);
                    }
                } catch (Exception e) {
                    LOGGER.info("Operation searchForSimilarProcesses failed. See process: "
                            + processVersion.getProcessId() + ", version: " + processVersion.getVersionName());
                    // throw new ExceptionComputeSimilarity(e.getMessage());
                }
            }
            if (similarProcesses.getProcessVersion().size() == 0)
                throw new ExceptionComputeSimilarity("Process model " + query.getProcessId()
                        + " version " + query.getVersionName() + " probably faulty");


            // Send a message to DA to get process summary of similar process
            ProcessSummariesType processSummariesDA = client.ReadProcessSummaries(similarProcesses);
            ProcessSummariesType processSummariesM = new ProcessSummariesType();
            for (ProcessSummaryType pDA : processSummariesDA.getProcessSummary()) {
                ProcessSummaryType pM = new ProcessSummaryType();
                processSummariesM.getProcessSummary().add(pM);
                pM.setDomain(pDA.getDomain());
                pM.setId(pDA.getId());
                pM.setLastVersion(pDA.getLastVersion());
                pM.setName(pDA.getName());
                pM.setOriginalNativeType(pDA.getOriginalNativeType());
                pM.setOwner(pDA.getOwner());
                pM.setRanking(pDA.getRanking());
                for (VersionSummaryType vDA : pDA.getVersionSummaries()) {
                    VersionSummaryType vM = new VersionSummaryType();
                    pM.getVersionSummaries().add(vM);
                    vM.setCreationDate(vDA.getCreationDate());
                    vM.setLastUpdate(vDA.getLastUpdate());
                    vM.setName(vDA.getName());
                    vM.setRanking(vDA.getRanking());
                    vM.setScore(vDA.getScore());
                    for (AnnotationsType aDA : vDA.getAnnotations()) {
                        AnnotationsType aM = new AnnotationsType();
                        vM.getAnnotations().add(aM);
                        aM.setNativeType(aDA.getNativeType());
                        aM.getAnnotationName().addAll(aDA.getAnnotationName());
                    }
                }
            }
            res.setProcessSummaries(processSummariesM);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }


    public MergeProcessesOutputMsgType mergeProcesses(MergeProcessesInputMsgType payload) {
        LOGGER.info("Executing operation mergeProcesses");
        MergeProcessesOutputMsgType res = new MergeProcessesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            String processName = payload.getProcessName();
            String versionName = payload.getVersionName();
            String domain = payload.getDomain();
            String userName = payload.getUsername();
            ProcessVersionsType idsDa = new ProcessVersionsType();
            for (ProcessVersionIdType cpf : payload.getProcessVersionIds().getProcessVersionId()) {
                ProcessVersionType idDa = new ProcessVersionType();
                idsDa.getProcessVersion().add(idDa);
                idDa.setProcessId(cpf.getProcessId());
                idDa.setVersionName(cpf.getVersionName());
            }

            // Send message to DA to get selected canonicals
            List<CanonicalType> canonicals = client.ReadCanonicals(idsDa, true);

            // Process merge of returned canonicals
            String algorithm = payload.getAlgorithm();
            boolean removeEntanglements = false;
            double modelthreshold = 0;
            double labelthreshold = 0;
            double contextthreshold = 0;
            double skipnweight = 0;
            double subnweight = 0;
            double skipeweight = 0;
            for (ParameterType p : payload.getParameters().getParameter()) {
                if ("modelthreshold".equals(p.getName())) {
                    modelthreshold = p.getValue();
                } else if ("labelthreshold".equals(p.getName())) {
                    labelthreshold = p.getValue();
                } else if ("contextthreshold".equals(p.getName())) {
                    contextthreshold = p.getValue();
                } else if ("skipnweight".equals(p.getName())) {
                    skipnweight = p.getValue();
                } else if ("subnweight".equals(p.getName())) {
                    subnweight = p.getValue();
                } else if ("skipeweight".equals(p.getName())) {
                    skipeweight = p.getValue();
                } else if ("removeent".equals(p.getName())) {
                    removeEntanglements = p.getValue() == 1 ? true : false;
                }
            }

            LinkedList<CanonicalProcessType> toMerge = new LinkedList<CanonicalProcessType>();

            JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
            Unmarshaller u = jc.createUnmarshaller();
            // mergedSources will be given to DA
            Map<Integer, String> mergedSources = new HashMap<Integer, String>();

            for (CanonicalType canonical : canonicals) {
                // search canonical model
                DataHandler document_cpf = canonical.getCpf();
                InputStream document_is = document_cpf.getInputStream();
                JAXBElement<CanonicalProcessType> documentRootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(document_is);
                CanonicalProcessType documentCpf = documentRootElement.getValue();
                toMerge.add(documentCpf);
                mergedSources.put(canonical.getProcessId(), canonical.getVersionName());
            }
            CanonicalProcessType merged =
                    MergeProcesses.mergeProcesses(
                            toMerge, removeEntanglements, algorithm,
                            modelthreshold, labelthreshold, contextthreshold, skipnweight, subnweight, skipeweight);

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement<CanonicalProcessType> rootcpf = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(merged);
            ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();
            m.marshal(rootcpf, cpf_xml);
            InputStream cpf_is = new ByteArrayInputStream(cpf_xml.toByteArray());

            // Send message to DA to save merged process
            ProcessSummaryType process = client.StoreCpf(processName, versionName, domain, userName, cpf_is, mergedSources);
            ProcessSummaryType processM = new ProcessSummaryType();
            VersionSummaryType versionM = new VersionSummaryType();

            processM.getVersionSummaries().add(versionM);
            processM.setId(process.getId());
            processM.setLastVersion(process.getLastVersion());
            processM.setName(process.getName());
            processM.setDomain(process.getDomain());
            processM.setOwner(process.getOwner());
            versionM.setCreationDate(process.getVersionSummaries().get(0).getCreationDate());
            versionM.setLastUpdate(process.getVersionSummaries().get(0).getLastUpdate());
            versionM.setName(process.getVersionSummaries().get(0).getName());
            versionM.setRanking(process.getVersionSummaries().get(0).getRanking());
            res.setProcessSummary(processM);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }



    public void setClient(ToolboxDataAccessClient client) {
        this.client = client;
    }

}

