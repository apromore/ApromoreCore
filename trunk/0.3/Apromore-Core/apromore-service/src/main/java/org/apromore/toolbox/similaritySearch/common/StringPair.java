package org.apromore.toolbox.similaritySearch.common;

public class StringPair{
	public String left;
	public String right;
	
	public boolean related = false;
	public boolean toRemove = false;
	public boolean chekced = false;
	
	public StringPair (String left, String right) {
		this.left =  left;
		this.right = right;
	}
	
	public void relate() {
		this.related = true;
	}
	
	public void markToRemove() {
		this.toRemove = true;
	}
}
