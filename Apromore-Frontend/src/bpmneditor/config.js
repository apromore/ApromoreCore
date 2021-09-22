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

let CONFIG = {};

/**
 * Previously from site.properties.js
 */
CONFIG.SERVER_HANDLER_ROOT_PREFIX =  '/bpmneditor';
CONFIG.EDITOR_PATH = "/editor";

/**
 * Previously from server.js
 */
CONFIG.ROOT_PATH =              (CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/editor/" : "../editor/"; //TODO: Remove last slash!!
CONFIG.EXPLORER_PATH =          (CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/explorer/" : "../explorer";
CONFIG.LIBS_PATH =              (CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/libs/" : "../libs";
CONFIG.PATH =                   "." + CONFIG.ROOT_PATH;

/**
 * Regular Config
 */
//CONFIG.SERVER_HANDLER_ROOT =         (CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/p" : "../p";
CONFIG.SERVER_HANDLER_ROOT =        CONFIG.SERVER_HANDLER_ROOT_PREFIX;
CONFIG.SERVER_EDITOR_HANDLER =      CONFIG.SERVER_HANDLER_ROOT + "/editor";
CONFIG.SERVER_MODEL_HANDLER =       CONFIG.SERVER_HANDLER_ROOT + "/model";
CONFIG.PLUGINS_CONFIG =             CONFIG.SERVER_HANDLER_ROOT + "/bpmneditor_plugins";
CONFIG.SYNTAXCHECKER_URL =          CONFIG.SERVER_HANDLER_ROOT + "/syntaxchecker";

CONFIG.SS_EXTENSIONS_FOLDER =       CONFIG.ROOT_PATH + "stencilsets/extensions/";
CONFIG.SS_EXTENSIONS_CONFIG =       CONFIG.SERVER_HANDLER_ROOT + "/editor_ssextensions";
CONFIG.Apromore_NEW_URL =           "/new";
CONFIG.BPMN_LAYOUTER =              CONFIG.ROOT_PATH + "bpmnlayouter";

CONFIG.GLOSSARY_PATH = "/glossary";
CONFIG.GLOSSARY_PROPERTY_SUFFIX = "_glossary";
CONFIG.GLOSSARY_PROPERTY_DIRTY_SUFFIX = "_glossary_dirty";

// Original config.js values

/**
 * Signavio specific variables
 */
CONFIG.BACKEND_SWITCH         =         true;
CONFIG.PANEL_LEFT_COLLAPSED     =       true;
CONFIG.PANEL_LEFT_WIDTH     =           200;
CONFIG.PANEL_RIGHT_COLLAPSED     =      true;
CONFIG.PANEL_RIGHT_WIDTH    =           200;
CONFIG.APPNAME =                        'Signavio';
CONFIG.WEB_URL =                        "explorer";

CONFIG.PDF_EXPORT_URL = '/bpmneditor' + '/editor/pdf';
CONFIG.BIMP_URL = "http://bimp.cs.ut.ee/uploadsignavio";
CONFIG.DIAGRAM_PRINTER_URL = "/printsvg";
CONFIG.LICENSE_URL = "/LICENSE";

CONFIG.BLANK_IMAGE = CONFIG.LIBS_PATH + '/ext-2.0.2/resources/images/default/s.gif';


/* Show grid line while dragging */
CONFIG.SHOW_GRIDLINE =                     false;

/* Editor-Mode */
CONFIG.MODE_READONLY =                      "readonly";
CONFIG.MODE_FULLSCREEN =                    "fullscreen";
CONFIG.WINDOW_HEIGHT =                      550;
CONFIG.PREVENT_LOADINGMASK_AT_READY =       true;

/* Plugins */
CONFIG.PLUGINS_ENABLED =                    true;
CONFIG.PLUGINS_FOLDER =                     "plugins/";
CONFIG.BPMN20_SCHEMA_VALIDATION_ON =        true;

/* Namespaces */
CONFIG.NAMESPACE_Apromore =                 "http://www.b3mn.org/Apromore";
CONFIG.NAMESPACE_SVG =                      "http://www.w3.org/2000/svg";

/* UI */
CONFIG.CANVAS_WIDTH =                       1485;
CONFIG.CANVAS_HEIGHT =                      1050;
CONFIG.CANVAS_RESIZE_INTERVAL =             300;
CONFIG.SELECTED_AREA_PADDING =              4;
CONFIG.CANVAS_BACKGROUND_COLOR =            "none";
CONFIG.GRID_DISTANCE =                      30;
CONFIG.GRID_ENABLED =                       true;
CONFIG.ZOOM_OFFSET =                        0.1;
CONFIG.DEFAULT_SHAPE_MARGIN =               60;
CONFIG.SCALERS_SIZE =                       7;
CONFIG.MINIMUM_SIZE =                       20;
CONFIG.MAXIMUM_SIZE =                       10000;
CONFIG.OFFSET_MAGNET =                      15;
CONFIG.OFFSET_EDGE_LABEL_TOP =              8;
CONFIG.OFFSET_EDGE_LABEL_BOTTOM =           8;
CONFIG.OFFSET_EDGE_BOUNDS =                 5;
CONFIG.COPY_MOVE_OFFSET =                   30;
CONFIG.BORDER_OFFSET =                      14;
CONFIG.MAX_NUM_SHAPES_NO_GROUP    =         12;
CONFIG.SHAPEMENU_CREATE_OFFSET_CORNER =     30;
CONFIG.SHAPEMENU_CREATE_OFFSET =            45;

/* Shape-Menu Align */
CONFIG.SHAPEMENU_RIGHT =                    "Apromore_Right";
CONFIG.SHAPEMENU_BOTTOM =                   "Apromore_Bottom";
CONFIG.SHAPEMENU_LEFT =                     "Apromore_Left";
CONFIG.SHAPEMENU_TOP =                      "Apromore_Top";

/* Morph-Menu Item */
CONFIG.MORPHITEM_DISABLED =                "Apromore_MorphItem_disabled";

/* Property type names */
CONFIG.TYPE_STRING =                        "string";
CONFIG.TYPE_BOOLEAN =                       "boolean";
CONFIG.TYPE_INTEGER =                       "integer";
CONFIG.TYPE_FLOAT =                         "float";
CONFIG.TYPE_COLOR =                         "color";
CONFIG.TYPE_DATE =                          "date";
CONFIG.TYPE_CHOICE =                        "choice";
CONFIG.TYPE_URL =                           "url";
CONFIG.TYPE_DIAGRAM_LINK =                  "diagramlink";
CONFIG.TYPE_COMPLEX =                       "complex";
CONFIG.TYPE_TEXT =                          "text";
CONFIG.TYPE_EPC_FREQ =                      "epcfrequency";
CONFIG.TYPE_GLOSSARY_LINK =                 "glossarylink";

/* Vertical line distance of multiline labels */
CONFIG.LABEL_LINE_DISTANCE =                2;
CONFIG.LABEL_DEFAULT_LINE_HEIGHT =          12;

/* Open Morph Menu with Hover */
CONFIG.ENABLE_MORPHMENU_BY_HOVER =          false;

/* Editor constants come here */
CONFIG.EDITOR_ALIGN_BOTTOM =                0x01;
CONFIG.EDITOR_ALIGN_MIDDLE =                0x02;
CONFIG.EDITOR_ALIGN_TOP =                   0x04;
CONFIG.EDITOR_ALIGN_LEFT =                  0x08;
CONFIG.EDITOR_ALIGN_CENTER =                0x10;
CONFIG.EDITOR_ALIGN_RIGHT =                 0x20;
CONFIG.EDITOR_ALIGN_SIZE =                  0x30;

/* Event types */
CONFIG.EVENT_MOUSEDOWN =                    "mousedown";
CONFIG.EVENT_MOUSEUP =                      "mouseup";
CONFIG.EVENT_MOUSEOVER =                    "mouseover";
CONFIG.EVENT_MOUSEOUT =                     "mouseout";
CONFIG.EVENT_MOUSEMOVE =                    "mousemove";
CONFIG.EVENT_DBLCLICK =                     "dblclick";
CONFIG.EVENT_KEYDOWN =                      "keydown";
CONFIG.EVENT_KEYUP =                        "keyup";
CONFIG.EVENT_LOADED =                       "editorloaded";
CONFIG.EVENT_EXECUTE_COMMANDS =             "executeCommands";
CONFIG.EVENT_STENCIL_SET_LOADED =           "stencilSetLoaded";
CONFIG.EVENT_SELECTION_CHANGED =            "selectionchanged";
CONFIG.EVENT_SHAPEADDED =                   "shapeadded";
CONFIG.EVENT_SHAPEREMOVED =                 "shaperemoved";
CONFIG.EVENT_PROPERTY_CHANGED =             "propertyChanged";
CONFIG.EVENT_DRAGDROP_START =               "dragdrop.start";
CONFIG.EVENT_SHAPE_MENU_CLOSE =             "shape.menu.close";
CONFIG.EVENT_DRAGDROP_END =                 "dragdrop.end";
CONFIG.EVENT_RESIZE_START =                 "resize.start";
CONFIG.EVENT_RESIZE_END =                   "resize.end";
CONFIG.EVENT_DRAGDOCKER_DOCKED =            "dragDocker.docked";
CONFIG.EVENT_HIGHLIGHT_SHOW =               "highlight.showHighlight";
CONFIG.EVENT_HIGHLIGHT_HIDE =               "highlight.hideHighlight";
CONFIG.EVENT_LOADING_ENABLE =               "loading.enable";
CONFIG.EVENT_LOADING_DISABLE =              "loading.disable";
CONFIG.EVENT_LOADING_STATUS =               "loading.status";
CONFIG.EVENT_OVERLAY_SHOW =                 "overlay.show";
CONFIG.EVENT_OVERLAY_HIDE =                 "overlay.hide";
CONFIG.EVENT_ARRANGEMENT_TOP =              "arrangement.setToTop";
CONFIG.EVENT_ARRANGEMENT_BACK =             "arrangement.setToBack";
CONFIG.EVENT_ARRANGEMENT_FORWARD =          "arrangement.setForward";
CONFIG.EVENT_ARRANGEMENT_BACKWARD =         "arrangement.setBackward";
CONFIG.EVENT_PROPWINDOW_PROP_CHANGED =      "propertyWindow.propertyChanged";
CONFIG.EVENT_LAYOUT_ROWS =                  "layout.rows";
CONFIG.EVENT_LAYOUT_BPEL =                  "layout.BPEL";
CONFIG.EVENT_LAYOUT_BPEL_VERTICAL =         "layout.BPEL.vertical";
CONFIG.EVENT_LAYOUT_BPEL_HORIZONTAL =       "layout.BPEL.horizontal";
CONFIG.EVENT_LAYOUT_BPEL_SINGLECHILD =      "layout.BPEL.singlechild";
CONFIG.EVENT_LAYOUT_BPEL_AUTORESIZE =       "layout.BPEL.autoresize";
CONFIG.EVENT_AUTOLAYOUT_LAYOUT =            "autolayout.layout";
CONFIG.EVENT_UNDO_EXECUTE =                 "undo.execute";
CONFIG.EVENT_UNDO_ROLLBACK =                "undo.rollback";
CONFIG.EVENT_BUTTON_UPDATE =                "toolbar.button.update";
CONFIG.EVENT_LAYOUT =                       "layout.dolayout";
CONFIG.EVENT_GLOSSARY_LINK_EDIT =           "glossary.link.edit";
CONFIG.EVENT_GLOSSARY_SHOW =                "glossary.show.info";
CONFIG.EVENT_GLOSSARY_NEW =                 "glossary.show.new";
CONFIG.EVENT_DOCKERDRAG =                   "dragTheDocker";
CONFIG.EVENT_SHOW_PROPERTYWINDOW =          "propertywindow.show";
CONFIG.EVENT_ABOUT_TO_SAVE =                "file.aboutToSave";

/* Selection Shapes Highlights */
CONFIG.SELECTION_HIGHLIGHT_SIZE =               5;
CONFIG.SELECTION_HIGHLIGHT_COLOR =              "#4444FF";
CONFIG.SELECTION_HIGHLIGHT_COLOR2 =             "#9999FF";
CONFIG.SELECTION_HIGHLIGHT_STYLE_CORNER =       "corner";
CONFIG.SELECTION_HIGHLIGHT_STYLE_RECTANGLE =    "rectangle";
CONFIG.SELECTION_VALID_COLOR =                  "#00FF00";
CONFIG.SELECTION_INVALID_COLOR =                "#FF0000";
CONFIG.DOCKER_DOCKED_COLOR =                    "#00FF00";
CONFIG.DOCKER_UNDOCKED_COLOR =                  "#FF0000";
CONFIG.DOCKER_SNAP_OFFSET =                     10;

/* Copy & Paste */
CONFIG.EDIT_OFFSET_PASTE =            10;

/* Key-Codes */
CONFIG.KEY_CODE_X =                 88;
CONFIG.KEY_CODE_C =                 67;
CONFIG.KEY_CODE_V =                 86;
CONFIG.KEY_CODE_DELETE =            46;
CONFIG.KEY_CODE_META =              224;
CONFIG.KEY_CODE_BACKSPACE =         8;
CONFIG.KEY_CODE_LEFT =              37;
CONFIG.KEY_CODE_RIGHT =             39;
CONFIG.KEY_CODE_UP =                38;
CONFIG.KEY_CODE_DOWN =              40;

// TODO Determine where the lowercase constants are still used and remove them from here.
CONFIG.KEY_Code_enter =             12;
CONFIG.KEY_Code_left =              37;
CONFIG.KEY_Code_right =             39;
CONFIG.KEY_Code_top =               38;
CONFIG.KEY_Code_bottom =            40;

/* Supported Meta Keys */
CONFIG.META_KEY_META_CTRL =         "metactrl";
CONFIG.META_KEY_ALT =               "alt";
CONFIG.META_KEY_SHIFT =             "shift";

/* Key Actions */
CONFIG.KEY_ACTION_DOWN =            "down";
CONFIG.KEY_ACTION_UP =              "up";

CONFIG.REMOTE_WINDOW_HEIGHT_DEFAULT = 300;
CONFIG.REMOTE_WINDOW_WIDTH_DEFAULT = 300;

export default CONFIG;
