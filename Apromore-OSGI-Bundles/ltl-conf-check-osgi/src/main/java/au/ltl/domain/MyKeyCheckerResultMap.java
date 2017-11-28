package au.ltl.domain;

public class MyKeyCheckerResultMap {
	private String ruleName;
	private String taskId;
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ruleName == null) ? 0 : ruleName.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyKeyCheckerResultMap other = (MyKeyCheckerResultMap) obj;
		if (ruleName == null) {
			if (other.ruleName != null)
				return false;
		} else if (!ruleName.equals(other.ruleName))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}
	public MyKeyCheckerResultMap(String ruleName, String taskId) {
		super();
		this.ruleName = ruleName;
		this.taskId = taskId;
	}
	@Override
	public String toString() {
		return "MyKeyCheckerResultMap [ruleName=" + ruleName + ", taskId=" + taskId + "]";
	}
	
	
}
