package org.apromore.processmining.plugins.xpdl.collections;

import java.util.ArrayList;
import java.util.List;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

public abstract class XpdlCollections<T extends XpdlElement> extends XpdlElement {

	protected List<T> list;

	public XpdlCollections(String tag) {
		super(tag);

		list = new ArrayList<T>();
	}

	public abstract T create();

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		T t = create();
		if (xpp.getName().equals(t.tag)) {
			t.importElement(xpp, xpdl);
			list.add(t);
			return true;
		}
		/*
		 * Unknown tag
		 */
		return false;
	}

	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		for (T t : list) {
			s += t.exportElement();
		}
		return s;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
	
	public void add2List(T t) {
		this.list.add(t);
	}
}
