package com.apql.Apql.controller;

import com.apql.Apql.QueryText;
import com.apql.Apql.VariableText;
import com.apql.Apql.highlight.ButtonAction;
import com.apql.Apql.highlight.Highlight;
import com.apql.Apql.highlight.Keywords;

import com.apql.Apql.history.QueueHistory;
import com.apql.Apql.tree.DraggableNodeFolder;
import com.apql.Apql.tree.DraggableNodeProcess;

import org.apromore.model.ResultPQL;
import org.apromore.model.VersionSummaryType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by corno on 25/07/2014.
 */
public class QueryController {

    private QueryText query;
    private int caretPosition=0;
    private HashSet<String> locations;
    private HashMap<String, DraggableNodeProcess> idNetsNode=new HashMap<>();

    private String versionMode=ViewController.ALLVERSIONS;
    private static QueryController queryController;
    private ViewController viewController=ViewController.getController();
    private VariableText variablePane;
    private JTextPane error;
    private Point popupPosition;

    private String beforeLoc;
    private String strLocations;
    private String afterLoc;

    private QueueHistory queueHistory;

    private boolean selectAll=false;

    private QueryController(){}

    public static QueryController getQueryController(){
        if(queryController==null) {
            queryController = new QueryController();
        }
        return queryController;
    }

    public void clearQueryController(){
        query=null;
        caretPosition=0;
        locations=null;
        idNetsNode=new HashMap<>();
//        queryController=null;
        variablePane=null;
        error=null;
        popupPosition=null;
        beforeLoc=null;
        strLocations=null;
        afterLoc=null;
        viewController.clear();
//        if(queryController!=null)
//            queryController.clearQueryController();
    }

//    public String getInfoNet(String netID, String )

    public void setQueueHistory(QueueHistory queueHistory){
        this.queueHistory=queueHistory;
    }

    public QueueHistory getQueueHistory(){
        return queueHistory;
    }

    public boolean isSelectAll(){
        return this.selectAll;
    }

    public void setSelectAll(boolean selectAll){
        this.selectAll=selectAll;
    }

    public void setErrorPane(JTextPane error){
        this.error=error;
    }

    public JTextPane getErrorPane(){
        return error;
    }

    public String getVersion(){
        return this.versionMode;
    }

    public void setVersion(String version){
        this.versionMode=version;
    }

    public void setPopupPosition(Point p){
        popupPosition=p;
    }

    public Point getPopupPosition(){
        return popupPosition;
    }

    public List<String> getLocations() {
        HashSet<String> nameNodes = new HashSet<>();
        for(DraggableNodeProcess dnp : idNetsNode.values()){
            nameNodes.add(dnp.getName());
        }
        LinkedList<String> result = new LinkedList<>(nameNodes);
        Collections.sort(result);
        return result;
    }

    public void setLocations(HashSet<String> locations) {
        this.locations = locations;
    }

    public int getCaretPosition() {
        return caretPosition;
    }

    public void setCaretPosition(int caretPosition) {
        this.caretPosition = caretPosition;
    }

    public void settextPane(QueryText query) {
        this.query = query;
    }

    public QueryText getTextPane() {
        return query;
    }

    public VariableText getVariablePane() {
        return variablePane;
    }

    public void setVariablePane(VariableText variablePane) {
        this.variablePane = variablePane;
    }

    public void addQueryLocation(){
        HashSet<String> locationsInQuery=keepLocationInQuery();
        HashSet<String> redundantLocations=new HashSet<>();

        if(!locationsInQuery.isEmpty()){
            for(String str : locations){
                for(String str2 : locationsInQuery){
                    String pathStr=null;
                    String pathStr2=null;
                    if(str.indexOf("{") > 0 && str2.indexOf("{") > 0 && !str.matches("[a-zA-Z0-9/ ._]+[{]([0-9]+[/]([0-9]+([.][0-9]+){1,2})[/][a-zA-Z0-9]+)[}]")) {
                        pathStr = str.substring(0, str.indexOf("{"));
                        pathStr2 = str2.substring(0, str2.indexOf("{"));
                        System.out.println("PATH: "+pathStr+ " "+ pathStr2);
                        if(pathStr.equals(pathStr2)){
                            redundantLocations.add(str2);
                        }
                    }
                }
            }
        }

        locationsInQuery.removeAll(redundantLocations);

        locations.addAll(locationsInQuery);

        StringBuilder querySB = new StringBuilder();
        querySB.append(beforeLoc + " ");
        for (String str : locations) {
            if (str.matches("[0-9]+"))
                querySB.append("" + str + "" + ", ");
            else
                querySB.append("\"" + str + "\"" + ", ");
        }
        if (!locations.isEmpty())
            querySB.deleteCharAt(querySB.length() - 2);
        querySB.append(afterLoc);

        queryController.getQueueHistory().addHistory(querySB.toString());

        this.query.setText(querySB.toString());
        Highlight.getHighlight().highlight(this.query.getText());
        System.out.println("LOCATIONS: " + locations);
        locations=new HashSet<String>();
    }

