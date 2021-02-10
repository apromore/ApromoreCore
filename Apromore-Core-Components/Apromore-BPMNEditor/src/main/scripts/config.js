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
