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

import com.apql.Apql.highlight.Keywords;

import com.apql.Apql.popup.FolderLabel;
import com.apql.Apql.popup.KeywordLabel;
import com.apql.Apql.popup.PopupPanel;
import com.apql.Apql.popup.ProcessLabel;
import com.apql.Apql.tree.DraggableNodeFolder;
import com.apql.Apql.tree.DraggableNodeProcess;
import com.apql.Apql.tree.DraggableNodeTree;
import com.apql.Apql.tree.FolderProcessTree;
import org.apromore.service.pql.DatabaseService;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by corno on 28/07/2014.
 */
public class ContextController {
    private static ContextController context;
    public static DatabaseService databaseService;
    private ViewController viewController=ViewController.getController();
    private QueryController queryController=QueryController.getQueryController();
    private HashMap<String,List<JLabel>> cache=new HashMap<>();

    /** If not <code>null</code>, replaces the calls to the manager for process model lists with a fixed set of test data. */
    private List<String> testLocations = null;

    /** If not <code>null</code>, replaces the calls to the manager for process label lists with a fixed set of test data. */
    private List<String> testProcessLabels = null;

    private ContextController(){}

    public static ContextController getContextController(){
        if(context==null){
            context=new ContextController();
        }
        return context;
    }

    public void addEntryToChache(String key, List<JLabel> values){
        cache.put(key,values);
    }

