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

import org.apromore.portal.exception.ExceptionDao;
import org.apromore.model.NativeTypesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import javax.xml.bind.JAXBException;
import java.text.ParseException;
import java.util.Date;
import java.util.Vector;

public class ProcessDetailsController extends BaseController {

    private MainController mainC; // the main controller
    private BaseListboxController processTableC; // the controller associated to
    private Window processDetailsW; // the view associated to
    private Textbox nameT; // process name
    private Textbox originalLanguageT; // process original native language
    private Textbox domainT; // process domain
    private Textbox versionT; // version name
    private Textbox rankingT; // process ranking
    private Textbox versionNumberT; // process version ranking
    private Datebox creationDateD; // version creation date
    private Datebox lastUpdateD; // version last update
    private Listbox exportL; // choice of export formats
    private Window graphicW; // the window where to display process graphical
    // popup
    private Image processImage; // the actual image to display in the window
    private Label pushToSign; // the message displayed when no user connected

    private class HistoryElement {
        Object value;
        Integer nb;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Integer getNb() {
            return nb;
        }

        public void setNb(Integer nb) {
            this.nb = nb;
        }
    }

    private Vector<HistoryElement> processNameH;
    private Vector<HistoryElement> originalLanguageH;
    private Vector<HistoryElement> domainH;
    private Vector<HistoryElement> versionNameH;
    private Vector<HistoryElement> creationDateH;
    private Vector<HistoryElement> lastUpdateH;
    private Vector<HistoryElement> rankingH;
    private Vector<HistoryElement> versionNumberH;

    public ProcessDetailsController(MainController mainController, BaseListboxController processtable) throws JAXBException,
            ExceptionDao {
        this.mainC = mainController;
        this.processTableC = processtable;
        this.processDetailsW = (Window) mainC.getFellow("processdetailscomp") .getFellow("processdetails");

        this.nameT = (Textbox) processDetailsW.getFellow("nameT");
        this.originalLanguageT = (Textbox) processDetailsW .getFellow("originalLanguageT");
        this.domainT = (Textbox) processDetailsW.getFellow("domainT");
        this.versionT = (Textbox) processDetailsW.getFellow("versionT");
        this.rankingT = (Textbox) processDetailsW.getFellow("rankingT");
        this.versionNumberT = (Textbox) processDetailsW.getFellow("versionNumberT");
        this.creationDateD = (Datebox) processDetailsW.getFellow("creationDateD");
        this.lastUpdateD = (Datebox) processDetailsW.getFellow("lastUpdateD");
        this.exportL = (Listbox) processDetailsW.getFellow("exportL");

        this.graphicW = (Window) processDetailsW.getFellow("graphicW");
        this.processImage = (Image) graphicW.getFellow("processImage");
        this.pushToSign = (Label) graphicW.getFellow("pushToSign");

        processNameH = new Vector<>();
        originalLanguageH = new Vector<>();
        domainH = new Vector<>();
        versionNameH = new Vector<>();
        creationDateH = new Vector<>();
        lastUpdateH = new Vector<>();
        rankingH = new Vector<>();

        NativeTypesType formats = getService().readNativeTypes();
        for (int i = 0; i < formats.getNativeType().size(); i++) {
            Listitem format = new Listitem();
            format.setLabel(formats.getNativeType().get(i).getFormat());
            this.exportL.appendChild(format);
        }
    }


