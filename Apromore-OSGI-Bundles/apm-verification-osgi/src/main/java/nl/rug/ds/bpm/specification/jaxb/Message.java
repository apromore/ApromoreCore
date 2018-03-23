package nl.rug.ds.bpm.specification.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by Heerko Groefsema on 29-Sep-17.
 */
@XmlRootElement
public class Message {
	private String hold, fail;
	
	public Message() {}
	
	public Message(String hold, String fail) {
		setHold(hold);
		setFail(fail);
	}
	
	@XmlAttribute(required = true)
	public void setHold(String hold) { this.hold = hold; }
	public String getHold() { return hold; }
	
	@XmlAttribute(required = true)
	public void setFail(String fail) { this.fail = fail; }
	public String getFail() { return fail; }
}