    /**
     * @return a UI offering context-sensitive suggestions at the current caret position, or <code>null</code> if there are no suggestions
     */
    public JScrollPane findContext(){
        JTextPane textPane=QueryController.getQueryController().getTextPane();

        String text=textPane.getText();

        int caretPosition=QueryController.getQueryController().getCaretPosition();
        int indexSELECT=text.indexOf(Keywords.SELECT);
        int indexFROM=text.indexOf(Keywords.FROM);
        int indexWHERE=text.indexOf(Keywords.WHERE);
        int queryLength=text.length();

        String word=findWord(text,caretPosition);
        System.out.println("WORD \"" + word + "\"");

        if(checkEmptyQuery(queryLength, text)){
            // eg: ""
            System.out.println("EMPTY QUERY");
            String[] selectClause={Keywords.SELECT};
            return buildScroll(selectClause);
        }else if(indexSELECT < 0 && indexFROM < 0 && indexWHERE < 0) {
            // eg: "random"
            System.out.println("NEITHER SELECT, FROM, NOR WHERE");
            String[] selectClause=null;
            if(Keywords.SELECT.toLowerCase().matches(word+"[a-zA-Z]*") || Keywords.SELECT.matches(word.toUpperCase()+"[a-zA-Z]*")){
                selectClause=new String[]{Keywords.SELECT};
            }else{
                selectClause=new String[]{""};
            }
            return buildScroll(selectClause);
        }else if(checkWordSelectContext(word, indexSELECT, indexFROM, indexWHERE, caretPosition)){
            // eg: "SELECT nimrod"
            System.out.println("SELECT WITH WORD");
            String[] selectClause;
            switch (word.substring(0,1).toLowerCase()) {
            case "i": selectClause= new String[]{Keywords.id};    break;
            case "n": selectClause= new String[]{Keywords.name};   break;
            case "*": selectClause= new String[]{Keywords._STAR_}; break;
            case "f": selectClause= new String[]{Keywords.FROM};   break;
            default:  selectClause= new String[]{""};
            }
            return buildScroll(selectClause);
        } else if(checkSelectContext(indexSELECT,indexFROM,indexWHERE,caretPosition)){
            // eg: "SELECT "
            System.out.println("EMPTY SELECT CLAUSE "+word);
            String[] selectClause=Keywords.getSelectClause();
            return buildScroll(selectClause);
        }else if(checkWordFromContext(word, text, indexSELECT, indexFROM, indexWHERE, caretPosition)){
            // eg: "SELECT * FROM foo"
            System.out.println("FROM WITH WORD: "+word+"-----"+text);
            StringTokenizer st=new StringTokenizer(word,"/");

            String process=findProcess(text,caretPosition);
            int length=0;
            System.out.println("pathProcess: "+process+" "+st.countTokens());

            String folder;

            if(st.hasMoreTokens()){
                folder=st.nextToken();
                System.out.println("folder: "+folder);
                if(process.equals("Home/")){
                    System.out.println("@@@@@@@@@@@ HOME/");
                    FolderProcessTree fpt=(FolderProcessTree)viewController.getFolderProcessTree();
                    if (fpt==null) {
                        System.out.println("  No processes in home folder");
                        return null;
                    }
                    DraggableNodeTree dnt=fpt.getRoot();
                    List<JLabel> labelResults=new LinkedList<>();
                    Enumeration e = dnt.children();

                    while (e.hasMoreElements()) {
                        DraggableNodeTree ob = (DraggableNodeTree) e.nextElement();
                        if (ob instanceof DraggableNodeFolder) {
                            labelResults.add(new FolderLabel(Integer.parseInt(ob.getId()), ob.getName()));
                        } else if (ob instanceof DraggableNodeProcess) {
                            labelResults.add(new ProcessLabel(Integer.parseInt(ob.getId()), (ob).getName(), ((DraggableNodeProcess) ob).getOriginalLanguage(), ((DraggableNodeProcess) ob).getLatestBranch(), ((DraggableNodeProcess) ob).getVersions()));
                        }
                    }
                    return buildScroll(labelResults);
                }else if((folder.equals("\"") || folder.equals("\"H") || folder.equals("\"Ho") || folder.equals("\"Hom") || folder.equals("\"Home")) && !st.hasMoreTokens()){
                    System.out.println("@@@@@@@@@@@ NO HOME");
                    System.out.println("@@@@@@@@@@@ TOKEN: "+folder);
                    List<JLabel> resultLabels=new LinkedList<>();
                    resultLabels.add(new FolderLabel(0,"Home"));
                    return buildScroll(resultLabels);
                }else if(!folder.startsWith("\"H")){
                    System.out.println("-------------------------------IDNETSNODE: "+ getLocations().size());
                    System.out.println("-------------------------------IDNETSNODE: "+ getLocations());
                    LinkedList<JLabel> results=new LinkedList<>();

                    LinkedList<String> tmp =new LinkedList<>();
                    for(String str : getLocations()){
                        if(str.startsWith(folder)){
                            tmp.add(str);
                        }
                    }
                    Collections.sort(tmp);
                    for(String str : tmp){
                        results.addLast(new ProcessLabel(0,"\""+str,null,null,null));
                    }

                    return buildScroll(results);
                }else{
                    System.out.println("@@@@@@@@@@@ HOME");
                    FolderProcessTree fpt=(FolderProcessTree)viewController.getFolderProcessTree();
                    if (fpt==null) {
                        System.out.println("  No processes in home folder");
                        return null;
                    }
                    DraggableNodeTree dnt=fpt.getRoot();
                    List<JLabel> labelResults=new LinkedList<>();
                    DraggableNodeTree ob;
                    length+=5;
                    while(st.hasMoreTokens()){
                        folder=st.nextToken();
                        length+=folder.length();
                        System.out.println("folder in HOME: "+folder+" "+length+" "+process+" "+process.length()+" "+st.countTokens());
                        Enumeration e = dnt.children();
                        while(e.hasMoreElements()){
                            ob =(DraggableNodeTree) e.nextElement();
                            if (ob.getName().startsWith(folder) && st.hasMoreTokens()) {
                                System.out.println("SECOND OPTION");
                                dnt=ob;
                                length+=1;
                            }else if(ob.getName().startsWith(folder) && !st.hasMoreTokens() && process.length()-1 == length){
                                System.out.println("THIRD OPTION");
                                e=ob.children();
                                while(e.hasMoreElements()){
                                    ob =(DraggableNodeTree) e.nextElement();
                                    if(ob instanceof DraggableNodeFolder){
                                        labelResults.add(new FolderLabel(Integer.parseInt(ob.getId()),ob.getName()));
                                    }else if(ob instanceof DraggableNodeProcess){
                                        labelResults.add(new ProcessLabel(Integer.parseInt(ob.getId()),((DraggableNodeProcess)ob).getName(),((DraggableNodeProcess)ob).getOriginalLanguage(),((DraggableNodeProcess)ob).getLatestBranch(),((DraggableNodeProcess)ob).getVersions()));
                                    }
                                }
                            }else if(ob.getName().startsWith(folder)){
                                     if(ob instanceof DraggableNodeFolder){
                                         labelResults.add(new FolderLabel(Integer.parseInt(ob.getId()),ob.getName()));
                                     }else if(ob instanceof DraggableNodeProcess){
                                         labelResults.add(new ProcessLabel(Integer.parseInt(ob.getId()),((DraggableNodeProcess)ob).getName(),((DraggableNodeProcess)ob).getOriginalLanguage(),((DraggableNodeProcess)ob).getLatestBranch(),((DraggableNodeProcess)ob).getVersions()));
                                     }
                            }
                        }
                        System.out.println();
                    }
                    return buildScroll(labelResults);
                }
            } else {
                System.out.println("NO TOKENS");
            }
        } else if(checkFromContext(text, indexSELECT, indexFROM, indexWHERE, caretPosition)){
            // eg: "SELECT * FROM "
            System.out.println("EMPTY FROM CLAUSE "+ word);
            try {

                LinkedList<String> processes=(LinkedList)getLocations();
                LinkedList<JLabel> results=new LinkedList<>();
                KeywordLabel where=new KeywordLabel(Keywords.WHERE,viewController.getImageIcon(ViewController.ICONKEY));
                FolderLabel home=new FolderLabel(0,"Home");
                results.addLast(home);
                results.addLast(where);
                ProcessLabel pl;
                for(String proc : processes){
                    pl=new ProcessLabel(0,"\""+proc,null,null,null);
                    pl.setToolTipText(proc);
                    results.addLast(pl);
                }
                return buildScroll(results);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else if(checkWordWhereContext(word, text, indexSELECT, indexFROM, indexWHERE, caretPosition)){
            System.out.println("WHERE CONTEXT " + word);
            String[] keywords={""};
            LinkedList<String> suggests=new LinkedList<>();
            String wordWhere=findWordWhere(text,caretPosition);
            System.out.println("  wordWhere " + wordWhere);
            System.out.println("PRIMA IF: "+wordWhere);
            if(wordWhere.matches("[a-zA-Z]+[,]") || wordWhere.matches("[\"][a-zA-Z0-9_]+[\"][,]") || word.matches("[a-zA-Z]+")) {
                // eg: "SELECT * FROM * WHERE foo"
                System.out.println("WITH WORD");
                for (String str : Keywords.getKeywords()) {
                    if (str.toLowerCase().startsWith(word.toLowerCase())) {
                        suggests.add(str);
                    }
                }
            }else if(wordWhere.matches("[a-zA-Z0-9 ]+")){
                // eg: "SELECT * FROM * WHERE 123"
                System.out.println("WITH SPACE");
                try {
                    List<String> labels = getProcessLabels();
                    for(String label : labels){
                        if(label.startsWith(wordWhere)){
                            suggests.add(label);
                        }
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }else if(word.matches("[a-zA-Z]+[(][\"]")){
                // eg: SELECT * FROM * WHERE CanOccur("
                System.out.println("WITH QUOTES");
                try {
                    List<String> labels = getProcessLabels();
                    System.out.println("  labels " + labels);
                    for(String label : labels){
                        suggests.add(label);
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }else {
                // eg: SELECT * FROM * WHERE sqrt(
                System.out.println("WITHOUT");
            }
            Collections.sort(suggests);
            keywords=(suggests.toArray(keywords));
            return buildScroll(keywords);
        }else if(checkWhereContext(text, indexSELECT, indexFROM, indexWHERE, caretPosition)) {
            // eg: "SELECT * FROM * WHERE "
            System.out.println("EMPTY WHERE CLAUSE " + word);
            return buildScroll(Keywords.getWhereClause());
        }else{
            // eg: "FROM un"
            System.out.println("ELSE");
            String[] keywords={""};
            LinkedList<String> suggests=new LinkedList<>();

            for(String str : Keywords.getKeywords()){
                if(str.toLowerCase().matches(word.toLowerCase()+"[a-z]+")){
                    suggests.add(str);
                }
            }
            Collections.sort(suggests);
            keywords=(suggests.toArray(keywords));
            return buildScroll(keywords);
        }

        return null;
    }

    private List<String> getProcessLabels() {
        if (testProcessLabels != null) {
            return testProcessLabels;
        } else {
            return databaseService.getLabels("jbpt_labels","label");
        }
    }

    /**
     * Disable querying the manager for process labels.
     *
     * This is only used to isolate this class from the manager during unit testing.
     *
     * @param processLabels  the list of process labels to use, rather than ones obtained from the manager
     */
    void setProcessLabels(List<String> processLabels) {
        testProcessLabels = processLabels;
    }

    private List<String> getLocations() {
        if (testLocations != null) {
            return testLocations;
        } else {
            System.out.println("getLocations() = " + queryController.getLocations());
            return queryController.getLocations();
        }
    }

    void setLocations(List<String> locations) {
        testLocations = locations;
    }

    private boolean checkEmptyQuery(int queryLength, String text){
        return queryLength==0 || text.trim().length()==0;
    }

    private boolean checkWordSelectContext(String word, int indexSELECT, int indexFROM, int indexWHERE, int caretPosition){
//        System.out.println("----------checkWordSelectContext----------");
//        System.out.println("word: "+word);
//        System.out.println("indexSelect: "+indexSELECT);
//        System.out.println("indexFrom: "+indexFROM);
//        System.out.println("indexWhere: "+indexWHERE);
//        System.out.println("caret: "+caretPosition);
//        System.out.println("----------checkWordSelectContext----------");
        return (!word.equals("") && checkSelectContext(indexSELECT,indexFROM,indexWHERE,caretPosition));
    }

    private boolean checkWordFromContext(String word, String text, int indexSELECT, int indexFROM, int indexWHERE, int caretPosition){
//        System.out.println("----------checkWordFromContext----------");
//        System.out.println("word: "+word);
//        System.out.println("indexSelect: "+indexSELECT);
//        System.out.println("indexFrom: "+indexFROM);
//        System.out.println("indexWhere: "+indexWHERE);
//        System.out.println("caret: "+caretPosition);
//        System.out.println("----------checkWordFromContext----------");
        return (!word.equals("") && checkFromContext(text, indexSELECT, indexFROM, indexWHERE, caretPosition));
    }

    private boolean checkWordWhereContext(String word, String text, int indexSELECT, int indexFROM, int indexWHERE, int caretPosition){
        System.out.println("----------checkWordWhereContext----------");
        System.out.println("word: "+word);
//        System.out.println("indexSelect: "+indexSELECT);
//        System.out.println("indexFrom: "+indexFROM);
//        System.out.println("indexWhere: "+indexWHERE);
//        System.out.println("caret: "+caretPosition);
        System.out.println("----------checkWordWhereContext----------");
//        word.matches("[a-zA-Z]+[(][\"][a-zA-Z0-9 ]+")
        return (!word.equals("") && checkWhereContext(text, indexSELECT, indexFROM, indexWHERE, caretPosition));
    }

    private boolean checkSelectContext(int indexSELECT, int indexFROM, int indexWHERE, int caretPosition){
//        System.out.println("----------checkSelectContext----------");
//        System.out.println("indexSelect: "+indexSELECT);
//        System.out.println("indexFrom: "+indexFROM);
//        System.out.println("indexWhere: "+indexWHERE);
//        System.out.println("caret: "+caretPosition);
//        System.out.println("----------checkSelectContext----------");
        return (indexSELECT >=0 && indexFROM < 0 && indexWHERE <0 && caretPosition > indexSELECT+6) ||
               (indexSELECT >= 0 && indexFROM > 0 && indexWHERE < 0 && caretPosition > indexSELECT+6 && caretPosition < indexFROM ) ||
               (indexSELECT >= 0 && indexFROM > 0 && indexWHERE > 0 && indexSELECT < indexFROM && indexFROM < indexWHERE && caretPosition > indexSELECT+6 && caretPosition < indexFROM);
    }

    private boolean checkFromContext(String text, int indexSELECT, int indexFROM, int indexWHERE, int caretPosition){
        int charTen=0;
        for(int i=indexFROM; i>0 ;i--)
            if(text.charAt(i)==13)
                charTen++;
//        System.out.println("----------checkFromContext----------");
//        System.out.println("indexSelect: "+indexSELECT);
//        System.out.println("indexFrom: "+indexFROM);
//        System.out.println("indexWhere: "+indexWHERE);
//        System.out.println("caret: "+caretPosition);
//        System.out.println("----------checkFromContext----------");
        return  (indexSELECT >= 0 && indexFROM > 0 && indexWHERE < 0 && indexSELECT<indexFROM && caretPosition >= indexFROM+4-charTen) ||
                (indexSELECT >= 0 && indexFROM > 0 && indexWHERE > 0 && indexSELECT < indexFROM && indexFROM < indexWHERE && caretPosition >= indexFROM+4 && caretPosition < indexWHERE);
    }

    private boolean checkWhereContext(String text, int indexSELECT, int indexFROM, int indexWHERE, int caretPosition){
        int charTen=0;
        for(int i=indexWHERE; i>0 ;i--)
            if(text.charAt(i)==13)
                charTen++;
        System.out.println("----------checkWhereContext----------");
        System.out.println("indexSelect: "+indexSELECT);
        System.out.println("indexFrom: "+indexFROM);
        System.out.println("indexWhere: "+indexWHERE);
        System.out.println("caret: "+caretPosition +" "+text.length()+" ");
//        boolean checkWord=false;
//        for(int i= caretPosition; i>indexWHERE+5-charTen; i--){
//            if(text.charAt(i-1)==')')
//                break;
//            if(text.charAt(i-1)=='"' && text.charAt(i-2)=='(') {
//                System.out.println("TRUE");
//                checkWord=true;
//                break;
//            }
//        }
        System.out.println("----------checkWhereContext----------");
        return (indexSELECT >= 0 && indexFROM > 0 && indexWHERE > 0 && indexSELECT < indexFROM && indexFROM < indexWHERE && caretPosition > indexWHERE+5-charTen);
    }

    String findWord(String text, int caretPos) {
        String word = "";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != 13) {
                res.append(text.charAt(i));
            }
        }
        text = res.toString();
        if (caretPos == 0 || text.charAt(caretPos - 1) == ' '
                || text.charAt(caretPos - 1) == '\n'
                || text.charAt(caretPos - 1) == '\t') {
        } else {
            String beforeCaret = text.substring(0, caretPos);
            for (int i = caretPos - 1; i >= 0; i--) {
                if (beforeCaret.charAt(i) == ' '
                        || beforeCaret.charAt(i) == '\n'
                        || beforeCaret.charAt(i) == '\t') {
                    break;
                } else {
                    word = beforeCaret.charAt(i) + word;
                }
            }
        }
        return word;
    }

    private String findWordWhere(String text, int caretPos){
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != 13) {
                res.append(text.charAt(i));
            }
        }
        text = res.toString();
        int indexWHERE=res.indexOf(Keywords.WHERE);

        if(caretPos > indexWHERE){
            for(int i= caretPos; i>indexWHERE+5; i--){
                if(res.charAt(i-1)==')' || res.charAt(i-1)=='E') {
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>> primoif");
                    return findWord(text,caretPos);
                }
                if(res.charAt(i-1)=='"' && res.charAt(i-2)=='(') {
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>> secondoif: "+res.substring(i,caretPos));
                    return res.substring(i,caretPos);
                }
            }
        }
        return findWord(text,caretPos);
    }

    String findProcess(String text, int caretPos){
        String word = "";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != 13) {
                res.append(text.charAt(i));
            }
        }
        text = res.toString();
        if (caretPos == 0 || text.charAt(caretPos - 1) == ' '
                || text.charAt(caretPos - 1) == '\n'
                || text.charAt(caretPos - 1) == '\t') {
        } else {
            String beforeCaret = text.substring(0, caretPos);
            for (int i = caretPos - 1; i >= 0; i--) {
                if (beforeCaret.charAt(i) == '\"') {
                    break;
                } else {
                    word = beforeCaret.charAt(i) + word;
                }
            }
        }
        return word;
    }