    public HashSet<String> keepLocationInQuery(){

        String text=this.query.getText();
        HashSet<String> locations = new HashSet<String>();

        int indexSELECT=text.indexOf(Keywords.SELECT);
        int indexFROM=text.indexOf(Keywords.FROM);
        int indexWHERE=text.indexOf(Keywords.WHERE, indexFROM + 4);
        int queryLength=text.length();

        if(malformedQuery()){
//            System.out.println("QUERY MALFORMATA"+fromBeforeSelect + " " +whereBeforeFrom + " " + whereBeforeSelect);
            beforeLoc="SELECT * FROM ";
            afterLoc="";
            return locations;
        }else {
            char[] strchar = text.toCharArray();
            /*
            for (int i = indexFROM + 4; i < queryLength; i++) {//trovo la posizione della keyword WHERE
                if (strchar[i] == 'W' && strchar[i - 1] != '"' && text.substring(i, i + Keywords.WHERE.length()).equals(Keywords.WHERE)) {
		    System.out.println("Changed indexWHERE from " + indexWHERE + " to " + i);
                    indexWHERE = i;
                    break;
                }
            }
            */
            if(indexFROM < 0) {
                beforeLoc = beforeLoc + " FROM ";
                return locations;
            }
            beforeLoc = text.substring(0, indexFROM + 4);
            afterLoc = "";

            if (indexWHERE == -1) {//prendo le locazioni
                strLocations = text.substring(indexFROM + 4);
            } else {
                strLocations = text.substring(indexFROM + 4, indexWHERE);
                afterLoc = text.substring(indexWHERE, queryLength);
            }

            StringTokenizer st = new StringTokenizer(strLocations, ",;");

            while (st.hasMoreTokens()) {
                String token=st.nextToken();
                String newToken=token.replace('\"',' ').trim();
                locations.add(newToken);
            }
            System.out.println("LOCATION IN QUERY: "+locations);
            locations.remove("");
//            locations.remove("...");
            return locations;
        }
    }

    public String getBeforeLoc(){
        return beforeLoc;
    }

    public String getAfterLoc(){
        return afterLoc;
    }

   public boolean malformedQuery(){
       String text=this.query.getText();
       int indexSELECT=text.indexOf(Keywords.SELECT);
       int indexFROM=text.indexOf(Keywords.FROM);
       int indexWHERE=text.indexOf(Keywords.WHERE);
       int queryLength=text.length();

       boolean wrongSelect=indexSELECT > 0 && text.charAt(indexSELECT-1)=='"';
       boolean wrongFrom=indexFROM > 0 && text.charAt(indexFROM-1)=='"';
       boolean wrongWhere=indexWHERE > 0 && text.charAt(indexWHERE-1)=='"';

       boolean fromBeforeSelect=indexSELECT > indexFROM;
       boolean whereBeforeFrom=indexFROM > indexWHERE && indexWHERE!= -1;
       boolean whereBeforeSelect= indexWHERE!=-1 && indexWHERE < indexSELECT;
       return queryLength==0 || wrongSelect || wrongFrom || wrongWhere || fromBeforeSelect || whereBeforeFrom || whereBeforeSelect || text.trim().length()==0;
   }

    public HashSet<String> getIdNets(){
        HashSet<String> locations=keepLocationInQuery();

        if(locations.contains("*")){
            return keepAllModels();
        }else if(locations.isEmpty()){
            return new HashSet<String>();
        }else if(locations.contains("+")){
            locations = ButtonAction.CTRLBack.getLocationInQuery();
        }

        HashSet<String> remove=new HashSet<>();
        System.out.println("KEEPLOCATIONS: "+locations+ " VERSION: "+versionMode);
        HashSet<String> idNets=new HashSet<>();

        int count=0;
        for(String location : locations) {
//            if(count==locations.size())
//                break;
            for (String id : idNetsNode.keySet()) {
                if (idNetsNode.get(id).getName().equals(location) || idNetsNode.get(id).getId().equals(location)) {
                    DraggableNodeProcess dnp=idNetsNode.get(id);
                    if(queryController.getVersion().equals(ViewController.LATESTVERSION)) {
                        idNets.add(dnp.getId()+"/"+dnp.getLatestVersion()+"/"+dnp.getLatestBranch());
                    }else if(queryController.getVersion().equals(ViewController.ALLVERSIONS)){
                        for (VersionSummaryType vst : dnp.getVersions()) {
                            idNets.add(dnp.getId()+"/"+vst.getVersionNumber()+"/"+vst.getName());
                        }
                    }
//                    break;
                }else if(location.matches(idNetsNode.get(id).getPathNode()+"[{](ALLVERSION)[}]")){
                    DraggableNodeProcess dnp=idNetsNode.get(id);
                    for (VersionSummaryType vst : dnp.getVersions()) {
                        idNets.add(dnp.getId()+"/"+vst.getVersionNumber()+"/"+vst.getName());
                    }
                    break;
                }else if(location.matches(idNetsNode.get(id).getPathNode()+"[{](LATESTVERSION)[}]")){
                    DraggableNodeProcess dnp=idNetsNode.get(id);
                    idNets.add(dnp.getId()+"/"+dnp.getLatestVersion()+"/"+dnp.getLatestBranch());
                    break;
                }else if(location.matches(idNetsNode.get(id).getPathNode()+"[{]([0-9]+[/]([0-9]+([.][0-9]+){1,2})[/][a-zA-Z0-9]+)[}]")){
                    int indexVersion=location.indexOf("{");
                    idNets.add(location.substring(indexVersion+1,location.length()-1));
                    break;
                }
            }
            count++;
        }
        System.out.println("IDNETS: "+idNets);

        return idNets;
    }

