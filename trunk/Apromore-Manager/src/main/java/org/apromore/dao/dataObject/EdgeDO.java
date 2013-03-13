package org.apromore.dao.dataObject;

import javax.persistence.Cacheable;

import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;

@Cacheable(true)
@org.eclipse.persistence.annotations.Cache(type = CacheType.WEAK, isolation = CacheIsolationType.SHARED, expiry = 60000, size = 10000, alwaysRefresh = true, disableHits = true, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
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
