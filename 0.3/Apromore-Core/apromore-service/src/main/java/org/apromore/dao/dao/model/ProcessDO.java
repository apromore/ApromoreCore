package org.apromore.dao.dao.model;

/**
 * @author Chathura Ekanayake
 */
public class ProcessDO {

	private String processModelVersionId;
	private String processName;
	private String branchName;
	private String versionName;

	public String getProcessModelVersionId() {
		return processModelVersionId;
	}
	
	public void setProcessModelVersionId(String processModelVersionId) {
		this.processModelVersionId = processModelVersionId;
	}
	
	public String getProcessName() {
		return processName;
	}
	
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public String getBranchName() {
		return branchName;
	}
	
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	public String getVersionName() {
		return versionName;
	}
	
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
}
