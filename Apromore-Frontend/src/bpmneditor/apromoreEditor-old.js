/*
* Steps to convert this bundled javascript source to be used for log animation
* 1. Add the global namespaces Apromore.xxx at the header
* 2. Add export {Apromore} as the last line
* 3. In Apromore.EditorApp constructor: remove window.setTimeout for loading XML
 */

if(!Apromore) var Apromore = {};
if(!Apromore.CONFIG) Apromore.CONFIG = {};
Apromore.CONFIG.EDITOR_PATH = "/editor";
Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX = '/bpmneditor';


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

/**
 * The super class for all classes in Apromore. Adds some OOP feeling to javascript.
 * See article "Object Oriented Super Class Method Calling with JavaScript" on
 * http://truecode.blogspot.com/2006/08/object-oriented-super-class-method.html
 * for a documentation on this. Fairly good article that points out errors in
 * Douglas Crockford's inheritance and super method calling approach.
 * Worth reading.
 * @class Clazz
 */
var Clazz = function() {};

/**
 * Empty constructor.
 * @methodOf Clazz.prototype
 */
Clazz.prototype.construct = function() {};

/**
 * Can be used to build up inheritances of classes.
 * @example
 * var MyClass = Clazz.extend({
 *   construct: function(myParam){
 *     // Do sth.
 *   }
 * });
 * var MySubClass = MyClass.extend({
 *   construct: function(myParam){
 *     // Use this to call constructor of super class
 *     arguments.callee.$.construct.apply(this, arguments);
 *     // Do sth.
 *   }
 * });
 * @param {Object} def The definition of the new class.
 */
Clazz.extend = function(def) {
    var classDef = function() {
        if (arguments[0] !== Clazz) { this.construct.apply(this, arguments); }
    };

    var proto = new this(Clazz);
    var superClass = this.prototype;

    for (var n in def) {
        var item = def[n];
        if (item instanceof Function) item.$ = superClass;
        proto[n] = item;
    }

    classDef.prototype = proto;

    //Give this new class the same static extend method
    classDef.extend = this.extend;
    return classDef;
};/**
 * Copyright (c) 2008
 * Willi Tscheschner
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

if (!Apromore) {
  var Apromore = {};
}

/**
 * @namespace Apromore name space for different utility methods
 * @name Apromore.Utils
*/
Apromore.Utils = {
    // TODO Implement namespace awareness on attribute level.
    /**
     * graft() function
     * Originally by Sean M. Burke from interglacial.com, altered for usage with
     * SVG and namespace (xmlns) support. Be sure you understand xmlns before
     * using this funtion, as it creates all grafted elements in the xmlns
     * provided by you and all element's attribures in default xmlns. If you
     * need to graft elements in a certain xmlns and wish to assign attributes
     * in both that and another xmlns, you will need to do stepwise grafting,
     * adding non-default attributes yourself or you'll have to enhance this
     * function. Latter, I would appreciate: martin�apfelfabrik.de
     * @param {Object} namespace The namespace in which
     *                    elements should be grafted.
     * @param {Object} parent The element that should contain the grafted
     *                    structure after the function returned.
     * @param {Object} t the crafting structure.
     * @param {Object} doc the document in which grafting is performed.
     */
    graft: function (namespace, parent, t, doc) {

        doc = (doc || (parent && parent.ownerDocument) || document);
        var e;
        if (t === undefined) {
            throw "Can't graft an undefined value";
        } else if (t.constructor == String) {
            e = doc.createTextNode(t);
        } else {
            for (var i = 0; i < t.length; i++) {
                if (i === 0 && t[i].constructor == String) {
                    var snared;
                    snared = t[i].match(/^([a-z][a-z0-9]*)\.([^\s\.]+)$/i);
                    if (snared) {
                        e = doc.createElementNS(namespace, snared[1]);
                        e.setAttributeNS(null, 'class', snared[2]);
                        continue;
                    }
                    snared = t[i].match(/^([a-z][a-z0-9]*)$/i);
                    if (snared) {
                        e = doc.createElementNS(namespace, snared[1]);  // but no class
                        continue;
                    }

                    // Otherwise:
                    e = doc.createElementNS(namespace, "span");
                    e.setAttribute(null, "class", "namelessFromLOL");
                }

                if (t[i] === undefined) {
                    throw "Can't graft an undefined value in a list!";
                } else if (t[i].constructor == String || t[i].constructor == Array) {
                    this.graft(namespace, e, t[i], doc);
                } else if (t[i].constructor == Number) {
                    this.graft(namespace, e, t[i].toString(), doc);
                } else if (t[i].constructor == Object) {
                    // hash's properties => element's attributes
                    for (var k in t[i]) {
                        e.setAttributeNS(null, k, t[i][k]);
                    }
                } else {

                }
            }
        }
        if (parent) {
            parent.appendChild(e);
        } else {

        }
        return e; // return the topmost created node
    },

    provideId: function () {
        var res = [], hex = '0123456789ABCDEF';

        for (var i = 0; i < 36; i++) res[i] = Math.floor(Math.random() * 0x10);

        res[14] = 4;
        res[19] = (res[19] & 0x3) | 0x8;

        for (var i = 0; i < 36; i++) res[i] = hex[res[i]];

        res[8] = res[13] = res[18] = res[23] = '-';

        return "Apromore_" + res.join('');
    }
};


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

if(!Apromore) var Apromore = {};

/**
 * The Apromore.Log logger.
 */
Apromore.Log = {
    // Apromore constants.
    Apromore_LOGLEVEL: 5,
    Apromore_LOGLEVEL_TRACE: 5,
	Apromore_LOGLEVEL_DEBUG: 4,
	Apromore_LOGLEVEL_INFO: 3,
	Apromore_LOGLEVEL_WARN: 2,
	Apromore_LOGLEVEL_ERROR: 1,
	Apromore_LOGLEVEL_FATAL: 0,
	Apromore_CONFIGURATION_DELAY: 100,
	Apromore_CONFIGURATION_WAIT_ATTEMPTS: 10,

    __appenders: [
        { append: function(message) {
                console.log(message); }}
    ],

	trace: function() {	if(Apromore.Log.Apromore_LOGLEVEL >= Apromore.Log.Apromore_LOGLEVEL_TRACE)
        Apromore.Log.__log('TRACE', arguments); },
    debug: function() { if(Apromore.Log.Apromore_LOGLEVEL >= Apromore.Log.Apromore_LOGLEVEL_DEBUG)
        Apromore.Log.__log('DEBUG', arguments); },
    info: function() { if(Apromore.Log.Apromore_LOGLEVEL >= Apromore.Log.Apromore_LOGLEVEL_INFO)
        Apromore.Log.__log('INFO', arguments); },
    warn: function() { if(Apromore.Log.Apromore_LOGLEVEL >= Apromore.Log.Apromore_LOGLEVEL_WARN)
        Apromore.Log.__log('WARN', arguments); },
    error: function() { if(Apromore.Log.Apromore_LOGLEVEL >= Apromore.Log.Apromore_LOGLEVEL_ERROR)
        Apromore.Log.__log('ERROR', arguments); },
    fatal: function() { if(Apromore.Log.Apromore_LOGLEVEL >= Apromore.Log.Apromore_LOGLEVEL_FATAL)
        Apromore.Log.__log('FATAL', arguments); },

    __log: function(prefix, messageParts) {

        messageParts[0] = (new Date()).getTime() + " "
            + prefix + " " + messageParts[0];
        var message = this.printf.apply(null, messageParts);

        Apromore.Log.__appenders.forEach(function(appender) {
            appender.append(message);
        });
    },

    addAppender: function(appender) {
        Apromore.Log.__appenders.push(appender);
    },

    printf: function() {
		var result = arguments[0];
		for (var i=1; i<arguments.length; i++)
			result = result.replace('%' + (i-1), arguments[i]);
		return result;
	}
};



/**
 * Copyright (c) 2010
 * Signavio GmbH
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
if(!Apromore) var Apromore = {};

if(!Apromore.CONFIG) Apromore.CONFIG = {};

/**
 * This file contains URI constants that may be used for XMLHTTPRequests.
 */