    /**
     * Add to the data displayed in the window processDetailsW those related to
     * version associated with process
     *
     * @param process
     * @param version TODO
     * @throws javax.xml.bind.JAXBException
     * @throws org.apromore.portal.exception.ExceptionDao
     * @throws NumberFormatException
     * @throws java.text.ParseException
     */
    public void displayProcessVersionDetails(ProcessSummaryType process, VersionSummaryType version, Boolean displayed)
            throws NumberFormatException, ExceptionDao, JAXBException,
            ParseException {

        String processName = process.getName();
        String language = process.getOriginalNativeType();
        String domain = process.getDomain();
        String ranking = version.getRanking();
        String creationDate = version.getCreationDate();
        String lastUpdate = version.getLastUpdate();
        String versionName = version.getName();
        String versionNumber = version.getVersionNumber();

        /*
           * for each grid attribute is associated an history whose elements are
           * of the form of <v,n>: v is a value for n selected process versions n
           * > 0 for each history, if singleton {<v,n>} then display v in the
           * corresponding grid attribute otherwise don't display anything
           */
        if (displayed) {
            insertInHistory(processName, this.processNameH);
            insertInHistory(language, this.originalLanguageH);
            insertInHistory(domain, this.domainH);
            insertInHistory(ranking, this.rankingH);
            insertInHistory(creationDate, this.creationDateH);
            insertInHistory(lastUpdate, this.lastUpdateH);
            insertInHistory(versionName, this.versionNameH);
            insertInHistory(versionNumber, this.versionNumberH);
        } else {
            dropFromHistory(processName, this.processNameH);
            dropFromHistory(language, this.originalLanguageH);
            dropFromHistory(domain, this.domainH);
            dropFromHistory(ranking, this.rankingH);
            dropFromHistory(creationDate, this.creationDateH);
            dropFromHistory(lastUpdate, this.lastUpdateH);
            dropFromHistory(versionName, this.versionNameH);
            dropFromHistory(versionNumber, this.versionNumberH);
        }

        if (this.processNameH.size() == 1) {
            this.nameT.setValue((String) this.processNameH.get(0).getValue());
        } else {
            this.nameT.setValue("");
        }
        if (this.versionNumberH.size() == 1) {
            this.versionNumberT.setValue((String) this.versionNumberH.get(0).getValue());
        } else {
            this.versionNumberT.setValue("");
        }
        if (this.originalLanguageH.size() == 1) {
            this.originalLanguageT.setValue((String) this.originalLanguageH.get(0).getValue());
        } else {
            this.originalLanguageT.setValue("");
        }
        if (domainH.size() == 1) {
            this.domainT.setValue((String) this.domainH.get(0).getValue());
        } else {
            this.domainT.setValue("");
        }
        if (rankingH.size() == 1) {
            this.rankingT.setValue((String) this.rankingH.get(0).getValue());
        } else {
            this.rankingT.setValue("");
        }
        if (this.lastUpdateH.size() == 1) {
            this.lastUpdateD.setValue((Date) this.lastUpdateH.get(0).getValue());
        } else {
            this.lastUpdateD.setValue(null);
        }
        if (this.creationDateH.size() == 1) {
            this.creationDateD.setValue((Date) this.creationDateH.get(0).getValue());
        } else {
            this.creationDateD.setValue(null);
        }
        if (this.versionNameH.size() == 1) {
            this.versionT.setValue((String) this.versionNameH.get(0).getValue());
        } else {
            this.versionT.setValue("");
        }
    }

    /**
     * insert value into history. history is kept sorted according to v. for
     * each <v,n> in history n > 0
     *
     * @param value
     * @param history
     */
    private void insertInHistory(Object value, Vector<HistoryElement> history) {
        int i = 0;
        while (i < history.size() && !value.equals(history.get(i).getValue())) {
            i++;
        }
        if (i == history.size()) {
            HistoryElement el = new HistoryElement();
            el.setValue(value);
            el.setNb(1);
            history.add(el);
        } else {
            if (value.equals(history.get(i).getValue())) {
                history.get(i).setNb(1 + history.get(i).getNb());
            } else {
                HistoryElement el = new HistoryElement();
                el.setValue(value);
                el.setNb(1);
                history.add(i, el);
            }
        }
    }

