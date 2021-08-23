if(!Apromore){var Apromore={}
}if(!Apromore.CONFIG){Apromore.CONFIG={}
}Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX="/bpmneditor";
Apromore.CONFIG.EDITOR_PATH="/editor";
var Clazz=function(){};
Clazz.prototype.construct=function(){};
Clazz.extend=function(def){var classDef=function(){if(arguments[0]!==Clazz){this.construct.apply(this,arguments)
}};
var proto=new this(Clazz);
var superClass=this.prototype;
for(var n in def){var item=def[n];
if(item instanceof Function){item.$=superClass
}proto[n]=item
}classDef.prototype=proto;
classDef.extend=this.extend;
return classDef
};
if(!Apromore){var Apromore={}
}Apromore.Utils={graft:function(namespace,parent,t,doc){doc=(doc||(parent&&parent.ownerDocument)||document);
var e;
if(t===undefined){throw"Can't graft an undefined value"
}else{if(t.constructor==String){e=doc.createTextNode(t)
}else{for(var i=0;
i<t.length;
i++){if(i===0&&t[i].constructor==String){var snared;
snared=t[i].match(/^([a-z][a-z0-9]*)\.([^\s\.]+)$/i);
if(snared){e=doc.createElementNS(namespace,snared[1]);
e.setAttributeNS(null,"class",snared[2]);
continue
}snared=t[i].match(/^([a-z][a-z0-9]*)$/i);
if(snared){e=doc.createElementNS(namespace,snared[1]);
continue
}e=doc.createElementNS(namespace,"span");
e.setAttribute(null,"class","namelessFromLOL")
}if(t[i]===undefined){throw"Can't graft an undefined value in a list!"
}else{if(t[i].constructor==String||t[i].constructor==Array){this.graft(namespace,e,t[i],doc)
}else{if(t[i].constructor==Number){this.graft(namespace,e,t[i].toString(),doc)
}else{if(t[i].constructor==Object){for(var k in t[i]){e.setAttributeNS(null,k,t[i][k])
}}else{}}}}}}}if(parent){parent.appendChild(e)
}else{}return e
},provideId:function(){var res=[],hex="0123456789ABCDEF";
for(var i=0;
i<36;
i++){res[i]=Math.floor(Math.random()*16)
}res[14]=4;
res[19]=(res[19]&3)|8;
for(var i=0;
i<36;
i++){res[i]=hex[res[i]]
}res[8]=res[13]=res[18]=res[23]="-";
return"Apromore_"+res.join("")
}};
if(!Apromore){var Apromore={}
}Apromore.Log={Apromore_LOGLEVEL:5,Apromore_LOGLEVEL_TRACE:5,Apromore_LOGLEVEL_DEBUG:4,Apromore_LOGLEVEL_INFO:3,Apromore_LOGLEVEL_WARN:2,Apromore_LOGLEVEL_ERROR:1,Apromore_LOGLEVEL_FATAL:0,Apromore_CONFIGURATION_DELAY:100,Apromore_CONFIGURATION_WAIT_ATTEMPTS:10,__appenders:[{append:function(message){console.log(message)
}}],trace:function(){if(Apromore.Log.Apromore_LOGLEVEL>=Apromore.Log.Apromore_LOGLEVEL_TRACE){Apromore.Log.__log("TRACE",arguments)
}},debug:function(){if(Apromore.Log.Apromore_LOGLEVEL>=Apromore.Log.Apromore_LOGLEVEL_DEBUG){Apromore.Log.__log("DEBUG",arguments)
}},info:function(){if(Apromore.Log.Apromore_LOGLEVEL>=Apromore.Log.Apromore_LOGLEVEL_INFO){Apromore.Log.__log("INFO",arguments)
}},warn:function(){if(Apromore.Log.Apromore_LOGLEVEL>=Apromore.Log.Apromore_LOGLEVEL_WARN){Apromore.Log.__log("WARN",arguments)
}},error:function(){if(Apromore.Log.Apromore_LOGLEVEL>=Apromore.Log.Apromore_LOGLEVEL_ERROR){Apromore.Log.__log("ERROR",arguments)
}},fatal:function(){if(Apromore.Log.Apromore_LOGLEVEL>=Apromore.Log.Apromore_LOGLEVEL_FATAL){Apromore.Log.__log("FATAL",arguments)
}},__log:function(prefix,messageParts){messageParts[0]=(new Date()).getTime()+" "+prefix+" "+messageParts[0];
var message=this.printf.apply(null,messageParts);
Apromore.Log.__appenders.forEach(function(appender){appender.append(message)
})
},addAppender:function(appender){Apromore.Log.__appenders.push(appender)
},printf:function(){var result=arguments[0];
for(var i=1;
i<arguments.length;
i++){result=result.replace("%"+(i-1),arguments[i])
}return result
}};
if(!Apromore){var Apromore={}
}if(!Apromore.CONFIG){Apromore.CONFIG={}
}Apromore.CONFIG.ROOT_PATH=(Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX)?Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX+"/editor/":"../editor/";
Apromore.CONFIG.EXPLORER_PATH=(Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX)?Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX+"/explorer/":"../explorer";
Apromore.CONFIG.LIBS_PATH=(Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX)?Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX+"/libs/":"../libs";
Apromore.PATH="."+Apromore.CONFIG.ROOT_PATH;
Apromore.CONFIG.SERVER_HANDLER_ROOT=Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX;
Apromore.CONFIG.SERVER_EDITOR_HANDLER=Apromore.CONFIG.SERVER_HANDLER_ROOT+"/editor";
Apromore.CONFIG.SERVER_MODEL_HANDLER=Apromore.CONFIG.SERVER_HANDLER_ROOT+"/model";
Apromore.CONFIG.PLUGINS_CONFIG=Apromore.CONFIG.SERVER_HANDLER_ROOT+"/bpmneditor_plugins";
Apromore.CONFIG.SYNTAXCHECKER_URL=Apromore.CONFIG.SERVER_HANDLER_ROOT+"/syntaxchecker";
Apromore.CONFIG.SS_EXTENSIONS_FOLDER=Apromore.CONFIG.ROOT_PATH+"stencilsets/extensions/";
Apromore.CONFIG.SS_EXTENSIONS_CONFIG=Apromore.CONFIG.SERVER_HANDLER_ROOT+"/editor_ssextensions";
Apromore.CONFIG.Apromore_NEW_URL="/new";
Apromore.CONFIG.BPMN_LAYOUTER=Apromore.CONFIG.ROOT_PATH+"bpmnlayouter";
Apromore.CONFIG.GLOSSARY_PATH="/glossary";
Apromore.CONFIG.GLOSSARY_PROPERTY_SUFFIX="_glossary";
Apromore.CONFIG.GLOSSARY_PROPERTY_DIRTY_SUFFIX="_glossary_dirty";
if(!Apromore){var Apromore={}
}if(!Apromore.CONFIG){Apromore.CONFIG={}
}Apromore.CONFIG.BACKEND_SWITCH=true;
Apromore.CONFIG.PANEL_LEFT_COLLAPSED=true;
Apromore.CONFIG.PANEL_LEFT_WIDTH=200;
Apromore.CONFIG.PANEL_RIGHT_COLLAPSED=true;
Apromore.CONFIG.PANEL_RIGHT_WIDTH=200;
Apromore.CONFIG.APPNAME="Signavio";
Apromore.CONFIG.WEB_URL="explorer";
Apromore.CONFIG.PDF_EXPORT_URL="/bpmneditor/editor/pdf";
Apromore.CONFIG.BIMP_URL="http://bimp.cs.ut.ee/uploadsignavio";
Apromore.CONFIG.DIAGRAM_PRINTER_URL="/printsvg";
Apromore.CONFIG.LICENSE_URL="/LICENSE";
Apromore.CONFIG.BLANK_IMAGE=Apromore.CONFIG.LIBS_PATH+"/ext-2.0.2/resources/images/default/s.gif";
Apromore.CONFIG.SHOW_GRIDLINE=false;
Apromore.CONFIG.MODE_READONLY="readonly";
Apromore.CONFIG.MODE_FULLSCREEN="fullscreen";
Apromore.CONFIG.WINDOW_HEIGHT=550;
Apromore.CONFIG.PREVENT_LOADINGMASK_AT_READY=true;
Apromore.CONFIG.PLUGINS_ENABLED=true;
Apromore.CONFIG.PLUGINS_FOLDER="Plugins/";
Apromore.CONFIG.BPMN20_SCHEMA_VALIDATION_ON=true;
Apromore.CONFIG.NAMESPACE_Apromore="http://www.b3mn.org/Apromore";
Apromore.CONFIG.NAMESPACE_SVG="http://www.w3.org/2000/svg";
Apromore.CONFIG.CANVAS_WIDTH=1485;
Apromore.CONFIG.CANVAS_HEIGHT=1050;
Apromore.CONFIG.CANVAS_RESIZE_INTERVAL=300;
Apromore.CONFIG.SELECTED_AREA_PADDING=4;
Apromore.CONFIG.CANVAS_BACKGROUND_COLOR="none";
Apromore.CONFIG.GRID_DISTANCE=30;
Apromore.CONFIG.GRID_ENABLED=true;
Apromore.CONFIG.ZOOM_OFFSET=0.1;
Apromore.CONFIG.DEFAULT_SHAPE_MARGIN=60;
Apromore.CONFIG.SCALERS_SIZE=7;
Apromore.CONFIG.MINIMUM_SIZE=20;
Apromore.CONFIG.MAXIMUM_SIZE=10000;
Apromore.CONFIG.OFFSET_MAGNET=15;
Apromore.CONFIG.OFFSET_EDGE_LABEL_TOP=8;
Apromore.CONFIG.OFFSET_EDGE_LABEL_BOTTOM=8;
Apromore.CONFIG.OFFSET_EDGE_BOUNDS=5;
Apromore.CONFIG.COPY_MOVE_OFFSET=30;
Apromore.CONFIG.BORDER_OFFSET=14;
Apromore.CONFIG.MAX_NUM_SHAPES_NO_GROUP=12;
Apromore.CONFIG.SHAPEMENU_CREATE_OFFSET_CORNER=30;
Apromore.CONFIG.SHAPEMENU_CREATE_OFFSET=45;
Apromore.CONFIG.SHAPEMENU_RIGHT="Apromore_Right";
Apromore.CONFIG.SHAPEMENU_BOTTOM="Apromore_Bottom";
Apromore.CONFIG.SHAPEMENU_LEFT="Apromore_Left";
Apromore.CONFIG.SHAPEMENU_TOP="Apromore_Top";
Apromore.CONFIG.MORPHITEM_DISABLED="Apromore_MorphItem_disabled";
Apromore.CONFIG.TYPE_STRING="string";
Apromore.CONFIG.TYPE_BOOLEAN="boolean";
Apromore.CONFIG.TYPE_INTEGER="integer";
Apromore.CONFIG.TYPE_FLOAT="float";
Apromore.CONFIG.TYPE_COLOR="color";
Apromore.CONFIG.TYPE_DATE="date";
Apromore.CONFIG.TYPE_CHOICE="choice";
Apromore.CONFIG.TYPE_URL="url";
Apromore.CONFIG.TYPE_DIAGRAM_LINK="diagramlink";
Apromore.CONFIG.TYPE_COMPLEX="complex";
Apromore.CONFIG.TYPE_TEXT="text";
Apromore.CONFIG.TYPE_EPC_FREQ="epcfrequency";
Apromore.CONFIG.TYPE_GLOSSARY_LINK="glossarylink";
Apromore.CONFIG.LABEL_LINE_DISTANCE=2;
Apromore.CONFIG.LABEL_DEFAULT_LINE_HEIGHT=12;
Apromore.CONFIG.ENABLE_MORPHMENU_BY_HOVER=false;
Apromore.CONFIG.EDITOR_ALIGN_BOTTOM=1;
Apromore.CONFIG.EDITOR_ALIGN_MIDDLE=2;
Apromore.CONFIG.EDITOR_ALIGN_TOP=4;
Apromore.CONFIG.EDITOR_ALIGN_LEFT=8;
Apromore.CONFIG.EDITOR_ALIGN_CENTER=16;
Apromore.CONFIG.EDITOR_ALIGN_RIGHT=32;
Apromore.CONFIG.EDITOR_ALIGN_SIZE=48;
Apromore.CONFIG.EVENT_MOUSEDOWN="mousedown";
Apromore.CONFIG.EVENT_MOUSEUP="mouseup";
Apromore.CONFIG.EVENT_MOUSEOVER="mouseover";
Apromore.CONFIG.EVENT_MOUSEOUT="mouseout";
Apromore.CONFIG.EVENT_MOUSEMOVE="mousemove";
Apromore.CONFIG.EVENT_DBLCLICK="dblclick";
Apromore.CONFIG.EVENT_KEYDOWN="keydown";
Apromore.CONFIG.EVENT_KEYUP="keyup";
Apromore.CONFIG.EVENT_LOADED="editorloaded";
Apromore.CONFIG.EVENT_EXECUTE_COMMANDS="executeCommands";
Apromore.CONFIG.EVENT_STENCIL_SET_LOADED="stencilSetLoaded";
Apromore.CONFIG.EVENT_SELECTION_CHANGED="selectionchanged";
Apromore.CONFIG.EVENT_SHAPEADDED="shapeadded";
Apromore.CONFIG.EVENT_SHAPEREMOVED="shaperemoved";
Apromore.CONFIG.EVENT_PROPERTY_CHANGED="propertyChanged";
Apromore.CONFIG.EVENT_DRAGDROP_START="dragdrop.start";
Apromore.CONFIG.EVENT_SHAPE_MENU_CLOSE="shape.menu.close";
Apromore.CONFIG.EVENT_DRAGDROP_END="dragdrop.end";
Apromore.CONFIG.EVENT_RESIZE_START="resize.start";
Apromore.CONFIG.EVENT_RESIZE_END="resize.end";
Apromore.CONFIG.EVENT_DRAGDOCKER_DOCKED="dragDocker.docked";
Apromore.CONFIG.EVENT_HIGHLIGHT_SHOW="highlight.showHighlight";
Apromore.CONFIG.EVENT_HIGHLIGHT_HIDE="highlight.hideHighlight";
Apromore.CONFIG.EVENT_LOADING_ENABLE="loading.enable";
Apromore.CONFIG.EVENT_LOADING_DISABLE="loading.disable";
Apromore.CONFIG.EVENT_LOADING_STATUS="loading.status";
Apromore.CONFIG.EVENT_OVERLAY_SHOW="overlay.show";
Apromore.CONFIG.EVENT_OVERLAY_HIDE="overlay.hide";
Apromore.CONFIG.EVENT_ARRANGEMENT_TOP="arrangement.setToTop";
Apromore.CONFIG.EVENT_ARRANGEMENT_BACK="arrangement.setToBack";
Apromore.CONFIG.EVENT_ARRANGEMENT_FORWARD="arrangement.setForward";
Apromore.CONFIG.EVENT_ARRANGEMENT_BACKWARD="arrangement.setBackward";
Apromore.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED="propertyWindow.propertyChanged";
Apromore.CONFIG.EVENT_LAYOUT_ROWS="layout.rows";
Apromore.CONFIG.EVENT_LAYOUT_BPEL="layout.BPEL";
Apromore.CONFIG.EVENT_LAYOUT_BPEL_VERTICAL="layout.BPEL.vertical";
Apromore.CONFIG.EVENT_LAYOUT_BPEL_HORIZONTAL="layout.BPEL.horizontal";
Apromore.CONFIG.EVENT_LAYOUT_BPEL_SINGLECHILD="layout.BPEL.singlechild";
Apromore.CONFIG.EVENT_LAYOUT_BPEL_AUTORESIZE="layout.BPEL.autoresize";
Apromore.CONFIG.EVENT_AUTOLAYOUT_LAYOUT="autolayout.layout";
Apromore.CONFIG.EVENT_UNDO_EXECUTE="undo.execute";
Apromore.CONFIG.EVENT_UNDO_ROLLBACK="undo.rollback";
Apromore.CONFIG.EVENT_BUTTON_UPDATE="toolbar.button.update";
Apromore.CONFIG.EVENT_LAYOUT="layout.dolayout";
Apromore.CONFIG.EVENT_GLOSSARY_LINK_EDIT="glossary.link.edit";
Apromore.CONFIG.EVENT_GLOSSARY_SHOW="glossary.show.info";
Apromore.CONFIG.EVENT_GLOSSARY_NEW="glossary.show.new";
Apromore.CONFIG.EVENT_DOCKERDRAG="dragTheDocker";
Apromore.CONFIG.EVENT_SHOW_PROPERTYWINDOW="propertywindow.show";
Apromore.CONFIG.EVENT_ABOUT_TO_SAVE="file.aboutToSave";
Apromore.CONFIG.SELECTION_HIGHLIGHT_SIZE=5;
Apromore.CONFIG.SELECTION_HIGHLIGHT_COLOR="#4444FF";
Apromore.CONFIG.SELECTION_HIGHLIGHT_COLOR2="#9999FF";
Apromore.CONFIG.SELECTION_HIGHLIGHT_STYLE_CORNER="corner";
Apromore.CONFIG.SELECTION_HIGHLIGHT_STYLE_RECTANGLE="rectangle";
Apromore.CONFIG.SELECTION_VALID_COLOR="#00FF00";
Apromore.CONFIG.SELECTION_INVALID_COLOR="#FF0000";
Apromore.CONFIG.DOCKER_DOCKED_COLOR="#00FF00";
Apromore.CONFIG.DOCKER_UNDOCKED_COLOR="#FF0000";
Apromore.CONFIG.DOCKER_SNAP_OFFSET=10;
Apromore.CONFIG.EDIT_OFFSET_PASTE=10;
Apromore.CONFIG.KEY_CODE_X=88;
Apromore.CONFIG.KEY_CODE_C=67;
Apromore.CONFIG.KEY_CODE_V=86;
Apromore.CONFIG.KEY_CODE_DELETE=46;
Apromore.CONFIG.KEY_CODE_META=224;
Apromore.CONFIG.KEY_CODE_BACKSPACE=8;
Apromore.CONFIG.KEY_CODE_LEFT=37;
Apromore.CONFIG.KEY_CODE_RIGHT=39;
Apromore.CONFIG.KEY_CODE_UP=38;
Apromore.CONFIG.KEY_CODE_DOWN=40;
Apromore.CONFIG.KEY_Code_enter=12;
Apromore.CONFIG.KEY_Code_left=37;
Apromore.CONFIG.KEY_Code_right=39;
Apromore.CONFIG.KEY_Code_top=38;
Apromore.CONFIG.KEY_Code_bottom=40;
Apromore.CONFIG.META_KEY_META_CTRL="metactrl";
Apromore.CONFIG.META_KEY_ALT="alt";
Apromore.CONFIG.META_KEY_SHIFT="shift";
Apromore.CONFIG.KEY_ACTION_DOWN="down";
Apromore.CONFIG.KEY_ACTION_UP="up";
Apromore.CONFIG.REMOTE_WINDOW_HEIGHT_DEFAULT=300;
Apromore.CONFIG.REMOTE_WINDOW_WIDTH_DEFAULT=300;
if(!Apromore){var Apromore={}
}Apromore.EditorApp={construct:function(config){this.editor=undefined;
this.availablePlugins=[];
this.activatedPlugins=[];
this.pluginsData=[];
this.layout_regions=undefined;
this.layout=undefined;
this.id=config.id;
if(!this.id){Apromore.Log.fatal("Missing the container HTML element for the editor");
return
}this.fullscreen=config.fullscreen!==false;
this.useSimulationPanel=config.useSimulationPanel||false;
this.enabledPlugins=config.enabledPlugins;
this._createEditor(config.preventFitDelay||false);
this._generateGUI();
this._loadPlugins();
var options={container:"#"+this.getEditor().rootNode.id,};
if(!config.viewOnly){options.keyboard={bindTo:window};
options.propertiesPanel=this.useSimulationPanel?{parent:"#js-properties-panel"}:undefined
}this.getEditor().attachEditor(new BpmnJS(options));
var me=this;
window.setTimeout(function(){if(config&&config.xml){me.importXML(config.xml,me._initUI.bind(me))
}else{me._initUI()
}},100)
},_initUI:function(){if(Apromore.CONFIG.PANEL_RIGHT_COLLAPSED===true){this.layout_regions.east.collapse()
}if(Apromore.CONFIG.PANEL_LEFT_COLLAPSED===true){this.layout_regions.west.collapse()
}},zoomFitToModel:function(){this.getEditor().zoomFitToModel()
},_generateGUI:function(){var layoutHeight=Apromore.CONFIG.WINDOW_HEIGHT;
this.layout_regions={north:new Ext.Panel({region:"north",cls:"x-panel-editor-north",autoEl:"div",border:false}),east:new Ext.Panel({region:"east",layout:"anchor",cls:"x-panel-editor-east",collapseTitle:window.Apromore.I18N.View.East,titleCollapse:true,border:false,cmargins:{left:0,right:0},floatable:false,expandTriggerAll:true,collapsible:true,width:450,split:true,title:"Simulation parameters",items:{layout:"fit",autoHeight:true,el:document.getElementById("js-properties-panel")}}),south:new Ext.Panel({region:"south",cls:"x-panel-editor-south",autoEl:"div",border:false}),west:new Ext.Panel({region:"west",layout:"anchor",autoEl:"div",cls:"x-panel-editor-west",collapsible:true,titleCollapse:true,collapseTitle:window.Apromore.I18N.View.West,width:Apromore.CONFIG.PANEL_LEFT_WIDTH||10,autoScroll:Ext.isIPad?false:true,cmargins:{left:0,right:0},floatable:false,expandTriggerAll:true,split:true,title:"West"}),center:new Ext.Panel({region:"center",cls:"x-panel-editor-center",autoScroll:false,items:{layout:"fit",autoHeight:true,el:this.getEditor().rootNode}}),info:new Ext.Panel({region:"south",cls:"x-panel-editor-info",autoEl:"div",border:false,layout:"fit",cmargins:{top:0,bottom:0,left:0,right:0},collapseTitle:"Information",floatable:false,titleCollapse:false,expandTriggerAll:true,collapsible:true,split:true,title:"Information",height:100,tools:[{id:"close",handler:function(g,f,e){e.hide();
e.ownerCt.layout.layout()
}}]})};
var layout_config={layout:"border",items:[this.layout_regions.north,this.layout_regions.east,this.layout_regions.south,this.layout_regions.west,new Ext.Panel({layout:"border",region:"center",height:500,border:false,items:[this.layout_regions.center,this.layout_regions.info]})]};
if(this.fullscreen){this.layout=new Ext.Viewport(layout_config)
}else{layout_config.renderTo=this.id;
layout_config.height=this._getContainer().clientHeight;
this.layout=new Ext.Panel(layout_config)
}if(!this.useSimulationPanel){this.layout_regions.east.hide()
}this.layout_regions.west.hide();
this.layout_regions.info.hide();
if(Ext.isIPad&&"undefined"!=typeof iScroll){this.getEditor().iscroll=new iScroll(this.layout_regions.center.body.dom.firstChild,{touchCount:2})
}this._getContainer().setAttributeNS(null,"align","left");
this.getEditor().rootNode.setAttributeNS(null,"align","left")
},addToRegion:function(region,component,title){if(region.toLowerCase&&this.layout_regions[region.toLowerCase()]){var current_region=this.layout_regions[region.toLowerCase()];
current_region.add(component);
Apromore.Log.debug("original dimensions of region %0: %1 x %2",current_region.region,current_region.width,current_region.height);
if(!current_region.width&&component.initialConfig&&component.initialConfig.width){Apromore.Log.debug("resizing width of region %0: %1",current_region.region,component.initialConfig.width);
current_region.setWidth(component.initialConfig.width)
}if(component.initialConfig&&component.initialConfig.height){Apromore.Log.debug("resizing height of region %0: %1",current_region.region,component.initialConfig.height);
var current_height=current_region.height||0;
current_region.height=component.initialConfig.height+current_height;
current_region.setHeight(component.initialConfig.height+current_height)
}if(typeof title=="string"){current_region.setTitle(title)
}current_region.ownerCt.doLayout();
current_region.show();
if(Ext.isMac){this.resizeFix()
}return current_region
}return null
},getAvailablePlugins:function(){var curAvailablePlugins=this.availablePlugins.clone();
curAvailablePlugins.each(function(plugin){if(this.activatedPlugins.find(function(loadedPlugin){return loadedPlugin.type==this.name
}.bind(plugin))){plugin.engaged=true
}else{plugin.engaged=false
}}.bind(this));
return curAvailablePlugins
},_activatePlugins:function(){var me=this;
var newPlugins=[];
var facade=this._getPluginFacade();
this.availablePlugins.each(function(value){Apromore.Log.debug("Initializing plugin '%0'",value.name);
try{var className=eval(value.name);
if(className){var plugin=new className(facade,value);
plugin.type=value.name;
plugin.engaged=true;
newPlugins.push(plugin)
}}catch(e){Apromore.Log.warn("Plugin %0 is not available",value.name);
Apromore.Log.error("Error: "+e.message)
}});
newPlugins.each(function(value){if(value.registryChanged){value.registryChanged(me.pluginsData)
}});
this.activatedPlugins=newPlugins;
if(Ext.isMac){this.resizeFix()
}},_getContainer:function(){return document.getElementById(this.id)
},_createEditor:function(preventFitDelay){this.editor=new Apromore.Editor({width:Apromore.CONFIG.CANVAS_WIDTH,height:Apromore.CONFIG.CANVAS_HEIGHT,id:Apromore.Utils.provideId(),parentNode:this._getContainer(),preventFitDelay:preventFitDelay})
},getEditor:function(){return this.editor
},getSimulationDrawer:function(){return this.layout_regions.east
},_getPluginFacade:function(){if(!(this._pluginFacade)){this._pluginFacade=(function(){return{getAvailablePlugins:this.getAvailablePlugins.bind(this),offer:this.offer.bind(this),getEditor:this.getEditor.bind(this),getSimulationDrawer:this.getSimulationDrawer.bind(this),useSimulationPanel:this.useSimulationPanel,getXML:this.getXML.bind(this),getSVG:this.getSVG.bind(this),addToRegion:this.addToRegion.bind(this)}
}.bind(this)())
}return this._pluginFacade
},getXML:function(){return this.getEditor().getXML()
},getSVG:function(){return this.getEditor().getSVG()
},importXML:function(xml,callback){this.getEditor().importXML(xml,callback)
},offer:function(pluginData){if(!(this.pluginsData.findIndex(function(plugin){return plugin.name===pluginData.name
})>=0)){if(this.enabledPlugins&&!this.enabledPlugins.includes(pluginData.name)){pluginData.isEnabled=function(){return false
}}this.pluginsData.push(pluginData)
}},resizeFix:function(){if(!this._resizeFixTimeout){this._resizeFixTimeout=window.setTimeout(function(){window.resizeBy(1,1);
window.resizeBy(-1,-1);
this._resizefixTimeout=null
},100)
}},_loadPlugins:function(){if(Apromore.CONFIG.PLUGINS_ENABLED){this._loadPluginData();
this._activatePlugins()
}else{Apromore.Log.warn("Ignoring plugins, loading Core only.")
}},_loadPluginData:function(){var me=this;
var source=Apromore.CONFIG.PLUGINS_CONFIG;
Apromore.Log.debug("Loading plugin configuration from '%0'.",source);
new Ajax.Request(source,{asynchronous:false,method:"get",onSuccess:function(result){Apromore.Log.info("Plugin configuration file loaded.");
var resultXml=result.responseXML;
console.log("Plugin list:",resultXml);
var globalProperties=[];
var preferences=$A(resultXml.getElementsByTagName("properties"));
preferences.each(function(p){var props=$A(p.childNodes);
props.each(function(prop){var property=new Hash();
var attributes=$A(prop.attributes);
attributes.each(function(attr){property[attr.nodeName]=attr.nodeValue
});
if(attributes.length>0){globalProperties.push(property)
}})
});
var plugin=resultXml.getElementsByTagName("plugin");
$A(plugin).each(function(node){var pluginData=new Hash();
$A(node.attributes).each(function(attr){pluginData[attr.nodeName]=attr.nodeValue
});
if(!pluginData.name){Apromore.Log.error("A plugin is not providing a name. Ignoring this plugin.");
return
}if(!pluginData.source){Apromore.Log.error("Plugin with name '%0' doesn't provide a source attribute.",pluginData.name);
return
}var propertyNodes=node.getElementsByTagName("property");
var properties=[];
$A(propertyNodes).each(function(prop){var property=new Hash();
var attributes=$A(prop.attributes);
attributes.each(function(attr){property[attr.nodeName]=attr.nodeValue
});
if(attributes.length>0){properties.push(property)
}});
properties=properties.concat(globalProperties);
pluginData.properties=properties;
var requireNodes=node.getElementsByTagName("requires");
var requires;
$A(requireNodes).each(function(req){var namespace=$A(req.attributes).find(function(attr){return attr.name=="namespace"
});
if(namespace&&namespace.nodeValue){if(!requires){requires={namespaces:[]}
}requires.namespaces.push(namespace.nodeValue)
}});
if(requires){pluginData.requires=requires
}var notUsesInNodes=node.getElementsByTagName("notUsesIn");
var notUsesIn;
$A(notUsesInNodes).each(function(not){var namespace=$A(not.attributes).find(function(attr){return attr.name=="namespace"
});
if(namespace&&namespace.nodeValue){if(!notUsesIn){notUsesIn={namespaces:[]}
}notUsesIn.namespaces.push(namespace.nodeValue)
}});
if(notUsesIn){pluginData.notUsesIn=notUsesIn
}var url=Apromore.PATH+Apromore.CONFIG.PLUGINS_FOLDER+pluginData.source;
Apromore.Log.debug("Requiring '%0'",url);
Apromore.Log.info("Plugin '%0' successfully loaded.",pluginData.name);
me.availablePlugins.push(pluginData)
})
},onFailure:function(){Apromore.Log.error("Plugin configuration file not available.")
}})
},toggleFullScreen:function(){if(!document.fullscreenElement){document.documentElement.requestFullscreen()
}else{if(document.exitFullscreen){document.exitFullscreen()
}}}};
Apromore.EditorApp=Clazz.extend(Apromore.EditorApp);
if(!Apromore){var Apromore={}
}Apromore.Editor={construct:function(options){this.actualEditor=undefined;
this.preventFitDelay=options.preventFitDelay;
if(!(options&&options.width&&options.height)){Apromore.Log.fatal("The editor is missing mandatory parameters options.width and options.height.");
return
}this.className="Apromore_Editor";
this.rootNode=Apromore.Utils.graft("http://www.w3.org/1999/xhtml",options.parentNode,["div",{id:options.id,width:options.width,height:options.height}]);
this.rootNode.classList.add(this.className)
},getScrollNode:function(){return Ext.get(this.rootNode).parent("div{overflow=auto}",true)
},updateUndoRedo:function(){try{var undo=jq("#ap-id-editor-undo-btn");
var redo=jq("#ap-id-editor-redo-btn");
if(this.canUndo()){undo.removeClass("disabled")
}else{undo.addClass("disabled")
}if(this.canRedo()){redo.removeClass("disabled")
}else{redo.addClass("disabled")
}}catch(e){console.log("Unexpected error occurred when update button status")
}},attachEditor:function(editor){var me=this;
this.actualEditor=editor;
this.updateUndoRedo();
this.addCommandStackChangeListener(function(){me.updateUndoRedo()
})
},getSVGContainer:function(){return $("div.Apromore_Editor div.bjs-container div.djs-container svg")[0]
},getSVGViewport:function(){return $("div.Apromore_Editor div.bjs-container div.djs-container svg g.viewport")[0]
},getSourceNodeId:function(sequenceFlowId){var foundId;
var elements=this.actualEditor.getDefinitions().rootElements[0].flowElements;
elements.forEach(function(element){if(!foundId&&element.$type=="bpmn:SequenceFlow"&&element.id==sequenceFlowId){foundId=element.sourceRef.id
}});
return foundId
},getTargetNodeId:function(sequenceFlowId){var foundId;
var flowElements=this.actualEditor.getDefinitions().rootElements[0].flowElements;
flowElements.forEach(function(element){if(!foundId&&element.$type=="bpmn:SequenceFlow"&&element.id==sequenceFlowId){foundId=element.targetRef.id
}});
return foundId
},getIncomingFlowId:function(nodeId){var foundId;
var flowElements=this.actualEditor.getDefinitions().rootElements[0].flowElements;
flowElements.forEach(function(element){if(!foundId&&element.$type=="bpmn:SequenceFlow"&&element.targetRef.id==nodeId){foundId=element.id
}});
return foundId
},getOutgoingFlowId:function(nodeId){var foundId;
var elements=this.actualEditor.getDefinitions().rootElements[0].flowElements;
elements.forEach(function(element){if(!foundId&&element.$type=="bpmn:SequenceFlow"&&element.sourceRef.id==nodeId){foundId=element.id
}});
return foundId
},toString:function(){return"EditorWrapper "+this.id
},importXML:function(xml,callback){var editor=this.actualEditor;
this.actualEditor.importXML(xml,function(err){if(err){window.alert("Failed to import BPMN diagram. Please make sure it's a valid BPMN 2.0 diagram.");
return
}var eventBus=editor.get("eventBus");
var elementRegistry=editor.get("elementRegistry");
var connections=elementRegistry.filter(function(e){return e.waypoints
});
try{var connectionDocking=editor.get("connectionDocking");
connections.forEach(function(connection){connection.waypoints=connectionDocking.getCroppedWaypoints(connection)
})
}catch(e){console.log("skip connectionDocking error")
}eventBus.fire("elements.changed",{elements:connections});
if(this.preventFitDelay){this.zoomFitToModel();
callback()
}else{callback();
var me=this;
setTimeout(function(){me.zoomFitToModel()
},500)
}}.bind(this))
},getXML:function(){var bpmnXML;
this.actualEditor.saveXML({format:true},function(err,xml){bpmnXML=xml
});
return bpmnXML
},getSVG:function(){var bpmnSVG;
this.actualEditor.saveSVG(function(err,svg){bpmnSVG=svg
});
return bpmnSVG
},zoomFitToModel:function(){if(this.actualEditor){var canvas=this.actualEditor.get("canvas");
canvas.viewbox(false);
canvas.zoom("fit-viewport","auto")
}},zoomIn:function(){if(this.actualEditor){var canvas=this.actualEditor.get("canvas");
canvas.zoom(canvas.zoom()*1.1)
}},zoomOut:function(){if(this.actualEditor){var canvas=this.actualEditor.get("canvas");
canvas.zoom(canvas.zoom()*0.9)
}},zoomDefault:function(){if(this.actualEditor){var canvas=this.actualEditor.get("canvas");
canvas.zoom(1)
}},createShape:function(type,x,y,w,h){var modelling=this.actualEditor.get("modeling");
var parent=this.actualEditor.get("canvas").getRootElement();
var shape=modelling.createShape({type:type,width:w,height:h},{x:x,y:y},parent);
return shape.id
},updateProperties:function(elementId,properties){var modelling=this.actualEditor.get("modeling");
var registry=this.actualEditor.get("elementRegistry");
modelling.updateProperties(registry.get(elementId),properties)
},createSequenceFlow:function(source,target,attrs){var attrs2={};
Object.assign(attrs2,{type:"bpmn:SequenceFlow"});
if(attrs.waypoints){Object.assign(attrs2,{waypoints:attrs.waypoints})
}var modelling=this.actualEditor.get("modeling");
var registry=this.actualEditor.get("elementRegistry");
var flow=modelling.connect(registry.get(source),registry.get(target),attrs2);
return flow.id
},createAssociation:function(source,target,attrs){var attrs2={};
Object.assign(attrs2,{type:"bpmn:Association"});
if(attrs.waypoints){Object.assign(attrs2,{waypoints:attrs.waypoints})
}var modelling=this.actualEditor.get("modeling");
var registry=this.actualEditor.get("elementRegistry");
var assoc=Object.assign(assoc,modelling.connect(registry.get(source),registry.get(target),attrs2));
return assoc.id
},highlight:function(elementId){var self=this;
var element=self.actualEditor.get("elementRegistry").get(elementId);
var modelling=self.actualEditor.get("modeling");
modelling.setColor([element],{stroke:"red"})
},colorElements:function(elementIds,color){var elements=[];
var registry=this.actualEditor.get("elementRegistry");
elementIds.forEach(function(elementId){elements.push(registry.get(elementId))
});
var modelling=this.actualEditor.get("modeling");
modelling.setColor(elements,{stroke:color})
},colorElement:function(elementId,color){var modelling=this.actualEditor.get("modeling");
var element=this.actualEditor.get("elementRegistry").get(elementId);
modelling.setColor([element],{stroke:color})
},fillColor:function(elementId,color){var modelling=this.actualEditor.get("modeling");
var element=this.actualEditor.get("elementRegistry").get(elementId);
modelling.setColor([element],{fill:color})
},greyOut:function(elementIds){var elementRegistry=this.actualEditor.get("elementRegistry");
var self=this;
elementIds.forEach(function(id){console.log("_elements",elementRegistry._elements);
var gfx=elementRegistry.getGraphics(id);
var visual=gfx.children[0];
visual.setAttributeNS(null,"style","opacity: 0.25")
})
},normalizeAll:function(){var registry=this.actualEditor.get("elementRegistry");
var modelling=this.actualEditor.get("modeling");
modelling.setColor(registry.getAll(),{stroke:"black"})
},removeShapes:function(shapeIds){var registry=this.actualEditor.get("elementRegistry");
var modelling=this.actualEditor.get("modeling");
console.log(shapeIds);
var shapes=[];
shapeIds.forEach(function(shapeId){shapes.push(registry.get(shapeId))
});
modelling.removeElements(shapes)
},getAllElementIds:function(){var ids=[];
var elementRegistry=this.actualEditor.get("elementRegistry");
elementRegistry.getAll().forEach(function(element){ids.push(element.id)
});
return ids
},shapeCenter:function(shapeId){var position={};
var registry=this.actualEditor.get("elementRegistry");
var shape=registry.get(shapeId);
position.x=(shape.x+shape.width/2);
position.y=(shape.y+shape.height/2);
return position
},clear:function(){this.actualEditor.clear()
},registerActionHandler:function(handlerName,handler){var commandStack=this.actualEditor.get("commandStack");
commandStack.registerHandler(handlerName,handler)
},executeActionHandler:function(handlerName,context){var commandStack=this.actualEditor.get("commandStack");
commandStack.execute(handlerName,context)
},getCenter:function(shapeId){var shape=this.actualEditor.get("elementRegistry").get(shapeId);
return{x:shape.x+(shape.width||0)/2,y:shape.y+(shape.height||0)/2}
},centerElement:function(elementId){var bbox=elementRegistry.get(elementId);
var currentViewbox=canvas.viewbox();
var elementMid={x:bbox.x+bbox.width/2,y:bbox.y+bbox.height/2};
canvas.viewbox({x:elementMid.x-currentViewbox.width/2,y:elementMid.y-currentViewbox.height/2,width:currentViewbox.width,height:currentViewbox.height})
},_getActionStack:function(){return this.actualEditor.get("commandStack")._stack
},_getCurrentStackIndex:function(){return this.actualEditor.get("commandStack")._stackIdx
},_getBaseActions:function(){var actions=this._getActionStack();
var stackIndex=this._getCurrentStackIndex();
var baseActionIndexes=[];
for(var i=0;
i<=stackIndex;
i++){if(i==0||(actions[i].id!=actions[i-1].id)){baseActionIndexes.push(i)
}}return baseActionIndexes
},undo:function(){this.actualEditor.get("commandStack").undo()
},undoSeriesUntil:function(actionName){var actions=this._getActionStack();
var baseActions=this._getBaseActions();
var baseActionNum=0;
for(var i=baseActions.length-1;
i>=0;
i--){if(actions[baseActions[i]].command==actionName){baseActionNum=baseActions.length-i;
break
}}console.log("baseActionNum",baseActionNum);
while(baseActionNum>0){this.undo();
baseActionNum--
}},canUndo:function(){if(!this.actualEditor){return false
}else{return this.actualEditor.get("commandStack").canUndo()
}},redo:function(){this.actualEditor.get("commandStack").redo()
},canRedo:function(){if(!this.actualEditor){return false
}else{return this.actualEditor.get("commandStack").canRedo()
}},getLastBaseAction:function(){var actions=this._getActionStack();
var baseActions=this._getBaseActions();
if(baseActions.length>0){return actions[baseActions[baseActions.length-1]].command
}else{return""
}},getNextBaseActionExcluding:function(excludingActions){var actions=this._getActionStack();
var baseActionIndexes=this._getBaseActions();
if(baseActionIndexes.length>=2){for(var i=baseActionIndexes.length-2;
i>=0;
i--){if(excludingActions.indexOf(actions[baseActionIndexes[i]].command)<0){return actions[baseActionIndexes[i]].command
}}}return""
},addCommandStackChangeListener:function(callback){this.actualEditor.on("commandStack.changed",callback)
},addEventBusListener:function(eventCode,callback){this.actualEditor.get("eventBus").on(eventCode,callback)
}};
Apromore.Editor=Clazz.extend(Apromore.Editor);
if(!Apromore){var Apromore={}
}if(!Apromore.Plugins){Apromore.Plugins={}
}Apromore.Plugins.ApromoreSave=Clazz.extend({facade:undefined,changeSymbol:"*",construct:function(facade){this.facade=facade;
this.facade.offer({name:window.Apromore.I18N.Save.save,btnId:"ap-id-editor-save-btn",functionality:this.save.bind(this,false),group:window.Apromore.I18N.Save.group,icon:Apromore.PATH+"images/ap/save.svg",description:window.Apromore.I18N.Save.saveDesc,index:1,minShape:0,maxShape:0,keyCodes:[{metaKeys:[Apromore.CONFIG.META_KEY_META_CTRL],keyCode:83,keyAction:Apromore.CONFIG.KEY_ACTION_UP}]});
this.facade.offer({name:window.Apromore.I18N.Save.saveAs,functionality:this.save.bind(this,true),group:window.Apromore.I18N.Save.group,icon:Apromore.PATH+"images/ap/save-as.svg",description:window.Apromore.I18N.Save.saveAsDesc,btnId:"ap-id-editor-save-as-btn",index:2,minShape:0,maxShape:0})
},updateTitle:function(){},onUnLoad:function(){},save:function(forceNew,event){if(this.saving){return false
}this.saving=true;
var xml=this.facade.getXML();
var svg=this.facade.getSVG();
if(forceNew){if(Apromore.Plugins.ApromoreSave.apromoreSaveAs){Apromore.Plugins.ApromoreSave.apromoreSaveAs(xml,svg)
}else{alert("Apromore Save As method is missing!")
}}else{if(Apromore.Plugins.ApromoreSave.apromoreSave){Apromore.Plugins.ApromoreSave.apromoreSave(xml,svg)
}else{alert("Apromore Save method is missing!")
}}this.saving=false;
return true
}});
if(!Apromore.Plugins){Apromore.Plugins=new Object()
}Apromore.Plugins.Export=Clazz.extend({facade:undefined,construct:function(facade){this.facade=facade;
this.facade.offer({btnId:"ap-id-editor-export-svg-btn",name:window.Apromore.I18N.File.svg,functionality:this.exportSVG.bind(this),group:window.Apromore.I18N.File.group,icon:Apromore.PATH+"images/ap/export-svg.svg",description:window.Apromore.I18N.File.svgDesc,index:3,minShape:0,maxShape:0});
this.facade.offer({btnId:"ap-id-editor-export-bpmn-btn",name:window.Apromore.I18N.File.bpmn,functionality:this.exportBPMN.bind(this),group:window.Apromore.I18N.File.group,icon:Apromore.PATH+"images/ap/export-bpmn.svg",description:window.Apromore.I18N.File.bpmnDesc,index:4,minShape:0,maxShape:0})
},exportSVG:function(){var svg=this.facade.getSVG();
var hiddenElement=document.createElement("a");
hiddenElement.href="data:application/bpmn20-xml;charset=UTF-8,"+encodeURIComponent(svg);
hiddenElement.target="_blank";
hiddenElement.download="diagram.svg";
hiddenElement.click()
},exportBPMN:function(){var xml=this.facade.getXML();
var hiddenElement=document.createElement("a");
hiddenElement.href="data:application/bpmn20-xml;charset=UTF-8,"+encodeURIComponent(xml);
hiddenElement.target="_blank";
hiddenElement.download="diagram.bpmn";
hiddenElement.click()
}});
if(!Apromore.Plugins){Apromore.Plugins=new Object()
}const logo='<g transform="translate(${xx yy}) scale(0.2 0.2)"><path fill="#4E4E4F" d="M206.5,36.4v52.9h-11.4v-7.6c-4,5.6-10.3,9-18.5,9c-14.4,0-26.3-12.2-26.3-27.8S162.2,35,176.6,35 c8.3,0,14.5,3.4,18.5,8.9v-7.5H206.5z M195.1,62.8c0-9.8-7.2-16.9-16.7-16.9s-16.7,7.1-16.7,16.9s7.2,16.9,16.7,16.9 S195.1,72.7,195.1,62.8z"/><path fill="#4E4E4F" d="M276.1,62.8c0,15.8-12,27.8-26.4,27.8c-8.2,0-14.5-3.4-18.5-8.9v28.7h-11.4V36.4h11.4V44c4-5.6,10.3-9,18.5-9 C264.2,35,276.1,47.2,276.1,62.8z M264.7,62.8c0-9.8-7.2-16.9-16.7-16.9s-16.7,7.1-16.7,16.9s7.2,16.9,16.7,16.9 C257.5,79.8,264.7,72.7,264.7,62.8z"/><path fill="#4E4E4F" d="M314.2,35.4v12.4c-7.5-0.5-16.3,3-16.3,14.8v26.7h-11.4V36.4H298v8.9C301.1,38.1,307.6,35.4,314.2,35.4z"/><path fill="#4E4E4F" d="M317.3,62.8c-0.1-15.4,12.4-28,27.8-28c15.4-0.1,28,12.4,28,27.8c0.1,15.4-12.4,28-27.8,28c0,0-0.1,0-0.1,0 c-15.2,0.2-27.8-12-27.9-27.3C317.3,63.2,317.3,63,317.3,62.8z M361.8,62.8c0.3-9.1-6.8-16.8-15.9-17.1s-16.8,6.8-17.1,15.9 c0,0.4,0,0.8,0,1.1c0,9.6,7.2,16.7,16.5,16.7c9,0.1,16.4-7,16.5-16C361.8,63.3,361.8,63.1,361.8,62.8z"/><path fill="#4E4E4F" d="M461.3,56.5v32.8h-11.4V57.2c0-7.4-4-11.5-10.3-11.5c-6.8,0-11.5,4.3-11.5,14v29.6h-11.4V57.2 c0-7.4-3.7-11.5-10-11.5c-6.5,0-11.7,4.3-11.7,14v29.6h-11.4V36.4H395v6.3c3.4-5.2,8.6-7.7,15.1-7.7c6.9,0,12,3.1,15,8.4 c3.5-5.5,9.1-8.4,16.2-8.4C453.3,35,461.3,43.3,461.3,56.5z"/><path fill="#4E4E4F" d="M470.6,62.8c-0.1-15.4,12.4-28,27.8-28c15.4-0.1,28,12.4,28,27.8s-12.4,28-27.8,28c0,0-0.1,0-0.1,0 c-15.3,0.2-27.8-12-27.9-27.3C470.6,63.2,470.6,63,470.6,62.8z M515.1,62.8c0.3-9.1-6.8-16.8-15.9-17.1s-16.8,6.8-17.1,15.9 c0,0.4,0,0.8,0,1.1c-0.3,9,6.8,16.5,15.8,16.7c0.2,0,0.5,0,0.7,0C507.9,79.5,515.1,72.5,515.1,62.8z"/><path fill="#4E4E4F" d="M564.6,35.4v12.4c-7.5-0.5-16.3,3-16.3,14.8v26.7h-11.4V36.4h11.4v8.9C551.5,38.1,557.9,35.4,564.6,35.4z"/><path fill="#4E4E4F" d="M596.5,80.2c6.3,0,11.1-2.9,13.6-6.6l9.4,5.5c-4.9,7.2-12.8,11.5-23.2,11.5c-17.5,0-28.8-12-28.8-27.8 c0-15.7,11.4-27.8,27.9-27.8c15.8,0,26.6,12.7,26.6,27.9c0,1.6-0.2,3.2-0.4,4.8h-42.1C581.4,76,588.1,80.2,596.5,80.2z M610.6,58.4 c-1.6-9.1-8.3-13-15.1-13c-8.6,0-14.4,5.1-16,13H610.6z"/><path fill="#85C7E2" d="M64.5,108.2v-3.8c22.4,0,40.6-18.2,40.7-40.6h3.8C108.9,88.3,89,108.2,64.5,108.2z"/><circle fill="#F26424" cx="64.4" cy="108" r="13.9"/><path fill="#C7E1ED" d="M23.8,63.8h-3.8c0-24.5,19.9-44.4,44.4-44.4v3.8C42,23.2,23.9,41.4,23.8,63.8z"/><circle fill="#BC3C51" cx="64.3" cy="63.8" r="20"/><circle fill="#FAA624" cx="64.4" cy="19.6" r="13.9"/><circle fill="#85C7E2" cx="108.6" cy="63.8" r="13.9"/><circle fill="#C7E1ED" cx="20.2" cy="63.8" r="13.9"/><path fill="#F26424" d="M64.5,106.3L64.5,106.3c0-1-0.8-1.8-1.8-1.9c-14-0.6-26.6-8.4-33.5-20.6c-0.5-0.9-1.6-1.2-2.5-0.7l0,0 c-0.9,0.5-1.3,1.6-0.8,2.6c0,0,0,0,0,0c7.5,13.3,21.4,21.7,36.6,22.5C63.5,108.2,64.4,107.4,64.5,106.3 C64.5,106.3,64.5,106.3,64.5,106.3z"/><path fill="#FAA624" d="M64.5,21.3L64.5,21.3c0,1,0.8,1.8,1.8,1.9c13.5,0.6,25.8,7.9,32.9,19.5c0.5,0.8,1.6,1.1,2.5,0.7l0,0 c0.9-0.5,1.3-1.6,0.8-2.6c0,0,0-0.1-0.1-0.1C94.7,28,81.2,20.1,66.5,19.4C65.4,19.4,64.5,20.2,64.5,21.3 C64.5,21.2,64.5,21.3,64.5,21.3z"/></g>';
Apromore.Plugins.File=Clazz.extend({facade:undefined,construct:function(facade){this.facade=facade;
this.facade.offer({btnId:"ap-id-editor-export-pdf-btn",name:window.Apromore.I18N.File.pdf,functionality:this.exportPDF.bind(this),group:window.Apromore.I18N.File.group,icon:Apromore.PATH+"images/ap/export-pdf.svg",description:window.Apromore.I18N.File.pdfDesc,index:5,minShape:0,maxShape:0})
},exportPDF:function(){var myMask=new Ext.LoadMask(Ext.getBody(),{msg:"Please wait..."});
myMask.show();
var resource=location.href;
var svgClone=this.facade.getEditor().getSVG();
var xy=null,width,height;
var viewBoxRegex=/(.*<svg.+?viewBox=")(.+?)(".+)/m;
function viewBoxReplacer(match,p1,p2,p3){xy=p2.split(" ").map(function(x){return parseInt(x)
});
width=xy[2]+40;
height=xy[3]+60;
return p1+[xy[0]-20,xy[1]-40,width,height].join(" ")+p3
}var whRegex=/(.*<svg.+?width=")(.+?)(".+?height=")(.+?)(".+)/m;
function whReplacer(match,p1,p2,p3,p4,p5){return p1+width+p3+height+p5
}try{svgClone=svgClone.replace(viewBoxRegex,viewBoxReplacer);
if(xy){svgClone=svgClone.replace(whRegex,whReplacer);
svgClone=svgClone.replace("</svg>",logo.replace("${xx yy}",(xy[0])+" "+(xy[1]-20))+"</svg>")
}}catch(e){}var xhr=new XMLHttpRequest();
var params="resource="+resource+"&data="+svgClone+"&format=pdf";
xhr.open("POST",Apromore.CONFIG.PDF_EXPORT_URL);
xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
xhr.responseType="blob";
xhr.onreadystatechange=function(){if(xhr.readyState==4&&xhr.status==200){myMask.hide();
var hiddenElement=document.createElement("a");
hiddenElement.href=window.URL.createObjectURL(xhr.response);
hiddenElement.target="_blank";
hiddenElement.download="diagram.pdf";
hiddenElement.click();
window.URL.revokeObjectURL(hiddenElement.href)
}};
xhr.onerror=function(){myMask.hide();
Ext.Msg.alert(window.Apromore.I18N.Apromore.title,window.Apromore.I18N.File.genPDFFailed)
};
xhr.send(params)
}});
if(!Apromore.Plugins){Apromore.Plugins=new Object()
}Apromore.Plugins.Share=Clazz.extend({facade:undefined,construct:function(facade){this.facade=facade;
this.facade.offer({btnId:"ap-id-editor-share-btn",name:Apromore.I18N.Share.share,functionality:this.share.bind(this),group:Apromore.I18N.Share.group,icon:Apromore.PATH+"images/ap/share.svg",description:Apromore.I18N.Share.shareDesc,index:1,minShape:0,maxShape:0})
},share:function(factor){if(Apromore.Plugins.Share.shareExt){Apromore.Plugins.Share.shareExt()
}},});
if(!Apromore.Plugins){Apromore.Plugins=new Object()
}Apromore.Plugins.SimulationPanel=Clazz.extend({facade:undefined,construct:function(facade){this.facade=facade;
this.facade.offer({btnId:"ap-id-editor-simulation-btn",name:window.Apromore.I18N.SimulationPanel.toggleSimulationDrawer,functionality:this.toggleSimulationDrawer.bind(this),group:window.Apromore.I18N.SimulationPanel.group,description:window.Apromore.I18N.SimulationPanel.toggleSimulationDrawerDesc,index:1,minShape:0,maxShape:0,icon:Apromore.PATH+"images/ap/simulate-model.svg",isEnabled:function(){return facade.useSimulationPanel
}.bind(this),})
},toggleSimulationDrawer:function(){this.facade.getSimulationDrawer().toggleCollapse(true)
}});
if(!Apromore.Plugins){Apromore.Plugins=new Object()
}Apromore.Plugins.Toolbar=Clazz.extend({facade:undefined,plugs:[],construct:function(facade,ownPluginData){this.facade=facade;
this.groupIndex=new Hash();
ownPluginData.properties.each((function(value){if(value.group&&value.index!=undefined){this.groupIndex[value.group]=value.index
}}).bind(this));
Ext.QuickTips.init();
this.buttons=[]
},onButtonUpdate:function(event){var button=this.buttons.find(function(button){return button.id===event.id
});
if(event.pressed!==undefined){button.buttonInstance.toggle(event.pressed)
}},registryChanged:function(pluginsData){var newPlugs=pluginsData.sortBy((function(value){return((this.groupIndex[value.group]!=undefined?this.groupIndex[value.group]:"")+value.group+""+value.index).toLowerCase()
}).bind(this));
var plugs=$A(newPlugs).findAll(function(plugin){return !this.plugs.include(plugin)&&(!plugin.target||plugin.target===Apromore.Plugins.Toolbar)
}.bind(this));
if(plugs.length<1){return
}this.buttons=[];
Apromore.Log.trace("Creating a toolbar.");
if(!this.toolbar){this.toolbar=new Ext.ux.SlicedToolbar({height:24});
var region=this.facade.addToRegion("north",this.toolbar,"Toolbar")
}var currentGroupsName=this.plugs.last()?this.plugs.last().group:plugs[0].group;
var currentGroupsDropDownButton={};
plugs.each((function(plugin){if(!plugin.name){return
}this.plugs.push(plugin);
if(currentGroupsName!=plugin.group){this.toolbar.add("-");
currentGroupsName=plugin.group;
currentGroupsDropDownButton={}
}if(plugin.dropDownGroupIcon){var splitButton=currentGroupsDropDownButton[plugin.dropDownGroupIcon];
if(splitButton===undefined){splitButton=currentGroupsDropDownButton[plugin.dropDownGroupIcon]=new Ext.Toolbar.SplitButton({cls:"x-btn-icon",icon:plugin.dropDownGroupIcon,menu:new Ext.menu.Menu({items:[]}),listeners:{click:function(button,event){if(!button.menu.isVisible()&&!button.ignoreNextClick){button.showMenu()
}else{button.hideMenu()
}}}});
this.toolbar.add(splitButton)
}var buttonCfg={icon:plugin.icon,text:plugin.name,itemId:plugin.id,handler:plugin.toggle?undefined:plugin.functionality,checkHandler:plugin.toggle?plugin.functionality:undefined,listeners:{render:function(item){if(plugin.description){new Ext.ToolTip({target:item.getEl(),title:plugin.description})
}}}};
if(plugin.toggle){var button=new Ext.menu.CheckItem(buttonCfg)
}else{var button=new Ext.menu.Item(buttonCfg)
}splitButton.menu.add(button)
}else{var options={icon:plugin.icon,cls:"x-btn-icon",itemId:plugin.id,tooltip:plugin.description,tooltipType:"title",handler:plugin.toggle?null:plugin.functionality,enableToggle:plugin.toggle,toggleHandler:plugin.toggle?plugin.functionality:null};
if(plugin.btnId){options.id=plugin.btnId
}var button=new Ext.Toolbar.Button(options);
this.toolbar.add(button);
button.getEl().onclick=function(){this.blur()
}}plugin.buttonInstance=button;
this.buttons.push(plugin)
}).bind(this));
this.enableButtons([]);
this.toolbar.calcSlices();
window.addEventListener("resize",function(event){this.toolbar.calcSlices()
}.bind(this),false);
window.addEventListener("onresize",function(event){this.toolbar.calcSlices()
}.bind(this),false)
},onSelectionChanged:function(event){this.enableButtons(event.elements)
},enableButtons:function(elements){this.buttons.each((function(pluginButton){pluginButton.buttonInstance.enable();
if(pluginButton.minShape&&pluginButton.minShape>elements.length){pluginButton.buttonInstance.disable()
}if(pluginButton.maxShape&&pluginButton.maxShape<elements.length){pluginButton.buttonInstance.disable()
}if(pluginButton.isEnabled&&!pluginButton.isEnabled(pluginButton.buttonInstance)){pluginButton.buttonInstance.disable()
}}).bind(this))
}});
Ext.ns("Ext.ux");
Ext.ux.SlicedToolbar=Ext.extend(Ext.Toolbar,{currentSlice:0,iconStandardWidth:22,seperatorStandardWidth:2,toolbarStandardPadding:2,initComponent:function(){Ext.apply(this,{});
Ext.ux.SlicedToolbar.superclass.initComponent.apply(this,arguments)
},onRender:function(){Ext.ux.SlicedToolbar.superclass.onRender.apply(this,arguments)
},onResize:function(){Ext.ux.SlicedToolbar.superclass.onResize.apply(this,arguments)
},calcSlices:function(){var slice=0;
this.sliceMap={};
var sliceWidth=0;
var toolbarWidth=this.getEl().getWidth();
this.items.getRange().each(function(item,index){if(item.helperItem){item.destroy();
return
}var itemWidth=item.getEl().getWidth();
if(sliceWidth+itemWidth+5*this.iconStandardWidth>toolbarWidth){var itemIndex=this.items.indexOf(item);
this.insertSlicingButton("next",slice,itemIndex);
if(slice!==0){this.insertSlicingButton("prev",slice,itemIndex)
}this.insertSlicingSeperator(slice,itemIndex);
slice+=1;
sliceWidth=0
}this.sliceMap[item.id]=slice;
sliceWidth+=itemWidth
}.bind(this));
if(slice>0){this.insertSlicingSeperator(slice,this.items.getCount()+1);
this.insertSlicingButton("prev",slice,this.items.getCount()+1);
var spacer=new Ext.Toolbar.Spacer();
this.insertSlicedHelperButton(spacer,slice,this.items.getCount()+1);
Ext.get(spacer.id).setWidth(this.iconStandardWidth)
}this.maxSlice=slice;
this.setCurrentSlice(this.currentSlice)
},insertSlicedButton:function(button,slice,index){this.insertButton(index,button);
this.sliceMap[button.id]=slice
},insertSlicedHelperButton:function(button,slice,index){button.helperItem=true;
this.insertSlicedButton(button,slice,index)
},insertSlicingSeperator:function(slice,index){this.insertSlicedHelperButton(new Ext.Toolbar.Fill(),slice,index)
},insertSlicingButton:function(type,slice,index){var nextHandler=function(){this.setCurrentSlice(this.currentSlice+1)
}.bind(this);
var prevHandler=function(){this.setCurrentSlice(this.currentSlice-1)
}.bind(this);
var button=new Ext.Toolbar.Button({cls:"x-btn-icon",icon:Apromore.CONFIG.ROOT_PATH+"images/toolbar_"+type+".png",handler:(type==="next")?nextHandler:prevHandler});
this.insertSlicedHelperButton(button,slice,index)
},setCurrentSlice:function(slice){if(slice>this.maxSlice||slice<0){return
}this.currentSlice=slice;
this.items.getRange().each(function(item){item.setVisible(slice===this.sliceMap[item.id])
}.bind(this))
}});
if(!Apromore.Plugins){Apromore.Plugins=new Object()
}Apromore.Plugins.Undo=Clazz.extend({facade:undefined,undoStack:[],redoStack:[],construct:function(facade){this.facade=facade;
this.facade.offer({name:window.Apromore.I18N.Undo.undo,description:window.Apromore.I18N.Undo.undoDesc,icon:Apromore.PATH+"images/ap/undo.svg",btnId:"ap-id-editor-undo-btn",keyCodes:[{metaKeys:[Apromore.CONFIG.META_KEY_META_CTRL],keyCode:90,keyAction:Apromore.CONFIG.KEY_ACTION_DOWN}],functionality:this.doUndo.bind(this),group:window.Apromore.I18N.Undo.group,isEnabled:function(){return true
}.bind(this),index:0});
this.facade.offer({name:window.Apromore.I18N.Undo.redo,description:window.Apromore.I18N.Undo.redoDesc,icon:Apromore.PATH+"images/ap/redo.svg",btnId:"ap-id-editor-redo-btn",keyCodes:[{metaKeys:[Apromore.CONFIG.META_KEY_META_CTRL],keyCode:89,keyAction:Apromore.CONFIG.KEY_ACTION_DOWN}],functionality:this.doRedo.bind(this),group:window.Apromore.I18N.Undo.group,isEnabled:function(){return true
}.bind(this),index:1})
},handleExecuteCommands:function(evt){},doUndo:function(){this.facade.getEditor().undo()
},doRedo:function(){this.facade.getEditor().redo()
}});
if(!Apromore.Plugins){Apromore.Plugins=new Object()
}Apromore.Plugins.View=Clazz.extend({facade:undefined,construct:function(facade){this.facade=facade;
this.facade.offer({name:window.Apromore.I18N.View.zoomIn,functionality:this.zoomIn.bind(this),group:window.Apromore.I18N.View.group,icon:Apromore.PATH+"images/ap/zoom-in.svg",description:window.Apromore.I18N.View.zoomInDesc,index:1,minShape:0,maxShape:0});
this.facade.offer({name:window.Apromore.I18N.View.zoomOut,functionality:this.zoomOut.bind(this),group:window.Apromore.I18N.View.group,icon:Apromore.PATH+"images/ap/zoom-out.svg",description:window.Apromore.I18N.View.zoomOutDesc,index:2,minShape:0,maxShape:0});
this.facade.offer({name:window.Apromore.I18N.View.zoomFitToModel,functionality:this.zoomFitToModel.bind(this),group:window.Apromore.I18N.View.group,icon:Apromore.PATH+"images/ap/zoom-to-fit.svg",description:window.Apromore.I18N.View.zoomFitToModelDesc,index:4,minShape:0,maxShape:0})
},zoomIn:function(factor){this.facade.getEditor().zoomIn()
},zoomOut:function(factor){this.facade.getEditor().zoomOut()
},zoomFitToModel:function(){this.facade.getEditor().zoomFitToModel()
}});

export {Apromore};