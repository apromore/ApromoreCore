package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

public class Table  {


	/**
	 * Build a Listbox with headers defined in attributeList and rows in rows.
	 * 	multiple <=> selection of rows is permitted
	 * @param attributeList Vector<String>
	 * @param rows Vector<Vector<String>>
	 * @param multiple Boolean
	 * @return Listbox
	 * @throws Exception
	 * 
	 * TODO add an extra column for action(s) that could be applied on row
	 * 
	 * Each Listitem has an id. In a given Listitem, each Listcell as an id: Listitem id + attribute name
	 */
	public static Listbox buildTable (List<String> attributeList, 
			List<List<String>> rows, Boolean multiple) throws Exception {
		Listbox table = new Listbox();
		Listhead headers = new Listhead();
		table.appendChild(headers);
		table.setMultiple(multiple);
		headers.setSizable(true);

		List<String> currentrow = new ArrayList<String>();

		/* display table attributes */
		for (int i=0;i<attributeList.size();i++){
			Listheader lh = new Listheader();
			lh.setLabel(attributeList.get(i));
			lh.setSort("auto");

			headers.appendChild(lh);
		}

		//		TODO /* the last column for buttons to trigger action */
		//		Listheader lh = new Listheader();
		//		lh.setLabel("Actions");
		//		headers.appendChild(lh);

		/* display table content */
		for (int i=0;i<rows.size();i++){
			Listitem li = new Listitem();
			// to identify selected items
			li.setId(((Integer) i).toString());
			table.appendChild(li);
			currentrow = rows.get(i);
			for (int j=0;j<currentrow.size();j++) {
				Listcell lc = new Listcell();
				// to get values of designated Listcell
				lc.setId(((Integer) i).toString()+attributeList.get(j));
				lc.setLabel(currentrow.get(j));
				li.appendChild(lc);
			}
			/*
			 add buttons 
			Listcell lc = new Listcell();
			Button delete = new Button();
			Button edit = new Button();
			this.deleteButtons.add(delete);
			this.editButtons.add(edit);
			delete.setImage("img/cancel2.png");
			delete.setTooltiptext("delete");
			delete.setVisible(false);
			edit.setImage("img/edit.png");
			edit.setTooltiptext("edit");
			edit.setVisible(false);
			lc.appendChild(delete);
			lc.appendChild(edit);
			li.appendChild(lc);*/

			/* add listeners */
			/*final int processRowId = i;
			delete.addEventListener("onClick", new EventListener() {
				public void onEvent(Event event) {
					deleteProcessRow (event, processRowId);
				}
			});

			edit.addEventListener("onClick", new EventListener() {
				public void onEvent(Event event) {
					editProcessRow (event, processRowId);
				}
			});*/
		}
		return table;
	}

}