Apromore.CONFIG.ROOT_PATH =					(Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/editor/" : "../editor/"; //TODO: Remove last slash!!
Apromore.CONFIG.EXPLORER_PATH =				(Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/explorer/" : "../explorer";
Apromore.CONFIG.LIBS_PATH =					(Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/libs/" : "../libs";
Apromore.PATH = ".."+Apromore.CONFIG.ROOT_PATH;

/**
 * Regular Config
 */
//Apromore.CONFIG.SERVER_HANDLER_ROOT = 		(Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/p" : "../p";
Apromore.CONFIG.SERVER_HANDLER_ROOT = 		Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX;
Apromore.CONFIG.SERVER_EDITOR_HANDLER =		Apromore.CONFIG.SERVER_HANDLER_ROOT + "/editor";
Apromore.CONFIG.SERVER_MODEL_HANDLER =		Apromore.CONFIG.SERVER_HANDLER_ROOT + "/model";
Apromore.CONFIG.STENCILSET_HANDLER = 		Apromore.CONFIG.SERVER_HANDLER_ROOT + "/editor_stencilset?embedsvg=true&url=true&namespace=";
Apromore.CONFIG.STENCIL_SETS_URL = 			Apromore.CONFIG.SERVER_HANDLER_ROOT + "/editor_stencilset";
Apromore.CONFIG.PLUGINS_CONFIG =			Apromore.CONFIG.SERVER_HANDLER_ROOT + "/bpmneditor_plugins";
Apromore.CONFIG.SYNTAXCHECKER_URL =			Apromore.CONFIG.SERVER_HANDLER_ROOT + "/syntaxchecker";

Apromore.CONFIG.SS_EXTENSIONS_FOLDER =		Apromore.CONFIG.ROOT_PATH + "stencilsets/extensions/";
Apromore.CONFIG.SS_EXTENSIONS_CONFIG =		Apromore.CONFIG.SERVER_HANDLER_ROOT + "/editor_ssextensions";
Apromore.CONFIG.Apromore_NEW_URL =				"/new";
Apromore.CONFIG.BPMN_LAYOUTER =				Apromore.CONFIG.ROOT_PATH + "bpmnlayouter";

Apromore.CONFIG.GLOSSARY_PATH = "/glossary";
Apromore.CONFIG.GLOSSARY_PROPERTY_SUFFIX = "_glossary";
Apromore.CONFIG.GLOSSARY_PROPERTY_DIRTY_SUFFIX = "_glossary_dirty";

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
if(!Apromore) var Apromore = {};

if(!Apromore.CONFIG) Apromore.CONFIG = {};

/**
 * Signavio specific variables
 */
Apromore.CONFIG.BACKEND_SWITCH 		= 		true;
Apromore.CONFIG.PANEL_LEFT_COLLAPSED 	= 	true;
Apromore.CONFIG.PANEL_LEFT_WIDTH 	= 		200;
Apromore.CONFIG.PANEL_RIGHT_COLLAPSED 	= 	true;
Apromore.CONFIG.PANEL_RIGHT_WIDTH	= 		200;
Apromore.CONFIG.APPNAME = 					'Signavio';
Apromore.CONFIG.WEB_URL = 					"explorer";

Apromore.CONFIG.PDF_EXPORT_URL = '/bpmneditor' + '/editor/pdf';
Apromore.CONFIG.BIMP_URL = "http://bimp.cs.ut.ee/uploadsignavio";
Apromore.CONFIG.DIAGRAM_PRINTER_URL = "/printsvg";
Apromore.CONFIG.LICENSE_URL = "/LICENSE";

Apromore.CONFIG.BLANK_IMAGE = Apromore.CONFIG.LIBS_PATH + '/ext-2.0.2/resources/images/default/s.gif';


/* Show grid line while dragging */
Apromore.CONFIG.SHOW_GRIDLINE = 			false;

/* Editor-Mode */
Apromore.CONFIG.MODE_READONLY =				"readonly";
Apromore.CONFIG.MODE_FULLSCREEN =			"fullscreen";
Apromore.CONFIG.WINDOW_HEIGHT = 			550;
Apromore.CONFIG.PREVENT_LOADINGMASK_AT_READY = true;

/* Plugins */
Apromore.CONFIG.PLUGINS_ENABLED =			true;
Apromore.CONFIG.PLUGINS_FOLDER =			"Plugins/";
Apromore.CONFIG.BPMN20_SCHEMA_VALIDATION_ON = true;

/* Namespaces */
Apromore.CONFIG.NAMESPACE_Apromore =			"http://www.b3mn.org/Apromore";
Apromore.CONFIG.NAMESPACE_SVG =				"http://www.w3.org/2000/svg";

/* UI */
Apromore.CONFIG.CANVAS_WIDTH =				1485;
Apromore.CONFIG.CANVAS_HEIGHT =				1050;
Apromore.CONFIG.CANVAS_RESIZE_INTERVAL =	300;
Apromore.CONFIG.SELECTED_AREA_PADDING =		4;
Apromore.CONFIG.CANVAS_BACKGROUND_COLOR =	"none";
Apromore.CONFIG.GRID_DISTANCE =				30;
Apromore.CONFIG.GRID_ENABLED =				true;
Apromore.CONFIG.ZOOM_OFFSET =				0.1;
Apromore.CONFIG.DEFAULT_SHAPE_MARGIN =		60;
Apromore.CONFIG.SCALERS_SIZE =				7;
Apromore.CONFIG.MINIMUM_SIZE =				20;
Apromore.CONFIG.MAXIMUM_SIZE =				10000;
Apromore.CONFIG.OFFSET_MAGNET =				15;
Apromore.CONFIG.OFFSET_EDGE_LABEL_TOP =		8;
Apromore.CONFIG.OFFSET_EDGE_LABEL_BOTTOM =	8;
Apromore.CONFIG.OFFSET_EDGE_BOUNDS =		5;
Apromore.CONFIG.COPY_MOVE_OFFSET =			30;
Apromore.CONFIG.BORDER_OFFSET =				14;
Apromore.CONFIG.MAX_NUM_SHAPES_NO_GROUP	=	12;
Apromore.CONFIG.SHAPEMENU_CREATE_OFFSET_CORNER = 30;
Apromore.CONFIG.SHAPEMENU_CREATE_OFFSET = 45;

/* Shape-Menu Align */
Apromore.CONFIG.SHAPEMENU_RIGHT =			"Apromore_Right";
Apromore.CONFIG.SHAPEMENU_BOTTOM =			"Apromore_Bottom";
Apromore.CONFIG.SHAPEMENU_LEFT =			"Apromore_Left";
Apromore.CONFIG.SHAPEMENU_TOP =				"Apromore_Top";

/* Morph-Menu Item */
Apromore.CONFIG.MORPHITEM_DISABLED =		"Apromore_MorphItem_disabled";

/* Property type names */
Apromore.CONFIG.TYPE_STRING =				"string";
Apromore.CONFIG.TYPE_BOOLEAN =				"boolean";
Apromore.CONFIG.TYPE_INTEGER =				"integer";
Apromore.CONFIG.TYPE_FLOAT =				"float";
Apromore.CONFIG.TYPE_COLOR =				"color";
Apromore.CONFIG.TYPE_DATE =					"date";
Apromore.CONFIG.TYPE_CHOICE =				"choice";
Apromore.CONFIG.TYPE_URL =					"url";
Apromore.CONFIG.TYPE_DIAGRAM_LINK =			"diagramlink";
Apromore.CONFIG.TYPE_COMPLEX =				"complex";
Apromore.CONFIG.TYPE_TEXT =					"text";
Apromore.CONFIG.TYPE_EPC_FREQ = 			"epcfrequency";
Apromore.CONFIG.TYPE_GLOSSARY_LINK =		"glossarylink";

/* Vertical line distance of multiline labels */
Apromore.CONFIG.LABEL_LINE_DISTANCE =		2;
Apromore.CONFIG.LABEL_DEFAULT_LINE_HEIGHT =	12;

/* Open Morph Menu with Hover */
Apromore.CONFIG.ENABLE_MORPHMENU_BY_HOVER = false;

/* Editor constants come here */
Apromore.CONFIG.EDITOR_ALIGN_BOTTOM =		0x01;
Apromore.CONFIG.EDITOR_ALIGN_MIDDLE =		0x02;
Apromore.CONFIG.EDITOR_ALIGN_TOP =			0x04;
Apromore.CONFIG.EDITOR_ALIGN_LEFT =			0x08;
Apromore.CONFIG.EDITOR_ALIGN_CENTER =		0x10;
Apromore.CONFIG.EDITOR_ALIGN_RIGHT =		0x20;
Apromore.CONFIG.EDITOR_ALIGN_SIZE =			0x30;

/* Event types */
Apromore.CONFIG.EVENT_MOUSEDOWN =			"mousedown";
Apromore.CONFIG.EVENT_MOUSEUP =				"mouseup";
Apromore.CONFIG.EVENT_MOUSEOVER =			"mouseover";
Apromore.CONFIG.EVENT_MOUSEOUT =			"mouseout";
Apromore.CONFIG.EVENT_MOUSEMOVE =			"mousemove";
Apromore.CONFIG.EVENT_DBLCLICK =			"dblclick";
Apromore.CONFIG.EVENT_KEYDOWN =				"keydown";
Apromore.CONFIG.EVENT_KEYUP =				"keyup";
Apromore.CONFIG.EVENT_LOADED =				"editorloaded";
Apromore.CONFIG.EVENT_EXECUTE_COMMANDS =		"executeCommands";
Apromore.CONFIG.EVENT_STENCIL_SET_LOADED =		"stencilSetLoaded";
Apromore.CONFIG.EVENT_SELECTION_CHANGED =		"selectionchanged";
Apromore.CONFIG.EVENT_SHAPEADDED =				"shapeadded";
Apromore.CONFIG.EVENT_SHAPEREMOVED =			"shaperemoved";
Apromore.CONFIG.EVENT_PROPERTY_CHANGED =		"propertyChanged";
Apromore.CONFIG.EVENT_DRAGDROP_START =			"dragdrop.start";
Apromore.CONFIG.EVENT_SHAPE_MENU_CLOSE =		"shape.menu.close";
Apromore.CONFIG.EVENT_DRAGDROP_END =			"dragdrop.end";
Apromore.CONFIG.EVENT_RESIZE_START =			"resize.start";
Apromore.CONFIG.EVENT_RESIZE_END =				"resize.end";
Apromore.CONFIG.EVENT_DRAGDOCKER_DOCKED =		"dragDocker.docked";
Apromore.CONFIG.EVENT_HIGHLIGHT_SHOW =			"highlight.showHighlight";
Apromore.CONFIG.EVENT_HIGHLIGHT_HIDE =			"highlight.hideHighlight";
Apromore.CONFIG.EVENT_LOADING_ENABLE =			"loading.enable";
Apromore.CONFIG.EVENT_LOADING_DISABLE =			"loading.disable";
Apromore.CONFIG.EVENT_LOADING_STATUS =			"loading.status";
Apromore.CONFIG.EVENT_OVERLAY_SHOW =			"overlay.show";
Apromore.CONFIG.EVENT_OVERLAY_HIDE =			"overlay.hide";
Apromore.CONFIG.EVENT_ARRANGEMENT_TOP =			"arrangement.setToTop";
Apromore.CONFIG.EVENT_ARRANGEMENT_BACK =		"arrangement.setToBack";
Apromore.CONFIG.EVENT_ARRANGEMENT_FORWARD =		"arrangement.setForward";
Apromore.CONFIG.EVENT_ARRANGEMENT_BACKWARD =	"arrangement.setBackward";
Apromore.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED =	"propertyWindow.propertyChanged";
Apromore.CONFIG.EVENT_LAYOUT_ROWS =				"layout.rows";
Apromore.CONFIG.EVENT_LAYOUT_BPEL =				"layout.BPEL";
Apromore.CONFIG.EVENT_LAYOUT_BPEL_VERTICAL =    "layout.BPEL.vertical";
Apromore.CONFIG.EVENT_LAYOUT_BPEL_HORIZONTAL =  "layout.BPEL.horizontal";
Apromore.CONFIG.EVENT_LAYOUT_BPEL_SINGLECHILD = "layout.BPEL.singlechild";
Apromore.CONFIG.EVENT_LAYOUT_BPEL_AUTORESIZE =	"layout.BPEL.autoresize";
Apromore.CONFIG.EVENT_AUTOLAYOUT_LAYOUT =		"autolayout.layout";
Apromore.CONFIG.EVENT_UNDO_EXECUTE =			"undo.execute";
Apromore.CONFIG.EVENT_UNDO_ROLLBACK =			"undo.rollback";
Apromore.CONFIG.EVENT_BUTTON_UPDATE =           "toolbar.button.update";
Apromore.CONFIG.EVENT_LAYOUT = 					"layout.dolayout";
Apromore.CONFIG.EVENT_GLOSSARY_LINK_EDIT = 		"glossary.link.edit";
Apromore.CONFIG.EVENT_GLOSSARY_SHOW =			"glossary.show.info";
Apromore.CONFIG.EVENT_GLOSSARY_NEW =			"glossary.show.new";
Apromore.CONFIG.EVENT_DOCKERDRAG = 				"dragTheDocker";
Apromore.CONFIG.EVENT_SHOW_PROPERTYWINDOW =		"propertywindow.show";
Apromore.CONFIG.EVENT_ABOUT_TO_SAVE = "file.aboutToSave";

/* Selection Shapes Highlights */
Apromore.CONFIG.SELECTION_HIGHLIGHT_SIZE =				5;
Apromore.CONFIG.SELECTION_HIGHLIGHT_COLOR =				"#4444FF";
Apromore.CONFIG.SELECTION_HIGHLIGHT_COLOR2 =			"#9999FF";
Apromore.CONFIG.SELECTION_HIGHLIGHT_STYLE_CORNER = 		"corner";
Apromore.CONFIG.SELECTION_HIGHLIGHT_STYLE_RECTANGLE = 	"rectangle";
Apromore.CONFIG.SELECTION_VALID_COLOR =					"#00FF00";
Apromore.CONFIG.SELECTION_INVALID_COLOR =				"#FF0000";
Apromore.CONFIG.DOCKER_DOCKED_COLOR =		"#00FF00";
Apromore.CONFIG.DOCKER_UNDOCKED_COLOR =		"#FF0000";
Apromore.CONFIG.DOCKER_SNAP_OFFSET =		10;

/* Copy & Paste */
Apromore.CONFIG.EDIT_OFFSET_PASTE =			10;

/* Key-Codes */
Apromore.CONFIG.KEY_CODE_X = 				88;
Apromore.CONFIG.KEY_CODE_C = 				67;
Apromore.CONFIG.KEY_CODE_V = 				86;
Apromore.CONFIG.KEY_CODE_DELETE = 			46;
Apromore.CONFIG.KEY_CODE_META =				224;
Apromore.CONFIG.KEY_CODE_BACKSPACE =		8;
Apromore.CONFIG.KEY_CODE_LEFT =				37;
Apromore.CONFIG.KEY_CODE_RIGHT =			39;
Apromore.CONFIG.KEY_CODE_UP =				38;
Apromore.CONFIG.KEY_CODE_DOWN =				40;

// TODO Determine where the lowercase constants are still used and remove them from here.
Apromore.CONFIG.KEY_Code_enter =			12;
Apromore.CONFIG.KEY_Code_left =				37;
Apromore.CONFIG.KEY_Code_right =			39;
Apromore.CONFIG.KEY_Code_top =				38;
Apromore.CONFIG.KEY_Code_bottom =			40;

/* Supported Meta Keys */
Apromore.CONFIG.META_KEY_META_CTRL = 		"metactrl";
Apromore.CONFIG.META_KEY_ALT = 				"alt";
Apromore.CONFIG.META_KEY_SHIFT = 			"shift";

/* Key Actions */
Apromore.CONFIG.KEY_ACTION_DOWN = 			"down";
Apromore.CONFIG.KEY_ACTION_UP = 			"up";

Apromore.CONFIG.REMOTE_WINDOW_HEIGHT_DEFAULT = 300;
Apromore.CONFIG.REMOTE_WINDOW_WIDTH_DEFAULT = 300;
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

if (!Apromore) {
    var Apromore = {};
}

/**
 * The EditorApp class represents the BPMN Editor. It calls to a BPMN.io editor internally while provides
 * other UI layout components (east, west, north, south) for property panel, navigation pane, etc.
 * and access to pluggable functions via buttons (called plugins). These editor features will call
 * to the wrapped BPMN.io editor.
 * Its behavior is divided into public and private methods (starting with underscore).
 * @todo: the namespace Apromore should be changed to Apromore throughout in one pass
 */
Apromore.EditorApp = {
    construct: function (config) {
        "use strict";
        this.editor = undefined;
        this.availablePlugins = []; // plugin config data read from plugin configuration files
        this.activatedPlugins = []; // available plugin objects created from plugin javascript source files
        this.pluginsData = [];      // plugin dynamic properties provided by a plugin object when it is created
        this.layout_regions = undefined;
        this.layout = undefined;

        this.id = config.id;
        if (!this.id) {
            Apromore.Log.fatal('Missing the container HTML element for the editor');
            return;
        }
        this.fullscreen = config.fullscreen !== false;
        this.useSimulationPanel = config.useSimulationPanel || false;
        this.enabledPlugins = config.enabledPlugins; // undefined means all plugins are enabled

        // CREATES the editor
        this._createEditor();

        // GENERATES the main UI regions
        this._generateGUI();

        // LOAD the plugins
        this._loadPlugins();

        // Attach the editor must be the LAST THING AFTER ALL HAS BEEN LOADED
        this.getEditor().attachEditor(new BpmnJS({
            container: '#' + this.getEditor().rootNode.id,
            keyboard: {
                bindTo: window
            },
            propertiesPanel: this.useSimulationPanel ? {
                parent: '#js-properties-panel'
            } : undefined
        }));

        // Wait until the editor is fully loaded to start XML import and then UI init
        var me = this;
        window.setTimeout(function() {
            if (config && config.xml) {
                me.importXML(config.xml, me._initUI.bind(me));
            }
            else {
                me._initUI();
            }
        }, 100);
    },

    _initUI: function () {
        // Fixed the problem that the viewport can not
        // start with collapsed panels correctly
        if (Apromore.CONFIG.PANEL_RIGHT_COLLAPSED === true) {
            this.layout_regions.east.collapse();
        }
        if (Apromore.CONFIG.PANEL_LEFT_COLLAPSED === true) {
            this.layout_regions.west.collapse();
        }
    },

    zoomFitToModel: function () {
        this.getEditor().zoomFitToModel();
    },

    /**
     * Generate the whole UI components for the editor
     * It is based on Ext Sencha regions: east, west, south and north
     */
    _generateGUI: function () {
        "use strict";

        // Defines the layout height if it's NOT fullscreen
        var layoutHeight = Apromore.CONFIG.WINDOW_HEIGHT;

        // DEFINITION OF THE VIEWPORT AREAS
        this.layout_regions = {

            // DEFINES TOP-AREA
            north: new Ext.Panel({ //TOOO make a composite of the Apromore header and addable elements (for toolbar), second one should contain margins
                region: 'north',
                cls: 'x-panel-editor-north',
                autoEl: 'div',
                border: false
            }),

            // DEFINES RIGHT-AREA
            east: new Ext.Panel({
                region: 'east',
                layout: 'anchor',
                cls: 'x-panel-editor-east',
                collapseTitle: window.Apromore.I18N.View.East,
                titleCollapse: true,
                border: false,
                cmargins: {left: 0, right: 0},
                floatable: false,
                expandTriggerAll: true,
                collapsible: true,
                width: 450,
                split: true,
                title: "Simulation Parameters",
                items: {
                    layout: "fit",
                    autoHeight: true,
                    el: document.getElementById("js-properties-panel")
                }
            }),

            // DEFINES BOTTOM-AREA
            south: new Ext.Panel({
                region: 'south',
                cls: 'x-panel-editor-south',
                autoEl: 'div',
                border: false
            }),

            //DEFINES LEFT-AREA
            west: new Ext.Panel({
                region: 'west',
                layout: 'anchor',
                autoEl: 'div',
                cls: 'x-panel-editor-west',
                collapsible: true,
                titleCollapse: true,
                collapseTitle: window.Apromore.I18N.View.West,
                width: Apromore.CONFIG.PANEL_LEFT_WIDTH || 10,
                autoScroll: Ext.isIPad ? false : true,
                cmargins: {left: 0, right: 0},
                floatable: false,
                expandTriggerAll: true,
                split: true,
                title: "West"
            }),

            // DEFINES CENTER-AREA (FOR THE EDITOR)
            center: new Ext.Panel({
                region: 'center',
                cls: 'x-panel-editor-center',
                autoScroll: false,
                items: {
                    layout: "fit",
                    autoHeight: true,
                    el: this.getEditor().rootNode
                }
            }),

            info: new Ext.Panel({
                region: "south",
                cls: "x-panel-editor-info",
                autoEl: "div",
                border: false,
                layout: "fit",
                cmargins: {
                    top: 0,
                    bottom: 0,
                    left: 0,
                    right: 0
                },
                collapseTitle: "Information",
                floatable: false,
                titleCollapse: false,
                expandTriggerAll: true,
                collapsible: true,
                split: true,
                title: "Information",
                height: 100,
                tools: [
                    {
                        id: "close",
                        handler: function (g, f, e) {
                            e.hide();
                            e.ownerCt.layout.layout()
                        }
                    }
                ]
            })
        };

        // Config for the Ext.Viewport
        var layout_config = {
            layout: "border",
            items: [
                this.layout_regions.north,
                this.layout_regions.east,
                this.layout_regions.south,
                this.layout_regions.west,
                new Ext.Panel({ //combine center and info into one panel
                    layout: "border",
                    region: "center",
                    height: 500,
                    border: false,
                    items: [this.layout_regions.center, this.layout_regions.info]
            })]
        };

        // IF Fullscreen, use a viewport
        if (this.fullscreen) {
            this.layout = new Ext.Viewport(layout_config);
            // IF NOT, use a panel and render it to the given id
        } else {
            layout_config.renderTo = this.id;
            //layout_config.height = layoutHeight;
            layout_config.height = this._getContainer().clientHeight; // the panel and the containing div should be of the same height
            this.layout = new Ext.Panel(layout_config)
        }

        if (!this.useSimulationPanel) {
            this.layout_regions.east.hide();
        }

        this.layout_regions.west.hide();
        this.layout_regions.info.hide();
        if (Ext.isIPad && "undefined" != typeof iScroll) {
            this.getEditor().iscroll = new iScroll(this.layout_regions.center.body.dom.firstChild, {
                touchCount: 2
            })
        }

        // Set the editor to the center, and refresh the size
        this._getContainer().setAttributeNS(null, 'align', 'left');
        this.getEditor().rootNode.setAttributeNS(null, 'align', 'left');

    },

    /**
     * Adds a component to the specified UI region on the editor
     *
     * @param {String} region
     * @param {Ext.Component} component
     * @param {String} title, optional
     * @return {Ext.Component} dom reference to the current region or null if specified region is unknown
     */
    addToRegion: function (region, component, title) {
        if (region.toLowerCase && this.layout_regions[region.toLowerCase()]) {
            var current_region = this.layout_regions[region.toLowerCase()];

            current_region.add(component);

            Apromore.Log.debug("original dimensions of region %0: %1 x %2", current_region.region, current_region.width, current_region.height);

            // update dimensions of region if required.
            if (!current_region.width && component.initialConfig && component.initialConfig.width) {
                Apromore.Log.debug("resizing width of region %0: %1", current_region.region, component.initialConfig.width);
                current_region.setWidth(component.initialConfig.width)
            }
            if (component.initialConfig && component.initialConfig.height) {
                Apromore.Log.debug("resizing height of region %0: %1", current_region.region, component.initialConfig.height);
                var current_height = current_region.height || 0;
                current_region.height = component.initialConfig.height + current_height;
                current_region.setHeight(component.initialConfig.height + current_height)
            }

            // set title if provided as parameter.
            if (typeof title == "string") {
                current_region.setTitle(title);
            }

            // trigger doLayout() and show the pane
            current_region.ownerCt.doLayout();
            current_region.show();

            if (Ext.isMac) {
                this.resizeFix();
            }

            return current_region;
        }

        return null;
    },

    // Get plugins that have been activated successfully (i.e. those plugin objects)
    getAvailablePlugins: function () {
        var curAvailablePlugins = this.availablePlugins.clone();
        curAvailablePlugins.each(function (plugin) {
            if (this.activatedPlugins.find(function (loadedPlugin) {
                return loadedPlugin.type == this.name;
            }.bind(plugin))) {
                plugin.engaged = true;
            } else {
                plugin.engaged = false;
            }
        }.bind(this));
        return curAvailablePlugins;
    },

    /**
     *  Make plugin object
     *  Activated plugins: array of plugin objects
     *  [
     *      {
     *          <plugin attributes>,
     *          type: name of the plugin,
     *          engaged: true
     *      }
     *  ]
     */
    _activatePlugins: function () {
        var me = this;
        var newPlugins = [];
        var facade = this._getPluginFacade();

        // Instantiate plugin class
        // this.pluginData is filled in
        this.availablePlugins.each(function (value) {
            Apromore.Log.debug("Initializing plugin '%0'", value.name);
            try {
                var className = eval(value.name);
                if (className) {
                    var plugin = new className(facade, value);
                    plugin.type = value.name;
                    plugin.engaged = true;
                    newPlugins.push(plugin);
                }
            } catch (e) {
                Apromore.Log.warn("Plugin %0 is not available", value.name);
                Apromore.Log.error("Error: " + e.message);
            }
        });

        newPlugins.each(function (value) {
            // For plugins that need to work on other plugins such as the toolbar
            if (value.registryChanged)
                value.registryChanged(me.pluginsData);
        });

        this.activatedPlugins = newPlugins;

        // Hack for the Scrollbars
        if (Ext.isMac) {
            this.resizeFix();
        }
    },

    _getContainer: function () {
        return document.getElementById(this.id);
    },

    _createEditor: function () {
        this.editor = new Apromore.Editor({
            width: Apromore.CONFIG.CANVAS_WIDTH,
            height: Apromore.CONFIG.CANVAS_HEIGHT,
            id: Apromore.Utils.provideId(),
            parentNode: this._getContainer()
        });
    },

    getEditor: function() {
        return this.editor;
    },

    getSimulationDrawer: function() {
        return this.layout_regions.east;
    },

    /**
     * Facade object representing the editor to be used by plugins instead of
     * passing the whole editor object
     */
    _getPluginFacade: function () {
        if (!(this._pluginFacade)) {
            this._pluginFacade = (function () {
                return {
                    getAvailablePlugins: this.getAvailablePlugins.bind(this),
                    offer: this.offer.bind(this),
                    getEditor: this.getEditor.bind(this),
                    getSimulationDrawer: this.getSimulationDrawer.bind(this),
                    useSimulationPanel: this.useSimulationPanel,
                    getXML: this.getXML.bind(this),
                    getSVG: this.getSVG.bind(this),
                    addToRegion: this.addToRegion.bind(this)
                }
            }.bind(this)())
        }
        return this._pluginFacade;
    },

    getXML: function() {
        return this.getEditor().getXML();
    },

    getSVG: function() {
        return this.getEditor().getSVG();
    },

    importXML: function(xml, callback) {
        this.getEditor().importXML(xml, callback);
    },

    offer: function (pluginData) {
        if (!(this.pluginsData.findIndex(function(plugin) {return plugin.name === pluginData.name;}) >=0)) {
            if (this.enabledPlugins && !this.enabledPlugins.includes(pluginData.name)) {
                pluginData.isEnabled = function(){ return false};
            }
            this.pluginsData.push(pluginData);
        }
    },

    /**
     * When working with Ext, conditionally the window needs to be resized. To do
     * so, use this class method. Resize is deferred until 100ms, and all subsequent
     * resizeBugFix calls are ignored until the initially requested resize is
     * performed.
     */
    resizeFix: function () {
        if (!this._resizeFixTimeout) {
            this._resizeFixTimeout = window.setTimeout(function () {
                window.resizeBy(1, 1);
                window.resizeBy(-1, -1);
                this._resizefixTimeout = null;
            }, 100);
        }
    },

    /**
     * Load a list of predefined plugins from the server
     */
    _loadPlugins: function() {
        if(Apromore.CONFIG.PLUGINS_ENABLED) {
            this._loadPluginData();
            this._activatePlugins();
        }
        else {
            Apromore.Log.warn("Ignoring plugins, loading Core only.");
        }
    },


    // Available plugins structure: array of plugin structures
    // [
    //      {
    //          name: plugin name,
    //          source: plugin javascript source filename,
    //          properties (both plugin and global properties):
    //          [
    //              {attributeName1 -> attributeValue1, attributeName2 -> attributeValue2,...}
    //              {attributeName1 -> attributeValue1, attributeName2 -> attributeValue2,...}
    //              {attributeName1 -> attributeValue1, attributeName2 -> attributeValue2,...}
    //          ],
    //          requires: namespaces:[list of javascript libraries],
    //          notUsesIn: namespaces:[list of javascript libraries]
    //      }
    // ]
    _loadPluginData: function() {
        var me = this;
        var source = Apromore.CONFIG.PLUGINS_CONFIG;

        Apromore.Log.debug("Loading plugin configuration from '%0'.", source);
        new Ajax.Request(source, {
            asynchronous: false,
            method: 'get',
            onSuccess: function(result) {
                Apromore.Log.info("Plugin configuration file loaded.");

                // get plugins.xml content
                var resultXml = result.responseXML;
                console.log('Plugin list:', resultXml);

                // Global properties XML:
                // <properties>
                //      <property attributeName1="attributeValue1" attributeName2="attributeValue2" />
                //      <property attributeName1="attributeValue1" attributeName2="attributeValue2" />
                //      ...
                // </properties>
                var globalProperties = [];
                var preferences = $A(resultXml.getElementsByTagName("properties"));
                preferences.each( function(p) {
                    var props = $A(p.childNodes);
                    props.each( function(prop) {
                        var property = new Hash(); // Hash is provided by Prototype library
                        // get all attributes from the node and set to global properties
                        var attributes = $A(prop.attributes)
                        attributes.each(function(attr){property[attr.nodeName] = attr.nodeValue});
                        if(attributes.length > 0) { globalProperties.push(property) };
                    });
                });

                // Plugin XML:
                //  <plugins>
                //      <plugin source="javascript filename.js" name="Javascript class name" property="" requires="" notUsesIn="" />
                //      <plugin source="javascript filename.js" name="Javascript class name" property="" requires="" notUsesIn="" />
                //  </plugins>
                var plugin = resultXml.getElementsByTagName("plugin");
                $A(plugin).each( function(node) {
                    var pluginData = new Hash();

                    //pluginData: for one plugin
                    //.source: source javascript
                    //.name: name
                    $A(node.attributes).each( function(attr){
                        pluginData[attr.nodeName] = attr.nodeValue});

                    // ensure there's a name attribute.
                    if(!pluginData['name']) {
                        Apromore.Log.error("A plugin is not providing a name. Ignoring this plugin.");
                        return;
                    }

                    // ensure there's a source attribute.
                    if(!pluginData['source']) {
                        Apromore.Log.error("Plugin with name '%0' doesn't provide a source attribute.", pluginData['name']);
                        return;
                    }

                    // Get all plugin properties
                    var propertyNodes = node.getElementsByTagName("property");
                    var properties = [];
                    $A(propertyNodes).each(function(prop) {
                        var property = new Hash();

                        // Get all Attributes from the Node
                        var attributes = $A(prop.attributes)
                        attributes.each(function(attr){property[attr.nodeName] = attr.nodeValue});
                        if(attributes.length > 0) { properties.push(property) };

                    });

                    // Set all Global-Properties to the Properties
                    properties = properties.concat(globalProperties);

                    // Set Properties to Plugin-Data
                    pluginData['properties'] = properties;

                    // Get the RequieredNodes
                    var requireNodes = node.getElementsByTagName("requires");
                    var requires;
                    $A(requireNodes).each(function(req) {
                        var namespace = $A(req.attributes).find(function(attr){ return attr.name == "namespace"})
                        if( namespace && namespace.nodeValue ){
                            if( !requires ){
                                requires = {namespaces:[]}
                            }
                            requires.namespaces.push(namespace.nodeValue)
                        }
                    });

                    // Set Requires to the Plugin-Data, if there is one
                    if( requires ){
                        pluginData['requires'] = requires;
                    }

                    // Get the RequieredNodes
                    var notUsesInNodes = node.getElementsByTagName("notUsesIn");
                    var notUsesIn;
                    $A(notUsesInNodes).each(function(not) {
                        var namespace = $A(not.attributes).find(function(attr){ return attr.name == "namespace"})
                        if( namespace && namespace.nodeValue ){
                            if( !notUsesIn ){
                                notUsesIn = {namespaces:[]}
                            }

                            notUsesIn.namespaces.push(namespace.nodeValue)
                        }
                    });

                    // Set Requires to the Plugin-Data, if there is one
                    if( notUsesIn ){
                        pluginData['notUsesIn'] = notUsesIn;
                    }

                    var url = Apromore.PATH + Apromore.CONFIG.PLUGINS_FOLDER + pluginData['source'];
                    Apromore.Log.debug("Requiring '%0'", url);
                    Apromore.Log.info("Plugin '%0' successfully loaded.", pluginData['name']);
                    me.availablePlugins.push(pluginData);
                });

            },
            onFailure: function () {
                Apromore.Log.error("Plugin configuration file not available.");
            }
        });

    },

    toggleFullScreen: function () {
        if (!document.fullscreenElement) {
            document.documentElement.requestFullscreen();
        } else {
            if (document.exitFullscreen) {
                document.exitFullscreen();
            }
        }
    }
};

Apromore.EditorApp = Clazz.extend(Apromore.EditorApp);




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

/**
 * Init namespaces
 */
if (!Apromore) {
    var Apromore = {};
}

/**
 * Editor is actually a wrapper around the true editor (e.g. BPMN.io)
 * It provides BPMN editing features while hiding the actual editor implementation provider.
 * The aim is to minimize the impact of the implementation changes with changes minimized to this
 * class only while the editor used in Apromore codebase is unchanged as they only access this Editor class.
 */
Apromore.Editor = {
    construct: function(options) {
        this.actualEditor = undefined;

        if (!(options && options.width && options.height)) {
            Apromore.Log.fatal("The editor is missing mandatory parameters options.width and options.height.");
            return;
        }

        this.className = "Apromore_Editor";
        this.rootNode = Apromore.Utils.graft("http://www.w3.org/1999/xhtml", options.parentNode,
            ['div', {id: options.id, width: options.width, height: options.height}
            ]);
        //this.rootNode.addClassName(this.className);
        this.rootNode.classList.add(this.className);
    },

    getScrollNode: function () {
        "use strict";
        return Ext.get(this.rootNode).parent("div{overflow=auto}", true);
    },

    attachEditor: function (editor) {
        this.actualEditor = editor;
    },

    getSVGContainer: function() {
        return $("div.Apromore_Editor div.bjs-container div.djs-container svg")[0];
    },

    getSVGViewport: function() {
        return $("div.Apromore_Editor div.bjs-container div.djs-container svg g.viewport")[0];
    },

    getSourceNodeId: function (sequenceFlowId) {
        var foundId;
        var elements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        elements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.id == sequenceFlowId) {
                foundId = element.sourceRef.id;
            }
        });
        return foundId;
    },

    getTargetNodeId: function (sequenceFlowId) {
        var foundId;
        var flowElements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        flowElements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.id == sequenceFlowId) {
                foundId = element.targetRef.id;
            }
        });
        return foundId;
    },

    getIncomingFlowId: function (nodeId) {
        var foundId;
        var flowElements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        flowElements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.targetRef.id == nodeId) {
                foundId = element.id;
            }
        });
        return foundId;
    },

    getOutgoingFlowId: function (nodeId) {
        var foundId;
        var elements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        elements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.sourceRef.id == nodeId) {
                foundId = element.id;
            }
        });
        return foundId;
    },

    toString: function () {
        return "EditorWrapper " + this.id;
    },

    /**
     * Import XML into the editor.
     * This method takes time depending on the complexity of the model
     * @param {String} xml: the BPMN XML
     * @param {Function} callback: callback function to call after the import finishes
     */
    importXML: function(xml, callback) {
      // this.editor.importXML(xml, function(err) {
      //   if (err) {
      //     return console.error('could not import BPMN 2.0 diagram', err);
      //   }
      //   this.zoomFitToModel();
      // }.bind(this));

      //EXPERIMENTING WITH THE BELOW TO FIX ARROWS NOT SNAP TO EDGES WHEN OPENING MODELS
      //Some BPMN files are not compatible with bpmn.io
      var editor = this.actualEditor;
      this.actualEditor.importXML(xml, function(err) {
        if (err) {
            window.alert("Failed to import BPMN diagram. Please make sure it's a valid BPMN 2.0 diagram.");
            return;
        }

        var eventBus = editor.get('eventBus');
        var connectionDocking = editor.get('connectionDocking');
        var elementRegistry = editor.get('elementRegistry');
        var connections = elementRegistry.filter(function(e) {
          return e.waypoints;
        });
        connections.forEach(function(connection) {
          connection.waypoints = connectionDocking.getCroppedWaypoints(connection);
        });
        eventBus.fire('elements.changed', { elements: connections });
        this.zoomFitToModel();
        callback();
      }.bind(this));
    },

    getXML: function() {
        var bpmnXML;
        this.actualEditor.saveXML({ format: true }, function(err, xml) {
            bpmnXML = xml;
        });
        return bpmnXML;
    },

    getSVG: function() {
        var bpmnSVG;
        this.actualEditor.saveSVG(function(err, svg) {
            bpmnSVG = svg;
        });
        return bpmnSVG;
    },

    zoomFitToModel: function() {
        if (this.actualEditor) {
            var canvas = this.actualEditor.get('canvas');
            // zoom to fit full viewport
            canvas.zoom('fit-viewport');
            var viewbox = canvas.viewbox();
            canvas.viewbox({
                x: viewbox.x - 200,
                y: viewbox.y,
                width: viewbox.outer.width * 1.5,
                height: viewbox.outer.height * 1.5
            });
        }
    },

    zoomIn: function() {
        this.actualEditor.get('editorActions').trigger('stepZoom', { value: 1 });
    },


    zoomOut: function() {
        this.actualEditor.get('editorActions').trigger('stepZoom', { value: -1 });
    },

    zoomDefault: function() {
        editorActions.trigger('zoom', { value: 1 });
    },

    createShape: function(type, x, y, w, h) {
        var modelling = this.actualEditor.get('modeling');
        var parent = this.actualEditor.get('canvas').getRootElement();
        //console.log('parent', parent);
        var shape = modelling.createShape({type:type, width:w, height:h}, {x:x, y:y}, parent);
        return shape.id;
    },

    updateProperties: function(elementId, properties) {
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        modelling.updateProperties(registry.get(elementId), properties);
    },


    createSequenceFlow: function (source, target, attrs) {
        var attrs2 = {};
        Object.assign(attrs2,{type:'bpmn:SequenceFlow'});
        if (attrs.waypoints) {
            Object.assign(attrs2,{waypoints: attrs.waypoints});
        }
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        var flow = modelling.connect(registry.get(source), registry.get(target), attrs2);
        //console.log(flow);
        return flow.id;
    },

    createAssociation: function (source, target, attrs) {
        var attrs2 = {};
        Object.assign(attrs2,{type:'bpmn:Association'});
        if (attrs.waypoints) {
            Object.assign(attrs2,{waypoints: attrs.waypoints});
        }
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        var assoc = Object.assign(assoc, modelling.connect(registry.get(source), registry.get(target), attrs2));
        return assoc.id;
    },

    highlight: function (elementId) {
        //console.log("Highlighting elementId: " + elementId);
        var self = this;
        var element = self.actualEditor.get('elementRegistry').get(elementId);
        var modelling = self.actualEditor.get('modeling');
        //console.log(element);
        modelling.setColor([element],{stroke:'red'});
    },

    colorElements: function (elementIds, color) {
        var elements = [];
        var registry = this.actualEditor.get('elementRegistry');
        elementIds.forEach(function(elementId) {
            elements.push(registry.get(elementId));
        });
        var modelling = this.actualEditor.get('modeling');
        modelling.setColor(elements, {stroke:color});
    },

    colorElement: function (elementId, color) {
        var modelling = this.actualEditor.get('modeling');
        var element = this.actualEditor.get('elementRegistry').get(elementId);
        modelling.setColor([element],{stroke:color});
    },

    fillColor: function (elementId, color) {
        var modelling = this.actualEditor.get('modeling');
        var element = this.actualEditor.get('elementRegistry').get(elementId);
        modelling.setColor([element],{fill:color});
    },

    greyOut: function(elementIds) {
        var elementRegistry = this.actualEditor.get('elementRegistry');
        var self = this;
        elementIds.forEach(function(id) {
            console.log('_elements', elementRegistry._elements);
            var gfx = elementRegistry.getGraphics(id);
            var visual = gfx.children[0];
            visual.setAttributeNS(null, "style", "opacity: 0.25");
        });

    },

    normalizeAll: function() {
        var registry = this.actualEditor.get('elementRegistry');
        var modelling = this.actualEditor.get('modeling');
        modelling.setColor(registry.getAll(), {stroke:'black'});
    },

    removeShapes: function(shapeIds) {
        var registry = this.actualEditor.get('elementRegistry');
        var modelling = this.actualEditor.get('modeling');
        console.log(shapeIds);
        var shapes = [];
        shapeIds.forEach(function(shapeId) {
            shapes.push(registry.get(shapeId));
        });
        modelling.removeElements(shapes);
    },

    getAllElementIds: function() {
        var ids = [];
        var elementRegistry = this.actualEditor.get('elementRegistry');
        elementRegistry.getAll().forEach(function(element) {
            ids.push(element.id);
        });
        return ids;
    },

    shapeCenter: function (shapeId) {
        var position = {};
        var registry = this.actualEditor.get('elementRegistry');
        var shape = registry.get(shapeId);
        //console.log('Shape of ' + shapeId);
        //console.log(shape);
        //console.log(shape.x);
        position.x = (shape.x + shape.width/2);
        position.y = (shape.y + shape.height/2);
        return position;
    },

    clear: function() {
        this.actualEditor.clear();
    },

    registerActionHandler: function(handlerName, handler) {
        var commandStack = this.actualEditor.get('commandStack');
        commandStack.registerHandler(handlerName, handler);
    },

    executeActionHandler: function(handlerName, context) {
        var commandStack = this.actualEditor.get('commandStack');
        commandStack.execute(handlerName, context);
    },

    getCenter: function (shapeId) {
        var shape = this.actualEditor.get('elementRegistry').get(shapeId);
        return {
            x: shape.x + (shape.width || 0) / 2,
            y: shape.y + (shape.height || 0) / 2
        }
    },

    // Center viewbox to an element
    // From https://forum.bpmn.io/t/centering-zooming-view-to-a-specific-element/1536/6
    centerElement: function(elementId) {
        // assuming we center on a shape.
        // for connections we must compute the bounding box
        // based on the connection's waypoints
        var bbox = elementRegistry.get(elementId);

        var currentViewbox = canvas.viewbox();

        var elementMid = {
          x: bbox.x + bbox.width / 2,
          y: bbox.y + bbox.height / 2
        };

        canvas.viewbox({
          x: elementMid.x - currentViewbox.width / 2,
          y: elementMid.y - currentViewbox.height / 2,
          width: currentViewbox.width,
          height: currentViewbox.height
        });
    },

  _getActionStack: function() {
    return this.actualEditor.get('commandStack')._stack;
  },

  _getCurrentStackIndex: function() {
    return this.actualEditor.get('commandStack')._stackIdx;
  },

  // Get all base action indexes backward from the current command stack index
  // The first element in the result is the earliest base action and so on
  _getBaseActions: function() {
    var actions = this._getActionStack();
    var stackIndex = this._getCurrentStackIndex();
    var baseActionIndexes = [];
    for (var i=0; i<=stackIndex; i++) {
      if (i==0 || (actions[i].id != actions[i-1].id)) {
        baseActionIndexes.push(i);
      }
    }
    return baseActionIndexes;
  },

  undo: function() {
    this.actualEditor.get('commandStack').undo();
  },

  // Undo to the point before an action (actionName is the input)
  // Nothing happens if the action is not found
  // The number of undo times is the number of base actions from the current stack index
  undoSeriesUntil: function(actionName) {
    var actions = this._getActionStack();
    var baseActions = this._getBaseActions();
    var baseActionNum = 0;
    for (var i=baseActions.length-1; i>=0; i--) {
      if (actions[baseActions[i]].command == actionName) {
        baseActionNum = baseActions.length - i;
        break;
      }
    }

    console.log('baseActionNum', baseActionNum);

    while (baseActionNum > 0) {
      this.undo();
      baseActionNum--;
    }
  },

  canUndo: function() {
    if (!this.actualEditor) {
      return false;
    }
    else {
      return this.actualEditor.get('commandStack').canUndo();
    }
  },

  redo: function() {
    this.actualEditor.get('commandStack').redo();
  },

  canRedo: function() {
    if (!this.actualEditor) {
      return false;
    }
    else {
      return this.actualEditor.get('commandStack').canRedo();
    }
  },

  getLastBaseAction: function() {
    var actions = this._getActionStack();
    var baseActions = this._getBaseActions();
    if (baseActions.length > 0) {
      return actions[baseActions[baseActions.length-1]].command;
    }
    else {
      return '';
    }
  },

  // Get the next latest base action in the command stack
  // that is not in the excluding list
  getNextBaseActionExcluding: function(excludingActions) {
    var actions = this._getActionStack();
    var baseActionIndexes = this._getBaseActions();
    if (baseActionIndexes.length >= 2) {
      for (var i = baseActionIndexes.length-2; i>=0; i--) {
        if (excludingActions.indexOf(actions[baseActionIndexes[i]].command) < 0) {
          return actions[baseActionIndexes[i]].command;
        }
      }
    }
    return '';
  },

  addCommandStackChangeListener: function(callback) {
    this.actualEditor.on('commandStack.changed', callback);
  },

  addEventBusListener: function(eventCode, callback) {
    this.actualEditor.get('eventBus').on(eventCode, callback);
  }

};

