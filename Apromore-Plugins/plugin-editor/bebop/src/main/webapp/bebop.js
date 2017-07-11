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

ORYX.Plugins.Bebop = ORYX.Plugins.AbstractPlugin.extend({

    facade: undefined,

    construct: function (facade) {

        arguments.callee.$.construct.apply(this, arguments);

        this.contextPath = "/bebop";
        this.servletPath = "/bebop/servlet";

        this.facade = facade;
        this.facade.offer({
            'name': 'Bebop Calculator',
            'functionality': this.checkGuidelines.bind(this, false),
            'group': 'bpmn-toolkit',
            'icon': this.contextPath +  "/images/bebop.png",
            'description': 'Check model guidelines with BEBoP',
            'index': 1
        });

        	// The color of the highlighting
        	this.color = "#DC143C";
    },


//Editor function for changing colors

    // Deselect all process model elements.
    resetSelection: function(){
	try {
		this.facade.getCanvas().getChildShapes().each(function (shape) {
			shape.setProperty("selected", false);
			shape.node.removeAttributeNS(null, "style");
		}.bind(this));
		this.facade.getCanvas().update();
	}
	catch (err) {
		alert("Unable to clear selection: " + err.message);
	}
    },



    checkGuidelines: function () {
        var json = this.facade.getSerializedJSON();
        if (this.facade.getCanvas().nodes.size() == 0) {
            Ext.Msg.show({
                title: "Info",
                msg: "There is nothing to measure.",
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.INFO
            }).getDialog().syncSize();
            return;
        }

        if (this.getDiagramType() != 'bpmn') {
                    Ext.Msg.show({
                        title: "Info",
                        msg: "The process must be a BPMN model, current model is " + this.getDiagramType(),
                        buttons: Ext.Msg.OK,
                        icon: Ext.Msg.INFO
                    }).getDialog().syncSize();
                    return;
        }

        var msg = Ext.Msg.wait("Waiting for BEBoP servlet to process model.");


        new Ajax.Request(this.servletPath, {
            parameters: {'data': json, 'type': this.getDiagramType()},
            method: 'POST',
            asynchronous: true,

            onSuccess: function (data) {
                msg.hide();
                var responseJson = data.responseText.evalJSON(true);
                if (responseJson != null) {
                    if (responseJson.hasOwnProperty("errors")) {
                        this.showErrors(responseJson.errors);
                    } else {
                        //grid.resetElementColor();
                        this.showGuidelines(responseJson);
                    }
                }
            }.bind(this),

            onFailure: function (data) {
                msg.hide();
                Ext.Msg.show({
                    title: "Error",
                    msg: "The communication with the bebop servlet failed.",
                    buttons: Ext.Msg.OK,
                    icon: Ext.Msg.ERROR
                }).getDialog().syncSize()
            }.bind(this)
         })
    },

    showGuidelines: function (responseJson) {

     //console.log("ResponseJson: "+responseJson);
    // The color of the highlighting
        var color = "#DC143C";


        var guidelines = [];
        var data = [];

        // shared reader
        var myReader = new Ext.data.JsonReader({}, [
           {name: 'GuidelineName', type:'string'},
           {name: 'GuidelineDescription', type:'string'},
           {name: 'NodeID', type:'string'},
           {name: 'NodeLabel', type:'string'},
           {name: 'NodeType', type:'string'},
//           {name: 'NodeJsonID', type:'string'}
        ]);

    var jsonStore = new Ext.data.JsonStore({
//                  fields: ['GuidelineName','GuidelineDescription','NodeID','NodeLabel','NodeType','NodeJsonID'],
                  fields: ['GuidelineName','GuidelineDescription','NodeID','NodeLabel','NodeType'],
                  reader: myReader,
                  root: 'Guidelines',
                  data: responseJson
     });

    var groupStore = new Ext.data.GroupingStore({
         reader: jsonStore.reader,
    	 data : jsonStore.reader.jsonData,
         sortInfo:{field: 'NodeID', direction: "ASC"},
         groupField:'GuidelineName'
    });

    this.store = groupStore;



//Additional Panel for text Area
     var textAreaPanel = new Ext.FormPanel({
                            resizable: false,
                           	height: 350,
                           	border : false,
                           	renderTo: document.body,
                           	color: '#DC143C',
                           	items: [{
                            id: 'id_textAreaPanel',
                           	html: 'Click on a row to read about which guideline has been violated.'

                           	}]
                           });

     console.log("textAreaPanel"+textAreaPanel);


    // create the Grid
        var grid = new Ext.grid.GridPanel({


        store : groupStore,

        columns: [

                {header: 'BPMNElementLabel', width: 75, sortable: true, renderer: 'NodeLabel', dataIndex: 'NodeLabel'},
                 {header: 'BPMNElementType', width: 75, sortable: true, renderer: 'NodeType', dataIndex: 'NodeType'},
                 {id:'NodeID',header: 'BPMNElementID', width: 75, sortable: true, dataIndex: 'NodeID'},
                // {id:'NodeJsonID',header: 'BPMNElementID', width: 75, sortable: true, dataIndex: 'NodeJsonID'},
                {header: 'GuidelineName', width: 75, sortable: true, renderer: 'GuidelineName', dataIndex: 'GuidelineName'},
                {header: 'GuidelineDescription', width: 75, sortable: true, renderer: 'GuidelineDescription', dataIndex: 'GuidelineDescription'},
         ],

                 view: new Ext.grid.GroupingView({
                     forceFit:true,
                     groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
                 }),

        stripeRows: true,
        autoExpandColumn: 'Node',
        height: 350,
        width: 600,


        // Deselect all process model elements.
        elementColor: function(nId){
             try {

                var retrievedShapes = this.facade.getCanvas().getChildShapes();
                for ( i=0; i< retrievedShapes.size() ; i++ ){

                    //DEBUG
                    //console.log("retrievedShapes.size(): "+retrievedShapes.size());
                    //console.log("retrievedShapes.nodeId: "+retrievedShapes[i].nodeId);
                    //console.log("retrievedShapes.resourceId: "+retrievedShapes[i].resourceId);

                    var string = retrievedShapes[i].toString();
                    var result = string.indexOf("Pool");

                    //DEBUG
                    //console.log("result = string.indexOf(Pool): "+result);

                    if (result != -1){
                          //DEBUG
//                        console.log("We have a Pool");

                           if(nId == retrievedShapes[i].resourceId){
                                 retrievedShapes[i].setProperty("selected", true);
                                 retrievedShapes[i].setProperty("selectioncolor", color);
                                 retrievedShapes[i].setProperty("bordercolor", color);
                                 break;
                           }

                            var poolShapes = retrievedShapes[i].getChildShapes();
                            //DEBUG
                            //console.log("poolShapes: "+poolShapes.size());

                             for ( j=0; j< poolShapes.size() ; j++ ){
                             //DEBUG
                                //console.log("poolShapesID: "+poolShapes[j]);
                                //console.log("poolShapesID.nodeId: "+poolShapes[j].nodeId);
                                //console.log("poolShapesID.resourceId: "+poolShapes[j].resourceId);
                                 var string2 = poolShapes[j].toString();
                                 var result2 = string2.indexOf("Lane");
                                 if(nId == retrievedShapes[i].resourceId){
                                            poolShapes[j].setProperty("selected", true);
                                            poolShapes[j].setProperty("selectioncolor", color);
                                            break;
                                 }

                                  if (result2 != -1){
                                  //DEBUG
                                  //console.log("We have a Lane");

                                    if(nId == poolShapes[j].resourceId){
                                         poolShapes[j].setProperty("selected", true);
                                         poolShapes[j].setProperty("selectioncolor", color);
                                         poolShapes[j].setProperty("bordercolor", color);
                                         //DEBUG
                                         //console.log("poolShapes[j].setProperty(selected, true): "+poolShapes[j]);
                                         break;
                                    }

                                    var laneShapes = poolShapes[j].getChildShapes();
                                    //DEBUG
                                    //console.log("laneShapes: "+laneShapes.size());

                                      for ( l=0; l< laneShapes.size() ; l++ ){
                                                //DEBUG
//                                             //console.log("laneShapesID: "+laneShapes[l]);
//                                             //console.log("laneShapesID.nodeId: "+laneShapes[l].nodeId);
//                                             //console.log("laneShapesID.resourceId: "+laneShapes[l].resourceId);

                                                if(nId == laneShapes[l].resourceId){
                                                  laneShapes[l].setProperty("selected", true);
                                                  laneShapes[l].setProperty("selectioncolor", color);
                                                  //DEBUG
                                                  //console.log("laneShapes[j].setProperty(selected, true)");
                                                  break;
                                                  }
                                      }

                                  }
                                   if(nId == poolShapes[j].resourceId){
                                  poolShapes[j].setProperty("selected", true);
                                  poolShapes[j].setProperty("selectioncolor", color);
                                  poolShapes[j].setProperty("bordercolor", color);
                                  break;
                                  }

                             }
                             //DEBUG
                             // console.log("poolShapes[i]: "+retrievedShapes[i].getChildShapes());

                    }
                    if(nId == retrievedShapes[i].resourceId){
                                             retrievedShapes[i].setProperty("selected", true);
                                             retrievedShapes[i].setProperty("selectioncolor", color);
                                            break;
                                          }
             	}


             	this.facade.getCanvas().update();
             }
             catch (err) {
             	alert("Unable to select all the elements: " + err.message);
             }
         }.bind(this),

         updateText : function( testoGuideline  ){
            //DEBUG
            //console.log("updateText textAreaPanel: "+textAreaPanel);
            //console.log("updateText this.textAreaPanel.items.length: "+textAreaPanel.items.length);
            //console.log("updateText this.textAreaPanel.items.items.length: "+textAreaPanel.items.items.length);
            //console.log("updateText this.textAreaPanel.items[0]): "+textAreaPanel.items[0]);
            //console.log("updateText this.textAreaPanel.items.items[0]: "+textAreaPanel.items.items[0]);

            textAreaPanel.items.items[0]=testoGuideline;
            //console.log("updateText this.textAreaPanel.items.items[0]: "+textAreaPanel.items.items[0]);

            this.textAreaPanel.update('<iframe></iframe>');

         }.bind(this),


         //Reset color
                 // Deselect all process model elements.
                 resetElementColor: function(){
                      try {

                         var retrievedShapes = this.facade.getCanvas().getChildShapes();
                         for ( i=0; i< retrievedShapes.size() ; i++ ){
                               //DEBUG
                               //console.log("retrievedShapes.nodeId: "+retrievedShapes[i].nodeId);
                               //console.log("retrievedShapes.resourceId: "+retrievedShapes[i].resourceId);

                             var string = retrievedShapes[i].toString();
                              //DEBUG
                              //console.log("retrievedShapes[i].toString(): "+string);

                             var result = string.indexOf("Pool");
                              //DEBUG
                              //console.log("result = string.indexOf(Pool): "+result);

                             if (result != -1){
                                //DEBUG
                                //console.log("We have a Pool");
                                 retrievedShapes[i].setProperty("selected", false);
                                 retrievedShapes[i].setProperty("selectioncolor", false);
                                 retrievedShapes[i].setProperty("bordercolor", false);
                                  var poolShapes = retrievedShapes[i].getChildShapes();
                                  //DEBUG
                                  //console.log("poolShapes: "+poolShapes.size());

                                      for ( j=0; j< poolShapes.size() ; j++ ){
                                      //DEBUG
                                      //console.log("poolShapesID: "+poolShapes[j]);
                                      //console.log("poolShapesID.nodeId: "+poolShapes[j].nodeId);
                                      //console.log("poolShapesID.resourceId: "+poolShapes[j].resourceId);
                                          var string2 = poolShapes[j].toString();
                                          var result2 = string2.indexOf("Lane");
                                           if (result2 != -1){
                                           //DEBUG
                                           //console.log("We have a Lane");

                                            poolShapes[j].setProperty("selected", false);

                                            poolShapes[j].setProperty("selectioncolor", false);
                                            poolShapes[j].setProperty("bordercolor", false);

                                            //DEBUG
                                            //console.log("poolShapes[j].setProperty(selected, true): "+poolShapes[j]);


                                               var laneShapes = poolShapes[j].getChildShapes();
                                               //DEBUG
                                               //console.log("laneShapes: "+laneShapes.size());

                                               for ( l=0; l< laneShapes.size() ; l++ ){
                                                        //DEBUG
         //                                             //console.log("laneShapesID: "+laneShapes[l]);
//                                                      //console.log("laneShapesID.nodeId: "+laneShapes[l].nodeId);
//                                                      //console.log("laneShapesID.resourceId: "+laneShapes[l].resourceId);

                                                           laneShapes[l].setProperty("selected", false);

                                                            laneShapes[l].setProperty("selectioncolor", false);
                                                            laneShapes[l].setProperty("bordercolor", false);
                                                        //DEBUG
                                                        //console.log("laneShapes[j].setProperty(selected, true)");
                                               }
                                           }

                                             poolShapes[j].setProperty("selected", false);
                                             poolShapes[j].setProperty("selectioncolor", false);
                                             poolShapes[j].setProperty("bordercolor", false);
                                      }
                                      //DEBUG
                                     // console.log("poolShapes[i]: "+retrievedShapes[i].getChildShapes());

                             }else{
                                //We have no Pool

                                if(string.indexOf("Gateway")){
                                                                 retrievedShapes[i].setProperty("selected", false);
                                                                 retrievedShapes[i].setProperty("selectioncolor", false);
                                }else{

                                    if(!string.indexOf("Flow")){
                                     retrievedShapes[i].setProperty("selected", false);
                                     retrievedShapes[i].setProperty("selectioncolor", false);
                                     retrievedShapes[i].setProperty("bordercolor", false);
                                    }//else is a Flow then continue
                                }
                             }

                      	}

                      	this.facade.getCanvas().update();
                      }
                      catch (err) {
                      	alert("Unable to select all the elements: " + err.message);
                      }
                  }.bind(this),




        listeners: {

            rowclick: function(grid, rowIndex, columnIndex, e) {

                var record = grid.getStore().getAt(rowIndex);
                var gDesc = record.get('GuidelineDescription');
                var gName = record.get('GuidelineName');
                //DEBUG
                //Ext.Msg.alert('Violated Guideline', gName+"<br /><br />"+gDesc);
                //console.log(record);
                var nId = record.get('NodeID');
                //DEBUG
                //console.log("nId: "+nId);
                //this.elementColor2(nId);
                this.resetElementColor();
                this.elementColor(nId);
                //DEBUG
                //console.log("this.textAreaPanel"+this.textAreaPanel);
                var newText = gName+"<br /><br />"+gDesc;
                Ext.getCmp('id_textAreaPanel').body.update(newText);

                }
        },

        // config options for stateful behavior
        stateful: true,
        stateId: 'grid'
    });


//To hide columns
grid.getColumnModel().setHidden(3, true);
grid.getColumnModel().setHidden(4, true);
//grid.getColumnModel().setHidden(5, true);

//DEBUG
//console.log("jsonStore: "+jsonStore);
//console.log("groupStore: "+groupStore);
//console.log("grid.store: "+grid.store);
//DEBUG
//console.log("After grid definition");

        var formWindow = new Ext.Window({
            resizable: false,
            closeable: true,
            minimizable: false,
            width: 800,
            minHeight: 250,
            height: 400,

                      xtype: 'container',
                      layout: 'border',
                      items: [{
                          xtype: 'panel',
                          region: 'east',
                          title: 'Guideline Description',
                          width: '200',
                          items:[textAreaPanel]

                      }
                      ,{
                          xtype: 'panel',
                          region: 'center',
                          title: 'Violated Understandability Modeling Guidelines',
                          items:[grid]
                      }
                      ],

            autoScroll: 'true',
            bodyStyle: "background-color:white; color: black; overflow: visible;",
            title: "Bebop",

              listeners:{
                 'close':function(win){
                          //this.items.items[1].items.items[0].resetElementColor();
                          grid.resetElementColor();
                          console.info('bye');
                  }
              },
            buttons: [
                {
                    text: "OK",
                    handler: function () {
                        //this.items.items[1].items.items[0].resetElementColor();
                        grid.resetElementColor();
                        this.ownerCt.close()
                    }
                }
            ]
        });
        formWindow.show()
        console.log("After windows.show()");
    },




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
                minHeight: 250,
                height: 400,
                x: 0,
                //layout: 'fit',
                //scrollable: 'vertical',
                autoScroll: 'true',
                bodyStyle: "background-color:white; color: black; overflow: visible;",
                title: "Errors that occurred",
                items: a,
                buttons: [
                    {
                        text: "OK",
                        handler: function () {
                            this.ownerCt.close()
                        }
                    }
                ]
            });
            b.show()
        }
    }
});

