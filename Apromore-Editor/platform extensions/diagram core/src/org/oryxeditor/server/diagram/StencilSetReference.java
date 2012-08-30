package org.oryxeditor.server.diagram;

/**
 * Represents a reference to a stencilset
 * <p>
 * Includes a namespace and an optional URL
 * @author philipp.maschke
 *
 */
public class StencilSetReference {

	String url;
	String namespace;
	
	
	public StencilSetReference(String namespace) {
		this(namespace, null);
	}
	public StencilSetReference(String namespace, String url) {
		this.namespace = namespace;
		this.url = url;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}
}