Apromore.Editor = Clazz.extend(Apromore.Editor);

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
if(!Apromore){ var Apromore = {} }
if(!Apromore.Plugins){ Apromore.Plugins = {} }

Apromore.Plugins.ApromoreSave = Clazz.extend({

    facade:undefined,

    changeSymbol:"*",

    construct:function (facade) {
        this.facade = facade;

        this.facade.offer({
            'name':window.Apromore.I18N.Save.save,
            'functionality':this.save.bind(this, false),
            'group':window.Apromore.I18N.Save.group,
            'icon':Apromore.PATH + "images/disk.png",
            'description':window.Apromore.I18N.Save.saveDesc,
            'index':1,
            'minShape':0,
            'maxShape':0,
            keyCodes:[
                {
                    metaKeys:[Apromore.CONFIG.META_KEY_META_CTRL],
                    keyCode:83, // s-Keycode
                    keyAction:Apromore.CONFIG.KEY_ACTION_UP
                }
            ]
        });

        this.facade.offer({
            'name':window.Apromore.I18N.Save.saveAs,
            'functionality':this.save.bind(this, true),
            'group':window.Apromore.I18N.Save.group,
            'icon':Apromore.PATH + "images/disk_multi.png",
            'description':window.Apromore.I18N.Save.saveAsDesc,
            'index':2,
            'minShape':0,
            'maxShape':0
        });
    },

    updateTitle:function () {

    },

    onUnLoad:function () {

    },

    /**
     * Saves the current process to the server.
     */
    save:function (forceNew, event) {
        if (this.saving) {
            return false;
        }

        this.saving = true;

        var xml = this.facade.getXML();
        var svg = this.facade.getSVG();

        if (forceNew) {
            if (Apromore.Plugins.ApromoreSave.apromoreSaveAs) {
                Apromore.Plugins.ApromoreSave.apromoreSaveAs(xml, svg);
            } else {
                alert("Apromore Save As method is missing!");
            }
        } else {
            if (Apromore.Plugins.ApromoreSave.apromoreSave) {
                Apromore.Plugins.ApromoreSave.apromoreSave(xml, svg);
            } else {
                alert("Apromore Save method is missing!");
            }
        }

        this.saving = false;
        return true;
    }

});


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
if (!Apromore.Plugins)
    Apromore.Plugins = new Object();