    private HashSet<String> keepAllModels(){
//        HashSet<String> idNets=new HashSet<>();
//
//        JTree tree = viewController.getFolderProcessTree();
//        TreeNode root = (TreeNode) tree.getModel().getRoot();
//        LinkedList<Enumeration> queue = new LinkedList<>();
//        queue.add(root.children());
//        while (!queue.isEmpty()) {
//            Enumeration e = queue.removeFirst();
//            while (e.hasMoreElements()) {
//                Object ob = e.nextElement();
//                if (ob instanceof DraggableNodeProcess) {
//                    DraggableNodeProcess dnp = (DraggableNodeProcess) ob;
//                    String idNet;
//                    for (VersionSummaryType vst : dnp.getVersions()) {
//                        idNet=dnp.getId() + "/" + vst.getVersionNumber() + "/" + vst.getName();
//                        idNets.add(idNet);
//                        idNetsNode.put(idNet,dnp);
//                    }
//                } else if (ob instanceof DraggableNodeFolder) {
//                    queue.addLast(((DraggableNodeFolder) ob).children());
//                }
//            }
//        }
        return new HashSet<>(idNetsNode.keySet());
    }

    public List<ResultPQL> buildResults(List<String> netResults){
        LinkedList<ResultPQL> results=new LinkedList<>();
        ResultPQL pql;
            DraggableNodeProcess dnp;

            Set<String> attributes = getSelectAttributes();

            if (attributes.contains("*")) {
                for (String net : netResults) {
                    dnp = idNetsNode.get(net);

                    pql = new ResultPQL();
                    pql.setPst(dnp.getValue());
                    pql.setVst(dnp.getVersionSummaryType(net));
                    boolean[] attributesToShow={true,true,true,true,true,true,true,true};
                    pql.getAttributesToShow().clear();
                    for(Boolean bool : attributesToShow)
                        pql.getAttributesToShow().add(bool);
//                    pql.setId(st.nextToken());
//                    pql.setName(dnp.getName());
//                    pql.setNativeType(dnp.getOriginalLanguage());
//                    pql.setLatestVersion(st.nextToken());
//                    pql.setLatestBranch(st.nextToken());
//                    pql.setOwner(viewController.getUsername());
//                    pql.setDomain(dnp.getDomain());
//                    pql.setRanking(dnp.getRanking());
                    results.addLast(pql);
                }
            } else {
                for (String net : netResults) {
                    pql = new ResultPQL();
                    dnp = idNetsNode.get(net);
                    boolean[] attributesToShow=new boolean[8];
                    if (attributes.contains("name"))
                        attributesToShow[0]=true;
                    if (attributes.contains("id"))
                        attributesToShow[1]=true;
                    if (attributes.contains("originalLanguage"))
                        attributesToShow[2]=true;
                    if (attributes.contains("domain"))
                        attributesToShow[3]=true;
                    if (attributes.contains("ranking"))
                        attributesToShow[4]=true;
                    if (attributes.contains("version"))
                        attributesToShow[5]=true;
                    if (attributes.contains("branch"))
                        attributesToShow[6]=true;
                    if (attributes.contains("owner"))
                        attributesToShow[7]=true;
                    pql.setPst(dnp.getValue());
                    pql.setVst(dnp.getVersionSummaryType(net));
                    pql.getAttributesToShow().clear();
                    for(boolean bool : attributesToShow)
                        pql.getAttributesToShow().add(bool);
                    results.addLast(pql);
                }
            }
        return results;
    }

    private Set<String> getSelectAttributes(){
        Set<String> attributes=new HashSet<>();
        String text=query.getText();
        int indexSELECT=text.indexOf(Keywords.SELECT);
        int indexFROM=text.indexOf(Keywords.FROM);
        String substring=text.substring(indexSELECT+6,indexFROM);
        StringTokenizer st=new StringTokenizer(substring," ,");
        while(st.hasMoreTokens())
            attributes.add(st.nextToken());
        return attributes;
    }

    public DraggableNodeProcess getLocation(String idNets){
        System.out.println("KEY: "+idNets);
        return idNetsNode.get(idNets);
    }

    public void addLocation(String idNet, DraggableNodeProcess process){
        idNetsNode.put(idNet,process);
    }

}
