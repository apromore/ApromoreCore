package org.apromore.dao.dataObject;

import javax.persistence.Cacheable;

import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;

@Cacheable(true)
@org.eclipse.persistence.annotations.Cache(type = CacheType.WEAK, isolation = CacheIsolationType.SHARED, expiry = 60000, size = 10000, alwaysRefresh = true, disableHits = true, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class NodeDO {

	private Integer id;
    private String name;
    private String nodeType;
    private String graphType;
    private String originalId;
    private Boolean configuration = false;
    private Integer contentId;


	public Integer getId() {
		return id;
	}
	
	public void setId(final Integer vid) {
		this.id = vid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(final String vname) {
		this.name = vname;
	}
	
	public String getNodeType() {
		return nodeType;
	}
	
	public void setNodeType(final String vtype) {
		this.nodeType = vtype;
	}
	
	public String getGraphType() {
		return graphType;
	}
	
	public void setGraphType(final String gtype) {
		this.graphType = gtype;
	}
	
	public String getOriginalId() {
		return originalId;
	}
	
	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}
	
	public Boolean getConfiguration() {
		return configuration;
	}
	
	public void setConfiguration(Boolean configuration) {
		this.configuration = configuration;
	}
	
	public Integer getContentId() {
		return contentId;
	}
	
	public void setContentId(final Integer newContentId) {
		this.contentId = newContentId;
	}
}