    private JScrollPane buildScroll(String[] rows){
        PopupPanel panel=new PopupPanel(rows.length,1);
        panel.addResults(rows);
        JScrollPane scroll = new JScrollPane(panel);
        if(rows.length==1) {
            scroll.setPreferredSize(new Dimension(190, 50 * rows.length));
            scroll.setMinimumSize(new Dimension(190, 50 * rows.length));
            scroll.setMaximumSize(new Dimension(190, 50 * rows.length));
        }else if(rows.length==2){
            scroll.setPreferredSize(new Dimension(190, 45 * rows.length));
            scroll.setMinimumSize(new Dimension(190, 45 * rows.length));
            scroll.setMaximumSize(new Dimension(190, 45 * rows.length));
        }else if(rows.length==3){
            scroll.setPreferredSize(new Dimension(190, 40 * rows.length));
            scroll.setMinimumSize(new Dimension(190, 40 * rows.length));
            scroll.setMaximumSize(new Dimension(190, 40 * rows.length));
        }else if(rows.length > 3){
            scroll.setPreferredSize(new Dimension(190, 150));
            scroll.setMinimumSize(new Dimension(190, 150));
            scroll.setMaximumSize(new Dimension(190, 150));
        }
        return scroll;
    }

    private JScrollPane buildScroll(List<JLabel> rows){
        PopupPanel panel=new PopupPanel(rows.size(),1);
        panel.addResults(rows);
        JScrollPane scroll = new JScrollPane(panel);
        if(rows.size()==1) {
            scroll.setPreferredSize(new Dimension(190, 50 * rows.size()));
            scroll.setMinimumSize(new Dimension(190, 50 * rows.size()));
            scroll.setMaximumSize(new Dimension(190, 50 * rows.size()));
        }else if(rows.size()==2){
            scroll.setPreferredSize(new Dimension(190, 45 * rows.size()));
            scroll.setMinimumSize(new Dimension(190, 45 * rows.size()));
            scroll.setMaximumSize(new Dimension(190, 45 * rows.size()));
        }else if(rows.size()==3){
            scroll.setPreferredSize(new Dimension(190, 40 * rows.size()));
            scroll.setMinimumSize(new Dimension(190, 40 * rows.size()));
            scroll.setMaximumSize(new Dimension(190, 40 * rows.size()));
        }else if(rows.size() > 3){
            scroll.setPreferredSize(new Dimension(190, 150));
            scroll.setMinimumSize(new Dimension(190, 150));
            scroll.setMaximumSize(new Dimension(190, 150));
        }
        return scroll;
    }
}