    private void dropFromHistory(Object value, Vector<HistoryElement> history) {
        int i = 0;
        while (i < history.size() && !value.equals(history.get(i).getValue())) {
            i++;
        }
        if (i != history.size()) {
            if (history.get(i).getNb() > 1) {
                history.get(i).setNb(history.get(i).getNb() - 1);
            } else {
                history.remove(i);
            }
        }
    }

    public BaseListboxController getProcessTableC() {
        return processTableC;
    }

    public void setProcessTableC(BaseListboxController processTableC) {
        this.processTableC = processTableC;
    }

    public Window getProcessDetailsW() {
        return processDetailsW;
    }

    public void setProcessDetailsW(Window processDetailsW) {
        this.processDetailsW = processDetailsW;
    }

    public Textbox getNameT() {
        return nameT;
    }

    public void setNameT(Textbox nameT) {
        this.nameT = nameT;
    }

    public Textbox getOriginalLanguageT() {
        return originalLanguageT;
    }

    public void setOriginalLanguageT(Textbox originalLanguageT) {
        this.originalLanguageT = originalLanguageT;
    }

    public Textbox getDomainT() {
        return domainT;
    }

    public void setDomainT(Textbox domainT) {
        this.domainT = domainT;
    }

    public Textbox getRankingT() {
        return rankingT;
    }

    public void setRankingT(Textbox rankingT) {
        this.rankingT = rankingT;
    }

    public Textbox getVersionNumberT() {
        return versionNumberT;
    }

    public void setVersionNumberT(Textbox versionNumberT) {
        this.versionNumberT = versionNumberT;
    }

    public Datebox getCreationDateD() {
        return creationDateD;
    }

    public void setCreationDateD(Datebox creationDateD) {
        this.creationDateD = creationDateD;
    }

    public Datebox getLastUpdateD() {
        return lastUpdateD;
    }

    public void setLastUpdateD(Datebox lastUpdateD) {
        this.lastUpdateD = lastUpdateD;
    }

    public Listbox getExportL() {
        return exportL;
    }

    public void setExportL(Listbox exportL) {
        this.exportL = exportL;
    }

    public Window getGraphicW() {
        return graphicW;
    }

    public void setGraphicW(Window graphicW) {
        this.graphicW = graphicW;
    }

    public Image getProcessImage() {
        return processImage;
    }

    public void setProcessImage(Image processImage) {
        this.processImage = processImage;
    }

    public Label getPushToSign() {
        return pushToSign;
    }

    public void setPushToSign(Label pushToSign) {
        this.pushToSign = pushToSign;
    }

    public Vector<HistoryElement> getProcessNameH() {
        return processNameH;
    }

    public void setProcessNameH(Vector<HistoryElement> processNameH) {
        this.processNameH = processNameH;
    }

    public Vector<HistoryElement> getOriginalLanguageH() {
        return originalLanguageH;
    }

    public void setOriginalLanguageH(Vector<HistoryElement> originalLanguageH) {
        this.originalLanguageH = originalLanguageH;
    }

    public Vector<HistoryElement> getDomainH() {
        return domainH;
    }

    public void setDomainH(Vector<HistoryElement> domainH) {
        this.domainH = domainH;
    }

    public Vector<HistoryElement> getCreationDateH() {
        return creationDateH;
    }

    public void setCreationDateH(Vector<HistoryElement> creationDateH) {
        this.creationDateH = creationDateH;
    }

    public Vector<HistoryElement> getLastUpdateH() {
        return lastUpdateH;
    }

    public void setLastUpdateH(Vector<HistoryElement> lastUpdateH) {
        this.lastUpdateH = lastUpdateH;
    }

    public Vector<HistoryElement> getRankingH() {
        return rankingH;
    }

    public void setRankingH(Vector<HistoryElement> rankingH) {
        this.rankingH = rankingH;
    }

    public Textbox getVersionT() {
        return versionT;
    }

    public void setVersionT(Textbox versionT) {
        this.versionT = versionT;
    }

}
