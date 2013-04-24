package org.apromore.portal.dialogController;

import java.util.List;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.dialogController.renderer.VersionSummaryItemRenderer;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

public class ProcessVersionDetailController extends BaseDetailController {

    private static final long serialVersionUID = 3661234712204860492L;

    private final Listbox listBox;

    public ProcessVersionDetailController(MainController mainController) {
        super(mainController);

        this.listBox = ((Listbox) Executions.createComponents("macros/detail/processVersionsDetail.zul", getMainController(), null));

        getListBox().setItemRenderer(new VersionSummaryItemRenderer());
        getListBox().setModel(new ListModelList());

        appendChild(getListBox());
    }

    public void displayProcessVersions(ProcessSummaryType data) {
        getListModel().clearSelection();
        getListModel().clear();
        List<VersionSummaryType> versionSummaries = data.getVersionSummaries();
        getListModel().addAll(versionSummaries);
        if (versionSummaries.size() > 0) {
            getListModel().addSelection(versionSummaries.get(versionSummaries.size() - 1));
        }
    }

    protected ListModelList getListModel() {
        return (ListModelList) listBox.getListModel();
    }

    protected Listbox getListBox() {
        return listBox;
    }

    public void clearProcessVersions() {
        getListModel().clear();
    }

    public VersionSummaryType getSelectedVersion() {
        if (getListModel().getSelection().size() == 1) {
            return (VersionSummaryType) getListModel().getSelection().iterator().next();
        } else {
            return null;
        }
    }

}
