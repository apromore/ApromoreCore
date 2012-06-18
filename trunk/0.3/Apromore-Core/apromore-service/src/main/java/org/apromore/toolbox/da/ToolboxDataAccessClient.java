package org.apromore.toolbox.da;

import org.apromore.dao.DataAccessToolboxManager;

public class ToolboxDataAccessClient {

	private DataAccessToolboxManager manager;

//	public List<CanonicalType> ReadCanonicals(ProcessVersionsType ids, Boolean latestVersions) throws ExceptionReadCanonicals {
//		ReadCanonicalsInputMsgType payload = new ReadCanonicalsInputMsgType();
//		payload.setLatestVersions(latestVersions);
//		payload.getProcessVersion().addAll(ids.getProcessVersion());
//		ReadCanonicalsOutputMsgType res = manager.readCanonicals(payload);
//		ResultType result = res.getResult();
//		List<CanonicalType> canonicals = new ArrayList<CanonicalType>();
//		if (result.getCode()==-1) {
//			throw new ExceptionReadCanonicals (result.getMessage());
//		} else {
//			CanonicalsType canonicalsDA = res.getCanonicals();
//			canonicals = canonicalsDA.getCanonical();
//        }
//		return canonicals;
//	}
//
//	public ProcessSummariesType ReadProcessSummaries(ProcessVersionsType processes) throws ExceptionReadProcessSummaries {
//		ReadProcessSummaryInputMsgType payload = new ReadProcessSummaryInputMsgType();
//		payload.setProcessVersions(processes);
//		ProcessSummariesType toReturn = null;
//		ReadProcessSummariesOutputMsgType res = manager.readProcessSummaries(payload);
//		ResultType result = res.getResult();
//		if (result.getCode()==-1) {
//			throw new ExceptionReadProcessSummaries (result.getMessage());
//		} else {
//			toReturn = res.getProcessSummaries();
//		}
//		return toReturn;
//	}
	
//	public ProcessSummaryType StoreCpf (String processName, String versionName,
//			String domain, String userName,
//			InputStream cpf_is, Map<Integer,String> sources) throws IOException, ExceptionStoreCpf {
//		ProcessSummaryType process = null;
//		StoreCpfInputMsgType payload = new StoreCpfInputMsgType();
//		payload.setProcessName(processName);
//		payload.setUsername(userName);
//		payload.setVersion(versionName);
//		payload.setDomain(domain);
//		DataSource sourceCpf = new ByteArrayDataSource(cpf_is, "text/xml");
//		payload.setCpf(new DataHandler(sourceCpf));
//		MergedSources mergedSources = new MergedSources();
//		payload.setSources(mergedSources);
//		for (Entry<Integer,String> source : sources.entrySet()) {
//			MergedSource mergedSource = new MergedSource();
//			mergedSource.setProcessId(source.getKey());
//			mergedSource.setVersionName(source.getValue());
//			mergedSources.getMergedSource().add(mergedSource);
//		}
//		StoreCpfOutputMsgType res = manager.storeCpf(payload);
//		if (res.getResult().getCode()==-1) {
//			throw new ExceptionStoreCpf(res.getResult().getMessage());
//		} else {
//			process = res.getProcessSummary();
//		}
//		return process;
//	}



    public void setManager(DataAccessToolboxManager manager) {
        this.manager = manager;
    }

}
