package org.apromore.manager.toolbox;

import org.apromore.toolbox.service.ToolboxManager;

public class ManagerToolboxClient {

    private ToolboxManager manager;


//    public ProcessSummariesType SearchForSimilarProcesses(
//            int processId, String versionName, Boolean latestVersions,
//            String method, ParametersType params) throws ExceptionSearchForSimilar {
//        SearchForSimilarProcessesInputMsgType payload = new SearchForSimilarProcessesInputMsgType();
//        payload.setAlgorithm(method);
//        payload.setProcessId(processId);
//        payload.setLatestVersions(latestVersions);
//        payload.setParameters(params);
//        payload.setVersionName(versionName);
//        payload.setLatestVersions(latestVersions);
//        SearchForSimilarProcessesOutputMsgType res = manager.searchForSimilarProcesses(payload);
//        ResultType result = res.getResult();
//        if (result.getCode() == -1) {
//            throw new ExceptionSearchForSimilar(result.getMessage());
//        } else {
//            ProcessSummariesType processesP = new ProcessSummariesType();
//            for (ProcessSummaryType processT : res.getProcessSummaries().getProcessSummary()) {
//                ProcessSummaryType processP = new ProcessSummaryType();
//                processesP.getProcessSummary().add(processP);
//                processP.setId(processT.getId());
//                processP.setLastVersion(processT.getLastVersion());
//                processP.setName(processT.getName());
//                processP.setOwner(processT.getOwner());
//                processP.setRanking(processT.getRanking());
//                processP.setDomain(processT.getDomain());
//                processP.setOriginalNativeType(processT.getOriginalNativeType());
//                for (VersionSummaryType versionT : processT.getVersionSummaries()) {
//                    VersionSummaryType versionP = new VersionSummaryType();
//                    processP.getVersionSummaries().add(versionP);
//                    versionP.setCreationDate(versionT.getCreationDate());
//                    versionP.setLastUpdate(versionT.getLastUpdate());
//                    versionP.setName(versionT.getName());
//                    versionP.setRanking(versionT.getRanking());
//                    versionP.setScore(versionT.getScore());
//                    for (AnnotationsType annotT : versionT.getAnnotations()) {
//                        AnnotationsType annotP = new AnnotationsType();
//                        versionP.getAnnotations().add(annotP);
//                        annotP.setNativeType(annotT.getNativeType());
//                        annotP.getAnnotationName().addAll(annotT.getAnnotationName());
//                    }
//                }
//            }
//            return processesP;
//        }
//    }


//    public ProcessSummaryType MergeProcesses(String processName,String version, String domain, String username,
//            String algo, ParametersType parameters, ProcessVersionIdsType ids) throws ExceptionMergeProcess {
//        ProcessSummaryType mergedProcessP = new ProcessSummaryType();
//        // build message to be sent to toolbox.
//        MergeProcessesInputMsgType payload = new MergeProcessesInputMsgType();
//        payload.setProcessName(processName);
//        payload.setVersionName(version);
//        payload.setDomain(domain);
//        payload.setAlgorithm(algo);
//        payload.setParameters(parameters);
//        payload.setProcessVersionIds(ids);
//        payload.setUsername(username);
//        MergeProcessesOutputMsgType res = manager.mergeProcesses(payload);
//        ResultType result = res.getResult();
//        if (result.getCode() == -1) {
//            throw new ExceptionMergeProcess(result.getMessage());
//        } else {
//            ProcessSummaryType mergedProcessT = res.getProcessSummary();
//            mergedProcessP.setDomain(mergedProcessT.getDomain());
//            mergedProcessP.setId(mergedProcessT.getId());
//            mergedProcessP.setLastVersion(mergedProcessT.getLastVersion());
//            mergedProcessP.setName(mergedProcessT.getName());
//            mergedProcessP.setOwner(mergedProcessT.getOwner());
//            VersionSummaryType versionP = new VersionSummaryType();
//            versionP.setCreationDate(mergedProcessT.getVersionSummaries().get(0).getCreationDate());
//            versionP.setLastUpdate(mergedProcessT.getVersionSummaries().get(0).getLastUpdate());
//            versionP.setName(mergedProcessT.getVersionSummaries().get(0).getName());
//            mergedProcessP.getVersionSummaries().add(versionP);
//        }
//        return mergedProcessP;
//    }


    public void setManager(ToolboxManager manager) {
        this.manager = manager;
    }
}