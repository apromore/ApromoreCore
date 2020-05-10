/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.dialogController;

import org.apromore.manager.client.ManagerService;
import org.apromore.portal.exception.ExceptionDao;
import org.apromore.model.DomainsType;
import org.apromore.model.NativeTypesType;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class FileController extends BaseController {

    private MainController mainC;        // the main controller
    private MenuController menuC;        // the menu controller which created this

    private Window modelW;                // the window component controlled by this

    private Textbox modelNameT;            // the textbox to enter process name
    private Textbox modelVersionT;        // the textbox to enter version name
    private Textbox modelUriT;            // the textbox to enter uri
    private Hbox modelDomainH;            // the hbox which contains the following combobox
    private AutoComplete modelDomainCB; // the combobox to choose domain
    private Listbox modelNativeL;        // the listbox to choose native language
    private Button modelUploadB;        // the button to trigger upload
    private InputStream nativeProcessFormat; // the uploaded stream
    private Label modelUploadL;            // the label where to display name and size of uploaded file
    private Button modelValidB;            // the button to validate
    private Button modelCancelB;        // the button to cancel

    public FileController(MenuController menu, MainController main) {
        this.mainC = main;
        this.menuC = menu;

        final Window win = (Window) Executions.createComponents(
                "macros/model.zul", null, null);
        try {
            win.doModal();
            // get components
            this.modelW = (Window) win.getFellow("modelWindow");
            this.modelNameT = (Textbox) this.modelW.getFellow("modelNameT");
            this.modelVersionT = (Textbox) this.modelW.getFellow("modelVersionT");
            this.modelUriT = (Textbox) this.modelW.getFellow("modelUriT");
            this.modelDomainH = (Hbox) this.modelW.getFellow("modelDomainH");
            this.modelNativeL = (Listbox) this.modelW.getFellow("modelNativeL");
            this.modelUploadB = (Button) this.modelW.getFellow("modelUploadB");
            this.modelUploadL = (Label) this.modelW.getFellow("modelUploadL");
            this.modelValidB = (Button) this.modelW.getFellow("modelValidB");
            this.modelCancelB = (Button) this.modelW.getFellow("modelCancelB");

            // build list of native languages to choose in
            NativeTypesType formats = getService().readNativeTypes();

            for (int i = 0; i < formats.getNativeType().size(); i++) {
                Listitem format = new Listitem();
                format.setLabel(formats.getNativeType().get(i).getFormat());
                this.modelNativeL.appendChild(format);
            }

            // build combobox of existing domains
            DomainsType domains = getService().readDomains();
            List<String> domainList = domains.getDomain();

            this.modelDomainCB = new AutoComplete(domainList);
            this.modelDomainH.appendChild(this.modelDomainCB);

            // event listeners
            this.modelValidB.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    validNewModel();
                }
            });
            this.modelCancelB.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    cancelNewModel();
                }
            });
            this.modelUploadB.addEventListener("onUpload", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    uploadSpecification((UploadEvent) event);
             }
            });

        } catch (SuspendNotAllowedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    protected void uploadSpecification(UploadEvent event) {
        this.nativeProcessFormat = event.getMedia().getStreamData();
        String fileName = event.getMedia().getName();
        String format = event.getMedia().getContentType();

        this.modelUploadL.setValue(fileName);
        // file name default value for uri field
        if (this.modelUriT.getValue().compareTo("") == 0) {
            String[] uri = fileName.split(".");
            String root = uri[0];
            this.modelUriT.setValue(uri[0]);
        }
    }

    /**
     * Closes the window for entering process details
     */
    protected void cancelNewModel() {
        this.modelW.detach();
    }

    /**
     * Validates entries given as process details.
     * <p/>
     */
    protected void validNewModel() {
        String processName = this.modelNameT.getValue();
        String domain = (String) this.modelDomainCB.getSelectedItem().getValue();
        String versionName = this.modelVersionT.getValue();
        String uri = this.modelUriT.getValue();
        String originalLanguage = (String) this.modelNativeL.getSelectedItem().getValue();
    }

    private class AutoComplete extends Combobox {

        private List<String> dict;

        public AutoComplete(List<String> domains) {
            this.dict = domains;
            refresh(""); //init the child comboitems
        }

        public AutoComplete(String value, List<String> dict) {
            super(value); //it invokes setValue(), which inits the child comboitems
            this.dict = dict;
        }

        public void setValue(String value) {
            super.setValue(value);
            refresh(value); //refresh the child comboitems
        }

        /**
         * Listens what an user is entering.
         */
        public void onChanging(InputEvent evt) {
            refresh(evt.getValue());
        }

        /**
         * Refresh comboitem based on the specified value.
         */
        private void refresh(String val) {

            //int j = Arrays.binarySearch(this.dict, val);
            int j = 0;
            while (j < this.dict.size()
                    && this.dict.get(j).compareTo(val) < 0) j++;

            if (j < 0) j = -j - 1;

            Iterator it = getItems().iterator();
            for (int cnt = 10; --cnt >= 0 && j < this.dict.size() && this.dict.get(j).startsWith(val); ++j) {
                if (it != null && it.hasNext()) {
                    ((Comboitem) it.next()).setLabel(this.dict.get(j));
                } else {
                    it = null;
                    new Comboitem(this.dict.get(j)).setParent(this);
                }
            }

            while (it != null && it.hasNext()) {
                it.next();
                it.remove();
            }
        }
    }
}
