package org.apromore.service.csvimporter.impl;

import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;


/**
 * The Class gridRendererController.
 */
public class gridRendererController implements RowRenderer<String[]> {

	private Integer AttribWidth;
	/*
	 * @see org.zkoss.zul.RowRenderer#render(org.zkoss.zul.Row, java.lang.Object, int)
	 * Append rows to the grid
	 */
	public void render(Row row, String[] data, int index) throws Exception  {
		for (int i = 0; i < data.length; i++) {
			Label lbl = new Label();
			lbl.setValue(data[i]);
			lbl.setWidth(this.AttribWidth + "px");
			lbl.setMultiline(false);
			lbl.setMaxlength(25);
			row.appendChild(lbl);
//			row.setStyle("height: 10px;");
		}
	}

	public void setAttribWidth(Integer attribWidth) {
		AttribWidth = attribWidth;
	}
}
