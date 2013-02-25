package org.apromore.dao.dataObject;

public class EdgeDO {
	
	private Integer id;
	private Integer sourceId;
	private Integer targetId;
	private Integer contentId;


	public Integer getId() {
		return id;
	}
	
	public void setId(final Integer edgeId) {
		this.id = edgeId;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(final Integer sourceVId) {
		this.sourceId = sourceVId;
	}

	public Integer getTargetId() {
		return targetId;
	}

	public void setTargetId(final Integer targetVId) {
		this.targetId = targetVId;
	}

	public Integer getContentId() {
		return contentId;
	}

	public void setContentId(final Integer newContentId) {
		this.contentId = newContentId;
	}
}
