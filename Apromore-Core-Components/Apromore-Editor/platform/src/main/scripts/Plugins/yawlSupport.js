/**
 * Copyright (c) 2012
 * Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
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
 *
 * See: http://www.opensource.org/licenses/mit-license.php
 **/

if (!ORYX.Plugins)
    ORYX.Plugins = new Object();

/**
 *  Location of Import and Export Servlet
 *  This was located in config.js in Oryx
 **/
ORYX.CONFIG.YAWLIMPORTURL = ORYX.CONFIG.ROOT_PATH + "yawlimport";
ORYX.CONFIG.YAWLEXPORTURL = ORYX.CONFIG.ROOT_PATH + "yawlexport";

/**
 * The YAWlSupport plugin provides import, export and utility functionality
 * for modeling YAWL workflows.
 *
 * @class ORYX.Plugins.YAWlSupport
 * @extends ORYX.Plugins.AbstractPlugin
 */
ORYX.Plugins.YAWLSupport = ORYX.Plugins.AbstractPlugin.extend({
    /** @lends ORYX.Plugins.YAWLSupport.prototype */

    /**
     * Plugin Constructor initializing all kind of things
     *
     * @constructor
     */
    construct:function () {

        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);

        // Progress dialog on export and import
        this.progressDialog = null;

        // Stub of YAWL Net in JSON
        this._dummyRootnetData = '{"resourceId":"canvas","properties":{"yawlid":"","name":"","documentation":"","title":"","creators":"","subject":"","description":"","contributor":"","coverage":"","validfrom":"","validto":"","created":"","version":"","status":"","persistent":"","uri":"","datatypedefinitions":"","bgcolor":"#FFFFFF","isrootnet":true,"externaldatagateway":"","decompositionid":"","decompositionname":"","decompositionexternalinteraction":"","decompositioncodelet":"","decompositionvariables":"","decompositionlogpredicate":""},"stencil":{"id":"Diagram"},"childShapes":[{"resourceId":"sid-A72E7647-D0AA-41BE-86FB-CB771A630869","properties":{"yawlid":"","name":"","documentation":""},"stencil":{"id":"InputCondition"},"childShapes":[],"outgoing":[],"bounds":{"lowerRight":{"x":107,"y":213},"upperLeft":{"x":75,"y":181}},"dockers":[]},{"resourceId":"sid-0B3EA74F-3F63-45DC-8F15-DF8D8E6A3718","properties":{"yawlid":"","name":"","documentation":""},"stencil":{"id":"OutputCondition"},"childShapes":[],"outgoing":[],"bounds":{"lowerRight":{"x":797,"y":213},"upperLeft":{"x":765,"y":181}},"dockers":[]}],"bounds":{"lowerRight":{"x":1485,"y":1050},"upperLeft":{"x":0,"y":0}},"stencilset":{"url":"/Apromore-editor/editor/stencilsets//yawl/yawl.json","namespace":"http://b3mn.org/stencilset/yawl2.2#"},"ssextensions":[]}';

        // Stub of YAWL Subnet in JSON
        this._dummySubnetData = '{"resourceId":"canvas","properties":{"yawlid":"","name":"","documentation":"","title":"","creators":"","subject":"","description":"","contributor":"","coverage":"","validfrom":"","validto":"","created":"","version":"","status":"","persistent":"","uri":"","datatypedefinitions":"","bgcolor":"#FFFFFF","isrootnet":false,"externaldatagateway":"","decompositionid":"","decompositionname":"","decompositionexternalinteraction":"","decompositioncodelet":"","decompositionvariables":"","decompositionlogpredicate":""},"stencil":{"id":"Diagram"},"childShapes":[{"resourceId":"sid-A72E7647-D0AA-41BE-86FB-CB771A630869","properties":{"yawlid":"","name":"","documentation":""},"stencil":{"id":"InputCondition"},"childShapes":[],"outgoing":[],"bounds":{"lowerRight":{"x":107,"y":213},"upperLeft":{"x":75,"y":181}},"dockers":[]},{"resourceId":"sid-0B3EA74F-3F63-45DC-8F15-DF8D8E6A3718","properties":{"yawlid":"","name":"","documentation":""},"stencil":{"id":"OutputCondition"},"childShapes":[],"outgoing":[],"bounds":{"lowerRight":{"x":797,"y":213},"upperLeft":{"x":765,"y":181}},"dockers":[]}],"bounds":{"lowerRight":{"x":1485,"y":1050},"upperLeft":{"x":0,"y":0}},"stencilset":{"url":"/Apromore-editor/editor/stencilsets//yawl/yawl.json","namespace":"http://b3mn.org/stencilset/yawl2.2#"},"ssextensions":[]}';

        // Stub of YAWL Net SVG representation
        this._dummySVG = '<svg/>';

        // Stub Parameters for saving an empty YAWL Subnet
        this._dummyParameters = {
            json_xml:this._dummySubnetData,
            svg_xml:this._dummySVG,
            name:"",
            description:"",
            parent:"/directory/root-directory",
            namespace:"http://b3mn.org/stencilset/yawl2.2#",
            type:"YAWL 2.2",
            comment:"",
            glossary_xml:"[]",
            views:"[]"
        };

        // Import from .yawl
        this.facade.offer({
            'name':ORYX.I18N.YAWL.importName,
            'description':ORYX.I18N.YAWL.importDesc,
            dropDownGroupIcon:ORYX.PATH + "images/import.png",
            'icon':ORYX.PATH + "images/yawl/yawl_import.png",
            'functionality':this.doImport.bind(this),
            'group':ORYX.I18N.YAWL.importGroup,
            'isEnabled':function () {
                return true
            }.bind(this),
            'index':1
        });

        // Export to .yawl
        this.facade.offer({
            'name':ORYX.I18N.YAWL.exportName,
            'description':ORYX.I18N.YAWL.exportDesc,
            dropDownGroupIcon:ORYX.PATH + "images/export2.png",
            'icon':ORYX.PATH + "images/yawl/yawl_export.png",
            'functionality':this.doExport.bind(this),
            'group':ORYX.I18N.YAWL.exportGroup,
            'isEnabled':function () {
                return true
            }.bind(this),
            'index':2
        });

        // The Shape for which the Cancelation Set is currently shown
        this._showingCancelationSetFor = null;

        // Highlight elements in the Cancelation Set of a element
        this.facade.offer({
            'name':ORYX.I18N.YAWL.cancelationSetName,
            'description':ORYX.I18N.YAWL.cancelationSetDesc,
            dropDownGroupIcon:ORYX.PATH + "images/yawl/yawl.png",
            'icon':ORYX.PATH + "images/magnifier_zoom_out.png",
            'functionality':this.doHighlightCancelationSet.bind(this),
            'group':ORYX.I18N.YAWL.yawlGroup,
            'index':1,
            'toggle':true,
            'minShape':1,
            'maxShape':1
        });

        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.initialiseDiagram.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPERTY_CHANGED, this.propertyChanged.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, this.onSelectElement.bind(this));
        //this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPERTY_CHANGED, this.handlePropertiesDecorator.bind(this));
        //this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SHAPEADDED, this.resetMagnetAdded.bind(this));

    },

    /**************** Utility functions for use in all parts *************/

    /**
     * Return the YAWL ID of the Net
     *
     * @private
     * @param diagram
     * @return YAWL ID
     */
    _extractNetID:function (diagram) {
        return this._extractNet(diagram).properties.yawlid;
    },

    /**
     * Return the YAWL ID of the Net
     *
     * @private
     * @param diagram
     * @return YAWL ID
     */
    _extractNetName:function (diagram) {
        return this._extractNet(diagram).properties.name;
    },

    /**
     * Return the YAWL Net of a Diagram
     *
     * @private
     * @param diagram
     * @return YAWL Net
     */
    _extractNet:function (diagram) {

        /*
         netShape = diagram.childShapes.find(function(shape) {
         return shape.stencil.id === "Net"
         });
         */

        // There is no more Net shape, properties are shared with Diagram
        return diagram;
    },

    _getShapeSVGElement:function (shape, elementId) {
        // Oryx uses the node id
        var globalElementId = shape.node.id + elementId;
        var element = shape.node.ownerDocument.getElementById(globalElementId);
        if (!element) {
            // Signavio uses the shape id
            globalElementId = shape.id + elementId;
            element = shape.node.ownerDocument.getElementById(globalElementId);
        }
        return element;
    },

    /**************** Import ******************/

    /**
     * Show the import dialog providing an AJAX upload for .yawl files, that
     * are shown in the editor after conversion.
     */
    doImport:function () {
        this._showImportDialog();
    },


    /**
     * Show the only net (i.e. root net)
     *
     * @param importObject containing only one net
     */
    _showRootNet:function (importObject) {
        this.facade.importJSON(importObject.diagrams[0]);
    },


    /**
     * Return the title of a diagram
     *
     * @private
     * @param diagram
     * @param isRootNet
     * @return diagram title
     */
    _buildDiagramTitle:function (diagram, isRootNet) {
        if (isRootNet) {
            // Title of Specification if available, else URI
            return (diagram.properties.spectitle) ? diagram.properties.spectitle : diagram.properties.specuri;
        } else {
            var netName = this._extractNetName(diagram);
            return (netName) ? netName : this._extractNetID(diagram);
        }
    },

    /**
     * Starts a three steps process:<br />
     * 1. Create a empty Diagram for each YAWL (Sub)net<br />
     * 2. Insert the URL of subnet to each composite task<br />
     * 3. Save all Diagrams with correct references to each other<br />
     *
     * @private
     * @param importObject
     */
    _prepareAndImportNets:function (importObject) {

        var storedSubnets = [];
        var abortPrepareSubnets = false;
        var ajaxRequestCounter = importObject.diagrams.length;

        Ext.each(importObject.diagrams, function (diagram, index) {

            var netYawlId = this._extractNetID(diagram);

            // Shallow Copy Parameters
            var dummyParams = Object.clone(this._dummyParameters);
            dummyParams.name = this._buildDiagramTitle(diagram, (importObject.rootNetName === netYawlId));
            dummyParams.description = ((diagram.properties.specdescription) ? diagram.properties.specdescription : "");
            dummyParams.json_xml = this._dummyRootnetData;

            var nextRequestCount = ajaxRequestCounter - 1;

            // First get an identifier (URL) for each subnet from the backend
            new Ajax.Request(ORYX.CONFIG.SERVER_MODEL_HANDLER, {
                parameters:dummyParams,
                asynchronous:false, //WORKAROUND for Oryx synchronization bug regarding IDs
                method:'POST',
                onSuccess:function (request) {

                    var response = Ext.decode(request.responseText);
                    var modelId = this._extractModelId(response);

                    storedSubnets.push({
                        yawlid:netYawlId,
                        data:diagram,
                        signavioid:modelId,
                        isRoot:(importObject.rootNetName === netYawlId)
                    });

                    ajaxRequestCounter--;
                    this.progressDialog.updateProgress(0.4, ORYX.I18N.YAWL.messageGetIDofNet + netYawlId);

                    if (ajaxRequestCounter == 0) {
                        // If every subnet is saved.
                        // Then insert the URL to "decomposeToLink" on each compositeTask
                        this._insertSubnetIdentifiers(storedSubnets);
                        // Now save the "real" subnet diagrams to the backend
                        this._saveSubnets(storedSubnets);
                    }

                }.bind(this),
                onFailure:function () {
                    // Do not go on with processing
                    abortPrepareSubnets = true;
                    this.progressDialog.hide();
                    Ext.Msg.alert(ORYX.I18N.YAWL.importFailed);
                }.bind(this)
            });

            // If any AJAX request fails, there is no need to execute others
            return !abortPrepareSubnets;
        }.bind(this));
    },


    /**
     * Get the ID of a model
     *
     * @private
     * @param model the Signavio model object
     * @return ID of model
     */
    _extractModelId:function (modelObject) {
        // Remove the Prefix "/model/"
        return modelObject.href.substr(7);
    },


    /**
     * Generates a relative link to another model stored in Signavio
     *
     * @private
     * @param modelId the ID of the model
     * @return relative URL of model in Signavio
     */
    _buildDecomposeToLink:function (modelId) {
        return ORYX.CONFIG.SERVER_EDITOR_HANDLER + '?id=' + modelId;
    },

    /**
     * Extracts the model Id of a the URL generated by the method above
     *
     * @private
     * @param modelUrl the model URL (e.g. "../p/editor?id=...")
     * @return just the model Id
     */
    _extractModelIdFromLink:function (modelUrl) {
        return modelUrl.substr(ORYX.CONFIG.SERVER_EDITOR_HANDLER.length + 4);
    },

    /**
     * Insert the URL of subnet to each composite task
     *
     * @private
     * @param storedSubnets array containing all subnets
     */
    _insertSubnetIdentifiers:function (storedSubnets) {

        this.progressDialog.updateProgress(0.5, ORYX.I18N.YAWL.messageInsertSubnetUrls);

        Ext.each(storedSubnets, function (subnet, index) {
            Ext.each(storedSubnets, function (otherSubnet, index) {
                otherSubnet.data.childShapes.each(function (shapeAsJSON) {

                    if (shapeAsJSON.stencil.id === 'CompositeTask' || shapeAsJSON.stencil.id === 'CompositeMultipleTask') {
                        if (shapeAsJSON.properties.decompositionid === subnet.yawlid) {
                            // Could not use setProperty here as Shape is only a JSON object
                            shapeAsJSON.properties['decompositionlink'] = this._buildDecomposeToLink(subnet.signavioid);
                        }
                    }

                }.bind(this));
            }.bind(this));
        }.bind(this));

    },

    /**
     * Generates the SVG for Diagram. This is necessary as Oryx generates the SVG client-side.
     *
     * @private
     * @param jsonData of Diagram
     * @return SVG of Diagram
     */
    _generateSVG:function (jsonData) {

        /* Version A - Use existing Canvas and delete Shapes after conversion
         this.facade.importJSON(jsonData);
         var svg = this.facade.getCanvas().getSVGRepresentation(true);
         var shapes = this.facade.getSelection();

         shapes.each(function(shape) {
         this.facade.deleteShape(shape);
         }.bind(this));

         return svg;
         */

        // Version B - Use new hidden Canvas
        var div = ORYX.Editor.graft("http://www.w3.org/1999/xhtml", null, ['div']);
        div.addClassName("ORYX_Editor");
        div.visibility = "hidden";

        $(document.body).appendChild(div);

        var canvasStencil = ORYX.Core.StencilSet.stencil("http://b3mn.org/stencilset/yawl2.2#Diagram");
        var tempCanvas = new ORYX.Core.Canvas({
            width:100,
            height:100,
            'eventHandlerCallback':function () {
                return false
            },
            id:ORYX.Editor.provideId(),
            parentNode:div
        }, canvasStencil);

        var properties = [];
        for (field in jsonData) {
            properties.push({
                prefix:'oryx',
                name:field,
                value:jsonData[field]
            });
        }

        tempCanvas.deserialize(properties);

        var shapes = tempCanvas.addShapeObjects(jsonData.childShapes, function () {
            return false;
        });

        tempCanvas.update();
        tempCanvas.updateSize();

        return tempCanvas.getSVGRepresentation(true);

    },

    /**
     * Generates the SVG for Diagram
     *
     * @private
     * @param jsonData of Diagram
     * @return SVG of Diagram
     */
    _saveSubnets:function (storedSubnets) {

        var ajaxRequestCounter = storedSubnets.length;

        Ext.each(storedSubnets, function (subnet, index) {

            // Get the URL
            var updateUrl = ORYX.CONFIG.SERVER_MODEL_HANDLER + '/' + subnet.signavioid;
            // Define the svg
            var svgElement = this._generateSVG(subnet.data);
            var svgDOM = DataManager.serialize(svgElement);

            var realParams = Object.clone(this._dummyParameters);
            realParams.name = this._buildDiagramTitle(subnet.data, subnet.isRoot);
            realParams.description = ((subnet.data.properties.specdescription) ? subnet.data.properties.specdescription : "");
            realParams.json_xml = Ext.encode(subnet.data);
            realParams.svg_xml = svgDOM;

            new Ajax.Request(updateUrl, {
                parameters:realParams,
                asynchronous:false,
                method:'PUT',
                onSuccess:function (request) {
                    ajaxRequestCounter--;
                    this.progressDialog.updateProgress(0.7, ORYX.I18N.YAWL.messageSavingNet + subnet.yawlid);

                    if (ajaxRequestCounter == 0) {
                        // Show the previously saved root net
                        storedSubnets.each(function (net) {
                            if (net.isRoot) {
                                this.progressDialog.updateProgress(1.0, "Import finished!");
                                this.progressDialog.hide();
                                Ext.Msg.getDialog().on('beforehide', function () {
                                    document.location = this._buildDecomposeToLink(net.signavioid);
                                }, this, {
                                    single:true
                                });
                                Ext.Msg.alert(ORYX.I18N.YAWL.importFinished, ORYX.I18N.YAWL.importFinishedText);

                            }
                        }.bind(this));
                    }

                }.bind(this),
                onFailure:function () {
                    this.progressDialog.hide();
                    Ext.Msg.alert(ORYX.I18N.YAWL.importFailed);
                }
            });
        }.bind(this));

    },

    /**
     * Show the Import Dialog
     */
    _showImportDialog:function () {
        var url = ORYX.CONFIG.YAWLIMPORTURL;
        var form = new Ext.form.FormPanel({
            baseCls:'x-plain',
            labelWidth:50,
            defaultType:'textfield',
            items:[
                {
                    text:ORYX.I18N.YAWL.selectFileText,
                    style:'font-size:12px;margin-bottom:10px;display:block;',
                    anchor:'100%',
                    xtype:'label'
                },
                {
                    fieldLabel:ORYX.I18N.YAWL.fileText,
                    name:'subject',
                    inputType:'file',
                    style:'margin-bottom:10px;display:block;',
                    itemCls:'ext_specific_window_overflow'
                },
                {
                    xtype:'textarea',
                    hideLabel:true,
                    name:'msg',
                    anchor:'100% -63'
                }
            ]
        });

        // Create the panel
        var dialog = new Ext.Window({
            autoCreate:true,
            layout:'fit',
            plain:true,
            bodyStyle:'padding:5px;',
            title:ORYX.I18N.YAWL.importTitle,
            height:350,
            width:500,
            modal:true,
            fixedcenter:true,
            shadow:true,
            proxyDrag:true,
            resizable:true,
            items:[ form ],
            buttons:[
                {
                    text:ORYX.I18N.YAWL.importButtonText,
                    handler:function () {

                        this.progressDialog = Ext.MessageBox.progress(ORYX.I18N.YAWL.importProgress);

                        window.setTimeout(function () {

                            var yawlString = form.items.items[2].getValue();
                            Ext.Ajax.request({
                                url:url,
                                method:'POST',
                                success:function (request) {
                                    /*
                                     * importObject should look like this:
                                     * 		rootNetName: name of the root net
                                     * 		diagrams: array of all converted nets
                                     */
                                    var importObject = Ext.decode(request.responseText);

                                    if (importObject.hasFailed) {
                                        alert(ORYX.I18N.YAWL.messageConversionFailed + importObject.warnings);
                                        this.progressDialog.hide();
                                    } else {

                                        if (importObject.hasWarnings) {
                                            alert(ORYX.I18N.YAWL.messageConversionWarnings + importObject.warnings);
                                        }

                                        if (importObject.diagrams.length === 1) {
                                            // Only one net, show it directly in the editor
                                            this._showRootNet(importObject);
                                            this.progressDialog.hide();
                                        } else {
                                            this._prepareAndImportNets(importObject);
                                        }
                                    }
                                }.bind(this),
                                failure:function () {
                                    this.progressDialog.hide();
                                    Ext.Msg.alert(ORYX.I18N.YAWL.importFailed);
                                },
                                params:{
                                    data:yawlString,
                                    action:"Import",
                                    standalone:"true"
                                }
                            });

                        }.bind(this), 100);

                        dialog.hide();

                    }.bind(this)
                },
                {
                    text:ORYX.I18N.YAWL.closeButtonText,
                    handler:function () {

                        dialog.hide();

                    }.bind(this)
                }
            ]
        });

        // Destroy the panel when hiding
        dialog.on('hide', function () {
            dialog.destroy(true);
            delete dialog;
        });

        // Show the panel
        dialog.show();

        // Adds the change event handler to
        form.items.items[1].getEl().dom.addEventListener('change',
            function (evt) {
                var reader = new FileReader();
                var file = evt.target.files[0];
                //Bugfix for Chrome
                if (typeof(FileReader.prototype.addEventListener) === "function") {
                    reader.addEventListener("loadend", function (evt) {
                        form.items.items[2].setValue(evt.target.result);
                    }, false);

                } else {
                    reader.onload = function (evt) {
                        form.items.items[2].setValue(evt.target.result);
                    };
                }
                reader.readAsText(file, "UTF-8");
            }, true)

    },


    /**************** Export ******************/

    _exportAjaxRequestCounter:0,

    /**
     * Show the export dialog.
     */
    doExport:function () {

        if (this._exportAjaxRequestCounter === 0) {
            var rootDiagram = Ext.decode(this.facade.getSerializedJSON());

            var exportData = {
                subDiagrams:[],
                rootDiagram:rootDiagram
            };

            var rootNet = this._extractNet(rootDiagram);
            this._fetchSubnets(rootNet, this._processSubnet.bind(this, exportData),
                this._submitExportData.bind(this, exportData));
        } else {
            Ext.Msg.alert("Export already in progress!");
        }

    },

    _fetchSubnets:function (parentNet, onSubnetRetrieved, onCompleted) {

        // Calculate how many subnets need to be retrieved
        this._exportAjaxRequestCounter += parentNet.childShapes.findAll(function (shape) {
            return ((shape.stencil.id === 'CompositeTask' || shape.stencil.id === 'CompositeMultipleTask') &&
                shape.properties.decompositionlink);
        }).size();

        // Directly return if there is no subnet
        if (this._exportAjaxRequestCounter === 0) {
            onCompleted();
        }

        parentNet.childShapes.each(function (shape) {
            // Here 'shape' is not a Oryx Shape object, just plain JS !!
            if (shape.stencil.id === 'CompositeTask' || shape.stencil.id === 'CompositeMultipleTask') {

                // Check if a subnet is available
                if (shape.properties.decompositionlink) {

                    // Just get the model id
                    var subnetModelId = this._extractModelIdFromLink(shape.properties.decompositionlink);

                    if (subnetModelId) {
                        var subnetJsonUrl = ORYX.CONFIG.SERVER_MODEL_HANDLER + '/' + subnetModelId + '/json';

                        new Ajax.Request(subnetJsonUrl, {
                            asynchronous:true,
                            method:'GET',
                            onSuccess:function (request) {

                                var subnetDiagram = Ext.decode(request.responseText);
                                var subnetID = this._extractNetID(subnetDiagram);
                                shape.properties.decompositionid = subnetID;

                                // Let caller decide what to do with subnet
                                if (onSubnetRetrieved && typeof(onSubnetRetrieved) === "function") {
                                    onSubnetRetrieved(subnetID, subnetDiagram);
                                }

                                this._exportAjaxRequestCounter--;
                                if (this._exportAjaxRequestCounter === 0) {
                                    if (onCompleted && typeof(onCompleted) === "function") {
                                        onCompleted();
                                    }
                                    ;
                                }

                            }.bind(this),
                            onFailure:function () {
                                // Do not go on with processing
                                Ext.Msg.alert(ORYX.I18N.YAWL.exportFailed);
                                this._exportAjaxRequestCounter--;
                            }.bind(this)
                        });
                    } else {
                        //TODO add warning that subnet could not be retrieved
                        this._exportAjaxRequestCounter--;
                    }

                } else {
                    // Generate a empty subnet
                    var subnetID = ORYX.Editor.provideId();
                    var emptySubnetDiagram = Ext.decode(this._dummySubnetData);
                    var emptySubnet = this._extractNet(emptySubnetDiagram);
                    emptySubnet.properties.yawlid = subnetID;
                    shape.properties.decompositionid = subnetID;

                    //TODO add warning about empty subnet
                    onSubnetRetrieved(subnetID, emptySubnetDiagram);
                }

            }
        }.bind(this));
    },

    _processSubnet:function (exportData, subnetID, subnetDiagram) {
        exportData.subDiagrams.push({'id':subnetID, 'diagram':subnetDiagram});
        this._fetchSubnets(this._extractNet(subnetDiagram),
            this._processSubnet.bind(this, exportData),
            this._submitExportData.bind(this, exportData));
    },

    _submitExportData:function (exportData) {

        var yawlExportJson = Ext.encode(exportData);

        // There are no more AJAX requests waiting -> Call Export Servlet
        Ext.Ajax.request({
            url:ORYX.CONFIG.YAWLEXPORTURL,
            method:'POST',
            success:function (request) {
                var exportResult = Ext.decode(request.responseText);
                if (exportResult.hasFailed) {
                    Ext.Msg.alert(ORYX.I18N.YAWL.messageConversionFailed + exportResult.warnings);
                } else {
                    if (exportResult.hasWarnings) {
                        alert(ORYX.I18N.YAWL.messageConversionWarnings + exportResult.warnings);
                    }
                    //this.openDownloadWindow(((exportResult.filename) ? exportResult.filename : "oryx-export") + ".yawl", exportResult.yawlXML);
                    this.openXMLWindow(exportResult.yawlXML);
                }
            }.bind(this),
            failure:function () {
                Ext.Msg.alert(ORYX.I18N.YAWL.exportFailed);
            },
            params:{
                data:yawlExportJson,
                action:"Export",
                standalone:"true"
            }
        });

    },


    /**************** Utilities ***************/

    /**
     * Highlight all elements in the Cancelation Set of the currently
     * selected shape.
     *
     * @param button
     * @param pressed
     */
    doHighlightCancelationSet:function (button, pressed) {

        if (!this._showingCancelationSetFor) {
            var selection = this.facade.getSelection();
            if (selection && selection.length > 0) {
                this._showingCancelationSetFor = selection[0];
            }
        }

        if (this._showingCancelationSetFor) {
            var cancelationSet = Ext.decode(this._showingCancelationSetFor.properties['oryx-cancelationset']);

            var shapeList = this.facade.getCanvas().getChildShapes(true);
            var cancelationShapes = shapeList.findAll(function (shape) {
                var itemFound = cancelationSet.items.find(function (item) {
                    return shape.properties['oryx-yawlid'] == item.element;
                });
                return itemFound != undefined;
            });

            if (pressed) {
                this.showOverlay(cancelationShapes);
            } else {
                this.hideOverlay();
            }

        }

        if (!pressed) {
            this._showingCancelationSetFor = null;
        }

    },

    /**
     * Show the cancelation set overlay for shapes.
     *
     * @param shapes
     */
    showOverlay:function (shapes) {

        var cross = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
            ['path', {
                "stroke-width":1.0, "stroke":"red", "d":"M0,0 L-10,-10 M-10,0 L0,-10", "line-captions":"round"
            }]);

        this.facade.raiseEvent({
            type:ORYX.CONFIG.EVENT_OVERLAY_SHOW,
            id:"yawl.cancelationset",
            shapes:shapes,
            attributes:{stroke:"#FF0000"},
            node:cross,
            nodePosition:"NE"
        });

    },

    /**
     * Hide the cancelation set overlay.
     */
    hideOverlay:function () {

        this.facade.raiseEvent({
            type:ORYX.CONFIG.EVENT_OVERLAY_HIDE,
            id:"yawl.cancelationset"
        });

    },

    /**
     * React on a property change
     *
     * @param {Object} args
     * @param element
     **/
    propertyChanged:function (args, element) {
        if (args.name == "oryx-cancelationset") {
            if (element) {
                var cancelationMarkerShape = this._getShapeSVGElement(element, "cancelation");
                if (cancelationMarkerShape) {
                    if (args.value && (args.value != "")) {
                        var cancelationSet = Ext.decode(args.value);
                        if ((cancelationSet.totalCount > 0) || (cancelationSet.items.length > 0)) {
                            // Show Cancelation Marker
                            cancelationMarkerShape.setAttribute('visibility', 'visible');
                        } else {
                            cancelationMarkerShape.setAttribute('visibility', 'hidden');
                        }
                    } else {
                        cancelationMarkerShape.setAttribute('visibility', 'hidden');
                    }

                } else {
                    console.warn("Cancellation set marker not found on element: " + element);
                }
            }
        }
    },

    _unfoldMenu:null,

    /**
     * Called as a shape is selected
     *
     * @param {Object} event
     *         The ORYX.CONFIG.EVENT_SELECTION_CHANGED event
     */
    onSelectElement:function (event) {

        if (this._unfoldMenu) {
            //TODO seems not to destroy submenus :(
            this._unfoldMenu.destroy();
            this._unfoldMenu = null;
        }

        // First selected shape
        var shape = event.elements[0];

        if (shape && event.elements.length === 1 && shape._stencil &&
            (shape.getStencil().idWithoutNs() ===
                "CompositeTask" ||
                shape.getStencil().idWithoutNs() ===
                    "CompositeMultipleTask")) {

            var decompositionLinkElement = this._getShapeSVGElement(shape, "decompositionlink");

            if (decompositionLinkElement) {
                if (shape.properties["oryx-decompositionlink"] && shape.properties["oryx-decompositionlink"] != "") {
                    // Enable Link
                    decompositionLinkElement.setAttribute("pointer-events", "visiblePainted");
                } else {
                    // Deactive Link
                    decompositionLinkElement.setAttribute("pointer-events", "none");
                    // Offer create new Subnet functionality
                    if (this._unfoldMenu) {
                        this._showUnfoldMenu(shape);
                    } else {
                        this._createNewSubnetMenu(shape, decompositionLinkElement);
                    }
                }
            } else {
                Ext.Msg.alert('Error creating the "Unfold to Net menu"!');
            }
        }
    },

    _createNewSubnetMenu:function (shape, decompositionLink) {

        this._unfoldMenu = new Ext.menu.Menu({
            id:'unfoldmenu',
            items:[
                {
                    text:"Unfold to",
                    icon:ORYX.PATH + 'images/shape_ungroup.png',
                    menu:{
                        xtype:'menu',
                        items:[
                            new Ext.menu.Item({
                                text:ORYX.I18N.YAWL.unfoldMenuNewSubnet,
                                handler:function (menu, event) {
                                    this._createNewSubnet(shape, decompositionLink);
                                }.bind(this)
                            }),
                            {
                                id:shape.node.id + '_menu_existing',
                                text:ORYX.I18N.YAWL.unfoldMenuExistingSubnet,
                                menu:{
                                    xtype:'menu',
                                    items:[
                                        ORYX.I18N.YAWL.unfoldMenuChooseSubnet,
                                        "-"
                                    ]
                                }
                            }
                        ]
                    }
                }
            ]
        });

        this._retrieveAvailableSubnets(shape, decompositionLink, Ext.getCmp(shape.node.id + '_menu_existing').menu);
        this._showUnfoldMenu(shape);

    },

    _showUnfoldMenu:function (shape) {
        this._unfoldMenu.show(shape.node);
        this._unfoldMenu.getEl().move("r", 50);
        this._unfoldMenu.getEl().move("b", 50);
    },

    _createNewSubnet:function (compositeTaskShape, linkElement) {

        var dummySVG = '<svg/>';
        var dummyParams = Object.clone(this._dummyParameters);

        if (compositeTaskShape.properties["oryx-name"]) {
            dummyParams.name = compositeTaskShape.properties["oryx-name"];
        }

        new Ajax.Request(ORYX.CONFIG.SERVER_MODEL_HANDLER, {
            parameters:dummyParams,
            asynchronous:true,
            method:'POST',
            onSuccess:function (request) {
                var response = Ext.decode(request.responseText);
                var link = this._buildDecomposeToLink(this._extractModelId(model));
                linkElement.setAttribute("pointer-events", "visiblePainted");
                compositeTaskShape.setProperty("oryx-decompositionlink", link);
                compositeTaskShape.refresh();
                this._unfoldMenu.destroy();
                Ext.Msg.alert(ORYX.I18N.YAWL.unfoldMenuSuccess, ORYX.I18N.YAWL.unfoldMenuSuccessText + link);
            }.bind(this),
            onFailure:function () {
                Ext.Msg.alert(ORYX.I18N.YAWL.unfoldNewMenuFailure);
            }.bind(this)
        });

    },

    _retrieveAvailableSubnets:function (shape, linkElement, menu) {

        new Ajax.Request(ORYX.CONFIG.SERVER_HANDLER_ROOT + "/directory/root-directory", {
            asynchronous:true,
            method:'GET',
            onSuccess:function (request) {
                var modelList = Ext.decode(request.responseText);
                Ext.each(modelList, function (model) {

                    if (model.rel === "mod") {
                        // It is a Model in Root-Directory
                        if (model.rep.type === "YAWL 2.2") {

                            menu.add(new Ext.menu.Item({
                                text:model.rep.name,
                                handler:function (menu, event) {
                                    linkElement.setAttribute("pointer-events", "visiblePainted");
                                    var link = this._buildDecomposeToLink(this._extractModelId(model));
                                    shape.setProperty("oryx-decompositionlink", link);
                                    shape.refresh();
                                    this._unfoldMenu.destroy();
                                    Ext.Msg.alert(ORYX.I18N.YAWL.unfoldMenuSuccess, ORYX.I18N.YAWL.unfoldMenuSuccessText + link);
                                }.bind(this)
                            }));

                        }
                    }

                }.bind(this));
            }.bind(this),
            onFailure:function () {
                Ext.Msg.alert(ORYX.I18N.YAWL.unfoldExistingMenuFailure);
            }.bind(this)
        });

    },

    /**
     * Initalise the Editor with a default YAWL Net
     **/
    initialiseDiagram:function () {
        /*
        if (this.facade.getCanvas().getChildNodes().length === 0) {
            this.facade.importJSON(this._dummyRootnetData);
        }
        */
    },

    /**************** Magnets Visible ***************/

    /**
     * Magnets change the visibilities by changing the Decorators
     *
     * @param event
     * @param uiObj
     */
    handlePropertiesDecorator:function (event, uiObj) {

        //TODO need to change this to really hide all unnessecary magnets

        var stencilId = uiObj.getStencil().idWithoutNs();
        if (stencilId === "AtomicTask" ||
            stencilId === "CompositeTask" ||
            stencilId === "MultipleAtomicTask" ||
            stencilId === "MultipleCompositeTask") {

            if (event.oldValue != "") {
                this._resetMagnet(event);

                // -- LEFT --
                if (event.elements[0].properties["oryx-join"] == "andL" ||
                    event.elements[0].properties["oryx-join"] == "xorL" ||
                    event.elements[0].properties["oryx-join"] == "orL" ||
                    event.elements[0].properties["oryx-split"] == "andL" ||
                    event.elements[0].properties["oryx-split"] == "xorL" ||
                    event.elements[0].properties["oryx-split"] == "orL") {
                    event.elements[0].magnets[15].node.style.display = "";
                    event.elements[0].magnets[16].node.style.display = "";
                    event.elements[0].magnets[17].node.style.display = "";
                    event.elements[0].magnets[18].node.style.display = "";
                    event.elements[0].magnets[19].node.style.display = "";
                }
                // -- RIGHT --
                if (event.elements[0].properties["oryx-join"] == "andR" ||
                    event.elements[0].properties["oryx-join"] == "xorR" ||
                    event.elements[0].properties["oryx-join"] == "orR" ||
                    event.elements[0].properties["oryx-split"] == "andR" ||
                    event.elements[0].properties["oryx-split"] == "xorR" ||
                    event.elements[0].properties["oryx-split"] == "orR") {
                    event.elements[0].magnets[20].node.style.display = "";
                    event.elements[0].magnets[21].node.style.display = "";
                    event.elements[0].magnets[22].node.style.display = "";
                    event.elements[0].magnets[23].node.style.display = "";
                    event.elements[0].magnets[24].node.style.display = "";
                }
                // -- TOP --
                if (event.elements[0].properties["oryx-join"] == "andT" ||
                    event.elements[0].properties["oryx-join"] == "xorT" ||
                    event.elements[0].properties["oryx-join"] == "orT" ||
                    event.elements[0].properties["oryx-split"] == "andT" ||
                    event.elements[0].properties["oryx-split"] == "xorT" ||
                    event.elements[0].properties["oryx-split"] == "orT") {
                    event.elements[0].magnets[5].node.style.display = "";
                    event.elements[0].magnets[6].node.style.display = "";
                    event.elements[0].magnets[7].node.style.display = "";
                    event.elements[0].magnets[8].node.style.display = "";
                    event.elements[0].magnets[9].node.style.display = "";
                }
                // -- BOTTOM --
                if (event.elements[0].properties["oryx-join"] == "andB" ||
                    event.elements[0].properties["oryx-join"] == "xorB" ||
                    event.elements[0].properties["oryx-join"] == "orB" ||
                    event.elements[0].properties["oryx-split"] == "andB" ||
                    event.elements[0].properties["oryx-split"] == "xorB" ||
                    event.elements[0].properties["oryx-split"] == "orB") {
                    event.elements[0].magnets[10].node.style.display = "";
                    event.elements[0].magnets[11].node.style.display = "";
                    event.elements[0].magnets[12].node.style.display = "";
                    event.elements[0].magnets[13].node.style.display = "";
                    event.elements[0].magnets[14].node.style.display = "";
                }
            }

        }
    },

    /**
     * Reset the Magnets Visibility
     *
     * @param event
     */
    _resetMagnet:function (event) {
        console.dir(event);
        // -- TOP --
        event.elements[0].magnets[5].node.style.display = "none";
        event.elements[0].magnets[6].node.style.display = "none";
        event.elements[0].magnets[7].node.style.display = "none";
        event.elements[0].magnets[8].node.style.display = "none";
        event.elements[0].magnets[9].node.style.display = "none";
        // -- BOTTOM --
        event.elements[0].magnets[10].node.style.display = "none";
        event.elements[0].magnets[11].node.style.display = "none";
        event.elements[0].magnets[12].node.style.display = "none";
        event.elements[0].magnets[13].node.style.display = "none";
        event.elements[0].magnets[14].node.style.display = "none";
        // -- LEFT --
        event.elements[0].magnets[15].node.style.display = "none";
        event.elements[0].magnets[16].node.style.display = "none";
        event.elements[0].magnets[17].node.style.display = "none";
        event.elements[0].magnets[18].node.style.display = "none";
        event.elements[0].magnets[19].node.style.display = "none";
        // --RIGHT --
        event.elements[0].magnets[20].node.style.display = "none";
        event.elements[0].magnets[21].node.style.display = "none";
        event.elements[0].magnets[22].node.style.display = "none";
        event.elements[0].magnets[23].node.style.display = "none";
        event.elements[0].magnets[24].node.style.display = "none";
    }



});


/**
 * Workaround for Bug cf. http://code.google.com/p/oryx-editor/issues/detail?id=482
 */
/*
 Copyright (C) 2011 by Florent FAYOLLE

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

/**
 * hack to use the canConnect property (specified in OryxSSS.pdf Manual)
 */
(function () {
    var prot = ORYX.Core.StencilSet.Rules.prototype;
    var origFn = prot.canConnect;
    prot.canConnect = function (args) {
        var ret = true;
        ret = ret && origFn.apply(this, arguments);
        if (ret) {
            // we get the rules
            var rules = args.edgeStencil.stencilSet().jsonRules();
            // if there is any canConnect Rules
            if (rules.canConnect !== undefined) {
                ret = rules.canConnect(args);
            }

        }
        return ret;
    };
})();