Apromore.Plugins.Export = Clazz.extend({

    facade: undefined,

    construct: function(facade){
        this.facade = facade;

        this.facade.offer({
            'name': window.Apromore.I18N.File.svg,
            'functionality': this.exportSVG.bind(this),
            'group': window.Apromore.I18N.File.group,
            'icon': Apromore.PATH + "images/exportsvg.png",
            'description': window.Apromore.I18N.File.svgDesc,
            'index': 3,
            'minShape': 0,
            'maxShape': 0
        });

        this.facade.offer({
            'name': window.Apromore.I18N.File.bpmn,
            'functionality': this.exportBPMN.bind(this),
            'group': window.Apromore.I18N.File.group,
            'icon': Apromore.PATH + "images/exportbpmn.png",
            'description': window.Apromore.I18N.File.bpmnDesc,
            'index': 4,
            'minShape': 0,
            'maxShape': 0
        });

    },

    exportSVG: function() {
        var svg = this.facade.getSVG();
        var hiddenElement = document.createElement('a');
        hiddenElement.href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodeURIComponent(svg);
        hiddenElement.target = '_blank';
        hiddenElement.download = 'diagram.svg';
        hiddenElement.click();
    },

    exportBPMN: function() {
        var xml = this.facade.getXML();
        var hiddenElement = document.createElement('a');
        hiddenElement.href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodeURIComponent(xml);
        hiddenElement.target = '_blank';
        hiddenElement.download = 'diagram.bpmn';
        hiddenElement.click();
    }

});

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
if (!Apromore.Plugins)
    Apromore.Plugins = new Object();

