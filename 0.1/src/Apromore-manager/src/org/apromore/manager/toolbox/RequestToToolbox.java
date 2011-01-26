package org.apromore.manager.toolbox;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apromore.manager.commons.Constants;
import org.apromore.manager.exception.ExceptionMergeProcess;
import org.apromore.manager.exception.ExceptionSearchForSimilar;
import org.apromore.manager.model_portal.ProcessSummariesType;
import org.apromore.manager.model_portal.VersionSummaryType;
import org.apromore.manager.model_toolbox.AnnotationsType;
import org.apromore.manager.model_toolbox.MergeProcessesInputMsgType;
import org.apromore.manager.model_toolbox.ParameterType;
import org.apromore.manager.model_toolbox.ParametersType;
import org.apromore.manager.model_toolbox.ProcessVersionIdsType;
import org.apromore.manager.model_toolbox.ResultType;
import org.apromore.manager.model_toolbox.SearchForSimilarProcessesInputMsgType;

public class RequestToToolbox {

	private static final QName SERVICE_NAME = new QName(Constants.TOOLBOX_MANAGER_URI, Constants.TOOLBOX_MANAGER_SERVICE);
	private ToolboxManagerPortType port;

	public RequestToToolbox() {
		URL wsdlURL = ToolboxManagerService.WSDL_LOCATION;
		ToolboxManagerService ss = new ToolboxManagerService(wsdlURL, SERVICE_NAME);
		this.port = ss.getToolboxManager();
	}


	public org.apromore.manager.model_portal.ProcessSummariesType SearchForSimilarProcesses(
			int processId, String versionName,
			String method, ParametersType params) throws ExceptionSearchForSimilar {
		org.apromore.manager.model_toolbox.SearchForSimilarProcessesInputMsgType payload =
			new SearchForSimilarProcessesInputMsgType();
		payload.setAlgorithm(method);
		payload.setProcessId(processId);
		payload.setParameters(params);
		payload.setVersionName(versionName);
		org.apromore.manager.model_toolbox.SearchForSimilarProcessesOutputMsgType res =
			this.port.searchForSimilarProcesses(payload);
		ResultType result = res.getResult();
		if (result.getCode()==-1) {
			throw new ExceptionSearchForSimilar (result.getMessage());
		} else {
			org.apromore.manager.model_portal.ProcessSummariesType processesP = new ProcessSummariesType();
			for (org.apromore.manager.model_toolbox.ProcessSummaryType processT: 
				res.getProcessSummaries().getProcessSummary()) {
				org.apromore.manager.model_portal.ProcessSummaryType processP = 
					new org.apromore.manager.model_portal.ProcessSummaryType();
				processesP.getProcessSummary().add(processP);
				processP.setId(processT.getId());
				processP.setLastVersion(processT.getLastVersion());
				processP.setName(processT.getName());
				processP.setOwner(processT.getOwner());
				for (org.apromore.manager.model_toolbox.VersionSummaryType versionT:
					processT.getVersionSummaries()) {
					org.apromore.manager.model_portal.VersionSummaryType versionP =
						new VersionSummaryType();
					processP.getVersionSummaries().add(versionP);
					versionP.setCreationDate(versionT.getCreationDate());
					versionP.setLastUpdate(versionT.getLastUpdate());
					versionP.setName(versionT.getName());
					for (org.apromore.manager.model_toolbox.AnnotationsType annotT:
						versionT.getAnnotations()) {
						org.apromore.manager.model_portal.AnnotationsType annotP =
							new org.apromore.manager.model_portal.AnnotationsType();
						versionP.getAnnotations().add(annotP);
						annotP.setNativeType(annotT.getNativeType());
						annotP.getAnnotationName().addAll(annotT.getAnnotationName());
					}
				}
			}
			return processesP;
		}
	}


	public org.apromore.manager.model_portal.ProcessSummaryType MergeProcesses(String processName,
			String version, String username, String algo, ParametersType parameters,
			ProcessVersionIdsType ids) throws ExceptionMergeProcess {
		org.apromore.manager.model_portal.ProcessSummaryType mergedProcessP =
			new org.apromore.manager.model_portal.ProcessSummaryType();
		// build message to be sent to toolbox.
		org.apromore.manager.model_toolbox.MergeProcessesInputMsgType payload =
			new MergeProcessesInputMsgType();
		payload.setProcessName(processName);
		payload.setVersionName(version);
		payload.setAlgorithm(algo);
		payload.setParameters(parameters);
		payload.setProcessVersionIds(ids);
		payload.setUsername(username);
		org.apromore.manager.model_toolbox.MergeProcessesOutputMsgType res =
			this.port.mergeProcesses(payload);
		ResultType result = res.getResult();
		if (result.getCode()==-1) {
			throw new ExceptionMergeProcess (result.getMessage());
		} else {
			org.apromore.manager.model_toolbox.ProcessSummaryType mergedProcessT = res.getProcessSummary();
			mergedProcessP.setDomain(mergedProcessT.getDomain());
			mergedProcessP.setId(mergedProcessT.getId());
			mergedProcessP.setLastVersion(mergedProcessT.getLastVersion());
			mergedProcessP.setName(mergedProcessT.getName());
			mergedProcessP.setOwner(mergedProcessT.getOwner());
			org.apromore.manager.model_portal.VersionSummaryType versionP =
				new VersionSummaryType();
			versionP.setCreationDate(mergedProcessT.getVersionSummaries().get(0).getCreationDate());
			versionP.setLastUpdate(mergedProcessT.getVersionSummaries().get(0).getLastUpdate());
			versionP.setName(mergedProcessT.getVersionSummaries().get(0).getName());
			mergedProcessP.getVersionSummaries().add(versionP);
		}
		return mergedProcessP;
	}
}