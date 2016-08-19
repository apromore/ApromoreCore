/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.apql.Apql.tree;

import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import com.apql.Apql.popup.FolderLabel;
import com.apql.Apql.popup.ProcessLabel;
import com.apql.Apql.tree.draghandler.TreeTransferHandler;
import org.apromore.helper.Version;
import org.apromore.manager.client.ManagerService;
import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.UserType;
import org.apromore.model.VersionSummaryType;
import org.apromore.service.pql.ExternalId;

import javax.swing.*;
import java.awt.dnd.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by corno on 16/06/2014.
 */
public class FolderProcessTree extends JTree implements DragGestureListener,DragSourceListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(FolderProcessTree.class);

    private DraggableNodeTree root;
    private ManagerService manager;
    private UserType user;
    private ViewController controller;
    private DragSource source;
    private HashSet<String> sound;

    public FolderProcessTree(ManagerService manager, DatabaseService databaseService){
        source = new DragSource();
        source.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        this.root=new DraggableNodeFolder("0","Home","Home");
        this.controller=ViewController.getController();
        this.manager= manager;
        this.user=manager.readUserByUsername(controller.getUsername());
        try {
            sound=new HashSet<>(databaseService.getLabels("jbpt_petri_nets","external_id"));
        }catch(Exception ex){
            ex.printStackTrace();
        }

        //Raffaele's Modifications
        this.setDragEnabled(true);
        this.setTransferHandler(new TreeTransferHandler(QueryController.getQueryController(), controller));
    }

    public DraggableNodeTree createTree(){
        List<FolderType> folders=manager.getWorkspaceFolderTree(user.getId());
        for(FolderType ft: folders){
            root.add(createNode(ft,"Home/"+ft.getFolderName()+"/"));
        }

        addProcesses(root, 0, "Home/");

        return root;
    }

    private DraggableNodeTree createNode(FolderType root, String parentFolder){
        DraggableNodeTree node=new DraggableNodeFolder(root.getId().toString(),root.getFolderName(),parentFolder+root);
        List<FolderType> queue=manager.getSubFolders(user.getId(),root.getId());
        if(queue.size()>0){
            for(FolderType ft: queue)
                node.add(createNode(ft,parentFolder+"/"+ft.getFolderName()+"/"));
        }

        addProcesses(node, root.getId(), parentFolder);

        return node;
    }

    private void addProcesses(DraggableNodeTree node, int folderId, String folderPath) {
        final int PAGE_SIZE = 100;
        int page = 0;
        ProcessSummariesType processes;
        do {
            processes = manager.getProcesses(user.getId(), folderId, page, PAGE_SIZE);
            for(ProcessSummaryType pst: processes.getProcessSummary()) {

                List<VersionSummaryType> versionList = new LinkedList<>();

                for(VersionSummaryType vst : pst.getVersionSummaries()){
                    if(sound.contains(new ExternalId(pst.getId(), vst.getName(), new Version(vst.getVersionNumber())).toString())) {
                        versionList.add(vst);
                    }
                }
                if(!versionList.isEmpty()) {
                    pst.getVersionSummaries().retainAll(versionList);
                    DraggableNodeTree process = new DraggableNodeProcess(pst, folderPath + pst.getName());
                    node.add(process);
                }
            }
        } while(PAGE_SIZE * page++ + processes.getProcessSummary().size() < processes.getProcessCount());
    }

    public DraggableNodeTree getRoot(){
        return this.root;
    }

    public List<JLabel> getFoldersProcesses(String idFolder){
        System.out.println("ID FOLDER: "+idFolder);
        LinkedList<JLabel> results=new LinkedList<>();
        LinkedList<Enumeration> queue = new LinkedList<>();
        queue.add(root.children());
        if(idFolder.equals("0")){
            Enumeration e = root.children();
            while(e.hasMoreElements()){
                Object ob2 = e.nextElement();
                if(ob2 instanceof DraggableNodeFolder){
                    DraggableNodeFolder dnf=(DraggableNodeFolder)ob2;
                    results.add(new FolderLabel(Integer.parseInt(dnf.getId()),dnf.getName()));
                }else{
                    DraggableNodeProcess dnp=(DraggableNodeProcess)ob2;
                    results.add(new ProcessLabel(Integer.parseInt(dnp.getId()),dnp.getName(),dnp.getOriginalLanguage(),dnp.getLatestBranch(),dnp.getVersions()));
                }
            }
            return results;
        }
        while (!queue.isEmpty()) {
            Enumeration e = queue.removeFirst();
            while (e.hasMoreElements()) {
                Object ob = e.nextElement();
                if (ob instanceof DraggableNodeFolder && ((DraggableNodeFolder)ob).getId().equals(idFolder)) {
                    Enumeration en=((DraggableNodeFolder)ob).children();
                   while(en.hasMoreElements()){
                       Object ob2 = en.nextElement();
                       if(ob2 instanceof DraggableNodeFolder){
                           DraggableNodeFolder dnf=(DraggableNodeFolder)ob2;
                           results.add(new FolderLabel(Integer.parseInt(dnf.getId()),dnf.getName()));
                       }else{
                           DraggableNodeProcess dnp=(DraggableNodeProcess)ob2;
                           results.add(new ProcessLabel(Integer.parseInt(dnp.getId()),dnp.getName(),dnp.getOriginalLanguage(),dnp.getLatestBranch(),dnp.getVersions()));
                       }
                   }
                }else if(ob instanceof DraggableNodeFolder){
                    queue.addLast(((DraggableNodeFolder) ob).children());
                }
            }
        }
        return results;
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {

    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {

    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {

    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {

    }

    @Override
    public void dragExit(DragSourceEvent dse) {

    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {

    }
}
