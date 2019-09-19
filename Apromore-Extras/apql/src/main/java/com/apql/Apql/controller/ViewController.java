/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.apql.Apql.controller;

import javax.swing.*;
import javax.swing.tree.TreeNode;

import com.apql.Apql.popup.PopupFrame;
import com.apql.Apql.table.TableProcess;
import com.apql.Apql.tree.DraggableNodeFolder;
import com.apql.Apql.tree.DraggableNodeProcess;

import java.util.*;

public class ViewController {

	private static ViewController controller;

	private PopupFrame popup;
	private boolean popupOpen = false;

    private String username;
    private String idSession;
    private String userID;
    private JTree folderProcessTree;
    private JRadioButton allVersion;
    private JRadioButton latestVersion;
    private JRadioButton chooseVersion;
    private JPanel treeVersionPanel;
    private JScrollPane resumeScroll;
    private JTextField nameQuery;
    private TableProcess tableProcess;

    private JButton ok;
    private JButton clear;
    private JButton queriedModel;

    public JApplet getApplet() {
        return applet;
    }

    public void setApplet(JApplet applet) {
        this.applet = applet;
    }

    private JApplet applet;

    public final ImageIcon iconProc = new ImageIcon(getClass().getResource("/img/svg/bpmn_model.svg"));
    public final ImageIcon iconFold = new ImageIcon(getClass().getResource("/img/icon/svg/folder_icon.svg"));
    public final ImageIcon iconHome = new ImageIcon(getClass().getResource("/img/icon/svg/folder_home.svg"));
    public final ImageIcon iconKeyword = new ImageIcon(getClass().getResource("/img/icons/key.png"));

    public static final String ALLVERSIONS="All versions";
    public static final String LATESTVERSION="Latest version";
    public static final String CHOOSEVERSION="Choose version";
    public static final String OK="Ok";
    public static final String CLEAR="Clear";
    public static final String QUERIEDMODELS ="Queried Models";
    public static final String EXPAND ="Expand";
    public static final String COLLAPSE ="Collapse";
    public static final String LOAD="Load";
    public static final String SAVE="Save";
    public static final String FILE="File";
    public static final int ICONFOLDER=0;
    public static final int ICONPROCESS=1;
    public static final int ICONHOME=2;
    public static final int ICONKEY=3;


    private ViewController() {
    }

    public static ViewController getController() {
        if (controller == null) {
            controller = new ViewController();
        }
        return controller;
    }

    public void clear(){
//        controller=null;
        popupOpen=false;
        idSession=null;
        userID=null;
        folderProcessTree=null;
        allVersion=null;
        latestVersion=null;
        chooseVersion=null;
        treeVersionPanel=null;
//        versionTable=null;
        resumeScroll=null;
        nameQuery=null;
        tableProcess=null;
//        if(controller!=null)
//            controller.clear();
//        applet=null;
    }

    public void setTableProcess(TableProcess table){
        this.tableProcess=table;
    }

    public TableProcess getTableProcess(){
        return tableProcess;
    }

    public JTextField getNameQuery() {
        return nameQuery;
    }

    public void setNameQuery(JTextField nameQuery) {
        this.nameQuery = nameQuery;
    }

    public void setResumeScroll(JScrollPane resumeScroll){
        this.resumeScroll=resumeScroll;
    }

    public JScrollPane getResumeScroll(){
        return resumeScroll;
    }

//    public void setVersionTable(VersionTable versionTable){
//        this.versionTable = versionTable;
//    }
//
//    public VersionTable getVersionTable(){
//        return versionTable;
//    }

    public void setTreeVersionPanel(JPanel treeVersionPanel){
        this.treeVersionPanel=treeVersionPanel;
    }

    public JTree getFolderProcessTree() {
        return folderProcessTree;
    }

    public void setFolderProcessTree(JTree folderProcessTree) {
        this.folderProcessTree = folderProcessTree;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

	public boolean isPopupOpen() {
		return popupOpen;
	}

	public void setPopupOpen(boolean popupOpen) {
		this.popupOpen = popupOpen;
	}

	public PopupFrame getPopup() {
		return popup;
	}

	public void setPopup(PopupFrame popup) {
		this.popup = popup;
	}

    public void setIdSession(String idSession) {
        this.idSession=idSession;
    }

    public String getIdSession() {
        return idSession;
    }

    public JRadioButton getRadioButton(String radioButton){
        if(radioButton.equals(ViewController.ALLVERSIONS))
            return allVersion;
        else if (radioButton.equals(ViewController.LATESTVERSION))
            return latestVersion;
        else if(radioButton.equals(ViewController.CHOOSEVERSION))
            return chooseVersion;
        return null;
    }

    public JPanel getTreeVersionPanel(){
        return treeVersionPanel;
    }

    public void setAllVersion(JRadioButton radioButton){
            allVersion =radioButton;
    }

    public void setLatestVersion(JRadioButton radioButton){
        latestVersion =radioButton;
    }

    public void setChooseVersion(JRadioButton chooseVersion){
        this.chooseVersion=chooseVersion;
    }

    public void setButton(JButton button, String buttonStr){
       switch(buttonStr){
           case ViewController.OK: this.ok=button;
           case ViewController.CLEAR: this.clear=button;
           case ViewController.QUERIEDMODELS: this.queriedModel=button;
       }
    }

    public JButton getButton(String buttonStr){
        switch(buttonStr){
            case ViewController.OK: return this.ok;
            case ViewController.CLEAR: return this.clear;
            case ViewController.QUERIEDMODELS: return this.queriedModel;
        }
        return null;
    }

    public ImageIcon getImageIcon(int icon){
        switch(icon){
            case ICONFOLDER: return iconFold;
            case ICONPROCESS: return iconProc;
            case ICONHOME: return iconHome;
            case ICONKEY: return iconKeyword;
        }
        return null;
    }

    public List<String> getProcesses(){
        TreeNode root = (TreeNode)folderProcessTree.getModel().getRoot();
        LinkedList<String> list=new LinkedList();
        LinkedList<Enumeration> queue=new LinkedList<>();
        queue.add(root.children());
        while(!queue.isEmpty()) {
            Enumeration e=queue.removeFirst();
            while (e.hasMoreElements()) {
                Object ob = e.nextElement();
                if (ob instanceof DraggableNodeProcess) {
                    list.add(((DraggableNodeProcess) ob).getName());
                } else if (ob instanceof DraggableNodeFolder) {
                    queue.addLast(((DraggableNodeFolder) ob).children());
                }
            }
        }
        return list;
    }

    @Override
    public String toString() {
        return "ViewController{" +
                "popup=" + popup +
                ", popupOpen=" + popupOpen +
                ", username='" + username + '\'' +
                ", idSession='" + idSession + '\'' +
                ", userID='" + userID + '\'' +
                ", folderProcessTree=" + folderProcessTree +
                ", allVersion=" + allVersion +
                ", latestVersion=" + latestVersion +
                ", chooseVersion=" + chooseVersion +
                ", treeVersionPanel=" + treeVersionPanel +
//                ", versionTable=" + versionTable +
                ", resumeScroll=" + resumeScroll +
                ", nameQuery=" + nameQuery +
                ", tableProcess=" + tableProcess +
                '}';
    }
}
