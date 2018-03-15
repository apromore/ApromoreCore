/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
if(!ORYX){ var ORYX = {} }
if(!ORYX.Plugins){ ORYX.Plugins = {} }

ORYX.Plugins.Bprove = ORYX.Plugins.AbstractPlugin.extend({

    facade: undefined,

    construct: function (facade) {

        arguments.callee.$.construct.apply(this, arguments);

        this.contextPath = "/bprove";
        this.servletPath = "/bprove/servlet";
        this.facade = facade;
        this.facade.offer({
            'name': 'Bprove Calculator',
            'functionality': this.checkBprove.bind(this, false),
            'group': 'bpmn-toolkit',
            'icon': this.contextPath +  "/images/bprove.png",
            'description': 'Check model correctness with BProVe',
            'index': 1
        });

        	// The color of the highlighting
        	this.color = "#DC143C";
    },

    checkBprove: function () {


        var parsedModelToBeUsed;
        var servlet=this.servletPath;
        var json = this.facade.getSerializedJSON();
        var passingFacade = this.facade;

        var resultString;
        var resultViolatingTraceString;

        if (this.getDiagramType() != 'bpmn') {
                    Ext.Msg.show({
                        title: "Info",
                        msg: "The process must be a BPMN model, current model is " + this.getDiagramType(),
                        buttons: Ext.Msg.OK,
                        icon: Ext.Msg.INFO
                    }).getDialog().syncSize();
                    return;
        }

        var mType = this.getDiagramType();
        var formPanel = new Ext.FormPanel({
                                            resizable: false,
                                            height: 590,
                                            border : false,
                                            renderTo: document.body,
                                            color: '#DC143C',
                                            items: [{
                                                 id: 'id_textAreaPanel',
                                                 html: '<form id="nonDomainDependent-form"><p><br></p><h1 id="control-6568532" align="center">Non-Domain dependent properties</h1><div class="fb-select form-group field-nondomaindependentproperties"><label for="nondomaindependentproperties" class="fb-select-label">Select Property</label><select class="form-control" name="nondomaindependentproperties" id="nondomaindependentproperties"><option value="Can a Process Start?" selected="true" id="nondomaindependentproperties-0">Can a Process Start?</option><option value="Can a Process End?" id="nondomaindependentproperties-1">Can a Process End?</option><option value="Can All the Processes End?" id="nondomaindependentproperties-2">Can All the Process End?</option><option value="No Dead Activities" id="nondomaindependentproperties-3">No Dead Activities</option><option value="Option to Complete" id="nondomaindependentproperties-4">Option to Complete</option><option value="Proper Completion [Control Flow Tokens Only]" id="nondomaindependentproperties-5">Proper Completion [Control Flow Tokens Only]</option><option value="Proper Completion [Message Flow Tokens Only]" id="nondomaindependentproperties-6">Proper Completion [Message Flow Tokens Only]</option><option value="Proper Completion [Control Flow and Message Flows Tokens]" id="nondomaindependentproperties-7">Proper Completion [Control Flow and Message Flows Tokens]</option><option value="Safeness" id="nondomaindependentproperties-8">Safeness</option></select></div></form><form id="initialForm"><p id="demoTextArea"></p></form><p><br></p><form id="domaindependent-form"><p><br></p><h1 id="control-6568532" align="center">Domain dependent properties</h1><p id="control-6568532" align="center">Properties over single Pool</p><div id="domaindependent"></div><div class="rendered-formPool"><div class="fb-select form-group field-poolNameSelection"><label for="poolNameSelection" class="fb-select-label">Select PoolName</label><select class="form-control" name="poolName" id="poolNameSelection"></select></div></div><div class="fb-select form-group field-domaindependentproperties"><label for="domaindependentproperties" class="fb-select-label">Select Property</label><select class="form-control" name="domaindependentproperties" id="domaindependentproperties"><option value="Can the Process Start?" selected="true" id="domaindependentproperties-0">Can the Process Start?</option><option value="Can the Process End?" id="domaindependentproperties-1">Can the Process End?</option><option value="No Dead Activities" id="domaindependentproperties-3">No Dead Activities</option><option value="Option to Complete" id="domaindependentproperties-4">Option to Complete</option><option value="Proper Completion [Control Flow Tokens Only]" id="domaindependentproperties-5">Proper Completion [Control Flow Tokens Only]</option><option value="Proper Completion [Message Flow Tokens Only]" id="domaindependentproperties-6">Proper Completion [Message Flow Tokens Only]</option><option value="Proper Completion [Control Flow and Message Flows Tokens]" id="domaindependentproperties-7">Proper Completion [Control Flow and Message Flows Tokens]</option><option value="Safeness" id="domaindependentproperties-8">Safeness</option></select></div></form><p align="center"><br>Does the selected Pool send the specified Message?<br></p><form id="poolForm1><label for="poolNameSelection1" class="fb-select-label">Select PoolName1</label><select class="form-control" name="poolName1" id="poolNameSelection1"></select><label for="msgName1" class="fb-select-label">Select Message</label><select class="form-control" name="msgName1" id="msgName1"></select></form><form id="poolForm12"></form><p align="center"><br>Does the selected Pool receive the specified Message?<br></p><form id="poolForm2><label for="poolNameSelection2" class="fb-select-label">Select PoolName2</label><select class="form-control" name="poolName2" id="poolNameSelection2"></select><label for="msgName2" class="fb-select-label">Select Message</label><select class="form-control" name="msgName2" id="msgName2"></select></form><form id="poolForm3"></form><p><br></p><form id="taskForm1"><p id="control-6568532" align="center">Properties over single Task</p><div class="fb-select form-group field-taskNameSelection1"><label for="taskNameSelection1" class="fb-select-label">Select TaskName</label><select class="form-control" name="taskName" id="taskNameSelection1"></select><label for="domaindependentpropertiesTask" class="fb-select-label">Select Property</label><select class="form-control" name="domaindependentpropertiesTask" id="domaindependentpropertiesTask"><option value="Can the Task be Enabled?" selected="true" id="domaindependentpropertiesTask-0">Can the Task be Enabled?</option><option value="Can the Task Run?" id="domaindependentpropertiesTask-1">Can the Task Run?</option><option value="Can the Task Complete?" id="domaindependentpropertiesTask-3">Can the Task Complete?</option></select></div></form><form id="taskForm2"><p align="center"><br>Does Task1 impliy Task2?</p><div class="fb-select form-group field-taskNameSelection2"><label for="taskNameSelection2" class="fb-select-label">Select TaskName1</label><select class="form-control" name="taskName1" id="taskNameSelection2"></select><label for="taskNameSelection3" class="fb-select-label">Select TaskName2</label><select class="form-control" name="taskName2" id="taskNameSelection3"></select></div></form><form id="finalForm"><p><br><br></p><h1 id="lastLabel" align="center">Verification Result</h1><textarea rows="7" cols="86" disabled id="buttonArea" style="overflow-y:scroll"></textarea></form>'
                                                }]
                                             });

        var textArea = document.getElementById("buttonArea");
        textArea.innerHTML = "Waiting for BProVe Response...";

        //console.log("json:"+json);

        //First Ajax to get poolList and taskList and parsedModel
        new Ajax.Request(servlet, {
            parameters: {'data': json, 'property' : "getParsedModel", 'param' : false, 'type': mType},
            method: 'POST',
            asynchronous: true,
            

            onSuccess: function (data) {

                var responseJson = data.responseText;
                var result = responseJson.toString();

                var fieldsParsedModel = result.split('$$$ParsedModel$$$');

                var parsedModelObtained = fieldsParsedModel[1];
                parsedModelToBeUsed=parsedModelObtained;

                var fields = fieldsParsedModel[0].split('$$$Separator$$$');

                var textArea = document.getElementById("buttonArea");
                textArea.innerHTML = "Model Parsed from BPMN to BPMNOS Syntax\n\n"+parsedModelToBeUsed;

                resultString = fields[0];          
                
                var poolList = fields[2];
                poolList = poolList.split('$$$pool$$$');

                var taskList = fields[3];
                taskList=taskList.split('$$$task$$$');

                var inputMsgList = fields[4];
                inputMsgList=inputMsgList.split('$$$inputMsg$$$');

                var outputMsgList = fields[5];
                outputMsgList=outputMsgList.split('$$$outputMsg$$$');

        //VA POPOLATA IL SELECTION MENU PER LA POOL
                var poolSelect = document.getElementById("poolNameSelection");  
                var poolSelect1 = document.getElementById("poolNameSelection1"); 
                var poolSelect2 = document.getElementById("poolNameSelection2");     
                var selectHTML="";

                for (i = 0; i < poolList.length; i++) { 

                    selectHTML+= "<option value='"+poolList[i]+"'>"+poolList[i]+"</option>";
                }
                poolSelect.innerHTML= selectHTML;
                poolSelect1.innerHTML= selectHTML;
                poolSelect2.innerHTML= selectHTML;

        //VA POPOLATA LA SELECTION PER IL TASK
                var task1Select = document.getElementById("taskNameSelection1"); 
                var task2Select = document.getElementById("taskNameSelection2");     
                var task3Select = document.getElementById("taskNameSelection3");          
                var task1selectHTML="";

                for (i = 0; i < taskList.length; i++) { 

                    task1selectHTML+= "<option value='"+taskList[i]+"'>"+taskList[i]+"</option>";
                }
                task1Select.innerHTML= task1selectHTML;
                task2Select.innerHTML= task1selectHTML;
                task3Select.innerHTML= task1selectHTML;

        //VA POPOLATA LA SELECTION PER INPUT MSG
                var msgName1Select = document.getElementById("msgName1"); 
                var msgName1selectHTML="";

                for (i = 0; i < inputMsgList.length; i++) { 

                    msgName1selectHTML+= "<option value='"+inputMsgList[i]+"'>"+inputMsgList[i]+"</option>";
                }
                msgName1Select.innerHTML= msgName1selectHTML;

        //VA POPOLATA LA SELECTION PER OUTPUT MSG
                var msgName2Select = document.getElementById("msgName2");         
                var msgName2selectHTML="";

                for (i = 0; i < outputMsgList.length; i++) { 

                    msgName2selectHTML+= "<option value='"+outputMsgList[i]+"'>"+outputMsgList[i]+"</option>";
                }
                msgName2Select.innerHTML= msgName2selectHTML;

            }.bind(this),

            onFailure: function (data) {
                ////console.log("onfailurePOOLLIST: "+data);
                ////console.log("onfailure0POOLLIST: "+data.responseText);
                msg.hide();
            }.bind(this)

        })



         new Ext.Button({
             id: 'nondomaindependentpropertiesbutton',                                                     
             text: 'Check Collaboration',                                                     
             style:'float: right; text-align: center;',
                handler: function(){
                        var e = document.getElementById("nondomaindependentproperties");
                        var propertyToVerify = e.options[e.selectedIndex].text;
                        
                        var textArea = document.getElementById("buttonArea");
                        textArea.innerHTML = "Waiting for BProVe Response...";

                        new Ajax.Request(servlet, {
                            parameters: {'data': json, 'parsedModel' : parsedModelToBeUsed, 'property' : propertyToVerify, 'param' : false, 'type': mType},
                            method: 'POST',
                            asynchronous: true,

                            onSuccess: function (data) {

                                ////console.log("passingFacade: "+passingFacade);
                                                                    
                                this.resetElementColor(passingFacade);
                                //msg.hide();
                                ////console.log("onsuccess");
                                var responseJson = data.responseText;
                                ////console.log("check bprove"+responseJson);
                                                                     
                                 var result = responseJson.toString();
                                
                                var textArea = document.getElementById("buttonArea");

                                var fields = result.split('$$$Separator$$$');

                                resultString = fields[0]; 
                                textArea.innerHTML = resultString;
                                resultViolatingTraceString = fields[1];
                                //console.log("resultViolatingTraceString: "+resultViolatingTraceString);

                                if((resultViolatingTraceString!=" ") && (resultViolatingTraceString!= "") && (resultViolatingTraceString!=null)){
                                    //console.log("resultViolatingTraceString for Selection: "+resultViolatingTraceString);
                                    ////console.log("passingFacade: "+passingFacade);
                                    this.Selection(passingFacade,resultViolatingTraceString);
                                    ////console.log("PostSelection");
                                }
  
                            }.bind(this),

                            onFailure: function (data) {
                                                                     
                                msg.hide();
                                                                   
                            }.bind(this)
                                                        
                        })                                             		            
                },

                Selection: function(facade,resultViolatingTraceString){

                    //divide violating trace into source and target ids

                    //"££sourceIDs££"+sourceIDs+"££targetIDs££"+targetIDs;
                    var iDsArray = resultViolatingTraceString.split("££targetIDs££");
                    //iDsArray[0]//source
                    //iDsArray[1]//target
                    var sourceIDs0 = iDsArray[0].replace("££sourceIDs££","");
                    var sourceIDs = sourceIDs0.split(",");

                    var targetIDs0 = iDsArray[1].replace("££targetIDs££","");
                    var targetIDs = targetIDs0.split(",");

                    //var iDsArray = resultViolatingTraceString.split(",");

                    //DUE FOR UNO PER SOURCE E UNO PER TARGET


                    var facciata = facade.getCanvas().getChildShapes();
                    //console.log("facciata "+facciata);
                    try {
    

                            this.elementColorSource(facade,sourceIDs,targetIDs);  
                           
                    }
                    catch (err) {
                        //alert("Unable to confront the string: " + err.message);
                    }
                                                
                }.bind(this),

                
                //Reset color
                // Deselect all process model elements.
                resetElementColor: function(facade){
                    try {

                        var retrievedShapes = this.facade.getCanvas().getChildShapes();
                        for ( i=0; i< retrievedShapes.size() ; i++ ){
                                                                     
                            var string = retrievedShapes[i].toString();
                            //DEBUG
                            //////console.log("retrievedShapes[i].toString(): "+string);

                            var result = string.indexOf("Pool");
                            //DEBUG
                            //////console.log("result = string.indexOf(Pool): "+result);

                            if (result != -1){
                                //DEBUG
                                //////console.log("We have a Pool");
                                retrievedShapes[i].setProperty("selected", false);
                                retrievedShapes[i].setProperty("selectioncolor", false);
                                
                                var poolShapes = retrievedShapes[i].getChildShapes();
                                //DEBUG
                                //////console.log("poolShapes: "+poolShapes.size());

                                for ( j=0; j< poolShapes.size() ; j++ ){
                                    //DEBUG
                                    //////console.log("poolShapesID: "+poolShapes[j]);
                                    //////console.log("poolShapesID.nodeId: "+poolShapes[j].nodeId);
                                    //////console.log("poolShapesID.resourceId: "+poolShapes[j].resourceId);
                                    var string2 = poolShapes[j].toString();
                                    var result2 = string2.indexOf("Lane");
                                    if (result2 != -1){
                                        //DEBUG
                                        //////console.log("We have a Lane");

                                        poolShapes[j].setProperty("selected", false);
                                        poolShapes[j].setProperty("selectioncolor", false);
                                        //DEBUG
                                        //////console.log("poolShapes[j].setProperty(selected, true): "+poolShapes[j]);


                                        var laneShapes = poolShapes[j].getChildShapes();
                                        //DEBUG
                                        //////console.log("laneShapes: "+laneShapes.size());

                                        for ( l=0; l< laneShapes.size() ; l++ ){
     
                                            laneShapes[l].setProperty("selected", false);

                                            laneShapes[l].setProperty("selectioncolor", false);
                                            
                                            //DEBUG
                                            //////console.log("laneShapes[j].setProperty(selected, true)");
                                        }
                                    }

                                    poolShapes[j].setProperty("selected", false);
                                    poolShapes[j].setProperty("selectioncolor", false);

                               }
                                                                            

                            }else{
                                //We have no Pool

                                if(string.indexOf("Gateway")){
                                    retrievedShapes[i].setProperty("selected", false);
                                    retrievedShapes[i].setProperty("selectioncolor", false);
                                }else{

                                    if(!string.indexOf("Flow")){
                                        retrievedShapes[i].setProperty("selected", false);
                                        retrievedShapes[i].setProperty("selectioncolor", false);
                                    }//else is a Flow then continue
                                }
                            }

                        }

                        facade.getCanvas().update();
                        facade.updateSelection();
                    }
                    catch (err) {
                        alert("Unable to select all the elements: " + err.message);
                    }
                }.bind(this),
                                                         
                iconCls: 'blist'
        }).render("initialForm");


        //BOTTONE 2 PER OPERAZIONI SU SINGOLE POOL 
        new Ext.Button({
            id: 'dependentpropertiesbutton',
            text: 'Check Pool',
            style:'float: right',
            handler: function(){
                var e = document.getElementById("domaindependentproperties");
                var propertyToVerify = e.options[e.selectedIndex].text;

                var pname = document.getElementById("poolNameSelection");
                var poolName1 = pname.options[pname.selectedIndex].text;
                var poolName2 = "poolName2";
                var taskName1 = "taskName1";
                var taskName2 = "taskName2";
        

                if((poolName1!=null)&&(poolName1!="")&&(poolName1!=" ")){

                    var textArea = document.getElementById("buttonArea");
                    textArea.innerHTML = "Waiting for BProVe Response...";

                new Ajax.Request(servlet, {
                    parameters: {'data': json, 'parsedModel' : parsedModelToBeUsed,  'param' : true, 'property' : propertyToVerify, 'poolName1': poolName1, 'poolName2': poolName2, 'taskName1': taskName1, 'taskName2': taskName2, 'type': mType},

                    method: 'POST',
                    asynchronous: true,

                    onSuccess: function (data) {

                    this.resetElementColor(passingFacade);
                        //msg.hide();
                        //console.log("onsuccess");
                        var responseJson = data.responseText;
                        ////console.log("check bprove"+responseJson);
                        
                        //var trace = responseJson.toString();
                        var result = responseJson.toString();
                        //this.Selection(this.facade,trace)
                        var textArea = document.getElementById("buttonArea");
                        

                        var fields = result.split('$$$Separator$$$');

                        resultString = fields[0];

                        textArea.innerHTML = resultString;

                        resultViolatingTraceString = fields[1];
                        //console.log("resultViolatingTraceString: "+resultViolatingTraceString);

                    if((resultViolatingTraceString!=" ") && (resultViolatingTraceString!= "") && (resultViolatingTraceString!=null)){
                        ////console.log("resultViolatingTraceString for Selection: "+resultViolatingTraceString);
                        ////console.log("passingFacade: "+passingFacade);
                        this.Selection(passingFacade,resultViolatingTraceString);
                        ////console.log("PostSelection");
                    }
                        

                    }.bind(this),

                    onFailure: function (data) {
                        //console.log("onfailure003: "+data);
                        //console.log("onfailure004: "+data.responseText);
                        msg.hide();
                    }.bind(this)
        
                })                
                }                             		            
            },



            Selection: function(facade,parsed){
                                    //"££sourceIDs££"+sourceIDs+"££targetIDs££"+targetIDs;
                                    var iDsArray = resultViolatingTraceString.split("££targetIDs££");
                                    //iDsArray[0]//source
                                    //iDsArray[1]//target
                                    var sourceIDs0 = iDsArray[0].replace("££sourceIDs££","");
                                    var sourceIDs = sourceIDs0.split(",");
                
                                    var targetIDs0 = iDsArray[1].replace("££targetIDs££","");
                                    var targetIDs = targetIDs0.split(",");
                
                                    //var iDsArray = resultViolatingTraceString.split(",");
                
                                    //DUE FOR UNO PER SOURCE E UNO PER TARGET
                
                
                                    var facciata = facade.getCanvas().getChildShapes();
                                    //console.log("facciata "+facciata);
                                    try {
                                        this.elementColorSource(facade,sourceIDs,targetIDs);  
                                    }
                                    catch (err) {
                                        alert("Unable to confront the string: " + err.message);
                                    }

            }.bind(this),


            //Reset color
            // Deselect all process model elements.
            resetElementColor: function(facade){
                try {

                var retrievedShapes = this.facade.getCanvas().getChildShapes();
                for ( i=0; i< retrievedShapes.size() ; i++ ){
                        //DEBUG
                        //////console.log("retrievedShapes.nodeId: "+retrievedShapes[i].nodeId);
                        //////console.log("retrievedShapes.resourceId: "+retrievedShapes[i].resourceId);

                    var string = retrievedShapes[i].toString();
                        //DEBUG
                        //////console.log("retrievedShapes[i].toString(): "+string);

                    var result = string.indexOf("Pool");
                        //DEBUG
                        //////console.log("result = string.indexOf(Pool): "+result);

                    if (result != -1){
                        //DEBUG
                        //////console.log("We have a Pool");
                        retrievedShapes[i].setProperty("selected", false);
                        retrievedShapes[i].setProperty("selectioncolor", false);

                            var poolShapes = retrievedShapes[i].getChildShapes();
                            //DEBUG
                            //////console.log("poolShapes: "+poolShapes.size());

                                for ( j=0; j< poolShapes.size() ; j++ ){
                                //DEBUG
                                //////console.log("poolShapesID: "+poolShapes[j]);
                                //////console.log("poolShapesID.nodeId: "+poolShapes[j].nodeId);
                                //////console.log("poolShapesID.resourceId: "+poolShapes[j].resourceId);
                                    var string2 = poolShapes[j].toString();
                                    var result2 = string2.indexOf("Lane");
                                    if (result2 != -1){
                                    //DEBUG
                                    //////console.log("We have a Lane");

                                    poolShapes[j].setProperty("selected", false);
                                    poolShapes[j].setProperty("selectioncolor", false);


                                    //DEBUG
                                    //////console.log("poolShapes[j].setProperty(selected, true): "+poolShapes[j]);


                                        var laneShapes = poolShapes[j].getChildShapes();
                                        //DEBUG
                                        //////console.log("laneShapes: "+laneShapes.size());

                                        for ( l=0; l< laneShapes.size() ; l++ ){

                                                    laneShapes[l].setProperty("selected", false);
                                                    laneShapes[l].setProperty("selectioncolor", false);

                                                //DEBUG
                                                //////console.log("laneShapes[j].setProperty(selected, true)");
                                        }
                                    }

                                    poolShapes[j].setProperty("selected", false);
                                    poolShapes[j].setProperty("selectioncolor", false);
                                }
                                //DEBUG
                            // ////console.log("poolShapes[i]: "+retrievedShapes[i].getChildShapes());

                    }else{
                        //We have no Pool

                        if(string.indexOf("Gateway")){
                                 retrievedShapes[i].setProperty("selected", false);
                                 retrievedShapes[i].setProperty("selectioncolor", false);
                        }else{

                            if(!string.indexOf("Flow")){
                            retrievedShapes[i].setProperty("selected", false);
                            retrievedShapes[i].setProperty("selectioncolor", false);
                            }//else is a Flow then continue
                        }
                    }

                    }

                    facade.getCanvas().update();
                    facade.updateSelection();
                }
                catch (err) {
                    alert("Unable to select all the elements: " + err.message);
                }
            }.bind(this),
            
            iconCls: 'blist'
        }).render("domaindependent-form");




        //BOTTONE 3 PER OPERAZIONI SU SINGOLO TASK

        new Ext.Button({
            id: 'dependentpropertiesbuttonTask',
            text: 'Check Task',
            style:'float: right',
            handler: function(){
                var e = document.getElementById("domaindependentpropertiesTask");
                var propertyToVerify = e.options[e.selectedIndex].text;

                var tname = document.getElementById("taskNameSelection1");
                var poolName1 = "poolName1";
                var poolName2 = "poolName2";
                var taskName1 =  tname.options[tname.selectedIndex].text;
                var taskName2 = "taskName2";


                if((poolName1!=null)&&(poolName1!="")&&(poolName1!=" ")){
                    
                    ////console.log("\nAjaxRequest\n");
                    var textArea = document.getElementById("buttonArea");
                    textArea.innerHTML = "Waiting for BProVe Response...";

                new Ajax.Request(servlet, {
                    parameters: {'data': json, 'parsedModel' : parsedModelToBeUsed,  'param' : true, 'property' : propertyToVerify, 'poolName1': poolName1, 'poolName2': poolName2, 'taskName1': taskName1, 'taskName2': taskName2, 'type': mType},

                    method: 'POST',
                    asynchronous: true,

                    onSuccess: function (data) {

                    this.resetElementColor(passingFacade);
                        //msg.hide();
                        ////console.log("onsuccess");
                        var responseJson = data.responseText;
                        ////console.log("check bprove"+responseJson);
                        
                        //var trace = responseJson.toString();
                        var result = responseJson.toString();
                        //this.Selection(this.facade,trace)
                        var textArea = document.getElementById("buttonArea");
                        

                        var fields = result.split('$$$Separator$$$');

                        resultString = fields[0];

                        textArea.innerHTML = resultString;

                        resultViolatingTraceString = fields[1];
                        //console.log("resultViolatingTraceString: "+resultViolatingTraceString);

                    if((resultViolatingTraceString!=" ") && (resultViolatingTraceString!= "") && (resultViolatingTraceString!=null)){
                        ////console.log("resultViolatingTraceString for Selection: "+resultViolatingTraceString);
                        ////console.log("passingFacade: "+passingFacade);
                        this.Selection(passingFacade,resultViolatingTraceString);
                        ////console.log("PostSelection");
                    }

                    }.bind(this),

                    onFailure: function (data) {
                        ////console.log("onfailure003: "+data);
                        ////console.log("onfailure004: "+data.responseText);
                        msg.hide();

                    }.bind(this)
        
                })                
                }                             		            
            },

            

            Selection: function(facade,parsed){
                    //"££sourceIDs££"+sourceIDs+"££targetIDs££"+targetIDs;
                    var iDsArray = resultViolatingTraceString.split("££targetIDs££");
                    //iDsArray[0]//source
                    //iDsArray[1]//target
                    var sourceIDs0 = iDsArray[0].replace("££sourceIDs££","");
                    var sourceIDs = sourceIDs0.split(",");

                    var targetIDs0 = iDsArray[1].replace("££targetIDs££","");
                    var targetIDs = targetIDs0.split(",");

                    //var iDsArray = resultViolatingTraceString.split(",");

                    //DUE FOR UNO PER SOURCE E UNO PER TARGET

                    var facciata = facade.getCanvas().getChildShapes();
                    //console.log("facciata "+facciata);
                    try {
                        this.elementColorSource(facade,sourceIDs,targetIDs);  
                    }
                    catch (err) {
                        alert("Unable to confront the string: " + err.message);
                    }

            }.bind(this),

           
            //Reset color
            // Deselect all process model elements.
            resetElementColor: function(facade){
                try {

                var retrievedShapes = this.facade.getCanvas().getChildShapes();
                for ( i=0; i< retrievedShapes.size() ; i++ ){
                        //DEBUG
                        //////console.log("retrievedShapes.nodeId: "+retrievedShapes[i].nodeId);
                        //////console.log("retrievedShapes.resourceId: "+retrievedShapes[i].resourceId);

                    var string = retrievedShapes[i].toString();
                        //DEBUG
                        //////console.log("retrievedShapes[i].toString(): "+string);

                    var result = string.indexOf("Pool");
                        //DEBUG
                        //////console.log("result = string.indexOf(Pool): "+result);

                    if (result != -1){
                        //DEBUG
                        //////console.log("We have a Pool");
                        retrievedShapes[i].setProperty("selected", false);
                        retrievedShapes[i].setProperty("selectioncolor", false);

                            var poolShapes = retrievedShapes[i].getChildShapes();
                            //DEBUG
                            //////console.log("poolShapes: "+poolShapes.size());

                                for ( j=0; j< poolShapes.size() ; j++ ){
                                //DEBUG
                                //////console.log("poolShapesID: "+poolShapes[j]);
                                //////console.log("poolShapesID.nodeId: "+poolShapes[j].nodeId);
                                //////console.log("poolShapesID.resourceId: "+poolShapes[j].resourceId);
                                    var string2 = poolShapes[j].toString();
                                    var result2 = string2.indexOf("Lane");
                                    if (result2 != -1){
                                    //DEBUG
                                    //////console.log("We have a Lane");

                                    poolShapes[j].setProperty("selected", false);
                                    poolShapes[j].setProperty("selectioncolor", false);


                                    //DEBUG
                                    //////console.log("poolShapes[j].setProperty(selected, true): "+poolShapes[j]);


                                        var laneShapes = poolShapes[j].getChildShapes();
                                        //DEBUG
                                        //////console.log("laneShapes: "+laneShapes.size());

                                        for ( l=0; l< laneShapes.size() ; l++ ){

                                                    laneShapes[l].setProperty("selected", false);
                                                    laneShapes[l].setProperty("selectioncolor", false);

                                                //DEBUG
                                                //////console.log("laneShapes[j].setProperty(selected, true)");
                                        }
                                    }

                                    poolShapes[j].setProperty("selected", false);
                                    poolShapes[j].setProperty("selectioncolor", false);
 
                                }
                                //DEBUG
                            // ////console.log("poolShapes[i]: "+retrievedShapes[i].getChildShapes());

                    }else{
                        //We have no Pool

                        if(string.indexOf("Gateway")){
                                                        retrievedShapes[i].setProperty("selected", false);
                                                        retrievedShapes[i].setProperty("selectioncolor", false);
                        }else{

                            if(!string.indexOf("Flow")){
                            retrievedShapes[i].setProperty("selected", false);
                            retrievedShapes[i].setProperty("selectioncolor", false);

                            }//else is a Flow then continue
                        }
                    }

                    }

                    facade.getCanvas().update();
                    facade.updateSelection();
                }
                catch (err) {
                    alert("Unable to select all the elements: " + err.message);
                }
            }.bind(this),
            
            iconCls: 'blist'
        }).render("taskForm1");




        //BOTTONE 4 PER TASK1 IMPLIES TASK2
        new Ext.Button({
            id: 'dependentpropertiesbuttonTask2',
            text: 'Check T1 Implies T2',
            style:'float: right',
            handler: function(){
                var propertyToVerify = "Task1ImpliesTask2";

                var tname1 = document.getElementById("taskNameSelection2");
                var tname2 = document.getElementById("taskNameSelection3");
                var poolName1 = "poolName1";
                var poolName2 = "poolName2";
                var taskName1 = tname1.options[tname1.selectedIndex].text;
                var taskName2 = tname2.options[tname2.selectedIndex].text;


                if((poolName1!=null)&&(poolName1!="")&&(poolName1!=" ")){
                    
                    ////console.log("\nAjaxRequest\n");
                    var textArea = document.getElementById("buttonArea");
                    textArea.innerHTML = "Waiting for BProVe Response...";

                new Ajax.Request(servlet, {
                    parameters: {'data': json, 'parsedModel' : parsedModelToBeUsed,  'param' : true, 'property' : propertyToVerify, 'poolName1': poolName1, 'poolName2': poolName2, 'taskName1': taskName1, 'taskName2': taskName2, 'type': mType},

                    method: 'POST',
                    asynchronous: true,

                    onSuccess: function (data) {

                    this.resetElementColor(passingFacade);
                        //msg.hide();
                        ////console.log("onsuccess");
                        var responseJson = data.responseText;
                        ////console.log("check bprove"+responseJson);
                        
                        //var trace = responseJson.toString();
                        var result = responseJson.toString();
                        //this.Selection(this.facade,trace)
                        var textArea = document.getElementById("buttonArea");
                        

                        var fields = result.split('$$$Separator$$$');

                        resultString = fields[0];

                        textArea.innerHTML = resultString;

                        resultViolatingTraceString = fields[1];
                        //console.log("resultViolatingTraceString: "+resultViolatingTraceString);

                    if((resultViolatingTraceString!=" ") && (resultViolatingTraceString!= "") && (resultViolatingTraceString!=null)){
                        ////console.log("resultViolatingTraceString for Selection: "+resultViolatingTraceString);
                        ////console.log("passingFacade: "+passingFacade);
                        this.Selection(passingFacade,resultViolatingTraceString);
                        ////console.log("PostSelection");
                    }



                    }.bind(this),

                    onFailure: function (data) {
                        ////console.log("onfailure003: "+data);
                        ////console.log("onfailure004: "+data.responseText);
                        msg.hide();
                        // Ext.Msg.show({
                        //     title: "Error",
                        //     msg: "The communication with the bprove servlet failed.",
                        //     buttons: Ext.Msg.OK,
                        //     icon: Ext.Msg.ERROR
                        // }).getDialog().syncSize()
                    }.bind(this)
        
                })                
                }                             		            
            },

            

            Selection: function(facade,parsed){
                    //"££sourceIDs££"+sourceIDs+"££targetIDs££"+targetIDs;
                    var iDsArray = resultViolatingTraceString.split("££targetIDs££");
                    //iDsArray[0]//source
                    //iDsArray[1]//target
                    var sourceIDs0 = iDsArray[0].replace("££sourceIDs££","");
                    var sourceIDs = sourceIDs0.split(",");

                    var targetIDs0 = iDsArray[1].replace("££targetIDs££","");
                    var targetIDs = targetIDs0.split(",");

                    //var iDsArray = resultViolatingTraceString.split(",");

                    //DUE FOR UNO PER SOURCE E UNO PER TARGET


                    var facciata = facade.getCanvas().getChildShapes();
                    //console.log("facciata "+facciata);
                    try {
                        this.elementColorSource(facade,sourceIDs,targetIDs);  
                    }
                    catch (err) {
                        alert("Unable to confront the string: " + err.message);
                    }
 

            }.bind(this),

            //Reset color
            // Deselect all process model elements.
            resetElementColor: function(facade){
                try {

                var retrievedShapes = this.facade.getCanvas().getChildShapes();
                for ( i=0; i< retrievedShapes.size() ; i++ ){
                        //DEBUG
                        //////console.log("retrievedShapes.nodeId: "+retrievedShapes[i].nodeId);
                        //////console.log("retrievedShapes.resourceId: "+retrievedShapes[i].resourceId);

                    var string = retrievedShapes[i].toString();
                        //DEBUG
                        //////console.log("retrievedShapes[i].toString(): "+string);

                    var result = string.indexOf("Pool");
                        //DEBUG
                        //////console.log("result = string.indexOf(Pool): "+result);

                    if (result != -1){
                        //DEBUG
                        //////console.log("We have a Pool");
                        retrievedShapes[i].setProperty("selected", false);
                        retrievedShapes[i].setProperty("selectioncolor", false);

                            var poolShapes = retrievedShapes[i].getChildShapes();
                            //DEBUG
                            //////console.log("poolShapes: "+poolShapes.size());

                                for ( j=0; j< poolShapes.size() ; j++ ){
                                //DEBUG
                                //////console.log("poolShapesID: "+poolShapes[j]);
                                //////console.log("poolShapesID.nodeId: "+poolShapes[j].nodeId);
                                //////console.log("poolShapesID.resourceId: "+poolShapes[j].resourceId);
                                    var string2 = poolShapes[j].toString();
                                    var result2 = string2.indexOf("Lane");
                                    if (result2 != -1){
                                    //DEBUG
                                    //////console.log("We have a Lane");

                                    poolShapes[j].setProperty("selected", false);
                                    poolShapes[j].setProperty("selectioncolor", false);
  

                                    //DEBUG
                                    //////console.log("poolShapes[j].setProperty(selected, true): "+poolShapes[j]);


                                        var laneShapes = poolShapes[j].getChildShapes();
                                        //DEBUG
                                        //////console.log("laneShapes: "+laneShapes.size());

                                        for ( l=0; l< laneShapes.size() ; l++ ){

                                                    laneShapes[l].setProperty("selected", false);
                                                    laneShapes[l].setProperty("selectioncolor", false);

                                                //DEBUG
                                                //////console.log("laneShapes[j].setProperty(selected, true)");
                                        }
                                    }

                                    poolShapes[j].setProperty("selected", false);
                                    poolShapes[j].setProperty("selectioncolor", false);
                                }
                                //DEBUG
                            // ////console.log("poolShapes[i]: "+retrievedShapes[i].getChildShapes());

                    }else{
                        //We have no Pool

                        if(string.indexOf("Gateway")){
                                                        retrievedShapes[i].setProperty("selected", false);
                                                        retrievedShapes[i].setProperty("selectioncolor", false);
                        }else{

                            if(!string.indexOf("Flow")){
                            retrievedShapes[i].setProperty("selected", false);
                            retrievedShapes[i].setProperty("selectioncolor", false);
                            }//else is a Flow then continue
                        }
                    }

                    }

                    facade.getCanvas().update();
                    facade.updateSelection();
                }
                catch (err) {
                    alert("Unable to select all the elements: " + err.message);
                }
            }.bind(this),
            
            iconCls: 'blist'
        }).render("taskForm2");


        // //BOTTONE 5 (IN REALTà 4 E POI 5 IL 4 ORA è IL 6)
        // //PER CHECK POOL SND MESSAGE
        new Ext.Button({
            id: 'dependentpropertiesbuttonPoolSndMsg',
            text: 'Check SndMsg',
            style:'float: right',
            handler: function(){
                var propertyToVerify = "aBPoolSndMsg";
                var msg = document.getElementById("msgName1");
                var msgName1 = msg.options[msg.selectedIndex].text;

                var pname = document.getElementById("poolNameSelection1");
                var poolName1 = pname.options[pname.selectedIndex].text;
                var poolName2 = "poolName2";
                var taskName1 = "taskName1";
                var taskName2 = "taskName2";


                if((poolName1!=null)&&(poolName1!="")&&(poolName1!=" ")){
                    
                    ////console.log("\nAjaxRequest\n");
                    var textArea = document.getElementById("buttonArea");
                    textArea.innerHTML = "Waiting for BProVe Response...";

                new Ajax.Request(servlet, {
                    parameters: {'data': json, 'parsedModel' : parsedModelToBeUsed,  'param' : true, 'property' : propertyToVerify, 'poolName1': poolName1, 'poolName2': poolName2, 'taskName1': taskName1, 'taskName2': taskName2, 'msgName':msgName1, 'type': mType},

                    method: 'POST',
                    asynchronous: true,

                    onSuccess: function (data) {

                    this.resetElementColor(passingFacade);
                        //msg.hide();
                        ////console.log("onsuccess");
                        var responseJson = data.responseText;
                        ////console.log("check bprove"+responseJson);
                        
                        //var trace = responseJson.toString();
                        var result = responseJson.toString();
                        //this.Selection(this.facade,trace)
                        var textArea = document.getElementById("buttonArea");
                        

                        var fields = result.split('$$$Separator$$$');

                        resultString = fields[0];

                        textArea.innerHTML = resultString;

                        resultViolatingTraceString = fields[1];
                        //console.log("resultViolatingTraceString: "+resultViolatingTraceString);

                    if((resultViolatingTraceString!=" ") && (resultViolatingTraceString!= "") && (resultViolatingTraceString!=null)){
                        ////console.log("resultViolatingTraceString for Selection: "+resultViolatingTraceString);
                        ////console.log("passingFacade: "+passingFacade);
                        this.Selection(passingFacade,resultViolatingTraceString);
                        ////console.log("PostSelection");
                    }


                    }.bind(this),

                    onFailure: function (data) {
                        ////console.log("onfailure003: "+data);
                        ////console.log("onfailure004: "+data.responseText);
                        msg.hide();

                    }.bind(this)
        
                })                
                }                             		            
            },

            

            Selection: function(facade,parsed){

                                    //"££sourceIDs££"+sourceIDs+"££targetIDs££"+targetIDs;
                                    var iDsArray = resultViolatingTraceString.split("££targetIDs££");
                                    //iDsArray[0]//source
                                    //iDsArray[1]//target
                                    var sourceIDs0 = iDsArray[0].replace("££sourceIDs££","");
                                    var sourceIDs = sourceIDs0.split(",");
                
                                    var targetIDs0 = iDsArray[1].replace("££targetIDs££","");
                                    var targetIDs = targetIDs0.split(",");
                
                                    //var iDsArray = resultViolatingTraceString.split(",");
                
                                    //DUE FOR UNO PER SOURCE E UNO PER TARGET
                
                
                                    var facciata = facade.getCanvas().getChildShapes();
                                    //console.log("facciata "+facciata);
                                    try {
                                        this.elementColorSource(facade,sourceIDs,targetIDs);  
                                    }
                                    catch (err) {
                                        alert("Unable to confront the string: " + err.message);
                                    }

            }.bind(this),


            //Reset color
            // Deselect all process model elements.
            resetElementColor: function(facade){
                try {

                var retrievedShapes = this.facade.getCanvas().getChildShapes();
                for ( i=0; i< retrievedShapes.size() ; i++ ){
                        //DEBUG
                        //////console.log("retrievedShapes.nodeId: "+retrievedShapes[i].nodeId);
                        //////console.log("retrievedShapes.resourceId: "+retrievedShapes[i].resourceId);

                    var string = retrievedShapes[i].toString();
                        //DEBUG
                        //////console.log("retrievedShapes[i].toString(): "+string);

                    var result = string.indexOf("Pool");
                        //DEBUG
                        //////console.log("result = string.indexOf(Pool): "+result);

                    if (result != -1){
                        //DEBUG
                        //////console.log("We have a Pool");
                        retrievedShapes[i].setProperty("selected", false);
                        retrievedShapes[i].setProperty("selectioncolor", false);

                            var poolShapes = retrievedShapes[i].getChildShapes();
                            //DEBUG
                            //////console.log("poolShapes: "+poolShapes.size());

                                for ( j=0; j< poolShapes.size() ; j++ ){
                                //DEBUG
                                //////console.log("poolShapesID: "+poolShapes[j]);
                                //////console.log("poolShapesID.nodeId: "+poolShapes[j].nodeId);
                                //////console.log("poolShapesID.resourceId: "+poolShapes[j].resourceId);
                                    var string2 = poolShapes[j].toString();
                                    var result2 = string2.indexOf("Lane");
                                    if (result2 != -1){
                                    //DEBUG
                                    //////console.log("We have a Lane");

                                    poolShapes[j].setProperty("selected", false);
                                    poolShapes[j].setProperty("selectioncolor", false);

                                    //DEBUG
                                    //////console.log("poolShapes[j].setProperty(selected, true): "+poolShapes[j]);


                                        var laneShapes = poolShapes[j].getChildShapes();
                                        //DEBUG
                                        //////console.log("laneShapes: "+laneShapes.size());

                                        for ( l=0; l< laneShapes.size() ; l++ ){

                                                    laneShapes[l].setProperty("selected", false);
                                                    laneShapes[l].setProperty("selectioncolor", false);

                                                //DEBUG
                                                //////console.log("laneShapes[j].setProperty(selected, true)");
                                        }
                                    }

                                    poolShapes[j].setProperty("selected", false);
                                    poolShapes[j].setProperty("selectioncolor", false);
                                }
                                //DEBUG
                            // ////console.log("poolShapes[i]: "+retrievedShapes[i].getChildShapes());

                    }else{
                        //We have no Pool

                        if(string.indexOf("Gateway")){
                                                        retrievedShapes[i].setProperty("selected", false);
                                                        retrievedShapes[i].setProperty("selectioncolor", false);
                        }else{

                            if(!string.indexOf("Flow")){
                            retrievedShapes[i].setProperty("selected", false);
                            retrievedShapes[i].setProperty("selectioncolor", false);
                            }//else is a Flow then continue
                        }
                    }

                    }

                    facade.getCanvas().update();
                    facade.updateSelection();
                }
                catch (err) {
                    alert("Unable to select all the elements: " + err.message);
                }
            }.bind(this),
            
            iconCls: 'blist'
        }).render("poolForm12");



        //BUTTON 6 IN REALTA' 5 PERCHè IL 4 ORA è 6
        // //PER CHECK POOL RCV MESSAGE
        new Ext.Button({
            id: 'dependentpropertiesbuttonPoolRcvMsg',
            text: 'Check RcvMsg',
            style:'float: right',
            handler: function(){
                var propertyToVerify = "aBPoolRcvMsg";
                var msg2 = document.getElementById("msgName2");
                var msgName2 = msg2.options[msg2.selectedIndex].text;

                var pname = document.getElementById("poolNameSelection2");
                var poolName1 = pname.options[pname.selectedIndex].text;
                var poolName2 = "poolName2";
                var taskName1 = "taskName1";
                var taskName2 = "taskName2";

                if((poolName1!=null)&&(poolName1!="")&&(poolName1!=" ")){
                    
                    ////console.log("\nAjaxRequest\n");
                    var textArea = document.getElementById("buttonArea");
                    textArea.innerHTML = "Waiting for BProVe Response...";

                new Ajax.Request(servlet, {
                    parameters: {'data': json, 'parsedModel' : parsedModelToBeUsed,  'param' : true, 'property' : propertyToVerify, 'poolName1': poolName1, 'poolName2': poolName2, 'taskName1': taskName1, 'taskName2': taskName2, 'msgName':msgName2, 'type': mType},

                    method: 'POST',
                    asynchronous: true,

                    onSuccess: function (data) {

                    this.resetElementColor(passingFacade);
                        //msg.hide();
                        ////console.log("onsuccess");
                        var responseJson = data.responseText;
                        ////console.log("check bprove"+responseJson);
                        
                        //var trace = responseJson.toString();
                        var result = responseJson.toString();
                        //this.Selection(this.facade,trace)
                        var textArea = document.getElementById("buttonArea");
                        

                        var fields = result.split('$$$Separator$$$');

                        resultString = fields[0];

                        textArea.innerHTML = resultString;

                        resultViolatingTraceString = fields[1];
                        //console.log("resultViolatingTraceString: "+resultViolatingTraceString);

                    if((resultViolatingTraceString!=" ") && (resultViolatingTraceString!= "") && (resultViolatingTraceString!=null)){
                        ////console.log("resultViolatingTraceString for Selection: "+resultViolatingTraceString);
                        ////console.log("passingFacade: "+passingFacade);
                        this.Selection(passingFacade,resultViolatingTraceString);
                        ////console.log("PostSelection");
                    }

                    }.bind(this),

                    onFailure: function (data) {
                        ////console.log("onfailure003: "+data);
                        ////console.log("onfailure004: "+data.responseText);
                        msg.hide();

                    }.bind(this)
        
                })                
                }                             		            
            },

            

            Selection: function(facade,parsed){
                    //"££sourceIDs££"+sourceIDs+"££targetIDs££"+targetIDs;
                    var iDsArray = resultViolatingTraceString.split("££targetIDs££");
                    //iDsArray[0]//source
                    //iDsArray[1]//target
                    var sourceIDs0 = iDsArray[0].replace("££sourceIDs££","");
                    var sourceIDs = sourceIDs0.split(",");

                    var targetIDs0 = iDsArray[1].replace("££targetIDs££","");
                    var targetIDs = targetIDs0.split(",");

                    //var iDsArray = resultViolatingTraceString.split(",");

                    //DUE FOR UNO PER SOURCE E UNO PER TARGET


                    var facciata = facade.getCanvas().getChildShapes();
                    //console.log("facciata "+facciata);
                    try {
                        this.elementColorSource(facade,sourceIDs,targetIDs);  
                    }
                    catch (err) {
                        alert("Unable to confront the string: " + err.message);
                    }

            }.bind(this),           

            //Reset color
            // Deselect all process model elements.
            resetElementColor: function(facade){
                try {

                var retrievedShapes = this.facade.getCanvas().getChildShapes();
                for ( i=0; i< retrievedShapes.size() ; i++ ){
                        //DEBUG
                        //////console.log("retrievedShapes.nodeId: "+retrievedShapes[i].nodeId);
                        //////console.log("retrievedShapes.resourceId: "+retrievedShapes[i].resourceId);

                    var string = retrievedShapes[i].toString();
                        //DEBUG
                        //////console.log("retrievedShapes[i].toString(): "+string);

                    var result = string.indexOf("Pool");
                        //DEBUG
                        //////console.log("result = string.indexOf(Pool): "+result);

                    if (result != -1){
                        //DEBUG
                        //////console.log("We have a Pool");
                        retrievedShapes[i].setProperty("selected", false);
                        retrievedShapes[i].setProperty("selectioncolor", false);
                            var poolShapes = retrievedShapes[i].getChildShapes();
                            //DEBUG
                            //////console.log("poolShapes: "+poolShapes.size());

                                for ( j=0; j< poolShapes.size() ; j++ ){
                                //DEBUG
                                //////console.log("poolShapesID: "+poolShapes[j]);
                                //////console.log("poolShapesID.nodeId: "+poolShapes[j].nodeId);
                                //////console.log("poolShapesID.resourceId: "+poolShapes[j].resourceId);
                                    var string2 = poolShapes[j].toString();
                                    var result2 = string2.indexOf("Lane");
                                    if (result2 != -1){
                                    //DEBUG
                                    //////console.log("We have a Lane");

                                    poolShapes[j].setProperty("selected", false);
                                    poolShapes[j].setProperty("selectioncolor", false);


                                    //DEBUG
                                    //////console.log("poolShapes[j].setProperty(selected, true): "+poolShapes[j]);


                                        var laneShapes = poolShapes[j].getChildShapes();
                                        //DEBUG
                                        //////console.log("laneShapes: "+laneShapes.size());

                                        for ( l=0; l< laneShapes.size() ; l++ ){

                                                    laneShapes[l].setProperty("selected", false);
                                                    laneShapes[l].setProperty("selectioncolor", false);
                                                //DEBUG
                                                //////console.log("laneShapes[j].setProperty(selected, true)");
                                        }
                                    }

                                    poolShapes[j].setProperty("selected", false);
                                    poolShapes[j].setProperty("selectioncolor", false);

                                }
                                //DEBUG
                            // ////console.log("poolShapes[i]: "+retrievedShapes[i].getChildShapes());

                    }else{
                        //We have no Pool

                        if(string.indexOf("Gateway")){
                                                        retrievedShapes[i].setProperty("selected", false);
                                                        retrievedShapes[i].setProperty("selectioncolor", false);
                        }else{

                            if(!string.indexOf("Flow")){
                            retrievedShapes[i].setProperty("selected", false);
                            retrievedShapes[i].setProperty("selectioncolor", false);

                            }//else is a Flow then continue
                        }
                    }

                    }

                    facade.getCanvas().update();
                    facade.updateSelection();
                }
                catch (err) {
                    alert("Unable to select all the elements: " + err.message);
                }
            }.bind(this),
            
            iconCls: 'blist'
        }).render("poolForm3");



    var formWindow = new Ext.Window({
        resizable: false,
        closeable: true,
        minimizable: false,
        width: 550,
        minHeight: 400,
        layout: "anchor",
        bodyStyle: "background-color:white; color: black; overflow: visible;",
        title: "Check Properties with BProVe",
        items: [formPanel],

        });
    formWindow.show()

    },

    //CHIUSO CECKBPROVE
        elementColor: function(nId,facade){

            ////console.log("inside elementColor: "+nId);
             var color = "#DC143C";
             try {
                 var retrievedShapes = facade.getCanvas().getChildShapes();
                for ( i=0; i< retrievedShapes.size() ; i++ ){

                    //DEBUG
                    //////console.log("retrievedShapes.size(): "+retrievedShapes.size());
                    //////console.log("retrievedShapes.nodeId: "+retrievedShapes[i].nodeId);
                    //////console.log("retrievedShapes.resourceId: "+retrievedShapes[i].resourceId);

                    var string = retrievedShapes[i].toString();
                    var result = string.indexOf("Pool");

                    //DEBUG
                    //////console.log("result = string.indexOf(Pool): "+result);

                    if (result != -1){

                           if(nId == retrievedShapes[i].resourceId){
                                 retrievedShapes[i].setProperty("selected", true);
                                 retrievedShapes[i].setProperty("selectioncolor", color);
                                 //retrievedShapes[i].setProperty("bordercolor", color);
                                 break;
                           }

                            var poolShapes = retrievedShapes[i].getChildShapes();
                            //DEBUG
                            //////console.log("poolShapes: "+poolShapes.size());

                             for ( j=0; j< poolShapes.size() ; j++ ){
                             //DEBUG
                                //////console.log("poolShapesID: "+poolShapes[j]);
                                //////console.log("poolShapesID.nodeId: "+poolShapes[j].nodeId);
                                //////console.log("poolShapesID.resourceId: "+poolShapes[j].resourceId);
                                 var string2 = poolShapes[j].toString();
                                 var result2 = string2.indexOf("Lane");
                                 if(nId == retrievedShapes[i].resourceId){
                                            poolShapes[j].setProperty("selected", true);
                                            poolShapes[j].setProperty("selectioncolor", color);
                                            break;
                                 }

                                  if (result2 != -1){
                                  //DEBUG
                                  //////console.log("We have a Lane");

                                    if(nId == poolShapes[j].resourceId){
                                         poolShapes[j].setProperty("selected", true);
                                         poolShapes[j].setProperty("selectioncolor", color);
                                         //poolShapes[j].setProperty("bordercolor", color);
                                         //DEBUG
                                         //////console.log("poolShapes[j].setProperty(selected, true): "+poolShapes[j]);
                                         break;
                                    }

                                    var laneShapes = poolShapes[j].getChildShapes();
                                    //DEBUG
                                    //////console.log("laneShapes: "+laneShapes.size());

                                      for ( l=0; l< laneShapes.size() ; l++ ){

                                                if(nId == laneShapes[l].resourceId){
                                                  laneShapes[l].setProperty("selected", true);
                                                  laneShapes[l].setProperty("selectioncolor", color);
                                                  //DEBUG
                                                  //////console.log("laneShapes[j].setProperty(selected, true)");
                                                  break;
                                                  }
                                      }

                                  }
                                   if(nId == poolShapes[j].resourceId){
                                    poolShapes[j].setProperty("selected", true);
                                    poolShapes[j].setProperty("selectioncolor", color);
                                    //poolShapes[j].setProperty("bordercolor", color);
                                    break;
                                  }

                             }
                             //DEBUG
                             // ////console.log("poolShapes[i]: "+retrievedShapes[i].getChildShapes());

                    }
                    ////console.log("retrievedShapes[i].resourceId: "+retrievedShapes[i].resourceId);
                    ////console.log("nId: "+nId);
                    if(nId == retrievedShapes[i].resourceId){
                                            ////console.log("FOUND MATCH: "+nId +", "+retrievedShapes[i].resourceId);
                                             retrievedShapes[i].setProperty("selected", true);
                                             retrievedShapes[i].setProperty("selectioncolor", color);
                                            break;
                                          }
                 }//for che scorre elementi del modello
                 ////console.log("facade.getCanvas().update()");
                 facade.getCanvas().update();
                 facade.updateSelection();
             }
             catch (err) {
             	alert("Unable to select all the elements: " + err.message);
             }
         }.bind(this),


         elementColorSource: function(facade,sourceIDs,targetIDs){

            var facciata = facade.getCanvas().getChildShapes();
            //console.log("inside elementColorSource: ");
            //console.log("facciata: "+facciata);
             var color = "#DC143C";
             try {
                 var retrievedShapes = facciata;               

//array di appoggio per salvare ID elementi colorati
                var coloredElements=new Array();
                var coloredElementsObject=new Array();
                
//scorro tutti gli elementi del modello                
                for ( i=0; i< retrievedShapes.size() ; i++ ){

                    //console.log("ANALYZING ELEMENT: "+retrievedShapes[i].toString());
                    var string = retrievedShapes[i].toString();
                    if(retrievedShapes[i].toString().includes("Message Flow")){
                        //console.log("FOUND A MESSAGE FLOW");
                        continue;
                    }
                    // if(retrievedShapes[i].toString().includes("Sequence Flow")){
                    //     //console.log("FOUND A SEQUENCE FLOW");
                    //     continue;
                    // }
                    var result = string.indexOf("Pool");
                    //DEBUG
                    ////console.log("result = string.indexOf(Pool): "+result);
 //CASO IN CUI l'elemento non è in una Pool
                    if (result == -1){

                    var elementSource = retrievedShapes[i].resourceId;

//colora gli elementi ai quali i sequence flow sorgenti (incoming SQF) puntano
                    for ( idIndex=0; idIndex< sourceIDs.size() ; idIndex++ ){
                        //retrievedShapes[i].resourceId del SQF non è l'id che è salvato negli altri elementi
                        //l'id salvato negli altri elementi è retrievedShapes[i].id
                        //confrontare quindi con questo id
                        if(retrievedShapes[i].resourceId.toString().includes(sourceIDs[idIndex].toString())){

                            var outgoingID = retrievedShapes[i].outgoing;
                                outgoingID= outgoingID.toString();
                                outgoingID= outgoingID.split(" ");
                            var outgoingIDString = outgoingID[outgoingID.size()-1];
                            ////console.log("outgoingIDString: "+outgoingIDString);
                            ////console.log("cerco id degli elementi");

                            for ( k=0; k< retrievedShapes.size() ; k++ ){
                                
                                if(retrievedShapes[k].toString().includes("Message Flow")){
                                    //console.log("FOUND A MESSAGE FLOW");
                                    continue;
                                }
                                
                                var elementId = retrievedShapes[k].id.toString();
                                //console.log("elementId: "+elementId+" outgoingIDString: "+outgoingIDString);
                                ////console.log("incoming dell'elemento: "+retrievedShapes[k].incoming+" incomingIDString: "+incomingIDString);
                                if(elementId == outgoingIDString){
                                    retrievedShapes[k].setProperty("selected", true);
                                    retrievedShapes[k].setProperty("selectioncolor", color);
                                    coloredElements.push(elementId);
                                    coloredElementsObject.push(retrievedShapes[k]);
                                }
                            }
                        }
                    }

//colora gli elementi dai quali i sequence flow target (outoing SQF) hanno origine [TODO: UNIRE A CODICE SOPRA]
                    for ( idIndex2=0; idIndex2< targetIDs.size() ; idIndex2++ ){
                        //retrievedShapes[i].resourceId del SQF non è l'id che è salvato negli altri elementi
                        //l'id salvato negli altri elementi è retrievedShapes[i].id
                        //confrontare quindi con questo id
                        if(retrievedShapes[i].resourceId.toString().includes(targetIDs[idIndex2].toString())){
                            
                            var incomingID = retrievedShapes[i].incoming;
                                incomingID= incomingID.toString();
                                incomingID= incomingID.split(" ");
                            var incomingIDString = incomingID[incomingID.size()-1];
                            ////console.log("incomingIDString: "+incomingIDString);
                            ////console.log("cerco id degli elementi");

                            for ( k2=0; k2< retrievedShapes.size() ; k2++ ){
                                if(retrievedShapes[k2].toString().includes("Message Flow")){
                                    //console.log("FOUND A MESSAGE FLOW");
                                    continue;
                                }                                                                     
                                            var elementId = retrievedShapes[k2].id.toString();
                                            //console.log("elementId: "+elementId+" incomingIDString: "+incomingIDString);
                                            ////console.log("incoming dell'elemento: "+retrievedShapes[k].incoming+" incomingIDString: "+incomingIDString);
                                            if(elementId == incomingIDString){
                                                retrievedShapes[k2].setProperty("selected", true);
                                                retrievedShapes[k2].setProperty("selectioncolor", color);
                                                coloredElements.push(elementId);
                                                coloredElementsObject.push(retrievedShapes[k2]);
                                            }
                            }
                                
                        }
                    }
                }else{
                    //console.log("c'è una Pool e va gestita");
                    //console.log("result = string.indexOf(Pool): "+result);

                    
                    //SE c'è una Pool devo cercare gli ID dei source e target dei Sequence Flow tra gli elementi della Pool
                            var poolShapes = retrievedShapes[i].getChildShapes();
                            
                                for ( j=0; j< poolShapes.size() ; j++ ){
                                    if(poolShapes[j].toString().includes("Message Flow")){
                                        //console.log("FOUND A MESSAGE FLOW");
                                        continue;
                                    }
                                    
                                    var elementSource = poolShapes[j].resourceId;
                                    var string2 = poolShapes[j].toString();
                                    var result2 = string2.indexOf("Lane");
                     //CASO IN CUI L'ELEMENTO NON E' NELLA LANE               
                                    if (result2 == -1){
                                        try {
                                            //console.log("NON C'E' LANE");
                                               

  //colora gli elementi ai quali i sequence flow sorgenti (incoming SQF) puntano
                                    for ( idIndex3=0; idIndex3< sourceIDs.size() ; idIndex3++ ){
                                        //DEVO PRENDERE I SEQUENCEFLOW DALLA LISTA ELEMENTI A LIVELLO ALTO 
                                        for ( v=0; v< retrievedShapes.size() ; v++ ){
                                            if(retrievedShapes[v].toString().includes("Message Flow")){
                                                //console.log("FOUND A MESSAGE FLOW");
                                                continue;
                                            }
                                            if(retrievedShapes[v].toString().includes("Sequence Flow")){
                                                //console.log("FOUND A SEQUENCE FLOW");
                                                if(retrievedShapes[v].resourceId.toString().includes(sourceIDs[idIndex3].toString())){
                                                    //console.log("COLORA A SEQUENCE FLOW");
                                                    retrievedShapes[v].setProperty("selected", true);
                                                    retrievedShapes[v].setProperty("selectioncolor", color);
                                                }
                                                //continue;
                                            }
                                            
                                        //laneShapes[x].resourceId del SQF non è l'id che è salvato negli altri elementi
                                        //l'id salvato negli altri elementi è laneShapes[x].id
                                        //confrontare quindi con questo id
                                        if(retrievedShapes[v].resourceId.toString().includes(sourceIDs[idIndex3].toString())){

                                            var outgoingID = retrievedShapes[v].outgoing;
                                                outgoingID= outgoingID.toString();
                                                outgoingID= outgoingID.split(" ");
                                            var outgoingIDString = outgoingID[outgoingID.size()-1];
                                            ////console.log("outgoingIDString: "+outgoingIDString);
                                            ////console.log("cerco id degli elementi");
                
                                            //var elementSource = laneShapes[x].resourceId;

                                            for ( k3=0; k3< laneShapes.size() ; k3++ ){
                                                if(laneShapes[k3].toString().includes("Message Flow")){
                                                    //console.log("FOUND A MESSAGE FLOW");
                                                    continue;
                                                }
                                                
                                                var elementId = laneShapes[k3].id.toString();
                                                ////console.log("elementId: "+elementId+" outgoingIDString: "+outgoingIDString);
                                                ////console.log("incoming dell'elemento: "+retrievedShapes[k].incoming+" incomingIDString: "+incomingIDString);
                                                if(elementId == outgoingIDString){
                                                    laneShapes[k3].setProperty("selected", true);
                                                    laneShapes[k3].setProperty("selectioncolor", color);
                                                    coloredElements.push(elementId);
                                                    coloredElementsObject.push(laneShapes[k3]);
                                                }
                                            }
                                        }
                                    }
                
                //colora gli elementi dai quali i sequence flow target (outoing SQF) hanno origine [TODO: UNIRE A CODICE SOPRA]
                                    for ( idIndex4=0; idIndex4< targetIDs.size() ; idIndex4++ ){
                                        //laneShapes[x].resourceId del SQF non è l'id che è salvato negli altri elementi
                                        //l'id salvato negli altri elementi è laneShapes[x].id
                                        //confrontare quindi con questo id
                                        for ( v2=0; v2< retrievedShapes.size() ; v2++ ){
                                            if(retrievedShapes[v].toString().includes("Message Flow")){
                                                //console.log("FOUND A MESSAGE FLOW");
                                                continue;
                                            }
                                            
                                        if(retrievedShapes[v2].resourceId.toString().includes(targetIDs[idIndex4].toString())){

                                            var incomingID = retrievedShapes[v2].incoming;
                                                incomingID= incomingID.toString();
                                                incomingID= incomingID.split(" ");
                                            var incomingIDString = incomingID[incomingID.size()-1];
                                            ////console.log("incomingIDString: "+incomingIDString);
                                            ////console.log("cerco id degli elementi");
                
                                            for ( k4=0; k4< laneShapes.size() ; k4++ ){
                                                if(laneShapes[k].toString().includes("Message Flow"))continue;
                                                            var elementId = laneShapes[k4].id.toString();
                                                            ////console.log("elementId: "+elementId+" incomingIDString: "+incomingIDString);
                                                            ////console.log("incoming dell'elemento: "+retrievedShapes[k].incoming+" incomingIDString: "+incomingIDString);
                                                            if(elementId == incomingIDString){
                                                                laneShapes[k4].setProperty("selected", true);
                                                                laneShapes[k4].setProperty("selectioncolor", color);
                                                                coloredElements.push(elementId);
                                                                coloredElementsObject.push(laneShapes[k4]);
                                                            }
                                            }

                                        }  
                                        }
                                    }
                                                            }

                            } catch (error) {
                                //console.log(error.message);
                            }
 //CASO IN CUI NON C'E' NESSUNA LANE
                             }else{


                                //console.log("C'E' LANE");
                                var laneShapes = poolShapes[j].getChildShapes();
                          
//SCORRO elementi Lane

                                    for ( idIndex5=0; idIndex5< sourceIDs.size() ; idIndex5++ ){
                                        //console.log("idIndex5: "+idIndex5);
                                        //DEVO PRENDERE I SEQUENCEFLOW DALLA LISTA ELEMENTI A LIVELLO ALTO 
                                        ////console.log("retrievedShapes.size(): "+retrievedShapes.size());
                                        for ( v3=0; v3< retrievedShapes.size() ; v3++ ){ 
                                            //console.log("retrievedShapes v3: "+v3);      
                                            ////console.log("retrievedShapes[v].toString(): "+retrievedShapes[v].toString());   
                                                                            
                                            if(retrievedShapes[v3].toString().includes("Message Flow")){
                                                //console.log("FOUND A MESSAGE FLOW");
                                                continue;
                                            }
                                            if(retrievedShapes[v3].toString().includes("Sequence Flow")){
                                                //console.log("FOUND A SEQUENCE FLOW");
                                                if(retrievedShapes[v3].resourceId.toString().includes(sourceIDs[idIndex5].toString())){
                                                    //console.log("COLORA A SEQUENCE FLOW");
                                                    retrievedShapes[v3].setProperty("selected", true);
                                                    retrievedShapes[v3].setProperty("selectioncolor", color);
                                                }
                                                //continue;
                                            }
                                            
                                        //laneShapes[x].resourceId del SQF non è l'id che è salvato negli altri elementi
                                        //l'id salvato negli altri elementi è laneShapes[x].id
                                        //confrontare quindi con questo id
                                        if(retrievedShapes[v3].resourceId.toString().includes(sourceIDs[idIndex5].toString())){

                                            var outgoingID = retrievedShapes[v3].outgoing;
                                                outgoingID= outgoingID.toString();
                                                outgoingID= outgoingID.split(" ");
                                            var outgoingIDString = outgoingID[outgoingID.size()-1];
                                            ////console.log("outgoingIDString: "+outgoingIDString);
                                            ////console.log("cerco id degli elementi");
                
                                            //var elementSource = laneShapes[x].resourceId;

                                            for ( k5=0; k5< laneShapes.size() ; k5++ ){
                                                //console.log("laneShapes k5: "+k5);
                                                if(laneShapes[k5].toString().includes("Message Flow")){
                                                    //console.log("FOUND A MESSAGE FLOW");
                                                    continue;
                                                }
                                               
                                                var elementId = laneShapes[k5].id.toString();
                                                ////console.log("elementId: "+elementId+" outgoingIDString: "+outgoingIDString);
                                                ////console.log("incoming dell'elemento: "+retrievedShapes[k].incoming+" incomingIDString: "+incomingIDString);
                                                if(elementId == outgoingIDString){
                                                    laneShapes[k5].setProperty("selected", true);
                                                    laneShapes[k5].setProperty("selectioncolor", color);
                                                    coloredElements.push(elementId);
                                                    coloredElementsObject.push(laneShapes[k5]);
                                                }
                                            }
                                        }
                                    }
                
                //colora gli elementi dai quali i sequence flow target (outoing SQF) hanno origine [TODO: UNIRE A CODICE SOPRA]
                                    for ( idIndex6=0; idIndex6< targetIDs.size() ; idIndex6++ ){
                                        //console.log("OUTGOING: ");
                                        //console.log("idIndex6: "+idIndex6);
                                        //laneShapes[x].resourceId del SQF non è l'id che è salvato negli altri elementi
                                        //l'id salvato negli altri elementi è laneShapes[x].id
                                        //confrontare quindi con questo id
                                        for ( v4=0; v4< retrievedShapes.size() ; v4++ ){
                                            //console.log("retrievedShapes V4: "+v4);
                                            if(retrievedShapes[v4].toString().includes("Message Flow")){
                                                //console.log("FOUND A MESSAGE FLOW");
                                                continue;
                                            }
                                            
                                        if(retrievedShapes[v4].resourceId.toString().includes(targetIDs[idIndex6].toString())){

                                            var incomingID = retrievedShapes[v4].incoming;
                                                incomingID= incomingID.toString();
                                                incomingID= incomingID.split(" ");
                                            var incomingIDString = incomingID[incomingID.size()-1];
                                            ////console.log("incomingIDString: "+incomingIDString);
                                            ////console.log("cerco id degli elementi");
                
                                            for ( k6=0; k6< laneShapes.size() ; k6++ ){
                                                //console.log("laneShapes k6: "+k6);
                                                if(laneShapes[k6].toString().includes("Message Flow")){
                                                    //console.log("FOUND A MESSAGE FLOW");
                                                    continue;
                                                }
                                               
                                                            var elementId = laneShapes[k6].id.toString();
                                                            ////console.log("elementId: "+elementId+" incomingIDString: "+incomingIDString);
                                                            ////console.log("incoming dell'elemento: "+retrievedShapes[k].incoming+" incomingIDString: "+incomingIDString);
                                                            if(elementId == incomingIDString){
                                                                laneShapes[k6].setProperty("selected", true);
                                                                laneShapes[k6].setProperty("selectioncolor", color);
                                                                coloredElements.push(elementId);
                                                                coloredElementsObject.push(laneShapes[k6]);
                                                            }
                                            }
                                                

                                        }  
                                        }
                                    }
                                                            }
                                                    }//else caso in cui c'è una Lane
//SE C'è LANE DEVO CONTROLLARE GLI ELEMENTI DENTRO LANE                             
                           

                    }

                }
 
                 }//for che scorre elementi del modello
                 

                 //For che scorre i sequence flow; se un sequence flow ha come sorgente e target un elemento colorato allora lo coloro.
                 for ( i=0; i< retrievedShapes.size() ; i++ ){
                    if(retrievedShapes[i].toString().includes("Sequence Flow")){
                        try {
                                               
                        var resourceIdSQF = retrievedShapes[i].resourceId.toString();
                        var outgoingIdSQF0 = retrievedShapes[i].outgoing.toString();
                        var outgoingIdSQFArray = outgoingIdSQF0.split(" ");
                        //console.log("outgoingIdSQFArray: "+outgoingIdSQFArray);
                        var outgoingIdSQF = outgoingIdSQFArray[outgoingIdSQFArray.size()-1];

                        var idSQF = retrievedShapes[i].id.toString();
                        //console.log("idSQF"+idSQF+"resourceIdSQF: "+resourceIdSQF+" outgoingIdSQF: "+outgoingIdSQF);
                        //SE TARGET è COLORATO
                        if(coloredElements.indexOf(outgoingIdSQF) > -1){
                            //CONTROLLO SE IL SOURCE è COLORATO E PER FARLO DEVO 
                            //SCORRERE I COLORED ELEMENT E VEDERE SE UNO DEGLI OUTGOING è L'ID DEL SEQUENCEFLOW
                            
                        
                        for ( j=0; j< coloredElementsObject.size() ; j++ ){
                            
                            if(coloredElementsObject[j].outgoing.toString().includes(idSQF)){
                                //TROVATO SOURCE
                                
                                //console.log("SOURCE IS COLORED");
                                if(coloredElementsObject[j].outgoing.toString().includes("Sequence Flow")){
                                    
                                    //COLORO IL SEQUENCE FLOW
                                    retrievedShapes[i].setProperty("selected", true);
                                    retrievedShapes[i].setProperty("selectioncolor", color);
                                }

                                      
                            }
                            
                        }
                        }
                    } catch (error) {
                            
                    }
                    }
                
                }


                 //console.log("facade.getCanvas().update()");
                 facade.getCanvas().update();
                 facade.updateSelection();
             }
             catch (err) {
             	alert("Unable to select all the elements: " + err.message);
             }
         }.bind(this),
        






    getDiagramType: function () {
        switch (this.facade.getCanvas().getStencil().namespace()) {
            case "http://b3mn.org/stencilset/bpmn1.1#":
                return("xpdl");
            case "http://b3mn.org/stencilset/bpmn2.0#":
                return("bpmn");
            case "http://b3mn.org/stencilset/epc#":
                return("epc");
            case "http://b3mn.org/stencilset/yawl2.2#":
                return("yawl");
            default:
                return("");
        }
    },

    showErrors: function (errors) {
        if (errors.size() == 0) {
            Ext.Msg.show({
                title: "Error",
                msg: "An error occured while measuring your process.",
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.ERROR
            }).getDialog().syncSize()
        } else {
            Ext.Msg.show({
                title: "Error",
                msg: "It was not possible to measure the process because of some unsupported content.",
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.ERROR
            }).getDialog().syncSize();
            var a = new Ext.ux.grid.ErrorGridPanel(errors, this.facade);
            var b = new Ext.Window({
                resizable: true,
                closeable: true,
                minimizable: false,
                width: 800,
                minHeight: 590,
                height: 590,
                x: 0,
                //layout: 'fit',
                //scrollable: 'vertical',
                autoScroll: 'true',
                bodyStyle: "background-color:white; color: black; overflow: visible;",
                title: "Errors that occurred",
                items: a,

            });
            b.show()
        }
    }
});