Apromore.Plugins.File = Clazz.extend({

    facade: undefined,

    construct: function(facade){
        this.facade = facade;

        this.facade.offer({
            'name': window.Apromore.I18N.File.pdf,
            'functionality': this.exportPDF.bind(this),
            'group': window.Apromore.I18N.File.group,
            'icon': Apromore.PATH + "images/page_white_acrobat.png",
            'description': window.Apromore.I18N.File.pdfDesc,
            'index': 5,
            'minShape': 0,
            'maxShape': 0
        });
    },

    exportPDF: function() {
        var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});
        myMask.show();

        var resource = location.href;

        // Get the serialized svg image source
        var svgClone = this.facade.getEditor().getSVG();
        //var svgDOM = DataManager.serialize(svgClone);

        // Send the svg to the server.
        new Ajax.Request(Apromore.CONFIG.PDF_EXPORT_URL, {
            method: 'POST',
            parameters: {
                resource: resource,
                data: svgClone,
                format: "pdf"
            },
            onSuccess: (function(request){
                myMask.hide();
                // Because the pdf may be opened in the same window as the
                // process, yet the process may not have been saved, we're
                // opening every other representation in a new window.
                // location.href = request.responseText
                window.open(request.responseText);
            }).bind(this),
            onFailure: (function(){
                myMask.hide();
                Ext.Msg.alert(window.Apromore.I18N.Apromore.title, window.Apromore.I18N.File.genPDFFailed);
            }).bind(this)
        });
    }

});

