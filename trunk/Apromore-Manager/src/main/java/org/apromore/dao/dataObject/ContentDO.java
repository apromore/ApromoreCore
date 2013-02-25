package org.apromore.dao.dataObject;

/**
 * @author Chathura Ekanayake
 */
public class ContentDO {

	private Integer id;
    private String boundaryS;
	private String boundaryE;

	public Integer getId() {
		return id;
	}
	
	public void setId(final Integer contentId) {
		this.id = contentId;
	}
	
	public String getBoundaryE() {
		return boundaryE;
	}
	
	public void setBoundaryE(final String boundary1) {
		this.boundaryE = boundary1;
	}
	
	public String getBoundaryS() {
		return boundaryS;
	}
	
	public void setBoundaryS(final String boundary2) {
		this.boundaryS = boundary2;
	}
}
