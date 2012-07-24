package org.apromore.portal.dialogController;

import java.util.HashSet;
import java.util.Set;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;

public abstract class BaseListboxController extends BaseController {

	private static final long serialVersionUID = -4693075788311730404L;

	private final Listbox listBox;

	private final MainController mainController; // the main controller

	private final Paging pg;

	private final Button revertSelectionB;
	private final Button selectAllB;
	private final Button unselectAllB;
	private final Button refreshB;

	public BaseListboxController(MainController mainController,
			String componentId, ListitemRenderer itemRenderer) {
		super();
		setHflex("true");
		setVflex("true");
		
		this.mainController = mainController;

		this.listBox = createListbox(componentId);

		this.pg = (Paging) mainController.getFellow("pg");
		getListBox().setPaginal(pg);

		this.revertSelectionB = (Button) mainController
				.getFellow("revertSelectionB");
		this.unselectAllB = (Button) mainController.getFellow("unselectAllB");
		this.selectAllB = (Button) mainController.getFellow("selectAllB");
		this.refreshB = (Button) mainController.getFellow("refreshB");

		getListBox().setItemRenderer(itemRenderer);
		getListBox().setModel(new ListModelList());

		attachEvents();

		appendChild(listBox);
	}

	protected void attachEvents() {
		this.revertSelectionB.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				revertSelection();
			}
		});

		this.selectAllB.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				selectAll();
			}
		});

		this.unselectAllB.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				unselectAll();
			}
		});

		this.refreshB.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				refreshContent();
			}
		});
	}

	/**
	 * Refresh the currently displayed content from any kind of data source
	 */
	protected abstract void refreshContent();

	protected Listbox createListbox(String componentId) {
		return (Listbox) Executions.createComponents(componentId,
				getMainController(), null);
	}

	protected Listbox getListBox() {
		return listBox;
	}

	protected ListModelList getListModel() {
		return (ListModelList) listBox.getModel();
	}

	public void unselectAll() {
		getListBox().clearSelection();
	}

	public void selectAll() {
		getListBox().selectAll();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void revertSelection() {
		Set selectedItems = getListBox().getSelectedItems();
		Set reveredSet = new HashSet();
		for (Object obj : getListBox().getItems()) {
			if (!selectedItems.contains(obj)) {
				reveredSet.add(obj);
			}
		}
		getListBox().clearSelection();
		getListBox().setSelectedItems(reveredSet);
	}

	public MainController getMainController() {
		return mainController;
	}

}