/**
 * @namespace Apromore name space for plugins
 * @name Apromore.Plugins
 */
if (!Apromore.Plugins)
    Apromore.Plugins = new Object();

/**
 * The simulation panel plugin offers functionality to change model simulation parameters over the
 * simulation parameters panel.
 *
 * @class Apromore.Plugins.SimulationPanel
 * @extends Clazz
 * @param {Object} facade The editor facade for plugins.
 */
Apromore.Plugins.SimulationPanel = Clazz.extend({
    /** @lends Apromore.Plugins.SimulationPanel.prototype */
    facade: undefined,

    construct: function (facade) {
        this.facade = facade;

        /* Register toggle simulation panel */
        this.facade.offer({
            'name': window.Apromore.I18N.SimulationPanel.toggleSimulationDrawer,
            'functionality': this.toggleSimulationDrawer.bind(this),
            'group': window.Apromore.I18N.SimulationPanel.group,
            'description': window.Apromore.I18N.SimulationPanel.toggleSimulationDrawerDesc,
            'index': 1,
            'minShape': 0,
            'maxShape': 0,
            isEnabled : function(){ return facade.useSimulationPanel}.bind(this),
        });
    },

    /**
     * Shortcut for performing an expand or collapse based on the current state of the panel.
     */
    toggleSimulationDrawer: function () {
        this.facade.getSimulationDrawer().toggleCollapse(true);
    }
});/**
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


if(!Apromore.Plugins) {
	Apromore.Plugins = new Object();
}

Apromore.Plugins.Toolbar = Clazz.extend({

	facade: undefined,
	plugs:	[],

	construct: function(facade, ownPluginData) {
		this.facade = facade;

		this.groupIndex = new Hash();
		ownPluginData.properties.each((function(value){
			if(value.group && value.index != undefined) {
				this.groupIndex[value.group] = value.index
			}
		}).bind(this));

		Ext.QuickTips.init();

		this.buttons = [];
	},

    /**
     * Can be used to manipulate the state of a button.
     * @example
     * this.facade.raiseEvent({
     *   type: Apromore.CONFIG.EVENT_BUTTON_UPDATE,
     *   id: this.buttonId, // have to be generated before and set in the offer method
     *   pressed: true
     * });
     * @param {Object} event
     */
    onButtonUpdate: function(event){
        var button = this.buttons.find(function(button){
            return button.id === event.id;
        });

        if(event.pressed !== undefined){
            button.buttonInstance.toggle(event.pressed);
        }
    },

	registryChanged: function(pluginsData) {
        // Sort plugins by group and index
		var newPlugs =  pluginsData.sortBy((function(value) {
			return ((this.groupIndex[value.group] != undefined ? this.groupIndex[value.group] : "" ) + value.group + "" + value.index).toLowerCase();
		}).bind(this));

		// Search all plugins that are defined as plugin toolbar buttons or undefined target (meaning for all)
		var plugs = $A(newPlugs).findAll(function(plugin){
										return !this.plugs.include(plugin) && (!plugin.target || plugin.target === Apromore.Plugins.Toolbar)
									}.bind(this));
		if(plugs.length<1) return;

		this.buttons = [];

		Apromore.Log.trace("Creating a toolbar.")

        if(!this.toolbar){
			this.toolbar = new Ext.ux.SlicedToolbar({height: 24});
			var region = this.facade.addToRegion("north", this.toolbar, "Toolbar");
		}

		var currentGroupsName = this.plugs.last() ? this.plugs.last().group: plugs[0].group;

        // Map used to store all drop down buttons of current group
        var currentGroupsDropDownButton = {};

		plugs.each((function(plugin) {
			if(!plugin.name) {return}
			this.plugs.push(plugin);

            // Add seperator if new group begins
			if(currentGroupsName != plugin.group) {
			    this.toolbar.add('-');
				currentGroupsName = plugin.group;
                currentGroupsDropDownButton = {};
			}

            // If an drop down group icon is provided, a split button should be used
            // This is unused at this stage as all toolbar buttons are simple
            if(plugin.dropDownGroupIcon){
                var splitButton = currentGroupsDropDownButton[plugin.dropDownGroupIcon];

                // Create a new split button if this is the first plugin using it
                if(splitButton === undefined){
                    splitButton = currentGroupsDropDownButton[plugin.dropDownGroupIcon] = new Ext.Toolbar.SplitButton({
                        cls: "x-btn-icon", //show icon only
                        icon: plugin.dropDownGroupIcon,
                        menu: new Ext.menu.Menu({
                            items: [] // items are added later on
                        }),
                        listeners: {
                          click: function(button, event){
                            // The "normal" button should behave like the arrow button
                            if(!button.menu.isVisible() && !button.ignoreNextClick){
                                button.showMenu();
                            } else {
                                button.hideMenu();
                            }
                          }
                        }
                    });

                    this.toolbar.add(splitButton);
                }

                // General config button which will be used either to create a normal button
                // or a check button (if toggling is enabled)
                var buttonCfg = {
                    icon: plugin.icon,
                    text: plugin.name,
                    itemId: plugin.id,
                    handler: plugin.toggle ? undefined : plugin.functionality,
                    checkHandler: plugin.toggle ? plugin.functionality : undefined,
                    listeners: {
                        render: function(item){
                            // After rendering, a tool tip should be added to component
                            if (plugin.description) {
                                new Ext.ToolTip({
                                    target: item.getEl(),
                                    title: plugin.description
                                })
                            }
                        }
                    }
                }

                // Create buttons depending on toggle
                if(plugin.toggle) {
                    var button = new Ext.menu.CheckItem(buttonCfg);
                } else {
                    var button = new Ext.menu.Item(buttonCfg);
                }

                splitButton.menu.add(button);

			} else { // create normal, simple button
                var button = new Ext.Toolbar.Button({
                    icon:           plugin.icon,         // icons can also be specified inline
                    cls:            'x-btn-icon',       // Class who shows only the icon
                    itemId:         plugin.id,
					tooltip:        plugin.description,  // Set the tooltip
                    tooltipType:    'title',            // Tooltip will be shown as in the html-title attribute
                    handler:        plugin.toggle ? null : plugin.functionality,  // Handler for mouse click
                    enableToggle:   plugin.toggle, // Option for enabling toggling
                    toggleHandler:  plugin.toggle ? plugin.functionality : null // Handler for toggle (Parameters: button, active)
                });

                this.toolbar.add(button);

                button.getEl().onclick = function() {this.blur()}
            }

			plugin['buttonInstance'] = button;
			this.buttons.push(plugin);

		}).bind(this));

		this.enableButtons([]);

        // This is unused at this stage as all toolbar buttons are simple
        this.toolbar.calcSlices();
		window.addEventListener("resize", function(event){this.toolbar.calcSlices()}.bind(this), false);
		window.addEventListener("onresize", function(event){this.toolbar.calcSlices()}.bind(this), false);

	},

	onSelectionChanged: function(event) {
		this.enableButtons(event.elements);
	},

	enableButtons: function(elements) {
		// Show the Buttons
		this.buttons.each((function(pluginButton){
			pluginButton.buttonInstance.enable();

			// If there is less elements than minShapes
			if(pluginButton.minShape && pluginButton.minShape > elements.length)
				pluginButton.buttonInstance.disable();
			// If there is more elements than minShapes
			if(pluginButton.maxShape && pluginButton.maxShape < elements.length)
				pluginButton.buttonInstance.disable();
			// If the plugin button is not enabled
			if(pluginButton.isEnabled && !pluginButton.isEnabled(pluginButton.buttonInstance))
				pluginButton.buttonInstance.disable();

		}).bind(this));
	}
});

