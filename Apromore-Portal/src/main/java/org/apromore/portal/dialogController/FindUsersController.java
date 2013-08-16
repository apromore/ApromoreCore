package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apromore.model.UserType;
import org.apromore.portal.common.FolderTreeNodeTypes;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * Controller used to find user in apromore and display them. usually for Security.
 */
public class FindUsersController extends BaseController {

    private MainController mainController;
    private Button btnSave;
    private Textbox txtSearch;
    private Listbox lstUsers;

    @SuppressWarnings("unchecked")
    public FindUsersController(final SecuritySetupController securitySetupController, Window win) throws DialogException {
        this.mainController = securitySetupController.getMainController();
        this.lstUsers = (Listbox)win.getFellow("findUsers").getFellow("lstUsers");
        Button btnSearch = (Button) win.getFellow("findUsers").getFellow("btnSearch");
        Button btnClear = (Button) win.getFellow("findUsers").getFellow("btnClear");
        this.btnSave = (Button)win.getFellow("findUsers").getFellow("btnSave");
        this.txtSearch = (Textbox)win.getFellow("findUsers").getFellow("txtSearch");
        btnSave.setDisabled(true);
        lstUsers.setPageSize(4);

        btnClear.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        txtSearch.setText("");
                        lstUsers.getItems().clear();
                        lstUsers.setVisible(false);
                        btnSave.setDisabled(true);
                    }
                });
        btnSave.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        Integer selectedId = UserSessionManager.getCurrentSecurityItem();
                        FolderTreeNodeTypes selectedType = UserSessionManager.getCurrentSecurityType();
                        if (selectedId == null){
                            Messagebox.show("Please select an item from a tree on the left.", "Attention", Messagebox.OK,
                                    Messagebox.ERROR);
                            return;
                        }
                        Set<Listitem> items = lstUsers.getSelectedItems();
                        if (items.size() > 0){
                            List<Listitem> processedItems = new ArrayList<>();
                            for (Listitem item : items){
                                List<Component> cells = item.getChildren();
                                if (cells.size() == 6){
                                    Checkbox chkWrite = (Checkbox)cells.get(3).getChildren().get(0);
                                    Checkbox chkOwner = (Checkbox)cells.get(4).getChildren().get(0);
                                    Label lblId = (Label)cells.get(5).getChildren().get(0);
                                    if (chkWrite != null && chkOwner != null && lblId != null){
                                        String message = "";
                                        if (selectedType == FolderTreeNodeTypes.Folder){
                                            message = mainController.getService().saveFolderPermissions(selectedId, lblId.getValue(), true, chkWrite.isChecked(), chkOwner.isChecked());                                            
                                        }
                                        else if (selectedType == FolderTreeNodeTypes.Process){
                                            message = mainController.getService().saveProcessPermissions(selectedId, lblId.getValue(), true, chkWrite.isChecked(), chkOwner.isChecked());
                                        }
                                        if (message.isEmpty()){                                                                                        
                                            securitySetupController.getPermissionsController().loadUsers(UserSessionManager.getCurrentSecurityItem(), UserSessionManager.getCurrentSecurityType());
                                            processedItems.add(item);
                                        }
                                        else{
                                            Messagebox.show(message, "Error", Messagebox.OK, Messagebox.ERROR);
                                        }
                                    }
                                }
                            }
                            
                            if (processedItems.size() > 0){
                                Messagebox.show("Successfully saved permissions.", "Success", Messagebox.OK,
                                        Messagebox.INFORMATION);
                                lstUsers.getItems().clear();
//                                for (int i = 0; i < processedItems.size(); i++){
//                                    lstUsers.getSelectedItems().remove(processedItems.get(i));
//                                }
                            }
                        }
                        else{
                            Messagebox.show("Please select at least one record to save.", "Attention", Messagebox.OK,
                                    Messagebox.ERROR);
                        }
                    }
                });
        
        btnSearch.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        if (txtSearch.getText().isEmpty()) {
                            Messagebox.show("Please type search text", "Attention", Messagebox.OK,
                                    Messagebox.ERROR);
                        } else {
                            boolean hasOwnership = UserSessionManager.getCurrentSecurityOwnership();

                            lstUsers.getItems().clear();
                            List<UserType> users = mainController.getService().searchUsers(txtSearch.getText().replace("*", "%"));
                            lstUsers.setVisible(users.size() > 0);
                            btnSave.setDisabled(users.size() == 0 || !hasOwnership);

                            for (UserType user : users) {
                                if (!(user.getId().equals(UserSessionManager.getCurrentUser().getId()))) {

                                    Listitem newItem = new Listitem();

                                    Listcell cellEmpty = new Listcell();
                                    newItem.appendChild(cellEmpty);

                                    newItem.appendChild(new Listcell(user.getUsername()));

                                    Checkbox chkRead = new Checkbox();
                                    chkRead.setChecked(true);
                                    chkRead.setDisabled(true);
                                    Listcell cellRead = new Listcell();
                                    cellRead.appendChild(chkRead);
                                    newItem.appendChild(cellRead);

                                    Checkbox chkWrite = new Checkbox();
                                    chkWrite.setChecked(false);
                                    Listcell cellWrite = new Listcell();
                                    cellWrite.appendChild(chkWrite);
                                    newItem.appendChild(cellWrite);

                                    Checkbox chkOwner = new Checkbox();
                                    chkOwner.setChecked(false);
                                    Listcell cellOwner = new Listcell();
                                    cellOwner.appendChild(chkOwner);
                                    newItem.appendChild(cellOwner);

                                    Label lblId = new Label(user.getId());
                                    lblId.setVisible(false);
                                    Listcell cellId = new Listcell();
                                    cellId.appendChild(lblId);
                                    newItem.appendChild(cellId);

                                    lstUsers.getItems().add(newItem);
                                }
                            }
                        }
                    }
                });
    }
}
