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
import org.apromore.manager.client.ManagerService;
import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.List;

/**
 * Created by corno on 28/07/2014.
 */
public class ContextController {
    private static ContextController context;
    private String text;
    private ViewController viewController=ViewController.getController();
    private QueryController queryController=QueryController.getQueryController();
    private HashMap<String,List<JLabel>> cache=new HashMap<>();

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

    public JScrollPane findContext(){
        JTextPane textPane=QueryController.getQueryController().getTextPane();

        text=textPane.getText();

        int caretPosition=QueryController.getQueryController().getCaretPosition();
        PopupPanel panel=null;
        JScrollPane scroll=null;
        int indexSELECT=text.indexOf(Keywords.SELECT);
        int indexFROM=text.indexOf(Keywords.FROM);
        int indexWHERE=text.indexOf(Keywords.WHERE);
        int queryLenght=text.length();

        String word=findWord(text,caretPosition);

        if(checkEmptyQuery(queryLenght, text)){
            System.out.println("EMPTY QUERY");
            String[] selectClause={Keywords.SELECT};
//            panel=new PopupPanel(1,1);
//            panel.addResults(selectClause);
//            scroll = new JScrollPane(panel);
//            scroll.setPreferredSize(new Dimension(30*selectClause.length, 150));
            return buildScroll(selectClause);
        }else if(indexSELECT < 0 && indexFROM < 0 && indexWHERE < 0) {
            String[] selectClause=null;
//            panel=new PopupPanel(1,1);
            if(Keywords.SELECT.toLowerCase().matches(word+"[a-zA-Z]*") || Keywords.SELECT.matches(word.toUpperCase()+"[a-zA-Z]*")){
                selectClause=new String[]{Keywords.SELECT};
//                panel.addResults(selectClause);
            }else{
                selectClause=new String[]{""};
            }
//            scroll = new JScrollPane(panel);
//            scroll.setPreferredSize(new Dimension(30*selectClause.length, 150));
            return buildScroll(selectClause);
        }else if(checkWordSelectContext(word, indexSELECT, indexFROM, indexWHERE, caretPosition)){
            System.out.println("SELECT WITH WORD");
            String[] selectClause;
//            panel=new PopupPanel(1,1);
             if(word.substring(0,1).toLowerCase().equals("i")){
                selectClause= new String[]{Keywords.id};
            }else if(word.substring(0,1).toLowerCase().equals("n")){
                selectClause= new String[]{Keywords.name};
            }else if(word.substring(0,1).toLowerCase().equals("*")){
                selectClause= new String[]{Keywords._STAR_};
            }else if(word.substring(0,1).toLowerCase().equals("f")){
                selectClause= new String[]{Keywords.FROM};
            }else{
                 selectClause= new String[]{""};
            }
//            panel.addResults(selectClause);
//            scroll = new JScrollPane(panel);
//            scroll.setPreferredSize(new Dimension(30*selectClause.length, 150));
            return buildScroll(selectClause);
        } else if(checkSelectContext(indexSELECT,indexFROM,indexWHERE,caretPosition)){
            System.out.println("EMPTY SELECT CLAUSE "+word);
            String[] selectClause=Keywords.getSelectClause();
//            panel=new PopupPanel(5,1);
//            panel.addResults(selectClause);
//            scroll = new JScrollPane(panel);
//            scroll.setPreferredSize(new Dimension(30*selectClause.length, 150));
            return buildScroll(selectClause);
        }else if(checkWordFromContext(word, indexSELECT, indexFROM, indexWHERE, caretPosition)){
            System.out.println("FROM WITH WORD: "+word+"-----"+text);
            StringTokenizer st=new StringTokenizer(word,"/");

//            ManagerService manager=ServiceController.getManagerService();
//            String userID=manager.readUserByUsername(viewController.getUsername()).getId();

            String process=findProcess(text,caretPosition);
            int lenght=0;
            System.out.println("pathProcess: "+process+" "+st.countTokens());

            String folder;
//            List<FolderType> subFolders;
//            List<ProcessSummaryType> subProcesses;

            if(st.hasMoreTokens()){
                folder=st.nextToken();
                System.out.println("folder: "+folder);
                if(process.equals("Home/")){
                    DraggableNodeTree dnt=((FolderProcessTree)viewController.getFolderProcessTree()).getRoot();
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
//                    panel=new PopupPanel(labelResults.size(),1);
//                    panel.addResults(labelResults);
//                    scroll = new JScrollPane(panel);
//                    if(labelResults.size()<=5) {
//                        scroll.setPreferredSize(new Dimension(190, 30 * labelResults.size()));
//                        scroll.setMaximumSize(new Dimension(190, 30 * labelResults.size()));
//                        scroll.setMinimumSize(new Dimension(190, 30 * labelResults.size()));
//                    }else {
//                        scroll.setPreferredSize(new Dimension(190, 150));
//                        scroll.setMaximumSize(new Dimension(190, 150));
//                        scroll.setMinimumSize(new Dimension(190, 150));
//                    }
                    return buildScroll(labelResults);
                }else if((folder.equals("\"") || folder.equals("\"H") || folder.equals("\"Ho") || folder.equals("\"Hom") || folder.equals("\"Home")) && !st.hasMoreTokens()){
                    System.out.println("@@@@@@@@@@@ NO HOME");
                    System.out.println("@@@@@@@@@@@ TOKEN: "+folder);
                    panel=new PopupPanel(1,1);
                    List<JLabel> resultLabels=new LinkedList<>();
                    resultLabels.add(new FolderLabel(0,"Home"));
//                    resultLabels.add(new KeywordLabel(Keywords.WHERE,viewController.getImageIcon(ViewController.ICONKEY)));
//                    panel.addResults(resultLabels);
//                    scroll = new JScrollPane(panel);
//                    if(resultLabels.size()<=5) {
//                        scroll.setPreferredSize(new Dimension(190, 40 * resultLabels.size()));
//                        scroll.setMaximumSize(new Dimension(190, 40 * resultLabels.size()));
//                        scroll.setMinimumSize(new Dimension(190, 40 * resultLabels.size()));
//                    }else {
//                        scroll.setPreferredSize(new Dimension(190, 150));
//                        scroll.setMaximumSize(new Dimension(190, 150));
//                        scroll.setMinimumSize(new Dimension(190, 150));
//                    }
//                    return scroll;
                    return buildScroll(resultLabels);
                }else if(!folder.startsWith("\"H")){
                    System.out.println("-------------------------------IDNETSNODE: "+ queryController.getLocations().size());
                    LinkedList<JLabel> results=new LinkedList<>();

                    LinkedList<String> tmp =new LinkedList<>();
                    for(String str : queryController.getLocations()){
                        if(str.startsWith(folder)){
                            tmp.add(str);
                        }
                    }
                    Collections.sort(tmp);
                    for(String str : tmp){
                        results.addLast(new ProcessLabel(0,"\""+str,null,null,null));
                    }

//                    panel=new PopupPanel(tmp.size(),1);
//                    panel.addResults(results);
//                    scroll = new JScrollPane(panel);
//                    if(results.size()<=5) {
//                        scroll.setPreferredSize(new Dimension(190, 30 * results.size()));
//                        scroll.setMaximumSize(new Dimension(190, 30*results.size()));
//                        scroll.setMinimumSize(new Dimension(190, 30 * results.size()));
//                    } else {
//                        scroll.setPreferredSize(new Dimension(190, 150));
//                        scroll.setMaximumSize(new Dimension(190, 150));
//                        scroll.setMinimumSize(new Dimension(190, 150));
//                    }


//                    scroll.setPreferredSize(new Dimension(200, 30));
//                    return scroll;
                    return buildScroll(results);
                }else{
                    System.out.println("@@@@@@@@@@@ HOME");
                    DraggableNodeTree dnt=((FolderProcessTree)viewController.getFolderProcessTree()).getRoot();
                    List<JLabel> labelResults=new LinkedList<>();
                    DraggableNodeTree ob;
                    lenght+=5;
                    while(st.hasMoreTokens()){
                        folder=st.nextToken();
                        lenght+=folder.length();
                        System.out.println("folder in HOME: "+folder+" "+lenght+" "+process+" "+process.length()+" "+st.countTokens());
                        Enumeration e = dnt.children();
                        while(e.hasMoreElements()){
                            ob =(DraggableNodeTree) e.nextElement();
                             if (ob.getName().startsWith(folder) && st.hasMoreTokens()) {
                                System.out.println("SECOND OPTION");
                                dnt=ob;
                                 lenght+=1;
                            }else if(ob.getName().startsWith(folder) && !st.hasMoreTokens() && process.length()-1 == lenght){
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
            }
        } else if(checkFromContext(indexSELECT, indexFROM, indexWHERE, caretPosition)){
            System.out.println("EMPTY FROM CLAUSE "+ word);
            try {

                LinkedList<String> processes=(LinkedList)queryController.getLocations();
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
        }else if(checkWordWhereContext(word, indexSELECT, indexFROM, indexWHERE, caretPosition)){
            String[] keywords={""};
            LinkedList<String> suggests=new LinkedList<>();
            String wordWhere=findWordWhere(text,caretPosition);
            System.out.println("PRIMA IF: "+wordWhere);
            if(wordWhere.matches("[a-zA-Z]+[,]") || wordWhere.matches("[\"][a-zA-Z0-9_]+[\"][,]") || word.matches("[a-zA-Z]+")) {
                for (String str : Keywords.getKeywords()) {
                    if (str.toLowerCase().startsWith(word.toLowerCase())) {
                        suggests.add(str);
                    }
                }
            }else if(wordWhere.matches("[a-zA-Z0-9 ]+")){
                System.out.println("WITH SPACE");
                try {
                    List<String> labels= ServiceController.getManagerService().getProcessesLabels("pql.jbpt_labels","label");
                    for(String label : labels){
                        if(label.startsWith(wordWhere)){
                            suggests.add(label);
                        }
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }else if(word.matches("[a-zA-Z]+[(][\"]")){
                System.out.println("WITH APICI");
                try {
                    List<String> labels= ServiceController.getManagerService().getProcessesLabels("pql.jbpt_labels","label");
                    for(String label : labels){
                        suggests.add(label);
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            Collections.sort(suggests);
            keywords=(suggests.toArray(keywords));
            return buildScroll(keywords);
        }else if(checkWhereContext(indexSELECT, indexFROM, indexWHERE, caretPosition)) {
            System.out.println("EMPTY WHERE CLAUSE " + word);
            return buildScroll(Keywords.getWhereClause());
        }else{
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

        return scroll;
    }

    private boolean checkEmptyQuery(int queryLenght, String text){
        return queryLenght==0 || text.trim().length()==0;
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

    private boolean checkWordFromContext(String word, int indexSELECT, int indexFROM, int indexWHERE, int caretPosition){
//        System.out.println("----------checkWordFromContext----------");
//        System.out.println("word: "+word);
//        System.out.println("indexSelect: "+indexSELECT);
//        System.out.println("indexFrom: "+indexFROM);
//        System.out.println("indexWhere: "+indexWHERE);
//        System.out.println("caret: "+caretPosition);
//        System.out.println("----------checkWordFromContext----------");
        return (!word.equals("") && checkFromContext(indexSELECT, indexFROM, indexWHERE, caretPosition));
    }

    private boolean checkWordWhereContext(String word, int indexSELECT, int indexFROM, int indexWHERE, int caretPosition){
        System.out.println("----------checkWordWhereContext----------");
        System.out.println("word: "+word);
//        System.out.println("indexSelect: "+indexSELECT);
//        System.out.println("indexFrom: "+indexFROM);
//        System.out.println("indexWhere: "+indexWHERE);
//        System.out.println("caret: "+caretPosition);
        System.out.println("----------checkWordWhereContext----------");
//        word.matches("[a-zA-Z]+[(][\"][a-zA-Z0-9 ]+")
        return (!word.equals("") && checkWhereContext(indexSELECT, indexFROM, indexWHERE, caretPosition));
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

    private boolean checkFromContext(int indexSELECT, int indexFROM, int indexWHERE, int caretPosition){
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

    private boolean checkWhereContext(int indexSELECT, int indexFROM, int indexWHERE, int caretPosition){
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

    private String findWord(String text, int caretPos) {
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

    private String findWordWhere(String Text,int caretPos){
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

    private String findProcess(String text, int caretPos){
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