Ext.ns("Ext.ux");
Ext.ux.SlicedToolbar = Ext.extend(Ext.Toolbar, {
    currentSlice: 0,
    iconStandardWidth: 22, //22 px
    seperatorStandardWidth: 2, //2px, minwidth for Ext.Toolbar.Fill
    toolbarStandardPadding: 2,

    initComponent: function(){
        Ext.apply(this, {
        });
        Ext.ux.SlicedToolbar.superclass.initComponent.apply(this, arguments);
    },

    onRender: function(){
        Ext.ux.SlicedToolbar.superclass.onRender.apply(this, arguments);
    },

    onResize: function(){
        Ext.ux.SlicedToolbar.superclass.onResize.apply(this, arguments);
    },

    // This is unused at this stage as all toolbar buttons are simple
    calcSlices: function(){
        var slice = 0;
        this.sliceMap = {};
        var sliceWidth = 0;
        var toolbarWidth = this.getEl().getWidth();

        this.items.getRange().each(function(item, index){
            //Remove all next and prev buttons
            if (item.helperItem) {
                item.destroy();
                return;
            }

            var itemWidth = item.getEl().getWidth();

            if(sliceWidth + itemWidth + 5 * this.iconStandardWidth > toolbarWidth){
                var itemIndex = this.items.indexOf(item);

                this.insertSlicingButton("next", slice, itemIndex);

                if (slice !== 0) {
                    this.insertSlicingButton("prev", slice, itemIndex);
                }

                this.insertSlicingSeperator(slice, itemIndex);

                slice += 1;
                sliceWidth = 0;
            }

            this.sliceMap[item.id] = slice;
            sliceWidth += itemWidth;
        }.bind(this));

        // Add prev button at the end
        if(slice > 0){
            this.insertSlicingSeperator(slice, this.items.getCount()+1);
            this.insertSlicingButton("prev", slice, this.items.getCount()+1);
            var spacer = new Ext.Toolbar.Spacer();
            this.insertSlicedHelperButton(spacer, slice, this.items.getCount()+1);
            Ext.get(spacer.id).setWidth(this.iconStandardWidth);
        }

        this.maxSlice = slice;

        // Update view
        this.setCurrentSlice(this.currentSlice);
    },

    insertSlicedButton: function(button, slice, index){
        this.insertButton(index, button);
        this.sliceMap[button.id] = slice;
    },

    insertSlicedHelperButton: function(button, slice, index){
        button.helperItem = true;
        this.insertSlicedButton(button, slice, index);
    },

    insertSlicingSeperator: function(slice, index){
        // Align right
        this.insertSlicedHelperButton(new Ext.Toolbar.Fill(), slice, index);
    },

    // type => next or prev
    insertSlicingButton: function(type, slice, index){
        var nextHandler = function(){this.setCurrentSlice(this.currentSlice+1)}.bind(this);
        var prevHandler = function(){this.setCurrentSlice(this.currentSlice-1)}.bind(this);

        var button = new Ext.Toolbar.Button({
            cls: "x-btn-icon",
            icon: Apromore.CONFIG.ROOT_PATH + "images/toolbar_"+type+".png",
            handler: (type === "next") ? nextHandler : prevHandler
        });

        this.insertSlicedHelperButton(button, slice, index);
    },

    setCurrentSlice: function(slice){
        if(slice > this.maxSlice || slice < 0) return;

        this.currentSlice = slice;

        this.items.getRange().each(function(item){
            item.setVisible(slice === this.sliceMap[item.id]);
        }.bind(this));
    }
});/**
 * Copyright (c) 2008
 * Willi Tscheschner
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


/**
 * This plugin offer the functionality of undo/redo
 * Therewith the command pattern is used.
 *
 * A Plugin which want that the changes could get undo/redo has
 * to implement a command-class (which implements the method .execute(), .rollback()).
 * Those instance of class must be execute thru the facade.executeCommands(). If so,
 * those command get stored here in the undo/redo stack and can get reset/restore.
 *
 **/

if (!Apromore.Plugins)
    Apromore.Plugins = new Object();

Apromore.Plugins.Undo = Clazz.extend({

	// Defines the facade
    facade		: undefined,

	// Defines the undo/redo Stack
	undoStack	: [],
	redoStack	: [],

	// Constructor
    construct: function(facade){

        this.facade = facade;

		// Offers the functionality of undo
        this.facade.offer({
			name			: window.Apromore.I18N.Undo.undo,
			description		: window.Apromore.I18N.Undo.undoDesc,
			icon			: Apromore.PATH + "images/arrow_undo.png",
			keyCodes: [{
					metaKeys: [Apromore.CONFIG.META_KEY_META_CTRL],
					keyCode: 90,
					keyAction: Apromore.CONFIG.KEY_ACTION_DOWN
				}
		 	],
			functionality	: this.doUndo.bind(this),
			group			: window.Apromore.I18N.Undo.group,
			isEnabled		: function(){ return true }.bind(this),
			index			: 0
		});

		// Offers the functionality of redo
        this.facade.offer({
			name			: window.Apromore.I18N.Undo.redo,
			description		: window.Apromore.I18N.Undo.redoDesc,
			icon			: Apromore.PATH + "images/arrow_redo.png",
			keyCodes: [{
					metaKeys: [Apromore.CONFIG.META_KEY_META_CTRL],
					keyCode: 89,
					keyAction: Apromore.CONFIG.KEY_ACTION_DOWN
				}
		 	],
			functionality	: this.doRedo.bind(this),
			group			: window.Apromore.I18N.Undo.group,
			isEnabled		: function(){ return true}.bind(this),
			index			: 1
		});

		// Register on event for executing commands --> store all commands in a stack
		//this.facade.registerOnEvent(Apromore.CONFIG.EVENT_EXECUTE_COMMANDS, this.handleExecuteCommands.bind(this) );

	},

	/**
	 * Stores all executed commands in a stack
	 *
	 * @param {Object} evt
	 */
	handleExecuteCommands: function( evt ){

	},

	/**
	 * Does the undo
	 *
	 */
	doUndo: function(){
        this.facade.getEditor().undo();
	},

	/**
	 * Does the redo
	 *
	 */
	doRedo: function(){
        this.facade.getEditor().redo();
	}

});
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

/**
 * @namespace Apromore name space for plugins
 * @name Apromore.Plugins
 */
if (!Apromore.Plugins)
    Apromore.Plugins = new Object();

/**
 * The view plugin offers all of zooming functionality accessible over the
 * tool bar. This are zoom in, zoom out, zoom to standard, zoom fit to model.
 *
 * @class Apromore.Plugins.View
 * @extends Clazz
 * @param {Object} facade The editor facade for plugins.
 */
Apromore.Plugins.View = Clazz.extend({
    /** @lends Apromore.Plugins.View.prototype */
    facade: undefined,

    construct: function (facade) {
        this.facade = facade;

        /* Register zoom in */
        this.facade.offer({
            'name': window.Apromore.I18N.View.zoomIn,
            'functionality': this.zoomIn.bind(this),
            'group': window.Apromore.I18N.View.group,
            'icon': Apromore.PATH + "images/magnifier_zoom_in.png",
            'description': window.Apromore.I18N.View.zoomInDesc,
            'index': 1,
            'minShape': 0,
            'maxShape': 0});

        /* Register zoom out */
        this.facade.offer({
            'name': window.Apromore.I18N.View.zoomOut,
            'functionality': this.zoomOut.bind(this),
            'group': window.Apromore.I18N.View.group,
            'icon': Apromore.PATH + "images/magnifier_zoom_out.png",
            'description': window.Apromore.I18N.View.zoomOutDesc,
            'index': 2,
            'minShape': 0,
            'maxShape': 0});

        /* Register zoom fit to model */
        this.facade.offer({
            'name': window.Apromore.I18N.View.zoomFitToModel,
            'functionality': this.zoomFitToModel.bind(this),
            'group': window.Apromore.I18N.View.group,
            'icon': Apromore.PATH + "images/image.png",
            'description': window.Apromore.I18N.View.zoomFitToModelDesc,
            'index': 4,
            'minShape': 0,
            'maxShape': 0 });
    },

    zoomIn: function (factor) {
        this.facade.getEditor().zoomIn();
    },

    zoomOut: function (factor) {
        this.facade.getEditor().zoomOut();
    },



    /**
     * It calculates the zoom level to fit whole model into the visible area
     * of the canvas. Than the model gets zoomed and the position of the
     * scroll bars are adjusted.
     *
     */
    zoomFitToModel: function () {
        this.facade.getEditor().zoomFitToModel();
    }
});

export {Apromore};